/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.topology;


import TopologyRequest.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for ClusterTopologyImpl.
 */
@SuppressWarnings("unchecked")
public class ClusterTopologyImplTest {
    private static final String CLUSTER_NAME = "cluster_name";

    private static final long CLUSTER_ID = 1L;

    private static final String predicate = "Hosts/host_name=foo";

    private final Blueprint blueprint = createNiceMock(Blueprint.class);

    private final HostGroup group1 = createNiceMock(HostGroup.class);

    private final HostGroup group2 = createNiceMock(HostGroup.class);

    private final HostGroup group3 = createNiceMock(HostGroup.class);

    private final HostGroup group4 = createNiceMock(HostGroup.class);

    private final Map<String, HostGroupInfo> hostGroupInfoMap = new HashMap<>();

    private final Map<String, HostGroup> hostGroupMap = new HashMap<>();

    private Configuration configuration;

    private Configuration bpconfiguration;

    @Test(expected = InvalidTopologyException.class)
    public void testCreate_duplicateHosts() throws Exception {
        // add a duplicate host
        hostGroupInfoMap.get("group2").addHost("host1");
        ClusterTopologyImplTest.TestTopologyRequest request = new ClusterTopologyImplTest.TestTopologyRequest(Type.PROVISION);
        replayAll();
        // should throw exception due to duplicate host
        new ClusterTopologyImpl(null, request);
    }

    @Test
    public void test_GetHostAssigmentForComponents() throws Exception {
        ClusterTopologyImplTest.TestTopologyRequest request = new ClusterTopologyImplTest.TestTopologyRequest(Type.PROVISION);
        replayAll();
        new ClusterTopologyImpl(null, request).getHostAssignmentsForComponent("component1");
    }

    @Test
    public void testDecidingIfComponentIsHadoopCompatible() throws Exception {
        expect(blueprint.getServiceInfos()).andReturn(Arrays.asList(aHCFSWith(aComponent("ONEFS_CLIENT")), aServiceWith(aComponent("ZOOKEEPER_CLIENT")))).anyTimes();
        replayAll();
        ClusterTopologyImpl topology = new ClusterTopologyImpl(null, new ClusterTopologyImplTest.TestTopologyRequest(Type.PROVISION));
        Assert.assertTrue(topology.isComponentHadoopCompatible("ONEFS_CLIENT"));
        Assert.assertFalse(topology.isComponentHadoopCompatible("ZOOKEEPER_CLIENT"));
    }

    private class TestTopologyRequest implements TopologyRequest {
        private Type type;

        public TestTopologyRequest(Type type) {
            this.type = type;
        }

        public String getClusterName() {
            return ClusterTopologyImplTest.CLUSTER_NAME;
        }

        @Override
        public Long getClusterId() {
            return ClusterTopologyImplTest.CLUSTER_ID;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Blueprint getBlueprint() {
            return blueprint;
        }

        @Override
        public Configuration getConfiguration() {
            return bpconfiguration;
        }

        @Override
        public Map<String, HostGroupInfo> getHostGroupInfo() {
            return hostGroupInfoMap;
        }

        @Override
        public String getDescription() {
            return "Test Request";
        }
    }
}

