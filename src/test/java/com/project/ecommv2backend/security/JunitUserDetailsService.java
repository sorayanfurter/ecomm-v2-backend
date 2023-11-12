package com.project.ecommv2backend.security;

import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@Primary
public class JunitUserDetailsService implements UserDetailsService {

    @Autowired
    private LocalUserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<LocalUser> opUser = repository.findByUsernameIgnoreCase(username);
        if(opUser.isPresent())
            return opUser.get();
        return null;
    }
}
