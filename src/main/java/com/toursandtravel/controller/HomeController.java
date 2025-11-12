package com.toursandtravel.controller;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.toursandtravel.model.User;
import com.toursandtravel.model.Booking;
import com.toursandtravel.model.Contact;
import com.toursandtravel.model.CustomTour;
import com.toursandtravel.model.Review;
import com.toursandtravel.model.Tour;
import com.toursandtravel.repository.BookingRepository;
import com.toursandtravel.repository.ContactRepository;
import com.toursandtravel.repository.CustomTourRepository;
import com.toursandtravel.repository.ReviewRepository;
import com.toursandtravel.repository.TourRepository;
import com.toursandtravel.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
    private TourRepository tRepo;
	
	@Autowired
	private BookingRepository bRepo;
	
	@Autowired
	private ContactRepository cRepo;
	
	@Autowired
	private UserRepository uRepo;
	
	@Autowired
	private ReviewRepository rRepo;
	
	@Autowired
	private CustomTourRepository customTourRepository;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@GetMapping("/")
    public String frontPage(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
	    List<Tour> topDestinations = tRepo.findTop5ByOrderByReviewDesc(); // Fetch top 5
	    model.addAttribute("topDestinations", topDestinations);
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
	
	@GetMapping("/contactSuccess")
    public String contactSuccess(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "contactSuccess.html";
    }
	
	@GetMapping("/succefull.html")
    public String paymentSuccess(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "succefull.html";
    }
	
	@GetMapping("/payment-failure.html")
    public String paymentFailure(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "payment-failure.html";
    }
	
	@PostMapping("/sendContactMessage")
	public String sendContactMessage(@RequestParam("name") String name,
	                                 @RequestParam("email") String email,
	                                 @RequestParam("phone") String phone,
	                                 @RequestParam("message") String message,
	                                 Model model) {
	    try {
	        // Save the contact details to the database
	        Contact contact = new Contact();
	        contact.setName(name);
	        contact.setEmail(email);
	        contact.setPhone(phone);
	        contact.setMessage(message);
	        cRepo.save(contact);

	        // Get all admin users
	        List<User> admins = uRepo.findByRole("Admin");
	        if (admins.isEmpty()) {
	            throw new RuntimeException("No admin users found to notify.");
	        }

	        // Collect admin email addresses
	        String[] adminEmails = admins.stream()
	                                     .map(User::getEmail)
	                                     .toArray(String[]::new);

	        // Send the email
	        String subject = "New Contact Form Submission from " + name;
	        String emailContent = "Name: " + name + "\nEmail: " + email + "\nPhone: " + phone + "\nMessage: " + message;

	        sendEmailToAdmins(adminEmails, subject, emailContent);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "redirect:/contactSuccess";
	}

	// Helper method to send emails to admins
	private void sendEmailToAdmins(String[] adminEmails, String subject, String emailContent) throws MessagingException {
	    Objects.requireNonNull(adminEmails, "Admin email list must not be null");
	    Objects.requireNonNull(subject, "Email subject must not be null");
	    Objects.requireNonNull(emailContent, "Email content must not be null");
	    
	    MimeMessage message = javaMailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message);

	    helper.setSubject(subject);
	    helper.setText(emailContent, false);
	    helper.setTo(adminEmails); // Send to all admin emails
	    javaMailSender.send(message);
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

	@GetMapping("/bookingPage")
    public String bookingPage(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "bookingPage.html";
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

	        // Get all admin users
	        List<User> admins = uRepo.findByRole("Admin");
	        
	        if (admins.isEmpty()) {
	        	log.warn("Booking {} saved for user {}, but no admin users found to notify.", booking.getId(), user.getUsername());
	        } else {
	        	// Collect admin email addresses
		        String[] adminEmails = admins.stream()
		                                     .map(User::getEmail)
		                                     .toArray(String[]::new);
	
		        // Send the email
		        String subject = "Tour Approval Request";
		        String emailContent = user.getUsername() + " just booked " + tour.getTitle() + " the tour." + "\n Please approve or deny this request.";
	
		        try {
					sendEmailToAdmins(adminEmails, subject, emailContent);
				} catch (MessagingException e) {
					log.error("Failed to send booking notification email to admins.", e);
				}
	        }

	        model.addAttribute("success", "Booking request submitted successfully. Please wait for approval.");
	        return "redirect:/bookingPage"; // Redirect to booking page
		
	    }
	 
		@GetMapping("/review")
	    public String reviewPage(HttpSession session, Model model) {
			User user = (User) session.getAttribute("user");
		    if (user != null) {
		        model.addAttribute("user", user);
		    }
		    List<Review> reviews = rRepo.findAll();
		    model.addAttribute("randomReviews", reviews);
	        return "review.html";
	    }
		
		@GetMapping("/add_review_page")
	    public String addReviewPage(HttpSession session, Model model) {
			User user = (User) session.getAttribute("user");
		    if (user != null) {
		    	List<Tour> tours = tRepo.findAll();
		        model.addAttribute("tours", tours);
		        model.addAttribute("user", user);
		        return "add_review_page.html";
		    }
		    return "login.html";
	    }
		
		@PostMapping("/add_review")
		public String addReview(@RequestParam("tourId") int tourId,
		                           @RequestParam("reviewText") String reviewText,
		                           @RequestParam("starRating") double starRating,
		                           HttpSession session,
		                           Model model) {
		    User user = (User) session.getAttribute("user");
		    if (user != null) {
			    Tour tour = tRepo.findById(tourId)
			                              .orElseThrow(() -> new RuntimeException("Tour not found"));
	
			    Review review = new Review();
			    review.setUser(user);
			    review.setTour(tour);
			    review.setReviewText(reviewText);
			    review.setRating(starRating);
	
			    rRepo.save(review);
	
			    return "redirect:/review";
		    }
		    return "redirect:/login";
		}
		
		@GetMapping("/custom_tour_page")
	    public String addCustomTourPage(@RequestParam("tourId") int tourId, HttpSession session, Model model) {
			User user = (User) session.getAttribute("user");
		    if (user != null) {		    
		        Tour tour = tRepo.findById(tourId).orElse(null);
		        model.addAttribute("tour", tour);
		        model.addAttribute("user", user);
		        return "customPkg.html";
		    }
		    return "login.html";
	    }
		
		@PostMapping("/request_custom_tour")
		 public String requestCustomTour(@RequestParam int tourId, @RequestParam int userId, HttpSession session, @ModelAttribute CustomTour customTour, Model model) {
				User user = (User) session.getAttribute("user");
				if (user != null) {
					Tour tour = tRepo.findById(tourId).orElseThrow(() -> new RuntimeException("Tour not found"));

					customTour.setUser(user);
					customTour.setTour(tour);
					customTour.setTitle(tour.getTitle());

					customTourRepository.save(customTour);
			
						// Get all admin users
					List<User> admins = uRepo.findByRole("Admin");
					if (admins.isEmpty()) {
						throw new RuntimeException("No admin users found to notify.");
					}
	
					// Collect admin email addresses
					String[] adminEmails = admins.stream().map(User::getEmail).toArray(String[]::new);
	
					// Send the email
					String subject = "Tour Approval Request";
					String emailContent = user.getUsername() + " just booked " + tour.getTitle() + " the tour."
							+ "\n Please approve or deny this request.";
	
					try {
						sendEmailToAdmins(adminEmails, subject, emailContent);
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        model.addAttribute("success", "Booking request submitted successfully. Please wait for approval.");
					return "redirect:/bookingPage";
				}

		        return "redirect:/custom_tour_page"; // Redirect to booking page
			
		    }
}
