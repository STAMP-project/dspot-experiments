/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.contrib.streaming.state;


import IntSerializer.INSTANCE;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.junit.Test;


/**
 * Test for @{@link RocksDBSerializedCompositeKeyBuilder}.
 */
public class RocksDBSerializedCompositeKeyBuilderTest {
    private final DataOutputSerializer dataOutputSerializer = new DataOutputSerializer(128);

    private static final int[] TEST_PARALLELISMS = new int[]{ 64, 4096 };

    private static final Collection<Integer> TEST_INTS = Arrays.asList(42, 4711);

    private static final Collection<String> TEST_STRINGS = Arrays.asList("test123", "abc");

    @Test
    public void testSetKey() throws IOException {
        for (int parallelism : RocksDBSerializedCompositeKeyBuilderTest.TEST_PARALLELISMS) {
            testSetKeyInternal(INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, parallelism);
            testSetKeyInternal(StringSerializer.INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, parallelism);
        }
    }

    @Test
    public void testSetKeyNamespace() throws IOException {
        for (int parallelism : RocksDBSerializedCompositeKeyBuilderTest.TEST_PARALLELISMS) {
            testSetKeyNamespaceInternal(INSTANCE, INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, parallelism);
            testSetKeyNamespaceInternal(INSTANCE, StringSerializer.INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, parallelism);
            testSetKeyNamespaceInternal(StringSerializer.INSTANCE, INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, parallelism);
            testSetKeyNamespaceInternal(StringSerializer.INSTANCE, StringSerializer.INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, parallelism);
        }
    }

    @Test
    public void testSetKeyNamespaceUserKey() throws IOException {
        for (int parallelism : RocksDBSerializedCompositeKeyBuilderTest.TEST_PARALLELISMS) {
            testSetKeyNamespaceUserKeyInternal(INSTANCE, INSTANCE, INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, parallelism);
            testSetKeyNamespaceUserKeyInternal(INSTANCE, StringSerializer.INSTANCE, INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, parallelism);
            testSetKeyNamespaceUserKeyInternal(StringSerializer.INSTANCE, INSTANCE, INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, parallelism);
            testSetKeyNamespaceUserKeyInternal(StringSerializer.INSTANCE, StringSerializer.INSTANCE, INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, parallelism);
            testSetKeyNamespaceUserKeyInternal(INSTANCE, INSTANCE, StringSerializer.INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, parallelism);
            testSetKeyNamespaceUserKeyInternal(INSTANCE, StringSerializer.INSTANCE, StringSerializer.INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, parallelism);
            testSetKeyNamespaceUserKeyInternal(StringSerializer.INSTANCE, INSTANCE, StringSerializer.INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_INTS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, parallelism);
            testSetKeyNamespaceUserKeyInternal(StringSerializer.INSTANCE, StringSerializer.INSTANCE, StringSerializer.INSTANCE, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, RocksDBSerializedCompositeKeyBuilderTest.TEST_STRINGS, parallelism);
        }
    }
}

