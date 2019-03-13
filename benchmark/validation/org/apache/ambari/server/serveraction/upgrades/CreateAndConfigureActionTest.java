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


import CreateAndConfigureTask.PARAMETER_CONFIG_TYPE;
import CreateAndConfigureTask.PARAMETER_KEY_VALUE_PAIRS;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleCommandFactory;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.RequestDAO;
import org.apache.ambari.server.orm.dao.UpgradeDAO;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.stack.upgrade.ConfigUpgradeChangeDefinition.ConfigurationKeyValue;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.ServiceComponentFactory;
import org.apache.ambari.server.state.ServiceComponentHostFactory;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests upgrade-related server side actions
 */
public class CreateAndConfigureActionTest {
    @Inject
    private Injector m_injector;

    @Inject
    private OrmTestHelper m_helper;

    @Inject
    private HostRoleCommandFactory hostRoleCommandFactory;

    @Inject
    private ServiceFactory serviceFactory;

    @Inject
    private ConfigHelper m_configHelper;

    @Inject
    private Clusters clusters;

    @Inject
    private ConfigFactory configFactory;

    @Inject
    private CreateAndConfigureAction action;

    @Inject
    private RequestDAO requestDAO;

    @Inject
    private UpgradeDAO upgradeDAO;

    @Inject
    private ServiceComponentFactory serviceComponentFactory;

    @Inject
    private ServiceComponentHostFactory serviceComponentHostFactory;

    private RepositoryVersionEntity repoVersion2110;

    private RepositoryVersionEntity repoVersion2111;

    private RepositoryVersionEntity repoVersion2200;

    private final Map<String, Map<String, String>> NO_ATTRIBUTES = new HashMap<>();

    /**
     * Tests that a new configuration is created when upgrading across stack when
     * there is no existing configuration with the correct target stack.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNewConfigCreatedWhenUpgradingWithoutChaningStack() throws Exception {
        Cluster c = clusters.getCluster("c1");
        Assert.assertEquals(1, c.getConfigsByType("zoo.cfg").size());
        Map<String, String> properties = new HashMap<String, String>() {
            {
                put("initLimit", "10");
            }
        };
        Config config = createConfig(c, "zoo.cfg", "version2", properties);
        c.addDesiredConfig("user", Collections.singleton(config));
        Assert.assertEquals(2, c.getConfigsByType("zoo.cfg").size());
        List<ConfigurationKeyValue> configurations = new ArrayList<>();
        ConfigurationKeyValue keyValue = new ConfigurationKeyValue();
        configurations.add(keyValue);
        keyValue.key = "initLimit";
        keyValue.value = "11";
        c.setCurrentStackVersion(repoVersion2110.getStackId());
        c.setDesiredStackVersion(repoVersion2111.getStackId());
        createUpgrade(c, repoVersion2111);
        Map<String, String> commandParams = new HashMap<>();
        commandParams.put("clusterName", "c1");
        commandParams.put(PARAMETER_CONFIG_TYPE, "zoo.cfg");
        commandParams.put(PARAMETER_KEY_VALUE_PAIRS, new Gson().toJson(configurations));
        ExecutionCommand executionCommand = getExecutionCommand(commandParams);
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        action.setExecutionCommand(executionCommand);
        action.setHostRoleCommand(hostRoleCommand);
        CommandReport report = action.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(3, c.getConfigsByType("zoo.cfg").size());
        config = c.getDesiredConfigByType("zoo.cfg");
        Assert.assertNotNull(config);
        Assert.assertFalse(StringUtils.equals("version2", config.getTag()));
        Assert.assertEquals("11", config.getProperties().get("initLimit"));
    }
}

