package com.doctify.doctorservice.controller;

import com.doctify.doctorservice.dto.DoctorCreateRequest;
import com.doctify.doctorservice.dto.DoctorUpdateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create doctor successfully and return 201 Created")
    void shouldCreateDoctorSuccessfully() throws Exception {
        DoctorCreateRequest request = DoctorCreateRequest.builder()
                .fullName("Dr. Meredith Grey")
                .email("grey@seattlegrace.com")
                .phone("+1-206-555-0100")
                .specialization("General Surgery")
                .experienceYears(12)
                .qualification("MD, FACS")
                .consultationFee(BigDecimal.valueOf(180.00))
                .bio("Chief of General Surgery with vast clinical expertise")
                .clinicId(200L)
                .city("Seattle")
                .build();

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.fullName").value("Dr. Meredith Grey"))
                .andExpect(jsonPath("$.email").value("grey@seattlegrace.com"))
                .andExpect(jsonPath("$.specialization").value("General Surgery"))
                .andExpect(jsonPath("$.consultationFee").value(180.00))
                .andExpect(jsonPath("$.city").value("Seattle"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Should return 409 Conflict when creating doctor with existing email")
    void shouldReturnConflictForDuplicateEmail() throws Exception {
        DoctorCreateRequest request1 = DoctorCreateRequest.builder()
                .fullName("Dr. John Watson")
                .email("watson@bakerstreet.com")
                .phone("+44-20-7946-0919")
                .specialization("General Medicine")
                .experienceYears(15)
                .qualification("MBBS, MD")
                .consultationFee(BigDecimal.valueOf(120.00))
                .city("London")
                .build();

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        DoctorCreateRequest request2 = DoctorCreateRequest.builder()
                .fullName("Dr. Jonathan Watson")
                .email("watson@bakerstreet.com")
                .phone("+44-20-7946-0920")
                .specialization("General Medicine")
                .experienceYears(16)
                .qualification("MBBS, MD")
                .consultationFee(BigDecimal.valueOf(130.00))
                .city("London")
                .build();

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("Should return 400 Bad Request with validation errors when request is invalid")
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        DoctorCreateRequest invalidRequest = DoctorCreateRequest.builder()
                .fullName("")
                .email("not-an-email")
                .phone("")
                .specialization("")
                .experienceYears(-5)
                .qualification("")
                .consultationFee(BigDecimal.valueOf(-20.00))
                .city("")
                .build();

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.fullName").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.experienceYears").exists())
                .andExpect(jsonPath("$.validationErrors.consultationFee").exists());
    }

    @Test
    @DisplayName("Should retrieve doctor by ID, update doctor, and search by filters")
    void shouldPerformCrudAndSearchFlow() throws Exception {
        // 1. Create
        DoctorCreateRequest createReq = DoctorCreateRequest.builder()
                .fullName("Dr. Stephen Strange")
                .email("strange@sanctum.com")
                .phone("+1-212-555-0199")
                .specialization("Neurosurgery")
                .experienceYears(18)
                .qualification("MD, PhD")
                .consultationFee(BigDecimal.valueOf(350.00))
                .clinicId(300L)
                .city("New York")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createdJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long doctorId = createdJson.get("id").asLong();

        // 2. Get by ID
        mockMvc.perform(get("/api/doctors/" + doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Dr. Stephen Strange"))
                .andExpect(jsonPath("$.specialization").value("Neurosurgery"));

        // 3. Update
        DoctorUpdateRequest updateReq = DoctorUpdateRequest.builder()
                .consultationFee(BigDecimal.valueOf(400.00))
                .city("Manhattan")
                .build();

        mockMvc.perform(put("/api/doctors/" + doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultationFee").value(400.00))
                .andExpect(jsonPath("$.city").value("Manhattan"));

        // 4. Search with filters
        mockMvc.perform(get("/api/doctors")
                        .param("specialization", "Neurosurgery")
                        .param("city", "Manhattan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Dr. Stephen Strange"));

        // 5. Specializations list
        mockMvc.perform(get("/api/doctors/specializations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("Neurosurgery")));

        // 6. Delete
        mockMvc.perform(delete("/api/doctors/" + doctorId))
                .andExpect(status().isNoContent());

        // 7. Verify 404
        mockMvc.perform(get("/api/doctors/" + doctorId))
                .andExpect(status().isNotFound());
    }
}
