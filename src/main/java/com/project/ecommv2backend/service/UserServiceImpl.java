package com.project.ecommv2backend.service;

import com.project.ecommv2backend.exception.*;
import com.project.ecommv2backend.model.*;
import com.project.ecommv2backend.model.dto.LocalUserDTO;
import com.project.ecommv2backend.repository.LocalUserRepository;
import com.project.ecommv2backend.repository.RoleRepository;
import com.project.ecommv2backend.repository.VerificationTokenRepository;
import com.project.ecommv2backend.security.auth.AuthenticationRequest;
import com.project.ecommv2backend.security.jwt.JwtService;
import com.project.ecommv2backend.security.model.UserRegistrationDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl {

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    JwtService jwtService;
    @Autowired
    EncryptionService encryptionService;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    /**
     * Attempts to register a user given the information provided.
     * @param userRegistrationDTO
     * @return The local user that has been written to the database.
     * @throws UserAlreadyExistsException Thrown if there is already a user with the given information.
     */

    public LocalUserDTO registerNewUser(UserRegistrationDTO userRegistrationDTO) throws UserAlreadyExistsException, EmailFailureException {
        if(localUserRepository.findUserByEmail(userRegistrationDTO.getEmail()).isPresent() || localUserRepository.findByUsernameIgnoreCase(userRegistrationDTO.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        LocalUser user = mapper.map(userRegistrationDTO, LocalUser.class);
        user.setUsername(user.getUsername());
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setEmail(user.getEmail());
        // Encode password
        user.setPassword(encryptionService.encryptPassword(userRegistrationDTO.getPassword()));
        // Set role to user
        Role role = roleRepository. findRoleByName(ERole.USER).get();
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        user.setRoles(roleSet);
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        LocalUser created = localUserRepository.save(user);
        LocalUserDTO localUserDTO = mapper.map(created, LocalUserDTO.class);

        return new LocalUserDTO(user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    private VerificationToken createVerificationToken(LocalUser user){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setLocalUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
        }

    public String loginUser(AuthenticationRequest authenticationRequest) throws UserNotVerifiedException, EmailFailureException{
        Optional<LocalUser> opUser = localUserRepository.findByEmailIgnoreCase(authenticationRequest.getEmail());
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            if (encryptionService.verifyPassword(authenticationRequest.getPassword(), user.getPassword())) {
                if (user.isEmailVerified()){
                return jwtService.generateJWT(user);
            }else{
                    List<VerificationToken>verificationTokens= user.getVerificationTokens();
                    boolean resend = verificationTokens.size() == 0 || verificationTokens.get(0).getCreatedTimeStamp().before(new Timestamp(System.currentTimeMillis() - (60*60*1000)));
                    if(resend) {
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenRepository.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken = verificationTokenRepository.findByToken(token);
        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getLocalUser();
            if(!user.isEmailVerified()){
                user.setEmailVerified(true);
                localUserRepository.save(user);
                verificationTokenRepository.deleteByUser(user);
                return true;
            }
        }
        return false;
    }

    public void resetPassword(PasswordResetBody body){
        String email = jwtService.getResetPasswordEmail(body.getToken());
        Optional<LocalUser> opUser = localUserRepository.findByEmailIgnoreCase(email);
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            user.setPassword(encryptionService.encryptPassword(body.getPassword()));
            localUserRepository.save(user);
        }
    }

    public void forgotPassword(String email) throws EmailFailureException {
        LocalUser user = localUserRepository.findByEmailIgnoreCase(email)
                .orElseThrow(()-> new EmailNotFoundException("Email has not been found"));
            String token = jwtService.generatePasswordResetJWT(user);
            emailService.sendPasswordResetEmail(user, token);

    }


}