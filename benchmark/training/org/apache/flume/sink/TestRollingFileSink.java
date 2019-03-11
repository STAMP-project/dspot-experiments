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
package org.apache.flume.sink;


import java.io.File;
import java.io.IOException;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.channel.BasicTransactionSemantics;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.instrumentation.SinkCounter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRollingFileSink {
    private static final Logger logger = LoggerFactory.getLogger(TestRollingFileSink.class);

    private File tmpDir;

    private RollingFileSink sink;

    @Test
    public void testLifecycle() {
        Context context = new Context();
        context.put("sink.directory", tmpDir.getPath());
        Configurables.configure(sink, context);
        sink.start();
        sink.stop();
    }

    @Test
    public void testAppend() throws IOException, InterruptedException, EventDeliveryException {
        Context context = new Context();
        context.put("sink.directory", tmpDir.getPath());
        context.put("sink.rollInterval", "1");
        context.put("sink.batchSize", "1");
        doTest(context);
    }

    @Test
    public void testAppend2() throws IOException, InterruptedException, EventDeliveryException {
        Context context = new Context();
        context.put("sink.directory", tmpDir.getPath());
        context.put("sink.rollInterval", "0");
        context.put("sink.batchSize", "1");
        doTest(context);
    }

    @Test
    public void testAppend3() throws IOException, InterruptedException, EventDeliveryException {
        File tmpDir = new File("target/tmpLog");
        tmpDir.mkdirs();
        cleanDirectory(tmpDir);
        Context context = new Context();
        context.put("sink.directory", "target/tmpLog");
        context.put("sink.rollInterval", "0");
        context.put("sink.batchSize", "1");
        context.put("sink.pathManager.prefix", "test3-");
        context.put("sink.pathManager.extension", "txt");
        doTest(context);
    }

    @Test
    public void testRollTime() throws IOException, InterruptedException, EventDeliveryException {
        File tmpDir = new File("target/tempLog");
        tmpDir.mkdirs();
        cleanDirectory(tmpDir);
        Context context = new Context();
        context.put("sink.directory", "target/tempLog/");
        context.put("sink.rollInterval", "1");
        context.put("sink.batchSize", "1");
        context.put("sink.pathManager", "rolltime");
        context.put("sink.pathManager.prefix", "test4-");
        context.put("sink.pathManager.extension", "txt");
        doTest(context);
    }

    @Test
    public void testChannelException() throws IOException, InterruptedException {
        Context context = new Context();
        context.put("sink.directory", tmpDir.getPath());
        context.put("sink.rollInterval", "0");
        context.put("sink.batchSize", "1");
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channel.take()).thenThrow(new ChannelException("dummy"));
        Transaction transaction = Mockito.mock(BasicTransactionSemantics.class);
        Mockito.when(channel.getTransaction()).thenReturn(transaction);
        try {
            doTest(context, channel);
        } catch (EventDeliveryException e) {
            // 
        }
        SinkCounter sinkCounter = ((SinkCounter) (Whitebox.getInternalState(sink, "sinkCounter")));
        Assert.assertEquals(1, sinkCounter.getChannelReadFail());
    }

    /**
     * This test is to reproduce batch size and
     * transaction capacity related configuration
     * problems
     */
    @Test(expected = EventDeliveryException.class)
    public void testTransCapBatchSizeCompatibility() throws EventDeliveryException {
        Context context = new Context();
        context.put("sink.directory", tmpDir.getPath());
        context.put("sink.rollInterval", "0");
        context.put("sink.batchSize", "1000");
        Configurables.configure(sink, context);
        context.put("capacity", "50");
        context.put("transactionCapacity", "5");
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        try {
            for (int j = 0; j < 10; j++) {
                Transaction tx = channel.getTransaction();
                tx.begin();
                for (int i = 0; i < 5; i++) {
                    Event event = new SimpleEvent();
                    event.setBody(("Test event " + i).getBytes());
                    channel.put(event);
                }
                tx.commit();
                tx.close();
            }
            sink.process();
        } finally {
            sink.stop();
        }
    }
}

