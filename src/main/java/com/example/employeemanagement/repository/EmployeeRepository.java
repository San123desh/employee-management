package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data JPA Repository.
 *
 * Why an interface (not a class)?
 *   - Spring generates the implementation at runtime via a dynamic proxy.
 *   - You NEVER write `implements` or a separate Impl class for the base CRUD.
 *   - You ONLY add custom methods when the generated ones aren't enough.
 *
 * What you get for FREE by extending JpaRepository<Employee, Long>:
 *   - save(Employee)              → INSERT or UPDATE (based on whether id is null)
 *   - findById(Long)              → Optional<Employee> (empty if not found)
 *   - findAll()                   → List<Employee>
 *   - deleteById(Long)            → DELETE
 *   - count()                     → total rows
 *   - existsById(Long)            → boolean
 *   - findAll(Example)            → query by example
 *   - ...and ~20 more
 */


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    // ─── Derived query methods (Spring parses the method name) ──────────────

    /**
     * Spring sees "findByDepartment" and generates:
     *   SELECT * FROM employees WHERE department = ?
     *
     * Naming convention: findBy + <FieldName> (camelCase of the entity field).
     * The field name must match EXACTLY (case-sensitive) in the entity.
     */
    List<Employee> findByDepartment(String department);

    /**
     * Generated SQL:
     *   SELECT * FROM employees WHERE department = ? AND is_active = true
     *
     * "And" chains conditions. "OrderBy<FieldName>Asc/Desc" adds sorting.
     */
    List<Employee> findByDepartmentAndIsActiveTrue(String department);

    /**
     * Generated SQL:
     *   SELECT * FROM employees WHERE email = ?
     *
     * Returns Optional because email is unique → at most one result.
     * Never return a raw entity that might be null — Optional forces the caller
     * to handle the "not found" case explicitly.
     */

     Optional<Employee> findByEmail(String email);

    /**
     * Generated SQL:
     *   SELECT * FROM employees WHERE is_active = true ORDER BY id ASC
     */
    List<Employee> findByIsActiveTrueOrderByIdAsc();




    // ─── Custom @Query (when method names get unreadable) ──────────────────

    /**
     * A simple derived query for "find by first name OR last name" would be:
     *   findByFirstNameOrLastName(String first, String last)
     * ...but that's ambiguous (is it "first = X OR last = X" or "first = X OR last = Y"?).
     *
     * A JPQL query is clearer:
     */

    // @Param makes jpql readable and immune to parameter reordering



    @Query("SELECT e FROM Employee e WHERE e.firstName LIKE %:keyword% OR e.lastName LIKE %:keyword%")
    List<Employee> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Native SQL (use sparingly — JPQL is preferred because it's DB-agnostic).
     * Here we use it to get a count grouped by department, which has no
     * clean derived-query equivalent.
     */
    @Query(value = "SELECT department, COUNT(*) as cnt FROM employees WHERE is_active = true GROUP BY department",
            nativeQuery = true)
    List<Object[]> countActiveByDepartment();





}


























