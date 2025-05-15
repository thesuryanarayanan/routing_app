package com.example.routing_backend.service;

import com.example.routing_backend.entity.Dispatch;

import com.example.routing_backend.repository.DispatchRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchRepository dispatchRepository;

    public List<Dispatch> findAll() {
        return dispatchRepository.findAll();
    }

    public Dispatch findById(Long id) {
        return dispatchRepository.findById(id).orElse(null);
    }

    public Dispatch save(Dispatch dispatch) {
        return dispatchRepository.save(dispatch);
    }

    public List<Dispatch> bulkSave(List<Dispatch> dispatches) {
        return dispatchRepository.saveAll(dispatches);
    }
}
