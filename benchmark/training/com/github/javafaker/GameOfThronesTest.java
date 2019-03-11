package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class GameOfThronesTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.gameOfThrones().character(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z\'\\-\\(\\) ]+"));
    }

    @Test
    public void house() {
        Assert.assertThat(faker.gameOfThrones().house(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z' ]+"));
    }

    @Test
    public void city() {
        Assert.assertThat(faker.gameOfThrones().city(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z' ]+"));
    }

    @Test
    public void dragon() {
        Assert.assertThat(faker.gameOfThrones().dragon(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }

    @Test
    public void quote() {
        Assert.assertThat(faker.gameOfThrones().quote(), IsNot.not(Matchers.isEmptyOrNullString()));
    }
}

