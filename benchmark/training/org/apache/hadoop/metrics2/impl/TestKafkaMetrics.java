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
package org.apache.hadoop.metrics2.impl;


import KafkaSink.BROKER_LIST;
import KafkaSink.TOPIC;
import com.google.common.collect.Lists;
import java.util.StringJoiner;
import java.util.concurrent.Future;
import org.apache.commons.configuration2.SubsetConfiguration;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricType;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.MetricsVisitor;
import org.apache.hadoop.metrics2.sink.KafkaSink;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This tests that the KafkaSink properly formats the Kafka message.
 */
public class TestKafkaMetrics {
    private static final Logger LOG = LoggerFactory.getLogger(TestKafkaMetrics.class);

    private KafkaSink kafkaSink;

    enum KafkaMetricsInfo implements MetricsInfo {

        KafkaMetrics("Kafka related metrics etc."),
        KafkaCounter("Kafka counter."),
        KafkaTag("Kafka tag.");
        // metrics
        private final String desc;

        KafkaMetricsInfo(String desc) {
            this.desc = desc;
        }

        @Override
        public String description() {
            return desc;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", ((this.getClass().getSimpleName()) + "{"), "}").add(("name=" + (name()))).add(("description=" + (desc))).toString();
        }
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testPutMetrics() throws Exception {
        // Create a record by mocking MetricsRecord class.
        MetricsRecord record = Mockito.mock(MetricsRecord.class);
        Mockito.when(record.tags()).thenReturn(Lists.newArrayList(new MetricsTag(TestKafkaMetrics.KafkaMetricsInfo.KafkaTag, "test_tag")));
        Mockito.when(record.timestamp()).thenReturn(System.currentTimeMillis());
        // Create a metric using AbstractMetric class.
        AbstractMetric metric = new AbstractMetric(TestKafkaMetrics.KafkaMetricsInfo.KafkaCounter) {
            @Override
            public Number value() {
                return new Integer(123);
            }

            @Override
            public MetricType type() {
                return null;
            }

            @Override
            public void visit(MetricsVisitor visitor) {
            }
        };
        // Create a list of metrics.
        Iterable<AbstractMetric> metrics = Lists.newArrayList(metric);
        Mockito.when(record.name()).thenReturn("Kafka record name");
        Mockito.when(record.metrics()).thenReturn(metrics);
        SubsetConfiguration conf = Mockito.mock(SubsetConfiguration.class);
        Mockito.when(conf.getString(BROKER_LIST)).thenReturn("localhost:9092");
        String topic = "myTestKafkaTopic";
        Mockito.when(conf.getString(TOPIC)).thenReturn(topic);
        // Create the KafkaSink object and initialize it.
        kafkaSink = new KafkaSink();
        kafkaSink.init(conf);
        // Create a mock KafkaProducer as a producer for KafkaSink.
        Producer<Integer, byte[]> mockProducer = Mockito.mock(KafkaProducer.class);
        kafkaSink.setProducer(mockProducer);
        // Create the json object from the record.
        StringBuilder jsonLines = recordToJson(record);
        if (TestKafkaMetrics.LOG.isDebugEnabled()) {
            TestKafkaMetrics.LOG.debug(("kafka message: " + (jsonLines.toString())));
        }
        // Send the record and store the result in a mock Future.
        Future<RecordMetadata> f = Mockito.mock(Future.class);
        Mockito.when(mockProducer.send(ArgumentMatchers.any())).thenReturn(f);
        kafkaSink.putMetrics(record);
        // Get the argument and verity it.
        ArgumentCaptor<ProducerRecord> argument = ArgumentCaptor.forClass(ProducerRecord.class);
        Mockito.verify(mockProducer).send(argument.capture());
        // Compare the received data with the original one.
        ProducerRecord<Integer, byte[]> data = argument.getValue();
        String jsonResult = new String(data.value());
        if (TestKafkaMetrics.LOG.isDebugEnabled()) {
            TestKafkaMetrics.LOG.debug(("kafka result: " + jsonResult));
        }
        Assert.assertEquals(jsonLines.toString(), jsonResult);
    }
}

