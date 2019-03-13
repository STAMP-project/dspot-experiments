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


import Operation.LOCAL_LOAD_CREATE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.connectors.jdbc.internal.SqlHandler;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.InternalRegion;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JdbcAsyncWriterTest {
    private SqlHandler sqlHandler;

    private InternalRegion region;

    private JdbcAsyncWriter writer;

    private InternalCache cache;

    @Test
    public void throwsNullPointerExceptionIfGivenNullList() {
        assertThatThrownBy(() -> writer.processEvents(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void doesNothingIfEventListIsEmpty() {
        writer.processEvents(Collections.emptyList());
        Mockito.verifyZeroInteractions(sqlHandler);
        assertThat(writer.getSuccessfulEvents()).isZero();
        assertThat(writer.getTotalEvents()).isZero();
    }

    @Test
    public void writesAProvidedEvent() throws Exception {
        writer.processEvents(Collections.singletonList(createMockEvent()));
        Mockito.verify(sqlHandler, Mockito.times(1)).write(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        assertThat(writer.getSuccessfulEvents()).isEqualTo(1);
        assertThat(writer.getTotalEvents()).isEqualTo(1);
    }

    @Test
    public void ignoresLoadEvent() throws Exception {
        writer.processEvents(Collections.singletonList(createMockEvent(LOCAL_LOAD_CREATE)));
        Mockito.verify(sqlHandler, Mockito.times(0)).write(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        assertThat(writer.getIgnoredEvents()).isEqualTo(1);
        assertThat(writer.getTotalEvents()).isEqualTo(1);
        assertThat(writer.getFailedEvents()).isEqualTo(0);
    }

    @Test
    public void writesMultipleProvidedEvents() throws Exception {
        List<AsyncEvent> events = new ArrayList<>();
        events.add(createMockEvent());
        events.add(createMockEvent());
        events.add(createMockEvent());
        writer.processEvents(events);
        Mockito.verify(sqlHandler, Mockito.times(3)).write(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        assertThat(writer.getSuccessfulEvents()).isEqualTo(3);
        assertThat(writer.getTotalEvents()).isEqualTo(3);
    }
}

