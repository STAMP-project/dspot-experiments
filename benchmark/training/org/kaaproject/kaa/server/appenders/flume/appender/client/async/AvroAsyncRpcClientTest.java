/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.appenders.flume.appender.client.async;


import java.nio.charset.Charset;
import java.util.List;
import org.apache.avro.AvroRemoteException;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.flume.source.avro.AvroSourceProtocol;
import org.apache.flume.source.avro.Status;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AvroAsyncRpcClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(AvroAsyncRpcClientTest.class);

    private static final String localhost = "localhost";

    @Test
    public void testOkAppendBatchAsyncClient() throws EventDeliveryException, FlumeException {
        AvroAsyncRpcClientTest.handlerBatchAppendAsyncTest(new AvroAsyncRpcClientTest.OKAvroHandler(), 10, 5, 5, false, false, 0);
        AvroAsyncRpcClientTest.handlerBatchAppendAsyncTest(new AvroAsyncRpcClientTest.OKAvroHandler(), 10, 5, 10, false, false, 0);
        AvroAsyncRpcClientTest.handlerBatchAppendAsyncTest(new AvroAsyncRpcClientTest.OKAvroHandler(), 10, 5, 10, true, true, 6);
    }

    @Test
    public void testOkSleepAppendBatchAsyncClient() throws EventDeliveryException, FlumeException {
        // This will send 100,000 messages and wait 50 milliseconds for each batch
        // 1 thread will take over 5 seconds
        // 10 threads will take under a second
        long startTime = System.currentTimeMillis();
        AvroAsyncRpcClientTest.handlerBatchAppendAsyncTest(new AvroAsyncRpcClientTest.OKSleepAvroHandler(50), 1000, 1, 100, false, false, 0);
        System.out.println(("Test 1: " + ((System.currentTimeMillis()) - startTime)));
        startTime = System.currentTimeMillis();
        AvroAsyncRpcClientTest.handlerBatchAppendAsyncTest(new AvroAsyncRpcClientTest.OKSleepAvroHandler(50), 1000, 5, 100, false, false, 0);
        System.out.println(("Test 2: " + ((System.currentTimeMillis()) - startTime)));
        startTime = System.currentTimeMillis();
        AvroAsyncRpcClientTest.handlerBatchAppendAsyncTest(new AvroAsyncRpcClientTest.OKSleepAvroHandler(50), 1000, 10, 100, false, false, 0);
        System.out.println(("Test 3: " + ((System.currentTimeMillis()) - startTime)));
    }

    @Test
    public void testOkAppendAsyncClient() throws EventDeliveryException, FlumeException {
        AvroAsyncRpcClientTest.handlerAppendAsyncTest(new AvroAsyncRpcClientTest.OKAvroHandler(), 5, 5, false, false, 0);
        AvroAsyncRpcClientTest.handlerAppendAsyncTest(new AvroAsyncRpcClientTest.OKAvroHandler(), 5, 10, false, false, 0);
        AvroAsyncRpcClientTest.handlerAppendAsyncTest(new AvroAsyncRpcClientTest.OKAvroHandler(), 5, 10, true, true, 6);
    }

    private static class CompressionChannelPipelineFactory implements ChannelPipelineFactory {
        @Override
        public ChannelPipeline getPipeline() throws Exception {
            ChannelPipeline pipeline = Channels.pipeline();
            ZlibEncoder encoder = new ZlibEncoder(6);
            pipeline.addFirst("deflater", encoder);
            pipeline.addFirst("inflater", new ZlibDecoder());
            return pipeline;
        }
    }

    /**
     * A service that logs receipt of the request and returns Failed
     */
    public static class FailedAvroHandler implements AvroSourceProtocol {
        @Override
        public Status append(AvroFlumeEvent event) throws AvroRemoteException {
            AvroAsyncRpcClientTest.LOG.info("Failed: Received event from append(): {}", new String(event.getBody().array(), Charset.forName("UTF8")));
            return Status.FAILED;
        }

        @Override
        public Status appendBatch(List<AvroFlumeEvent> events) throws AvroRemoteException {
            AvroAsyncRpcClientTest.LOG.info("Failed: Received {} events from appendBatch()", events.size());
            return Status.FAILED;
        }
    }

    /**
     * A service that logs receipt of the request and returns Unknown
     */
    public static class UnknownAvroHandler implements AvroSourceProtocol {
        @Override
        public Status append(AvroFlumeEvent event) throws AvroRemoteException {
            AvroAsyncRpcClientTest.LOG.info("Unknown: Received event from append(): {}", new String(event.getBody().array(), Charset.forName("UTF8")));
            return Status.UNKNOWN;
        }

        @Override
        public Status appendBatch(List<AvroFlumeEvent> events) throws AvroRemoteException {
            AvroAsyncRpcClientTest.LOG.info("Unknown: Received {} events from appendBatch()", events.size());
            return Status.UNKNOWN;
        }
    }

    /**
     * A service that logs receipt of the request and returns OK
     */
    public static class OKAvroHandler implements AvroSourceProtocol {
        @Override
        public Status append(AvroFlumeEvent event) throws AvroRemoteException {
            AvroAsyncRpcClientTest.LOG.info("OK: Received event from append(): {}", new String(event.getBody().array(), Charset.forName("UTF8")));
            return Status.OK;
        }

        @Override
        public Status appendBatch(List<AvroFlumeEvent> events) throws AvroRemoteException {
            AvroAsyncRpcClientTest.LOG.info("OK: Received {} events from appendBatch()", events.size());
            return Status.OK;
        }
    }

    /**
     * A service that logs receipt of the request and returns OK
     */
    public static class OKSleepAvroHandler implements AvroSourceProtocol {
        int sleepTime;

        public OKSleepAvroHandler(int sleepTime) {
            this.sleepTime = sleepTime;
        }

        @Override
        public Status append(AvroFlumeEvent event) throws AvroRemoteException {
            AvroAsyncRpcClientTest.LOG.info("OK Sleep: Received event from append(): {}", new String(event.getBody().array(), Charset.forName("UTF8")));
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return Status.OK;
        }

        @Override
        public Status appendBatch(List<AvroFlumeEvent> events) throws AvroRemoteException {
            // System.out.println("OK Sleep: Received {} events from appendBatch()" + events.size());
            AvroAsyncRpcClientTest.LOG.info("OK Sleep: Received {} events from appendBatch()", events.size());
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return Status.OK;
        }
    }
}

