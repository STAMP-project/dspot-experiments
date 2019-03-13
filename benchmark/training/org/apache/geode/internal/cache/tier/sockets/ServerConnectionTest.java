/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.tier.sockets;


import Version.GFE_61;
import Version.GFE_82;
import org.apache.geode.internal.cache.tier.Encryptor;
import org.apache.geode.internal.cache.tier.ServerSideHandshake;
import org.apache.geode.security.AuthenticationRequiredException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class ServerConnectionTest {
    @Mock
    private Message requestMsg;

    @Mock
    private MessageIdExtractor messageIdExtractor;

    @InjectMocks
    private ServerConnection serverConnection;

    private ServerSideHandshake handshake;

    @Test
    public void pre65SecureShouldReturnUserAuthId() {
        long userAuthId = 12345L;
        serverConnection.setUserAuthId(userAuthId);
        Mockito.when(handshake.getVersion()).thenReturn(GFE_61);
        Mockito.when(requestMsg.isSecureMode()).thenReturn(true);
        assertThat(serverConnection.getUniqueId()).isEqualTo(userAuthId);
    }

    @Test
    public void pre65NonSecureShouldReturnUserAuthId() {
        long userAuthId = 12345L;
        serverConnection.setUserAuthId(userAuthId);
        Mockito.when(handshake.getVersion()).thenReturn(GFE_61);
        Mockito.when(requestMsg.isSecureMode()).thenReturn(false);
        assertThat(serverConnection.getUniqueId()).isEqualTo(userAuthId);
    }

    @Test
    public void post65SecureShouldUseUniqueIdFromMessage() {
        long uniqueIdFromMessage = 23456L;
        Mockito.when(handshake.getVersion()).thenReturn(GFE_82);
        serverConnection.setRequestMessage(requestMsg);
        assertThat(serverConnection.getRequestMessage()).isSameAs(requestMsg);
        Mockito.when(requestMsg.isSecureMode()).thenReturn(true);
        Mockito.when(messageIdExtractor.getUniqueIdFromMessage(ArgumentMatchers.any(Message.class), ArgumentMatchers.any(Encryptor.class), ArgumentMatchers.anyLong())).thenReturn(uniqueIdFromMessage);
        serverConnection.setMessageIdExtractor(messageIdExtractor);
        assertThat(serverConnection.getUniqueId()).isEqualTo(uniqueIdFromMessage);
    }

    @Test
    public void post65NonSecureShouldThrow() {
        Mockito.when(handshake.getVersion()).thenReturn(GFE_82);
        Mockito.when(requestMsg.isSecureMode()).thenReturn(false);
        assertThatThrownBy(serverConnection::getUniqueId).isExactlyInstanceOf(AuthenticationRequiredException.class).hasMessage("No security credentials are provided");
    }
}

