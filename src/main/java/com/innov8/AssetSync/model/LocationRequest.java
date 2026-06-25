package com.innov8.AssetSync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requested_category", nullable = false)
    private String requestedCategory;

    @Column(name = "requester_name", nullable = false)
    private String requesterName;

    @Column(nullable = false)
    private Integer priority; // 1 for LECTURER, 2 for STUDENT

    @Column(name = "student_count", nullable = false)
    private Integer studentCount;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status = "PENDING"; // Default is "PENDING"
}
