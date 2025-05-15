package com.example.routing_backend.service;

import com.example.routing_backend.entity.Branch;
import com.example.routing_backend.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

}
