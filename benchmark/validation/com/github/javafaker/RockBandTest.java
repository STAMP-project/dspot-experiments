package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class RockBandTest extends AbstractFakerTest {
    @Test
    public void name() {
        Assert.assertThat(faker.rockBand().name(), MatchesRegularExpression.matchesRegularExpression("([\\w\'/.,&]+ ?)+"));
    }
}

