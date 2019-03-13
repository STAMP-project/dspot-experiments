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
package org.apache.beam.sdk.values;


import ParDo.SingleOutput;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link TypedPValue}, primarily focusing on Coder inference.
 */
@RunWith(JUnit4.class)
public class TypedPValueTest {
    @Rule
    public final TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static class IdentityDoFn extends DoFn<Integer, Integer> {
        private static final long serialVersionUID = 0;

        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
            c.output(c.element());
        }
    }

    @Test
    public void testUntypedOutputTupleTagGivesActionableMessage() {
        TupleTag<Integer> mainOutputTag = new TupleTag<Integer>() {};
        // untypedOutputTag did not use anonymous subclass.
        TupleTag<Integer> untypedOutputTag = new TupleTag();
        PCollectionTuple tuple = buildPCollectionTupleWithTags(mainOutputTag, untypedOutputTag);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No Coder has been manually specified");
        thrown.expectMessage("Building a Coder using a registered CoderProvider failed");
        Coder<?> coder = tuple.get(untypedOutputTag).getCoder();
        System.out.println(coder);
    }

    @Test
    public void testStaticFactoryOutputTupleTagGivesActionableMessage() {
        TupleTag<Integer> mainOutputTag = new TupleTag<Integer>() {};
        // untypedOutputTag constructed from a static factory method.
        TupleTag<Integer> untypedOutputTag = TypedPValueTest.makeTagStatically();
        PCollectionTuple tuple = buildPCollectionTupleWithTags(mainOutputTag, untypedOutputTag);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No Coder has been manually specified");
        thrown.expectMessage("Building a Coder using a registered CoderProvider failed");
        tuple.get(untypedOutputTag).getCoder();
    }

    @Test
    public void testTypedOutputTupleTag() {
        TupleTag<Integer> mainOutputTag = new TupleTag<Integer>() {};
        // typedOutputTag was constructed with compile-time type information.
        TupleTag<Integer> typedOutputTag = new TupleTag<Integer>() {};
        PCollectionTuple tuple = buildPCollectionTupleWithTags(mainOutputTag, typedOutputTag);
        Assert.assertThat(tuple.get(typedOutputTag).getCoder(), Matchers.instanceOf(VarIntCoder.class));
    }

    @Test
    public void testUntypedMainOutputTagTypedOutputTupleTag() {
        // mainOutputTag is allowed to be untyped because Coder can be inferred other ways.
        TupleTag<Integer> mainOutputTag = new TupleTag();
        TupleTag<Integer> typedOutputTag = new TupleTag<Integer>() {};
        PCollectionTuple tuple = buildPCollectionTupleWithTags(mainOutputTag, typedOutputTag);
        Assert.assertThat(tuple.get(typedOutputTag).getCoder(), Matchers.instanceOf(VarIntCoder.class));
    }

    /**
     * This type is incompatible with all known coder providers such as Serializable,
     * {@code @DefaultCoder} which allows testing coder registry lookup failure cases.
     */
    static class EmptyClass {}

    private static class EmptyClassDoFn extends DoFn<Integer, TypedPValueTest.EmptyClass> {
        private static final long serialVersionUID = 0;

        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
            c.output(new TypedPValueTest.EmptyClass());
        }
    }

    @Test
    public void testParDoWithNoOutputsErrorDoesNotMentionTupleTag() {
        PCollection<TypedPValueTest.EmptyClass> input = p.apply(Create.of(1, 2, 3)).apply(ParDo.of(new TypedPValueTest.EmptyClassDoFn()));
        thrown.expect(IllegalStateException.class);
        // Output specific to ParDo additional TupleTag outputs should not be present.
        thrown.expectMessage(Matchers.not(Matchers.containsString("erasure")));
        thrown.expectMessage(Matchers.not(Matchers.containsString("see TupleTag Javadoc")));
        // Instead, expect output suggesting other possible fixes.
        thrown.expectMessage("Building a Coder using a registered CoderProvider failed");
        input.getCoder();
    }

    @Test
    public void testFinishSpecifyingShouldFailIfNoCoderInferrable() {
        p.enableAbandonedNodeEnforcement(false);
        PCollection<Integer> created = p.apply(Create.of(1, 2, 3));
        SingleOutput<Integer, TypedPValueTest.EmptyClass> uninferrableParDo = ParDo.of(new TypedPValueTest.EmptyClassDoFn());
        PCollection<TypedPValueTest.EmptyClass> unencodable = created.apply(uninferrableParDo);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Unable to return a default Coder");
        thrown.expectMessage("Inferring a Coder from the CoderRegistry failed");
        unencodable.finishSpecifying(created, uninferrableParDo);
    }
}

