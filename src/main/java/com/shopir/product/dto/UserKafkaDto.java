package com.shopir.product.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserKafkaDto implements Serializable {
    private Long idUser;
    private String email;
    private String password;
    private String role;


}
