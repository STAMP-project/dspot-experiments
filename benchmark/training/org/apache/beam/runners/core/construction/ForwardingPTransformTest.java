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
package org.apache.beam.runners.core.construction;


import DisplayData.Builder;
import PCollection.IsBounded.BOUNDED;
import java.util.Map;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PValue;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link ForwardingPTransform}.
 */
@RunWith(JUnit4.class)
public class ForwardingPTransformTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private PTransform<PCollection<Integer>, PCollection<String>> delegate;

    private ForwardingPTransform<PCollection<Integer>, PCollection<String>> forwarding;

    @Test
    public void applyDelegates() {
        @SuppressWarnings("unchecked")
        PCollection<Integer> collection = Mockito.mock(PCollection.class);
        @SuppressWarnings("unchecked")
        PCollection<String> output = Mockito.mock(PCollection.class);
        Mockito.when(delegate.expand(collection)).thenReturn(output);
        PCollection<String> result = forwarding.expand(collection);
        Assert.assertThat(result, Matchers.equalTo(output));
    }

    @Test
    public void getNameDelegates() {
        String name = "My_forwardingptransform-name;for!thisTest";
        Mockito.when(delegate.getName()).thenReturn(name);
        Assert.assertThat(forwarding.getName(), Matchers.equalTo(name));
    }

    @Test
    public void getAdditionalInputsDelegates() {
        Map<TupleTag<?>, PValue> additionalInputs = ImmutableMap.of(new TupleTag("test_tag"), Pipeline.create().apply(Create.of("1")));
        Mockito.when(delegate.getAdditionalInputs()).thenReturn(additionalInputs);
        Assert.assertThat(forwarding.getAdditionalInputs(), Matchers.equalTo(additionalInputs));
    }

    @Test
    public void validateDelegates() {
        @SuppressWarnings("unchecked")
        PipelineOptions options = Mockito.mock(PipelineOptions.class);
        Mockito.doThrow(RuntimeException.class).when(delegate).validate(options);
        thrown.expect(RuntimeException.class);
        forwarding.validate(options);
    }

    @Test
    public void getDefaultOutputCoderDelegates() throws Exception {
        @SuppressWarnings("unchecked")
        PCollection<Integer> input = /* pipeline */
        /* coder */
        PCollection.createPrimitiveOutputInternal(null, WindowingStrategy.globalDefault(), BOUNDED, null);
        @SuppressWarnings("unchecked")
        PCollection<String> output = /* pipeline */
        /* coder */
        PCollection.createPrimitiveOutputInternal(null, WindowingStrategy.globalDefault(), BOUNDED, null);
        @SuppressWarnings("unchecked")
        Coder<String> outputCoder = Mockito.mock(Coder.class);
        Mockito.when(delegate.expand(input)).thenReturn(output);
        Mockito.when(delegate.getDefaultOutputCoder(input, output)).thenReturn(outputCoder);
        Assert.assertThat(forwarding.expand(input).getCoder(), Matchers.equalTo(outputCoder));
    }

    @Test
    public void populateDisplayDataDelegates() {
        Mockito.doThrow(RuntimeException.class).when(delegate).populateDisplayData(ArgumentMatchers.any(Builder.class));
        thrown.expect(RuntimeException.class);
        DisplayData.from(forwarding);
    }
}

