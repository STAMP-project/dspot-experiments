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
package org.apache.geode.internal.cache;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.client.internal.ServerRegionProxy;
import org.junit.Test;
import org.mockito.Mockito;


public class LocalRegionBulkOperationTest {
    private LocalRegion region;

    private EntryEventImpl event;

    private EventID eventID;

    private ServerRegionProxy serverRegionProxy;

    private CancelCriterion cancelCriterion;

    private PutAllPartialResultException exception;

    private final Object callbacks = new Object();

    private final CacheClosedException cacheClosedException = new CacheClosedException();

    @Test(expected = CacheClosedException.class)
    public void basicRemoveAllThrowsCacheClosedExceptionIfCacheIsClosing() {
        String[] strings = new String[]{ "key" };
        Set keys = new HashSet<>(Arrays.asList(strings));
        DistributedRemoveAllOperation removeAll = Mockito.mock(DistributedRemoveAllOperation.class);
        Mockito.when(removeAll.getBaseEvent()).thenReturn(event);
        Mockito.when(region.basicRemoveAll(keys, removeAll, null)).thenCallRealMethod();
        Mockito.when(serverRegionProxy.removeAll(keys, eventID, callbacks)).thenThrow(exception);
        region.basicRemoveAll(keys, removeAll, null);
    }

    @Test(expected = CacheClosedException.class)
    public void basicPutAllThrowsCacheClosedExceptionIfCacheIsClosing() {
        Map map = new HashMap();
        map.put("key", "value");
        DistributedPutAllOperation putAll = Mockito.mock(DistributedPutAllOperation.class);
        Mockito.when(putAll.getBaseEvent()).thenReturn(event);
        Mockito.when(region.basicPutAll(map, putAll, null)).thenCallRealMethod();
        Mockito.when(region.getAtomicThresholdInfo()).thenReturn(Mockito.mock(MemoryThresholdInfo.class));
        Mockito.when(serverRegionProxy.putAll(map, eventID, true, callbacks)).thenThrow(exception);
        region.basicPutAll(map, putAll, null);
    }
}

