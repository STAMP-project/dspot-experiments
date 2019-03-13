package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class FoodTest extends AbstractFakerTest {
    @Test
    public void ingredient() {
        Assert.assertThat(faker.food().ingredient(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void spice() {
        Assert.assertThat(faker.food().spice(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z1-9- ]+"));
    }

    @Test
    public void measurement() {
        Assert.assertThat(faker.food().measurement(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z1-9/ ]+{2}"));
    }
}

