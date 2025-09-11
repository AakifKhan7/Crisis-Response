package com.example.Crisis_response.services;

import com.example.Crisis_response.Entity.ResourceEntity;
import com.example.Crisis_response.Repository.ResourceRepository;
import com.example.Crisis_response.dto.ResourceDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResourceDTO createResource(ResourceDTO dto) {
        ResourceEntity entity = modelMapper.map(dto, ResourceEntity.class);
        ResourceEntity saved = resourceRepository.save(entity);
        return modelMapper.map(saved, ResourceDTO.class);
    }

    public List<ResourceDTO> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, ResourceDTO.class))
                .collect(Collectors.toList());
    }

    public List<ResourceDTO> getAvailableResources() {
        return resourceRepository.findByAvailableTrue().stream()
                .map(entity -> modelMapper.map(entity, ResourceDTO.class))
                .collect(Collectors.toList());
    }

    public void updateResourceAvailability(Long id, Boolean available) {
        Optional<ResourceEntity> entityOpt = resourceRepository.findById(id);
        if (entityOpt.isPresent()) {
            ResourceEntity entity = entityOpt.get();
            entity.setAvailable(available);
            resourceRepository.save(entity);
        }
    }
}
