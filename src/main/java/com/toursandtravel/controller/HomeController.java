package com.toursandtravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.toursandtravel.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@GetMapping("/")
    public String frontPage(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "index.html";
    }
	
	@GetMapping("/about")
    public String aboutPage() {
        return "about.html";
    }
	
	@GetMapping("/contact")
    public String contactPage() {
        return "contact.html";
    }
	
	@GetMapping("/bookingPage")
    public String bookingPage() {
        return "bookingPage.html";
	}
    
	@GetMapping("/booknow")
	public String booknow(HttpSession session) {
	    // Check if the user session exists
	    if (session.getAttribute("user") != null) {
	        // User is logged in, redirect to booking page
	        return "redirect:/bookingPage";
	    }
	    // User not logged in, redirect to login page
	    return "redirect:/login";
	}

}
