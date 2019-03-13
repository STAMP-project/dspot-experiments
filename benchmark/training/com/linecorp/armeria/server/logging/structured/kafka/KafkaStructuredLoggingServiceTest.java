/**
 * Copyright 2016 LINE Corporation
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
package com.linecorp.armeria.server.logging.structured.kafka;


import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Service;
import javax.annotation.Nullable;
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


public class KafkaStructuredLoggingServiceTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String TOPIC_NAME = "topic-test";

    private static final class SimpleStructuredLog {
        private final String name;

        private SimpleStructuredLog(String name) {
            this.name = name;
        }
    }

    @Mock
    private static Service<HttpRequest, HttpResponse> service;

    private static class KafkaStructuredLoggingServiceExposed extends KafkaStructuredLoggingService<HttpRequest, HttpResponse, KafkaStructuredLoggingServiceTest.SimpleStructuredLog> {
        KafkaStructuredLoggingServiceExposed(Producer<byte[], KafkaStructuredLoggingServiceTest.SimpleStructuredLog> producer, @Nullable
        KeySelector<KafkaStructuredLoggingServiceTest.SimpleStructuredLog> keySelector, boolean needToCloseProducer) {
            super(KafkaStructuredLoggingServiceTest.service, ( log) -> null, producer, KafkaStructuredLoggingServiceTest.TOPIC_NAME, keySelector, needToCloseProducer);
        }
    }

    @Mock
    private Producer<byte[], KafkaStructuredLoggingServiceTest.SimpleStructuredLog> producer;

    @Captor
    private ArgumentCaptor<ProducerRecord<byte[], KafkaStructuredLoggingServiceTest.SimpleStructuredLog>> captor;

    @Test
    public void testServiceWithoutKeySelector() {
        final KafkaStructuredLoggingServiceTest.KafkaStructuredLoggingServiceExposed service = new KafkaStructuredLoggingServiceTest.KafkaStructuredLoggingServiceExposed(producer, null, false);
        final KafkaStructuredLoggingServiceTest.SimpleStructuredLog log = new KafkaStructuredLoggingServiceTest.SimpleStructuredLog("kawamuray");
        writeLog(null, log);
        Mockito.verify(producer, VerificationModeFactory.times(1)).send(captor.capture(), ArgumentMatchers.any(Callback.class));
        final ProducerRecord<byte[], KafkaStructuredLoggingServiceTest.SimpleStructuredLog> record = captor.getValue();
        assertThat(record.key()).isNull();
        assertThat(record.value()).isEqualTo(log);
    }

    @Test
    public void testWithKeySelector() {
        final KafkaStructuredLoggingServiceTest.KafkaStructuredLoggingServiceExposed service = new KafkaStructuredLoggingServiceTest.KafkaStructuredLoggingServiceExposed(producer, ( res, log) -> log.name.getBytes(), false);
        final KafkaStructuredLoggingServiceTest.SimpleStructuredLog log = new KafkaStructuredLoggingServiceTest.SimpleStructuredLog("kawamuray");
        writeLog(null, log);
        Mockito.verify(producer, VerificationModeFactory.times(1)).send(captor.capture(), ArgumentMatchers.any(Callback.class));
        final ProducerRecord<byte[], KafkaStructuredLoggingServiceTest.SimpleStructuredLog> record = captor.getValue();
        assertThat(record.key()).isNotNull();
        assertThat(new String(record.key())).isEqualTo(log.name);
        assertThat(record.value()).isEqualTo(log);
    }

    @Test
    public void testCloseProducerWhenRequested() {
        final KafkaStructuredLoggingServiceTest.KafkaStructuredLoggingServiceExposed service = new KafkaStructuredLoggingServiceTest.KafkaStructuredLoggingServiceExposed(producer, null, true);
        close();
        close();
    }

    @Test
    public void testDoNotCloseProducerWhenNotRequested() {
        final KafkaStructuredLoggingServiceTest.KafkaStructuredLoggingServiceExposed service = new KafkaStructuredLoggingServiceTest.KafkaStructuredLoggingServiceExposed(producer, null, false);
        close();
        close();
    }
}

