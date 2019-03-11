package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class HipsterTest extends AbstractFakerTest {
    @Test
    public void word() {
        Assert.assertThat(faker.hipster().word(), MatchesRegularExpression.matchesRegularExpression("^([\\w-+&\']+ ?)+$"));
    }
}

