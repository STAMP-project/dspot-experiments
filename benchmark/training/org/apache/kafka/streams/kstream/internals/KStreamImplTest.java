/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream.internals;


import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.TopologyWrapper;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.FailOnInvalidTimestamp;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.apache.kafka.streams.processor.internals.ProcessorTopology;
import org.apache.kafka.streams.processor.internals.SourceNode;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.test.MockMapper;
import org.apache.kafka.test.MockProcessor;
import org.apache.kafka.test.MockProcessorSupplier;
import org.apache.kafka.test.MockValueJoiner;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class KStreamImplTest {
    private final Consumed<String, String> stringConsumed = Consumed.with(Serdes.String(), Serdes.String());

    private final MockProcessorSupplier<String, String> processorSupplier = new MockProcessorSupplier<>();

    private KStream<String, String> testStream;

    private StreamsBuilder builder;

    private final ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory(new StringSerializer(), new StringSerializer());

    private final Properties props = StreamsTestUtils.getStreamsConfig(Serdes.String(), Serdes.String());

    private Serde<String> mySerde = new Serdes.StringSerde();

    @Test
    public void testNumProcesses() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> source1 = builder.stream(Arrays.asList("topic-1", "topic-2"), stringConsumed);
        final KStream<String, String> source2 = builder.stream(Arrays.asList("topic-3", "topic-4"), stringConsumed);
        final KStream<String, String> stream1 = source1.filter(new org.apache.kafka.streams.kstream.Predicate<String, String>() {
            @Override
            public boolean test(final String key, final String value) {
                return true;
            }
        }).filterNot(new org.apache.kafka.streams.kstream.Predicate<String, String>() {
            @Override
            public boolean test(final String key, final String value) {
                return false;
            }
        });
        final KStream<String, Integer> stream2 = stream1.mapValues(new org.apache.kafka.streams.kstream.ValueMapper<String, Integer>() {
            @Override
            public Integer apply(final String value) {
                return new Integer(value);
            }
        });
        final KStream<String, Integer> stream3 = source2.flatMapValues(new org.apache.kafka.streams.kstream.ValueMapper<String, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(final String value) {
                return Collections.singletonList(new Integer(value));
            }
        });
        final KStream<String, Integer>[] streams2 = stream2.branch(new org.apache.kafka.streams.kstream.Predicate<String, Integer>() {
            @Override
            public boolean test(final String key, final Integer value) {
                return (value % 2) == 0;
            }
        }, new org.apache.kafka.streams.kstream.Predicate<String, Integer>() {
            @Override
            public boolean test(final String key, final Integer value) {
                return true;
            }
        });
        final KStream<String, Integer>[] streams3 = stream3.branch(new org.apache.kafka.streams.kstream.Predicate<String, Integer>() {
            @Override
            public boolean test(final String key, final Integer value) {
                return (value % 2) == 0;
            }
        }, new org.apache.kafka.streams.kstream.Predicate<String, Integer>() {
            @Override
            public boolean test(final String key, final Integer value) {
                return true;
            }
        });
        final int anyWindowSize = 1;
        final Joined<String, Integer, Integer> joined = Joined.with(Serdes.String(), Serdes.Integer(), Serdes.Integer());
        final KStream<String, Integer> stream4 = streams2[0].join(streams3[0], new org.apache.kafka.streams.kstream.ValueJoiner<Integer, Integer, Integer>() {
            @Override
            public Integer apply(final Integer value1, final Integer value2) {
                return value1 + value2;
            }
        }, JoinWindows.of(Duration.ofMillis(anyWindowSize)), joined);
        streams2[1].join(streams3[1], new org.apache.kafka.streams.kstream.ValueJoiner<Integer, Integer, Integer>() {
            @Override
            public Integer apply(final Integer value1, final Integer value2) {
                return value1 + value2;
            }
        }, JoinWindows.of(Duration.ofMillis(anyWindowSize)), joined);
        stream4.to("topic-5");
        streams2[1].through("topic-6").process(new MockProcessorSupplier<String, Integer>());
        // process
        Assert.assertEquals((((((((((((2// sources
         + 2)// stream1
         + 1)// stream2
         + 1)// stream3
         + 1) + 2)// streams2
         + 1) + 2)// streams3
         + (5 * 2))// stream2-stream3 joins
         + 1)// to
         + 2)// through
         + 1), TopologyWrapper.getInternalTopologyBuilder(builder.build()).setApplicationId("X").build(null).processors().size());
    }

    @Test
    public void shouldPreserveSerdesForOperators() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> stream1 = builder.stream(Collections.singleton("topic-1"), stringConsumed);
        final KTable<String, String> table1 = builder.table("topic-2", stringConsumed);
        final GlobalKTable<String, String> table2 = builder.globalTable("topic-2", stringConsumed);
        final ConsumedInternal<String, String> consumedInternal = new ConsumedInternal(stringConsumed);
        final KeyValueMapper<String, String, String> selector = ( key, value) -> key;
        final KeyValueMapper<String, String, Iterable<KeyValue<String, String>>> flatSelector = ( key, value) -> Collections.singleton(new KeyValue<>(key, value));
        final org.apache.kafka.streams.kstream.ValueMapper<String, String> mapper = ( value) -> value;
        final org.apache.kafka.streams.kstream.ValueMapper<String, Iterable<String>> flatMapper = Collections::singleton;
        final org.apache.kafka.streams.kstream.ValueJoiner<String, String, String> joiner = ( value1, value2) -> value1;
        final TransformerSupplier<String, String, KeyValue<String, String>> transformerSupplier = () -> new Transformer<String, String, KeyValue<String, String>>() {
            @Override
            public void init(final ProcessorContext context) {
            }

            @Override
            public KeyValue<String, String> transform(final String key, final String value) {
                return new KeyValue<>(key, value);
            }

            @Override
            public void close() {
            }
        };
        final ValueTransformerSupplier<String, String> valueTransformerSupplier = () -> new ValueTransformer<String, String>() {
            @Override
            public void init(final ProcessorContext context) {
            }

            @Override
            public String transform(final String value) {
                return value;
            }

            @Override
            public void close() {
            }
        };
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertEquals(valueSerde(), consumedInternal.valueSerde());
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertEquals(valueSerde(), consumedInternal.valueSerde());
        Assert.assertNull(keySerde());
        Assert.assertEquals(valueSerde(), consumedInternal.valueSerde());
        Assert.assertNull(keySerde());
        Assert.assertNull(valueSerde());
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertNull(valueSerde());
        Assert.assertNull(keySerde());
        Assert.assertNull(valueSerde());
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertNull(valueSerde());
        Assert.assertNull(keySerde());
        Assert.assertNull(valueSerde());
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertNull(valueSerde());
        Assert.assertNull(keySerde());
        Assert.assertNull(valueSerde());
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertEquals(valueSerde(), consumedInternal.valueSerde());
        Assert.assertEquals(keySerde(), mySerde);
        Assert.assertEquals(valueSerde(), mySerde);
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertEquals(valueSerde(), consumedInternal.valueSerde());
        Assert.assertEquals(keySerde(), mySerde);
        Assert.assertEquals(valueSerde(), mySerde);
        Assert.assertEquals(keySerde(), null);
        Assert.assertEquals(valueSerde(), consumedInternal.valueSerde());
        Assert.assertEquals(keySerde(), mySerde);
        Assert.assertEquals(valueSerde(), mySerde);
        Assert.assertEquals(keySerde(), null);
        Assert.assertEquals(valueSerde(), null);
        Assert.assertEquals(keySerde(), mySerde);
        Assert.assertNull(valueSerde());
        Assert.assertEquals(keySerde(), null);
        Assert.assertEquals(valueSerde(), null);
        Assert.assertEquals(keySerde(), mySerde);
        Assert.assertNull(valueSerde());
        Assert.assertEquals(keySerde(), null);
        Assert.assertEquals(valueSerde(), null);
        Assert.assertEquals(keySerde(), mySerde);
        Assert.assertNull(valueSerde());
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertEquals(valueSerde(), null);
        Assert.assertEquals(keySerde(), mySerde);
        Assert.assertEquals(valueSerde(), null);
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertEquals(valueSerde(), null);
        Assert.assertEquals(keySerde(), mySerde);
        Assert.assertEquals(valueSerde(), null);
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertEquals(valueSerde(), null);
        Assert.assertEquals(keySerde(), consumedInternal.keySerde());
        Assert.assertEquals(valueSerde(), null);
    }

    @Test
    public void shouldUseRecordMetadataTimestampExtractorWithThrough() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> stream1 = builder.stream(Arrays.asList("topic-1", "topic-2"), stringConsumed);
        final KStream<String, String> stream2 = builder.stream(Arrays.asList("topic-3", "topic-4"), stringConsumed);
        stream1.to("topic-5");
        stream2.through("topic-6");
        final ProcessorTopology processorTopology = TopologyWrapper.getInternalTopologyBuilder(builder.build()).setApplicationId("X").build(null);
        MatcherAssert.assertThat(processorTopology.source("topic-6").getTimestampExtractor(), IsInstanceOf.instanceOf(FailOnInvalidTimestamp.class));
        Assert.assertEquals(processorTopology.source("topic-4").getTimestampExtractor(), null);
        Assert.assertEquals(processorTopology.source("topic-3").getTimestampExtractor(), null);
        Assert.assertEquals(processorTopology.source("topic-2").getTimestampExtractor(), null);
        Assert.assertEquals(processorTopology.source("topic-1").getTimestampExtractor(), null);
    }

    @Test
    public void shouldSendDataThroughTopicUsingProduced() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String input = "topic";
        final KStream<String, String> stream = builder.stream(input, stringConsumed);
        stream.through("through-topic", Produced.with(Serdes.String(), Serdes.String())).process(processorSupplier);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            driver.pipeInput(recordFactory.create(input, "a", "b"));
        }
        MatcherAssert.assertThat(processorSupplier.theCapturedProcessor().processed, CoreMatchers.equalTo(Collections.singletonList("a:b")));
    }

    @Test
    public void shouldSendDataToTopicUsingProduced() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String input = "topic";
        final KStream<String, String> stream = builder.stream(input, stringConsumed);
        stream.to("to-topic", Produced.with(Serdes.String(), Serdes.String()));
        builder.stream("to-topic", stringConsumed).process(processorSupplier);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            driver.pipeInput(recordFactory.create(input, "e", "f"));
        }
        MatcherAssert.assertThat(processorSupplier.theCapturedProcessor().processed, CoreMatchers.equalTo(Collections.singletonList("e:f")));
    }

    @Test
    public void shouldSendDataToDynamicTopics() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String input = "topic";
        final KStream<String, String> stream = builder.stream(input, stringConsumed);
        stream.to(( key, value, context) -> ((((context.topic()) + "-") + key) + "-") + (value.substring(0, 1)), Produced.with(Serdes.String(), Serdes.String()));
        builder.stream((input + "-a-v"), stringConsumed).process(processorSupplier);
        builder.stream((input + "-b-v"), stringConsumed).process(processorSupplier);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            driver.pipeInput(recordFactory.create(input, "a", "v1"));
            driver.pipeInput(recordFactory.create(input, "a", "v2"));
            driver.pipeInput(recordFactory.create(input, "b", "v1"));
        }
        final List<MockProcessor<String, String>> mockProcessors = processorSupplier.capturedProcessors(2);
        MatcherAssert.assertThat(mockProcessors.get(0).processed, CoreMatchers.equalTo(Arrays.asList("a:v1", "a:v2")));
        MatcherAssert.assertThat(mockProcessors.get(1).processed, CoreMatchers.equalTo(Collections.singletonList("b:v1")));
    }

    // specifically testing the deprecated variant
    @SuppressWarnings("deprecation")
    @Test
    public void shouldUseRecordMetadataTimestampExtractorWhenInternalRepartitioningTopicCreatedWithRetention() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> kStream = builder.stream("topic-1", stringConsumed);
        final org.apache.kafka.streams.kstream.ValueJoiner<String, String, String> valueJoiner = MockValueJoiner.instance(":");
        final long windowSize = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
        final KStream<String, String> stream = kStream.map(new KeyValueMapper<String, String, KeyValue<? extends String, ? extends String>>() {
            @Override
            public KeyValue<? extends String, ? extends String> apply(final String key, final String value) {
                return KeyValue.pair(value, value);
            }
        });
        stream.join(kStream, valueJoiner, JoinWindows.of(Duration.ofMillis(windowSize)).grace(Duration.ofMillis((3 * windowSize))), Joined.with(Serdes.String(), Serdes.String(), Serdes.String())).to("output-topic", Produced.with(Serdes.String(), Serdes.String()));
        final ProcessorTopology topology = TopologyWrapper.getInternalTopologyBuilder(builder.build()).setApplicationId("X").build();
        final SourceNode originalSourceNode = topology.source("topic-1");
        for (final SourceNode sourceNode : topology.sources()) {
            if (sourceNode.name().equals(originalSourceNode.name())) {
                Assert.assertNull(sourceNode.getTimestampExtractor());
            } else {
                MatcherAssert.assertThat(sourceNode.getTimestampExtractor(), IsInstanceOf.instanceOf(FailOnInvalidTimestamp.class));
            }
        }
    }

    @Test
    public void shouldUseRecordMetadataTimestampExtractorWhenInternalRepartitioningTopicCreated() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> kStream = builder.stream("topic-1", stringConsumed);
        final org.apache.kafka.streams.kstream.ValueJoiner<String, String, String> valueJoiner = MockValueJoiner.instance(":");
        final long windowSize = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
        final KStream<String, String> stream = kStream.map(new KeyValueMapper<String, String, KeyValue<? extends String, ? extends String>>() {
            @Override
            public KeyValue<? extends String, ? extends String> apply(final String key, final String value) {
                return KeyValue.pair(value, value);
            }
        });
        stream.join(kStream, valueJoiner, JoinWindows.of(Duration.ofMillis(windowSize)).grace(Duration.ofMillis((3L * windowSize))), Joined.with(Serdes.String(), Serdes.String(), Serdes.String())).to("output-topic", Produced.with(Serdes.String(), Serdes.String()));
        final ProcessorTopology topology = TopologyWrapper.getInternalTopologyBuilder(builder.build()).setApplicationId("X").build();
        final SourceNode originalSourceNode = topology.source("topic-1");
        for (final SourceNode sourceNode : topology.sources()) {
            if (sourceNode.name().equals(originalSourceNode.name())) {
                Assert.assertNull(sourceNode.getTimestampExtractor());
            } else {
                MatcherAssert.assertThat(sourceNode.getTimestampExtractor(), IsInstanceOf.instanceOf(FailOnInvalidTimestamp.class));
            }
        }
    }

    @Test
    public void shouldPropagateRepartitionFlagAfterGlobalKTableJoin() {
        final StreamsBuilder builder = new StreamsBuilder();
        final GlobalKTable<String, String> globalKTable = builder.globalTable("globalTopic");
        final KeyValueMapper<String, String, String> kvMappper = ( k, v) -> k + v;
        final org.apache.kafka.streams.kstream.ValueJoiner<String, String, String> valueJoiner = ( v1, v2) -> v1 + v2;
        builder.<String, String>stream("topic").selectKey(( k, v) -> v).join(globalKTable, kvMappper, valueJoiner).groupByKey().count();
        final Pattern repartitionTopicPattern = Pattern.compile("Sink: .*-repartition");
        final String topology = builder.build().describe().toString();
        final Matcher matcher = repartitionTopicPattern.matcher(topology);
        Assert.assertTrue(matcher.find());
        final String match = matcher.group();
        MatcherAssert.assertThat(match, CoreMatchers.notNullValue());
        Assert.assertTrue(match.endsWith("repartition"));
    }

    @Test
    public void testToWithNullValueSerdeDoesntNPE() {
        final StreamsBuilder builder = new StreamsBuilder();
        final Consumed<String, String> consumed = Consumed.with(Serdes.String(), Serdes.String());
        final KStream<String, String> inputStream = builder.stream(Collections.singleton("input"), consumed);
        inputStream.to("output", Produced.with(Serdes.String(), Serdes.String()));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullPredicateOnFilter() {
        testStream.filter(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullPredicateOnFilterNot() {
        testStream.filterNot(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullMapperOnSelectKey() {
        testStream.selectKey(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullMapperOnMap() {
        testStream.map(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullMapperOnMapValues() {
        testStream.mapValues(((org.apache.kafka.streams.kstream.ValueMapper) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullMapperOnMapValuesWithKey() {
        testStream.mapValues(((ValueMapperWithKey) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullMapperOnFlatMap() {
        testStream.flatMap(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullMapperOnFlatMapValues() {
        testStream.flatMapValues(((org.apache.kafka.streams.kstream.ValueMapper<? super String, ? extends Iterable<? extends String>>) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullMapperOnFlatMapValuesWithKey() {
        testStream.flatMapValues(((ValueMapperWithKey<? super String, ? super String, ? extends Iterable<? extends String>>) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldHaveAtLeastOnPredicateWhenBranching() {
        testStream.branch();
    }

    @Test(expected = NullPointerException.class)
    public void shouldCantHaveNullPredicate() {
        testStream.branch(((org.apache.kafka.streams.kstream.Predicate) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTopicOnThrough() {
        testStream.through(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTopicOnTo() {
        testStream.to(((String) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTopicChooserOnTo() {
        testStream.to(((TopicNameExtractor<String, String>) (null)));
    }

    @Test
    public void shouldNotAllowNullTransformSupplierOnTransform() {
        final Exception e = Assert.assertThrows(NullPointerException.class, () -> testStream.transform(null));
        Assert.assertEquals("transformerSupplier can't be null", e.getMessage());
    }

    @Test
    public void shouldNotAllowNullTransformSupplierOnFlatTransform() {
        final Exception e = Assert.assertThrows(NullPointerException.class, () -> testStream.flatTransform(null));
        Assert.assertEquals("transformerSupplier can't be null", e.getMessage());
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTransformSupplierOnTransformValues() {
        testStream.transformValues(((ValueTransformerSupplier) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTransformSupplierOnTransformValuesWithKey() {
        testStream.transformValues(((ValueTransformerWithKeySupplier) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullProcessSupplier() {
        testStream.process(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullOtherStreamOnJoin() {
        testStream.join(null, MockValueJoiner.TOSTRING_JOINER, JoinWindows.of(Duration.ofMillis(10)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullValueJoinerOnJoin() {
        testStream.join(testStream, null, JoinWindows.of(Duration.ofMillis(10)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullJoinWindowsOnJoin() {
        testStream.join(testStream, MockValueJoiner.TOSTRING_JOINER, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTableOnTableJoin() {
        testStream.leftJoin(((KTable) (null)), MockValueJoiner.TOSTRING_JOINER);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullValueMapperOnTableJoin() {
        testStream.leftJoin(builder.table("topic", stringConsumed), null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullSelectorOnGroupBy() {
        testStream.groupBy(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullActionOnForEach() {
        testStream.foreach(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTableOnJoinWithGlobalTable() {
        testStream.join(((GlobalKTable) (null)), MockMapper.<String, String>selectValueMapper(), MockValueJoiner.TOSTRING_JOINER);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullMapperOnJoinWithGlobalTable() {
        testStream.join(builder.globalTable("global", stringConsumed), null, MockValueJoiner.TOSTRING_JOINER);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullJoinerOnJoinWithGlobalTable() {
        testStream.join(builder.globalTable("global", stringConsumed), MockMapper.<String, String>selectValueMapper(), null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTableOnJLeftJoinWithGlobalTable() {
        testStream.leftJoin(((GlobalKTable) (null)), MockMapper.<String, String>selectValueMapper(), MockValueJoiner.TOSTRING_JOINER);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullMapperOnLeftJoinWithGlobalTable() {
        testStream.leftJoin(builder.globalTable("global", stringConsumed), null, MockValueJoiner.TOSTRING_JOINER);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullJoinerOnLeftJoinWithGlobalTable() {
        testStream.leftJoin(builder.globalTable("global", stringConsumed), MockMapper.<String, String>selectValueMapper(), null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnPrintIfPrintedIsNull() {
        testStream.print(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnThroughWhenProducedIsNull() {
        testStream.through("topic", null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnToWhenProducedIsNull() {
        testStream.to("topic", null);
    }

    @Test
    public void shouldThrowNullPointerOnLeftJoinWithTableWhenJoinedIsNull() {
        final KTable<String, String> table = builder.table("blah", stringConsumed);
        try {
            testStream.leftJoin(table, MockValueJoiner.TOSTRING_JOINER, null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (final NullPointerException e) {
            // ok
        }
    }

    @Test
    public void shouldThrowNullPointerOnJoinWithTableWhenJoinedIsNull() {
        final KTable<String, String> table = builder.table("blah", stringConsumed);
        try {
            testStream.join(table, MockValueJoiner.TOSTRING_JOINER, null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (final NullPointerException e) {
            // ok
        }
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnJoinWithStreamWhenJoinedIsNull() {
        testStream.join(testStream, MockValueJoiner.TOSTRING_JOINER, JoinWindows.of(Duration.ofMillis(10)), null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnOuterJoinJoinedIsNull() {
        testStream.outerJoin(testStream, MockValueJoiner.TOSTRING_JOINER, JoinWindows.of(Duration.ofMillis(10)), null);
    }

    @Test
    public void shouldMergeTwoStreams() {
        final String topic1 = "topic-1";
        final String topic2 = "topic-2";
        final KStream<String, String> source1 = builder.stream(topic1);
        final KStream<String, String> source2 = builder.stream(topic2);
        final KStream<String, String> merged = source1.merge(source2);
        merged.process(processorSupplier);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            driver.pipeInput(recordFactory.create(topic1, "A", "aa"));
            driver.pipeInput(recordFactory.create(topic2, "B", "bb"));
            driver.pipeInput(recordFactory.create(topic2, "C", "cc"));
            driver.pipeInput(recordFactory.create(topic1, "D", "dd"));
        }
        Assert.assertEquals(Arrays.asList("A:aa", "B:bb", "C:cc", "D:dd"), processorSupplier.theCapturedProcessor().processed);
    }

    @Test
    public void shouldMergeMultipleStreams() {
        final String topic1 = "topic-1";
        final String topic2 = "topic-2";
        final String topic3 = "topic-3";
        final String topic4 = "topic-4";
        final KStream<String, String> source1 = builder.stream(topic1);
        final KStream<String, String> source2 = builder.stream(topic2);
        final KStream<String, String> source3 = builder.stream(topic3);
        final KStream<String, String> source4 = builder.stream(topic4);
        final KStream<String, String> merged = source1.merge(source2).merge(source3).merge(source4);
        merged.process(processorSupplier);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            driver.pipeInput(recordFactory.create(topic1, "A", "aa"));
            driver.pipeInput(recordFactory.create(topic2, "B", "bb"));
            driver.pipeInput(recordFactory.create(topic3, "C", "cc"));
            driver.pipeInput(recordFactory.create(topic4, "D", "dd"));
            driver.pipeInput(recordFactory.create(topic4, "E", "ee"));
            driver.pipeInput(recordFactory.create(topic3, "F", "ff"));
            driver.pipeInput(recordFactory.create(topic2, "G", "gg"));
            driver.pipeInput(recordFactory.create(topic1, "H", "hh"));
        }
        Assert.assertEquals(Arrays.asList("A:aa", "B:bb", "C:cc", "D:dd", "E:ee", "F:ff", "G:gg", "H:hh"), processorSupplier.theCapturedProcessor().processed);
    }

    @Test
    public void shouldProcessFromSourceThatMatchPattern() {
        final KStream<String, String> pattern2Source = builder.stream(Pattern.compile("topic-\\d"));
        pattern2Source.process(processorSupplier);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            driver.pipeInput(recordFactory.create("topic-3", "A", "aa"));
            driver.pipeInput(recordFactory.create("topic-4", "B", "bb"));
            driver.pipeInput(recordFactory.create("topic-5", "C", "cc"));
            driver.pipeInput(recordFactory.create("topic-6", "D", "dd"));
            driver.pipeInput(recordFactory.create("topic-7", "E", "ee"));
        }
        Assert.assertEquals(Arrays.asList("A:aa", "B:bb", "C:cc", "D:dd", "E:ee"), processorSupplier.theCapturedProcessor().processed);
    }

    @Test
    public void shouldProcessFromSourcesThatMatchMultiplePattern() {
        final String topic3 = "topic-without-pattern";
        final KStream<String, String> pattern2Source1 = builder.stream(Pattern.compile("topic-\\d"));
        final KStream<String, String> pattern2Source2 = builder.stream(Pattern.compile("topic-[A-Z]"));
        final KStream<String, String> source3 = builder.stream(topic3);
        final KStream<String, String> merged = pattern2Source1.merge(pattern2Source2).merge(source3);
        merged.process(processorSupplier);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            driver.pipeInput(recordFactory.create("topic-3", "A", "aa"));
            driver.pipeInput(recordFactory.create("topic-4", "B", "bb"));
            driver.pipeInput(recordFactory.create("topic-A", "C", "cc"));
            driver.pipeInput(recordFactory.create("topic-Z", "D", "dd"));
            driver.pipeInput(recordFactory.create(topic3, "E", "ee"));
        }
        Assert.assertEquals(Arrays.asList("A:aa", "B:bb", "C:cc", "D:dd", "E:ee"), processorSupplier.theCapturedProcessor().processed);
    }
}

