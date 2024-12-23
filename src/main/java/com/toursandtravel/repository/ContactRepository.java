package com.toursandtravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toursandtravel.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
}
