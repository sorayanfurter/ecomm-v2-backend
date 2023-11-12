package com.project.ecommv2backend.controller;

import com.project.ecommv2backend.model.Address;
import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class AddressController {

    @Autowired
    private AddressService addressService;


    @GetMapping("/{userId}/address")

    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId){
       List<Address> addresses = addressService.getAddress(user, userId);
        return new ResponseEntity <List<Address>>(addresses, HttpStatus.OK);
    }

    @PutMapping("/{userId}/address")

    public ResponseEntity<Address> putAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId, @RequestBody Address address) {
      Address savedAddress = addressService.saveAddress(user, userId, address);
      return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/address/{addressId}")

    public ResponseEntity<Address> updateAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId, @PathVariable Long addressId, @RequestBody Address address){
        Address updatedAddress = addressService.updateAddress(user, userId, addressId, address);
        return new ResponseEntity<>(updatedAddress, HttpStatus.CREATED);
    }

}
