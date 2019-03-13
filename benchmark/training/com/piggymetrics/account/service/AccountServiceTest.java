package com.piggymetrics.account.service;


import Currency.USD;
import TimePeriod.DAY;
import TimePeriod.MONTH;
import com.piggymetrics.account.client.AuthServiceClient;
import com.piggymetrics.account.client.StatisticsServiceClient;
import com.piggymetrics.account.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AccountServiceTest {
    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private StatisticsServiceClient statisticsClient;

    @Mock
    private AuthServiceClient authClient;

    @Mock
    private AccountRepository repository;

    @Test
    public void shouldFindByName() {
        final Account account = new Account();
        account.setName("test");
        Mockito.when(accountService.findByName(account.getName())).thenReturn(account);
        Account found = accountService.findByName(account.getName());
        Assert.assertEquals(account, found);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenNameIsEmpty() {
        accountService.findByName("");
    }

    @Test
    public void shouldCreateAccountWithGivenUser() {
        User user = new User();
        user.setUsername("test");
        Account account = accountService.create(user);
        Assert.assertEquals(user.getUsername(), account.getName());
        Assert.assertEquals(0, account.getSaving().getAmount().intValue());
        Assert.assertEquals(Currency.getDefault(), account.getSaving().getCurrency());
        Assert.assertEquals(0, account.getSaving().getInterest().intValue());
        Assert.assertEquals(false, account.getSaving().getDeposit());
        Assert.assertEquals(false, account.getSaving().getCapitalization());
        Assert.assertNotNull(account.getLastSeen());
        Mockito.verify(authClient, Mockito.times(1)).createUser(user);
        Mockito.verify(repository, Mockito.times(1)).save(account);
    }

    @Test
    public void shouldSaveChangesWhenUpdatedAccountGiven() {
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
        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(1500));
        saving.setCurrency(USD);
        saving.setInterest(new BigDecimal("3.32"));
        saving.setDeposit(true);
        saving.setCapitalization(false);
        final Account update = new Account();
        update.setName("test");
        update.setNote("test note");
        update.setIncomes(Arrays.asList(salary));
        update.setExpenses(Arrays.asList(grocery));
        update.setSaving(saving);
        final Account account = new Account();
        Mockito.when(accountService.findByName("test")).thenReturn(account);
        accountService.saveChanges("test", update);
        Assert.assertEquals(update.getNote(), account.getNote());
        Assert.assertNotNull(account.getLastSeen());
        Assert.assertEquals(update.getSaving().getAmount(), account.getSaving().getAmount());
        Assert.assertEquals(update.getSaving().getCurrency(), account.getSaving().getCurrency());
        Assert.assertEquals(update.getSaving().getInterest(), account.getSaving().getInterest());
        Assert.assertEquals(update.getSaving().getDeposit(), account.getSaving().getDeposit());
        Assert.assertEquals(update.getSaving().getCapitalization(), account.getSaving().getCapitalization());
        Assert.assertEquals(update.getExpenses().size(), account.getExpenses().size());
        Assert.assertEquals(update.getIncomes().size(), account.getIncomes().size());
        Assert.assertEquals(update.getExpenses().get(0).getTitle(), account.getExpenses().get(0).getTitle());
        Assert.assertEquals(0, update.getExpenses().get(0).getAmount().compareTo(account.getExpenses().get(0).getAmount()));
        Assert.assertEquals(update.getExpenses().get(0).getCurrency(), account.getExpenses().get(0).getCurrency());
        Assert.assertEquals(update.getExpenses().get(0).getPeriod(), account.getExpenses().get(0).getPeriod());
        Assert.assertEquals(update.getExpenses().get(0).getIcon(), account.getExpenses().get(0).getIcon());
        Assert.assertEquals(update.getIncomes().get(0).getTitle(), account.getIncomes().get(0).getTitle());
        Assert.assertEquals(0, update.getIncomes().get(0).getAmount().compareTo(account.getIncomes().get(0).getAmount()));
        Assert.assertEquals(update.getIncomes().get(0).getCurrency(), account.getIncomes().get(0).getCurrency());
        Assert.assertEquals(update.getIncomes().get(0).getPeriod(), account.getIncomes().get(0).getPeriod());
        Assert.assertEquals(update.getIncomes().get(0).getIcon(), account.getIncomes().get(0).getIcon());
        Mockito.verify(repository, Mockito.times(1)).save(account);
        Mockito.verify(statisticsClient, Mockito.times(1)).updateStatistics("test", account);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenNoAccountsExistedWithGivenName() {
        final Account update = new Account();
        update.setIncomes(Arrays.asList(new Item()));
        update.setExpenses(Arrays.asList(new Item()));
        Mockito.when(accountService.findByName("test")).thenReturn(null);
        accountService.saveChanges("test", update);
    }
}

