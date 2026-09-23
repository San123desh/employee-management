package com.example.employeemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ─────────────────────────────────────────────────────────────
    // TEST 1: GET /api/employees → 200 + JSON array
    // ─────────────────────────────────────────────────────────────
    @Test
    @Order(1)
    void getAllEmployees_returns200WithList() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[0].email").exists());
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 2: GET /api/employees/1 → 200 + single object
    // ─────────────────────────────────────────────────────────────
    @Test
    @Order(2)
    void getEmployeeById_returns200() throws Exception {
        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.active").value(true));
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 3: GET /api/employees/99999 → 404 + error body
    // ─────────────────────────────────────────────────────────────
    @Test
    @Order(3)
    void getEmployeeById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/employees/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 4: POST /api/employees (valid) → 201 + created object
    // ─────────────────────────────────────────────────────────────
    @Test
    @Order(4)
    void createEmployee_validBody_returns201() throws Exception {
        String body = """
            {
                "firstName": "Test",
                "lastName": "User",
                "email": "test.user@example.com",
                "phone": "1234567890",
                "department": "QA",
                "designation": "QA Engineer",
                "joiningDate": "2025-01-15",
                "salary": 50000
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.email").value("test.user@example.com"))
                .andReturn();

        // Verify it's actually in the DB by fetching it
        String response = result.getResponse().getContentAsString();
        int newId = objectMapper.readTree(response).get("id").asInt();

        mockMvc.perform(get("/api/employees/" + newId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"));
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 5: POST /api/employees (invalid) → 400 + field errors
    // ─────────────────────────────────────────────────────────────
    @Test
    @Order(5)
    void createEmployee_invalidBody_returns400() throws Exception {
        String body = """
            {
                "firstName": "",
                "lastName": "",
                "email": "not-an-email",
                "department": "",
                "designation": "",
                "joiningDate": null,
                "salary": -5
            }
            """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.salary").exists());
    }
}