package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class TeamTest extends AbstractFakerTest {
    @Test
    public void testName() {
        Assert.assertThat(faker.team().name(), MatchesRegularExpression.matchesRegularExpression("(\\w+( )?){2,4}"));
    }

    @Test
    public void testCreature() {
        Assert.assertThat(faker.team().creature(), MatchesRegularExpression.matchesRegularExpression("\\w+( \\w+)?"));
    }

    @Test
    public void testState() {
        Assert.assertThat(faker.team().state(), MatchesRegularExpression.matchesRegularExpression("(\\w+( )?){1,2}"));
    }

    @Test
    public void testSport() {
        Assert.assertThat(faker.team().sport(), MatchesRegularExpression.matchesRegularExpression("(\\p{L}|\\s)+"));
    }
}

