package com.project.ecommv2backend.service;

import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.model.WebOrder;
import com.project.ecommv2backend.repository.WebOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private WebOrderRepository webOrderRepository;

    public List<WebOrder>getOrders(LocalUser user){
        return webOrderRepository.findByUser(user);
    }
}
