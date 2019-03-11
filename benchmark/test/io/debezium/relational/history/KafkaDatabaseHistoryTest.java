/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.relational.history;


import ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG;
import ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;
import DatabaseHistory.NAME;
import KafkaDatabaseHistory.BOOTSTRAP_SERVERS;
import KafkaDatabaseHistory.RECOVERY_POLL_INTERVAL_MS;
import KafkaDatabaseHistory.SKIP_UNPARSEABLE_DDL_STATEMENTS;
import KafkaDatabaseHistory.TOPIC;
import ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import ProducerConfig.CLIENT_ID_CONFIG;
import ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import io.debezium.config.Configuration;
import io.debezium.kafka.KafkaCluster;
import io.debezium.text.ParsingException;
import java.util.Map;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class KafkaDatabaseHistoryTest {
    private KafkaDatabaseHistory history;

    private KafkaCluster kafka;

    private Map<String, String> source;

    private Map<String, Object> position;

    private String topicName;

    private String ddl;

    private static final int PARTITION_NO = 0;

    @Test
    public void shouldStartWithEmptyTopicAndStoreDataAndRecoverAllState() throws Exception {
        // Create the empty topic ...
        kafka.createTopic(topicName, 1, 1);
        testHistoryTopicContent(false);
    }

    @Test
    public void shouldIgnoreUnparseableMessages() throws Exception {
        // Create the empty topic ...
        kafka.createTopic(topicName, 1, 1);
        // Create invalid records
        final ProducerRecord<String, String> nullRecord = new ProducerRecord(topicName, KafkaDatabaseHistoryTest.PARTITION_NO, null, null);
        final ProducerRecord<String, String> emptyRecord = new ProducerRecord(topicName, KafkaDatabaseHistoryTest.PARTITION_NO, null, "");
        final ProducerRecord<String, String> noSourceRecord = new ProducerRecord(topicName, KafkaDatabaseHistoryTest.PARTITION_NO, null, "{\"position\":{\"filename\":\"my-txn-file.log\",\"position\":39},\"databaseName\":\"db1\",\"ddl\":\"DROP TABLE foo;\"}");
        final ProducerRecord<String, String> noPositionRecord = new ProducerRecord(topicName, KafkaDatabaseHistoryTest.PARTITION_NO, null, "{\"source\":{\"server\":\"my-server\"},\"databaseName\":\"db1\",\"ddl\":\"DROP TABLE foo;\"}");
        final ProducerRecord<String, String> invalidJSONRecord1 = new ProducerRecord(topicName, KafkaDatabaseHistoryTest.PARTITION_NO, null, "{\"source\":{\"server\":\"my-server\"},\"position\":{\"filename\":\"my-txn-file.log\",\"position\":39},\"databaseName\":\"db1\",\"ddl\":\"DROP TABLE foo;\"");
        final ProducerRecord<String, String> invalidJSONRecord2 = new ProducerRecord(topicName, KafkaDatabaseHistoryTest.PARTITION_NO, null, "\"source\":{\"server\":\"my-server\"},\"position\":{\"filename\":\"my-txn-file.log\",\"position\":39},\"databaseName\":\"db1\",\"ddl\":\"DROP TABLE foo;\"}");
        final ProducerRecord<String, String> invalidSQL = new ProducerRecord(topicName, KafkaDatabaseHistoryTest.PARTITION_NO, null, "{\"source\":{\"server\":\"my-server\"},\"position\":{\"filename\":\"my-txn-file.log\",\"position\":39},\"databaseName\":\"db1\",\"ddl\":\"xxxDROP TABLE foo;\"}");
        final Configuration intruderConfig = Configuration.create().withDefault(BOOTSTRAP_SERVERS_CONFIG, kafka.brokerList()).withDefault(CLIENT_ID_CONFIG, "intruder").withDefault(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class).withDefault(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class).build();
        try (final KafkaProducer<String, String> producer = new KafkaProducer(intruderConfig.asProperties())) {
            producer.send(nullRecord).get();
            producer.send(emptyRecord).get();
            producer.send(noSourceRecord).get();
            producer.send(noPositionRecord).get();
            producer.send(invalidJSONRecord1).get();
            producer.send(invalidJSONRecord2).get();
            producer.send(invalidSQL).get();
        }
        testHistoryTopicContent(true);
    }

    @Test(expected = ParsingException.class)
    public void shouldStopOnUnparseableSQL() throws Exception {
        // Create the empty topic ...
        kafka.createTopic(topicName, 1, 1);
        // Create invalid records
        final ProducerRecord<String, String> invalidSQL = new ProducerRecord(topicName, KafkaDatabaseHistoryTest.PARTITION_NO, null, "{\"source\":{\"server\":\"my-server\"},\"position\":{\"filename\":\"my-txn-file.log\",\"position\":39},\"databaseName\":\"db1\",\"ddl\":\"xxxDROP TABLE foo;\"}");
        final Configuration intruderConfig = Configuration.create().withDefault(BOOTSTRAP_SERVERS_CONFIG, kafka.brokerList()).withDefault(CLIENT_ID_CONFIG, "intruder").withDefault(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class).withDefault(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class).build();
        try (final KafkaProducer<String, String> producer = new KafkaProducer(intruderConfig.asProperties())) {
            producer.send(invalidSQL).get();
        }
        testHistoryTopicContent(false);
    }

    @Test
    public void testExists() {
        // happy path
        testHistoryTopicContent(true);
        Assert.assertTrue(history.exists());
        // Set history to use dummy topic
        Configuration config = // new since 0.10.1.0 - we want a low value because we're running everything locally
        // in this test. However, it can't be so low that the broker returns the same
        // messages more than once.
        Configuration.create().with(BOOTSTRAP_SERVERS, kafka.brokerList()).with(TOPIC, "dummytopic").with(NAME, "my-db-history").with(RECOVERY_POLL_INTERVAL_MS, 500).with(KafkaDatabaseHistory.consumerConfigPropertyName(MAX_POLL_INTERVAL_MS_CONFIG), 100).with(KafkaDatabaseHistory.consumerConfigPropertyName(SESSION_TIMEOUT_MS_CONFIG), 50000).with(SKIP_UNPARSEABLE_DDL_STATEMENTS, true).build();
        history.configure(config, null);
        history.start();
        // dummytopic should not exist yet
        Assert.assertFalse(history.exists());
    }
}

