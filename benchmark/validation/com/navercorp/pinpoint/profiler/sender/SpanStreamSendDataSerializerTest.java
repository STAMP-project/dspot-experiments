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
package com.navercorp.pinpoint.profiler.sender;


import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.io.request.Message;
import com.navercorp.pinpoint.thrift.dto.TSpan;
import com.navercorp.pinpoint.thrift.dto.TSpanChunk;
import com.navercorp.pinpoint.thrift.dto.TSpanEvent;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseDeserializer;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseDeserializerFactory;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseSerializerFactory;
import java.nio.ByteBuffer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class SpanStreamSendDataSerializerTest {
    private final String applicationName = "applicationName";

    private final String agentId = "agentId";

    private final long agentStartTime = System.currentTimeMillis();

    private final ServiceType applicationServiceType = ServiceType.STAND_ALONE;

    @Test
    public void spanStreamSendDataSerializerTest1() throws InterruptedException, TException {
        int spanEventSize = 10;
        SpanStreamSendDataSerializer serializer = new SpanStreamSendDataSerializer();
        HeaderTBaseSerializerFactory factory = new HeaderTBaseSerializerFactory();
        TSpanChunk spanChunk = newSpanChunk();
        spanChunk.setSpanEventList(createSpanEventList(spanEventSize));
        // spanChunkspanChunkFactory.create(newInternalTraceId(), createSpanEventList(spanEventSize));
        PartitionedByteBufferLocator partitionedByteBufferLocator = serializer.serializeSpanChunkStream(factory.createSerializer(), spanChunk);
        Assert.assertEquals((spanEventSize + 1), partitionedByteBufferLocator.getPartitionedCount());
        HeaderTBaseDeserializer deserializer = new HeaderTBaseDeserializerFactory().createDeserializer();
        for (int i = 0; i < (partitionedByteBufferLocator.getPartitionedCount()); i++) {
            ByteBuffer byteBuffer = partitionedByteBufferLocator.getByteBuffer(i);
            byte[] readBuffer = new byte[byteBuffer.remaining()];
            byteBuffer.get(readBuffer);
            Message<TBase<?, ?>> message = deserializer.deserialize(readBuffer);
            TBase<?, ?> data = message.getData();
            if (data == null) {
                Assert.fail();
            }
            if (i < spanEventSize) {
                Assert.assertTrue((data instanceof TSpanEvent));
            } else {
                Assert.assertTrue((data instanceof TSpanChunk));
            }
        }
    }

    @Test
    public void spanStreamSendDataSerializerTest2() throws InterruptedException, TException {
        int spanEventSize = 10;
        SpanStreamSendDataSerializer serializer = new SpanStreamSendDataSerializer();
        HeaderTBaseSerializerFactory factory = new HeaderTBaseSerializerFactory();
        TSpan span = createSpan(createSpanEventList(spanEventSize));
        PartitionedByteBufferLocator partitionedByteBufferLocator = serializer.serializeSpanStream(factory.createSerializer(), span);
        Assert.assertEquals((spanEventSize + 1), partitionedByteBufferLocator.getPartitionedCount());
        HeaderTBaseDeserializer deserializer = new HeaderTBaseDeserializerFactory().createDeserializer();
        for (int i = 0; i < (partitionedByteBufferLocator.getPartitionedCount()); i++) {
            ByteBuffer byteBuffer = partitionedByteBufferLocator.getByteBuffer(i);
            byte[] readBuffer = new byte[byteBuffer.remaining()];
            byteBuffer.get(readBuffer);
            Message<TBase<?, ?>> message = deserializer.deserialize(readBuffer);
            TBase<?, ?> data = message.getData();
            if (data == null) {
                Assert.fail();
            }
            if (i < spanEventSize) {
                Assert.assertTrue((data instanceof TSpanEvent));
            } else {
                Assert.assertTrue((data instanceof TSpan));
            }
        }
    }
}

