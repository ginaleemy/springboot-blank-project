package com.java.service.impl;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.dto.request.LoginRequest;
import com.java.dto.request.RegisteRequest;
import com.java.dto.response.JwtAuthResponse;
import com.java.entity.Role;
import com.java.entity.User;
import com.java.exception.EmployeeApiException;
import com.java.repository.RoleRepository;
import com.java.repository.UserRepository;
import com.java.security.JwtTokenProvider;
import com.java.service.AuthService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	private JwtTokenProvider jwtTokenProvider;

	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

	public JwtAuthResponse register(RegisteRequest registerDto) {

		// 1. Check username
		if (userRepository.existsByUsername(registerDto.getUsername())) {
			throw new EmployeeApiException(HttpStatus.BAD_REQUEST, "Username already exists!");
		}

		// 2. Check email
		if (userRepository.existsByEmail(registerDto.getEmail())) {
			throw new EmployeeApiException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
		}
		// 3. Create user
		User user = new User();
		user.setName(registerDto.getName());
		user.setUsername(registerDto.getUsername());
		user.setEmail(registerDto.getEmail());
		user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		// 4. Assign role
		Set<Role> roles = registerDto.getRoles()
		        .stream()
		        .map(roleName -> {
		            Role role = roleRepository.findByName(roleName);
		            if (role == null) {
		                throw new EmployeeApiException(
		                        HttpStatus.BAD_REQUEST,
		                        "Role not found: " + roleName
		                );
		            }

		            return role;
		        })
		        .collect(Collectors.toSet());

		user.setRoles(roles);
		// 5. Save user
		User userRes = userRepository.save(user);
		
		
		// 6. Authenticate
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(registerDto.getUsername(), registerDto.getPassword()));

		// 7. Generate JWT
		String token = jwtTokenProvider.generateToken(authentication);

		// 8. Convert roles to response
	    Set<String> roleNames = userRes.getRoles()
	            .stream()
	            .map(Role::getName)
	            .collect(Collectors.toSet());
	    
	    // 9. Return response
	    return JwtAuthResponse.builder()
	            .username(userRes.getUsername())
	            .name(userRes.getName())
	            .accessToken(token)
	            .tokenType("Bearer")
	            .roles(roleNames)
	            .id(userRes.getId())
	            .email(userRes.getEmail())
	            .build();
	}

	public JwtAuthResponse login(LoginRequest loginDto) {

		log.info("===== LOGIN START =====");
		log.info("Login username/email: {}", loginDto.getUsernameOrEmail());

		// Do NOT log the real password in production
		log.info("Password provided: {}", loginDto.getPassword() != null ? "YES" : "NO");

		try {

			// 1. Check whether user exists before authentication
			Optional<User> userOptional = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(),
					loginDto.getUsernameOrEmail());

			if (userOptional.isEmpty()) {

				log.error("LOGIN FAILED: User not found for username/email: {}", loginDto.getUsernameOrEmail());

				throw new EmployeeApiException(HttpStatus.UNAUTHORIZED, "Invalid username/email or password");
			}

			User dbUser = userOptional.get();

			log.info("User found. ID: {}", dbUser.getId());
			log.info("Username: {}", dbUser.getUsername());
			log.info("Email: {}", dbUser.getEmail());

			// Check BCrypt stored password format
			if (dbUser.getPassword() == null) {

				log.error("LOGIN FAILED: Database password is NULL");

			} else {

				log.info("Stored password starts with BCrypt prefix: {}", dbUser.getPassword().startsWith("$2"));

				log.info("Stored password length: {}", dbUser.getPassword().length());
			}

			// 2. Manually test password matching for debugging
			boolean passwordMatched = passwordEncoder.matches(loginDto.getPassword(), dbUser.getPassword());

			log.info("BCrypt password match result: {}", passwordMatched);

			if (!passwordMatched) {

				log.error("LOGIN FAILED: Password does not match for user: {}", loginDto.getUsernameOrEmail());
			}

			// 3. Let Spring Security authenticate
			log.info("Calling AuthenticationManager.authenticate()...");

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

			log.info("Authentication successful.");
			log.info("Authenticated principal: {}", authentication.getName());

			log.info("Authorities: {}", authentication.getAuthorities());

			// 4. Store authentication
			SecurityContextHolder.getContext().setAuthentication(authentication);

			log.info("Authentication stored in SecurityContext.");

			// 5. Generate JWT
			String token = jwtTokenProvider.generateToken(authentication);

			log.info("JWT generated successfully: {}", token != null);

			// 6. Get role
			String role = null;

			Optional<Role> optionalRole = dbUser.getRoles()
			        .stream()
			        .min(Comparator.comparing(Role::getId));

			if (optionalRole.isPresent()) {

				role = optionalRole.get().getName();

				log.info("Role found: {}", role);

			} else {

				log.warn("User has no role assigned. User ID: {}", dbUser.getId());
			}
			Set<String> roleNames = dbUser.getRoles()
			        .stream()
			        .map(Role::getName)
			        .collect(Collectors.toSet());
			
			// 7. Build response
			log.info("===== LOGIN SUCCESS =====");

			return JwtAuthResponse.builder()
					.accessToken(token)
					.id(dbUser.getId())
					.name(dbUser.getName())
					.username(dbUser.getUsername())
					.email(dbUser.getEmail())
					.roles(roleNames)
					.currRole(role).build();
			

		} catch (BadCredentialsException e) {

			log.error("LOGIN FAILED: Bad credentials for user: {}", loginDto.getUsernameOrEmail());

			throw new EmployeeApiException(HttpStatus.UNAUTHORIZED, "Invalid username/email or password");

		} catch (UsernameNotFoundException e) {

			log.error("LOGIN FAILED: Username not found: {}", loginDto.getUsernameOrEmail());

			throw new EmployeeApiException(HttpStatus.UNAUTHORIZED, "Invalid username/email or password");

		} catch (Exception e) {

			log.error("LOGIN ERROR: {}", e.getMessage(), e);

			throw e;
		}
	}

}
