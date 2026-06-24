package com.innov8.AssetSync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = true)
    private Integer capacity; // Nullable - applied to Locations

    @Column(nullable = true)
    private Integer quantity; // Nullable - applied to Equipment

    @Column(name = "quality_status", nullable = true)
    private String qualityStatus; // Nullable - applied to Equipment (e.g., NEW, GOOD, NEEDS_INSPECTION)

    public enum ResourceType {
        LOCATION, EQUIPMENT
    }
}
