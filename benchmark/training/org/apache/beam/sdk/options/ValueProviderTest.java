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
package org.apache.beam.sdk.options;


import Default.String;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.beam.sdk.options.ValueProvider.NestedValueProvider;
import org.apache.beam.sdk.options.ValueProvider.RuntimeValueProvider;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.util.common.ReflectHelpers;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ValueProvider}.
 */
@RunWith(JUnit4.class)
public class ValueProviderTest {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModules(ObjectMapper.findModules(ReflectHelpers.findClassLoader()));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * A test interface.
     */
    public interface TestOptions extends PipelineOptions {
        @Default.String("bar")
        ValueProvider<String> getBar();

        void setBar(ValueProvider<String> bar);

        ValueProvider<String> getFoo();

        void setFoo(ValueProvider<String> foo);

        ValueProvider<List<Integer>> getList();

        void setList(ValueProvider<List<Integer>> list);
    }

    @Test
    public void testCommandLineNoDefault() {
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.fromArgs("--foo=baz").as(ValueProviderTest.TestOptions.class);
        ValueProvider<String> provider = options.getFoo();
        Assert.assertEquals("baz", provider.get());
        Assert.assertTrue(provider.isAccessible());
    }

    @Test
    public void testListValueProvider() {
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.fromArgs("--list=1,2,3").as(ValueProviderTest.TestOptions.class);
        ValueProvider<List<Integer>> provider = options.getList();
        Assert.assertEquals(ImmutableList.of(1, 2, 3), provider.get());
        Assert.assertTrue(provider.isAccessible());
    }

    @Test
    public void testCommandLineWithDefault() {
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.fromArgs("--bar=baz").as(ValueProviderTest.TestOptions.class);
        ValueProvider<String> provider = options.getBar();
        Assert.assertEquals("baz", provider.get());
        Assert.assertTrue(provider.isAccessible());
    }

    @Test
    public void testStaticValueProvider() {
        ValueProvider<String> provider = StaticValueProvider.of("foo");
        Assert.assertEquals("foo", provider.get());
        Assert.assertTrue(provider.isAccessible());
        Assert.assertEquals("foo", provider.toString());
    }

    @Test
    public void testNoDefaultRuntimeProvider() {
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.as(ValueProviderTest.TestOptions.class);
        ValueProvider<String> provider = options.getFoo();
        Assert.assertFalse(provider.isAccessible());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Value only available at runtime");
        expectedException.expectMessage("foo");
        provider.get();
    }

    @Test
    public void testRuntimePropertyName() {
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.as(ValueProviderTest.TestOptions.class);
        ValueProvider<String> provider = options.getFoo();
        Assert.assertEquals("foo", propertyName());
        Assert.assertEquals("RuntimeValueProvider{propertyName=foo, default=null}", provider.toString());
    }

    @Test
    public void testDefaultRuntimeProvider() {
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.as(ValueProviderTest.TestOptions.class);
        ValueProvider<String> provider = options.getBar();
        Assert.assertFalse(provider.isAccessible());
    }

    @Test
    public void testNoDefaultRuntimeProviderWithOverride() throws Exception {
        ValueProviderTest.TestOptions runtime = ValueProviderTest.MAPPER.readValue("{ \"options\": { \"foo\": \"quux\" }}", PipelineOptions.class).as(ValueProviderTest.TestOptions.class);
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.as(ValueProviderTest.TestOptions.class);
        runtime.setOptionsId(getOptionsId());
        RuntimeValueProvider.setRuntimeOptions(runtime);
        ValueProvider<String> provider = options.getFoo();
        Assert.assertTrue(provider.isAccessible());
        Assert.assertEquals("quux", provider.get());
    }

    @Test
    public void testDefaultRuntimeProviderWithOverride() throws Exception {
        ValueProviderTest.TestOptions runtime = ValueProviderTest.MAPPER.readValue("{ \"options\": { \"bar\": \"quux\" }}", PipelineOptions.class).as(ValueProviderTest.TestOptions.class);
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.as(ValueProviderTest.TestOptions.class);
        runtime.setOptionsId(getOptionsId());
        RuntimeValueProvider.setRuntimeOptions(runtime);
        ValueProvider<String> provider = options.getBar();
        Assert.assertTrue(provider.isAccessible());
        Assert.assertEquals("quux", provider.get());
    }

    @Test
    public void testDefaultRuntimeProviderWithoutOverride() throws Exception {
        ValueProviderTest.TestOptions runtime = PipelineOptionsFactory.as(ValueProviderTest.TestOptions.class);
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.as(ValueProviderTest.TestOptions.class);
        runtime.setOptionsId(getOptionsId());
        RuntimeValueProvider.setRuntimeOptions(runtime);
        ValueProvider<String> provider = options.getBar();
        Assert.assertTrue(provider.isAccessible());
        Assert.assertEquals("bar", provider.get());
    }

    /**
     * A test interface.
     */
    public interface BadOptionsRuntime extends PipelineOptions {
        RuntimeValueProvider<String> getBar();

        void setBar(RuntimeValueProvider<String> bar);
    }

    @Test
    public void testOptionReturnTypeRuntime() {
        ValueProviderTest.BadOptionsRuntime options = PipelineOptionsFactory.as(ValueProviderTest.BadOptionsRuntime.class);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(("Method getBar should not have return type " + "RuntimeValueProvider, use ValueProvider instead."));
        options.getBar();
    }

    /**
     * A test interface.
     */
    public interface BadOptionsStatic extends PipelineOptions {
        StaticValueProvider<String> getBar();

        void setBar(StaticValueProvider<String> bar);
    }

    @Test
    public void testOptionReturnTypeStatic() {
        ValueProviderTest.BadOptionsStatic options = PipelineOptionsFactory.as(ValueProviderTest.BadOptionsStatic.class);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(("Method getBar should not have return type " + "StaticValueProvider, use ValueProvider instead."));
        options.getBar();
    }

    @Test
    public void testSerializeDeserializeNoArg() throws Exception {
        ValueProviderTest.TestOptions submitOptions = PipelineOptionsFactory.as(ValueProviderTest.TestOptions.class);
        Assert.assertFalse(submitOptions.getFoo().isAccessible());
        ObjectNode root = ValueProviderTest.MAPPER.valueToTree(submitOptions);
        ((ObjectNode) (root.get("options"))).put("foo", "quux");
        ValueProviderTest.TestOptions runtime = ValueProviderTest.MAPPER.convertValue(root, PipelineOptions.class).as(ValueProviderTest.TestOptions.class);
        ValueProvider<String> vp = runtime.getFoo();
        Assert.assertTrue(vp.isAccessible());
        Assert.assertEquals("quux", vp.get());
        Assert.assertEquals(vp.getClass(), StaticValueProvider.class);
    }

    @Test
    public void testSerializeDeserializeWithArg() throws Exception {
        ValueProviderTest.TestOptions submitOptions = PipelineOptionsFactory.fromArgs("--foo=baz").as(ValueProviderTest.TestOptions.class);
        Assert.assertTrue(submitOptions.getFoo().isAccessible());
        Assert.assertEquals("baz", submitOptions.getFoo().get());
        ObjectNode root = ValueProviderTest.MAPPER.valueToTree(submitOptions);
        ((ObjectNode) (root.get("options"))).put("foo", "quux");
        ValueProviderTest.TestOptions runtime = ValueProviderTest.MAPPER.convertValue(root, PipelineOptions.class).as(ValueProviderTest.TestOptions.class);
        ValueProvider<String> vp = runtime.getFoo();
        Assert.assertTrue(vp.isAccessible());
        Assert.assertEquals("quux", vp.get());
    }

    @Test
    public void testNestedValueProviderStatic() throws Exception {
        ValueProvider<String> svp = StaticValueProvider.of("foo");
        ValueProvider<String> nvp = NestedValueProvider.of(svp, ( from) -> from + "bar");
        Assert.assertTrue(nvp.isAccessible());
        Assert.assertEquals("foobar", nvp.get());
        Assert.assertEquals("foobar", nvp.toString());
    }

    @Test
    public void testNestedValueProviderRuntime() throws Exception {
        ValueProviderTest.TestOptions options = PipelineOptionsFactory.as(ValueProviderTest.TestOptions.class);
        ValueProvider<String> rvp = options.getBar();
        ValueProvider<String> nvp = NestedValueProvider.of(rvp, ( from) -> from + "bar");
        ValueProvider<String> doubleNvp = NestedValueProvider.of(nvp, ( from) -> from);
        Assert.assertEquals("bar", propertyName());
        Assert.assertEquals("bar", propertyName());
        Assert.assertFalse(nvp.isAccessible());
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Value only available at runtime");
        nvp.get();
    }

    private static class NonSerializable {}

    private static class NonSerializableTranslator implements SerializableFunction<String, ValueProviderTest.NonSerializable> {
        @Override
        public ValueProviderTest.NonSerializable apply(String from) {
            return new ValueProviderTest.NonSerializable();
        }
    }

    @Test
    public void testNestedValueProviderSerialize() throws Exception {
        ValueProvider<ValueProviderTest.NonSerializable> nvp = NestedValueProvider.of(StaticValueProvider.of("foo"), new ValueProviderTest.NonSerializableTranslator());
        SerializableUtils.ensureSerializable(nvp);
    }

    private static class IncrementAtomicIntegerTranslator implements SerializableFunction<AtomicInteger, Integer> {
        @Override
        public Integer apply(AtomicInteger from) {
            return from.incrementAndGet();
        }
    }

    @Test
    public void testNestedValueProviderCached() throws Exception {
        AtomicInteger increment = new AtomicInteger();
        ValueProvider<Integer> nvp = NestedValueProvider.of(StaticValueProvider.of(increment), new ValueProviderTest.IncrementAtomicIntegerTranslator());
        Integer originalValue = nvp.get();
        Integer cachedValue = nvp.get();
        Integer incrementValue = increment.incrementAndGet();
        Integer secondCachedValue = nvp.get();
        Assert.assertEquals(originalValue, cachedValue);
        Assert.assertEquals(secondCachedValue, cachedValue);
        Assert.assertNotEquals(originalValue, incrementValue);
    }
}

