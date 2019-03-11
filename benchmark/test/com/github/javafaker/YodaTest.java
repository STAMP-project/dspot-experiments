package com.github.javafaker;


import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luka Obradovic (luka@vast.com)
 */
public class YodaTest extends AbstractFakerTest {
    @Test
    public void quote() {
        Assert.assertThat(faker.yoda().quote(), IsNot.not(Matchers.isEmptyOrNullString()));
    }
}

