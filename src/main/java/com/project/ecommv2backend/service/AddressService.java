package com.project.ecommv2backend.service;

import com.project.ecommv2backend.exception.UserDoesNotHavePermission;
import com.project.ecommv2backend.model.Address;
import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    //Checks if the user making the request has the same userId of the address
    private boolean userIsTheSame(LocalUser user, Long id){
        return user.getId() == id;
    }

    public List<Address> getAddress (@AuthenticationPrincipal LocalUser user, @PathVariable Long userId){
        if(userIsTheSame(user,userId)) {
       return addressRepository.findByUser_Id(userId);
        }
        throw new UserDoesNotHavePermission("UserId is not the same as the one associated with the address");
    }

    public Address saveAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId, @RequestBody Address address) {
        //See if user has permission to edit
        if (!userIsTheSame(user, userId)) {
            throw new UserDoesNotHavePermission("User Id is not the same");
        }
        address.setId(null); //We set to null in case someone tries to change somebody else address
        LocalUser refUser = new LocalUser();
        refUser.setId(userId); // Set id to the user
        address.setUser(refUser);
        return addressRepository.save(address); //We will return the new id of the address too.
    }

    public Address updateAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId, @PathVariable Long addressId, @RequestBody Address address) {
        //See if user has permission to edit
        if (userIsTheSame(user, userId) && address.getId() == addressId) { //Checks if address matches body with url
            Optional<Address> opOriginalAddress = addressRepository.findById(addressId);
            if (opOriginalAddress.isPresent()) { //Check if address exists
                LocalUser originalUser = opOriginalAddress.get().getUser();
                if (opOriginalAddress.get().getUser().getId() == userId) { // Check if address belongs to the user we've been provided
                    address.setUser(originalUser); // We set the user's reference to the updated body
                    return addressRepository.save(address);
                }
            }
        } throw new UserDoesNotHavePermission("User id is not the same as the one associated with Address");
    }
}

