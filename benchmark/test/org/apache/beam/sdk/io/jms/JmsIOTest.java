/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.jms;


import JmsIO.Read;
import JmsIO.UnboundedJmsReader;
import JmsIO.UnboundedJmsSource;
import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.broker.BrokerService;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of {@link JmsIO}.
 */
@RunWith(JUnit4.class)
public class JmsIOTest {
    private static final String BROKER_URL = "vm://localhost";

    private static final String USERNAME = "test_user";

    private static final String PASSWORD = "test_password";

    private static final String QUEUE = "test_queue";

    private static final String TOPIC = "test_topic";

    private BrokerService broker;

    private ConnectionFactory connectionFactory;

    private ConnectionFactory connectionFactoryWithSyncAcksAndWithoutPrefetch;

    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testAuthenticationRequired() {
        pipeline.apply(JmsIO.read().withConnectionFactory(connectionFactory).withQueue(JmsIOTest.QUEUE));
        runPipelineExpectingJmsConnectException("User name [null] or password is invalid.");
    }

    @Test
    public void testAuthenticationWithBadPassword() {
        pipeline.apply(JmsIO.read().withConnectionFactory(connectionFactory).withQueue(JmsIOTest.QUEUE).withUsername(JmsIOTest.USERNAME).withPassword("BAD"));
        runPipelineExpectingJmsConnectException((("User name [" + (JmsIOTest.USERNAME)) + "] or password is invalid."));
    }

    @Test
    public void testReadMessages() throws Exception {
        // produce message
        Connection connection = connectionFactory.createConnection(JmsIOTest.USERNAME, JmsIOTest.PASSWORD);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(session.createQueue(JmsIOTest.QUEUE));
        TextMessage message = session.createTextMessage("This Is A Test");
        producer.send(message);
        producer.send(message);
        producer.send(message);
        producer.send(message);
        producer.send(message);
        producer.send(message);
        producer.close();
        session.close();
        connection.close();
        // read from the queue
        PCollection<JmsRecord> output = pipeline.apply(JmsIO.read().withConnectionFactory(connectionFactory).withQueue(JmsIOTest.QUEUE).withUsername(JmsIOTest.USERNAME).withPassword(JmsIOTest.PASSWORD).withMaxNumRecords(5));
        PAssert.thatSingleton(output.apply("Count", Count.globally())).isEqualTo(5L);
        pipeline.run();
        connection = connectionFactory.createConnection(JmsIOTest.USERNAME, JmsIOTest.PASSWORD);
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(session.createQueue(JmsIOTest.QUEUE));
        Message msg = consumer.receiveNoWait();
        Assert.assertNull(msg);
    }

    @Test
    public void testReadBytesMessages() throws Exception {
        // produce message
        Connection connection = connectionFactory.createConnection(JmsIOTest.USERNAME, JmsIOTest.PASSWORD);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(session.createQueue(JmsIOTest.QUEUE));
        BytesMessage message = session.createBytesMessage();
        message.writeBytes("This Is A Test".getBytes(StandardCharsets.UTF_8));
        producer.send(message);
        producer.close();
        session.close();
        connection.close();
        // read from the queue
        PCollection<String> output = pipeline.apply(JmsIO.<String>readMessage().withConnectionFactory(connectionFactory).withQueue(JmsIOTest.QUEUE).withUsername(JmsIOTest.USERNAME).withPassword(JmsIOTest.PASSWORD).withMaxNumRecords(1).withCoder(SerializableCoder.of(String.class)).withMessageMapper(new JmsIOTest.BytesMessageToStringMessageMapper()));
        PAssert.thatSingleton(output.apply("Count", Count.<String>globally())).isEqualTo(1L);
        pipeline.run();
        connection = connectionFactory.createConnection(JmsIOTest.USERNAME, JmsIOTest.PASSWORD);
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(session.createQueue(JmsIOTest.QUEUE));
        Message msg = consumer.receiveNoWait();
        Assert.assertNull(msg);
    }

    @Test
    public void testWriteMessage() throws Exception {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add(("Message " + i));
        }
        pipeline.apply(Create.of(data)).apply(JmsIO.write().withConnectionFactory(connectionFactory).withQueue(JmsIOTest.QUEUE).withUsername(JmsIOTest.USERNAME).withPassword(JmsIOTest.PASSWORD));
        pipeline.run();
        Connection connection = connectionFactory.createConnection(JmsIOTest.USERNAME, JmsIOTest.PASSWORD);
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(session.createQueue(JmsIOTest.QUEUE));
        int count = 0;
        while ((consumer.receive(1000)) != null) {
            count++;
        } 
        Assert.assertEquals(100, count);
    }

    @Test
    public void testSplitForQueue() throws Exception {
        JmsIO.Read read = JmsIO.read().withQueue(JmsIOTest.QUEUE);
        PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
        int desiredNumSplits = 5;
        JmsIO.UnboundedJmsSource initialSource = new JmsIO.UnboundedJmsSource(read);
        List<JmsIO.UnboundedJmsSource> splits = initialSource.split(desiredNumSplits, pipelineOptions);
        // in the case of a queue, we have concurrent consumers by default, so the initial number
        // splits is equal to the desired number of splits
        Assert.assertEquals(desiredNumSplits, splits.size());
    }

    @Test
    public void testSplitForTopic() throws Exception {
        JmsIO.Read read = JmsIO.read().withTopic(JmsIOTest.TOPIC);
        PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
        int desiredNumSplits = 5;
        JmsIO.UnboundedJmsSource initialSource = new JmsIO.UnboundedJmsSource(read);
        List<JmsIO.UnboundedJmsSource> splits = initialSource.split(desiredNumSplits, pipelineOptions);
        // in the case of a topic, we can have only an unique subscriber on the topic per pipeline
        // else it means we can have duplicate messages (all subscribers on the topic receive every
        // message).
        // So, whatever the desizedNumSplits is, the actual number of splits should be 1.
        Assert.assertEquals(1, splits.size());
    }

    @Test
    public void testCheckpointMark() throws Exception {
        // we are using no prefetch here
        // prefetch is an ActiveMQ feature: to make efficient use of network resources the broker
        // utilizes a 'push' model to dispatch messages to consumers. However, in the case of our
        // test, it means that we can have some latency between the receiveNoWait() method used by
        // the consumer and the prefetch buffer populated by the broker. Using a prefetch to 0 means
        // that the consumer will poll for message, which is exactly what we want for the test.
        // We are also sending message acknowledgements synchronously to ensure that they are
        // processed before any subsequent assertions.
        Connection connection = connectionFactoryWithSyncAcksAndWithoutPrefetch.createConnection(JmsIOTest.USERNAME, JmsIOTest.PASSWORD);
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(session.createQueue(JmsIOTest.QUEUE));
        for (int i = 0; i < 10; i++) {
            producer.send(session.createTextMessage(("test " + i)));
        }
        producer.close();
        session.close();
        connection.close();
        JmsIO.Read spec = JmsIO.read().withConnectionFactory(connectionFactoryWithSyncAcksAndWithoutPrefetch).withUsername(JmsIOTest.USERNAME).withPassword(JmsIOTest.PASSWORD).withQueue(JmsIOTest.QUEUE);
        JmsIO.UnboundedJmsSource source = new JmsIO.UnboundedJmsSource(spec);
        JmsIO.UnboundedJmsReader reader = source.createReader(null, null);
        // start the reader and move to the first record
        Assert.assertTrue(reader.start());
        // consume 3 messages (NB: start already consumed the first message)
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(reader.advance());
        }
        // the messages are still pending in the queue (no ACK yet)
        Assert.assertEquals(10, count(JmsIOTest.QUEUE));
        // we finalize the checkpoint
        reader.getCheckpointMark().finalizeCheckpoint();
        // the checkpoint finalize ack the messages, and so they are not pending in the queue anymore
        Assert.assertEquals(6, count(JmsIOTest.QUEUE));
        // we read the 6 pending messages
        for (int i = 0; i < 6; i++) {
            Assert.assertTrue(reader.advance());
        }
        // still 6 pending messages as we didn't finalize the checkpoint
        Assert.assertEquals(6, count(JmsIOTest.QUEUE));
        // we finalize the checkpoint: no more message in the queue
        reader.getCheckpointMark().finalizeCheckpoint();
        Assert.assertEquals(0, count(JmsIOTest.QUEUE));
    }

    @Test
    public void testCheckpointMarkSafety() throws Exception {
        final int messagesToProcess = 100;
        // we are using no prefetch here
        // prefetch is an ActiveMQ feature: to make efficient use of network resources the broker
        // utilizes a 'push' model to dispatch messages to consumers. However, in the case of our
        // test, it means that we can have some latency between the receiveNoWait() method used by
        // the consumer and the prefetch buffer populated by the broker. Using a prefetch to 0 means
        // that the consumer will poll for message, which is exactly what we want for the test.
        // We are also sending message acknowledgements synchronously to ensure that they are
        // processed before any subsequent assertions.
        Connection connection = connectionFactoryWithSyncAcksAndWithoutPrefetch.createConnection(JmsIOTest.USERNAME, JmsIOTest.PASSWORD);
        connection.start();
        Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        // Fill the queue with messages
        MessageProducer producer = session.createProducer(session.createQueue(JmsIOTest.QUEUE));
        for (int i = 0; i < messagesToProcess; i++) {
            producer.send(session.createTextMessage(("test " + i)));
        }
        producer.close();
        session.close();
        connection.close();
        // create a JmsIO.Read with a decorated ConnectionFactory which will introduce a delay in
        // sending
        // acknowledgements - this should help uncover threading issues around checkpoint management.
        JmsIO.Read spec = JmsIO.read().withConnectionFactory(withSlowAcks(connectionFactoryWithSyncAcksAndWithoutPrefetch, 10)).withUsername(JmsIOTest.USERNAME).withPassword(JmsIOTest.PASSWORD).withQueue(JmsIOTest.QUEUE);
        JmsIO.UnboundedJmsSource source = new JmsIO.UnboundedJmsSource(spec);
        JmsIO.UnboundedJmsReader reader = source.createReader(null, null);
        // start the reader and move to the first record
        Assert.assertTrue(reader.start());
        // consume half the messages (NB: start already consumed the first message)
        for (int i = 0; i < ((messagesToProcess / 2) - 1); i++) {
            Assert.assertTrue(reader.advance());
        }
        // the messages are still pending in the queue (no ACK yet)
        Assert.assertEquals(messagesToProcess, count(JmsIOTest.QUEUE));
        // we finalize the checkpoint for the already-processed messages while simultaneously consuming
        // the remainder of
        // messages from the queue
        Thread runner = new Thread(() -> {
            try {
                for (int i = 0; i < (messagesToProcess / 2); i++) {
                    Assert.assertTrue(reader.advance());
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        runner.start();
        reader.getCheckpointMark().finalizeCheckpoint();
        // Concurrency issues would cause an exception to be thrown before this method exits, failing
        // the test
        runner.join();
    }

    /**
     * A test class that maps a {@link javax.jms.BytesMessage} into a {@link String}.
     */
    public static class BytesMessageToStringMessageMapper implements JmsIO.MessageMapper<String> {
        @Override
        public String mapMessage(Message message) throws Exception {
            BytesMessage bytesMessage = ((BytesMessage) (message));
            byte[] bytes = new byte[((int) (bytesMessage.getBodyLength()))];
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}

