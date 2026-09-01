package com.java.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.java.dto.request.EmployeeRequest;
import com.java.dto.response.EmployeeResponse;
import com.java.entity.Employee;
import com.java.enums.Gender;
import com.java.exception.ResourceNotFoundException;
import com.java.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;


    // =========================================================
    // 1. CREATE EMPLOYEE
    // =========================================================
    @Test
    void shouldCreateEmployee() {

        // Arrange
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        Employee savedEmployee = Employee.builder()
                .id(1L)
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        when(employeeRepository.saveAll(any()))
                .thenReturn(List.of(savedEmployee));

        // Act
        List<EmployeeResponse> result =
                employeeService.createEmployee(
                        List.of(request)
                );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                1L,
                result.get(0).getId()
        );

        assertEquals(
                "Daniel",
                result.get(0).getFirstName()
        );

        assertEquals(
                "Wong",
                result.get(0).getLastName()
        );

        assertEquals(
                Gender.MALE,
                result.get(0).getGender()
        );

        assertEquals(
                "daniel@example.com",
                result.get(0).getEmail()
        );

        verify(employeeRepository)
                .saveAll(any());
    }


    // =========================================================
    // 2. GET EMPLOYEE BY id
    // =========================================================
    @Test
    void shouldGetEmployeeById() {

        // Arrange
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act
        EmployeeResponse result =
                employeeService.getEmployeeById(1L);

        // Assert
        assertNotNull(result);

        assertEquals(1L, result.getId());
        assertEquals("Daniel", result.getFirstName());
        assertEquals("Wong", result.getLastName());
        assertEquals(Gender.MALE, result.getGender());
        assertEquals(
                "daniel@example.com",
                result.getEmail()
        );

        verify(employeeRepository)
                .findById(1L);
    }


    // =========================================================
    // 3. EMPLOYEE NOT FOUND
    // =========================================================
    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {

        // Arrange
        when(employeeRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> employeeService
                                .getEmployeeById(999L)
                );

        assertEquals(
                "Employee is not exists with given id : 999",
                exception.getMessage()
        );

        verify(employeeRepository)
                .findById(999L);
    }


    // =========================================================
    // 4. GET ALL EMPLOYEES
    // =========================================================
    @Test
    void shouldGetAllEmployees() {

        // Arrange
        Employee employee1 = Employee.builder()
                .id(1L)
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        Employee employee2 = Employee.builder()
                .id(2L)
                .firstName("Anna")
                .lastName("Lee")
                .gender(Gender.FEMALE)
                .email("anna@example.com")
                .build();

        when(employeeRepository.findAll())
                .thenReturn(
                        List.of(employee1, employee2)
                );

        // Act
        List<EmployeeResponse> result =
                employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(
                "Daniel",
                result.get(0).getFirstName()
        );

        assertEquals(
                Gender.MALE,
                result.get(0).getGender()
        );

        assertEquals(
                "Anna",
                result.get(1).getFirstName()
        );

        assertEquals(
                Gender.FEMALE,
                result.get(1).getGender()
        );

        verify(employeeRepository)
                .findAll();
    }


    // =========================================================
    // 5. EMPTY EMPLOYEE LIST
    // =========================================================
    @Test
    void shouldReturnEmptyListWhenNoEmployees() {

        // Arrange
        when(employeeRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<EmployeeResponse> result =
                employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(employeeRepository)
                .findAll();
    }


    // =========================================================
    // 6. UPDATE EMPLOYEE
    // =========================================================
    @Test
    void shouldUpdateEmployee() {

        // Arrange
        Employee existingEmployee = Employee.builder()
                .id(1L)
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        EmployeeRequest updateRequest =
                EmployeeRequest.builder()
                        .firstName("Anna")
                        .lastName("Lee")
                        .gender(Gender.FEMALE)
                        .email("anna@example.com")
                        .build();

        when(employeeRepository.findById(1L))
                .thenReturn(
                        Optional.of(existingEmployee)
                );

        when(employeeRepository.save(
                any(Employee.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        // Act
        EmployeeResponse result =
                employeeService.updateEmployee(
                        1L,
                        updateRequest
                );

        // Assert
        assertNotNull(result);

        assertEquals(1L, result.getId());
        assertEquals("Anna", result.getFirstName());
        assertEquals("Lee", result.getLastName());

        assertEquals(
                Gender.FEMALE,
                result.getGender()
        );

        assertEquals(
                "anna@example.com",
                result.getEmail()
        );

        verify(employeeRepository)
                .findById(1L);

        verify(employeeRepository)
                .save(existingEmployee);
    }


    // =========================================================
    // 7. UPDATE EMPLOYEE NOT FOUND
    // =========================================================
    @Test
    void shouldThrowExceptionWhenUpdatingMissingEmployee() {

        // Arrange
        EmployeeRequest request =
                EmployeeRequest.builder()
                        .firstName("Anna")
                        .lastName("Lee")
                        .gender(Gender.FEMALE)
                        .email("anna@example.com")
                        .build();

        when(employeeRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService
                        .updateEmployee(
                                999L,
                                request
                        )
        );

        verify(employeeRepository)
                .findById(999L);

        verify(
                employeeRepository,
                never()
        ).save(any(Employee.class));
    }


    // =========================================================
    // 8. DELETE EMPLOYEE
    // =========================================================
    @Test
    void shouldDeleteEmployee() {

        // Arrange
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployee(1L);

        // Assert
        verify(employeeRepository)
                .findById(1L);

        verify(employeeRepository)
                .deleteById(1L);
    }


    // =========================================================
    // 9. DELETE EMPLOYEE NOT FOUND
    // =========================================================
    @Test
    void shouldThrowExceptionWhenDeletingMissingEmployee() {

        // Arrange
        when(employeeRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService
                        .deleteEmployee(999L)
        );

        verify(employeeRepository)
                .findById(999L);

        verify(
                employeeRepository,
                never()
        ).deleteById(999L);
    }
}