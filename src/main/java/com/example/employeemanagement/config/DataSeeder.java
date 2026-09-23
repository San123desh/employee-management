package com.example.employeemanagement.config;


import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * CommandLineRunner – runs ONCE after the Spring context is fully loaded.
 *
 * Why this exists:
 *   - H2 is in-memory → it's EMPTY every time you restart the app.
 *   - Without this, every restart means you have to manually POST employees
 *     just to test your GET endpoints.
 *   - This seeds a few realistic records so you can immediately see data
 *     in your API responses.
 *
 * Why CommandLineRunner (not @PostConstruct or ApplicationRunner)?
 *   - @PostConstruct runs too early (DB might not be ready).
 *   - CommandLineRunner runs AFTER the full context is up (DB, JPA, everything).
 *   - ApplicationRunner is the same but gives you an Arguments object (not needed here).
 *
 * In production you'd DELETE this class and use Flyway/Liquibase migrations.
 */


// It's a marker annotation that tells Spring: "Create an instance of this class and manage it as a bean."
//Without it, Spring doesn't know the class exists. With it, Spring:Finds the class during package scanning
//Creates one instance (singleton by default)
//Makes it available for injection into other beans 
@Component
public class DataSeeder implements CommandLineRunner {


    private final EmployeeRepository repository;

    //constructor injection (prefered over @Autowired for final fields
    public DataSeeder(EmployeeRepository repository){
        this.repository = repository;
    }


    @Override
    public void run(String... args){ //command-line arguments passed at startup (e.g., --port=9090).
        //only seed if the table is empty(so rerun don't create duplicates
        if(repository.count() > 0){
            return;
        }

        Employee motka = new Employee();
        motka.setFirstName("Kriteeka");
        motka.setLastName("Shrestha");
        motka.setEmail("kriteeka@gmail.com");
        motka.setPhone("9876543210");
        motka.setDepartment("IT");
        motka.setDesignation("Senior Software Engineer");
        motka.setJoiningDate(LocalDate.of(2021, 3, 15));
        motka.setSalary(new BigDecimal("85000.00"));
        motka.setActive(true);
        repository.save(motka);


        Employee Ram = new Employee();
        Ram.setFirstName("Ram");
        Ram.setLastName("ji");
        Ram.setEmail("Ram.ji@gmail.com");
        Ram.setPhone("9123456780");
        Ram.setDepartment("HR");
        Ram.setDesignation("HR Manager");
        Ram.setJoiningDate(LocalDate.of(2019, 7, 1));
        Ram.setSalary(new BigDecimal("72000.00"));
        Ram.setActive(true);
        repository.save(Ram);

        Employee amit = new Employee();
        amit.setFirstName("Amit");
        amit.setLastName("son");
        amit.setEmail("amit.son@gmail.com");
        amit.setPhone("9988776655");
        amit.setDepartment("Finance");
        amit.setDesignation("Accounts Officer");
        amit.setJoiningDate(LocalDate.of(2022, 1, 10));
        amit.setSalary(new BigDecimal("58000.00"));
        amit.setActive(true);
        repository.save(amit);

        Employee sita = new Employee();
        sita.setFirstName("sita");
        sita.setLastName("ma");
        sita.setEmail("sita.ma@gmail.com");
        sita.setPhone("9090909090");
        sita.setDepartment("IT");
        sita.setDesignation("Junior Developer");
        sita.setJoiningDate(LocalDate.of(2024, 6, 20));
        sita.setSalary(new BigDecimal("42000.00"));
        sita.setActive(true);
        repository.save(sita);

        // One INACTIVE employee (resigned) to test filtering
        Employee old = new Employee();
        old.setFirstName("Bajra");
        old.setLastName("man");
        old.setEmail("man.bajra@gmail.com");
        old.setPhone("8888777766");
        old.setDepartment("IT");
        old.setDesignation("System Administrator");
        old.setJoiningDate(LocalDate.of(2015, 11, 5));
        old.setSalary(new BigDecimal("65000.00"));
        old.setActive(false);  // ← resigned
        repository.save(old);


        System.out.println("══════════════════════════════════════════");
        System.out.println(" Seeded " + repository.count() + "employees into H2.");

        System.out.println("══════════════════════════════════════════");


    }

}














































