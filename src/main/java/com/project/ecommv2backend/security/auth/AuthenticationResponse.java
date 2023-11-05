package com.project.ecommv2backend.security.auth;

import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.model.dto.LocalUserDTO;
import lombok.*;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponse {

    private String jwt;
    private boolean success;
    private String failureReason;


}
