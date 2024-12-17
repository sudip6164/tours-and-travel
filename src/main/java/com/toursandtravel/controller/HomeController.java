package com.toursandtravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.toursandtravel.model.User;
import com.toursandtravel.model.Tour;
import com.toursandtravel.repository.TourRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
    private TourRepository tRepo;
	
	@GetMapping("/")
    public String frontPage(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "index.html";
    }
	
	@GetMapping("/about")
    public String aboutPage(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "about.html";
    }
	
	@GetMapping("/contact")
    public String contactPage(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "contact.html";
    }
	
	@GetMapping("/bookingPage")
    public String bookingPage(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "bookingPage.html";
    }
    
	@GetMapping("/booknow")
	public String booknow(HttpSession session) {
	    if (session.getAttribute("user") != null) {
	        return "redirect:/bookingPage";
	    }
	    return "redirect:/login";
	}
	
	@GetMapping("/recommend")
    public String recommendPage(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        model.addAttribute("tourList", tRepo.findAll());
        return "recommend.html";
    }
	
	@GetMapping("/learnmore")
    public String learnmorePage(@RequestParam int id, HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
	    Tour tour=tRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid tour ID: " + id));
	    model.addAttribute("tour", tour);
        return "learn.html";
    }

}
