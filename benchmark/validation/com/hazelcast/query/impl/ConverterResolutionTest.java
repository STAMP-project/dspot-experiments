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


import Index.OperationSource.USER;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConverterResolutionTest {
    private InternalSerializationService serializationService;

    private Extractors extractors;

    private Indexes indexes;

    @Test
    public void testPopulatedNonCompositeIndex() {
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.addOrGetIndex("value", false);
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.putEntry(new ConverterResolutionTest.Entry(0, null), null, USER);
        Assert.assertNull(indexes.getConverter("value"));
        // just to make sure double-invocation doesn't change anything
        Assert.assertNull(indexes.getConverter("value"));
        indexes.putEntry(new ConverterResolutionTest.Entry(0, 1L), null, USER);
        Assert.assertSame(TypeConverters.LONG_CONVERTER, indexes.getConverter("value"));
        indexes.destroyIndexes();
        Assert.assertNull(indexes.getConverter("value"));
    }

    @Test
    public void testUnpopulatedNonCompositeIndex() {
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.addOrGetIndex("value", false);
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.putEntry(new ConverterResolutionTest.Entry(0, 1L), null, USER);
        Assert.assertSame(TypeConverters.LONG_CONVERTER, indexes.getConverter("value"));
        indexes.destroyIndexes();
        Assert.assertNull(indexes.getConverter("value"));
    }

    @Test
    public void testUnpopulatedCompositeIndex() {
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.addOrGetIndex("__key, value", false);
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.addOrGetIndex("value, __key", true);
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        // just to make sure double-invocation doesn't change anything
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.putEntry(new ConverterResolutionTest.Entry(0, null), null, USER);
        Assert.assertSame(TypeConverters.INTEGER_CONVERTER, indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        indexes.putEntry(new ConverterResolutionTest.Entry(0, 1L), null, USER);
        Assert.assertSame(TypeConverters.INTEGER_CONVERTER, indexes.getConverter("__key"));
        Assert.assertSame(TypeConverters.LONG_CONVERTER, indexes.getConverter("value"));
        indexes.destroyIndexes();
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
    }

    @Test
    public void testPopulatedCompositeIndex() {
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.addOrGetIndex("__key, value", false);
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.putEntry(new ConverterResolutionTest.Entry(0, null), null, USER);
        Assert.assertSame(TypeConverters.INTEGER_CONVERTER, indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        indexes.putEntry(new ConverterResolutionTest.Entry(0, 1L), null, USER);
        Assert.assertSame(TypeConverters.INTEGER_CONVERTER, indexes.getConverter("__key"));
        Assert.assertSame(TypeConverters.LONG_CONVERTER, indexes.getConverter("value"));
        indexes.destroyIndexes();
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
    }

    @Test
    public void testCompositeAndNonCompositeIndexes() {
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.addOrGetIndex("value", true);
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.addOrGetIndex("__key, value", false);
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.putEntry(new ConverterResolutionTest.Entry(0, null), null, USER);
        Assert.assertSame(TypeConverters.INTEGER_CONVERTER, indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.putEntry(new ConverterResolutionTest.Entry(0, 1L), null, USER);
        Assert.assertSame(TypeConverters.INTEGER_CONVERTER, indexes.getConverter("__key"));
        Assert.assertSame(TypeConverters.LONG_CONVERTER, indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
        indexes.destroyIndexes();
        Assert.assertNull(indexes.getConverter("__key"));
        Assert.assertNull(indexes.getConverter("value"));
        Assert.assertNull(indexes.getConverter("unknown"));
    }

    public static class Value implements Serializable {
        public Long value;

        public Value(Long value) {
            this.value = value;
        }
    }

    private class Entry extends QueryableEntry<Integer, ConverterResolutionTest.Value> {
        private final int key;

        private final ConverterResolutionTest.Value value;

        public Entry(int key, Long value) {
            this.serializationService = ConverterResolutionTest.this.serializationService;
            this.extractors = ConverterResolutionTest.this.extractors;
            this.key = key;
            this.value = new ConverterResolutionTest.Value(value);
        }

        @Override
        public ConverterResolutionTest.Value getValue() {
            return value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public Data getKeyData() {
            return serializationService.toData(key);
        }

        @Override
        public Data getValueData() {
            return serializationService.toData(value);
        }

        @Override
        protected Object getTargetObject(boolean key) {
            return key ? this.key : value;
        }

        @Override
        public ConverterResolutionTest.Value setValue(ConverterResolutionTest.Value value) {
            throw new UnsupportedOperationException();
        }
    }
}

