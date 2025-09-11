package com.example.P1.Crisis.service;

import com.example.P1.Crisis.dto.AssignmentDTO;
import com.example.P1.Crisis.entity.Assignment;
import com.example.P1.Crisis.entity.Request;
import com.example.P1.Crisis.entity.Resource;
import com.example.P1.Crisis.repository.AssignmentRepository;
import com.example.P1.Crisis.repository.RequestRepository;
import com.example.P1.Crisis.repository.ResourceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final RequestRepository requestRepository;
    private final ResourceRepository resourceRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository, RequestRepository requestRepository, ResourceRepository resourceRepository, ModelMapper modelMapper) {
        this.assignmentRepository = assignmentRepository;
        this.requestRepository = requestRepository;
        this.resourceRepository = resourceRepository;
        this.modelMapper = modelMapper;
    }

    public AssignmentDTO assignResource(AssignmentDTO dto) {
        Assignment assignment = modelMapper.map(dto, Assignment.class);
        assignment.setRequest(requestRepository.findById(dto.getRequestId()).orElseThrow());
        assignment.setResource(resourceRepository.findById(dto.getResourceId()).orElseThrow());
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setStatus("ASSIGNED");
        Assignment saved = assignmentRepository.save(assignment);
        return modelMapper.map(saved, AssignmentDTO.class);
    }

    public List<AssignmentDTO> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(a -> modelMapper.map(a, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    public AssignmentDTO updateAssignmentStatus(Long id, String status) {
        Assignment assignment = assignmentRepository.findById(id).orElseThrow();
        assignment.setStatus(status);
        Assignment saved = assignmentRepository.save(assignment);
        return modelMapper.map(saved, AssignmentDTO.class);
    }
}
