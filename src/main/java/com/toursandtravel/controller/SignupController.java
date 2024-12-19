package com.toursandtravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.toursandtravel.model.User;
import com.toursandtravel.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class SignupController {
    @Autowired
    private UserRepository uRepo;

    @Autowired
    private JavaMailSender emailSender;

    private Map<String, String> otpMap = new HashMap<>();

    @GetMapping("/signup")
    public String signup() {
        return "signup.html";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
        	user.setRole("Tourist");
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            uRepo.save(user);
            return "login.html";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "signup.html";
    }

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @PostMapping("/postlogin")
    public String postLogin(@ModelAttribute User u, Model model, HttpSession session) {
        User user = uRepo.findByUsername(u.getUsername());
        if (user != null && BCrypt.checkpw(u.getPassword(), user.getPassword())) {
        	session.setAttribute("user", user);
            return "index.html";
        } else {
            model.addAttribute("loginError", "Invalid username or password");
            return "login.html";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate the session
        session.invalidate();
        return "login.html";
    }
    
    @GetMapping("/forgotPasswordPage")
    public String forgotPasswordPage() {
        return "forgotPassword.html";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        User user = uRepo.findByEmail(email);
        if (user == null) {
            model.addAttribute("invalidEmail", true);
            return "forgotPassword.html";
        }

        String otp = generateOTP();
        otpMap.put(email, otp);
        sendOTPEmail(email, otp);

        model.addAttribute("email", email);
        return "verifyOTP.html";
    }

    private String generateOTP() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private void sendOTPEmail(String email, String otp) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(email);
            helper.setSubject("Password Reset OTP");
            helper.setText("Your OTP for password reset is: " + otp + "\n\nYour OTP will expire in 5 minutes.");
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        String storedOTP = otpMap.get(email);
        if (storedOTP != null && storedOTP.equals(otp)) {
            model.addAttribute("email", email);
            return "resetPassword.html";
        } else {
            model.addAttribute("invalidOTP", true);
            return "forgotPassword.html";
        }
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
        User user = uRepo.findByEmail(email);
        if (user != null) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            user.setPassword(hashedPassword);
            uRepo.save(user);
            otpMap.remove(email);
            model.addAttribute("passwordResetSuccess", true);
            return "login.html";
        }
        return "resetPassword.html";
    }
}
