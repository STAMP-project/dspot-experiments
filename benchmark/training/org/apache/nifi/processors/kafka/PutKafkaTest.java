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


import PutKafka.CLIENT_NAME;
import PutKafka.KEY;
import PutKafka.MESSAGE_DELIMITER;
import PutKafka.PARTITION;
import PutKafka.PARTITION_STRATEGY;
import PutKafka.REL_FAILURE;
import PutKafka.REL_SUCCESS;
import PutKafka.SEED_BROKERS;
import PutKafka.TOPIC;
import PutKafka.USER_DEFINED_PARTITIONING;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import kafka.consumer.ConsumerIterator;
import org.apache.nifi.processors.kafka.test.EmbeddedKafka;
import org.apache.nifi.processors.kafka.test.EmbeddedKafkaProducerHelper;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


// The test is valid and should be ran when working on this module. @Ignore is
// to speed up the overall build
@Ignore
@SuppressWarnings("deprecation")
public class PutKafkaTest {
    private static EmbeddedKafka kafkaLocal;

    private static EmbeddedKafkaProducerHelper producerHelper;

    @Test
    public void validateSingleCharacterDemarcatedMessages() {
        String topicName = "validateSingleCharacterDemarcatedMessages";
        PutKafka putKafka = new PutKafka();
        TestRunner runner = TestRunners.newTestRunner(putKafka);
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(CLIENT_NAME, "foo");
        runner.setProperty(KEY, "key1");
        runner.setProperty(SEED_BROKERS, ("localhost:" + (PutKafkaTest.kafkaLocal.getKafkaPort())));
        runner.setProperty(MESSAGE_DELIMITER, "\n");
        runner.enqueue("Hello World\nGoodbye\n1\n2\n3\n4\n5".getBytes(StandardCharsets.UTF_8));
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ConsumerIterator<byte[], byte[]> consumer = this.buildConsumer(topicName);
        Assert.assertEquals("Hello World", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("Goodbye", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("1", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("2", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("3", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("4", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("5", new String(consumer.next().message(), StandardCharsets.UTF_8));
        runner.shutdown();
    }

    @Test
    public void validateMultiCharacterDelimitedMessages() {
        String topicName = "validateMultiCharacterDemarcatedMessagesAndCustomPartitioner";
        PutKafka putKafka = new PutKafka();
        TestRunner runner = TestRunners.newTestRunner(putKafka);
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(CLIENT_NAME, "foo");
        runner.setProperty(KEY, "key1");
        runner.setProperty(SEED_BROKERS, ("localhost:" + (PutKafkaTest.kafkaLocal.getKafkaPort())));
        runner.setProperty(MESSAGE_DELIMITER, "foo");
        runner.enqueue("Hello WorldfooGoodbyefoo1foo2foo3foo4foo5".getBytes(StandardCharsets.UTF_8));
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ConsumerIterator<byte[], byte[]> consumer = this.buildConsumer(topicName);
        Assert.assertEquals("Hello World", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("Goodbye", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("1", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("2", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("3", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("4", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("5", new String(consumer.next().message(), StandardCharsets.UTF_8));
        runner.shutdown();
    }

    @Test
    public void validateDemarcationIntoEmptyMessages() {
        String topicName = "validateDemarcationIntoEmptyMessages";
        PutKafka putKafka = new PutKafka();
        final TestRunner runner = TestRunners.newTestRunner(putKafka);
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(KEY, "key1");
        runner.setProperty(CLIENT_NAME, "foo");
        runner.setProperty(SEED_BROKERS, ("localhost:" + (PutKafkaTest.kafkaLocal.getKafkaPort())));
        runner.setProperty(MESSAGE_DELIMITER, "\n");
        final byte[] bytes = "\n\n\n1\n2\n\n\n3\n4\n\n\n".getBytes(StandardCharsets.UTF_8);
        runner.enqueue(bytes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ConsumerIterator<byte[], byte[]> consumer = this.buildConsumer(topicName);
        Assert.assertNotNull(consumer.next());
        Assert.assertNotNull(consumer.next());
        Assert.assertNotNull(consumer.next());
        Assert.assertNotNull(consumer.next());
        try {
            consumer.next();
            Assert.fail();
        } catch (Exception e) {
            // ignore
        }
    }

    @Test
    public void validateComplexRightPartialDemarcatedMessages() {
        String topicName = "validateComplexRightPartialDemarcatedMessages";
        PutKafka putKafka = new PutKafka();
        TestRunner runner = TestRunners.newTestRunner(putKafka);
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(CLIENT_NAME, "foo");
        runner.setProperty(SEED_BROKERS, ("localhost:" + (PutKafkaTest.kafkaLocal.getKafkaPort())));
        runner.setProperty(MESSAGE_DELIMITER, "?<?WILDSTUFF?>?");
        runner.enqueue("Hello World?<?WILDSTUFF?>?Goodbye?<?WILDSTUFF?>?I Mean IT!?<?WILDSTUFF?>".getBytes(StandardCharsets.UTF_8));
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ConsumerIterator<byte[], byte[]> consumer = this.buildConsumer(topicName);
        Assert.assertEquals("Hello World", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("Goodbye", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("I Mean IT!?<?WILDSTUFF?>", new String(consumer.next().message(), StandardCharsets.UTF_8));
        runner.shutdown();
    }

    @Test
    public void validateComplexLeftPartialDemarcatedMessages() {
        String topicName = "validateComplexLeftPartialDemarcatedMessages";
        PutKafka putKafka = new PutKafka();
        TestRunner runner = TestRunners.newTestRunner(putKafka);
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(CLIENT_NAME, "foo");
        runner.setProperty(SEED_BROKERS, ("localhost:" + (PutKafkaTest.kafkaLocal.getKafkaPort())));
        runner.setProperty(MESSAGE_DELIMITER, "?<?WILDSTUFF?>?");
        runner.enqueue("Hello World?<?WILDSTUFF?>?Goodbye?<?WILDSTUFF?>?I Mean IT!?<?WILDSTUFF?>?<?WILDSTUFF?>?".getBytes(StandardCharsets.UTF_8));
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ConsumerIterator<byte[], byte[]> consumer = this.buildConsumer(topicName);
        byte[] message = consumer.next().message();
        Assert.assertEquals("Hello World", new String(message, StandardCharsets.UTF_8));
        Assert.assertEquals("Goodbye", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("I Mean IT!", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("<?WILDSTUFF?>?", new String(consumer.next().message(), StandardCharsets.UTF_8));
        runner.shutdown();
    }

    @Test
    public void validateComplexPartialMatchDemarcatedMessages() {
        String topicName = "validateComplexPartialMatchDemarcatedMessages";
        PutKafka putKafka = new PutKafka();
        TestRunner runner = TestRunners.newTestRunner(putKafka);
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(CLIENT_NAME, "foo");
        runner.setProperty(SEED_BROKERS, ("localhost:" + (PutKafkaTest.kafkaLocal.getKafkaPort())));
        runner.setProperty(MESSAGE_DELIMITER, "?<?WILDSTUFF?>?");
        runner.enqueue("Hello World?<?WILDSTUFF?>?Goodbye?<?WILDBOOMSTUFF?>?".getBytes(StandardCharsets.UTF_8));
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ConsumerIterator<byte[], byte[]> consumer = this.buildConsumer(topicName);
        Assert.assertEquals("Hello World", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("Goodbye?<?WILDBOOMSTUFF?>?", new String(consumer.next().message(), StandardCharsets.UTF_8));
        runner.shutdown();
    }

    @Test
    public void validateDeprecatedPartitionStrategy() {
        String topicName = "validateDeprecatedPartitionStrategy";
        PutKafka putKafka = new PutKafka();
        TestRunner runner = TestRunners.newTestRunner(putKafka);
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(CLIENT_NAME, "foo");
        runner.setProperty(KEY, "key1");
        runner.setProperty(SEED_BROKERS, ("localhost:" + (PutKafkaTest.kafkaLocal.getKafkaPort())));
        runner.setProperty(MESSAGE_DELIMITER, "\n");
        // Old configuration using deprecated property still work.
        runner.setProperty(PARTITION_STRATEGY, USER_DEFINED_PARTITIONING);
        runner.setProperty(PARTITION, "${partition}");
        runner.assertValid();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("partition", "0");
        runner.enqueue("Hello World\nGoodbye".getBytes(StandardCharsets.UTF_8), attributes);
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ConsumerIterator<byte[], byte[]> consumer = this.buildConsumer(topicName);
        Assert.assertEquals("Hello World", new String(consumer.next().message(), StandardCharsets.UTF_8));
        Assert.assertEquals("Goodbye", new String(consumer.next().message(), StandardCharsets.UTF_8));
        runner.shutdown();
    }

    @Test
    public void validatePartitionOutOfBounds() {
        String topicName = "validatePartitionOutOfBounds";
        PutKafka putKafka = new PutKafka();
        TestRunner runner = TestRunners.newTestRunner(putKafka);
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(CLIENT_NAME, "foo");
        runner.setProperty(KEY, "key1");
        runner.setProperty(SEED_BROKERS, ("localhost:" + (PutKafkaTest.kafkaLocal.getKafkaPort())));
        runner.setProperty(MESSAGE_DELIMITER, "\n");
        runner.setProperty(PARTITION, "${partition}");
        runner.assertValid();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("partition", "123");
        runner.enqueue("Hello World\nGoodbye".getBytes(StandardCharsets.UTF_8), attributes);
        runner.run(1, false);
        Assert.assertTrue("Error message should be logged", ((runner.getLogger().getErrorMessages().size()) > 0));
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.shutdown();
    }
}

