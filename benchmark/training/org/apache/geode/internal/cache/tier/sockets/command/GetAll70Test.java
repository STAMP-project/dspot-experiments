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
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.tier.sockets.ChunkedMessage;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.ObjectPartList;
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
public class GetAll70Test {
    private static final String REGION_NAME = "region1";

    private static final Object[] KEYS = new Object[]{ "key1", "key2", "key3" };

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
    private LocalRegion region;

    @Mock
    private Part regionNamePart;

    @Mock
    private Part keyPart;

    @Mock
    private Part requestSerializableValuesPart;

    @Mock
    private RegionAttributes regionAttributes;

    @Mock
    private ChunkedMessage chunkedResponseMessage;

    @InjectMocks
    private GetAll70 getAll70;

    @Test
    public void noSecurityShouldSucceed() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(false);
        this.getAll70.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void integratedSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(true);
        this.getAll70.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        ArgumentCaptor<ObjectPartList> argument = ArgumentCaptor.forClass(ObjectPartList.class);
        Mockito.verify(this.chunkedResponseMessage).addObjPartNoCopying(argument.capture());
        assertThat(argument.getValue().getObjects()).hasSize(GetAll70Test.KEYS.length);
        for (Object key : argument.getValue().getKeys()) {
            assertThat(key).isIn(GetAll70Test.KEYS);
        }
        for (Object key : GetAll70Test.KEYS) {
            Mockito.verify(this.securityService).authorize(DATA, READ, GetAll70Test.REGION_NAME, key.toString());
        }
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void integratedSecurityShouldFailIfNotAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(true);
        for (Object key : GetAll70Test.KEYS) {
            Mockito.doThrow(new NotAuthorizedException("")).when(this.securityService).authorize(DATA, READ, GetAll70Test.REGION_NAME, key.toString());
        }
        this.getAll70.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        for (Object key : GetAll70Test.KEYS) {
            Mockito.verify(this.securityService).authorize(DATA, READ, GetAll70Test.REGION_NAME, key.toString());
        }
        ArgumentCaptor<ObjectPartList> argument = ArgumentCaptor.forClass(ObjectPartList.class);
        Mockito.verify(this.chunkedResponseMessage).addObjPartNoCopying(argument.capture());
        assertThat(argument.getValue().getObjects()).hasSize(GetAll70Test.KEYS.length);
        for (Object key : argument.getValue().getObjects()) {
            assertThat(key).isExactlyInstanceOf(NotAuthorizedException.class);
        }
        Mockito.verify(this.chunkedResponseMessage).sendChunk(ArgumentMatchers.eq(this.serverConnection));
    }

    @Test
    public void oldSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(false);
        this.getAll70.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        ArgumentCaptor<ObjectPartList> argument = ArgumentCaptor.forClass(ObjectPartList.class);
        Mockito.verify(this.chunkedResponseMessage).addObjPartNoCopying(argument.capture());
        assertThat(argument.getValue().getObjects()).hasSize(GetAll70Test.KEYS.length);
        for (Object key : argument.getValue().getKeys()) {
            assertThat(key).isIn(GetAll70Test.KEYS);
        }
        for (Object key : GetAll70Test.KEYS) {
            Mockito.verify(this.authzRequest).getAuthorize(ArgumentMatchers.eq(GetAll70Test.REGION_NAME), ArgumentMatchers.eq(key.toString()), ArgumentMatchers.eq(null));
        }
        Mockito.verify(this.chunkedResponseMessage).sendChunk(ArgumentMatchers.eq(this.serverConnection));
    }

    @Test
    public void oldSecurityShouldFailIfNotAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(false);
        for (Object key : GetAll70Test.KEYS) {
            Mockito.doThrow(new NotAuthorizedException("")).when(this.authzRequest).getAuthorize(ArgumentMatchers.eq(GetAll70Test.REGION_NAME), ArgumentMatchers.eq(key.toString()), ArgumentMatchers.eq(null));
        }
        this.getAll70.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        ArgumentCaptor<ObjectPartList> argument = ArgumentCaptor.forClass(ObjectPartList.class);
        Mockito.verify(this.chunkedResponseMessage).addObjPartNoCopying(argument.capture());
        assertThat(argument.getValue().getObjects()).hasSize(GetAll70Test.KEYS.length);
        for (Object o : argument.getValue().getObjects()) {
            assertThat(o).isExactlyInstanceOf(NotAuthorizedException.class);
        }
        for (Object key : GetAll70Test.KEYS) {
            Mockito.verify(this.authzRequest).getAuthorize(ArgumentMatchers.eq(GetAll70Test.REGION_NAME), ArgumentMatchers.eq(key.toString()), ArgumentMatchers.eq(null));
        }
        Mockito.verify(this.chunkedResponseMessage).sendChunk(ArgumentMatchers.eq(this.serverConnection));
    }
}

