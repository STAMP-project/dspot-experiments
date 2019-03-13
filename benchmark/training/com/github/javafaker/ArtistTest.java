package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class ArtistTest extends AbstractFakerTest {
    @Test
    public void name() {
        Assert.assertThat(faker.artist().name(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){1,2}"));
    }
}

