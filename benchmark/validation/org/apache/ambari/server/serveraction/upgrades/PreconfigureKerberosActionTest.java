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
package org.apache.ambari.server.serveraction.upgrades;


import Direction.DOWNGRADE;
import SecurityType.NONE;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.StackId;
import org.easymock.EasyMockSupport;
import org.junit.Test;


public class PreconfigureKerberosActionTest extends EasyMockSupport {
    private static final String CLUSTER_NAME = "c1";

    @Test
    public void testSkipWhenDowngrade() throws Exception {
        Injector injector = getInjector();
        Map<String, String> commandParams = getDefaultCommandParams();
        commandParams.put(PreconfigureKerberosAction.UPGRADE_DIRECTION_KEY, DOWNGRADE.name());
        ExecutionCommand executionCommand = createMockExecutionCommand(commandParams);
        replayAll();
        injector.getInstance(AmbariMetaInfo.class).init();
        PreconfigureKerberosAction action = injector.getInstance(PreconfigureKerberosAction.class);
        ConcurrentMap<String, Object> context = new ConcurrentHashMap<>();
        action.setExecutionCommand(executionCommand);
        action.execute(context);
        verifyAll();
    }

    @Test
    public void testSkipWhenNotKerberos() throws Exception {
        Injector injector = getInjector();
        ExecutionCommand executionCommand = createMockExecutionCommand(getDefaultCommandParams());
        Cluster cluster = createMockCluster(NONE, Collections.<Host>emptyList(), Collections.<String, Service>emptyMap(), Collections.<String, List<ServiceComponentHost>>emptyMap(), createNiceMock(StackId.class), Collections.<String, Config>emptyMap());
        Clusters clusters = injector.getInstance(Clusters.class);
        expect(clusters.getCluster(PreconfigureKerberosActionTest.CLUSTER_NAME)).andReturn(cluster).atLeastOnce();
        replayAll();
        injector.getInstance(AmbariMetaInfo.class).init();
        PreconfigureKerberosAction action = injector.getInstance(PreconfigureKerberosAction.class);
        ConcurrentMap<String, Object> context = new ConcurrentHashMap<>();
        action.setExecutionCommand(executionCommand);
        action.execute(context);
        verifyAll();
    }

    private Long hostId = 1L;
}

