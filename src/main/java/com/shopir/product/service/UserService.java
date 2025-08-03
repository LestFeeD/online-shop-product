package com.shopir.product.service;

import com.shopir.product.dto.UserKafkaDto;
import com.shopir.product.security.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
   private final KafkaConsumerService kafkaConsumerService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(KafkaConsumerService kafkaConsumerService) {
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserKafkaDto user = kafkaConsumerService.getInfoUserByEmail(email);
            logger.info("User found: {}", user.getEmail());
            logger.info("Stored password: {}", user.getPassword());
            return MyUserDetails.buildUserDetails(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
}
