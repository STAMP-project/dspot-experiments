/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.source;


import java.util.List;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurables;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestSequenceGeneratorSource {
    private PollableSource source;

    @Test
    public void testLifecycle() throws EventDeliveryException {
        final int DOPROCESS_LOOPS = 5;
        Context context = new Context();
        Configurables.configure(source, context);
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        source.setChannelProcessor(cp);
        source.start();
        for (int i = 0; i < DOPROCESS_LOOPS; i++) {
            source.process();
        }
        source.stop();
        // no exception is expected during lifecycle calls
    }

    @Test
    public void testSingleEvents() throws EventDeliveryException {
        final int BATCH_SIZE = 1;
        final int TOTAL_EVENTS = 5;
        final int DOPROCESS_LOOPS = 10;
        Context context = new Context();
        context.put("batchSize", Integer.toString(BATCH_SIZE));
        context.put("totalEvents", Integer.toString(TOTAL_EVENTS));
        Configurables.configure(source, context);
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        // failure injection
        Mockito.doNothing().doThrow(ChannelException.class).doNothing().when(cp).processEvent(Mockito.any(Event.class));
        source.setChannelProcessor(cp);
        source.start();
        for (int i = 0; i < DOPROCESS_LOOPS; i++) {
            source.process();
        }
        ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(cp, Mockito.times(6)).processEvent(argumentCaptor.capture());
        Mockito.verify(cp, Mockito.never()).processEventBatch(Mockito.anyListOf(Event.class));
        TestSequenceGeneratorSource.verifyEventSequence(TOTAL_EVENTS, argumentCaptor.getAllValues());
    }

    @Test
    public void testBatch() throws EventDeliveryException {
        final int BATCH_SIZE = 3;
        final int TOTAL_EVENTS = 5;
        final int DOPROCESS_LOOPS = 10;
        Context context = new Context();
        context.put("batchSize", Integer.toString(BATCH_SIZE));
        context.put("totalEvents", Integer.toString(TOTAL_EVENTS));
        Configurables.configure(source, context);
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        // failure injection on the second batch
        Mockito.doNothing().doThrow(ChannelException.class).doNothing().when(cp).processEventBatch(Mockito.anyListOf(Event.class));
        source.setChannelProcessor(cp);
        source.start();
        for (int i = 0; i < DOPROCESS_LOOPS; i++) {
            source.process();
        }
        ArgumentCaptor<List<Event>> argumentCaptor = ArgumentCaptor.forClass(((Class) (List.class)));
        Mockito.verify(cp, Mockito.never()).processEvent(Mockito.any(Event.class));
        Mockito.verify(cp, Mockito.times(3)).processEventBatch(argumentCaptor.capture());
        List<List<Event>> eventBatches = argumentCaptor.getAllValues();
        TestSequenceGeneratorSource.verifyEventSequence(TOTAL_EVENTS, TestSequenceGeneratorSource.flatOutBatches(eventBatches));
    }
}

