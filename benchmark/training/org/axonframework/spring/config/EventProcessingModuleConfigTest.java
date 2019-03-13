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


import SpringAxonAutoConfigurer.ImportSelector;
import java.util.List;
import org.axonframework.config.EventProcessingModule;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventProcessor;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.messaging.interceptors.CorrelationDataInterceptor;
import org.axonframework.messaging.interceptors.LoggingInterceptor;
import org.axonframework.spring.stereotype.Saga;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Tests configuration of {@link EventProcessingModule}.
 *
 * @author Milan Savic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class EventProcessingModuleConfigTest {
    @Autowired
    private EventProcessingModule eventProcessingConfiguration;

    @Test
    public void testEventProcessingConfiguration() {
        Assert.assertEquals(3, eventProcessingConfiguration.eventProcessors().size());
        Assert.assertTrue(eventProcessingConfiguration.eventProcessor("processor2").isPresent());
        Assert.assertTrue(eventProcessingConfiguration.eventProcessor("subscribingProcessor").isPresent());
        EventProcessor processor2 = eventProcessingConfiguration.eventProcessorByProcessingGroup("processor1").get();
        Assert.assertEquals("processor2", processor2.getName());
        List<MessageHandlerInterceptor<? super EventMessage<?>>> interceptorsFor = eventProcessingConfiguration.interceptorsFor("processor2");
        Assert.assertEquals(2, interceptorsFor.size());
        Assert.assertTrue(interceptorsFor.stream().anyMatch(( i) -> i instanceof CorrelationDataInterceptor));
        Assert.assertTrue(interceptorsFor.stream().anyMatch(( i) -> i instanceof LoggingInterceptor));
        Assert.assertEquals("processor2", eventProcessingConfiguration.eventProcessorByProcessingGroup("processor2").get().getName());
        Assert.assertEquals("subscribingProcessor", eventProcessingConfiguration.eventProcessorByProcessingGroup("processor3").get().getName());
        Assert.assertEquals("subscribingProcessor", eventProcessingConfiguration.eventProcessorByProcessingGroup("Saga3Processor").get().getName());
        Assert.assertEquals("processor4", eventProcessingConfiguration.eventProcessorByProcessingGroup("processor4").get().getName());
    }

    @Import(ImportSelector.class)
    @Configuration
    public static class Context {
        @Bean
        public EventProcessingModule eventProcessingConfiguration() {
            EventProcessingModule config = new EventProcessingModule();
            config.assignProcessingGroup("processor1", "processor2");
            config.assignProcessingGroup(( group) -> group.contains("3") ? "subscribingProcessor" : group);
            config.registerSubscribingEventProcessor("subscribingProcessor");
            config.registerDefaultHandlerInterceptor(( configuration, name) -> new LoggingInterceptor<>());
            return config;
        }

        @Saga
        @ProcessingGroup("processor1")
        public static class Saga1 {}

        @Saga
        @ProcessingGroup("processor2")
        public static class Saga2 {}

        @Saga
        public static class Saga3 {}

        @Component
        @ProcessingGroup("processor3")
        public static class EventHandler1 {
            @EventHandler
            public void on(String evt) {
                // nothing to do
            }
        }

        @Saga
        @ProcessingGroup("processor4")
        public static class Saga4 {}

        @Saga
        @ProcessingGroup("processor4")
        public static class Saga5 {}
    }
}

