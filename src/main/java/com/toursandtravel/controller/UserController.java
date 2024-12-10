package com.toursandtravel.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        // User is logged in, redirect to profile page
	    	model.addAttribute("user", user);
	        return "user.html";
	    }
	    // User not logged in, redirect to login page
	    return "redirect:/login";
	}
	
	@GetMapping("/editProfilePage")
	public String editProfilePage(HttpSession session, Model model) {
	    // Check if the user session exists
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        // User is logged in, redirect to edit profile
	    	model.addAttribute("user", user);
	        return "editProfile.html";
	    }
	    // User not logged in, redirect to login page
	    return "redirect:/login";
	}
	
	@PostMapping("/editProfile")
	public String addCar(HttpSession session, @ModelAttribute User user, 
	                     @RequestParam("profilePicture") MultipartFile profilePicture, 
	                     Model model) {

	    // Path to save the uploaded image (relative to static folder)
	    String uploadDir = Paths.get("src", "main", "resources", "static", "img", "profile").toString();

	    // Process image
	    if (!profilePicture.isEmpty()) {
	        try {
	            // Get the original filename
	            String imageName = StringUtils.cleanPath(profilePicture.getOriginalFilename());
	            
	            // Create a path for the image file in the upload directory
	            Path imagePath = Paths.get(uploadDir, imageName);

	            // Create the directory if it doesn't exist
	            Files.createDirectories(imagePath.getParent());

	            // Save the image file
	            profilePicture.transferTo(imagePath);

	            // Save the image path to the user object (the URL should be relative for serving)
	            user.setProfilePictureUrl("/img/profile/" + imageName);  // You can serve images from static folder using this URL
	        } catch (IOException e) {
	            model.addAttribute("error", "Failed to upload image.");
	            return "redirect:/editProfilePage";  // Return to the form if there was an error
	        }
	    }

	    // Save user details to the database
	    uRepo.save(user);  // Save the user object with the image URL
	    
	 // Update session with the latest user object
	    session.setAttribute("user", user);

	    model.addAttribute("message", "Profile updated successfully!");
	    return "redirect:/user";  // Redirect to the page where cars are listed
	}
}
