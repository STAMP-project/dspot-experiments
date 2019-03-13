package com.piggymetrics.statistics.service;


import Currency.EUR;
import Currency.RUB;
import Currency.USD;
import com.google.common.collect.ImmutableMap;
import com.piggymetrics.statistics.client.ExchangeRatesClient;
import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.ExchangeRatesContainer;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ExchangeRatesServiceImplTest {
    @InjectMocks
    private ExchangeRatesServiceImpl ratesService;

    @Mock
    private ExchangeRatesClient client;

    @Test
    public void shouldReturnCurrentRatesWhenContainerIsEmptySoFar() {
        ExchangeRatesContainer container = new ExchangeRatesContainer();
        container.setRates(ImmutableMap.of(EUR.name(), new BigDecimal("0.8"), RUB.name(), new BigDecimal("80")));
        Mockito.when(client.getRates(Currency.getBase())).thenReturn(container);
        Map<Currency, BigDecimal> result = ratesService.getCurrentRates();
        Mockito.verify(client, Mockito.times(1)).getRates(Currency.getBase());
        Assert.assertEquals(container.getRates().get(EUR.name()), result.get(EUR));
        Assert.assertEquals(container.getRates().get(RUB.name()), result.get(RUB));
        Assert.assertEquals(BigDecimal.ONE, result.get(USD));
    }

    @Test
    public void shouldNotRequestRatesWhenTodaysContainerAlreadyExists() {
        ExchangeRatesContainer container = new ExchangeRatesContainer();
        container.setRates(ImmutableMap.of(EUR.name(), new BigDecimal("0.8"), RUB.name(), new BigDecimal("80")));
        Mockito.when(client.getRates(Currency.getBase())).thenReturn(container);
        // initialize container
        ratesService.getCurrentRates();
        // use existing container
        ratesService.getCurrentRates();
        Mockito.verify(client, Mockito.times(1)).getRates(Currency.getBase());
    }

    @Test
    public void shouldConvertCurrency() {
        ExchangeRatesContainer container = new ExchangeRatesContainer();
        container.setRates(ImmutableMap.of(EUR.name(), new BigDecimal("0.8"), RUB.name(), new BigDecimal("80")));
        Mockito.when(client.getRates(Currency.getBase())).thenReturn(container);
        final BigDecimal amount = new BigDecimal(100);
        final BigDecimal expectedConvertionResult = new BigDecimal("1.25");
        BigDecimal result = ratesService.convert(RUB, USD, amount);
        Assert.assertTrue(((expectedConvertionResult.compareTo(result)) == 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToConvertWhenAmountIsNull() {
        ratesService.convert(EUR, RUB, null);
    }
}

