package com.java.dto.request;

import java.util.Set;

import com.java.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteRequest {

	@NotBlank(message = "Name is required")
	@Size(max = 100)
	private String name;

	@NotBlank(message = "User Name is required")
	@Size(max = 100)
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Email format is invalid")
	@Size(max = 100)
	private String email;

	@NotBlank(message = "Password is required")
	@Size(max = 100)
	private String password;

	@NotEmpty(message = "At least one role is required")
	@Size(max = 10, message = "Maximum 10 roles are allowed")
	private Set<String> roles;

}
