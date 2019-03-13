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


import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.kaaproject.kaa.server.common.thrift.KaaThriftService;
import org.kaaproject.kaa.server.common.zk.gen.ConnectionInfo;
import org.kaaproject.kaa.server.sync.Event;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


public class NeighborsTest {
    @SuppressWarnings("unchecked")
    @Test
    public void sendMessageTest() throws IllegalAccessException, InterruptedException, NoSuchMethodException, InvocationTargetException {
        ConnectionInfo connectionInfo1 = new ConnectionInfo("thriftHost", 9999, ByteBuffer.allocate(10));
        NeighborTemplate<Event> template = ((NeighborTemplate<Event>) (Mockito.mock(NeighborTemplate.class)));
        Neighbors<NeighborTemplate<Event>, Event> neighbors = new Neighbors(KaaThriftService.OPERATIONS_SERVICE, template, 1);
        ReflectionTestUtils.setField(neighbors, "zkId", "someZkId");
        NeighborConnection<NeighborTemplate<Event>, Event> neighborConnection = new NeighborConnection(connectionInfo1, 1, null);
        LinkedBlockingQueue<Event> eventQueue = Mockito.spy(new LinkedBlockingQueue<Event>());
        ReflectionTestUtils.setField(neighborConnection, "messageQueue", eventQueue);
        ConcurrentMap<String, NeighborConnection<NeighborTemplate<Event>, Event>> neighborMap = Mockito.spy(((ConcurrentMap<String, NeighborConnection<NeighborTemplate<Event>, Event>>) (ReflectionTestUtils.getField(neighbors, "neigbors"))));
        ReflectionTestUtils.setField(neighbors, "neigbors", neighborMap);
        Mockito.when(neighborMap.get(ArgumentMatchers.anyString())).thenReturn(neighborConnection);
        Collection<Event> messages = new ArrayList<>();
        Event e = new Event(10, "FQN", null, null, null);
        messages.add(e);
        Collection<Event> messagesSpy = Mockito.spy(messages);
        neighbors.sendMessages(connectionInfo1, messagesSpy);
        Mockito.verify(messagesSpy, Mockito.timeout(1000)).iterator();
        Mockito.verify(eventQueue, Mockito.timeout(1000)).offer(ArgumentMatchers.eq(e), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        neighbors.shutdown();
    }
}

