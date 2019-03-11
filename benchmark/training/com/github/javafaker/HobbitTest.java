package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class HobbitTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.hobbit().character(), MatchesRegularExpression.matchesRegularExpression("^(\\(?\\w+\\.?\\s?\\)?)+$"));
    }

    @Test
    public void thorinsCompany() {
        Assert.assertThat(faker.hobbit().thorinsCompany(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\s?)+$"));
    }

    @Test
    public void quote() {
        Assert.assertFalse(faker.hobbit().quote().isEmpty());
    }

    @Test
    public void location() {
        Assert.assertThat(faker.hobbit().location(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\'?-?\\s?)+$"));
    }
}

