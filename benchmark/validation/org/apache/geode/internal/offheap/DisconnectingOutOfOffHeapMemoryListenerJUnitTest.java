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


import OffHeapStorage.STAY_CONNECTED_ON_OUTOFOFFHEAPMEMORY_PROPERTY;
import org.apache.geode.LogWriter;
import org.apache.geode.OutOfOffHeapMemoryException;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.mockito.Mockito;


public class DisconnectingOutOfOffHeapMemoryListenerJUnitTest {
    private final InternalDistributedSystem ids = Mockito.mock(InternalDistributedSystem.class);

    private final OutOfOffHeapMemoryException ex = new OutOfOffHeapMemoryException();

    private final LogWriter lw = Mockito.mock(LogWriter.class);

    private final DistributionManager dm = Mockito.mock(DistributionManager.class);

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void constructWithNullSupportsClose() {
        DisconnectingOutOfOffHeapMemoryListener listener = new DisconnectingOutOfOffHeapMemoryListener(null);
        listener.close();
    }

    @Test
    public void constructWithNullSupportsOutOfOffHeapMemory() {
        DisconnectingOutOfOffHeapMemoryListener listener = new DisconnectingOutOfOffHeapMemoryListener(null);
        listener.outOfOffHeapMemory(null);
    }

    @Test
    public void disconnectNotCalledWhenSysPropIsSet() {
        System.setProperty(STAY_CONNECTED_ON_OUTOFOFFHEAPMEMORY_PROPERTY, "true");
        DisconnectingOutOfOffHeapMemoryListener listener = new DisconnectingOutOfOffHeapMemoryListener(ids);
        listener.outOfOffHeapMemory(ex);
        Mockito.verify(ids, Mockito.never()).disconnect(ex.getMessage(), ex, false);
    }

    @Test
    public void disconnectNotCalledWhenListenerClosed() {
        DisconnectingOutOfOffHeapMemoryListener listener = new DisconnectingOutOfOffHeapMemoryListener(ids);
        listener.close();
        listener.outOfOffHeapMemory(ex);
        Mockito.verify(ids, Mockito.never()).disconnect(ex.getMessage(), ex, false);
    }

    @Test
    public void setRootCauseCalledWhenGetRootCauseReturnsNull() {
        DisconnectingOutOfOffHeapMemoryListener listener = new DisconnectingOutOfOffHeapMemoryListener(ids);
        Mockito.when(dm.getRootCause()).thenReturn(null);
        listener.outOfOffHeapMemory(ex);
        Mockito.verify(dm).setRootCause(ex);
    }

    @Test
    public void setRootCauseNotCalledWhenGetRootCauseReturnsNonNull() {
        DisconnectingOutOfOffHeapMemoryListener listener = new DisconnectingOutOfOffHeapMemoryListener(ids);
        Mockito.when(dm.getRootCause()).thenReturn(ex);
        listener.outOfOffHeapMemory(ex);
        Mockito.verify(dm, Mockito.never()).setRootCause(ex);
    }

    @Test
    public void disconnectCalledAsyncAfterCallingOutOfOffHeapMemory() {
        DisconnectingOutOfOffHeapMemoryListener listener = new DisconnectingOutOfOffHeapMemoryListener(ids);
        listener.outOfOffHeapMemory(ex);
        Mockito.verify(ids, Mockito.timeout(5000).atLeastOnce()).disconnect(ex.getMessage(), ex, false);
        Mockito.verify(lw).info(("OffHeapStorage about to invoke disconnect on " + (ids)));
    }
}

