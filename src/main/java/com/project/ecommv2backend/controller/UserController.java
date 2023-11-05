package com.project.ecommv2backend.controller;

import com.project.ecommv2backend.exception.EmailFailureException;
import com.project.ecommv2backend.exception.UserAlreadyExistsException;
import com.project.ecommv2backend.model.dto.LocalUserDTO;
import com.project.ecommv2backend.security.model.UserRegistrationDTO;
import com.project.ecommv2backend.service.UserServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserServiceImpl userService;

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
