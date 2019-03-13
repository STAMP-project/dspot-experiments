package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class SpaceTest extends AbstractFakerTest {
    @Test
    public void planet() {
        Assert.assertThat(faker.space().planet(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void moon() {
        Assert.assertThat(faker.space().moon(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void galaxy() {
        Assert.assertThat(faker.space().galaxy(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void nebula() {
        Assert.assertThat(faker.space().nebula(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void starCluster() {
        Assert.assertThat(faker.space().starCluster(), MatchesRegularExpression.matchesRegularExpression("(\\w+[ -]?){1,3}"));
    }

    @Test
    public void constellation() {
        Assert.assertThat(faker.space().constellation(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void star() {
        Assert.assertThat(faker.space().star(), MatchesRegularExpression.matchesRegularExpression("(\\w+[ -]?){2,3}"));
    }

    @Test
    public void agency() {
        Assert.assertThat(faker.space().agency(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,5}"));
    }

    @Test
    public void agencyAbbreviation() {
        Assert.assertThat(faker.space().agencyAbbreviation(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void nasaSpaceCraft() {
        Assert.assertThat(faker.space().nasaSpaceCraft(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void company() {
        Assert.assertThat(faker.space().company(), MatchesRegularExpression.matchesRegularExpression("((\\w|\')+ ?){2,4}"));
    }

    @Test
    public void distanceMeasurement() {
        Assert.assertThat(faker.space().distanceMeasurement(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void meteorite() {
        Assert.assertThat(faker.space().meteorite(), MatchesRegularExpression.matchesRegularExpression("(?U)([\\w()]+[ -\u2013]?){1,4}"));
    }
}

