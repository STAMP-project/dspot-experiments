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
package org.apache.ambari.server.actionmanager;


import AgentCommandType.EXECUTION_COMMAND;
import KeyNames.VERSION;
import RoleCommand.INSTALL;
import RoleCommand.START;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.utils.StageUtils;
import org.codehaus.jettison.json.JSONException;
import org.junit.Assert;
import org.junit.Test;


public class ExecutionCommandWrapperTest {
    private static final String HOST1 = "dev01.ambari.apache.org";

    private static final String CLUSTER1 = "c1";

    private static final String CLUSTER_VERSION_TAG = "clusterVersion";

    private static final String SERVICE_VERSION_TAG = "serviceVersion";

    private static final String HOST_VERSION_TAG = "hostVersion";

    private static final String GLOBAL_CONFIG = "global";

    private static final String SERVICE_SITE_CONFIG = "service-site";

    private static final String SERVICE_SITE_NAME1 = "ssn1";

    private static final String SERVICE_SITE_NAME2 = "ssn2";

    private static final String SERVICE_SITE_NAME3 = "ssn3";

    private static final String SERVICE_SITE_NAME4 = "ssn4";

    private static final String SERVICE_SITE_NAME5 = "ssn5";

    private static final String SERVICE_SITE_NAME6 = "ssn6";

    private static final String SERVICE_SITE_VAL1 = "ssv1";

    private static final String SERVICE_SITE_VAL1_S = "ssv1_s";

    private static final String SERVICE_SITE_VAL2 = "ssv2";

    private static final String SERVICE_SITE_VAL2_H = "ssv2_h";

    private static final String SERVICE_SITE_VAL3 = "ssv3";

    private static final String SERVICE_SITE_VAL4 = "ssv4";

    private static final String SERVICE_SITE_VAL5 = "ssv5";

    private static final String SERVICE_SITE_VAL5_S = "ssv5_s";

    private static final String SERVICE_SITE_VAL6_H = "ssv6_h";

    private static final String GLOBAL_NAME1 = "gn1";

    private static final String GLOBAL_NAME2 = "gn2";

    private static final String GLOBAL_CLUSTER_VAL1 = "gcv1";

    private static final String GLOBAL_CLUSTER_VAL2 = "gcv2";

    private static final String GLOBAL_VAL1 = "gv1";

    private static Map<String, String> GLOBAL_CLUSTER;

    private static Map<String, String> SERVICE_SITE_CLUSTER;

    private static Map<String, String> SERVICE_SITE_SERVICE;

    private static Map<String, String> SERVICE_SITE_HOST;

    private static Map<String, Map<String, String>> CONFIG_ATTRIBUTES;

    private static Injector injector;

    private static Clusters clusters;

    private static ConfigFactory configFactory;

    private static ConfigHelper configHelper;

    private static StageFactory stageFactory;

    private static OrmTestHelper ormTestHelper;

    @Test
    public void testGetExecutionCommand() throws AmbariException, JSONException {
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setClusterName(ExecutionCommandWrapperTest.CLUSTER1);
        executionCommand.setTaskId(1);
        executionCommand.setRequestAndStage(1, 1);
        executionCommand.setHostname(ExecutionCommandWrapperTest.HOST1);
        executionCommand.setRole("NAMENODE");
        executionCommand.setRoleParams(Collections.emptyMap());
        executionCommand.setRoleCommand(START);
        executionCommand.setServiceName("HDFS");
        executionCommand.setCommandType(EXECUTION_COMMAND);
        executionCommand.setCommandParams(Collections.emptyMap());
        String json = StageUtils.getGson().toJson(executionCommand, ExecutionCommand.class);
        ExecutionCommandWrapper execCommWrap = new ExecutionCommandWrapper(json);
        ExecutionCommandWrapperTest.injector.injectMembers(execCommWrap);
        ExecutionCommand processedExecutionCommand = execCommWrap.getExecutionCommand();
        Assert.assertNotNull(processedExecutionCommand.getRepositoryFile());
    }

    @Test
    public void testGetMergedConfig() {
        Map<String, String> baseConfig = new HashMap<>();
        baseConfig.put(ExecutionCommandWrapperTest.SERVICE_SITE_NAME1, ExecutionCommandWrapperTest.SERVICE_SITE_VAL1);
        baseConfig.put(ExecutionCommandWrapperTest.SERVICE_SITE_NAME2, ExecutionCommandWrapperTest.SERVICE_SITE_VAL2);
        baseConfig.put(ExecutionCommandWrapperTest.SERVICE_SITE_NAME3, ExecutionCommandWrapperTest.SERVICE_SITE_VAL3);
        baseConfig.put(ExecutionCommandWrapperTest.SERVICE_SITE_NAME4, ExecutionCommandWrapperTest.SERVICE_SITE_VAL4);
        baseConfig.put(ExecutionCommandWrapperTest.SERVICE_SITE_NAME5, ExecutionCommandWrapperTest.SERVICE_SITE_VAL5);
        Map<String, String> overrideConfig = new HashMap<>();
        overrideConfig.put(ExecutionCommandWrapperTest.SERVICE_SITE_NAME2, ExecutionCommandWrapperTest.SERVICE_SITE_VAL2_H);
        overrideConfig.put(ExecutionCommandWrapperTest.SERVICE_SITE_NAME6, ExecutionCommandWrapperTest.SERVICE_SITE_VAL6_H);
        Map<String, String> mergedConfig = ExecutionCommandWrapperTest.configHelper.getMergedConfig(baseConfig, overrideConfig);
        Set<String> configsKeys = new HashSet<>();
        configsKeys.addAll(baseConfig.keySet());
        configsKeys.addAll(overrideConfig.keySet());
        Assert.assertEquals(configsKeys.size(), mergedConfig.size());
        Assert.assertEquals(ExecutionCommandWrapperTest.SERVICE_SITE_VAL1, mergedConfig.get(ExecutionCommandWrapperTest.SERVICE_SITE_NAME1));
        Assert.assertEquals(ExecutionCommandWrapperTest.SERVICE_SITE_VAL2_H, mergedConfig.get(ExecutionCommandWrapperTest.SERVICE_SITE_NAME2));
        Assert.assertEquals(ExecutionCommandWrapperTest.SERVICE_SITE_VAL3, mergedConfig.get(ExecutionCommandWrapperTest.SERVICE_SITE_NAME3));
        Assert.assertEquals(ExecutionCommandWrapperTest.SERVICE_SITE_VAL4, mergedConfig.get(ExecutionCommandWrapperTest.SERVICE_SITE_NAME4));
        Assert.assertEquals(ExecutionCommandWrapperTest.SERVICE_SITE_VAL5, mergedConfig.get(ExecutionCommandWrapperTest.SERVICE_SITE_NAME5));
        Assert.assertEquals(ExecutionCommandWrapperTest.SERVICE_SITE_VAL6_H, mergedConfig.get(ExecutionCommandWrapperTest.SERVICE_SITE_NAME6));
    }

    /**
     * Test that the execution command wrapper properly sets the version
     * information when the cluster is in the INSTALLING state.
     *
     * @throws JSONException
     * 		
     * @throws AmbariException
     * 		
     */
    @Test
    public void testExecutionCommandHasVersionInfoWithoutCurrentClusterVersion() throws AmbariException, JSONException {
        Cluster cluster = ExecutionCommandWrapperTest.clusters.getCluster(ExecutionCommandWrapperTest.CLUSTER1);
        StackId stackId = cluster.getDesiredStackVersion();
        // set the repo version resolved state to verify that the version is not sent
        RepositoryVersionEntity repositoryVersion = ExecutionCommandWrapperTest.ormTestHelper.getOrCreateRepositoryVersion(stackId, "0.1-0000");
        repositoryVersion.setResolved(false);
        ExecutionCommandWrapperTest.ormTestHelper.repositoryVersionDAO.merge(repositoryVersion);
        Service service = cluster.getService("HDFS");
        service.setDesiredRepositoryVersion(repositoryVersion);
        // first try with an INSTALL command - this should not populate version info
        ExecutionCommand executionCommand = new ExecutionCommand();
        Map<String, String> commandParams = new HashMap<>();
        executionCommand.setClusterName(ExecutionCommandWrapperTest.CLUSTER1);
        executionCommand.setTaskId(1);
        executionCommand.setRequestAndStage(1, 1);
        executionCommand.setHostname(ExecutionCommandWrapperTest.HOST1);
        executionCommand.setRole("NAMENODE");
        executionCommand.setRoleParams(Collections.<String, String>emptyMap());
        executionCommand.setRoleCommand(INSTALL);
        executionCommand.setServiceName("HDFS");
        executionCommand.setCommandType(EXECUTION_COMMAND);
        executionCommand.setCommandParams(commandParams);
        String json = StageUtils.getGson().toJson(executionCommand, ExecutionCommand.class);
        ExecutionCommandWrapper execCommWrap = new ExecutionCommandWrapper(json);
        ExecutionCommandWrapperTest.injector.injectMembers(execCommWrap);
        ExecutionCommand processedExecutionCommand = execCommWrap.getExecutionCommand();
        commandParams = processedExecutionCommand.getCommandParams();
        Assert.assertFalse(commandParams.containsKey(VERSION));
        // now try with a START command, but still unresolved
        executionCommand = new ExecutionCommand();
        commandParams = new HashMap<>();
        executionCommand.setClusterName(ExecutionCommandWrapperTest.CLUSTER1);
        executionCommand.setTaskId(1);
        executionCommand.setRequestAndStage(1, 1);
        executionCommand.setHostname(ExecutionCommandWrapperTest.HOST1);
        executionCommand.setRole("NAMENODE");
        executionCommand.setRoleParams(Collections.<String, String>emptyMap());
        executionCommand.setRoleCommand(START);
        executionCommand.setServiceName("HDFS");
        executionCommand.setCommandType(EXECUTION_COMMAND);
        executionCommand.setCommandParams(commandParams);
        json = StageUtils.getGson().toJson(executionCommand, ExecutionCommand.class);
        execCommWrap = new ExecutionCommandWrapper(json);
        ExecutionCommandWrapperTest.injector.injectMembers(execCommWrap);
        processedExecutionCommand = execCommWrap.getExecutionCommand();
        commandParams = processedExecutionCommand.getCommandParams();
        Assert.assertFalse(commandParams.containsKey(VERSION));
        // now that the repositoryVersion is resolved, it should populate the version even
        // though the state is INSTALLING
        repositoryVersion.setResolved(true);
        ExecutionCommandWrapperTest.ormTestHelper.repositoryVersionDAO.merge(repositoryVersion);
        execCommWrap = new ExecutionCommandWrapper(json);
        ExecutionCommandWrapperTest.injector.injectMembers(execCommWrap);
        processedExecutionCommand = execCommWrap.getExecutionCommand();
        commandParams = processedExecutionCommand.getCommandParams();
        Assert.assertEquals("0.1-0000", commandParams.get(VERSION));
    }

    /**
     * Test that the execution command wrapper ignores repository file when there are none to use.
     */
    @Test
    public void testExecutionCommandNoRepositoryFile() throws Exception {
        Cluster cluster = ExecutionCommandWrapperTest.clusters.getCluster(ExecutionCommandWrapperTest.CLUSTER1);
        StackId stackId = cluster.getDesiredStackVersion();
        RepositoryVersionEntity repositoryVersion = ExecutionCommandWrapperTest.ormTestHelper.getOrCreateRepositoryVersion(new StackId("HDP", "0.2"), "0.2-0000");
        repositoryVersion.setResolved(true);// has build number

        Service service = cluster.addService("HIVE", repositoryVersion);
        service.setDesiredRepositoryVersion(repositoryVersion);
        repositoryVersion.addRepoOsEntities(new ArrayList());
        ExecutionCommandWrapperTest.ormTestHelper.repositoryVersionDAO.merge(repositoryVersion);
        // first try with an INSTALL command - this should not populate version info
        ExecutionCommand executionCommand = new ExecutionCommand();
        Map<String, String> commandParams = new HashMap<>();
        executionCommand.setClusterName(ExecutionCommandWrapperTest.CLUSTER1);
        executionCommand.setTaskId(1);
        executionCommand.setRequestAndStage(1, 1);
        executionCommand.setHostname(ExecutionCommandWrapperTest.HOST1);
        executionCommand.setRole("HIVE_SERVER");
        executionCommand.setRoleParams(Collections.<String, String>emptyMap());
        executionCommand.setRoleCommand(INSTALL);
        executionCommand.setServiceName("HIVE");
        executionCommand.setCommandType(EXECUTION_COMMAND);
        executionCommand.setCommandParams(commandParams);
        String json = StageUtils.getGson().toJson(executionCommand, ExecutionCommand.class);
        ExecutionCommandWrapper execCommWrap = new ExecutionCommandWrapper(json);
        ExecutionCommandWrapperTest.injector.injectMembers(execCommWrap);
        ExecutionCommand processedExecutionCommand = execCommWrap.getExecutionCommand();
        commandParams = processedExecutionCommand.getCommandParams();
        Assert.assertFalse(commandParams.containsKey(VERSION));
        // now try with a START command which should populate the version even
        // though the state is INSTALLING
        executionCommand = new ExecutionCommand();
        commandParams = new HashMap<>();
        executionCommand.setClusterName(ExecutionCommandWrapperTest.CLUSTER1);
        executionCommand.setTaskId(1);
        executionCommand.setRequestAndStage(1, 1);
        executionCommand.setHostname(ExecutionCommandWrapperTest.HOST1);
        executionCommand.setRole("HIVE_SERVER");
        executionCommand.setRoleParams(Collections.<String, String>emptyMap());
        executionCommand.setRoleCommand(START);
        executionCommand.setServiceName("HIVE");
        executionCommand.setCommandType(EXECUTION_COMMAND);
        executionCommand.setCommandParams(commandParams);
        json = StageUtils.getGson().toJson(executionCommand, ExecutionCommand.class);
        execCommWrap = new ExecutionCommandWrapper(json);
        ExecutionCommandWrapperTest.injector.injectMembers(execCommWrap);
        processedExecutionCommand = execCommWrap.getExecutionCommand();
        commandParams = processedExecutionCommand.getCommandParams();
        Assert.assertEquals("0.2-0000", commandParams.get(VERSION));
    }
}

