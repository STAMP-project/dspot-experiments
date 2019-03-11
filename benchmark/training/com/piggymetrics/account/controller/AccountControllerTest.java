package com.piggymetrics.account.controller;


import Currency.USD;
import MediaType.APPLICATION_JSON;
import TimePeriod.DAY;
import TimePeriod.MONTH;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.piggymetrics.account.service.AccountService;
import com.sun.security.auth.UserPrincipal;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountControllerTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    @Test
    public void shouldGetAccountByName() throws Exception {
        final Account account = new Account();
        account.setName("test");
        Mockito.when(accountService.findByName(account.getName())).thenReturn(account);
        mockMvc.perform(get(("/" + (account.getName())))).andExpect(jsonPath("$.name").value(account.getName())).andExpect(status().isOk());
    }

    @Test
    public void shouldGetCurrentAccount() throws Exception {
        final Account account = new Account();
        account.setName("test");
        Mockito.when(accountService.findByName(account.getName())).thenReturn(account);
        mockMvc.perform(get("/current").principal(new UserPrincipal(account.getName()))).andExpect(jsonPath("$.name").value(account.getName())).andExpect(status().isOk());
    }

    @Test
    public void shouldSaveCurrentAccount() throws Exception {
        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(1500));
        saving.setCurrency(USD);
        saving.setInterest(new BigDecimal("3.32"));
        saving.setDeposit(true);
        saving.setCapitalization(false);
        Item grocery = new Item();
        grocery.setTitle("Grocery");
        grocery.setAmount(new BigDecimal(10));
        grocery.setCurrency(USD);
        grocery.setPeriod(DAY);
        grocery.setIcon("meal");
        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setAmount(new BigDecimal(9100));
        salary.setCurrency(USD);
        salary.setPeriod(MONTH);
        salary.setIcon("wallet");
        final Account account = new Account();
        account.setName("test");
        account.setNote("test note");
        account.setLastSeen(new Date());
        account.setSaving(saving);
        account.setExpenses(ImmutableList.of(grocery));
        account.setIncomes(ImmutableList.of(salary));
        String json = AccountControllerTest.mapper.writeValueAsString(account);
        mockMvc.perform(put("/current").principal(new UserPrincipal(account.getName())).contentType(APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }

    @Test
    public void shouldFailOnValidationTryingToSaveCurrentAccount() throws Exception {
        final Account account = new Account();
        account.setName("test");
        String json = AccountControllerTest.mapper.writeValueAsString(account);
        mockMvc.perform(put("/current").principal(new UserPrincipal(account.getName())).contentType(APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRegisterNewAccount() throws Exception {
        final User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        String json = AccountControllerTest.mapper.writeValueAsString(user);
        System.out.println(json);
        mockMvc.perform(post("/").principal(new UserPrincipal("test")).contentType(APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }

    @Test
    public void shouldFailOnValidationTryingToRegisterNewAccount() throws Exception {
        final User user = new User();
        user.setUsername("t");
        String json = AccountControllerTest.mapper.writeValueAsString(user);
        mockMvc.perform(post("/").principal(new UserPrincipal("test")).contentType(APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }
}

