package com.toursandtravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toursandtravel.model.Review;
import com.toursandtravel.model.Tour;
import com.toursandtravel.model.User;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>{

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.tour.id = :tourId")
    Double findAverageRatingByTourId(@Param("tourId") int tourId);
	
	List<Review> findByUser(User user);
	
	List<Review> findByTour(Tour tour);

}
