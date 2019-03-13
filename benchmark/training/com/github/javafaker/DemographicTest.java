package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class DemographicTest extends AbstractFakerTest {
    @Test
    public void race() {
        Assert.assertThat(faker.demographic().race(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?)+"));
    }

    @Test
    public void educationalAttainment() {
        Assert.assertThat(faker.demographic().educationalAttainment(), MatchesRegularExpression.matchesRegularExpression("(?U)([\\w\'-]+ ?)+"));
    }

    @Test
    public void demonym() {
        Assert.assertThat(faker.demographic().demonym(), MatchesRegularExpression.matchesRegularExpression("(?U)([\\w\'-]+ ?)+"));
    }

    @Test
    public void maritalStatus() {
        Assert.assertThat(faker.demographic().maritalStatus(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?)+"));
    }

    @Test
    public void sex() {
        Assert.assertThat(faker.demographic().sex(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }
}

