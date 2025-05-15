package com.example.routing_backend.controller;

import com.example.routing_backend.entity.Dispatch;
import com.example.routing_backend.service.DispatchService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/dispatches")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @GetMapping
    public List<Dispatch> getAllDispatches() {
        return dispatchService.findAll();
    }

    @GetMapping("/{id}")
    public Dispatch getDispatchById(@PathVariable Long id) {
        return dispatchService.findById(id);
    }

    @PostMapping
    public Dispatch createDispatch(@RequestBody Dispatch dispatch) {
        return dispatchService.save(dispatch);
    }

    @PostMapping("/bulk")
    public List<Dispatch> createDispatches(@RequestBody List<Dispatch> dispatches) {
        return dispatchService.bulkSave(dispatches);
    }
}
