package com.github.javafaker;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class StockTest extends AbstractFakerTest {
    @Test
    public void testNasdaq() {
        Assert.assertThat(faker.stock().nsdqSymbol(), Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void testNYSE() {
        Assert.assertThat(faker.stock().nyseSymbol(), Matchers.not(Matchers.isEmptyOrNullString()));
    }
}

