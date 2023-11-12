package com.project.ecommv2backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.project.ecommv2backend.exception.EmailNotFoundException;
import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.repository.LocalUserRepository;
import com.project.ecommv2backend.security.jwt.JwtService;
import com.project.ecommv2backend.security.model.UserRegistrationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTServiceTest {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private LocalUserRepository repository;

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void testVerificationTokenNotUsableForLogin(){
        LocalUser user = repository.findByUsernameIgnoreCase("UserA@junit.com").get();
        String token = jwtService.generateVerificationJWT(user);
        Assertions.assertNull(jwtService.getUsername(token), "Verification token should not contain username");

    }

    @Test
    public void testAuthTokenReturnsUsername(){
        LocalUser user = repository.findByUsernameIgnoreCase("UserA@junit.com ").get();
        String token = jwtService.generateJWT(user);
        Assertions.assertEquals(user.getUsername(), jwtService.getUsername(token), "Token for auth should not contain user's username");
    }

   @Test
    public void testLoginJWTNotGeneratedByUs(){
       String token = JWT.create().withClaim("USERNAME", "UserA@junit.com").sign(Algorithm.HMAC256("NotTheRealSecret"));
       Assertions.assertThrows(SignatureVerificationException.class, ()-> jwtService.getUsername(token));
   }

   @Test
    public void testJWTCorrectlySignedNoIssuer(){
        String token = JWT.create().withClaim("USERNAME", "UserA@junit.com")
                .sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class, ()-> jwtService.getUsername(token));
   }

   @Test
    public void testPasswordResetToken(){
        LocalUser user = repository.findByUsernameIgnoreCase("UserA@junit.com").get();
        String token = jwtService.generatePasswordResetJWT(user);
        Assertions.assertEquals(user.getEmail(), jwtService.getResetPasswordEmail(token), "Email should match inside "+ "JWT");
   }

    @Test
    public void testResetPasswordJWTNotGeneratedByUs(){
        String token = JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com").sign(Algorithm.HMAC256("NotTheRealSecret"));
        Assertions.assertThrows(SignatureVerificationException.class, ()-> jwtService.getResetPasswordEmail(token));
    }

    @Test
    public void testResetPasswordJWTCorrectlySignedNoIssuer(){
        String token = JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com")
                .sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class, ()-> jwtService.getResetPasswordEmail(token));
    }
}
