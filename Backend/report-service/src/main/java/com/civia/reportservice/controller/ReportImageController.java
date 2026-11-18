package com.civia.reportservice.controller;

import com.civia.reportservice.dto.request.ReportImageRequest;
import com.civia.reportservice.dto.response.ReportImageResponse;
import com.civia.reportservice.service.ReportImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports/{reportId}/images")
@RequiredArgsConstructor
@Tag(name = "Report Images", description = "Endpoints for managing images attached to reports")
public class ReportImageController {

    private final ReportImageService imageService;

    @GetMapping
    @Operation(summary = "Get images of a report")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of images"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<List<ReportImageResponse>> getImages(@PathVariable Long reportId) {
        return ResponseEntity.ok(imageService.getImagesByReportId(reportId));
    }

    @PostMapping
    @Operation(summary = "Attach an image to a report", description = "Stores Cloudinary URL metadata.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Image attached successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<ReportImageResponse> addImage(
            @PathVariable Long reportId,
            @Valid @RequestBody ReportImageRequest request
    ) {
        ReportImageResponse created = imageService.addImage(reportId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{imageId}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete an image from a report")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Image removed"),
            @ApiResponse(responseCode = "404", description = "Report or image not found")
    })
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long reportId,
            @PathVariable Long imageId
    ) {
        imageService.deleteImage(reportId, imageId);
        return ResponseEntity.noContent().build();
    }
}
