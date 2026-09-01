package com.java.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.java.dto.request.EmployeeRequest;
import com.java.dto.response.EmployeeResponse;
import com.java.entity.Employee;
import com.java.exception.ResourceNotFoundException;
import com.java.mapper.EmployeeMapper;
import com.java.repository.EmployeeRepository;
import com.java.service.EmployeeService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

	private EmployeeRepository employeeRepository;

	public List<EmployeeResponse> createEmployee(List<EmployeeRequest> request) {

		List<Employee> employees = request.stream()
	            .map(EmployeeMapper::toEntity)
	            .toList();

	    List<Employee> savedEmployees = employeeRepository.saveAll(employees);

	    return savedEmployees.stream()
	            .map(EmployeeMapper::toResponse)
	            .toList();
	}

	public EmployeeResponse getEmployeeById(Long employeeId) {
		Employee employee = employeeRepository.findById(employeeId).orElseThrow(
				() -> new ResourceNotFoundException("Employee is not exists with given id : " + employeeId));

		return EmployeeMapper.toResponse(employee);
	}

	@Override
	public List<EmployeeResponse> getAllEmployees() {
		List<Employee> employees = employeeRepository.findAll();
		return employees.stream().map((employee) -> EmployeeMapper.toResponse(employee)).collect(Collectors.toList());
	}

	public EmployeeResponse updateEmployee(Long employeeId, EmployeeRequest updatedEmployee) {

		Employee employee = employeeRepository.findById(employeeId).orElseThrow(
				() -> new ResourceNotFoundException("Employee is not exists with given id: " + employeeId));

		employee.setFirstName(updatedEmployee.getFirstName());
		employee.setLastName(updatedEmployee.getLastName());
		employee.setEmail(updatedEmployee.getEmail());
		employee.setGender(updatedEmployee.getGender());

		Employee updatedEmployeeObj = employeeRepository.save(employee);

		return EmployeeMapper.toResponse(updatedEmployeeObj);
	}

	public void deleteEmployee(Long employeeId) {

		Employee employee = employeeRepository.findById(employeeId).orElseThrow(
				() -> new ResourceNotFoundException("Employee is not exists with given id: " + employeeId));

		employeeRepository.deleteById(employeeId);
	}
}
