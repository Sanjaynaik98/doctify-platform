package com.doctify.clinicservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicResponse {
    private Long id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private String operatingHours;
    private String servicesOffered;
    private Double rating;
    private Integer totalReviews;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
