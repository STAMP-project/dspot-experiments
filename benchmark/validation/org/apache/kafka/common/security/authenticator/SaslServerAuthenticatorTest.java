/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.security.authenticator;


import BrokerSecurityConfigs.SASL_ENABLED_MECHANISMS_CONFIG;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import org.apache.kafka.common.errors.IllegalSaslStateException;
import org.apache.kafka.common.network.InvalidReceiveException;
import org.apache.kafka.common.network.TransportLayer;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.requests.RequestHeader;
import org.apache.kafka.common.security.scram.internals.ScramMechanism;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SaslServerAuthenticatorTest {
    @Test(expected = InvalidReceiveException.class)
    public void testOversizeRequest() throws IOException {
        TransportLayer transportLayer = Mockito.mock(TransportLayer.class);
        Map<String, ?> configs = Collections.singletonMap(SASL_ENABLED_MECHANISMS_CONFIG, Collections.singletonList(ScramMechanism.SCRAM_SHA_256.mechanismName()));
        SaslServerAuthenticator authenticator = setupAuthenticator(configs, transportLayer, ScramMechanism.SCRAM_SHA_256.mechanismName());
        Mockito.when(transportLayer.read(ArgumentMatchers.any(ByteBuffer.class))).then(( invocation) -> {
            invocation.<ByteBuffer>getArgument(0).putInt((SaslServerAuthenticator.MAX_RECEIVE_SIZE + 1));
            return 4;
        });
        authenticator.authenticate();
        Mockito.verify(transportLayer).read(ArgumentMatchers.any(ByteBuffer.class));
    }

    @Test
    public void testUnexpectedRequestType() throws IOException {
        TransportLayer transportLayer = Mockito.mock(TransportLayer.class);
        Map<String, ?> configs = Collections.singletonMap(SASL_ENABLED_MECHANISMS_CONFIG, Collections.singletonList(ScramMechanism.SCRAM_SHA_256.mechanismName()));
        SaslServerAuthenticator authenticator = setupAuthenticator(configs, transportLayer, ScramMechanism.SCRAM_SHA_256.mechanismName());
        final RequestHeader header = new RequestHeader(ApiKeys.METADATA, ((short) (0)), "clientId", 13243);
        final Struct headerStruct = header.toStruct();
        Mockito.when(transportLayer.read(ArgumentMatchers.any(ByteBuffer.class))).then(( invocation) -> {
            invocation.<ByteBuffer>getArgument(0).putInt(headerStruct.sizeOf());
            return 4;
        }).then(( invocation) -> {
            // serialize only the request header. the authenticator should not parse beyond this
            headerStruct.writeTo(invocation.getArgument(0));
            return headerStruct.sizeOf();
        });
        try {
            authenticator.authenticate();
            Assert.fail("Expected authenticate() to raise an exception");
        } catch (IllegalSaslStateException e) {
            // expected exception
        }
        Mockito.verify(transportLayer, Mockito.times(2)).read(ArgumentMatchers.any(ByteBuffer.class));
    }
}

