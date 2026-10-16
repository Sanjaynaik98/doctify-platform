package com.doctify.clinicservice.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicUpdateRequest {

    @Size(max = 150, message = "Clinic name must not exceed 150 characters")
    private String name;

    @Size(max = 1500, message = "Description must not exceed 1500 characters")
    private String description;

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phone;

    @Size(max = 250, message = "Address line must not exceed 250 characters")
    private String addressLine;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Size(max = 150, message = "Operating hours must not exceed 150 characters")
    private String operatingHours;

    @Size(max = 500, message = "Services offered must not exceed 500 characters")
    private String servicesOffered;

    private Boolean active;
}
