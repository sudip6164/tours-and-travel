package com.toursandtravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import com.toursandtravel.model.User;
import com.toursandtravel.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

@Controller
public class SignupController {
	@Autowired
	private UserRepository uRepo;
	
	@GetMapping("/")
	public String frontPage()
	{
		return "index.html";
	}
	
	@GetMapping("/signup")
	public String signup(){
		return "signup.html";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute User user, Model model) {
	    try {
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
	public String login(){
		return "login.html";
	}
	
	@PostMapping("/postlogin")
	public String postLogin(@ModelAttribute User u,Model model){
		if (uRepo.existsByUsernameAndPassword(u.getUsername(), u.getPassword())) {
			return "index.html";
		}
		return "index.html";	
	}
}
