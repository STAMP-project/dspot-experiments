/**
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.block.function;


import MinFunction.DOUBLE;
import MinFunction.INTEGER;
import MinFunction.LONG;
import org.junit.Assert;
import org.junit.Test;


public class MinAndMaxBlocksTest {
    private static final Double FORTY_TWO_DOUBLE = 42.0;

    private static final Integer FORTY_TWO_INTEGER = 42;

    private static final Long FORTY_TWO_LONG = 42L;

    @Test
    public void minBlocks() {
        Assert.assertEquals(new Double(1.0), DOUBLE.value(1.0, 2.0));
        Assert.assertEquals(new Double(0.0), DOUBLE.value(0.0, 1.0));
        Assert.assertEquals(new Double((-1.0)), DOUBLE.value(1.0, (-1.0)));
        Assert.assertEquals(Integer.valueOf(1), INTEGER.value(1, 2));
        Assert.assertEquals(Integer.valueOf(0), INTEGER.value(0, 1));
        Assert.assertEquals(Integer.valueOf((-1)), INTEGER.value(1, (-1)));
        Assert.assertEquals(Long.valueOf(1L), LONG.value(1L, 2L));
        Assert.assertEquals(Long.valueOf(0L), LONG.value(0L, 1L));
        Assert.assertEquals(Long.valueOf((-1L)), LONG.value(1L, (-1L)));
    }

    @Test
    public void minBlocksNull() {
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_DOUBLE, DOUBLE.value(null, MinAndMaxBlocksTest.FORTY_TWO_DOUBLE));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_DOUBLE, DOUBLE.value(MinAndMaxBlocksTest.FORTY_TWO_DOUBLE, null));
        Assert.assertSame(null, DOUBLE.value(null, null));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_INTEGER, INTEGER.value(null, MinAndMaxBlocksTest.FORTY_TWO_INTEGER));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_INTEGER, INTEGER.value(MinAndMaxBlocksTest.FORTY_TWO_INTEGER, null));
        Assert.assertSame(null, INTEGER.value(null, null));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_LONG, LONG.value(null, MinAndMaxBlocksTest.FORTY_TWO_LONG));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_LONG, LONG.value(MinAndMaxBlocksTest.FORTY_TWO_LONG, null));
        Assert.assertSame(null, LONG.value(null, null));
    }

    @Test
    public void maxBlocks() {
        Assert.assertEquals(new Double(2.0), MaxFunction.DOUBLE.value(1.0, 2.0));
        Assert.assertEquals(new Double(1.0), MaxFunction.DOUBLE.value(0.0, 1.0));
        Assert.assertEquals(new Double(1.0), MaxFunction.DOUBLE.value(1.0, (-1.0)));
        Assert.assertEquals(Integer.valueOf(2), MaxFunction.INTEGER.value(1, 2));
        Assert.assertEquals(Integer.valueOf(1), MaxFunction.INTEGER.value(0, 1));
        Assert.assertEquals(Integer.valueOf(1), MaxFunction.INTEGER.value(1, (-1)));
        Assert.assertEquals(Long.valueOf(2L), MaxFunction.LONG.value(1L, 2L));
        Assert.assertEquals(Long.valueOf(1L), MaxFunction.LONG.value(0L, 1L));
        Assert.assertEquals(Long.valueOf(1L), MaxFunction.LONG.value(1L, (-1L)));
    }

    @Test
    public void maxBlocksNull() {
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_DOUBLE, MaxFunction.DOUBLE.value(null, MinAndMaxBlocksTest.FORTY_TWO_DOUBLE));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_DOUBLE, MaxFunction.DOUBLE.value(MinAndMaxBlocksTest.FORTY_TWO_DOUBLE, null));
        Assert.assertSame(null, MaxFunction.DOUBLE.value(null, null));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_INTEGER, MaxFunction.INTEGER.value(null, MinAndMaxBlocksTest.FORTY_TWO_INTEGER));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_INTEGER, MaxFunction.INTEGER.value(MinAndMaxBlocksTest.FORTY_TWO_INTEGER, null));
        Assert.assertSame(null, MaxFunction.INTEGER.value(null, null));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_LONG, MaxFunction.LONG.value(null, MinAndMaxBlocksTest.FORTY_TWO_LONG));
        Assert.assertSame(MinAndMaxBlocksTest.FORTY_TWO_LONG, MaxFunction.LONG.value(MinAndMaxBlocksTest.FORTY_TWO_LONG, null));
        Assert.assertSame(null, MaxFunction.LONG.value(null, null));
    }
}

