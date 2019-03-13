package com.baeldung.web3j.controllers;


import Constants.API_ACCOUNTS;
import Constants.API_BALANCE;
import Constants.API_BLOCK;
import Constants.API_TRANSACTIONS;
import com.baeldung.web3j.services.Web3Service;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;


public class EthereumRestControllerUnitTest {
    private MockMvc mockMvc;

    @Mock
    private Web3Service web3Service;

    @InjectMocks
    private EthereumRestController ethereumRestController;

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

