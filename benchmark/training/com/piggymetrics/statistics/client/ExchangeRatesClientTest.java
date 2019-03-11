package com.piggymetrics.statistics.client;


import Currency.EUR;
import Currency.RUB;
import Currency.USD;
import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.ExchangeRatesContainer;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ExchangeRatesClientTest {
    @Autowired
    private ExchangeRatesClient client;

    @Test
    public void shouldRetrieveExchangeRates() {
        ExchangeRatesContainer container = client.getRates(Currency.getBase());
        Assert.assertEquals(container.getDate(), LocalDate.now());
        Assert.assertEquals(container.getBase(), Currency.getBase());
        Assert.assertNotNull(container.getRates());
        Assert.assertNotNull(container.getRates().get(USD.name()));
        Assert.assertNotNull(container.getRates().get(EUR.name()));
        Assert.assertNotNull(container.getRates().get(RUB.name()));
    }

    @Test
    public void shouldRetrieveExchangeRatesForSpecifiedCurrency() {
        Currency requestedCurrency = Currency.EUR;
        ExchangeRatesContainer container = client.getRates(Currency.getBase());
        Assert.assertEquals(container.getDate(), LocalDate.now());
        Assert.assertEquals(container.getBase(), Currency.getBase());
        Assert.assertNotNull(container.getRates());
        Assert.assertNotNull(container.getRates().get(requestedCurrency.name()));
    }
}

