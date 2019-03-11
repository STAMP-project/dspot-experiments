package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class OverwatchTest extends AbstractFakerTest {
    @Test
    public void hero() {
        Assert.assertThat(faker.overwatch().hero(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\.?\\s?)+$"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.overwatch().location(), MatchesRegularExpression.matchesRegularExpression("^(.+\'?:?\\s?)+$"));
    }

    @Test
    public void quote() {
        Assert.assertFalse(faker.overwatch().quote().isEmpty());
    }
}

