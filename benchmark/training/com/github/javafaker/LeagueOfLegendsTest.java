package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class LeagueOfLegendsTest extends AbstractFakerTest {
    @Test
    public void champion() {
        Assert.assertThat(faker.leagueOfLegends().champion(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\.?-?\'?\\s?)+$"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.leagueOfLegends().location(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\s?)+$"));
    }

    @Test
    public void quote() {
        Assert.assertFalse(faker.leagueOfLegends().quote().isEmpty());
    }

    @Test
    public void summonerSpell() {
        Assert.assertThat(faker.leagueOfLegends().summonerSpell(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\s?!?)+$"));
    }

    @Test
    public void masteries() {
        Assert.assertThat(faker.leagueOfLegends().masteries(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\s?\'?)+$"));
    }

    @Test
    public void rank() {
        Assert.assertThat(faker.leagueOfLegends().rank(), MatchesRegularExpression.matchesRegularExpression("^\\w+(\\s[IV]+)?$"));
    }
}

