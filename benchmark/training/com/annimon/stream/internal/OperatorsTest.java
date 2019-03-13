package com.annimon.stream.internal;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Operators}
 */
public class OperatorsTest {
    @Test
    public void testPrivateConstructor() {
        Assert.assertThat(Operators.class, hasOnlyPrivateConstructors());
    }
}

