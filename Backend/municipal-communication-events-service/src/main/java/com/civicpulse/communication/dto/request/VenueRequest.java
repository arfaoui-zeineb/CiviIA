package com.civicpulse.communication.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueRequest {

    @NotBlank(message = "Venue name is mandatory")
    @Size(max = 150, message = "Venue name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Venue address is mandatory")
    @Size(max = 255, message = "Venue address must not exceed 255 characters")
    private String address;

    private Double latitude;

    private Double longitude;

    @NotNull(message = "Venue capacity is mandatory")
    @Min(value = 1, message = "Venue capacity must be at least 1")
    private Integer capacity;
}

