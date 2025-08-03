package com.shopir.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopir.product.dto.UserKafkaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaConsumerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Map<String, CompletableFuture<UserKafkaDto>> pendingUserKafkaDtoRequests = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public UserKafkaDto getInfoUserByEmail(String email) throws Exception {
        CompletableFuture<UserKafkaDto> future = new CompletableFuture<>();
        pendingUserKafkaDtoRequests.put(email, future);

        kafkaTemplate.send("user-request", email);

        return future.get(20, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "user-response", groupId = "user-group")
    public void handleProductByIdResponse(String json) throws JsonProcessingException {
        UserKafkaDto dto = objectMapper.readValue(json, UserKafkaDto.class);
        String email = dto.getEmail(); // Должно быть поле в dto

        CompletableFuture<UserKafkaDto> future = pendingUserKafkaDtoRequests.remove(email);
        if (future != null) {
            future.complete(dto);
        }
    }

    public Map<String, CompletableFuture<UserKafkaDto>> getPendingRequests() {
        return pendingUserKafkaDtoRequests;
    }
}
