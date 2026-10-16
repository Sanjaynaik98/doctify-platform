package com.doctify.doctorservice.controller;

import com.doctify.doctorservice.dto.DoctorCreateRequest;
import com.doctify.doctorservice.dto.DoctorResponse;
import com.doctify.doctorservice.dto.DoctorUpdateRequest;
import com.doctify.doctorservice.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorCreateRequest request) {
        log.info("REST request to create doctor with email: {}", request.getEmail());
        DoctorResponse response = doctorService.createDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        DoctorResponse response = doctorService.getDoctorById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateRequest request) {
        log.info("REST request to update doctor ID: {}", id);
        DoctorResponse response = doctorService.updateDoctor(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        log.info("REST request to delete doctor ID: {}", id);
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> searchDoctors(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minFee,
            @RequestParam(required = false) BigDecimal maxFee,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating,desc") String sort
    ) {
        String[] sortParams = sort.split(",");
        String sortProperty = sortParams[0];
        Sort.Direction sortDirection = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Page<DoctorResponse> result = doctorService.searchDoctors(
                query, specialization, city, minFee, maxFee, minRating, available, pageable
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<DoctorResponse>> getDoctorsByClinic(@PathVariable Long clinicId) {
        List<DoctorResponse> response = doctorService.getDoctorsByClinic(clinicId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<String>> getSpecializations() {
        List<String> specializations = doctorService.getSpecializations();
        return ResponseEntity.ok(specializations);
    }
}
