package com.github.javafaker;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ShakespeareTest extends AbstractFakerTest {
    @Test
    public void testHamletQuote() {
        Assert.assertThat(faker.shakespeare().hamletQuote(), Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void testAsYouLikeItQuote() {
        Assert.assertThat(faker.shakespeare().asYouLikeItQuote(), Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void testKingRichardIIIQuote() {
        Assert.assertThat(faker.shakespeare().kingRichardIIIQuote(), Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void testRomeoAndJulietQuote() {
        Assert.assertThat(faker.shakespeare().romeoAndJulietQuote(), Matchers.not(Matchers.isEmptyOrNullString()));
    }
}

