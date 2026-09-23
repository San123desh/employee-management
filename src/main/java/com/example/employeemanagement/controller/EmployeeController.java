package com.example.employeemanagement.controller;


import com.example.employeemanagement.dto.EmployeeRequestDto;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller – the HTTP entry point.
 *
 * Why @RestController (not @Controller)?
 *   - @Controller + @ResponseBody on each method = verbose.
 *   - @RestController = @Controller + @ResponseBody baked in.
 *   - Every method's return value is automatically serialized to JSON.
 *
 * Why is this class THIN (no logic)?
 *   - It receives the HTTP request, calls the service, returns the response.
 *   - All business rules are in the Service layer.
 *   - If you ever need to change a rule, you touch the Service, not the Controller.
 */


@RestController
@RequestMapping("/api/employees")

public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // ─── GET /api/employees ────────────────────────────────────────────────
    // Returns all active employees, sorted by last name.
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployee() {
        List<Employee> employees = employeeService.getActiveEmployees();
        return ResponseEntity.ok(employees);
        //ResponseEntity.ok() = HTTP 200 + the body
        // You could just `return employees;` and Spring would still return 200,
        // but ResponseEntity gives you control over status codes
    }


    // ─── GET /api/employees/{id} ───────────────────────────────────────────
    // Returns a single employee by their ID.
    // If not found → GlobalExceptionHandler catches ResourceNotFoundException → 404 JSON.
    // Extracts {id} from the URL path: /api/employees/42 → id = 42
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id){
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);

    }


    // ─── GET /api/employees?department=IT ──────────────────────────────────
    // Returns active employees filtered by department.
    // @RequestParam makes "department" an optional query parameter.
    // If the client doesn't pass it, Spring returns null → we return all active employees.
    @GetMapping("/department")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(
            @RequestParam String department){
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);

    }

    // ─── GET /api/employees/search?keyword=ra ──────────────────────────────
    // Fuzzy search by first or last name.
    // Extracts ?keyword=ra from the query string
    @GetMapping("/search")
    public  ResponseEntity<List<Employee>> searchEmployees(
            @RequestParam String keyword){
        List<Employee> employees = employeeService.searchEmployees(keyword);
        return  ResponseEntity.ok(employees);
    }

    // ─── POST /api/employees ───────────────────────────────────────────────
    // Creates a new employee.
    // @Valid triggers Bean Validation on the DTO.
    // If any @NotBlank/@Email/@Positive fails → MethodArgumentNotValidException
    // → caught by GlobalExceptionHandler → 400 with field-level error messages.
    @PostMapping
    public ResponseEntity<Employee> createEmployee(
            @Valid @RequestBody EmployeeRequestDto request){

        // DTO -> Entity mapping (manual, explicit, safe)
        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setSalary(request.getSalary());
        // Note: id, createdAt, updatedAt, isActive are NOT set here.
        // The service layer forces them to safe values.

        Employee saved = employeeService.createEmployee(employee);

        // 201 Created (not 200 OK) – a new resource was created.
        // Location header tells the client where the new resource lives.
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/employees/" + saved.getId())
                .body(saved);

    }


    // ─── PUT /api/employees/{id} ───────────────────────────────────────────
    // Full update: replaces the employee's data.
    // @Valid triggers the same Bean Validation as POST.
    // The service layer handles "only update non-null fields" logic.

    @PutMapping("/{id}")
    public  ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto request){

        // DTO -> Entity
        Employee updatedData = new Employee();
        updatedData.setFirstName(request.getFirstName());
        updatedData.setLastName(request.getLastName());
        updatedData.setEmail(request.getEmail());
        updatedData.setPhone(request.getPhone());
        updatedData.setDepartment(request.getDepartment());
        updatedData.setDesignation(request.getDesignation());
        updatedData.setJoiningDate(request.getJoiningDate());
        updatedData.setSalary(request.getSalary());


        Employee updated = employeeService.updateEmployee(id, updatedData);

        return ResponseEntity.ok(updated);

    }

    // ─── DELETE /api/employees/{id} ────────────────────────────────────────
    // Soft-delete: sets isActive = false. The record stays in the DB.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateEmployee(@PathVariable Long id){
        employeeService.deactivateEmployee(id);
        return ResponseEntity.noContent().build();
        // 204 No Content – the resource is "gone" from the API's perspective,
        // but there's nothing to return in the body.
    }

    // ─── DELETE /api/employees/{id}/hard ───────────────────────────────────
    // Hard-delete: permanently removes the row.
    // Exposed as a separate endpoint so it's not accidentally called.
    // In a real government system, this would be behind a special admin role.
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id){
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }


    // ─── GET /api/employees/stats ──────────────────────────────────────────
    // Returns a count of active employees grouped by department.
    // Example response:
    //   { "IT": 3, "HR": 1, "Finance": 1 }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getDepartmentStats() {
        List<Object []> results = employeeService.getDepartmentCounts();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] row : results){
            // row[0] = department name, row[1] = count
            stats.put((String) row[0], (Long) row[1]);
        }
        return ResponseEntity.ok(stats);
    }


}
