package com.example.employeemanagement.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) – what the CLIENT sends in the POST/PUT body.
 *
 * Why a DTO instead of the Entity directly?
 *   1. Security: The client should NOT be able to set `id`, `createdAt`,
 *      `updatedAt`, or `isActive`. Those are server-controlled.
 *      If you accept the Entity, a malicious client can POST:
 *        { "id": 1, "isActive": false, "createdAt": "2000-01-01" }
 *      With a DTO, those fields simply don't exist → Jackson ignores them.
 *   2. Validation: @NotNull, @Email, @Size live HERE, not on the entity.
 *      The entity represents the DB schema; the DTO represents the API contract.
 *   3. Decoupling: You can rename a DB column or add a field to the entity
 *      without breaking the API (and vice versa).
 */

@Data
public class EmployeeRequestDto {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be at most 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must be at most 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Size(max = 10, message = "Phone must be at most 10 characters")
    private String phone;


    @NotBlank(message = "Department is required")
    @Size(max = 100)
    private String department;

    @NotBlank(message = "Designation is required")
    @Size(max = 100)
    private String designation;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be greater than zero")
    private BigDecimal salary;


    // ─── Getters & Setters ─────────────────────────────────────────────────


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
