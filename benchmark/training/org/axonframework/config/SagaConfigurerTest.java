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


import org.axonframework.eventhandling.ListenerInvocationErrorHandler;
import org.axonframework.modelling.saga.AnnotatedSagaManager;
import org.axonframework.modelling.saga.SagaRepository;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.inmemory.InMemorySagaStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SagaConfigurerTest {
    @Test
    public void testNullChecksOnSagaConfigurer() {
        SagaConfigurer<Object> configurer = SagaConfigurer.forType(Object.class);
        assertConfigurerNullCheck(() -> SagaConfigurer.forType(null), "Saga type should be checked for null");
        assertConfigurerNullCheck(() -> configurer.configureSagaStore(null), "Saga store builder should be checked for null");
        assertConfigurerNullCheck(() -> configurer.configureSagaManager(null), "Saga manager should be checked for null");
        assertConfigurerNullCheck(() -> configurer.configureRepository(null), "Saga repository should be checked for null");
    }

    @Test
    public void testDefaultConfiguration() {
        ListenerInvocationErrorHandler listenerInvocationErrorHandler = Mockito.mock(ListenerInvocationErrorHandler.class);
        SagaStore store = Mockito.mock(SagaStore.class);
        Configuration configuration = DefaultConfigurer.defaultConfiguration().eventProcessing(( ep) -> ep.registerSaga(.class)).registerComponent(ListenerInvocationErrorHandler.class, ( c) -> listenerInvocationErrorHandler).registerComponent(SagaStore.class, ( c) -> store).buildConfiguration();
        SagaConfiguration<Object> sagaConfiguration = configuration.eventProcessingConfiguration().sagaConfiguration(Object.class);
        Assert.assertEquals("ObjectProcessor", sagaConfiguration.processingGroup());
        Assert.assertEquals(Object.class, sagaConfiguration.type());
        Assert.assertEquals(store, sagaConfiguration.store());
        Assert.assertEquals(listenerInvocationErrorHandler, sagaConfiguration.listenerInvocationErrorHandler());
    }

    @Test
    public void testCustomConfiguration() {
        SagaStore<Object> sagaStore = new InMemorySagaStore();
        SagaRepository<Object> repository = Mockito.mock(SagaRepository.class);
        AnnotatedSagaManager<Object> manager = Mockito.mock(AnnotatedSagaManager.class);
        String processingGroup = "myProcessingGroup";
        EventProcessingModule eventProcessingModule = new EventProcessingModule();
        eventProcessingModule.registerSaga(Object.class, ( sc) -> sc.configureSagaStore(( c) -> sagaStore).configureRepository(( c) -> repository).configureSagaManager(( c) -> manager));
        eventProcessingModule.assignProcessingGroup("ObjectProcessor", processingGroup).assignHandlerTypesMatching(processingGroup, ( clazz) -> clazz.equals(.class));
        Configuration configuration = DefaultConfigurer.defaultConfiguration().registerModule(eventProcessingModule).buildConfiguration();
        SagaConfiguration<Object> sagaConfiguration = configuration.eventProcessingConfiguration().sagaConfiguration(Object.class);
        Assert.assertEquals(Object.class, sagaConfiguration.type());
        Assert.assertEquals(processingGroup, sagaConfiguration.processingGroup());
        Assert.assertEquals(manager, sagaConfiguration.manager());
        Assert.assertEquals(repository, sagaConfiguration.repository());
        Assert.assertEquals(sagaStore, sagaConfiguration.store());
    }
}

