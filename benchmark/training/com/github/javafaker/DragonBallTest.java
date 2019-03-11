package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class DragonBallTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.dragonBall().character(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\.?\\s?-?)+$"));
    }
}

