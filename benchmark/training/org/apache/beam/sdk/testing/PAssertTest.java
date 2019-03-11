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
package org.apache.beam.sdk.testing;


import GlobalWindow.INSTANCE;
import PAssert.PAssertionSite;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.regex.Pattern;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.SlidingWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.common.ElementByteSizeObserver;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TimestampedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Throwables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test case for {@link PAssert}.
 */
@RunWith(JUnit4.class)
public class PAssertTest implements Serializable {
    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    private static class NotSerializableObject {
        @Override
        public boolean equals(Object other) {
            return other instanceof PAssertTest.NotSerializableObject;
        }

        @Override
        public int hashCode() {
            return 73;
        }
    }

    private static class NotSerializableObjectCoder extends AtomicCoder<PAssertTest.NotSerializableObject> {
        private NotSerializableObjectCoder() {
        }

        private static final PAssertTest.NotSerializableObjectCoder INSTANCE = new PAssertTest.NotSerializableObjectCoder();

        @JsonCreator
        public static PAssertTest.NotSerializableObjectCoder of() {
            return PAssertTest.NotSerializableObjectCoder.INSTANCE;
        }

        @Override
        public void encode(PAssertTest.NotSerializableObject value, OutputStream outStream) throws IOException, CoderException {
        }

        @Override
        public PAssertTest.NotSerializableObject decode(InputStream inStream) throws IOException, CoderException {
            return new PAssertTest.NotSerializableObject();
        }

        @Override
        public boolean isRegisterByteSizeObserverCheap(PAssertTest.NotSerializableObject value) {
            return true;
        }

        @Override
        public void registerByteSizeObserver(PAssertTest.NotSerializableObject value, ElementByteSizeObserver observer) throws Exception {
            observer.update(0L);
        }
    }

    @Test
    public void testFailureWithExceptionEncodedDecoded() throws IOException {
        Throwable error;
        try {
            throwWrappedError();
            throw new IllegalStateException("Should have failed");
        } catch (Throwable e) {
            error = e;
        }
        SuccessOrFailure failure = SuccessOrFailure.failure(PAssertionSite.capture("here"), error);
        SuccessOrFailure res = CoderUtils.clone(SerializableCoder.of(SuccessOrFailure.class), failure);
        Assert.assertEquals("Encode-decode failed SuccessOrFailure", Throwables.getStackTraceAsString(failure.assertionError()), Throwables.getStackTraceAsString(res.assertionError()));
    }

    @Test
    public void testSuccessEncodedDecoded() throws IOException {
        SuccessOrFailure success = SuccessOrFailure.success();
        SerializableCoder<SuccessOrFailure> coder = SerializableCoder.of(SuccessOrFailure.class);
        byte[] encoded = CoderUtils.encodeToByteArray(coder, success);
        SuccessOrFailure res = CoderUtils.decodeFromByteArray(coder, encoded);
        Assert.assertEquals("Encode-decode successful SuccessOrFailure", success.isSuccess(), res.isSuccess());
        Assert.assertEquals("Encode-decode successful SuccessOrFailure", success.assertionError(), res.assertionError());
    }

    /**
     * A {@link PAssert} about the contents of a {@link PCollection} must not require the contents of
     * the {@link PCollection} to be serializable.
     */
    @Test
    @Category({ ValidatesRunner.class, DataflowPortabilityApiUnsupported.class })
    public void testContainsInAnyOrderNotSerializable() throws Exception {
        PCollection<PAssertTest.NotSerializableObject> pcollection = pipeline.apply(Create.of(new PAssertTest.NotSerializableObject(), new PAssertTest.NotSerializableObject()).withCoder(PAssertTest.NotSerializableObjectCoder.of()));
        PAssert.that(pcollection).containsInAnyOrder(new PAssertTest.NotSerializableObject(), new PAssertTest.NotSerializableObject());
        pipeline.run();
    }

    /**
     * A {@link PAssert} about the contents of a {@link PCollection} is allows to be verified by an
     * arbitrary {@link SerializableFunction}, though.
     */
    @Test
    @Category({ ValidatesRunner.class, DataflowPortabilityApiUnsupported.class })
    public void testSerializablePredicate() throws Exception {
        PCollection<PAssertTest.NotSerializableObject> pcollection = pipeline.apply(Create.of(new PAssertTest.NotSerializableObject(), new PAssertTest.NotSerializableObject()).withCoder(PAssertTest.NotSerializableObjectCoder.of()));
        PAssert.that(pcollection).satisfies(( contents) -> {
            return null;// no problem!

        });
        pipeline.run();
    }

    /**
     * A {@link PAssert} about the contents of a {@link PCollection} is allows to be verified by an
     * arbitrary {@link SerializableFunction}, though.
     */
    @Test
    @Category({ ValidatesRunner.class, DataflowPortabilityApiUnsupported.class })
    public void testWindowedSerializablePredicate() throws Exception {
        PCollection<PAssertTest.NotSerializableObject> pcollection = pipeline.apply(Create.timestamped(TimestampedValue.of(new PAssertTest.NotSerializableObject(), new Instant(250L)), TimestampedValue.of(new PAssertTest.NotSerializableObject(), new Instant(500L))).withCoder(PAssertTest.NotSerializableObjectCoder.of())).apply(Window.into(FixedWindows.of(Duration.millis(300L))));
        PAssert.that(pcollection).inWindow(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(0L), new Instant(300L))).satisfies(( contents) -> {
            assertThat(Iterables.isEmpty(contents), is(false));
            return null;// no problem!

        });
        PAssert.that(pcollection).inWindow(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(300L), new Instant(600L))).satisfies(( contents) -> {
            assertThat(Iterables.isEmpty(contents), is(false));
            return null;// no problem!

        });
        pipeline.run();
    }

    /**
     * Test that we throw an error at pipeline construction time when the user mistakenly uses {@code PAssert.thatSingleton().equals()} instead of the test method {@code .isEqualTo}.
     */
    @SuppressWarnings({ "deprecation"// test of deprecated function
    , "EqualsIncompatibleType" })
    @Test
    public void testPAssertEqualsSingletonUnsupported() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("isEqualTo");
        PCollection<Integer> pcollection = pipeline.apply(Create.of(42));
        PAssert.thatSingleton(pcollection).equals(42);
    }

    /**
     * Test that we throw an error at pipeline construction time when the user mistakenly uses {@code PAssert.that().equals()} instead of the test method {@code .containsInAnyOrder}.
     */
    @SuppressWarnings({ "deprecation"// test of deprecated function
    , "EqualsIncompatibleType" })
    @Test
    public void testPAssertEqualsIterableUnsupported() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("containsInAnyOrder");
        PCollection<Integer> pcollection = pipeline.apply(Create.of(42));
        PAssert.that(pcollection).equals(42);
    }

    /**
     * Test that {@code PAssert.thatSingleton().hashCode()} is unsupported. See {@link #testPAssertEqualsSingletonUnsupported}.
     */
    // test of deprecated function
    @SuppressWarnings("deprecation")
    @Test
    public void testPAssertHashCodeSingletonUnsupported() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage(".hashCode() is not supported.");
        PCollection<Integer> pcollection = pipeline.apply(Create.of(42));
        PAssert.thatSingleton(pcollection).hashCode();
    }

    /**
     * Test that {@code PAssert.thatIterable().hashCode()} is unsupported. See {@link #testPAssertEqualsIterableUnsupported}.
     */
    // test of deprecated function
    @SuppressWarnings("deprecation")
    @Test
    public void testPAssertHashCodeIterableUnsupported() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage(".hashCode() is not supported.");
        PCollection<Integer> pcollection = pipeline.apply(Create.of(42));
        PAssert.that(pcollection).hashCode();
    }

    /**
     * Basic test for {@code isEqualTo}.
     */
    @Test
    @Category({ ValidatesRunner.class, UsesSideInputs.class })
    public void testIsEqualTo() throws Exception {
        PCollection<Integer> pcollection = pipeline.apply(Create.of(43));
        PAssert.thatSingleton(pcollection).isEqualTo(43);
        pipeline.run();
    }

    /**
     * Basic test for {@code isEqualTo}.
     */
    @Test
    @Category({ ValidatesRunner.class, UsesStatefulParDo.class// This test fails if State is unsupported despite no direct usage.
     })
    public void testWindowedIsEqualTo() throws Exception {
        PCollection<Integer> pcollection = // Materialize final panes to be able to check for single element ON_TIME panes,
        // elements might be in EARLY panes otherwise.
        pipeline.apply(Create.timestamped(TimestampedValue.of(43, new Instant(250L)), TimestampedValue.of(22, new Instant((-250L))))).apply(Window.into(FixedWindows.of(Duration.millis(500L)))).apply(WithKeys.of(0)).apply(GroupByKey.create()).apply(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<KV<Integer, Iterable<Integer>>, Integer>() {
            @ProcessElement
            public void processElement(ProcessContext ctxt) {
                for (Integer integer : ctxt.element().getValue()) {
                    ctxt.output(integer);
                }
            }
        }));
        PAssert.thatSingleton(pcollection).inOnlyPane(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(0L), new Instant(500L))).isEqualTo(43);
        PAssert.thatSingleton(pcollection).inOnlyPane(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant((-500L)), new Instant(0L))).isEqualTo(22);
        pipeline.run();
    }

    /**
     * Basic test for {@code notEqualTo}.
     */
    @Test
    @Category({ ValidatesRunner.class, UsesSideInputs.class })
    public void testNotEqualTo() throws Exception {
        PCollection<Integer> pcollection = pipeline.apply(Create.of(43));
        PAssert.thatSingleton(pcollection).notEqualTo(42);
        pipeline.run();
    }

    /**
     * Test that we throw an error for false assertion on singleton.
     */
    @Test
    @Category({ ValidatesRunner.class, UsesFailureMessage.class, UsesSideInputs.class })
    public void testPAssertEqualsSingletonFalse() throws Exception {
        PCollection<Integer> pcollection = pipeline.apply(Create.of(42));
        PAssert.thatSingleton("The value was not equal to 44", pcollection).isEqualTo(44);
        Throwable thrown = PAssertTest.runExpectingAssertionFailure(pipeline);
        String message = thrown.getMessage();
        Assert.assertThat(message, Matchers.containsString("The value was not equal to 44"));
        Assert.assertThat(message, Matchers.containsString("Expected: <44>"));
        Assert.assertThat(message, Matchers.containsString("but: was <42>"));
    }

    /**
     * Test that we throw an error for false assertion on singleton.
     */
    @Test
    @Category({ ValidatesRunner.class, UsesFailureMessage.class, UsesSideInputs.class })
    public void testPAssertEqualsSingletonFalseDefaultReasonString() throws Exception {
        PCollection<Integer> pcollection = pipeline.apply(Create.of(42));
        PAssert.thatSingleton(pcollection).isEqualTo(44);
        Throwable thrown = PAssertTest.runExpectingAssertionFailure(pipeline);
        String message = thrown.getMessage();
        Assert.assertThat(message, Matchers.containsString("Create.Values/Read(CreateSource).out"));
        Assert.assertThat(message, Matchers.containsString("Expected: <44>"));
        Assert.assertThat(message, Matchers.containsString("but: was <42>"));
    }

    /**
     * Tests that {@code containsInAnyOrder} is actually order-independent.
     */
    @Test
    @Category(ValidatesRunner.class)
    public void testContainsInAnyOrder() throws Exception {
        PCollection<Integer> pcollection = pipeline.apply(Create.of(1, 2, 3, 4));
        PAssert.that(pcollection).containsInAnyOrder(2, 1, 4, 3);
        pipeline.run();
    }

    /**
     * Tests that {@code containsInAnyOrder} is actually order-independent.
     */
    @Test
    @Category(ValidatesRunner.class)
    public void testGlobalWindowContainsInAnyOrder() throws Exception {
        PCollection<Integer> pcollection = pipeline.apply(Create.of(1, 2, 3, 4));
        PAssert.that(pcollection).inWindow(INSTANCE).containsInAnyOrder(2, 1, 4, 3);
        pipeline.run();
    }

    /**
     * Tests that windowed {@code containsInAnyOrder} is actually order-independent.
     */
    @Test
    @Category(ValidatesRunner.class)
    public void testWindowedContainsInAnyOrder() throws Exception {
        PCollection<Integer> pcollection = pipeline.apply(Create.timestamped(TimestampedValue.of(1, new Instant(100L)), TimestampedValue.of(2, new Instant(200L)), TimestampedValue.of(3, new Instant(300L)), TimestampedValue.of(4, new Instant(400L)))).apply(Window.into(SlidingWindows.of(Duration.millis(200L)).every(Duration.millis(100L)).withOffset(Duration.millis(50L))));
        PAssert.that(pcollection).inWindow(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant((-50L)), new Instant(150L))).containsInAnyOrder(1);
        PAssert.that(pcollection).inWindow(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(50L), new Instant(250L))).containsInAnyOrder(2, 1);
        PAssert.that(pcollection).inWindow(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(150L), new Instant(350L))).containsInAnyOrder(2, 3);
        PAssert.that(pcollection).inWindow(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(250L), new Instant(450L))).containsInAnyOrder(4, 3);
        PAssert.that(pcollection).inWindow(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(350L), new Instant(550L))).containsInAnyOrder(4);
        pipeline.run();
    }

    @Test
    @Category(ValidatesRunner.class)
    public void testEmpty() {
        PCollection<Long> vals = pipeline.apply(Create.empty(VarLongCoder.of()));
        PAssert.that(vals).empty();
        pipeline.run();
    }

    /**
     * Tests that {@code containsInAnyOrder} fails when and how it should.
     */
    @Test
    @Category({ ValidatesRunner.class, UsesFailureMessage.class })
    public void testContainsInAnyOrderFalse() throws Exception {
        PCollection<Integer> pcollection = pipeline.apply(Create.of(1, 2, 3, 4));
        PAssert.that(pcollection).containsInAnyOrder(2, 1, 4, 3, 7);
        Throwable exc = PAssertTest.runExpectingAssertionFailure(pipeline);
        Pattern expectedPattern = Pattern.compile("Expected: iterable over \\[((<4>|<7>|<3>|<2>|<1>)(, )?){5}\\] in any order");
        // A loose pattern, but should get the job done.
        Assert.assertTrue((((("Expected error message from PAssert with substring matching " + expectedPattern) + " but the message was \"") + (exc.getMessage())) + "\""), expectedPattern.matcher(exc.getMessage()).find());
    }

    @Test
    @Category({ ValidatesRunner.class, UsesFailureMessage.class })
    public void testEmptyFalse() throws Exception {
        PCollection<Long> vals = pipeline.apply(GenerateSequence.from(0).to(5));
        PAssert.that("Vals should have been empty", vals).empty();
        Throwable thrown = PAssertTest.runExpectingAssertionFailure(pipeline);
        String message = thrown.getMessage();
        Assert.assertThat(message, Matchers.containsString("Vals should have been empty"));
        Assert.assertThat(message, Matchers.containsString("Expected: iterable over [] in any order"));
    }

    @Test
    @Category({ ValidatesRunner.class, UsesFailureMessage.class })
    public void testEmptyFalseDefaultReasonString() throws Exception {
        PCollection<Long> vals = pipeline.apply(GenerateSequence.from(0).to(5));
        PAssert.that(vals).empty();
        Throwable thrown = PAssertTest.runExpectingAssertionFailure(pipeline);
        String message = thrown.getMessage();
        Assert.assertThat(message, Matchers.containsString("GenerateSequence/Read(BoundedCountingSource).out"));
        Assert.assertThat(message, Matchers.containsString("Expected: iterable over [] in any order"));
    }

    @Test
    public void testAssertionSiteIsCaptured() {
        // This check should return a failure.
        SuccessOrFailure res = PAssert.doChecks(PAssertionSite.capture("Captured assertion message."), 10, new org.apache.beam.sdk.testing.PAssert.PCollectionContentsAssert.MatcherCheckerFn(SerializableMatchers.contains(11)));
        String stacktrace = Throwables.getStackTraceAsString(res.assertionError());
        Assert.assertEquals(false, res.isSuccess());
        Assert.assertThat(stacktrace, Matchers.containsString("PAssertionSite.capture"));
    }

    @Test
    @Category({ ValidatesRunner.class, UsesFailureMessage.class })
    public void testAssertionSiteIsCapturedWithMessage() throws Exception {
        PCollection<Long> vals = pipeline.apply(GenerateSequence.from(0).to(5));
        PAssertTest.assertThatCollectionIsEmptyWithMessage(vals);
        Throwable thrown = PAssertTest.runExpectingAssertionFailure(pipeline);
        Assert.assertThat(thrown.getMessage(), Matchers.containsString("Should be empty"));
        Assert.assertThat(thrown.getMessage(), Matchers.containsString("Expected: iterable over [] in any order"));
        String stacktrace = Throwables.getStackTraceAsString(thrown);
        Assert.assertThat(stacktrace, Matchers.containsString("testAssertionSiteIsCapturedWithMessage"));
        Assert.assertThat(stacktrace, Matchers.containsString("assertThatCollectionIsEmptyWithMessage"));
    }

    @Test
    @Category({ ValidatesRunner.class, UsesFailureMessage.class })
    public void testAssertionSiteIsCapturedWithoutMessage() throws Exception {
        PCollection<Long> vals = pipeline.apply(GenerateSequence.from(0).to(5));
        PAssertTest.assertThatCollectionIsEmptyWithoutMessage(vals);
        Throwable thrown = PAssertTest.runExpectingAssertionFailure(pipeline);
        Assert.assertThat(thrown.getMessage(), Matchers.containsString("Expected: iterable over [] in any order"));
        String stacktrace = Throwables.getStackTraceAsString(thrown);
        Assert.assertThat(stacktrace, Matchers.containsString("testAssertionSiteIsCapturedWithoutMessage"));
        Assert.assertThat(stacktrace, Matchers.containsString("assertThatCollectionIsEmptyWithoutMessage"));
    }

    @Test
    public void countAssertsSucceeds() {
        PCollection<Integer> create = pipeline.apply("FirstCreate", Create.of(1, 2, 3));
        PAssert.that(create).containsInAnyOrder(1, 2, 3);
        PAssert.thatSingleton(create.apply(Sum.integersGlobally())).isEqualTo(6);
        PAssert.thatMap(pipeline.apply("CreateMap", Create.of(KV.of(1, 2)))).isEqualTo(Collections.singletonMap(1, 2));
        Assert.assertThat(PAssert.countAsserts(pipeline), Matchers.equalTo(3));
    }

    @Test
    public void countAssertsMultipleCallsIndependent() {
        PCollection<Integer> create = pipeline.apply("FirstCreate", Create.of(1, 2, 3));
        PAssert.that(create).containsInAnyOrder(1, 2, 3);
        PAssert.thatSingleton(create.apply(Sum.integersGlobally())).isEqualTo(6);
        Assert.assertThat(PAssert.countAsserts(pipeline), Matchers.equalTo(2));
        PAssert.thatMap(pipeline.apply("CreateMap", Create.of(KV.of(1, 2)))).isEqualTo(Collections.singletonMap(1, 2));
        Assert.assertThat(PAssert.countAsserts(pipeline), Matchers.equalTo(3));
    }
}

