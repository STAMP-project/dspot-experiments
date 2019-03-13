/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.bootstrap.service.transport;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Properties;
import org.junit.Test;
import org.kaaproject.kaa.server.bootstrap.service.security.KeyStoreService;
import org.kaaproject.kaa.server.transport.message.MessageHandler;
import org.kaaproject.kaa.server.transport.message.SessionInitMessage;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


public class BootstrapTransportServiceTest {
    private static final String RSA = "RSA";

    private KeyPairGenerator keyPairGenerator;

    @Test
    public void messageHandlerProcessTest() {
        BootstrapTransportService bService = new BootstrapTransportService();
        KeyStoreService keyStoreService = Mockito.mock(KeyStoreService.class);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        Mockito.when(keyStoreService.getPublicKey()).thenReturn(publicKey);
        Mockito.when(keyStoreService.getPrivateKey()).thenReturn(privateKey);
        ReflectionTestUtils.setField(bService, "supportUnencryptedConnection", true);
        ReflectionTestUtils.setField(bService, "bootstrapKeyStoreService", keyStoreService);
        ReflectionTestUtils.setField(bService, "properties", new Properties());
        bService.lookupAndInit();
        MessageHandler handler = ((BootstrapTransportService.BootstrapMessageHandler) (ReflectionTestUtils.getField(bService, "handler")));
        SessionInitMessage encryptedSessionInitMessage = mockForSessionInitMessage(true);
        SessionInitMessage nonEncryptedSessionInitMessage = mockForSessionInitMessage(false);
        handler.process(encryptedSessionInitMessage);
        handler.process(nonEncryptedSessionInitMessage);
        Mockito.verify(encryptedSessionInitMessage, Mockito.timeout(1000)).getEncodedMessageData();
        Mockito.verify(nonEncryptedSessionInitMessage, Mockito.timeout(1000)).getEncodedMessageData();
    }
}

