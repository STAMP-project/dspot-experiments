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
package org.apache.geode.cache.server.internal;


import CommunicationMode.ProtobufClientServerProtocol;
import org.apache.geode.cache.server.ServerLoadProbe;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class LoadMonitorTest {
    @Test
    public void protobufConnectionIsIncludedInLoadMetrics() throws Exception {
        ServerLoadProbe probe = Mockito.mock(ServerLoadProbe.class);
        Mockito.when(probe.getLoad(ArgumentMatchers.any())).thenReturn(null);
        LoadMonitor loadMonitor = new LoadMonitor(probe, 10000, 0, 0, null);
        loadMonitor.connectionOpened(true, ProtobufClientServerProtocol);
        Assert.assertEquals(1, loadMonitor.metrics.getClientCount());
        Assert.assertEquals(1, loadMonitor.metrics.getConnectionCount());
    }

    @Test
    public void protobufConnectionIsRemovedFromLoadMetrics() throws Exception {
        ServerLoadProbe probe = Mockito.mock(ServerLoadProbe.class);
        Mockito.when(probe.getLoad(ArgumentMatchers.any())).thenReturn(null);
        LoadMonitor loadMonitor = new LoadMonitor(probe, 10000, 0, 0, null);
        loadMonitor.connectionOpened(true, ProtobufClientServerProtocol);
        loadMonitor.connectionClosed(true, ProtobufClientServerProtocol);
        Assert.assertEquals(0, loadMonitor.metrics.getClientCount());
        Assert.assertEquals(0, loadMonitor.metrics.getConnectionCount());
    }
}

