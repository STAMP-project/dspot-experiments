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
package org.apache.beam.sdk.coders;


import Schema.Type.UNION;
import SpecificData.CLASS_PROP;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.AvroName;
import org.apache.avro.reflect.AvroSchema;
import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.Stringable;
import org.apache.avro.reflect.Union;
import org.apache.avro.util.Utf8;
import org.apache.beam.sdk.coders.Coder.Context;
import org.apache.beam.sdk.coders.Coder.NonDeterministicException;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.testing.InterceptingUrlClassLoader;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.InstanceBuilder;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.objenesis.strategy.StdInstantiatorStrategy;


/**
 * Tests for {@link AvroCoder}.
 */
@RunWith(JUnit4.class)
public class AvroCoderTest {
    @DefaultCoder(AvroCoder.class)
    private static class Pojo {
        public String text;

        public int count;

        // Empty constructor required for Avro decoding.
        @SuppressWarnings("unused")
        public Pojo() {
        }

        public Pojo(String text, int count) {
            this.text = text;
            this.count = count;
        }

        // auto-generated
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            AvroCoderTest.Pojo pojo = ((AvroCoderTest.Pojo) (o));
            if ((count) != (pojo.count)) {
                return false;
            }
            if ((text) != null ? !(text.equals(pojo.text)) : (pojo.text) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return ((((("Pojo{" + "text='") + (text)) + '\'') + ", count=") + (count)) + '}';
        }
    }

    private static class GetTextFn extends DoFn<AvroCoderTest.Pojo, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(c.element().text);
        }
    }

    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testAvroCoderEncoding() throws Exception {
        AvroCoder<AvroCoderTest.Pojo> coder = AvroCoder.of(AvroCoderTest.Pojo.class);
        CoderProperties.coderSerializable(coder);
        AvroCoder<AvroCoderTest.Pojo> copy = SerializableUtils.clone(coder);
        AvroCoderTest.Pojo pojo = new AvroCoderTest.Pojo("foo", 3);
        AvroCoderTest.Pojo equalPojo = new AvroCoderTest.Pojo("foo", 3);
        AvroCoderTest.Pojo otherPojo = new AvroCoderTest.Pojo("bar", (-19));
        CoderProperties.coderConsistentWithEquals(coder, pojo, equalPojo);
        CoderProperties.coderConsistentWithEquals(copy, pojo, equalPojo);
        CoderProperties.coderConsistentWithEquals(coder, pojo, otherPojo);
        CoderProperties.coderConsistentWithEquals(copy, pojo, otherPojo);
    }

    /**
     * Tests that {@link AvroCoder} works around issues in Avro where cache classes might be from the
     * wrong ClassLoader, causing confusing "Cannot cast X to X" error messages.
     */
    @Test
    public void testTwoClassLoaders() throws Exception {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader loader1 = new InterceptingUrlClassLoader(contextClassLoader, AvroCoderTestPojo.class.getName());
        ClassLoader loader2 = new InterceptingUrlClassLoader(contextClassLoader, AvroCoderTestPojo.class.getName());
        Class<?> pojoClass1 = loader1.loadClass(AvroCoderTestPojo.class.getName());
        Class<?> pojoClass2 = loader2.loadClass(AvroCoderTestPojo.class.getName());
        Object pojo1 = InstanceBuilder.ofType(pojoClass1).withArg(String.class, "hello").build();
        Object pojo2 = InstanceBuilder.ofType(pojoClass2).withArg(String.class, "goodbye").build();
        // Confirm incompatibility
        try {
            pojoClass2.cast(pojo1);
            Assert.fail("Expected ClassCastException; without it, this test is vacuous");
        } catch (ClassCastException e) {
            // g2g
        }
        // The first coder is expected to populate the Avro SpecificData cache
        // The second coder is expected to be corrupted if the caching is done wrong.
        AvroCoder<Object> avroCoder1 = ((AvroCoder) (AvroCoder.of(pojoClass1)));
        AvroCoder<Object> avroCoder2 = ((AvroCoder) (AvroCoder.of(pojoClass2)));
        Object cloned1 = CoderUtils.clone(avroCoder1, pojo1);
        Object cloned2 = CoderUtils.clone(avroCoder2, pojo2);
        // Confirming that the uncorrupted coder is fine
        pojoClass1.cast(cloned1);
        // Confirmed to fail prior to the fix
        pojoClass2.cast(cloned2);
    }

    /**
     * Confirm that we can serialize and deserialize an AvroCoder object and still decode after.
     * (BEAM-349).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTransientFieldInitialization() throws Exception {
        AvroCoderTest.Pojo value = new AvroCoderTest.Pojo("Hello", 42);
        AvroCoder<AvroCoderTest.Pojo> coder = AvroCoder.of(AvroCoderTest.Pojo.class);
        // Serialization of object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(coder);
        // De-serialization of object
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        AvroCoder<AvroCoderTest.Pojo> copied = ((AvroCoder<AvroCoderTest.Pojo>) (in.readObject()));
        CoderProperties.coderDecodeEncodeEqual(copied, value);
    }

    /**
     * Confirm that we can serialize and deserialize an AvroCoder object using Kryo. (BEAM-626).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKryoSerialization() throws Exception {
        AvroCoderTest.Pojo value = new AvroCoderTest.Pojo("Hello", 42);
        AvroCoder<AvroCoderTest.Pojo> coder = AvroCoder.of(AvroCoderTest.Pojo.class);
        // Kryo instantiation
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        // Serialization of object without any memoization
        ByteArrayOutputStream coderWithoutMemoizationBos = new ByteArrayOutputStream();
        try (Output output = new Output(coderWithoutMemoizationBos)) {
            kryo.writeObject(output, coder);
        }
        // Force thread local memoization to store values.
        CoderProperties.coderDecodeEncodeEqual(coder, value);
        // Serialization of object with memoized fields
        ByteArrayOutputStream coderWithMemoizationBos = new ByteArrayOutputStream();
        try (Output output = new Output(coderWithMemoizationBos)) {
            kryo.writeObject(output, coder);
        }
        // Copy empty and memoized variants of the Coder
        ByteArrayInputStream bisWithoutMemoization = new ByteArrayInputStream(coderWithoutMemoizationBos.toByteArray());
        AvroCoder<AvroCoderTest.Pojo> copiedWithoutMemoization = ((AvroCoder<AvroCoderTest.Pojo>) (kryo.readObject(new Input(bisWithoutMemoization), AvroCoder.class)));
        ByteArrayInputStream bisWithMemoization = new ByteArrayInputStream(coderWithMemoizationBos.toByteArray());
        AvroCoder<AvroCoderTest.Pojo> copiedWithMemoization = ((AvroCoder<AvroCoderTest.Pojo>) (kryo.readObject(new Input(bisWithMemoization), AvroCoder.class)));
        CoderProperties.coderDecodeEncodeEqual(copiedWithoutMemoization, value);
        CoderProperties.coderDecodeEncodeEqual(copiedWithMemoization, value);
    }

    @Test
    public void testPojoEncoding() throws Exception {
        AvroCoderTest.Pojo value = new AvroCoderTest.Pojo("Hello", 42);
        AvroCoder<AvroCoderTest.Pojo> coder = AvroCoder.of(AvroCoderTest.Pojo.class);
        CoderProperties.coderDecodeEncodeEqual(coder, value);
    }

    @Test
    public void testGenericRecordEncoding() throws Exception {
        String schemaString = "{\"namespace\": \"example.avro\",\n" + (((((((" \"type\": \"record\",\n" + " \"name\": \"User\",\n") + " \"fields\": [\n") + "     {\"name\": \"name\", \"type\": \"string\"},\n") + "     {\"name\": \"favorite_number\", \"type\": [\"int\", \"null\"]},\n") + "     {\"name\": \"favorite_color\", \"type\": [\"string\", \"null\"]}\n") + " ]\n") + "}");
        Schema schema = new Schema.Parser().parse(schemaString);
        GenericRecord before = new org.apache.avro.generic.GenericData.Record(schema);
        before.put("name", "Bob");
        before.put("favorite_number", 256);
        // Leave favorite_color null
        AvroCoder<GenericRecord> coder = AvroCoder.of(GenericRecord.class, schema);
        CoderProperties.coderDecodeEncodeEqual(coder, before);
        Assert.assertEquals(schema, coder.getSchema());
    }

    @Test
    public void testEncodingNotBuffered() throws Exception {
        // This test ensures that the coder doesn't read ahead and buffer data.
        // Reading ahead causes a problem if the stream consists of records of different
        // types.
        AvroCoderTest.Pojo before = new AvroCoderTest.Pojo("Hello", 42);
        AvroCoder<AvroCoderTest.Pojo> coder = AvroCoder.of(AvroCoderTest.Pojo.class);
        SerializableCoder<Integer> intCoder = SerializableCoder.of(Integer.class);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Context context = Context.NESTED;
        coder.encode(before, outStream, context);
        intCoder.encode(10, outStream, context);
        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        AvroCoderTest.Pojo after = coder.decode(inStream, context);
        Assert.assertEquals(before, after);
        Integer intAfter = intCoder.decode(inStream, context);
        Assert.assertEquals(Integer.valueOf(10), intAfter);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testDefaultCoder() throws Exception {
        // Use MyRecord as input and output types without explicitly specifying
        // a coder (this uses the default coders, which may not be AvroCoder).
        PCollection<String> output = pipeline.apply(Create.of(new AvroCoderTest.Pojo("hello", 1), new AvroCoderTest.Pojo("world", 2))).apply(ParDo.of(new AvroCoderTest.GetTextFn()));
        PAssert.that(output).containsInAnyOrder("hello", "world");
        pipeline.run();
    }

    @Test
    public void testAvroCoderIsSerializable() throws Exception {
        AvroCoder<AvroCoderTest.Pojo> coder = AvroCoder.of(AvroCoderTest.Pojo.class);
        // Check that the coder is serializable using the regular JSON approach.
        SerializableUtils.ensureSerializable(coder);
    }

    @Test
    public void testDeterministicInteger() {
        assertDeterministic(AvroCoder.of(Integer.class));
    }

    @Test
    public void testDeterministicInt() {
        assertDeterministic(AvroCoder.of(int.class));
    }

    private static class SimpleDeterministicClass {
        @SuppressWarnings("unused")
        private Integer intField;

        @SuppressWarnings("unused")
        private char charField;

        @SuppressWarnings("unused")
        private Integer[] intArray;

        @SuppressWarnings("unused")
        private Utf8 utf8field;
    }

    @Test
    public void testDeterministicSimple() {
        assertDeterministic(AvroCoder.of(AvroCoderTest.SimpleDeterministicClass.class));
    }

    private static class UnorderedMapClass {
        @SuppressWarnings("unused")
        private Map<String, String> mapField;
    }

    @Test
    public void testDeterministicUnorderedMap() {
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.UnorderedMapClass.class), reasonField(AvroCoderTest.UnorderedMapClass.class, "mapField", ("java.util.Map<java.lang.String, java.lang.String> " + "may not be deterministically ordered")));
    }

    private static class NonDeterministicArray {
        @SuppressWarnings("unused")
        private AvroCoderTest.UnorderedMapClass[] arrayField;
    }

    @Test
    public void testDeterministicNonDeterministicArray() {
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.NonDeterministicArray.class), reasonField(AvroCoderTest.UnorderedMapClass.class, "mapField", ("java.util.Map<java.lang.String, java.lang.String>" + " may not be deterministically ordered")));
    }

    private static class SubclassOfUnorderedMapClass extends AvroCoderTest.UnorderedMapClass {}

    @Test
    public void testDeterministicNonDeterministicChild() {
        // Super class has non deterministic fields.
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.SubclassOfUnorderedMapClass.class), reasonField(AvroCoderTest.UnorderedMapClass.class, "mapField", "may not be deterministically ordered"));
    }

    private static class SubclassHidingParent extends AvroCoderTest.UnorderedMapClass {
        // AvroName is not enough
        @SuppressWarnings("unused")
        @AvroName("mapField2")
        private int mapField;
    }

    @Test
    public void testAvroProhibitsShadowing() {
        // This test verifies that Avro won't serialize a class with two fields of
        // the same name. This is important for our error reporting, and also how
        // we lookup a field.
        try {
            ReflectData.get().getSchema(AvroCoderTest.SubclassHidingParent.class);
            Assert.fail("Expected AvroTypeException");
        } catch (AvroRuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("mapField"));
            Assert.assertThat(e.getMessage(), Matchers.containsString("two fields named"));
        }
    }

    private static class FieldWithAvroName {
        @AvroName("name")
        @SuppressWarnings("unused")
        private int someField;
    }

    @Test
    public void testDeterministicWithAvroName() {
        assertDeterministic(AvroCoder.of(AvroCoderTest.FieldWithAvroName.class));
    }

    @Test
    public void testDeterminismSortedMap() {
        assertDeterministic(AvroCoder.of(AvroCoderTest.StringSortedMapField.class));
    }

    private static class StringSortedMapField {
        @SuppressWarnings("unused")
        SortedMap<String, String> sortedMapField;
    }

    @Test
    public void testDeterminismTreeMapValue() {
        // The value is non-deterministic, so we should fail.
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.TreeMapNonDetValue.class), reasonField(AvroCoderTest.UnorderedMapClass.class, "mapField", ("java.util.Map<java.lang.String, java.lang.String> " + "may not be deterministically ordered")));
    }

    private static class TreeMapNonDetValue {
        @SuppressWarnings("unused")
        TreeMap<String, AvroCoderTest.NonDeterministicArray> nonDeterministicField;
    }

    @Test
    public void testDeterminismUnorderedMap() {
        // LinkedHashMap is not deterministically ordered, so we should fail.
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.LinkedHashMapField.class), reasonField(AvroCoderTest.LinkedHashMapField.class, "nonDeterministicMap", ("java.util.LinkedHashMap<java.lang.String, java.lang.String> " + "may not be deterministically ordered")));
    }

    private static class LinkedHashMapField {
        @SuppressWarnings("unused")
        LinkedHashMap<String, String> nonDeterministicMap;
    }

    @Test
    public void testDeterminismCollection() {
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.StringCollection.class), reasonField(AvroCoderTest.StringCollection.class, "stringCollection", "java.util.Collection<java.lang.String> may not be deterministically ordered"));
    }

    private static class StringCollection {
        @SuppressWarnings("unused")
        Collection<String> stringCollection;
    }

    @Test
    public void testDeterminismList() {
        assertDeterministic(AvroCoder.of(AvroCoderTest.StringList.class));
        assertDeterministic(AvroCoder.of(AvroCoderTest.StringArrayList.class));
    }

    private static class StringList {
        @SuppressWarnings("unused")
        List<String> stringCollection;
    }

    private static class StringArrayList {
        @SuppressWarnings("unused")
        ArrayList<String> stringCollection;
    }

    @Test
    public void testDeterminismSet() {
        assertDeterministic(AvroCoder.of(AvroCoderTest.StringSortedSet.class));
        assertDeterministic(AvroCoder.of(AvroCoderTest.StringTreeSet.class));
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.StringHashSet.class), reasonField(AvroCoderTest.StringHashSet.class, "stringCollection", "java.util.HashSet<java.lang.String> may not be deterministically ordered"));
    }

    private static class StringSortedSet {
        @SuppressWarnings("unused")
        SortedSet<String> stringCollection;
    }

    private static class StringTreeSet {
        @SuppressWarnings("unused")
        TreeSet<String> stringCollection;
    }

    private static class StringHashSet {
        @SuppressWarnings("unused")
        HashSet<String> stringCollection;
    }

    @Test
    public void testDeterminismCollectionValue() {
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.OrderedSetOfNonDetValues.class), reasonField(AvroCoderTest.UnorderedMapClass.class, "mapField", "may not be deterministically ordered"));
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.ListOfNonDetValues.class), reasonField(AvroCoderTest.UnorderedMapClass.class, "mapField", "may not be deterministically ordered"));
    }

    private static class OrderedSetOfNonDetValues {
        @SuppressWarnings("unused")
        SortedSet<AvroCoderTest.UnorderedMapClass> set;
    }

    private static class ListOfNonDetValues {
        @SuppressWarnings("unused")
        List<AvroCoderTest.UnorderedMapClass> set;
    }

    @Test
    public void testDeterminismUnion() {
        assertDeterministic(AvroCoder.of(AvroCoderTest.DeterministicUnionBase.class));
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.NonDeterministicUnionBase.class), reasonField(AvroCoderTest.UnionCase3.class, "mapField", "may not be deterministically ordered"));
    }

    @Test
    public void testDeterminismStringable() {
        assertDeterministic(AvroCoder.of(String.class));
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.StringableClass.class), reasonClass(AvroCoderTest.StringableClass.class, "may not have deterministic #toString()"));
    }

    @Stringable
    private static class StringableClass {}

    @Test
    public void testDeterminismCyclicClass() {
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.Cyclic.class), reasonField(AvroCoderTest.Cyclic.class, "cyclicField", "appears recursively"));
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.CyclicField.class), reasonField(AvroCoderTest.Cyclic.class, "cyclicField", ((AvroCoderTest.Cyclic.class.getName()) + " appears recursively")));
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.IndirectCycle1.class), reasonField(AvroCoderTest.IndirectCycle2.class, "field2", ((AvroCoderTest.IndirectCycle1.class.getName()) + " appears recursively")));
    }

    private static class Cyclic {
        @SuppressWarnings("unused")
        int intField;

        @SuppressWarnings("unused")
        AvroCoderTest.Cyclic cyclicField;
    }

    private static class CyclicField {
        @SuppressWarnings("unused")
        AvroCoderTest.Cyclic cyclicField2;
    }

    private static class IndirectCycle1 {
        @SuppressWarnings("unused")
        AvroCoderTest.IndirectCycle2 field1;
    }

    private static class IndirectCycle2 {
        @SuppressWarnings("unused")
        AvroCoderTest.IndirectCycle1 field2;
    }

    @Test
    public void testDeterminismHasGenericRecord() {
        assertDeterministic(AvroCoder.of(AvroCoderTest.HasGenericRecord.class));
    }

    private static class HasGenericRecord {
        @AvroSchema("{\"name\": \"bar\", \"type\": \"record\", \"fields\": [" + "{\"name\": \"foo\", \"type\": \"int\"}]}")
        GenericRecord genericRecord;
    }

    @Test
    public void testDeterminismHasCustomSchema() {
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.HasCustomSchema.class), reasonField(AvroCoderTest.HasCustomSchema.class, "withCustomSchema", "Custom schemas are only supported for subtypes of IndexedRecord."));
    }

    private static class HasCustomSchema {
        @AvroSchema("{\"name\": \"bar\", \"type\": \"record\", \"fields\": [" + "{\"name\": \"foo\", \"type\": \"int\"}]}")
        int withCustomSchema;
    }

    @Test
    public void testAvroCoderTreeMapDeterminism() throws Exception, NonDeterministicException {
        AvroCoderTest.TreeMapField size1 = new AvroCoderTest.TreeMapField();
        AvroCoderTest.TreeMapField size2 = new AvroCoderTest.TreeMapField();
        // Different order for entries
        size1.field.put("hello", "world");
        size1.field.put("another", "entry");
        size2.field.put("another", "entry");
        size2.field.put("hello", "world");
        AvroCoder<AvroCoderTest.TreeMapField> coder = AvroCoder.of(AvroCoderTest.TreeMapField.class);
        coder.verifyDeterministic();
        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream outStream2 = new ByteArrayOutputStream();
        Context context = Context.NESTED;
        coder.encode(size1, outStream1, context);
        coder.encode(size2, outStream2, context);
        Assert.assertTrue(Arrays.equals(outStream1.toByteArray(), outStream2.toByteArray()));
    }

    private static class TreeMapField {
        private TreeMap<String, String> field = new TreeMap<>();
    }

    @Union({ AvroCoderTest.UnionCase1.class, AvroCoderTest.UnionCase2.class })
    private abstract static class DeterministicUnionBase {}

    @Union({ AvroCoderTest.UnionCase1.class, AvroCoderTest.UnionCase2.class, AvroCoderTest.UnionCase3.class })
    private abstract static class NonDeterministicUnionBase {}

    private static class UnionCase1 extends AvroCoderTest.DeterministicUnionBase {}

    private static class UnionCase2 extends AvroCoderTest.DeterministicUnionBase {
        @SuppressWarnings("unused")
        String field;
    }

    private static class UnionCase3 extends AvroCoderTest.NonDeterministicUnionBase {
        @SuppressWarnings("unused")
        private Map<String, String> mapField;
    }

    @Test
    public void testAvroCoderSimpleSchemaDeterminism() {
        assertDeterministic(AvroCoder.of(SchemaBuilder.record("someRecord").fields().endRecord()));
        assertDeterministic(AvroCoder.of(SchemaBuilder.record("someRecord").fields().name("int").type().intType().noDefault().endRecord()));
        assertDeterministic(AvroCoder.of(SchemaBuilder.record("someRecord").fields().name("string").type().stringType().noDefault().endRecord()));
        assertNonDeterministic(AvroCoder.of(SchemaBuilder.record("someRecord").fields().name("map").type().map().values().stringType().noDefault().endRecord()), reason("someRecord.map", "HashMap to represent MAPs"));
        assertDeterministic(AvroCoder.of(SchemaBuilder.record("someRecord").fields().name("array").type().array().items().stringType().noDefault().endRecord()));
        assertDeterministic(AvroCoder.of(SchemaBuilder.record("someRecord").fields().name("enum").type().enumeration("anEnum").symbols("s1", "s2").enumDefault("s1").endRecord()));
        assertDeterministic(AvroCoder.of(SchemaBuilder.unionOf().intType().and().record("someRecord").fields().nullableString("someField", "").endRecord().endUnion()));
    }

    @Test
    public void testAvroCoderStrings() {
        // Custom Strings in Records
        assertDeterministic(AvroCoder.of(SchemaBuilder.record("someRecord").fields().name("string").prop(CLASS_PROP, "java.lang.String").type().stringType().noDefault().endRecord()));
        assertNonDeterministic(AvroCoder.of(SchemaBuilder.record("someRecord").fields().name("string").prop(CLASS_PROP, "unknownString").type().stringType().noDefault().endRecord()), reason("someRecord.string", "unknownString is not known to be deterministic"));
        // Custom Strings in Unions
        assertNonDeterministic(AvroCoder.of(SchemaBuilder.unionOf().intType().and().record("someRecord").fields().name("someField").prop(CLASS_PROP, "unknownString").type().stringType().noDefault().endRecord().endUnion()), reason("someRecord.someField", "unknownString is not known to be deterministic"));
    }

    @Test
    public void testAvroCoderNestedRecords() {
        // Nested Record
        assertDeterministic(AvroCoder.of(SchemaBuilder.record("nestedRecord").fields().name("subRecord").type().record("subRecord").fields().name("innerField").type().stringType().noDefault().endRecord().noDefault().endRecord()));
    }

    @Test
    public void testAvroCoderCyclicRecords() {
        // Recursive record
        assertNonDeterministic(AvroCoder.of(SchemaBuilder.record("cyclicRecord").fields().name("cycle").type("cyclicRecord").noDefault().endRecord()), reason("cyclicRecord.cycle", "cyclicRecord appears recursively"));
    }

    private static class NullableField {
        @SuppressWarnings("unused")
        @Nullable
        private String nullable;
    }

    @Test
    public void testNullableField() {
        assertDeterministic(AvroCoder.of(AvroCoderTest.NullableField.class));
    }

    private static class NullableNonDeterministicField {
        @SuppressWarnings("unused")
        @Nullable
        private AvroCoderTest.NonDeterministicArray nullableNonDetArray;
    }

    private static class NullableCyclic {
        @SuppressWarnings("unused")
        @Nullable
        private AvroCoderTest.NullableCyclic nullableNullableCyclicField;
    }

    private static class NullableCyclicField {
        @SuppressWarnings("unused")
        @Nullable
        private AvroCoderTest.Cyclic nullableCyclicField;
    }

    @Test
    public void testNullableNonDeterministicField() {
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.NullableCyclic.class), reasonField(AvroCoderTest.NullableCyclic.class, "nullableNullableCyclicField", ((AvroCoderTest.NullableCyclic.class.getName()) + " appears recursively")));
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.NullableCyclicField.class), reasonField(AvroCoderTest.Cyclic.class, "cyclicField", ((AvroCoderTest.Cyclic.class.getName()) + " appears recursively")));
        assertNonDeterministic(AvroCoder.of(AvroCoderTest.NullableNonDeterministicField.class), reasonField(AvroCoderTest.UnorderedMapClass.class, "mapField", " may not be deterministically ordered"));
    }

    /**
     * Tests that a parameterized class can have an automatically generated schema if the generic
     * field is annotated with a union tag.
     */
    @Test
    public void testGenericClassWithUnionAnnotation() throws Exception {
        // Cast is safe as long as the same coder is used for encoding and decoding.
        @SuppressWarnings({ "unchecked", "rawtypes" })
        AvroCoder<AvroCoderTest.GenericWithAnnotation<String>> coder = ((AvroCoder) (AvroCoder.of(AvroCoderTest.GenericWithAnnotation.class)));
        Assert.assertThat(coder.getSchema().getField("onlySomeTypesAllowed").schema().getType(), Matchers.equalTo(UNION));
        CoderProperties.coderDecodeEncodeEqual(coder, new AvroCoderTest.GenericWithAnnotation("hello"));
    }

    private static class GenericWithAnnotation<T> {
        @AvroSchema("[\"string\", \"int\"]")
        private T onlySomeTypesAllowed;

        public GenericWithAnnotation(T value) {
            onlySomeTypesAllowed = value;
        }

        // For deserialization only
        @SuppressWarnings("unused")
        protected GenericWithAnnotation() {
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof AvroCoderTest.GenericWithAnnotation) && (onlySomeTypesAllowed.equals(((AvroCoderTest.GenericWithAnnotation<?>) (other)).onlySomeTypesAllowed));
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClass(), onlySomeTypesAllowed);
        }
    }

    @Test
    public void testAvroCoderForGenerics() throws Exception {
        Schema fooSchema = AvroCoder.of(AvroCoderTest.Foo.class).getSchema();
        Schema schema = new Schema.Parser().parse((((("{" + (((("\"type\":\"record\"," + "\"name\":\"SomeGeneric\",") + "\"namespace\":\"ns\",") + "\"fields\":[") + "  {\"name\":\"foo\", \"type\":")) + (fooSchema.toString())) + "}") + "]}"));
        @SuppressWarnings("rawtypes")
        AvroCoder<AvroCoderTest.SomeGeneric> coder = AvroCoder.of(AvroCoderTest.SomeGeneric.class, schema);
        assertNonDeterministic(coder, reasonField(AvroCoderTest.SomeGeneric.class, "foo", "erasure"));
    }

    @Test
    public void testEncodedTypeDescriptor() throws Exception {
        AvroCoder<AvroCoderTest.Pojo> coder = AvroCoder.of(AvroCoderTest.Pojo.class);
        Assert.assertThat(coder.getEncodedTypeDescriptor(), Matchers.equalTo(TypeDescriptor.of(AvroCoderTest.Pojo.class)));
    }

    private static class SomeGeneric<T> {
        @SuppressWarnings("unused")
        private T foo;
    }

    private static class Foo {
        @SuppressWarnings("unused")
        String id;
    }
}

