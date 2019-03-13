package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class CurrencyTest extends AbstractFakerTest {
    @Test
    public void testName() {
        Assert.assertThat(faker.currency().name(), MatchesRegularExpression.matchesRegularExpression("[\\w\\\'\\.\\-\\(\\) ]+"));
    }

    @Test
    public void testCode() {
        final Currency currency = faker.currency();
        Assert.assertThat(currency.code(), MatchesRegularExpression.matchesRegularExpression("[A-Z]{3}"));
    }
}

