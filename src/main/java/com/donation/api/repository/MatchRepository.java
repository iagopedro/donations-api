package com.donation.api.repository;

import com.donation.api.entity.Donation;
import com.donation.api.entity.Match;
import com.donation.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findByRequester(User requester);
    
    Page<Match> findByRequester(User requester, Pageable pageable);
    
    List<Match> findByDonation(Donation donation);
    
    Page<Match> findByDonation(Donation donation, Pageable pageable);
    
    @Query("SELECT m FROM Match m WHERE m.donation.donor = :donor")
    List<Match> findByDonor(@Param("donor") User donor);
    
    @Query("SELECT m FROM Match m WHERE m.donation.donor = :donor")
    Page<Match> findByDonor(@Param("donor") User donor, Pageable pageable);
    
    List<Match> findByStatus(Match.MatchStatus status);
    
    @Query("SELECT m FROM Match m WHERE m.requester = :requester AND m.status = :status")
    List<Match> findByRequesterAndStatus(@Param("requester") User requester, @Param("status") Match.MatchStatus status);
    
    @Query("SELECT m FROM Match m WHERE m.donation.donor = :donor AND m.status = :status")
    List<Match> findByDonorAndStatus(@Param("donor") User donor, @Param("status") Match.MatchStatus status);
    
    @Query("SELECT m FROM Match m WHERE m.donation = :donation AND m.requester = :requester")
    Optional<Match> findByDonationAndRequester(@Param("donation") Donation donation, @Param("requester") User requester);
    
    @Query("SELECT m FROM Match m WHERE m.donation = :donation AND m.status IN :statuses")
    List<Match> findByDonationAndStatusIn(@Param("donation") Donation donation, @Param("statuses") List<Match.MatchStatus> statuses);
    
    boolean existsByDonationAndRequester(Donation donation, User requester);
}
