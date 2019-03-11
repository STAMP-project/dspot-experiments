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


import StackVersionListener.UNKNOWN_VERSION;
import State.INSTALLED;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.ServiceComponentNotFoundException;
import org.apache.ambari.server.ServiceNotFoundException;
import org.apache.ambari.server.stack.MasterHostResolver;
import org.apache.ambari.server.stack.upgrade.orchestrate.UpgradeContext;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link AddComponentAction}.
 */
/**
 * Tests {@link AmbariPerformanceRunnable}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AddComponentAction.class, MasterHostResolver.class })
public class AddComponentActionTest extends EasyMockSupport {
    private static final String CANDIDATE_SERVICE = "FOO-SERVICE";

    private static final String CANDIDATE_COMPONENT = "FOO-COMPONENT";

    private static final String NEW_SERVICE = AddComponentActionTest.CANDIDATE_SERVICE;

    private static final String NEW_COMPONENT = "FOO-NEW-COMPONENT";

    private static final String CLUSTER_NAME = "c1";

    private final Map<String, String> m_commandParams = new HashMap<>();

    private final Clusters m_mockClusters = createNiceMock(Clusters.class);

    private final Cluster m_mockCluster = createNiceMock(Cluster.class);

    private final Service m_mockCandidateService = createNiceMock(Service.class);

    private final ServiceComponent m_mockCandidateServiceComponent = createNiceMock(ServiceComponent.class);

    private final UpgradeContext m_mockUpgradeContext = createNiceMock(UpgradeContext.class);

    private final String CANDIDATE_HOST_NAME = "c6401.ambari.apache.org";

    private final Host m_mockHost = createStrictMock(Host.class);

    private final Collection<Host> m_candidateHosts = Lists.newArrayList(m_mockHost);

    private AddComponentAction m_action;

    /**
     * Tests that adding a component during upgrade invokes the correct methods.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddComponentDuringUpgrade() throws Exception {
        expect(m_mockCluster.getService(AddComponentActionTest.NEW_SERVICE)).andReturn(m_mockCandidateService).once();
        expect(m_mockCandidateService.getServiceComponent(AddComponentActionTest.NEW_COMPONENT)).andThrow(new ServiceComponentNotFoundException(AddComponentActionTest.CLUSTER_NAME, AddComponentActionTest.NEW_SERVICE, AddComponentActionTest.NEW_COMPONENT));
        expect(m_mockCandidateService.addServiceComponent(AddComponentActionTest.NEW_COMPONENT)).andReturn(m_mockCandidateServiceComponent).once();
        expect(m_mockHost.getHostName()).andReturn(CANDIDATE_HOST_NAME).atLeastOnce();
        m_mockCandidateServiceComponent.setDesiredState(INSTALLED);
        expectLastCall().once();
        Map<String, ServiceComponentHost> existingSCHs = new HashMap<>();
        expect(m_mockCandidateServiceComponent.getServiceComponentHosts()).andReturn(existingSCHs).once();
        ServiceComponentHost mockServiceComponentHost = createNiceMock(ServiceComponentHost.class);
        expect(m_mockCandidateServiceComponent.addServiceComponentHost(CANDIDATE_HOST_NAME)).andReturn(mockServiceComponentHost).once();
        mockServiceComponentHost.setState(INSTALLED);
        expectLastCall().once();
        mockServiceComponentHost.setDesiredState(INSTALLED);
        expectLastCall().once();
        mockServiceComponentHost.setVersion(UNKNOWN_VERSION);
        expectLastCall().once();
        PowerMock.replay(m_action);
        replayAll();
        m_action.execute(null);
        verifyAll();
    }

    /**
     * Tests that we fail without any candidates.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddComponentDuringUpgradeFailsWithNoCandidates() throws Exception {
        PowerMock.replay(m_action);
        replayAll();
        m_candidateHosts.clear();
        m_action.execute(null);
        verifyAll();
    }

    /**
     * Tests that we fail when the candidateg service isn't installed in the
     * cluster.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddComponentWhereServiceIsNotInstalled() throws Exception {
        expect(m_mockCluster.getService(AddComponentActionTest.NEW_SERVICE)).andThrow(new ServiceNotFoundException(AddComponentActionTest.CLUSTER_NAME, AddComponentActionTest.CANDIDATE_SERVICE)).once();
        PowerMock.replay(m_action);
        replayAll();
        m_action.execute(null);
        verifyAll();
    }
}

