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
package org.apache.flume.source.thriftLegacy;


import com.cloudera.flume.handlers.thrift.Priority;
import com.cloudera.flume.handlers.thrift.ThriftFlumeEvent;
import com.cloudera.flume.handlers.thrift.ThriftFlumeEventServer.Client;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.Test;


public class TestThriftLegacySource {
    private int selectedPort;

    private ThriftLegacySource source;

    private Channel channel;

    public class FlumeClient {
        private String host;

        private int port;

        public FlumeClient(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void append(ThriftFlumeEvent evt) {
            TTransport transport;
            try {
                transport = new TSocket(host, port);
                TProtocol protocol = new org.apache.thrift.protocol.TBinaryProtocol(transport);
                Client client = new Client(protocol);
                transport.open();
                client.append(evt);
                transport.close();
            } catch (TTransportException e) {
                e.printStackTrace();
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testLifecycle() throws InterruptedException {
        bind();
        stop();
    }

    @Test
    public void testRequest() throws IOException, InterruptedException {
        bind();
        Map<String, ByteBuffer> flumeMap = new HashMap<>();
        ThriftFlumeEvent thriftEvent = new ThriftFlumeEvent(1, Priority.INFO, ByteBuffer.wrap("foo".getBytes()), 0, "fooHost", flumeMap);
        TestThriftLegacySource.FlumeClient fClient = new TestThriftLegacySource.FlumeClient("0.0.0.0", selectedPort);
        fClient.append(thriftEvent);
        // check if the even has arrived in the channel through OG thrift source
        Transaction transaction = channel.getTransaction();
        transaction.begin();
        Event event = channel.take();
        Assert.assertNotNull(event);
        Assert.assertEquals("Channel contained our event", "foo", new String(event.getBody()));
        transaction.commit();
        transaction.close();
        stop();
    }

    @Test
    public void testHeaders() throws IOException, InterruptedException {
        bind();
        Map<String, ByteBuffer> flumeHeaders = new HashMap<>();
        flumeHeaders.put("hello", ByteBuffer.wrap("world".getBytes("UTF-8")));
        ThriftFlumeEvent thriftEvent = new ThriftFlumeEvent(1, Priority.INFO, ByteBuffer.wrap("foo".getBytes()), 0, "fooHost", flumeHeaders);
        TestThriftLegacySource.FlumeClient fClient = new TestThriftLegacySource.FlumeClient("0.0.0.0", selectedPort);
        fClient.append(thriftEvent);
        // check if the event has arrived in the channel through OG thrift source
        Transaction transaction = channel.getTransaction();
        transaction.begin();
        Event event = channel.take();
        Assert.assertNotNull(event);
        Assert.assertEquals("Event in channel has our header", "world", event.getHeaders().get("hello"));
        transaction.commit();
        transaction.close();
        stop();
    }
}

