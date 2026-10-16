package com.doctify.clinicservice.service;

import com.doctify.clinicservice.dto.ClinicCreateRequest;
import com.doctify.clinicservice.dto.ClinicResponse;
import com.doctify.clinicservice.dto.ClinicUpdateRequest;
import com.doctify.clinicservice.entity.Clinic;
import com.doctify.clinicservice.exception.DuplicateResourceException;
import com.doctify.clinicservice.exception.ResourceNotFoundException;
import com.doctify.clinicservice.repository.ClinicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicServiceTest {

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private ClinicService clinicService;

    private Clinic testClinic;
    private ClinicCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testClinic = Clinic.builder()
                .id(1L)
                .name("Metro Healthcare Center")
                .description("Multi-specialty primary and urgent care clinic")
                .email("contact@metrohealth.com")
                .phone("+1-555-0144")
                .addressLine("450 Lexington Ave")
                .city("New York")
                .state("NY")
                .postalCode("10017")
                .operatingHours("Mon-Sun: 8:00 AM - 9:00 PM")
                .servicesOffered("Cardiology, Pediatrics, Urgent Care, Radiology")
                .rating(4.8)
                .totalReviews(85)
                .active(true)
                .build();

        createRequest = ClinicCreateRequest.builder()
                .name("Metro Healthcare Center")
                .description("Multi-specialty primary and urgent care clinic")
                .email("contact@metrohealth.com")
                .phone("+1-555-0144")
                .addressLine("450 Lexington Ave")
                .city("New York")
                .state("NY")
                .postalCode("10017")
                .operatingHours("Mon-Sun: 8:00 AM - 9:00 PM")
                .servicesOffered("Cardiology, Pediatrics, Urgent Care, Radiology")
                .build();
    }

    @Test
    @DisplayName("Should successfully create a new clinic")
    void shouldCreateClinicSuccessfully() {
        when(clinicRepository.existsByName("Metro Healthcare Center")).thenReturn(false);
        when(clinicRepository.existsByEmail("contact@metrohealth.com")).thenReturn(false);
        when(clinicRepository.save(any(Clinic.class))).thenReturn(testClinic);

        ClinicResponse response = clinicService.createClinic(createRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Metro Healthcare Center", response.getName());
        assertEquals("New York", response.getCity());
        verify(clinicRepository).save(any(Clinic.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when clinic name exists")
    void shouldThrowWhenClinicNameExists() {
        when(clinicRepository.existsByName("Metro Healthcare Center")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> clinicService.createClinic(createRequest));
        verify(clinicRepository, never()).save(any(Clinic.class));
    }

    @Test
    @DisplayName("Should retrieve clinic by ID successfully")
    void shouldGetClinicById() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(testClinic));

        ClinicResponse response = clinicService.getClinicById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Metro Healthcare Center", response.getName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when clinic ID not found")
    void shouldThrowWhenClinicNotFound() {
        when(clinicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clinicService.getClinicById(99L));
    }

    @Test
    @DisplayName("Should update clinic details successfully")
    void shouldUpdateClinicSuccessfully() {
        ClinicUpdateRequest updateRequest = ClinicUpdateRequest.builder()
                .operatingHours("24/7 Emergency & Urgent Care")
                .phone("+1-555-9999")
                .build();

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(testClinic));
        when(clinicRepository.save(any(Clinic.class))).thenReturn(testClinic);

        ClinicResponse response = clinicService.updateClinic(1L, updateRequest);

        assertNotNull(response);
        verify(clinicRepository).save(testClinic);
        assertEquals("24/7 Emergency & Urgent Care", testClinic.getOperatingHours());
        assertEquals("+1-555-9999", testClinic.getPhone());
    }

    @Test
    @DisplayName("Should delete clinic by ID successfully")
    void shouldDeleteClinicSuccessfully() {
        when(clinicRepository.existsById(1L)).thenReturn(true);

        clinicService.deleteClinic(1L);

        verify(clinicRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should search clinics with filters and pagination")
    void shouldSearchClinicsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Clinic> clinicPage = new PageImpl<>(List.of(testClinic), pageable, 1);

        when(clinicRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(clinicPage);

        Page<ClinicResponse> result = clinicService.searchClinics("Metro", "New York", "Cardiology", true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Metro Healthcare Center", result.getContent().get(0).getName());
    }
}
