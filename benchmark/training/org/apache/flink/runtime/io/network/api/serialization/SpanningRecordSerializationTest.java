/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.io.network.api.serialization;


import RecordSerializer.SerializationResult;
import SerializationTestTypeFactory.INT;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.buffer.BufferBuilder;
import org.apache.flink.runtime.io.network.buffer.BufferBuilderTestUtils;
import org.apache.flink.runtime.io.network.buffer.BufferConsumer;
import org.apache.flink.runtime.io.network.serialization.types.LargeObjectType;
import org.apache.flink.testutils.serialization.types.IntType;
import org.apache.flink.testutils.serialization.types.SerializationTestType;
import org.apache.flink.testutils.serialization.types.Util;
import org.apache.flink.util.TestLogger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link SpillingAdaptiveSpanningRecordDeserializer}.
 */
public class SpanningRecordSerializationTest extends TestLogger {
    private static final Random RANDOM = new Random(42);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testIntRecordsSpanningMultipleSegments() throws Exception {
        final int segmentSize = 1;
        final int numValues = 10;
        testSerializationRoundTrip(Util.randomRecords(numValues, INT), segmentSize);
    }

    @Test
    public void testIntRecordsWithAlignedBuffers() throws Exception {
        final int segmentSize = 64;
        final int numValues = 64;
        testSerializationRoundTrip(Util.randomRecords(numValues, INT), segmentSize);
    }

    @Test
    public void testIntRecordsWithUnalignedBuffers() throws Exception {
        final int segmentSize = 31;
        final int numValues = 248;
        testSerializationRoundTrip(Util.randomRecords(numValues, INT), segmentSize);
    }

    @Test
    public void testRandomRecords() throws Exception {
        final int segmentSize = 127;
        final int numValues = 10000;
        testSerializationRoundTrip(Util.randomRecords(numValues), segmentSize);
    }

    @Test
    public void testHandleMixedLargeRecords() throws Exception {
        final int numValues = 99;
        final int segmentSize = 32 * 1024;
        List<SerializationTestType> originalRecords = new ArrayList<>(((numValues + 1) / 2));
        LargeObjectType genLarge = new LargeObjectType();
        Random rnd = new Random();
        for (int i = 0; i < numValues; i++) {
            if ((i % 2) == 0) {
                originalRecords.add(new IntType(42));
            } else {
                originalRecords.add(genLarge.getRandom(rnd));
            }
        }
        testSerializationRoundTrip(originalRecords, segmentSize);
    }

    private static class BufferAndSerializerResult {
        private final BufferBuilder bufferBuilder;

        private final BufferConsumer bufferConsumer;

        private final SerializationResult serializationResult;

        public BufferAndSerializerResult(BufferBuilder bufferBuilder, BufferConsumer bufferConsumer, RecordSerializer.SerializationResult serializationResult) {
            this.bufferBuilder = bufferBuilder;
            this.bufferConsumer = bufferConsumer;
            this.serializationResult = serializationResult;
        }

        public BufferBuilder getBufferBuilder() {
            return bufferBuilder;
        }

        public Buffer buildBuffer() {
            return BufferBuilderTestUtils.buildSingleBuffer(bufferConsumer);
        }

        public boolean isFullBuffer() {
            return serializationResult.isFullBuffer();
        }
    }
}

