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
package org.apache.nifi.processors.kafka;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processors.kafka.KafkaPublisher.KafkaPublisherResult;
import org.apache.nifi.processors.kafka.test.EmbeddedKafka;
import org.apache.nifi.processors.kafka.test.EmbeddedKafkaProducerHelper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


// The test is valid and should be ran when working on this module. @Ignore is
// to speed up the overall build
@Ignore
public class KafkaPublisherTest {
    private static EmbeddedKafka kafkaLocal;

    private static EmbeddedKafkaProducerHelper producerHelper;

    @Test
    public void validateSuccessfulSendAsWhole() throws Exception {
        InputStream contentStream = new ByteArrayInputStream("Hello Kafka".getBytes(StandardCharsets.UTF_8));
        String topicName = "validateSuccessfulSendAsWhole";
        Properties kafkaProperties = this.buildProducerProperties();
        KafkaPublisher publisher = new KafkaPublisher(kafkaProperties, Mockito.mock(ComponentLog.class));
        PublishingContext publishingContext = new PublishingContext(contentStream, topicName);
        KafkaPublisherResult result = publisher.publish(publishingContext);
        Assert.assertEquals(0, result.getLastMessageAcked());
        Assert.assertEquals(1, result.getMessagesSent());
        contentStream.close();
        publisher.close();
        ConsumerIterator<byte[], byte[]> iter = this.buildConsumer(topicName);
        Assert.assertNotNull(iter.next());
        try {
            iter.next();
        } catch (ConsumerTimeoutException e) {
            // that's OK since this is the Kafka mechanism to unblock
        }
    }

    @Test
    public void validateSuccessfulSendAsDelimited() throws Exception {
        InputStream contentStream = new ByteArrayInputStream("Hello Kafka\nHello Kafka\nHello Kafka\nHello Kafka\n".getBytes(StandardCharsets.UTF_8));
        String topicName = "validateSuccessfulSendAsDelimited";
        Properties kafkaProperties = this.buildProducerProperties();
        KafkaPublisher publisher = new KafkaPublisher(kafkaProperties, Mockito.mock(ComponentLog.class));
        PublishingContext publishingContext = new PublishingContext(contentStream, topicName);
        publishingContext.setDelimiterBytes("\n".getBytes(StandardCharsets.UTF_8));
        KafkaPublisherResult result = publisher.publish(publishingContext);
        Assert.assertEquals(3, result.getLastMessageAcked());
        Assert.assertEquals(4, result.getMessagesSent());
        contentStream.close();
        publisher.close();
        ConsumerIterator<byte[], byte[]> iter = this.buildConsumer(topicName);
        Assert.assertNotNull(iter.next());
        Assert.assertNotNull(iter.next());
        Assert.assertNotNull(iter.next());
        Assert.assertNotNull(iter.next());
        try {
            iter.next();
            Assert.fail();
        } catch (ConsumerTimeoutException e) {
            // that's OK since this is the Kafka mechanism to unblock
        }
    }

    /* This test simulates the condition where not all messages were ACKed by
    Kafka
     */
    @Test
    public void validateRetries() throws Exception {
        byte[] testValue = "Hello Kafka1\nHello Kafka2\nHello Kafka3\nHello Kafka4\n".getBytes(StandardCharsets.UTF_8);
        InputStream contentStream = new ByteArrayInputStream(testValue);
        String topicName = "validateSuccessfulReSendOfFailedSegments";
        Properties kafkaProperties = this.buildProducerProperties();
        KafkaPublisher publisher = new KafkaPublisher(kafkaProperties, Mockito.mock(ComponentLog.class));
        // simulates the first re-try
        int lastAckedMessageIndex = 1;
        PublishingContext publishingContext = new PublishingContext(contentStream, topicName, lastAckedMessageIndex);
        publishingContext.setDelimiterBytes("\n".getBytes(StandardCharsets.UTF_8));
        publisher.publish(publishingContext);
        ConsumerIterator<byte[], byte[]> iter = this.buildConsumer(topicName);
        String m1 = new String(iter.next().message());
        String m2 = new String(iter.next().message());
        Assert.assertEquals("Hello Kafka3", m1);
        Assert.assertEquals("Hello Kafka4", m2);
        try {
            iter.next();
            Assert.fail();
        } catch (ConsumerTimeoutException e) {
            // that's OK since this is the Kafka mechanism to unblock
        }
        // simulates the second re-try
        lastAckedMessageIndex = 2;
        contentStream = new ByteArrayInputStream(testValue);
        publishingContext = new PublishingContext(contentStream, topicName, lastAckedMessageIndex);
        publishingContext.setDelimiterBytes("\n".getBytes(StandardCharsets.UTF_8));
        publisher.publish(publishingContext);
        m1 = new String(iter.next().message());
        Assert.assertEquals("Hello Kafka4", m1);
        publisher.close();
    }

    /* Similar to the above test, but it sets the first retry index to the last
    possible message index and second index to an out of bound index. The
    expectation is that no messages will be sent to Kafka
     */
    @Test
    public void validateRetriesWithWrongIndex() throws Exception {
        byte[] testValue = "Hello Kafka1\nHello Kafka2\nHello Kafka3\nHello Kafka4\n".getBytes(StandardCharsets.UTF_8);
        InputStream contentStream = new ByteArrayInputStream(testValue);
        String topicName = "validateRetriesWithWrongIndex";
        Properties kafkaProperties = this.buildProducerProperties();
        KafkaPublisher publisher = new KafkaPublisher(kafkaProperties, Mockito.mock(ComponentLog.class));
        // simulates the first re-try
        int lastAckedMessageIndex = 3;
        PublishingContext publishingContext = new PublishingContext(contentStream, topicName, lastAckedMessageIndex);
        publishingContext.setDelimiterBytes("\n".getBytes(StandardCharsets.UTF_8));
        publisher.publish(publishingContext);
        ConsumerIterator<byte[], byte[]> iter = this.buildConsumer(topicName);
        try {
            iter.next();
            Assert.fail();
        } catch (ConsumerTimeoutException e) {
            // that's OK since this is the Kafka mechanism to unblock
        }
        // simulates the second re-try
        lastAckedMessageIndex = 6;
        contentStream = new ByteArrayInputStream(testValue);
        publishingContext = new PublishingContext(contentStream, topicName, lastAckedMessageIndex);
        publishingContext.setDelimiterBytes("\n".getBytes(StandardCharsets.UTF_8));
        publisher.publish(publishingContext);
        try {
            iter.next();
            Assert.fail();
        } catch (ConsumerTimeoutException e) {
            // that's OK since this is the Kafka mechanism to unblock
        }
        publisher.close();
    }

    @Test
    public void validateWithMultiByteCharactersNoDelimiter() throws Exception {
        String data = "?THIS IS MY NEW TEXT.?IT HAS A NEWLINE.";
        InputStream contentStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        String topicName = "validateWithMultiByteCharacters";
        Properties kafkaProperties = this.buildProducerProperties();
        KafkaPublisher publisher = new KafkaPublisher(kafkaProperties, Mockito.mock(ComponentLog.class));
        PublishingContext publishingContext = new PublishingContext(contentStream, topicName);
        publisher.publish(publishingContext);
        publisher.close();
        ConsumerIterator<byte[], byte[]> iter = this.buildConsumer(topicName);
        String r = new String(iter.next().message(), StandardCharsets.UTF_8);
        Assert.assertEquals(data, r);
    }
}

