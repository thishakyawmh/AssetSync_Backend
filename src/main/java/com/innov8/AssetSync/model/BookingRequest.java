package com.innov8.AssetSync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "requester_name", nullable = false)
    private String requesterName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "user_role", nullable = false)
    private String userRole;

    @NotNull
    @Positive
    @Column(name = "requested_quantity", nullable = false)
    private Integer requestedQuantity;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status;
}
