package com.piggymetrics.statistics.controller;


import Currency.USD;
import MediaType.APPLICATION_JSON;
import TimePeriod.DAY;
import TimePeriod.MONTH;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.piggymetrics.statistics.domain.Account;
import com.piggymetrics.statistics.domain.Item;
import com.piggymetrics.statistics.domain.Saving;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.DataPointId;
import com.piggymetrics.statistics.service.StatisticsService;
import com.sun.security.auth.UserPrincipal;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsControllerTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private StatisticsController statisticsController;

    @Mock
    private StatisticsService statisticsService;

    private MockMvc mockMvc;

    @Test
    public void shouldGetStatisticsByAccountName() throws Exception {
        final DataPoint dataPoint = new DataPoint();
        dataPoint.setId(new DataPointId("test", new Date()));
        Mockito.when(statisticsService.findByAccountName(dataPoint.getId().getAccount())).thenReturn(ImmutableList.of(dataPoint));
        mockMvc.perform(get("/test").principal(new UserPrincipal(dataPoint.getId().getAccount()))).andExpect(jsonPath("$[0].id.account").value(dataPoint.getId().getAccount())).andExpect(status().isOk());
    }

    @Test
    public void shouldGetCurrentAccountStatistics() throws Exception {
        final DataPoint dataPoint = new DataPoint();
        dataPoint.setId(new DataPointId("test", new Date()));
        Mockito.when(statisticsService.findByAccountName(dataPoint.getId().getAccount())).thenReturn(ImmutableList.of(dataPoint));
        mockMvc.perform(get("/current").principal(new UserPrincipal(dataPoint.getId().getAccount()))).andExpect(jsonPath("$[0].id.account").value(dataPoint.getId().getAccount())).andExpect(status().isOk());
    }

    @Test
    public void shouldSaveAccountStatistics() throws Exception {
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
        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setAmount(new BigDecimal(9100));
        salary.setCurrency(USD);
        salary.setPeriod(MONTH);
        final Account account = new Account();
        account.setSaving(saving);
        account.setExpenses(ImmutableList.of(grocery));
        account.setIncomes(ImmutableList.of(salary));
        String json = StatisticsControllerTest.mapper.writeValueAsString(account);
        mockMvc.perform(put("/test").contentType(APPLICATION_JSON).content(json)).andExpect(status().isOk());
        Mockito.verify(statisticsService, VerificationModeFactory.times(1)).save(ArgumentMatchers.anyString(), ArgumentMatchers.any(Account.class));
    }
}

