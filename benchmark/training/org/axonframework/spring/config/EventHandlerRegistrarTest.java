/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.spring.config;


import java.util.Arrays;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.config.ModuleConfiguration;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.core.annotation.Order;


public class EventHandlerRegistrarTest {
    private AxonConfiguration axonConfig;

    private ModuleConfiguration eventConfiguration;

    private EventProcessingConfigurer eventConfigurer;

    private EventHandlerRegistrar testSubject;

    @Test
    public void testBeansRegisteredInOrder() {
        testSubject.setEventHandlers(Arrays.asList(new EventHandlerRegistrarTest.OrderedBean(), new EventHandlerRegistrarTest.LateOrderedBean(), new EventHandlerRegistrarTest.UnorderedBean()));
        InOrder inOrder = Mockito.inOrder(eventConfigurer);
        inOrder.verify(eventConfigurer).registerEventHandler(returns(EventHandlerRegistrarTest.OrderedBean.class));
        inOrder.verify(eventConfigurer).registerEventHandler(returns(EventHandlerRegistrarTest.LateOrderedBean.class));
        inOrder.verify(eventConfigurer).registerEventHandler(returns(EventHandlerRegistrarTest.UnorderedBean.class));
    }

    public static class UnorderedBean {}

    @Order(0)
    public static class OrderedBean {}

    @Order(100)
    public static class LateOrderedBean {}
}

