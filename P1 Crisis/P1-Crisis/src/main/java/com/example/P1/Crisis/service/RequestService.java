package com.example.P1.Crisis.service;

import com.example.P1.Crisis.dto.RequestDTO;
import com.example.P1.Crisis.entity.Request;
import com.example.P1.Crisis.repository.RequestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RequestService(RequestRepository requestRepository, ModelMapper modelMapper) {
        this.requestRepository = requestRepository;
        this.modelMapper = modelMapper;
    }

    public RequestDTO createRequest(RequestDTO dto) {
        Request request = modelMapper.map(dto, Request.class);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        Request saved = requestRepository.save(request);
        return modelMapper.map(saved, RequestDTO.class);
    }

    public List<RequestDTO> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(r -> modelMapper.map(r, RequestDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<RequestDTO> getRequestById(Long id) {
        return requestRepository.findById(id)
                .map(r -> modelMapper.map(r, RequestDTO.class));
    }

    public RequestDTO updateRequestStatus(Long id, String status) {
        Request request = requestRepository.findById(id).orElseThrow();
        request.setStatus(status);
        Request saved = requestRepository.save(request);
        return modelMapper.map(saved, RequestDTO.class);
    }

    public RequestDTO updateRequestTypeAndSeverity(Long id, String type, Integer severity) {
        Request request = requestRepository.findById(id).orElseThrow();
        request.setType(type);
        request.setSeverity(severity);
        Request saved = requestRepository.save(request);
        return modelMapper.map(saved, RequestDTO.class);
    }
}
