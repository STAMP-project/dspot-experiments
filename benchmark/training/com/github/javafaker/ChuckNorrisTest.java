package com.github.javafaker;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ChuckNorrisTest extends AbstractFakerTest {
    @Test
    public void testFact() {
        Assert.assertThat(faker.chuckNorris().fact(), Matchers.not(Matchers.isEmptyOrNullString()));
    }
}

