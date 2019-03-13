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


import Reason.OTHER;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.testing.GcFinalization;
import com.google.devtools.build.lib.testutil.TestUtils;
import com.google.devtools.build.lib.util.Pair;
import com.google.devtools.build.skyframe.InvalidatingNodeVisitor.DeletingNodeVisitor;
import com.google.devtools.build.skyframe.InvalidatingNodeVisitor.DirtyingInvalidationState;
import com.google.devtools.build.skyframe.InvalidatingNodeVisitor.DirtyingNodeVisitor;
import com.google.devtools.build.skyframe.InvalidatingNodeVisitor.InvalidationState;
import com.google.devtools.build.skyframe.InvalidatingNodeVisitor.InvalidationType;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link InvalidatingNodeVisitor}.
 */
@RunWith(Enclosed.class)
public class EagerInvalidatorTest {
    protected InMemoryGraphImpl graph;

    protected GraphTester tester = new GraphTester();

    protected InvalidationState state = newInvalidationState();

    protected AtomicReference<InvalidatingNodeVisitor<?>> visitor = new AtomicReference<>();

    protected DirtyTrackingProgressReceiver progressReceiver;

    private IntVersion graphVersion = IntVersion.of(0);

    @Test
    public void receiverWorks() throws Exception {
        final Set<SkyKey> invalidated = Sets.newConcurrentHashSet();
        DirtyTrackingProgressReceiver receiver = new DirtyTrackingProgressReceiver(new EvaluationProgressReceiver.NullEvaluationProgressReceiver() {
            @Override
            public void invalidated(SkyKey skyKey, InvalidationState state) {
                Preconditions.checkState((state == (expectedState())));
                invalidated.add(skyKey);
            }
        });
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.nonHermeticKey("a");
        SkyKey bKey = GraphTester.nonHermeticKey("b");
        tester.set(aKey, new GraphTester.StringValue("a"));
        tester.set(bKey, new GraphTester.StringValue("b"));
        tester.getOrCreate("ab").addDependency(aKey).addDependency(bKey).setComputedValue(GraphTester.CONCATENATE);
        assertValueValue("ab", "ab");
        tester.set(aKey, new GraphTester.StringValue("c"));
        invalidateWithoutError(receiver, aKey);
        assertThat(invalidated).containsExactly(aKey, skyKey("ab"));
        assertValueValue("ab", "cb");
        tester.set(bKey, new GraphTester.StringValue("d"));
        invalidateWithoutError(receiver, bKey);
        assertThat(invalidated).containsExactly(aKey, skyKey("ab"), bKey);
    }

    @Test
    public void receiverIsNotifiedAboutNodesInError() throws Exception {
        final Set<SkyKey> invalidated = Sets.newConcurrentHashSet();
        DirtyTrackingProgressReceiver receiver = new DirtyTrackingProgressReceiver(new EvaluationProgressReceiver.NullEvaluationProgressReceiver() {
            @Override
            public void invalidated(SkyKey skyKey, InvalidationState state) {
                Preconditions.checkState((state == (expectedState())));
                invalidated.add(skyKey);
            }
        });
        // Given a graph consisting of two nodes, "a" and "ab" such that "ab" depends on "a",
        // And given "ab" is in error,
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.nonHermeticKey("a");
        tester.set(aKey, new GraphTester.StringValue("a"));
        tester.getOrCreate("ab").addDependency(aKey).setHasError(true);
        eval(false, skyKey("ab"));
        // When "a" is invalidated,
        invalidateWithoutError(receiver, aKey);
        // Then the invalidation receiver is notified of both "a" and "ab"'s invalidations.
        assertThat(invalidated).containsExactly(aKey, skyKey("ab"));
        // Note that this behavior isn't strictly required for correctness. This test is
        // meant to document current behavior and protect against programming error.
    }

    @Test
    public void invalidateValuesNotInGraph() throws Exception {
        final Set<SkyKey> invalidated = Sets.newConcurrentHashSet();
        DirtyTrackingProgressReceiver receiver = new DirtyTrackingProgressReceiver(new EvaluationProgressReceiver.NullEvaluationProgressReceiver() {
            @Override
            public void invalidated(SkyKey skyKey, InvalidationState state) {
                Preconditions.checkState((state == (InvalidationState.DIRTY)));
                invalidated.add(skyKey);
            }
        });
        graph = new InMemoryGraphImpl();
        SkyKey aKey = GraphTester.nonHermeticKey("a");
        invalidateWithoutError(receiver, aKey);
        assertThat(invalidated).isEmpty();
        tester.set(aKey, new GraphTester.StringValue("a"));
        GraphTester.StringValue value = ((GraphTester.StringValue) (eval(false, aKey)));
        assertThat(value.getValue()).isEqualTo("a");
        invalidateWithoutError(receiver, GraphTester.nonHermeticKey("b"));
        assertThat(invalidated).isEmpty();
    }

    @Test
    public void invalidatedValuesAreGCedAsExpected() throws Exception {
        SkyKey key = GraphTester.nonHermeticKey("a");
        EagerInvalidatorTest.HeavyValue heavyValue = new EagerInvalidatorTest.HeavyValue();
        WeakReference<EagerInvalidatorTest.HeavyValue> weakRef = new WeakReference<>(heavyValue);
        tester.set(key, heavyValue);
        graph = new InMemoryGraphImpl();
        eval(false, key);
        invalidate(graph, new DirtyTrackingProgressReceiver(null), key);
        tester = null;
        heavyValue = null;
        if (gcExpected()) {
            GcFinalization.awaitClear(weakRef);
        } else {
            // Not a reliable check, but better than nothing.
            System.gc();
            Thread.sleep(300);
            assertThat(weakRef.get()).isNotNull();
        }
    }

    @Test
    public void reverseDepsConsistent() throws Exception {
        graph = new InMemoryGraphImpl();
        set("a", "a");
        set("b", "b");
        set("c", "c");
        SkyKey abKey = GraphTester.nonHermeticKey("ab");
        tester.getOrCreate(abKey).addDependency("a").addDependency("b").setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate("bc").addDependency("b").addDependency("c").setComputedValue(GraphTester.CONCATENATE);
        tester.getOrCreate("ab_c").addDependency(abKey).addDependency("c").setComputedValue(GraphTester.CONCATENATE);
        eval(false, skyKey("ab_c"), skyKey("bc"));
        assertThat(graph.get(null, OTHER, skyKey("a")).getReverseDepsForDoneEntry()).containsExactly(abKey);
        assertThat(graph.get(null, OTHER, skyKey("b")).getReverseDepsForDoneEntry()).containsExactly(abKey, skyKey("bc"));
        assertThat(graph.get(null, OTHER, skyKey("c")).getReverseDepsForDoneEntry()).containsExactly(skyKey("ab_c"), skyKey("bc"));
        invalidateWithoutError(new DirtyTrackingProgressReceiver(null), abKey);
        eval(false);
        // The graph values should be gone.
        assertThat(isInvalidated(abKey)).isTrue();
        assertThat(isInvalidated(skyKey("abc"))).isTrue();
        // The reverse deps to ab and ab_c should have been removed if reverse deps are cleared.
        Set<SkyKey> reverseDeps = new HashSet<>();
        if (reverseDepsPresent()) {
            reverseDeps.add(abKey);
        }
        assertThat(graph.get(null, OTHER, skyKey("a")).getReverseDepsForDoneEntry()).containsExactlyElementsIn(reverseDeps);
        reverseDeps.add(skyKey("bc"));
        assertThat(graph.get(null, OTHER, skyKey("b")).getReverseDepsForDoneEntry()).containsExactlyElementsIn(reverseDeps);
        reverseDeps.clear();
        if (reverseDepsPresent()) {
            reverseDeps.add(skyKey("ab_c"));
        }
        reverseDeps.add(skyKey("bc"));
        assertThat(graph.get(null, OTHER, skyKey("c")).getReverseDepsForDoneEntry()).containsExactlyElementsIn(reverseDeps);
    }

    @Test
    public void interruptChild() throws Exception {
        graph = new InMemoryGraphImpl();
        int numValues = 50;// More values than the invalidator has threads.

        SkyKey[] family = new SkyKey[numValues];
        SkyKey child = GraphTester.nonHermeticKey("child");
        GraphTester.StringValue childValue = new GraphTester.StringValue("child");
        tester.set(child, childValue);
        family[0] = child;
        for (int i = 1; i < numValues; i++) {
            SkyKey member = skyKey(Integer.toString(i));
            tester.getOrCreate(member).addDependency(family[(i - 1)]).setComputedValue(GraphTester.CONCATENATE);
            family[i] = member;
        }
        SkyKey parent = GraphTester.skyKey("parent");
        tester.getOrCreate(parent).addDependency(family[(numValues - 1)]).setComputedValue(GraphTester.CONCATENATE);
        /* keepGoing= */
        eval(false, parent);
        final Thread mainThread = Thread.currentThread();
        final AtomicReference<SkyKey> badKey = new AtomicReference<>();
        DirtyTrackingProgressReceiver receiver = new DirtyTrackingProgressReceiver(new EvaluationProgressReceiver.NullEvaluationProgressReceiver() {
            @Override
            public void invalidated(SkyKey skyKey, InvalidationState state) {
                if (skyKey.equals(child)) {
                    // Interrupt on the very first invalidate
                    mainThread.interrupt();
                } else
                    if (!(skyKey.functionName().equals(GraphTester.NODE_TYPE))) {
                        // All other invalidations should have the GraphTester's key type.
                        // Exceptions thrown here may be silently dropped, so keep track of errors
                        // ourselves.
                        badKey.set(skyKey);
                    }

                try {
                    assertThat(visitor.get().getInterruptionLatchForTestingOnly().await(2, TimeUnit.HOURS)).isTrue();
                } catch (InterruptedException e) {
                    // We may well have thrown here because by the time we try to await, the main
                    // thread is already interrupted.
                }
            }
        });
        try {
            invalidateWithoutError(receiver, child);
            Assert.fail();
        } catch (InterruptedException e) {
            // Expected.
        }
        assertThat(badKey.get()).isNull();
        assertThat(state.isEmpty()).isFalse();
        final Set<SkyKey> invalidated = Sets.newConcurrentHashSet();
        assertThat(isInvalidated(parent)).isFalse();
        assertThat(graph.get(null, OTHER, parent).getValue()).isNotNull();
        receiver = new DirtyTrackingProgressReceiver(new EvaluationProgressReceiver.NullEvaluationProgressReceiver() {
            @Override
            public void invalidated(SkyKey skyKey, InvalidationState state) {
                invalidated.add(skyKey);
            }
        });
        invalidateWithoutError(receiver);
        assertThat(invalidated).contains(parent);
        assertThat(state.getInvalidationsForTesting()).isEmpty();
        // Regression test coverage:
        // "all pending values are marked changed on interrupt".
        assertThat(isInvalidated(child)).isTrue();
        assertChanged(child);
        for (int i = 1; i < numValues; i++) {
            assertDirtyAndNotChanged(family[i]);
        }
        assertDirtyAndNotChanged(parent);
    }

    @Test
    public void interruptThreadInReceiver() throws Exception {
        Random random = new Random(TestUtils.getRandomSeed());
        int graphSize = 1000;
        int tries = 5;
        graph = new InMemoryGraphImpl();
        SkyKey[] values = constructLargeGraph(graphSize);
        /* keepGoing= */
        eval(false, values);
        final Thread mainThread = Thread.currentThread();
        for (int run = 0; run < tries; run++) {
            Set<Pair<SkyKey, InvalidationType>> valuesToInvalidate = getValuesToInvalidate(values);
            // Find how many invalidations will actually be enqueued for invalidation in the first round,
            // so that we can interrupt before all of them are done.
            int validValuesToDo = Sets.difference(valuesToInvalidate, state.getInvalidationsForTesting()).size();
            for (Pair<SkyKey, InvalidationType> pair : state.getInvalidationsForTesting()) {
                if (!(isInvalidated(pair.first))) {
                    validValuesToDo++;
                }
            }
            int countDownStart = (validValuesToDo > 0) ? random.nextInt(validValuesToDo) : 0;
            final CountDownLatch countDownToInterrupt = new CountDownLatch(countDownStart);
            final DirtyTrackingProgressReceiver receiver = new DirtyTrackingProgressReceiver(new EvaluationProgressReceiver.NullEvaluationProgressReceiver() {
                @Override
                public void invalidated(SkyKey skyKey, InvalidationState state) {
                    countDownToInterrupt.countDown();
                    if ((countDownToInterrupt.getCount()) == 0) {
                        mainThread.interrupt();
                        // Wait for the main thread to be interrupted uninterruptibly, because the main
                        // thread is going to interrupt us, and we don't want to get into an interrupt
                        // fight. Only if we get interrupted without the main thread also being interrupted
                        // will this throw an InterruptedException.
                        TrackingAwaiter.INSTANCE.awaitLatchAndTrackExceptions(visitor.get().getInterruptionLatchForTestingOnly(), "Main thread was not interrupted");
                    }
                }
            });
            try {
                invalidate(graph, receiver, Sets.newHashSet(Iterables.transform(valuesToInvalidate, ( pair) -> pair.first)).toArray(new SkyKey[0]));
                assertThat(state.getInvalidationsForTesting()).isEmpty();
            } catch (InterruptedException e) {
                // Expected.
            }
            if (state.isEmpty()) {
                // Ran out of values to invalidate.
                break;
            }
        }
        /* keepGoing= */
        eval(false, values);
    }

    private static class HeavyValue implements SkyValue {}

    /**
     * Test suite for the deleting invalidator.
     */
    @RunWith(JUnit4.class)
    public static class DeletingInvalidatorTest extends EagerInvalidatorTest {
        @Override
        protected void invalidate(InMemoryGraph graph, DirtyTrackingProgressReceiver progressReceiver, SkyKey... keys) throws InterruptedException {
            Iterable<SkyKey> diff = ImmutableList.copyOf(keys);
            DeletingNodeVisitor deletingNodeVisitor = EagerInvalidator.createDeletingVisitorIfNeeded(graph, diff, new DirtyTrackingProgressReceiver(progressReceiver), state, true);
            if (deletingNodeVisitor != null) {
                visitor.set(deletingNodeVisitor);
                deletingNodeVisitor.run();
            }
        }

        @Override
        InvalidationState expectedState() {
            return InvalidationState.DELETED;
        }

        @Override
        boolean gcExpected() {
            return true;
        }

        @Override
        protected InvalidationState newInvalidationState() {
            return new InvalidatingNodeVisitor.DeletingInvalidationState();
        }

        @Override
        protected InvalidationType defaultInvalidationType() {
            return InvalidationType.DELETED;
        }

        @Override
        protected boolean reverseDepsPresent() {
            return false;
        }

        @Test
        public void dirtyTrackingProgressReceiverWorksWithDeletingInvalidator() throws Exception {
            setupInvalidatableGraph();
            DirtyTrackingProgressReceiver receiver = new DirtyTrackingProgressReceiver(new EvaluationProgressReceiver.NullEvaluationProgressReceiver());
            // Dirty the node, and ensure that the tracker is aware of it:
            ImmutableList<SkyKey> diff = ImmutableList.of(GraphTester.nonHermeticKey("a"));
            InvalidationState state1 = new DirtyingInvalidationState();
            Preconditions.checkNotNull(EagerInvalidator.createInvalidatingVisitorIfNeeded(graph, diff, receiver, state1)).run();
            assertThat(receiver.getUnenqueuedDirtyKeys()).containsExactly(diff.get(0), skyKey("ab"));
            // Delete the node, and ensure that the tracker is no longer tracking it:
            Preconditions.checkNotNull(EagerInvalidator.createDeletingVisitorIfNeeded(graph, diff, receiver, state, true)).run();
            assertThat(receiver.getUnenqueuedDirtyKeys()).isEmpty();
        }
    }

    /**
     * Test suite for the dirtying invalidator.
     */
    @RunWith(JUnit4.class)
    public static class DirtyingInvalidatorTest extends EagerInvalidatorTest {
        @Override
        protected void invalidate(InMemoryGraph graph, DirtyTrackingProgressReceiver progressReceiver, SkyKey... keys) throws InterruptedException {
            Iterable<SkyKey> diff = ImmutableList.copyOf(keys);
            DirtyingNodeVisitor dirtyingNodeVisitor = EagerInvalidator.createInvalidatingVisitorIfNeeded(graph, diff, progressReceiver, state);
            if (dirtyingNodeVisitor != null) {
                visitor.set(dirtyingNodeVisitor);
                dirtyingNodeVisitor.run();
            }
        }

        @Override
        InvalidationState expectedState() {
            return InvalidationState.DIRTY;
        }

        @Override
        boolean gcExpected() {
            return false;
        }

        @Override
        protected InvalidationState newInvalidationState() {
            return new DirtyingInvalidationState();
        }

        @Override
        protected InvalidationType defaultInvalidationType() {
            return InvalidationType.CHANGED;
        }

        @Override
        protected boolean reverseDepsPresent() {
            return true;
        }

        @Test
        public void dirtyTrackingProgressReceiverWorksWithDirtyingInvalidator() throws Exception {
            setupInvalidatableGraph();
            DirtyTrackingProgressReceiver receiver = new DirtyTrackingProgressReceiver(new EvaluationProgressReceiver.NullEvaluationProgressReceiver());
            // Dirty the node, and ensure that the tracker is aware of it:
            invalidate(graph, receiver, GraphTester.nonHermeticKey("a"));
            assertThat(receiver.getUnenqueuedDirtyKeys()).hasSize(2);
        }
    }
}

