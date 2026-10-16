package com.doctify.clinicservice.service;

import com.doctify.clinicservice.dto.ClinicCreateRequest;
import com.doctify.clinicservice.dto.ClinicResponse;
import com.doctify.clinicservice.dto.ClinicUpdateRequest;
import com.doctify.clinicservice.entity.Clinic;
import com.doctify.clinicservice.exception.DuplicateResourceException;
import com.doctify.clinicservice.exception.ResourceNotFoundException;
import com.doctify.clinicservice.repository.ClinicRepository;
import com.doctify.clinicservice.repository.ClinicSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClinicService {

    private final ClinicRepository clinicRepository;

    @Transactional
    public ClinicResponse createClinic(ClinicCreateRequest request) {
        String name = request.getName().trim();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (clinicRepository.existsByName(name)) {
            throw new DuplicateResourceException("A clinic with name '" + name + "' already exists");
        }

        if (clinicRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("A clinic with email '" + email + "' already exists");
        }

        Clinic clinic = Clinic.builder()
                .name(name)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .email(email)
                .phone(request.getPhone().trim())
                .addressLine(request.getAddressLine().trim())
                .city(request.getCity().trim())
                .state(request.getState().trim())
                .postalCode(request.getPostalCode().trim())
                .operatingHours(request.getOperatingHours().trim())
                .servicesOffered(request.getServicesOffered().trim())
                .rating(5.0)
                .totalReviews(0)
                .active(true)
                .build();

        Clinic savedClinic = clinicRepository.save(clinic);
        log.info("Successfully created clinic with ID: {} and name: {}", savedClinic.getId(), savedClinic.getName());

        return mapToResponse(savedClinic);
    }

    @Transactional(readOnly = true)
    public ClinicResponse getClinicById(Long id) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + id));

        return mapToResponse(clinic);
    }

    @Transactional
    public ClinicResponse updateClinic(Long id, ClinicUpdateRequest request) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            String newName = request.getName().trim();
            if (!newName.equalsIgnoreCase(clinic.getName()) && clinicRepository.existsByName(newName)) {
                throw new DuplicateResourceException("A clinic with name '" + newName + "' already exists");
            }
            clinic.setName(newName);
        }

        if (request.getDescription() != null) {
            clinic.setDescription(request.getDescription().trim());
        }
        if (request.getPhone() != null) {
            clinic.setPhone(request.getPhone().trim());
        }
        if (request.getAddressLine() != null) {
            clinic.setAddressLine(request.getAddressLine().trim());
        }
        if (request.getCity() != null) {
            clinic.setCity(request.getCity().trim());
        }
        if (request.getState() != null) {
            clinic.setState(request.getState().trim());
        }
        if (request.getPostalCode() != null) {
            clinic.setPostalCode(request.getPostalCode().trim());
        }
        if (request.getOperatingHours() != null) {
            clinic.setOperatingHours(request.getOperatingHours().trim());
        }
        if (request.getServicesOffered() != null) {
            clinic.setServicesOffered(request.getServicesOffered().trim());
        }
        if (request.getActive() != null) {
            clinic.setActive(request.getActive());
        }

        Clinic updatedClinic = clinicRepository.save(clinic);
        log.info("Successfully updated clinic with ID: {}", updatedClinic.getId());

        return mapToResponse(updatedClinic);
    }

    @Transactional
    public void deleteClinic(Long id) {
        if (!clinicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clinic not found with ID: " + id);
        }
        clinicRepository.deleteById(id);
        log.info("Successfully deleted clinic with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<ClinicResponse> searchClinics(
            String query,
            String city,
            String service,
            Boolean active,
            Pageable pageable
    ) {
        Specification<Clinic> spec = ClinicSpecification.withFilters(query, city, service, active);
        return clinicRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<String> getCities() {
        return clinicRepository.findDistinctCities();
    }

    public ClinicResponse mapToResponse(Clinic clinic) {
        return ClinicResponse.builder()
                .id(clinic.getId())
                .name(clinic.getName())
                .description(clinic.getDescription())
                .email(clinic.getEmail())
                .phone(clinic.getPhone())
                .addressLine(clinic.getAddressLine())
                .city(clinic.getCity())
                .state(clinic.getState())
                .postalCode(clinic.getPostalCode())
                .operatingHours(clinic.getOperatingHours())
                .servicesOffered(clinic.getServicesOffered())
                .rating(clinic.getRating())
                .totalReviews(clinic.getTotalReviews())
                .active(clinic.isActive())
                .createdAt(clinic.getCreatedAt())
                .updatedAt(clinic.getUpdatedAt())
                .build();
    }
}
