package com.doctify.doctorservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorUpdateRequest {

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phone;

    @Size(max = 80, message = "Specialization must not exceed 80 characters")
    private String specialization;

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 70, message = "Years of experience must be realistic")
    private Integer experienceYears;

    @Size(max = 150, message = "Qualification must not exceed 150 characters")
    private String qualification;

    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fee must be non-negative")
    private BigDecimal consultationFee;

    @Size(max = 1500, message = "Bio must not exceed 1500 characters")
    private String bio;

    private Long clinicId;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    private Boolean available;
}
