package com.innov8.AssetSync.controller;

import com.innov8.AssetSync.model.BookingRequest;
import com.innov8.AssetSync.model.Resource;
import com.innov8.AssetSync.repository.BookingRequestRepository;
import com.innov8.AssetSync.repository.ResourceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "http://localhost:5173")
public class EquipmentController {

    private final ResourceRepository resourceRepository;
    private final BookingRequestRepository bookingRequestRepository;

    public EquipmentController(ResourceRepository resourceRepository, BookingRequestRepository bookingRequestRepository) {
        this.resourceRepository = resourceRepository;
        this.bookingRequestRepository = bookingRequestRepository;
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookEquipment(@RequestBody BookingRequest request) {
        Optional<Resource> resourceOpt = resourceRepository.findById(request.getResourceId());
        if (resourceOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
        }

        Resource resource = resourceOpt.get();
        int availableQuantity = resource.getQuantity() != null ? resource.getQuantity() : 0;

        if (availableQuantity >= request.getRequestedQuantity()) {
            // Subtract the requested quantity
            resource.setQuantity(availableQuantity - request.getRequestedQuantity());
            resourceRepository.save(resource);

            // Save the allocated booking request
            request.setStatus("ALLOCATED");
            BookingRequest savedRequest = bookingRequestRepository.save(request);

            return ResponseEntity.ok(savedRequest);
        } else {
            // Save the conflicted booking request
            request.setStatus("CONFLICT");
            bookingRequestRepository.save(request);

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Insufficient inventory. Requested: " + request.getRequestedQuantity() + ", Available: " + availableQuantity);
        }
    }
}
