/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.security.authenticator;


import ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import ConsumerConfig.GROUP_ID_CONFIG;
import ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG;
import ProducerConfig.TRANSACTIONAL_ID_CONFIG;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SaslAuthenticationException;
import org.apache.kafka.common.network.NioEchoServer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.MockTime;
import org.junit.Assert;
import org.junit.Test;


public class ClientAuthenticationFailureTest {
    private static MockTime time = new MockTime(50);

    private NioEchoServer server;

    private Map<String, Object> saslServerConfigs;

    private Map<String, Object> saslClientConfigs;

    private final String topic = "test";

    private TestJaasConfig testJaasConfig;

    @Test
    public void testConsumerWithInvalidCredentials() {
        Map<String, Object> props = new HashMap<>(saslClientConfigs);
        props.put(BOOTSTRAP_SERVERS_CONFIG, ("localhost:" + (server.port())));
        props.put(GROUP_ID_CONFIG, "");
        StringDeserializer deserializer = new StringDeserializer();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer(props, deserializer, deserializer)) {
            consumer.subscribe(Arrays.asList(topic));
            consumer.poll(Duration.ofSeconds(10));
            Assert.fail("Expected an authentication error!");
        } catch (SaslAuthenticationException e) {
            // OK
        } catch (Exception e) {
            throw new AssertionError("Expected only an authentication error, but another error occurred.", e);
        }
    }

    @Test
    public void testProducerWithInvalidCredentials() {
        Map<String, Object> props = new HashMap<>(saslClientConfigs);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ("localhost:" + (server.port())));
        StringSerializer serializer = new StringSerializer();
        try (KafkaProducer<String, String> producer = new KafkaProducer(props, serializer, serializer)) {
            ProducerRecord<String, String> record = new ProducerRecord(topic, "message");
            producer.send(record).get();
            Assert.fail("Expected an authentication error!");
        } catch (Exception e) {
            Assert.assertTrue(("Expected SaslAuthenticationException, got " + (e.getCause().getClass())), ((e.getCause()) instanceof SaslAuthenticationException));
        }
    }

    @Test
    public void testAdminClientWithInvalidCredentials() {
        Map<String, Object> props = new HashMap<>(saslClientConfigs);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ("localhost:" + (server.port())));
        try (AdminClient client = AdminClient.create(props)) {
            DescribeTopicsResult result = client.describeTopics(Collections.singleton("test"));
            result.all().get();
            Assert.fail("Expected an authentication error!");
        } catch (Exception e) {
            Assert.assertTrue(("Expected SaslAuthenticationException, got " + (e.getCause().getClass())), ((e.getCause()) instanceof SaslAuthenticationException));
        }
    }

    @Test
    public void testTransactionalProducerWithInvalidCredentials() {
        Map<String, Object> props = new HashMap<>(saslClientConfigs);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ("localhost:" + (server.port())));
        props.put(TRANSACTIONAL_ID_CONFIG, "txclient-1");
        props.put(ENABLE_IDEMPOTENCE_CONFIG, "true");
        StringSerializer serializer = new StringSerializer();
        try (KafkaProducer<String, String> producer = new KafkaProducer(props, serializer, serializer)) {
            producer.initTransactions();
            Assert.fail("Expected an authentication error!");
        } catch (SaslAuthenticationException e) {
            // expected exception
        }
    }
}

