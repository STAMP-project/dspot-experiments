package com.annimon.stream.function;


import DoubleUnaryOperator.Util;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link DoubleUnaryOperator}
 */
public class DoubleUnaryOperatorTest {
    @Test
    public void testIdentity() {
        DoubleUnaryOperator identity = Util.identity();
        Assert.assertThat(identity.applyAsDouble(3.228), Matchers.closeTo(3.228, 0.001));
    }

    @Test
    public void testPrivateUtilConstructor() {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }
}

