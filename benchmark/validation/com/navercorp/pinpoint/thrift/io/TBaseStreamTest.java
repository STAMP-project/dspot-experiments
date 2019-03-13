/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.thrift.io;


import com.navercorp.pinpoint.thrift.dto.TSpanChunk;
import com.navercorp.pinpoint.thrift.dto.TSpanEvent;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TBaseStreamTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String AGENT_ID = "agentId";

    private static final String APPLICATION_NAME = "applicationName";

    private static final byte[] TRANSACTION_ID = "transactionId".getBytes();

    private static final long START_TIME = System.currentTimeMillis();

    private static final short SERVICE_TYPE = Short.valueOf("1");

    private static final TProtocolFactory DEFAULT_PROTOCOL_FACTORY = new TCompactProtocol.Factory();

    @Test
    public void clear() throws Exception {
        TBaseStream stream = new TBaseStream(TBaseStreamTest.DEFAULT_PROTOCOL_FACTORY);
        TSpanEvent spanEvent = newSpanEvent();
        stream.write(spanEvent);
        Assert.assertTrue(((stream.size()) > 0));
        stream.clear();
        Assert.assertTrue(stream.isEmpty());
    }

    @Test
    public void write() throws Exception {
        TBaseStream stream = new TBaseStream(TBaseStreamTest.DEFAULT_PROTOCOL_FACTORY);
        // single span event
        TSpanEvent spanEvent = newSpanEvent();
        stream.write(spanEvent);
        int size = stream.size();
        // append
        stream.write(spanEvent);
        Assert.assertEquals((size * 2), stream.size());
    }

    @Test
    public void splite() throws Exception {
        TBaseStream stream = new TBaseStream(TBaseStreamTest.DEFAULT_PROTOCOL_FACTORY);
        TSpanEvent spanEvent = newSpanEvent();
        stream.write(spanEvent);
        int size = stream.size();
        // append 3
        stream.write(spanEvent);
        stream.write(spanEvent);
        stream.write(spanEvent);
        // split 1
        List<ByteArrayOutput> nodes = stream.split(size);
        Assert.assertEquals(1, nodes.size());
        // split 2
        nodes = stream.split((size * 2));
        Assert.assertEquals(2, nodes.size());
        // split 1
        nodes = stream.split(1);
        Assert.assertEquals(1, nodes.size());
        nodes = stream.split(1);
        Assert.assertEquals(0, nodes.size());
    }

    @Test
    public void chunk() throws Exception {
        TBaseStream stream = new TBaseStream(TBaseStreamTest.DEFAULT_PROTOCOL_FACTORY);
        final String str1k = RandomStringUtils.randomAlphabetic(1024);
        // chunk
        TSpanChunk spanChunk = newSpanChunk();
        List<TSpanEvent> spanEventList = new ArrayList<TSpanEvent>();
        spanChunk.setSpanEventList(spanEventList);
        spanChunk.setSpanEventListIsSet(true);
        TSpanEvent event1k = new TSpanEvent();
        event1k.setDestinationId(str1k);
        event1k.setDestinationIdIsSet(true);
        // add 2 event
        spanEventList.add(event1k);
        spanEventList.add(event1k);
        // write event
        for (TSpanEvent e : spanChunk.getSpanEventList()) {
            stream.write(e);
        }
        logger.debug("event {}", stream);
        // split 1k
        TBaseStream chunkStream = new TBaseStream(TBaseStreamTest.DEFAULT_PROTOCOL_FACTORY);
        List<ByteArrayOutput> nodes = stream.split(1024);
        logger.debug("nodes {}", nodes);
        // chunkStream.write(spanChunk, "spanEventList", nodes);
        // while (!stream.isEmpty()) {
        // nodes = stream.split(1024);
        // System.out.println("nodes " + nodes);
        // chunkStream.write(spanChunk, "spanEventList", nodes);
        // }
        // 
        // System.out.println("chunk " + chunkStream);
    }
}

