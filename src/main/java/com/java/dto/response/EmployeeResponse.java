package com.java.dto.response;

import com.java.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
	
	private Long id;
	
	private String firstName;

	
	private String lastName;

	
	private Gender gender;

	
	private String email;
}
