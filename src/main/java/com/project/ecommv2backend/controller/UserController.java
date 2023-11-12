package com.project.ecommv2backend.controller;

import com.project.ecommv2backend.exception.EmailFailureException;
import com.project.ecommv2backend.exception.UserAlreadyExistsException;
import com.project.ecommv2backend.exception.UserDoesNotHavePermission;
import com.project.ecommv2backend.model.Address;
import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.model.dto.LocalUserDTO;
import com.project.ecommv2backend.repository.AddressRepository;
import com.project.ecommv2backend.security.model.UserRegistrationDTO;
import com.project.ecommv2backend.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    private AddressRepository addressRepository;

    @PostMapping("/register")

    public ResponseEntity<LocalUserDTO> registerNewUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) throws UserAlreadyExistsException, EmailFailureException {
           try{
               LocalUserDTO u = userService.registerNewUser(userRegistrationDTO);
               return ResponseEntity.status(HttpStatus.CREATED).body(u);
           } catch (UserAlreadyExistsException ex){
               return ResponseEntity.status(HttpStatus.CONFLICT).build();
           } catch (EmailFailureException ex){
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
           }
    }





}
