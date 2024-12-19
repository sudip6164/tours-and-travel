package com.toursandtravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.toursandtravel.model.User;
import com.toursandtravel.model.Booking;
import com.toursandtravel.model.Tour;
import com.toursandtravel.repository.BookingRepository;
import com.toursandtravel.repository.TourRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
    private TourRepository tRepo;
	
	@Autowired
	private BookingRepository bRepo;
	
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
   
	@GetMapping("/booknow")
	public String booknow(HttpSession session) {
	    if (session.getAttribute("user") != null) {
	        return "redirect:/recommend";
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

	 @PostMapping("/bookTour")
	    public String bookTour(@RequestParam int tourId, HttpSession session, Model model) {
	        User user = (User) session.getAttribute("user");

	        if (user == null) {
	            return "redirect:/login"; // Redirect to login if user is not logged in
	        }

	        Tour tour = tRepo.findById(tourId).orElse(null);

	        Booking booking = new Booking();
	        booking.setUser(user);
	        booking.setTour(tour);

	        bRepo.save(booking);

	        model.addAttribute("success", "Booking request submitted successfully. Please wait for approval.");
	        return "bookingPage.html"; // Redirect to booking page
	    }
}
