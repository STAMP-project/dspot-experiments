package com.github.javafaker;


import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class MatzTest extends AbstractFakerTest {
    @Test
    public void quote() {
        Assert.assertThat(faker.matz().quote(), IsNot.not(Matchers.isEmptyOrNullString()));
    }
}

