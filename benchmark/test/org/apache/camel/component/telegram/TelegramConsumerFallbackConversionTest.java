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
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.component.telegram.util.TelegramTestSupport;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Checks if conversions of generic objects are happening correctly.
 */
public class TelegramConsumerFallbackConversionTest extends TelegramTestSupport {
    @EndpointInject(uri = "direct:message")
    protected ProducerTemplate template;

    @Test
    public void testEverythingOk() throws Exception {
        TelegramService service = currentMockService();
        template.sendBody(new TelegramConsumerFallbackConversionTest.BrandNewType("wrapped message"));
        ArgumentCaptor<OutgoingTextMessage> captor = ArgumentCaptor.forClass(OutgoingTextMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        List<OutgoingTextMessage> msgs = captor.getAllValues();
        assertCollectionSize(msgs, 1);
        String text = msgs.get(0).getText();
        assertEquals("wrapped message", text);
    }

    private static class BrandNewType {
        String message;

        BrandNewType(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            // to use default conversion from Object to String
            return message;
        }
    }
}

