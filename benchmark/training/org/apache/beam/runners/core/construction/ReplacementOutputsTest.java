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


import IsBounded.BOUNDED;
import java.util.Map;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.runners.PTransformOverrideFactory.ReplacementOutput;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.PValue;
import org.apache.beam.sdk.values.TaggedPValue;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ReplacementOutputs}.
 */
@RunWith(JUnit4.class)
public class ReplacementOutputsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private TestPipeline p = TestPipeline.create();

    private PCollection<Integer> ints = PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of());

    private PCollection<Integer> moreInts = PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of());

    private PCollection<String> strs = PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, StringUtf8Coder.of());

    private PCollection<Integer> replacementInts = PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of());

    private PCollection<Integer> moreReplacementInts = PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of());

    private PCollection<String> replacementStrs = PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, StringUtf8Coder.of());

    @Test
    public void singletonSucceeds() {
        Map<PValue, ReplacementOutput> replacements = ReplacementOutputs.singleton(ints.expand(), replacementInts);
        Assert.assertThat(replacements, Matchers.hasKey(replacementInts));
        ReplacementOutput replacement = replacements.get(replacementInts);
        Map.Entry<TupleTag<?>, PValue> taggedInts = Iterables.getOnlyElement(ints.expand().entrySet());
        Assert.assertThat(replacement.getOriginal().getTag(), Matchers.equalTo(taggedInts.getKey()));
        Assert.assertThat(replacement.getOriginal().getValue(), Matchers.equalTo(taggedInts.getValue()));
        Assert.assertThat(replacement.getReplacement().getValue(), Matchers.equalTo(replacementInts));
    }

    @Test
    public void singletonMultipleOriginalsThrows() {
        thrown.expect(IllegalArgumentException.class);
        ReplacementOutputs.singleton(ImmutableMap.<TupleTag<?>, PValue>builder().putAll(ints.expand()).putAll(moreInts.expand()).build(), replacementInts);
    }

    private TupleTag<Integer> intsTag = new TupleTag();

    private TupleTag<Integer> moreIntsTag = new TupleTag();

    private TupleTag<String> strsTag = new TupleTag();

    @Test
    public void taggedSucceeds() {
        PCollectionTuple original = PCollectionTuple.of(intsTag, ints).and(strsTag, strs).and(moreIntsTag, moreInts);
        Map<PValue, ReplacementOutput> replacements = ReplacementOutputs.tagged(original.expand(), PCollectionTuple.of(strsTag, replacementStrs).and(moreIntsTag, moreReplacementInts).and(intsTag, replacementInts));
        Assert.assertThat(replacements.keySet(), Matchers.containsInAnyOrder(replacementStrs, replacementInts, moreReplacementInts));
        ReplacementOutput intsReplacement = replacements.get(replacementInts);
        ReplacementOutput strsReplacement = replacements.get(replacementStrs);
        ReplacementOutput moreIntsReplacement = replacements.get(moreReplacementInts);
        Assert.assertThat(intsReplacement, Matchers.equalTo(ReplacementOutput.of(TaggedPValue.of(intsTag, ints), TaggedPValue.of(intsTag, replacementInts))));
        Assert.assertThat(strsReplacement, Matchers.equalTo(ReplacementOutput.of(TaggedPValue.of(strsTag, strs), TaggedPValue.of(strsTag, replacementStrs))));
        Assert.assertThat(moreIntsReplacement, Matchers.equalTo(ReplacementOutput.of(TaggedPValue.of(moreIntsTag, moreInts), TaggedPValue.of(moreIntsTag, moreReplacementInts))));
    }

    @Test
    public void taggedMissingReplacementThrows() {
        PCollectionTuple original = PCollectionTuple.of(intsTag, ints).and(strsTag, strs).and(moreIntsTag, moreInts);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing replacement");
        thrown.expectMessage(intsTag.toString());
        thrown.expectMessage(ints.toString());
        ReplacementOutputs.tagged(original.expand(), PCollectionTuple.of(strsTag, replacementStrs).and(moreIntsTag, moreReplacementInts));
    }

    @Test
    public void taggedExtraReplacementThrows() {
        PCollectionTuple original = PCollectionTuple.of(intsTag, ints).and(strsTag, strs);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing original output");
        thrown.expectMessage(moreIntsTag.toString());
        thrown.expectMessage(moreReplacementInts.toString());
        ReplacementOutputs.tagged(original.expand(), PCollectionTuple.of(strsTag, replacementStrs).and(moreIntsTag, moreReplacementInts).and(intsTag, replacementInts));
    }
}

