/**
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.sender.planer;


import com.navercorp.pinpoint.profiler.sender.PartitionedByteBufferLocator;
import com.navercorp.pinpoint.profiler.sender.SpanStreamSendDataFactory;
import com.navercorp.pinpoint.profiler.sender.SpanStreamSendDataSerializer;
import com.navercorp.pinpoint.profiler.util.ObjectPool;
import com.navercorp.pinpoint.thrift.dto.TSpan;
import com.navercorp.pinpoint.thrift.dto.TSpanEvent;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseSerializer;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseSerializerFactory;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SpanStreamSendDataPlanerTest {
    private static ObjectPool<HeaderTBaseSerializer> objectPool;

    @Test
    public void spanStreamSendDataPlanerTest() throws Exception {
        int spanEventSize = 10;
        SpanStreamSendDataSerializer serializer = new SpanStreamSendDataSerializer();
        HeaderTBaseSerializerFactory headerTBaseSerializerFactory = new HeaderTBaseSerializerFactory();
        List<TSpanEvent> originalSpanEventList = createSpanEventList(spanEventSize);
        TSpan span = createSpan(originalSpanEventList);
        PartitionedByteBufferLocator partitionedByteBufferLocator = serializer.serializeSpanStream(headerTBaseSerializerFactory.createSerializer(), span);
        SpanStreamSendDataFactory factory = new SpanStreamSendDataFactory(100, 50, SpanStreamSendDataPlanerTest.objectPool);
        List<TSpanEvent> spanEventList = getSpanEventList(partitionedByteBufferLocator, factory);
        partitionedByteBufferLocator = serializer.serializeSpanStream(headerTBaseSerializerFactory.createSerializer(), span);
        factory = new SpanStreamSendDataFactory(SpanStreamSendDataPlanerTest.objectPool);
        List<TSpanEvent> spanEventList2 = getSpanEventList(partitionedByteBufferLocator, factory);
        Assert.assertEquals(spanEventSize, spanEventList.size());
        Assert.assertEquals(spanEventSize, spanEventList2.size());
    }
}

