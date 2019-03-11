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
package org.apache.geode.internal.cache.execute;


import java.io.IOException;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.tier.CachedRegionHelper;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.apache.geode.security.NotAuthorizedException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class ServerToClientFunctionResultSenderJUnitTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    ServerConnection serverConnection;

    @Test
    public void whenLastResultReceivedIsSetThenLastResultMustReturnImmediately() throws IOException {
        ServerToClientFunctionResultSender resultSender = getResultSender();
        resultSender.lastResultReceived = true;
        Object object = Mockito.mock(Object.class);
        DistributedMember memberId = Mockito.mock(DistributedMember.class);
        resultSender.lastResult(object, memberId);
        Mockito.verify(serverConnection, Mockito.times(0)).getPostAuthzRequest();
        resultSender.lastResult(object);
        Mockito.verify(serverConnection, Mockito.times(0)).getPostAuthzRequest();
    }

    @Test
    public void whenExceptionOccursThenLastResultReceivedMustNotBeSet() throws Exception {
        ServerToClientFunctionResultSender resultSender = getResultSender();
        resultSender.ids = Mockito.mock(InternalDistributedSystem.class);
        Mockito.when(resultSender.ids.isDisconnecting()).thenReturn(false);
        Object object = Mockito.mock(Object.class);
        resultSender.lastResultReceived = false;
        DistributedMember distributedMember = Mockito.mock(DistributedMember.class);
        Mockito.when(serverConnection.getPostAuthzRequest()).thenThrow(new NotAuthorizedException("Should catch this exception"));
        CachedRegionHelper cachedRegionHelper = Mockito.mock(CachedRegionHelper.class);
        Mockito.when(serverConnection.getCachedRegionHelper()).thenReturn(cachedRegionHelper);
        InternalCache cache = Mockito.mock(InternalCache.class);
        Mockito.when(cachedRegionHelper.getCache()).thenReturn(cache);
        Mockito.when(cache.isClosed()).thenReturn(false);
        expectedException.expect(NotAuthorizedException.class);
        resultSender.lastResult(object, distributedMember);
        Assert.assertFalse(resultSender.lastResultReceived);
        resultSender.lastResult(object);
        Assert.assertFalse(resultSender.lastResultReceived);
    }

    @Test
    public void whenLastResultReceivedIsSetThenSendResultMustReturnImmediately() throws IOException {
        ServerToClientFunctionResultSender resultSender = getResultSender();
        resultSender.lastResultReceived = true;
        Object object = Mockito.mock(Object.class);
        DistributedMember memberId = Mockito.mock(DistributedMember.class);
        resultSender.sendResult(object, memberId);
        Mockito.verify(serverConnection, Mockito.times(0)).getPostAuthzRequest();
        resultSender.sendResult(object);
        Mockito.verify(serverConnection, Mockito.times(0)).getPostAuthzRequest();
    }
}

