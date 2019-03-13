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
package org.kaaproject.kaa.client.channel.failover;


import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.KaaClient;
import org.kaaproject.kaa.client.channel.TransportConnectionInfo;
import org.kaaproject.kaa.client.channel.failover.FailoverDecision.FailoverAction;
import org.kaaproject.kaa.client.channel.failover.strategies.DefaultFailoverStrategy;
import org.kaaproject.kaa.client.channel.failover.strategies.FailoverStrategy;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static FailoverStatus.BOOTSTRAP_SERVERS_NA;
import static FailoverStatus.OPERATION_SERVERS_NA;


public class FailoverStrategyTest {
    private FailoverManager failoverManager;

    private FailoverStrategy failoverStrategy;

    @Test
    public void changeStrategyAtRuntimeTest() {
        KaaClient kaaClient = Mockito.mock(KaaClient.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                FailoverStrategy strategy = ((FailoverStrategy) (invocationOnMock.getArguments()[0]));
                failoverManager.setFailoverStrategy(strategy);
                return null;
            }
        }).when(kaaClient).setFailoverStrategy(Mockito.any(FailoverStrategy.class));
        FailoverStatus singleFailoverStatus = OPERATION_SERVERS_NA;
        FailoverDecision primaryFailoverDecision = failoverManager.onFailover(singleFailoverStatus);
        kaaClient.setFailoverStrategy(new DefaultFailoverStrategy() {
            @Override
            public FailoverDecision onFailover(FailoverStatus failoverStatus) {
                if (failoverStatus == (FailoverStatus.OPERATION_SERVERS_NA)) {
                    return new FailoverDecision(FailoverAction.USE_NEXT_BOOTSTRAP);
                }
                return null;
            }
        });
        FailoverDecision secondaryFailoverDecision = failoverManager.onFailover(singleFailoverStatus);
        Assert.assertNotEquals(primaryFailoverDecision.getAction(), secondaryFailoverDecision.getAction());
    }

    @Test
    public void basicFailoverStrategyTest() {
        FailoverStatus incomingStatus = BOOTSTRAP_SERVERS_NA;
        Assert.assertNotNull(failoverManager.onFailover(incomingStatus));
        Mockito.verify(failoverStrategy, Mockito.times(1)).onFailover(incomingStatus);
        TransportConnectionInfo connectionInfo = Mockito.mock(TransportConnectionInfo.class);
        failoverManager.onServerConnected(connectionInfo);
        Mockito.verify(failoverStrategy, Mockito.times(1)).onRecover(connectionInfo);
    }
}

