package com.toursandtravel.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import com.toursandtravel.model.User;
import com.toursandtravel.repository.UserRepository;

@Component
public class AdminUserInitializer {

    @Bean
    CommandLineRunner initializeAdmin(UserRepository userRepository) {
        return args -> {
            // Check if the admin user exists
            if (userRepository.findByUsername("admin") == null) {
                // Create the admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt())); // Hash the password
                admin.setEmail("sudippradhanadgj@gmail.com");
                admin.setRole("Admin");
                admin.setPhone("1234567890");
                admin.setAddress("Admin HQ");

                userRepository.save(admin);
                System.out.println("Admin user created successfully.");
            } else {
                System.out.println("Admin user already exists.");
            }
        };
    }
}
