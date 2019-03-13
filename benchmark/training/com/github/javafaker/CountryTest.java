package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import com.github.javafaker.repeating.Repeat;
import org.junit.Assert;
import org.junit.Test;


public class CountryTest extends AbstractFakerTest {
    @Test
    @Repeat(times = 10)
    public void testFlag() {
        String flag = faker.country().flag();
        Assert.assertThat(flag, MatchesRegularExpression.matchesRegularExpression("^http:\\/\\/flags.fmcdn\\.net\\/data\\/flags\\/w580\\/[a-zA-Z0-9_]+\\.png$"));
    }

    @Test
    public void testCode2() {
        Assert.assertThat(faker.country().countryCode2(), MatchesRegularExpression.matchesRegularExpression("([a-z]{2})"));
    }

    @Test
    public void testCode3() {
        Assert.assertThat(faker.country().countryCode3(), MatchesRegularExpression.matchesRegularExpression("([a-z]{3})"));
    }

    @Test
    public void testCapital() {
        Assert.assertThat(faker.country().capital(), MatchesRegularExpression.matchesRegularExpression("([\\w-]+ ?)+"));
    }

    @Test
    public void testCurrency() {
        Assert.assertThat(faker.country().currency(), MatchesRegularExpression.matchesRegularExpression("([\\w-]+ ?)+"));
    }

    @Test
    public void testCurrencyCode() {
        Assert.assertThat(faker.country().currencyCode(), MatchesRegularExpression.matchesRegularExpression("([\\w-]+ ?)+"));
    }
}

