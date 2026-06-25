package com.innov8.AssetSync.controller;

import com.innov8.AssetSync.model.CalendarEvent;
import com.innov8.AssetSync.model.Resource;
import com.innov8.AssetSync.model.Allocation;
import com.innov8.AssetSync.model.BookingRequest;
import com.innov8.AssetSync.repository.AllocationRepository;
import com.innov8.AssetSync.repository.BookingRequestRepository;
import com.innov8.AssetSync.repository.ResourceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "http://localhost:5173")
public class CalendarController {

    private final AllocationRepository allocationRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final ResourceRepository resourceRepository;

    public CalendarController(
            AllocationRepository allocationRepository,
            BookingRequestRepository bookingRequestRepository,
            ResourceRepository resourceRepository) {
        this.allocationRepository = allocationRepository;
        this.bookingRequestRepository = bookingRequestRepository;
        this.resourceRepository = resourceRepository;
    }

    @GetMapping("/events")
    public List<CalendarEvent> getEvents() {

        // 1. Fetch all Location Allocation records and map to CalendarEvent

        List<Allocation> allocations = allocationRepository.findAll();
        List<CalendarEvent> allocationEvents = allocations.stream()
                .map(allocation -> {
                    Resource resource = resourceRepository.findById(allocation.getResourceId()).orElse(null);
                    String resourceName = resource != null ? resource.getName() : "Unknown Location";
                    return new CalendarEvent(
                            allocation.getId(),
                            resourceName + " - Allocated",
                            allocation.getStartTime(),
                            allocation.getEndTime(),
                            "LOCATION");
                })
                .toList();

        // 2. Fetch all BookingRequest records with status "ALLOCATED" and map to
        // CalendarEvent

        List<BookingRequest> bookings = bookingRequestRepository.findAll();
        List<CalendarEvent> bookingEvents = bookings.stream()
                .filter(request -> "ALLOCATED".equalsIgnoreCase(request.getStatus()))
                .map(request -> {
                    Resource resource = resourceRepository.findById(request.getResourceId()).orElse(null);
                    String resourceName = resource != null ? resource.getName() : "Unknown Equipment";
                    String title = "Requested by: " + request.getRequesterName() + " - "
                            + request.getRequestedQuantity() + "x " + resourceName;
                    return new CalendarEvent(
                            request.getId(),
                            title,
                            request.getStartTime(),
                            request.getEndTime(),
                            "EQUIPMENT");
                })
                .toList();

        // 3. Combine both lists

        List<CalendarEvent> combined = new ArrayList<>();
        combined.addAll(allocationEvents);
        combined.addAll(bookingEvents);

        // 4. Sort chronologically by start time

        combined.sort(Comparator.comparing(CalendarEvent::start));

        return combined;
    }
}
