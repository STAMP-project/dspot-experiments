/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.monitoring.internal;


import com.amazonaws.monitoring.ApiCallAttemptMonitoringEvent;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link AsynchronousAgentDispatcher}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AsynchronousAgentDispatcherTest {
    @Mock
    private DatagramChannel channel;

    private AsynchronousAgentDispatcher dispatcher = AsynchronousAgentDispatcher.getInstance();

    @Test
    public void testIgnoresIOExceptionFromChannelWrite() throws IOException, InterruptedException {
        Mockito.when(channel.write(ArgumentMatchers.any(ByteBuffer.class))).thenThrow(new IOException("buffer is full"));
        dispatcher.addWriteTask(new ApiCallAttemptMonitoringEvent().withApi("foo"), channel, 8192);
        dispatcher.addWriteTask(new ApiCallAttemptMonitoringEvent().withApi("foo"), channel, 8192);
        Thread.sleep(100);
        Mockito.verify(channel, Mockito.times(2)).write(ArgumentMatchers.any(ByteBuffer.class));
    }

    @Test
    public void testIgnoresMarshallingException() throws IOException, InterruptedException {
        ObjectWriter marshaller = Mockito.mock(ObjectWriter.class);
        ApiCallAttemptMonitoringEvent event1 = new ApiCallAttemptMonitoringEvent();
        ApiCallAttemptMonitoringEvent event2 = new ApiCallAttemptMonitoringEvent();
        Mockito.when(marshaller.writeValueAsBytes(ArgumentMatchers.eq(event1))).thenThrow(new RuntimeException("bad event"));
        Mockito.when(marshaller.writeValueAsBytes(ArgumentMatchers.eq(event2))).thenReturn(new byte[16]);
        AsynchronousAgentDispatcher dispatcher = new AsynchronousAgentDispatcher(marshaller);
        dispatcher.init();
        try {
            dispatcher.addWriteTask(event1, channel, 8192);
            dispatcher.addWriteTask(event2, channel, 8192);
            Thread.sleep(100);
            Mockito.verify(marshaller).writeValueAsBytes(ArgumentMatchers.eq(event1));
            Mockito.verify(marshaller).writeValueAsBytes(ArgumentMatchers.eq(event2));
            Mockito.verify(channel, Mockito.times(1)).write(ArgumentMatchers.any(ByteBuffer.class));
        } finally {
            dispatcher.release();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testAddWriteTaskThrowsWhenNotInitialized() {
        dispatcher.release();
        dispatcher.addWriteTask(new ApiCallAttemptMonitoringEvent(), channel, 8192);
    }

    @Test
    public void testDropsEventWhenTooLarge() throws IOException, InterruptedException {
        ApiCallAttemptMonitoringEvent tooLarge = new ApiCallAttemptMonitoringEvent().withApi("SomeApi");
        dispatcher.addWriteTask(tooLarge, channel, 1);
        Thread.sleep(100);
        Mockito.verify(channel, Mockito.times(0)).write(ArgumentMatchers.any(ByteBuffer.class));
    }

    @Test
    public void testWritesEventIfUnderMaxSize() throws IOException, InterruptedException {
        ApiCallAttemptMonitoringEvent okay = new ApiCallAttemptMonitoringEvent().withApi("SomeApi");
        dispatcher.addWriteTask(okay, channel, 8192);
        Thread.sleep(100);
        Mockito.verify(channel, Mockito.times(1)).write(ArgumentMatchers.any(ByteBuffer.class));
    }
}

