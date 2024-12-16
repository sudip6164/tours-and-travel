package com.toursandtravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toursandtravel.model.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer>{
}
