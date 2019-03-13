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
package org.apache.avro.ipc;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.avro.test.Mail;
import org.apache.avro.test.Message;
import org.junit.Assert;
import org.junit.Test;


public class TestRpcPluginOrdering {
    private static AtomicInteger orderCounter = new AtomicInteger();

    public class OrderPlugin extends RPCPlugin {
        public void clientStartConnect(RPCContext context) {
            Assert.assertEquals(0, TestRpcPluginOrdering.orderCounter.getAndIncrement());
        }

        public void clientSendRequest(RPCContext context) {
            Assert.assertEquals(1, TestRpcPluginOrdering.orderCounter.getAndIncrement());
        }

        public void clientReceiveResponse(RPCContext context) {
            Assert.assertEquals(6, TestRpcPluginOrdering.orderCounter.getAndIncrement());
        }

        public void clientFinishConnect(RPCContext context) {
            Assert.assertEquals(5, TestRpcPluginOrdering.orderCounter.getAndIncrement());
        }

        public void serverConnecting(RPCContext context) {
            Assert.assertEquals(2, TestRpcPluginOrdering.orderCounter.getAndIncrement());
        }

        public void serverReceiveRequest(RPCContext context) {
            Assert.assertEquals(3, TestRpcPluginOrdering.orderCounter.getAndIncrement());
        }

        public void serverSendResponse(RPCContext context) {
            Assert.assertEquals(4, TestRpcPluginOrdering.orderCounter.getAndIncrement());
        }
    }

    @Test
    public void testRpcPluginOrdering() throws Exception {
        TestRpcPluginOrdering.OrderPlugin plugin = new TestRpcPluginOrdering.OrderPlugin();
        SpecificResponder responder = new SpecificResponder(Mail.class, new TestRpcPluginOrdering.TestMailImpl());
        SpecificRequestor requestor = new SpecificRequestor(Mail.class, new LocalTransceiver(responder));
        responder.addRPCPlugin(plugin);
        requestor.addRPCPlugin(plugin);
        Mail client = SpecificRequestor.getClient(Mail.class, requestor);
        Message message = createTestMessage();
        client.send(message);
    }

    private static class TestMailImpl implements Mail {
        public String send(Message message) throws AvroRemoteException {
            return "Received";
        }

        public void fireandforget(Message message) {
        }
    }
}

