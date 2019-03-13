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
import java.io.IOException;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.TransactionException;
import org.apache.geode.cache.operations.KeySetOperationContext;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.LocalRegion;
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
public class KeySetTest {
    private static final String REGION_NAME = "region1";

    private static final String KEY = "key1";

    private static final Object CALLBACK_ARG = "arg";

    private static final byte[] EVENT = new byte[8];

    @Mock
    private SecurityService securityService;

    @Mock
    private Message message;

    @Mock
    private ServerConnection serverConnection;

    @Mock
    private AuthorizeRequest authzRequest;

    @Mock
    private LocalRegion region;

    @Mock
    private InternalCache cache;

    @Mock
    private ChunkedMessage chunkedResponseMessage;

    @Mock
    private Part regionNamePart;

    @Mock
    private KeySetOperationContext keySetOperationContext;

    @InjectMocks
    private KeySet keySet;

    @Test
    public void retryKeySet_doesNotWriteTransactionException_ifIsNotInTransaction() throws Exception {
        long startTime = 0;// arbitrary value

        KeySetTest.TestableKeySet keySet = new KeySetTest.TestableKeySet();
        keySet.setIsInTransaction(false);
        Mockito.when(message.isRetry()).thenReturn(true);
        Mockito.when(region.getPartitionAttributes()).thenReturn(Mockito.mock(PartitionAttributes.class));
        keySet.cmdExecute(message, serverConnection, securityService, startTime);
        assertThat(keySet.exceptionSentToClient).isNull();
    }

    @Test
    public void nonRetryKeySet_doesNotWriteTransactionException() throws Exception {
        long startTime = 0;// arbitrary value

        KeySetTest.TestableKeySet keySet = new KeySetTest.TestableKeySet();
        keySet.setIsInTransaction(true);
        Mockito.when(message.isRetry()).thenReturn(false);
        Mockito.when(region.getPartitionAttributes()).thenReturn(Mockito.mock(PartitionAttributes.class));
        keySet.cmdExecute(message, serverConnection, securityService, startTime);
        assertThat(keySet.exceptionSentToClient).isNull();
    }

    @Test
    public void retryKeySet_doesNotWriteTransactionException_ifIsInTransactionAndIsNotPartitionedRegion() throws Exception {
        long startTime = 0;// arbitrary value

        KeySetTest.TestableKeySet keySet = new KeySetTest.TestableKeySet();
        keySet.setIsInTransaction(true);
        Mockito.when(message.isRetry()).thenReturn(true);
        Mockito.when(region.getPartitionAttributes()).thenReturn(null);
        keySet.cmdExecute(message, serverConnection, securityService, startTime);
        assertThat(keySet.exceptionSentToClient).isNull();
    }

    @Test
    public void retryKeySet_writesTransactionException_ifIsInTransactionAndIsPartitionedRegion() throws Exception {
        long startTime = 0;// arbitrary value

        KeySetTest.TestableKeySet keySet = new KeySetTest.TestableKeySet();
        keySet.setIsInTransaction(true);
        Mockito.when(message.isRetry()).thenReturn(true);
        Mockito.when(region.getPartitionAttributes()).thenReturn(Mockito.mock(PartitionAttributes.class));
        keySet.cmdExecute(message, serverConnection, securityService, startTime);
        assertThat(keySet.exceptionSentToClient).isInstanceOf(TransactionException.class).hasMessage("Failover on a set operation of a partitioned region is not allowed in a transaction.");
    }

    @Test
    public void noSecurityShouldSucceed() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(false);
        this.keySet.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void integratedSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(true);
        this.keySet.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.securityService).authorize(DATA, READ, KeySetTest.REGION_NAME);
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void integratedSecurityShouldFailIfNotAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(true);
        Mockito.doThrow(new NotAuthorizedException("")).when(this.securityService).authorize(DATA, READ, KeySetTest.REGION_NAME);
        this.keySet.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.securityService).authorize(DATA, READ, KeySetTest.REGION_NAME);
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void oldSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(false);
        this.keySet.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.authzRequest).keySetAuthorize(ArgumentMatchers.eq(KeySetTest.REGION_NAME));
        Mockito.verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
    }

    @Test
    public void oldSecurityShouldFailIfNotAuthorized() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(this.securityService.isIntegratedSecurity()).thenReturn(false);
        Mockito.doThrow(new NotAuthorizedException("")).when(this.authzRequest).keySetAuthorize(ArgumentMatchers.eq(KeySetTest.REGION_NAME));
        this.keySet.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.authzRequest).keySetAuthorize(ArgumentMatchers.eq(KeySetTest.REGION_NAME));
        ArgumentCaptor<NotAuthorizedException> argument = ArgumentCaptor.forClass(NotAuthorizedException.class);
        Mockito.verify(this.chunkedResponseMessage).addObjPart(argument.capture());
        assertThat(argument.getValue()).isExactlyInstanceOf(NotAuthorizedException.class);
        Mockito.verify(this.chunkedResponseMessage).sendChunk(ArgumentMatchers.eq(this.serverConnection));
    }

    private class TestableKeySet extends KeySet {
        private boolean isInTransaction = false;

        public Throwable exceptionSentToClient;

        public void setIsInTransaction(boolean isInTransaction) {
            this.isInTransaction = isInTransaction;
        }

        @Override
        public boolean isInTransaction() {
            return isInTransaction;
        }

        @Override
        protected void keySetWriteChunkedException(Message clientMessage, Throwable ex, ServerConnection serverConnection) throws IOException {
            this.exceptionSentToClient = ex;
        }
    }
}

