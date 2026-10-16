package com.doctify.doctorservice.service;

import com.doctify.doctorservice.dto.DoctorCreateRequest;
import com.doctify.doctorservice.dto.DoctorResponse;
import com.doctify.doctorservice.dto.DoctorUpdateRequest;
import com.doctify.doctorservice.entity.Doctor;
import com.doctify.doctorservice.exception.DuplicateResourceException;
import com.doctify.doctorservice.exception.ResourceNotFoundException;
import com.doctify.doctorservice.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor testDoctor;
    private DoctorCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testDoctor = Doctor.builder()
                .id(1L)
                .fullName("Dr. Gregory House")
                .email("house@doctify.com")
                .phone("+1234567890")
                .specialization("Diagnostics")
                .experienceYears(20)
                .qualification("MD")
                .consultationFee(BigDecimal.valueOf(250.00))
                .bio("Specialist in infectious diseases and nephrology")
                .clinicId(101L)
                .city("Princeton")
                .rating(4.9)
                .totalReviews(120)
                .available(true)
                .build();

        createRequest = DoctorCreateRequest.builder()
                .fullName("Dr. Gregory House")
                .email("house@doctify.com")
                .phone("+1234567890")
                .specialization("Diagnostics")
                .experienceYears(20)
                .qualification("MD")
                .consultationFee(BigDecimal.valueOf(250.00))
                .bio("Specialist in infectious diseases and nephrology")
                .clinicId(101L)
                .city("Princeton")
                .build();
    }

    @Test
    @DisplayName("Should successfully create a new doctor")
    void shouldCreateDoctorSuccessfully() {
        when(doctorRepository.existsByEmail("house@doctify.com")).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        DoctorResponse response = doctorService.createDoctor(createRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Dr. Gregory House", response.getFullName());
        assertEquals("house@doctify.com", response.getEmail());
        assertEquals("Diagnostics", response.getSpecialization());
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void shouldThrowWhenDoctorEmailExists() {
        when(doctorRepository.existsByEmail("house@doctify.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> doctorService.createDoctor(createRequest));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Should retrieve doctor by ID successfully")
    void shouldGetDoctorById() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));

        DoctorResponse response = doctorService.getDoctorById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Dr. Gregory House", response.getFullName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when doctor ID is not found")
    void shouldThrowWhenDoctorNotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(99L));
    }

    @Test
    @DisplayName("Should update doctor profile fields successfully")
    void shouldUpdateDoctorSuccessfully() {
        DoctorUpdateRequest updateRequest = DoctorUpdateRequest.builder()
                .fullName("Dr. G. House")
                .consultationFee(BigDecimal.valueOf(300.00))
                .available(false)
                .build();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        DoctorResponse response = doctorService.updateDoctor(1L, updateRequest);

        assertNotNull(response);
        verify(doctorRepository).save(testDoctor);
        assertEquals("Dr. G. House", testDoctor.getFullName());
        assertEquals(BigDecimal.valueOf(300.00), testDoctor.getConsultationFee());
        assertFalse(testDoctor.isAvailable());
    }

    @Test
    @DisplayName("Should delete doctor by ID successfully")
    void shouldDeleteDoctorSuccessfully() {
        when(doctorRepository.existsById(1L)).thenReturn(true);

        doctorService.deleteDoctor(1L);

        verify(doctorRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should search doctors with pagination and filters")
    void shouldSearchDoctorsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("rating").descending());
        Page<Doctor> doctorPage = new PageImpl<>(List.of(testDoctor), pageable, 1);

        when(doctorRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(doctorPage);

        Page<DoctorResponse> result = doctorService.searchDoctors(
                "House", "Diagnostics", "Princeton", BigDecimal.valueOf(100), BigDecimal.valueOf(300), 4.5, true, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Dr. Gregory House", result.getContent().get(0).getFullName());
    }
}
