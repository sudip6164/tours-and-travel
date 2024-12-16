package com.toursandtravel.controller;

import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.toursandtravel.model.User;
import com.toursandtravel.model.ItineraryItem;
import com.toursandtravel.model.Tour;
import com.toursandtravel.repository.TourRepository;
import com.toursandtravel.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	@Autowired
    private UserRepository uRepo;
	
	@Autowired
    private TourRepository tRepo;
	
	@GetMapping("/admin/adminLogin")
    public String adminLogin() {
        return "admin/login.html";
    }

    @PostMapping("/admin/adminPostLogin")
    public String adminPostLogin(@ModelAttribute User u, Model model, HttpSession session) {
        User user = uRepo.findByUsername(u.getUsername());
        if (user != null && BCrypt.checkpw(u.getPassword(), user.getPassword())) {
        	session.setAttribute("user", user);
            return "admin/dashboard.html";
        } else {
            return "admin/login.html";
        }
    }

    @GetMapping("/admin/adminLogout")
    public String logout(HttpSession session) {
        // Invalidate the session
        session.invalidate();
        return "admin/login.html";
    }
    
    @GetMapping("/admin")
	public String adminDashboard(HttpSession session, Model model) {
	    // Check if the user session exists
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	    	model.addAttribute("user", user);
	        return "admin/dashboard.html";
	    }
	    return "redirect:/admin/adminLogin";
	}
    
    @GetMapping("/admin/tour_list")
   	public String toursList(HttpSession session, Model model) {
   		User user = (User) session.getAttribute("user");
   	    if (user != null) {
   	    	model.addAttribute("user", user);
            model.addAttribute("tourList", tRepo.findAll());
   	        return "admin/tour_list.html";
   	    }
   	    return "redirect:/admin/adminLogin";
   	}
    
    @GetMapping("/admin/add_tour_page")
    public String addTourPage(HttpSession session, Model model) {
    	User user = (User) session.getAttribute("user");
    	if (user != null) {
    		model.addAttribute("user", user);
    		return "admin/tour_form.html";
    	}
    	return "redirect:/admin/adminLogin";
    }

    @PostMapping("/admin/add_tour")
    public String addTour(
        @RequestParam String tourImageUrl,
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam double review,
        @RequestParam double price,
        @RequestParam String place,
        @RequestParam String duration,
        @RequestParam String startPoint,
        @RequestParam String endPoint,
        @RequestParam List<String> itinerary_day,
        @RequestParam List<String> itinerary_description,
        @RequestParam List<String> inclusion,
        @RequestParam List<String> exclusion,
        HttpSession session, Model model) {
        
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            Tour tour = new Tour();
            tour.setTourImageUrl(tourImageUrl);
            tour.setTitle(title);
            tour.setDescription(description);
            tour.setReview(review);
            tour.setPrice(price);
            tour.setPlace(place);
            tour.setDuration(duration);
            tour.setStartPoint(startPoint);
            tour.setEndPoint(endPoint);
            System.out.println("itinerary_day: " + itinerary_day);
            System.out.println("itinerary_description: " + itinerary_description);
            
            // Combine itinerary_day and itinerary_description
            List<ItineraryItem> itinerary = new ArrayList<>();
            for (int i = 0; i < itinerary_day.size(); i++) {
                ItineraryItem item = new ItineraryItem();
                item.setDay(itinerary_day.get(i));
                item.setDescription(itinerary_description.get(i));
                itinerary.add(item);
            }
            tour.setItinerary(itinerary);
            tour.setInclusion(inclusion);
            tour.setExclusion(exclusion);

            tRepo.save(tour);
            model.addAttribute("tourList", tRepo.findAll());
            return "redirect:/admin/tour_list";
        }
        return "redirect:/admin/adminLogin";
    }

}
