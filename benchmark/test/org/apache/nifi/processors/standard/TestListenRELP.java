/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import ListenRELP.MAX_BATCH_SIZE;
import ListenRELP.RELPAttributes.COMMAND;
import ListenRELP.RELPAttributes.PORT;
import ListenRELP.RELPAttributes.SENDER;
import ListenRELP.RELPAttributes.TXNR;
import ListenRELP.REL_SUCCESS;
import PostHTTP.SSL_CONTEXT_SERVICE;
import ProvenanceEventType.RECEIVE;
import StandardSSLContextService.KEYSTORE;
import StandardSSLContextService.KEYSTORE_PASSWORD;
import StandardSSLContextService.KEYSTORE_TYPE;
import StandardSSLContextService.TRUSTSTORE;
import StandardSSLContextService.TRUSTSTORE_PASSWORD;
import StandardSSLContextService.TRUSTSTORE_TYPE;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.listen.dispatcher.ChannelDispatcher;
import org.apache.nifi.processor.util.listen.response.ChannelResponder;
import org.apache.nifi.processors.standard.relp.event.RELPEvent;
import org.apache.nifi.processors.standard.relp.frame.RELPEncoder;
import org.apache.nifi.processors.standard.relp.frame.RELPFrame;
import org.apache.nifi.processors.standard.relp.response.RELPResponse;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.ssl.StandardSSLContextService;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestListenRELP {
    public static final String OPEN_FRAME_DATA = "relp_version=0\nrelp_software=librelp,1.2.7,http://librelp.adiscon.com\ncommands=syslog";

    public static final String SYSLOG_FRAME_DATA = "this is a syslog message here";

    static final RELPFrame OPEN_FRAME = new RELPFrame.Builder().txnr(1).command("open").dataLength(TestListenRELP.OPEN_FRAME_DATA.length()).data(TestListenRELP.OPEN_FRAME_DATA.getBytes(StandardCharsets.UTF_8)).build();

    static final RELPFrame SYSLOG_FRAME = new RELPFrame.Builder().txnr(2).command("syslog").dataLength(TestListenRELP.SYSLOG_FRAME_DATA.length()).data(TestListenRELP.SYSLOG_FRAME_DATA.getBytes(StandardCharsets.UTF_8)).build();

    static final RELPFrame CLOSE_FRAME = new RELPFrame.Builder().txnr(3).command("close").dataLength(0).data(new byte[0]).build();

    private RELPEncoder encoder;

    private TestListenRELP.ResponseCapturingListenRELP proc;

    private TestRunner runner;

    @Test
    public void testListenRELP() throws IOException, InterruptedException {
        final List<RELPFrame> frames = new ArrayList<>();
        frames.add(TestListenRELP.OPEN_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.CLOSE_FRAME);
        // three syslog frames should be transferred and three responses should be sent
        run(frames, 3, 3, null);
        final List<ProvenanceEventRecord> events = runner.getProvenanceEvents();
        Assert.assertNotNull(events);
        Assert.assertEquals(3, events.size());
        final ProvenanceEventRecord event = events.get(0);
        Assert.assertEquals(RECEIVE, event.getEventType());
        Assert.assertTrue("transit uri must be set and start with proper protocol", event.getTransitUri().toLowerCase().startsWith("relp"));
        final List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3, mockFlowFiles.size());
        final MockFlowFile mockFlowFile = mockFlowFiles.get(0);
        Assert.assertEquals(String.valueOf(TestListenRELP.SYSLOG_FRAME.getTxnr()), mockFlowFile.getAttribute(TXNR.key()));
        Assert.assertEquals(TestListenRELP.SYSLOG_FRAME.getCommand(), mockFlowFile.getAttribute(COMMAND.key()));
        Assert.assertTrue((!(StringUtils.isBlank(mockFlowFile.getAttribute(PORT.key())))));
        Assert.assertTrue((!(StringUtils.isBlank(mockFlowFile.getAttribute(SENDER.key())))));
    }

    @Test
    public void testBatching() throws IOException, InterruptedException {
        runner.setProperty(MAX_BATCH_SIZE, "5");
        final List<RELPFrame> frames = new ArrayList<>();
        frames.add(TestListenRELP.OPEN_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.CLOSE_FRAME);
        // one syslog frame should be transferred since we are batching, but three responses should be sent
        run(frames, 1, 3, null);
        final List<ProvenanceEventRecord> events = runner.getProvenanceEvents();
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.size());
        final ProvenanceEventRecord event = events.get(0);
        Assert.assertEquals(RECEIVE, event.getEventType());
        Assert.assertTrue("transit uri must be set and start with proper protocol", event.getTransitUri().toLowerCase().startsWith("relp"));
        final List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, mockFlowFiles.size());
        final MockFlowFile mockFlowFile = mockFlowFiles.get(0);
        Assert.assertEquals(TestListenRELP.SYSLOG_FRAME.getCommand(), mockFlowFile.getAttribute(COMMAND.key()));
        Assert.assertTrue((!(StringUtils.isBlank(mockFlowFile.getAttribute(PORT.key())))));
        Assert.assertTrue((!(StringUtils.isBlank(mockFlowFile.getAttribute(SENDER.key())))));
    }

    @Test
    public void testTLS() throws IOException, InterruptedException, InitializationException {
        final SSLContextService sslContextService = new StandardSSLContextService();
        runner.addControllerService("ssl-context", sslContextService);
        runner.setProperty(sslContextService, TRUSTSTORE, "src/test/resources/truststore.jks");
        runner.setProperty(sslContextService, TRUSTSTORE_PASSWORD, "passwordpassword");
        runner.setProperty(sslContextService, TRUSTSTORE_TYPE, "JKS");
        runner.setProperty(sslContextService, KEYSTORE, "src/test/resources/keystore.jks");
        runner.setProperty(sslContextService, KEYSTORE_PASSWORD, "passwordpassword");
        runner.setProperty(sslContextService, KEYSTORE_TYPE, "JKS");
        runner.enableControllerService(sslContextService);
        runner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        final List<RELPFrame> frames = new ArrayList<>();
        frames.add(TestListenRELP.OPEN_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.SYSLOG_FRAME);
        frames.add(TestListenRELP.CLOSE_FRAME);
        // three syslog frames should be transferred and three responses should be sent
        run(frames, 5, 5, sslContextService);
    }

    @Test
    public void testNoEventsAvailable() throws IOException, InterruptedException {
        TestListenRELP.MockListenRELP mockListenRELP = new TestListenRELP.MockListenRELP(new ArrayList<RELPEvent>());
        runner = TestRunners.newTestRunner(mockListenRELP);
        runner.setProperty(ListenRELP.PORT, "1");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
    }

    @Test
    public void testBatchingWithDifferentSenders() throws IOException, InterruptedException {
        final String sender1 = "sender1";
        final String sender2 = "sender2";
        final ChannelResponder<SocketChannel> responder = Mockito.mock(ChannelResponder.class);
        final List<RELPEvent> mockEvents = new ArrayList<>();
        mockEvents.add(new RELPEvent(sender1, TestListenRELP.SYSLOG_FRAME.getData(), responder, TestListenRELP.SYSLOG_FRAME.getTxnr(), TestListenRELP.SYSLOG_FRAME.getCommand()));
        mockEvents.add(new RELPEvent(sender1, TestListenRELP.SYSLOG_FRAME.getData(), responder, TestListenRELP.SYSLOG_FRAME.getTxnr(), TestListenRELP.SYSLOG_FRAME.getCommand()));
        mockEvents.add(new RELPEvent(sender2, TestListenRELP.SYSLOG_FRAME.getData(), responder, TestListenRELP.SYSLOG_FRAME.getTxnr(), TestListenRELP.SYSLOG_FRAME.getCommand()));
        mockEvents.add(new RELPEvent(sender2, TestListenRELP.SYSLOG_FRAME.getData(), responder, TestListenRELP.SYSLOG_FRAME.getTxnr(), TestListenRELP.SYSLOG_FRAME.getCommand()));
        TestListenRELP.MockListenRELP mockListenRELP = new TestListenRELP.MockListenRELP(mockEvents);
        runner = TestRunners.newTestRunner(mockListenRELP);
        runner.setProperty(ListenRELP.PORT, "1");
        runner.setProperty(MAX_BATCH_SIZE, "10");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
    }

    // Extend ListenRELP so we can use the CapturingSocketChannelResponseDispatcher
    private static class ResponseCapturingListenRELP extends ListenRELP {
        private List<RELPResponse> responses = new ArrayList<>();

        @Override
        protected void respond(RELPEvent event, RELPResponse relpResponse) {
            this.responses.add(relpResponse);
            super.respond(event, relpResponse);
        }
    }

    // Extend ListenRELP to mock the ChannelDispatcher and allow us to return staged events
    private static class MockListenRELP extends ListenRELP {
        private List<RELPEvent> mockEvents;

        public MockListenRELP(List<RELPEvent> mockEvents) {
            this.mockEvents = mockEvents;
        }

        @OnScheduled
        @Override
        public void onScheduled(ProcessContext context) throws IOException {
            super.onScheduled(context);
            events.addAll(mockEvents);
        }

        @Override
        protected ChannelDispatcher createDispatcher(ProcessContext context, BlockingQueue<RELPEvent> events) throws IOException {
            return Mockito.mock(ChannelDispatcher.class);
        }
    }
}

