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
package org.apache.geode.internal.tcp;


import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import org.apache.geode.CancelCriterion;
import org.apache.geode.distributed.internal.DMStats;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.distributed.internal.membership.MembershipManager;
import org.apache.geode.internal.net.SocketCloser;
import org.apache.geode.internal.net.SocketCreator;
import org.apache.geode.test.junit.categories.MembershipTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ MembershipTest.class })
public class ConnectionJUnitTest {
    /**
     * Test whether suspicion is raised about a member that closes its shared/unordered TCPConduit
     * connection
     */
    @Test
    public void testSuspicionRaised() throws Exception {
        // this test has to create a lot of mocks because Connection
        // uses a lot of objects
        // mock the socket
        ConnectionTable table = Mockito.mock(ConnectionTable.class);
        DistributionManager distMgr = Mockito.mock(DistributionManager.class);
        MembershipManager membership = Mockito.mock(MembershipManager.class);
        TCPConduit conduit = Mockito.mock(TCPConduit.class);
        // mock the connection table and conduit
        Mockito.when(table.getConduit()).thenReturn(conduit);
        CancelCriterion stopper = Mockito.mock(CancelCriterion.class);
        Mockito.when(stopper.cancelInProgress()).thenReturn(null);
        Mockito.when(conduit.getCancelCriterion()).thenReturn(stopper);
        Mockito.when(conduit.getSocketId()).thenReturn(new InetSocketAddress(SocketCreator.getLocalHost(), 10337));
        // mock the distribution manager and membership manager
        Mockito.when(distMgr.getMembershipManager()).thenReturn(membership);
        Mockito.when(conduit.getDM()).thenReturn(distMgr);
        Mockito.when(conduit.getStats()).thenReturn(Mockito.mock(DMStats.class));
        Mockito.when(table.getDM()).thenReturn(distMgr);
        SocketCloser closer = Mockito.mock(SocketCloser.class);
        Mockito.when(table.getSocketCloser()).thenReturn(closer);
        SocketChannel channel = SocketChannel.open();
        Connection conn = new Connection(table, channel.socket());
        conn.setSharedUnorderedForTest();
        conn.run();
        Mockito.verify(membership).suspectMember(ArgumentMatchers.isNull(InternalDistributedMember.class), ArgumentMatchers.any(String.class));
    }
}

