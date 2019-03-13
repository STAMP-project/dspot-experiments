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
package org.apache.geode.cache.query.internal.cq;


import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.query.CqException;
import org.apache.geode.cache.query.cq.internal.CqServiceImpl;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.tier.sockets.CacheClientNotifier;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CqServiceImplJUnitTest {
    @Test
    public void closedCacheClientProxyInExecuteCqShouldThrowCQException() {
        InternalCache cache = Mockito.mock(InternalCache.class);
        CancelCriterion cancelCriterion = Mockito.mock(CancelCriterion.class);
        DistributedSystem distributedSystem = Mockito.mock(DistributedSystem.class);
        Mockito.doNothing().when(cancelCriterion).checkCancelInProgress(null);
        Mockito.when(cache.getCancelCriterion()).thenReturn(cancelCriterion);
        Mockito.when(cache.getDistributedSystem()).thenReturn(distributedSystem);
        ClientProxyMembershipID clientProxyMembershipID = Mockito.mock(ClientProxyMembershipID.class);
        CacheClientNotifier cacheClientNotifier = Mockito.mock(CacheClientNotifier.class);
        Mockito.when(cacheClientNotifier.getClientProxy(clientProxyMembershipID, true)).thenReturn(null);
        CqServiceImpl cqService = new CqServiceImpl(cache);
        try {
            cqService.getCacheClientProxy(clientProxyMembershipID, cacheClientNotifier);
            Assert.fail();
        } catch (Exception ex) {
            if (!((ex instanceof CqException) && (ex.getMessage().contains("No Cache Client Proxy found while executing CQ.")))) {
                Assert.fail();
            }
        }
    }
}

