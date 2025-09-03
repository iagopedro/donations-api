package com.donation.api.controller;

import com.donation.api.dto.MatchRequest;
import com.donation.api.dto.MatchResponse;
import com.donation.api.dto.UpdateMatchStatusRequest;
import com.donation.api.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Matches", description = "Endpoints para gerenciamento de solicitações de doações")
public class MatchController {
    
    @Autowired
    private MatchService matchService;
    
    @Operation(summary = "Solicitar doação", description = "Cria uma nova solicitação para uma doação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Doação não encontrada")
    })
    @PostMapping
    public ResponseEntity<?> requestDonation(
            @Parameter(description = "ID da doação") @RequestParam Long donationId,
            @Valid @RequestBody MatchRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            MatchResponse match = matchService.requestDonation(donationId, email, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(match);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(summary = "Minhas solicitações", description = "Lista todas as solicitações feitas pelo usuário logado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitações retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping("/my")
    public ResponseEntity<?> getMyMatches(
            @Parameter(description = "Página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            if (page >= 0 && size > 0) {
                Page<MatchResponse> matches = matchService.getUserMatchesPaginated(email, page, size);
                return ResponseEntity.ok(matches);
            } else {
                List<MatchResponse> matches = matchService.getUserMatches(email);
                return ResponseEntity.ok(matches);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(summary = "Solicitações para minhas doações", description = "Lista solicitações recebidas para doações do usuário logado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitações retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedMatches() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            List<MatchResponse> matches = matchService.getDonorMatches(email);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(summary = "Atualizar status da solicitação", description = "Atualiza o status de uma solicitação (aprovar, rejeitar, etc.)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão para alterar este status"),
        @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateMatchStatus(
            @Parameter(description = "ID da solicitação") @PathVariable Long id,
            @Valid @RequestBody UpdateMatchStatusRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            MatchResponse match = matchService.updateMatchStatus(id, email, request);
            return ResponseEntity.ok(match);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(summary = "Listar solicitações de uma doação", description = "Lista todas as solicitações para uma doação específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitações retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Doação não encontrada")
    })
    @GetMapping("/donation/{donationId}")
    public ResponseEntity<?> getMatchesByDonation(
            @Parameter(description = "ID da doação") @PathVariable Long donationId) {
        try {
            List<MatchResponse> matches = matchService.getMatchesByDonation(donationId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
}
