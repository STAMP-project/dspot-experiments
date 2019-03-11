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


import JmsProperties.ACKNOWLEDGEMENT_MODE;
import JmsProperties.ATTRIBUTES_TO_JMS_PROPS;
import JmsProperties.DESTINATION_NAME;
import JmsProperties.DESTINATION_TYPE;
import JmsProperties.JMS_PROPS_TO_ATTRIBUTES;
import JmsProperties.JMS_PROVIDER;
import JmsProperties.MAX_BUFFER_SIZE;
import JmsProperties.MESSAGE_PRIORITY;
import JmsProperties.MESSAGE_TTL;
import JmsProperties.MESSAGE_TYPE;
import JmsProperties.MSG_TYPE_BYTE;
import JmsProperties.MSG_TYPE_EMPTY;
import JmsProperties.MSG_TYPE_MAP;
import JmsProperties.MSG_TYPE_STREAM;
import JmsProperties.MSG_TYPE_TEXT;
import JmsProperties.REPLY_TO_QUEUE;
import JmsProperties.TIMEOUT;
import JmsProperties.URL;
import PutJMS.DEFAULT_MESSAGE_PRIORITY;
import PutJMS.REL_FAILURE;
import PutJMS.REL_SUCCESS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processors.standard.util.JmsFactory;
import org.apache.nifi.processors.standard.util.JmsProperties;
import org.apache.nifi.processors.standard.util.WrappedMessageProducer;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings("deprecation")
public class TestPutJMS {
    private final String TEST_PROVIDER = JmsProperties.ACTIVEMQ_PROVIDER;

    private final String TEST_URL = "vm://localhost?broker.persistent=false";

    private final String TEST_DEST_TYPE = JmsProperties.DESTINATION_TYPE_QUEUE;

    private final String TEST_DEST_NAME = "queue.testing";

    private final String TEST_ACK_MODE = JmsProperties.ACK_MODE_AUTO;

    @Test
    public void testGetRelationships() {
        final PutJMS putJMS = new PutJMS();
        final Set<Relationship> relationships = putJMS.getRelationships();
        Assert.assertEquals(2, relationships.size());
        Assert.assertTrue(relationships.contains(REL_FAILURE));
        Assert.assertTrue(relationships.contains(REL_SUCCESS));
    }

    @Test
    public void testCleanupResources() throws IllegalAccessException, NoSuchFieldException, JMSException {
        final PutJMS putJMS = new PutJMS();
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        final Queue<WrappedMessageProducer> wrappedMessageProducerQueue = Mockito.spy(new LinkedBlockingQueue<>());
        injectFieldValue(PutJMS.class, putJMS, "producerQueue", wrappedMessageProducerQueue);
        final WrappedMessageProducer wrappedProducer = JmsFactory.createMessageProducer(runnerPut.getProcessContext(), true);
        wrappedMessageProducerQueue.offer(wrappedProducer);
        Assert.assertNotNull(wrappedMessageProducerQueue.peek());
        putJMS.cleanupResources();
        Assert.assertNull(wrappedMessageProducerQueue.peek());
    }

    @Test
    public void testCreateMessageDirectly() throws JMSException {
        final PutJMS putJMS = new PutJMS();
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPut.setProperty(ACKNOWLEDGEMENT_MODE, TEST_ACK_MODE);
        final WrappedMessageProducer wrappedProducer = JmsFactory.createMessageProducer(runnerPut.getProcessContext(), true);
        final Session jmsSession = wrappedProducer.getSession();
        final MessageProducer producer = wrappedProducer.getProducer();
        final Message message = jmsSession.createTextMessage("createMessageDirectly");
        producer.send(message);
        jmsSession.commit();
        final GetJMSQueue getJmsQueue = new GetJMSQueue();
        final TestRunner runnerGet = TestRunners.newTestRunner(getJmsQueue);
        runnerGet.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerGet.setProperty(URL, TEST_URL);
        runnerGet.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerGet.setProperty(ACKNOWLEDGEMENT_MODE, TEST_ACK_MODE);
        runnerGet.run();
        final List<MockFlowFile> flowFiles = runnerGet.getFlowFilesForRelationship(new Relationship.Builder().name("success").build());
        Assert.assertEquals(1, flowFiles.size());
        final MockFlowFile successFlowFile = flowFiles.get(0);
        successFlowFile.assertContentEquals("createMessageDirectly");
        successFlowFile.assertAttributeEquals("jms.JMSDestination", ((TEST_DEST_NAME) + (testQueueSuffix())));
        producer.close();
        jmsSession.close();
    }

    @Test
    public void testPutGetAttributesAndProps() throws JMSException {
        final PutJMS putJMS = Mockito.spy(new PutJMS());
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPut.setProperty(REPLY_TO_QUEUE, (((TEST_DEST_NAME) + (testQueueSuffix())) + ".reply"));
        runnerPut.setProperty(ATTRIBUTES_TO_JMS_PROPS, "true");
        runnerPut.run();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "file1.txt");
        attributes.put("jms.string", "banana");
        attributes.put("jms.integer", "50");
        attributes.put("jms.integer.type", "integer");
        attributes.put("jms.float", "3.14159");
        attributes.put("jms.float.type", "float");
        attributes.put("jms.boolean", "true");
        attributes.put("jms.boolean.type", "boolean");
        attributes.put("jms.long", "123456789");
        attributes.put("jms.long.type", "long");
        attributes.put("jms.short", "16384");
        attributes.put("jms.short.type", "short");
        attributes.put("jms.byte", "127");
        attributes.put("jms.byte.type", "byte");
        attributes.put("jms.double", "3.1415626547");
        attributes.put("jms.double.type", "double");
        attributes.put("jms.object", "{\"id\":215, \"name\": \"john doe\"}");
        attributes.put("jms.object.type", "object");
        attributes.put("jms.eyes", "blue");
        attributes.put("jms.eyes.type", "color");
        attributes.put("jms.badinteger", "3.14");
        attributes.put("jms.badinteger.type", "integer");
        runnerPut.enqueue("putGetMessage".getBytes(), attributes);
        runnerPut.run();
        Assert.assertEquals(0, runnerPut.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPut.getFlowFilesForRelationship(REL_SUCCESS).size());
        final GetJMSQueue getJmsQueue = new GetJMSQueue();
        final TestRunner runnerGet = TestRunners.newTestRunner(getJmsQueue);
        runnerGet.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerGet.setProperty(URL, TEST_URL);
        runnerGet.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerGet.setProperty(ACKNOWLEDGEMENT_MODE, TEST_ACK_MODE);
        runnerGet.setProperty(JMS_PROPS_TO_ATTRIBUTES, "true");
        runnerGet.run();
        Assert.assertEquals(1, runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS).size());
        final List<MockFlowFile> flowFilesGet = runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS);
        Assert.assertEquals(1, flowFilesGet.size());
        final MockFlowFile successFlowFile = flowFilesGet.get(0);
        successFlowFile.assertContentEquals("putGetMessage");
        successFlowFile.assertAttributeEquals("jms.JMSDestination", ((TEST_DEST_NAME) + (testQueueSuffix())));
        successFlowFile.assertAttributeEquals("jms.JMSReplyTo", ((("queue://" + (TEST_DEST_NAME)) + (testQueueSuffix())) + ".reply"));
        successFlowFile.assertAttributeEquals("jms.string", "banana");
        successFlowFile.assertAttributeEquals("jms.integer", "50");
        successFlowFile.assertAttributeEquals("jms.float", "3.14159");
        successFlowFile.assertAttributeEquals("jms.boolean", "true");
        successFlowFile.assertAttributeEquals("jms.long", "123456789");
        successFlowFile.assertAttributeEquals("jms.short", "16384");
        successFlowFile.assertAttributeEquals("jms.byte", "127");
        successFlowFile.assertAttributeEquals("jms.double", "3.1415626547");
        successFlowFile.assertAttributeEquals("jms.object", "{\"id\":215, \"name\": \"john doe\"}");
        successFlowFile.assertAttributeEquals("jms.eyes", null);
        successFlowFile.assertAttributeEquals("jms.badinteger", null);
    }

    @Test
    public void testPutGetMessageTypes() throws JMSException {
        final GetJMSQueue getJmsQueue = new GetJMSQueue();
        final TestRunner runnerGet = TestRunners.newTestRunner(getJmsQueue);
        runnerGet.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerGet.setProperty(URL, TEST_URL);
        runnerGet.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerGet.setProperty(ACKNOWLEDGEMENT_MODE, TEST_ACK_MODE);
        runnerGet.setProperty(JMS_PROPS_TO_ATTRIBUTES, "true");
        // ------------------------------------------------------------
        final PutJMS putJMStext = Mockito.spy(new PutJMS());
        final TestRunner runnerPutText = TestRunners.newTestRunner(putJMStext);
        runnerPutText.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPutText.setProperty(URL, TEST_URL);
        runnerPutText.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPutText.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPutText.setProperty(MESSAGE_TYPE, MSG_TYPE_TEXT);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "file1.txt");
        runnerPutText.enqueue("putGetTextMessage", attributes);
        runnerPutText.run();
        Assert.assertEquals(0, runnerPutText.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPutText.getFlowFilesForRelationship(REL_SUCCESS).size());
        runnerGet.run();
        final List<MockFlowFile> ffText = runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS);
        Assert.assertEquals(1, ffText.size());
        final MockFlowFile successText = ffText.get(0);
        successText.assertContentEquals("putGetTextMessage");
        // ------------------------------------------------------------
        final PutJMS putJMSempty = Mockito.spy(new PutJMS());
        final TestRunner runnerPutEmpty = TestRunners.newTestRunner(putJMSempty);
        runnerPutEmpty.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPutEmpty.setProperty(URL, TEST_URL);
        runnerPutEmpty.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPutEmpty.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPutEmpty.setProperty(MESSAGE_TYPE, MSG_TYPE_EMPTY);
        final Map<String, String> attributesEmpty = new HashMap<>();
        attributesEmpty.put("filename", "file1.txt");
        runnerPutEmpty.enqueue("putGetEmptyMessage", attributesEmpty);
        runnerPutEmpty.run();
        Assert.assertEquals(0, runnerPutEmpty.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPutEmpty.getFlowFilesForRelationship(REL_SUCCESS).size());
        runnerGet.clearTransferState();
        runnerGet.run();
        final List<MockFlowFile> ffEmpty = runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS);
        Assert.assertEquals(1, ffEmpty.size());
        final MockFlowFile successEmpty = ffEmpty.get(0);
        successEmpty.assertContentEquals("");
        // ------------------------------------------------------------
        final PutJMS putJMSstream = Mockito.spy(new PutJMS());
        final TestRunner runnerPutStream = TestRunners.newTestRunner(putJMSstream);
        runnerPutStream.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPutStream.setProperty(URL, TEST_URL);
        runnerPutStream.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPutStream.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPutStream.setProperty(MESSAGE_TYPE, MSG_TYPE_STREAM);
        final Map<String, String> attributesStream = new HashMap<>();
        attributesStream.put("filename", "file1.txt");
        runnerPutStream.enqueue("putGetStreamMessage", attributesStream);
        runnerGet.clearTransferState();
        runnerPutStream.run();
        Assert.assertEquals(0, runnerPutStream.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPutStream.getFlowFilesForRelationship(REL_SUCCESS).size());
        runnerGet.run();
        final List<MockFlowFile> ffStream = runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS);
        Assert.assertEquals(1, ffStream.size());
        final MockFlowFile successStream = ffStream.get(0);
        successStream.assertContentEquals("putGetStreamMessage");
        // ------------------------------------------------------------
        final PutJMS putJMSmap = Mockito.spy(new PutJMS());
        final TestRunner runnerPutMap = TestRunners.newTestRunner(putJMSmap);
        runnerPutMap.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPutMap.setProperty(URL, TEST_URL);
        runnerPutMap.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPutMap.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPutMap.setProperty(MESSAGE_TYPE, MSG_TYPE_MAP);
        final Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("filename", "file1.txt");
        runnerPutMap.enqueue("putGetMapMessage", attributesMap);
        runnerGet.clearTransferState();
        runnerPutMap.run();
        Assert.assertEquals(0, runnerPutMap.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPutMap.getFlowFilesForRelationship(REL_SUCCESS).size());
        runnerGet.run();
        final List<MockFlowFile> ffMap = runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS);
        Assert.assertEquals(1, ffMap.size());
        final MockFlowFile successMap = ffMap.get(0);
        successMap.assertContentEquals("");
        // ------------------------------------------------------------
        final PutJMS putJMSByte = Mockito.spy(new PutJMS());
        final TestRunner runnerPutByte = TestRunners.newTestRunner(putJMSByte);
        runnerPutByte.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPutByte.setProperty(URL, TEST_URL);
        runnerPutByte.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPutByte.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPutByte.setProperty(MESSAGE_TYPE, MSG_TYPE_BYTE);
        final Map<String, String> attributesByte = new HashMap<>();
        attributesByte.put("filename", "file1.txt");
        runnerPutByte.enqueue("putGetTextMessage", attributesByte);
        runnerPutByte.run();
        Assert.assertEquals(0, runnerPutByte.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPutByte.getFlowFilesForRelationship(REL_SUCCESS).size());
        runnerGet.clearTransferState();
        runnerGet.run();
        final List<MockFlowFile> ffByte = runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS);
        Assert.assertEquals(1, ffByte.size());
        final MockFlowFile successByte = ffByte.get(0);
        successByte.assertContentEquals("putGetTextMessage");
    }

    @Test
    public void testTTL() throws InterruptedException, JMSException {
        final PutJMS putJMS = Mockito.spy(new PutJMS());
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPut.setProperty(MESSAGE_TTL, "3 s");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "file1.txt");
        runnerPut.enqueue("ttl10secNotExpired".getBytes(), attributes);
        runnerPut.run();
        Assert.assertEquals(0, runnerPut.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPut.getFlowFilesForRelationship(REL_SUCCESS).size());
        final GetJMSQueue getJmsQueue = new GetJMSQueue();
        final TestRunner runnerGet = TestRunners.newTestRunner(getJmsQueue);
        runnerGet.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerGet.setProperty(URL, TEST_URL);
        runnerGet.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerGet.setProperty(ACKNOWLEDGEMENT_MODE, TEST_ACK_MODE);
        runnerGet.setProperty(TIMEOUT, "1 s");
        runnerGet.run();
        final List<MockFlowFile> flowFiles1 = runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS);
        Assert.assertEquals(1, flowFiles1.size());
        final MockFlowFile successFlowFile1 = flowFiles1.get(0);
        successFlowFile1.assertContentEquals("ttl10secNotExpired");
        runnerPut.clearTransferState();
        runnerGet.clearTransferState();
        runnerPut.setProperty(MESSAGE_TTL, "1 s");
        runnerPut.enqueue("ttl1secExpired".getBytes(), attributes);
        runnerPut.run();
        Assert.assertEquals(0, runnerPut.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPut.getFlowFilesForRelationship(REL_SUCCESS).size());
        Thread.sleep(2000L);
        runnerGet.run();
        final List<MockFlowFile> flowFiles2 = runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS);
        Assert.assertEquals(0, flowFiles2.size());
    }

    @Test
    public void testFailureOnFileExceedsBufferSize() throws JMSException {
        final PutJMS putJMS = Mockito.spy(new PutJMS());
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPut.setProperty(MAX_BUFFER_SIZE, "10 B");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "file1.txt");
        runnerPut.enqueue("failureOnFileExceedsBufferSize".getBytes(), attributes);
        runnerPut.run();
        Assert.assertEquals(1, runnerPut.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(0, runnerPut.getFlowFilesForRelationship(REL_SUCCESS).size());
    }

    @Test(expected = NumberFormatException.class)
    public void testBadMessagePriorityValueFails() throws JMSException {
        final PutJMS putJMS = Mockito.spy(new PutJMS());
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPut.setProperty(MESSAGE_PRIORITY, "negative one");
        Assert.assertEquals(DEFAULT_MESSAGE_PRIORITY, runnerPut.getProcessContext().getProperty(MESSAGE_PRIORITY).asInteger().intValue());
    }

    @Test
    public void testBadMessagePriorityRunSucceeds() throws JMSException {
        final PutJMS putJMS = Mockito.spy(new PutJMS());
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPut.setProperty(MESSAGE_PRIORITY, "negative one");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "file1.txt");
        runnerPut.enqueue("badMessagePriorityRunSucceeds".getBytes(), attributes);
        runnerPut.run();
        Assert.assertEquals(0, runnerPut.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPut.getFlowFilesForRelationship(REL_SUCCESS).size());
    }

    @Test
    public void testPutSendRoutesToFailure() throws IllegalAccessException, NoSuchFieldException, JMSException {
        final PutJMS putJMS = Mockito.spy(new PutJMS());
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        final ProcessContext context = runnerPut.getProcessContext();
        final Queue<WrappedMessageProducer> wrappedMessageProducerQueue = Mockito.spy(new LinkedBlockingQueue<>());
        injectFieldValue(PutJMS.class, putJMS, "producerQueue", wrappedMessageProducerQueue);
        final WrappedMessageProducer wrappedMessageProducer = Mockito.spy(JmsFactory.createMessageProducer(context, true));
        final MessageProducer messageProducer = Mockito.spy(wrappedMessageProducer.getProducer());
        Mockito.doAnswer(new Answer<WrappedMessageProducer>() {
            @Override
            public WrappedMessageProducer answer(InvocationOnMock invocationOnMock) {
                return wrappedMessageProducer;
            }
        }).when(wrappedMessageProducerQueue).poll();
        Assert.assertEquals(wrappedMessageProducer, wrappedMessageProducerQueue.poll());
        Mockito.doAnswer(new Answer<MessageProducer>() {
            @Override
            public MessageProducer answer(InvocationOnMock invocationOnMock) {
                return messageProducer;
            }
        }).when(wrappedMessageProducer).getProducer();
        Mockito.doThrow(new JMSException("force send to fail")).when(messageProducer).send(ArgumentMatchers.any(Message.class));
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "file1.txt");
        runnerPut.enqueue("putSendRoutesToFailure".getBytes(), attributes);
        runnerPut.run();
        Assert.assertEquals(0, runnerPut.getFlowFilesForRelationship(REL_SUCCESS).size());
        Assert.assertEquals(1, runnerPut.getFlowFilesForRelationship(REL_FAILURE).size());
        final List<MockFlowFile> flowFilesFail = runnerPut.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, flowFilesFail.size());
    }

    @Test
    public void testPutCommitRoutesToFailure() throws IllegalAccessException, NoSuchFieldException, JMSException {
        final PutJMS putJMS = Mockito.spy(new PutJMS());
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        final ProcessContext context = runnerPut.getProcessContext();
        final Queue<WrappedMessageProducer> wrappedMessageProducerQueue = Mockito.spy(new LinkedBlockingQueue<>());
        injectFieldValue(PutJMS.class, putJMS, "producerQueue", wrappedMessageProducerQueue);
        final WrappedMessageProducer wrappedMessageProducer = Mockito.spy(JmsFactory.createMessageProducer(context, true));
        final MessageProducer messageProducer = Mockito.spy(wrappedMessageProducer.getProducer());
        final Connection connection = JmsFactory.createConnection(context);
        final Session jmsSession = Mockito.spy(JmsFactory.createSession(context, connection, true));
        Mockito.doAnswer(new Answer<WrappedMessageProducer>() {
            @Override
            public WrappedMessageProducer answer(InvocationOnMock invocationOnMock) {
                return wrappedMessageProducer;
            }
        }).when(wrappedMessageProducerQueue).poll();
        Mockito.doAnswer(new Answer<MessageProducer>() {
            @Override
            public MessageProducer answer(InvocationOnMock invocationOnMock) {
                return messageProducer;
            }
        }).when(wrappedMessageProducer).getProducer();
        Mockito.doAnswer(new Answer<Session>() {
            @Override
            public Session answer(InvocationOnMock invocationOnMock) {
                return jmsSession;
            }
        }).when(wrappedMessageProducer).getSession();
        Mockito.doThrow(new JMSException("force commit to fail")).when(jmsSession).commit();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "file1.txt");
        runnerPut.enqueue("putCommitRoutesToFailure".getBytes(), attributes);
        runnerPut.run();
        Assert.assertEquals(0, runnerPut.getFlowFilesForRelationship(REL_SUCCESS).size());
        Assert.assertEquals(1, runnerPut.getFlowFilesForRelationship(REL_FAILURE).size());
        final List<MockFlowFile> flowFilesFail = runnerPut.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, flowFilesFail.size());
    }

    @Test
    public void testPutProvenanceSendEventTransitUri() throws JMSException {
        final PutJMS putJMS = Mockito.spy(new PutJMS());
        final TestRunner runnerPut = TestRunners.newTestRunner(putJMS);
        runnerPut.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerPut.setProperty(URL, TEST_URL);
        runnerPut.setProperty(DESTINATION_TYPE, TEST_DEST_TYPE);
        runnerPut.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerPut.setProperty(ATTRIBUTES_TO_JMS_PROPS, "true");
        runnerPut.enqueue("putGetMessage".getBytes());
        runnerPut.run();
        Assert.assertEquals(0, runnerPut.getFlowFilesForRelationship(REL_FAILURE).size());
        Assert.assertEquals(1, runnerPut.getFlowFilesForRelationship(REL_SUCCESS).size());
        final List<ProvenanceEventRecord> putProvenanceEvents = runnerPut.getProvenanceEvents();
        Assert.assertEquals(1, putProvenanceEvents.size());
        // Verify the transitUri is the same as that configured in the properties
        Assert.assertEquals(TEST_URL, putProvenanceEvents.get(0).getTransitUri());
        final GetJMSQueue getJmsQueue = new GetJMSQueue();
        final TestRunner runnerGet = TestRunners.newTestRunner(getJmsQueue);
        runnerGet.setProperty(JMS_PROVIDER, TEST_PROVIDER);
        runnerGet.setProperty(URL, TEST_URL);
        runnerGet.setProperty(DESTINATION_NAME, ((TEST_DEST_NAME) + (testQueueSuffix())));
        runnerGet.setProperty(ACKNOWLEDGEMENT_MODE, TEST_ACK_MODE);
        runnerGet.run();
        Assert.assertEquals(1, runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS).size());
        final List<MockFlowFile> flowFilesGet = runnerGet.getFlowFilesForRelationship(GetJMSQueue.REL_SUCCESS);
        Assert.assertEquals(1, flowFilesGet.size());
        final MockFlowFile successFlowFile = flowFilesGet.get(0);
        successFlowFile.assertContentEquals("putGetMessage");
    }
}

