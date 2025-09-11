package com.example.Crisis_response.services;

import com.example.Crisis_response.Entity.RequestEntity;
import com.example.Crisis_response.Repository.RequestRepository;
import com.example.Crisis_response.dto.RequestDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ModelMapper modelMapper;

    public RequestDTO createRequest(RequestDTO dto) {
        RequestEntity entity = modelMapper.map(dto, RequestEntity.class);
        entity.setStatus("PENDING");
        entity.setCreatedAt(LocalDateTime.now());
        RequestEntity saved = requestRepository.save(entity);
        return modelMapper.map(saved, RequestDTO.class);
    }

    public List<RequestDTO> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, RequestDTO.class))
                .collect(Collectors.toList());
    }

    public RequestDTO getRequestById(Long id) {
        Optional<RequestEntity> entity = requestRepository.findById(id);
        return entity.map(e -> modelMapper.map(e, RequestDTO.class)).orElse(null);
    }

    public void updateRequestStatus(Long id, String status) {
        Optional<RequestEntity> entityOpt = requestRepository.findById(id);
        if (entityOpt.isPresent()) {
            RequestEntity entity = entityOpt.get();
            entity.setStatus(status);
            requestRepository.save(entity);
        }
    }
}
