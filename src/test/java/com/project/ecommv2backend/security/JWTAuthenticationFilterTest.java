package com.project.ecommv2backend.security;

import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.repository.LocalUserRepository;
import com.project.ecommv2backend.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class JWTAuthenticationFilterTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private LocalUserRepository repository;

    private static final String AUTHENTICATED_PATH = "/auth/me";

    @Test
    public void testAuthenticatedRequest() throws Exception {
        mvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));

    }

    @Test
    public void testBadToken() throws Exception{
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "BadTokenThatIsNotValid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        mvc.perform((get(AUTHENTICATED_PATH).header("Authorization", "Bearer BadTokenThatIsNotValid")))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testUnverifiedUser() throws Exception{
        LocalUser user = repository.findByUsernameIgnoreCase("UserB@junit.com").get();
        String token = jwtService.generateJWT(user);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer " + token)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testSuccessful() throws Exception{
        LocalUser user = repository.findByUsernameIgnoreCase("UserA@junit.com").get();
        String token = jwtService.generateJWT(user);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer "+ token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
