/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.telegram;


import java.util.List;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.component.telegram.util.TelegramTestSupport;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.verification.Timeout;


/**
 * Tests a chain made of a consumer and a producer to create a direct chat-bot.
 */
public class TelegramChatBotTest extends TelegramTestSupport {
    @Test
    public void testChatBotResult() throws Exception {
        TelegramService service = currentMockService();
        ArgumentCaptor<OutgoingTextMessage> captor = ArgumentCaptor.forClass(OutgoingTextMessage.class);
        Mockito.verify(service, new Timeout(5000, Mockito.times(2))).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        List<OutgoingTextMessage> msgs = captor.getAllValues();
        assertCollectionSize(msgs, 2);
        assertTrue(msgs.stream().anyMatch(( m) -> "echo from the bot: Hello World!".equals(m.getText())));
        assertTrue(msgs.stream().anyMatch(( m) -> "echo from the bot: taken".equals(m.getText())));
        assertTrue(msgs.stream().noneMatch(( m) -> (m.getParseMode()) != null));
    }
}

