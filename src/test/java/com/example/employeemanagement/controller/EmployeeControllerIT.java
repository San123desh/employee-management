package com.example.employeemanagement.controller;

import com.example.employeemanagement.security.JwtTokenProvider;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private JwtTokenProvider tokenProvider;

    private String token;


    @BeforeEach
    void generateToken() {
        token = tokenProvider.generateToken("admin");
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 1: GET /api/employees → 200 + JSON array
    // ─────────────────────────────────────────────────────────────
    @Test
    @Order(1)
    void getAllEmployees_returns200WithList() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/employees")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andReturn();
        System.out.println("TEST 1 → " + result.getResponse().getStatus()
                + " | " + result.getResponse().getContentAsString());
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 2: GET /api/employees/1 → 200 + single object
    // ─────────────────────────────────────────────────────────────
    @Test
    @Order(2)
    void getEmployeeById_returns200() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/employees/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.active").value(true))
                .andReturn();

        System.out.println("TEST 2 → " + result.getResponse().getStatus()
                + " | " + result.getResponse().getContentAsString());
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 3: GET /api/employees/99999 → 404 + error body
    // ─────────────────────────────────────────────────────────────
    @Test
    @Order(3)
    void getEmployeeById_notFound_returns404() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/employees/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andReturn();

        System.out.println("TEST 3 → " + result.getResponse().getStatus()
                + " | " + result.getResponse().getContentAsString());
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 4: POST /api/employees (valid) → 201 + created object
    // ─────────────────────────────────────────────────────────────
    @Test
    @Order(4)
    void createEmployee_validBody_returns201() throws Exception {
        SecurityContextHolder.clearContext();
        // Raw JSON string
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

        // Builds a MockHttpServletRequest (POST + JSON body) and dispatches it
        // through the full Spring MVC pipeline (filters → controller → service → DB).
        // .andExpect(...) asserts on the response; .andReturn() captures it for later use.
        MvcResult result = mockMvc.perform(post("/api/employees")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)  // sets Content-Type header
                        .content(body))                            // sets the request body
                .andExpect(status().isCreated())                    // asserts HTTP 201
                .andExpect(jsonPath("$.id").isNumber())             // asserts response JSON has a numeric "id"
                .andExpect(jsonPath("$.firstName").value("Test"))   // asserts field matches
                .andExpect(jsonPath("$.email").value("test.user@example.com"))
                .andReturn();                                       // returns the full MvcResult (request + response)


        System.out.println("TEST 4 (POST) → " + result.getResponse().getStatus()
                + " | " + result.getResponse().getContentAsString());


        //extract the raw response body as string
        String response = result.getResponse().getContentAsString();

        // Parse JSON → JsonNode, then pull out the generated "id" for use in the next request
        int newId = JsonPath.parse(response).read("$.id");

        // Verify the created resource is retrievable via GET
        MvcResult verifyResult = mockMvc.perform(get("/api/employees/" + newId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andReturn();

        System.out.println("TEST 4 (GET verify) → " + verifyResult.getResponse().getStatus()
                + " | " + verifyResult.getResponse().getContentAsString());
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

        //Executes the request and returns a ResultActions
        // @Valid on the controller triggers Bean Validation → MethodArgumentNotValidException
        // →  @ControllerAdvice catches it → returns 400 with field-level errors
        MvcResult result = mockMvc.perform(post("/api/employees")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.salary").exists())
                .andReturn();

        System.out.println("TEST 5 → " + result.getResponse().getStatus()
                + " | " + result.getResponse().getContentAsString());
    }
}