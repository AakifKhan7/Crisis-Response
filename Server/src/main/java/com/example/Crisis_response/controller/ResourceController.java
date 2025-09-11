package com.example.Crisis_response.controller;

import com.example.Crisis_response.dto.ResourceDTO;
import com.example.Crisis_response.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceDTO> createResource(@RequestBody ResourceDTO dto) {
        ResourceDTO created = resourceService.createResource(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ResourceDTO>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/available")
    public ResponseEntity<List<ResourceDTO>> getAvailableResources() {
        return ResponseEntity.ok(resourceService.getAvailableResources());
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Void> updateResourceAvailability(@PathVariable Long id, @RequestParam Boolean available) {
        resourceService.updateResourceAvailability(id, available);
        return ResponseEntity.ok().build();
    }
}
