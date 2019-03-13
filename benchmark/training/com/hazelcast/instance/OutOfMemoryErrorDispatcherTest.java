/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.instance;


import MemoryUnit.MEGABYTES;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.OutOfMemoryHandler;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.MemoryInfoAccessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static DefaultOutOfMemoryHandler.GC_OVERHEAD_LIMIT_EXCEEDED;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class OutOfMemoryErrorDispatcherTest extends HazelcastTestSupport {
    @Test
    public void onOutOfMemory() {
        OutOfMemoryError oome = new OutOfMemoryError();
        OutOfMemoryHandler handler = Mockito.mock(OutOfMemoryHandler.class);
        Mockito.when(handler.shouldHandle(oome)).thenReturn(Boolean.TRUE);
        HazelcastInstance hz1 = Mockito.mock(HazelcastInstance.class);
        OutOfMemoryErrorDispatcher.registerServer(hz1);
        OutOfMemoryErrorDispatcher.setServerHandler(handler);
        HazelcastInstance[] registeredInstances = OutOfMemoryErrorDispatcher.current();
        OutOfMemoryErrorDispatcher.onOutOfMemory(oome);
        // make sure the handler is called
        Mockito.verify(handler).onOutOfMemory(oome, registeredInstances);
        // make sure that the registered instances are removed.
        Assert.assertArrayEquals(new HazelcastInstance[]{  }, OutOfMemoryErrorDispatcher.current());
    }

    @Test
    public void register() {
        HazelcastInstance hz1 = Mockito.mock(HazelcastInstance.class);
        HazelcastInstance hz2 = Mockito.mock(HazelcastInstance.class);
        OutOfMemoryErrorDispatcher.registerServer(hz1);
        Assert.assertArrayEquals(new HazelcastInstance[]{ hz1 }, OutOfMemoryErrorDispatcher.current());
        OutOfMemoryErrorDispatcher.registerServer(hz2);
        Assert.assertArrayEquals(new HazelcastInstance[]{ hz1, hz2 }, OutOfMemoryErrorDispatcher.current());
    }

    @Test(expected = IllegalArgumentException.class)
    public void register_whenNull() {
        OutOfMemoryErrorDispatcher.registerServer(null);
    }

    @Test
    public void deregister_Existing() {
        HazelcastInstance hz1 = Mockito.mock(HazelcastInstance.class);
        HazelcastInstance hz2 = Mockito.mock(HazelcastInstance.class);
        HazelcastInstance hz3 = Mockito.mock(HazelcastInstance.class);
        OutOfMemoryErrorDispatcher.registerServer(hz1);
        OutOfMemoryErrorDispatcher.registerServer(hz2);
        OutOfMemoryErrorDispatcher.registerServer(hz3);
        OutOfMemoryErrorDispatcher.deregisterServer(hz2);
        Assert.assertArrayEquals(new HazelcastInstance[]{ hz1, hz3 }, OutOfMemoryErrorDispatcher.current());
        OutOfMemoryErrorDispatcher.deregisterServer(hz1);
        Assert.assertArrayEquals(new HazelcastInstance[]{ hz3 }, OutOfMemoryErrorDispatcher.current());
        OutOfMemoryErrorDispatcher.deregisterServer(hz3);
        Assert.assertArrayEquals(new HazelcastInstance[]{  }, OutOfMemoryErrorDispatcher.current());
    }

    @Test
    public void deregister_nonExisting() {
        HazelcastInstance instance = Mockito.mock(HazelcastInstance.class);
        OutOfMemoryErrorDispatcher.deregisterServer(instance);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deregister_null() {
        OutOfMemoryErrorDispatcher.deregisterServer(null);
    }

    @Test
    public void test_OutOfMemoryHandler_shouldHandle_true() {
        test_OutOfMemoryHandler_with_shouldHandle(Boolean.TRUE, Mockito.times(1));
    }

    @Test
    public void test_OutOfMemoryHandler_shouldHandle_false() {
        test_OutOfMemoryHandler_with_shouldHandle(Boolean.FALSE, Mockito.never());
    }

    @Test
    public void test_DefaultOutOfMemoryHandler_whenGcOverheadLimitExceeded() {
        OutOfMemoryError oome = new OutOfMemoryError(GC_OVERHEAD_LIMIT_EXCEEDED);
        HazelcastInstance hz = Mockito.mock(HazelcastInstance.class);
        OutOfMemoryErrorDispatcher.registerServer(hz);
        OutOfMemoryHandler handler = Mockito.spy(new DefaultOutOfMemoryHandler());
        Mockito.when(handler.shouldHandle(oome)).thenCallRealMethod();
        OutOfMemoryErrorDispatcher.setServerHandler(handler);
        OutOfMemoryErrorDispatcher.onOutOfMemory(oome);
        Mockito.verify(handler).onOutOfMemory(oome, new HazelcastInstance[]{ hz });
    }

    @Test
    public void test_DefaultOutOfMemoryHandler_total_smallerThan_max() {
        test_DefaultOutOfMemoryHandler_using_accessor(new MemoryInfoAccessor() {
            @Override
            public long getMaxMemory() {
                return MEGABYTES.toBytes(100);
            }

            @Override
            public long getTotalMemory() {
                return MEGABYTES.toBytes(80);
            }

            @Override
            public long getFreeMemory() {
                return MEGABYTES.toBytes(10);
            }
        }, Mockito.never());
    }

    @Test
    public void test_DefaultOutOfMemoryHandler_total_equalTo_max() {
        test_DefaultOutOfMemoryHandler_using_accessor(new MemoryInfoAccessor() {
            @Override
            public long getMaxMemory() {
                return MEGABYTES.toBytes(100);
            }

            @Override
            public long getTotalMemory() {
                return MEGABYTES.toBytes(100);
            }

            @Override
            public long getFreeMemory() {
                return MEGABYTES.toBytes(20);
            }
        }, Mockito.never());
    }

    @Test
    public void test_DefaultOutOfMemoryHandler_not_enough_memory() {
        test_DefaultOutOfMemoryHandler_using_accessor(new MemoryInfoAccessor() {
            @Override
            public long getMaxMemory() {
                return MEGABYTES.toBytes(100);
            }

            @Override
            public long getTotalMemory() {
                return MEGABYTES.toBytes(100);
            }

            @Override
            public long getFreeMemory() {
                return MEGABYTES.toBytes(5);
            }
        }, Mockito.times(1));
    }
}

