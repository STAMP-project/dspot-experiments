/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona.collections;


import org.junit.Assert;
import org.junit.Test;


public class ArrayUtilTest {
    // Reference Equality
    private static final Integer ONE = Integer.valueOf(1);

    private static final Integer TWO = Integer.valueOf(2);

    private static final Integer THREE = Integer.valueOf(3);

    private final Integer[] values = new Integer[]{ ArrayUtilTest.ONE, ArrayUtilTest.TWO };

    @Test
    public void shouldNotRemoveMissingElement() {
        final Integer[] result = ArrayUtil.remove(values, ArrayUtilTest.THREE);
        Assert.assertArrayEquals(values, result);
    }

    @Test
    public void shouldRemovePresentElementAtEnd() {
        final Integer[] result = ArrayUtil.remove(values, ArrayUtilTest.TWO);
        Assert.assertArrayEquals(new Integer[]{ ArrayUtilTest.ONE }, result);
    }

    @Test
    public void shouldRemovePresentElementAtStart() {
        final Integer[] result = ArrayUtil.remove(values, ArrayUtilTest.ONE);
        Assert.assertArrayEquals(new Integer[]{ ArrayUtilTest.TWO }, result);
    }

    @Test
    public void shouldRemoveByIndex() {
        final Integer[] result = ArrayUtil.remove(values, 0);
        Assert.assertArrayEquals(new Integer[]{ ArrayUtilTest.TWO }, result);
    }
}

