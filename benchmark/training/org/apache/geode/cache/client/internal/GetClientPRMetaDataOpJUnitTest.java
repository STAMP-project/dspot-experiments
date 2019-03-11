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
package org.apache.geode.cache.client.internal;


import GetClientPRMetaDataOp.GetClientPRMetaDataOpImpl;
import MessageType.RESPONSE_CLIENT_PR_METADATA;
import org.apache.geode.cache.Cache;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GetClientPRMetaDataOpJUnitTest {
    @Test
    public void processResponseWhenCacheClosedShuouldReturnNull() throws Exception {
        Cache cache = Mockito.mock(Cache.class);
        ClientMetadataService cms = new ClientMetadataService(cache);
        cms = Mockito.spy(cms);
        Mockito.doReturn(true).when(cache).isClosed();
        Message msg = Mockito.mock(Message.class);
        GetClientPRMetaDataOp.GetClientPRMetaDataOpImpl op = new GetClientPRMetaDataOp.GetClientPRMetaDataOpImpl("testRegion", cms);
        op = Mockito.spy(op);
        Mockito.when(msg.getMessageType()).thenReturn(RESPONSE_CLIENT_PR_METADATA);
        Assert.assertNull(op.processResponse(msg));
        Mockito.verify(cms, Mockito.times(1)).setMetadataStable(ArgumentMatchers.eq(true));
    }
}

