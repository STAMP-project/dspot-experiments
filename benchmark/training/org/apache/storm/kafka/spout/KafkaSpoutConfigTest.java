/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.apache.storm.kafka.spout;


import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST;
import KafkaSpoutConfig.DEFAULT_METRICS_TIME_BUCKET_SIZE_SECONDS;
import KafkaSpoutConfig.ProcessingGuarantee.AT_MOST_ONCE;
import java.util.HashMap;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class KafkaSpoutConfigTest {
    @Test
    public void testBasic() {
        KafkaSpoutConfig<String, String> conf = KafkaSpoutConfig.builder("localhost:1234", "topic").build();
        Assertions.assertEquals(conf.getFirstPollOffsetStrategy(), UNCOMMITTED_EARLIEST);
        Assertions.assertNull(conf.getConsumerGroupId());
        Assertions.assertTrue(((conf.getTranslator()) instanceof DefaultRecordTranslator));
        HashMap<String, Object> expected = new HashMap<>();
        expected.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:1234");
        expected.put(ENABLE_AUTO_COMMIT_CONFIG, false);
        expected.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        expected.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        expected.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        Assertions.assertEquals(conf.getKafkaProps(), expected);
        Assertions.assertEquals(conf.getMetricsTimeBucketSizeInSecs(), DEFAULT_METRICS_TIME_BUCKET_SIZE_SECONDS);
    }

    @Test
    public void testSetEmitNullTuplesToTrue() {
        final KafkaSpoutConfig<String, String> conf = KafkaSpoutConfig.builder("localhost:1234", "topic").setEmitNullTuples(true).build();
        Assertions.assertTrue(conf.isEmitNullTuples(), "Failed to set emit null tuples to true");
    }

    @Test
    public void testShouldNotChangeAutoOffsetResetPolicyWhenNotUsingAtLeastOnce() {
        KafkaSpoutConfig<String, String> conf = KafkaSpoutConfig.builder("localhost:1234", "topic").setProcessingGuarantee(AT_MOST_ONCE).build();
        MatcherAssert.assertThat("When at-least-once is not specified, the spout should use the Kafka default auto offset reset policy", conf.getKafkaProps().get(AUTO_OFFSET_RESET_CONFIG), CoreMatchers.nullValue());
    }

    @Test
    public void testWillRespectExplicitAutoOffsetResetPolicy() {
        KafkaSpoutConfig<String, String> conf = KafkaSpoutConfig.builder("localhost:1234", "topic").setProp(AUTO_OFFSET_RESET_CONFIG, "none").build();
        MatcherAssert.assertThat("Should allow users to pick a different auto offset reset policy than the one recommended for the at-least-once processing guarantee", conf.getKafkaProps().get(AUTO_OFFSET_RESET_CONFIG), CoreMatchers.is("none"));
    }

    @Test
    public void testMetricsTimeBucketSizeInSecs() {
        KafkaSpoutConfig<String, String> conf = KafkaSpoutConfig.builder("localhost:1234", "topic").setMetricsTimeBucketSizeInSecs(100).build();
        Assertions.assertEquals(conf.getMetricsTimeBucketSizeInSecs(), 100);
    }

    @Test
    public void testThrowsIfEnableAutoCommitIsSet() {
        Assertions.assertThrows(IllegalStateException.class, () -> KafkaSpoutConfig.builder("localhost:1234", "topic").setProp(ENABLE_AUTO_COMMIT_CONFIG, true).build());
    }
}

