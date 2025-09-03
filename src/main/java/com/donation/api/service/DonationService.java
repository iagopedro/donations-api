package com.donation.api.service;

import com.donation.api.dto.DonationRequest;
import com.donation.api.dto.DonationResponse;
import com.donation.api.dto.UserResponse;
import com.donation.api.entity.Donation;
import com.donation.api.entity.User;
import com.donation.api.repository.DonationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationService {
    
    @Autowired
    private DonationRepository donationRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public Page<DonationResponse> getAllDonations(
            String category, 
            String city, 
            String state, 
            Donation.DonationStatus status,
            String search,
            int page, 
            int size, 
            String sortBy, 
            String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Donation> donations = donationRepository.findWithFilters(
            category, city, state, status, search, pageable);
        
        return donations.map(this::convertToResponse);
    }
    
    public DonationResponse createDonation(String userEmail, DonationRequest request) {
        User donor = userService.findByEmail(userEmail);
        
        Donation donation = new Donation();
        donation.setTitle(request.getTitle());
        donation.setDescription(request.getDescription());
        donation.setCategory(request.getCategory());
        donation.setCondition(request.getCondition());
        donation.setQuantity(request.getQuantity());
        donation.setLocation(request.getLocation());
        donation.setCity(request.getCity());
        donation.setState(request.getState());
        donation.setZipCode(request.getZipCode());
        donation.setImageUrls(request.getImageUrls());
        donation.setPickupInstructions(request.getPickupInstructions());
        donation.setExpiresAt(request.getExpiresAt());
        donation.setDonor(donor);
        
        Donation savedDonation = donationRepository.save(donation);
        return convertToResponse(savedDonation);
    }
    
    public DonationResponse getDonationById(Long id) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada"));
        
        return convertToResponse(donation);
    }
    
    public DonationResponse updateDonation(Long id, String userEmail, DonationRequest request) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada"));
        
        // Verificar se o usuário é o dono da doação
        if (!donation.getDonor().getEmail().equals(userEmail)) {
            throw new RuntimeException("Você não tem permissão para editar esta doação");
        }
        
        // Atualizar campos
        donation.setTitle(request.getTitle());
        donation.setDescription(request.getDescription());
        donation.setCategory(request.getCategory());
        donation.setCondition(request.getCondition());
        donation.setQuantity(request.getQuantity());
        donation.setLocation(request.getLocation());
        donation.setCity(request.getCity());
        donation.setState(request.getState());
        donation.setZipCode(request.getZipCode());
        donation.setImageUrls(request.getImageUrls());
        donation.setPickupInstructions(request.getPickupInstructions());
        donation.setExpiresAt(request.getExpiresAt());
        
        Donation updatedDonation = donationRepository.save(donation);
        return convertToResponse(updatedDonation);
    }
    
    public void deleteDonation(Long id, String userEmail) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada"));
        
        // Verificar se o usuário é o dono da doação
        if (!donation.getDonor().getEmail().equals(userEmail)) {
            throw new RuntimeException("Você não tem permissão para deletar esta doação");
        }
        
        // Verificar se não há matches pendentes ou aprovados
        if (donation.getMatches() != null && !donation.getMatches().isEmpty()) {
            boolean hasActiveMatches = donation.getMatches().stream()
                .anyMatch(match -> match.getStatus() == com.donation.api.entity.Match.MatchStatus.PENDING ||
                                 match.getStatus() == com.donation.api.entity.Match.MatchStatus.APPROVED ||
                                 match.getStatus() == com.donation.api.entity.Match.MatchStatus.IN_PROGRESS);
            
            if (hasActiveMatches) {
                throw new RuntimeException("Não é possível deletar doação com solicitações ativas");
            }
        }
        
        donationRepository.delete(donation);
    }
    
    public List<DonationResponse> getUserDonations(String userEmail) {
        User user = userService.findByEmail(userEmail);
        List<Donation> donations = donationRepository.findByDonor(user);
        
        return donations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<String> getCategories() {
        return donationRepository.findDistinctCategories();
    }
    
    public List<String> getCities() {
        return donationRepository.findDistinctCities();
    }
    
    private DonationResponse convertToResponse(Donation donation) {
        DonationResponse response = modelMapper.map(donation, DonationResponse.class);
        UserResponse donorResponse = modelMapper.map(donation.getDonor(), UserResponse.class);
        response.setDonor(donorResponse);
        return response;
    }
}
