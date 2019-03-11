/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling;


import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class GapAwareTrackingTokenTest {
    @Test
    public void testGapAwareTokenConcurrency() throws InterruptedException {
        AtomicLong counter = new AtomicLong();
        AtomicReference<GapAwareTrackingToken> currentToken = new AtomicReference(GapAwareTrackingToken.newInstance((-1), Collections.emptySortedSet()));
        ExecutorService executorService = Executors.newCachedThreadPool();
        // we need more threads than available processors, for a high likelihood to trigger this concurrency issue
        int threadCount = (Runtime.getRuntime().availableProcessors()) + 1;
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                long deadline = (System.currentTimeMillis()) + 1000;
                while ((System.currentTimeMillis()) < deadline) {
                    long next = counter.getAndIncrement();
                    currentToken.getAndUpdate(( t) -> t.advanceTo(next, Integer.MAX_VALUE));
                } 
            });
        }
        executorService.shutdown();
        Assert.assertTrue("ExecutorService not stopped within expected reasonable time frame", executorService.awaitTermination(5, TimeUnit.SECONDS));
        Assert.assertTrue("The test did not seem to have generated any tokens", ((counter.get()) > 0));
        TestCase.assertEquals(((counter.get()) - 1), currentToken.get().getIndex());
        TestCase.assertEquals(Collections.emptySortedSet(), currentToken.get().getGaps());
    }

    @Test
    public void testAdvanceToWithoutGaps() {
        GapAwareTrackingToken subject = GapAwareTrackingToken.newInstance(0L, Collections.emptyList());
        subject = subject.advanceTo(1L, 10);
        TestCase.assertEquals(1L, subject.getIndex());
        TestCase.assertEquals(Collections.emptySortedSet(), subject.getGaps());
    }

    @Test
    public void testAdvanceToWithInitialGaps() {
        GapAwareTrackingToken subject = GapAwareTrackingToken.newInstance(10L, Arrays.asList(1L, 5L, 6L));
        subject = subject.advanceTo(5L, 10);
        TestCase.assertEquals(10L, subject.getIndex());
        TestCase.assertEquals(Stream.of(1L, 6L).collect(Collectors.toCollection(TreeSet::new)), subject.getGaps());
    }

    @Test
    public void testAdvanceToWithNewGaps() {
        GapAwareTrackingToken subject = GapAwareTrackingToken.newInstance(10L, Collections.emptyList());
        subject = subject.advanceTo(13L, 10);
        TestCase.assertEquals(13L, subject.getIndex());
        TestCase.assertEquals(Stream.of(11L, 12L).collect(Collectors.toCollection(TreeSet::new)), subject.getGaps());
    }

    @Test
    public void testAdvanceToGapClearsOldGaps() {
        GapAwareTrackingToken subject = GapAwareTrackingToken.newInstance(15L, Arrays.asList(1L, 5L, 12L));
        subject = subject.advanceTo(12L, 10);
        TestCase.assertEquals(15L, subject.getIndex());
        TestCase.assertEquals(Stream.of(5L).collect(Collectors.toCollection(TreeSet::new)), subject.getGaps());
    }

    @Test
    public void testAdvanceToHigherSequenceClearsOldGaps() {
        GapAwareTrackingToken subject = GapAwareTrackingToken.newInstance(15L, Arrays.asList(1L, 5L, 12L));
        subject = subject.advanceTo(16L, 10);
        TestCase.assertEquals(16L, subject.getIndex());
        TestCase.assertEquals(Stream.of(12L).collect(Collectors.toCollection(TreeSet::new)), subject.getGaps());
    }

    @Test(expected = Exception.class)
    public void testAdvanceToLowerSequenceThatIsNotAGapNotAllowed() {
        GapAwareTrackingToken subject = GapAwareTrackingToken.newInstance(15L, Arrays.asList(1L, 5L, 12L));
        subject.advanceTo(4L, 10);
    }

    @Test(expected = Exception.class)
    public void testNewInstanceWithGapHigherThanSequenceNotAllowed() {
        GapAwareTrackingToken.newInstance(9L, Arrays.asList(1L, 5L, 12L));
    }

    @Test
    public void testTokenCoversOther() {
        GapAwareTrackingToken token1 = GapAwareTrackingToken.newInstance(3L, Collections.singleton(1L));
        GapAwareTrackingToken token2 = GapAwareTrackingToken.newInstance(4L, Collections.singleton(2L));
        GapAwareTrackingToken token3 = GapAwareTrackingToken.newInstance(2L, Arrays.asList(0L, 1L));
        GapAwareTrackingToken token4 = GapAwareTrackingToken.newInstance(3L, Collections.emptySortedSet());
        GapAwareTrackingToken token5 = GapAwareTrackingToken.newInstance(3L, Collections.singleton(2L));
        GapAwareTrackingToken token6 = GapAwareTrackingToken.newInstance(1L, Collections.emptySortedSet());
        GapAwareTrackingToken token7 = GapAwareTrackingToken.newInstance(2L, Collections.singleton(1L));
        TestCase.assertFalse(token1.covers(token2));
        TestCase.assertFalse(token2.covers(token1));
        Assert.assertTrue(token1.covers(token3));
        TestCase.assertFalse(token3.covers(token1));
        Assert.assertTrue(token4.covers(token1));
        TestCase.assertFalse(token1.covers(token4));
        TestCase.assertFalse(token2.covers(token4));
        TestCase.assertFalse(token4.covers(token2));
        Assert.assertTrue(token4.covers(token5));
        TestCase.assertFalse(token5.covers(token4));
        TestCase.assertFalse(token1.covers(token5));
        TestCase.assertFalse(token5.covers(token1));
        TestCase.assertFalse(token1.covers(token6));
        TestCase.assertFalse(token6.covers(token1));
        TestCase.assertFalse(token3.covers(token7));
    }

    @Test
    public void testOccurrenceOfInconsistentRangeException() {
        // verifies issue 655 (https://github.com/AxonFramework/AxonFramework/issues/655)
        GapAwareTrackingToken.newInstance(10L, Arrays.asList(0L, 1L, 2L, 8L, 9L)).advanceTo(0L, 5).covers(GapAwareTrackingToken.newInstance(0L, Collections.emptySet()));
    }

    @Test
    public void testLowerBound() {
        GapAwareTrackingToken token1 = GapAwareTrackingToken.newInstance(3L, Collections.singleton(1L));
        GapAwareTrackingToken token2 = GapAwareTrackingToken.newInstance(4L, Collections.singleton(2L));
        GapAwareTrackingToken token3 = GapAwareTrackingToken.newInstance(2L, Arrays.asList(0L, 1L));
        GapAwareTrackingToken token4 = GapAwareTrackingToken.newInstance(3L, Collections.emptySortedSet());
        GapAwareTrackingToken token5 = GapAwareTrackingToken.newInstance(3L, Collections.singleton(2L));
        GapAwareTrackingToken token6 = GapAwareTrackingToken.newInstance(1L, Collections.emptySortedSet());
        TestCase.assertEquals(token1.lowerBound(token2), GapAwareTrackingToken.newInstance(3L, Arrays.asList(1L, 2L)));
        TestCase.assertEquals(token1.lowerBound(token3), token3);
        TestCase.assertEquals(token1.lowerBound(token4), token1);
        TestCase.assertEquals(token1.lowerBound(token5), GapAwareTrackingToken.newInstance(3L, Arrays.asList(1L, 2L)));
        TestCase.assertEquals(token1.lowerBound(token6), GapAwareTrackingToken.newInstance(0L, Collections.emptySortedSet()));
        TestCase.assertEquals(token2.lowerBound(token3), GapAwareTrackingToken.newInstance((-1L), Collections.emptySortedSet()));
    }

    @Test
    public void testUpperBound() {
        GapAwareTrackingToken token0 = GapAwareTrackingToken.newInstance(9, Collections.emptyList());
        GapAwareTrackingToken token1 = GapAwareTrackingToken.newInstance(10, Collections.singleton(9L));
        GapAwareTrackingToken token2 = GapAwareTrackingToken.newInstance(10, Arrays.asList(9L, 8L));
        GapAwareTrackingToken token3 = GapAwareTrackingToken.newInstance(15, Collections.singletonList(14L));
        GapAwareTrackingToken token4 = GapAwareTrackingToken.newInstance(15, Arrays.asList(14L, 9L, 8L));
        GapAwareTrackingToken token5 = GapAwareTrackingToken.newInstance(14, Collections.emptyList());
        TestCase.assertEquals(GapAwareTrackingToken.newInstance(10, Collections.emptyList()), token0.upperBound(token1));
        TestCase.assertEquals(token1, token1.upperBound(token2));
        TestCase.assertEquals(token3, token1.upperBound(token3));
        TestCase.assertEquals(GapAwareTrackingToken.newInstance(15, Arrays.asList(14L, 9L)), token1.upperBound(token4));
        TestCase.assertEquals(GapAwareTrackingToken.newInstance(15, Arrays.asList(14L, 9L, 8L)), token2.upperBound(token4));
        TestCase.assertEquals(GapAwareTrackingToken.newInstance(15, Collections.emptyList()), token5.upperBound(token3));
    }

    @Test
    public void testGapTruncationRetainsEquality() {
        GapAwareTrackingToken token1 = GapAwareTrackingToken.newInstance(15, Arrays.asList(14L, 9L, 8L));
        TestCase.assertEquals(token1, token1.withGapsTruncatedAt(8L));
        TestCase.assertEquals(token1.withGapsTruncatedAt(9L), token1);
        TestCase.assertEquals(token1.withGapsTruncatedAt(9L), token1.withGapsTruncatedAt(8L));
        TestCase.assertEquals(token1.withGapsTruncatedAt(16L), token1.withGapsTruncatedAt(16L));
    }

    @Test
    public void testTruncateGaps() {
        GapAwareTrackingToken token1 = GapAwareTrackingToken.newInstance(15, Arrays.asList(14L, 9L, 8L));
        Assert.assertSame(token1, token1.withGapsTruncatedAt(7));
        TestCase.assertEquals(asTreeSet(9L, 14L), token1.withGapsTruncatedAt(9L).getGaps());
        TestCase.assertEquals(asTreeSet(14L), token1.withGapsTruncatedAt(10L).getGaps());
        TestCase.assertEquals(Collections.emptySet(), token1.withGapsTruncatedAt(15L).getGaps());
    }

    @Test
    public void testGapTruncatedTokenCoveredByOriginal() {
        GapAwareTrackingToken token1 = GapAwareTrackingToken.newInstance(15, Arrays.asList(14L, 9L, 8L));
        Assert.assertTrue(token1.covers(token1.withGapsTruncatedAt(10)));
        Assert.assertTrue(token1.withGapsTruncatedAt(10).covers(token1));
    }
}

