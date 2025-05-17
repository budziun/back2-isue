package com.lombard.lombard.utils;

import com.lombard.lombard.models.Employee;
import com.lombard.lombard.models.Role;
import com.lombard.lombard.repositories.EmployeeRepository;
import com.lombard.lombard.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class StartupUserInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StartupUserInitializer(
            EmployeeRepository employeeRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() == 0) {
            createDefaultAdmin();
        }
    }

    private void createDefaultAdmin() {
        try {
            Role adminRole = ensureAdminRole();

            Employee admin = new Employee();
            admin.setLogin("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setFirstName("system");
            admin.setLastName("admin");
            admin.setRole(adminRole);
            admin.setHireDate(LocalDate.now());
            admin.setStatus(Employee.EmployeeStatus.active);

            employeeRepository.save(admin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private Role ensureAdminRole() {
        Optional<Role> adminRoleOpt = roleRepository.findByRoleName("admin");

        if (adminRoleOpt.isPresent()) {
            return adminRoleOpt.get();
        }

        Role adminRole = new Role();
        adminRole.setRoleName("admin");
        adminRole.setDescription("Can Rm -rf / You");
        adminRole.setPermissionsLevel(100);

        return roleRepository.save(adminRole);
    }
}