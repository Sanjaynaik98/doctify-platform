package com.doctify.identityservice.config;

import com.doctify.identityservice.entity.Role;
import com.doctify.identityservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private static final List<String> DEFAULT_ROLES = List.of("PATIENT", "DOCTOR", "ADMIN");

    @Override
    public void run(String... args) {
        for (String roleName : DEFAULT_ROLES) {
            roleRepository.findByName(roleName).ifPresentOrElse(
                    role -> log.debug("Role '{}' already exists", roleName),
                    () -> {
                        roleRepository.save(new Role(roleName));
                        log.info("Initialized default role: {}", roleName);
                    }
            );
        }
    }
}
