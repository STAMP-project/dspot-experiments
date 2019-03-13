/**
 * Copyright 2011 LMAX Ltd.
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
package com.lmax.disruptor;


import com.lmax.disruptor.support.DummyEventHandler;
import org.junit.Test;


@SuppressWarnings("unchecked")
public final class AggregateEventHandlerTest {
    private final DummyEventHandler<int[]> eh1 = new DummyEventHandler<int[]>();

    private final DummyEventHandler<int[]> eh2 = new DummyEventHandler<int[]>();

    private final DummyEventHandler<int[]> eh3 = new DummyEventHandler<int[]>();

    @Test
    public void shouldCallOnEventInSequence() throws Exception {
        final int[] event = new int[]{ 7 };
        final long sequence = 3L;
        final boolean endOfBatch = true;
        final AggregateEventHandler<int[]> aggregateEventHandler = new AggregateEventHandler<int[]>(eh1, eh2, eh3);
        aggregateEventHandler.onEvent(event, sequence, endOfBatch);
        AggregateEventHandlerTest.assertLastEvent(event, sequence, eh1, eh2, eh3);
    }

    @Test
    public void shouldCallOnStartInSequence() throws Exception {
        final AggregateEventHandler<int[]> aggregateEventHandler = new AggregateEventHandler<int[]>(eh1, eh2, eh3);
        aggregateEventHandler.onStart();
        AggregateEventHandlerTest.assertStartCalls(1, eh1, eh2, eh3);
    }

    @Test
    public void shouldCallOnShutdownInSequence() throws Exception {
        final AggregateEventHandler<int[]> aggregateEventHandler = new AggregateEventHandler<int[]>(eh1, eh2, eh3);
        aggregateEventHandler.onShutdown();
        AggregateEventHandlerTest.assertShutoownCalls(1, eh1, eh2, eh3);
    }

    @Test
    public void shouldHandleEmptyListOfEventHandlers() throws Exception {
        final AggregateEventHandler<int[]> aggregateEventHandler = new AggregateEventHandler<int[]>();
        aggregateEventHandler.onEvent(new int[]{ 7 }, 0L, true);
        aggregateEventHandler.onStart();
        aggregateEventHandler.onShutdown();
    }
}

