package com.project.ecommv2backend.repository;

import com.project.ecommv2backend.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductRepository extends ListCrudRepository<Product, Long> {
}
