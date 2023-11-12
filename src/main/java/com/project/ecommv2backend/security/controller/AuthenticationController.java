package com.project.ecommv2backend.security.controller;


import com.project.ecommv2backend.exception.EmailFailureException;
import com.project.ecommv2backend.exception.EmailNotFoundException;
import com.project.ecommv2backend.exception.UserNotVerifiedException;
import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.model.PasswordResetBody;
import com.project.ecommv2backend.security.auth.AuthenticationRequest;
import com.project.ecommv2backend.security.auth.AuthenticationResponse;
import com.project.ecommv2backend.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {


    @Autowired
    private UserServiceImpl userService;


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginUser(@Valid @RequestBody AuthenticationRequest authenticationRequest){
        String jwt = null;
        try {
            jwt = userService.loginUser(authenticationRequest);
        } catch (UserNotVerifiedException ex) {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if(ex.isNewEmailSent()){
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if( jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token){
     if(userService.verifyUser(token)){
         return ResponseEntity.ok().build();
     } else {
         return ResponseEntity.status(HttpStatus.CONFLICT).build();
     }
    }

    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user){
        return user;
    }

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestParam String email){
    try{ userService.forgotPassword(email);
    return ResponseEntity.ok().build();
    } catch (EmailNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (EmailFailureException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    }

    @PostMapping("/reset")

    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body){
        userService.resetPassword(body);
        return ResponseEntity.ok().build();
    }

}