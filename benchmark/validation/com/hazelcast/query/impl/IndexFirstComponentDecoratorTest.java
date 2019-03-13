/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.query.impl;


import Comparison.GREATER;
import Comparison.GREATER_OR_EQUAL;
import Comparison.LESS;
import Comparison.LESS_OR_EQUAL;
import Comparison.NOT_EQUAL;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IndexFirstComponentDecoratorTest {
    private InternalSerializationService serializationService;

    private InternalIndex expected;

    private InternalIndex actual;

    @Test
    public void testQuerying() {
        Assert.assertEquals(expected.getRecords((-1)), actual.getRecords((-1)));
        Assert.assertEquals(expected.getRecords(0), actual.getRecords(0));
        Assert.assertEquals(expected.getRecords(50), actual.getRecords(50));
        Assert.assertEquals(expected.getRecords(99), actual.getRecords(99));
        Assert.assertEquals(expected.getRecords(100), actual.getRecords(100));
        Assert.assertEquals(expected.getRecords(new Comparable[]{  }), actual.getRecords(new Comparable[]{  }));
        Assert.assertEquals(expected.getRecords(new Comparable[]{ 50 }), actual.getRecords(new Comparable[]{ 50 }));
        Assert.assertEquals(expected.getRecords(new Comparable[]{ -1 }), actual.getRecords(new Comparable[]{ -1 }));
        Assert.assertEquals(expected.getRecords(new Comparable[]{ 100 }), actual.getRecords(new Comparable[]{ 100 }));
        Assert.assertEquals(expected.getRecords(new Comparable[]{ 10, 10 }), actual.getRecords(new Comparable[]{ 10, 10 }));
        Assert.assertEquals(expected.getRecords(new Comparable[]{ 20, -1, 100 }), actual.getRecords(new Comparable[]{ 20, -1, 100 }));
        Assert.assertEquals(expected.getRecords(new Comparable[]{ -1, -2, -3 }), actual.getRecords(new Comparable[]{ -1, -2, -3 }));
        Assert.assertEquals(expected.getRecords(new Comparable[]{ 100, 101, 102 }), actual.getRecords(new Comparable[]{ 100, 101, 102 }));
        Assert.assertEquals(expected.getRecords(new Comparable[]{ 10, 20, 30, 30 }), actual.getRecords(new Comparable[]{ 10, 20, 30, 30 }));
        Assert.assertEquals(expected.getRecords(NOT_EQUAL, 50), actual.getRecords(NOT_EQUAL, 50));
        Assert.assertEquals(expected.getRecords(NOT_EQUAL, (-1)), actual.getRecords(NOT_EQUAL, (-1)));
        Assert.assertEquals(expected.getRecords(LESS, 50), actual.getRecords(LESS, 50));
        Assert.assertEquals(expected.getRecords(LESS, 99), actual.getRecords(LESS, 99));
        Assert.assertEquals(expected.getRecords(LESS, 100), actual.getRecords(LESS, 100));
        Assert.assertEquals(expected.getRecords(LESS, 0), actual.getRecords(LESS, 0));
        Assert.assertEquals(expected.getRecords(LESS, (-1)), actual.getRecords(LESS, (-1)));
        Assert.assertEquals(expected.getRecords(GREATER, 50), actual.getRecords(GREATER, 50));
        Assert.assertEquals(expected.getRecords(GREATER, 99), actual.getRecords(GREATER, 99));
        Assert.assertEquals(expected.getRecords(GREATER, 100), actual.getRecords(GREATER, 100));
        Assert.assertEquals(expected.getRecords(GREATER, 0), actual.getRecords(GREATER, 0));
        Assert.assertEquals(expected.getRecords(GREATER, (-1)), actual.getRecords(GREATER, (-1)));
        Assert.assertEquals(expected.getRecords(LESS_OR_EQUAL, 50), actual.getRecords(LESS_OR_EQUAL, 50));
        Assert.assertEquals(expected.getRecords(LESS_OR_EQUAL, 99), actual.getRecords(LESS_OR_EQUAL, 99));
        Assert.assertEquals(expected.getRecords(LESS_OR_EQUAL, 100), actual.getRecords(LESS_OR_EQUAL, 100));
        Assert.assertEquals(expected.getRecords(LESS_OR_EQUAL, 0), actual.getRecords(LESS_OR_EQUAL, 0));
        Assert.assertEquals(expected.getRecords(LESS_OR_EQUAL, (-1)), actual.getRecords(LESS_OR_EQUAL, (-1)));
        Assert.assertEquals(expected.getRecords(GREATER_OR_EQUAL, 50), actual.getRecords(GREATER_OR_EQUAL, 50));
        Assert.assertEquals(expected.getRecords(GREATER_OR_EQUAL, 99), actual.getRecords(GREATER_OR_EQUAL, 99));
        Assert.assertEquals(expected.getRecords(GREATER_OR_EQUAL, 100), actual.getRecords(GREATER_OR_EQUAL, 100));
        Assert.assertEquals(expected.getRecords(GREATER_OR_EQUAL, 0), actual.getRecords(GREATER_OR_EQUAL, 0));
        Assert.assertEquals(expected.getRecords(GREATER_OR_EQUAL, (-1)), actual.getRecords(GREATER_OR_EQUAL, (-1)));
        Assert.assertEquals(expected.getRecords(0, false, 99, false), actual.getRecords(0, false, 99, false));
        Assert.assertEquals(expected.getRecords(0, true, 99, false), actual.getRecords(0, true, 99, false));
        Assert.assertEquals(expected.getRecords(0, false, 99, true), actual.getRecords(0, false, 99, true));
        Assert.assertEquals(expected.getRecords(0, true, 99, true), actual.getRecords(0, true, 99, true));
        Assert.assertEquals(expected.getRecords((-10), false, 99, false), actual.getRecords((-10), false, 99, false));
        Assert.assertEquals(expected.getRecords((-10), true, 99, false), actual.getRecords((-10), true, 99, false));
        Assert.assertEquals(expected.getRecords((-10), false, 99, true), actual.getRecords((-10), false, 99, true));
        Assert.assertEquals(expected.getRecords((-10), true, 99, true), actual.getRecords((-10), true, 99, true));
        Assert.assertEquals(expected.getRecords(10, false, 50, false), actual.getRecords(10, false, 50, false));
        Assert.assertEquals(expected.getRecords(10, true, 50, false), actual.getRecords(10, true, 50, false));
        Assert.assertEquals(expected.getRecords(10, false, 50, true), actual.getRecords(10, false, 50, true));
        Assert.assertEquals(expected.getRecords(10, true, 50, true), actual.getRecords(10, true, 50, true));
        Assert.assertEquals(expected.getRecords(90, false, 150, false), actual.getRecords(90, false, 150, false));
        Assert.assertEquals(expected.getRecords(90, true, 150, false), actual.getRecords(90, true, 150, false));
        Assert.assertEquals(expected.getRecords(90, false, 150, true), actual.getRecords(90, false, 150, true));
        Assert.assertEquals(expected.getRecords(90, true, 150, true), actual.getRecords(90, true, 150, true));
        Assert.assertEquals(expected.getRecords((-100), false, (-10), false), actual.getRecords((-100), false, (-10), false));
        Assert.assertEquals(expected.getRecords((-100), true, (-10), false), actual.getRecords((-100), true, (-10), false));
        Assert.assertEquals(expected.getRecords((-100), false, (-10), true), actual.getRecords((-100), false, (-10), true));
        Assert.assertEquals(expected.getRecords((-100), true, (-10), true), actual.getRecords((-100), true, (-10), true));
        Assert.assertEquals(expected.getRecords(110, false, 150, false), actual.getRecords(110, false, 150, false));
        Assert.assertEquals(expected.getRecords(110, true, 150, false), actual.getRecords(110, true, 150, false));
        Assert.assertEquals(expected.getRecords(110, false, 150, true), actual.getRecords(110, false, 150, true));
        Assert.assertEquals(expected.getRecords(110, true, 150, true), actual.getRecords(110, true, 150, true));
        Assert.assertEquals(expected.getRecords((-100), false, 200, false), actual.getRecords((-100), false, 200, false));
        Assert.assertEquals(expected.getRecords((-100), true, 200, false), actual.getRecords((-100), true, 200, false));
        Assert.assertEquals(expected.getRecords((-100), false, 200, true), actual.getRecords((-100), false, 200, true));
        Assert.assertEquals(expected.getRecords((-100), true, 200, true), actual.getRecords((-100), true, 200, true));
    }

    private class Entry extends QueryableEntry<Integer, Long> {
        private final int key;

        private final long value;

        public Entry(int key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Long getValue() {
            return value;
        }

        @Override
        public Long setValue(Long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public Data getKeyData() {
            return IndexFirstComponentDecoratorTest.this.serializationService.toData(key);
        }

        @Override
        public Data getValueData() {
            return IndexFirstComponentDecoratorTest.this.serializationService.toData(value);
        }

        @Override
        protected Object getTargetObject(boolean key) {
            return key ? this.key : value;
        }
    }
}

