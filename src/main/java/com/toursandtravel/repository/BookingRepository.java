package com.toursandtravel.repository;

import com.toursandtravel.model.Booking;
import com.toursandtravel.model.Tour;
import com.toursandtravel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
	List<Booking> findByUser(User user);
	List<Booking> findByTour(Tour tour);
}
