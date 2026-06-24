package com.innov8.AssetSync.seeder;

import com.innov8.AssetSync.model.Resource;
import com.innov8.AssetSync.repository.ResourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ResourceRepository resourceRepository;

    public DatabaseSeeder(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (resourceRepository.count() == 0) {
            // Seed Locations
            Resource auditorium = new Resource(
                null,
                Resource.ResourceType.LOCATION,
                "Auditorium A",
                "Lecture Hall",
                250,
                null,
                null
            );
            Resource scienceLab = new Resource(
                null,
                Resource.ResourceType.LOCATION,
                "Science Lab 3",
                "Laboratory",
                40,
                null,
                null
            );
            Resource seminarRoom = new Resource(
                null,
                Resource.ResourceType.LOCATION,
                "Seminar Room 102",
                "Meeting Room",
                15,
                null,
                null
            );

            // Seed Equipment
            Resource camera = new Resource(
                null,
                Resource.ResourceType.EQUIPMENT,
                "Sony Alpha Camera",
                "Multimedia",
                null,
                5,
                "NEW"
            );
            Resource micKit = new Resource(
                null,
                Resource.ResourceType.EQUIPMENT,
                "Wireless Microphone Kit",
                "Audio",
                null,
                10,
                "GOOD"
            );
            Resource projector = new Resource(
                null,
                Resource.ResourceType.EQUIPMENT,
                "Epson Projector",
                "Multimedia",
                null,
                8,
                "GOOD"
            );
            Resource vrHeadset = new Resource(
                null,
                Resource.ResourceType.EQUIPMENT,
                "Oculus Quest 2",
                "VR Lab",
                null,
                15,
                "NEW"
            );

            resourceRepository.saveAll(Arrays.asList(
                auditorium, scienceLab, seminarRoom,
                camera, micKit, projector, vrHeadset
            ));

            System.out.println("Database seeded with sample Resources successfully!");
        } else {
            System.out.println("Database already contains resources. Skipping seeding.");
        }
    }
}
