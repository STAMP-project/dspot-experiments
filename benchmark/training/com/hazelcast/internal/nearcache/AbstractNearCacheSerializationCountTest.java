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
package com.hazelcast.internal.nearcache;


import DataStructureMethods.PUT;
import DataStructureMethods.PUT_ALL;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.internal.adapter.DataStructureAdapter.DataStructureMethods;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.test.HazelcastTestSupport;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


/**
 * Contains the logic code for unified Near Cache serialization count tests.
 * <p>
 * This test has some requirements to deliver stable results:
 * <ul>
 * <li>no backups should be created, since backup operations cause additional serializations</li>
 * <li>no migrations should happen, since migration operations cause additional serializations</li>
 * <li>no eviction or expiration should happen, since that causes additional serializations</li>
 * <li>no custom invalidation listeners should be registered, since they cause additional serializations</li>
 * </ul>
 *
 * @param <NK>
 * 		key type of the tested Near Cache
 * @param <NV>
 * 		value type of the tested Near Cache
 */
public abstract class AbstractNearCacheSerializationCountTest<NK, NV> extends HazelcastTestSupport {
    /**
     * The default name used for the data structures which have a Near Cache.
     */
    protected static final String DEFAULT_NEAR_CACHE_NAME = "defaultNearCache";

    private static final AtomicInteger KEY_SERIALIZE_COUNT = new AtomicInteger();

    private static final AtomicInteger KEY_DESERIALIZE_COUNT = new AtomicInteger();

    private static final AtomicInteger VALUE_SERIALIZE_COUNT = new AtomicInteger();

    private static final AtomicInteger VALUE_DESERIALIZE_COUNT = new AtomicInteger();

    private static final AtomicReference<List<String>> KEY_SERIALIZE_STACKTRACE = new AtomicReference<List<String>>();

    private static final AtomicReference<List<String>> KEY_DESERIALIZE_STACKTRACE = new AtomicReference<List<String>>();

    private static final AtomicReference<List<String>> VALUE_SERIALIZE_STACKTRACE = new AtomicReference<List<String>>();

    private static final AtomicReference<List<String>> VALUE_DESERIALIZE_STACKTRACE = new AtomicReference<List<String>>();

    /**
     * The {@link DataStructureMethods} which should be used in the test.
     */
    protected DataStructureMethods testMethod;

    /**
     * An array with the expected number of key serializations for a {@link DataStructureAdapter#put}
     * and two {@link DataStructureAdapter#get(Object)} calls for the given {@link NearCacheConfig}.
     */
    protected int[] expectedKeySerializationCounts;

    /**
     * An array with the expected number of key deserializations for a {@link DataStructureAdapter#put}
     * and two {@link DataStructureAdapter#get(Object)} calls for the given {@link NearCacheConfig}.
     */
    protected int[] expectedKeyDeserializationCounts;

    /**
     * An array with the expected number of value serializations for a {@link DataStructureAdapter#put}
     * and two {@link DataStructureAdapter#get(Object)} calls for the given {@link NearCacheConfig}.
     */
    protected int[] expectedValueSerializationCounts;

    /**
     * An array with the expected number of value deserializations for a {@link DataStructureAdapter#put}
     * and two {@link DataStructureAdapter#get(Object)} calls for the given {@link NearCacheConfig}.
     */
    protected int[] expectedValueDeserializationCounts;

    /**
     * The {@link NearCacheConfig} used by the Near Cache tests.
     * <p>
     * Needs to be set by the implementations of this class in their {@link org.junit.Before} methods.
     */
    protected NearCacheConfig nearCacheConfig;

    /**
     * Tests the serialization and deserialization counts on a {@link DataStructureAdapter} with a {@link NearCache}.
     */
    @Test
    public void testSerializationCounts() {
        assumeThatMethodIsAvailable(testMethod);
        switch (testMethod) {
            case GET :
                assumeThatMethodIsAvailable(PUT);
                break;
            case GET_ALL :
                assumeThatMethodIsAvailable(PUT_ALL);
                break;
            default :
                Assert.fail(("Unsupported method: " + (testMethod)));
        }
        NearCacheTestContext<AbstractNearCacheSerializationCountTest.KeySerializationCountingData, AbstractNearCacheSerializationCountTest.ValueSerializationCountingData, NK, NV> context = createContext();
        // execute the test with key/value classes which provide no custom equals()/hashCode() methods
        runTest(context, false);
        // reset the Near Cache for the next round
        if ((context.nearCache) != null) {
            context.nearCache.clear();
            NearCacheTestUtils.assertNearCacheSizeEventually(context, 0);
        }
        // execute the test with key/value classes which provide custom equals()/hashCode() methods
        runTest(context, true);
    }

    private static class KeySerializationCountingData implements Portable {
        private static final int FACTORY_ID = 1;

        private static final int CLASS_ID = 1;

        private boolean executeEqualsAndHashCode;

        KeySerializationCountingData() {
        }

        KeySerializationCountingData(boolean executeEqualsAndHashCode) {
            this.executeEqualsAndHashCode = executeEqualsAndHashCode;
        }

        @Override
        public int getFactoryId() {
            return AbstractNearCacheSerializationCountTest.KeySerializationCountingData.FACTORY_ID;
        }

        @Override
        public int getClassId() {
            return AbstractNearCacheSerializationCountTest.KeySerializationCountingData.CLASS_ID;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeBoolean("b", executeEqualsAndHashCode);
            AbstractNearCacheSerializationCountTest.KEY_SERIALIZE_COUNT.incrementAndGet();
            AbstractNearCacheSerializationCountTest.KEY_SERIALIZE_STACKTRACE.get().add(AbstractNearCacheSerializationCountTest.getStackTrace((("invoked key serialization (executeEqualsAndHashCode: " + (executeEqualsAndHashCode)) + ")")));
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            executeEqualsAndHashCode = reader.readBoolean("b");
            AbstractNearCacheSerializationCountTest.KEY_DESERIALIZE_COUNT.incrementAndGet();
            AbstractNearCacheSerializationCountTest.KEY_DESERIALIZE_STACKTRACE.get().add(AbstractNearCacheSerializationCountTest.getStackTrace((("invoked key deserialization (executeEqualsAndHashCode: " + (executeEqualsAndHashCode)) + ")")));
        }

        @Override
        public boolean equals(Object o) {
            if (!(executeEqualsAndHashCode)) {
                return super.equals(o);
            }
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof AbstractNearCacheSerializationCountTest.KeySerializationCountingData)) {
                return false;
            }
            AbstractNearCacheSerializationCountTest.KeySerializationCountingData that = ((AbstractNearCacheSerializationCountTest.KeySerializationCountingData) (o));
            return that.executeEqualsAndHashCode;
        }

        @Override
        public int hashCode() {
            if (!(executeEqualsAndHashCode)) {
                return super.hashCode();
            }
            return 1;
        }
    }

    private static class ValueSerializationCountingData implements Portable {
        private static final int FACTORY_ID = 2;

        private static final int CLASS_ID = 2;

        private boolean executeEqualsAndHashCode;

        ValueSerializationCountingData() {
        }

        ValueSerializationCountingData(boolean executeEqualsAndHashCode) {
            this.executeEqualsAndHashCode = executeEqualsAndHashCode;
        }

        @Override
        public int getFactoryId() {
            return AbstractNearCacheSerializationCountTest.ValueSerializationCountingData.FACTORY_ID;
        }

        @Override
        public int getClassId() {
            return AbstractNearCacheSerializationCountTest.ValueSerializationCountingData.CLASS_ID;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeBoolean("b", executeEqualsAndHashCode);
            AbstractNearCacheSerializationCountTest.VALUE_SERIALIZE_COUNT.incrementAndGet();
            AbstractNearCacheSerializationCountTest.VALUE_SERIALIZE_STACKTRACE.get().add(AbstractNearCacheSerializationCountTest.getStackTrace((("invoked value serialization for (executeEqualsAndHashCode: " + (executeEqualsAndHashCode)) + ")")));
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            executeEqualsAndHashCode = reader.readBoolean("b");
            AbstractNearCacheSerializationCountTest.VALUE_DESERIALIZE_COUNT.incrementAndGet();
            AbstractNearCacheSerializationCountTest.VALUE_DESERIALIZE_STACKTRACE.get().add(AbstractNearCacheSerializationCountTest.getStackTrace((("invoked value deserialization for (executeEqualsAndHashCode: " + (executeEqualsAndHashCode)) + ")")));
        }

        @Override
        public boolean equals(Object o) {
            if (!(executeEqualsAndHashCode)) {
                return super.equals(o);
            }
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof AbstractNearCacheSerializationCountTest.ValueSerializationCountingData)) {
                return false;
            }
            AbstractNearCacheSerializationCountTest.ValueSerializationCountingData that = ((AbstractNearCacheSerializationCountTest.ValueSerializationCountingData) (o));
            return that.executeEqualsAndHashCode;
        }

        @Override
        public int hashCode() {
            if (!(executeEqualsAndHashCode)) {
                return super.hashCode();
            }
            return 1;
        }
    }
}

