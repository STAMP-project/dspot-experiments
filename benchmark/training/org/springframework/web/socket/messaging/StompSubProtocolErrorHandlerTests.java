/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.socket.messaging;


import StompCommand.ERROR;
import StompCommand.SEND;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;


/**
 * Unit tests for {@link StompSubProtocolErrorHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class StompSubProtocolErrorHandlerTests {
    private StompSubProtocolErrorHandler handler;

    @Test
    public void handleClientMessageProcessingError() throws Exception {
        Exception ex = new Exception("fake exception");
        Message<byte[]> actual = this.handler.handleClientMessageProcessingError(null, ex);
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(actual, StompHeaderAccessor.class);
        Assert.assertNotNull(accessor);
        Assert.assertEquals(ERROR, accessor.getCommand());
        Assert.assertEquals(ex.getMessage(), accessor.getMessage());
        Assert.assertArrayEquals(new byte[0], actual.getPayload());
    }

    @Test
    public void handleClientMessageProcessingErrorWithReceipt() throws Exception {
        String receiptId = "123";
        StompHeaderAccessor clientHeaderAccessor = StompHeaderAccessor.create(SEND);
        clientHeaderAccessor.setReceipt(receiptId);
        MessageHeaders clientHeaders = clientHeaderAccessor.getMessageHeaders();
        Message<byte[]> clientMessage = MessageBuilder.createMessage(new byte[0], clientHeaders);
        Message<byte[]> actual = this.handler.handleClientMessageProcessingError(clientMessage, new Exception());
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(actual, StompHeaderAccessor.class);
        Assert.assertNotNull(accessor);
        Assert.assertEquals(receiptId, accessor.getReceiptId());
    }
}

