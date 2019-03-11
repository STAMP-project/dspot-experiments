package com.annimon.stream.function;


import LongUnaryOperator.Util;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link LongUnaryOperator}
 */
public class LongUnaryOperatorTest {
    @Test
    public void testIdentity() {
        LongUnaryOperator identity = Util.identity();
        Assert.assertThat(identity.applyAsLong(3228), Matchers.is(3228L));
    }

    @Test
    public void testPrivateUtilConstructor() {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }
}

