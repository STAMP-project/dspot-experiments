package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class MusicTest extends AbstractFakerTest {
    @Test
    public void instrument() {
        Assert.assertThat(faker.music().instrument(), MatchesRegularExpression.matchesRegularExpression("\\w+ ?\\w+"));
    }

    @Test
    public void key() {
        Assert.assertThat(faker.music().key(), MatchesRegularExpression.matchesRegularExpression("([A-Z])+(b|#){0,1}"));
    }

    @Test
    public void chord() {
        Assert.assertThat(faker.music().chord(), MatchesRegularExpression.matchesRegularExpression("([A-Z])+(b|#){0,1}+(-?[a-zA-Z0-9]{0,4})"));
    }

    @Test
    public void genre() {
        Assert.assertThat(faker.music().genre(), MatchesRegularExpression.matchesRegularExpression("[[ -]?\\w+]+"));
    }
}

