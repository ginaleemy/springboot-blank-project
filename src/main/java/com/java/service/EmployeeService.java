package com.java.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.java.dto.request.EmployeeRequest;
import com.java.dto.response.EmployeeResponse;


public interface EmployeeService {
	 List<EmployeeResponse> createEmployee(List<EmployeeRequest> employeeRequest);

	 EmployeeResponse getEmployeeById(Long employeeId);

	 List<EmployeeResponse> getAllEmployees();

	 EmployeeResponse updateEmployee(Long employeeId, EmployeeRequest employeeRequest);

	 void deleteEmployee(Long employeeId);
}
