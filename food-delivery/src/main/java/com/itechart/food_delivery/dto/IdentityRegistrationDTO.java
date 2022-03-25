package com.itechart.food_delivery.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdentityRegistrationDTO {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
}