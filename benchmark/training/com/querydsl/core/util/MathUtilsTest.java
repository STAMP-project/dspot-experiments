/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.util;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MathUtilsTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void sum() {
        Assert.assertEquals(Integer.valueOf(5), MathUtils.sum(2, 3.0));
    }

    @Test
    public void difference() {
        Assert.assertEquals(Integer.valueOf(2), MathUtils.difference(5, 3.0));
    }

    @Test
    public void cast_returns_correct_type() {
        MathUtilsTest.checkCast(1, BigDecimal.class);
        MathUtilsTest.checkCast(1, BigInteger.class);
        MathUtilsTest.checkCast(1, Double.class);
        MathUtilsTest.checkCast(1, Float.class);
        MathUtilsTest.checkCast(1, Integer.class);
        MathUtilsTest.checkCast(1, Long.class);
        MathUtilsTest.checkCast(1, Short.class);
        MathUtilsTest.checkCast(1, Byte.class);
    }

    @Test
    public void cast_returns_argument_as_is_when_compatible() {
        MathUtilsTest.checkSame(BigDecimal.ONE, BigDecimal.class);
        MathUtilsTest.checkSame(BigInteger.ONE, BigInteger.class);
        MathUtilsTest.checkSame(((double) (1)), Double.class);
        MathUtilsTest.checkSame(((float) (1)), Float.class);
        MathUtilsTest.checkSame(1, Integer.class);
        MathUtilsTest.checkSame(((long) (1)), Long.class);
        MathUtilsTest.checkSame(((short) (1)), Short.class);
        MathUtilsTest.checkSame(((byte) (1)), Byte.class);
    }

    @Test
    public void cast_returns_null_when_input_is_null() {
        Integer result = MathUtils.cast(null, Integer.class);
        Assert.assertNull(result);
    }

    @Test
    public void cast_throws_on_unsupported_numbers() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Unsupported target type");
        MathUtilsTest.checkCast(1, AtomicInteger.class);
    }
}

