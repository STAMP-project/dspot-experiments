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
package org.kaaproject.kaa.server.thrift;


import OperationsThriftService.Iface;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.zk.gen.ConnectionInfo;
import org.kaaproject.kaa.server.sync.Event;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class NeighborConnectionTest {
    private NeighborConnection<NeighborTemplate<Event>, Event> neighborConnection;

    private NeighborTemplate<Event> template = ((NeighborTemplate<Event>) (Mockito.mock(NeighborTemplate.class)));

    @SuppressWarnings("unchecked")
    @Test
    public void startTest() throws InterruptedException, TException {
        neighborConnection.start();
        ExecutorService executorSpy = getSpyOnExecutorAndInjectIt();
        neighborConnection.sendMessages(Collections.singleton(new Event()));
        Mockito.verify(template, Mockito.timeout(1000)).process(ArgumentMatchers.any(Iface.class), ArgumentMatchers.anyList());
        neighborConnection.shutdown();
        Mockito.verify(executorSpy, Mockito.timeout(1000)).shutdown();
    }

    @Test
    public void startTExceptionThrownTest() throws InterruptedException, TException {
        TException tException = new TException();
        Mockito.doThrow(tException).when(template).process(ArgumentMatchers.any(Iface.class), ArgumentMatchers.anyList());
        neighborConnection.start();
        ExecutorService executorSpy = getSpyOnExecutorAndInjectIt();
        neighborConnection.sendMessages(Arrays.asList(new Event()));
        Mockito.verify(template, Mockito.timeout(1000)).onServerError(ArgumentMatchers.anyString(), ArgumentMatchers.eq(tException));
        neighborConnection.shutdown();
        Mockito.verify(executorSpy, Mockito.timeout(1000)).shutdown();
    }

    @Test
    public void equalsHashCodeTest() {
        ConnectionInfo connectionInfo1 = new ConnectionInfo("thriftHost1", 9991, null);
        ConnectionInfo connectionInfo2 = new ConnectionInfo("thriftHost1", 9991, null);
        ConnectionInfo connectionInfo3 = new ConnectionInfo("thriftHost1", 9993, null);
        NeighborConnection<NeighborTemplate<Event>, Event> neighborConnection1 = new NeighborConnection(connectionInfo1, 10, null);
        NeighborConnection<NeighborTemplate<Event>, Event> neighborConnection2 = new NeighborConnection(connectionInfo2, 8, null);
        NeighborConnection<NeighborTemplate<Event>, Event> neighborConnection3 = new NeighborConnection(connectionInfo3, 7, null);
        Assert.assertEquals(neighborConnection1.hashCode(), neighborConnection2.hashCode());
        Assert.assertNotEquals(neighborConnection1.hashCode(), neighborConnection3.hashCode());
        Assert.assertEquals(neighborConnection1, neighborConnection1);
        Assert.assertNotEquals(neighborConnection1, null);
        Assert.assertNotEquals(neighborConnection1, new Object());
        Assert.assertEquals(neighborConnection1, neighborConnection2);
        Assert.assertNotEquals(neighborConnection1, neighborConnection3);
    }
}

