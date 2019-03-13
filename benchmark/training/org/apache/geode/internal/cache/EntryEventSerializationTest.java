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


import Scope.DISTRIBUTED_NO_ACK;
import Scope.GLOBAL;
import Scope.LOCAL;
import java.io.NotSerializableException;
import org.apache.geode.SerializationException;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EntryEventSerializationTest {
    private InternalRegion region;

    private InternalEntryEvent event;

    private EntryEventSerialization instance;

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void serializeNewValueIfNeeded_bothNull() {
        assertThatThrownBy(() -> instance.serializeNewValueIfNeeded(null, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void serializeNewValueIfNeeded_regionNull() {
        assertThatThrownBy(() -> instance.serializeNewValueIfNeeded(null, event)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void serializeNewValueIfNeeded_eventNull() {
        assertThatThrownBy(() -> instance.serializeNewValueIfNeeded(region, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void serializeNewValueIfNeeded_bothMocks() {
        assertThatCode(() -> instance.serializeNewValueIfNeeded(region, event)).doesNotThrowAnyException();
    }

    @Test
    public void localRegionDoesNothing() {
        Mockito.when(region.getScope()).thenReturn(LOCAL);
        instance.serializeNewValueIfNeeded(region, event);
        Mockito.verify(event, Mockito.times(0)).setCachedSerializedNewValue(ArgumentMatchers.any());
    }

    @Test
    public void distributedAckRegionSetsCachedSerializedNewValue() {
        instance.serializeNewValueIfNeeded(region, event);
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(event, Mockito.times(1)).setCachedSerializedNewValue(captor.capture());
        assertThat(captor.getValue().length).isGreaterThan(0);
    }

    @Test
    public void distributedNoAckRegionSetsCachedSerializedNewValue() {
        Mockito.when(region.getScope()).thenReturn(DISTRIBUTED_NO_ACK);
        instance.serializeNewValueIfNeeded(region, event);
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(event, Mockito.times(1)).setCachedSerializedNewValue(captor.capture());
        assertThat(captor.getValue().length).isGreaterThan(0);
    }

    @Test
    public void globalRegionSetsCachedSerializedNewValue() {
        Mockito.when(region.getScope()).thenReturn(GLOBAL);
        instance.serializeNewValueIfNeeded(region, event);
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(event, Mockito.times(1)).setCachedSerializedNewValue(captor.capture());
        assertThat(captor.getValue().length).isGreaterThan(0);
    }

    @Test
    public void hasCachedSerializedNewValueDoesNothing() {
        Mockito.when(event.getCachedSerializedNewValue()).thenReturn(new byte[0]);
        instance.serializeNewValueIfNeeded(region, event);
        Mockito.verify(event, Mockito.times(0)).setCachedSerializedNewValue(ArgumentMatchers.any());
    }

    @Test
    public void newValueIsByteArrayDoesNothing() {
        Mockito.when(event.basicGetNewValue()).thenReturn(new byte[0]);
        instance.serializeNewValueIfNeeded(region, event);
        Mockito.verify(event, Mockito.times(0)).setCachedSerializedNewValue(ArgumentMatchers.any());
    }

    @Test
    public void newValueIsCachedDeserializableUsesItsSerializedValue() {
        CachedDeserializable newValue = Mockito.mock(CachedDeserializable.class);
        Mockito.when(event.basicGetNewValue()).thenReturn(newValue);
        byte[] bytes = new byte[]{ 0, 3, 4 };
        Mockito.when(newValue.getSerializedValue()).thenReturn(bytes);
        instance.serializeNewValueIfNeeded(region, event);
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(event, Mockito.times(1)).setCachedSerializedNewValue(captor.capture());
        assertThat(captor.getValue()).isEqualTo(bytes);
    }

    @Test
    public void newValueIsSerializableUsesItsSerializedValue() {
        String newValue = "newValue";
        Mockito.when(event.basicGetNewValue()).thenReturn(newValue);
        instance.serializeNewValueIfNeeded(region, event);
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(event, Mockito.times(1)).setCachedSerializedNewValue(captor.capture());
        assertThat(captor.getValue()).isEqualTo(EntryEventImpl.serialize(newValue));
    }

    @Test
    public void newValueIsNotSerializableThrows() {
        Object newValue = new Object();
        Mockito.when(event.basicGetNewValue()).thenReturn(newValue);
        Throwable thrown = catchThrowable(() -> instance.serializeNewValueIfNeeded(region, event));
        assertThat(thrown).isInstanceOf(SerializationException.class);
        assertThat(thrown.getCause()).isInstanceOf(NotSerializableException.class);
    }

    @Test
    public void newValueIsPdxInstanceUsesItsSerializedValue() throws Exception {
        PdxInstanceImpl newValue = Mockito.mock(PdxInstanceImpl.class);
        Mockito.when(event.basicGetNewValue()).thenReturn(newValue);
        byte[] bytes = new byte[]{ 0, 3, 4 };
        Mockito.when(newValue.toBytes()).thenReturn(bytes);
        instance.serializeNewValueIfNeeded(region, event);
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(event, Mockito.times(1)).setCachedSerializedNewValue(captor.capture());
        assertThat(captor.getValue()).isEqualTo(bytes);
    }
}

