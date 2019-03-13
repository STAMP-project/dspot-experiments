package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class BeerTest extends AbstractFakerTest {
    @Test
    public void testName() {
        Assert.assertThat(faker.beer().name(), MatchesRegularExpression.matchesRegularExpression("[\\p{L}\'()\\., 0-9-\u2019\u2019]+"));
    }

    @Test
    public void testStyle() {
        Assert.assertThat(faker.beer().style(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z'() 0-9-]+"));
    }

    @Test
    public void testHop() {
        Assert.assertThat(faker.beer().hop(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z\'\u2019()\\. 0-9-]+"));
    }

    @Test
    public void testMalt() {
        Assert.assertThat(faker.beer().malt(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z'() 0-9-]+"));
    }

    @Test
    public void testYeast() {
        Assert.assertThat(faker.beer().yeast(), MatchesRegularExpression.matchesRegularExpression("[\\p{L}\'() 0-9-\u00f6]+"));
    }
}

