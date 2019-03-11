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


import MemoryAllocatorImpl.FREE_OFF_HEAP_MEMORY_PROPERTY;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;


/**
 * Tests LifecycleListener
 */
public class LifecycleListenerJUnitTest {
    private final List<LifecycleListenerJUnitTest.LifecycleListenerCallback> afterCreateCallbacks = new ArrayList<LifecycleListenerJUnitTest.LifecycleListenerCallback>();

    private final List<LifecycleListenerJUnitTest.LifecycleListenerCallback> afterReuseCallbacks = new ArrayList<LifecycleListenerJUnitTest.LifecycleListenerCallback>();

    private final List<LifecycleListenerJUnitTest.LifecycleListenerCallback> beforeCloseCallbacks = new ArrayList<LifecycleListenerJUnitTest.LifecycleListenerCallback>();

    private final LifecycleListenerJUnitTest.TestLifecycleListener listener = new LifecycleListenerJUnitTest.TestLifecycleListener(this.afterCreateCallbacks, this.afterReuseCallbacks, this.beforeCloseCallbacks);

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void testAddRemoveListener() {
        LifecycleListener.addLifecycleListener(this.listener);
        LifecycleListener.removeLifecycleListener(this.listener);
        SlabImpl slab = new SlabImpl(1024);// 1k

        MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
        Assert.assertEquals(0, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(0, this.beforeCloseCallbacks.size());
        ma.close();
        Assert.assertEquals(0, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(0, this.beforeCloseCallbacks.size());
        LifecycleListener.removeLifecycleListener(this.listener);
    }

    @Test
    public void testCallbacksAreCalledAfterCreate() {
        LifecycleListener.addLifecycleListener(this.listener);
        SlabImpl slab = new SlabImpl(1024);// 1k

        MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
        Assert.assertEquals(1, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(0, this.beforeCloseCallbacks.size());
        closeAndFree(ma);
        Assert.assertEquals(1, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(1, this.beforeCloseCallbacks.size());
        LifecycleListener.removeLifecycleListener(this.listener);
    }

    @Test
    public void testCallbacksAreCalledAfterReuse() {
        LifecycleListener.addLifecycleListener(this.listener);
        System.setProperty(FREE_OFF_HEAP_MEMORY_PROPERTY, "false");
        SlabImpl slab = new SlabImpl(1024);// 1k

        MemoryAllocatorImpl ma = createAllocator(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
        Assert.assertEquals(1, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(0, this.beforeCloseCallbacks.size());
        ma.close();
        Assert.assertEquals(1, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(1, this.beforeCloseCallbacks.size());
        ma = createAllocator(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), null);
        Assert.assertEquals(1, this.afterCreateCallbacks.size());
        Assert.assertEquals(1, this.afterReuseCallbacks.size());
        Assert.assertEquals(1, this.beforeCloseCallbacks.size());
        MemoryAllocatorImpl ma2 = createAllocator(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
        Assert.assertEquals(null, ma2);
        Assert.assertEquals(1, this.afterCreateCallbacks.size());
        Assert.assertEquals(1, this.afterReuseCallbacks.size());
        Assert.assertEquals(1, this.beforeCloseCallbacks.size());
        ma.close();
        Assert.assertEquals(1, this.afterCreateCallbacks.size());
        Assert.assertEquals(1, this.afterReuseCallbacks.size());
        Assert.assertEquals(2, this.beforeCloseCallbacks.size());
    }

    @Test
    public void testCallbacksAreCalledAfterReuseWithFreeTrue() {
        LifecycleListener.addLifecycleListener(this.listener);
        SlabImpl slab = new SlabImpl(1024);// 1k

        MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
        Assert.assertEquals(1, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(0, this.beforeCloseCallbacks.size());
        closeAndFree(ma);
        Assert.assertEquals(1, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(1, this.beforeCloseCallbacks.size());
        slab = new SlabImpl(1024);// 1k

        MemoryAllocatorImpl ma2 = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
        Assert.assertEquals(2, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(1, this.beforeCloseCallbacks.size());
        closeAndFree(ma);
        Assert.assertEquals(2, this.afterCreateCallbacks.size());
        Assert.assertEquals(0, this.afterReuseCallbacks.size());
        Assert.assertEquals(2, this.beforeCloseCallbacks.size());
    }

    private static class LifecycleListenerCallback {
        private final MemoryAllocatorImpl allocator;

        private final long timeStamp;

        private final Throwable creationTime;

        LifecycleListenerCallback(MemoryAllocatorImpl allocator) {
            this.allocator = allocator;
            this.timeStamp = System.currentTimeMillis();
            this.creationTime = new Exception();
        }
    }

    private static class TestLifecycleListener implements LifecycleListener {
        private final List<LifecycleListenerJUnitTest.LifecycleListenerCallback> afterCreateCallbacks;

        private final List<LifecycleListenerJUnitTest.LifecycleListenerCallback> afterReuseCallbacks;

        private final List<LifecycleListenerJUnitTest.LifecycleListenerCallback> beforeCloseCallbacks;

        TestLifecycleListener(List<LifecycleListenerJUnitTest.LifecycleListenerCallback> afterCreateCallbacks, List<LifecycleListenerJUnitTest.LifecycleListenerCallback> afterReuseCallbacks, List<LifecycleListenerJUnitTest.LifecycleListenerCallback> beforeCloseCallbacks) {
            this.afterCreateCallbacks = afterCreateCallbacks;
            this.afterReuseCallbacks = afterReuseCallbacks;
            this.beforeCloseCallbacks = beforeCloseCallbacks;
        }

        @Override
        public void afterCreate(MemoryAllocatorImpl allocator) {
            this.afterCreateCallbacks.add(new LifecycleListenerJUnitTest.LifecycleListenerCallback(allocator));
        }

        @Override
        public void afterReuse(MemoryAllocatorImpl allocator) {
            this.afterReuseCallbacks.add(new LifecycleListenerJUnitTest.LifecycleListenerCallback(allocator));
        }

        @Override
        public void beforeClose(MemoryAllocatorImpl allocator) {
            this.beforeCloseCallbacks.add(new LifecycleListenerJUnitTest.LifecycleListenerCallback(allocator));
        }
    }
}

