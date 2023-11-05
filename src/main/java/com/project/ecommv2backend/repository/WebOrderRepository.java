package com.project.ecommv2backend.repository;

import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderRepository extends ListCrudRepository<WebOrder, Long> {
    List<WebOrder> findByUser(LocalUser user);
}
