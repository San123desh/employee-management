package com.example.employeemanagement.service;


import com.example.employeemanagement.entity.Employee;

import java.util.List;

/**
 * Service interface – defines the CONTRACT for employee operations.
 *
 * Why an interface (not just a class)?
 *   1. Dependency Inversion: The Controller depends on this ABSTRACTION,
 *      not on the concrete implementation. If you swap the impl (e.g., for
 *      a mock in tests), the controller doesn't change.
 *   2. Testability: In unit tests you can mock this interface with Mockito
 *      and verify controller behavior without hitting a real database.
 *   3. Enterprise convention: Government/enterprise Java shops almost always
 *      use interface + impl. It signals "this is a swappable service."
 */

public interface EmployeeService {


    List<Employee> getAllEmployees();

    List<Employee> getActiveEmployees();

    Employee getEmployeeById(Long id);

    List<Employee> getEmployeesByDepartment(String department);

    List<Employee> searchEmployees(String keyword);

    /** Create a new employee. Returns the saved entity (with generated ID). */
    Employee createEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee updatedData);

    void deactivateEmployee(Long id);

    void deleteEmployee(Long id);

    /** Returns department-wise count of active employees. Each row: [department, count]. */
    List<Object[]> getDepartmentCounts();
}
