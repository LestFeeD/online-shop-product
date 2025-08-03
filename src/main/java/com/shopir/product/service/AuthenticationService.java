package com.shopir.product.service;

import com.shopir.product.security.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails userDetails) {
            return userDetails.getIdUser();
        }
        throw   new IllegalArgumentException("User is not authenticated");
    }
}
