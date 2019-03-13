package com.annimon.stream.function;


import IndexedLongUnaryOperator.Util;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link IndexedLongUnaryOperator}
 */
public class IndexedLongUnaryOperatorTest {
    @Test
    public void testWrap() {
        IndexedLongUnaryOperator identity = Util.wrap(LongUnaryOperator.Util.identity());
        Assert.assertThat(identity.applyAsLong(1, 3228), Matchers.is(3228L));
    }

    @Test
    public void testPrivateUtilConstructor() {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }
}

