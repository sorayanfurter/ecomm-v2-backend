package com.project.ecommv2backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ecommv2backend.model.WebOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithUserDetails("UserA@junit.com")
    public void testAuthenticatedOrderList() throws Exception{
        testAuthenticatedListBelongsToUser("UserA@junit.com");
    }

    @Test
    @WithUserDetails("UserB@junit.com")
    public void testUserBAuthenticatedOrderList() throws Exception{
        testAuthenticatedListBelongsToUser("UserB@junit.com");
    }

    public void testAuthenticatedListBelongsToUser(String username) throws Exception {
        mvc.perform(get("/order/")).andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(result -> {String json= result.getResponse().getContentAsString();
                    List<WebOrder> orders = new ObjectMapper().readValue(json, new TypeReference<List<WebOrder>>(){});
                    for(WebOrder order : orders){
                        Assertions.assertEquals(username, order.getUser().getUsername(), "Order list should only be orders of the user");
                    }
                });
    }

    @Test
    public void testUnauthenticatedOrderList() throws Exception{
        mvc.perform(get("/order")).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
}
