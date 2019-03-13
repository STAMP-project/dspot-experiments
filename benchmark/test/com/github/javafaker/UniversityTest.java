package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class UniversityTest extends AbstractFakerTest {
    @Test
    public void testName() {
        Assert.assertThat(faker.university().name(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z'() ]+"));
    }

    @Test
    public void testPrefix() {
        Assert.assertThat(faker.university().prefix(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }

    @Test
    public void testSuffix() {
        Assert.assertThat(faker.university().suffix(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }
}

