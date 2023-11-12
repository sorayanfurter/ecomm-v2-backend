package com.project.ecommv2backend.repository;

import com.project.ecommv2backend.model.Address;
import com.project.ecommv2backend.model.LocalUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
@Repository
public interface AddressRepository extends ListCrudRepository<Address, Long> {

    List<Address> findByUser_Id(Long id);


}
