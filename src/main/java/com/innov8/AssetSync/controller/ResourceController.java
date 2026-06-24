package com.innov8.AssetSync.controller;

import com.innov8.AssetSync.model.Resource;
import com.innov8.AssetSync.repository.ResourceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "http://localhost:5173")
public class ResourceController {

    private final ResourceRepository resourceRepository;

    public ResourceController(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @GetMapping
    public List<Resource> getResources(@RequestParam(value = "type", required = false) Resource.ResourceType type) {
        if (type != null) {
            return resourceRepository.findByResourceType(type);
        }
        return resourceRepository.findAll();
    }
}
