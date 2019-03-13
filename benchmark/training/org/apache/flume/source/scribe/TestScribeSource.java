/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.source.scribe;


import Scribe.Client;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class TestScribeSource {
    private static int port;

    private static Channel memoryChannel;

    private static ScribeSource scribeSource;

    @Test
    public void testScribeMessage() throws Exception {
        sendSingle();
        // try to get it from Channels
        Transaction tx = TestScribeSource.memoryChannel.getTransaction();
        tx.begin();
        Event e = TestScribeSource.memoryChannel.take();
        Assert.assertNotNull(e);
        Assert.assertEquals("Sending info msg to scribe source", new String(e.getBody()));
        tx.commit();
        tx.close();
    }

    @Test
    public void testScribeMultipleMessages() throws Exception {
        TTransport transport = new org.apache.thrift.transport.TFramedTransport(new TSocket("localhost", TestScribeSource.port));
        TProtocol protocol = new org.apache.thrift.protocol.TBinaryProtocol(transport);
        Scribe.Client client = new Scribe.Client(protocol);
        transport.open();
        List<LogEntry> logEntries = new ArrayList<LogEntry>(10);
        for (int i = 0; i < 10; i++) {
            LogEntry logEntry = new LogEntry("INFO", String.format("Sending info msg# %d to scribe source", i));
            logEntries.add(logEntry);
        }
        client.Log(logEntries);
        // try to get it from Channels
        Transaction tx = TestScribeSource.memoryChannel.getTransaction();
        tx.begin();
        for (int i = 0; i < 10; i++) {
            Event e = TestScribeSource.memoryChannel.take();
            Assert.assertNotNull(e);
            Assert.assertEquals(String.format("Sending info msg# %d to scribe source", i), new String(e.getBody()));
        }
        tx.commit();
        tx.close();
    }

    @Test
    public void testErrorCounter() throws Exception {
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        Mockito.doThrow(new ChannelException("dummy")).when(cp).processEventBatch(ArgumentMatchers.anyListOf(Event.class));
        ChannelProcessor origCp = TestScribeSource.scribeSource.getChannelProcessor();
        TestScribeSource.scribeSource.setChannelProcessor(cp);
        sendSingle();
        TestScribeSource.scribeSource.setChannelProcessor(origCp);
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(TestScribeSource.scribeSource, "sourceCounter")));
        assertEquals(1, sc.getChannelWriteFail());
    }
}

