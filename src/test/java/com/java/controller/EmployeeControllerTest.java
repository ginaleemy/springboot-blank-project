package com.java.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.java.dto.request.EmployeeRequest;
import com.java.dto.response.EmployeeResponse;
import com.java.enums.Gender;
import com.java.security.JwtAuthenticationFilter;
import com.java.service.EmployeeService;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    /*
     * =========================================================
     * 1. GET employee by ID
     * GET /api/employees/1
     * =========================================================
     */
    @Test
    void shouldGetEmployeeById() throws Exception {

    	  // 1. Prepare fake response
        EmployeeResponse response = EmployeeResponse.builder()
                .id(1L)
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel.wong@example.com")
                .build();

        // 2. Tell Mockito what service should return
        when(employeeService.getEmployeeById(1L))
                .thenReturn(response);

        // 3. Trigger GET endpoint
        mockMvc.perform(
                get("/api/employees/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        )

        // 4. Check HTTP response
        .andExpect(status().isOk())

        // 5. Check JSON
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstName").value("Daniel"))
        .andExpect(jsonPath("$.lastName").value("Wong"))
        .andExpect(jsonPath("$.gender").value("MALE"))
        .andExpect(jsonPath("$.email")
                .value("daniel.wong@example.com"));

        // 6. Verify controller called service
        verify(employeeService)
                .getEmployeeById(1L);
       }


    /*
     * =========================================================
     * 2. GET all employees
     * GET /api/employees
     * =========================================================
     */
    @Test
    void shouldGetAllEmployees() throws Exception {

        // Arrange
        EmployeeResponse employee1 = EmployeeResponse.builder()
                .id(1L)
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel.wong@example.com")
                .build();

        EmployeeResponse employee2 = EmployeeResponse.builder()
                .id(2L)
                .firstName("Anna")
                .lastName("Lee")
                .gender(Gender.FEMALE)
                .email("anna.lee@example.com")
                .build();

        when(employeeService.getAllEmployees())
                .thenReturn(List.of(employee1, employee2));

        // Act + Assert
        mockMvc.perform(
                get("/api/employees")
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())

        .andExpect(jsonPath("$.length()").value(2))

        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].firstName").value("Daniel"))

        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].firstName").value("Anna"));

        // Verify
        verify(employeeService).getAllEmployees();
    }


    /*
     * =========================================================
     * 3. CREATE employee
     * POST /api/employees
     *
     * Your controller accepts LIST<EmployeeRequest>
     * =========================================================
     */
    @Test
    void shouldCreateEmployee() throws Exception {

        // Arrange
        EmployeeResponse response = EmployeeResponse.builder()
                .id(1L)
                .firstName("Daniel")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel.wong@example.com")
                .build();

        when(employeeService.createEmployee(anyList()))
                .thenReturn(List.of(response));

        String requestJson = """
                [
                    {
                        "firstName": "Daniel",
                        "lastName": "Wong",
                        "gender": "MALE",
                        "email": "daniel.wong@example.com"
                    }
                ]
                """;

        // Act + Assert
        mockMvc.perform(
                post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].firstName").value("Daniel"))
        .andExpect(jsonPath("$[0].lastName").value("Wong"))
        .andExpect(jsonPath("$[0].gender").value("MALE"))
        .andExpect(jsonPath("$[0].email")
                .value("daniel.wong@example.com"));

        // Verify
        verify(employeeService).createEmployee(anyList());
    }


    /*
     * =========================================================
     * 4. UPDATE employee
     * PUT /api/employees/1
     * =========================================================
     */
    @Test
    void shouldUpdateEmployee() throws Exception {

        // Arrange
        EmployeeResponse response = EmployeeResponse.builder()
                .id(1L)
                .firstName("Daniel Updated")
                .lastName("Wong")
                .gender(Gender.MALE)
                .email("daniel.updated@example.com")
                .build();

        when(employeeService.updateEmployee(
                eq(1L),
                any(EmployeeRequest.class)
        )).thenReturn(response);

        String requestJson = """
                {
                    "firstName": "Daniel Updated",
                    "lastName": "Wong",
                    "gender": "MALE",
                    "email": "daniel.updated@example.com"
                }
                """;

        // Act + Assert
        mockMvc.perform(
                put("/api/employees/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstName")
                .value("Daniel Updated"))
        .andExpect(jsonPath("$.email")
                .value("daniel.updated@example.com"));

        // Verify
        verify(employeeService).updateEmployee(
                eq(1L),
                any(EmployeeRequest.class)
        );
    }


    /*
     * =========================================================
     * 5. DELETE employee
     * DELETE /api/employees/1
     * =========================================================
     */
    @Test
    void shouldDeleteEmployee() throws Exception {

        // Act + Assert
        mockMvc.perform(
                delete("/api/employees/{id}", 1L)
        )
        .andExpect(status().isOk())
        .andExpect(content()
                .string("Employee deleted Successfully"));

        // Verify
        verify(employeeService).deleteEmployee(1L);
    }
}