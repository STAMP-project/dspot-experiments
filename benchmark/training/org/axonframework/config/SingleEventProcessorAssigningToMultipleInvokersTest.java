/**
 * Copyright (c) 2010-2018. Axon Framework
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
package org.axonframework.config;


import org.axonframework.eventhandling.EventProcessor;
import org.axonframework.eventhandling.SubscribingEventProcessor;
import org.axonframework.modelling.saga.repository.inmemory.InMemorySagaStore;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing functionality of assigning different invokers (saga or event handlers) to the same event processor.
 *
 * @author Milan Savic
 */
public class SingleEventProcessorAssigningToMultipleInvokersTest {
    @Test
    public void testMultipleAssignmentsToTrackingProcessor() {
        Configurer configurer = DefaultConfigurer.defaultConfiguration();
        configurer.eventProcessing().registerEventHandler(( config) -> new org.axonframework.config.EventHandler1()).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class, ( sc) -> sc.configureSagaStore(( c) -> new InMemorySagaStore())).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class);
        Configuration configuration = configurer.buildConfiguration();
        EventProcessor saga1Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class).orElse(null);
        EventProcessor saga2Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).orElse(null);
        EventProcessor saga3Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class).orElse(null);
        Assert.assertNotNull(saga1Processor);
        Assert.assertNotNull(saga2Processor);
        Assert.assertNotNull(saga3Processor);
        Assert.assertNotNull(configuration.eventProcessingConfiguration().eventProcessor("processor1").get());
        Assert.assertEquals(saga1Processor, saga2Processor);
        Assert.assertEquals(saga1Processor, configuration.eventProcessingConfiguration().eventProcessor("processor1").get());
        Assert.assertNotEquals(saga1Processor, saga3Processor);
        Assert.assertNotEquals(saga2Processor, saga3Processor);
        Assert.assertNotEquals(saga3Processor, configuration.eventProcessingConfiguration().eventProcessor("processor1").get());
    }

    @Test
    public void testMultipleAssignmentsToSubscribingProcessor() {
        Configurer configurer = DefaultConfigurer.defaultConfiguration();
        configurer.eventProcessing().usingSubscribingEventProcessors().registerEventHandler(( config) -> new org.axonframework.config.EventHandler1()).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class);
        Configuration configuration = configurer.buildConfiguration();
        EventProcessor saga1Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class).orElse(null);
        EventProcessor saga2Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).orElse(null);
        EventProcessor saga3Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class).orElse(null);
        Assert.assertNotNull(saga1Processor);
        Assert.assertNotNull(saga2Processor);
        Assert.assertNotNull(saga3Processor);
        Assert.assertNotNull(configuration.eventProcessingConfiguration().eventProcessor("processor1").get());
        Assert.assertEquals(saga1Processor, saga2Processor);
        Assert.assertEquals(saga1Processor, configuration.eventProcessingConfiguration().eventProcessor("processor1").get());
        Assert.assertNotEquals(saga1Processor, saga3Processor);
        Assert.assertNotEquals(saga2Processor, saga3Processor);
        Assert.assertNotEquals(configuration.eventProcessingConfiguration().eventProcessor("processor1").get(), saga3Processor);
    }

    @Test
    public void testMultipleAssignmentsWithProvidedProcessorName() {
        Configurer configurer = DefaultConfigurer.defaultConfiguration();
        configurer.eventProcessing().assignHandlerTypesMatching("processor1", ( clazz) -> clazz.equals(.class)).assignHandlerTypesMatching("processor1", ( clazz) -> clazz.equals(.class)).assignHandlerTypesMatching("someOtherProcessor", ( clazz) -> clazz.equals(.class)).assignProcessingGroup("processor1", "myProcessor").registerEventHandler(( config) -> new org.axonframework.config.EventHandler1()).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class).registerEventProcessor("myProcessor", ( name, conf, eventHandlerInvoker) -> SubscribingEventProcessor.builder().name(name).eventHandlerInvoker(eventHandlerInvoker).messageSource(conf.eventBus()).build());
        Configuration configuration = configurer.buildConfiguration();
        EventProcessor saga1Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class).orElse(null);
        EventProcessor saga2Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).orElse(null);
        EventProcessor saga3Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class).orElse(null);
        Assert.assertNotNull(saga1Processor);
        Assert.assertNotNull(saga2Processor);
        Assert.assertNotNull(saga3Processor);
        Assert.assertNotNull(configuration.eventProcessingConfiguration().eventProcessor("myProcessor").get());
        Assert.assertEquals(saga1Processor, saga3Processor);
        Assert.assertEquals(saga2Processor, configuration.eventProcessingConfiguration().eventProcessor("someOtherProcessor").get());
        Assert.assertNotEquals(saga2Processor, saga3Processor);
        Assert.assertEquals(saga3Processor, configuration.eventProcessingConfiguration().eventProcessor("myProcessor").get());
        Assert.assertNotEquals(saga3Processor, configuration.eventProcessingConfiguration().eventProcessor("someOtherProcessor").get());
    }

    @Test
    public void testProcessorGroupAssignment() {
        Configurer configurer = DefaultConfigurer.defaultConfiguration();
        configurer.eventProcessing().registerEventProcessor("myProcessor", ( name, conf, eventHandlerInvoker) -> SubscribingEventProcessor.builder().name(name).eventHandlerInvoker(eventHandlerInvoker).messageSource(conf.eventBus()).build()).assignProcessingGroup("processor1", "myProcessor").registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class);
        Configuration configuration = configurer.buildConfiguration();
        EventProcessor saga1Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class).orElse(null);
        EventProcessor saga2Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).orElse(null);
        EventProcessor saga3Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class).orElse(null);
        Assert.assertEquals("myProcessor", saga1Processor.getName());
        Assert.assertEquals("myProcessor", saga2Processor.getName());
        Assert.assertEquals("Saga3Processor", saga3Processor.getName());
    }

    @Test
    public void testProcessorGroupAssignmentByRule() {
        Configurer configurer = DefaultConfigurer.defaultConfiguration();
        configurer.eventProcessing().assignHandlerTypesMatching("myProcessor", ( clazz) -> clazz.equals(.class)).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).registerSaga(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class).registerEventProcessor("myProcessor", ( name, conf, eventHandlerInvoker) -> SubscribingEventProcessor.builder().name(name).eventHandlerInvoker(eventHandlerInvoker).messageSource(conf.eventBus()).build()).assignProcessingGroup(( group) -> "myProcessor");
        Configuration configuration = configurer.buildConfiguration();
        EventProcessor saga1Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga1.class).orElse(null);
        EventProcessor saga2Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga2.class).orElse(null);
        EventProcessor saga3Processor = configuration.eventProcessingConfiguration().sagaEventProcessor(SingleEventProcessorAssigningToMultipleInvokersTest.Saga3.class).orElse(null);
        Assert.assertEquals("myProcessor", saga1Processor.getName());
        Assert.assertEquals("myProcessor", saga2Processor.getName());
        Assert.assertEquals("myProcessor", saga3Processor.getName());
    }

    @ProcessingGroup("processor1")
    private static class Saga1 {}

    @ProcessingGroup("processor1")
    private static class Saga2 {}

    private static class Saga3 {}

    @ProcessingGroup("processor1")
    private static class EventHandler1 {}
}

