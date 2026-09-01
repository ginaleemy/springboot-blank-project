package com.java.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.java.dto.request.EmployeeRequest;
import com.java.dto.response.EmployeeResponse;
import com.java.entity.Employee;
import com.java.enums.Gender;

class EmployeeMapperTest {

    /*
     * =========================================================
     * 1. EmployeeRequest -> Employee
     * =========================================================
     */
    @Test
    void shouldConvertEmployeeRequestToEntity() {

        // Arrange
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel.wong@example.com")
                .build();

        // Act
        Employee employee = EmployeeMapper.toEntity(request);

        // Assert
        assertNotNull(employee);

        assertEquals("Daniel", employee.getFirstName());
        assertEquals("Wong", employee.getLastName());
        assertEquals(Gender.MALE, employee.getGender());
        assertEquals(
                "daniel.wong@example.com",
                employee.getEmail()
        );
    }


    /*
     * =========================================================
     * 2. Employee -> EmployeeResponse
     * =========================================================
     */
    @Test
    void shouldConvertEmployeeToResponse() {

        // Arrange
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Anna")
                .lastName("Lee")
                .gender(Gender.FEMALE)
                .email("anna.lee@example.com")
                .build();

        // Act
        EmployeeResponse response =
                EmployeeMapper.toResponse(employee);

        // Assert
        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals("Anna", response.getFirstName());
        assertEquals("Lee", response.getLastName());
        assertEquals(Gender.FEMALE, response.getGender());
        assertEquals(
                "anna.lee@example.com",
                response.getEmail()
        );
    }


    /*
     * =========================================================
     * 3. Null EmployeeRequest
     * =========================================================
     */
    @Test
    void shouldReturnNullWhenEmployeeRequestIsNull() {

        Employee employee =
                EmployeeMapper.toEntity(null);

        assertNull(employee);
    }


    /*
     * =========================================================
     * 4. Null Employee
     * =========================================================
     */
    @Test
    void shouldReturnNullWhenEmployeeIsNull() {

        EmployeeResponse response =
                EmployeeMapper.toResponse(null);

        assertNull(response);
    }
}