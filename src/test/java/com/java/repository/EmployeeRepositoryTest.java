package com.java.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.java.entity.Employee;
import com.java.enums.Gender;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;


    // =========================================================
    // 1. SAVE EMPLOYEE
    // =========================================================
    @Test
    void shouldSaveEmployee() {

        // Arrange
        Employee employee = Employee.builder()
                .firstName("daniel")
                .lastName("wong")
                .gender(Gender.MALE)
                .email("DANIEL.WONG@EXAMPLE.COM")
                .build();

        // Act
        Employee savedEmployee =
                employeeRepository.saveAndFlush(employee);

        // Assert
        assertNotNull(savedEmployee.getId());

        assertEquals(
                "Daniel",
                savedEmployee.getFirstName()
        );

        assertEquals(
                "Wong",
                savedEmployee.getLastName()
        );

        assertEquals(
                Gender.MALE,
                savedEmployee.getGender()
        );

        assertEquals(
                "daniel.wong@example.com",
                savedEmployee.getEmail()
        );
    }


    // =========================================================
    // 2. FIND EMPLOYEE BY ID
    // =========================================================
    @Test
    void shouldFindEmployeeById() {

        // Arrange
        Employee employee = Employee.builder()
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        Employee savedEmployee =
                employeeRepository.saveAndFlush(employee);

        Long employeeId = savedEmployee.getId();

        // Act
        Optional<Employee> result =
                employeeRepository.findById(employeeId);

        // Assert
        assertTrue(result.isPresent());

        assertEquals(
                employeeId,
                result.get().getId()
        );

        assertEquals(
                "Daniel",
                result.get().getFirstName()
        );

        assertEquals(
                "Wong",
                result.get().getLastName()
        );

        assertEquals(
                Gender.MALE,
                result.get().getGender()
        );

        assertEquals(
                "daniel@example.com",
                result.get().getEmail()
        );
    }


    // =========================================================
    // 3. FIND ALL EMPLOYEES
    // =========================================================
    @Test
    void shouldFindAllEmployees() {

        // Arrange
        Employee employee1 = Employee.builder()
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        Employee employee2 = Employee.builder()
                .firstName("Anna")
                .lastName("Lee")
                .gender(Gender.FEMALE)
                .email("anna@example.com")
                .build();

        employeeRepository.saveAll(
                List.of(employee1, employee2)
        );

        employeeRepository.flush();

        // Act
        List<Employee> employees =
                employeeRepository.findAll();

        // Assert
        assertEquals(2, employees.size());

        assertEquals(
                "Daniel",
                employees.get(0).getFirstName()
        );

        assertEquals(
                "Anna",
                employees.get(1).getFirstName()
        );
    }


    // =========================================================
    // 4. DELETE EMPLOYEE
    // =========================================================
    @Test
    void shouldDeleteEmployee() {

        // Arrange
        Employee employee = Employee.builder()
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        Employee savedEmployee =
                employeeRepository.saveAndFlush(employee);

        Long employeeId = savedEmployee.getId();

        // Act
        employeeRepository.deleteById(employeeId);
        employeeRepository.flush();

        // Assert
        Optional<Employee> result =
                employeeRepository.findById(employeeId);

        assertFalse(result.isPresent());
    }
}