package com.donation.api.service;

import com.donation.api.dto.MatchRequest;
import com.donation.api.dto.MatchResponse;
import com.donation.api.dto.UpdateMatchStatusRequest;
import com.donation.api.entity.Donation;
import com.donation.api.entity.Match;
import com.donation.api.entity.User;
import com.donation.api.repository.DonationRepository;
import com.donation.api.repository.MatchRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private DonationRepository donationRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public MatchResponse requestDonation(Long donationId, String requesterEmail, MatchRequest request) {
        User requester = userService.findByEmail(requesterEmail);
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada"));
        
        // Verificar se a doação está disponível
        if (donation.getStatus() != Donation.DonationStatus.AVAILABLE) {
            throw new RuntimeException("Esta doação não está mais disponível");
        }
        
        // Verificar se o solicitante não é o próprio doador
        if (donation.getDonor().getEmail().equals(requesterEmail)) {
            throw new RuntimeException("Você não pode solicitar sua própria doação");
        }
        
        // Verificar se já existe uma solicitação deste usuário para esta doação
        if (matchRepository.existsByDonationAndRequester(donation, requester)) {
            throw new RuntimeException("Você já solicitou esta doação");
        }
        
        Match match = new Match();
        match.setDonation(donation);
        match.setRequester(requester);
        match.setMessage(request.getMessage());
        match.setStatus(Match.MatchStatus.PENDING);
        
        Match savedMatch = matchRepository.save(match);
        return convertToResponse(savedMatch);
    }
    
    public List<MatchResponse> getUserMatches(String userEmail) {
        User user = userService.findByEmail(userEmail);
        List<Match> matches = matchRepository.findByRequester(user);
        
        return matches.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public Page<MatchResponse> getUserMatchesPaginated(String userEmail, int page, int size) {
        User user = userService.findByEmail(userEmail);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<Match> matches = matchRepository.findByRequester(user, pageable);
        return matches.map(this::convertToResponse);
    }
    
    public List<MatchResponse> getDonorMatches(String donorEmail) {
        User donor = userService.findByEmail(donorEmail);
        List<Match> matches = matchRepository.findByDonor(donor);
        
        return matches.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public MatchResponse updateMatchStatus(Long matchId, String userEmail, UpdateMatchStatusRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        
        User currentUser = userService.findByEmail(userEmail);
        
        // Verificar permissões baseadas no status e usuário
        validateStatusUpdatePermission(match, currentUser, request.getStatus());
        
        // Atualizar status
        match.setStatus(request.getStatus());
        
        // Atualizar campos específicos baseados no status
        updateMatchFields(match, request);
        
        // Atualizar status da doação se necessário
        updateDonationStatus(match);
        
        Match updatedMatch = matchRepository.save(match);
        return convertToResponse(updatedMatch);
    }
    
    public List<MatchResponse> getMatchesByDonation(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada"));
        
        List<Match> matches = matchRepository.findByDonation(donation);
        return matches.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private void validateStatusUpdatePermission(Match match, User currentUser, Match.MatchStatus newStatus) {
        String currentUserEmail = currentUser.getEmail();
        String donorEmail = match.getDonation().getDonor().getEmail();
        String requesterEmail = match.getRequester().getEmail();
        
        switch (newStatus) {
            case APPROVED:
            case REJECTED:
                // Apenas o doador pode aprovar ou rejeitar
                if (!currentUserEmail.equals(donorEmail)) {
                    throw new RuntimeException("Apenas o doador pode aprovar ou rejeitar solicitações");
                }
                break;
                
            case IN_PROGRESS:
                // Doador ou solicitante podem marcar como em andamento
                if (!currentUserEmail.equals(donorEmail) && !currentUserEmail.equals(requesterEmail)) {
                    throw new RuntimeException("Apenas o doador ou solicitante podem marcar como em andamento");
                }
                break;
                
            case COMPLETED:
                // Ambos podem marcar como concluído
                if (!currentUserEmail.equals(donorEmail) && !currentUserEmail.equals(requesterEmail)) {
                    throw new RuntimeException("Apenas o doador ou solicitante podem marcar como concluído");
                }
                break;
                
            case CANCELLED:
                // Ambos podem cancelar
                if (!currentUserEmail.equals(donorEmail) && !currentUserEmail.equals(requesterEmail)) {
                    throw new RuntimeException("Apenas o doador ou solicitante podem cancelar");
                }
                break;
                
            default:
                throw new RuntimeException("Status inválido");
        }
    }
    
    private void updateMatchFields(Match match, UpdateMatchStatusRequest request) {
        if (request.getPickupDate() != null) {
            match.setPickupDate(request.getPickupDate());
        }
        
        if (request.getPickupNotes() != null) {
            match.setPickupNotes(request.getPickupNotes());
        }
        
        if (request.getDonorNotes() != null) {
            match.setDonorNotes(request.getDonorNotes());
        }
        
        if (request.getRating() != null) {
            // Determinar se é rating do doador ou solicitante baseado no contexto
            match.setRequesterRating(request.getRating());
        }
    }
    
    private void updateDonationStatus(Match match) {
        if (match.getStatus() == Match.MatchStatus.APPROVED) {
            // Marcar doação como reservada
            match.getDonation().setStatus(Donation.DonationStatus.RESERVED);
            donationRepository.save(match.getDonation());
            
            // Rejeitar outros matches pendentes
            List<Match> otherMatches = matchRepository.findByDonationAndStatusIn(
                match.getDonation(), 
                Arrays.asList(Match.MatchStatus.PENDING)
            );
            
            otherMatches.stream()
                .filter(m -> !m.getId().equals(match.getId()))
                .forEach(m -> {
                    m.setStatus(Match.MatchStatus.REJECTED);
                    m.setRespondedAt(LocalDateTime.now());
                });
            
            matchRepository.saveAll(otherMatches);
            
        } else if (match.getStatus() == Match.MatchStatus.COMPLETED) {
            // Marcar doação como concluída
            match.getDonation().setStatus(Donation.DonationStatus.COMPLETED);
            donationRepository.save(match.getDonation());
            
        } else if (match.getStatus() == Match.MatchStatus.CANCELLED && 
                   match.getDonation().getStatus() == Donation.DonationStatus.RESERVED) {
            // Se foi cancelado e a doação estava reservada, voltar para disponível
            match.getDonation().setStatus(Donation.DonationStatus.AVAILABLE);
            donationRepository.save(match.getDonation());
        }
    }
    
    private MatchResponse convertToResponse(Match match) {
        MatchResponse response = modelMapper.map(match, MatchResponse.class);
        
        // Converter dados da doação e usuários
        response.setDonation(modelMapper.map(match.getDonation(), com.donation.api.dto.DonationResponse.class));
        response.setRequester(modelMapper.map(match.getRequester(), com.donation.api.dto.UserResponse.class));
        
        return response;
    }
}
