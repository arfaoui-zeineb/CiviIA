package com.civicpulse.communication.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;

    private Long venueId; // Nullable

    @NotNull(message = "Department ID is mandatory")
    private Long departmentId;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Start date is mandatory")
    @Future(message = "Start date must be in the future")
    private OffsetDateTime startDate;

    @NotNull(message = "End date is mandatory")
    private OffsetDateTime endDate;

    @NotNull(message = "Capacity is mandatory")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}

