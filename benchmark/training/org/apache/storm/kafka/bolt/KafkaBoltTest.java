/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.kafka.bolt;


import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.KafkaException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KafkaBoltTest {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaBoltTest.class);

    @Test
    public void testSimple() {
        MockProducer<String, String> producer = new MockProducer(Cluster.empty(), false, null, null, null);
        KafkaBolt<String, String> bolt = makeBolt(producer);
        OutputCollector collector = Mockito.mock(OutputCollector.class);
        TopologyContext context = Mockito.mock(TopologyContext.class);
        Map<String, Object> conf = new HashMap<>();
        bolt.prepare(conf, context, collector);
        String key = "KEY";
        String value = "VALUE";
        Tuple testTuple = createTestTuple(key, value);
        bolt.execute(testTuple);
        MatcherAssert.assertThat(producer.history().size(), CoreMatchers.is(1));
        ProducerRecord<String, String> arg = producer.history().get(0);
        KafkaBoltTest.LOG.info("GOT {} ->", arg);
        KafkaBoltTest.LOG.info("{}, {}, {}", arg.topic(), arg.key(), arg.value());
        MatcherAssert.assertThat(arg.topic(), CoreMatchers.is("MY_TOPIC"));
        MatcherAssert.assertThat(arg.key(), CoreMatchers.is(key));
        MatcherAssert.assertThat(arg.value(), CoreMatchers.is(value));
        // Complete the send
        producer.completeNext();
        Mockito.verify(collector).ack(testTuple);
    }

    @Test
    public void testSimpleWithError() {
        MockProducer<String, String> producer = new MockProducer(Cluster.empty(), false, null, null, null);
        KafkaBolt<String, String> bolt = makeBolt(producer);
        OutputCollector collector = Mockito.mock(OutputCollector.class);
        TopologyContext context = Mockito.mock(TopologyContext.class);
        Map<String, Object> conf = new HashMap<>();
        bolt.prepare(conf, context, collector);
        String key = "KEY";
        String value = "VALUE";
        Tuple testTuple = createTestTuple(key, value);
        bolt.execute(testTuple);
        MatcherAssert.assertThat(producer.history().size(), CoreMatchers.is(1));
        ProducerRecord<String, String> arg = producer.history().get(0);
        KafkaBoltTest.LOG.info("GOT {} ->", arg);
        KafkaBoltTest.LOG.info("{}, {}, {}", arg.topic(), arg.key(), arg.value());
        MatcherAssert.assertThat(arg.topic(), CoreMatchers.is("MY_TOPIC"));
        MatcherAssert.assertThat(arg.key(), CoreMatchers.is(key));
        MatcherAssert.assertThat(arg.value(), CoreMatchers.is(value));
        // Force a send error
        KafkaException ex = new KafkaException();
        producer.errorNext(ex);
        Mockito.verify(collector).reportError(ex);
        Mockito.verify(collector).fail(testTuple);
    }

    @Test
    public void testCustomCallbackIsWrappedByDefaultCallbackBehavior() {
        MockProducer<String, String> producer = new MockProducer(Cluster.empty(), false, null, null, null);
        KafkaBolt<String, String> bolt = makeBolt(producer);
        PreparableCallback customCallback = Mockito.mock(PreparableCallback.class);
        bolt.withProducerCallback(customCallback);
        OutputCollector collector = Mockito.mock(OutputCollector.class);
        TopologyContext context = Mockito.mock(TopologyContext.class);
        Map<String, Object> topoConfig = new HashMap<>();
        bolt.prepare(topoConfig, context, collector);
        Mockito.verify(customCallback).prepare(topoConfig, context);
        String key = "KEY";
        String value = "VALUE";
        Tuple testTuple = createTestTuple(key, value);
        bolt.execute(testTuple);
        MatcherAssert.assertThat(producer.history().size(), CoreMatchers.is(1));
        ProducerRecord<String, String> arg = producer.history().get(0);
        KafkaBoltTest.LOG.info("GOT {} ->", arg);
        KafkaBoltTest.LOG.info("{}, {}, {}", arg.topic(), arg.key(), arg.value());
        MatcherAssert.assertThat(arg.topic(), CoreMatchers.is("MY_TOPIC"));
        MatcherAssert.assertThat(arg.key(), CoreMatchers.is(key));
        MatcherAssert.assertThat(arg.value(), CoreMatchers.is(value));
        // Force a send error
        KafkaException ex = new KafkaException();
        producer.errorNext(ex);
        Mockito.verify(customCallback).onCompletion(ArgumentMatchers.any(), ArgumentMatchers.eq(ex));
        Mockito.verify(collector).reportError(ex);
        Mockito.verify(collector).fail(testTuple);
    }
}

