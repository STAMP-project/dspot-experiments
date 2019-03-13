package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class ZeldaTest extends AbstractFakerTest {
    @Test
    public void game() {
        Assert.assertThat(faker.zelda().game(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z\'\\- :]+"));
    }

    @Test
    public void character() {
        Assert.assertThat(faker.zelda().character(), MatchesRegularExpression.matchesRegularExpression("(?U)([\\w\'\\-.\\(\\)]+ ?)+"));
    }
}

