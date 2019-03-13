package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class SuperheroTest extends AbstractFakerTest {
    @Test
    public void testName() {
        Assert.assertThat(faker.superhero().name(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z' -/]+"));
    }

    @Test
    public void testPrefix() {
        Assert.assertThat(faker.superhero().prefix(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z -]+"));
    }

    @Test
    public void testSuffix() {
        Assert.assertThat(faker.superhero().suffix(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z -]+"));
    }

    @Test
    public void testPower() {
        Assert.assertThat(faker.superhero().power(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z/ -]+"));
    }

    @Test
    public void testDescriptor() {
        Assert.assertThat(faker.superhero().descriptor(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z' -]+"));
    }
}

