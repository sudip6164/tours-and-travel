package com.toursandtravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.toursandtravel.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	@GetMapping("/user")
	public String userProfile(HttpSession session, Model model) {
	    // Check if the user session exists
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        // User is logged in, redirect to booking page
	    	model.addAttribute("user", user);
	        return "user.html";
	    }
	    // User not logged in, redirect to login page
	    return "redirect:/login";
	}
}
