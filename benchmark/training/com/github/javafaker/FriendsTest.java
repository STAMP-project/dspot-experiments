package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class FriendsTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.friends().character(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z .,]+"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.friends().location(), MatchesRegularExpression.matchesRegularExpression("[\\w.\', ]+"));
    }

    @Test
    public void quote() {
        Assert.assertThat(faker.friends().quote(), IsNot.not(Matchers.isEmptyOrNullString()));
    }
}

