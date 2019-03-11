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
package org.axonframework.test.aggregate;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.correlation.SimpleCorrelationDataProvider;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FixtureTest_CommandInterceptors {
    private static final String DISPATCH_META_DATA_KEY = "dispatchKey";

    private static final String DISPATCH_META_DATA_VALUE = "dispatchValue";

    private static final String HANDLER_META_DATA_KEY = "handlerKey";

    private static final String HANDLER_META_DATA_VALUE = "handlerValue";

    private FixtureConfiguration<FixtureTest_CommandInterceptors.InterceptorAggregate> fixture;

    @Mock
    private MessageDispatchInterceptor<CommandMessage<?>> firstMockCommandDispatchInterceptor;

    @Mock
    private MessageDispatchInterceptor<CommandMessage<?>> secondMockCommandDispatchInterceptor;

    @Mock
    private MessageHandlerInterceptor<CommandMessage<?>> mockCommandHandlerInterceptor;

    @Test
    public void testRegisteredCommandDispatchInterceptorsAreInvoked() {
        Mockito.when(firstMockCommandDispatchInterceptor.handle(ArgumentMatchers.any(CommandMessage.class))).thenAnswer(( it) -> it.getArguments()[0]);
        fixture.registerCommandDispatchInterceptor(firstMockCommandDispatchInterceptor);
        Mockito.when(secondMockCommandDispatchInterceptor.handle(ArgumentMatchers.any(CommandMessage.class))).thenAnswer(( it) -> it.getArguments()[0]);
        fixture.registerCommandDispatchInterceptor(secondMockCommandDispatchInterceptor);
        TestCommand expectedCommand = new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER);
        fixture.given(new FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(expectedCommand);
        ArgumentCaptor<GenericCommandMessage> firstCommandMessageCaptor = ArgumentCaptor.forClass(GenericCommandMessage.class);
        Mockito.verify(firstMockCommandDispatchInterceptor).handle(firstCommandMessageCaptor.capture());
        GenericCommandMessage firstResult = firstCommandMessageCaptor.getValue();
        Assert.assertEquals(expectedCommand, firstResult.getPayload());
        ArgumentCaptor<GenericCommandMessage> secondCommandMessageCaptor = ArgumentCaptor.forClass(GenericCommandMessage.class);
        Mockito.verify(secondMockCommandDispatchInterceptor).handle(secondCommandMessageCaptor.capture());
        GenericCommandMessage secondResult = secondCommandMessageCaptor.getValue();
        Assert.assertEquals(expectedCommand, secondResult.getPayload());
    }

    @Test
    public void testRegisteredCommandDispatchInterceptorIsInvokedAndAltersAppliedEvent() {
        fixture.given(new FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).expectEvents(new TestEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER, Collections.emptyMap()));
        fixture.registerCommandDispatchInterceptor(new FixtureTest_CommandInterceptors.TestCommandDispatchInterceptor());
        MetaData expectedValues = new MetaData(Collections.singletonMap(FixtureTest_CommandInterceptors.DISPATCH_META_DATA_KEY, FixtureTest_CommandInterceptors.DISPATCH_META_DATA_VALUE));
        fixture.given(new FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).expectEvents(new TestEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER, expectedValues));
    }

    @Test
    public void testRegisteredCommandDispatchInterceptorIsInvokedForFixtureMethodsGivenCommands() {
        fixture.registerCommandDispatchInterceptor(new FixtureTest_CommandInterceptors.TestCommandDispatchInterceptor());
        MetaData expectedValues = new MetaData(Collections.singletonMap(FixtureTest_CommandInterceptors.DISPATCH_META_DATA_KEY, FixtureTest_CommandInterceptors.DISPATCH_META_DATA_VALUE));
        fixture.givenCommands(new FixtureTest_CommandInterceptors.CreateStandardAggregateCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).expectEvents(new TestEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER, expectedValues));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegisteredCommandHandlerInterceptorsAreInvoked() throws Exception {
        fixture.registerCommandHandlerInterceptor(new FixtureTest_CommandInterceptors.TestCommandHandlerInterceptor());
        Mockito.when(mockCommandHandlerInterceptor.handle(ArgumentMatchers.any(UnitOfWork.class), ArgumentMatchers.any(InterceptorChain.class))).thenAnswer(InvocationOnMock::getArguments);
        fixture.registerCommandHandlerInterceptor(mockCommandHandlerInterceptor);
        TestCommand expectedCommand = new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER);
        Map<String, Object> expectedMetaDataMap = new HashMap<>();
        expectedMetaDataMap.put(FixtureTest_CommandInterceptors.HANDLER_META_DATA_KEY, FixtureTest_CommandInterceptors.HANDLER_META_DATA_VALUE);
        fixture.given(new FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(expectedCommand, expectedMetaDataMap);
        ArgumentCaptor<UnitOfWork> unitOfWorkCaptor = ArgumentCaptor.forClass(UnitOfWork.class);
        ArgumentCaptor<InterceptorChain> interceptorChainCaptor = ArgumentCaptor.forClass(InterceptorChain.class);
        Mockito.verify(mockCommandHandlerInterceptor).handle(unitOfWorkCaptor.capture(), interceptorChainCaptor.capture());
        UnitOfWork unitOfWorkResult = unitOfWorkCaptor.getValue();
        Message messageResult = unitOfWorkResult.getMessage();
        Assert.assertEquals(expectedCommand, messageResult.getPayload());
        Assert.assertEquals(expectedMetaDataMap, messageResult.getMetaData());
    }

    @Test
    public void testRegisteredCommandHandlerInterceptorIsInvokedAndAltersEvent() {
        fixture.given(new FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).expectEvents(new TestEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER, Collections.emptyMap()));
        fixture.registerCommandHandlerInterceptor(new FixtureTest_CommandInterceptors.TestCommandHandlerInterceptor());
        Map<String, Object> expectedMetaDataMap = new HashMap<>();
        expectedMetaDataMap.put(FixtureTest_CommandInterceptors.HANDLER_META_DATA_KEY, FixtureTest_CommandInterceptors.HANDLER_META_DATA_VALUE);
        fixture.given(new FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER), expectedMetaDataMap).expectEvents(new TestEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER, expectedMetaDataMap));
    }

    @Test
    public void testRegisteredCommandHandlerInterceptorIsInvokedForFixtureMethodsGivenCommands() {
        fixture.registerCommandHandlerInterceptor(new FixtureTest_CommandInterceptors.TestCommandHandlerInterceptor());
        Map<String, Object> expectedMetaDataMap = new HashMap<>();
        expectedMetaDataMap.put(FixtureTest_CommandInterceptors.HANDLER_META_DATA_KEY, FixtureTest_CommandInterceptors.HANDLER_META_DATA_VALUE);
        fixture.givenCommands(new FixtureTest_CommandInterceptors.CreateStandardAggregateCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER), expectedMetaDataMap).expectEvents(new TestEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER, expectedMetaDataMap));
    }

    @Test
    public void testRegisteredCommandDispatchAndHandlerInterceptorAreBothInvokedAndAlterEvent() {
        fixture.given(new FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).expectEvents(new TestEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER, Collections.emptyMap()));
        fixture.registerCommandDispatchInterceptor(new FixtureTest_CommandInterceptors.TestCommandDispatchInterceptor());
        fixture.registerCommandHandlerInterceptor(new FixtureTest_CommandInterceptors.TestCommandHandlerInterceptor());
        Map<String, Object> testMetaDataMap = new HashMap<>();
        testMetaDataMap.put(FixtureTest_CommandInterceptors.HANDLER_META_DATA_KEY, FixtureTest_CommandInterceptors.HANDLER_META_DATA_VALUE);
        Map<String, Object> expectedMetaDataMap = new HashMap<>(testMetaDataMap);
        expectedMetaDataMap.put(FixtureTest_CommandInterceptors.DISPATCH_META_DATA_KEY, FixtureTest_CommandInterceptors.DISPATCH_META_DATA_VALUE);
        fixture.given(new FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER)).when(new TestCommand(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER), testMetaDataMap).expectEvents(new TestEvent(FixtureTest_CommandInterceptors.InterceptorAggregate.AGGREGATE_IDENTIFIER, new MetaData(expectedMetaDataMap)));
    }

    public static class InterceptorAggregate {
        public static final String AGGREGATE_IDENTIFIER = "id1";

        @SuppressWarnings("UnusedDeclaration")
        private transient int counter;

        private Integer lastNumber;

        @AggregateIdentifier
        private String identifier;

        private MyEntity entity;

        public InterceptorAggregate() {
        }

        public InterceptorAggregate(Object aggregateIdentifier) {
            identifier = aggregateIdentifier.toString();
        }

        @CommandHandler
        public InterceptorAggregate(FixtureTest_CommandInterceptors.CreateStandardAggregateCommand cmd) {
            apply(new FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent(cmd.getAggregateIdentifier()));
        }

        @SuppressWarnings("UnusedParameters")
        @CommandHandler
        public void handle(TestCommand command, MetaData metaData) {
            apply(new TestEvent(command.getAggregateIdentifier(), metaData));
        }

        @EventHandler
        public void handle(FixtureTest_CommandInterceptors.StandardAggregateCreatedEvent event) {
            this.identifier = event.getAggregateIdentifier().toString();
        }
    }

    private static class CreateStandardAggregateCommand {
        private final Object aggregateIdentifier;

        public CreateStandardAggregateCommand(Object aggregateIdentifier) {
            this.aggregateIdentifier = aggregateIdentifier;
        }

        public Object getAggregateIdentifier() {
            return aggregateIdentifier;
        }
    }

    private static class StandardAggregateCreatedEvent {
        private final Object aggregateIdentifier;

        public StandardAggregateCreatedEvent(Object aggregateIdentifier) {
            this.aggregateIdentifier = aggregateIdentifier;
        }

        public Object getAggregateIdentifier() {
            return aggregateIdentifier;
        }
    }

    class TestCommandDispatchInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
        @Override
        public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {
            return ( index, message) -> {
                Map<String, Object> testMetaDataMap = new HashMap<>();
                testMetaDataMap.put(FixtureTest_CommandInterceptors.DISPATCH_META_DATA_KEY, FixtureTest_CommandInterceptors.DISPATCH_META_DATA_VALUE);
                message = message.andMetaData(testMetaDataMap);
                return message;
            };
        }
    }

    class TestCommandHandlerInterceptor implements MessageHandlerInterceptor<CommandMessage<?>> {
        @Override
        public Object handle(UnitOfWork<? extends CommandMessage<?>> unitOfWork, InterceptorChain interceptorChain) throws Exception {
            unitOfWork.registerCorrelationDataProvider(new SimpleCorrelationDataProvider(FixtureTest_CommandInterceptors.HANDLER_META_DATA_KEY));
            return interceptorChain.proceed();
        }
    }
}

