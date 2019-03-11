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


import RecordSerializer.SerializationResult.FULL_RECORD;
import RecordSerializer.SerializationResult.PARTIAL_RECORD_MEMORY_SEGMENT_FULL;
import SerializationTestTypeFactory.INT;
import java.io.IOException;
import java.util.Random;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.runtime.io.network.buffer.BufferBuilder;
import org.apache.flink.runtime.io.network.buffer.BufferBuilderTestUtils;
import org.apache.flink.testutils.serialization.types.SerializationTestType;
import org.apache.flink.testutils.serialization.types.Util;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link SpanningRecordSerializer}.
 */
public class SpanningRecordSerializerTest {
    @Test
    public void testHasSerializedData() throws IOException {
        final SpanningRecordSerializer<SerializationTestType> serializer = new SpanningRecordSerializer();
        final SerializationTestType randomIntRecord = Util.randomRecord(INT);
        Assert.assertFalse(serializer.hasSerializedData());
        serializer.serializeRecord(randomIntRecord);
        Assert.assertTrue(serializer.hasSerializedData());
        final BufferBuilder bufferBuilder1 = BufferBuilderTestUtils.createBufferBuilder(16);
        serializer.copyToBufferBuilder(bufferBuilder1);
        Assert.assertFalse(serializer.hasSerializedData());
        final BufferBuilder bufferBuilder2 = BufferBuilderTestUtils.createBufferBuilder(8);
        serializer.reset();
        serializer.copyToBufferBuilder(bufferBuilder2);
        Assert.assertFalse(serializer.hasSerializedData());
        serializer.reset();
        serializer.copyToBufferBuilder(bufferBuilder2);
        // Buffer builder full!
        Assert.assertTrue(serializer.hasSerializedData());
    }

    @Test
    public void testEmptyRecords() throws IOException {
        final int segmentSize = 11;
        final SpanningRecordSerializer<SerializationTestType> serializer = new SpanningRecordSerializer();
        final BufferBuilder bufferBuilder1 = BufferBuilderTestUtils.createBufferBuilder(segmentSize);
        Assert.assertEquals(FULL_RECORD, serializer.copyToBufferBuilder(bufferBuilder1));
        SerializationTestType emptyRecord = new SerializationTestType() {
            @Override
            public SerializationTestType getRandom(Random rnd) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int length() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void write(DataOutputView out) {
            }

            @Override
            public void read(DataInputView in) {
            }

            @Override
            public int hashCode() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equals(Object obj) {
                throw new UnsupportedOperationException();
            }
        };
        serializer.serializeRecord(emptyRecord);
        Assert.assertEquals(FULL_RECORD, serializer.copyToBufferBuilder(bufferBuilder1));
        serializer.reset();
        Assert.assertEquals(FULL_RECORD, serializer.copyToBufferBuilder(bufferBuilder1));
        serializer.reset();
        Assert.assertEquals(PARTIAL_RECORD_MEMORY_SEGMENT_FULL, serializer.copyToBufferBuilder(bufferBuilder1));
        final BufferBuilder bufferBuilder2 = BufferBuilderTestUtils.createBufferBuilder(segmentSize);
        Assert.assertEquals(FULL_RECORD, serializer.copyToBufferBuilder(bufferBuilder2));
    }

    @Test
    public void testIntRecordsSpanningMultipleSegments() throws Exception {
        final int segmentSize = 1;
        final int numValues = 10;
        test(Util.randomRecords(numValues, INT), segmentSize);
    }

    @Test
    public void testIntRecordsWithAlignedSegments() throws Exception {
        final int segmentSize = 64;
        final int numValues = 64;
        test(Util.randomRecords(numValues, INT), segmentSize);
    }

    @Test
    public void testIntRecordsWithUnalignedSegments() throws Exception {
        final int segmentSize = 31;
        final int numValues = 248;// least common multiple => last record should align

        test(Util.randomRecords(numValues, INT), segmentSize);
    }

    @Test
    public void testRandomRecords() throws Exception {
        final int segmentSize = 127;
        final int numValues = 100000;
        test(Util.randomRecords(numValues), segmentSize);
    }
}

