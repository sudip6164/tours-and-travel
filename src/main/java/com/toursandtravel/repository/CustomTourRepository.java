package com.toursandtravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toursandtravel.model.CustomTour;
import com.toursandtravel.model.Tour;
import com.toursandtravel.model.User;
import java.util.List;

@Repository
public interface CustomTourRepository extends JpaRepository<CustomTour, Integer>{
	List<CustomTour> findByUser(User user);
	List<CustomTour> findByTour(Tour tour);
}
