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
package org.apache.geode.connectors.jdbc;


import Operation.CREATE;
import Operation.DESTROY;
import Operation.LOCAL_LOAD_CREATE;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.RegionEvent;
import org.apache.geode.cache.SerializedCacheValue;
import org.apache.geode.connectors.jdbc.internal.SqlHandler;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.InternalRegion;
import org.apache.geode.pdx.PdxInstance;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JdbcWriterTest {
    private EntryEvent<Object, Object> entryEvent;

    private PdxInstance pdxInstance;

    private SqlHandler sqlHandler;

    private InternalRegion region;

    private SerializedCacheValue<Object> serializedNewValue;

    private RegionEvent<Object, Object> regionEvent;

    private InternalCache cache;

    private Object key;

    private JdbcWriter<Object, Object> writer;

    @Test
    public void beforeUpdateWithPdxInstanceWritesToSqlHandler() throws Exception {
        writer.beforeUpdate(entryEvent);
        Mockito.verify(sqlHandler, Mockito.times(1)).write(ArgumentMatchers.eq(region), ArgumentMatchers.eq(CREATE), ArgumentMatchers.eq(key), ArgumentMatchers.eq(pdxInstance));
    }

    @Test
    public void beforeUpdateWithoutPdxInstanceWritesToSqlHandler() {
        Mockito.when(serializedNewValue.getDeserializedValue()).thenReturn(new Object());
        assertThatThrownBy(() -> writer.beforeUpdate(entryEvent)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void beforeCreateWithPdxInstanceWritesToSqlHandler() throws Exception {
        writer.beforeCreate(entryEvent);
        Mockito.verify(sqlHandler, Mockito.times(1)).write(ArgumentMatchers.eq(region), ArgumentMatchers.eq(CREATE), ArgumentMatchers.eq(key), ArgumentMatchers.eq(pdxInstance));
        assertThat(writer.getTotalEvents()).isEqualTo(1);
    }

    @Test
    public void beforeCreateWithNewPdxInstanceWritesToSqlHandler() throws Exception {
        PdxInstance newPdxInstance = Mockito.mock(PdxInstance.class);
        Mockito.when(entryEvent.getNewValue()).thenReturn(newPdxInstance);
        Mockito.when(entryEvent.getSerializedNewValue()).thenReturn(null);
        writer.beforeCreate(entryEvent);
        Mockito.verify(sqlHandler, Mockito.times(1)).write(ArgumentMatchers.eq(region), ArgumentMatchers.eq(CREATE), ArgumentMatchers.eq(key), ArgumentMatchers.eq(newPdxInstance));
        assertThat(writer.getTotalEvents()).isEqualTo(1);
    }

    @Test
    public void beforeCreateWithLoadEventDoesNothing() throws Exception {
        Mockito.when(entryEvent.getOperation()).thenReturn(LOCAL_LOAD_CREATE);
        writer.beforeCreate(entryEvent);
        Mockito.verify(sqlHandler, Mockito.times(0)).write(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        assertThat(writer.getTotalEvents()).isEqualTo(0);
    }

    @Test
    public void beforeDestroyWithDestroyEventWritesToSqlHandler() throws Exception {
        Mockito.when(entryEvent.getOperation()).thenReturn(DESTROY);
        Mockito.when(entryEvent.getSerializedNewValue()).thenReturn(null);
        writer.beforeDestroy(entryEvent);
        Mockito.verify(sqlHandler, Mockito.times(1)).write(ArgumentMatchers.eq(region), ArgumentMatchers.eq(DESTROY), ArgumentMatchers.eq(key), ArgumentMatchers.eq(null));
    }

    @Test
    public void beforeRegionDestroyDoesNotWriteToSqlHandler() {
        writer.beforeRegionDestroy(Mockito.mock(RegionEvent.class));
        Mockito.verifyZeroInteractions(sqlHandler);
    }

    @Test
    public void beforeRegionClearDoesNotWriteToSqlHandler() {
        writer.beforeRegionClear(Mockito.mock(RegionEvent.class));
        Mockito.verifyZeroInteractions(sqlHandler);
    }
}

