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


import DisplayData.Builder;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataMatchers;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
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

import static org.apache.beam.sdk.values.TypeDescriptors.integers;


/**
 * Tests for {@link FlatMapElements}.
 */
@RunWith(JUnit4.class)
public class FlatMapElementsTest implements Serializable {
    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    /**
     * Basic test of {@link FlatMapElements} with a {@link SimpleFunction}.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testFlatMapSimpleFunction() throws Exception {
        PCollection<Integer> output = // Note that FlatMapElements takes an InferableFunction<InputT, ? extends
        // Iterable<OutputT>>
        // so the use of List<Integer> here (as opposed to Iterable<Integer>) deliberately
        // exercises
        // the use of an upper bound.
        pipeline.apply(Create.of(1, 2, 3)).apply(FlatMapElements.via(new SimpleFunction<Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer input) {
                return ImmutableList.of((-input), input);
            }
        }));
        PAssert.that(output).containsInAnyOrder(1, (-2), (-1), (-3), 2, 3);
        pipeline.run();
    }

    /**
     * Basic test of {@link FlatMapElements} with an {@link InferableFunction}.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testFlatMapInferableFunction() throws Exception {
        PCollection<Integer> output = pipeline.apply(Create.of(1, 2, 3)).apply(FlatMapElements.via(new InferableFunction<Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer input) throws Exception {
                return ImmutableList.of((-input), input);
            }
        }));
        PAssert.that(output).containsInAnyOrder(1, (-2), (-1), (-3), 2, 3);
        pipeline.run();
    }

    /**
     * Basic test of {@link FlatMapElements} with a {@link Fn} and a side input.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testFlatMapBasicWithSideInput() throws Exception {
        final PCollectionView<Integer> view = pipeline.apply("Create base", Create.of(40)).apply(View.asSingleton());
        PCollection<Integer> output = pipeline.apply(Create.of(0, 1, 2)).apply(FlatMapElements.into(integers()).via(Contextful.fn(( input, c) -> ImmutableList.of(((c.sideInput(view)) - input), ((c.sideInput(view)) + input)), Requirements.requiresSideInputs(view))));
        PAssert.that(output).containsInAnyOrder(38, 39, 40, 40, 41, 42);
        pipeline.run();
    }

    /**
     * Tests that when built with a concrete subclass of {@link SimpleFunction}, the type descriptor
     * of the output reflects its static type.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testFlatMapFnOutputTypeDescriptor() throws Exception {
        PCollection<String> output = pipeline.apply(Create.of("hello")).apply(FlatMapElements.via(new SimpleFunction<String, java.util.Set<String>>() {
            @Override
            public java.util.Set<String> apply(String input) {
                return org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet.copyOf(input.split(""));
            }
        }));
        Assert.assertThat(output.getTypeDescriptor(), Matchers.equalTo(((TypeDescriptor<String>) (new TypeDescriptor<String>() {}))));
        Assert.assertThat(pipeline.getCoderRegistry().getCoder(output.getTypeDescriptor()), Matchers.equalTo(pipeline.getCoderRegistry().getCoder(new TypeDescriptor<String>() {})));
        // Make sure the pipeline runs
        pipeline.run();
    }

    /**
     * A {@link SimpleFunction} to test that the coder registry can propagate coders that are bound to
     * type variables.
     */
    private static class PolymorphicSimpleFunction<T> extends SimpleFunction<T, Iterable<T>> {
        @Override
        public Iterable<T> apply(T input) {
            return Collections.emptyList();
        }
    }

    /**
     * Basic test of {@link MapElements} coder propagation with a parametric {@link SimpleFunction}.
     */
    @Test
    public void testPolymorphicSimpleFunction() throws Exception {
        pipeline.enableAbandonedNodeEnforcement(false);
        // This is a consumer to ensure that all coder inference logic is executed.
        // This is the function that needs to propagate the input T to output T
        pipeline.apply(Create.of(1, 2, 3)).apply("Polymorphic Identity", MapElements.via(new FlatMapElementsTest.PolymorphicSimpleFunction())).apply("Test Consumer", MapElements.via(new SimpleFunction<Iterable<Integer>, Integer>() {
            @Override
            public Integer apply(Iterable<Integer> input) {
                return 42;
            }
        }));
    }

    @Test
    public void testSimpleFunctionClassDisplayData() {
        SimpleFunction<Integer, List<Integer>> simpleFn = new SimpleFunction<Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer input) {
                return Collections.emptyList();
            }
        };
        FlatMapElements<?, ?> simpleMap = FlatMapElements.via(simpleFn);
        Assert.assertThat(DisplayData.from(simpleMap), DisplayDataMatchers.hasDisplayItem("class", simpleFn.getClass()));
    }

    @Test
    public void testInferableFunctionClassDisplayData() {
        InferableFunction<Integer, List<Integer>> inferableFn = new InferableFunction<Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer input) {
                return Collections.emptyList();
            }
        };
        FlatMapElements<?, ?> inferableMap = FlatMapElements.via(inferableFn);
        Assert.assertThat(DisplayData.from(inferableMap), DisplayDataMatchers.hasDisplayItem("class", inferableFn.getClass()));
    }

    @Test
    public void testSimpleFunctionDisplayData() {
        SimpleFunction<Integer, List<Integer>> simpleFn = new SimpleFunction<Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer input) {
                return Collections.emptyList();
            }

            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "baz"));
            }
        };
        FlatMapElements<?, ?> simpleFlatMap = FlatMapElements.via(simpleFn);
        Assert.assertThat(DisplayData.from(simpleFlatMap), DisplayDataMatchers.hasDisplayItem("class", simpleFn.getClass()));
        Assert.assertThat(DisplayData.from(simpleFlatMap), DisplayDataMatchers.hasDisplayItem("foo", "baz"));
    }

    @Test
    public void testInferableFunctionDisplayData() {
        InferableFunction<Integer, List<Integer>> inferableFn = new InferableFunction<Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer input) {
                return Collections.emptyList();
            }

            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "baz"));
            }
        };
        FlatMapElements<?, ?> inferableFlatMap = FlatMapElements.via(inferableFn);
        Assert.assertThat(DisplayData.from(inferableFlatMap), DisplayDataMatchers.hasDisplayItem("class", inferableFn.getClass()));
        Assert.assertThat(DisplayData.from(inferableFlatMap), DisplayDataMatchers.hasDisplayItem("foo", "baz"));
    }

    @Test
    @Category(NeedsRunner.class)
    public void testVoidValues() throws Exception {
        pipeline.apply(Create.of("hello")).apply(WithKeys.of("k")).apply(new FlatMapElementsTest.VoidValues<String, String>() {});
        // Make sure the pipeline runs
        pipeline.run();
    }

    static class VoidValues<K, V> extends PTransform<PCollection<KV<K, V>>, PCollection<KV<K, Void>>> {
        @Override
        public PCollection<KV<K, Void>> expand(PCollection<KV<K, V>> input) {
            return input.apply(FlatMapElements.<KV<K, V>, KV<K, Void>>via(new SimpleFunction<KV<K, V>, Iterable<KV<K, Void>>>() {
                @Override
                public Iterable<KV<K, Void>> apply(KV<K, V> input) {
                    return Collections.singletonList(KV.<K, Void>of(input.getKey(), null));
                }
            }));
        }
    }

    /**
     * Basic test of {@link FlatMapElements} with a lambda (which is instantiated as a {@link ProcessFunction}).
     */
    @Test
    @Category(NeedsRunner.class)
    public void testFlatMapBasicWithLambda() throws Exception {
        PCollection<Integer> output = pipeline.apply(Create.of(1, 2, 3)).apply(// Note that the input type annotation is required.
        FlatMapElements.into(integers()).via((Integer i) -> ImmutableList.of(i, (-i))));
        PAssert.that(output).containsInAnyOrder(1, 3, (-1), (-3), 2, (-2));
        pipeline.run();
    }

    /**
     * Basic test of {@link FlatMapElements} with a method reference.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testFlatMapMethodReference() throws Exception {
        PCollection<Integer> output = pipeline.apply(Create.of(1, 2, 3)).apply(// Note that the input type annotation is required.
        FlatMapElements.into(integers()).via(new FlatMapElementsTest.Negater()::numAndNegation));
        PAssert.that(output).containsInAnyOrder(1, 3, (-1), (-3), 2, (-2));
        pipeline.run();
    }

    private static class Negater implements Serializable {
        public List<Integer> numAndNegation(int input) {
            return ImmutableList.of(input, (-input));
        }
    }
}

