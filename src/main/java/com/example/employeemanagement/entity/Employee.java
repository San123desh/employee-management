package com.example.employeemanagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * JPA Entity – maps 1:1 to the `employees` table in the database.
 *
 * Why a separate entity (not a DTO)?
 *   - The entity IS the database row. Spring Data JPA reads/writes it directly.
 *   - We use DTOs at the API boundary so we never accidentally expose
 *     internal fields (like `id` generation strategy) or break the API
 *     when the schema changes.
 */

@Entity
@Table(name = "employees")
@EntityListeners(AuditingEntityListener.class) //activates @CreatedDate / @LastModifiedDate
//@Builder   // lets us write: Employee.builder().firstName("san").lastName("stha").build()
//@NoArgsConstructor    // required by JPA (Hibernate uses reflection + no-arg constructor)
//@AllArgsConstructor   // lets us write: new Employee(1L, "san", ...) – useful in tests
public class Employee {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    // nullable=false → Hibernate adds a NOT NULL constraint in the generated DDL.
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 15)
    // nullable by default
    private String phone;

    @Column(name = "department", nullable = false, length = 100)
    // In a real system this might be a @ManyToOne to a Department entity.
    // Keeping it as a String here for simplicity; you can upgrade it later.
    private String department;

    @Column(name = "designation", nullable = false, length = 100)
    // Job title: "Software Engineer", "HR Officer", "Accountant", etc.
    private String designation;

    @Column(name = "joining_date", nullable = false)
    // LocalDate (no time component) – an employee joins on a DATE, not a timestamp.
    private LocalDate joiningDate;


    @Column(name = "salary", precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "is_active", nullable = false)
    // Soft-delete pattern: instead of DELETE, we set is_active=false.
    private Boolean isActive = true;

    // ─── Audit fields (auto-populated by Spring Data JPA) ───────────
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ─── No-arg constructor (required by JPA) ──────────────────
    public Employee() {
    }

    // ─── Getter ──────────────────
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDepartment() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public Boolean getActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    // ─── Setter ──────────────────


    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
