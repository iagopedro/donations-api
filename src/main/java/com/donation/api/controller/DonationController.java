package com.donation.api.controller;

import com.donation.api.dto.DonationRequest;
import com.donation.api.dto.DonationResponse;
import com.donation.api.entity.Donation;
import com.donation.api.service.DonationService;
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
@RequestMapping("/api/donations")
@Tag(name = "Doações", description = "Endpoints para gerenciamento de doações")
public class DonationController {
    
    @Autowired
    private DonationService donationService;
    
    @Operation(summary = "Listar doações", description = "Lista todas as doações com filtros opcionais e paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de doações retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<DonationResponse>> getAllDonations(
            @Parameter(description = "Categoria da doação") @RequestParam(required = false) String category,
            @Parameter(description = "Cidade") @RequestParam(required = false) String city,
            @Parameter(description = "Estado (UF)") @RequestParam(required = false) String state,
            @Parameter(description = "Status da doação") @RequestParam(required = false) Donation.DonationStatus status,
            @Parameter(description = "Termo de busca") @RequestParam(required = false) String search,
            @Parameter(description = "Página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Page<DonationResponse> donations = donationService.getAllDonations(
            category, city, state, status, search, page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(donations);
    }
    
    @Operation(summary = "Criar nova doação", description = "Cria uma nova doação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Doação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> createDonation(@Valid @RequestBody DonationRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            DonationResponse donation = donationService.createDonation(email, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(donation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(summary = "Obter doação por ID", description = "Retorna detalhes de uma doação específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doação encontrada"),
        @ApiResponse(responseCode = "404", description = "Doação não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationById(@Parameter(description = "ID da doação") @PathVariable Long id) {
        try {
            DonationResponse donation = donationService.getDonationById(id);
            return ResponseEntity.ok(donation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(summary = "Atualizar doação", description = "Atualiza uma doação existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doação atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão para editar esta doação"),
        @ApiResponse(responseCode = "404", description = "Doação não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonation(
            @Parameter(description = "ID da doação") @PathVariable Long id,
            @Valid @RequestBody DonationRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            DonationResponse donation = donationService.updateDonation(id, email, request);
            return ResponseEntity.ok(donation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(summary = "Deletar doação", description = "Remove uma doação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Doação deletada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão para deletar esta doação"),
        @ApiResponse(responseCode = "404", description = "Doação não encontrada"),
        @ApiResponse(responseCode = "409", description = "Doação possui solicitações ativas")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonation(@Parameter(description = "ID da doação") @PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            donationService.deleteDonation(id, email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(summary = "Listar minhas doações", description = "Lista doações do usuário logado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de doações do usuário"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my")
    public ResponseEntity<?> getUserDonations() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            List<DonationResponse> donations = donationService.getUserDonations(email);
            return ResponseEntity.ok(donations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias disponíveis")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = donationService.getCategories();
        return ResponseEntity.ok(categories);
    }
    
    @Operation(summary = "Listar cidades", description = "Retorna todas as cidades disponíveis")
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        List<String> cities = donationService.getCities();
        return ResponseEntity.ok(cities);
    }
}
