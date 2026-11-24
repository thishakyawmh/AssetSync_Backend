package com.innov8.AssetSync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(max = 100)
    @Column(name = "requested_category", nullable = false)
    private String requestedCategory;

    @NotBlank
    @Size(max = 255)
    @Column(name = "requester_name", nullable = false)
    private String requesterName;

    @NotNull
    @Min(1)
    @Max(2)
    @Column(nullable = false)
    private Integer priority;

    @NotNull
    @Positive
    @Column(name = "student_count", nullable = false)
    private Integer studentCount;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status = "PENDING";
}
