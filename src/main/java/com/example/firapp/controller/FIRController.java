package com.example.firapp.controller;

import com.example.firapp.model.FIR;
import com.example.firapp.repository.FIRRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fir")
public class FIRController {

    private final FIRRepository firRepository;

    public FIRController(FIRRepository firRepository) {
        this.firRepository = firRepository;
    }

    @PostMapping
    public ResponseEntity<FIR> create(@RequestBody FIR fir) {
        fir.setFirNumber(UUID.randomUUID().toString().substring(0, 8)); // Simple auto gen
        fir.setDateTime(new Date());
        return ResponseEntity.ok(firRepository.save(fir));
    }

    @GetMapping
    public ResponseEntity<List<FIR>> getAll() {
        return ResponseEntity.ok(firRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FIR> getById(@PathVariable String id) {
        return firRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FIR> update(@PathVariable String id, @RequestBody FIR fir) {
        return firRepository.findById(id).map(existing -> {
            fir.setId(id);
            return ResponseEntity.ok(firRepository.save(fir));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        firRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}