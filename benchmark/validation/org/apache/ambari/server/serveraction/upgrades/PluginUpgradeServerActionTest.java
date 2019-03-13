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


import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.agent.stomp.AgentConfigsHolder;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.stack.upgrade.UpgradePack;
import org.apache.ambari.server.stack.upgrade.orchestrate.UpgradeContext;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.spi.ClusterInformation;
import org.apache.ambari.spi.RepositoryType;
import org.apache.ambari.spi.RepositoryVersion;
import org.apache.ambari.spi.exceptions.UpgradeActionException;
import org.apache.ambari.spi.upgrade.UpgradeAction;
import org.apache.ambari.spi.upgrade.UpgradeActionOperations;
import org.apache.ambari.spi.upgrade.UpgradeActionOperations.ConfigurationChanges;
import org.apache.ambari.spi.upgrade.UpgradeInformation;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link PluginUpgradeServerAction}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginUpgradeServerAction.class })
public class PluginUpgradeServerActionTest extends EasyMockSupport {
    private static final String CLUSTER_NAME = "c1";

    private static final String FOO_SITE = "foo-site";

    private static final String AUTH_USERNAME = "admin";

    private static final String CLASS_NAME = PluginUpgradeServerActionTest.MockUpgradeAction.class.getName();

    private final Map<String, String> m_commandParams = new HashMap<>();

    private final StackId m_stackId = new StackId("FOO-STACK-1.0");

    private final StackInfo m_mockStackInfo = createNiceMock(StackInfo.class);

    private final Clusters m_mockClusters = createNiceMock(Clusters.class);

    private final Cluster m_mockCluster = createNiceMock(Cluster.class);

    private final Config m_mockConfig = createNiceMock(Config.class);

    private final UpgradeContext m_mockUpgradeContext = createNiceMock(UpgradeContext.class);

    private final UpgradePack m_mockUpgradePack = createNiceMock(UpgradePack.class);

    private final URLClassLoader m_mockClassLoader = createNiceMock(URLClassLoader.class);

    private final AmbariMetaInfo m_mockMetaInfo = createNiceMock(AmbariMetaInfo.class);

    private final AmbariManagementController m_mockController = createNiceMock(AmbariManagementController.class);

    private final RepositoryVersion m_repositoryVersion = new RepositoryVersion(1L, "FOO-STACK", "1.0", "FOO-STACK-1.0", "1.0.0.0-b1", RepositoryType.STANDARD);

    private final RepositoryVersionEntity m_mocRepoEntity = createNiceMock(RepositoryVersionEntity.class);

    private final AgentConfigsHolder m_mockAgentConfigsHolder = createNiceMock(AgentConfigsHolder.class);

    private final ConfigHelper m_mockConfigHelper = createNiceMock(ConfigHelper.class);

    private PluginUpgradeServerAction m_action;

    /**
     * Tests that a class can be invoked and its operations performed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExecute() throws Exception {
        // mock out the config stuff
        expect(m_mockCluster.getDesiredConfigByType(PluginUpgradeServerActionTest.FOO_SITE)).andReturn(m_mockConfig).once();
        Map<String, String> configUpdates = new HashMap<>();
        configUpdates.put("property-name", "property-value");
        m_mockConfig.updateProperties(configUpdates);
        expectLastCall().once();
        m_mockConfig.save();
        expectLastCall().once();
        PowerMock.replay(m_action);
        replayAll();
        m_action.execute(null);
        // easymock verify
        verifyAll();
    }

    /**
     * Tests that when a new configuration type is specified in the list of
     * configurattion changes, that the new type is created first.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExecuteAddNewConfiguration() throws Exception {
        // mock two different answers
        expect(m_mockCluster.getDesiredConfigByType(PluginUpgradeServerActionTest.FOO_SITE)).andReturn(null).once();
        expect(m_mockCluster.getDesiredConfigByType(PluginUpgradeServerActionTest.FOO_SITE)).andReturn(m_mockConfig).once();
        m_mockConfigHelper.createConfigType(eq(m_mockCluster), eq(m_stackId), eq(m_mockController), eq(PluginUpgradeServerActionTest.FOO_SITE), eq(new HashMap()), eq(PluginUpgradeServerActionTest.AUTH_USERNAME), eq("Upgrade to 1.0.0.0-b1"));
        expectLastCall();
        Map<String, String> configUpdates = new HashMap<>();
        configUpdates.put("property-name", "property-value");
        m_mockConfig.updateProperties(configUpdates);
        expectLastCall().once();
        m_mockConfig.save();
        expectLastCall().once();
        PowerMock.replay(m_action);
        replayAll();
        m_action.execute(null);
        // easymock verify
        verifyAll();
    }

    /**
     * A mock {@link UpgradeAction} for testing.
     */
    public static class MockUpgradeAction implements UpgradeAction {
        /**
         * {@inheritDoc }
         */
        @Override
        public UpgradeActionOperations getOperations(ClusterInformation clusterInformation, UpgradeInformation upgradeInformation) throws UpgradeActionException {
            List<ConfigurationChanges> allChanges = new ArrayList<>();
            ConfigurationChanges configurationTypeChanges = new ConfigurationChanges(PluginUpgradeServerActionTest.FOO_SITE);
            configurationTypeChanges.set("property-name", "property-value");
            allChanges.add(configurationTypeChanges);
            UpgradeActionOperations upgradeActionOperations = new UpgradeActionOperations();
            upgradeActionOperations.setConfigurationChanges(allChanges).setStandardOutput("Standard Output");
            return upgradeActionOperations;
        }
    }
}

