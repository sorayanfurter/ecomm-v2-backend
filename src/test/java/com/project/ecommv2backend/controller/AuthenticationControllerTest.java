package com.project.ecommv2backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ecommv2backend.security.auth.AuthenticationRequest;
import com.project.ecommv2backend.security.model.UserRegistrationDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;



    @Test
    @Transactional

    public void testRegister() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setUsername(null);
        userRegistrationDTO.setEmail("AuthenticationControllerTest$testRegister@junit.com");
        userRegistrationDTO.setFirstName("FirstName");
        userRegistrationDTO.setLastName("LastName");
        userRegistrationDTO.setPassword("Password123");

        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userRegistrationDTO)))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
