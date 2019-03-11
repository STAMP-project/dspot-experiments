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
package org.axonframework.axonserver.connector.command;


import io.axoniq.axonserver.grpc.command.Command;
import java.util.Objects;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test all the functions provided on the {@link GrpcBackedCommandMessage}. The {@link Command} to be passed to a
 * GrpcBackedCommandMessage is created by using the {@link CommandSerializer}.
 *
 * @author Steven van Beelen
 */
public class GrpcBackedCommandMessageTest {
    private static final GrpcBackedCommandMessageTest.TestCommand TEST_COMMAND = new GrpcBackedCommandMessageTest.TestCommand("aggregateId", 42);

    private static final String ROUTING_KEY = "someRoutingKey";

    private static final int PRIORITY = 1;

    private final Serializer serializer = XStreamSerializer.defaultSerializer();

    private final CommandSerializer commandSerializer = new CommandSerializer(serializer, new AxonServerConfiguration());

    @Test
    public void testGetCommandNameReturnsTheNameOfTheCommandAsSpecifiedInTheGrpcCommand() {
        CommandMessage<GrpcBackedCommandMessageTest.TestCommand> testCommandMessage = GenericCommandMessage.asCommandMessage(GrpcBackedCommandMessageTest.TEST_COMMAND);
        Command testCommand = commandSerializer.serialize(testCommandMessage, GrpcBackedCommandMessageTest.ROUTING_KEY, GrpcBackedCommandMessageTest.PRIORITY);
        GrpcBackedCommandMessage<GrpcBackedCommandMessageTest.TestCommand> testSubject = new GrpcBackedCommandMessage(testCommand, serializer);
        Assert.assertEquals(testCommand.getName(), testSubject.getCommandName());
    }

    @Test
    public void testGetIdentifierReturnsTheSameIdentifierAsSpecifiedInTheGrpcCommand() {
        CommandMessage<GrpcBackedCommandMessageTest.TestCommand> testCommandMessage = GenericCommandMessage.asCommandMessage(GrpcBackedCommandMessageTest.TEST_COMMAND);
        Command testCommand = commandSerializer.serialize(testCommandMessage, GrpcBackedCommandMessageTest.ROUTING_KEY, GrpcBackedCommandMessageTest.PRIORITY);
        GrpcBackedCommandMessage<GrpcBackedCommandMessageTest.TestCommand> testSubject = new GrpcBackedCommandMessage(testCommand, serializer);
        Assert.assertEquals(testCommand.getMessageIdentifier(), testSubject.getIdentifier());
    }

    @Test
    public void testGetMetaDataReturnsTheSameMapAsWasInsertedInTheGrpcCommand() {
        MetaData expectedMetaData = MetaData.with("some-key", "some-value");
        CommandMessage<GrpcBackedCommandMessageTest.TestCommand> testCommandMessage = GenericCommandMessage.<GrpcBackedCommandMessageTest.TestCommand>asCommandMessage(GrpcBackedCommandMessageTest.TEST_COMMAND).withMetaData(expectedMetaData);
        Command testCommand = commandSerializer.serialize(testCommandMessage, GrpcBackedCommandMessageTest.ROUTING_KEY, GrpcBackedCommandMessageTest.PRIORITY);
        GrpcBackedCommandMessage<GrpcBackedCommandMessageTest.TestCommand> testSubject = new GrpcBackedCommandMessage(testCommand, serializer);
        Assert.assertEquals(expectedMetaData, testSubject.getMetaData());
    }

    @Test
    public void testGetPayloadReturnsAnIdenticalObjectAsInsertedThroughTheGrpcCommand() {
        GrpcBackedCommandMessageTest.TestCommand expectedCommand = GrpcBackedCommandMessageTest.TEST_COMMAND;
        CommandMessage<GrpcBackedCommandMessageTest.TestCommand> testCommandMessage = GenericCommandMessage.asCommandMessage(expectedCommand);
        Command testCommand = commandSerializer.serialize(testCommandMessage, GrpcBackedCommandMessageTest.ROUTING_KEY, GrpcBackedCommandMessageTest.PRIORITY);
        GrpcBackedCommandMessage<GrpcBackedCommandMessageTest.TestCommand> testSubject = new GrpcBackedCommandMessage(testCommand, serializer);
        Assert.assertEquals(expectedCommand, testSubject.getPayload());
    }

    @Test
    public void testGetPayloadTypeReturnsTheTypeOfTheInsertedCommand() {
        CommandMessage<GrpcBackedCommandMessageTest.TestCommand> testCommandMessage = GenericCommandMessage.asCommandMessage(GrpcBackedCommandMessageTest.TEST_COMMAND);
        Command testCommand = commandSerializer.serialize(testCommandMessage, GrpcBackedCommandMessageTest.ROUTING_KEY, GrpcBackedCommandMessageTest.PRIORITY);
        GrpcBackedCommandMessage<GrpcBackedCommandMessageTest.TestCommand> testSubject = new GrpcBackedCommandMessage(testCommand, serializer);
        Assert.assertEquals(GrpcBackedCommandMessageTest.TestCommand.class, testSubject.getPayloadType());
    }

    @Test
    public void testWithMetaDataCompletelyReplacesTheInitialMetaDataMap() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        CommandMessage<GrpcBackedCommandMessageTest.TestCommand> testCommandMessage = GenericCommandMessage.<GrpcBackedCommandMessageTest.TestCommand>asCommandMessage(GrpcBackedCommandMessageTest.TEST_COMMAND).withMetaData(testMetaData);
        Command testCommand = commandSerializer.serialize(testCommandMessage, GrpcBackedCommandMessageTest.ROUTING_KEY, GrpcBackedCommandMessageTest.PRIORITY);
        GrpcBackedCommandMessage<GrpcBackedCommandMessageTest.TestCommand> testSubject = new GrpcBackedCommandMessage(testCommand, serializer);
        MetaData replacementMetaData = MetaData.with("some-other-key", "some-other-value");
        testSubject = testSubject.withMetaData(replacementMetaData);
        MetaData resultMetaData = testSubject.getMetaData();
        Assert.assertFalse(resultMetaData.containsKey(testMetaData.keySet().iterator().next()));
        Assert.assertEquals(replacementMetaData, resultMetaData);
    }

    @Test
    public void testAndMetaDataAppendsToTheExistingMetaData() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        CommandMessage<GrpcBackedCommandMessageTest.TestCommand> testCommandMessage = GenericCommandMessage.<GrpcBackedCommandMessageTest.TestCommand>asCommandMessage(GrpcBackedCommandMessageTest.TEST_COMMAND).withMetaData(testMetaData);
        Command testCommand = commandSerializer.serialize(testCommandMessage, GrpcBackedCommandMessageTest.ROUTING_KEY, GrpcBackedCommandMessageTest.PRIORITY);
        GrpcBackedCommandMessage<GrpcBackedCommandMessageTest.TestCommand> testSubject = new GrpcBackedCommandMessage(testCommand, serializer);
        MetaData additionalMetaData = MetaData.with("some-other-key", "some-other-value");
        testSubject = testSubject.andMetaData(additionalMetaData);
        MetaData resultMetaData = testSubject.getMetaData();
        Assert.assertTrue(resultMetaData.containsKey(testMetaData.keySet().iterator().next()));
        Assert.assertTrue(resultMetaData.containsKey(additionalMetaData.keySet().iterator().next()));
    }

    private static class TestCommand {
        private final String aggregateId;

        private final int someValue;

        private TestCommand(String aggregateId, int someValue) {
            this.aggregateId = aggregateId;
            this.someValue = someValue;
        }

        @SuppressWarnings("unused")
        public String getAggregateId() {
            return aggregateId;
        }

        @SuppressWarnings("unused")
        public int getSomeValue() {
            return someValue;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            GrpcBackedCommandMessageTest.TestCommand that = ((GrpcBackedCommandMessageTest.TestCommand) (o));
            return ((someValue) == (that.someValue)) && (Objects.equals(aggregateId, that.aggregateId));
        }

        @Override
        public int hashCode() {
            return Objects.hash(aggregateId, someValue);
        }
    }
}

