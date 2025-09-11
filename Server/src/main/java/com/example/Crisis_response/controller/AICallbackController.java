package com.example.Crisis_response.controller;

import com.example.Crisis_response.Entity.RequestEntity;
import com.example.Crisis_response.Repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ai")
public class AICallbackController {
    @Autowired
    private RequestRepository requestRepository;

    @PostMapping("/callback")
    public ResponseEntity<String> aiCallback(@RequestBody AICallbackRequest input) {
        Optional<RequestEntity> entityOpt = requestRepository.findById(input.getRequestId());
        if (entityOpt.isPresent()) {
            RequestEntity entity = entityOpt.get();
            entity.setType(input.getType());
            entity.setSeverity(input.getSeverity());
            requestRepository.save(entity);
            return ResponseEntity.ok("Request updated");
        }
        return ResponseEntity.notFound().build();
    }

    public static class AICallbackRequest {
        private Long requestId;
        private String type;
        private Integer severity;
        public Long getRequestId() { return requestId; }
        public void setRequestId(Long requestId) { this.requestId = requestId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getSeverity() { return severity; }
        public void setSeverity(Integer severity) { this.severity = severity; }
    }
}
