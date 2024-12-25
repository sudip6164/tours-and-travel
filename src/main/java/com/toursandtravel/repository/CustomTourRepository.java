package com.toursandtravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toursandtravel.model.CustomTour;

@Repository
public interface CustomTourRepository extends JpaRepository<CustomTour, Integer>{

}
