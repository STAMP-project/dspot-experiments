/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling.gateway;


import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Bert Laverman
 */
public class DefaultEventGatewayTest {
    private DefaultEventGateway testSubject;

    private EventBus mockEventBus;

    private MessageDispatchInterceptor mockEventMessageTransformer;

    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public void testPublish() {
        testSubject.publish("Event1");
        Mockito.verify(mockEventBus).publish(ArgumentMatchers.argThat((GenericEventMessage msg) -> msg.getPayload().equals("Event1")));
        Mockito.verify(mockEventMessageTransformer).handle(ArgumentMatchers.argThat((GenericEventMessage msg) -> msg.getPayload().equals("Event1")));
        testSubject.publish("Event2", "Event3");
        Mockito.verify(mockEventBus).publish(ArgumentMatchers.argThat((GenericEventMessage msg) -> msg.getPayload().equals("Event2")));
        Mockito.verify(mockEventMessageTransformer).handle(ArgumentMatchers.argThat((GenericEventMessage msg) -> msg.getPayload().equals("Event2")));
        Mockito.verify(mockEventBus).publish(ArgumentMatchers.argThat((GenericEventMessage msg) -> msg.getPayload().equals("Event3")));
        Mockito.verify(mockEventMessageTransformer).handle(ArgumentMatchers.argThat((GenericEventMessage msg) -> msg.getPayload().equals("Event3")));
    }
}

