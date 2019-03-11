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
package org.apache.geode.internal.cache;


import Version.GEODE_1_7_0;
import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.Version;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ExpireDisconnectedClientTransactionsMessageTest {
    private final ClusterDistributionManager dm = Mockito.mock(ClusterDistributionManager.class);

    private final InternalCache cache = Mockito.mock(InternalCache.class);

    private final TXManagerImpl txManager = Mockito.mock(TXManagerImpl.class);

    private final InternalDistributedMember sender = Mockito.mock(InternalDistributedMember.class);

    private final ExpireDisconnectedClientTransactionsMessage message = Mockito.spy(new ExpireDisconnectedClientTransactionsMessage());

    private Version version = Mockito.mock(Version.class);

    @Test
    public void processMessageFromServerOfGeode170AndLaterVersionWillExpireDisconnectedClientTransactions() {
        Mockito.when(version.compareTo(GEODE_1_7_0)).thenReturn(1);
        message.process(dm);
        Mockito.verify(txManager, Mockito.times(1)).expireDisconnectedClientTransactions(ArgumentMatchers.any(), ArgumentMatchers.eq(false));
    }

    @Test
    public void processMessageFromServerOfPriorGeode170VersionWillRemoveExpiredClientTransactions() {
        Mockito.when(version.compareTo(GEODE_1_7_0)).thenReturn((-1));
        message.process(dm);
        Mockito.verify(txManager, Mockito.times(1)).removeExpiredClientTransactions(ArgumentMatchers.any());
    }
}

