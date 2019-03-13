/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.websocket;


import org.apache.nifi.processor.Processor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestWebSocketMessageRouter {
    @Test
    public void testRegisterProcessor() throws Exception {
        final WebSocketMessageRouter router = new WebSocketMessageRouter("endpoint-id");
        final Processor processor1 = Mockito.mock(Processor.class);
        Mockito.when(processor1.getIdentifier()).thenReturn("processor-1");
        final Processor processor2 = Mockito.mock(Processor.class);
        Mockito.when(processor1.getIdentifier()).thenReturn("processor-2");
        router.registerProcessor(processor1);
        try {
            router.registerProcessor(processor2);
            Assert.fail("Should fail since a processor is already registered.");
        } catch (WebSocketConfigurationException e) {
        }
        Assert.assertTrue(router.isProcessorRegistered(processor1));
        Assert.assertFalse(router.isProcessorRegistered(processor2));
        // It's safe to call deregister even if it's not registered.
        router.deregisterProcessor(processor2);
        router.deregisterProcessor(processor1);
        // It's safe to call deregister even if it's not registered.
        router.deregisterProcessor(processor2);
    }

    @Test
    public void testSendMessage() throws Exception {
        final WebSocketMessageRouter router = new WebSocketMessageRouter("endpoint-id");
        final Processor processor1 = Mockito.mock(Processor.class);
        Mockito.when(processor1.getIdentifier()).thenReturn("processor-1");
        final AbstractWebSocketSession session = Mockito.mock(AbstractWebSocketSession.class);
        Mockito.when(session.getSessionId()).thenReturn("session-1");
        Mockito.doAnswer(( invocation) -> {
            Assert.assertEquals("message", getArgumentAt(0, String.class));
            return null;
        }).when(session).sendString(ArgumentMatchers.anyString());
        router.registerProcessor(processor1);
        router.captureSession(session);
        router.sendMessage("session-1", ( sender) -> sender.sendString("message"));
        try {
            router.sendMessage("session-2", ( sender) -> sender.sendString("message"));
            Assert.fail("Should fail because there's no session with id session-2.");
        } catch (IllegalStateException e) {
        }
    }
}

