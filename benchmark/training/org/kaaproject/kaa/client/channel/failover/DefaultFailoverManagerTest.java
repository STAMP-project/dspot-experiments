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


import DefaultFailoverManager.AccessPointIdResolution;
import FailoverStatus.BOOTSTRAP_SERVERS_NA;
import FailoverStatus.NO_CONNECTIVITY;
import ServerType.BOOTSTRAP;
import java.util.Map;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.channel.KaaChannelManager;
import org.kaaproject.kaa.client.channel.ServerType;
import org.kaaproject.kaa.client.channel.TransportConnectionInfo;
import org.kaaproject.kaa.client.context.ExecutorContext;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultFailoverManagerTest {
    private static final int RESOLUTION_TIMEOUT_MS = 500;

    private static final int BOOTSTRAP_RETRY_PERIOD = 100;

    private KaaChannelManager channelManager;

    private Map<ServerType, DefaultFailoverManager.AccessPointIdResolution> resolutionProgressMap;

    private DefaultFailoverManager failoverManager;

    private ExecutorContext context;

    @Test
    public void onServerConnectedTest() {
        TransportConnectionInfo transportConnectionInfo = mockForTransportConnectionInfo(1);
        failoverManager.onServerConnected(transportConnectionInfo);
        failoverManager.onServerChanged(transportConnectionInfo);
        failoverManager.onServerFailed(transportConnectionInfo, NO_CONNECTIVITY);
        DefaultFailoverManager.AccessPointIdResolution accessPointIdResolutionSpy = spyForResolutionMap(transportConnectionInfo);
        failoverManager.onServerConnected(transportConnectionInfo);
        Mockito.verify(accessPointIdResolutionSpy, Mockito.times(1)).setCurResolution(Mockito.any(Future.class));
    }

    @Test
    public void onServerChangedTest() {
        TransportConnectionInfo info = mockForTransportConnectionInfo(1);
        failoverManager.onServerChanged(null);
        Mockito.verify(resolutionProgressMap, Mockito.never()).put(Mockito.any(ServerType.class), Mockito.any(AccessPointIdResolution.class));
        failoverManager.onServerChanged(info);
        Mockito.verify(resolutionProgressMap, Mockito.times(1)).put(info.getServerType(), new DefaultFailoverManager.AccessPointIdResolution(info.getAccessPointId(), null));
        TransportConnectionInfo info2 = mockForTransportConnectionInfo(2);
        failoverManager.onServerFailed(info, NO_CONNECTIVITY);
        DefaultFailoverManager.AccessPointIdResolution accessPointIdResolutionSpy = spyForResolutionMap(info);
        failoverManager.onServerChanged(info2);
        Mockito.verify(accessPointIdResolutionSpy, Mockito.times(1)).setCurResolution(null);
        Mockito.verify(resolutionProgressMap, Mockito.times(1)).put(info2.getServerType(), new DefaultFailoverManager.AccessPointIdResolution(info2.getAccessPointId(), null));
    }

    @Test
    public void onServerFailedTest() throws InterruptedException {
        TransportConnectionInfo info = mockForTransportConnectionInfo(1);
        failoverManager.onServerFailed(null, NO_CONNECTIVITY);
        Mockito.verify(resolutionProgressMap, Mockito.never()).put(Mockito.any(ServerType.class), Mockito.any(AccessPointIdResolution.class));
        failoverManager.onServerFailed(info, NO_CONNECTIVITY);
        Mockito.verify(channelManager, Mockito.times(1)).onServerFailed(info, NO_CONNECTIVITY);
        Mockito.verify(context, Mockito.times(1)).getScheduledExecutor();
        ArgumentCaptor<DefaultFailoverManager.AccessPointIdResolution> argument = ArgumentCaptor.forClass(AccessPointIdResolution.class);
        Mockito.verify(resolutionProgressMap, Mockito.times(1)).put(Mockito.eq(info.getServerType()), argument.capture());
        Assert.assertEquals(argument.getValue().getAccessPointId(), info.getAccessPointId());
        Assert.assertNotNull(argument.getValue().getCurResolution());
        Mockito.verify(resolutionProgressMap, Mockito.timeout(((DefaultFailoverManagerTest.RESOLUTION_TIMEOUT_MS) * 2)).times(1)).remove(info.getServerType());
        TransportConnectionInfo info2 = mockForTransportConnectionInfo(2, BOOTSTRAP);
        failoverManager.onServerFailed(info2, NO_CONNECTIVITY);
        final DefaultFailoverManager.AccessPointIdResolution accessPointIdResolutionSpy = spyForResolutionMap(info2);
        failoverManager.onServerFailed(info2, NO_CONNECTIVITY);
        Mockito.verify(accessPointIdResolutionSpy, Mockito.never()).setCurResolution(null);
        info2 = mockForTransportConnectionInfo(3, BOOTSTRAP);
        failoverManager.onServerFailed(info2, NO_CONNECTIVITY);
        Mockito.verify(channelManager, Mockito.times(1)).onServerFailed(info2, NO_CONNECTIVITY);
        Mockito.reset(accessPointIdResolutionSpy);
        failoverManager.onFailover(BOOTSTRAP_SERVERS_NA);
        failoverManager.onServerFailed(info2, NO_CONNECTIVITY);
        Mockito.verify(accessPointIdResolutionSpy, Mockito.never()).setCurResolution(null);
        Thread.sleep(((DefaultFailoverManagerTest.BOOTSTRAP_RETRY_PERIOD) * 2));
        failoverManager.onServerFailed(info2, NO_CONNECTIVITY);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mockito.verify(accessPointIdResolutionSpy, Mockito.timeout(((DefaultFailoverManagerTest.BOOTSTRAP_RETRY_PERIOD) * 2)).times(1)).setCurResolution(null);
            }
        }).start();
    }
}

