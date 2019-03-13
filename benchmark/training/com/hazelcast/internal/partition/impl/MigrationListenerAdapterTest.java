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
package com.hazelcast.internal.partition.impl;


import com.hazelcast.core.MigrationEvent;
import com.hazelcast.core.MigrationListener;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@Category(QuickTest.class)
public class MigrationListenerAdapterTest {
    @Mock
    private MigrationListener listener;

    private MigrationListenerAdapter adapter;

    @Test
    public void test_migrationStarted() {
        final MigrationEvent event = new MigrationEvent(0, null, null, STARTED);
        adapter.onEvent(event);
        Mockito.verify(listener).migrationStarted(event);
        Mockito.verify(listener, Mockito.never()).migrationCompleted(ArgumentMatchers.any(MigrationEvent.class));
        Mockito.verify(listener, Mockito.never()).migrationFailed(ArgumentMatchers.any(MigrationEvent.class));
    }

    @Test
    public void test_migrationCompleted() {
        final MigrationEvent event = new MigrationEvent(0, null, null, COMPLETED);
        adapter.onEvent(event);
        Mockito.verify(listener, Mockito.never()).migrationStarted(ArgumentMatchers.any(MigrationEvent.class));
        Mockito.verify(listener).migrationCompleted(event);
        Mockito.verify(listener, Mockito.never()).migrationFailed(ArgumentMatchers.any(MigrationEvent.class));
    }

    @Test
    public void test_migrationFailed() {
        final MigrationEvent event = new MigrationEvent(0, null, null, FAILED);
        adapter.onEvent(event);
        Mockito.verify(listener, Mockito.never()).migrationStarted(ArgumentMatchers.any(MigrationEvent.class));
        Mockito.verify(listener, Mockito.never()).migrationCompleted(ArgumentMatchers.any(MigrationEvent.class));
        Mockito.verify(listener).migrationFailed(event);
    }

    @Test
    public void test_migrationEvent_serialization() throws IOException {
        final MigrationEvent event = new MigrationEvent(0, null, null, STARTED);
        final ObjectDataOutput output = Mockito.mock(ObjectDataOutput.class);
        event.writeData(output);
        Mockito.verify(output).writeInt(0);
        Mockito.verify(output, Mockito.times(2)).writeObject(null);
        Mockito.verify(output).writeByte(0);
    }

    @Test
    public void test_migrationEvent_deserialization() throws IOException {
        final ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readInt()).thenReturn(0);
        Mockito.when(input.readObject()).thenReturn(null);
        Mockito.when(input.readObject()).thenReturn(null);
        Mockito.when(input.readByte()).thenReturn(((byte) (0)));
        final MigrationEvent event = new MigrationEvent();
        event.readData(input);
        Assert.assertEquals(0, event.getPartitionId());
        Assert.assertNull(event.getOldOwner());
        Assert.assertNull(event.getNewOwner());
        Assert.assertEquals(STARTED, event.getStatus());
    }
}

