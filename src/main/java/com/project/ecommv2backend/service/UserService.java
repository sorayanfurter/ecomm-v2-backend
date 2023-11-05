package com.project.ecommv2backend.service;

import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.model.dto.LocalUserDTO;
import com.project.ecommv2backend.security.auth.AuthenticationRequest;
import com.project.ecommv2backend.security.model.UserRegistrationDTO;

import java.util.Optional;

public interface UserService {

    //Register User
    LocalUserDTO registerNewUser (UserRegistrationDTO user);


}
