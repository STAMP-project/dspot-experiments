/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.eviction;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.RegionEntryContext;
import org.apache.geode.internal.lang.SystemPropertyHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class LRUListWithAsyncSortingTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private BucketRegion bucketRegion;

    private EvictionCounters stats;

    private EvictionController controller;

    private ExecutorService executor = Mockito.mock(ExecutorService.class);

    @Test
    public void scansOnlyWhenOverThreshold() throws Exception {
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, executor, 1);
        for (int i = 0; i < 5; i++) {
            list.appendEntry(Mockito.mock(EvictionNode.class));
        }
        list.incrementRecentlyUsed();
        Mockito.verifyNoMoreInteractions(executor);
        list.incrementRecentlyUsed();
        Mockito.verify(executor).submit(ArgumentMatchers.any(Runnable.class));
    }

    @Test
    public void clearResetsRecentlyUsedCounter() throws Exception {
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, executor, 1);
        list.incrementRecentlyUsed();
        assertThat(list.getRecentlyUsedCount()).isEqualTo(1);
        list.clear(null, null);
        assertThat(list.getRecentlyUsedCount()).isZero();
    }

    @Test
    public void doesNotRunScanOnEmptyList() throws Exception {
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, executor, 1);
        list.incrementRecentlyUsed();
        Mockito.verifyNoMoreInteractions(executor);
    }

    @Test
    public void usesSystemPropertyThresholdIfSpecified() throws Exception {
        System.setProperty(("geode." + (SystemPropertyHelper.EVICTION_SCAN_THRESHOLD_PERCENT)), "55");
        try {
            LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, executor, 1);
            list.appendEntry(Mockito.mock(EvictionNode.class));
            list.appendEntry(Mockito.mock(EvictionNode.class));
            list.incrementRecentlyUsed();
            Mockito.verifyNoMoreInteractions(executor);
            list.incrementRecentlyUsed();
            Mockito.verify(executor).submit(ArgumentMatchers.any(Runnable.class));
        } finally {
            System.clearProperty(("geode." + (SystemPropertyHelper.EVICTION_SCAN_THRESHOLD_PERCENT)));
        }
    }

    @Test
    public void evictingFromEmptyListTest() throws Exception {
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, executor, 1);
        assertThat(list.getEvictableEntry()).isNull();
        assertThat(list.size()).isZero();
    }

    @Test
    public void evictingFromNonEmptyListTest() throws Exception {
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, executor, 1);
        EvictionNode node = Mockito.mock(EvictableEntry.class);
        list.appendEntry(node);
        assertThat(list.size()).isEqualTo(1);
        Mockito.when(node.next()).thenReturn(list.tail);
        Mockito.when(node.previous()).thenReturn(list.head);
        assertThat(list.getEvictableEntry()).isSameAs(node);
        assertThat(list.size()).isZero();
    }

    @Test
    public void doesNotEvictNodeInTransaction() throws Exception {
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, executor, 1);
        EvictionNode nodeInTransaction = Mockito.mock(EvictableEntry.class, "nodeInTransaction");
        Mockito.when(nodeInTransaction.isInUseByTransaction()).thenReturn(true);
        EvictionNode nodeNotInTransaction = Mockito.mock(EvictableEntry.class, "nodeNotInTransaction");
        list.appendEntry(nodeInTransaction);
        list.appendEntry(nodeNotInTransaction);
        assertThat(list.size()).isEqualTo(2);
        Mockito.when(nodeInTransaction.next()).thenReturn(nodeNotInTransaction);
        Mockito.when(nodeInTransaction.previous()).thenReturn(list.head);
        Mockito.when(nodeNotInTransaction.next()).thenReturn(list.tail);
        Mockito.when(nodeNotInTransaction.previous()).thenReturn(list.head);
        assertThat(list.getEvictableEntry()).isSameAs(nodeNotInTransaction);
        assertThat(list.size()).isZero();
    }

    @Test
    public void doesNotEvictNodeThatIsEvicted() throws Exception {
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, executor, 1);
        EvictionNode evictedNode = Mockito.mock(EvictableEntry.class);
        Mockito.when(evictedNode.isEvicted()).thenReturn(true);
        EvictionNode node = Mockito.mock(EvictableEntry.class);
        list.appendEntry(evictedNode);
        list.appendEntry(node);
        assertThat(list.size()).isEqualTo(2);
        Mockito.when(evictedNode.next()).thenReturn(node);
        Mockito.when(evictedNode.previous()).thenReturn(list.head);
        Mockito.when(node.next()).thenReturn(list.tail);
        Mockito.when(node.previous()).thenReturn(list.head);
        assertThat(list.getEvictableEntry()).isSameAs(node);
        assertThat(list.size()).isZero();
    }

    @Test
    public void scanUnsetsRecentlyUsedOnNode() throws Exception {
        ExecutorService realExecutor = Executors.newSingleThreadExecutor();
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, realExecutor, 1);
        EvictionNode recentlyUsedNode = Mockito.mock(EvictableEntry.class);
        Mockito.when(recentlyUsedNode.previous()).thenReturn(list.head);
        Mockito.when(recentlyUsedNode.isRecentlyUsed()).thenReturn(true);
        list.appendEntry(recentlyUsedNode);
        Mockito.when(recentlyUsedNode.next()).thenReturn(list.tail);
        list.incrementRecentlyUsed();
        // unsetRecentlyUsed() is called once during scan
        await().untilAsserted(() -> verify(recentlyUsedNode, times(1)).unsetRecentlyUsed());
        realExecutor.shutdown();
    }

    @Test
    public void scanEndsOnlyUpToSize() throws Exception {
        ExecutorService realExecutor = Executors.newSingleThreadExecutor();
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, realExecutor, 1);
        EvictionNode recentlyUsedNode = Mockito.mock(EvictableEntry.class);
        Mockito.when(recentlyUsedNode.previous()).thenReturn(list.head);
        Mockito.when(recentlyUsedNode.isRecentlyUsed()).thenReturn(true);
        list.appendEntry(recentlyUsedNode);
        Mockito.when(recentlyUsedNode.next()).thenReturn(recentlyUsedNode);
        list.incrementRecentlyUsed();
        // unsetRecentlyUsed() is called once during scan
        await().untilAsserted(() -> verify(recentlyUsedNode, times(1)).unsetRecentlyUsed());
        realExecutor.shutdown();
    }

    @Test
    public void scanMovesRecentlyUsedNodeToTail() throws Exception {
        ExecutorService realExecutor = Executors.newSingleThreadExecutor();
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, realExecutor, 1);
        EvictionNode recentlyUsedNode = Mockito.mock(EvictableEntry.class, "first");
        EvictionNode secondNode = Mockito.mock(EvictableEntry.class, "second");
        EvictionNode thirdNode = Mockito.mock(EvictableEntry.class, "third");
        list.appendEntry(recentlyUsedNode);
        list.appendEntry(secondNode);
        list.appendEntry(thirdNode);
        Mockito.when(recentlyUsedNode.next()).thenReturn(secondNode);
        Mockito.when(recentlyUsedNode.previous()).thenReturn(list.head);
        Mockito.when(secondNode.next()).thenReturn(thirdNode);
        // The second node is moved to first. Its previous will be head.
        Mockito.when(secondNode.previous()).thenReturn(list.head);
        Mockito.when(thirdNode.next()).thenReturn(list.tail);
        Mockito.when(thirdNode.previous()).thenReturn(secondNode);
        Mockito.when(recentlyUsedNode.isRecentlyUsed()).thenReturn(true);
        list.incrementRecentlyUsed();
        // unsetRecentlyUsed() is called once during scan
        await().untilAsserted(() -> verify(recentlyUsedNode, times(1)).unsetRecentlyUsed());
        assertThat(list.tail.previous()).isEqualTo(recentlyUsedNode);
        realExecutor.shutdown();
    }

    @Test
    public void startScanIfEvictableEntryIsRecentlyUsed() throws Exception {
        List<EvictionNode> nodes = new ArrayList<>();
        LRUListWithAsyncSorting lruEvictionList = new LRUListWithAsyncSorting(controller, executor, 1);
        IntStream.range(0, 11).forEach(( i) -> {
            EvictionNode node = new LRUTestEntry(i);
            nodes.add(node);
            lruEvictionList.appendEntry(node);
            node.setRecentlyUsed(Mockito.mock(RegionEntryContext.class));
        });
        assertThat(lruEvictionList.getEvictableEntry().isRecentlyUsed()).isTrue();
        Mockito.verify(executor).submit(ArgumentMatchers.any(Runnable.class));
    }

    @Test
    public void scanNotStartedIfSizeBelowMaxEvictionAttempts() {
        LRUListWithAsyncSorting list = new LRUListWithAsyncSorting(controller, executor, 2);
        EvictionNode recentlyUsedNode = Mockito.mock(EvictableEntry.class, "first");
        EvictionNode secondNode = Mockito.mock(EvictableEntry.class, "second");
        list.appendEntry(recentlyUsedNode);
        list.incrementRecentlyUsed();
        Mockito.verifyNoMoreInteractions(executor);
        list.appendEntry(secondNode);
        list.incrementRecentlyUsed();
        Mockito.verify(executor).submit(ArgumentMatchers.any(Runnable.class));
    }
}

