package com.innov8.AssetSync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "requester_name", nullable = false)
    private String requesterName;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Column(name = "requested_quantity", nullable = false)
    private Integer requestedQuantity;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status;
}
