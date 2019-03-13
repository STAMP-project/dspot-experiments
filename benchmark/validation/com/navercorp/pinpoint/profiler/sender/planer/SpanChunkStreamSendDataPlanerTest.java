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


import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.profiler.context.SpanChunk;
import com.navercorp.pinpoint.profiler.context.SpanEvent;
import com.navercorp.pinpoint.profiler.context.compress.SpanProcessor;
import com.navercorp.pinpoint.profiler.context.compress.SpanProcessorV1;
import com.navercorp.pinpoint.profiler.context.id.DefaultTransactionIdEncoder;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;
import com.navercorp.pinpoint.profiler.context.id.TransactionIdEncoder;
import com.navercorp.pinpoint.profiler.context.thrift.MessageConverter;
import com.navercorp.pinpoint.profiler.sender.PartitionedByteBufferLocator;
import com.navercorp.pinpoint.profiler.sender.SpanStreamSendDataFactory;
import com.navercorp.pinpoint.profiler.sender.SpanStreamSendDataSerializer;
import com.navercorp.pinpoint.profiler.util.ObjectPool;
import com.navercorp.pinpoint.thrift.dto.TSpan;
import com.navercorp.pinpoint.thrift.dto.TSpanChunk;
import com.navercorp.pinpoint.thrift.dto.TSpanEvent;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseSerializer;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseSerializerFactory;
import java.util.List;
import org.apache.thrift.TBase;
import org.junit.Assert;
import org.junit.Test;


public class SpanChunkStreamSendDataPlanerTest {
    private final String applicationName = "applicationName";

    private final String agentId = "agentId";

    private final long agentStartTime = System.currentTimeMillis();

    private final ServiceType applicationServiceType = ServiceType.STAND_ALONE;

    private final TransactionIdEncoder encoder = new DefaultTransactionIdEncoder(agentId, agentStartTime);

    private SpanProcessor<TSpan, TSpanChunk> spanPostProcessor = new SpanProcessorV1();

    private TraceRoot traceRoot;

    private MessageConverter<TBase<?, ?>> messageConverter = new com.navercorp.pinpoint.profiler.context.thrift.SpanThriftMessageConverter(applicationName, agentId, agentStartTime, applicationServiceType.getCode(), encoder, spanPostProcessor);

    private ObjectPool<HeaderTBaseSerializer> objectPool;

    @Test
    public void spanChunkStreamSendDataPlanerTest() throws Exception {
        int spanEventSize = 10;
        SpanStreamSendDataSerializer serializer = new SpanStreamSendDataSerializer();
        HeaderTBaseSerializerFactory headerTBaseSerializerFactory = new HeaderTBaseSerializerFactory();
        List<SpanEvent> originalSpanEventList = createSpanEventList(spanEventSize);
        SpanChunk spanChunk = new com.navercorp.pinpoint.profiler.context.DefaultSpanChunk(traceRoot, originalSpanEventList);
        TBase<?, ?> tSpanChunk = messageConverter.toMessage(spanChunk);
        PartitionedByteBufferLocator partitionedByteBufferLocator = serializer.serializeSpanChunkStream(headerTBaseSerializerFactory.createSerializer(), ((TSpanChunk) (tSpanChunk)));
        SpanStreamSendDataFactory factory = new SpanStreamSendDataFactory(100, 50, objectPool);
        List<TSpanEvent> spanEventList = getSpanEventList(partitionedByteBufferLocator, factory);
        partitionedByteBufferLocator = serializer.serializeSpanChunkStream(headerTBaseSerializerFactory.createSerializer(), ((TSpanChunk) (tSpanChunk)));
        factory = new SpanStreamSendDataFactory(objectPool);
        List<TSpanEvent> spanEventList2 = getSpanEventList(partitionedByteBufferLocator, factory);
        Assert.assertEquals(spanEventSize, spanEventList.size());
        Assert.assertEquals(spanEventSize, spanEventList2.size());
    }
}

