/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.utils;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class AtomicPositiveIntegerTest {
    private AtomicPositiveInteger i1 = new AtomicPositiveInteger();

    private AtomicPositiveInteger i2 = new AtomicPositiveInteger(127);

    private AtomicPositiveInteger i3 = new AtomicPositiveInteger(Integer.MAX_VALUE);

    @Test
    public void testGet() throws Exception {
        Assertions.assertEquals(0, i1.get());
        Assertions.assertEquals(127, i2.get());
        Assertions.assertEquals(Integer.MAX_VALUE, i3.get());
    }

    @Test
    public void testSet() throws Exception {
        i1.set(100);
        Assertions.assertEquals(100, i1.get());
        try {
            i1.set((-1));
            Assertions.fail();
        } catch (IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("new value"), CoreMatchers.containsString("< 0")));
        }
    }

    @Test
    public void testGetAndIncrement() throws Exception {
        int get = i1.getAndIncrement();
        Assertions.assertEquals(0, get);
        Assertions.assertEquals(1, i1.get());
        get = i2.getAndIncrement();
        Assertions.assertEquals(127, get);
        Assertions.assertEquals(128, i2.get());
        get = i3.getAndIncrement();
        Assertions.assertEquals(Integer.MAX_VALUE, get);
        Assertions.assertEquals(0, i3.get());
    }

    @Test
    public void testGetAndDecrement() throws Exception {
        int get = i1.getAndDecrement();
        Assertions.assertEquals(0, get);
        Assertions.assertEquals(Integer.MAX_VALUE, i1.get());
        get = i2.getAndDecrement();
        Assertions.assertEquals(127, get);
        Assertions.assertEquals(126, i2.get());
        get = i3.getAndDecrement();
        Assertions.assertEquals(Integer.MAX_VALUE, get);
        Assertions.assertEquals(((Integer.MAX_VALUE) - 1), i3.get());
    }

    @Test
    public void testIncrementAndGet() throws Exception {
        int get = i1.incrementAndGet();
        Assertions.assertEquals(1, get);
        Assertions.assertEquals(1, i1.get());
        get = i2.incrementAndGet();
        Assertions.assertEquals(128, get);
        Assertions.assertEquals(128, i2.get());
        get = i3.incrementAndGet();
        Assertions.assertEquals(0, get);
        Assertions.assertEquals(0, i3.get());
    }

    @Test
    public void testDecrementAndGet() throws Exception {
        int get = i1.decrementAndGet();
        Assertions.assertEquals(Integer.MAX_VALUE, get);
        Assertions.assertEquals(Integer.MAX_VALUE, i1.get());
        get = i2.decrementAndGet();
        Assertions.assertEquals(126, get);
        Assertions.assertEquals(126, i2.get());
        get = i3.decrementAndGet();
        Assertions.assertEquals(((Integer.MAX_VALUE) - 1), get);
        Assertions.assertEquals(((Integer.MAX_VALUE) - 1), i3.get());
    }

    @Test
    public void testGetAndSet() throws Exception {
        int get = i1.getAndSet(100);
        Assertions.assertEquals(0, get);
        Assertions.assertEquals(100, i1.get());
        try {
            i1.getAndSet((-1));
        } catch (IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("new value"), CoreMatchers.containsString("< 0")));
        }
    }

    @Test
    public void testGetAndAnd() throws Exception {
        int get = i1.getAndAdd(3);
        Assertions.assertEquals(0, get);
        Assertions.assertEquals(3, i1.get());
        get = i2.getAndAdd(3);
        Assertions.assertEquals(127, get);
        Assertions.assertEquals((127 + 3), i2.get());
        get = i3.getAndAdd(3);
        Assertions.assertEquals(Integer.MAX_VALUE, get);
        Assertions.assertEquals(2, i3.get());
    }

    @Test
    public void testAddAndGet() throws Exception {
        int get = i1.addAndGet(3);
        Assertions.assertEquals(3, get);
        Assertions.assertEquals(3, i1.get());
        get = i2.addAndGet(3);
        Assertions.assertEquals((127 + 3), get);
        Assertions.assertEquals((127 + 3), i2.get());
        get = i3.addAndGet(3);
        Assertions.assertEquals(2, get);
        Assertions.assertEquals(2, i3.get());
    }

    @Test
    public void testCompareAndSet1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            i1.compareAndSet(i1.get(), (-1));
        });
    }

    @Test
    public void testCompareAndSet2() {
        MatcherAssert.assertThat(i1.compareAndSet(i1.get(), 2), CoreMatchers.is(true));
        MatcherAssert.assertThat(i1.get(), CoreMatchers.is(2));
    }

    @Test
    public void testWeakCompareAndSet1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            i1.weakCompareAndSet(i1.get(), (-1));
        });
    }

    @Test
    public void testWeakCompareAndSet2() {
        MatcherAssert.assertThat(i1.weakCompareAndSet(i1.get(), 2), CoreMatchers.is(true));
        MatcherAssert.assertThat(i1.get(), CoreMatchers.is(2));
    }

    @Test
    public void testValues() throws Exception {
        Integer i = i1.get();
        MatcherAssert.assertThat(i1.byteValue(), CoreMatchers.equalTo(i.byteValue()));
        MatcherAssert.assertThat(i1.shortValue(), CoreMatchers.equalTo(i.shortValue()));
        MatcherAssert.assertThat(i1.intValue(), CoreMatchers.equalTo(i.intValue()));
        MatcherAssert.assertThat(i1.longValue(), CoreMatchers.equalTo(i.longValue()));
        MatcherAssert.assertThat(i1.floatValue(), CoreMatchers.equalTo(i.floatValue()));
        MatcherAssert.assertThat(i1.doubleValue(), CoreMatchers.equalTo(i.doubleValue()));
        MatcherAssert.assertThat(i1.toString(), CoreMatchers.equalTo(i.toString()));
    }

    @Test
    public void testEquals() {
        Assertions.assertEquals(new AtomicPositiveInteger(), new AtomicPositiveInteger());
        Assertions.assertEquals(new AtomicPositiveInteger(1), new AtomicPositiveInteger(1));
    }
}

