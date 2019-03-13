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
package org.apache.beam.runners.apex.translation;


import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.beam.runners.apex.ApexPipelineOptions;
import org.apache.beam.runners.apex.TestApexRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.ListCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test that view overrides are applied by checking the corresponding side input coders. Unlike
 * runner validation these don't run the pipeline, they only check translation.
 */
@RunWith(JUnit4.class)
public class SideInputTranslationTest implements Serializable {
    private static final AtomicReference<Boolean> SIDE_INPUT_ACCESSED = new AtomicReference<>();

    @Test
    public void testMapAsEntrySetSideInput() {
        SideInputTranslationTest.SIDE_INPUT_ACCESSED.set(false);
        ApexPipelineOptions options = PipelineOptionsFactory.as(ApexPipelineOptions.class);
        options.setApplicationName("SideInputTranslationTest");
        options.setRunner(TestApexRunner.class);
        Pipeline pipeline = Pipeline.create(options);
        final PCollectionView<Map<String, Integer>> view = pipeline.apply("CreateSideInput", Create.of(KV.of("a", 1), KV.of("b", 3))).apply(View.asMap());
        PCollection<KV<String, Integer>> output = pipeline.apply("CreateMainInput", /* size */
        Create.of(2)).apply("OutputSideInputs", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<Integer, KV<String, Integer>>() {
            @ProcessElement
            public void processElement(ProcessContext c) {
                Assert.assertEquals(((int) (c.element())), c.sideInput(view).size());
                Assert.assertEquals(((int) (c.element())), c.sideInput(view).entrySet().size());
                for (Map.Entry<String, Integer> entry : c.sideInput(view).entrySet()) {
                    c.output(KV.of(entry.getKey(), entry.getValue()));
                }
                // Using this to ensure that execution really reaches this point,
                // as a workaround for https://issues.apache.org/jira/browse/BEAM-3261.
                // When that issue is resolved, this test should simply be deleted,
                // as it duplicates a test in ViewTest.
                SideInputTranslationTest.SIDE_INPUT_ACCESSED.set(true);
            }
        }).withSideInputs(view));
        PAssert.that(output).containsInAnyOrder(KV.of("a", 1), KV.of("b", 3));
        pipeline.run();
        Assert.assertTrue(SideInputTranslationTest.SIDE_INPUT_ACCESSED.get());
    }

    @Test
    public void testListSideInputTranslation() throws Exception {
        Assert.assertEquals(ListCoder.of(KvCoder.of(VoidCoder.of(), VarIntCoder.of())), getTranslatedSideInputCoder(ImmutableList.of(11, 13, 17, 23), View.asList()));
    }

    @Test
    public void testMapSideInputTranslation() throws Exception {
        Assert.assertEquals(ListCoder.of(KvCoder.of(VoidCoder.of(), KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of()))), getTranslatedSideInputCoder(ImmutableList.of(KV.of("a", 1), KV.of("b", 3)), View.asMap()));
    }

    @Test
    public void testMultimapSideInputTranslation() throws Exception {
        Assert.assertEquals(ListCoder.of(KvCoder.of(VoidCoder.of(), KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of()))), getTranslatedSideInputCoder(ImmutableList.of(KV.of("a", 1), KV.of("a", 2), KV.of("b", 3)), View.asMultimap()));
    }
}

