package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class StarTrekTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.starTrek().character(), MatchesRegularExpression.matchesRegularExpression("^(\\w+-?\'?\\.?\\s?)+$"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.starTrek().location(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\'?\\s?)+$"));
    }

    @Test
    public void specie() {
        Assert.assertThat(faker.starTrek().specie(), MatchesRegularExpression.matchesRegularExpression("^(\\w+-?\'?\\s?)+$"));
    }

    @Test
    public void villain() {
        Assert.assertThat(faker.starTrek().villain(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\'?\\.?\\s?)+$"));
    }
}

