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


import java.util.Arrays;
import java.util.List;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.internals.ForwardingDisabledProcessorContext;
import org.apache.kafka.streams.processor.internals.InternalProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.test.MockProcessorSupplier;
import org.apache.kafka.test.MockReducer;
import org.apache.kafka.test.SingletonNoOpValueTransformer;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class KTableTransformValuesTest {
    private static final String QUERYABLE_NAME = "queryable-store";

    private static final String INPUT_TOPIC = "inputTopic";

    private static final String STORE_NAME = "someStore";

    private static final String OTHER_STORE_NAME = "otherStore";

    private static final Consumed<String, String> CONSUMED = Consumed.with(Serdes.String(), Serdes.String());

    private final ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory(new StringSerializer(), new StringSerializer());

    private TopologyTestDriver driver;

    private MockProcessorSupplier<String, String> capture;

    private StreamsBuilder builder;

    @Mock(MockType.NICE)
    private KTableImpl<String, String, String> parent;

    @Mock(MockType.NICE)
    private InternalProcessorContext context;

    @Mock(MockType.NICE)
    private KTableValueGetterSupplier<String, String> parentGetterSupplier;

    @Mock(MockType.NICE)
    private KTableValueGetter<String, String> parentGetter;

    @Mock(MockType.NICE)
    private KeyValueStore<String, String> stateStore;

    @Mock(MockType.NICE)
    private ValueTransformerWithKeySupplier<String, String, String> mockSupplier;

    @Mock(MockType.NICE)
    private ValueTransformerWithKey<String, String, String> transformer;

    @Test
    public void shouldThrowOnGetIfSupplierReturnsNull() {
        final KTableTransformValues<String, String, String> transformer = new KTableTransformValues(parent, new KTableTransformValuesTest.NullSupplier(), KTableTransformValuesTest.QUERYABLE_NAME);
        try {
            transformer.get();
            Assert.fail("NPE expected");
        } catch (final NullPointerException expected) {
            // expected
        }
    }

    @Test
    public void shouldThrowOnViewGetIfSupplierReturnsNull() {
        final KTableValueGetterSupplier<String, String> view = new KTableTransformValues(parent, new KTableTransformValuesTest.NullSupplier(), null).view();
        try {
            view.get();
            Assert.fail("NPE expected");
        } catch (final NullPointerException expected) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldInitializeTransformerWithForwardDisabledProcessorContext() {
        final SingletonNoOpValueTransformer<String, String> transformer = new SingletonNoOpValueTransformer<>();
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, transformer, null);
        final Processor<String, Change<String>> processor = transformValues.get();
        processor.init(context);
        MatcherAssert.assertThat(transformer.context, CoreMatchers.isA(((Class) (ForwardingDisabledProcessorContext.class))));
    }

    @Test
    public void shouldNotSendOldValuesByDefault() {
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, new KTableTransformValuesTest.ExclamationValueTransformerSupplier(), null);
        final Processor<String, Change<String>> processor = transformValues.get();
        processor.init(context);
        context.forward("Key", new Change("Key->newValue!", null));
        expectLastCall();
        replay(context);
        processor.process("Key", new Change("newValue", "oldValue"));
        verify(context);
    }

    @Test
    public void shouldSendOldValuesIfConfigured() {
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, new KTableTransformValuesTest.ExclamationValueTransformerSupplier(), null);
        transformValues.enableSendingOldValues();
        final Processor<String, Change<String>> processor = transformValues.get();
        processor.init(context);
        context.forward("Key", new Change("Key->newValue!", "Key->oldValue!"));
        expectLastCall();
        replay(context);
        processor.process("Key", new Change("newValue", "oldValue"));
        verify(context);
    }

    @Test
    public void shouldSetSendOldValuesOnParent() {
        parent.enableSendingOldValues();
        expectLastCall();
        replay(parent);
        new KTableTransformValues(parent, new SingletonNoOpValueTransformer(), KTableTransformValuesTest.QUERYABLE_NAME).enableSendingOldValues();
        verify(parent);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTransformOnGetIfNotMaterialized() {
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, new KTableTransformValuesTest.ExclamationValueTransformerSupplier(), null);
        expect(parent.valueGetterSupplier()).andReturn(parentGetterSupplier);
        expect(parentGetterSupplier.get()).andReturn(parentGetter);
        expect(parentGetter.get("Key")).andReturn("Value");
        replay(parent, parentGetterSupplier, parentGetter);
        final KTableValueGetter<String, String> getter = transformValues.view().get();
        getter.init(context);
        final String result = getter.get("Key");
        MatcherAssert.assertThat(result, CoreMatchers.is("Key->Value!"));
    }

    @Test
    public void shouldGetFromStateStoreIfMaterialized() {
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, new KTableTransformValuesTest.ExclamationValueTransformerSupplier(), KTableTransformValuesTest.QUERYABLE_NAME);
        expect(context.getStateStore(KTableTransformValuesTest.QUERYABLE_NAME)).andReturn(stateStore);
        expect(stateStore.get("Key")).andReturn("something");
        replay(context, stateStore);
        final KTableValueGetter<String, String> getter = transformValues.view().get();
        getter.init(context);
        final String result = getter.get("Key");
        MatcherAssert.assertThat(result, CoreMatchers.is("something"));
    }

    @Test
    public void shouldGetStoreNamesFromParentIfNotMaterialized() {
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, new KTableTransformValuesTest.ExclamationValueTransformerSupplier(), null);
        expect(parent.valueGetterSupplier()).andReturn(parentGetterSupplier);
        expect(parentGetterSupplier.storeNames()).andReturn(new String[]{ "store1", "store2" });
        replay(parent, parentGetterSupplier);
        final String[] storeNames = transformValues.view().storeNames();
        MatcherAssert.assertThat(storeNames, CoreMatchers.is(new String[]{ "store1", "store2" }));
    }

    @Test
    public void shouldGetQueryableStoreNameIfMaterialized() {
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, new KTableTransformValuesTest.ExclamationValueTransformerSupplier(), KTableTransformValuesTest.QUERYABLE_NAME);
        final String[] storeNames = transformValues.view().storeNames();
        MatcherAssert.assertThat(storeNames, CoreMatchers.is(new String[]{ KTableTransformValuesTest.QUERYABLE_NAME }));
    }

    @Test
    public void shouldCloseTransformerOnProcessorClose() {
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, mockSupplier, null);
        expect(mockSupplier.get()).andReturn(transformer);
        transformer.close();
        expectLastCall();
        replay(mockSupplier, transformer);
        final Processor<String, Change<String>> processor = transformValues.get();
        processor.close();
        verify(transformer);
    }

    @Test
    public void shouldCloseTransformerOnGetterClose() {
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, mockSupplier, null);
        expect(mockSupplier.get()).andReturn(transformer);
        expect(parentGetterSupplier.get()).andReturn(parentGetter);
        expect(parent.valueGetterSupplier()).andReturn(parentGetterSupplier);
        transformer.close();
        expectLastCall();
        replay(mockSupplier, transformer, parent, parentGetterSupplier);
        final KTableValueGetter<String, String> getter = transformValues.view().get();
        getter.close();
        verify(transformer);
    }

    @Test
    public void shouldCloseParentGetterClose() {
        final KTableTransformValues<String, String, String> transformValues = new KTableTransformValues(parent, mockSupplier, null);
        expect(parent.valueGetterSupplier()).andReturn(parentGetterSupplier);
        expect(mockSupplier.get()).andReturn(transformer);
        expect(parentGetterSupplier.get()).andReturn(parentGetter);
        parentGetter.close();
        expectLastCall();
        replay(mockSupplier, parent, parentGetterSupplier, parentGetter);
        final KTableValueGetter<String, String> getter = transformValues.view().get();
        getter.close();
        verify(parentGetter);
    }

    @Test
    public void shouldTransformValuesWithKey() {
        builder.addStateStore(KTableTransformValuesTest.storeBuilder(KTableTransformValuesTest.STORE_NAME)).addStateStore(KTableTransformValuesTest.storeBuilder(KTableTransformValuesTest.OTHER_STORE_NAME)).table(KTableTransformValuesTest.INPUT_TOPIC, KTableTransformValuesTest.CONSUMED).transformValues(new KTableTransformValuesTest.ExclamationValueTransformerSupplier(KTableTransformValuesTest.STORE_NAME, KTableTransformValuesTest.OTHER_STORE_NAME), KTableTransformValuesTest.STORE_NAME, KTableTransformValuesTest.OTHER_STORE_NAME).toStream().process(capture);
        driver = new TopologyTestDriver(builder.build(), KTableTransformValuesTest.props());
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "A", "a", 0L));
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "B", "b", 0L));
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "D", ((String) (null)), 0L));
        MatcherAssert.assertThat(output(), CoreMatchers.hasItems("A:A->a!", "B:B->b!", "D:D->null!"));
        Assert.assertNull("Store should not be materialized", driver.getKeyValueStore(KTableTransformValuesTest.QUERYABLE_NAME));
    }

    @Test
    public void shouldTransformValuesWithKeyAndMaterialize() {
        builder.addStateStore(KTableTransformValuesTest.storeBuilder(KTableTransformValuesTest.STORE_NAME)).table(KTableTransformValuesTest.INPUT_TOPIC, KTableTransformValuesTest.CONSUMED).transformValues(new KTableTransformValuesTest.ExclamationValueTransformerSupplier(KTableTransformValuesTest.STORE_NAME, KTableTransformValuesTest.QUERYABLE_NAME), Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as(KTableTransformValuesTest.QUERYABLE_NAME).withKeySerde(Serdes.String()).withValueSerde(Serdes.String()), KTableTransformValuesTest.STORE_NAME).toStream().process(capture);
        driver = new TopologyTestDriver(builder.build(), KTableTransformValuesTest.props());
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "A", "a", 0L));
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "B", "b", 0L));
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "C", ((String) (null)), 0L));
        MatcherAssert.assertThat(output(), CoreMatchers.hasItems("A:A->a!", "B:B->b!", "C:C->null!"));
        final KeyValueStore<String, String> keyValueStore = driver.getKeyValueStore(KTableTransformValuesTest.QUERYABLE_NAME);
        MatcherAssert.assertThat(keyValueStore.get("A"), CoreMatchers.is("A->a!"));
        MatcherAssert.assertThat(keyValueStore.get("B"), CoreMatchers.is("B->b!"));
        MatcherAssert.assertThat(keyValueStore.get("C"), CoreMatchers.is("C->null!"));
    }

    @Test
    public void shouldCalculateCorrectOldValuesIfMaterializedEvenIfStateful() {
        builder.table(KTableTransformValuesTest.INPUT_TOPIC, KTableTransformValuesTest.CONSUMED).transformValues(new KTableTransformValuesTest.StatefulTransformerSupplier(), Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>as(KTableTransformValuesTest.QUERYABLE_NAME).withKeySerde(Serdes.String()).withValueSerde(Serdes.Integer())).groupBy(KTableTransformValuesTest.toForceSendingOfOldValues(), Grouped.with(Serdes.String(), Serdes.Integer())).reduce(MockReducer.INTEGER_ADDER, MockReducer.INTEGER_SUBTRACTOR).mapValues(KTableTransformValuesTest.mapBackToStrings()).toStream().process(capture);
        driver = new TopologyTestDriver(builder.build(), KTableTransformValuesTest.props());
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "A", "ignore", 0L));
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "A", "ignored", 0L));
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "A", "ignored", 0L));
        MatcherAssert.assertThat(output(), CoreMatchers.hasItems("A:1", "A:0", "A:2", "A:0", "A:3"));
        final KeyValueStore<String, Integer> keyValueStore = driver.getKeyValueStore(KTableTransformValuesTest.QUERYABLE_NAME);
        MatcherAssert.assertThat(keyValueStore.get("A"), CoreMatchers.is(3));
    }

    @Test
    public void shouldCalculateCorrectOldValuesIfNotStatefulEvenIfNotMaterialized() {
        builder.table(KTableTransformValuesTest.INPUT_TOPIC, KTableTransformValuesTest.CONSUMED).transformValues(new KTableTransformValuesTest.StatelessTransformerSupplier()).groupBy(KTableTransformValuesTest.toForceSendingOfOldValues(), Grouped.with(Serdes.String(), Serdes.Integer())).reduce(MockReducer.INTEGER_ADDER, MockReducer.INTEGER_SUBTRACTOR).mapValues(KTableTransformValuesTest.mapBackToStrings()).toStream().process(capture);
        driver = new TopologyTestDriver(builder.build(), KTableTransformValuesTest.props());
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "A", "a", 0L));
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "A", "aa", 0L));
        driver.pipeInput(recordFactory.create(KTableTransformValuesTest.INPUT_TOPIC, "A", "aaa", 0L));
        MatcherAssert.assertThat(output(), CoreMatchers.hasItems("A:1", "A:0", "A:2", "A:0", "A:3"));
    }

    public static class ExclamationValueTransformerSupplier implements ValueTransformerWithKeySupplier<Object, String, String> {
        private final List<String> expectedStoredNames;

        ExclamationValueTransformerSupplier(final String... expectedStoreNames) {
            this.expectedStoredNames = Arrays.asList(expectedStoreNames);
        }

        @Override
        public KTableTransformValuesTest.ExclamationValueTransformer get() {
            return new KTableTransformValuesTest.ExclamationValueTransformer(expectedStoredNames);
        }
    }

    public static class ExclamationValueTransformer implements ValueTransformerWithKey<Object, String, String> {
        private final List<String> expectedStoredNames;

        ExclamationValueTransformer(final List<String> expectedStoredNames) {
            this.expectedStoredNames = expectedStoredNames;
        }

        @Override
        public void init(final ProcessorContext context) {
            KTableTransformValuesTest.throwIfStoresNotAvailable(context, expectedStoredNames);
        }

        @Override
        public String transform(final Object readOnlyKey, final String value) {
            return (((readOnlyKey.toString()) + "->") + value) + "!";
        }

        @Override
        public void close() {
        }
    }

    private static class NullSupplier implements ValueTransformerWithKeySupplier<String, String, String> {
        @Override
        public ValueTransformerWithKey<String, String, String> get() {
            return null;
        }
    }

    private static class StatefulTransformerSupplier implements ValueTransformerWithKeySupplier<String, String, Integer> {
        @Override
        public ValueTransformerWithKey<String, String, Integer> get() {
            return new KTableTransformValuesTest.StatefulTransformer();
        }
    }

    private static class StatefulTransformer implements ValueTransformerWithKey<String, String, Integer> {
        private int counter;

        @Override
        public void init(final ProcessorContext context) {
        }

        @Override
        public Integer transform(final String readOnlyKey, final String value) {
            return ++(counter);
        }

        @Override
        public void close() {
        }
    }

    private static class StatelessTransformerSupplier implements ValueTransformerWithKeySupplier<String, String, Integer> {
        @Override
        public ValueTransformerWithKey<String, String, Integer> get() {
            return new KTableTransformValuesTest.StatelessTransformer();
        }
    }

    private static class StatelessTransformer implements ValueTransformerWithKey<String, String, Integer> {
        @Override
        public void init(final ProcessorContext context) {
        }

        @Override
        public Integer transform(final String readOnlyKey, final String value) {
            return value == null ? null : value.length();
        }

        @Override
        public void close() {
        }
    }
}

