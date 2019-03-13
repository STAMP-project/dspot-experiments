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


import ExpressionLanguageScope.VARIABLE_REGISTRY;
import ListenSyslog.MAX_BATCH_SIZE;
import ListenSyslog.MESSAGE_DELIMITER;
import ListenSyslog.PARSE_MESSAGES;
import ListenSyslog.PORT;
import ListenSyslog.PROTOCOL;
import ListenSyslog.REL_INVALID;
import ListenSyslog.REL_SUCCESS;
import ListenSyslog.RawSyslogEvent;
import ListenSyslog.UDP_VALUE;
import ProvenanceEventType.RECEIVE;
import SyslogAttributes.SYSLOG_PORT;
import SyslogAttributes.SYSLOG_PROTOCOL;
import SyslogAttributes.SYSLOG_SENDER;
import Validator.VALID;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessSessionFactory;
import org.apache.nifi.processor.exception.FlowFileAccessException;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.syslog.events.SyslogEvent;
import org.apache.nifi.syslog.parsers.SyslogParser;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestListenSyslog {
    static final Logger LOGGER = LoggerFactory.getLogger(TestListenSyslog.class);

    static final String PRI = "34";

    static final String SEV = "2";

    static final String FAC = "4";

    static final String TIME = "Oct 13 15:43:23";

    static final String HOST = "localhost.home";

    static final String BODY = "some message";

    static final String VALID_MESSAGE = (((((("<" + (TestListenSyslog.PRI)) + ">") + (TestListenSyslog.TIME)) + " ") + (TestListenSyslog.HOST)) + " ") + (TestListenSyslog.BODY);

    static final String VALID_MESSAGE_TCP = ((((((("<" + (TestListenSyslog.PRI)) + ">") + (TestListenSyslog.TIME)) + " ") + (TestListenSyslog.HOST)) + " ") + (TestListenSyslog.BODY)) + "\n";

    static final String INVALID_MESSAGE = "this is not valid\n";

    @Test
    public void testBatching() throws IOException, InterruptedException {
        final ListenSyslog proc = new ListenSyslog();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(PROTOCOL, UDP_VALUE.getValue());
        runner.setProperty(PORT, "0");
        runner.setProperty(MAX_BATCH_SIZE, "25");
        runner.setProperty(MESSAGE_DELIMITER, "|");
        runner.setProperty(PARSE_MESSAGES, "false");
        // schedule to start listening on a random port
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        final ProcessContext context = runner.getProcessContext();
        proc.onScheduled(context);
        // the processor has internal blocking queue with capacity 10 so we have to send
        // less than that since we are sending all messages before the processors ever runs
        final int numMessages = 5;
        final int port = proc.getPort();
        Assert.assertTrue((port > 0));
        // write some UDP messages to the port in the background
        final Thread sender = new Thread(new TestListenSyslog.DatagramSender(port, numMessages, 10, TestListenSyslog.VALID_MESSAGE));
        sender.setDaemon(true);
        sender.start();
        sender.join();
        try {
            proc.onTrigger(context, processSessionFactory);
            runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
            final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            Assert.assertEquals("0", flowFile.getAttribute(SYSLOG_PORT.key()));
            Assert.assertEquals(UDP_VALUE.getValue(), flowFile.getAttribute(SYSLOG_PROTOCOL.key()));
            Assert.assertTrue((!(StringUtils.isBlank(flowFile.getAttribute(SYSLOG_SENDER.key())))));
            final String content = new String(flowFile.toByteArray(), StandardCharsets.UTF_8);
            final String[] splits = content.split("\\|");
            Assert.assertEquals(numMessages, splits.length);
            final List<ProvenanceEventRecord> events = runner.getProvenanceEvents();
            Assert.assertNotNull(events);
            Assert.assertEquals(1, events.size());
            final ProvenanceEventRecord event = events.get(0);
            Assert.assertEquals(RECEIVE, event.getEventType());
            Assert.assertTrue("transit uri must be set and start with proper protocol", event.getTransitUri().toLowerCase().startsWith("udp"));
        } finally {
            // unschedule to close connections
            proc.onUnscheduled();
        }
    }

    @Test
    public void testParsingError() throws IOException {
        final TestListenSyslog.FailParseProcessor proc = new TestListenSyslog.FailParseProcessor();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(PROTOCOL, UDP_VALUE.getValue());
        runner.setProperty(PORT, "0");
        // schedule to start listening on a random port
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        final ProcessContext context = runner.getProcessContext();
        proc.onScheduled(context);
        try {
            final int port = getPort();
            final TestListenSyslog.DatagramSender sender = new TestListenSyslog.DatagramSender(port, 1, 1, TestListenSyslog.INVALID_MESSAGE);
            sender.run();
            // should keep re-processing event1 from the error queue
            proc.onTrigger(context, processSessionFactory);
            runner.assertTransferCount(REL_INVALID, 1);
            runner.assertTransferCount(REL_SUCCESS, 0);
        } finally {
            onUnscheduled();
        }
    }

    @Test
    public void testErrorQueue() throws IOException {
        final List<ListenSyslog.RawSyslogEvent> msgs = new ArrayList<>();
        msgs.add(new ListenSyslog.RawSyslogEvent(TestListenSyslog.VALID_MESSAGE.getBytes(), "sender-01"));
        msgs.add(new ListenSyslog.RawSyslogEvent(TestListenSyslog.VALID_MESSAGE.getBytes(), "sender-01"));
        // Add message that will throw a FlowFileAccessException the first time that we attempt to read
        // the contents but will succeed the second time.
        final AtomicInteger getMessageAttempts = new AtomicInteger(0);
        msgs.add(new ListenSyslog.RawSyslogEvent(TestListenSyslog.VALID_MESSAGE.getBytes(), "sender-01") {
            @Override
            public byte[] getData() {
                final int attempts = getMessageAttempts.incrementAndGet();
                if (attempts == 1) {
                    throw new FlowFileAccessException("Unit test failure");
                } else {
                    return TestListenSyslog.VALID_MESSAGE.getBytes();
                }
            }
        });
        final TestListenSyslog.CannedMessageProcessor proc = new TestListenSyslog.CannedMessageProcessor(msgs);
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(MAX_BATCH_SIZE, "5");
        runner.setProperty(PROTOCOL, UDP_VALUE.getValue());
        runner.setProperty(PORT, "0");
        runner.setProperty(PARSE_MESSAGES, "false");
        runner.run();
        Assert.assertEquals(1, getErrorQueueSize());
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals((((TestListenSyslog.VALID_MESSAGE) + "\n") + (TestListenSyslog.VALID_MESSAGE)));
        // running again should pull from the error queue
        runner.clearTransferState();
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(TestListenSyslog.VALID_MESSAGE);
    }

    /**
     * Sends a given number of datagrams to the given port.
     */
    public static final class DatagramSender implements Runnable {
        final int port;

        final int numMessages;

        final long delay;

        final String message;

        public DatagramSender(int port, int numMessages, long delay, String message) {
            this.port = port;
            this.numMessages = numMessages;
            this.delay = delay;
            this.message = message;
        }

        @Override
        public void run() {
            byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
            final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            try (DatagramChannel channel = DatagramChannel.open()) {
                channel.connect(new InetSocketAddress("localhost", port));
                for (int i = 0; i < (numMessages); i++) {
                    buffer.clear();
                    buffer.put(bytes);
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        channel.write(buffer);
                    } 
                    Thread.sleep(delay);
                }
            } catch (IOException e) {
                TestListenSyslog.LOGGER.error(e.getMessage(), e);
            } catch (InterruptedException e) {
                TestListenSyslog.LOGGER.error(e.getMessage(), e);
            }
        }
    }

    // A mock version of ListenSyslog that will queue the provided events
    private static class FailParseProcessor extends ListenSyslog {
        @Override
        protected SyslogParser getParser() {
            return new SyslogParser(StandardCharsets.UTF_8) {
                @Override
                public SyslogEvent parseEvent(byte[] bytes, String sender) {
                    throw new ProcessException("Unit test intentionally failing");
                }
            };
        }
    }

    private static class CannedMessageProcessor extends ListenSyslog {
        private final Iterator<RawSyslogEvent> eventItr;

        public CannedMessageProcessor(final List<RawSyslogEvent> events) {
            this.eventItr = events.iterator();
        }

        @Override
        public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            final List<PropertyDescriptor> properties = new ArrayList(super.getSupportedPropertyDescriptors());
            properties.remove(PORT);
            properties.add(new PropertyDescriptor.Builder().name(PORT.getName()).expressionLanguageSupported(VARIABLE_REGISTRY).addValidator(VALID).build());
            return properties;
        }

        @Override
        protected RawSyslogEvent getMessage(final boolean longPoll, final boolean pollErrorQueue, final ProcessSession session) {
            if (eventItr.hasNext()) {
                return eventItr.next();
            }
            return super.getMessage(longPoll, pollErrorQueue, session);
        }
    }
}

