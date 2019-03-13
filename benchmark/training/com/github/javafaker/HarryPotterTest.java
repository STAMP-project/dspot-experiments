package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class HarryPotterTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.harryPotter().character(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z,\\-\\.\\(\\) ]+"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.harryPotter().location(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z0-9\'\\. &,/]+"));
    }

    @Test
    public void quote() {
        Assert.assertThat(faker.harryPotter().quote(), IsNot.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void book() {
        Assert.assertThat(faker.harryPotter().book(), MatchesRegularExpression.matchesRegularExpression("Harry Potter and the ([A-Za-z\'\\-]+ ?)+"));
    }
}

