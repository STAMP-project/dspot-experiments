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


import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.beam.sdk.coders.CoderRegistry.IncompatibleCoderException;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.util.common.ElementByteSizeObserver;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for CoderRegistry.
 */
@RunWith(JUnit4.class)
public class CoderRegistryTest {
    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public ExpectedLogs expectedLogs = ExpectedLogs.none(CoderRegistry.class);

    private static class SerializableClass implements Serializable {}

    private static class NotSerializableClass {}

    @Test
    public void testRegisterInstantiatedCoder() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        registry.registerCoderForClass(CoderRegistryTest.MyValue.class, CoderRegistryTest.MyValueCoder.of());
        Assert.assertEquals(registry.getCoder(CoderRegistryTest.MyValue.class), CoderRegistryTest.MyValueCoder.of());
    }

    @Test
    public void testSimpleDefaultCoder() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        Assert.assertEquals(StringUtf8Coder.of(), registry.getCoder(String.class));
    }

    @Test
    public void testSimpleUnknownDefaultCoder() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        thrown.expect(CannotProvideCoderException.class);
        thrown.expectMessage(Matchers.allOf(Matchers.containsString(CoderRegistryTest.UnknownType.class.getName()), Matchers.containsString("Unable to provide a Coder for")));
        registry.getCoder(CoderRegistryTest.UnknownType.class);
    }

    @Test
    public void testParameterizedDefaultListCoder() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        TypeDescriptor<List<Integer>> listToken = new TypeDescriptor<List<Integer>>() {};
        Assert.assertEquals(ListCoder.of(VarIntCoder.of()), registry.getCoder(listToken));
        registry.registerCoderProvider(CoderProviders.fromStaticMethods(CoderRegistryTest.MyValue.class, CoderRegistryTest.MyValueCoder.class));
        TypeDescriptor<KV<String, List<CoderRegistryTest.MyValue>>> kvToken = new TypeDescriptor<KV<String, List<CoderRegistryTest.MyValue>>>() {};
        Assert.assertEquals(KvCoder.of(StringUtf8Coder.of(), ListCoder.of(CoderRegistryTest.MyValueCoder.of())), registry.getCoder(kvToken));
    }

    @Test
    public void testParameterizedDefaultMapCoder() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        TypeDescriptor<Map<Integer, String>> mapToken = new TypeDescriptor<Map<Integer, String>>() {};
        Assert.assertEquals(MapCoder.of(VarIntCoder.of(), StringUtf8Coder.of()), registry.getCoder(mapToken));
    }

    @Test
    public void testParameterizedDefaultNestedMapCoder() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        TypeDescriptor<Map<Integer, Map<String, Double>>> mapToken = new TypeDescriptor<Map<Integer, Map<String, Double>>>() {};
        Assert.assertEquals(MapCoder.of(VarIntCoder.of(), MapCoder.of(StringUtf8Coder.of(), DoubleCoder.of())), registry.getCoder(mapToken));
    }

    @Test
    public void testParameterizedDefaultSetCoder() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        TypeDescriptor<Set<Integer>> setToken = new TypeDescriptor<Set<Integer>>() {};
        Assert.assertEquals(SetCoder.of(VarIntCoder.of()), registry.getCoder(setToken));
    }

    @Test
    public void testParameterizedDefaultNestedSetCoder() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        TypeDescriptor<Set<Set<Integer>>> setToken = new TypeDescriptor<Set<Set<Integer>>>() {};
        Assert.assertEquals(SetCoder.of(SetCoder.of(VarIntCoder.of())), registry.getCoder(setToken));
    }

    @Test
    public void testParameterizedDefaultCoderUnknown() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        TypeDescriptor<List<CoderRegistryTest.UnknownType>> listUnknownToken = new TypeDescriptor<List<CoderRegistryTest.UnknownType>>() {};
        thrown.expect(CannotProvideCoderException.class);
        thrown.expectMessage(String.format("Cannot provide coder for parameterized type %s: Unable to provide a Coder for %s", listUnknownToken, CoderRegistryTest.UnknownType.class.getName()));
        registry.getCoder(listUnknownToken);
    }

    @Test
    public void testParameterizedWildcardTypeIsUnknown() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        TypeDescriptor<List<? extends CoderRegistryTest.MyValue>> wildcardUnknownToken = new TypeDescriptor<List<? extends CoderRegistryTest.MyValue>>() {};
        thrown.expect(CannotProvideCoderException.class);
        thrown.expectMessage(String.format("Cannot provide coder for parameterized type %s: Cannot provide a coder for wildcard type %s.", wildcardUnknownToken, ((ParameterizedType) (wildcardUnknownToken.getType())).getActualTypeArguments()[0]));
        registry.getCoder(wildcardUnknownToken);
    }

    @Test
    public void testTypeParameterInferenceForward() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        CoderRegistryTest.MyGenericClass<CoderRegistryTest.MyValue, List<CoderRegistryTest.MyValue>> instance = new CoderRegistryTest.MyGenericClass<CoderRegistryTest.MyValue, List<CoderRegistryTest.MyValue>>() {};
        Coder<?> bazCoder = registry.getCoder(instance.getClass(), CoderRegistryTest.MyGenericClass.class, Collections.<Type, Coder<?>>singletonMap(TypeDescriptor.of(CoderRegistryTest.MyGenericClass.class).getTypeParameter("FooT"), CoderRegistryTest.MyValueCoder.of()), TypeDescriptor.of(CoderRegistryTest.MyGenericClass.class).getTypeParameter("BazT"));
        Assert.assertEquals(ListCoder.of(CoderRegistryTest.MyValueCoder.of()), bazCoder);
    }

    @Test
    public void testTypeParameterInferenceBackward() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        CoderRegistryTest.MyGenericClass<CoderRegistryTest.MyValue, List<CoderRegistryTest.MyValue>> instance = new CoderRegistryTest.MyGenericClass<CoderRegistryTest.MyValue, List<CoderRegistryTest.MyValue>>() {};
        Coder<?> fooCoder = registry.getCoder(instance.getClass(), CoderRegistryTest.MyGenericClass.class, Collections.<Type, Coder<?>>singletonMap(TypeDescriptor.of(CoderRegistryTest.MyGenericClass.class).getTypeParameter("BazT"), ListCoder.of(CoderRegistryTest.MyValueCoder.of())), TypeDescriptor.of(CoderRegistryTest.MyGenericClass.class).getTypeParameter("FooT"));
        Assert.assertEquals(CoderRegistryTest.MyValueCoder.of(), fooCoder);
    }

    @Test
    public void testTypeCompatibility() throws Exception {
        CoderRegistry.verifyCompatible(BigEndianIntegerCoder.of(), Integer.class);
        CoderRegistry.verifyCompatible(ListCoder.of(BigEndianIntegerCoder.of()), getType());
    }

    @Test
    public void testIntVersusStringIncompatibility() throws Exception {
        thrown.expect(IncompatibleCoderException.class);
        thrown.expectMessage("not assignable");
        CoderRegistry.verifyCompatible(BigEndianIntegerCoder.of(), String.class);
    }

    // BEAM-3160: We can't choose between the BigEndianIntegerCoder or the VarIntCoder since
    // they are incompatible.
    @Test
    public void testTypeOverSpecifiedWithMultipleCoders() throws Exception {
        thrown.expect(CannotProvideCoderException.class);
        thrown.expectMessage("type is over specified");
        CoderRegistry.createDefault().getCoder(new TypeDescriptor<Integer>() {}, new TypeDescriptor<KV<Integer, Integer>>() {}, KvCoder.of(BigEndianIntegerCoder.of(), VarIntCoder.of()));
    }

    private static class TooManyComponentCoders<T> extends ListCoder<T> {
        public TooManyComponentCoders(Coder<T> actualComponentCoder) {
            super(actualComponentCoder);
        }

        @Override
        public List<? extends Coder<?>> getCoderArguments() {
            return ImmutableList.<Coder<?>>builder().addAll(super.getCoderArguments()).add(BigEndianLongCoder.of()).build();
        }
    }

    @Test
    public void testTooManyCoderArguments() throws Exception {
        thrown.expect(IncompatibleCoderException.class);
        thrown.expectMessage("type parameters");
        thrown.expectMessage("less than the number of coder arguments");
        CoderRegistry.verifyCompatible(new CoderRegistryTest.TooManyComponentCoders(BigEndianIntegerCoder.of()), List.class);
    }

    @Test
    public void testComponentIncompatibility() throws Exception {
        thrown.expect(IncompatibleCoderException.class);
        thrown.expectMessage("component coder is incompatible");
        CoderRegistry.verifyCompatible(ListCoder.of(BigEndianIntegerCoder.of()), getType());
    }

    @Test
    public void testDefaultCoderAnnotationGenericRawtype() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        Assert.assertEquals(registry.getCoder(CoderRegistryTest.MySerializableGeneric.class), SerializableCoder.of(CoderRegistryTest.MySerializableGeneric.class));
    }

    @Test
    public void testDefaultCoderAnnotationGeneric() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        Assert.assertEquals(registry.getCoder(new TypeDescriptor<CoderRegistryTest.MySerializableGeneric<String>>() {}), SerializableCoder.of(CoderRegistryTest.MySerializableGeneric.class));
    }

    private static class PTransformOutputingMySerializableGeneric extends PTransform<PCollection<String>, PCollection<KV<String, CoderRegistryTest.MySerializableGeneric<String>>>> {
        private static class OutputDoFn extends DoFn<String, KV<String, CoderRegistryTest.MySerializableGeneric<String>>> {
            @ProcessElement
            public void processElement(ProcessContext c) {
            }
        }

        @Override
        public PCollection<KV<String, CoderRegistryTest.MySerializableGeneric<String>>> expand(PCollection<String> input) {
            return input.apply(ParDo.of(new CoderRegistryTest.PTransformOutputingMySerializableGeneric.OutputDoFn()));
        }
    }

    /**
     * Tests that the error message for a type variable includes a mention of where the type variable
     * was declared.
     */
    @Test
    public void testTypeVariableErrorMessage() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        thrown.expect(CannotProvideCoderException.class);
        thrown.expectMessage("Unable to provide a Coder");
        registry.getCoder(TypeDescriptor.of(CoderRegistryTest.TestGenericClass.class.getTypeParameters()[0]));
    }

    private static class TestGenericClass<TestGenericT> {}

    @Test
    @SuppressWarnings("rawtypes")
    public void testSerializableTypeVariableDefaultCoder() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        TypeDescriptor type = TypeDescriptor.of(CoderRegistryTest.TestSerializableGenericClass.class.getTypeParameters()[0]);
        Assert.assertEquals(SerializableCoder.of(type), registry.getCoder(type));
    }

    private static class TestSerializableGenericClass<TestGenericT extends Serializable> {}

    /**
     * In-context test that assures the functionality tested in {@link #testDefaultCoderAnnotationGeneric} is invoked in the right ways.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testSpecializedButIgnoredGenericInPipeline() throws Exception {
        pipeline.apply(Create.of("hello", "goodbye")).apply(new CoderRegistryTest.PTransformOutputingMySerializableGeneric());
        pipeline.run();
    }

    private static class GenericOutputMySerializedGeneric<T extends Serializable> extends PTransform<PCollection<String>, PCollection<KV<String, CoderRegistryTest.MySerializableGeneric<T>>>> {
        private class OutputDoFn extends DoFn<String, KV<String, CoderRegistryTest.MySerializableGeneric<T>>> {
            @ProcessElement
            public void processElement(ProcessContext c) {
            }
        }

        @Override
        public PCollection<KV<String, CoderRegistryTest.MySerializableGeneric<T>>> expand(PCollection<String> input) {
            return input.apply(ParDo.of(new OutputDoFn()));
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testIgnoredGenericInPipeline() throws Exception {
        pipeline.apply(Create.of("hello", "goodbye")).apply(new CoderRegistryTest.GenericOutputMySerializedGeneric<String>());
        pipeline.run();
    }

    private static class MyGenericClass<FooT, BazT> {}

    private static class MyValue {}

    private static class MyValueCoder extends AtomicCoder<CoderRegistryTest.MyValue> {
        private static final CoderRegistryTest.MyValueCoder INSTANCE = new CoderRegistryTest.MyValueCoder();

        private static final TypeDescriptor<CoderRegistryTest.MyValue> TYPE_DESCRIPTOR = TypeDescriptor.of(CoderRegistryTest.MyValue.class);

        public static CoderRegistryTest.MyValueCoder of() {
            return CoderRegistryTest.MyValueCoder.INSTANCE;
        }

        @Override
        public void encode(CoderRegistryTest.MyValue value, OutputStream outStream) throws IOException, CoderException {
        }

        @Override
        public CoderRegistryTest.MyValue decode(InputStream inStream) throws IOException, CoderException {
            return new CoderRegistryTest.MyValue();
        }

        @Override
        public void verifyDeterministic() {
        }

        @Override
        public boolean consistentWithEquals() {
            return true;
        }

        @Override
        public Object structuralValue(CoderRegistryTest.MyValue value) {
            return value;
        }

        @Override
        public boolean isRegisterByteSizeObserverCheap(CoderRegistryTest.MyValue value) {
            return true;
        }

        @Override
        public void registerByteSizeObserver(CoderRegistryTest.MyValue value, ElementByteSizeObserver observer) throws Exception {
            observer.update(0L);
        }

        @Override
        public TypeDescriptor<CoderRegistryTest.MyValue> getEncodedTypeDescriptor() {
            return CoderRegistryTest.MyValueCoder.TYPE_DESCRIPTOR;
        }
    }

    /**
     * This type is incompatible with all known coder providers such as Serializable,
     * {@code @DefaultCoder} which allows testing scenarios where coder lookup fails.
     */
    private static class UnknownType {}

    @DefaultCoder(SerializableCoder.class)
    private static class MySerializableGeneric<T extends Serializable> implements Serializable {
        @SuppressWarnings("unused")
        private T foo;
    }

    /**
     * This type is incompatible with all known coder providers such as Serializable,
     * {@code @DefaultCoder} which allows testing the automatic registration mechanism.
     */
    private static class AutoRegistrationClass {}

    private static class AutoRegistrationClassCoder extends CustomCoder<CoderRegistryTest.AutoRegistrationClass> {
        private static final CoderRegistryTest.AutoRegistrationClassCoder INSTANCE = new CoderRegistryTest.AutoRegistrationClassCoder();

        @Override
        public void encode(CoderRegistryTest.AutoRegistrationClass value, OutputStream outStream) {
        }

        @Override
        public CoderRegistryTest.AutoRegistrationClass decode(InputStream inStream) {
            return null;
        }
    }

    @Test
    public void testAutomaticRegistrationOfCoderProviders() throws Exception {
        Assert.assertEquals(CoderRegistryTest.AutoRegistrationClassCoder.INSTANCE, CoderRegistry.createDefault().getCoder(CoderRegistryTest.AutoRegistrationClass.class));
    }

    /**
     * A {@link CoderProviderRegistrar} to demonstrate default {@link Coder} registration.
     */
    @AutoService(CoderProviderRegistrar.class)
    public static class RegisteredTestCoderProviderRegistrar implements CoderProviderRegistrar {
        @Override
        public List<CoderProvider> getCoderProviders() {
            return ImmutableList.of(CoderProviders.forCoder(TypeDescriptor.of(CoderRegistryTest.AutoRegistrationClass.class), CoderRegistryTest.AutoRegistrationClassCoder.INSTANCE));
        }
    }

    @Test
    public void testCoderPrecedence() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        // DefaultCoder precedes CoderProviderRegistrar
        Assert.assertEquals(AvroCoder.of(CoderRegistryTest.MyValueA.class), registry.getCoder(CoderRegistryTest.MyValueA.class));
        // CoderProviderRegistrar precedes SerializableCoder
        Assert.assertEquals(CoderRegistryTest.MyValueBCoder.INSTANCE, registry.getCoder(CoderRegistryTest.MyValueB.class));
        // fallbacks to SerializableCoder at last
        Assert.assertEquals(SerializableCoder.of(CoderRegistryTest.MyValueC.class), registry.getCoder(CoderRegistryTest.MyValueC.class));
    }

    @DefaultCoder(AvroCoder.class)
    private static class MyValueA implements Serializable {}

    private static class MyValueB implements Serializable {}

    private static class MyValueC implements Serializable {}

    private static class MyValueACoder extends CustomCoder<CoderRegistryTest.MyValueA> {
        private static final CoderRegistryTest.MyValueACoder INSTANCE = new CoderRegistryTest.MyValueACoder();

        @Override
        public void encode(CoderRegistryTest.MyValueA value, OutputStream outStream) throws IOException, CoderException {
        }

        @Override
        public CoderRegistryTest.MyValueA decode(InputStream inStream) throws IOException, CoderException {
            return null;
        }
    }

    /**
     * A {@link CoderProviderRegistrar} to demonstrate default {@link Coder} registration.
     */
    @AutoService(CoderProviderRegistrar.class)
    public static class MyValueACoderProviderRegistrar implements CoderProviderRegistrar {
        @Override
        public List<CoderProvider> getCoderProviders() {
            return ImmutableList.of(CoderProviders.forCoder(TypeDescriptor.of(CoderRegistryTest.MyValueA.class), CoderRegistryTest.MyValueACoder.INSTANCE));
        }
    }

    private static class MyValueBCoder extends CustomCoder<CoderRegistryTest.MyValueB> {
        private static final CoderRegistryTest.MyValueBCoder INSTANCE = new CoderRegistryTest.MyValueBCoder();

        @Override
        public void encode(CoderRegistryTest.MyValueB value, OutputStream outStream) throws IOException, CoderException {
        }

        @Override
        public CoderRegistryTest.MyValueB decode(InputStream inStream) throws IOException, CoderException {
            return null;
        }
    }

    /**
     * A {@link CoderProviderRegistrar} to demonstrate default {@link Coder} registration.
     */
    @AutoService(CoderProviderRegistrar.class)
    public static class MyValueBCoderProviderRegistrar implements CoderProviderRegistrar {
        @Override
        public List<CoderProvider> getCoderProviders() {
            return ImmutableList.of(CoderProviders.forCoder(TypeDescriptor.of(CoderRegistryTest.MyValueB.class), CoderRegistryTest.MyValueBCoder.INSTANCE));
        }
    }
}

