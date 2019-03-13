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
package org.apache.geode.internal.cache.tier.sockets.command;


import Operation.READ;
import Resource.DATA;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.tier.sockets.ChunkedMessage;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.Part;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.apache.geode.internal.security.AuthorizeRequest;
import org.apache.geode.internal.security.SecurityService;
import org.apache.geode.security.NotAuthorizedException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class RegisterInterestTest {
    private static final String REGION_NAME = "region1";

    private static final String KEY = "key1";

    private static final byte[] DURABLE = new byte[8];

    @Mock
    private SecurityService securityService;

    @Mock
    private Message message;

    @Mock
    private ServerConnection serverConnection;

    @Mock
    private AuthorizeRequest authzRequest;

    @Mock
    private InternalCache cache;

    @Mock
    private Part regionNamePart;

    @Mock
    private Part interestTypePart;

    @Mock
    private Part durablePart;

    @Mock
    private Part keyPart;

    @Mock
    private Part notifyPart;

    @Mock
    private ChunkedMessage chunkedResponseMessage;

    @InjectMocks
    private RegisterInterest registerInterest;

    @Test
    public void noSecurityShouldSucceed() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(false);
        this.registerInterest.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void integratedSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(true);
        this.registerInterest.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.securityService).authorize(DATA, READ, RegisterInterestTest.REGION_NAME, RegisterInterestTest.KEY);
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void integratedSecurityShouldThrowIfNotAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(true);
        Mockito.doThrow(new NotAuthorizedException("")).when(this.securityService).authorize(DATA, READ, RegisterInterestTest.REGION_NAME, RegisterInterestTest.KEY);
        this.registerInterest.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.securityService).authorize(DATA, READ, RegisterInterestTest.REGION_NAME, RegisterInterestTest.KEY);
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void oldSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(false);
        this.registerInterest.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.authzRequest).registerInterestAuthorize(ArgumentMatchers.eq(RegisterInterestTest.REGION_NAME), ArgumentMatchers.eq(RegisterInterestTest.KEY), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void oldSecurityShouldFailIfNotAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(false);
        Mockito.doThrow(new NotAuthorizedException("")).when(this.authzRequest).registerInterestAuthorize(ArgumentMatchers.eq(RegisterInterestTest.REGION_NAME), ArgumentMatchers.eq(RegisterInterestTest.KEY), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        this.registerInterest.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.authzRequest).registerInterestAuthorize(ArgumentMatchers.eq(RegisterInterestTest.REGION_NAME), ArgumentMatchers.eq(RegisterInterestTest.KEY), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        ArgumentCaptor<NotAuthorizedException> argument = ArgumentCaptor.forClass(NotAuthorizedException.class);
        Mockito.verify(this.chunkedResponseMessage).addObjPart(argument.capture());
        assertThat(argument.getValue()).isExactlyInstanceOf(NotAuthorizedException.class);
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }
}

