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
package zipkin2.collector.kafka;


import KafkaCollector.Builder;
import SpanBytesEncoder.JSON_V1;
import SpanBytesEncoder.JSON_V2;
import SpanBytesEncoder.PROTO3;
import com.github.charithe.kafka.EphemeralKafkaBroker;
import com.github.charithe.kafka.KafkaJunitRule;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.test.InstanceSpec;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.KafkaException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import zipkin2.Call;
import zipkin2.Callback;
import zipkin2.Span;
import zipkin2.TestObjects;
import zipkin2.collector.InMemoryCollectorMetrics;
import zipkin2.storage.SpanConsumer;
import zipkin2.storage.StorageComponent;


public class KafkaCollectorTest {
    static final int RANDOM_PORT = -1;

    static final EphemeralKafkaBroker broker = EphemeralKafkaBroker.create(KafkaCollectorTest.RANDOM_PORT, KafkaCollectorTest.RANDOM_PORT, KafkaCollectorTest.buildBrokerConfig());

    @Rule
    public KafkaJunitRule kafka = waitForStartup();

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    List<Span> spans = Arrays.asList(TestObjects.LOTS_OF_SPANS[0], TestObjects.LOTS_OF_SPANS[1]);

    InMemoryCollectorMetrics metrics = new InMemoryCollectorMetrics();

    InMemoryCollectorMetrics kafkaMetrics = metrics.forTransport("kafka");

    CopyOnWriteArraySet<Thread> threadsProvidingSpans = new CopyOnWriteArraySet<>();

    LinkedBlockingQueue<List<Span>> receivedSpans = new LinkedBlockingQueue<>();

    SpanConsumer consumer = ( spans) -> {
        threadsProvidingSpans.add(Thread.currentThread());
        receivedSpans.add(spans);
        return Call.create(null);
    };

    private KafkaProducer<byte[], byte[]> producer;

    @Test
    public void checkPasses() {
        try (KafkaCollector collector = builder("check_passes").build()) {
            assertThat(collector.check().ok()).isTrue();
        }
    }

    /**
     * Don't raise exception (crash process), rather fail status check! This allows the health check
     * to report the cause.
     */
    @Test
    public void check_failsOnInvalidBootstrapServers() throws Exception {
        KafkaCollector.Builder builder = builder("fail_invalid_bootstrap_servers").bootstrapServers("1.1.1.1");
        try (KafkaCollector collector = builder.build()) {
            collector.start();
            Thread.sleep(1000L);// wait for crash

            assertThat(collector.check().error()).isInstanceOf(KafkaException.class).hasMessage("Invalid url in bootstrap.servers: 1.1.1.1");
        }
    }

    /**
     * If the Kafka broker(s) specified in the connection string are not available, the Kafka consumer
     * library will attempt to reconnect indefinitely. The Kafka consumer will not throw an exception
     * and does not expose the status of its connection to the Kafka broker(s) in its API.
     *
     * An AdminClient API instance has been added to the connector to validate that connection with
     * Kafka is available in every health check. This AdminClient reuses Consumer's properties to
     * Connect to the cluster, and request a Cluster description to validate communication with Kafka.
     */
    @Test
    public void reconnectsIndefinitelyAndReportsUnhealthyWhenKafkaUnavailable() throws Exception {
        KafkaCollector.Builder builder = builder("fail_invalid_bootstrap_servers").bootstrapServers(("localhost:" + (InstanceSpec.getRandomPort())));
        try (KafkaCollector collector = builder.build()) {
            collector.start();
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            assertThat(collector.check().error()).isInstanceOf(TimeoutException.class);
        }
    }

    /**
     * Ensures legacy encoding works: a single TBinaryProtocol encoded span
     */
    @Test
    public void messageWithSingleThriftSpan() throws Exception {
        KafkaCollector.Builder builder = builder("single_span");
        byte[] bytes = THRIFT.encode(TestObjects.CLIENT_SPAN);
        produceSpans(bytes, builder.topic);
        try (KafkaCollector collector = builder.build()) {
            collector.start();
            assertThat(receivedSpans.take()).containsExactly(TestObjects.CLIENT_SPAN);
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
        KafkaCollector.Builder builder = builder("multiple_spans_thrift");
        byte[] bytes = THRIFT.encodeList(spans);
        produceSpans(bytes, builder.topic);
        try (KafkaCollector collector = builder.build()) {
            collector.start();
            assertThat(receivedSpans.take()).containsExactlyElementsOf(spans);
        }
        assertThat(kafkaMetrics.messages()).isEqualTo(1);
        assertThat(kafkaMetrics.bytes()).isEqualTo(bytes.length);
        assertThat(kafkaMetrics.spans()).isEqualTo(2);
    }

    /**
     * Ensures list encoding works: a json encoded list of spans
     */
    @Test
    public void messageWithMultipleSpans_json() throws Exception {
        KafkaCollector.Builder builder = builder("multiple_spans_json");
        byte[] bytes = JSON_V1.encodeList(spans);
        produceSpans(bytes, builder.topic);
        try (KafkaCollector collector = builder.build()) {
            collector.start();
            assertThat(receivedSpans.take()).containsExactlyElementsOf(spans);
        }
        assertThat(kafkaMetrics.messages()).isEqualTo(1);
        assertThat(kafkaMetrics.bytes()).isEqualTo(bytes.length);
        assertThat(kafkaMetrics.spans()).isEqualTo(2);
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
        KafkaCollector.Builder builder = builder("decoder_exception");
        produceSpans(THRIFT.encodeList(spans), builder.topic);
        produceSpans(new byte[0], builder.topic);
        produceSpans("[\"=\'".getBytes(), builder.topic);// screwed up json

        produceSpans("malformed".getBytes(), builder.topic);
        produceSpans(THRIFT.encodeList(spans), builder.topic);
        try (KafkaCollector collector = builder.build()) {
            collector.start();
            assertThat(receivedSpans.take()).containsExactlyElementsOf(spans);
            // the only way we could read this, is if the malformed spans were skipped.
            assertThat(receivedSpans.take()).containsExactlyElementsOf(spans);
        }
        assertThat(kafkaMetrics.messagesDropped()).isEqualTo(3);
    }

    /**
     * Guards against errors that leak from storage, such as InvalidQueryException
     */
    @Test
    public void skipsOnSpanConsumerException() throws Exception {
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
                    receivedSpans.add(spans);
                    callback.onSuccess(null);
                }
            }

            @Override
            public Call clone() {
                throw new AssertionError();
            }
        };
        final StorageComponent storage = buildStorage(consumer);
        KafkaCollector.Builder builder = builder("consumer_exception").storage(storage);
        produceSpans(THRIFT.encodeList(spans), builder.topic);
        produceSpans(THRIFT.encodeList(spans), builder.topic);// tossed on error

        produceSpans(THRIFT.encodeList(spans), builder.topic);
        try (KafkaCollector collector = builder.build()) {
            collector.start();
            assertThat(receivedSpans.take()).containsExactlyElementsOf(spans);
            // the only way we could read this, is if the malformed span was skipped.
            assertThat(receivedSpans.take()).containsExactlyElementsOf(spans);
        }
        assertThat(kafkaMetrics.spansDropped()).isEqualTo(spans.size());
    }

    @Test
    public void messagesDistributedAcrossMultipleThreadsSuccessfully() throws Exception {
        KafkaCollector.Builder builder = builder("multi_thread", 2);
        warmUpTopic(builder.topic);
        final byte[] traceBytes = THRIFT.encodeList(spans);
        try (KafkaCollector collector = builder.build()) {
            collector.start();
            waitForPartitionAssignments(collector);
            produceSpans(traceBytes, builder.topic, 0);
            assertThat(receivedSpans.take()).containsExactlyElementsOf(spans);
            produceSpans(traceBytes, builder.topic, 1);
            assertThat(receivedSpans.take()).containsExactlyElementsOf(spans);
        }
        assertThat(threadsProvidingSpans.size()).isEqualTo(2);
        assertThat(kafkaMetrics.messages()).isEqualTo(3);
        assertThat(kafkaMetrics.bytes()).isEqualTo(((traceBytes.length) * 2));
        assertThat(kafkaMetrics.spans()).isEqualTo(((spans.size()) * 2));
    }

    @Test
    public void multipleTopicsCommaDelimited() {
        try (KafkaCollector collector = builder("topic1,topic2").build()) {
            collector.start();
            assertThat(collector.kafkaWorkers.workers.get(0).topics).containsExactly("topic1", "topic2");
        }
    }
}

