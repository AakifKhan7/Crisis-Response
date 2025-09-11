package com.example.Crisis_response.services;

import com.example.Crisis_response.Entity.AssignmentEntity;
import com.example.Crisis_response.Entity.RequestEntity;
import com.example.Crisis_response.Entity.ResourceEntity;
import com.example.Crisis_response.Repository.AssignmentRepository;
import com.example.Crisis_response.Repository.RequestRepository;
import com.example.Crisis_response.Repository.ResourceRepository;
import com.example.Crisis_response.dto.AssignmentDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ModelMapper modelMapper;

    public AssignmentDTO assignResource(AssignmentDTO dto) {
        Optional<RequestEntity> requestOpt = requestRepository.findById(dto.getRequestId());
        Optional<ResourceEntity> resourceOpt = resourceRepository.findById(dto.getResourceId());
        if (requestOpt.isPresent() && resourceOpt.isPresent()) {
            AssignmentEntity entity = new AssignmentEntity();
            entity.setRequest(requestOpt.get());
            entity.setResource(resourceOpt.get());
            entity.setAssignedAt(LocalDateTime.now());
            entity.setStatus("ASSIGNED");
            AssignmentEntity saved = assignmentRepository.save(entity);
            return modelMapper.map(saved, AssignmentDTO.class);
        }
        return null;
    }

    public List<AssignmentDTO> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    public void updateAssignmentStatus(Long id, String status) {
        Optional<AssignmentEntity> entityOpt = assignmentRepository.findById(id);
        if (entityOpt.isPresent()) {
            AssignmentEntity entity = entityOpt.get();
            entity.setStatus(status);
            assignmentRepository.save(entity);
        }
    }
}
