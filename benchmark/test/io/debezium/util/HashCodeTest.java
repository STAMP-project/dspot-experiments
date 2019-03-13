/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.util;


import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class HashCodeTest {
    @Test
    public void shouldComputeHashCodeForOnePrimitive() {
        Assert.assertThat(HashCode.compute(1), Is.is(IsNot.not(0)));
        Assert.assertThat(HashCode.compute(((long) (8))), Is.is(IsNot.not(0)));
        Assert.assertThat(HashCode.compute(((short) (3))), Is.is(IsNot.not(0)));
        Assert.assertThat(HashCode.compute(1.0F), Is.is(IsNot.not(0)));
        Assert.assertThat(HashCode.compute(1.0), Is.is(IsNot.not(0)));
        Assert.assertThat(HashCode.compute(true), Is.is(IsNot.not(0)));
    }

    @Test
    public void shouldComputeHashCodeForMultiplePrimitives() {
        Assert.assertThat(HashCode.compute(1, 2, 3), Is.is(IsNot.not(0)));
        Assert.assertThat(HashCode.compute(((long) (8)), ((long) (22)), 33), Is.is(IsNot.not(0)));
        Assert.assertThat(HashCode.compute(((short) (3)), ((long) (22)), true), Is.is(IsNot.not(0)));
    }

    @Test
    public void shouldAcceptNoArguments() {
        Assert.assertThat(HashCode.compute(), Is.is(0));
    }

    @Test
    public void shouldAcceptNullArguments() {
        Assert.assertThat(HashCode.compute(((Object) (null))), Is.is(0));
        Assert.assertThat(HashCode.compute("abc", ((Object) (null))), Is.is(IsNot.not(0)));
    }
}

