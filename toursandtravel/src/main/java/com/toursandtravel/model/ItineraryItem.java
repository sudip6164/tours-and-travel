package com.toursandtravel.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ItineraryItem {
    private String day;
    private String description;
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
   
}
