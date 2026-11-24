package com.innov8.AssetSync.controller;

import com.innov8.AssetSync.model.Allocation;
import com.innov8.AssetSync.model.LocationRequest;
import com.innov8.AssetSync.model.Resource;
import com.innov8.AssetSync.repository.AllocationRepository;
import com.innov8.AssetSync.repository.LocationRequestRepository;
import com.innov8.AssetSync.repository.ResourceRepository;
import com.innov8.AssetSync.model.ConflictResponse;
import com.innov8.AssetSync.service.ConflictResolutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:5173}")
public class LocationController {

    private final ResourceRepository resourceRepository;
    private final LocationRequestRepository locationRequestRepository;
    private final AllocationRepository allocationRepository;
    private final ConflictResolutionService conflictResolutionService;

    public LocationController(ResourceRepository resourceRepository,
                              LocationRequestRepository locationRequestRepository,
                              AllocationRepository allocationRepository,
                              ConflictResolutionService conflictResolutionService) {
        this.resourceRepository = resourceRepository;
        this.locationRequestRepository = locationRequestRepository;
        this.allocationRepository = allocationRepository;
        this.conflictResolutionService = conflictResolutionService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> createRequest(@Valid @RequestBody LocationRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            return ResponseEntity.badRequest().body("End time must be later than start time");
        }
        List<Resource> matchingRooms;
        if (request.getPreferredResourceId() != null) {
            Resource preferredRoom = resourceRepository.findById(request.getPreferredResourceId()).orElse(null);
            if (
                    preferredRoom == null ||
                    preferredRoom.getResourceType() != Resource.ResourceType.LOCATION ||
                    !preferredRoom.getCategory().equalsIgnoreCase(request.getRequestedCategory()) ||
                    preferredRoom.getCapacity() == null ||
                    preferredRoom.getCapacity() < request.getStudentCount()
            ) {
                return ResponseEntity.badRequest().body("Selected alternative room is not valid for this request");
            }
            matchingRooms = List.of(preferredRoom);
        } else {
            matchingRooms = resourceRepository.findByResourceTypeAndCategoryAndCapacityGreaterThanEqual(
                    Resource.ResourceType.LOCATION,
                    request.getRequestedCategory(),
                    request.getStudentCount()
            );
        }

        boolean hasAvailableRoom = false;
        for (Resource room : matchingRooms) {
            int overlapCount = allocationRepository.countOverlaps(
                    room.getId(),
                    request.getStartTime(),
                    request.getEndTime()
            );
            if (overlapCount == 0) {
                hasAvailableRoom = true;
                break;
            }
        }

        if (hasAvailableRoom) {
            request.setStatus("PENDING");
            LocationRequest savedRequest = locationRequestRepository.save(request);
            return ResponseEntity.ok(savedRequest);
        } else {
            request.setStatus("CONFLICT");
            locationRequestRepository.save(request);

            List<Resource> alternatives = conflictResolutionService.findAlternativeLocations(
                    request.getRequestedCategory(),
                    request.getStudentCount(),
                    request.getStartTime(),
                    request.getEndTime()
            );

            ConflictResponse conflictResponse = new ConflictResponse(
                    "Requested resource is unavailable.",
                    alternatives,
                    List.of("08:00 AM - 10:00 AM", "10:00 AM - 12:00 PM", "02:00 PM - 04:00 PM", "04:00 PM - 06:00 PM")
            );

            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictResponse);
        }
    }

    @PostMapping("/process-batch")
    @Transactional
    public String processBatch() {
        List<LocationRequest> pendingRequests = locationRequestRepository.findByStatusOrderByPriorityAsc("PENDING");

        for (LocationRequest pendingRequest : pendingRequests) {
            LocationRequest request = locationRequestRepository.findByIdForUpdate(pendingRequest.getId()).orElse(null);
            if (request == null || !"PENDING".equals(request.getStatus())) {
                continue;
            }

            List<Resource> matchingRooms = request.getPreferredResourceId() != null
                    ? resourceRepository.findById(request.getPreferredResourceId()).stream().toList()
                    : resourceRepository.findByResourceTypeAndCategoryAndCapacityGreaterThanEqual(
                            Resource.ResourceType.LOCATION,
                            request.getRequestedCategory(),
                            request.getStudentCount()
                    );

            boolean allocated = false;
            for (Resource room : matchingRooms) {
                Resource lockedRoom = resourceRepository.findByIdForUpdate(room.getId()).orElse(null);
                if (lockedRoom == null) {
                    continue;
                }

                int overlapCount = allocationRepository.countOverlaps(
                        lockedRoom.getId(),
                        request.getStartTime(),
                        request.getEndTime()
                );

                if (overlapCount == 0) {
                    Allocation allocation = new Allocation();
                    allocation.setRequestId(request.getId());
                    allocation.setResourceId(lockedRoom.getId());
                    allocation.setStartTime(request.getStartTime());
                    allocation.setEndTime(request.getEndTime());
                    allocationRepository.save(allocation);

                    request.setStatus("ALLOCATED");
                    locationRequestRepository.save(request);
                    allocated = true;
                    break;
                }
            }

            if (!allocated) {
                request.setStatus("CONFLICT");
                locationRequestRepository.save(request);
            }
        }

        return "Batch Process Complete.";
    }
}
