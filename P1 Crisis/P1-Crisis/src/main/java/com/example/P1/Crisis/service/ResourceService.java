package com.example.P1.Crisis.service;

import com.example.P1.Crisis.dto.ResourceDTO;
import com.example.P1.Crisis.entity.Resource;
import com.example.P1.Crisis.repository.ResourceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository, ModelMapper modelMapper) {
        this.resourceRepository = resourceRepository;
        this.modelMapper = modelMapper;
    }

    public ResourceDTO createResource(ResourceDTO dto) {
        Resource resource = modelMapper.map(dto, Resource.class);
        Resource saved = resourceRepository.save(resource);
        return modelMapper.map(saved, ResourceDTO.class);
    }

    public List<ResourceDTO> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(r -> modelMapper.map(r, ResourceDTO.class))
                .collect(Collectors.toList());
    }

    public List<ResourceDTO> getAvailableResources() {
        return resourceRepository.findAll().stream()
                .filter(Resource::getAvailable)
                .map(r -> modelMapper.map(r, ResourceDTO.class))
                .collect(Collectors.toList());
    }

    public ResourceDTO updateResourceAvailability(Long id, Boolean available) {
        Resource resource = resourceRepository.findById(id).orElseThrow();
        resource.setAvailable(available);
        Resource saved = resourceRepository.save(resource);
        return modelMapper.map(saved, ResourceDTO.class);
    }
}
