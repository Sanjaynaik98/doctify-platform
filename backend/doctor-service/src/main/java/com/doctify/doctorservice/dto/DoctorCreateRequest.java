package com.doctify.doctorservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorCreateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phone;

    @NotBlank(message = "Specialization is required")
    @Size(max = 80, message = "Specialization must not exceed 80 characters")
    private String specialization;

    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 70, message = "Years of experience must be realistic")
    private Integer experienceYears;

    @NotBlank(message = "Qualification is required")
    @Size(max = 150, message = "Qualification must not exceed 150 characters")
    private String qualification;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fee must be non-negative")
    private BigDecimal consultationFee;

    @Size(max = 1500, message = "Bio must not exceed 1500 characters")
    private String bio;

    private Long clinicId;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
}
