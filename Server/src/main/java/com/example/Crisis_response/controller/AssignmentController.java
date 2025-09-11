package com.example.Crisis_response.controller;

import com.example.Crisis_response.dto.AssignmentDTO;
import com.example.Crisis_response.services.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<AssignmentDTO> assignResource(@RequestBody AssignmentDTO dto) {
        AssignmentDTO created = assignmentService.assignResource(dto);
        if (created == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateAssignmentStatus(@PathVariable Long id, @RequestParam String status) {
        assignmentService.updateAssignmentStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
