package com.java.mapper;

import com.java.dto.request.EmployeeRequest;
import com.java.dto.response.EmployeeResponse;
import com.java.entity.Employee;

public class EmployeeMapper {

	public static Employee toEntity(EmployeeRequest request) {
		if (request == null)
			return null;
		return Employee.builder().firstName(request.getFirstName()).lastName(request.getLastName())
				.email(request.getEmail()).gender(request.getGender()).build();

	}

	public static EmployeeResponse toResponse(Employee employee) {
		if (employee == null)
			return null;
		return EmployeeResponse.builder().id(employee.getId()).firstName(employee.getFirstName())
				.lastName(employee.getLastName()).email(employee.getEmail()).gender(employee.getGender()).build();
	}
}
