package com.example.employeemanagement.service;


import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


/**
 * Service implementation – all business logic lives here.
 *
 * Why @Service?
 *   - It's a Spring stereotype annotation (like @Component) that tells Spring
 *     "create a bean for this class and manage its lifecycle."
 *   - @Service is semantically more specific than @Component (it says
 *     "this is a business-logic layer"), which helps with code organization.
 *
 * Why @Transactional on the class?
 *   - Every public method in this class runs inside a DB transaction.
 *   - If an exception is thrown mid-method, the entire transaction ROLLS BACK.
 *   - Without it, a failed save could leave your DB in a half-written state.
 **/


@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired  // @Autowired injects the Spring-generated proxy for EmployeeRepository.
    private EmployeeRepository repository;


    // Read Operations
    @Override
    public List<Employee> getAllEmployees(){
        return repository.findAll();
    }

    @Override
    public List<Employee> getActiveEmployees(){
        return repository.findByIsActiveTrueOrderByIdAsc();
    }


    @Override
    public Employee getEmployeeById(Long id) {
        // repository.findById() returns Optional<Employee>.
        // .orElseThrow() converts "empty" into an exception instead of null.
        // The controller's @ControllerAdvice will catch this and return 404.
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    @Override
    public List<Employee> getEmployeesByDepartment(String department) {
        return repository.findByDepartmentAndIsActiveTrue(department);
    }

    @Override
    public List<Employee> searchEmployees(String keyword) {
        return repository.searchByKeyword(keyword);
    }


    // ─── WRITE operations ──────────────────────────────────────────────────

    @Override
    public Employee createEmployee(Employee employee) {
        // Business rule: email must be unique.
        // The DB has a UNIQUE constraint, but we check HERE to give a friendlier
        // error message instead of a raw SQL constraint violation.
        repository.findByEmail(employee.getEmail()).ifPresent(existing -> {
            throw new RuntimeException("An employee with email '" + employee.getEmail() + "' already exists.");
        });

        // Don't trust the client to set id, createdAt, updatedAt, or isActive.
        // Force them to safe values:
        employee.setId(null);           // let the DB generate it
        employee.setCreatedAt(null);    // let @CreatedDate fill it
        employee.setUpdatedAt(null);    // let @LastModifiedDate fill it
        employee.setActive(true);     // new employees are always active

        return repository.save(employee);
        // save() = INSERT when id is null, UPDATE when id is set.
        // Since we forced id=null, this is always an INSERT here.
    }

    @Override
    public Employee updateEmployee(Long id, Employee updatedData) {
        // 1. Fetch the existing entity (throws 404 if not found)
        Employee existing = getEmployeeById(id);

        // 2. Copy only the fields we allow to be updated.
        //    We do NOT copy: id, createdAt, isActive (via this endpoint).
        //    This prevents a malicious client from setting isActive=false
        //    or changing the creation timestamp.
        // null wipe safe
        if (updatedData.getFirstName() != null) {
            existing.setFirstName(updatedData.getFirstName());
        }
        if (updatedData.getLastName() != null) {
            existing.setLastName(updatedData.getLastName());
        }
        if (updatedData.getEmail() != null && !updatedData.getEmail().equals(existing.getEmail())) {
            // Check uniqueness if email is actually changing
            repository.findByEmail(updatedData.getEmail()).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "An employee with email '" + updatedData.getEmail() + "' already exists."
                    );
                }
            });
            existing.setEmail(updatedData.getEmail());
        }
        if (updatedData.getPhone() != null) {
            existing.setPhone(updatedData.getPhone());
        }
        if (updatedData.getDepartment() != null) {
            existing.setDepartment(updatedData.getDepartment());
        }
        if (updatedData.getDesignation() != null) {
            existing.setDesignation(updatedData.getDesignation());
        }
        if (updatedData.getJoiningDate() != null) {
            existing.setJoiningDate(updatedData.getJoiningDate());
        }
        if (updatedData.getSalary() != null) {
            existing.setSalary(updatedData.getSalary());
        }

        // 3. Save (Hibernate sees the same ID → generates an UPDATE)
        return repository.save(existing);
    }

    @Override
    public void deactivateEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employee.setActive(false);
        // No need to call save() — @Transactional + dirty checking means
        // Hibernate will auto-generate an UPDATE at the end of the transaction.
        // (This is called "dirty checking" or "auto-flush.")
    }

    @Override
    @Transactional  // Explicit here to emphasize: hard delete is a separate concern
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        repository.delete(employee);
        // repository.delete() marks the entity for deletion.
        // The actual DELETE SQL runs when the transaction commits.
    }

    @Override
    public List<Object[]> getDepartmentCounts() {
        return repository.countActiveByDepartment();
    }

}
