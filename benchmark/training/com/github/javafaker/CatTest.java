package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class CatTest extends AbstractFakerTest {
    @Test
    public void name() {
        Assert.assertThat(faker.cat().name(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z'() 0-9-]+"));
    }

    @Test
    public void breed() {
        Assert.assertThat(faker.cat().breed(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z'() 0-9-,]+"));
    }

    @Test
    public void registry() {
        Assert.assertThat(faker.cat().registry(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z?'() 0-9-]+"));
    }
}

