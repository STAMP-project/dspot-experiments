package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class FunnyNameTest extends AbstractFakerTest {
    @Test
    public void name() {
        Assert.assertThat(faker.funnyName().name(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\.?\\s?\'?-?)+$"));
    }
}

