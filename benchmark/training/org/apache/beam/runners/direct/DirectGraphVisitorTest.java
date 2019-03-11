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
package org.apache.beam.runners.direct;


import DoFn.ProcessContext;
import java.io.Serializable;
import java.util.List;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.CountingSource;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.Flatten.PCollections;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.PDone;
import org.apache.beam.sdk.values.PInput;
import org.apache.beam.sdk.values.POutput;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DirectGraphVisitor}.
 */
@RunWith(JUnit4.class)
public class DirectGraphVisitorTest implements Serializable {
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Rule
    public transient TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    private transient DirectGraphVisitor visitor = new DirectGraphVisitor();

    @Test
    public void getViewsReturnsViews() {
        PCollectionView<List<String>> listView = p.apply("listCreate", Create.of("foo", "bar")).apply(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void processElement(ProcessContext c) throws Exception {
                c.output(Integer.toString(c.element().length()));
            }
        })).apply(View.asList());
        PCollectionView<Object> singletonView = p.apply("singletonCreate", Create.<Object>of(1, 2, 3)).apply(View.asSingleton());
        p.replaceAll(DirectRunner.fromOptions(TestPipeline.testingPipelineOptions()).defaultTransformOverrides());
        p.traverseTopologically(visitor);
        Assert.assertThat(visitor.getGraph().getViews(), Matchers.containsInAnyOrder(listView, singletonView));
    }

    @Test
    public void getRootTransformsContainsRootTransforms() {
        PCollection<String> created = p.apply(Create.of("foo", "bar"));
        PCollection<Long> counted = p.apply(Read.from(CountingSource.upTo(1234L)));
        PCollection<Long> unCounted = p.apply(GenerateSequence.from(0));
        p.traverseTopologically(visitor);
        DirectGraph graph = visitor.getGraph();
        Assert.assertThat(graph.getRootTransforms(), Matchers.hasSize(3));
        Assert.assertThat(graph.getRootTransforms(), Matchers.containsInAnyOrder(new Object[]{ graph.getProducer(created), graph.getProducer(counted), graph.getProducer(unCounted) }));
        for (AppliedPTransform<?, ?, ?> root : graph.getRootTransforms()) {
            // Root transforms will have no inputs
            Assert.assertThat(root.getInputs().entrySet(), Matchers.emptyIterable());
            Assert.assertThat(Iterables.getOnlyElement(root.getOutputs().values()), Matchers.<POutput>isOneOf(created, counted, unCounted));
        }
    }

    @Test
    public void getRootTransformsContainsEmptyFlatten() {
        PCollections<String> flatten = Flatten.pCollections();
        PCollectionList<String> emptyList = PCollectionList.empty(p);
        PCollection<String> empty = emptyList.apply(flatten);
        empty.setCoder(StringUtf8Coder.of());
        p.traverseTopologically(visitor);
        DirectGraph graph = visitor.getGraph();
        Assert.assertThat(graph.getRootTransforms(), Matchers.containsInAnyOrder(new Object[]{ graph.getProducer(empty) }));
        AppliedPTransform<?, ?, ?> onlyRoot = Iterables.getOnlyElement(graph.getRootTransforms());
        Assert.assertThat(((Object) (onlyRoot.getTransform())), Matchers.equalTo(flatten));
        Assert.assertThat(onlyRoot.getInputs().entrySet(), Matchers.emptyIterable());
        Assert.assertThat(onlyRoot.getOutputs(), Matchers.equalTo(empty.expand()));
    }

    @Test
    public void getValueToConsumersSucceeds() {
        PCollection<String> created = p.apply(Create.of("1", "2", "3"));
        PCollection<String> transformed = created.apply(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void processElement(ProcessContext c) throws Exception {
                c.output(Integer.toString(c.element().length()));
            }
        }));
        PCollection<String> flattened = PCollectionList.of(created).and(transformed).apply(Flatten.pCollections());
        p.traverseTopologically(visitor);
        DirectGraph graph = visitor.getGraph();
        AppliedPTransform<?, ?, ?> transformedProducer = graph.getProducer(transformed);
        AppliedPTransform<?, ?, ?> flattenedProducer = graph.getProducer(flattened);
        Assert.assertThat(graph.getPerElementConsumers(created), Matchers.containsInAnyOrder(new Object[]{ transformedProducer, flattenedProducer }));
        Assert.assertThat(graph.getPerElementConsumers(transformed), Matchers.containsInAnyOrder(new Object[]{ flattenedProducer }));
        Assert.assertThat(graph.getPerElementConsumers(flattened), Matchers.emptyIterable());
    }

    @Test
    public void getValueToConsumersWithDuplicateInputSucceeds() {
        PCollection<String> created = p.apply(Create.of("1", "2", "3"));
        PCollection<String> flattened = PCollectionList.of(created).and(created).apply(Flatten.pCollections());
        p.traverseTopologically(visitor);
        DirectGraph graph = visitor.getGraph();
        AppliedPTransform<?, ?, ?> flattenedProducer = graph.getProducer(flattened);
        Assert.assertThat(graph.getPerElementConsumers(created), Matchers.containsInAnyOrder(new Object[]{ flattenedProducer, flattenedProducer }));
        Assert.assertThat(graph.getPerElementConsumers(flattened), Matchers.emptyIterable());
    }

    @Test
    public void getStepNamesContainsAllTransforms() {
        PCollection<String> created = p.apply(Create.of("1", "2", "3"));
        PCollection<String> transformed = created.apply(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void processElement(ProcessContext c) throws Exception {
                c.output(Integer.toString(c.element().length()));
            }
        }));
        PDone finished = transformed.apply(new org.apache.beam.sdk.transforms.PTransform<PInput, PDone>() {
            @Override
            public PDone expand(PInput input) {
                return PDone.in(input.getPipeline());
            }
        });
        p.traverseTopologically(visitor);
        DirectGraph graph = visitor.getGraph();
        Assert.assertThat(graph.getStepName(graph.getProducer(created)), Matchers.equalTo("s0"));
        Assert.assertThat(graph.getStepName(graph.getProducer(transformed)), Matchers.equalTo("s1"));
        // finished doesn't have a producer, because it's not a PValue.
        // TODO: Demonstrate that PCollectionList/Tuple and other composite PValues are either safe to
        // use, or make them so.
    }

    @Test
    public void traverseMultipleTimesThrows() {
        p.apply(Create.of(1, 2, 3));
        p.traverseTopologically(visitor);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(DirectGraphVisitor.class.getSimpleName());
        thrown.expectMessage("is finalized");
        p.traverseTopologically(visitor);
    }

    @Test
    public void traverseIndependentPathsSucceeds() {
        p.apply("left", Create.of(1, 2, 3));
        p.apply("right", Create.of("foo", "bar", "baz"));
        p.traverseTopologically(visitor);
    }

    @Test
    public void getGraphWithoutVisitingThrows() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("completely traversed");
        thrown.expectMessage("get a graph");
        visitor.getGraph();
    }
}

