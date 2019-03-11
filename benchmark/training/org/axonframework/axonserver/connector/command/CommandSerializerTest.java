/**
 * Copyright (c) 2018. AxonIQ
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
package org.axonframework.axonserver.connector.command;


import io.axoniq.axonserver.grpc.command.Command;
import io.axoniq.axonserver.grpc.command.CommandProviderOutbound;
import java.util.HashMap;
import java.util.Map;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Author: marc
 */
public class CommandSerializerTest {
    private final Serializer jacksonSerializer = JacksonSerializer.builder().build();

    private final AxonServerConfiguration configuration = new AxonServerConfiguration() {
        {
            setClientId("client");
            setComponentName("component");
        }
    };

    private final CommandSerializer testSubject = new CommandSerializer(jacksonSerializer, configuration);

    @Test
    public void testSerializeRequest() {
        Map<String, ?> metadata = new HashMap<String, Object>() {
            {
                this.put("firstKey", "firstValue");
                this.put("secondKey", "secondValue");
            }
        };
        CommandMessage message = new org.axonframework.commandhandling.GenericCommandMessage("payload", metadata);
        Command command = testSubject.serialize(message, "routingKey", 1);
        CommandMessage<?> deserialize = testSubject.deserialize(command);
        Assert.assertEquals(message.getIdentifier(), deserialize.getIdentifier());
        Assert.assertEquals(message.getCommandName(), deserialize.getCommandName());
        Assert.assertEquals(message.getMetaData(), deserialize.getMetaData());
        Assert.assertEquals(message.getPayloadType(), deserialize.getPayloadType());
        Assert.assertEquals(message.getPayload(), deserialize.getPayload());
    }

    @Test
    public void testSerializeResponse() {
        CommandResultMessage response = new org.axonframework.commandhandling.GenericCommandResultMessage("response", MetaData.with("test", "testValue"));
        CommandProviderOutbound outbound = testSubject.serialize(response, "requestIdentifier");
        CommandResultMessage deserialize = testSubject.deserialize(outbound.getCommandResponse());
        Assert.assertEquals(response.getPayload(), deserialize.getPayload());
        Assert.assertEquals(response.getMetaData(), deserialize.getMetaData());
        Assert.assertFalse(response.isExceptional());
        Assert.assertFalse(response.optionalExceptionResult().isPresent());
    }

    @Test
    public void testSerializeExceptionalResponse() {
        RuntimeException exception = new RuntimeException("oops");
        CommandResultMessage response = new org.axonframework.commandhandling.GenericCommandResultMessage(exception, MetaData.with("test", "testValue"));
        CommandProviderOutbound outbound = testSubject.serialize(response, "requestIdentifier");
        CommandResultMessage deserialize = testSubject.deserialize(outbound.getCommandResponse());
        Assert.assertEquals(response.getMetaData(), deserialize.getMetaData());
        Assert.assertTrue(deserialize.isExceptional());
        Assert.assertTrue(deserialize.optionalExceptionResult().isPresent());
        Assert.assertEquals(exception.getMessage(), deserialize.exceptionResult().getMessage());
    }
}

