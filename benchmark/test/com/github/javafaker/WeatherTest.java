package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class WeatherTest extends AbstractFakerTest {
    @Test
    public void description() {
        Assert.assertThat(faker.weather().description(), CoreMatchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void temperatureCelsius() {
        Assert.assertThat(faker.weather().temperatureCelsius(), MatchesRegularExpression.matchesRegularExpression("[-]?\\d+\u00b0C"));
    }

    @Test
    public void temperatureFahrenheit() {
        Assert.assertThat(faker.weather().temperatureFahrenheit(), MatchesRegularExpression.matchesRegularExpression("[-]?\\d+\u00b0F"));
    }

    @Test
    public void temperatureCelsiusInRange() {
        for (int i = 1; i < 100; i++) {
            Assert.assertThat(faker.weather().temperatureCelsius((-5), 5), MatchesRegularExpression.matchesRegularExpression("[-]?[0-5]?C"));
        }
    }

    @Test
    public void temperatureFahrenheitInRange() {
        for (int i = 1; i < 100; i++) {
            Assert.assertThat(faker.weather().temperatureFahrenheit((-5), 5), MatchesRegularExpression.matchesRegularExpression("[-]?[0-5]?F"));
        }
    }
}

