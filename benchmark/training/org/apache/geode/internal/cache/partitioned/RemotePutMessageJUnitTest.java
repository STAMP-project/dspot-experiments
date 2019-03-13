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
package org.apache.geode.internal.cache.partitioned;


import Operation.UPDATE;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.cache.EventID;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class RemotePutMessageJUnitTest {
    static final int UNKNOWN_REGION = 234;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void putThrowsIOExceptionOnMissingRegion() throws Exception {
        thrown.expect(IOException.class);
        // Mocks with minimal implementation to ensure code path through PutMessage.toData
        EntryEventImpl mockEvent = Mockito.mock(EntryEventImpl.class);
        Mockito.when(mockEvent.isPossibleDuplicate()).thenReturn(false);
        Mockito.when(mockEvent.getEventId()).thenReturn(Mockito.mock(EventID.class));
        Mockito.when(mockEvent.getOperation()).thenReturn(UPDATE);
        Mockito.when(mockEvent.getDeltaBytes()).thenReturn(new byte[]{  });
        DistributionConfig mockDistributionConfig = Mockito.mock(DistributionConfig.class);
        Mockito.when(mockDistributionConfig.getDeltaPropagation()).thenReturn(true);
        InternalDistributedSystem mockInternalDistributedSystem = Mockito.mock(InternalDistributedSystem.class);
        Mockito.when(mockInternalDistributedSystem.getConfig()).thenReturn(mockDistributionConfig);
        // Construct a put with minimum configuration needed to reach region check.
        PutMessage put = new PutMessage(new HashSet(), false, RemotePutMessageJUnitTest.UNKNOWN_REGION, null, mockEvent, 0, false, false, null, false);
        put.setSendDelta(true);
        put.setInternalDs(mockInternalDistributedSystem);
        put.toData(new DataOutputStream(new ByteArrayOutputStream()));
    }
}

