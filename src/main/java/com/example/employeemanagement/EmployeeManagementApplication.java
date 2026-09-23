package com.example.employeemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableJpaAuditing tells Spring to look for @CreatedDate and @LastModifiedDate annotations on entities and fill them automatically before saving.
// No manual setCreatedAt(LocalDateTime.now()) calls needed.
@EnableJpaAuditing
public class EmployeeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }

}


