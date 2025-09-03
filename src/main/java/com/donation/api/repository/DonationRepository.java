package com.donation.api.repository;

import com.donation.api.entity.Donation;
import com.donation.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    
    List<Donation> findByDonor(User donor);
    
    Page<Donation> findByDonor(User donor, Pageable pageable);
    
    @Query("SELECT d FROM Donation d WHERE " +
           "(:category IS NULL OR d.category = :category) AND " +
           "(:city IS NULL OR d.city = :city) AND " +
           "(:state IS NULL OR d.state = :state) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Donation> findWithFilters(
        @Param("category") String category,
        @Param("city") String city,
        @Param("state") String state,
        @Param("status") Donation.DonationStatus status,
        @Param("search") String search,
        Pageable pageable
    );
    
    List<Donation> findByStatus(Donation.DonationStatus status);
    
    List<Donation> findByCategory(String category);
    
    @Query("SELECT d FROM Donation d WHERE d.city = :city AND d.status = :status")
    List<Donation> findByCityAndStatus(@Param("city") String city, @Param("status") Donation.DonationStatus status);
    
    @Query("SELECT DISTINCT d.category FROM Donation d ORDER BY d.category")
    List<String> findDistinctCategories();
    
    @Query("SELECT DISTINCT d.city FROM Donation d WHERE d.city IS NOT NULL ORDER BY d.city")
    List<String> findDistinctCities();
}
