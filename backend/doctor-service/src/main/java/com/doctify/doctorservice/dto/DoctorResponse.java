package com.doctify.doctorservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private Integer experienceYears;
    private String qualification;
    private BigDecimal consultationFee;
    private String bio;
    private Double rating;
    private Integer totalReviews;
    private Long clinicId;
    private String city;
    private boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
