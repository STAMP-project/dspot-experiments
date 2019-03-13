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


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JGroupsRaftComponentConfiguredTest extends CamelTestSupport {
    static final String CLUSTER_NAME = "JGroupsRaftComponentConfiguredTest";

    static final String CONFIGURED_ENDPOINT_URI = String.format("my-config-jgroupsraft:%s?raftId=B&channelProperties=raftB.xml", JGroupsRaftComponentConfiguredTest.CLUSTER_NAME);

    static final String CLUSTER_NAME2 = "JGroupsraftComponentConfiguredTest2";

    static final String CONFIGURED_ENDPOINT_URI2 = String.format("my-config-jgroupsraft2:%s?raftId=C&channelProperties=raftXXX.xml", JGroupsRaftComponentConfiguredTest.CLUSTER_NAME2);

    @Test
    public void shouldUseChannelPropertiesAndRaftHandle() {
        JGroupsRaftEndpoint endpoint = getMandatoryEndpoint(JGroupsRaftComponentConfiguredTest.CONFIGURED_ENDPOINT_URI, JGroupsRaftEndpoint.class);
        JGroupsRaftComponent component = ((JGroupsRaftComponent) (endpoint.getComponent()));
        JGroupsRaftEndpoint endpoint2 = getMandatoryEndpoint(JGroupsRaftComponentConfiguredTest.CONFIGURED_ENDPOINT_URI2, JGroupsRaftEndpoint.class);
        JGroupsRaftComponent component2 = ((JGroupsRaftComponent) (endpoint2.getComponent()));
        assertNotNull(component);
        assertNotNull(endpoint.getResolvedRaftHandle());
        assertEquals("raftB.xml", endpoint.getChannelProperties());
        assertNotNull(component2);
        assertEquals(endpoint2.getRaftHandle(), endpoint2.getResolvedRaftHandle());
    }
}

