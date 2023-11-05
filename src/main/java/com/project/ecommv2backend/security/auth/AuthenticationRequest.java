package com.project.ecommv2backend.security.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
public class AuthenticationRequest {


    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String password;


}