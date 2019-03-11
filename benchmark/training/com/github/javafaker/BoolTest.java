package com.github.javafaker;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BoolTest extends AbstractFakerTest {
    @Test
    public void testBool() {
        for (int i = 0; i < 100; i++) {
            Assert.assertThat(faker.bool().bool(), Matchers.isOneOf(true, false));
        }
    }
}

