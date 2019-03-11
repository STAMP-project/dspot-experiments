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
package org.axonframework.integrationtests.loopbacktest.synchronous;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;
import org.axonframework.eventhandling.SequenceNumber;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.command.Aggregate;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.modelling.command.AggregateRoot;
import org.axonframework.modelling.command.Repository;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Gerard de Leeuw
 * @author Allard Buijze
 */
public class LoopBackWithInterweavedCommandsAndEventsTest {
    private final String aggregateIdentifier = "Aggregate";

    private LoopBackWithInterweavedCommandsAndEventsTest.MyCommand command;

    private Configuration configuration;

    @Test
    public void orderInCommandHandlerAggregate() {
        LoopBackWithInterweavedCommandsAndEventsTest.MyAggregate commandHandlerAggregate = configuration.commandGateway().sendAndWait(command);
        Assert.assertEquals(expectedDescriptions(command), commandHandlerAggregate.getHandledCommands());
    }

    @Test
    public void orderInEventSourcedAggregate() {
        Repository<LoopBackWithInterweavedCommandsAndEventsTest.MyAggregate> repository = configuration.repository(LoopBackWithInterweavedCommandsAndEventsTest.MyAggregate.class);
        configuration.commandGateway().sendAndWait(command);
        UnitOfWork unitOfWork = DefaultUnitOfWork.startAndGet(GenericCommandMessage.asCommandMessage("loading"));
        LoopBackWithInterweavedCommandsAndEventsTest.MyAggregate loadedAggregate = repository.load(aggregateIdentifier).invoke(Function.identity());
        unitOfWork.commit();
        Assert.assertEquals(expectedDescriptions(command), loadedAggregate.getHandledCommands());
    }

    @Test
    public void orderInEventStore() {
        configuration.commandGateway().sendAndWait(command);
        Assert.assertEquals(expectedDescriptions(command), configuration.eventStore().readEvents(aggregateIdentifier).asStream().map(Message::getPayload).map(LoopBackWithInterweavedCommandsAndEventsTest.MyEvent.class::cast).map(LoopBackWithInterweavedCommandsAndEventsTest.MyEvent::getDescription).collect(Collectors.toList()));
    }

    /**
     *
     *
     * @author Gerard de Leeuw
     * @since 0.1.0 on 4-1-2017
     */
    @AggregateRoot
    public static class MyAggregate {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final List<String> handledCommands;

        @AggregateIdentifier
        private String aggregateIdentifier;

        public MyAggregate(String aggregateIdentifier) {
            this.aggregateIdentifier = aggregateIdentifier;
            this.handledCommands = new ArrayList<>();
        }

        public void handle(LoopBackWithInterweavedCommandsAndEventsTest.MyCommand command, CommandGateway commandGateway) {
            apply(new LoopBackWithInterweavedCommandsAndEventsTest.MyEvent(aggregateIdentifier, command.startDescription()));
            if ((command.getInnerCommand()) != null) {
                commandGateway.sendAndWait(command.getInnerCommand());
            }
            apply(new LoopBackWithInterweavedCommandsAndEventsTest.MyEvent(aggregateIdentifier, command.doneDescription()));
        }

        @EventSourcingHandler
        public void handle(LoopBackWithInterweavedCommandsAndEventsTest.MyEvent event, @SequenceNumber
        long sequenceNumber) {
            this.aggregateIdentifier = event.getAggregateIdentifier();
            logger.info(String.format("Event sourcing event: aggregateIdentifier = %s, sequenceNumber = %d, payload = %s", aggregateIdentifier, sequenceNumber, event));
            handledCommands.add(event.getDescription());
        }

        public List<String> getHandledCommands() {
            return handledCommands;
        }
    }

    /**
     *
     *
     * @author Gerard de Leeuw
     * @since 0.1.0 on 4-1-2017
     */
    public static class MyCommand {
        private final String name;

        private final String aggregateIdentifier;

        private final LoopBackWithInterweavedCommandsAndEventsTest.MyCommand innerCommand;

        public MyCommand(String name, String aggregateIdentifier) {
            this(name, aggregateIdentifier, null);
        }

        public MyCommand(String name, String aggregateIdentifier, LoopBackWithInterweavedCommandsAndEventsTest.MyCommand innerCommand) {
            this.name = name;
            this.aggregateIdentifier = aggregateIdentifier;
            this.innerCommand = innerCommand;
        }

        public String getName() {
            return name;
        }

        public String getAggregateIdentifier() {
            return aggregateIdentifier;
        }

        public LoopBackWithInterweavedCommandsAndEventsTest.MyCommand getInnerCommand() {
            return innerCommand;
        }

        public String startDescription() {
            return "Start handling command " + (name);
        }

        public String doneDescription() {
            return "Done handling command " + (name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     *
     *
     * @author Gerard de Leeuw
     * @since 0.1.0 on 4-1-2017
     */
    public static class MyCommandHandler {
        private final Repository<LoopBackWithInterweavedCommandsAndEventsTest.MyAggregate> repository;

        private final CommandGateway commandGateway;

        public MyCommandHandler(Repository<LoopBackWithInterweavedCommandsAndEventsTest.MyAggregate> repository, CommandGateway commandGateway) {
            this.repository = repository;
            this.commandGateway = commandGateway;
        }

        @CommandHandler
        public LoopBackWithInterweavedCommandsAndEventsTest.MyAggregate handle(LoopBackWithInterweavedCommandsAndEventsTest.MyCommand command) throws Exception {
            Aggregate<LoopBackWithInterweavedCommandsAndEventsTest.MyAggregate> aggregate;
            try {
                aggregate = repository.load(command.getAggregateIdentifier());
            } catch (AggregateNotFoundException e) {
                aggregate = repository.newInstance(() -> new org.axonframework.integrationtests.loopbacktest.synchronous.MyAggregate(command.getAggregateIdentifier()));
            }
            aggregate.execute(( a) -> a.handle(command, commandGateway));
            return aggregate.invoke(Function.identity());
        }
    }

    /**
     *
     *
     * @author Gerard de Leeuw
     * @since 0.1.0 on 4-1-2017
     */
    public static class MyEvent {
        private final String aggregateIdentifier;

        private final String description;

        public MyEvent(String aggregateIdentifier, String description) {
            this.aggregateIdentifier = aggregateIdentifier;
            this.description = description;
        }

        public String getAggregateIdentifier() {
            return aggregateIdentifier;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}

