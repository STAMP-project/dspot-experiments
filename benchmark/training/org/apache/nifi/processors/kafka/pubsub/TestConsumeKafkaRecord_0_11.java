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


import ConsumeKafkaRecord_0_11.AUTO_OFFSET_RESET;
import ConsumeKafkaRecord_0_11.GROUP_ID;
import ConsumeKafkaRecord_0_11.OFFSET_EARLIEST;
import ConsumeKafkaRecord_0_11.TOPICS;
import ConsumeKafkaRecord_0_11.TOPIC_TYPE;
import ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import KafkaProcessorUtils.JAAS_SERVICE_NAME;
import KafkaProcessorUtils.SECURITY_PROTOCOL;
import KafkaProcessorUtils.SEC_SASL_PLAINTEXT;
import KafkaProcessorUtils.USER_KEYTAB;
import KafkaProcessorUtils.USER_PRINCIPAL;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestConsumeKafkaRecord_0_11 {
    private ConsumerLease mockLease = null;

    private ConsumerPool mockConsumerPool = null;

    private TestRunner runner;

    @Test
    public void validateCustomValidatorSettings() throws Exception {
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
    public void validateGetAllMessages() throws Exception {
        String groupName = "validateGetAllMessages";
        Mockito.when(mockConsumerPool.obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(mockLease);
        Mockito.when(mockLease.continuePolling()).thenReturn(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
        Mockito.when(mockLease.commit()).thenReturn(Boolean.TRUE);
        runner.setProperty(TOPICS, "foo,bar");
        runner.setProperty(GROUP_ID, groupName);
        runner.setProperty(AUTO_OFFSET_RESET, OFFSET_EARLIEST);
        runner.run(1, false);
        Mockito.verify(mockConsumerPool, Mockito.times(1)).obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
        Mockito.verify(mockLease, Mockito.times(3)).continuePolling();
        Mockito.verify(mockLease, Mockito.times(2)).poll();
        Mockito.verify(mockLease, Mockito.times(1)).commit();
        Mockito.verify(mockLease, Mockito.times(1)).close();
        Mockito.verifyNoMoreInteractions(mockConsumerPool);
        Mockito.verifyNoMoreInteractions(mockLease);
    }

    @Test
    public void validateGetAllMessagesPattern() throws Exception {
        String groupName = "validateGetAllMessagesPattern";
        Mockito.when(mockConsumerPool.obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(mockLease);
        Mockito.when(mockLease.continuePolling()).thenReturn(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
        Mockito.when(mockLease.commit()).thenReturn(Boolean.TRUE);
        runner.setProperty(TOPICS, "(fo.*)|(ba)");
        runner.setProperty(TOPIC_TYPE, "pattern");
        runner.setProperty(GROUP_ID, groupName);
        runner.setProperty(AUTO_OFFSET_RESET, OFFSET_EARLIEST);
        runner.run(1, false);
        Mockito.verify(mockConsumerPool, Mockito.times(1)).obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
        Mockito.verify(mockLease, Mockito.times(3)).continuePolling();
        Mockito.verify(mockLease, Mockito.times(2)).poll();
        Mockito.verify(mockLease, Mockito.times(1)).commit();
        Mockito.verify(mockLease, Mockito.times(1)).close();
        Mockito.verifyNoMoreInteractions(mockConsumerPool);
        Mockito.verifyNoMoreInteractions(mockLease);
    }

    @Test
    public void validateGetErrorMessages() throws Exception {
        String groupName = "validateGetErrorMessages";
        Mockito.when(mockConsumerPool.obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(mockLease);
        Mockito.when(mockLease.continuePolling()).thenReturn(true, false);
        Mockito.when(mockLease.commit()).thenReturn(Boolean.FALSE);
        runner.setProperty(TOPICS, "foo,bar");
        runner.setProperty(GROUP_ID, groupName);
        runner.setProperty(AUTO_OFFSET_RESET, OFFSET_EARLIEST);
        runner.run(1, false);
        Mockito.verify(mockConsumerPool, Mockito.times(1)).obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
        Mockito.verify(mockLease, Mockito.times(2)).continuePolling();
        Mockito.verify(mockLease, Mockito.times(1)).poll();
        Mockito.verify(mockLease, Mockito.times(1)).commit();
        Mockito.verify(mockLease, Mockito.times(1)).close();
        Mockito.verifyNoMoreInteractions(mockConsumerPool);
        Mockito.verifyNoMoreInteractions(mockLease);
    }

    @Test
    public void testJaasConfiguration() throws Exception {
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

