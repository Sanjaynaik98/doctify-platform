package com.doctify.clinicservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicCreateRequest {

    @NotBlank(message = "Clinic name is required")
    @Size(max = 150, message = "Clinic name must not exceed 150 characters")
    private String name;

    @Size(max = 1500, message = "Description must not exceed 1500 characters")
    private String description;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phone;

    @NotBlank(message = "Address line is required")
    @Size(max = 250, message = "Address line must not exceed 250 characters")
    private String addressLine;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @NotBlank(message = "Operating hours are required")
    @Size(max = 150, message = "Operating hours must not exceed 150 characters")
    private String operatingHours;

    @NotBlank(message = "Services offered are required")
    @Size(max = 500, message = "Services offered must not exceed 500 characters")
    private String servicesOffered;
}
