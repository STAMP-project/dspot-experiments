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


import ListenUDP.MAX_BATCH_SIZE;
import ListenUDP.MAX_MESSAGE_QUEUE_SIZE;
import ListenUDP.MESSAGE_DELIMITER;
import ListenUDP.PORT;
import ListenUDP.REL_SUCCESS;
import ListenUDP.SENDING_HOST;
import ListenUDP.SENDING_HOST_PORT;
import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.listen.dispatcher.ChannelDispatcher;
import org.apache.nifi.processor.util.listen.event.StandardEvent;
import org.apache.nifi.processor.util.listen.response.ChannelResponder;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;
import org.mockito.Mockito;


public class TestListenUDP {
    private int port = 0;

    private ListenUDP proc;

    private TestRunner runner;

    @Test
    public void testCustomValidation() {
        runner.assertNotValid();
        runner.setProperty(PORT, "1");
        runner.assertValid();
        runner.setProperty(SENDING_HOST, "localhost");
        runner.assertNotValid();
        runner.setProperty(SENDING_HOST_PORT, "1234");
        runner.assertValid();
        runner.setProperty(SENDING_HOST, "");
        runner.assertNotValid();
    }

    @Test
    public void testDefaultBehavior() throws IOException, InterruptedException {
        final List<String> messages = getMessages(15);
        final int expectedQueued = messages.size();
        final int expectedTransferred = messages.size();
        // default behavior should produce a FlowFile per message sent
        run(new DatagramSocket(), messages, expectedQueued, expectedTransferred);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, messages.size());
        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        verifyFlowFiles(mockFlowFiles);
        verifyProvenance(expectedTransferred);
    }

    @Test
    public void testSendingMoreThanQueueSize() throws IOException, InterruptedException {
        final int maxQueueSize = 3;
        runner.setProperty(MAX_MESSAGE_QUEUE_SIZE, String.valueOf(maxQueueSize));
        final List<String> messages = getMessages(20);
        final int expectedQueued = maxQueueSize;
        final int expectedTransferred = maxQueueSize;
        run(new DatagramSocket(), messages, expectedQueued, expectedTransferred);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, maxQueueSize);
        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        verifyFlowFiles(mockFlowFiles);
        verifyProvenance(expectedTransferred);
    }

    @Test
    public void testBatchingSingleSender() throws IOException, InterruptedException {
        final String delimiter = "NN";
        runner.setProperty(MESSAGE_DELIMITER, delimiter);
        runner.setProperty(MAX_BATCH_SIZE, "3");
        final List<String> messages = getMessages(5);
        final int expectedQueued = messages.size();
        final int expectedTransferred = 2;
        run(new DatagramSocket(), messages, expectedQueued, expectedTransferred);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, expectedTransferred);
        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        MockFlowFile mockFlowFile1 = mockFlowFiles.get(0);
        mockFlowFile1.assertContentEquals((((("This is message 1" + delimiter) + "This is message 2") + delimiter) + "This is message 3"));
        MockFlowFile mockFlowFile2 = mockFlowFiles.get(1);
        mockFlowFile2.assertContentEquals((("This is message 4" + delimiter) + "This is message 5"));
        verifyProvenance(expectedTransferred);
    }

    @Test
    public void testBatchingWithDifferentSenders() throws IOException, InterruptedException {
        final String sender1 = "sender1";
        final String sender2 = "sender2";
        final ChannelResponder responder = Mockito.mock(ChannelResponder.class);
        final byte[] message = "test message".getBytes(StandardCharsets.UTF_8);
        final List<StandardEvent> mockEvents = new ArrayList<>();
        mockEvents.add(new StandardEvent(sender1, message, responder));
        mockEvents.add(new StandardEvent(sender1, message, responder));
        mockEvents.add(new StandardEvent(sender2, message, responder));
        mockEvents.add(new StandardEvent(sender2, message, responder));
        TestListenUDP.MockListenUDP mockListenUDP = new TestListenUDP.MockListenUDP(mockEvents);
        runner = TestRunners.newTestRunner(mockListenUDP);
        runner.setProperty(ListenRELP.PORT, "1");
        runner.setProperty(ListenRELP.MAX_BATCH_SIZE, "10");
        // sending 4 messages with a batch size of 10, but should get 2 FlowFiles because of different senders
        runner.run();
        runner.assertAllFlowFilesTransferred(ListenRELP.REL_SUCCESS, 2);
        verifyProvenance(2);
    }

    @Test
    public void testRunWhenNoEventsAvailable() throws IOException, InterruptedException {
        final List<StandardEvent> mockEvents = new ArrayList<>();
        TestListenUDP.MockListenUDP mockListenUDP = new TestListenUDP.MockListenUDP(mockEvents);
        runner = TestRunners.newTestRunner(mockListenUDP);
        runner.setProperty(ListenRELP.PORT, "1");
        runner.setProperty(ListenRELP.MAX_BATCH_SIZE, "10");
        runner.run(5);
        runner.assertAllFlowFilesTransferred(ListenRELP.REL_SUCCESS, 0);
    }

    @Test
    public void testWithSendingHostAndPortSameAsSender() throws IOException, InterruptedException {
        final String sendingHost = "localhost";
        final Integer sendingPort = 21001;
        runner.setProperty(SENDING_HOST, sendingHost);
        runner.setProperty(SENDING_HOST_PORT, String.valueOf(sendingPort));
        // bind to the same sending port that processor has for Sending Host Port
        final DatagramSocket socket = new DatagramSocket(sendingPort);
        final List<String> messages = getMessages(6);
        final int expectedQueued = messages.size();
        final int expectedTransferred = messages.size();
        run(socket, messages, expectedQueued, expectedTransferred);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, messages.size());
        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        verifyFlowFiles(mockFlowFiles);
        verifyProvenance(expectedTransferred);
    }

    @Test
    public void testWithSendingHostAndPortDifferentThanSender() throws IOException, InterruptedException {
        final String sendingHost = "localhost";
        final Integer sendingPort = 21001;
        runner.setProperty(SENDING_HOST, sendingHost);
        runner.setProperty(SENDING_HOST_PORT, String.valueOf(sendingPort));
        // bind to a different sending port than the processor has for Sending Host Port
        final DatagramSocket socket = new DatagramSocket(21002);
        // no messages should come through since we are listening for 21001 and sending from 21002
        final List<String> messages = getMessages(6);
        final int expectedQueued = 0;
        final int expectedTransferred = 0;
        run(socket, messages, expectedQueued, expectedTransferred);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
    }

    // Extend ListenUDP to mock the ChannelDispatcher and allow us to return staged events
    private static class MockListenUDP extends ListenUDP {
        private List<StandardEvent> mockEvents;

        public MockListenUDP(List<StandardEvent> mockEvents) {
            this.mockEvents = mockEvents;
        }

        @OnScheduled
        @Override
        public void onScheduled(ProcessContext context) throws IOException {
            super.onScheduled(context);
            events.addAll(mockEvents);
        }

        @Override
        protected ChannelDispatcher createDispatcher(ProcessContext context, BlockingQueue<StandardEvent> events) throws IOException {
            return Mockito.mock(ChannelDispatcher.class);
        }
    }
}

