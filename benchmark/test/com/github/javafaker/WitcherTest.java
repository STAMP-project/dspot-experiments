package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class WitcherTest extends AbstractFakerTest {
    @Test
    public void testCharacter() {
        Assert.assertThat(faker.witcher().character(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z' -???]+"));
    }

    @Test
    public void testWitcher() {
        Assert.assertThat(faker.witcher().character(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z -???]+"));
    }

    @Test
    public void testSchool() {
        Assert.assertThat(faker.witcher().school(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z]+"));
    }

    @Test
    public void testLocation() {
        Assert.assertThat(faker.witcher().location(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z -???]+"));
    }

    @Test
    public void testQuote() {
        Assert.assertThat(faker.witcher().quote(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z0-9 \u2026\\?\\!\\.\u2019\',]+"));
    }

    @Test
    public void testMonster() {
        Assert.assertThat(faker.witcher().monster(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z -]+"));
    }
}

