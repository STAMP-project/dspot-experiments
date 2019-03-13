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
package org.apache.beam.sdk.transforms;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.beam.sdk.TestUtils;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.options.ValueProvider.NestedValueProvider;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.SchemaCoder;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.Create.Values.CreateSource;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.sdk.values.TimestampedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for Create.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("unchecked")
public class CreateTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final TestPipeline p = TestPipeline.create();

    @Test
    @Category(ValidatesRunner.class)
    public void testCreate() {
        PCollection<String> output = p.apply(Create.of(TestUtils.LINES));
        PAssert.that(output).containsInAnyOrder(TestUtils.LINES_ARRAY);
        p.run();
    }

    @Test
    @Category(ValidatesRunner.class)
    public void testCreateEmpty() {
        PCollection<String> output = p.apply(Create.empty(StringUtf8Coder.of()));
        PAssert.that(output).containsInAnyOrder(TestUtils.NO_LINES_ARRAY);
        Assert.assertEquals(StringUtf8Coder.of(), output.getCoder());
        p.run();
    }

    @Test
    public void testCreateEmptyIterableRequiresCoder() {
        p.enableAbandonedNodeEnforcement(false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("determine a default Coder");
        thrown.expectMessage("Create.empty(Coder)");
        thrown.expectMessage("Create.empty(TypeDescriptor)");
        thrown.expectMessage("withCoder(Coder)");
        thrown.expectMessage("withType(TypeDescriptor)");
        p.apply(Create.of(Collections.emptyList()));
    }

    @Test
    @Category(NeedsRunner.class)
    public void testCreateEmptyIterableWithCoder() {
        PCollection<Void> output = p.apply(Create.of(Collections.<Void>emptyList()).withCoder(VoidCoder.of()));
        Assert.assertEquals(VoidCoder.of(), output.getCoder());
        PAssert.that(output).empty();
        p.run();
    }

    static class Record implements Serializable {}

    static class Record2 extends CreateTest.Record {}

    private static class RecordCoder extends AtomicCoder<CreateTest.Record> {
        @Override
        public void encode(CreateTest.Record value, OutputStream outStream) throws IOException, CoderException {
        }

        @Override
        public CreateTest.Record decode(InputStream inStream) throws IOException, CoderException {
            return null;
        }
    }

    @Test
    public void testPolymorphicType() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("Unable to infer a coder"));
        // Create won't infer a default coder in this case.
        p.apply(Create.of(new CreateTest.Record(), new CreateTest.Record2()));
        p.run();
    }

    @Test
    @Category(ValidatesRunner.class)
    public void testCreateWithNullsAndValues() throws Exception {
        PCollection<String> output = p.apply(Create.of(null, "test1", null, "test2", null).withCoder(SerializableCoder.of(String.class)));
        PAssert.that(output).containsInAnyOrder(null, "test1", null, "test2", null);
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testCreateParameterizedType() throws Exception {
        PCollection<TimestampedValue<String>> output = p.apply(Create.of(TimestampedValue.of("a", new Instant(0)), TimestampedValue.of("b", new Instant(0))));
        PAssert.that(output).containsInAnyOrder(TimestampedValue.of("a", new Instant(0)), TimestampedValue.of("b", new Instant(0)));
        p.run();
    }

    /**
     * An unserializable class to demonstrate encoding of elements.
     */
    private static class UnserializableRecord {
        private final String myString;

        private UnserializableRecord(String myString) {
            this.myString = myString;
        }

        @Override
        public int hashCode() {
            return myString.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return myString.equals(((CreateTest.UnserializableRecord) (o)).myString);
        }

        static class UnserializableRecordCoder extends AtomicCoder<CreateTest.UnserializableRecord> {
            private final Coder<String> stringCoder = StringUtf8Coder.of();

            @Override
            public void encode(CreateTest.UnserializableRecord value, OutputStream outStream) throws IOException, CoderException {
                stringCoder.encode(value.myString, outStream);
            }

            @Override
            public CreateTest.UnserializableRecord decode(InputStream inStream) throws IOException, CoderException {
                return new CreateTest.UnserializableRecord(stringCoder.decode(inStream));
            }
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testCreateWithUnserializableElements() throws Exception {
        List<CreateTest.UnserializableRecord> elements = ImmutableList.of(new CreateTest.UnserializableRecord("foo"), new CreateTest.UnserializableRecord("bar"), new CreateTest.UnserializableRecord("baz"));
        Create.Values<CreateTest.UnserializableRecord> create = Create.of(elements).withCoder(new CreateTest.UnserializableRecord.UnserializableRecordCoder());
        PAssert.that(p.apply(create)).containsInAnyOrder(new CreateTest.UnserializableRecord("foo"), new CreateTest.UnserializableRecord("bar"), new CreateTest.UnserializableRecord("baz"));
        p.run();
    }

    private static class PrintTimestamps extends DoFn<String, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output((((c.element()) + ":") + (c.timestamp().getMillis())));
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testCreateTimestamped() {
        List<TimestampedValue<String>> data = Arrays.asList(TimestampedValue.of("a", new Instant(1L)), TimestampedValue.of("b", new Instant(2L)), TimestampedValue.of("c", new Instant(3L)));
        PCollection<String> output = p.apply(Create.timestamped(data)).apply(ParDo.of(new CreateTest.PrintTimestamps()));
        PAssert.that(output).containsInAnyOrder("a:1", "b:2", "c:3");
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testCreateTimestampedEmpty() {
        PCollection<String> output = p.apply(Create.timestamped(new ArrayList<TimestampedValue<String>>()).withCoder(StringUtf8Coder.of()));
        PAssert.that(output).empty();
        p.run();
    }

    @Test
    public void testCreateTimestampedEmptyUnspecifiedCoder() {
        p.enableAbandonedNodeEnforcement(false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("determine a default Coder");
        thrown.expectMessage("Create.empty(Coder)");
        thrown.expectMessage("Create.empty(TypeDescriptor)");
        thrown.expectMessage("withCoder(Coder)");
        thrown.expectMessage("withType(TypeDescriptor)");
        p.apply(Create.timestamped(new ArrayList()));
    }

    @Test
    public void testCreateTimestampedPolymorphicType() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("Unable to infer a coder"));
        // Create won't infer a default coder in this case.
        PCollection<CreateTest.Record> c = p.apply(Create.timestamped(TimestampedValue.of(new CreateTest.Record(), new Instant(0)), TimestampedValue.of(new CreateTest.Record2(), new Instant(0))));
        p.run();
        throw new RuntimeException(("Coder: " + (c.getCoder())));
    }

    @Test
    public void testCreateTimestampedDefaultOutputCoderUsingCoder() throws Exception {
        Coder<CreateTest.Record> coder = new CreateTest.RecordCoder();
        Create.TimestampedValues<CreateTest.Record> values = Create.timestamped(TimestampedValue.of(new CreateTest.Record(), new Instant(0)), TimestampedValue.of(new CreateTest.Record2(), new Instant(0))).withCoder(coder);
        Assert.assertThat(p.apply(values).getCoder(), Matchers.equalTo(coder));
    }

    @Test
    public void testCreateTimestampedDefaultOutputCoderUsingTypeDescriptor() throws Exception {
        Coder<CreateTest.Record> coder = new CreateTest.RecordCoder();
        p.getCoderRegistry().registerCoderForClass(CreateTest.Record.class, coder);
        Create.TimestampedValues<CreateTest.Record> values = Create.timestamped(TimestampedValue.of(new CreateTest.Record(), new Instant(0)), TimestampedValue.of(new CreateTest.Record2(), new Instant(0))).withType(new org.apache.beam.sdk.values.TypeDescriptor<CreateTest.Record>() {});
        Assert.assertThat(p.apply(values).getCoder(), Matchers.equalTo(coder));
    }

    @Test
    @Category(ValidatesRunner.class)
    public void testCreateWithVoidType() throws Exception {
        PCollection<Void> output = p.apply(Create.of(((Void) (null)), ((Void) (null))));
        PAssert.that(output).containsInAnyOrder(((Void) (null)), ((Void) (null)));
        p.run();
    }

    @Test
    @Category(ValidatesRunner.class)
    public void testCreateWithKVVoidType() throws Exception {
        PCollection<KV<Void, Void>> output = p.apply(Create.of(KV.of(((Void) (null)), ((Void) (null))), KV.of(((Void) (null)), ((Void) (null)))));
        PAssert.that(output).containsInAnyOrder(KV.of(((Void) (null)), ((Void) (null))), KV.of(((Void) (null)), ((Void) (null))));
        p.run();
    }

    /**
     * Testing options for {@link #testCreateOfProvider()}.
     */
    public interface CreateOfProviderOptions extends PipelineOptions {
        ValueProvider<String> getFoo();

        void setFoo(ValueProvider<String> value);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testCreateOfProvider() throws Exception {
        PAssert.that(p.apply("Static", Create.ofProvider(StaticValueProvider.of("foo"), StringUtf8Coder.of()))).containsInAnyOrder("foo");
        PAssert.that(p.apply("Static nested", Create.ofProvider(NestedValueProvider.of(StaticValueProvider.of("foo"), ( input) -> input + "bar"), StringUtf8Coder.of()))).containsInAnyOrder("foobar");
        PAssert.that(p.apply("Runtime", Create.ofProvider(p.newProvider("runtimeFoo"), StringUtf8Coder.of()))).containsInAnyOrder("runtimeFoo");
        p.run();
    }

    @Test
    public void testCreateGetName() {
        Assert.assertEquals("Create.Values", Create.of(1, 2, 3).getName());
        Assert.assertEquals("Create.TimestampedValues", Create.timestamped(Collections.emptyList()).getName());
    }

    @Test
    public void testCreateDefaultOutputCoderUsingInference() throws Exception {
        Coder<CreateTest.Record> coder = new CreateTest.RecordCoder();
        p.getCoderRegistry().registerCoderForClass(CreateTest.Record.class, coder);
        Assert.assertThat(p.apply(Create.of(new CreateTest.Record(), new CreateTest.Record(), new CreateTest.Record())).getCoder(), Matchers.equalTo(coder));
    }

    @Test
    public void testCreateDefaultOutputCoderUsingCoder() throws Exception {
        Coder<CreateTest.Record> coder = new CreateTest.RecordCoder();
        Assert.assertThat(p.apply(Create.of(new CreateTest.Record(), new CreateTest.Record2()).withCoder(coder)).getCoder(), Matchers.equalTo(coder));
    }

    @Test
    public void testCreateDefaultOutputCoderUsingTypeDescriptor() throws Exception {
        Coder<CreateTest.Record> coder = new CreateTest.RecordCoder();
        p.getCoderRegistry().registerCoderForClass(CreateTest.Record.class, coder);
        Create.Values<CreateTest.Record> values = Create.of(new CreateTest.Record(), new CreateTest.Record2()).withType(new org.apache.beam.sdk.values.TypeDescriptor<CreateTest.Record>() {});
        Assert.assertThat(p.apply(values).getCoder(), Matchers.equalTo(coder));
    }

    private static final Schema STRING_SCHEMA = Schema.builder().addStringField("field").build();

    @Test
    public void testCreateRegisteredSchema() {
        p.getSchemaRegistry().registerSchemaForClass(String.class, CreateTest.STRING_SCHEMA, ( s) -> Row.withSchema(STRING_SCHEMA).addValue(s).build(), ( r) -> r.getString("field"));
        PCollection<String> out = p.apply(Create.of("a", "b", "c", "d"));
        Assert.assertThat(out.getCoder(), Matchers.instanceOf(SchemaCoder.class));
    }

    @Test
    public void testCreateExplicitSchema() {
        PCollection<String> out = p.apply(Create.of("a", "b", "c", "d").withSchema(CreateTest.STRING_SCHEMA, ( s) -> Row.withSchema(STRING_SCHEMA).addValue(s).build(), ( r) -> r.getString("field")));
        Assert.assertThat(out.getCoder(), Matchers.instanceOf(SchemaCoder.class));
    }

    @Test
    public void testSourceIsSerializableWithUnserializableElements() throws Exception {
        List<CreateTest.UnserializableRecord> elements = ImmutableList.of(new CreateTest.UnserializableRecord("foo"), new CreateTest.UnserializableRecord("bar"), new CreateTest.UnserializableRecord("baz"));
        CreateSource<CreateTest.UnserializableRecord> source = CreateSource.fromIterable(elements, new CreateTest.UnserializableRecord.UnserializableRecordCoder());
        SerializableUtils.ensureSerializable(source);
    }

    @Test
    public void testSourceSplit() throws Exception {
        CreateSource<Integer> source = CreateSource.fromIterable(ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8), BigEndianIntegerCoder.of());
        PipelineOptions options = PipelineOptionsFactory.create();
        List<? extends BoundedSource<Integer>> splitSources = source.split(12, options);
        Assert.assertThat(splitSources, Matchers.hasSize(3));
        SourceTestUtils.assertSourcesEqualReferenceSource(source, splitSources, options);
    }

    @Test
    public void testSourceSplitVoid() throws Exception {
        CreateSource<Void> source = CreateSource.fromIterable(Lists.newArrayList(null, null, null, null, null), VoidCoder.of());
        PipelineOptions options = PipelineOptionsFactory.create();
        List<? extends BoundedSource<Void>> splitSources = source.split(3, options);
        SourceTestUtils.assertSourcesEqualReferenceSource(source, splitSources, options);
    }

    @Test
    public void testSourceSplitEmpty() throws Exception {
        CreateSource<Integer> source = CreateSource.fromIterable(ImmutableList.of(), BigEndianIntegerCoder.of());
        PipelineOptions options = PipelineOptionsFactory.create();
        List<? extends BoundedSource<Integer>> splitSources = source.split(12, options);
        SourceTestUtils.assertSourcesEqualReferenceSource(source, splitSources, options);
    }

    @Test
    public void testSourceGetOutputCoderReturnsConstructorCoder() throws Exception {
        Coder<Integer> coder = VarIntCoder.of();
        CreateSource<Integer> source = CreateSource.fromIterable(ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8), coder);
        Coder<Integer> defaultCoder = source.getOutputCoder();
        Assert.assertThat(defaultCoder, Matchers.equalTo(coder));
    }

    @Test
    public void testSourceSplitAtFraction() throws Exception {
        List<Integer> elements = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            elements.add(random.nextInt());
        }
        CreateSource<Integer> source = CreateSource.fromIterable(elements, VarIntCoder.of());
        SourceTestUtils.assertSplitAtFractionExhaustive(source, PipelineOptionsFactory.create());
    }
}

