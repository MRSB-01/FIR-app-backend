package com.example.firapp.repository;

import com.example.firapp.model.FIR;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FIRRepository extends MongoRepository<FIR, String> {
}