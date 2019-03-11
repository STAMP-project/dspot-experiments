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


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.common.AxonConfigurationException;
import org.axonframework.common.jpa.SimpleEntityManagerProvider;
import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.utils.AssertUtils;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.async.FullConcurrencyPolicy;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.GenericJpaRepository;
import org.axonframework.modelling.command.VersionedAggregateIdentifier;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.queryhandling.SimpleQueryUpdateEmitter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class DefaultConfigurerTest {
    private EntityManager em;

    @Test
    public void defaultConfigurationWithEventSourcing() throws Exception {
        Configuration config = DefaultConfigurer.defaultConfiguration().configureEmbeddedEventStore(( c) -> new InMemoryEventStorageEngine()).configureCommandBus(( c) -> AsynchronousCommandBus.builder().build()).configureAggregate(DefaultConfigurerTest.StubAggregate.class).buildConfiguration();
        config.start();
        FutureCallback<Object, Object> callback = new FutureCallback();
        config.commandBus().dispatch(GenericCommandMessage.asCommandMessage("test"), callback);
        Assert.assertEquals("test", callback.get().getPayload());
        Assert.assertNotNull(config.repository(DefaultConfigurerTest.StubAggregate.class));
        Assert.assertEquals(1, config.getModules().size());
        ConfigAssertions.assertExpectedModules(config, AggregateConfiguration.class);
    }

    @Test
    public void defaultConfigurationWithTrackingProcessorConfigurationInMainConfig() {
        Configurer configurer = DefaultConfigurer.defaultConfiguration();
        configurer.eventProcessing().registerEventHandler(( c) -> ((EventMessageHandler) (( event) -> null)));
        Configuration config = configurer.registerComponent(TrackingEventProcessorConfiguration.class, ( c) -> TrackingEventProcessorConfiguration.forParallelProcessing(2)).configureEmbeddedEventStore(( c) -> new InMemoryEventStorageEngine()).start();
        try {
            TrackingEventProcessor processor = config.eventProcessingConfiguration().eventProcessor(getClass().getPackage().getName(), TrackingEventProcessor.class).orElseThrow(RuntimeException::new);
            AssertUtils.assertWithin(5, TimeUnit.SECONDS, () -> Assert.assertEquals(2, config.getComponent(TokenStore.class).fetchSegments(processor.getName()).length));
        } finally {
            config.shutdown();
        }
    }

    @Test
    public void defaultConfigurationWithTrackingProcessorExplicitlyConfigured() {
        Configurer configurer = DefaultConfigurer.defaultConfiguration();
        String processorName = "myProcessor";
        configurer.eventProcessing().registerTrackingEventProcessor(processorName, Configuration::eventStore, ( c) -> TrackingEventProcessorConfiguration.forParallelProcessing(2)).byDefaultAssignTo(processorName).registerDefaultSequencingPolicy(( c) -> new FullConcurrencyPolicy()).registerEventHandler(( c) -> ((EventMessageHandler) (( event) -> null)));
        Configuration config = configurer.configureEmbeddedEventStore(( c) -> new InMemoryEventStorageEngine()).start();
        try {
            TrackingEventProcessor processor = config.eventProcessingConfiguration().eventProcessor(processorName, TrackingEventProcessor.class).orElseThrow(RuntimeException::new);
            AssertUtils.assertWithin(5, TimeUnit.SECONDS, () -> Assert.assertEquals(2, config.getComponent(TokenStore.class).fetchSegments(processor.getName()).length));
        } finally {
            config.shutdown();
        }
    }

    @Test
    public void defaultConfigurationWithUpcaster() {
        AtomicInteger counter = new AtomicInteger();
        Configuration config = DefaultConfigurer.defaultConfiguration().configureEmbeddedEventStore(( c) -> JpaEventStorageEngine.builder().snapshotSerializer(c.serializer()).upcasterChain(c.upcasterChain()).persistenceExceptionResolver(c.getComponent(.class)).entityManagerProvider(() -> em).transactionManager(c.getComponent(.class)).build()).configureAggregate(AggregateConfigurer.defaultConfiguration(DefaultConfigurerTest.StubAggregate.class).configureCommandTargetResolver(( c) -> ( command) -> new VersionedAggregateIdentifier(command.getPayload().toString(), null))).registerEventUpcaster(( c) -> ( events) -> {
            counter.incrementAndGet();
            return events;
        }).configureTransactionManager(( c) -> new org.axonframework.config.EntityManagerTransactionManager(em)).buildConfiguration();
        config.start();
        config.commandGateway().sendAndWait(GenericCommandMessage.asCommandMessage("test"));
        config.commandGateway().sendAndWait(new GenericCommandMessage(new org.axonframework.messaging.GenericMessage("test"), "update"));
        Assert.assertEquals(1, counter.get());
        Assert.assertNotNull(config.repository(DefaultConfigurerTest.StubAggregate.class));
    }

    @Test
    public void testJpaConfigurationWithInitialTransactionManagerJpaRepository() throws Exception {
        DefaultConfigurerTest.EntityManagerTransactionManager transactionManager = Mockito.spy(new DefaultConfigurerTest.EntityManagerTransactionManager(em));
        Configuration config = DefaultConfigurer.jpaConfiguration(() -> em, transactionManager).configureCommandBus(( c) -> {
            AsynchronousCommandBus commandBus = AsynchronousCommandBus.builder().build();
            commandBus.registerHandlerInterceptor(new TransactionManagingInterceptor<>(c.getComponent(.class)));
            return commandBus;
        }).configureAggregate(AggregateConfigurer.defaultConfiguration(DefaultConfigurerTest.StubAggregate.class).configureRepository(( c) -> GenericJpaRepository.builder(.class).entityManagerProvider(new SimpleEntityManagerProvider(em)).eventBus(c.eventBus()).parameterResolverFactory(c.parameterResolverFactory()).build())).buildConfiguration();
        config.start();
        FutureCallback<Object, Object> callback = new FutureCallback();
        config.commandBus().dispatch(GenericCommandMessage.asCommandMessage("test"), callback);
        Assert.assertEquals("test", callback.get().getPayload());
        Assert.assertNotNull(config.repository(DefaultConfigurerTest.StubAggregate.class));
        Assert.assertEquals(1, config.getModules().size());
        ConfigAssertions.assertExpectedModules(config, AggregateConfiguration.class);
        Mockito.verify(transactionManager).startTransaction();
    }

    @Test
    public void testJpaConfigurationWithInitialTransactionManagerJpaRepositoryFromConfiguration() throws Exception {
        DefaultConfigurerTest.EntityManagerTransactionManager transactionManager = Mockito.spy(new DefaultConfigurerTest.EntityManagerTransactionManager(em));
        Configuration config = DefaultConfigurer.jpaConfiguration(() -> em, transactionManager).configureCommandBus(( c) -> {
            AsynchronousCommandBus commandBus = AsynchronousCommandBus.builder().build();
            commandBus.registerHandlerInterceptor(new TransactionManagingInterceptor<>(c.getComponent(.class)));
            return commandBus;
        }).configureAggregate(AggregateConfigurer.jpaMappedConfiguration(DefaultConfigurerTest.StubAggregate.class)).buildConfiguration();
        config.start();
        FutureCallback<Object, Object> callback = new FutureCallback();
        config.commandBus().dispatch(GenericCommandMessage.asCommandMessage("test"), callback);
        Assert.assertEquals("test", callback.get().getPayload());
        Assert.assertNotNull(config.repository(DefaultConfigurerTest.StubAggregate.class));
        Assert.assertTrue(config.getModules().stream().anyMatch(( m) -> m instanceof AggregateConfiguration));
        Mockito.verify(transactionManager).startTransaction();
    }

    @Test
    public void testMissingEntityManagerProviderIsReported() {
        Configuration config = DefaultConfigurer.defaultConfiguration().configureCommandBus(( c) -> {
            AsynchronousCommandBus commandBus = AsynchronousCommandBus.builder().build();
            commandBus.registerHandlerInterceptor(new TransactionManagingInterceptor<>(c.getComponent(.class)));
            return commandBus;
        }).configureAggregate(AggregateConfigurer.jpaMappedConfiguration(DefaultConfigurerTest.StubAggregate.class)).buildConfiguration();
        try {
            config.start();
            Assert.fail("Expected AxonConfigurationException");
        } catch (AxonConfigurationException e) {
            // expected
        }
    }

    @Test
    public void testJpaConfigurationWithJpaRepository() throws Exception {
        DefaultConfigurerTest.EntityManagerTransactionManager transactionManager = Mockito.spy(new DefaultConfigurerTest.EntityManagerTransactionManager(em));
        Configuration config = DefaultConfigurer.jpaConfiguration(() -> em).registerComponent(TransactionManager.class, ( c) -> transactionManager).configureCommandBus(( c) -> {
            AsynchronousCommandBus commandBus = AsynchronousCommandBus.builder().build();
            commandBus.registerHandlerInterceptor(new TransactionManagingInterceptor<>(c.getComponent(.class)));
            return commandBus;
        }).configureAggregate(AggregateConfigurer.defaultConfiguration(DefaultConfigurerTest.StubAggregate.class).configureRepository(( c) -> GenericJpaRepository.builder(.class).entityManagerProvider(new SimpleEntityManagerProvider(em)).eventBus(c.eventBus()).parameterResolverFactory(c.parameterResolverFactory()).build())).buildConfiguration();
        config.start();
        FutureCallback<Object, Object> callback = new FutureCallback();
        config.commandBus().dispatch(GenericCommandMessage.asCommandMessage("test"), callback);
        Assert.assertEquals("test", callback.get().getPayload());
        Assert.assertNotNull(config.repository(DefaultConfigurerTest.StubAggregate.class));
        Assert.assertEquals(1, config.getModules().size());
        ConfigAssertions.assertExpectedModules(config, AggregateConfiguration.class);
        Mockito.verify(transactionManager).startTransaction();
    }

    @Test
    public void defaultConfigurationWithMonitors() throws Exception {
        MessageCollectingMonitor defaultMonitor = new MessageCollectingMonitor();
        MessageCollectingMonitor commandBusMonitor = new MessageCollectingMonitor();
        Configuration config = DefaultConfigurer.defaultConfiguration().configureEmbeddedEventStore(( c) -> new InMemoryEventStorageEngine()).configureAggregate(DefaultConfigurerTest.StubAggregate.class).configureMessageMonitor(( c) -> ( t, n) -> defaultMonitor).configureMessageMonitor(CommandBus.class, "commandBus", ( c) -> commandBusMonitor).buildConfiguration();
        config.start();
        FutureCallback<Object, Object> callback = new FutureCallback();
        config.commandBus().dispatch(GenericCommandMessage.asCommandMessage("test"), callback);
        Assert.assertEquals("test", callback.get().getPayload());
        Assert.assertEquals(1, defaultMonitor.getMessages().size());
        Assert.assertEquals(1, commandBusMonitor.getMessages().size());
    }

    @Test
    public void testRegisterSeveralModules() {
        Configuration config = DefaultConfigurer.defaultConfiguration().configureAggregate(DefaultConfigurerTest.StubAggregate.class).configureAggregate(Object.class).configureEmbeddedEventStore(( c) -> new InMemoryEventStorageEngine()).start();
        Assert.assertThat(config.getModules().size(), CoreMatchers.is(2));
        ConfigAssertions.assertExpectedModules(config, AggregateConfiguration.class, AggregateConfiguration.class);
    }

    @Test
    public void testModuleHandlersOrdering() {
        ModuleConfiguration module1 = Mockito.mock(ModuleConfiguration.class);
        ModuleConfiguration module2 = Mockito.mock(ModuleConfiguration.class);
        ModuleConfiguration module3 = Mockito.mock(ModuleConfiguration.class);
        Mockito.when(module1.phase()).thenReturn(2);
        Mockito.when(module2.phase()).thenReturn(3);
        Mockito.when(module3.phase()).thenReturn(1);
        Configuration configuration = DefaultConfigurer.defaultConfiguration().registerModule(module1).registerModule(module2).registerModule(module3).start();
        Assert.assertNotNull(configuration);
        configuration.shutdown();
        InOrder inOrder = Mockito.inOrder(module1, module2, module3);
        inOrder.verify(module3).initialize(configuration);
        inOrder.verify(module1).initialize(configuration);
        inOrder.verify(module2).initialize(configuration);
        inOrder.verify(module3).start();
        inOrder.verify(module1).start();
        inOrder.verify(module2).start();
        inOrder.verify(module2).shutdown();
        inOrder.verify(module1).shutdown();
        inOrder.verify(module3).shutdown();
    }

    @Test
    public void testModuleHandlersOrderingAfterConfigIsInitialized() {
        ModuleConfiguration module1 = Mockito.mock(ModuleConfiguration.class);
        ModuleConfiguration module2 = Mockito.mock(ModuleConfiguration.class);
        ModuleConfiguration module3 = Mockito.mock(ModuleConfiguration.class);
        Mockito.when(module1.phase()).thenReturn(2);
        Mockito.when(module2.phase()).thenReturn(3);
        Mockito.when(module3.phase()).thenReturn(1);
        Configurer configurer = DefaultConfigurer.defaultConfiguration();
        Configuration configuration = configurer.buildConfiguration();
        configurer.registerModule(module1).registerModule(module2).registerModule(module3);
        configuration.start();
        Assert.assertNotNull(configuration);
        configuration.shutdown();
        InOrder inOrder = Mockito.inOrder(module1, module2, module3);
        inOrder.verify(module3).start();
        inOrder.verify(module1).start();
        inOrder.verify(module2).start();
        inOrder.verify(module2).shutdown();
        inOrder.verify(module1).shutdown();
        inOrder.verify(module3).shutdown();
    }

    @Test
    public void testQueryUpdateEmitterConfigurationPropagatedToTheQueryBus() {
        QueryUpdateEmitter queryUpdateEmitter = SimpleQueryUpdateEmitter.builder().build();
        Configuration configuration = DefaultConfigurer.defaultConfiguration().configureQueryUpdateEmitter(( c) -> queryUpdateEmitter).buildConfiguration();
        Assert.assertEquals(queryUpdateEmitter, configuration.queryBus().queryUpdateEmitter());
        Assert.assertEquals(queryUpdateEmitter, configuration.queryUpdateEmitter());
    }

    @Entity(name = "StubAggregate")
    private static class StubAggregate {
        @Id
        @AggregateIdentifier
        private String id;

        public StubAggregate() {
        }

        @CommandHandler
        public StubAggregate(String command, CommandBus commandBus) {
            apply(command);
        }

        @CommandHandler(commandName = "update")
        public void update(String command) {
            apply(1L);
        }

        @EventSourcingHandler
        protected void on(String event) {
            this.id = event;
        }
    }

    private static class EntityManagerTransactionManager implements TransactionManager {
        private final EntityManager em;

        public EntityManagerTransactionManager(EntityManager em) {
            this.em = em;
        }

        @Override
        public Transaction startTransaction() {
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                return new Transaction() {
                    @Override
                    public void commit() {
                    }

                    @Override
                    public void rollback() {
                    }
                };
            }
            tx.begin();
            return new Transaction() {
                @Override
                public void commit() {
                    tx.commit();
                }

                @Override
                public void rollback() {
                    tx.rollback();
                }
            };
        }
    }
}

