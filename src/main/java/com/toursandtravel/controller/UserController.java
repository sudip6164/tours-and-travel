package com.toursandtravel.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import com.toursandtravel.model.User;
import com.toursandtravel.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	 @Autowired
	 private UserRepository uRepo;
	 
	@GetMapping("/user")
	public String userProfile(HttpSession session, Model model) {
	    // Check if the user session exists
		User sessionUser = (User) session.getAttribute("user");
	    if (sessionUser != null) {
	        // Use session user directly - it's updated immediately after save, no need to reload
	    	model.addAttribute("user", sessionUser);
	        return "user.html";
	    }
	    // User not logged in, redirect to login page
	    return "redirect:/login";
	}
	
	@GetMapping("/editProfilePage")
	public String editProfilePage(HttpSession session, Model model) {
	    // Check if the user session exists
		User sessionUser = (User) session.getAttribute("user");
	    if (sessionUser != null) {
	        // Use session user directly - no need to reload from database
	        model.addAttribute("user", sessionUser);
	        return "editProfile.html";
	    }
	    // User not logged in, redirect to login page
	    return "redirect:/login";
	}
	
	@PostMapping("/editProfile")
	public String addCar(HttpSession session, 
	                     @RequestParam("id") int id,
	                     @RequestParam("username") String username,
	                     @RequestParam("email") String email,
	                     @RequestParam("phone") String phone,
	                     @RequestParam("address") String address,
	                     @RequestParam("password") String password,
	                     @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture, 
	                     Model model) {

	    // Load existing user from database to preserve existing data
	    User existingUser = uRepo.findById(id).orElse(null);
	    if (existingUser == null) {
	        model.addAttribute("error", "User not found.");
	        return "redirect:/editProfilePage";
	    }

	    // Update user fields
	    existingUser.setUsername(username);
	    existingUser.setEmail(email);
	    existingUser.setPhone(phone);
	    existingUser.setAddress(address);
	    existingUser.setPassword(password); // Password should already be hashed, but we preserve it

	    // Path to save the uploaded image (separate uploads folder)
	    String uploadDir = Paths.get("src", "main", "resources", "static", "uploads", "profile").toString();

	    // Process image only if a new one is uploaded
	    if (profilePicture != null && !profilePicture.isEmpty()) {
	        try {
	            // Get the original filename
	            String originalFilename = profilePicture.getOriginalFilename();
	            if (originalFilename == null || originalFilename.isEmpty()) {
	                model.addAttribute("error", "Invalid image file name.");
	                return "redirect:/editProfilePage";
	            }
	            String imageName = StringUtils.cleanPath(originalFilename);
	            
	            // Create a path for the image file in the upload directory
	            Path imagePath = Paths.get(uploadDir, imageName);

	            // Create the directory if it doesn't exist
	            Files.createDirectories(imagePath.getParent());

	            // Save the image file - transferTo writes the file completely
	            profilePicture.transferTo(imagePath);
	            
	            // Verify file was written (ensure it exists and is readable)
	            // This ensures the file is fully written to disk before proceeding
	            if (!Files.exists(imagePath) || !Files.isReadable(imagePath)) {
	                model.addAttribute("error", "Failed to verify image upload.");
	                return "redirect:/editProfilePage";
	            }

	            // Save the image path to the user object (the URL should be relative for serving)
	            existingUser.setProfilePictureUrl("/uploads/profile/" + imageName);
	        } catch (IOException e) {
	            model.addAttribute("error", "Failed to upload image.");
	            return "redirect:/editProfilePage";  // Return to the form if there was an error
	        }
	    }
	    // If no new image is uploaded, existingUser keeps its existing profilePictureUrl

	    // Save user details to the database
	    User savedUser = uRepo.save(existingUser);
	    
	    // Update session immediately with the saved user object (has latest data including image URL)
	    session.setAttribute("user", savedUser);

	    return "redirect:/user";  // Redirect to user profile page
	}
}
