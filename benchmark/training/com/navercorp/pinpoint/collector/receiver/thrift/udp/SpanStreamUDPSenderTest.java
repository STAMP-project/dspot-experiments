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
package com.navercorp.pinpoint.collector.receiver.thrift.udp;


import com.navercorp.pinpoint.collector.receiver.DispatchHandler;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.io.request.ServerRequest;
import com.navercorp.pinpoint.io.request.ServerResponse;
import com.navercorp.pinpoint.profiler.context.compress.SpanProcessor;
import com.navercorp.pinpoint.profiler.context.compress.SpanProcessorV1;
import com.navercorp.pinpoint.profiler.context.id.DefaultTransactionIdEncoder;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;
import com.navercorp.pinpoint.profiler.context.id.TransactionIdEncoder;
import com.navercorp.pinpoint.profiler.context.thrift.MessageConverter;
import com.navercorp.pinpoint.profiler.sender.SpanStreamUdpSender;
import com.navercorp.pinpoint.test.utils.TestAwaitUtils;
import com.navercorp.pinpoint.thrift.dto.TResult;
import com.navercorp.pinpoint.thrift.dto.TSpan;
import com.navercorp.pinpoint.thrift.dto.TSpanChunk;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import org.apache.thrift.TBase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author emeroad
 */
public class SpanStreamUDPSenderTest {
    private String applicationName = "appName";

    private String agentId = "agentId";

    private long agentStartTime = 0;

    private ServiceType applicationServiceType = ServiceType.STAND_ALONE;

    private static SpanStreamUDPSenderTest.MessageHolderDispatchHandler messageHolder;

    private static UDPReceiver receiver = null;

    private static int port;

    private final TestAwaitUtils awaitUtils = new TestAwaitUtils(100, 6000);

    private final TransactionIdEncoder transactionIdEncoder = new DefaultTransactionIdEncoder(agentId, agentStartTime);

    private final SpanProcessor<TSpan, TSpanChunk> spanPostProcessor = new SpanProcessorV1();

    private final MessageConverter<TBase<?, ?>> messageConverter = new com.navercorp.pinpoint.profiler.context.thrift.SpanThriftMessageConverter(applicationName, agentId, agentStartTime, applicationServiceType.getCode(), transactionIdEncoder, spanPostProcessor);

    @Test
    public void sendTest1() throws InterruptedException {
        SpanStreamUdpSender sender = null;
        try {
            final TraceRoot traceRoot = mockTraceRoot();
            sender = new SpanStreamUdpSender("127.0.0.1", SpanStreamUDPSenderTest.port, "threadName", 10, 200, SpanStreamUdpSender.SEND_BUFFER_SIZE, messageConverter);
            sender.send(createSpanChunk(traceRoot, 10));
            sender.send(createSpanChunk(traceRoot, 3));
            awaitMessageReceived(2, SpanStreamUDPSenderTest.messageHolder, TSpanChunk.class);
            List<ServerRequest> tBaseList = SpanStreamUDPSenderTest.messageHolder.getMessageHolder();
            tBaseList.clear();
        } finally {
            if (sender != null) {
                sender.stop();
            }
        }
    }

    @Test
    public void sendTest2() throws InterruptedException {
        SpanStreamUdpSender sender = null;
        try {
            final TraceRoot traceRoot = mockTraceRoot();
            sender = new SpanStreamUdpSender("127.0.0.1", SpanStreamUDPSenderTest.port, "threadName", 10, 200, SpanStreamUdpSender.SEND_BUFFER_SIZE, messageConverter);
            sender.send(createSpan(traceRoot, 10));
            sender.send(createSpan(traceRoot, 3));
            awaitMessageReceived(2, SpanStreamUDPSenderTest.messageHolder, TSpan.class);
            List<ServerRequest> tBaseList = SpanStreamUDPSenderTest.messageHolder.getMessageHolder();
            tBaseList.clear();
        } finally {
            if (sender != null) {
                sender.stop();
            }
        }
    }

    @Test
    public void sendTest3() throws InterruptedException {
        SpanStreamUdpSender sender = null;
        try {
            final TraceRoot traceRoot = mockTraceRoot();
            sender = new SpanStreamUdpSender("127.0.0.1", SpanStreamUDPSenderTest.port, "threadName", 10, 200, SpanStreamUdpSender.SEND_BUFFER_SIZE, messageConverter);
            sender.send(createSpan(traceRoot, 10));
            sender.send(createSpan(traceRoot, 3));
            sender.send(createSpanChunk(traceRoot, 3));
            awaitMessageReceived(2, SpanStreamUDPSenderTest.messageHolder, TSpan.class);
            awaitMessageReceived(1, SpanStreamUDPSenderTest.messageHolder, TSpanChunk.class);
            List<ServerRequest> tBaseList = SpanStreamUDPSenderTest.messageHolder.getMessageHolder();
            tBaseList.clear();
        } finally {
            if (sender != null) {
                sender.stop();
            }
        }
    }

    static class TestTBaseFilter<T> implements TBaseFilter<T> {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public boolean filter(DatagramSocket localSocket, TBase<?, ?> tBase, T remoteHostAddress) {
            logger.debug("filter");
            return false;
        }
    }

    static class MessageHolderDispatchHandler implements DispatchHandler {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private List<ServerRequest> messageHolder = new ArrayList<>();

        @Override
        public void dispatchSendMessage(ServerRequest serverRequest) {
            logger.debug("dispatchSendMessage");
        }

        @Override
        public void dispatchRequestMessage(ServerRequest serverRequest, ServerResponse serverResponse) {
            messageHolder.add(serverRequest);
            TResult tResult = new TResult(true);
            serverResponse.write(tResult);
        }

        public List<ServerRequest> getMessageHolder() {
            return messageHolder;
        }
    }
}

