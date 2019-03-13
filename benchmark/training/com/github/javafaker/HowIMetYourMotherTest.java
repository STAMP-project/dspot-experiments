package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class HowIMetYourMotherTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.howIMetYourMother().character(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\.?\\s?)+$"));
    }

    @Test
    public void catchPhrase() {
        Assert.assertFalse(faker.howIMetYourMother().catchPhrase().isEmpty());
    }

    @Test
    public void highFive() {
        Assert.assertThat(faker.howIMetYourMother().highFive(), MatchesRegularExpression.matchesRegularExpression("^(\\w+-?\\s?)+$"));
    }

    @Test
    public void quote() {
        Assert.assertFalse(faker.howIMetYourMother().quote().isEmpty());
    }
}

