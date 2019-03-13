/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.skyframe;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Interner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.devtools.build.lib.concurrent.BlazeInterners;
import com.google.devtools.build.lib.events.Event;
import com.google.devtools.build.lib.events.EventHandler;
import com.google.devtools.build.lib.events.EventKind;
import com.google.devtools.build.lib.events.ExtendedEventHandler;
import com.google.devtools.build.lib.events.ExtendedEventHandler.Postable;
import com.google.devtools.build.lib.events.Reporter;
import com.google.devtools.build.lib.events.StoredEventHandler;
import com.google.devtools.build.lib.testutil.EventIterableSubjectFactory;
import com.google.devtools.build.lib.testutil.TestThread;
import com.google.devtools.build.lib.testutil.TestUtils;
import com.google.devtools.build.skyframe.SkyFunctionException.Transience;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.google.devtools.build.skyframe.NotifyingHelper.EventType.ADD_EXTERNAL_DEP;
import static com.google.devtools.build.skyframe.NotifyingHelper.EventType.SET_VALUE;
import static com.google.devtools.build.skyframe.NotifyingHelper.EventType.SIGNAL;
import static com.google.devtools.build.skyframe.NotifyingHelper.Order.AFTER;


/**
 * Tests for {@link ParallelEvaluator}.
 */
@RunWith(JUnit4.class)
public class ParallelEvaluatorTest {
    private static final SkyFunctionName CHILD_TYPE = SkyFunctionName.createHermetic("child");

    private static final SkyFunctionName PARENT_TYPE = SkyFunctionName.createHermetic("parent");

    protected ProcessableGraph graph;

    protected IntVersion graphVersion = IntVersion.of(0);

    protected GraphTester tester = new GraphTester();

    private StoredEventHandler storedEventHandler;

    private DirtyTrackingProgressReceiver revalidationReceiver = new DirtyTrackingProgressReceiver(null);

    @Test
    public void smoke() throws Exception {
        graph = new InMemoryGraphImpl();
        set("a", "a");
        set("b", "b");
        tester.getOrCreate("ab").addDependency("a").addDependency("b").setComputedValue(GraphTester.CONCATENATE);
        GraphTester.StringValue value = ((GraphTester.StringValue) (eval(false, GraphTester.toSkyKey("ab"))));
        assertThat(value.getValue()).isEqualTo("ab");
        assertThat(storedEventHandler.getEvents()).isEmpty();
        assertThat(storedEventHandler.getPosts()).isEmpty();
    }

    @Test
    public void enqueueDoneFuture() throws Exception {
        final SkyKey parentKey = GraphTester.toSkyKey("parentKey");
        tester.getOrCreate(parentKey).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws InterruptedException {
                SettableFuture<SkyValue> future = SettableFuture.create();
                future.set(new GraphTester.StringValue("good"));
                env.dependOnFuture(future);
                assertThat(env.valuesMissing()).isFalse();
                try {
                    return future.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        graph = new InMemoryGraphImpl();
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, ImmutableList.of(parentKey));
        assertThat(result.hasError()).isFalse();
        assertThat(result.get(parentKey)).isEqualTo(new GraphTester.StringValue("good"));
    }

    @Test
    public void enqueueBadFuture() throws Exception {
        final SkyKey parentKey = GraphTester.toSkyKey("parentKey");
        final CountDownLatch doneLatch = new CountDownLatch(1);
        final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
        tester.getOrCreate(parentKey).setBuilder(new SkyFunction() {
            private ListenableFuture<SkyValue> future;

            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws InterruptedException {
                if ((future) == null) {
                    future = executor.submit(() -> {
                        doneLatch.await();
                        throw new UnsupportedOperationException();
                    });
                    env.dependOnFuture(future);
                    assertThat(env.valuesMissing()).isTrue();
                    return null;
                }
                assertThat(future.isDone()).isTrue();
                try {
                    future.get();
                    Assert.fail();
                } catch (ExecutionException expected) {
                    assertThat(expected.getCause()).isInstanceOf(UnsupportedOperationException.class);
                }
                return new GraphTester.StringValue("Caught!");
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        graph = NotifyingHelper.makeNotifyingTransformer(new NotifyingHelper.Listener() {
            @Override
            public void accept(SkyKey key, NotifyingHelper.EventType type, NotifyingHelper.Order order, Object context) {
                // NodeEntry.addExternalDep is called as part of bookkeeping at the end of
                // AbstractParallelEvaluator.Evaluate#run.
                if ((key == parentKey) && (type == (ADD_EXTERNAL_DEP))) {
                    doneLatch.countDown();
                }
            }
        }).transform(new InMemoryGraphImpl());
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, ImmutableList.of(parentKey));
        assertThat(result.hasError()).isFalse();
        assertThat(result.get(parentKey)).isEqualTo(new GraphTester.StringValue("Caught!"));
    }

    @Test
    public void dependsOnKeyAndFuture() throws Exception {
        final SkyKey parentKey = GraphTester.toSkyKey("parentKey");
        final SkyKey childKey = GraphTester.toSkyKey("childKey");
        final CountDownLatch doneLatch = new CountDownLatch(1);
        tester.getOrCreate(childKey).setConstantValue(new GraphTester.StringValue("child"));
        tester.getOrCreate(parentKey).setBuilder(new SkyFunction() {
            private SettableFuture<SkyValue> future;

            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws InterruptedException {
                SkyValue child = env.getValue(childKey);
                if ((future) == null) {
                    assertThat(child).isNull();
                    future = SettableFuture.create();
                    env.dependOnFuture(future);
                    assertThat(env.valuesMissing()).isTrue();
                    new Thread(() -> {
                        try {
                            doneLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        future.set(new GraphTester.StringValue("future"));
                    }).start();
                    return null;
                }
                assertThat(child).isEqualTo(new GraphTester.StringValue("child"));
                assertThat(future.isDone()).isTrue();
                try {
                    assertThat(future.get()).isEqualTo(new GraphTester.StringValue("future"));
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
                return new GraphTester.StringValue("All done!");
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        graph = NotifyingHelper.makeNotifyingTransformer(new NotifyingHelper.Listener() {
            @Override
            public void accept(SkyKey key, NotifyingHelper.EventType type, NotifyingHelper.Order order, Object context) {
                if ((key == childKey) && (type == (SET_VALUE))) {
                    doneLatch.countDown();
                }
            }
        }).transform(new InMemoryGraphImpl());
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, ImmutableList.of(parentKey));
        assertThat(result.hasError()).isFalse();
        assertThat(result.get(parentKey)).isEqualTo(new GraphTester.StringValue("All done!"));
    }

    /**
     * Test interruption handling when a long-running SkyFunction gets interrupted.
     */
    @Test
    public void interruptedFunction() throws Exception {
        runInterruptionTest(new ParallelEvaluatorTest.SkyFunctionFactory() {
            @Override
            public SkyFunction create(final Semaphore threadStarted, final String[] errorMessage) {
                return new SkyFunction() {
                    @Override
                    public SkyValue compute(SkyKey key, Environment env) throws InterruptedException {
                        // Signal the waiting test thread that the evaluator thread has really started.
                        threadStarted.release();
                        // Simulate a SkyFunction that runs for 10 seconds (this number was chosen arbitrarily).
                        // The main thread should interrupt it shortly after it got started.
                        Thread.sleep((10 * 1000));
                        // Set an error message to indicate that the expected interruption didn't happen.
                        // We can't use Assert.fail(String) on an async thread.
                        errorMessage[0] = "SkyFunction should have been interrupted";
                        return null;
                    }

                    @Nullable
                    @Override
                    public String extractTag(SkyKey skyKey) {
                        return null;
                    }
                };
            }
        });
    }

    /**
     * Test interruption handling when the Evaluator is in-between running SkyFunctions.
     *
     * <p>This is the point in time after a SkyFunction requested a dependency which is not yet built
     * so the builder returned null to the Evaluator, and the latter is about to schedule evaluation
     * of the missing dependency but gets interrupted before the dependency's SkyFunction could start.
     */
    @Test
    public void interruptedEvaluatorThread() throws Exception {
        runInterruptionTest(new ParallelEvaluatorTest.SkyFunctionFactory() {
            @Override
            public SkyFunction create(final Semaphore threadStarted, final String[] errorMessage) {
                return new SkyFunction() {
                    // No need to synchronize access to this field; we always request just one more
                    // dependency, so it's only one SkyFunction running at any time.
                    private int valueIdCounter = 0;

                    @Override
                    public SkyValue compute(SkyKey key, Environment env) throws InterruptedException {
                        // Signal the waiting test thread that the Evaluator thread has really started.
                        threadStarted.release();
                        // Keep the evaluator busy until the test's thread gets scheduled and can
                        // interrupt the Evaluator's thread.
                        env.getValue(GraphTester.toSkyKey(("a" + ((valueIdCounter)++))));
                        // This method never throws InterruptedException, therefore it's the responsibility
                        // of the Evaluator to detect the interrupt and avoid calling subsequent
                        // SkyFunctions.
                        return null;
                    }

                    @Nullable
                    @Override
                    public String extractTag(SkyKey skyKey) {
                        return null;
                    }
                };
            }
        });
    }

    @Test
    public void interruptedEvaluatorThreadAfterEnqueueBeforeWaitForCompletionAndConstructResult() throws InterruptedException {
        // This is a regression test for a crash bug in
        // AbstractExceptionalParallelEvaluator#doMutatingEvaluation in a very specific window of time
        // between enqueueing one top-level node for evaluation and checking if another top-level node
        // is done.
        // When we have two top-level nodes, A and B,
        SkyKey keyA = GraphTester.toSkyKey("a");
        SkyKey keyB = GraphTester.toSkyKey("b");
        // And rig the graph and node entries, such that B's addReverseDepAndCheckIfDone waits for A to
        // start computing and then tries to observe an interrupt (which will happen on the calling
        // thread, aka the main Skyframe evaluation thread),
        CountDownLatch keyAStartedComputingLatch = new CountDownLatch(1);
        CountDownLatch keyBAddReverseDepAndCheckIfDoneLatch = new CountDownLatch(1);
        NodeEntry nodeEntryB = Mockito.mock(NodeEntry.class);
        AtomicBoolean keyBAddReverseDepAndCheckIfDoneInterrupted = new AtomicBoolean(false);
        Mockito.doAnswer(( invocation) -> {
            keyAStartedComputingLatch.await();
            keyBAddReverseDepAndCheckIfDoneLatch.countDown();
            try {
                Thread.sleep(TestUtils.WAIT_TIMEOUT_MILLISECONDS);
                throw new IllegalStateException("shouldn't get here");
            } catch (InterruptedException e) {
                keyBAddReverseDepAndCheckIfDoneInterrupted.set(true);
                throw e;
            }
        }).when(nodeEntryB).addReverseDepAndCheckIfDone(Mockito.eq(null));
        graph = new InMemoryGraphImpl() {
            @Override
            protected NodeEntry newNodeEntry(SkyKey key) {
                return key.equals(keyB) ? nodeEntryB : super.newNodeEntry(key);
            }
        };
        // And A's SkyFunction tries to observe an interrupt after it starts computing,
        AtomicBoolean keyAComputeInterrupted = new AtomicBoolean(false);
        tester.getOrCreate(keyA).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws InterruptedException {
                keyAStartedComputingLatch.countDown();
                try {
                    Thread.sleep(TestUtils.WAIT_TIMEOUT_MILLISECONDS);
                    throw new IllegalStateException("shouldn't get here");
                } catch (InterruptedException e) {
                    keyAComputeInterrupted.set(true);
                    throw e;
                }
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        // And we have a dedicated thread that kicks off the evaluation of A and B together (in that
        // order).
        TestThread evalThread = new TestThread() {
            @Override
            public void runTest() throws Exception {
                try {
                    /* keepGoing= */
                    eval(true, keyA, keyB);
                    Assert.fail();
                } catch (InterruptedException e) {
                    // Expected.
                }
            }
        };
        // Then when we start that thread,
        evalThread.start();
        // We (the thread running the test) are able to observe that B's addReverseDepAndCheckIfDone has
        // just been called (implying that A has started to be computed).
        assertThat(keyBAddReverseDepAndCheckIfDoneLatch.await(TestUtils.WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();
        // Then when we interrupt the evaluation thread,
        evalThread.interrupt();
        // The evaluation thread eventually terminates.
        evalThread.joinAndAssertState(TestUtils.WAIT_TIMEOUT_MILLISECONDS);
        // And we are able to verify both that A's SkyFunction had observed an interrupt,
        assertThat(keyAComputeInterrupted.get()).isTrue();
        // And also that B's addReverseDepAndCheckIfDoneInterrupted had observed an interrupt.
        assertThat(keyBAddReverseDepAndCheckIfDoneInterrupted.get()).isTrue();
    }

    @Test
    public void partialResultOnInterruption() throws Exception {
        /* buildFastFirst= */
        runPartialResultOnInterruption(false);
    }

    @Test
    public void partialCachedResultOnInterruption() throws Exception {
        /* buildFastFirst= */
        runPartialResultOnInterruption(true);
    }

    /**
     * Factory for SkyFunctions for interruption testing (see {@link #runInterruptionTest}).
     */
    private interface SkyFunctionFactory {
        /**
         * Creates a SkyFunction suitable for a specific test scenario.
         *
         * @param threadStarted
         * 		a latch which the returned SkyFunction must
         * 		{@link Semaphore#release() release} once it started (otherwise the test won't work)
         * @param errorMessage
         * 		a single-element array; the SkyFunction can put a error message in it
         * 		to indicate that an assertion failed (calling {@code fail} from async thread doesn't
         * 		work)
         */
        SkyFunction create(final Semaphore threadStarted, final String[] errorMessage);
    }

    @Test
    public void unrecoverableError() throws Exception {
        class CustomRuntimeException extends RuntimeException {}
        final CustomRuntimeException expected = new CustomRuntimeException();
        final SkyFunction builder = new SkyFunction() {
            @Override
            @Nullable
            public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
                throw expected;
            }

            @Override
            @Nullable
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        };
        final ParallelEvaluator evaluator = makeEvaluator(new InMemoryGraphImpl(), ImmutableMap.of(GraphTester.NODE_TYPE, builder), false);
        SkyKey valueToEval = GraphTester.toSkyKey("a");
        try {
            evaluator.eval(ImmutableList.of(valueToEval));
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException re) {
            assertThat(re).hasMessageThat().contains((("Unrecoverable error while evaluating node '" + (valueToEval.toString())) + "'"));
            assertThat(re).hasCauseThat().isInstanceOf(CustomRuntimeException.class);
        }
    }

    @Test
    public void simpleWarning() throws Exception {
        graph = new InMemoryGraphImpl();
        set("a", "a").setWarning("warning on 'a'");
        GraphTester.StringValue value = ((GraphTester.StringValue) (eval(false, GraphTester.toSkyKey("a"))));
        assertThat(value.getValue()).isEqualTo("a");
        EventIterableSubjectFactory.assertThatEvents(storedEventHandler.getEvents()).containsExactly("warning on 'a'");
    }

    /**
     * Regression test: events from already-done value not replayed.
     */
    @Test
    public void eventFromDoneChildRecorded() throws Exception {
        graph = new InMemoryGraphImpl();
        set("a", "a").setWarning("warning on 'a'");
        SkyKey a = GraphTester.toSkyKey("a");
        SkyKey top = GraphTester.toSkyKey("top");
        tester.getOrCreate(top).addDependency(a).setComputedValue(GraphTester.CONCATENATE);
        // Build a so that it is already in the graph.
        eval(false, a);
        assertThat(storedEventHandler.getEvents()).hasSize(1);
        storedEventHandler.clear();
        // Build top. The warning from a should be printed.
        eval(false, top);
        assertThat(storedEventHandler.getEvents()).hasSize(1);
        storedEventHandler.clear();
        // Build top again. The warning should have been stored in the value.
        eval(false, top);
        assertThat(storedEventHandler.getEvents()).hasSize(1);
    }

    @Test
    public void postableFromDoneChildRecorded() throws Exception {
        graph = new InMemoryGraphImpl();
        Postable post = new Postable() {};
        set("a", "a").setPostable(post);
        SkyKey a = GraphTester.toSkyKey("a");
        SkyKey top = GraphTester.toSkyKey("top");
        tester.getOrCreate(top).addDependency(a).setComputedValue(GraphTester.CONCATENATE);
        // Build a so that it is already in the graph.
        eval(false, a);
        assertThat(storedEventHandler.getPosts()).containsExactly(post);
        storedEventHandler.clear();
        // Build top. The post from a should be printed.
        eval(false, top);
        assertThat(storedEventHandler.getPosts()).containsExactly(post);
        storedEventHandler.clear();
        // Build top again. The post should have been stored in the value.
        eval(false, top);
        assertThat(storedEventHandler.getPosts()).containsExactly(post);
    }

    @Test
    public void eventReportedTimely() throws Exception {
        graph = new InMemoryGraphImpl();
        set("a", "a").setWarning("warning on 'a'");
        SkyKey a = GraphTester.toSkyKey("a");
        SkyKey top = GraphTester.toSkyKey("top");
        tester.getOrCreate(top).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey key, Environment env) throws SkyFunctionException, InterruptedException {
                // The event from a should already have been posted.
                assertThat(storedEventHandler.getEvents()).hasSize(1);
                return new GraphTester.StringValue("foo");
            }

            @Override
            @Nullable
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        // Build a so that it is already in the graph.
        eval(false, a);
        storedEventHandler.clear();
        // Build top. The warning from a should be printed before evaluating top.
        eval(false, ImmutableList.of(a, top));
        assertThat(storedEventHandler.getEvents()).hasSize(1);
        storedEventHandler.clear();
    }

    @Test
    public void errorOfTopLevelTargetReported() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey a = GraphTester.toSkyKey("a");
        SkyKey b = GraphTester.toSkyKey("b");
        tester.getOrCreate(b).setHasError(true);
        Event errorEvent = Event.error("foobar");
        tester.getOrCreate(a).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey key, Environment env) throws SkyFunctionException, InterruptedException {
                try {
                    if ((env.getValueOrThrow(b, SomeErrorException.class)) == null) {
                        return null;
                    }
                } catch (SomeErrorException ignored) {
                    // Continue silently.
                }
                env.getListener().handle(errorEvent);
                throw new SkyFunctionException(new SomeErrorException("bazbar"), Transience.PERSISTENT) {};
            }

            @Override
            @Nullable
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        eval(false, a);
        assertThat(storedEventHandler.getEvents()).containsExactly(errorEvent);
    }

    @Test
    public void storedEventFilter() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey a = GraphTester.toSkyKey("a");
        final AtomicBoolean evaluated = new AtomicBoolean(false);
        tester.getOrCreate(a).setBuilder(new SkyFunction() {
            @Nullable
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) {
                evaluated.set(true);
                env.getListener().handle(Event.error(null, "boop"));
                env.getListener().handle(Event.warn(null, "beep"));
                return new GraphTester.StringValue("a");
            }

            @Nullable
            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        ParallelEvaluator evaluator = /* keepGoing= */
        makeEvaluator(graph, tester.getSkyFunctionMap(), false, new EventFilter() {
            @Override
            public boolean apply(Event event) {
                return (event.getKind()) == (EventKind.ERROR);
            }

            @Override
            public boolean storeEventsAndPosts() {
                return true;
            }
        });
        evaluator.eval(ImmutableList.of(a));
        assertThat(evaluated.get()).isTrue();
        assertThat(storedEventHandler.getEvents()).hasSize(2);
        EventIterableSubjectFactory.assertThatEvents(storedEventHandler.getEvents()).containsExactly("boop", "beep");
        storedEventHandler.clear();
        evaluator = /* keepGoing= */
        makeEvaluator(graph, tester.getSkyFunctionMap(), false);
        evaluated.set(false);
        evaluator.eval(ImmutableList.of(a));
        assertThat(evaluated.get()).isFalse();
        EventIterableSubjectFactory.assertThatEvents(storedEventHandler.getEvents()).containsExactly("boop");
    }

    @Test
    public void shouldCreateErrorValueWithRootCause() throws Exception {
        graph = new InMemoryGraphImpl();
        set("a", "a");
        SkyKey parentErrorKey = GraphTester.toSkyKey("parent");
        SkyKey errorKey = GraphTester.toSkyKey("error");
        tester.getOrCreate(parentErrorKey).addDependency("a").addDependency(errorKey).setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate(errorKey).setHasError(true);
        ErrorInfo error = evalValueInError(parentErrorKey);
        assertThat(error.getRootCauses()).containsExactly(errorKey);
    }

    @Test
    public void shouldBuildOneTarget() throws Exception {
        graph = new InMemoryGraphImpl();
        set("a", "a");
        set("b", "b");
        SkyKey parentErrorKey = GraphTester.toSkyKey("parent");
        SkyKey errorFreeKey = GraphTester.toSkyKey("ab");
        SkyKey errorKey = GraphTester.toSkyKey("error");
        tester.getOrCreate(parentErrorKey).addDependency(errorKey).addDependency("a").setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate(errorKey).setHasError(true);
        tester.getOrCreate(errorFreeKey).addDependency("a").addDependency("b").setComputedValue(GraphTester.CONCATENATE);
        EvaluationResult<GraphTester.StringValue> result = eval(true, parentErrorKey, errorFreeKey);
        ErrorInfo error = result.getError(parentErrorKey);
        assertThat(error.getRootCauses()).containsExactly(errorKey);
        GraphTester.StringValue abValue = result.get(errorFreeKey);
        assertThat(abValue.getValue()).isEqualTo("ab");
    }

    @Test
    public void catastropheHaltsBuild_KeepGoing_KeepEdges() throws Exception {
        catastrophicBuild(true, true);
    }

    @Test
    public void catastropheHaltsBuild_KeepGoing_NoKeepEdges() throws Exception {
        catastrophicBuild(true, false);
    }

    @Test
    public void catastropheInBuild_NoKeepGoing_KeepEdges() throws Exception {
        catastrophicBuild(false, true);
    }

    @Test
    public void incrementalCycleWithCatastropheAndFailedBubbleUp() throws Exception {
        SkyKey topKey = GraphTester.toSkyKey("top");
        // Comes alphabetically before "top".
        SkyKey cycleKey = GraphTester.toSkyKey("cycle");
        SkyKey catastropheKey = GraphTester.toSkyKey("catastrophe");
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        GraphTester.StringValue topValue = new GraphTester.StringValue("top");
        tester.getOrCreate(topKey).setBuilder(new SkyFunction() {
            @Nullable
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws InterruptedException {
                env.getValues(ImmutableList.of(cycleKey));
                return env.valuesMissing() ? null : topValue;
            }

            @Nullable
            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        tester.getOrCreate(cycleKey).setBuilder(new SkyFunction() {
            @Nullable
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws InterruptedException {
                env.getValues(ImmutableList.of(cycleKey, catastropheKey));
                Preconditions.checkState(env.valuesMissing());
                return null;
            }

            @Nullable
            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        tester.getOrCreate(catastropheKey).setBuilder(new SkyFunction() {
            @Nullable
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException {
                throw new SkyFunctionException(new SomeErrorException("catastrophe"), Transience.TRANSIENT) {
                    @Override
                    public boolean isCatastrophic() {
                        return true;
                    }
                };
            }

            @Nullable
            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(topKey));
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasError();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(topKey).hasCycleInfoThat().containsExactly(new CycleInfo(ImmutableList.of(topKey), ImmutableList.of(cycleKey)));
    }

    @Test
    public void parentFailureDoesntAffectChild() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey parentKey = GraphTester.toSkyKey("parent");
        tester.getOrCreate(parentKey).setHasError(true);
        SkyKey childKey = GraphTester.toSkyKey("child");
        set("child", "onions");
        tester.getOrCreate(parentKey).addDependency(childKey).setComputedValue(GraphTester.CONCATENATE);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, parentKey, childKey);
        // Child is guaranteed to complete successfully before parent can run (and fail),
        // since parent depends on it.
        GraphTester.StringValue childValue = result.get(childKey);
        assertThat(childValue).isNotNull();
        assertThat(childValue.getValue()).isEqualTo("onions");
        ErrorInfo error = result.getError(parentKey);
        assertThat(error).isNotNull();
        assertThat(error.getRootCauses()).containsExactly(parentKey);
    }

    @Test
    public void newParentOfErrorShouldHaveError() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey errorKey = GraphTester.toSkyKey("error");
        tester.getOrCreate(errorKey).setHasError(true);
        ErrorInfo error = evalValueInError(errorKey);
        assertThat(error.getRootCauses()).containsExactly(errorKey);
        SkyKey parentKey = GraphTester.toSkyKey("parent");
        tester.getOrCreate(parentKey).addDependency("error").setComputedValue(GraphTester.CONCATENATE);
        error = evalValueInError(parentKey);
        assertThat(error.getRootCauses()).containsExactly(errorKey);
    }

    @Test
    public void errorTwoLevelsDeep() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey parentKey = GraphTester.toSkyKey("parent");
        SkyKey errorKey = GraphTester.toSkyKey("error");
        tester.getOrCreate(errorKey).setHasError(true);
        tester.getOrCreate("mid").addDependency(errorKey).setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate(parentKey).addDependency("mid").setComputedValue(GraphTester.CONCATENATE);
        ErrorInfo error = evalValueInError(parentKey);
        assertThat(error.getRootCauses()).containsExactly(errorKey);
    }

    @Test
    public void valueNotUsedInFailFastErrorRecovery() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey topKey = GraphTester.toSkyKey("top");
        SkyKey recoveryKey = GraphTester.toSkyKey("midRecovery");
        SkyKey badKey = GraphTester.toSkyKey("bad");
        tester.getOrCreate(topKey).addDependency(recoveryKey).setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate(recoveryKey).addErrorDependency(badKey, new GraphTester.StringValue("i recovered")).setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate(badKey).setHasError(true);
        EvaluationResult<SkyValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(recoveryKey));
        assertThat(result.errorMap()).isEmpty();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        assertThat(result.get(recoveryKey)).isEqualTo(new GraphTester.StringValue("i recovered"));
        result = /* keepGoing= */
        eval(false, ImmutableList.of(topKey));
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasError();
        assertThat(result.keyNames()).isEmpty();
        assertThat(result.errorMap()).hasSize(1);
        assertThat(result.getError(topKey).getException()).isNotNull();
    }

    @Test
    public void multipleRootCauses() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey parentKey = GraphTester.toSkyKey("parent");
        SkyKey errorKey = GraphTester.toSkyKey("error");
        SkyKey errorKey2 = GraphTester.toSkyKey("error2");
        SkyKey errorKey3 = GraphTester.toSkyKey("error3");
        tester.getOrCreate(errorKey).setHasError(true);
        tester.getOrCreate(errorKey2).setHasError(true);
        tester.getOrCreate(errorKey3).setHasError(true);
        tester.getOrCreate("mid").addDependency(errorKey).addDependency(errorKey2).setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate(parentKey).addDependency("mid").addDependency(errorKey2).addDependency(errorKey3).setComputedValue(GraphTester.CONCATENATE);
        ErrorInfo error = evalValueInError(parentKey);
        assertThat(error.getRootCauses()).containsExactly(errorKey, errorKey2, errorKey3);
    }

    @Test
    public void rootCauseWithNoKeepGoing() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey parentKey = GraphTester.toSkyKey("parent");
        SkyKey errorKey = GraphTester.toSkyKey("error");
        tester.getOrCreate(errorKey).setHasError(true);
        tester.getOrCreate("mid").addDependency(errorKey).setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate(parentKey).addDependency("mid").setComputedValue(GraphTester.CONCATENATE);
        EvaluationResult<GraphTester.StringValue> result = eval(false, ImmutableList.of(parentKey));
        Map.Entry<SkyKey, ErrorInfo> error = Iterables.getOnlyElement(result.errorMap().entrySet());
        assertThat(error.getKey()).isEqualTo(parentKey);
        assertThat(error.getValue().getRootCauses()).containsExactly(errorKey);
    }

    @Test
    public void errorBubblesToParentsOfTopLevelValue() throws Exception {
        SkyKey parentKey = GraphTester.toSkyKey("parent");
        SkyKey errorKey = GraphTester.toSkyKey("error");
        CountDownLatch latch = new CountDownLatch(1);
        graph = new NotifyingHelper.NotifyingProcessableGraph(new InMemoryGraphImpl(), ( key, type, order, context) -> {
            if ((((key.equals(errorKey)) && (parentKey.equals(context))) && (type == EventType.ADD_REVERSE_DEP)) && (order == Order.AFTER)) {
                latch.countDown();
            }
        });
        tester.getOrCreate(errorKey).setBuilder(/* waitToFinish= */
        /* value= */
        new ChainedFunction(null, latch, null, false, null, ImmutableList.<SkyKey>of()));
        tester.getOrCreate(parentKey).addDependency(errorKey).setComputedValue(GraphTester.CONCATENATE);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, ImmutableList.of(parentKey, errorKey));
        assertWithMessage(result.toString()).that(result.errorMap().size()).isEqualTo(2);
    }

    @Test
    public void noKeepGoingAfterKeepGoingFails() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey errorKey = GraphTester.toSkyKey("my_error_value");
        tester.getOrCreate(errorKey).setHasError(true);
        SkyKey parentKey = GraphTester.toSkyKey("parent");
        tester.getOrCreate(parentKey).addDependency(errorKey);
        ErrorInfo error = evalValueInError(parentKey);
        assertThat(error.getRootCauses()).containsExactly(errorKey);
        SkyKey[] list = new SkyKey[]{ parentKey };
        EvaluationResult<GraphTester.StringValue> result = eval(false, list);
        ErrorInfo errorInfo = result.getError();
        assertThat(errorInfo.getRootCauses()).containsExactly(errorKey);
        assertThat(errorInfo.getException()).hasMessageThat().isEqualTo(errorKey.toString());
    }

    @Test
    public void twoErrors() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey firstError = GraphTester.toSkyKey("error1");
        SkyKey secondError = GraphTester.toSkyKey("error2");
        CountDownLatch firstStart = new CountDownLatch(1);
        CountDownLatch secondStart = new CountDownLatch(1);
        tester.getOrCreate(firstError).setBuilder(/* notifyFinish= */
        /* waitForException= */
        /* value= */
        new ChainedFunction(firstStart, secondStart, null, false, null, ImmutableList.<SkyKey>of()));
        tester.getOrCreate(secondError).setBuilder(/* notifyFinish= */
        /* waitForException= */
        /* value= */
        new ChainedFunction(secondStart, firstStart, null, false, null, ImmutableList.<SkyKey>of()));
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, firstError, secondError);
        assertWithMessage(result.toString()).that(result.hasError()).isTrue();
        // With keepGoing=false, the eval call will terminate with exactly one error (the first one
        // thrown). But the first one thrown here is non-deterministic since we synchronize the
        // builders so that they run at roughly the same time.
        assertThat(ImmutableSet.of(firstError, secondError)).contains(Iterables.getOnlyElement(result.errorMap().keySet()));
    }

    @Test
    public void simpleCycle() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        tester.getOrCreate(aKey).addDependency(bKey);
        tester.getOrCreate(bKey).addDependency(aKey);
        ErrorInfo errorInfo = eval(false, ImmutableList.of(aKey)).getError();
        assertThat(errorInfo.getException()).isNull();
        CycleInfo cycleInfo = Iterables.getOnlyElement(errorInfo.getCycleInfo());
        assertThat(cycleInfo.getCycle()).containsExactly(aKey, bKey).inOrder();
        assertThat(cycleInfo.getPathToCycle()).isEmpty();
    }

    @Test
    public void cycleWithHead() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        SkyKey topKey = GraphTester.toSkyKey("top");
        SkyKey midKey = GraphTester.toSkyKey("mid");
        tester.getOrCreate(topKey).addDependency(midKey);
        tester.getOrCreate(midKey).addDependency(aKey);
        tester.getOrCreate(aKey).addDependency(bKey);
        tester.getOrCreate(bKey).addDependency(aKey);
        ErrorInfo errorInfo = eval(false, ImmutableList.of(topKey)).getError();
        assertThat(errorInfo.getException()).isNull();
        CycleInfo cycleInfo = Iterables.getOnlyElement(errorInfo.getCycleInfo());
        assertThat(cycleInfo.getCycle()).containsExactly(aKey, bKey).inOrder();
        assertThat(cycleInfo.getPathToCycle()).containsExactly(topKey, midKey).inOrder();
    }

    @Test
    public void selfEdgeWithHead() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey topKey = GraphTester.toSkyKey("top");
        SkyKey midKey = GraphTester.toSkyKey("mid");
        tester.getOrCreate(topKey).addDependency(midKey);
        tester.getOrCreate(midKey).addDependency(aKey);
        tester.getOrCreate(aKey).addDependency(aKey);
        ErrorInfo errorInfo = eval(false, ImmutableList.of(topKey)).getError();
        assertThat(errorInfo.getException()).isNull();
        CycleInfo cycleInfo = Iterables.getOnlyElement(errorInfo.getCycleInfo());
        assertThat(cycleInfo.getCycle()).containsExactly(aKey).inOrder();
        assertThat(cycleInfo.getPathToCycle()).containsExactly(topKey, midKey).inOrder();
    }

    @Test
    public void cycleWithKeepGoing() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        SkyKey topKey = GraphTester.toSkyKey("top");
        SkyKey midKey = GraphTester.toSkyKey("mid");
        SkyKey goodKey = GraphTester.toSkyKey("good");
        GraphTester.StringValue goodValue = new GraphTester.StringValue("good");
        tester.set(goodKey, goodValue);
        tester.getOrCreate(topKey).addDependency(midKey);
        tester.getOrCreate(midKey).addDependency(aKey);
        tester.getOrCreate(aKey).addDependency(bKey);
        tester.getOrCreate(bKey).addDependency(aKey);
        EvaluationResult<GraphTester.StringValue> result = eval(true, topKey, goodKey);
        assertThat(result.get(goodKey)).isEqualTo(goodValue);
        assertThat(result.get(topKey)).isNull();
        ErrorInfo errorInfo = result.getError(topKey);
        CycleInfo cycleInfo = Iterables.getOnlyElement(errorInfo.getCycleInfo());
        assertThat(cycleInfo.getCycle()).containsExactly(aKey, bKey).inOrder();
        assertThat(cycleInfo.getPathToCycle()).containsExactly(topKey, midKey).inOrder();
    }

    @Test
    public void twoCycles() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        SkyKey cKey = GraphTester.toSkyKey("c");
        SkyKey dKey = GraphTester.toSkyKey("d");
        SkyKey topKey = GraphTester.toSkyKey("top");
        tester.getOrCreate(topKey).addDependency(aKey).addDependency(cKey);
        tester.getOrCreate(aKey).addDependency(bKey);
        tester.getOrCreate(bKey).addDependency(aKey);
        tester.getOrCreate(cKey).addDependency(dKey);
        tester.getOrCreate(dKey).addDependency(cKey);
        EvaluationResult<GraphTester.StringValue> result = eval(false, ImmutableList.of(topKey));
        assertThat(result.get(topKey)).isNull();
        ErrorInfo errorInfo = result.getError(topKey);
        Iterable<CycleInfo> cycles = CycleInfo.prepareCycles(topKey, ImmutableList.of(new CycleInfo(ImmutableList.of(aKey, bKey)), new CycleInfo(ImmutableList.of(cKey, dKey))));
        assertThat(cycles).contains(Iterables.getOnlyElement(errorInfo.getCycleInfo()));
    }

    @Test
    public void twoCyclesKeepGoing() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        SkyKey cKey = GraphTester.toSkyKey("c");
        SkyKey dKey = GraphTester.toSkyKey("d");
        SkyKey topKey = GraphTester.toSkyKey("top");
        tester.getOrCreate(topKey).addDependency(aKey).addDependency(cKey);
        tester.getOrCreate(aKey).addDependency(bKey);
        tester.getOrCreate(bKey).addDependency(aKey);
        tester.getOrCreate(cKey).addDependency(dKey);
        tester.getOrCreate(dKey).addDependency(cKey);
        EvaluationResult<GraphTester.StringValue> result = eval(true, ImmutableList.of(topKey));
        assertThat(result.get(topKey)).isNull();
        ErrorInfo errorInfo = result.getError(topKey);
        CycleInfo aCycle = new CycleInfo(ImmutableList.of(topKey), ImmutableList.of(aKey, bKey));
        CycleInfo cCycle = new CycleInfo(ImmutableList.of(topKey), ImmutableList.of(cKey, dKey));
        assertThat(errorInfo.getCycleInfo()).containsExactly(aCycle, cCycle);
    }

    @Test
    public void triangleBelowHeadCycle() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        SkyKey cKey = GraphTester.toSkyKey("c");
        SkyKey topKey = GraphTester.toSkyKey("top");
        tester.getOrCreate(topKey).addDependency(aKey);
        tester.getOrCreate(aKey).addDependency(bKey).addDependency(cKey);
        tester.getOrCreate(bKey).addDependency(cKey);
        tester.getOrCreate(cKey).addDependency(topKey);
        EvaluationResult<GraphTester.StringValue> result = eval(true, ImmutableList.of(topKey));
        assertThat(result.get(topKey)).isNull();
        ErrorInfo errorInfo = result.getError(topKey);
        CycleInfo topCycle = new CycleInfo(ImmutableList.of(topKey, aKey, cKey));
        assertThat(errorInfo.getCycleInfo()).containsExactly(topCycle);
    }

    @Test
    public void longCycle() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        SkyKey cKey = GraphTester.toSkyKey("c");
        SkyKey topKey = GraphTester.toSkyKey("top");
        tester.getOrCreate(topKey).addDependency(aKey);
        tester.getOrCreate(aKey).addDependency(bKey);
        tester.getOrCreate(bKey).addDependency(cKey);
        tester.getOrCreate(cKey).addDependency(topKey);
        EvaluationResult<GraphTester.StringValue> result = eval(true, ImmutableList.of(topKey));
        assertThat(result.get(topKey)).isNull();
        ErrorInfo errorInfo = result.getError(topKey);
        CycleInfo topCycle = new CycleInfo(ImmutableList.of(topKey, aKey, bKey, cKey));
        assertThat(errorInfo.getCycleInfo()).containsExactly(topCycle);
    }

    @Test
    public void cycleWithTail() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        SkyKey cKey = GraphTester.toSkyKey("c");
        SkyKey topKey = GraphTester.toSkyKey("top");
        tester.getOrCreate(topKey).addDependency(aKey).addDependency(cKey);
        tester.getOrCreate(aKey).addDependency(bKey);
        tester.getOrCreate(bKey).addDependency(aKey).addDependency(cKey);
        tester.getOrCreate(cKey);
        tester.set(cKey, new GraphTester.StringValue("cValue"));
        EvaluationResult<GraphTester.StringValue> result = eval(false, ImmutableList.of(topKey));
        assertThat(result.get(topKey)).isNull();
        ErrorInfo errorInfo = result.getError(topKey);
        CycleInfo cycleInfo = Iterables.getOnlyElement(errorInfo.getCycleInfo());
        assertThat(cycleInfo.getCycle()).containsExactly(aKey, bKey).inOrder();
        assertThat(cycleInfo.getPathToCycle()).containsExactly(topKey).inOrder();
    }

    /**
     * Regression test: "value cannot be ready in a cycle".
     */
    @Test
    public void selfEdgeWithExtraChildrenUnderCycle() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey zKey = GraphTester.toSkyKey("z");
        SkyKey cKey = GraphTester.toSkyKey("c");
        tester.getOrCreate(aKey).addDependency(zKey);
        tester.getOrCreate(zKey).addDependency(cKey).addDependency(zKey);
        tester.getOrCreate(cKey).addDependency(aKey);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(aKey));
        assertThat(result.get(aKey)).isNull();
        ErrorInfo errorInfo = result.getError(aKey);
        CycleInfo cycleInfo = Iterables.getOnlyElement(errorInfo.getCycleInfo());
        assertThat(cycleInfo.getCycle()).containsExactly(zKey).inOrder();
        assertThat(cycleInfo.getPathToCycle()).containsExactly(aKey).inOrder();
    }

    /**
     * Regression test: "value cannot be ready in a cycle".
     */
    @Test
    public void cycleWithExtraChildrenUnderCycle() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        SkyKey cKey = GraphTester.toSkyKey("c");
        SkyKey dKey = GraphTester.toSkyKey("d");
        tester.getOrCreate(aKey).addDependency(bKey);
        tester.getOrCreate(bKey).addDependency(cKey).addDependency(dKey);
        tester.getOrCreate(cKey).addDependency(aKey);
        tester.getOrCreate(dKey).addDependency(bKey);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(aKey));
        assertThat(result.get(aKey)).isNull();
        ErrorInfo errorInfo = result.getError(aKey);
        CycleInfo cycleInfo = Iterables.getOnlyElement(errorInfo.getCycleInfo());
        assertThat(cycleInfo.getCycle()).containsExactly(bKey, dKey).inOrder();
        assertThat(cycleInfo.getPathToCycle()).containsExactly(aKey).inOrder();
    }

    /**
     * Regression test: "value cannot be ready in a cycle".
     */
    @Test
    public void cycleAboveIndependentCycle() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey bKey = GraphTester.toSkyKey("b");
        SkyKey cKey = GraphTester.toSkyKey("c");
        tester.getOrCreate(aKey).addDependency(bKey);
        tester.getOrCreate(bKey).addDependency(cKey);
        tester.getOrCreate(cKey).addDependency(aKey).addDependency(bKey);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(aKey));
        assertThat(result.get(aKey)).isNull();
        assertThat(result.getError(aKey).getCycleInfo()).containsExactly(new CycleInfo(ImmutableList.of(aKey, bKey, cKey)), new CycleInfo(ImmutableList.of(aKey), ImmutableList.of(bKey, cKey)));
    }

    @Test
    public void valueAboveCycleAndExceptionReportsException() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.toSkyKey("a");
        SkyKey errorKey = GraphTester.toSkyKey("error");
        SkyKey bKey = GraphTester.toSkyKey("b");
        tester.getOrCreate(aKey).addDependency(bKey).addDependency(errorKey);
        tester.getOrCreate(bKey).addDependency(bKey);
        tester.getOrCreate(errorKey).setHasError(true);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(aKey));
        assertThat(result.get(aKey)).isNull();
        assertThat(result.getError(aKey).getException()).isNotNull();
        CycleInfo cycleInfo = Iterables.getOnlyElement(result.getError(aKey).getCycleInfo());
        assertThat(cycleInfo.getCycle()).containsExactly(bKey).inOrder();
        assertThat(cycleInfo.getPathToCycle()).containsExactly(aKey).inOrder();
    }

    @Test
    public void errorValueStored() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey errorKey = GraphTester.toSkyKey("my_error_value");
        tester.getOrCreate(errorKey).setHasError(true);
        EvaluationResult<GraphTester.StringValue> result = eval(false, ImmutableList.of(errorKey));
        assertThat(result.keyNames()).isEmpty();
        assertThat(result.errorMap().keySet()).containsExactly(errorKey);
        ErrorInfo errorInfo = result.getError();
        assertThat(errorInfo.getRootCauses()).containsExactly(errorKey);
        // Update value. But builder won't rebuild it.
        tester.getOrCreate(errorKey).setHasError(false);
        tester.set(errorKey, new GraphTester.StringValue("no error?"));
        result = eval(false, ImmutableList.of(errorKey));
        assertThat(result.keyNames()).isEmpty();
        assertThat(result.errorMap().keySet()).containsExactly(errorKey);
        errorInfo = result.getError();
        assertThat(errorInfo.getRootCauses()).containsExactly(errorKey);
    }

    /**
     * Regression test: "OOM in Skyframe cycle detection".
     * We only store the first 20 cycles found below any given root value.
     */
    @Test
    public void manyCycles() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey topKey = GraphTester.toSkyKey("top");
        for (int i = 0; i < 100; i++) {
            SkyKey dep = GraphTester.toSkyKey(Integer.toString(i));
            tester.getOrCreate(topKey).addDependency(dep);
            tester.getOrCreate(dep).addDependency(dep);
        }
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(topKey));
        assertThat(result.get(topKey)).isNull();
        /* selfEdge= */
        ParallelEvaluatorTest.assertManyCycles(result.getError(topKey), topKey, false);
    }

    /**
     * Regression test: "OOM in Skyframe cycle detection".
     * We filter out multiple paths to a cycle that go through the same child value.
     */
    @Test
    public void manyPathsToCycle() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey topKey = GraphTester.toSkyKey("top");
        SkyKey midKey = GraphTester.toSkyKey("mid");
        SkyKey cycleKey = GraphTester.toSkyKey("cycle");
        tester.getOrCreate(topKey).addDependency(midKey);
        tester.getOrCreate(cycleKey).addDependency(cycleKey);
        for (int i = 0; i < 100; i++) {
            SkyKey dep = GraphTester.toSkyKey(Integer.toString(i));
            tester.getOrCreate(midKey).addDependency(dep);
            tester.getOrCreate(dep).addDependency(cycleKey);
        }
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(topKey));
        assertThat(result.get(topKey)).isNull();
        CycleInfo cycleInfo = Iterables.getOnlyElement(result.getError(topKey).getCycleInfo());
        assertThat(cycleInfo.getCycle()).hasSize(1);
        assertThat(cycleInfo.getPathToCycle()).hasSize(3);
        assertThat(cycleInfo.getPathToCycle().subList(0, 2)).containsExactly(topKey, midKey).inOrder();
    }

    @Test
    public void manyUnprocessedValuesInCycle() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        SkyKey lastSelfKey = GraphTester.toSkyKey("zlastSelf");
        SkyKey firstSelfKey = GraphTester.toSkyKey("afirstSelf");
        SkyKey midSelfKey = GraphTester.toSkyKey("midSelf9");
        // We add firstSelf first so that it is processed last in cycle detection (LIFO), meaning that
        // none of the dep values have to be cleared from firstSelf.
        tester.getOrCreate(firstSelfKey).addDependency(firstSelfKey);
        for (int i = 0; i < 100; i++) {
            SkyKey firstDep = GraphTester.toSkyKey(("first" + i));
            SkyKey midDep = GraphTester.toSkyKey((("midSelf" + i) + "dep"));
            SkyKey lastDep = GraphTester.toSkyKey(("last" + i));
            tester.getOrCreate(firstSelfKey).addDependency(firstDep);
            tester.getOrCreate(midSelfKey).addDependency(midDep);
            tester.getOrCreate(lastSelfKey).addDependency(lastDep);
            if (i == 90) {
                // Most of the deps will be cleared from midSelf.
                tester.getOrCreate(midSelfKey).addDependency(midSelfKey);
            }
            tester.getOrCreate(firstDep).addDependency(firstDep);
            tester.getOrCreate(midDep).addDependency(midDep);
            tester.getOrCreate(lastDep).addDependency(lastDep);
        }
        // All the deps will be cleared from lastSelf.
        tester.getOrCreate(lastSelfKey).addDependency(lastSelfKey);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(lastSelfKey, firstSelfKey, midSelfKey));
        assertWithMessage(result.toString()).that(result.keyNames()).isEmpty();
        assertThat(result.errorMap().keySet()).containsExactly(lastSelfKey, firstSelfKey, midSelfKey);
        // Check lastSelfKey.
        ErrorInfo errorInfo = result.getError(lastSelfKey);
        assertWithMessage(errorInfo.toString()).that(Iterables.size(errorInfo.getCycleInfo())).isEqualTo(1);
        CycleInfo cycleInfo = Iterables.getOnlyElement(errorInfo.getCycleInfo());
        assertThat(cycleInfo.getCycle()).containsExactly(lastSelfKey);
        assertThat(cycleInfo.getPathToCycle()).isEmpty();
        // Check firstSelfKey. It should not have discovered its own self-edge, because there were too
        // many other values before it in the queue.
        /* selfEdge= */
        ParallelEvaluatorTest.assertManyCycles(result.getError(firstSelfKey), firstSelfKey, false);
        // Check midSelfKey. It should have discovered its own self-edge.
        /* selfEdge= */
        ParallelEvaluatorTest.assertManyCycles(result.getError(midSelfKey), midSelfKey, true);
    }

    @Test
    public void errorValueStoredWithKeepGoing() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey errorKey = GraphTester.toSkyKey("my_error_value");
        tester.getOrCreate(errorKey).setHasError(true);
        EvaluationResult<GraphTester.StringValue> result = eval(true, ImmutableList.of(errorKey));
        assertThat(result.keyNames()).isEmpty();
        assertThat(result.errorMap().keySet()).containsExactly(errorKey);
        ErrorInfo errorInfo = result.getError();
        assertThat(errorInfo.getRootCauses()).containsExactly(errorKey);
        // Update value. But builder won't rebuild it.
        tester.getOrCreate(errorKey).setHasError(false);
        tester.set(errorKey, new GraphTester.StringValue("no error?"));
        result = eval(true, ImmutableList.of(errorKey));
        assertThat(result.keyNames()).isEmpty();
        assertThat(result.errorMap().keySet()).containsExactly(errorKey);
        errorInfo = result.getError();
        assertThat(errorInfo.getRootCauses()).containsExactly(errorKey);
    }

    @Test
    public void continueWithErrorDep() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey errorKey = GraphTester.toSkyKey("my_error_value");
        tester.getOrCreate(errorKey).setHasError(true);
        tester.set("after", new GraphTester.StringValue("after"));
        SkyKey parentKey = GraphTester.toSkyKey("parent");
        tester.getOrCreate(parentKey).addErrorDependency(errorKey, new GraphTester.StringValue("recovered")).setComputedValue(GraphTester.CONCATENATE).addDependency("after");
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(parentKey));
        assertThat(result.errorMap()).isEmpty();
        assertThat(result.get(parentKey).getValue()).isEqualTo("recoveredafter");
        result = /* keepGoing= */
        eval(false, ImmutableList.of(parentKey));
        assertThat(result.keyNames()).isEmpty();
        Map.Entry<SkyKey, ErrorInfo> error = Iterables.getOnlyElement(result.errorMap().entrySet());
        assertThat(error.getKey()).isEqualTo(parentKey);
        assertThat(error.getValue().getRootCauses()).containsExactly(errorKey);
    }

    @Test
    public void transformErrorDep() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey errorKey = GraphTester.toSkyKey("my_error_value");
        tester.getOrCreate(errorKey).setHasError(true);
        SkyKey parentErrorKey = GraphTester.toSkyKey("parent");
        tester.getOrCreate(parentErrorKey).addErrorDependency(errorKey, new GraphTester.StringValue("recovered")).setHasError(true);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, ImmutableList.of(parentErrorKey));
        assertThat(result.keyNames()).isEmpty();
        Map.Entry<SkyKey, ErrorInfo> error = Iterables.getOnlyElement(result.errorMap().entrySet());
        assertThat(error.getKey()).isEqualTo(parentErrorKey);
        assertThat(error.getValue().getRootCauses()).containsExactly(parentErrorKey);
    }

    @Test
    public void transformErrorDepKeepGoing() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey errorKey = GraphTester.toSkyKey("my_error_value");
        tester.getOrCreate(errorKey).setHasError(true);
        SkyKey parentErrorKey = GraphTester.toSkyKey("parent");
        tester.getOrCreate(parentErrorKey).addErrorDependency(errorKey, new GraphTester.StringValue("recovered")).setHasError(true);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(parentErrorKey));
        assertThat(result.keyNames()).isEmpty();
        Map.Entry<SkyKey, ErrorInfo> error = Iterables.getOnlyElement(result.errorMap().entrySet());
        assertThat(error.getKey()).isEqualTo(parentErrorKey);
        assertThat(error.getValue().getRootCauses()).containsExactly(parentErrorKey);
    }

    @Test
    public void transformErrorDepOneLevelDownKeepGoing() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey errorKey = GraphTester.toSkyKey("my_error_value");
        tester.getOrCreate(errorKey).setHasError(true);
        tester.set("after", new GraphTester.StringValue("after"));
        SkyKey parentErrorKey = GraphTester.toSkyKey("parent");
        tester.getOrCreate(parentErrorKey).addErrorDependency(errorKey, new GraphTester.StringValue("recovered"));
        tester.set(parentErrorKey, new GraphTester.StringValue("parent value"));
        SkyKey topKey = GraphTester.toSkyKey("top");
        tester.getOrCreate(topKey).addDependency(parentErrorKey).addDependency("after").setComputedValue(GraphTester.CONCATENATE);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(topKey));
        assertThat(ImmutableList.<String>copyOf(result.<String>keyNames())).containsExactly("top");
        assertThat(result.get(topKey).getValue()).isEqualTo("parent valueafter");
        assertThat(result.errorMap()).isEmpty();
    }

    @Test
    public void transformErrorDepOneLevelDownNoKeepGoing() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey errorKey = GraphTester.toSkyKey("my_error_value");
        tester.getOrCreate(errorKey).setHasError(true);
        tester.set("after", new GraphTester.StringValue("after"));
        SkyKey parentErrorKey = GraphTester.toSkyKey("parent");
        tester.getOrCreate(parentErrorKey).addErrorDependency(errorKey, new GraphTester.StringValue("recovered"));
        tester.set(parentErrorKey, new GraphTester.StringValue("parent value"));
        SkyKey topKey = GraphTester.toSkyKey("top");
        tester.getOrCreate(topKey).addDependency(parentErrorKey).addDependency("after").setComputedValue(GraphTester.CONCATENATE);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, ImmutableList.of(topKey));
        assertThat(result.keyNames()).isEmpty();
        Map.Entry<SkyKey, ErrorInfo> error = Iterables.getOnlyElement(result.errorMap().entrySet());
        assertThat(error.getKey()).isEqualTo(topKey);
        assertThat(error.getValue().getRootCauses()).containsExactly(errorKey);
    }

    @Test
    public void errorDepDoesntStopOtherDep() throws Exception {
        graph = new InMemoryGraphImpl();
        final SkyKey errorKey = GraphTester.toSkyKey("error");
        tester.getOrCreate(errorKey).setHasError(true);
        EvaluationResult<GraphTester.StringValue> result1 = /* keepGoing= */
        eval(true, ImmutableList.of(errorKey));
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result1).hasError();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result1).hasErrorEntryForKeyThat(errorKey).hasExceptionThat().isNotNull();
        final SkyKey otherKey = GraphTester.toSkyKey("other");
        tester.getOrCreate(otherKey).setConstantValue(new GraphTester.StringValue("other"));
        SkyKey topKey = GraphTester.toSkyKey("top");
        final Exception topException = new SomeErrorException("top exception");
        final AtomicInteger numComputes = new AtomicInteger(0);
        tester.getOrCreate(topKey).setBuilder(new SkyFunction() {
            @Nullable
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
                Map<SkyKey, ValueOrException<SomeErrorException>> values = env.getValuesOrThrow(ImmutableList.of(errorKey, otherKey), SomeErrorException.class);
                if ((numComputes.incrementAndGet()) == 1) {
                    assertThat(env.valuesMissing()).isTrue();
                } else {
                    assertThat(numComputes.get()).isEqualTo(2);
                    assertThat(env.valuesMissing()).isFalse();
                }
                try {
                    values.get(errorKey).get();
                    throw new AssertionError("Should have thrown");
                } catch (SomeErrorException e) {
                    throw new SkyFunctionException(topException, Transience.PERSISTENT) {};
                }
            }

            @Nullable
            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        EvaluationResult<GraphTester.StringValue> result2 = /* keepGoing= */
        eval(true, ImmutableList.of(topKey));
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result2).hasError();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result2).hasErrorEntryForKeyThat(topKey).hasExceptionThat().isSameAs(topException);
        assertThat(numComputes.get()).isEqualTo(2);
    }

    /**
     * Make sure that multiple unfinished children can be cleared from a cycle value.
     */
    @Test
    public void cycleWithMultipleUnfinishedChildren() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        SkyKey cycleKey = GraphTester.toSkyKey("zcycle");
        SkyKey midKey = GraphTester.toSkyKey("mid");
        SkyKey topKey = GraphTester.toSkyKey("top");
        SkyKey selfEdge1 = GraphTester.toSkyKey("selfEdge1");
        SkyKey selfEdge2 = GraphTester.toSkyKey("selfEdge2");
        tester.getOrCreate(topKey).addDependency(midKey).setComputedValue(GraphTester.CONCATENATE);
        // selfEdge* come before cycleKey, so cycleKey's path will be checked first (LIFO), and the
        // cycle with mid will be detected before the selfEdge* cycles are.
        tester.getOrCreate(midKey).addDependency(selfEdge1).addDependency(selfEdge2).addDependency(cycleKey).setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate(cycleKey).addDependency(midKey);
        tester.getOrCreate(selfEdge1).addDependency(selfEdge1);
        tester.getOrCreate(selfEdge2).addDependency(selfEdge2);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableSet.of(topKey));
        assertThat(result.errorMap().keySet()).containsExactly(topKey);
        Iterable<CycleInfo> cycleInfos = result.getError(topKey).getCycleInfo();
        CycleInfo cycleInfo = Iterables.getOnlyElement(cycleInfos);
        assertThat(cycleInfo.getPathToCycle()).containsExactly(topKey);
        assertThat(cycleInfo.getCycle()).containsExactly(midKey, cycleKey);
    }

    @Test
    public void cycleAndErrorInBubbleUpNoKeepGoing() throws Exception {
        cycleAndErrorInBubbleUp(false);
    }

    @Test
    public void cycleAndErrorInBubbleUpKeepGoing() throws Exception {
        cycleAndErrorInBubbleUp(true);
    }

    /**
     * Regression test: "value in cycle depends on error".
     * We add another value that won't finish building before the threadpool shuts down, to check that
     * the cycle detection can handle unfinished values.
     */
    @Test
    public void cycleAndErrorAndOtherInBubbleUp() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        tester = new GraphTester();
        SkyKey errorKey = GraphTester.toSkyKey("error");
        SkyKey cycleKey = GraphTester.toSkyKey("cycle");
        SkyKey midKey = GraphTester.toSkyKey("mid");
        SkyKey topKey = GraphTester.toSkyKey("top");
        tester.getOrCreate(topKey).addDependency(midKey).setComputedValue(GraphTester.CONCATENATE);
        // We should add cycleKey first and errorKey afterwards. Otherwise there is a chance that
        // during error propagation cycleKey will not be processed, and we will not detect the cycle.
        tester.getOrCreate(midKey).addDependency(errorKey).addDependency(cycleKey).setComputedValue(GraphTester.CONCATENATE);
        SkyKey otherTop = GraphTester.toSkyKey("otherTop");
        CountDownLatch topStartAndCycleFinish = new CountDownLatch(2);
        // In nokeep_going mode, otherTop will wait until the threadpool has received an exception,
        // then request its own dep. This guarantees that there is a value that is not finished when
        // cycle detection happens.
        tester.getOrCreate(otherTop).setBuilder(/* waitForException= */
        new ChainedFunction(topStartAndCycleFinish, new CountDownLatch(0), null, true, new GraphTester.StringValue("never returned"), ImmutableSet.<SkyKey>of(GraphTester.toSkyKey("dep that never builds"))));
        tester.getOrCreate(cycleKey).setBuilder(/* waitForException= */
        new ChainedFunction(null, null, topStartAndCycleFinish, false, new GraphTester.StringValue(""), ImmutableSet.<SkyKey>of(midKey)));
        // error waits until otherTop starts and cycle finishes, to make sure otherTop will request
        // its dep before the threadpool shuts down.
        tester.getOrCreate(errorKey).setBuilder(/* waitForException= */
        new ChainedFunction(null, topStartAndCycleFinish, null, false, null, ImmutableSet.<SkyKey>of()));
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, ImmutableSet.of(topKey, otherTop));
        assertThat(result.errorMap().keySet()).containsExactly(topKey);
        Iterable<CycleInfo> cycleInfos = result.getError(topKey).getCycleInfo();
        assertThat(cycleInfos).isNotEmpty();
        CycleInfo cycleInfo = Iterables.getOnlyElement(cycleInfos);
        assertThat(cycleInfo.getPathToCycle()).containsExactly(topKey);
        assertThat(cycleInfo.getCycle()).containsExactly(midKey, cycleKey);
    }

    @Test
    public void cycleAndErrorAndErrorNoKeepGoing() throws Exception {
        cycleAndErrorAndError(false);
    }

    @Test
    public void cycleAndErrorAndErrorKeepGoing() throws Exception {
        cycleAndErrorAndError(true);
    }

    @Test
    public void testFunctionCrashTrace() throws Exception {
        class ChildFunction implements SkyFunction {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) {
                throw new IllegalStateException("I WANT A PONY!!!");
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        }
        class ParentFunction implements SkyFunction {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws InterruptedException {
                SkyValue dep = env.getValue(ParallelEvaluatorTest.ChildKey.create("billy the kid"));
                if (dep == null) {
                    return null;
                }
                throw new IllegalStateException();// Should never get here.

            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        }
        ImmutableMap<SkyFunctionName, SkyFunction> skyFunctions = ImmutableMap.of(ParallelEvaluatorTest.CHILD_TYPE, new ChildFunction(), ParallelEvaluatorTest.PARENT_TYPE, new ParentFunction());
        ParallelEvaluator evaluator = makeEvaluator(new InMemoryGraphImpl(), skyFunctions, false);
        try {
            evaluator.eval(ImmutableList.of(ParallelEvaluatorTest.ParentKey.create("octodad")));
            Assert.fail();
        } catch (RuntimeException e) {
            assertThat(e).hasCauseThat().hasMessageThat().isEqualTo("I WANT A PONY!!!");
            assertThat(e).hasMessageThat().isEqualTo(("Unrecoverable error while evaluating node 'child:billy the kid' " + "(requested by nodes 'parent:octodad')"));
        }
    }

    private static class SomeOtherErrorException extends Exception {
        public SomeOtherErrorException(String msg) {
            super(msg);
        }
    }

    /**
     * This and the following three tests are in response a bug: "Skyframe error propagation model is
     * problematic". They ensure that exceptions a child throws that a value does not specify it can
     * handle in getValueOrThrow do not cause a crash.
     */
    @Test
    public void unexpectedErrorDepKeepGoing() throws Exception {
        unexpectedErrorDep(true);
    }

    @Test
    public void unexpectedErrorDepNoKeepGoing() throws Exception {
        unexpectedErrorDep(false);
    }

    @Test
    public void unexpectedErrorDepOneLevelDownKeepGoing() throws Exception {
        unexpectedErrorDepOneLevelDown(true);
    }

    @Test
    public void unexpectedErrorDepOneLevelDownNoKeepGoing() throws Exception {
        unexpectedErrorDepOneLevelDown(false);
    }

    @Test
    public void sameDepInTwoGroups_Same_Two_Throw() throws Exception {
        /* sameFirst= */
        /* twoCalls= */
        /* valuesOrThrow= */
        sameDepInTwoGroups(true, true, true);
    }

    @Test
    public void sameDepInTwoGroups_Same_Two_Deps() throws Exception {
        /* sameFirst= */
        /* twoCalls= */
        /* valuesOrThrow= */
        sameDepInTwoGroups(true, true, false);
    }

    @Test
    public void sameDepInTwoGroups_Same_One_Throw() throws Exception {
        /* sameFirst= */
        /* twoCalls= */
        /* valuesOrThrow= */
        sameDepInTwoGroups(true, false, true);
    }

    @Test
    public void sameDepInTwoGroups_Same_One_Deps() throws Exception {
        /* sameFirst= */
        /* twoCalls= */
        /* valuesOrThrow= */
        sameDepInTwoGroups(true, false, false);
    }

    @Test
    public void sameDepInTwoGroups_Different_Two_Throw() throws Exception {
        /* sameFirst= */
        /* twoCalls= */
        /* valuesOrThrow= */
        sameDepInTwoGroups(false, true, true);
    }

    @Test
    public void sameDepInTwoGroups_Different_Two_Deps() throws Exception {
        /* sameFirst= */
        /* twoCalls= */
        /* valuesOrThrow= */
        sameDepInTwoGroups(false, true, false);
    }

    @Test
    public void sameDepInTwoGroups_Different_One_Throw() throws Exception {
        /* sameFirst= */
        /* twoCalls= */
        /* valuesOrThrow= */
        sameDepInTwoGroups(false, false, true);
    }

    @Test
    public void sameDepInTwoGroups_Different_One_Deps() throws Exception {
        /* sameFirst= */
        /* twoCalls= */
        /* valuesOrThrow= */
        sameDepInTwoGroups(false, false, false);
    }

    @Test
    public void getValuesOrThrowWithErrors_NoKeepGoing() throws Exception {
        /* keepGoing= */
        getValuesOrThrowWithErrors(false);
    }

    @Test
    public void getValuesOrThrowWithErrors_KeepGoing() throws Exception {
        /* keepGoing= */
        getValuesOrThrowWithErrors(true);
    }

    @Test
    public void duplicateCycles() throws Exception {
        graph = new InMemoryGraphImpl();
        SkyKey grandparentKey = GraphTester.toSkyKey("grandparent");
        SkyKey parentKey1 = GraphTester.toSkyKey("parent1");
        SkyKey parentKey2 = GraphTester.toSkyKey("parent2");
        SkyKey loopKey1 = GraphTester.toSkyKey("loop1");
        SkyKey loopKey2 = GraphTester.toSkyKey("loop2");
        tester.getOrCreate(loopKey1).addDependency(loopKey2);
        tester.getOrCreate(loopKey2).addDependency(loopKey1);
        tester.getOrCreate(parentKey1).addDependency(loopKey1);
        tester.getOrCreate(parentKey2).addDependency(loopKey2);
        tester.getOrCreate(grandparentKey).addDependency(parentKey1);
        tester.getOrCreate(grandparentKey).addDependency(parentKey2);
        ErrorInfo errorInfo = evalValueInError(grandparentKey);
        List<ImmutableList<SkyKey>> cycles = Lists.newArrayList();
        for (CycleInfo cycleInfo : errorInfo.getCycleInfo()) {
            cycles.add(cycleInfo.getCycle());
        }
        // Skyframe doesn't automatically dedupe cycles that are the same except for entry point.
        assertThat(cycles).hasSize(2);
        int numUniqueCycles = 0;
        CycleDeduper<SkyKey> cycleDeduper = new CycleDeduper<SkyKey>();
        for (ImmutableList<SkyKey> cycle : cycles) {
            if (cycleDeduper.seen(cycle)) {
                numUniqueCycles++;
            }
        }
        assertThat(numUniqueCycles).isEqualTo(1);
    }

    @Test
    public void signalValueEnqueuedAndEvaluated() throws Exception {
        final Set<SkyKey> enqueuedValues = Sets.newConcurrentHashSet();
        final Set<SkyKey> evaluatedValues = Sets.newConcurrentHashSet();
        EvaluationProgressReceiver progressReceiver = new EvaluationProgressReceiver.NullEvaluationProgressReceiver() {
            @Override
            public void enqueueing(SkyKey skyKey) {
                enqueuedValues.add(skyKey);
            }

            @Override
            public void evaluated(SkyKey skyKey, @Nullable
            SkyValue value, Supplier<EvaluationSuccessState> evaluationSuccessState, EvaluationState state) {
                evaluatedValues.add(skyKey);
            }
        };
        ExtendedEventHandler reporter = new Reporter(new EventBus(), new EventHandler() {
            @Override
            public void handle(Event e) {
                throw new IllegalStateException();
            }
        });
        MemoizingEvaluator aug = new InMemoryMemoizingEvaluator(ImmutableMap.of(GraphTester.NODE_TYPE, tester.getFunction()), new SequencedRecordingDifferencer(), progressReceiver);
        SequentialBuildDriver driver = new SequentialBuildDriver(aug);
        tester.getOrCreate("top1").setComputedValue(GraphTester.CONCATENATE).addDependency("d1").addDependency("d2");
        tester.getOrCreate("top2").setComputedValue(GraphTester.CONCATENATE).addDependency("d3");
        tester.getOrCreate("top3");
        assertThat(enqueuedValues).isEmpty();
        assertThat(evaluatedValues).isEmpty();
        tester.set("d1", new GraphTester.StringValue("1"));
        tester.set("d2", new GraphTester.StringValue("2"));
        tester.set("d3", new GraphTester.StringValue("3"));
        EvaluationContext evaluationContext = EvaluationContext.newBuilder().setKeepGoing(false).setNumThreads(200).setEventHander(reporter).build();
        driver.evaluate(ImmutableList.of(GraphTester.toSkyKey("top1")), evaluationContext);
        assertThat(enqueuedValues).containsExactlyElementsIn(GraphTester.toSkyKeys("top1", "d1", "d2"));
        assertThat(evaluatedValues).containsExactlyElementsIn(GraphTester.toSkyKeys("top1", "d1", "d2"));
        enqueuedValues.clear();
        evaluatedValues.clear();
        driver.evaluate(ImmutableList.of(GraphTester.toSkyKey("top2")), evaluationContext);
        assertThat(enqueuedValues).containsExactlyElementsIn(GraphTester.toSkyKeys("top2", "d3"));
        assertThat(evaluatedValues).containsExactlyElementsIn(GraphTester.toSkyKeys("top2", "d3"));
        enqueuedValues.clear();
        evaluatedValues.clear();
        driver.evaluate(ImmutableList.of(GraphTester.toSkyKey("top1")), evaluationContext);
        assertThat(enqueuedValues).isEmpty();
        assertThat(evaluatedValues).containsExactlyElementsIn(GraphTester.toSkyKeys("top1"));
    }

    @Test
    public void depOnErrorHaltsNoKeepGoingBuildEagerly_ChildErrorCachedAndHandled() throws Exception {
        /* childErrorCached= */
        /* handleChildError= */
        runDepOnErrorHaltsNoKeepGoingBuildEagerly(true, true);
    }

    @Test
    public void depOnErrorHaltsNoKeepGoingBuildEagerly_ChildErrorCachedAndNotHandled() throws Exception {
        /* childErrorCached= */
        /* handleChildError= */
        runDepOnErrorHaltsNoKeepGoingBuildEagerly(true, false);
    }

    @Test
    public void depOnErrorHaltsNoKeepGoingBuildEagerly_ChildErrorFreshAndHandled() throws Exception {
        /* childErrorCached= */
        /* handleChildError= */
        runDepOnErrorHaltsNoKeepGoingBuildEagerly(false, true);
    }

    @Test
    public void depOnErrorHaltsNoKeepGoingBuildEagerly_ChildErrorFreshAndNotHandled() throws Exception {
        /* childErrorCached= */
        /* handleChildError= */
        runDepOnErrorHaltsNoKeepGoingBuildEagerly(false, false);
    }

    @Test
    public void raceConditionWithNoKeepGoingErrors_FutureError() throws Exception {
        final CountDownLatch errorCommitted = new CountDownLatch(1);
        final CountDownLatch otherStarted = new CountDownLatch(1);
        final CountDownLatch otherParentSignaled = new CountDownLatch(1);
        final SkyKey errorParentKey = GraphTester.toSkyKey("errorParentKey");
        final SkyKey errorKey = GraphTester.toSkyKey("errorKey");
        final SkyKey otherParentKey = GraphTester.toSkyKey("otherParentKey");
        final SkyKey otherKey = GraphTester.toSkyKey("otherKey");
        final AtomicInteger numOtherParentInvocations = new AtomicInteger(0);
        final AtomicInteger numErrorParentInvocations = new AtomicInteger(0);
        tester.getOrCreate(otherParentKey).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
                int invocations = numOtherParentInvocations.incrementAndGet();
                assertWithMessage("otherParentKey should not be restarted").that(invocations).isEqualTo(1);
                return env.getValue(otherKey);
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        tester.getOrCreate(otherKey).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException {
                otherStarted.countDown();
                TrackingAwaiter.INSTANCE.awaitLatchAndTrackExceptions(errorCommitted, "error didn't get committed to the graph in time");
                return new GraphTester.StringValue("other");
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        tester.getOrCreate(errorKey).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException {
                TrackingAwaiter.INSTANCE.awaitLatchAndTrackExceptions(otherStarted, "other didn't start in time");
                throw new GenericFunctionException(new SomeErrorException("error"), Transience.PERSISTENT);
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        tester.getOrCreate(errorParentKey).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
                int invocations = numErrorParentInvocations.incrementAndGet();
                try {
                    SkyValue value = env.getValueOrThrow(errorKey, SomeErrorException.class);
                    assertWithMessage(("bogus non-null value " + value)).that((value == null)).isTrue();
                    if (invocations == 1) {
                        return null;
                    } else {
                        assertThat(env.inErrorBubblingForTesting()).isFalse();
                        Assert.fail("RACE CONDITION: errorParentKey was restarted!");
                        return null;
                    }
                } catch (SomeErrorException e) {
                    assertWithMessage("child error propagated during normal evaluation").that(env.inErrorBubblingForTesting()).isTrue();
                    assertThat(invocations).isEqualTo(2);
                    return null;
                }
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        graph = new NotifyingHelper.NotifyingProcessableGraph(new InMemoryGraphImpl(), new NotifyingHelper.Listener() {
            @Override
            public void accept(SkyKey key, NotifyingHelper.EventType type, NotifyingHelper.Order order, Object context) {
                if (((key.equals(errorKey)) && (type == (SET_VALUE))) && (order == (AFTER))) {
                    errorCommitted.countDown();
                    TrackingAwaiter.INSTANCE.awaitLatchAndTrackExceptions(otherParentSignaled, "otherParent didn't get signaled in time");
                    // We try to give some time for ParallelEvaluator to incorrectly re-evaluate
                    // 'otherParentKey'. This test case is testing for a real race condition and the
                    // 10ms time was chosen experimentally to give a true positive rate of 99.8%
                    // (without a sleep it has a 1% true positive rate). There's no good way to do
                    // this without sleeping. We *could* introspect ParallelEvaulator's
                    // AbstractQueueVisitor to see if the re-evaluation has been enqueued, but that's
                    // relying on pretty low-level implementation details.
                    Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
                }
                if (((key.equals(otherParentKey)) && (type == (SIGNAL))) && (order == (AFTER))) {
                    otherParentSignaled.countDown();
                }
            }
        });
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, ImmutableList.of(otherParentKey, errorParentKey));
        assertThat(result.hasError()).isTrue();
        assertThat(result.getError().getRootCauseOfException()).isEqualTo(errorKey);
    }

    @Test
    public void cachedErrorsFromKeepGoingUsedOnNoKeepGoing() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        tester = new GraphTester();
        SkyKey errorKey = GraphTester.toSkyKey("error");
        SkyKey parent1Key = GraphTester.toSkyKey("parent1");
        SkyKey parent2Key = GraphTester.toSkyKey("parent2");
        tester.getOrCreate(parent1Key).addDependency(errorKey).setConstantValue(new GraphTester.StringValue("parent1"));
        tester.getOrCreate(parent2Key).addDependency(errorKey).setConstantValue(new GraphTester.StringValue("parent2"));
        tester.getOrCreate(errorKey).setHasError(true);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(parent1Key));
        assertThat(result.hasError()).isTrue();
        assertThat(result.getError().getRootCauseOfException()).isEqualTo(errorKey);
        result = /* keepGoing= */
        eval(false, ImmutableList.of(parent2Key));
        assertThat(result.hasError()).isTrue();
        assertThat(result.getError(parent2Key).getRootCauseOfException()).isEqualTo(errorKey);
    }

    @Test
    public void cachedTopLevelErrorsShouldHaltNoKeepGoingBuildEarly() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        tester = new GraphTester();
        SkyKey errorKey = GraphTester.toSkyKey("error");
        tester.getOrCreate(errorKey).setHasError(true);
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(true, ImmutableList.of(errorKey));
        assertThat(result.hasError()).isTrue();
        assertThat(result.getError().getRootCauseOfException()).isEqualTo(errorKey);
        SkyKey rogueKey = GraphTester.toSkyKey("rogue");
        tester.getOrCreate(rogueKey).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) {
                // This SkyFunction could do an arbitrarily bad computation, e.g. loop-forever. So we want
                // to make sure that it is never run when we want to fail-fast anyway.
                Assert.fail("eval call should have already terminated");
                return null;
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        result = /* keepGoing= */
        eval(false, ImmutableList.of(errorKey, rogueKey));
        assertThat(result.hasError()).isTrue();
        assertThat(result.getError(errorKey).getRootCauseOfException()).isEqualTo(errorKey);
        assertThat(result.errorMap()).doesNotContainKey(rogueKey);
    }

    // Explicit test that we tolerate a SkyFunction that declares different [sequences of] deps each
    // restart. Such behavior from a SkyFunction isn't desired, but Bazel-on-Skyframe does indeed do
    // this.
    @Test
    public void declaresDifferentDepsAfterRestart() throws Exception {
        graph = new DeterministicHelper.DeterministicProcessableGraph(new InMemoryGraphImpl());
        tester = new GraphTester();
        SkyKey grandChild1Key = GraphTester.toSkyKey("grandChild1");
        tester.getOrCreate(grandChild1Key).setConstantValue(new GraphTester.StringValue("grandChild1"));
        SkyKey child1Key = GraphTester.toSkyKey("child1");
        tester.getOrCreate(child1Key).addDependency(grandChild1Key).setConstantValue(new GraphTester.StringValue("child1"));
        SkyKey grandChild2Key = GraphTester.toSkyKey("grandChild2");
        tester.getOrCreate(grandChild2Key).setConstantValue(new GraphTester.StringValue("grandChild2"));
        SkyKey child2Key = GraphTester.toSkyKey("child2");
        tester.getOrCreate(child2Key).setConstantValue(new GraphTester.StringValue("child2"));
        SkyKey parentKey = GraphTester.toSkyKey("parent");
        AtomicInteger numComputes = new AtomicInteger(0);
        tester.getOrCreate(parentKey).setBuilder(new SkyFunction() {
            @Override
            public SkyValue compute(SkyKey skyKey, Environment env) throws InterruptedException {
                switch (numComputes.incrementAndGet()) {
                    case 1 :
                        env.getValue(child1Key);
                        Preconditions.checkState(env.valuesMissing());
                        return null;
                    case 2 :
                        env.getValue(child2Key);
                        Preconditions.checkState(env.valuesMissing());
                        return null;
                    case 3 :
                        return new GraphTester.StringValue("the third time's the charm!");
                    default :
                        throw new IllegalStateException();
                }
            }

            @Override
            public String extractTag(SkyKey skyKey) {
                return null;
            }
        });
        EvaluationResult<GraphTester.StringValue> result = /* keepGoing= */
        eval(false, ImmutableList.of(parentKey));
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasEntryThat(parentKey).isEqualTo(new GraphTester.StringValue("the third time's the charm!"));
    }

    @Test
    public void unhandledTransitiveErrorsDuringErrorBubbling_ImplicitPropagation() throws Exception {
        /* keepGoing= */
        /* explicitlyPropagateError= */
        runUnhandledTransitiveErrors(false, false);
    }

    @Test
    public void unhandledTransitiveErrorsDuringErrorBubbling_ExplicitPropagation() throws Exception {
        /* keepGoing= */
        /* explicitlyPropagateError= */
        runUnhandledTransitiveErrors(false, true);
    }

    @Test
    public void unhandledTransitiveErrorsDuringNormalEvaluation_ImplicitPropagation() throws Exception {
        /* keepGoing= */
        /* explicitlyPropagateError= */
        runUnhandledTransitiveErrors(true, false);
    }

    @Test
    public void unhandledTransitiveErrorsDuringNormalEvaluation_ExplicitPropagation() throws Exception {
        /* keepGoing= */
        /* explicitlyPropagateError= */
        runUnhandledTransitiveErrors(true, true);
    }

    private static class ChildKey extends AbstractSkyKey<String> {
        private static final Interner<ParallelEvaluatorTest.ChildKey> interner = BlazeInterners.newWeakInterner();

        private ChildKey(String arg) {
            super(arg);
        }

        static ParallelEvaluatorTest.ChildKey create(String arg) {
            return ParallelEvaluatorTest.ChildKey.interner.intern(new ParallelEvaluatorTest.ChildKey(arg));
        }

        @Override
        public SkyFunctionName functionName() {
            return ParallelEvaluatorTest.CHILD_TYPE;
        }
    }

    private static class ParentKey extends AbstractSkyKey<String> {
        private static final Interner<ParallelEvaluatorTest.ParentKey> interner = BlazeInterners.newWeakInterner();

        private ParentKey(String arg) {
            super(arg);
        }

        private static ParallelEvaluatorTest.ParentKey create(String arg) {
            return ParallelEvaluatorTest.ParentKey.interner.intern(new ParallelEvaluatorTest.ParentKey(arg));
        }

        @Override
        public SkyFunctionName functionName() {
            return ParallelEvaluatorTest.PARENT_TYPE;
        }
    }
}

