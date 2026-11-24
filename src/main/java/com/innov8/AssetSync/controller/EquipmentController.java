package com.innov8.AssetSync.controller;

import com.innov8.AssetSync.model.BookingRequest;
import com.innov8.AssetSync.model.Resource;
import com.innov8.AssetSync.repository.BookingRequestRepository;
import com.innov8.AssetSync.repository.ResourceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.innov8.AssetSync.model.ConflictResponse;
import com.innov8.AssetSync.service.ConflictResolutionService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:5173}")
public class EquipmentController {

    private final ResourceRepository resourceRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final ConflictResolutionService conflictResolutionService;

    public EquipmentController(ResourceRepository resourceRepository,
                               BookingRequestRepository bookingRequestRepository,
                               ConflictResolutionService conflictResolutionService) {
        this.resourceRepository = resourceRepository;
        this.bookingRequestRepository = bookingRequestRepository;
        this.conflictResolutionService = conflictResolutionService;
    }

    @PostMapping("/book")
    @Transactional
    public ResponseEntity<?> bookEquipment(@Valid @RequestBody BookingRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            return ResponseEntity.badRequest().body("End time must be later than start time");
        }

        Optional<Resource> resourceOpt = resourceRepository.findByIdForUpdate(request.getResourceId());
        if (resourceOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
        }

        Resource resource = resourceOpt.get();
        if (resource.getResourceType() != Resource.ResourceType.EQUIPMENT) {
            return ResponseEntity.badRequest().body("Selected resource is not equipment");
        }

        int availableQuantity = resource.getQuantity() != null ? resource.getQuantity() : 0;

        if (availableQuantity >= request.getRequestedQuantity()) {
            resource.setQuantity(availableQuantity - request.getRequestedQuantity());
            resourceRepository.save(resource);

            request.setStatus("ALLOCATED");
            BookingRequest savedRequest = bookingRequestRepository.save(request);
            return ResponseEntity.ok(savedRequest);
        }

        request.setStatus("CONFLICT");
        bookingRequestRepository.save(request);

        List<Resource> alternatives = conflictResolutionService.findAlternativeEquipment(
                resource.getCategory(),
                request.getRequestedQuantity()
        );

        ConflictResponse conflictResponse = new ConflictResponse(
                "Insufficient inventory. Requested: " + request.getRequestedQuantity() + ", Available: " + availableQuantity,
                alternatives,
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictResponse);
    }
}
