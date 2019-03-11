package com.piggymetrics.statistics.service;


import Currency.EUR;
import Currency.RUB;
import Currency.USD;
import StatisticMetric.EXPENSES_AMOUNT;
import StatisticMetric.INCOMES_AMOUNT;
import StatisticMetric.SAVING_AMOUNT;
import TimePeriod.DAY;
import TimePeriod.MONTH;
import TimePeriod.YEAR;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.piggymetrics.statistics.domain.Account;
import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.Item;
import com.piggymetrics.statistics.domain.Saving;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.ItemMetric;
import com.piggymetrics.statistics.repository.DataPointRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class StatisticsServiceImplTest {
    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private ExchangeRatesServiceImpl ratesService;

    @Mock
    private DataPointRepository repository;

    @Test
    public void shouldFindDataPointListByAccountName() {
        final List<DataPoint> list = ImmutableList.of(new DataPoint());
        Mockito.when(repository.findByIdAccount("test")).thenReturn(list);
        List<DataPoint> result = statisticsService.findByAccountName("test");
        Assert.assertEquals(list, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToFindDataPointWhenAccountNameIsNull() {
        statisticsService.findByAccountName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToFindDataPointWhenAccountNameIsEmpty() {
        statisticsService.findByAccountName("");
    }

    @Test
    public void shouldSaveDataPoint() {
        /**
         * Given
         */
        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setAmount(new BigDecimal(9100));
        salary.setCurrency(USD);
        salary.setPeriod(MONTH);
        Item grocery = new Item();
        grocery.setTitle("Grocery");
        grocery.setAmount(new BigDecimal(500));
        grocery.setCurrency(RUB);
        grocery.setPeriod(DAY);
        Item vacation = new Item();
        vacation.setTitle("Vacation");
        vacation.setAmount(new BigDecimal(3400));
        vacation.setCurrency(EUR);
        vacation.setPeriod(YEAR);
        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(1000));
        saving.setCurrency(EUR);
        saving.setInterest(new BigDecimal(3.2));
        saving.setDeposit(true);
        saving.setCapitalization(false);
        Account account = new Account();
        account.setIncomes(ImmutableList.of(salary));
        account.setExpenses(ImmutableList.of(grocery, vacation));
        account.setSaving(saving);
        final Map<Currency, BigDecimal> rates = ImmutableMap.of(EUR, new BigDecimal("0.8"), RUB, new BigDecimal("80"), USD, BigDecimal.ONE);
        /**
         * When
         */
        Mockito.when(ratesService.convert(ArgumentMatchers.any(Currency.class), ArgumentMatchers.any(Currency.class), ArgumentMatchers.any(BigDecimal.class))).then(( i) -> ((BigDecimal) (i.getArgument(2))).divide(rates.get(i.getArgument(0)), 4, RoundingMode.HALF_UP));
        Mockito.when(ratesService.getCurrentRates()).thenReturn(rates);
        Mockito.when(repository.save(ArgumentMatchers.any(DataPoint.class))).then(AdditionalAnswers.returnsFirstArg());
        DataPoint dataPoint = statisticsService.save("test", account);
        /**
         * Then
         */
        final BigDecimal expectedExpensesAmount = new BigDecimal("17.8861");
        final BigDecimal expectedIncomesAmount = new BigDecimal("298.9802");
        final BigDecimal expectedSavingAmount = new BigDecimal("1250");
        final BigDecimal expectedNormalizedSalaryAmount = new BigDecimal("298.9802");
        final BigDecimal expectedNormalizedVacationAmount = new BigDecimal("11.6361");
        final BigDecimal expectedNormalizedGroceryAmount = new BigDecimal("6.25");
        Assert.assertEquals(dataPoint.getId().getAccount(), "test");
        Assert.assertEquals(dataPoint.getId().getDate(), Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Assert.assertTrue(((expectedExpensesAmount.compareTo(dataPoint.getStatistics().get(EXPENSES_AMOUNT))) == 0));
        Assert.assertTrue(((expectedIncomesAmount.compareTo(dataPoint.getStatistics().get(INCOMES_AMOUNT))) == 0));
        Assert.assertTrue(((expectedSavingAmount.compareTo(dataPoint.getStatistics().get(SAVING_AMOUNT))) == 0));
        ItemMetric salaryItemMetric = dataPoint.getIncomes().stream().filter(( i) -> i.getTitle().equals(salary.getTitle())).findFirst().get();
        ItemMetric vacationItemMetric = dataPoint.getExpenses().stream().filter(( i) -> i.getTitle().equals(vacation.getTitle())).findFirst().get();
        ItemMetric groceryItemMetric = dataPoint.getExpenses().stream().filter(( i) -> i.getTitle().equals(grocery.getTitle())).findFirst().get();
        Assert.assertTrue(((expectedNormalizedSalaryAmount.compareTo(salaryItemMetric.getAmount())) == 0));
        Assert.assertTrue(((expectedNormalizedVacationAmount.compareTo(vacationItemMetric.getAmount())) == 0));
        Assert.assertTrue(((expectedNormalizedGroceryAmount.compareTo(groceryItemMetric.getAmount())) == 0));
        Assert.assertEquals(rates, dataPoint.getRates());
        Mockito.verify(repository, Mockito.times(1)).save(dataPoint);
    }
}

