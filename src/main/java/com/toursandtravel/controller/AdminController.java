package com.toursandtravel.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.toursandtravel.model.User;
import com.toursandtravel.model.Booking;
import com.toursandtravel.model.CustomTour;
import com.toursandtravel.model.ItineraryItem;
import com.toursandtravel.model.Tour;
import com.toursandtravel.model.Review;
import com.toursandtravel.repository.BookingRepository;
import com.toursandtravel.repository.CustomTourRepository;
import com.toursandtravel.repository.ReviewRepository;
import com.toursandtravel.repository.TourRepository;
import com.toursandtravel.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	@Autowired
    private UserRepository uRepo;
	
	@Autowired
    private TourRepository tRepo;
	
	@Autowired
    private BookingRepository bRepo;
	
	@Autowired
	private ReviewRepository rRepo;
	
	@Autowired
	private CustomTourRepository customTourRepository;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@GetMapping("/admin/adminLogin")
    public String adminLogin() {
        return "admin/login.html";
    }

    @PostMapping("/admin/adminPostLogin")
    public String adminPostLogin(@ModelAttribute User u, Model model, HttpSession session) {
        User user = uRepo.findByUsername(u.getUsername());
        if (user != null && BCrypt.checkpw(u.getPassword(), user.getPassword())) {
        	if ("Admin".equals(user.getRole())) { // Check if the role is Admin
                session.setAttribute("user", user);
                int totalTours = (int) tRepo.count();
                int totalUsers = (int) uRepo.count();
                int totalBookings = (int) bRepo.count();
                model.addAttribute("totalTours", totalTours);
                model.addAttribute("totalUsers", totalUsers);
                model.addAttribute("totalBookings", totalBookings);
                
                session.setMaxInactiveInterval(3600); 
                return "admin/dashboard.html";
            } else {
                model.addAttribute("error", "You are not authorized to access the admin panel.");
                return "admin/login.html";
            }
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
	    	int totalBookings = (int) bRepo.count();
	    	model.addAttribute("totalTours", totalTours);
	        model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalBookings", totalBookings);
	        
	        return "admin/dashboard.html";
	    }
	    return "redirect:/admin/adminLogin";
	}
    
    @GetMapping("/admin/tour_list")
   	public String toursList(HttpSession session, Model model) {
   		User user = (User) session.getAttribute("user");
   	    if (user != null) {
   	    	model.addAttribute("user", user);
			// Fetch all tours
			List<Tour> tours = tRepo.findAll();

			// Compute the average review for each tour and update the database
			for (Tour tour : tours) {
				// Fetch average rating for this tour
				Double averageRating = rRepo.findAverageRatingByTourId(tour.getId());
				double finalRating = averageRating != null ? averageRating : 0.0;

				// Update the review field in the database
				tour.setReview(finalRating);
				tRepo.save(tour); // Persist the updated value in the database
			}
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
    	@RequestParam(value = "tourImage", required = false) MultipartFile tourImage,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "review", required = false) String reviewStr,
        @RequestParam(value = "price", required = false) String priceStr,
        @RequestParam(value = "place", required = false) String place,
        @RequestParam(value = "duration", required = false) String duration,
        @RequestParam(value = "startPoint", required = false) String startPoint,
        @RequestParam(value = "endPoint", required = false) String endPoint,
        @RequestParam(value = "itinerary_day", required = false) List<String> itinerary_day,
        @RequestParam(value = "itinerary_description", required = false) List<String> itinerary_description,
        @RequestParam(value = "inclusion", required = false) List<String> inclusion,
        @RequestParam(value = "exclusion", required = false) List<String> exclusion,
        HttpSession session, Model model) {
        
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            
            // Validate required fields
            List<String> errors = new ArrayList<>();
            
            // Normalize and validate strings
            title = (title != null) ? title.trim() : "";
            description = (description != null) ? description.trim() : "";
            place = (place != null) ? place.trim() : "";
            duration = (duration != null) ? duration.trim() : "";
            startPoint = (startPoint != null) ? startPoint.trim() : "";
            endPoint = (endPoint != null) ? endPoint.trim() : "";
            reviewStr = (reviewStr != null) ? reviewStr.trim() : "0.0";
            priceStr = (priceStr != null) ? priceStr.trim() : "";
            
            if (title.isEmpty()) {
                errors.add("Title is required.");
            }
            if (description.isEmpty()) {
                errors.add("Description is required.");
            }
            if (place.isEmpty()) {
                errors.add("Place is required.");
            }
            if (duration.isEmpty()) {
                errors.add("Duration is required.");
            }
            if (startPoint.isEmpty()) {
                errors.add("Start Point is required.");
            }
            if (endPoint.isEmpty()) {
                errors.add("End Point is required.");
            }
            if (tourImage == null || tourImage.isEmpty()) {
                errors.add("Tour Image is required.");
            }
            
            // Validate itinerary - check if at least one entry has both day and description
            boolean hasValidItinerary = false;
            if (itinerary_day != null && itinerary_description != null && 
                !itinerary_day.isEmpty() && !itinerary_description.isEmpty()) {
                int minSize = Math.min(itinerary_day.size(), itinerary_description.size());
                for (int i = 0; i < minSize; i++) {
                    String day = (itinerary_day.get(i) != null) ? itinerary_day.get(i).trim() : "";
                    String desc = (itinerary_description.get(i) != null) ? itinerary_description.get(i).trim() : "";
                    if (!day.isEmpty() && !desc.isEmpty()) {
                        hasValidItinerary = true;
                        break;
                    }
                }
            }
            if (!hasValidItinerary) {
                errors.add("At least one itinerary entry with both Day and Description is required.");
            }
            
            // Parse numeric values
            double review = 0.0;
            double price = 0.0;
            
            try {
                if (!reviewStr.isEmpty() && !reviewStr.equals("0.0")) {
                    review = Double.parseDouble(reviewStr);
                }
            } catch (NumberFormatException e) {
                errors.add("Invalid rating value.");
            }
            
            // Validate and parse price
            if (priceStr == null || priceStr.isEmpty()) {
                errors.add("Price is required.");
            } else {
                try {
                    price = Double.parseDouble(priceStr);
                    if (price <= 0) {
                        errors.add("Price must be greater than 0.");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Invalid price value. Please enter a valid number.");
                }
            }
            
            // If there are validation errors, return to form with error messages and preserve form values
            if (!errors.isEmpty()) {
                model.addAttribute("error", String.join("<br>", errors));
                // Preserve form values so user doesn't lose their input
                model.addAttribute("formTitle", title);
                model.addAttribute("formDescription", description);
                model.addAttribute("formPrice", priceStr);
                model.addAttribute("formPlace", place);
                model.addAttribute("formDuration", duration);
                model.addAttribute("formStartPoint", startPoint);
                model.addAttribute("formEndPoint", endPoint);
                model.addAttribute("formItineraryDay", itinerary_day);
                model.addAttribute("formItineraryDescription", itinerary_description);
                model.addAttribute("formInclusion", inclusion);
                model.addAttribute("formExclusion", exclusion);
                return "admin/tour_form.html";
            }
            
            Tour tour = new Tour();
    	    // Path to save the uploaded image (relative to static folder)
    	    String uploadDir = Paths.get("src", "main", "resources", "static", "img", "tour").toString();

    	    // Process image
    	    if (tourImage != null && !tourImage.isEmpty()) {
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
    	            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
    	            // Preserve form values when image upload fails
    	            model.addAttribute("formTitle", title);
    	            model.addAttribute("formDescription", description);
    	            model.addAttribute("formPrice", priceStr);
    	            model.addAttribute("formPlace", place);
    	            model.addAttribute("formDuration", duration);
    	            model.addAttribute("formStartPoint", startPoint);
    	            model.addAttribute("formEndPoint", endPoint);
    	            model.addAttribute("formItineraryDay", itinerary_day);
    	            model.addAttribute("formItineraryDescription", itinerary_description);
    	            model.addAttribute("formInclusion", inclusion);
    	            model.addAttribute("formExclusion", exclusion);
    	            return "admin/tour_form.html";
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
            
            // Combine itinerary_day and itinerary_description
            List<ItineraryItem> itinerary = new ArrayList<>();
            if (itinerary_day != null && itinerary_description != null) {
                int minSize = Math.min(itinerary_day.size(), itinerary_description.size());
                for (int i = 0; i < minSize; i++) {
                    String day = itinerary_day.get(i);
                    String desc = itinerary_description.get(i);
                    if (day != null && !day.trim().isEmpty() && 
                        desc != null && !desc.trim().isEmpty()) {
                        ItineraryItem item = new ItineraryItem();
                        item.setDay(day.trim());
                        item.setDescription(desc.trim());
                        itinerary.add(item);
                    }
                }
            }
            tour.setItinerary(itinerary);
            
            // Filter out empty inclusion and exclusion entries
            List<String> filteredInclusion = new ArrayList<>();
            if (inclusion != null) {
                for (String inc : inclusion) {
                    if (inc != null && !inc.trim().isEmpty()) {
                        filteredInclusion.add(inc.trim());
                    }
                }
            }
            tour.setInclusion(filteredInclusion);
            
            List<String> filteredExclusion = new ArrayList<>();
            if (exclusion != null) {
                for (String exc : exclusion) {
                    if (exc != null && !exc.trim().isEmpty()) {
                        filteredExclusion.add(exc.trim());
                    }
                }
            }
            tour.setExclusion(filteredExclusion);

            try {
                tRepo.save(tour);
                model.addAttribute("success", "Tour added successfully!");
                return "redirect:/admin/tour_list";
            } catch (Exception e) {
                model.addAttribute("error", "Failed to save tour: " + e.getMessage());
                return "admin/tour_form.html";
            }
        }
        return "redirect:/admin/adminLogin";
    }
    
    @GetMapping("/admin/delete_tour")
    public String deleteTour(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            // Find the tour first
            Tour tourToDelete = tRepo.findById(id).orElse(null);
            if (tourToDelete != null) {
                // Delete all reviews associated with this tour
                List<Review> tourReviews = rRepo.findByTour(tourToDelete);
                rRepo.deleteAll(tourReviews);
                
                // Delete all bookings associated with this tour
                List<Booking> tourBookings = bRepo.findByTour(tourToDelete);
                bRepo.deleteAll(tourBookings);
                
                // Delete all custom tours associated with this tour
                List<CustomTour> tourCustomTours = customTourRepository.findByTour(tourToDelete);
                customTourRepository.deleteAll(tourCustomTours);
                
                // Now delete the tour itself
                tRepo.deleteById(id);
            }
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
        User adminUser = (User) session.getAttribute("user");
        if (adminUser != null) {
            // Get the user to be deleted
            User userToDelete = uRepo.findById(id).orElse(null);
            if (userToDelete != null) {
                // Delete all bookings associated with this user
                List<Booking> userBookings = bRepo.findByUser(userToDelete);
                bRepo.deleteAll(userBookings);
                
                // Delete all custom tours associated with this user
                List<CustomTour> userCustomTours = customTourRepository.findByUser(userToDelete);
                customTourRepository.deleteAll(userCustomTours);
                
                // Delete all reviews associated with this user
                List<Review> userReviews = rRepo.findByUser(userToDelete);
                rRepo.deleteAll(userReviews);
                
                // Now delete the user
                uRepo.deleteById(id);
            }
            model.addAttribute("userList", uRepo.findAll());
            return "redirect:/admin/user_list"; // Redirect to the user list page
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
    
    @GetMapping("/admin/delete_booking")
    public String deleteBooking(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            bRepo.deleteById(id);
            model.addAttribute("bookingList", bRepo.findAll());
            return "redirect:/admin/booking_list"; 
        }
        return "redirect:/admin/adminLogin"; 
    }
    
    @GetMapping("/admin/update_booking_status")
    public String updateBookingStatus(@RequestParam int id, @RequestParam String status, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Booking booking = bRepo.findById(id).orElse(null);
            if (booking != null) {
                booking.setApprovalStatus(status); // Update status to 'Approved' or 'Denied'
                bRepo.save(booking); // Save the updated booking
                Tour tour=booking.getTour();
                String subject = "Booking Approval status ";
    	        String emailContent = "The tour " + tour.getTitle() + " you booked is " + status.toLowerCase() + "." + "\n Please contact us for any queries.";

    	        try {
					sendEmailToUser(user.getEmail(), subject, emailContent);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            return "redirect:/admin/booking_list";
        }
        return "redirect:/admin/adminLogin";
    }

	private void sendEmailToUser(String email, String subject, String emailContent) throws MessagingException {
	    MimeMessage message = javaMailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message);

	    helper.setSubject(subject);
	    helper.setText(emailContent, false);
	    helper.setTo(email); // Send to all admin emails
	    javaMailSender.send(message);
	}
	
    @GetMapping("/admin/change_user_role")
    public String changeUserRole(@RequestParam int id, @RequestParam String role, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            User u = uRepo.findById(id).orElse(null);
            if (u != null) {
                u.setRole(role); // Update status to 'Approved' or 'Denied'
                uRepo.save(u); // Save the updated booking
            }
            return "redirect:/admin/user_list";
        }
            return "redirect:/admin/adminLogin";
    }
    
    @GetMapping("/admin/review_list")
   	public String reviewList(HttpSession session, Model model) {
   		User user = (User) session.getAttribute("user");
   	    if (user != null) {
   	    	model.addAttribute("user", user);
            model.addAttribute("reviewList", rRepo.findAll());
   	        return "admin/review_list.html";
   	    }
   	    return "redirect:/admin/adminLogin";
   	}
    
    @GetMapping("/admin/delete_review")
    public String deletereview(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            rRepo.deleteById(id); // Delete the tour by ID
            model.addAttribute("reviewList", rRepo.findAll());
            return "redirect:/admin/review_list"; // Redirect to the tour list page
        }
        return "redirect:/admin/adminLogin"; // Redirect to login if session is invalid
    }
     
    @GetMapping("/admin/custom_tour_list")
   	public String customTourList(HttpSession session, Model model) {
   		User user = (User) session.getAttribute("user");
   	    if (user != null) {
   	    	model.addAttribute("user", user);
            model.addAttribute("customTourList", customTourRepository.findAll());
   	        return "admin/custom_tour_list.html";
   	    }
   	    return "redirect:/admin/adminLogin";
   	}
    
    @GetMapping("/admin/delete_custom_tour")
    public String deleteCustomTour(@RequestParam int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
        	customTourRepository.deleteById(id);
            model.addAttribute("customTourList", customTourRepository.findAll());
            return "redirect:/admin/custom_tour_list"; 
        }
        return "redirect:/admin/adminLogin"; 
    }
    
    @GetMapping("/admin/update_custom_tour_booking_status")
    public String updateCustomTourBookingStatus(@RequestParam int id, @RequestParam String status, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            CustomTour customTour = customTourRepository.findById(id).orElse(null);
            if (customTour != null) {
            	customTour.setApprovalStatus(status); // Update status to 'Approved' or 'Denied'
            	customTourRepository.save(customTour); // Save the updated booking
                Tour tour=customTour.getTour();
                String subject = "Booking Approval status ";
    	        String emailContent = "The customized tour " + tour.getTitle() + " you booked is " + status.toLowerCase() + "." + "\n Please contact us for any queries.";

    	        try {
					sendEmailToUser(user.getEmail(), subject, emailContent);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            return "redirect:/admin/custom_tour_list";
        }
        return "redirect:/admin/adminLogin";
    }
}
