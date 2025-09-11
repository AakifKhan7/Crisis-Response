package com.example.P1.Crisis.controller;

import com.example.P1.Crisis.dto.ResourceDTO;
import com.example.P1.Crisis.entity.Resource;
import com.example.P1.Crisis.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody ResourceDTO dto) {
        return ResponseEntity.ok(resourceService.createResource(dto));
    }

    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Resource>> getAvailableResources() {
        return ResponseEntity.ok(resourceService.getAvailableResources());
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Resource> updateResourceAvailability(@PathVariable Long id, @RequestBody Boolean available) {
        return ResponseEntity.ok(resourceService.updateResourceAvailability(id, available));
    }
}
