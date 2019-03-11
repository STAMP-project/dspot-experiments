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
package org.apache.flink.streaming.connectors.kafka;


import java.io.IOException;
import java.util.Properties;
import org.apache.flink.streaming.connectors.kafka.internal.FlinkKafkaInternalProducer;
import org.apache.kafka.clients.producer.Producer;
import org.junit.Test;


/**
 * Tests for our own {@link FlinkKafkaInternalProducer}.
 */
@SuppressWarnings("serial")
public class FlinkKafkaInternalProducerITCase extends KafkaTestBase {
    protected String transactionalId;

    protected Properties extraProperties;

    @Test(timeout = 30000L)
    public void testHappyPath() throws IOException {
        String topicName = "flink-kafka-producer-happy-path";
        try (Producer<String, String> kafkaProducer = new FlinkKafkaInternalProducer(extraProperties)) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            kafkaProducer.send(new org.apache.kafka.clients.producer.ProducerRecord(topicName, "42", "42"));
            kafkaProducer.commitTransaction();
        }
        assertRecord(topicName, "42", "42");
        deleteTestTopic(topicName);
    }

    @Test(timeout = 30000L)
    public void testResumeTransaction() throws IOException {
        String topicName = "flink-kafka-producer-resume-transaction";
        try (FlinkKafkaInternalProducer<String, String> kafkaProducer = new FlinkKafkaInternalProducer(extraProperties)) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            kafkaProducer.send(new org.apache.kafka.clients.producer.ProducerRecord(topicName, "42", "42"));
            kafkaProducer.flush();
            long producerId = kafkaProducer.getProducerId();
            short epoch = kafkaProducer.getEpoch();
            try (FlinkKafkaInternalProducer<String, String> resumeProducer = new FlinkKafkaInternalProducer(extraProperties)) {
                resumeProducer.resumeTransaction(producerId, epoch);
                resumeProducer.commitTransaction();
            }
            assertRecord(topicName, "42", "42");
            // this shouldn't throw - in case of network split, old producer might attempt to commit it's transaction
            kafkaProducer.commitTransaction();
            // this shouldn't fail also, for same reason as above
            try (FlinkKafkaInternalProducer<String, String> resumeProducer = new FlinkKafkaInternalProducer(extraProperties)) {
                resumeProducer.resumeTransaction(producerId, epoch);
                resumeProducer.commitTransaction();
            }
        }
        deleteTestTopic(topicName);
    }
}

