package com.donation.api.dto;

import com.donation.api.entity.Match;

import java.time.LocalDateTime;

public class MatchResponse {
    
    private Long id;
    private String message;
    private Match.MatchStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
    private LocalDateTime completedAt;
    private LocalDateTime pickupDate;
    private String pickupNotes;
    private String donorNotes;
    private Integer requesterRating;
    private Integer donorRating;
    private LocalDateTime createdAt;
    private DonationResponse donation;
    private UserResponse requester;
    
    public MatchResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Match.MatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(Match.MatchStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
    
    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }
    
    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
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
    
    public Integer getRequesterRating() {
        return requesterRating;
    }
    
    public void setRequesterRating(Integer requesterRating) {
        this.requesterRating = requesterRating;
    }
    
    public Integer getDonorRating() {
        return donorRating;
    }
    
    public void setDonorRating(Integer donorRating) {
        this.donorRating = donorRating;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public DonationResponse getDonation() {
        return donation;
    }
    
    public void setDonation(DonationResponse donation) {
        this.donation = donation;
    }
    
    public UserResponse getRequester() {
        return requester;
    }
    
    public void setRequester(UserResponse requester) {
        this.requester = requester;
    }
}
