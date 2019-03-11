/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.collector.kafka08;


import SpanBytesEncoder.JSON_V1;
import SpanBytesEncoder.JSON_V2;
import SpanBytesEncoder.PROTO3;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import kafka.javaapi.producer.Producer;
import org.I0Itec.zkclient.exception.ZkTimeoutException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import zipkin2.Call;
import zipkin2.Callback;
import zipkin2.Span;
import zipkin2.TestObjects;
import zipkin2.collector.InMemoryCollectorMetrics;
import zipkin2.collector.kafka08.KafkaCollector.Builder;
import zipkin2.storage.SpanConsumer;


public class KafkaCollectorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @ClassRule
    public static Timeout globalTimeout = Timeout.seconds(20);

    List<Span> spans = Arrays.asList(TestObjects.LOTS_OF_SPANS[0], TestObjects.LOTS_OF_SPANS[1]);

    Producer<String, byte[]> producer = KafkaTestGraph.INSTANCE.producer();

    InMemoryCollectorMetrics metrics = new InMemoryCollectorMetrics();

    InMemoryCollectorMetrics kafkaMetrics = metrics.forTransport("kafka");

    LinkedBlockingQueue<List<Span>> recvdSpans = new LinkedBlockingQueue<>();

    SpanConsumer consumer = ( spans) -> {
        recvdSpans.add(spans);
        return Call.create(null);
    };

    @Test
    public void checkPasses() {
        try (KafkaCollector collector = newKafkaTransport(builder("check_passes"), consumer)) {
            assertThat(collector.check().ok()).isTrue();
        }
    }

    @Test
    public void start_failsOnInvalidZooKeeper() {
        thrown.expect(ZkTimeoutException.class);
        thrown.expectMessage("Unable to connect to zookeeper server within timeout: 6000");
        Builder builder = builder("fail_invalid_zk").zookeeper("1.1.1.1");
        try (KafkaCollector collector = newKafkaTransport(builder, consumer)) {
        }
    }

    @Test
    public void canSetMaxMessageSize() {
        Builder builder = builder("max_message").maxMessageSize(1);
        try (KafkaCollector collector = newKafkaTransport(builder, consumer)) {
            assertThat(collector.connector.get().config().fetchMessageMaxBytes()).isEqualTo(1);
        }
    }

    /**
     * Ensures legacy encoding works: a single TBinaryProtocol encoded span
     */
    @Test
    public void messageWithSingleThriftSpan() throws Exception {
        Builder builder = builder("single_span");
        byte[] bytes = THRIFT.encode(TestObjects.CLIENT_SPAN);
        producer.send(new kafka.producer.KeyedMessage(builder.topic, bytes));
        try (KafkaCollector collector = newKafkaTransport(builder, consumer)) {
            assertThat(recvdSpans.take()).containsExactly(TestObjects.CLIENT_SPAN);
        }
        assertThat(kafkaMetrics.messages()).isEqualTo(1);
        assertThat(kafkaMetrics.bytes()).isEqualTo(bytes.length);
        assertThat(kafkaMetrics.spans()).isEqualTo(1);
    }

    /**
     * Ensures list encoding works: a TBinaryProtocol encoded list of spans
     */
    @Test
    public void messageWithMultipleSpans_thrift() throws Exception {
        Builder builder = builder("multiple_spans_thrift");
        byte[] bytes = THRIFT.encodeList(spans);
        producer.send(new kafka.producer.KeyedMessage(builder.topic, bytes));
        try (KafkaCollector collector = newKafkaTransport(builder, consumer)) {
            assertThat(recvdSpans.take()).containsExactlyElementsOf(spans);
        }
        assertThat(kafkaMetrics.messages()).isEqualTo(1);
        assertThat(kafkaMetrics.bytes()).isEqualTo(bytes.length);
        assertThat(kafkaMetrics.spans()).isEqualTo(spans.size());
    }

    /**
     * Ensures list encoding works: a json encoded list of spans
     */
    @Test
    public void messageWithMultipleSpans_json() throws Exception {
        Builder builder = builder("multiple_spans_json");
        byte[] bytes = JSON_V1.encodeList(spans);
        producer.send(new kafka.producer.KeyedMessage(builder.topic, bytes));
        try (KafkaCollector collector = newKafkaTransport(builder, consumer)) {
            assertThat(recvdSpans.take()).containsExactlyElementsOf(spans);
        }
        assertThat(kafkaMetrics.messages()).isEqualTo(1);
        assertThat(kafkaMetrics.bytes()).isEqualTo(bytes.length);
        assertThat(kafkaMetrics.spans()).isEqualTo(spans.size());
    }

    /**
     * Ensures list encoding works: a version 2 json list of spans
     */
    @Test
    public void messageWithMultipleSpans_json2() throws Exception {
        messageWithMultipleSpans(builder("multiple_spans_json2"), JSON_V2);
    }

    /**
     * Ensures list encoding works: proto3 ListOfSpans
     */
    @Test
    public void messageWithMultipleSpans_proto3() throws Exception {
        messageWithMultipleSpans(builder("multiple_spans_proto3"), PROTO3);
    }

    /**
     * Ensures malformed spans don't hang the collector
     */
    @Test
    public void skipsMalformedData() throws Exception {
        Builder builder = builder("decoder_exception");
        producer.send(new kafka.producer.KeyedMessage(builder.topic, THRIFT.encodeList(spans)));
        producer.send(new kafka.producer.KeyedMessage(builder.topic, new byte[0]));
        producer.send(new kafka.producer.KeyedMessage(builder.topic, "[\"=\'".getBytes()));// screwed up json

        producer.send(new kafka.producer.KeyedMessage(builder.topic, "malformed".getBytes()));
        producer.send(new kafka.producer.KeyedMessage(builder.topic, THRIFT.encodeList(spans)));
        try (KafkaCollector collector = newKafkaTransport(builder, consumer)) {
            assertThat(recvdSpans.take()).containsExactlyElementsOf(spans);
            // the only way we could read this, is if the malformed spans were skipped.
            assertThat(recvdSpans.take()).containsExactlyElementsOf(spans);
        }
        assertThat(kafkaMetrics.messagesDropped()).isEqualTo(3);
    }

    /**
     * Guards against errors that leak from storage, such as InvalidQueryException
     */
    @Test
    public void skipsOnConsumerException() throws Exception {
        Builder builder = builder("consumer_exception");
        AtomicInteger counter = new AtomicInteger();
        consumer = ( input) -> new Call.Base<Void>() {
            @Override
            protected Void doExecute() {
                throw new AssertionError();
            }

            @Override
            protected void doEnqueue(Callback callback) {
                if ((counter.getAndIncrement()) == 1) {
                    callback.onError(new RuntimeException("storage fell over"));
                } else {
                    recvdSpans.add(spans);
                    callback.onSuccess(null);
                }
            }

            @Override
            public Call clone() {
                throw new AssertionError();
            }
        };
        producer.send(new kafka.producer.KeyedMessage(builder.topic, THRIFT.encodeList(spans)));
        producer.send(new kafka.producer.KeyedMessage(builder.topic, THRIFT.encodeList(spans)));// tossed on error

        producer.send(new kafka.producer.KeyedMessage(builder.topic, THRIFT.encodeList(spans)));
        try (KafkaCollector collector = newKafkaTransport(builder, consumer)) {
            assertThat(recvdSpans.take()).containsExactlyElementsOf(spans);
            // the only way we could read this, is if the malformed span was skipped.
            assertThat(recvdSpans.take()).containsExactlyElementsOf(spans);
        }
        assertThat(kafkaMetrics.spansDropped()).isEqualTo(spans.size());
    }
}

