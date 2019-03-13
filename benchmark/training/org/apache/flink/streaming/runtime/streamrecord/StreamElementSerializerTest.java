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
package org.apache.flink.streaming.runtime.streamrecord;


import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link StreamElementSerializer}.
 */
public class StreamElementSerializerTest {
    @Test
    public void testDeepDuplication() {
        @SuppressWarnings("unchecked")
        TypeSerializer<Long> serializer1 = ((TypeSerializer<Long>) (Mockito.mock(TypeSerializer.class)));
        @SuppressWarnings("unchecked")
        TypeSerializer<Long> serializer2 = ((TypeSerializer<Long>) (Mockito.mock(TypeSerializer.class)));
        Mockito.when(serializer1.duplicate()).thenReturn(serializer2);
        StreamElementSerializer<Long> streamRecSer = new StreamElementSerializer<Long>(serializer1);
        Assert.assertEquals(serializer1, streamRecSer.getContainedTypeSerializer());
        StreamElementSerializer<Long> copy = streamRecSer.duplicate();
        Assert.assertNotEquals(copy, streamRecSer);
        Assert.assertNotEquals(copy.getContainedTypeSerializer(), streamRecSer.getContainedTypeSerializer());
    }

    @Test
    public void testBasicProperties() {
        StreamElementSerializer<Long> streamRecSer = new StreamElementSerializer<Long>(LongSerializer.INSTANCE);
        Assert.assertFalse(streamRecSer.isImmutableType());
        Assert.assertEquals(Long.class, streamRecSer.createInstance().getValue().getClass());
        Assert.assertEquals((-1L), streamRecSer.getLength());
    }

    @Test
    public void testSerialization() throws Exception {
        final StreamElementSerializer<String> serializer = new StreamElementSerializer<String>(StringSerializer.INSTANCE);
        StreamRecord<String> withoutTimestamp = new StreamRecord("test 1 2 ???????????????!");
        Assert.assertEquals(withoutTimestamp, StreamElementSerializerTest.serializeAndDeserialize(withoutTimestamp, serializer));
        StreamRecord<String> withTimestamp = new StreamRecord("one more test ? ? ?", 77L);
        Assert.assertEquals(withTimestamp, StreamElementSerializerTest.serializeAndDeserialize(withTimestamp, serializer));
        StreamRecord<String> negativeTimestamp = new StreamRecord("?", Long.MIN_VALUE);
        Assert.assertEquals(negativeTimestamp, StreamElementSerializerTest.serializeAndDeserialize(negativeTimestamp, serializer));
        Watermark positiveWatermark = new Watermark(13);
        Assert.assertEquals(positiveWatermark, StreamElementSerializerTest.serializeAndDeserialize(positiveWatermark, serializer));
        Watermark negativeWatermark = new Watermark((-4647654567676555876L));
        Assert.assertEquals(negativeWatermark, StreamElementSerializerTest.serializeAndDeserialize(negativeWatermark, serializer));
        LatencyMarker latencyMarker = new LatencyMarker(System.currentTimeMillis(), new OperatorID((-1), (-1)), 1);
        Assert.assertEquals(latencyMarker, StreamElementSerializerTest.serializeAndDeserialize(latencyMarker, serializer));
    }
}

