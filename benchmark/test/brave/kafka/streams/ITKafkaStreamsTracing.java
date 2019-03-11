package brave.kafka.streams;


import brave.Span.Kind.CONSUMER;
import brave.Span.Kind.PRODUCER;
import brave.Tracing;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import com.github.charithe.kafka.EphemeralKafkaBroker;
import com.github.charithe.kafka.KafkaJunitRule;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import zipkin2.Annotation;
import zipkin2.Span;


public class ITKafkaStreamsTracing {
    @ClassRule
    public static KafkaJunitRule kafka = new KafkaJunitRule(EphemeralKafkaBroker.create());

    @Rule
    public TestName testName = new TestName();

    String TEST_KEY = "foo";

    String TEST_VALUE = "bar";

    BlockingQueue<Span> spans = new LinkedBlockingQueue<>();

    @Rule
    public TestRule assertSpansEmpty = new TestWatcher() {
        // only check success path to avoid masking assertion errors or exceptions
        @Override
        protected void succeeded(Description description) {
            try {
                assertThat(spans.poll(100, TimeUnit.MILLISECONDS)).withFailMessage("Stream span remaining in queue. Check for redundant reporting").isNull();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    Tracing tracing = Tracing.newBuilder().localServiceName("streams-app").currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).build()).spanReporter(spans::add).build();

    KafkaStreamsTracing kafkaStreamsTracing = KafkaStreamsTracing.create(tracing);

    Producer<String, String> producer;

    Consumer<String, String> consumer;

    @Test
    public void should_create_span_from_stream_input_topic() throws Exception {
        String inputTopic = (testName.getMethodName()) + "-input";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic).foreach(( k, v) -> {
        });
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span span = takeSpan();
        assertThat(span.tags()).containsEntry("kafka.topic", inputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_input_and_output_topics() throws Exception {
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic).to(outputTopic);
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        consumer = createTracingConsumer(outputTopic);
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanOutput = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanOutput.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        assertThat(spanOutput.tags()).containsEntry("kafka.topic", outputTopic);
        assertThat(spanOutput.kind().name()).isEqualTo(PRODUCER.name());
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_processor() throws Exception {
        ProcessorSupplier<String, String> processorSupplier = kafkaStreamsTracing.processor("forward-1", new org.apache.kafka.streams.processor.AbstractProcessor<String, String>() {
            @Override
            public void process(String key, String value) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        String inputTopic = (testName.getMethodName()) + "-input";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).process(processorSupplier);
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_peek() throws Exception {
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        long now = System.currentTimeMillis();
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transform(kafkaStreamsTracing.peek("peek-1", ( key, value) -> {
            try {
                Thread.sleep(100L);
            } catch ( e) {
                e.printStackTrace();
            }
            tracing.tracer().currentSpan().annotate(now, "test");
        })).to(outputTopic);
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        Span spanOutput = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanProcessor.traceId()).isEqualTo(spanOutput.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        assertThat(spanProcessor.annotations()).contains(Annotation.create(now, "test"));
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_mark() throws Exception {
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transform(kafkaStreamsTracing.mark("mark-1")).to(outputTopic);
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        Span spanOutput = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanProcessor.traceId()).isEqualTo(spanOutput.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_foreach() throws Exception {
        String inputTopic = (testName.getMethodName()) + "-input";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).process(kafkaStreamsTracing.foreach("foreach-1", ( key, value) -> {
            try {
                Thread.sleep(100L);
            } catch ( e) {
                e.printStackTrace();
            }
        }));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_without_tracing_and_tracing_processor() throws Exception {
        ProcessorSupplier<String, String> processorSupplier = kafkaStreamsTracing.processor("forward-1", new org.apache.kafka.streams.processor.AbstractProcessor<String, String>() {
            @Override
            public void process(String key, String value) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        String inputTopic = (testName.getMethodName()) + "-input";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).process(processorSupplier);
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreamsWithoutTracing(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanProcessor = takeSpan();
        assertThat(spanProcessor.tags().size()).isEqualTo(2);
        assertThat(spanProcessor.tags()).containsKeys("kafka.streams.application.id", "kafka.streams.task.id");
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_transformer() throws Exception {
        TransformerSupplier<String, String, KeyValue<String, String>> transformerSupplier = kafkaStreamsTracing.transformer("transformer-1", new org.apache.kafka.streams.kstream.Transformer<String, String, KeyValue<String, String>>() {
            ProcessorContext context;

            @Override
            public void init(ProcessorContext context) {
                this.context = context;
            }

            @Override
            public KeyValue<String, String> transform(String key, String value) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return KeyValue.pair(key, value);
            }

            @Override
            public void close() {
            }
        });
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transform(transformerSupplier).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        Span spanOutput = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanProcessor.traceId()).isEqualTo(spanOutput.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        assertThat(spanOutput.tags()).containsEntry("kafka.topic", outputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_without_tracing_with_tracing_transformer() throws Exception {
        TransformerSupplier<String, String, KeyValue<String, String>> transformerSupplier = kafkaStreamsTracing.transformer("transformer-1", new org.apache.kafka.streams.kstream.Transformer<String, String, KeyValue<String, String>>() {
            ProcessorContext context;

            @Override
            public void init(ProcessorContext context) {
                this.context = context;
            }

            @Override
            public KeyValue<String, String> transform(String key, String value) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return KeyValue.pair(key, value);
            }

            @Override
            public void close() {
            }
        });
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transform(transformerSupplier).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreamsWithoutTracing(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanProcessor = takeSpan();
        assertThat(spanProcessor.tags().size()).isEqualTo(2);
        assertThat(spanProcessor.tags()).containsKeys("kafka.streams.application.id", "kafka.streams.task.id");
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_valueTransformer() throws Exception {
        ValueTransformerSupplier<String, String> transformerSupplier = kafkaStreamsTracing.valueTransformer("transformer-1", new org.apache.kafka.streams.kstream.ValueTransformer<String, String>() {
            ProcessorContext context;

            @Override
            public void init(ProcessorContext context) {
                this.context = context;
            }

            @Override
            public String transform(String value) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return value;
            }

            @Override
            public void close() {
            }
        });
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transformValues(transformerSupplier).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        Span spanOutput = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanProcessor.traceId()).isEqualTo(spanOutput.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        assertThat(spanOutput.tags()).containsEntry("kafka.topic", outputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_map() throws Exception {
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transform(kafkaStreamsTracing.map("map-1", ( key, value) -> {
            try {
                Thread.sleep(100L);
            } catch ( e) {
                e.printStackTrace();
            }
            return KeyValue.pair(key, value);
        })).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        Span spanOutput = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanProcessor.traceId()).isEqualTo(spanOutput.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        assertThat(spanOutput.tags()).containsEntry("kafka.topic", outputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_mapValues() throws Exception {
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transformValues(kafkaStreamsTracing.mapValues("mapValue-1", ( value) -> {
            try {
                Thread.sleep(100L);
            } catch ( e) {
                e.printStackTrace();
            }
            return value;
        })).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        Span spanOutput = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanProcessor.traceId()).isEqualTo(spanOutput.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        assertThat(spanOutput.tags()).containsEntry("kafka.topic", outputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_mapValues_withKey() throws Exception {
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transformValues(kafkaStreamsTracing.mapValues("mapValue-1", ( key, value) -> {
            try {
                Thread.sleep(100L);
            } catch ( e) {
                e.printStackTrace();
            }
            return value;
        })).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        Span spanOutput = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanProcessor.traceId()).isEqualTo(spanOutput.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        assertThat(spanOutput.tags()).containsEntry("kafka.topic", outputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_without_tracing_with_tracing_valueTransformer() throws Exception {
        ValueTransformerSupplier<String, String> transformerSupplier = kafkaStreamsTracing.valueTransformer("transformer-1", new org.apache.kafka.streams.kstream.ValueTransformer<String, String>() {
            ProcessorContext context;

            @Override
            public void init(ProcessorContext context) {
                this.context = context;
            }

            @Override
            public String transform(String value) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return value;
            }

            @Override
            public void close() {
            }
        });
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transformValues(transformerSupplier).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreamsWithoutTracing(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanProcessor = takeSpan();
        assertThat(spanProcessor.tags().size()).isEqualTo(2);
        assertThat(spanProcessor.tags()).containsKeys("kafka.streams.application.id", "kafka.streams.task.id");
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_with_tracing_valueTransformerWithKey() throws Exception {
        ValueTransformerWithKeySupplier<String, String, String> transformerSupplier = kafkaStreamsTracing.valueTransformerWithKey("transformer-1", new org.apache.kafka.streams.kstream.ValueTransformerWithKey<String, String, String>() {
            ProcessorContext context;

            @Override
            public void init(ProcessorContext context) {
                this.context = context;
            }

            @Override
            public String transform(String key, String value) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return value;
            }

            @Override
            public void close() {
            }
        });
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transformValues(transformerSupplier).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreams(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanInput = takeSpan();
        Span spanProcessor = takeSpan();
        Span spanOutput = takeSpan();
        assertThat(spanInput.kind().name()).isEqualTo(CONSUMER.name());
        assertThat(spanInput.traceId()).isEqualTo(spanProcessor.traceId());
        assertThat(spanProcessor.traceId()).isEqualTo(spanOutput.traceId());
        assertThat(spanInput.tags()).containsEntry("kafka.topic", inputTopic);
        assertThat(spanOutput.tags()).containsEntry("kafka.topic", outputTopic);
        streams.close();
        streams.cleanUp();
    }

    @Test
    public void should_create_spans_from_stream_without_tracing_with_tracing_valueTransformerWithKey() throws Exception {
        ValueTransformerWithKeySupplier<String, String, String> transformerSupplier = kafkaStreamsTracing.valueTransformerWithKey("transformer-1", new org.apache.kafka.streams.kstream.ValueTransformerWithKey<String, String, String>() {
            ProcessorContext context;

            @Override
            public void init(ProcessorContext context) {
                this.context = context;
            }

            @Override
            public String transform(String key, String value) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return value;
            }

            @Override
            public void close() {
            }
        });
        String inputTopic = (testName.getMethodName()) + "-input";
        String outputTopic = (testName.getMethodName()) + "-output";
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String())).transformValues(transformerSupplier).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        Topology topology = builder.build();
        KafkaStreams streams = buildKafkaStreamsWithoutTracing(topology);
        producer = createTracingProducer();
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(inputTopic, TEST_KEY, TEST_VALUE)).get();
        waitForStreamToRun(streams);
        Span spanProcessor = takeSpan();
        assertThat(spanProcessor.tags().size()).isEqualTo(2);
        assertThat(spanProcessor.tags()).containsKeys("kafka.streams.application.id", "kafka.streams.task.id");
        streams.close();
        streams.cleanUp();
    }
}

