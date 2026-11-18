package com.civia.reportservice.controller;

import com.civia.reportservice.dto.request.ReportImageRequest;
import com.civia.reportservice.dto.response.ReportImageResponse;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.service.ReportImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportImageController.class)
class ReportImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportImageService imageService;

    @Test
    @DisplayName("GET /api/v1/reports/{reportId}/images - Returns 200 with list of images")
    void getImages_returns200() throws Exception {
        ReportImageResponse img = new ReportImageResponse(1L, 10L, "https://cloudinary.com/sample.jpg", Instant.now());
        when(imageService.getImagesByReportId(10L)).thenReturn(List.of(img));

        mockMvc.perform(get("/api/v1/reports/10/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cloudinaryUrl", is("https://cloudinary.com/sample.jpg")));
    }

    @Test
    @DisplayName("POST /api/v1/reports/{reportId}/images - Returns 201 with Location header")
    void addImage_success_returns201() throws Exception {
        ReportImageRequest request = new ReportImageRequest("https://cloudinary.com/sample.jpg", Instant.now());
        ReportImageResponse response = new ReportImageResponse(5L, 10L, "https://cloudinary.com/sample.jpg", Instant.now());

        when(imageService.addImage(eq(10L), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/reports/10/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/reports/10/images/5")))
                .andExpect(jsonPath("$.id", is(5)));
    }

    @Test
    @DisplayName("DELETE /api/v1/reports/{reportId}/images/{imageId} - Returns 204")
    void deleteImage_returns204() throws Exception {
        doNothing().when(imageService).deleteImage(10L, 5L);

        mockMvc.perform(delete("/api/v1/reports/10/images/5"))
                .andExpect(status().isNoContent());

        verify(imageService).deleteImage(10L, 5L);
    }
}
