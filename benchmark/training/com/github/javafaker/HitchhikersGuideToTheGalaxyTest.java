package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class HitchhikersGuideToTheGalaxyTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.hitchhikersGuideToTheGalaxy().character(), MatchesRegularExpression.matchesRegularExpression("^(\\w+(\\.?\\s?\'?))+$"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.hitchhikersGuideToTheGalaxy().location(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\S?\\.?\\s?\'?-?)+$"));
    }

    @Test
    public void marvinQuote() {
        Assert.assertFalse(faker.hitchhikersGuideToTheGalaxy().marvinQuote().isEmpty());
    }

    @Test
    public void planet() {
        Assert.assertThat(faker.hitchhikersGuideToTheGalaxy().planet(), MatchesRegularExpression.matchesRegularExpression("^(\\w+-?\\s?)+$"));
    }

    @Test
    public void quote() {
        Assert.assertFalse(faker.hitchhikersGuideToTheGalaxy().quote().isEmpty());
    }

    @Test
    public void specie() {
        Assert.assertThat(faker.hitchhikersGuideToTheGalaxy().specie(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\'?\\s?)+$"));
    }
}

