/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.logging.kafka;


import com.linecorp.armeria.common.logging.RequestLog;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class KafkaAccessLogWriterTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String TOPIC_NAME = "topic-test";

    @Mock
    private Producer<String, String> producer;

    @Captor
    private ArgumentCaptor<ProducerRecord<String, String>> captor;

    @Test
    public void withoutKeyExtractor() {
        final RequestLog log = Mockito.mock(RequestLog.class);
        Mockito.when(log.authority()).thenReturn("kawamuray");
        final KafkaAccessLogWriter<String, String> service = new KafkaAccessLogWriter(producer, KafkaAccessLogWriterTest.TOPIC_NAME, RequestLog::authority);
        service.log(log);
        Mockito.verify(producer, VerificationModeFactory.times(1)).send(captor.capture(), ArgumentMatchers.any(Callback.class));
        final ProducerRecord<String, String> record = captor.getValue();
        assertThat(record.key()).isNull();
        assertThat(record.value()).isEqualTo("kawamuray");
    }

    @Test
    public void withKeyExtractor() {
        final RequestLog log = Mockito.mock(RequestLog.class);
        Mockito.when(log.authority()).thenReturn("kawamuray");
        Mockito.when(log.decodedPath()).thenReturn("kyuto");
        final KafkaAccessLogWriter<String, String> service = new KafkaAccessLogWriter(producer, KafkaAccessLogWriterTest.TOPIC_NAME, RequestLog::decodedPath, RequestLog::authority);
        service.log(log);
        Mockito.verify(producer, VerificationModeFactory.times(1)).send(captor.capture(), ArgumentMatchers.any(Callback.class));
        final ProducerRecord<String, String> record = captor.getValue();
        assertThat(record.key()).isEqualTo("kyuto");
        assertThat(record.value()).isEqualTo("kawamuray");
    }

    @Test
    public void closeProducerWhenRequested() {
        final KafkaAccessLogWriter<String, String> service = new KafkaAccessLogWriter(producer, KafkaAccessLogWriterTest.TOPIC_NAME, ( log) -> "");
        service.shutdown().join();
        Mockito.verify(producer, VerificationModeFactory.times(1)).close();
    }
}

