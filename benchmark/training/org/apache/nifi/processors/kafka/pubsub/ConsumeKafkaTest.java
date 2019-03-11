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
package org.apache.nifi.processors.kafka.pubsub;


import ConsumeKafka_0_10.AUTO_OFFSET_RESET;
import ConsumeKafka_0_10.GROUP_ID;
import ConsumeKafka_0_10.OFFSET_EARLIEST;
import ConsumeKafka_0_10.TOPICS;
import ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import KafkaProcessorUtils.BOOTSTRAP_SERVERS;
import KafkaProcessorUtils.JAAS_SERVICE_NAME;
import KafkaProcessorUtils.SECURITY_PROTOCOL;
import KafkaProcessorUtils.SEC_SASL_PLAINTEXT;
import KafkaProcessorUtils.USER_KEYTAB;
import KafkaProcessorUtils.USER_PRINCIPAL;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class ConsumeKafkaTest {
    ConsumerLease mockLease = null;

    ConsumerPool mockConsumerPool = null;

    @Test
    public void validateCustomValidatorSettings() throws Exception {
        ConsumeKafka_0_10 consumeKafka = new ConsumeKafka_0_10();
        TestRunner runner = TestRunners.newTestRunner(consumeKafka);
        runner.setProperty(BOOTSTRAP_SERVERS, "okeydokey:1234");
        runner.setProperty(TOPICS, "foo");
        runner.setProperty(GROUP_ID, "foo");
        runner.setProperty(AUTO_OFFSET_RESET, OFFSET_EARLIEST);
        runner.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        runner.assertValid();
        runner.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, "Foo");
        runner.assertNotValid();
        runner.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        runner.assertValid();
        runner.setProperty(ENABLE_AUTO_COMMIT_CONFIG, "false");
        runner.assertValid();
        runner.setProperty(ENABLE_AUTO_COMMIT_CONFIG, "true");
        runner.assertNotValid();
    }

    @Test
    public void validatePropertiesValidation() throws Exception {
        ConsumeKafka_0_10 consumeKafka = new ConsumeKafka_0_10();
        TestRunner runner = TestRunners.newTestRunner(consumeKafka);
        runner.setProperty(BOOTSTRAP_SERVERS, "okeydokey:1234");
        runner.setProperty(TOPICS, "foo");
        runner.setProperty(GROUP_ID, "foo");
        runner.setProperty(AUTO_OFFSET_RESET, OFFSET_EARLIEST);
        runner.removeProperty(GROUP_ID);
        try {
            runner.assertValid();
            Assert.fail();
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().contains("invalid because Group ID is required"));
        }
        runner.setProperty(GROUP_ID, "");
        try {
            runner.assertValid();
            Assert.fail();
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().contains("must contain at least one character that is not white space"));
        }
        runner.setProperty(GROUP_ID, "  ");
        try {
            runner.assertValid();
            Assert.fail();
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().contains("must contain at least one character that is not white space"));
        }
    }

    @Test
    public void testJaasConfiguration() throws Exception {
        ConsumeKafka_0_10 consumeKafka = new ConsumeKafka_0_10();
        TestRunner runner = TestRunners.newTestRunner(consumeKafka);
        runner.setProperty(BOOTSTRAP_SERVERS, "okeydokey:1234");
        runner.setProperty(TOPICS, "foo");
        runner.setProperty(GROUP_ID, "foo");
        runner.setProperty(AUTO_OFFSET_RESET, OFFSET_EARLIEST);
        runner.setProperty(SECURITY_PROTOCOL, SEC_SASL_PLAINTEXT);
        runner.assertNotValid();
        runner.setProperty(JAAS_SERVICE_NAME, "kafka");
        runner.assertValid();
        runner.setProperty(USER_PRINCIPAL, "nifi@APACHE.COM");
        runner.assertNotValid();
        runner.setProperty(USER_KEYTAB, "not.A.File");
        runner.assertNotValid();
        runner.setProperty(USER_KEYTAB, "src/test/resources/server.properties");
        runner.assertValid();
        runner.setVariable("keytab", "src/test/resources/server.properties");
        runner.setVariable("principal", "nifi@APACHE.COM");
        runner.setVariable("service", "kafka");
        runner.setProperty(USER_PRINCIPAL, "${principal}");
        runner.setProperty(USER_KEYTAB, "${keytab}s");
        runner.setProperty(JAAS_SERVICE_NAME, "${service}");
        runner.assertValid();
    }
}

