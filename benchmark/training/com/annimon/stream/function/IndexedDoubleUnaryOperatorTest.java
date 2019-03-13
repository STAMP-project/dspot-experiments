package com.annimon.stream.function;


import IndexedDoubleUnaryOperator.Util;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link IndexedDoubleUnaryOperator}
 */
public class IndexedDoubleUnaryOperatorTest {
    @Test
    public void testWrap() {
        IndexedDoubleUnaryOperator identity = Util.wrap(DoubleUnaryOperator.Util.identity());
        Assert.assertThat(identity.applyAsDouble(1, 0.2), Matchers.closeTo(0.2, 0.001));
    }

    @Test
    public void testPrivateUtilConstructor() {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }
}

