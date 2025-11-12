package com.toursandtravel.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mindrot.jbcrypt.BCrypt;

import com.toursandtravel.model.User;
import com.toursandtravel.repository.UserRepository;

@Configuration
public class AdminUserInitializer {

    @Bean
    CommandLineRunner initializeAdmin(UserRepository userRepository) {
        return args -> {
            try {
                System.out.println("=== Starting Admin User Initialization ===");
                
                // Check if the admin user exists by username
                User existingAdmin = userRepository.findByUsername("admin");
                
                // Also check by email as fallback
                if (existingAdmin == null) {
                    existingAdmin = userRepository.findByEmail("inforahulstoursandtravels@gmail.com");
                }
                
                if (existingAdmin == null) {
                    System.out.println("Admin user not found. Creating new admin user...");
                    
                    // Create the admin user
                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt())); // Hash the password
                    admin.setEmail("inforahulstoursandtravels@gmail.com");
                    admin.setRole("Admin");
                    admin.setPhone("1234567890");
                    admin.setAddress("Admin HQ");

                    // Save and flush to ensure it's persisted immediately
                    User savedAdmin = userRepository.saveAndFlush(admin);
                    
                    // Verify the save was successful
                    if (savedAdmin != null && savedAdmin.getId() > 0) {
                        System.out.println("✓ Admin user created successfully!");
                        System.out.println("  - ID: " + savedAdmin.getId());
                        System.out.println("  - Username: " + savedAdmin.getUsername());
                        System.out.println("  - Email: " + savedAdmin.getEmail());
                        System.out.println("  - Role: " + savedAdmin.getRole());
                        
                        // Double-check by querying again
                        User verifyAdmin = userRepository.findById(savedAdmin.getId()).orElse(null);
                        if (verifyAdmin != null) {
                            System.out.println("✓ Verification: Admin user confirmed in database.");
                        } else {
                            System.err.println("✗ ERROR: Admin user verification failed after save!");
                        }
                    } else {
                        System.err.println("✗ ERROR: Admin user was not saved properly!");
                    }
                } else {
                    System.out.println("Admin user already exists:");
                    System.out.println("  - ID: " + existingAdmin.getId());
                    System.out.println("  - Username: " + existingAdmin.getUsername());
                    System.out.println("  - Email: " + existingAdmin.getEmail());
                    System.out.println("  - Role: " + existingAdmin.getRole());
                    
                    // Ensure the role is set to Admin (in case it was changed)
                    boolean needsUpdate = false;
                    if (!"Admin".equals(existingAdmin.getRole())) {
                        existingAdmin.setRole("Admin");
                        needsUpdate = true;
                    }
                    
                    // Ensure username is "admin" (case-sensitive check)
                    if (!"admin".equals(existingAdmin.getUsername())) {
                        existingAdmin.setUsername("admin");
                        needsUpdate = true;
                    }
                    
                    if (needsUpdate) {
                        userRepository.saveAndFlush(existingAdmin);
                        System.out.println("✓ Admin user updated successfully.");
                    }
                }
                
                // Final verification: Try to find admin user
                User finalCheck = userRepository.findByUsername("admin");
                if (finalCheck != null) {
                    System.out.println("✓ Final verification: Admin user found in database.");
                    System.out.println("  - ID: " + finalCheck.getId());
                    System.out.println("  - Username: " + finalCheck.getUsername());
                    System.out.println("  - Email: " + finalCheck.getEmail());
                    System.out.println("  - Role: " + finalCheck.getRole());
                } else {
                    System.err.println("✗ ERROR: Final verification failed! Admin user not found.");
                    System.err.println("  Attempting to find by email...");
                    User emailCheck = userRepository.findByEmail("inforahulstoursandtravels@gmail.com");
                    if (emailCheck != null) {
                        System.err.println("  Found user by email, but username mismatch!");
                        System.err.println("  Current username: " + emailCheck.getUsername());
                    }
                }
                
                System.out.println("=== Admin User Initialization Complete ===\n");
                
            } catch (Exception e) {
                System.err.println("✗ ERROR creating admin user: " + e.getMessage());
                e.printStackTrace();
                System.err.println("=== Admin User Initialization Failed ===\n");
            }
        };
    }
}
