package com.toursandtravel.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String tourImageUrl;
    private String title;
    private String description;
    private double review;
    private double price;
    private String place;
    private String duration;
    private String startPoint;
    private String endPoint;
   
    @ElementCollection
    private List<ItineraryItem> itinerary;

    @ElementCollection
    private List<String> inclusion;

    @ElementCollection
    private List<String> exclusion;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTourImageUrl() {
		return tourImageUrl;
	}

	public void setTourImageUrl(String tourImageUrl) {
		this.tourImageUrl = tourImageUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getReview() {
		return review;
	}

	public void setReview(double review) {
		this.review = review;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public List<ItineraryItem> getItinerary() {
		return itinerary;
	}

	public void setItinerary(List<ItineraryItem> itinerary) {
		this.itinerary = itinerary;
	}

	public List<String> getInclusion() {
		return inclusion;
	}

	public void setInclusion(List<String> inclusion) {
		this.inclusion = inclusion;
	}

	public List<String> getExclusion() {
		return exclusion;
	}

	public void setExclusion(List<String> exclusion) {
		this.exclusion = exclusion;
	}

}
