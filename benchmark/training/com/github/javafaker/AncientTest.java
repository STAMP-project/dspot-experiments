package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class AncientTest extends AbstractFakerTest {
    @Test
    public void god() {
        Assert.assertThat(faker.ancient().god(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }

    @Test
    public void primordial() {
        Assert.assertThat(faker.ancient().primordial(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }

    @Test
    public void titan() {
        Assert.assertThat(faker.ancient().titan(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }

    @Test
    public void hero() {
        Assert.assertThat(faker.ancient().hero(), MatchesRegularExpression.matchesRegularExpression("(?U)\\w+"));
    }
}

