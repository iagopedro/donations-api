package com.donation.api.service;

import com.donation.api.dto.UpdateProfileRequest;
import com.donation.api.dto.UserResponse;
import com.donation.api.entity.User;
import com.donation.api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmailAndEnabledTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return modelMapper.map(user, UserResponse.class);
    }
    
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmailAndEnabledTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Atualizar apenas os campos não nulos
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }
        
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress().trim());
        }
        
        if (request.getCity() != null) {
            user.setCity(request.getCity().trim());
        }
        
        if (request.getState() != null) {
            user.setState(request.getState().trim());
        }
        
        if (request.getZipCode() != null) {
            user.setZipCode(request.getZipCode().trim());
        }
        
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }
        
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl().trim());
        }
        
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponse.class);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmailAndEnabledTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
