package com.java.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.java.enums.Gender;

class EmployeeTest {

    // =========================================================
    // 1. TEST EMPLOYEE BUILDER
    // =========================================================
    @Test
    void shouldCreateEmployeeUsingBuilder() {

        // Arrange + Act
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel@example.com")
                .build();

        // Assert
        assertNotNull(employee);

        assertEquals(1L, employee.getId());
        assertEquals("Daniel", employee.getFirstName());
        assertEquals("Wong", employee.getLastName());
        assertEquals(Gender.MALE, employee.getGender());
        assertEquals("daniel@example.com", employee.getEmail());
    }


    // =========================================================
    // 2. TEST SETTERS
    // =========================================================
    @Test
    void shouldSetEmployeeFields() {

        // Arrange
        Employee employee = new Employee();

        // Act
        employee.setId(1L);
        employee.setFirstName("Anna");
        employee.setLastName("Lee");
        employee.setGender(Gender.FEMALE);
        employee.setEmail("anna@example.com");

        // Assert
        assertEquals(1L, employee.getId());
        assertEquals("Anna", employee.getFirstName());
        assertEquals("Lee", employee.getLastName());
        assertEquals(Gender.FEMALE, employee.getGender());
        assertEquals("anna@example.com", employee.getEmail());
    }


    // =========================================================
    // 3. TEST EMPTY EMPLOYEE
    // =========================================================
    @Test
    void shouldCreateEmptyEmployee() {

        Employee employee = new Employee();

        assertNotNull(employee);

        assertNull(employee.getId());
        assertNull(employee.getFirstName());
        assertNull(employee.getLastName());
        assertNull(employee.getGender());
        assertNull(employee.getEmail());
    }


    // =========================================================
    // 4. TEST OTHER GENDER
    // =========================================================
    @Test
    void shouldAllowOtherGender() {

        Employee employee = Employee.builder()
                .firstName("Alex")
                .lastName("Tan")
                .gender(Gender.OTHER)
                .email("alex@example.com")
                .build();

        assertEquals(Gender.OTHER, employee.getGender());
    }
}