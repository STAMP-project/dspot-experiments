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
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class LIFOListTest {
    private BucketRegion bucketRegion;

    private EvictionCounters stats;

    private EvictionController controller;

    @Test
    public void evictingFromEmptyListTest() throws Exception {
        LIFOList list = new LIFOList(controller);
        assertThat(list.getEvictableEntry()).isNull();
        assertThat(list.size()).isZero();
    }

    @Test
    public void evictingFromNonEmptyListTest() throws Exception {
        LIFOList list = new LIFOList(controller);
        EvictionNode node = Mockito.mock(EvictableEntry.class);
        list.appendEntry(node);
        assertThat(list.size()).isEqualTo(1);
        Mockito.when(node.next()).thenReturn(list.tail);
        Mockito.when(node.previous()).thenReturn(list.head);
        assertThat(list.getEvictableEntry()).isSameAs(node);
        Mockito.verify(stats).incEvaluations(ArgumentMatchers.anyLong());
        assertThat(list.size()).isZero();
    }

    @Test
    public void doesNotEvictNodeInTransaction() throws Exception {
        LIFOList list = new LIFOList(controller);
        EvictionNode nodeInTransaction = Mockito.mock(EvictableEntry.class);
        Mockito.when(nodeInTransaction.isInUseByTransaction()).thenReturn(true);
        EvictionNode nodeNotInTransaction = Mockito.mock(EvictableEntry.class);
        list.appendEntry(nodeNotInTransaction);
        list.appendEntry(nodeInTransaction);
        assertThat(list.size()).isEqualTo(2);
        Mockito.when(nodeInTransaction.next()).thenReturn(list.tail);
        Mockito.when(nodeInTransaction.previous()).thenReturn(nodeNotInTransaction);
        Mockito.when(nodeNotInTransaction.next()).thenReturn(list.tail);
        Mockito.when(nodeNotInTransaction.previous()).thenReturn(list.head);
        assertThat(list.getEvictableEntry()).isSameAs(nodeNotInTransaction);
        Mockito.verify(stats).incEvaluations(2);
        assertThat(list.size()).isZero();
    }

    @Test
    public void doesNotEvictNodeThatIsEvicted() throws Exception {
        LIFOList list = new LIFOList(controller);
        EvictionNode evictedNode = Mockito.mock(EvictableEntry.class);
        Mockito.when(evictedNode.isEvicted()).thenReturn(true);
        EvictionNode node = Mockito.mock(EvictableEntry.class);
        list.appendEntry(node);
        list.appendEntry(evictedNode);
        assertThat(list.size()).isEqualTo(2);
        Mockito.when(evictedNode.next()).thenReturn(list.tail);
        Mockito.when(evictedNode.previous()).thenReturn(node);
        Mockito.when(node.next()).thenReturn(list.tail);
        Mockito.when(node.previous()).thenReturn(list.head);
        assertThat(list.getEvictableEntry()).isSameAs(node);
        Mockito.verify(stats).incEvaluations(2);
        assertThat(list.size()).isZero();
    }
}

