package com.innov8.AssetSync.service;

import com.innov8.AssetSync.model.Resource;
import com.innov8.AssetSync.repository.AllocationRepository;
import com.innov8.AssetSync.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConflictResolutionService {

    private final ResourceRepository resourceRepository;
    private final AllocationRepository allocationRepository;

    public ConflictResolutionService(ResourceRepository resourceRepository, AllocationRepository allocationRepository) {
        this.resourceRepository = resourceRepository;
        this.allocationRepository = allocationRepository;
    }

    /**
     * Method A: List alternative locations
     * Finds up to 3 Resource entities of type LOCATION under requestedCategory and capacity >= studentCount
     * that have 0 overlaps for the exact requested time range.
     */
    public List<Resource> findAlternativeLocations(String requestedCategory, Integer studentCount, LocalDateTime startTime, LocalDateTime endTime) {
        List<Resource> rooms = resourceRepository.findByResourceTypeAndCategoryAndCapacityGreaterThanEqual(
                Resource.ResourceType.LOCATION,
                requestedCategory,
                studentCount
        );

        List<Resource> alternatives = new ArrayList<>();
        for (Resource room : rooms) {
            int overlaps = allocationRepository.countOverlaps(room.getId(), startTime, endTime);
            if (overlaps == 0) {
                alternatives.add(room);
                if (alternatives.size() >= 3) {
                    break;
                }
            }
        }
        return alternatives;
    }

    /**
     * Method B: List alternative equipment
     * Finds up to 3 Resource entities of type EQUIPMENT under requestedCategory and quantity >= requestedQuantity.
     */
    public List<Resource> findAlternativeEquipment(String requestedCategory, Integer requestedQuantity) {
        List<Resource> equipmentList = resourceRepository.findByResourceTypeAndCategoryAndQuantityGreaterThanEqual(
                Resource.ResourceType.EQUIPMENT,
                requestedCategory,
                requestedQuantity
        );

        return equipmentList.stream()
                .limit(3)
                .collect(Collectors.toList());
    }
}
