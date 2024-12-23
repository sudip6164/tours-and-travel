package com.toursandtravel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.toursandtravel.model.User;
import com.toursandtravel.model.Booking;
import com.toursandtravel.model.Contact;
import com.toursandtravel.model.Tour;
import com.toursandtravel.repository.BookingRepository;
import com.toursandtravel.repository.ContactRepository;
import com.toursandtravel.repository.TourRepository;
import com.toursandtravel.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
    private TourRepository tRepo;
	
	@Autowired
	private BookingRepository bRepo;
	
	@Autowired
	private ContactRepository cRepo;
	
	@Autowired
	private UserRepository uRepo;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
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
	
	@GetMapping("/contactSuccess")
    public String contactSuccess(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
	    if (user != null) {
	        model.addAttribute("user", user);
	    }
        return "contactSuccess.html";
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
	            throw new RuntimeException("No admin users found to notify.");
	        }

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
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	        return "review.html";
	    }
		
		@GetMapping("/add_review_page")
	    public String addReviewPage(HttpSession session, Model model) {
			User user = (User) session.getAttribute("user");
		    if (user != null) {
		        model.addAttribute("user", user);
		    }
	        return "add_review_page.html";
	    }
}
