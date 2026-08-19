package com.example.fullness.stationary.security;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.fullness.stationary.repository.ProductRepository;

public class ProductRegistrationService {
    @Autowired
    ProductRepository productRepository;
}
