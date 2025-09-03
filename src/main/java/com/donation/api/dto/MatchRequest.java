package com.donation.api.dto;

import jakarta.validation.constraints.Size;

public class MatchRequest {
    
    @Size(max = 500, message = "Mensagem deve ter no máximo 500 caracteres")
    private String message;
    
    public MatchRequest() {}
    
    public MatchRequest(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
