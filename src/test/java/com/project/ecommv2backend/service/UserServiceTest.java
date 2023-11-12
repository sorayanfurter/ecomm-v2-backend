package com.project.ecommv2backend.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.project.ecommv2backend.exception.EmailFailureException;
import com.project.ecommv2backend.exception.EmailNotFoundException;
import com.project.ecommv2backend.exception.UserAlreadyExistsException;
import com.project.ecommv2backend.exception.UserNotVerifiedException;
import com.project.ecommv2backend.model.*;
import com.project.ecommv2backend.repository.LocalUserRepository;
import com.project.ecommv2backend.repository.RoleRepository;
import com.project.ecommv2backend.repository.VerificationTokenRepository;
import com.project.ecommv2backend.security.auth.AuthenticationRequest;
import com.project.ecommv2backend.security.jwt.JwtService;
import com.project.ecommv2backend.security.model.UserRegistrationDTO;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);



    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtService jwtService;

    @Mock
    private RoleRepository roleRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private EncryptionService encryptionService;
    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {

        UserRegistrationDTO body = new UserRegistrationDTO();
        LocalUser user = new LocalUser();
        body.setUsername("UserA");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setFirstName("FirstName");
        body.setLastName("LastName");
        body.setPassword("MySecretPassword123");
        Assertions.assertThrows(UserAlreadyExistsException.class, ()->userService.registerNewUser(body), "Username should already be in use");
        Role role = roleRepository. findRoleByName(ERole.USER).get();
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        user.setRoles(roleSet);
        when(roleRepository.findRoleByName(ERole.USER)).thenReturn(Optional.of(new Role()));
        body.setUsername("UserServiceTest$testRegisterUser");
        body.setEmail("UserA@junit.com");
        Assertions.assertThrows(UserAlreadyExistsException.class, ()-> userService.registerNewUser(body), "Email should already be in use");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        Assertions.assertDoesNotThrow(()-> userService.registerNewUser(body), "User should register succesfully");
        Assertions.assertEquals("UserServiceTest$testRegisterUser@junit.com", greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    @Transactional

    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("UserA-not@junit.com");
        request.setPassword("PasswordA123-BadPassword");
        Assertions.assertNull(userService.loginUser(request), "The user should not exist");
        request.setEmail("UserA@junit.com");
        Assertions.assertNull(userService.loginUser(request), "The password should be incorrect");
        request.setPassword("PasswordA123");
        Assertions.assertNotNull(userService.loginUser(request), "The user should login succesfully");
        request.setEmail("UserB@junit.com ");
        request.setPassword("PasswordB123");
        try{
            userService.loginUser(request);
            Assertions.assertTrue(false, "User should not have email verified");

        }catch (UserNotVerifiedException ex){
            Assertions.assertTrue(ex.isNewEmailSent(), "Email verification should be sent");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
        try{
            userService.loginUser(request);
            Assertions.assertTrue(false, "User should not have email verified");

        }catch (UserNotVerifiedException ex){
            Assertions.assertFalse(ex.isNewEmailSent(), "Email verification should not be resent");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
    }

    @Test
    @Transactional

    public void testVerifyUser() throws EmailFailureException{
        Assertions.assertFalse(userService.verifyUser("Bad Token"), "Token that is bad or does not exist should return false");
        AuthenticationRequest body = new AuthenticationRequest();
        body.setEmail("UserB@junit.com");
        body.setPassword("PasswordB123");
        try{
            userService.loginUser(body);
            Assertions.assertTrue(false, "User should not have email verified");

        }catch (UserNotVerifiedException ex){
          List<VerificationToken> tokens = verificationTokenRepository.findByUser_IdOrderByIdDesc(2L);
          String token = tokens.get(0).getToken();
          Assertions.assertTrue(userService.verifyUser(token), "Token should be valid");
          Assertions.assertNotNull(body, "The user should now be verified");
        }
    }

    @Test
    public void resetForgotPasswordErr() throws MessagingException {
        Assertions.assertThrows(EmailNotFoundException.class, ()-> userService.forgotPassword("UserNotExists@junit.com"));
        Assertions.assertDoesNotThrow(()-> userService.forgotPassword("UserA@junit.com"), "Non existing email should be rejected");
        Assertions.assertEquals("UserA@junit.com", greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString(), "Password " + "reset email should be sent");

    }
    @Test
    public void testResetPassword(){
        LocalUser user = localUserRepository.findByUsernameIgnoreCase("UserA@junit.com").get();
        String token = jwtService.generatePasswordResetJWT(user);
        PasswordResetBody body = new PasswordResetBody();
        body.setToken(token);
        body.setPassword("Password123456");
        userService.resetPassword(body);
        user = localUserRepository.findByUsernameIgnoreCase("UserA@junit.com").get();
        Assertions.assertTrue(encryptionService.verifyPassword("Password1234 56", user.getPassword()), "Password change should be written to DB");
    }
}
