package com.toursandtravel.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.toursandtravel.model.User;
import com.toursandtravel.model.ItineraryItem;
import com.toursandtravel.model.Tour;
import com.toursandtravel.repository.BookingRepository;
import com.toursandtravel.repository.TourRepository;
import com.toursandtravel.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	@Autowired
    private UserRepository uRepo;
	
	@Autowired
    private TourRepository tRepo;
	
	@Autowired
    private BookingRepository bRepo;
	
	@GetMapping("/admin/adminLogin")
    public String adminLogin() {
        return "admin/login.html";
    }

    @PostMapping("/admin/adminPostLogin")
    public String adminPostLogin(@ModelAttribute User u, Model model, HttpSession session) {
        User user = uRepo.findByUsername(u.getUsername());
        if (user != null && BCrypt.checkpw(u.getPassword(), user.getPassword())) {
        	session.setAttribute("user", user);
        	int totalTours = (int) tRepo.count();
	    	int totalUsers = (int) uRepo.count();
	    	int totalBookings = (int) bRepo.count();
	    	model.addAttribute("totalTours", totalTours);
	        model.addAttribute("totalUsers", totalUsers);
	        model.addAttribute("totalBookings", totalBookings);
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
	    	
	    	int totalTours = (int) tRepo.count();
	    	int totalUsers = (int) uRepo.count();
	    	model.addAttribute("totalTours", totalTours);
	        model.addAttribute("totalUsers", totalUsers);
	        
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
    	@RequestParam("tourImage") MultipartFile tourImage,
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
    	    // Path to save the uploaded image (relative to static folder)
    	    String uploadDir = Paths.get("src", "main", "resources", "static", "img", "tour").toString();

    	    // Process image
    	    if (!tourImage.isEmpty()) {
    	        try {
    	            // Get the original filename
    	            String imageName = StringUtils.cleanPath(tourImage.getOriginalFilename());
    	            
    	            // Create a path for the image file in the upload directory
    	            Path imagePath = Paths.get(uploadDir, imageName);

    	            // Create the directory if it doesn't exist
    	            Files.createDirectories(imagePath.getParent());

    	            // Save the image file
    	            tourImage.transferTo(imagePath);
    	            tour.setTourImageUrl("/img/tour/" + imageName);  
    	        } catch (IOException e) {
    	            model.addAttribute("error", "Failed to upload image.");
    	            return "redirect:/admin/add_tour";  
    	        }
    	    }
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
    
    @GetMapping("/admin/delete_tour")
    public String deleteTour(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            tRepo.deleteById(id); // Delete the tour by ID
            model.addAttribute("tourList", tRepo.findAll());
            return "redirect:/admin/tour_list"; // Redirect to the tour list page
        }
        return "redirect:/admin/adminLogin"; // Redirect to login if session is invalid
    }
    
 // Edit Tour Details
    @GetMapping("/admin/edit_tour_details")
    public String editTourDetails(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("tour", tRepo.findById(id).orElse(null));
            return "admin/edit_tour_details.html";
        }
        return "redirect:/admin/adminLogin";
    }

    @PostMapping("/admin/update_tour_details")
    public String updateTourDetails(
        @RequestParam int id,
        @RequestParam(required = false) MultipartFile tourImage,
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam double review,
        @RequestParam double price,
        HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            Tour tour = tRepo.findById(id).orElse(null);
            if (tour != null) {
                tour.setTitle(title);
                tour.setDescription(description);
                tour.setReview(review);
                tour.setPrice(price);
                // Process image if uploaded
                if (tourImage != null && !tourImage.isEmpty()) {
                    try {
                        String uploadDir = Paths.get("src", "main", "resources", "static", "img", "tour").toString();
                        String imageName = StringUtils.cleanPath(tourImage.getOriginalFilename());
                        Path imagePath = Paths.get(uploadDir, imageName);
                        Files.createDirectories(imagePath.getParent());
                        tourImage.transferTo(imagePath);
                        tour.setTourImageUrl("/img/tour/" + imageName);
                    } catch (IOException e) {
                        model.addAttribute("error", "Image upload failed.");
                        return "redirect:/admin/edit_tour_details?id=" + id;
                    }
                }
                tRepo.save(tour);
            }
            return "redirect:/admin/tour_list";
        }
        return "redirect:/admin/adminLogin";
    }

    // Edit Overview Details
    @GetMapping("/admin/edit_overview_details")
    public String editOverviewDetails(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("tour", tRepo.findById(id).orElse(null));
            return "admin/edit_overview_details.html";
        }
        return "redirect:/admin/adminLogin";
    }

    @PostMapping("/admin/update_overview_details")
    public String updateOverviewDetails(
        @RequestParam int id,
        @RequestParam String place,
        @RequestParam String duration,
        @RequestParam String startPoint,
        @RequestParam String endPoint,
        HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            Tour tour = tRepo.findById(id).orElse(null);
            if (tour != null) {
                tour.setPlace(place);
                tour.setDuration(duration);
                tour.setStartPoint(startPoint);
                tour.setEndPoint(endPoint);
                tRepo.save(tour);
            }
            return "redirect:/admin/tour_list";
        }
        return "redirect:/admin/adminLogin";
    }

    // Edit Itinerary Details
    @GetMapping("/admin/edit_itinerary_details")
    public String editItineraryDetails(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("tour", tRepo.findById(id).orElse(null));
            return "admin/edit_itinerary_details.html";
        }
        return "redirect:/admin/adminLogin";
    }

    @PostMapping("/admin/update_itinerary_details")
    public String updateItineraryDetails(
        @RequestParam int id,
        @RequestParam List<String> itinerary_day,
        @RequestParam List<String> itinerary_description,
        HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            Tour tour = tRepo.findById(id).orElse(null);
            if (tour != null) {
                List<ItineraryItem> itinerary = new ArrayList<>();
                for (int i = 0; i < itinerary_day.size(); i++) {
                    ItineraryItem item = new ItineraryItem();
                    item.setDay(itinerary_day.get(i));
                    item.setDescription(itinerary_description.get(i));
                    itinerary.add(item);
                }
                tour.setItinerary(itinerary);
                tRepo.save(tour);
            }
            return "redirect:/admin/tour_list";
        }
        return "redirect:/admin/adminLogin";
    }

    // Edit Inclusion & Exclusion Details
    @GetMapping("/admin/edit_inclusion_exclusion")
    public String editInclusionExclusion(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("tour", tRepo.findById(id).orElse(null));
            return "admin/edit_inclusion_exclusion.html";
        }
        return "redirect:/admin/adminLogin";
    }

    @PostMapping("/admin/update_inclusion_exclusion")
    public String updateInclusionExclusion(
        @RequestParam int id,
        @RequestParam List<String> inclusion,
        @RequestParam List<String> exclusion,
        HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            Tour tour = tRepo.findById(id).orElse(null);
            if (tour != null) {
                tour.setInclusion(inclusion);
                tour.setExclusion(exclusion);
                tRepo.save(tour);
            }
            return "redirect:/admin/tour_list";
        }
        return "redirect:/admin/adminLogin";
    }
    
    @GetMapping("/admin/user_list")
   	public String userList(HttpSession session, Model model) {
   		User user = (User) session.getAttribute("user");
   	    if (user != null) {
   	    	model.addAttribute("user", user);
            model.addAttribute("userList", uRepo.findAll());
   	        return "admin/user_list.html";
   	    }
   	    return "redirect:/admin/adminLogin";
   	}
    
    @GetMapping("/admin/delete_user")
    public String deleteUser(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            uRepo.deleteById(id); // Delete the tour by ID
            model.addAttribute("userList", uRepo.findAll());
            return "redirect:/admin/user_list"; // Redirect to the tour list page
        }
        return "redirect:/admin/adminLogin"; // Redirect to login if session is invalid
    }

    @GetMapping("/admin/booking_list")
   	public String bookingList(HttpSession session, Model model) {
   		User user = (User) session.getAttribute("user");
   	    if (user != null) {
   	    	model.addAttribute("user", user);
            model.addAttribute("bookingList", bRepo.findAll());
   	        return "admin/booking_list.html";
   	    }
   	    return "redirect:/admin/adminLogin";
   	}
}
