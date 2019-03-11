package com.baeldung.junit4vstestng;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ParametrizedUnitTest {
    private int value;

    private boolean isEven;

    public ParametrizedUnitTest(int value, boolean isEven) {
        this.value = value;
        this.isEven = isEven;
    }

    @Test
    public void givenParametrizedNumber_ifEvenCheckOK_thenCorrect() {
        Assert.assertEquals(isEven, (((value) % 2) == 0));
    }
}

