package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class LebowskiTest extends AbstractFakerTest {
    @Test
    public void actor() {
        Assert.assertThat(faker.lebowski().actor(), MatchesRegularExpression.matchesRegularExpression("^([\\w]+ ?){1,3}$"));
    }

    @Test
    public void character() {
        Assert.assertThat(faker.lebowski().character(), MatchesRegularExpression.matchesRegularExpression("^([\\w]+ ?){1,3}$"));
    }

    @Test
    public void quote() {
        Assert.assertThat(faker.lebowski().quote(), MatchesRegularExpression.matchesRegularExpression("^([\\w.,!?\'-]+ ?)+$"));
    }
}

