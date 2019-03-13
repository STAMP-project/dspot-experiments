package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class ColorTest extends AbstractFakerTest {
    @Test
    public void testName() {
        Assert.assertThat(faker.color().name(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){1,2}"));
    }
}

