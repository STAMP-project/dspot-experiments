/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jgroups.raft;


import java.io.DataInput;
import java.io.DataOutput;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jgroups.protocols.raft.StateMachine;
import org.junit.Test;


public class JGroupsRaftEndpointTest extends CamelTestSupport {
    static final String CLUSTER_NAME = "JGroupsRaftEndpointTest";

    static final String CONFIGURED_ENDPOINT_URI = ("jgroups-raft:" + (JGroupsRaftEndpointTest.CLUSTER_NAME)) + "?raftId=A";

    static final String CLUSTER_NAME1 = "JGroupsraftEndpointTest1";

    static final String CONFIGURED_ENDPOINT_URI1 = ("jgroups-raft:" + (JGroupsRaftEndpointTest.CLUSTER_NAME1)) + "?raftHandle=#rh";

    static final String CLUSTER_NAME2 = "JGroupsraftEndpointTest2";

    static final String CONFIGURED_ENDPOINT_URI2 = ("jgroups-raft:" + (JGroupsRaftEndpointTest.CLUSTER_NAME2)) + "?stateMachine=#sm&raftId=C&channelProperties=raftC.xml";

    StateMachine sm = new StateMachine() {
        @Override
        public byte[] apply(byte[] bytes, int i, int i1) throws Exception {
            return new byte[0];
        }

        @Override
        public void readContentFrom(DataInput dataInput) throws Exception {
        }

        @Override
        public void writeContentTo(DataOutput dataOutput) throws Exception {
        }
    };

    @Test
    public void shouldSetClusterNameAndResolveRaftHandle() throws Exception {
        JGroupsRaftEndpoint endpoint = getMandatoryEndpoint(JGroupsRaftEndpointTest.CONFIGURED_ENDPOINT_URI, JGroupsRaftEndpoint.class);
        assertEquals(JGroupsRaftEndpointTest.CLUSTER_NAME, endpoint.getClusterName());
        JGroupsRaftEndpoint endpoint1 = getMandatoryEndpoint(JGroupsRaftEndpointTest.CONFIGURED_ENDPOINT_URI1, JGroupsRaftEndpoint.class);
        assertNotNull(endpoint1.getRaftHandle());
        assertEquals(endpoint1.getRaftHandle(), endpoint1.getResolvedRaftHandle());
        JGroupsRaftEndpoint endpoint2 = getMandatoryEndpoint(JGroupsRaftEndpointTest.CONFIGURED_ENDPOINT_URI2, JGroupsRaftEndpoint.class);
        assertEquals(sm, endpoint2.getStateMachine());
    }
}

