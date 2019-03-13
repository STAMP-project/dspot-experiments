/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.server.util;


import TCommandType.THREAD_DUMP_RESPONSE;
import com.navercorp.pinpoint.io.util.TypeLocator;
import com.navercorp.pinpoint.thrift.dto.command.TCommandThreadDumpResponse;
import com.navercorp.pinpoint.thrift.io.DeserializerFactory;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseDeserializer;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseSerializerFactory;
import com.navercorp.pinpoint.thrift.io.SerializerFactory;
import com.navercorp.pinpoint.thrift.io.TCommandRegistry;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import org.apache.thrift.TBase;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class AgentEventMessageSerDesTest {
    private final TProtocolFactory protocolFactory = new TCompactProtocol.Factory();

    private final TypeLocator<TBase<?, ?>> commandTbaseRegistry = TCommandRegistry.build(Collections.singletonList(THREAD_DUMP_RESPONSE));

    private final SerializerFactory serializerFactory = new HeaderTBaseSerializerFactory(true, HeaderTBaseSerializerFactory.DEFAULT_STREAM_SIZE, true, this.protocolFactory, this.commandTbaseRegistry);

    private final DeserializerFactory<HeaderTBaseDeserializer> deserializerFactory = new com.navercorp.pinpoint.thrift.io.HeaderTBaseDeserializerFactory(this.protocolFactory, this.commandTbaseRegistry);

    private final AgentEventMessageSerializer serializer = new AgentEventMessageSerializer(Collections.singletonList(serializerFactory));

    private final AgentEventMessageDeserializer deserializer = new AgentEventMessageDeserializer(deserializerFactory);

    @Test
    public void Void_event_messages_should_serialized_and_deserialize_into_null() throws UnsupportedEncodingException {
        final Class<Void> messageTypeToTest = Void.class;
        final Object expectedEventMessage = null;
        verifyEventMessageSerDer(messageTypeToTest, expectedEventMessage);
    }

    @Test
    public void String_event_messages_should_serialize_and_deserialize_correctly() throws UnsupportedEncodingException {
        final Class<String> messageTypeToTest = String.class;
        final String expectedEventMessage = "TEST_EVENT_MESSAGE";
        verifyEventMessageSerDer(messageTypeToTest, expectedEventMessage);
    }

    @Test
    public void TCommandThreadDumpResponse_event_messages_should_serialize_and_deserialize_correctly() throws UnsupportedEncodingException {
        final Class<TCommandThreadDumpResponse> messageTypeToTest = TCommandThreadDumpResponse.class;
        final TCommandThreadDumpResponse expectedEventMessage = createTCommandThreadDumpResponse();
        verifyEventMessageSerDer(messageTypeToTest, expectedEventMessage);
    }
}

