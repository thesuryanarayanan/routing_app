package com.example.routing_backend.repository;

import com.example.routing_backend.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {
}
