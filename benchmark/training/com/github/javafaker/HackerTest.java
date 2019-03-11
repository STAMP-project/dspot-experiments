package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class HackerTest extends AbstractFakerTest {
    @Test
    public void testAbbreviation() {
        Assert.assertThat(faker.hacker().abbreviation(), MatchesRegularExpression.matchesRegularExpression("[A-Z]{2,4}"));
    }

    @Test
    public void testAdjective() {
        Assert.assertThat(faker.hacker().adjective(), MatchesRegularExpression.matchesRegularExpression("(\\w+[- ]?){1,2}"));
    }

    @Test
    public void testNoun() {
        Assert.assertThat(faker.hacker().noun(), MatchesRegularExpression.matchesRegularExpression("\\w+( \\w+)?"));
    }

    @Test
    public void testVerb() {
        Assert.assertThat(faker.hacker().verb(), MatchesRegularExpression.matchesRegularExpression("\\w+( \\w+)?"));
    }

    @Test
    public void testIngverb() {
        Assert.assertThat(faker.hacker().ingverb(), MatchesRegularExpression.matchesRegularExpression("\\w+ing( \\w+)?"));
    }
}

