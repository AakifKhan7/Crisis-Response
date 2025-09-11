package com.example.P1.Crisis.controller;

import com.example.P1.Crisis.dto.AssignmentDTO;
import com.example.P1.Crisis.entity.Assignment;
import com.example.P1.Crisis.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<Assignment> assignResource(@RequestBody AssignmentDTO dto) {
        return ResponseEntity.ok(assignmentService.assignResource(dto));
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Assignment> updateAssignmentStatus(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(assignmentService.updateAssignmentStatus(id, status));
    }
}
