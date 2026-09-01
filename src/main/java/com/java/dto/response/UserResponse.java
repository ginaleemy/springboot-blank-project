package com.java.dto.response;

import java.util.Set;

import com.java.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
	private Long id;

	private String name;

	private String username;

	private String email;

	private Set<Role> roles;
}
