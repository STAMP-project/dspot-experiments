package com.baeldung.kotlin;


import org.junit.Assert;
import org.junit.Test;


public class JavaCallToKotlinUnitTest {
    @Test
    public void givenKotlinClass_whenCallFromJava_shouldProduceResults() {
        // when
        int res = new MathematicsOperations().addTwoNumbers(2, 4);
        // then
        Assert.assertEquals(6, res);
    }
}

