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


import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.versions.RegionVersionVector;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class AbstractEvictionListTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private BucketRegion bucketRegion;

    private EvictionCounters stats;

    private EvictionController controller;

    @Test
    public void sizeIsZeroByDefault() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        assertThat(size()).isZero();
    }

    @Test
    public void sizeIncreasesWithAppendEntry() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        evictionList.appendEntry(new LinkableEvictionNode());
        assertThat(size()).isEqualTo(1);
        evictionList.appendEntry(new LinkableEvictionNode());
        assertThat(size()).isEqualTo(2);
    }

    @Test
    public void sizeDecreasedWhenDecremented() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        evictionList.appendEntry(new LinkableEvictionNode());
        decrementSize();
        assertThat(size()).isZero();
    }

    @Test
    public void getStatisticsReturnsRightObject() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        assertThat(getStatistics()).isSameAs(stats);
    }

    @Test
    public void closeStats() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        evictionList.closeStats();
        Mockito.verify(stats).close();
    }

    @Test
    public void clearWithVersionVectorDoesNotChangeStats() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        evictionList.appendEntry(new LinkableEvictionNode());
        assertThat(size()).isEqualTo(1);
        evictionList.clear(Mockito.mock(RegionVersionVector.class), bucketRegion);
        assertThat(size()).isEqualTo(1);
    }

    @Test
    public void clearWithoutBucketRegionResetsStats() throws Exception {
        AbstractEvictionListTest.TestEvictionList noBucketRegionEvictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        noBucketRegionEvictionList.appendEntry(new LinkableEvictionNode());
        assertThat(size()).isEqualTo(1);
        clear(null, null);
        Mockito.verify(stats).resetCounter();
        assertThat(size()).isZero();
    }

    @Test
    public void clearWithBucketRegionResetsBucketStats() throws Exception {
        long bucketSize = 10L;
        Mockito.when(bucketRegion.getCounter()).thenReturn(bucketSize);
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        evictionList.clear(null, bucketRegion);
        Mockito.verify(bucketRegion).resetCounter();
        Mockito.verify(stats).decrementCounter(bucketSize);
        assertThat(size()).isZero();
    }

    @Test
    public void appendEntryAlreadyInListDoesNothing() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        EvictionNode node = Mockito.mock(EvictionNode.class);
        Mockito.when(node.next()).thenReturn(Mockito.mock(EvictionNode.class));
        evictionList.appendEntry(node);
        Mockito.verify(node, Mockito.never()).unsetRecentlyUsed();
    }

    @Test
    public void appendingNewEntryAddsItToList() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        EvictionNode node = Mockito.mock(EvictionNode.class);
        evictionList.appendEntry(node);
        Mockito.verify(node).setNext(evictionList.tail);
        Mockito.verify(node).setPrevious(evictionList.head);
        assertThat(evictionList.tail.previous()).isSameAs(node);
        assertThat(evictionList.head.next()).isSameAs(node);
        assertThat(size()).isEqualTo(1);
    }

    @Test
    public void unlinkEntryNotInListTest() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        EvictionNode node = Mockito.mock(EvictionNode.class);
        evictionList.destroyEntry(node);
        assertThat(size()).isEqualTo(0);
    }

    @Test
    public void unlinkEntryInListTest() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        EvictionNode node = Mockito.mock(EvictionNode.class);
        Mockito.when(node.next()).thenReturn(evictionList.tail);
        Mockito.when(node.previous()).thenReturn(evictionList.head);
        appendEntry(Mockito.mock(EvictionNode.class));
        assertThat(size()).isEqualTo(1);
        evictionList.destroyEntry(node);
        assertThat(size()).isEqualTo(0);
        Mockito.verify(stats).incDestroys();
    }

    @Test
    public void unlinkHeadOnEmptyListReturnsNull() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        assertThat(unlinkHeadEntry()).isNull();
    }

    @Test
    public void unlinkTailOnEmptyListReturnsNull() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        assertThat(unlinkTailEntry()).isNull();
    }

    @Test
    public void unlinkHeadInListTest() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        EvictionNode node = Mockito.mock(EvictionNode.class);
        Mockito.when(node.next()).thenReturn(null, evictionList.tail);
        Mockito.when(node.previous()).thenReturn(evictionList.head);
        evictionList.appendEntry(node);
        assertThat(unlinkHeadEntry()).isSameAs(node);
        assertThat(size()).isEqualTo(0);
    }

    @Test
    public void unlinkTailInListTest() throws Exception {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        EvictionNode node = Mockito.mock(EvictionNode.class);
        Mockito.when(node.next()).thenReturn(null, evictionList.tail);
        Mockito.when(node.previous()).thenReturn(evictionList.head);
        evictionList.appendEntry(node);
        assertThat(unlinkTailEntry()).isSameAs(node);
        assertThat(size()).isEqualTo(0);
    }

    @Test
    public void nodeUsedByTransactionIsNotEvictable() {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        EvictionNode node = Mockito.mock(EvictionNode.class);
        Mockito.when(node.isInUseByTransaction()).thenReturn(true);
        assertThat(evictionList.isEvictable(node)).isFalse();
    }

    @Test
    public void evictedNodeIsNotEvictable() {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        EvictionNode node = Mockito.mock(EvictionNode.class);
        Mockito.when(node.isEvicted()).thenReturn(true);
        assertThat(evictionList.isEvictable(node)).isFalse();
    }

    @Test
    public void defaultNodeIsEvictable() {
        AbstractEvictionListTest.TestEvictionList evictionList = new AbstractEvictionListTest.TestEvictionList(controller);
        EvictionNode node = Mockito.mock(EvictionNode.class);
        assertThat(evictionList.isEvictable(node)).isTrue();
    }

    private static class TestEvictionList extends AbstractEvictionList {
        TestEvictionList(EvictionController controller) {
            super(controller);
        }

        @Override
        public EvictableEntry getEvictableEntry() {
            return null;
        }

        @Override
        public void incrementRecentlyUsed() {
        }
    }
}

