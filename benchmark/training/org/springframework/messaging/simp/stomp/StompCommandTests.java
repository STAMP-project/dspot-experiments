/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.messaging.simp.stomp;


import SimpMessageType.CONNECT;
import StompCommand.DISCONNECT;
import StompCommand.ERROR;
import StompCommand.MESSAGE;
import StompCommand.SEND;
import StompCommand.STOMP;
import StompCommand.SUBSCRIBE;
import StompCommand.UNSUBSCRIBE;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessageType;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class StompCommandTests {
    private static final Collection<StompCommand> destinationRequired = Arrays.asList(SEND, SUBSCRIBE, MESSAGE);

    private static final Collection<StompCommand> subscriptionIdRequired = Arrays.asList(SUBSCRIBE, UNSUBSCRIBE, MESSAGE);

    private static final Collection<StompCommand> contentLengthRequired = Arrays.asList(SEND, MESSAGE, ERROR);

    private static final Collection<StompCommand> bodyAllowed = Arrays.asList(SEND, MESSAGE, ERROR);

    private static final Map<StompCommand, SimpMessageType> messageTypes = new java.util.EnumMap(StompCommand.class);

    static {
        StompCommandTests.messageTypes.put(STOMP, CONNECT);
        StompCommandTests.messageTypes.put(StompCommand.CONNECT, CONNECT);
        StompCommandTests.messageTypes.put(DISCONNECT, SimpMessageType.DISCONNECT);
        StompCommandTests.messageTypes.put(SUBSCRIBE, SimpMessageType.SUBSCRIBE);
        StompCommandTests.messageTypes.put(UNSUBSCRIBE, SimpMessageType.UNSUBSCRIBE);
        StompCommandTests.messageTypes.put(SEND, SimpMessageType.MESSAGE);
        StompCommandTests.messageTypes.put(MESSAGE, SimpMessageType.MESSAGE);
    }

    @Test
    public void getMessageType() throws Exception {
        for (StompCommand stompCommand : StompCommand.values()) {
            SimpMessageType simp = StompCommandTests.messageTypes.get(stompCommand);
            if (simp == null) {
                simp = SimpMessageType.OTHER;
            }
            Assert.assertSame(simp, stompCommand.getMessageType());
        }
    }

    @Test
    public void requiresDestination() throws Exception {
        for (StompCommand stompCommand : StompCommand.values()) {
            Assert.assertEquals(StompCommandTests.destinationRequired.contains(stompCommand), stompCommand.requiresDestination());
        }
    }

    @Test
    public void requiresSubscriptionId() throws Exception {
        for (StompCommand stompCommand : StompCommand.values()) {
            Assert.assertEquals(StompCommandTests.subscriptionIdRequired.contains(stompCommand), stompCommand.requiresSubscriptionId());
        }
    }

    @Test
    public void requiresContentLength() throws Exception {
        for (StompCommand stompCommand : StompCommand.values()) {
            Assert.assertEquals(StompCommandTests.contentLengthRequired.contains(stompCommand), stompCommand.requiresContentLength());
        }
    }

    @Test
    public void isBodyAllowed() throws Exception {
        for (StompCommand stompCommand : StompCommand.values()) {
            Assert.assertEquals(StompCommandTests.bodyAllowed.contains(stompCommand), stompCommand.isBodyAllowed());
        }
    }
}

