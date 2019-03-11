/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util;


import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class Arrays_array_Test {
    @Test
    public void should_return_parameter() {
        Object[] array = new Object[]{ "one", "two" };
        Assertions.assertThat(array).isSameAs(Arrays.array(array));
    }

    @Test
    public void should_return_an_int_array_from_AtomicIntegerArray() {
        // GIVEN
        int[] expected = new int[]{ 1, 2, 3, 4 };
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(expected);
        // WHEN
        int[] actual = Arrays.array(atomicIntegerArray);
        // THEN
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void should_return_null_if_given_a_null_AtomicIntegerArray() {
        // GIVEN
        AtomicIntegerArray atomicIntegerArray = null;
        // WHEN
        int[] actual = Arrays.array(atomicIntegerArray);
        // THEN
        Assertions.assertThat(actual).isNull();
    }

    @Test
    public void should_return_an_long_array_from_AtomicLongArray() {
        // GIVEN
        long[] expected = new long[]{ 1, 2, 3, 4 };
        AtomicLongArray atomicLongArray = new AtomicLongArray(expected);
        // WHEN
        long[] actual = Arrays.array(atomicLongArray);
        // THEN
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void should_return_null_if_given_a_null_AtomicLongArray() {
        // GIVEN
        AtomicLongArray atomicLongArray = null;
        // WHEN
        long[] actual = Arrays.array(atomicLongArray);
        // THEN
        Assertions.assertThat(actual).isNull();
    }
}

