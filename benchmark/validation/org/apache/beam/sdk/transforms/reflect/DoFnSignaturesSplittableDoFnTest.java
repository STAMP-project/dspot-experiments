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
package org.apache.beam.sdk.transforms.reflect;


import DoFn.ProcessContext;
import DoFnSignature.Parameter.RestrictionTrackerParameter;
import DoFnSignature.ProcessElementMethod;
import PCollection.IsBounded.BOUNDED;
import PCollection.IsBounded.UNBOUNDED;
import java.util.List;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StructuredCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.BoundedPerElement;
import org.apache.beam.sdk.transforms.DoFn.UnboundedPerElement;
import org.apache.beam.sdk.transforms.splittabledofn.HasDefaultTracker;
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Predicates;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DoFnSignatures} focused on methods related to <a
 * href="https://s.apache.org/splittable-do-fn">splittable</a> {@link DoFn}.
 */
@SuppressWarnings("unused")
@RunWith(JUnit4.class)
public class DoFnSignaturesSplittableDoFnTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private abstract static class SomeRestriction implements HasDefaultTracker<DoFnSignaturesSplittableDoFnTest.SomeRestriction, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker> {}

    private abstract static class SomeRestrictionTracker extends RestrictionTracker<DoFnSignaturesSplittableDoFnTest.SomeRestriction, Void> {}

    private abstract static class SomeRestrictionCoder extends StructuredCoder<DoFnSignaturesSplittableDoFnTest.SomeRestriction> {}

    @Test
    public void testReturnsProcessContinuation() throws Exception {
        DoFnSignature.ProcessElementMethod signature = DoFnSignaturesTestUtils.analyzeProcessElementMethod(new DoFnSignaturesTestUtils.AnonymousMethod() {
            private DoFn.ProcessContinuation method(ProcessContext context) {
                return null;
            }
        });
        Assert.assertTrue(signature.hasReturnValue());
    }

    @Test
    public void testHasRestrictionTracker() throws Exception {
        DoFnSignature.ProcessElementMethod signature = DoFnSignaturesTestUtils.analyzeProcessElementMethod(new DoFnSignaturesTestUtils.AnonymousMethod() {
            private void method(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
            }
        });
        Assert.assertTrue(signature.isSplittable());
        Assert.assertTrue(signature.extraParameters().stream().anyMatch(Predicates.instanceOf(RestrictionTrackerParameter.class)::apply));
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker.class, signature.trackerT().getRawType());
    }

    @Test
    public void testSplittableProcessElementMustNotHaveOtherParams() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Illegal parameter");
        thrown.expectMessage("BoundedWindow");
        DoFnSignature.ProcessElementMethod signature = DoFnSignaturesTestUtils.analyzeProcessElementMethod(new DoFnSignaturesTestUtils.AnonymousMethod() {
            private void method(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker, BoundedWindow window) {
            }
        });
    }

    @Test
    public void testInfersBoundednessFromAnnotation() throws Exception {
        class BaseSplittableFn extends DoFn<Integer, String> {
            @ProcessElement
            public void processElement(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
            }

            @GetInitialRestriction
            public DoFnSignaturesSplittableDoFnTest.SomeRestriction getInitialRestriction(Integer element) {
                return null;
            }
        }
        @BoundedPerElement
        class BoundedSplittableFn extends BaseSplittableFn {}
        @UnboundedPerElement
        class UnboundedSplittableFn extends BaseSplittableFn {}
        Assert.assertEquals(BOUNDED, DoFnSignatures.getSignature(BaseSplittableFn.class).isBoundedPerElement());
        Assert.assertEquals(BOUNDED, DoFnSignatures.getSignature(BoundedSplittableFn.class).isBoundedPerElement());
        Assert.assertEquals(UNBOUNDED, DoFnSignatures.getSignature(UnboundedSplittableFn.class).isBoundedPerElement());
    }

    private static class BaseFnWithoutContinuation extends DoFn<Integer, String> {
        @ProcessElement
        public void processElement(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
        }

        @GetInitialRestriction
        public DoFnSignaturesSplittableDoFnTest.SomeRestriction getInitialRestriction(Integer element) {
            return null;
        }
    }

    private static class BaseFnWithContinuation extends DoFn<Integer, String> {
        @ProcessElement
        public ProcessContinuation processElement(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
            return null;
        }

        @GetInitialRestriction
        public DoFnSignaturesSplittableDoFnTest.SomeRestriction getInitialRestriction(Integer element) {
            return null;
        }
    }

    @Test
    public void testSplittableBoundednessInferredFromReturnValue() throws Exception {
        Assert.assertEquals(BOUNDED, DoFnSignatures.getSignature(DoFnSignaturesSplittableDoFnTest.BaseFnWithoutContinuation.class).isBoundedPerElement());
        Assert.assertEquals(UNBOUNDED, DoFnSignatures.getSignature(DoFnSignaturesSplittableDoFnTest.BaseFnWithContinuation.class).isBoundedPerElement());
    }

    @Test
    public void testSplittableRespectsBoundednessAnnotation() throws Exception {
        @BoundedPerElement
        class BoundedFnWithContinuation extends DoFnSignaturesSplittableDoFnTest.BaseFnWithContinuation {}
        Assert.assertEquals(BOUNDED, DoFnSignatures.getSignature(BoundedFnWithContinuation.class).isBoundedPerElement());
        @UnboundedPerElement
        class UnboundedFnWithContinuation extends DoFnSignaturesSplittableDoFnTest.BaseFnWithContinuation {}
        Assert.assertEquals(UNBOUNDED, DoFnSignatures.getSignature(UnboundedFnWithContinuation.class).isBoundedPerElement());
    }

    @Test
    public void testUnsplittableIsBounded() throws Exception {
        class UnsplittableFn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext context) {
            }
        }
        Assert.assertEquals(BOUNDED, DoFnSignatures.getSignature(UnsplittableFn.class).isBoundedPerElement());
    }

    @Test
    public void testUnsplittableButDeclaresBounded() throws Exception {
        @BoundedPerElement
        class SomeFn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext context) {
            }
        }
        thrown.expectMessage("Non-splittable, but annotated as @Bounded");
        DoFnSignatures.getSignature(SomeFn.class);
    }

    @Test
    public void testUnsplittableButDeclaresUnbounded() throws Exception {
        @UnboundedPerElement
        class SomeFn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext context) {
            }
        }
        thrown.expectMessage("Non-splittable, but annotated as @Unbounded");
        DoFnSignatures.getSignature(SomeFn.class);
    }

    /**
     * Tests a splittable {@link DoFn} that defines all methods in their full form, correctly.
     */
    @Test
    public void testSplittableWithAllFunctions() throws Exception {
        class GoodSplittableDoFn extends DoFn<Integer, String> {
            @ProcessElement
            public ProcessContinuation processElement(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
                return null;
            }

            @GetInitialRestriction
            public DoFnSignaturesSplittableDoFnTest.SomeRestriction getInitialRestriction(Integer element) {
                return null;
            }

            @SplitRestriction
            public void splitRestriction(Integer element, DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction, OutputReceiver<DoFnSignaturesSplittableDoFnTest.SomeRestriction> receiver) {
            }

            @NewTracker
            public DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker newTracker(DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction) {
                return null;
            }

            @GetRestrictionCoder
            public DoFnSignaturesSplittableDoFnTest.SomeRestrictionCoder getRestrictionCoder() {
                return null;
            }
        }
        DoFnSignature signature = DoFnSignatures.getSignature(GoodSplittableDoFn.class);
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker.class, signature.processElement().trackerT().getRawType());
        Assert.assertTrue(signature.processElement().isSplittable());
        Assert.assertTrue(signature.processElement().hasReturnValue());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestriction.class, signature.getInitialRestriction().restrictionT().getRawType());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestriction.class, signature.splitRestriction().restrictionT().getRawType());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker.class, signature.newTracker().trackerT().getRawType());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestriction.class, signature.newTracker().restrictionT().getRawType());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestrictionCoder.class, signature.getRestrictionCoder().coderT().getRawType());
    }

    /**
     * Tests a splittable {@link DoFn} that defines all methods in their full form, correctly, using
     * generic types.
     */
    @Test
    public void testSplittableWithAllFunctionsGeneric() throws Exception {
        class GoodGenericSplittableDoFn<RestrictionT, TrackerT, CoderT> extends DoFn<Integer, String> {
            @ProcessElement
            public ProcessContinuation processElement(ProcessContext context, TrackerT tracker) {
                return null;
            }

            @GetInitialRestriction
            public RestrictionT getInitialRestriction(Integer element) {
                return null;
            }

            @SplitRestriction
            public void splitRestriction(Integer element, RestrictionT restriction, OutputReceiver<RestrictionT> receiver) {
            }

            @NewTracker
            public TrackerT newTracker(RestrictionT restriction) {
                return null;
            }

            @GetRestrictionCoder
            public CoderT getRestrictionCoder() {
                return null;
            }
        }
        DoFnSignature signature = DoFnSignatures.getSignature(new GoodGenericSplittableDoFn<DoFnSignaturesSplittableDoFnTest.SomeRestriction, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker, DoFnSignaturesSplittableDoFnTest.SomeRestrictionCoder>() {}.getClass());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker.class, signature.processElement().trackerT().getRawType());
        Assert.assertTrue(signature.processElement().isSplittable());
        Assert.assertTrue(signature.processElement().hasReturnValue());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestriction.class, signature.getInitialRestriction().restrictionT().getRawType());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestriction.class, signature.splitRestriction().restrictionT().getRawType());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker.class, signature.newTracker().trackerT().getRawType());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestriction.class, signature.newTracker().restrictionT().getRawType());
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeRestrictionCoder.class, signature.getRestrictionCoder().coderT().getRawType());
    }

    @Test
    public void testSplittableMissingRequiredMethods() throws Exception {
        class BadFn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
            }
        }
        thrown.expectMessage(("Splittable, but does not define the following required methods: " + "[@GetInitialRestriction, @NewTracker]"));
        DoFnSignatures.getSignature(BadFn.class);
    }

    abstract static class SomeDefaultTracker extends RestrictionTracker<DoFnSignaturesSplittableDoFnTest.RestrictionWithDefaultTracker, Void> {}

    abstract static class RestrictionWithDefaultTracker implements HasDefaultTracker<DoFnSignaturesSplittableDoFnTest.RestrictionWithDefaultTracker, DoFnSignaturesSplittableDoFnTest.SomeDefaultTracker> {}

    @Test
    public void testHasDefaultTracker() throws Exception {
        class Fn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext c, DoFnSignaturesSplittableDoFnTest.SomeDefaultTracker tracker) {
            }

            @GetInitialRestriction
            public DoFnSignaturesSplittableDoFnTest.RestrictionWithDefaultTracker getInitialRestriction(Integer element) {
                return null;
            }
        }
        DoFnSignature signature = DoFnSignatures.getSignature(Fn.class);
        Assert.assertEquals(DoFnSignaturesSplittableDoFnTest.SomeDefaultTracker.class, signature.processElement().trackerT().getRawType());
    }

    @Test
    public void testRestrictionHasDefaultTrackerProcessUsesWrongTracker() throws Exception {
        class Fn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext c, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
            }

            @GetInitialRestriction
            public DoFnSignaturesSplittableDoFnTest.RestrictionWithDefaultTracker getInitialRestriction(Integer element) {
                return null;
            }
        }
        thrown.expectMessage("Has tracker type SomeRestrictionTracker, but the DoFn's tracker type was inferred as ");
        thrown.expectMessage("SomeDefaultTracker");
        thrown.expectMessage(("from restriction type RestrictionWithDefaultTracker " + "of @GetInitialRestriction method getInitialRestriction(Integer)"));
        DoFnSignatures.getSignature(Fn.class);
    }

    @Test
    public void testNewTrackerReturnsWrongType() throws Exception {
        class BadFn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
            }

            @NewTracker
            public void newTracker(DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction) {
            }

            @GetInitialRestriction
            public DoFnSignaturesSplittableDoFnTest.SomeRestriction getInitialRestriction(Integer element) {
                return null;
            }
        }
        thrown.expectMessage("Returns void, but must return a subtype of RestrictionTracker<SomeRestriction, ?>");
        DoFnSignatures.getSignature(BadFn.class);
    }

    @Test
    public void testGetInitialRestrictionMismatchesNewTracker() throws Exception {
        class BadFn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
            }

            @NewTracker
            public DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker newTracker(DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction) {
                return null;
            }

            @GetInitialRestriction
            public String getInitialRestriction(Integer element) {
                return null;
            }
        }
        thrown.expectMessage("getInitialRestriction(Integer): Uses restriction type String, but @NewTracker method");
        thrown.expectMessage("newTracker(SomeRestriction) uses restriction type SomeRestriction");
        DoFnSignatures.getSignature(BadFn.class);
    }

    @Test
    public void testGetRestrictionCoderReturnsWrongType() throws Exception {
        class BadFn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
            }

            @NewTracker
            public DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker newTracker(DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction) {
                return null;
            }

            @GetInitialRestriction
            public DoFnSignaturesSplittableDoFnTest.SomeRestriction getInitialRestriction(Integer element) {
                return null;
            }

            @GetRestrictionCoder
            public KvCoder getRestrictionCoder() {
                return null;
            }
        }
        thrown.expectMessage("getRestrictionCoder() returns KvCoder which is not a subtype of Coder<SomeRestriction>");
        DoFnSignatures.getSignature(BadFn.class);
    }

    @Test
    public void testSplitRestrictionReturnsWrongType() throws Exception {
        thrown.expectMessage(("Third argument must be DoFn.OutputReceiver<SomeRestriction>, " + "but is DoFn.OutputReceiver<String>"));
        DoFnSignatures.analyzeSplitRestrictionMethod(DoFnSignaturesTestUtils.errors(), TypeDescriptor.of(DoFnSignaturesTestUtils.FakeDoFn.class), new DoFnSignaturesTestUtils.AnonymousMethod() {
            void method(Integer element, DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction, DoFn.OutputReceiver<String> receiver) {
            }
        }.getMethod(), TypeDescriptor.of(Integer.class));
    }

    @Test
    public void testSplitRestrictionWrongElementArgument() throws Exception {
        class BadFn {
            private List<DoFnSignaturesSplittableDoFnTest.SomeRestriction> splitRestriction(String element, DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction) {
                return null;
            }
        }
        thrown.expectMessage("First argument must be the element type Integer");
        DoFnSignatures.analyzeSplitRestrictionMethod(DoFnSignaturesTestUtils.errors(), TypeDescriptor.of(DoFnSignaturesTestUtils.FakeDoFn.class), new DoFnSignaturesTestUtils.AnonymousMethod() {
            void method(String element, DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction, DoFn.OutputReceiver<DoFnSignaturesSplittableDoFnTest.SomeRestriction> receiver) {
            }
        }.getMethod(), TypeDescriptor.of(Integer.class));
    }

    @Test
    public void testSplitRestrictionWrongNumArguments() throws Exception {
        thrown.expectMessage("Must have exactly 3 arguments");
        DoFnSignatures.analyzeSplitRestrictionMethod(DoFnSignaturesTestUtils.errors(), TypeDescriptor.of(DoFnSignaturesTestUtils.FakeDoFn.class), new DoFnSignaturesTestUtils.AnonymousMethod() {
            private void method(Integer element, DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction, DoFn.OutputReceiver<DoFnSignaturesSplittableDoFnTest.SomeRestriction> receiver, Object extra) {
            }
        }.getMethod(), TypeDescriptor.of(Integer.class));
    }

    @Test
    public void testSplitRestrictionConsistentButWrongType() throws Exception {
        class OtherRestriction {}
        class BadFn extends DoFn<Integer, String> {
            @ProcessElement
            public void process(ProcessContext context, DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker tracker) {
            }

            @NewTracker
            public DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker newTracker(DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction) {
                return null;
            }

            @GetInitialRestriction
            public DoFnSignaturesSplittableDoFnTest.SomeRestriction getInitialRestriction(Integer element) {
                return null;
            }

            @DoFn.SplitRestriction
            public void splitRestriction(Integer element, OtherRestriction restriction, OutputReceiver<OtherRestriction> receiver) {
            }
        }
        thrown.expectMessage(("getInitialRestriction(Integer): Uses restriction type SomeRestriction, " + "but @SplitRestriction method "));
        thrown.expectMessage(("splitRestriction(Integer, OtherRestriction, OutputReceiver) " + "uses restriction type OtherRestriction"));
        DoFnSignatures.getSignature(BadFn.class);
    }

    @Test
    public void testUnsplittableMustNotDefineExtraMethods() throws Exception {
        class BadFn extends DoFn<Integer, String> {
            @ProcessElement
            public void processElement(ProcessContext context) {
            }

            @GetInitialRestriction
            public DoFnSignaturesSplittableDoFnTest.SomeRestriction getInitialRestriction(Integer element) {
                return null;
            }

            @SplitRestriction
            public void splitRestriction(Integer element, DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction, OutputReceiver<DoFnSignaturesSplittableDoFnTest.SomeRestriction> receiver) {
            }

            @NewTracker
            public DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker newTracker(DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction) {
                return null;
            }

            @GetRestrictionCoder
            public DoFnSignaturesSplittableDoFnTest.SomeRestrictionCoder getRestrictionCoder() {
                return null;
            }
        }
        thrown.expectMessage(("Non-splittable, but defines methods: " + "[@GetInitialRestriction, @SplitRestriction, @NewTracker, @GetRestrictionCoder]"));
        DoFnSignatures.getSignature(BadFn.class);
    }

    @Test
    public void testNewTrackerWrongNumArguments() throws Exception {
        thrown.expectMessage("Must have a single argument");
        DoFnSignatures.analyzeNewTrackerMethod(DoFnSignaturesTestUtils.errors(), TypeDescriptor.of(DoFnSignaturesTestUtils.FakeDoFn.class), new DoFnSignaturesTestUtils.AnonymousMethod() {
            private DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker method(DoFnSignaturesSplittableDoFnTest.SomeRestriction restriction, Object extra) {
                return null;
            }
        }.getMethod());
    }

    @Test
    public void testNewTrackerInconsistent() throws Exception {
        thrown.expectMessage(("Returns SomeRestrictionTracker, " + "but must return a subtype of RestrictionTracker<String, ?>"));
        DoFnSignatures.analyzeNewTrackerMethod(DoFnSignaturesTestUtils.errors(), TypeDescriptor.of(DoFnSignaturesTestUtils.FakeDoFn.class), new DoFnSignaturesTestUtils.AnonymousMethod() {
            private DoFnSignaturesSplittableDoFnTest.SomeRestrictionTracker method(String restriction) {
                return null;
            }
        }.getMethod());
    }
}

