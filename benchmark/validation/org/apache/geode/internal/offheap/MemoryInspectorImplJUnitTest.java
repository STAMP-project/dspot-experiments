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
package org.apache.geode.internal.offheap;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MemoryInspectorImplJUnitTest {
    private FreeListManager freeList;

    private MemoryInspector inspector;

    @Test
    public void getSnapshotBeforeCreateSnapshotReturnsEmptyList() {
        Assert.assertTrue(inspector.getSnapshot().isEmpty());
    }

    @Test
    public void getAllBlocksBeforeCreateSnapshotReturnsEmptyList() {
        Assert.assertTrue(inspector.getAllBlocks().isEmpty());
    }

    @Test
    public void getAllocatedBlocksBeforeCreateSnapshotReturnsEmptyList() {
        Assert.assertTrue(inspector.getAllocatedBlocks().isEmpty());
    }

    @Test
    public void getFirstBlockBeforeCreateSnapshotReturnsNull() {
        Assert.assertNull(inspector.getFirstBlock());
    }

    @Test
    public void getBlockAfterWithNullBeforeCreateSnapshotReturnsNull() {
        Assert.assertNull(inspector.getBlockAfter(null));
    }

    @Test
    public void getBlockAfterBeforeCreateSnapshotReturnsNull() {
        MemoryBlock block = Mockito.mock(MemoryBlock.class);
        Assert.assertNull(inspector.getBlockAfter(block));
    }

    @Test
    public void canClearUncreatedSnapshot() {
        inspector.clearSnapshot();
        Assert.assertTrue(inspector.getSnapshot().isEmpty());
    }

    @Test
    public void createSnapshotCallsGetOrderedBlocks() {
        List<MemoryBlock> emptyList = new ArrayList<MemoryBlock>();
        createSnapshot(emptyList);
        Mockito.verify(this.freeList, Mockito.times(1)).getOrderedBlocks();
        Assert.assertSame(emptyList, inspector.getSnapshot());
    }

    @Test
    public void createSnapshotIsIdempotent() {
        List<MemoryBlock> emptyList = new ArrayList<MemoryBlock>();
        createSnapshot(emptyList);
        Mockito.when(this.freeList.getOrderedBlocks()).thenReturn(null);
        inspector.createSnapshot();
        Mockito.verify(this.freeList, Mockito.times(1)).getOrderedBlocks();
        Assert.assertSame(emptyList, inspector.getSnapshot());
    }

    @Test
    public void clearSnapshotAfterCreatingOneReturnsEmptyList() {
        List<MemoryBlock> emptyList = new ArrayList<MemoryBlock>();
        createSnapshot(emptyList);
        inspector.clearSnapshot();
        Assert.assertTrue(inspector.getSnapshot().isEmpty());
    }

    @Test
    public void getFirstBlockReturnsFirstBlockFromSnapshot() {
        List<MemoryBlock> fakeSnapshot = setupFakeSnapshot();
        Assert.assertSame(fakeSnapshot.get(0), inspector.getFirstBlock());
    }

    @Test
    public void getFirstBlockAfterReturnsCorrectBlock() {
        List<MemoryBlock> fakeSnapshot = setupFakeSnapshot();
        Assert.assertSame(fakeSnapshot.get(1), inspector.getBlockAfter(fakeSnapshot.get(0)));
    }

    @Test
    public void getFirstBlockAfterReturnsNullForLastBlock() {
        List<MemoryBlock> fakeSnapshot = setupFakeSnapshot();
        Assert.assertNull(inspector.getBlockAfter(fakeSnapshot.get(1)));
    }
}

