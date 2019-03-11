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
package org.axonframework.commandhandling.distributed;


import java.io.Serializable;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.GenericCommandResultMessage;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.Serializer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests serialization/deserialization of {@link ReplyMessage}.
 *
 * @author Milan Savic
 */
@RunWith(Parameterized.class)
public class ReplyMessageSerializationTest {
    private final Serializer serializer;

    // Test name used to give sensible name to parameterized test
    @SuppressWarnings("unused")
    public ReplyMessageSerializationTest(String testName, Serializer serializer) {
        this.serializer = serializer;
    }

    @Test
    public void testSerializationDeserializationOfSuccessfulMessage() {
        String commandId = "commandId";
        CommandResultMessage<String> success = GenericCommandResultMessage.asCommandResultMessage("success");
        ReplyMessageSerializationTest.DummyReplyMessage message = new ReplyMessageSerializationTest.DummyReplyMessage(commandId, success, serializer);
        SerializedObject<byte[]> serialized = serializer.serialize(message, byte[].class);
        ReplyMessageSerializationTest.DummyReplyMessage deserialized = serializer.deserialize(serialized);
        Assert.assertEquals(message, deserialized);
    }

    @Test
    public void testSerializationDeserializationOfUnsuccessfulMessage() {
        String commandId = "commandId";
        CommandResultMessage<String> failure = GenericCommandResultMessage.asCommandResultMessage(new RuntimeException("oops"));
        ReplyMessageSerializationTest.DummyReplyMessage message = new ReplyMessageSerializationTest.DummyReplyMessage(commandId, failure, serializer);
        SerializedObject<byte[]> serialized = serializer.serialize(message, byte[].class);
        ReplyMessageSerializationTest.DummyReplyMessage deserialized = serializer.deserialize(serialized);
        Assert.assertEquals(message, deserialized);
    }

    private static class DummyReplyMessage extends ReplyMessage implements Serializable {
        private static final long serialVersionUID = -6583822511843818492L;

        public DummyReplyMessage() {
            super();
        }

        public DummyReplyMessage(String commandIdentifier, CommandResultMessage<?> commandResultMessage, Serializer serializer) {
            super(commandIdentifier, commandResultMessage, serializer);
        }
    }
}

