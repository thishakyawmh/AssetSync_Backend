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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> createRequest(@RequestBody LocationRequest request) {
        List<Resource> matchingRooms = resourceRepository.findByResourceTypeAndCategoryAndCapacityGreaterThanEqual(
                Resource.ResourceType.LOCATION,
                request.getRequestedCategory(),
                request.getStudentCount()
        );

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
    public String processBatch() {
        List<LocationRequest> pendingRequests = locationRequestRepository.findByStatusOrderByPriorityAsc("PENDING");

        for (LocationRequest request : pendingRequests) {
            List<Resource> matchingRooms = resourceRepository.findByResourceTypeAndCategoryAndCapacityGreaterThanEqual(
                    Resource.ResourceType.LOCATION,
                    request.getRequestedCategory(),
                    request.getStudentCount()
            );

            boolean allocated = false;
            for (Resource room : matchingRooms) {
                int overlapCount = allocationRepository.countOverlaps(
                        room.getId(),
                        request.getStartTime(),
                        request.getEndTime()
                );

                if (overlapCount == 0) {
                    Allocation allocation = new Allocation();
                    allocation.setRequestId(request.getId());
                    allocation.setResourceId(room.getId());
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
