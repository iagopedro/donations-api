package com.donation.api.dto;

import com.donation.api.entity.Match;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class UpdateMatchStatusRequest {
    
    @NotNull(message = "Status é obrigatório")
    private Match.MatchStatus status;
    
    private LocalDateTime pickupDate;
    private String pickupNotes;
    private String donorNotes;
    private Integer rating;
    
    public UpdateMatchStatusRequest() {}
    
    public UpdateMatchStatusRequest(Match.MatchStatus status) {
        this.status = status;
    }
    
    // Getters and Setters
    public Match.MatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(Match.MatchStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getPickupDate() {
        return pickupDate;
    }
    
    public void setPickupDate(LocalDateTime pickupDate) {
        this.pickupDate = pickupDate;
    }
    
    public String getPickupNotes() {
        return pickupNotes;
    }
    
    public void setPickupNotes(String pickupNotes) {
        this.pickupNotes = pickupNotes;
    }
    
    public String getDonorNotes() {
        return donorNotes;
    }
    
    public void setDonorNotes(String donorNotes) {
        this.donorNotes = donorNotes;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
