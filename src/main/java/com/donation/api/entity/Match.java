package com.donation.api.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.PENDING;
    
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "pickup_date")
    private LocalDateTime pickupDate;
    
    @Column(name = "pickup_notes", columnDefinition = "TEXT")
    private String pickupNotes;
    
    @Column(name = "donor_notes", columnDefinition = "TEXT")
    private String donorNotes;
    
    @Column(name = "requester_rating")
    private Integer requesterRating;
    
    @Column(name = "donor_rating")
    private Integer donorRating;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;




    
    public enum MatchStatus {
        PENDING,     // Solicitação enviada, aguardando resposta do doador
        APPROVED,    // Doador aprovou a solicitação
        REJECTED,    // Doador rejeitou a solicitação
        IN_PROGRESS, // Doação em andamento (aprovada e sendo coordenada)
        COMPLETED,   // Doação concluída com sucesso
        CANCELLED    // Doação cancelada por qualquer uma das partes
    }
    
    public Match() {
        this.createdAt = LocalDateTime.now();
        this.requestedAt = LocalDateTime.now();
    }
    
    public Match(Donation donation, User requester, String message) {
        this();
        this.donation = donation;
        this.requester = requester;
        this.message = message;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        
        // Atualizar timestamp baseado no status
        if (this.status == MatchStatus.APPROVED || this.status == MatchStatus.REJECTED) {
            if (this.respondedAt == null) {
                this.respondedAt = LocalDateTime.now();
            }
        }
        
        if (this.status == MatchStatus.COMPLETED) {
            if (this.completedAt == null) {
                this.completedAt = LocalDateTime.now();
            }
        }
    }



    
    
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
    
    public MatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(MatchStatus status) {
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Donation getDonation() {
        return donation;
    }
    
    public void setDonation(Donation donation) {
        this.donation = donation;
    }
    
    public User getRequester() {
        return requester;
    }
    
    public void setRequester(User requester) {
        this.requester = requester;
    }
}
