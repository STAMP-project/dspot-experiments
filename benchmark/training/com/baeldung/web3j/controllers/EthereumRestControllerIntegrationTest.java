package com.baeldung.web3j.controllers;


import Constants.API_ACCOUNTS;
import Constants.API_BALANCE;
import Constants.API_BLOCK;
import Constants.API_TRANSACTIONS;
import com.baeldung.web3j.config.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@WebAppConfiguration
public class EthereumRestControllerIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void accounts() {
        constructAsyncTest(API_ACCOUNTS);
    }

    @Test
    public void transactions() {
        constructAsyncTest(API_TRANSACTIONS);
    }

    @Test
    public void block() {
        constructAsyncTest(API_BLOCK);
    }

    @Test
    public void balance() {
        constructAsyncTest(API_BALANCE);
    }
}

