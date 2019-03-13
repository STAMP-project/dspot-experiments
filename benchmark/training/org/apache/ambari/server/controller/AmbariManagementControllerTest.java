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
package org.apache.ambari.server.controller;


import Configuration.PREFIX_DIR;
import ExecutionCommand.KeyNames.COMPONENT_CATEGORY;
import ExecutionCommand.KeyNames.DB_DRIVER_FILENAME;
import ExecutionCommand.KeyNames.GROUP_LIST;
import ExecutionCommand.KeyNames.MYSQL_JDBC_URL;
import ExecutionCommand.KeyNames.ORACLE_JDBC_URL;
import ExecutionCommand.KeyNames.USER_GROUPS;
import ExecutionCommand.KeyNames.USER_LIST;
import HostComponentAdminState.DECOMMISSIONED;
import HostComponentAdminState.INSERVICE;
import HostRoleStatus.PENDING;
import HostState.HEALTHY;
import HostState.HEARTBEAT_LOST;
import HostState.UNHEALTHY;
import HostState.WAITING_FOR_HOST_STATUS_UPDATES;
import MaintenanceState.IMPLIED_FROM_HOST;
import MaintenanceState.IMPLIED_FROM_SERVICE;
import MaintenanceState.OFF;
import MaintenanceState.ON;
import Role.AMBARI_SERVER_ACTION;
import Role.DATANODE;
import Role.GANGLIA_MONITOR;
import Role.GANGLIA_SERVER;
import Role.HBASE_CLIENT;
import Role.HBASE_MASTER;
import Role.HBASE_REGIONSERVER;
import Role.HCAT;
import Role.HDFS_CLIENT;
import Role.HDFS_SERVICE_CHECK;
import Role.HIVE_CLIENT;
import Role.HIVE_METASTORE;
import Role.HIVE_SERVER;
import Role.JOBTRACKER;
import Role.MAPREDUCE_CLIENT;
import Role.MAPREDUCE_SERVICE_CHECK;
import Role.NAMENODE;
import Role.OOZIE_CLIENT;
import Role.OOZIE_SERVER;
import Role.PIG;
import Role.PIG_SERVICE_CHECK;
import Role.RESOURCEMANAGER;
import Role.SECONDARY_NAMENODE;
import Role.SQOOP;
import Role.TASKTRACKER;
import Role.WEBHCAT_SERVER;
import Role.ZOOKEEPER_CLIENT;
import Role.ZOOKEEPER_SERVER;
import RoleCommand.ACTIONEXECUTE;
import RoleCommand.CUSTOM_COMMAND;
import RoleCommand.EXECUTE;
import RoleCommand.INSTALL;
import RoleCommand.START;
import RootService.AMBARI;
import ServiceOsSpecific.Package;
import State.DISABLED;
import State.INIT;
import State.INSTALLED;
import State.INSTALLING;
import State.INSTALL_FAILED;
import State.STARTED;
import State.STOPPING;
import State.UNINSTALLED;
import State.UNKNOWN;
import TaskResourceProvider.TASK_CLUSTER_NAME_PROPERTY_ID;
import TaskResourceProvider.TASK_ID_PROPERTY_ID;
import TaskResourceProvider.TASK_REQUEST_ID_PROPERTY_ID;
import TaskResourceProvider.TASK_STAGE_ID_PROPERTY_ID;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Injector;
import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.ClusterNotFoundException;
import org.apache.ambari.server.DuplicateResourceException;
import org.apache.ambari.server.HostNotFoundException;
import org.apache.ambari.server.ObjectNotFoundException;
import org.apache.ambari.server.ParentObjectNotFoundException;
import org.apache.ambari.server.Role;
import org.apache.ambari.server.RoleCommand;
import org.apache.ambari.server.ServiceNotFoundException;
import org.apache.ambari.server.StackAccessException;
import org.apache.ambari.server.actionmanager.ActionDBAccessor;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.actionmanager.ActionType;
import org.apache.ambari.server.actionmanager.ExecutionCommandWrapper;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.Request;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.actionmanager.StageFactory;
import org.apache.ambari.server.actionmanager.TargetHostType;
import org.apache.ambari.server.actionmanager.org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.internal.ClusterStackVersionResourceProviderTest;
import org.apache.ambari.server.controller.internal.ComponentResourceProviderTest;
import org.apache.ambari.server.controller.internal.DeleteStatusMetaData;
import org.apache.ambari.server.controller.internal.HostResourceProviderTest;
import org.apache.ambari.server.controller.internal.RequestOperationLevel;
import org.apache.ambari.server.controller.internal.RequestResourceFilter;
import org.apache.ambari.server.controller.internal.ServiceResourceProviderTest;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.customactions.ActionDefinition;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.ExecutionCommandDAO;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.StackDAO;
import org.apache.ambari.server.orm.dao.TopologyHostInfoDAO;
import org.apache.ambari.server.orm.dao.WidgetDAO;
import org.apache.ambari.server.orm.dao.WidgetLayoutDAO;
import org.apache.ambari.server.orm.entities.ExecutionCommandEntity;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.StackEntity;
import org.apache.ambari.server.orm.entities.WidgetEntity;
import org.apache.ambari.server.orm.entities.WidgetLayoutEntity;
import org.apache.ambari.server.orm.entities.WidgetLayoutUserWidgetEntity;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.stack.StackManagerMock;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.MaintenanceState;
import org.apache.ambari.server.state.RepositoryInfo;
import org.apache.ambari.server.state.SecurityType;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentFactory;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceComponentHostFactory;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.ServiceOsSpecific;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.server.state.State;
import org.apache.ambari.server.state.configgroup.ConfigGroupFactory;
import org.apache.ambari.server.state.svccomphost.ServiceComponentHostServerActionEvent;
import org.apache.ambari.server.utils.StageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AmbariManagementControllerTest {
    private static final Logger LOG = LoggerFactory.getLogger(AmbariManagementControllerTest.class);

    private static final String STACK_NAME = "HDP";

    private static final String SERVICE_NAME_YARN = "YARN";

    private static final String COMPONENT_NAME_NODEMANAGER = "NODEMANAGER";

    private static final String SERVICE_NAME_HBASE = "HBASE";

    private static final String COMPONENT_NAME_REGIONSERVER = "HBASE_REGIONSERVER";

    private static final String COMPONENT_NAME_DATANODE = "DATANODE";

    private static final String SERVICE_NAME_HIVE = "HIVE";

    private static final String COMPONENT_NAME_HIVE_METASTORE = "HIVE_METASTORE";

    private static final String COMPONENT_NAME_HIVE_SERVER = "HIVE_SERVER";

    private static final String STACK_VERSION = "0.2";

    private static final String NEW_STACK_VERSION = "2.0.6";

    private static final String OS_TYPE = "centos5";

    private static final String REPO_ID = "HDP-1.1.1.16";

    private static final String REPO_NAME = "HDP";

    private static final String PROPERTY_NAME = "hbase.regionserver.msginterval";

    private static final String SERVICE_NAME = "HDFS";

    private static final String FAKE_SERVICE_NAME = "FAKENAGIOS";

    private static final int STACK_VERSIONS_CNT = 17;

    private static final int REPOS_CNT = 3;

    private static final int STACK_PROPERTIES_CNT = 103;

    private static final int STACK_COMPONENTS_CNT = 5;

    private static final int OS_CNT = 2;

    private static final String NON_EXT_VALUE = "XXX";

    private static final String INCORRECT_BASE_URL = "http://incorrect.url";

    private static final String COMPONENT_NAME = "NAMENODE";

    private static final String REQUEST_CONTEXT_PROPERTY = "context";

    private static AmbariManagementController controller;

    private static Clusters clusters;

    private ActionDBAccessor actionDB;

    private static Injector injector;

    private ServiceFactory serviceFactory;

    private ServiceComponentFactory serviceComponentFactory;

    private ServiceComponentHostFactory serviceComponentHostFactory;

    private static AmbariMetaInfo ambariMetaInfo;

    private EntityManager entityManager;

    private ConfigHelper configHelper;

    private ConfigGroupFactory configGroupFactory;

    private OrmTestHelper helper;

    private StageFactory stageFactory;

    private HostDAO hostDAO;

    private TopologyHostInfoDAO topologyHostInfoDAO;

    private HostRoleCommandDAO hostRoleCommandDAO;

    private StackManagerMock stackManagerMock;

    private RepositoryVersionDAO repositoryVersionDAO;

    RepositoryVersionEntity repositoryVersion01;

    RepositoryVersionEntity repositoryVersion02;

    RepositoryVersionEntity repositoryVersion120;

    RepositoryVersionEntity repositoryVersion201;

    RepositoryVersionEntity repositoryVersion206;

    RepositoryVersionEntity repositoryVersion207;

    RepositoryVersionEntity repositoryVersion208;

    RepositoryVersionEntity repositoryVersion220;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCreateClusterSimple() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Set<ClusterResponse> r = AmbariManagementControllerTest.controller.getClusters(Collections.singleton(new ClusterRequest(null, cluster1, null, null)));
        Assert.assertEquals(1, r.size());
        ClusterResponse c = r.iterator().next();
        Assert.assertEquals(cluster1, c.getClusterName());
        try {
            createCluster(cluster1);
            Assert.fail("Duplicate cluster creation should fail");
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    public void testCreateClusterWithHostMapping() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        Set<String> hostNames = new HashSet<>();
        hostNames.add(host1);
        hostNames.add(host2);
        ClusterRequest r = new ClusterRequest(null, cluster1, "HDP-0.1", hostNames);
        try {
            AmbariManagementControllerTest.controller.createCluster(r);
            Assert.fail("Expected create cluster to fail for invalid hosts");
        } catch (Exception e) {
            // Expected
        }
        try {
            AmbariManagementControllerTest.clusters.getCluster(cluster1);
            Assert.fail("Expected to fail for non created cluster");
        } catch (ClusterNotFoundException e) {
            // Expected
        }
        AmbariManagementControllerTest.clusters.addHost(host1);
        AmbariManagementControllerTest.clusters.addHost(host2);
        setOsFamily(AmbariManagementControllerTest.clusters.getHost(host1), "redhat", "6.3");
        setOsFamily(AmbariManagementControllerTest.clusters.getHost(host2), "redhat", "6.3");
        AmbariManagementControllerTest.controller.createCluster(r);
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateClusterWithInvalidRequest1() throws Exception {
        ClusterRequest r = new ClusterRequest(null, null, null, null);
        AmbariManagementControllerTest.controller.createCluster(r);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateClusterWithInvalidRequest2() throws Exception {
        ClusterRequest r = new ClusterRequest(1L, null, null, null);
        AmbariManagementControllerTest.controller.createCluster(r);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateClusterWithInvalidRequest3() throws Exception {
        ClusterRequest r = new ClusterRequest(null, AmbariManagementControllerTest.getUniqueName(), null, null);
        AmbariManagementControllerTest.controller.createCluster(r);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateClusterWithInvalidRequest4() throws Exception {
        ClusterRequest r = new ClusterRequest(null, null, INSTALLING.name(), null, "HDP-1.2.0", null);
        AmbariManagementControllerTest.controller.createCluster(r);
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(r), null);
    }

    @Test
    public void testCreateServicesSimple() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName = "HDFS";
        createService(cluster1, serviceName, repositoryVersion02, INIT);
        Service s = AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName);
        Assert.assertNotNull(s);
        Assert.assertEquals(serviceName, s.getName());
        Assert.assertEquals(cluster1, s.getCluster().getClusterName());
        ServiceRequest req = new ServiceRequest(cluster1, "HDFS", repositoryVersion02.getId(), null, null);
        Set<ServiceResponse> r = ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, Collections.singleton(req));
        Assert.assertEquals(1, r.size());
        ServiceResponse resp = r.iterator().next();
        Assert.assertEquals(serviceName, resp.getServiceName());
        Assert.assertEquals(cluster1, resp.getClusterName());
        Assert.assertEquals(INIT.toString(), resp.getDesiredState());
        Assert.assertEquals("HDP-0.2", resp.getDesiredStackId());
    }

    @Test
    public void testCreateServicesWithInvalidRequest() throws Exception, AuthorizationException {
        // invalid request
        // dups in requests
        // multi cluster updates
        Set<ServiceRequest> set1 = new HashSet<>();
        try {
            set1.clear();
            ServiceRequest rInvalid = new ServiceRequest(null, null, null, null, null);
            set1.add(rInvalid);
            ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (Exception e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceRequest rInvalid = new ServiceRequest("foo", null, null, null, null);
            set1.add(rInvalid);
            ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (Exception e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceRequest rInvalid = new ServiceRequest("foo", "bar", null, null, null);
            set1.add(rInvalid);
            ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
            Assert.fail("Expected failure for invalid cluster");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue(checkExceptionType(e, ClusterNotFoundException.class));
        }
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String cluster2 = AmbariManagementControllerTest.getUniqueName();
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.1"));
        AmbariManagementControllerTest.clusters.addCluster(cluster2, new StackId("HDP-0.1"));
        try {
            set1.clear();
            ServiceRequest valid1 = new ServiceRequest(cluster1, "HDFS", null, null, null);
            ServiceRequest valid2 = new ServiceRequest(cluster1, "HDFS", null, null, null);
            set1.add(valid1);
            set1.add(valid2);
            ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (Exception e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceRequest valid1 = new ServiceRequest(cluster1, "bar", repositoryVersion02.getId(), STARTED.toString(), null);
            set1.add(valid1);
            ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
            Assert.fail("Expected failure for invalid service");
        } catch (Exception e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceRequest valid1 = new ServiceRequest(cluster1, "HDFS", repositoryVersion02.getId(), STARTED.toString(), null);
            ServiceRequest valid2 = new ServiceRequest(cluster2, "HDFS", repositoryVersion02.getId(), STARTED.toString(), null);
            set1.add(valid1);
            set1.add(valid2);
            ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
            Assert.fail("Expected failure for multiple clusters");
        } catch (Exception e) {
            // Expected
        }
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1));
        Assert.assertEquals(0, AmbariManagementControllerTest.clusters.getCluster(cluster1).getServices().size());
        set1.clear();
        ServiceRequest valid = new ServiceRequest(cluster1, "HDFS", repositoryVersion02.getId(), null, null);
        set1.add(valid);
        ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
        try {
            set1.clear();
            ServiceRequest valid1 = new ServiceRequest(cluster1, "HDFS", repositoryVersion02.getId(), STARTED.toString(), null);
            ServiceRequest valid2 = new ServiceRequest(cluster1, "HDFS", repositoryVersion02.getId(), STARTED.toString(), null);
            set1.add(valid1);
            set1.add(valid2);
            ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
            Assert.fail("Expected failure for existing service");
        } catch (Exception e) {
            // Expected
        }
        Assert.assertEquals(1, AmbariManagementControllerTest.clusters.getCluster(cluster1).getServices().size());
    }

    @Test
    public void testCreateServiceWithInvalidInfo() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName = "HDFS";
        try {
            createService(cluster1, serviceName, INSTALLING);
            Assert.fail("Service creation should fail for invalid state");
        } catch (Exception e) {
            // Expected
        }
        try {
            AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName);
            Assert.fail("Service creation should have failed");
        } catch (Exception e) {
            // Expected
        }
        try {
            createService(cluster1, serviceName, INSTALLED);
            Assert.fail("Service creation should fail for invalid initial state");
        } catch (Exception e) {
            // Expected
        }
        createService(cluster1, serviceName, null);
        String serviceName2 = "MAPREDUCE";
        createService(cluster1, serviceName2, INIT);
        ServiceRequest r = new ServiceRequest(cluster1, null, null, null, null);
        Set<ServiceResponse> response = ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(2, response.size());
        for (ServiceResponse svc : response) {
            Assert.assertTrue(((svc.getServiceName().equals(serviceName)) || (svc.getServiceName().equals(serviceName2))));
            Assert.assertEquals("HDP-0.2", svc.getDesiredStackId());
            Assert.assertEquals(INIT.toString(), svc.getDesiredState());
        }
    }

    @Test
    public void testCreateServicesMultiple() throws Exception, AuthorizationException {
        Set<ServiceRequest> set1 = new HashSet<>();
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.1"));
        ServiceRequest valid1 = new ServiceRequest(cluster1, "HDFS", repositoryVersion01.getId(), null, null);
        ServiceRequest valid2 = new ServiceRequest(cluster1, "MAPREDUCE", repositoryVersion01.getId(), null, null);
        set1.add(valid1);
        set1.add(valid2);
        ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
        try {
            valid1 = new ServiceRequest(cluster1, "PIG", repositoryVersion01.getId(), null, null);
            valid2 = new ServiceRequest(cluster1, "MAPREDUCE", 4L, null, null);
            set1.add(valid1);
            set1.add(valid2);
            ServiceResourceProviderTest.createServices(AmbariManagementControllerTest.controller, repositoryVersionDAO, set1);
            Assert.fail("Expected failure for invalid services");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue(checkExceptionType(e, DuplicateResourceException.class));
        }
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1));
        Assert.assertEquals(2, AmbariManagementControllerTest.clusters.getCluster(cluster1).getServices().size());
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService("HDFS"));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService("MAPREDUCE"));
    }

    @Test
    public void testCreateServiceComponentSimple() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName = "NAMENODE";
        try {
            createServiceComponent(cluster1, serviceName, componentName, INSTALLING);
            Assert.fail("ServiceComponent creation should fail for invalid state");
        } catch (Exception e) {
            // Expected
        }
        try {
            AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName);
            Assert.fail("ServiceComponent creation should have failed");
        } catch (Exception e) {
            // Expected
        }
        createServiceComponent(cluster1, serviceName, componentName, INIT);
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName));
        ServiceComponentRequest r = new ServiceComponentRequest(cluster1, serviceName, null, null);
        Set<ServiceComponentResponse> response = ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(1, response.size());
        ServiceComponentResponse sc = response.iterator().next();
        Assert.assertEquals(INIT.toString(), sc.getDesiredState());
        Assert.assertEquals(componentName, sc.getComponentName());
        Assert.assertEquals(cluster1, sc.getClusterName());
        Assert.assertEquals(serviceName, sc.getServiceName());
    }

    @Test
    public void testCreateServiceComponentWithInvalidRequest() throws Exception, AuthorizationException {
        // multiple clusters
        // dup objects
        // existing components
        // invalid request params
        // invalid service
        // invalid cluster
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String cluster2 = AmbariManagementControllerTest.getUniqueName();
        Set<ServiceComponentRequest> set1 = new HashSet<>();
        try {
            set1.clear();
            ServiceComponentRequest rInvalid = new ServiceComponentRequest(null, null, null, null);
            set1.add(rInvalid);
            ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (Exception e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentRequest rInvalid = new ServiceComponentRequest(cluster1, null, null, null);
            set1.add(rInvalid);
            ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (Exception e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentRequest rInvalid = new ServiceComponentRequest(cluster1, "s1", null, null);
            set1.add(rInvalid);
            ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (Exception e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentRequest rInvalid = new ServiceComponentRequest(cluster1, "s1", "sc1", null);
            set1.add(rInvalid);
            ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for invalid cluster");
        } catch (ParentObjectNotFoundException e) {
            // Expected
        }
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.1"));
        AmbariManagementControllerTest.clusters.addCluster(cluster2, new StackId("HDP-0.1"));
        try {
            set1.clear();
            ServiceComponentRequest rInvalid = new ServiceComponentRequest(cluster1, "HDFS", "NAMENODE", null);
            set1.add(rInvalid);
            ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for invalid service");
        } catch (ParentObjectNotFoundException e) {
            // Expected
        }
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        StackId stackId = new StackId("HDP-0.1");
        c1.setDesiredStackVersion(stackId);
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        Service s1 = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        Service s2 = serviceFactory.createNew(c1, "MAPREDUCE", repositoryVersion);
        c1.addService(s1);
        c1.addService(s2);
        set1.clear();
        ServiceComponentRequest valid1 = new ServiceComponentRequest(cluster1, "HDFS", "NAMENODE", null);
        ServiceComponentRequest valid2 = new ServiceComponentRequest(cluster1, "MAPREDUCE", "JOBTRACKER", null);
        ServiceComponentRequest valid3 = new ServiceComponentRequest(cluster1, "MAPREDUCE", "TASKTRACKER", null);
        set1.add(valid1);
        set1.add(valid2);
        set1.add(valid3);
        ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
        try {
            set1.clear();
            ServiceComponentRequest rInvalid1 = new ServiceComponentRequest(cluster1, "HDFS", "HDFS_CLIENT", null);
            ServiceComponentRequest rInvalid2 = new ServiceComponentRequest(cluster1, "HDFS", "HDFS_CLIENT", null);
            set1.add(rInvalid1);
            set1.add(rInvalid2);
            ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for dups in requests");
        } catch (Exception e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentRequest rInvalid1 = new ServiceComponentRequest(cluster1, "HDFS", "HDFS_CLIENT", null);
            ServiceComponentRequest rInvalid2 = new ServiceComponentRequest(cluster2, "HDFS", "HDFS_CLIENT", null);
            set1.add(rInvalid1);
            set1.add(rInvalid2);
            ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for multiple clusters");
        } catch (Exception e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentRequest rInvalid = new ServiceComponentRequest(cluster1, "HDFS", "NAMENODE", null);
            set1.add(rInvalid);
            ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for already existing component");
        } catch (Exception e) {
            // Expected
        }
        Assert.assertEquals(1, s1.getServiceComponents().size());
        Assert.assertNotNull(s1.getServiceComponent("NAMENODE"));
        Assert.assertEquals(2, s2.getServiceComponents().size());
        Assert.assertNotNull(s2.getServiceComponent("JOBTRACKER"));
        Assert.assertNotNull(s2.getServiceComponent("TASKTRACKER"));
    }

    @Test
    public void testGetExecutionCommand() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        createServiceComponentHostSimple(cluster1, host1, AmbariManagementControllerTest.getUniqueName());
        String serviceName = "HDFS";
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s1 = cluster.getService(serviceName);
        // Create and attach config
        Map<String, String> configs = new HashMap<>();
        configs.put("a", "b");
        Map<String, String> hadoopEnvConfigs = new HashMap<>();
        hadoopEnvConfigs.put("hdfs_user", "myhdfsuser");
        hadoopEnvConfigs.put("hdfs_group", "myhdfsgroup");
        ConfigurationRequest cr1;
        ConfigurationRequest cr2;
        ConfigurationRequest cr3;
        cr1 = new ConfigurationRequest(cluster1, "core-site", "version1", configs, null);
        cr2 = new ConfigurationRequest(cluster1, "hdfs-site", "version1", configs, null);
        cr3 = new ConfigurationRequest(cluster1, "hadoop-env", "version1", hadoopEnvConfigs, null);
        ClusterRequest crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr1));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr2));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr3));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        // Install
        installService(cluster1, serviceName, false, false);
        ExecutionCommand ec = AmbariManagementControllerTest.controller.getExecutionCommand(cluster, s1.getServiceComponent("NAMENODE").getServiceComponentHost(host1), START);
        Assert.assertEquals("1-0", ec.getCommandId());
        Assert.assertEquals(cluster1, ec.getClusterName());
        Map<String, Map<String, String>> configurations = ec.getConfigurations();
        Assert.assertNotNull(configurations);
        Assert.assertEquals(0, configurations.size());
        Assert.assertTrue(ec.getCommandParams().containsKey("max_duration_for_retries"));
        Assert.assertEquals("0", ec.getCommandParams().get("max_duration_for_retries"));
        Assert.assertTrue(ec.getCommandParams().containsKey("command_retry_enabled"));
        Assert.assertEquals("false", ec.getCommandParams().get("command_retry_enabled"));
        Assert.assertFalse(ec.getCommandParams().containsKey("custom_folder"));
        ec = AmbariManagementControllerTest.controller.getExecutionCommand(cluster, s1.getServiceComponent("DATANODE").getServiceComponentHost(host1), START);
        Assert.assertEquals(cluster1, ec.getClusterName());
        Assert.assertNotNull(ec.getCommandParams());
        Assert.assertNotNull(ec.getHostLevelParams());
        Assert.assertTrue(ec.getHostLevelParams().containsKey(USER_LIST));
        Assert.assertEquals("[\"myhdfsuser\"]", ec.getHostLevelParams().get(USER_LIST));
        Assert.assertTrue(ec.getHostLevelParams().containsKey(GROUP_LIST));
        Assert.assertEquals("[\"myhdfsgroup\"]", ec.getHostLevelParams().get(GROUP_LIST));
        Assert.assertTrue(ec.getHostLevelParams().containsKey(USER_GROUPS));
        Assert.assertEquals("{\"myhdfsuser\":[\"myhdfsgroup\"]}", ec.getHostLevelParams().get(USER_GROUPS));
    }

    @Test
    public void testCreateServiceComponentMultiple() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String cluster2 = AmbariManagementControllerTest.getUniqueName();
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.2"));
        AmbariManagementControllerTest.clusters.addCluster(cluster2, new StackId("HDP-0.2"));
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        StackId stackId = new StackId("HDP-0.2");
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        Service s1 = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        Service s2 = serviceFactory.createNew(c1, "MAPREDUCE", repositoryVersion);
        c1.addService(s1);
        c1.addService(s2);
        Set<ServiceComponentRequest> set1 = new HashSet<>();
        ServiceComponentRequest valid1 = new ServiceComponentRequest(cluster1, "HDFS", "NAMENODE", null);
        ServiceComponentRequest valid2 = new ServiceComponentRequest(cluster1, "MAPREDUCE", "JOBTRACKER", null);
        ServiceComponentRequest valid3 = new ServiceComponentRequest(cluster1, "MAPREDUCE", "TASKTRACKER", null);
        set1.add(valid1);
        set1.add(valid2);
        set1.add(valid3);
        ComponentResourceProviderTest.createComponents(AmbariManagementControllerTest.controller, set1);
        Assert.assertEquals(1, c1.getService("HDFS").getServiceComponents().size());
        Assert.assertEquals(2, c1.getService("MAPREDUCE").getServiceComponents().size());
        Assert.assertNotNull(c1.getService("HDFS").getServiceComponent("NAMENODE"));
        Assert.assertNotNull(c1.getService("MAPREDUCE").getServiceComponent("JOBTRACKER"));
        Assert.assertNotNull(c1.getService("MAPREDUCE").getServiceComponent("TASKTRACKER"));
    }

    @Test
    public void testCreateServiceComponentHostSimple1() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        createServiceComponentHostSimple(cluster1, host1, host2);
    }

    @Test
    public void testCreateServiceComponentHostMultiple() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        Set<ServiceComponentHostRequest> set1 = new HashSet<>();
        ServiceComponentHostRequest r1 = new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, INIT.toString());
        ServiceComponentHostRequest r2 = new ServiceComponentHostRequest(cluster1, serviceName, componentName2, host1, INIT.toString());
        ServiceComponentHostRequest r3 = new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host2, INIT.toString());
        ServiceComponentHostRequest r4 = new ServiceComponentHostRequest(cluster1, serviceName, componentName2, host2, INIT.toString());
        set1.add(r1);
        set1.add(r2);
        set1.add(r3);
        set1.add(r4);
        AmbariManagementControllerTest.controller.createHostComponents(set1);
        Assert.assertEquals(2, AmbariManagementControllerTest.clusters.getCluster(cluster1).getServiceComponentHosts(host1).size());
        Assert.assertEquals(2, AmbariManagementControllerTest.clusters.getCluster(cluster1).getServiceComponentHosts(host2).size());
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName1).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName1).getServiceComponentHost(host2));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host2));
    }

    @Test(expected = AmbariException.class)
    public void testCreateServiceComponentHostExclusiveAmbariException() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "EXCLUSIVE_DEPENDENCY_COMPONENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        Set<ServiceComponentHostRequest> set1 = new HashSet<>();
        ServiceComponentHostRequest r1 = new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, INIT.toString());
        ServiceComponentHostRequest r2 = new ServiceComponentHostRequest(cluster1, serviceName, componentName3, host1, INIT.toString());
        ServiceComponentHostRequest r3 = new ServiceComponentHostRequest(cluster1, serviceName, componentName2, host1, INIT.toString());
        set1.add(r1);
        set1.add(r2);
        set1.add(r3);
        AmbariManagementControllerTest.controller.createHostComponents(set1);
    }

    @Test
    public void testCreateServiceComponentHostWithInvalidRequest() throws Exception, AuthorizationException {
        // multiple clusters
        // dup objects
        // existing components
        // invalid request params
        // invalid service
        // invalid cluster
        // invalid component
        // invalid host
        Set<ServiceComponentHostRequest> set1 = new HashSet<>();
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid = new ServiceComponentHostRequest(null, null, null, null, null);
            set1.add(rInvalid);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid = new ServiceComponentHostRequest("foo", null, null, null, null);
            set1.add(rInvalid);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid = new ServiceComponentHostRequest("foo", "HDFS", null, null, null);
            set1.add(rInvalid);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid = new ServiceComponentHostRequest("foo", "HDFS", "NAMENODE", null, null);
            set1.add(rInvalid);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for invalid requests");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        String host3 = AmbariManagementControllerTest.getUniqueName();
        String clusterFoo = AmbariManagementControllerTest.getUniqueName();
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String cluster2 = AmbariManagementControllerTest.getUniqueName();
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host1, null);
            set1.add(rInvalid);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for invalid cluster");
        } catch (ParentObjectNotFoundException e) {
            // Expected
        }
        AmbariManagementControllerTest.clusters.addCluster(clusterFoo, new StackId("HDP-0.2"));
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.2"));
        AmbariManagementControllerTest.clusters.addCluster(cluster2, new StackId("HDP-0.2"));
        Cluster foo = AmbariManagementControllerTest.clusters.getCluster(clusterFoo);
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Cluster c2 = AmbariManagementControllerTest.clusters.getCluster(cluster2);
        StackId stackId = new StackId("HDP-0.2");
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        foo.setDesiredStackVersion(stackId);
        foo.setCurrentStackVersion(stackId);
        stackId = new StackId("HDP-0.2");
        c1.setDesiredStackVersion(stackId);
        c1.setCurrentStackVersion(stackId);
        stackId = new StackId("HDP-0.2");
        c2.setDesiredStackVersion(stackId);
        c2.setCurrentStackVersion(stackId);
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host1, null);
            set1.add(rInvalid);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for invalid service");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        Service s1 = serviceFactory.createNew(foo, "HDFS", repositoryVersion);
        foo.addService(s1);
        Service s2 = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(s2);
        Service s3 = serviceFactory.createNew(c2, "HDFS", repositoryVersion);
        c2.addService(s3);
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host1, null);
            set1.add(rInvalid);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for invalid service");
        } catch (Exception e) {
            // Expected
        }
        ServiceComponent sc1 = serviceComponentFactory.createNew(s1, "NAMENODE");
        s1.addServiceComponent(sc1);
        ServiceComponent sc2 = serviceComponentFactory.createNew(s2, "NAMENODE");
        s2.addServiceComponent(sc2);
        ServiceComponent sc3 = serviceComponentFactory.createNew(s3, "NAMENODE");
        s3.addServiceComponent(sc3);
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host1, null);
            set1.add(rInvalid);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for invalid host");
        } catch (Exception e) {
            // Expected
        }
        AmbariManagementControllerTest.clusters.addHost(host1);
        Host h1 = AmbariManagementControllerTest.clusters.getHost(host1);
        h1.setIPv4("ipv41");
        h1.setIPv6("ipv61");
        setOsFamily(h1, "redhat", "6.3");
        AmbariManagementControllerTest.clusters.addHost(host2);
        Host h2 = AmbariManagementControllerTest.clusters.getHost(host2);
        h2.setIPv4("ipv42");
        h2.setIPv6("ipv62");
        setOsFamily(h2, "redhat", "6.3");
        AmbariManagementControllerTest.clusters.addHost(host3);
        Host h3 = AmbariManagementControllerTest.clusters.getHost(host3);
        h3.setIPv4("ipv43");
        h3.setIPv6("ipv63");
        setOsFamily(h3, "redhat", "6.3");
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host1, null);
            set1.add(rInvalid);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for invalid host cluster mapping");
        } catch (Exception e) {
            // Expected
        }
        Set<String> hostnames = new HashSet<>();
        hostnames.add(host1);
        hostnames.add(host2);
        hostnames.add(host3);
        AmbariManagementControllerTest.clusters.mapAndPublishHostsToCluster(hostnames, clusterFoo);
        AmbariManagementControllerTest.clusters.mapAndPublishHostsToCluster(hostnames, cluster1);
        AmbariManagementControllerTest.clusters.mapAndPublishHostsToCluster(hostnames, cluster2);
        AmbariManagementControllerTest.clusters.updateHostMappings(AmbariManagementControllerTest.clusters.getHost(host1));
        AmbariManagementControllerTest.clusters.updateHostMappings(AmbariManagementControllerTest.clusters.getHost(host2));
        AmbariManagementControllerTest.clusters.updateHostMappings(AmbariManagementControllerTest.clusters.getHost(host3));
        set1.clear();
        ServiceComponentHostRequest valid = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host1, null);
        set1.add(valid);
        AmbariManagementControllerTest.controller.createHostComponents(set1);
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid1 = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host2, null);
            ServiceComponentHostRequest rInvalid2 = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host2, null);
            set1.add(rInvalid1);
            set1.add(rInvalid2);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for dup requests");
        } catch (DuplicateResourceException e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid1 = new ServiceComponentHostRequest(cluster1, "HDFS", "NAMENODE", host2, null);
            ServiceComponentHostRequest rInvalid2 = new ServiceComponentHostRequest(cluster2, "HDFS", "NAMENODE", host3, null);
            set1.add(rInvalid1);
            set1.add(rInvalid2);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for multiple clusters");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            set1.clear();
            ServiceComponentHostRequest rInvalid1 = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host1, null);
            ServiceComponentHostRequest rInvalid2 = new ServiceComponentHostRequest(clusterFoo, "HDFS", "NAMENODE", host2, null);
            set1.add(rInvalid1);
            set1.add(rInvalid2);
            AmbariManagementControllerTest.controller.createHostComponents(set1);
            Assert.fail("Expected failure for already existing");
        } catch (DuplicateResourceException e) {
            // Expected
        }
        Assert.assertEquals(1, foo.getServiceComponentHosts(host1).size());
        Assert.assertEquals(0, foo.getServiceComponentHosts(host2).size());
        Assert.assertEquals(0, foo.getServiceComponentHosts(host3).size());
        set1.clear();
        ServiceComponentHostRequest valid1 = new ServiceComponentHostRequest(cluster1, "HDFS", "NAMENODE", host1, null);
        set1.add(valid1);
        AmbariManagementControllerTest.controller.createHostComponents(set1);
        set1.clear();
        ServiceComponentHostRequest valid2 = new ServiceComponentHostRequest(cluster2, "HDFS", "NAMENODE", host1, null);
        set1.add(valid2);
        AmbariManagementControllerTest.controller.createHostComponents(set1);
        Assert.assertEquals(1, foo.getServiceComponentHosts(host1).size());
        Assert.assertEquals(1, c1.getServiceComponentHosts(host1).size());
        Assert.assertEquals(1, c2.getServiceComponentHosts(host1).size());
    }

    @Test
    public void testCreateHostSimple() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        HostRequest r1 = new HostRequest(host1, null);
        r1.toString();
        Set<HostRequest> requests = new HashSet<>();
        requests.add(r1);
        try {
            HostResourceProviderTest.createHosts(AmbariManagementControllerTest.controller, requests);
            Assert.fail("Create host should fail for non-bootstrapped host");
        } catch (Exception e) {
            // Expected
        }
        AmbariManagementControllerTest.clusters.addHost(host1);
        AmbariManagementControllerTest.clusters.addHost(host2);
        setOsFamily(AmbariManagementControllerTest.clusters.getHost(host1), "redhat", "5.9");
        setOsFamily(AmbariManagementControllerTest.clusters.getHost(host2), "redhat", "5.9");
        HostRequest request = new HostRequest(host2, "foo");
        requests.add(request);
        try {
            HostResourceProviderTest.createHosts(AmbariManagementControllerTest.controller, requests);
            Assert.fail("Create host should fail for invalid clusters");
        } catch (Exception e) {
            // Expected
        }
        request.setClusterName(cluster1);
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.1"));
        Cluster c = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        StackId stackId = new StackId("HDP-0.1");
        c.setDesiredStackVersion(stackId);
        c.setCurrentStackVersion(stackId);
        helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        HostResourceProviderTest.createHosts(AmbariManagementControllerTest.controller, requests);
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getHost(host2));
        Assert.assertEquals(0, AmbariManagementControllerTest.clusters.getClustersForHost(host1).size());
        Assert.assertEquals(1, AmbariManagementControllerTest.clusters.getClustersForHost(host2).size());
    }

    @Test
    public void testCreateHostMultiple() throws Exception {
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        String host3 = AmbariManagementControllerTest.getUniqueName();
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        AmbariManagementControllerTest.clusters.addHost(host1);
        AmbariManagementControllerTest.clusters.addHost(host2);
        AmbariManagementControllerTest.clusters.addHost(host3);
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.1"));
        Cluster c = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        StackId stackID = new StackId("HDP-0.1");
        c.setDesiredStackVersion(stackID);
        c.setCurrentStackVersion(stackID);
        helper.getOrCreateRepositoryVersion(stackID, stackID.getStackVersion());
        setOsFamily(AmbariManagementControllerTest.clusters.getHost(host1), "redhat", "5.9");
        setOsFamily(AmbariManagementControllerTest.clusters.getHost(host2), "redhat", "5.9");
        setOsFamily(AmbariManagementControllerTest.clusters.getHost(host3), "redhat", "5.9");
        HostRequest r1 = new HostRequest(host1, cluster1);
        HostRequest r2 = new HostRequest(host2, cluster1);
        HostRequest r3 = new HostRequest(host3, null);
        Set<HostRequest> set1 = new HashSet<>();
        set1.add(r1);
        set1.add(r2);
        set1.add(r3);
        HostResourceProviderTest.createHosts(AmbariManagementControllerTest.controller, set1);
        Assert.assertEquals(1, AmbariManagementControllerTest.clusters.getClustersForHost(host1).size());
        Assert.assertEquals(1, AmbariManagementControllerTest.clusters.getClustersForHost(host2).size());
        Assert.assertEquals(0, AmbariManagementControllerTest.clusters.getClustersForHost(host3).size());
    }

    @Test
    public void testCreateHostWithInvalidRequests() throws Exception {
        // unknown host
        // invalid clusters
        // duplicate host
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        Set<HostRequest> set1 = new HashSet<>();
        try {
            set1.clear();
            HostRequest rInvalid = new HostRequest(host1, null);
            set1.add(rInvalid);
            HostResourceProviderTest.createHosts(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for invalid host");
        } catch (Exception e) {
            // Expected
        }
        AmbariManagementControllerTest.clusters.addHost(host1);
        try {
            set1.clear();
            HostRequest rInvalid = new HostRequest(host1, cluster1);
            set1.add(rInvalid);
            HostResourceProviderTest.createHosts(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for invalid cluster");
        } catch (Exception e) {
            // Expected
        }
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.1"));
        try {
            set1.clear();
            HostRequest rInvalid1 = new HostRequest(host1, cluster1);
            rInvalid1.setRackInfo(UUID.randomUUID().toString());
            HostRequest rInvalid2 = new HostRequest(host1, cluster1);
            set1.add(rInvalid1);
            set1.add(rInvalid2);
            HostResourceProviderTest.createHosts(AmbariManagementControllerTest.controller, set1);
            Assert.fail("Expected failure for dup requests");
        } catch (Exception e) {
            // Expected
        }
    }

    /**
     * Create a cluster with a service, and verify that the request tasks have the correct output log and error log paths.
     */
    @Test
    public void testRequestStatusLogs() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createServiceComponentHostSimple(cluster1, AmbariManagementControllerTest.getUniqueName(), AmbariManagementControllerTest.getUniqueName());
        String serviceName = "HDFS";
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        for (Host h : AmbariManagementControllerTest.clusters.getHosts()) {
            // Simulate each agent registering and setting the prefix path on its host
            h.setPrefix(PREFIX_DIR);
        }
        Map<String, Config> configs = new HashMap<>();
        Map<String, String> properties = new HashMap<>();
        Map<String, Map<String, String>> propertiesAttributes = new HashMap<>();
        ConfigFactory configFactory = AmbariManagementControllerTest.injector.getInstance(ConfigFactory.class);
        Config c1 = configFactory.createNew(cluster, "hdfs-site", "v1", properties, propertiesAttributes);
        configs.put(c1.getType(), c1);
        ServiceRequest r = new ServiceRequest(cluster1, serviceName, repositoryVersion02.getId(), INSTALLED.toString(), null);
        Set<ServiceRequest> requests = new HashSet<>();
        requests.add(r);
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        RequestStatusResponse trackAction = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        List<ShortTaskStatus> taskStatuses = trackAction.getTasks();
        Assert.assertFalse(taskStatuses.isEmpty());
        for (ShortTaskStatus task : taskStatuses) {
            Assert.assertEquals("Task output logs don't match", ((((Configuration.PREFIX_DIR) + "/output-") + (task.getTaskId())) + ".txt"), task.getOutputLog());
            Assert.assertEquals("Task error logs don't match", ((((Configuration.PREFIX_DIR) + "/errors-") + (task.getTaskId())) + ".txt"), task.getErrorLog());
        }
    }

    @Test
    public void testInstallAndStartService() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        createServiceComponentHostSimple(cluster1, host1, host2);
        String serviceName = "HDFS";
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Map<String, Config> configs = new HashMap<>();
        Map<String, String> properties = new HashMap<>();
        Map<String, Map<String, String>> propertiesAttributes = new HashMap<>();
        properties.put("a", "a1");
        properties.put("b", "b1");
        ConfigFactory configFactory = AmbariManagementControllerTest.injector.getInstance(ConfigFactory.class);
        Config c1 = configFactory.createNew(cluster, "hdfs-site", "v1", properties, propertiesAttributes);
        properties.put("c", cluster1);
        properties.put("d", "d1");
        Config c2 = configFactory.createNew(cluster, "core-site", "v1", properties, propertiesAttributes);
        configFactory.createNew(cluster, "foo-site", "v1", properties, propertiesAttributes);
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        configs.put(c1.getType(), c1);
        configs.put(c2.getType(), c2);
        ServiceRequest r = new ServiceRequest(cluster1, serviceName, repositoryVersion02.getId(), INSTALLED.toString(), null);
        Set<ServiceRequest> requests = new HashSet<>();
        requests.add(r);
        RequestStatusResponse trackAction = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertEquals(INSTALLED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            Assert.assertEquals(INSTALLED, sc.getDesiredState());
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals(INSTALLED, sch.getDesiredState());
                Assert.assertEquals(INIT, sch.getState());
            }
        }
        List<ShortTaskStatus> taskStatuses = trackAction.getTasks();
        Assert.assertEquals(5, taskStatuses.size());
        boolean foundH1NN = false;
        boolean foundH1DN = false;
        boolean foundH2DN = false;
        boolean foundH1CLT = false;
        boolean foundH2CLT = false;
        for (ShortTaskStatus taskStatus : taskStatuses) {
            AmbariManagementControllerTest.LOG.debug("Task dump :{}", taskStatus);
            Assert.assertEquals(INSTALL.toString(), taskStatus.getCommand());
            Assert.assertEquals(PENDING.toString(), taskStatus.getStatus());
            if (taskStatus.getHostName().equals(host1)) {
                if (NAMENODE.toString().equals(taskStatus.getRole())) {
                    foundH1NN = true;
                } else
                    if (DATANODE.toString().equals(taskStatus.getRole())) {
                        foundH1DN = true;
                    } else
                        if (HDFS_CLIENT.toString().equals(taskStatus.getRole())) {
                            foundH1CLT = true;
                        } else {
                            Assert.fail("Found invalid role for host h1");
                        }


            } else
                if (taskStatus.getHostName().equals(host2)) {
                    if (DATANODE.toString().equals(taskStatus.getRole())) {
                        foundH2DN = true;
                    } else
                        if (HDFS_CLIENT.toString().equals(taskStatus.getRole())) {
                            foundH2CLT = true;
                        } else {
                            Assert.fail("Found invalid role for host h2");
                        }

                } else {
                    Assert.fail("Found invalid host in task list");
                }

        }
        Assert.assertTrue(((((foundH1DN && foundH1NN) && foundH2DN) && foundH1CLT) && foundH2CLT));
        // TODO validate stages?
        List<Stage> stages = actionDB.getAllStages(trackAction.getRequestId());
        Assert.assertEquals(1, stages.size());
        for (Stage stage : stages) {
            AmbariManagementControllerTest.LOG.info((((("Stage Details for Install Service" + ", stageId=") + (stage.getStageId())) + ", actionId=") + (stage.getActionId())));
            for (String host : stage.getHosts()) {
                for (ExecutionCommandWrapper ecw : stage.getExecutionCommands(host)) {
                    Assert.assertNotNull(ecw.getExecutionCommand().getRepositoryFile());
                }
            }
        }
        org.apache.ambari.server.controller.spi.Request request = PropertyHelper.getReadRequest(TASK_CLUSTER_NAME_PROPERTY_ID, TASK_REQUEST_ID_PROPERTY_ID, TASK_STAGE_ID_PROPERTY_ID);
        Predicate predicate = new PredicateBuilder().property(TASK_REQUEST_ID_PROPERTY_ID).equals(trackAction.getRequestId()).toPredicate();
        List<HostRoleCommandEntity> entities = hostRoleCommandDAO.findAll(request, predicate);
        Assert.assertEquals(5, entities.size());
        // !!! pick any entity to make sure a request brings back only one
        Long taskId = entities.get(0).getTaskId();
        predicate = new PredicateBuilder().property(TASK_REQUEST_ID_PROPERTY_ID).equals(trackAction.getRequestId()).and().property(TASK_ID_PROPERTY_ID).equals(taskId).toPredicate();
        entities = hostRoleCommandDAO.findAll(request, predicate);
        Assert.assertEquals(1, entities.size());
        // manually change live state to installed as no running action manager
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                sch.setState(INSTALLED);
            }
        }
        r = new ServiceRequest(cluster1, serviceName, repositoryVersion02.getId(), STARTED.toString(), null);
        requests.clear();
        requests.add(r);
        trackAction = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertEquals(STARTED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            if (sc.getName().equals("HDFS_CLIENT")) {
                Assert.assertEquals(INSTALLED, sc.getDesiredState());
            } else {
                Assert.assertEquals(STARTED, sc.getDesiredState());
            }
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                if (sch.getServiceComponentName().equals("HDFS_CLIENT")) {
                    Assert.assertEquals(INSTALLED, sch.getDesiredState());
                } else {
                    Assert.assertEquals(STARTED, sch.getDesiredState());
                }
            }
        }
        // TODO validate stages?
        stages = actionDB.getAllStages(trackAction.getRequestId());
        Assert.assertEquals(2, stages.size());
        StringBuilder sb = new StringBuilder();
        AmbariManagementControllerTest.clusters.debugDump(sb);
        AmbariManagementControllerTest.LOG.info(("Cluster Dump: " + sb));
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                if (sc.isClientComponent()) {
                    sch.setState(INSTALLED);
                } else {
                    sch.setState(INSTALL_FAILED);
                }
            }
        }
        r = new ServiceRequest(cluster1, serviceName, repositoryVersion02.getId(), INSTALLED.toString(), null);
        requests.clear();
        requests.add(r);
        trackAction = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertEquals(INSTALLED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            Assert.assertEquals(INSTALLED, sc.getDesiredState());
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals(INSTALLED, sch.getDesiredState());
            }
        }
        // TODO validate stages?
        stages = actionDB.getAllStages(trackAction.getRequestId());
        Assert.assertEquals(1, stages.size());
    }

    @Test
    public void testGetClusters() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.1"));
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        StackId stackId = new StackId("HDP-0.1");
        c1.setDesiredStackVersion(stackId);
        helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        ClusterRequest r = new ClusterRequest(null, null, null, null);
        Set<ClusterResponse> resp = AmbariManagementControllerTest.controller.getClusters(Collections.singleton(r));
        // !!! many tests are creating clusters, just make sure we have at least one
        Assert.assertFalse(resp.isEmpty());
        boolean found = false;
        for (ClusterResponse cr : resp) {
            if (cr.getClusterName().equals(cluster1)) {
                Assert.assertEquals(c1.getClusterId(), cr.getClusterId());
                Assert.assertEquals(c1.getDesiredStackVersion().getStackId(), cr.getDesiredStackVersion());
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testGetClustersWithFilters() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String cluster2 = AmbariManagementControllerTest.getUniqueName();
        String cluster3 = AmbariManagementControllerTest.getUniqueName();
        String cluster4 = AmbariManagementControllerTest.getUniqueName();
        AmbariManagementControllerTest.clusters.addCluster(cluster1, new StackId("HDP-0.1"));
        AmbariManagementControllerTest.clusters.addCluster(cluster2, new StackId("HDP-0.1"));
        AmbariManagementControllerTest.clusters.addCluster(cluster3, new StackId("HDP-1.2.0"));
        AmbariManagementControllerTest.clusters.addCluster(cluster4, new StackId("HDP-0.1"));
        ClusterRequest r = new ClusterRequest(null, null, null, null);
        Set<ClusterResponse> resp = AmbariManagementControllerTest.controller.getClusters(Collections.singleton(r));
        Assert.assertTrue(((resp.size()) >= 4));
        r = new ClusterRequest(null, cluster1, null, null);
        resp = AmbariManagementControllerTest.controller.getClusters(Collections.singleton(r));
        Assert.assertEquals(1, resp.size());
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Assert.assertEquals(c1.getClusterId(), resp.iterator().next().getClusterId());
        r = new ClusterRequest(null, null, "HDP-0.1", null);
        resp = AmbariManagementControllerTest.controller.getClusters(Collections.singleton(r));
        Assert.assertTrue(((resp.size()) >= 3));
        r = new ClusterRequest(null, null, null, null);
        resp = AmbariManagementControllerTest.controller.getClusters(Collections.singleton(r));
        Assert.assertTrue("Stack ID request is invalid and expect them all", ((resp.size()) > 3));
    }

    @Test
    public void testGetServices() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        StackId stackId = new StackId("HDP-0.1");
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        AmbariManagementControllerTest.clusters.addCluster(cluster1, stackId);
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s1 = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(s1);
        s1.setDesiredState(INSTALLED);
        ServiceRequest r = new ServiceRequest(cluster1, null, null, null, null);
        Set<ServiceResponse> resp = ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, Collections.singleton(r));
        ServiceResponse resp1 = resp.iterator().next();
        Assert.assertEquals(s1.getClusterId(), resp1.getClusterId().longValue());
        Assert.assertEquals(s1.getCluster().getClusterName(), resp1.getClusterName());
        Assert.assertEquals(s1.getName(), resp1.getServiceName());
        Assert.assertEquals("HDP-0.1", s1.getDesiredStackId().getStackId());
        Assert.assertEquals(s1.getDesiredStackId().getStackId(), resp1.getDesiredStackId());
        Assert.assertEquals(INSTALLED.toString(), resp1.getDesiredState());
    }

    @Test
    public void testGetServicesWithFilters() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String cluster2 = AmbariManagementControllerTest.getUniqueName();
        StackId stackId = new StackId("HDP-0.2");
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        AmbariManagementControllerTest.clusters.addCluster(cluster1, stackId);
        AmbariManagementControllerTest.clusters.addCluster(cluster2, stackId);
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Cluster c2 = AmbariManagementControllerTest.clusters.getCluster(cluster2);
        c1.setDesiredStackVersion(stackId);
        c2.setDesiredStackVersion(stackId);
        Service s1 = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        Service s2 = serviceFactory.createNew(c1, "MAPREDUCE", repositoryVersion);
        Service s3 = serviceFactory.createNew(c1, "HBASE", repositoryVersion);
        Service s4 = serviceFactory.createNew(c2, "HIVE", repositoryVersion);
        Service s5 = serviceFactory.createNew(c2, "ZOOKEEPER", repositoryVersion);
        c1.addService(s1);
        c1.addService(s2);
        c1.addService(s3);
        c2.addService(s4);
        c2.addService(s5);
        s1.setDesiredState(INSTALLED);
        s2.setDesiredState(INSTALLED);
        s4.setDesiredState(INSTALLED);
        ServiceRequest r = new ServiceRequest(null, null, null, null, null);
        Set<ServiceResponse> resp;
        try {
            ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, Collections.singleton(r));
            Assert.fail("Expected failure for invalid request");
        } catch (Exception e) {
            // Expected
        }
        r = new ServiceRequest(c1.getClusterName(), null, null, null, null);
        resp = ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(3, resp.size());
        r = new ServiceRequest(c1.getClusterName(), s2.getName(), null, null, null);
        resp = ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(1, resp.size());
        Assert.assertEquals(s2.getName(), resp.iterator().next().getServiceName());
        try {
            r = new ServiceRequest(c2.getClusterName(), s1.getName(), null, null, null);
            ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, Collections.singleton(r));
            Assert.fail("Expected failure for invalid service");
        } catch (Exception e) {
            // Expected
        }
        r = new ServiceRequest(c1.getClusterName(), null, null, "INSTALLED", null);
        resp = ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(2, resp.size());
        r = new ServiceRequest(c2.getClusterName(), null, null, "INIT", null);
        resp = ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(1, resp.size());
        ServiceRequest r1;
        ServiceRequest r2;
        ServiceRequest r3;
        r1 = new ServiceRequest(c1.getClusterName(), null, null, "INSTALLED", null);
        r2 = new ServiceRequest(c2.getClusterName(), null, null, "INIT", null);
        r3 = new ServiceRequest(c2.getClusterName(), null, null, "INIT", null);
        Set<ServiceRequest> reqs = new HashSet<>();
        reqs.addAll(Arrays.asList(r1, r2, r3));
        resp = ServiceResourceProviderTest.getServices(AmbariManagementControllerTest.controller, reqs);
        Assert.assertEquals(3, resp.size());
    }

    @Test
    public void testGetServiceComponents() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        StackId stackId = new StackId("HDP-0.2");
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        AmbariManagementControllerTest.clusters.addCluster(cluster1, stackId);
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        c1.setDesiredStackVersion(stackId);
        Service s1 = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(s1);
        s1.setDesiredState(INSTALLED);
        ServiceComponent sc1 = serviceComponentFactory.createNew(s1, "DATANODE");
        s1.addServiceComponent(sc1);
        sc1.setDesiredState(UNINSTALLED);
        ServiceComponentRequest r = new ServiceComponentRequest(cluster1, s1.getName(), sc1.getName(), null);
        Set<ServiceComponentResponse> resps = ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        ServiceComponentResponse resp = resps.iterator().next();
        Assert.assertEquals(c1.getClusterName(), resp.getClusterName());
        Assert.assertEquals(sc1.getName(), resp.getComponentName());
        Assert.assertEquals(s1.getName(), resp.getServiceName());
        Assert.assertEquals("HDP-0.2", resp.getDesiredStackId());
        Assert.assertEquals(sc1.getDesiredState().toString(), resp.getDesiredState());
        Assert.assertEquals(c1.getClusterId(), resp.getClusterId().longValue());
    }

    @Test
    public void testGetServiceComponentsWithFilters() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String cluster2 = AmbariManagementControllerTest.getUniqueName();
        StackId stackId = new StackId("HDP-0.2");
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        AmbariManagementControllerTest.clusters.addCluster(cluster1, stackId);
        AmbariManagementControllerTest.clusters.addCluster(cluster2, stackId);
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Cluster c2 = AmbariManagementControllerTest.clusters.getCluster(cluster2);
        Service s1 = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        Service s2 = serviceFactory.createNew(c1, "MAPREDUCE", repositoryVersion);
        Service s3 = serviceFactory.createNew(c1, "HBASE", repositoryVersion);
        Service s4 = serviceFactory.createNew(c2, "HIVE", repositoryVersion);
        Service s5 = serviceFactory.createNew(c2, "ZOOKEEPER", repositoryVersion);
        c1.addService(s1);
        c1.addService(s2);
        c1.addService(s3);
        c2.addService(s4);
        c2.addService(s5);
        s1.setDesiredState(INSTALLED);
        s2.setDesiredState(INSTALLED);
        s4.setDesiredState(INSTALLED);
        ServiceComponent sc1 = serviceComponentFactory.createNew(s1, "DATANODE");
        ServiceComponent sc2 = serviceComponentFactory.createNew(s1, "NAMENODE");
        ServiceComponent sc3 = serviceComponentFactory.createNew(s3, "HBASE_REGIONSERVER");
        ServiceComponent sc4 = serviceComponentFactory.createNew(s4, "HIVE_SERVER");
        ServiceComponent sc5 = serviceComponentFactory.createNew(s4, "HIVE_CLIENT");
        ServiceComponent sc6 = serviceComponentFactory.createNew(s4, "MYSQL_SERVER");
        ServiceComponent sc7 = serviceComponentFactory.createNew(s5, "ZOOKEEPER_SERVER");
        ServiceComponent sc8 = serviceComponentFactory.createNew(s5, "ZOOKEEPER_CLIENT");
        s1.addServiceComponent(sc1);
        s1.addServiceComponent(sc2);
        s3.addServiceComponent(sc3);
        s4.addServiceComponent(sc4);
        s4.addServiceComponent(sc5);
        s4.addServiceComponent(sc6);
        s5.addServiceComponent(sc7);
        s5.addServiceComponent(sc8);
        sc1.setDesiredState(UNINSTALLED);
        sc3.setDesiredState(UNINSTALLED);
        sc5.setDesiredState(UNINSTALLED);
        sc6.setDesiredState(UNINSTALLED);
        sc7.setDesiredState(UNINSTALLED);
        sc8.setDesiredState(UNINSTALLED);
        ServiceComponentRequest r = new ServiceComponentRequest(null, null, null, null);
        try {
            ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, Collections.singleton(r));
            Assert.fail("Expected failure for invalid cluster");
        } catch (Exception e) {
            // Expected
        }
        // all comps per cluster
        r = new ServiceComponentRequest(c1.getClusterName(), null, null, null);
        Set<ServiceComponentResponse> resps = ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(3, resps.size());
        // all comps per cluster filter on state
        r = new ServiceComponentRequest(c2.getClusterName(), null, null, UNINSTALLED.toString());
        resps = ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(4, resps.size());
        // all comps for given service
        r = new ServiceComponentRequest(c2.getClusterName(), s5.getName(), null, null);
        resps = ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(2, resps.size());
        // all comps for given service filter by state
        r = new ServiceComponentRequest(c2.getClusterName(), s4.getName(), null, INIT.toString());
        resps = ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        Assert.assertEquals(sc4.getName(), resps.iterator().next().getComponentName());
        // get single given comp
        r = new ServiceComponentRequest(c2.getClusterName(), null, sc5.getName(), INIT.toString());
        resps = ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        Assert.assertEquals(sc5.getName(), resps.iterator().next().getComponentName());
        // get single given comp and given svc
        r = new ServiceComponentRequest(c2.getClusterName(), s4.getName(), sc5.getName(), INIT.toString());
        resps = ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        Assert.assertEquals(sc5.getName(), resps.iterator().next().getComponentName());
        ServiceComponentRequest r1;
        ServiceComponentRequest r2;
        ServiceComponentRequest r3;
        Set<ServiceComponentRequest> reqs = new HashSet<>();
        r1 = new ServiceComponentRequest(c2.getClusterName(), null, null, UNINSTALLED.toString());
        r2 = new ServiceComponentRequest(c1.getClusterName(), null, null, null);
        r3 = new ServiceComponentRequest(c1.getClusterName(), null, null, INIT.toString());
        reqs.addAll(Arrays.asList(r1, r2, r3));
        resps = ComponentResourceProviderTest.getComponents(AmbariManagementControllerTest.controller, reqs);
        Assert.assertEquals(7, resps.size());
    }

    @Test
    public void testGetServiceComponentHosts() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String host1 = AmbariManagementControllerTest.getUniqueName();
        Cluster c1 = setupClusterWithHosts(cluster1, "HDP-0.1", Lists.newArrayList(host1), "centos5");
        RepositoryVersionEntity repositoryVersion = repositoryVersion01;
        Service s1 = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(s1);
        ServiceComponent sc1 = serviceComponentFactory.createNew(s1, "DATANODE");
        s1.addServiceComponent(sc1);
        sc1.setDesiredState(UNINSTALLED);
        ServiceComponentHost sch1 = serviceComponentHostFactory.createNew(sc1, host1);
        sc1.addServiceComponentHost(sch1);
        sch1.setDesiredState(INSTALLED);
        sch1.setState(INSTALLING);
        /* sch1.updateActualConfigs(new HashMap<String, Map<String,String>>() {{
        put("global", new HashMap<String,String>() {{ put("tag", "version1"); }});
        }});
         */
        ServiceComponentHostRequest r = new ServiceComponentHostRequest(c1.getClusterName(), null, null, null, null);
        Set<ServiceComponentHostResponse> resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        ServiceComponentHostResponse resp = resps.iterator().next();
        Assert.assertEquals(c1.getClusterName(), resp.getClusterName());
        Assert.assertEquals(sc1.getName(), resp.getComponentName());
        Assert.assertEquals(s1.getName(), resp.getServiceName());
        Assert.assertEquals(sch1.getHostName(), resp.getHostname());
        Assert.assertEquals(sch1.getDesiredState().toString(), resp.getDesiredState());
        Assert.assertEquals(sch1.getState().toString(), resp.getLiveState());
        Assert.assertEquals(repositoryVersion.getStackId(), sch1.getServiceComponent().getDesiredStackId());
    }

    @Test
    public void testServiceComponentHostsWithDecommissioned() throws Exception {
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        setupClusterWithHosts(cluster1, "HDP-2.0.7", new ArrayList<String>() {
            {
                add(host1);
                add(host2);
            }
        }, "centos5");
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Start
        startService(cluster1, serviceName, false, false);
        Service s1 = AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName);
        s1.getServiceComponent(componentName2).getServiceComponentHost(host1).setComponentAdminState(DECOMMISSIONED);
        s1.getServiceComponent(componentName2).getServiceComponentHost(host2).setComponentAdminState(INSERVICE);
        ServiceComponentHostRequest r = new ServiceComponentHostRequest(cluster1, null, null, null, null);
        Set<ServiceComponentHostResponse> resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(5, resps.size());
        // Get all host components with decommissiond = true
        r = new ServiceComponentHostRequest(cluster1, null, null, null, null);
        r.setAdminState("DECOMMISSIONED");
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        // Get all host components with decommissioned = false
        r = new ServiceComponentHostRequest(cluster1, null, null, null, null);
        r.setAdminState("INSERVICE");
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        // Get all host components with decommissioned = some random string
        r = new ServiceComponentHostRequest(cluster1, null, null, null, null);
        r.setAdminState("INSTALLED");
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(0, resps.size());
        // Update adminState
        r = new ServiceComponentHostRequest(cluster1, "HDFS", "DATANODE", host2, null);
        r.setAdminState("DECOMMISSIONED");
        try {
            updateHostComponents(Collections.singleton(r), new HashMap(), false);
            Assert.fail("Must throw exception when decommission attribute is updated.");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("Property adminState cannot be modified through update"));
        }
    }

    @Test
    public void testHbaseDecommission() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-2.0.7"));
        String serviceName = "HBASE";
        createService(cluster1, serviceName, repositoryVersion207, null);
        String componentName1 = "HBASE_MASTER";
        String componentName2 = "HBASE_REGIONSERVER";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName1, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        RequestOperationLevel level = new RequestOperationLevel(Type.HostComponent, cluster1, null, null, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Start
        startService(cluster1, serviceName, false, false);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = cluster.getService(serviceName);
        Assert.assertEquals(STARTED, s.getDesiredState());
        ServiceComponentHost scHost = s.getServiceComponent("HBASE_REGIONSERVER").getServiceComponentHost(host2);
        Assert.assertEquals(INSERVICE, scHost.getComponentAdminState());
        // Decommission one RS
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("excluded_hosts", host2);
                put("align_maintenance_state", "true");
            }
        };
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HBASE", "HBASE_MASTER", null);
        List<RequestResourceFilter> resourceFilters = new ArrayList<>();
        resourceFilters.add(resourceFilter);
        ExecuteActionRequest request = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, resourceFilters, level, params, false);
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        RequestStatusResponse response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        List<HostRoleCommand> storedTasks = actionDB.getRequestTasks(response.getRequestId());
        ExecutionCommand execCmd = storedTasks.get(0).getExecutionCommandWrapper().getExecutionCommand();
        Assert.assertNotNull(storedTasks);
        Assert.assertEquals(1, storedTasks.size());
        Assert.assertEquals(DECOMMISSIONED, scHost.getComponentAdminState());
        Assert.assertEquals(ON, scHost.getMaintenanceState());
        HostRoleCommand command = storedTasks.get(0);
        Assert.assertTrue("DECOMMISSION".equals(command.getCustomCommandName()));
        Assert.assertTrue(("DECOMMISSION, Excluded: " + host2).equals(command.getCommandDetail()));
        Map<String, String> cmdParams = command.getExecutionCommandWrapper().getExecutionCommand().getCommandParams();
        Assert.assertTrue(cmdParams.containsKey("mark_draining_only"));
        Assert.assertEquals("false", cmdParams.get("mark_draining_only"));
        Assert.assertEquals(HBASE_MASTER, command.getRole());
        Assert.assertEquals(CUSTOM_COMMAND, command.getRoleCommand());
        Assert.assertEquals("DECOMMISSION", execCmd.getCommandParams().get("custom_command"));
        Assert.assertEquals(host2, execCmd.getCommandParams().get("all_decommissioned_hosts"));
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        // RS stops
        s.getServiceComponent("HBASE_REGIONSERVER").getServiceComponentHost(host2).setState(INSTALLED);
        // Remove RS from draining
        params = new HashMap<String, String>() {
            {
                put("excluded_hosts", host2);
                put("mark_draining_only", "true");
                put("slave_type", "HBASE_REGIONSERVER");
                put("align_maintenance_state", "true");
            }
        };
        resourceFilter = new RequestResourceFilter("HBASE", "HBASE_MASTER", null);
        ArrayList<RequestResourceFilter> filters = new ArrayList<>();
        filters.add(resourceFilter);
        request = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, filters, level, params, false);
        response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        storedTasks = actionDB.getRequestTasks(response.getRequestId());
        execCmd = storedTasks.get(0).getExecutionCommandWrapper().getExecutionCommand();
        Assert.assertNotNull(storedTasks);
        Assert.assertEquals(1, storedTasks.size());
        Assert.assertEquals(DECOMMISSIONED, scHost.getComponentAdminState());
        Assert.assertEquals(ON, scHost.getMaintenanceState());
        command = storedTasks.get(0);
        Assert.assertEquals("DECOMMISSION", execCmd.getCommandParams().get("custom_command"));
        Assert.assertEquals(host2, execCmd.getCommandParams().get("all_decommissioned_hosts"));
        Assert.assertTrue("DECOMMISSION".equals(command.getCustomCommandName()));
        Assert.assertTrue(("DECOMMISSION, Excluded: " + host2).equals(command.getCommandDetail()));
        cmdParams = command.getExecutionCommandWrapper().getExecutionCommand().getCommandParams();
        Assert.assertTrue(cmdParams.containsKey("mark_draining_only"));
        Assert.assertEquals("true", cmdParams.get("mark_draining_only"));
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        // Recommission
        params = new HashMap<String, String>() {
            {
                put("included_hosts", host2);
            }
        };
        request = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, resourceFilters, level, params, false);
        response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        storedTasks = actionDB.getRequestTasks(response.getRequestId());
        execCmd = storedTasks.get(0).getExecutionCommandWrapper().getExecutionCommand();
        Assert.assertNotNull(storedTasks);
        Assert.assertEquals(1, storedTasks.size());
        Assert.assertEquals(INSERVICE, scHost.getComponentAdminState());
        Assert.assertEquals(ON, scHost.getMaintenanceState());
        command = storedTasks.get(0);
        Assert.assertTrue("DECOMMISSION".equals(command.getCustomCommandName()));
        Assert.assertTrue(("DECOMMISSION, Included: " + host2).equals(command.getCommandDetail()));
        cmdParams = command.getExecutionCommandWrapper().getExecutionCommand().getCommandParams();
        Assert.assertTrue(cmdParams.containsKey("mark_draining_only"));
        Assert.assertEquals("false", cmdParams.get("mark_draining_only"));
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        Assert.assertTrue(cmdParams.containsKey("excluded_hosts"));
        Assert.assertEquals("", cmdParams.get("excluded_hosts"));
        Assert.assertEquals(HBASE_MASTER, command.getRole());
        Assert.assertEquals(CUSTOM_COMMAND, command.getRoleCommand());
        Assert.assertEquals("DECOMMISSION", execCmd.getCommandParams().get("custom_command"));
    }

    @Test
    public void testGetServiceComponentHostsWithFilters() throws Exception, AuthorizationException {
        final String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        final String host3 = AmbariManagementControllerTest.getUniqueName();
        Cluster c1 = setupClusterWithHosts(cluster1, "HDP-0.2", new ArrayList<String>() {
            {
                add(host1);
                add(host2);
                add(host3);
            }
        }, "centos5");
        RepositoryVersionEntity repositoryVersion = repositoryVersion02;
        Service s1 = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        Service s2 = serviceFactory.createNew(c1, "MAPREDUCE", repositoryVersion);
        Service s3 = serviceFactory.createNew(c1, "HBASE", repositoryVersion);
        c1.addService(s1);
        c1.addService(s2);
        c1.addService(s3);
        s1.setDesiredState(INSTALLED);
        s2.setDesiredState(INSTALLED);
        ServiceComponent sc1 = serviceComponentFactory.createNew(s1, "DATANODE");
        ServiceComponent sc2 = serviceComponentFactory.createNew(s1, "NAMENODE");
        ServiceComponent sc3 = serviceComponentFactory.createNew(s3, "HBASE_REGIONSERVER");
        s1.addServiceComponent(sc1);
        s1.addServiceComponent(sc2);
        s3.addServiceComponent(sc3);
        sc1.setDesiredState(UNINSTALLED);
        sc3.setDesiredState(UNINSTALLED);
        ServiceComponentHost sch1 = serviceComponentHostFactory.createNew(sc1, host1);
        ServiceComponentHost sch2 = serviceComponentHostFactory.createNew(sc1, host2);
        ServiceComponentHost sch3 = serviceComponentHostFactory.createNew(sc1, host3);
        ServiceComponentHost sch4 = serviceComponentHostFactory.createNew(sc2, host1);
        ServiceComponentHost sch5 = serviceComponentHostFactory.createNew(sc2, host2);
        ServiceComponentHost sch6 = serviceComponentHostFactory.createNew(sc3, host3);
        sc1.addServiceComponentHost(sch1);
        sc1.addServiceComponentHost(sch2);
        sc1.addServiceComponentHost(sch3);
        sc2.addServiceComponentHost(sch4);
        sc2.addServiceComponentHost(sch5);
        sc3.addServiceComponentHost(sch6);
        sch1.setDesiredState(INSTALLED);
        sch2.setDesiredState(INIT);
        sch4.setDesiredState(INSTALLED);
        sch5.setDesiredState(UNINSTALLED);
        ServiceComponentHostRequest r = new ServiceComponentHostRequest(null, null, null, null, null);
        try {
            AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
            Assert.fail("Expected failure for invalid cluster");
        } catch (Exception e) {
            // Expected
        }
        // all across cluster
        r = new ServiceComponentHostRequest(c1.getClusterName(), null, null, null, null);
        Set<ServiceComponentHostResponse> resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(6, resps.size());
        // all for service
        r = new ServiceComponentHostRequest(c1.getClusterName(), s1.getName(), null, null, null);
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(5, resps.size());
        // all for component
        r = new ServiceComponentHostRequest(c1.getClusterName(), null, sc3.getName(), null, null);
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        // all for host
        r = new ServiceComponentHostRequest(c1.getClusterName(), null, null, host2, null);
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(2, resps.size());
        // all across cluster with state filter
        r = new ServiceComponentHostRequest(c1.getClusterName(), null, null, null, UNINSTALLED.toString());
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        // all for service with state filter
        r = new ServiceComponentHostRequest(c1.getClusterName(), s1.getName(), null, null, INIT.toString());
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(2, resps.size());
        // all for component with state filter
        r = new ServiceComponentHostRequest(c1.getClusterName(), null, sc3.getName(), null, INSTALLED.toString());
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(0, resps.size());
        // all for host with state filter
        r = new ServiceComponentHostRequest(c1.getClusterName(), null, null, host2, INIT.toString());
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        // for service and host
        r = new ServiceComponentHostRequest(c1.getClusterName(), s3.getName(), null, host1, null);
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(0, resps.size());
        // single sch - given service and host and component
        r = new ServiceComponentHostRequest(c1.getClusterName(), s3.getName(), sc3.getName(), host3, INSTALLED.toString());
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(0, resps.size());
        // single sch - given service and host and component
        r = new ServiceComponentHostRequest(c1.getClusterName(), s3.getName(), sc3.getName(), host3, null);
        resps = AmbariManagementControllerTest.controller.getHostComponents(Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        ServiceComponentHostRequest r1;
        ServiceComponentHostRequest r2;
        ServiceComponentHostRequest r3;
        r1 = new ServiceComponentHostRequest(c1.getClusterName(), null, null, host3, null);
        r2 = new ServiceComponentHostRequest(c1.getClusterName(), s3.getName(), sc3.getName(), host2, null);
        r3 = new ServiceComponentHostRequest(c1.getClusterName(), null, null, host2, null);
        Set<ServiceComponentHostRequest> reqs = new HashSet<>();
        reqs.addAll(Arrays.asList(r1, r2, r3));
        resps = AmbariManagementControllerTest.controller.getHostComponents(reqs);
        Assert.assertEquals(4, resps.size());
    }

    @Test
    public void testGetHosts() throws Exception, AuthorizationException {
        final String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String cluster2 = AmbariManagementControllerTest.getUniqueName();
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        final String host3 = AmbariManagementControllerTest.getUniqueName();
        final String host4 = AmbariManagementControllerTest.getUniqueName();
        setupClusterWithHosts(cluster1, "HDP-0.2", new ArrayList<String>() {
            {
                add(host1);
                add(host2);
            }
        }, "centos5");
        setupClusterWithHosts(cluster2, "HDP-0.2", new ArrayList<String>() {
            {
                add(host3);
            }
        }, "centos5");
        AmbariManagementControllerTest.clusters.addHost(host4);
        setOsFamily(AmbariManagementControllerTest.clusters.getHost(host4), "redhat", "5.9");
        Map<String, String> attrs = new HashMap<>();
        attrs.put("a1", "b1");
        AmbariManagementControllerTest.clusters.getHost(host3).setHostAttributes(attrs);
        attrs.put("a2", "b2");
        AmbariManagementControllerTest.clusters.getHost(host4).setHostAttributes(attrs);
        HostRequest r = new HostRequest(null, null);
        Set<HostResponse> resps = HostResourceProviderTest.getHosts(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Set<String> foundHosts = new HashSet<>();
        for (HostResponse resp : resps) {
            if (resp.getHostname().equals(host1)) {
                Assert.assertEquals(cluster1, resp.getClusterName());
                Assert.assertEquals(2, resp.getHostAttributes().size());
                Assert.assertEquals(OFF, resp.getMaintenanceState());
                foundHosts.add(resp.getHostname());
            } else
                if (resp.getHostname().equals(host2)) {
                    Assert.assertEquals(cluster1, resp.getClusterName());
                    Assert.assertEquals(2, resp.getHostAttributes().size());
                    Assert.assertEquals(OFF, resp.getMaintenanceState());
                    foundHosts.add(resp.getHostname());
                } else
                    if (resp.getHostname().equals(host3)) {
                        Assert.assertEquals(cluster2, resp.getClusterName());
                        Assert.assertEquals(3, resp.getHostAttributes().size());
                        Assert.assertEquals(OFF, resp.getMaintenanceState());
                        foundHosts.add(resp.getHostname());
                    } else
                        if (resp.getHostname().equals(host4)) {
                            // todo: why wouldn't this be null?
                            Assert.assertEquals("", resp.getClusterName());
                            Assert.assertEquals(4, resp.getHostAttributes().size());
                            Assert.assertEquals(null, resp.getMaintenanceState());
                            foundHosts.add(resp.getHostname());
                        }



        }
        Assert.assertEquals(4, foundHosts.size());
        r = new HostRequest(host1, null);
        resps = HostResourceProviderTest.getHosts(AmbariManagementControllerTest.controller, Collections.singleton(r));
        Assert.assertEquals(1, resps.size());
        HostResponse resp = resps.iterator().next();
        Assert.assertEquals(host1, resp.getHostname());
        Assert.assertEquals(cluster1, resp.getClusterName());
        Assert.assertEquals(OFF, resp.getMaintenanceState());
        Assert.assertEquals(2, resp.getHostAttributes().size());
    }

    @Test
    public void testServiceUpdateBasic() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName = "HDFS";
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.2"));
        createService(cluster1, serviceName, INIT);
        Service s = AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName);
        Assert.assertNotNull(s);
        Assert.assertEquals(serviceName, s.getName());
        Assert.assertEquals(INIT, s.getDesiredState());
        Assert.assertEquals(cluster1, s.getCluster().getClusterName());
        Set<ServiceRequest> reqs = new HashSet<>();
        ServiceRequest r;
        try {
            r = new ServiceRequest(cluster1, serviceName, repositoryVersion02.getId(), INSTALLING.toString(), null);
            reqs.clear();
            reqs.add(r);
            ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, reqs, mapRequestProps, true, false);
            Assert.fail("Expected fail for invalid state transition");
        } catch (Exception e) {
            // Expected
        }
        r = new ServiceRequest(cluster1, serviceName, repositoryVersion02.getId(), INSTALLED.toString(), null);
        reqs.clear();
        reqs.add(r);
        RequestStatusResponse trackAction = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, reqs, mapRequestProps, true, false);
        Assert.assertNull(trackAction);
    }

    @Test
    public void testServiceUpdateInvalidRequest() throws Exception, AuthorizationException {
        // multiple clusters
        // dup services
        // multiple diff end states
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String cluster2 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster2);
        String serviceName1 = "HDFS";
        createService(cluster1, serviceName1, null);
        String serviceName2 = "HBASE";
        String serviceName3 = "HBASE";
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        try {
            createService(cluster2, serviceName3, repositoryVersion01, null);
            Assert.fail("Expected fail for invalid service for stack 0.1");
        } catch (Exception e) {
            // Expected
        }
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.2"));
        AmbariManagementControllerTest.clusters.getCluster(cluster2).setDesiredStackVersion(new StackId("HDP-0.2"));
        createService(cluster1, serviceName2, null);
        createService(cluster2, serviceName3, null);
        Set<ServiceRequest> reqs = new HashSet<>();
        ServiceRequest req1;
        ServiceRequest req2;
        try {
            reqs.clear();
            req1 = new ServiceRequest(cluster1, serviceName1, repositoryVersion02.getId(), INSTALLED.toString(), null);
            req2 = new ServiceRequest(cluster2, serviceName2, repositoryVersion02.getId(), INSTALLED.toString(), null);
            reqs.add(req1);
            reqs.add(req2);
            ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, reqs, mapRequestProps, true, false);
            Assert.fail("Expected failure for multi cluster update");
        } catch (Exception e) {
            // Expected
        }
        try {
            reqs.clear();
            req1 = new ServiceRequest(cluster1, serviceName1, repositoryVersion02.getId(), INSTALLED.toString(), null);
            req2 = new ServiceRequest(cluster1, serviceName1, repositoryVersion02.getId(), INSTALLED.toString(), null);
            reqs.add(req1);
            reqs.add(req2);
            ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, reqs, mapRequestProps, true, false);
            Assert.fail("Expected failure for dups services");
        } catch (Exception e) {
            // Expected
        }
        AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName2).setDesiredState(INSTALLED);
        try {
            reqs.clear();
            req1 = new ServiceRequest(cluster1, serviceName1, repositoryVersion02.getId(), INSTALLED.toString(), null);
            req2 = new ServiceRequest(cluster1, serviceName2, repositoryVersion02.getId(), STARTED.toString(), null);
            reqs.add(req1);
            reqs.add(req2);
            ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, reqs, mapRequestProps, true, false);
            Assert.fail("Expected failure for different states");
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    public void testServiceComponentUpdateRecursive() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName1 = "HDFS";
        createService(cluster1, serviceName1, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName1, componentName1, INIT);
        createServiceComponent(cluster1, serviceName1, componentName2, INIT);
        createServiceComponent(cluster1, serviceName1, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        Set<ServiceComponentHostRequest> set1 = new HashSet<>();
        ServiceComponentHostRequest r1 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host1, INIT.toString());
        ServiceComponentHostRequest r2 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName2, host1, INIT.toString());
        ServiceComponentHostRequest r3 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host2, INIT.toString());
        ServiceComponentHostRequest r4 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName2, host2, INIT.toString());
        ServiceComponentHostRequest r5 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName3, host1, INIT.toString());
        set1.add(r1);
        set1.add(r2);
        set1.add(r3);
        set1.add(r4);
        set1.add(r5);
        AmbariManagementControllerTest.controller.createHostComponents(set1);
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s1 = c1.getService(serviceName1);
        ServiceComponent sc1 = s1.getServiceComponent(componentName1);
        ServiceComponent sc2 = s1.getServiceComponent(componentName2);
        ServiceComponent sc3 = s1.getServiceComponent(componentName3);
        ServiceComponentHost sch1 = sc1.getServiceComponentHost(host1);
        ServiceComponentHost sch2 = sc2.getServiceComponentHost(host1);
        ServiceComponentHost sch3 = sc1.getServiceComponentHost(host2);
        ServiceComponentHost sch4 = sc2.getServiceComponentHost(host2);
        ServiceComponentHost sch5 = sc3.getServiceComponentHost(host1);
        s1.setDesiredState(INSTALLED);
        sc1.setDesiredState(INIT);
        sc2.setDesiredState(INIT);
        sc3.setDesiredState(STARTED);
        sch1.setDesiredState(INSTALLED);
        sch2.setDesiredState(INSTALLED);
        sch3.setDesiredState(STARTED);
        sch4.setDesiredState(INSTALLED);
        sch5.setDesiredState(INSTALLED);
        sch1.setState(INSTALLED);
        sch2.setState(INSTALLED);
        sch3.setState(STARTED);
        sch4.setState(INSTALLED);
        sch5.setState(UNKNOWN);
        Set<ServiceComponentRequest> reqs = new HashSet<>();
        ServiceComponentRequest req1;
        ServiceComponentRequest req2;
        ServiceComponentRequest req3;
        // confirm an UNKOWN doesn't fail
        req1 = new ServiceComponentRequest(cluster1, serviceName1, sc3.getName(), INSTALLED.toString());
        reqs.add(req1);
        ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, reqs, Collections.emptyMap(), true);
        try {
            reqs.clear();
            req1 = new ServiceComponentRequest(cluster1, serviceName1, sc1.getName(), INIT.toString());
            reqs.add(req1);
            ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, reqs, Collections.emptyMap(), true);
            Assert.fail("Expected failure for invalid state update");
        } catch (Exception e) {
            // Expected
        }
        s1.setDesiredState(INSTALLED);
        sc1.setDesiredState(STARTED);
        sc2.setDesiredState(INSTALLED);
        sc3.setDesiredState(STARTED);
        sch1.setDesiredState(INIT);
        sch2.setDesiredState(INIT);
        sch3.setDesiredState(INIT);
        sch4.setDesiredState(INIT);
        sch5.setDesiredState(INIT);
        sch1.setState(INIT);
        sch2.setState(INSTALLED);
        sch3.setState(INIT);
        sch4.setState(INSTALLED);
        sch5.setState(INSTALLED);
        try {
            reqs.clear();
            req1 = new ServiceComponentRequest(cluster1, serviceName1, sc1.getName(), STARTED.toString());
            reqs.add(req1);
            ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, reqs, Collections.emptyMap(), true);
            Assert.fail("Expected failure for invalid state update");
        } catch (Exception e) {
            // Expected
        }
        s1.setDesiredState(INSTALLED);
        sc1.setDesiredState(STARTED);
        sc2.setDesiredState(INIT);
        sc3.setDesiredState(STARTED);
        sch1.setDesiredState(INIT);
        sch2.setDesiredState(INIT);
        sch3.setDesiredState(INIT);
        sch4.setDesiredState(INIT);
        sch5.setDesiredState(INIT);
        sch1.setState(STARTED);
        sch2.setState(INIT);
        sch3.setState(INSTALLED);
        sch4.setState(STARTED);
        sch5.setState(INIT);
        reqs.clear();
        req1 = new ServiceComponentRequest(cluster1, serviceName1, sc1.getName(), INSTALLED.toString());
        req2 = new ServiceComponentRequest(cluster1, serviceName1, sc2.getName(), INSTALLED.toString());
        req3 = new ServiceComponentRequest(cluster1, serviceName1, sc3.getName(), INSTALLED.toString());
        reqs.add(req1);
        reqs.add(req2);
        reqs.add(req3);
        RequestStatusResponse trackAction = ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, reqs, Collections.emptyMap(), true);
        Assert.assertEquals(INSTALLED, s1.getDesiredState());
        Assert.assertEquals(INSTALLED, sc1.getDesiredState());
        Assert.assertEquals(INSTALLED, sc2.getDesiredState());
        Assert.assertEquals(INSTALLED, sc3.getDesiredState());
        Assert.assertEquals(INSTALLED, sch1.getDesiredState());
        Assert.assertEquals(INSTALLED, sch2.getDesiredState());
        Assert.assertEquals(INSTALLED, sch3.getDesiredState());
        Assert.assertEquals(INSTALLED, sch4.getDesiredState());
        Assert.assertEquals(INSTALLED, sch5.getDesiredState());
        Assert.assertEquals(STARTED, sch1.getState());
        Assert.assertEquals(INIT, sch2.getState());
        Assert.assertEquals(INSTALLED, sch3.getState());
        Assert.assertEquals(STARTED, sch4.getState());
        Assert.assertEquals(INIT, sch5.getState());
        long requestId = trackAction.getRequestId();
        List<Stage> stages = actionDB.getAllStages(requestId);
        Assert.assertTrue((!(stages.isEmpty())));
        // FIXME check stage count
        for (Stage stage : stages) {
            AmbariManagementControllerTest.LOG.debug("Stage dump: {}", stage);
        }
        // FIXME verify stages content - execution commands, etc
        // maually set live state
        sch1.setState(INSTALLED);
        sch2.setState(INSTALLED);
        sch3.setState(INSTALLED);
        sch4.setState(INSTALLED);
        sch5.setState(INSTALLED);
        // test no-op
        reqs.clear();
        req1 = new ServiceComponentRequest(cluster1, serviceName1, sc1.getName(), INSTALLED.toString());
        req2 = new ServiceComponentRequest(cluster1, serviceName1, sc2.getName(), INSTALLED.toString());
        reqs.add(req1);
        reqs.add(req2);
        trackAction = ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, reqs, Collections.emptyMap(), true);
        Assert.assertNull(trackAction);
    }

    @Test
    public void testServiceComponentHostUpdateRecursive() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName1 = "HDFS";
        createService(cluster1, serviceName1, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName1, componentName1, INIT);
        createServiceComponent(cluster1, serviceName1, componentName2, INIT);
        createServiceComponent(cluster1, serviceName1, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        Set<ServiceComponentHostRequest> set1 = new HashSet<>();
        ServiceComponentHostRequest r1 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host1, INIT.toString());
        ServiceComponentHostRequest r2 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName2, host1, INIT.toString());
        ServiceComponentHostRequest r3 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host2, INIT.toString());
        ServiceComponentHostRequest r4 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName2, host2, INIT.toString());
        ServiceComponentHostRequest r5 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName3, host1, INIT.toString());
        set1.add(r1);
        set1.add(r2);
        set1.add(r3);
        set1.add(r4);
        set1.add(r5);
        AmbariManagementControllerTest.controller.createHostComponents(set1);
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s1 = c1.getService(serviceName1);
        ServiceComponent sc1 = s1.getServiceComponent(componentName1);
        ServiceComponent sc2 = s1.getServiceComponent(componentName2);
        ServiceComponent sc3 = s1.getServiceComponent(componentName3);
        ServiceComponentHost sch1 = sc1.getServiceComponentHost(host1);
        ServiceComponentHost sch2 = sc2.getServiceComponentHost(host1);
        ServiceComponentHost sch3 = sc1.getServiceComponentHost(host2);
        ServiceComponentHost sch4 = sc2.getServiceComponentHost(host2);
        ServiceComponentHost sch5 = sc3.getServiceComponentHost(host1);
        s1.setDesiredState(INIT);
        sc1.setDesiredState(INIT);
        sc2.setDesiredState(INIT);
        sc3.setDesiredState(INIT);
        sch1.setDesiredState(INIT);
        sch2.setDesiredState(INIT);
        sch3.setDesiredState(INIT);
        sch4.setDesiredState(INSTALLED);
        sch5.setDesiredState(INSTALLED);
        sch1.setState(INIT);
        sch2.setState(INSTALL_FAILED);
        sch3.setState(INIT);
        sch4.setState(INSTALLED);
        sch5.setState(INSTALLED);
        ServiceComponentHostRequest req1;
        ServiceComponentHostRequest req2;
        ServiceComponentHostRequest req3;
        ServiceComponentHostRequest req4;
        ServiceComponentHostRequest req5;
        Set<ServiceComponentHostRequest> reqs = new HashSet<>();
        // todo: I had to comment this portion of the test out for now because I had to modify
        // todo: the transition validation code for the new advanced provisioning
        // todo: work which causes a failure here due to lack of an exception.
        // try {
        // reqs.clear();
        // req1 = new ServiceComponentHostRequest(cluster1, serviceName1,
        // componentName1, host1,
        // State.STARTED.toString());
        // reqs.add(req1);
        // updateHostComponents(reqs, Collections.<String, String>emptyMap(), true);
        // fail("Expected failure for invalid transition");
        // } catch (Exception e) {
        // // Expected
        // }
        try {
            reqs.clear();
            req1 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host1, INSTALLED.toString());
            req2 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host2, INSTALLED.toString());
            req3 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName2, host1, INSTALLED.toString());
            req4 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName2, host2, INSTALLED.toString());
            req5 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName3, host1, STARTED.toString());
            reqs.add(req1);
            reqs.add(req2);
            reqs.add(req3);
            reqs.add(req4);
            reqs.add(req5);
            updateHostComponents(reqs, Collections.emptyMap(), true);
            // Expected, now client components with STARTED status will be ignored
        } catch (Exception e) {
            Assert.fail("Failure for invalid states");
        }
        reqs.clear();
        req1 = new ServiceComponentHostRequest(cluster1, null, componentName1, host1, INSTALLED.toString());
        req2 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host2, INSTALLED.toString());
        req3 = new ServiceComponentHostRequest(cluster1, null, componentName2, host1, INSTALLED.toString());
        req4 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName2, host2, INSTALLED.toString());
        req5 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName3, host1, INSTALLED.toString());
        reqs.add(req1);
        reqs.add(req2);
        reqs.add(req3);
        reqs.add(req4);
        reqs.add(req5);
        RequestStatusResponse trackAction = updateHostComponents(reqs, Collections.emptyMap(), true);
        Assert.assertNotNull(trackAction);
        long requestId = trackAction.getRequestId();
        Assert.assertFalse(actionDB.getAllStages(requestId).isEmpty());
        List<Stage> stages = actionDB.getAllStages(requestId);
        // FIXME check stage count
        for (Stage stage : stages) {
            AmbariManagementControllerTest.LOG.debug("Stage dump: {}", stage);
        }
        // FIXME verify stages content - execution commands, etc
        // manually set live state
        sch1.setState(INSTALLED);
        sch2.setState(INSTALLED);
        sch3.setState(INSTALLED);
        sch4.setState(INSTALLED);
        sch5.setState(INSTALLED);
        // test no-op
        reqs.clear();
        req1 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host1, INSTALLED.toString());
        req2 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host2, INSTALLED.toString());
        reqs.add(req1);
        reqs.add(req2);
        trackAction = updateHostComponents(reqs, Collections.emptyMap(), true);
        Assert.assertNull(trackAction);
    }

    @Test
    public void testCreateCustomActions() throws Exception {
        final String cluster1 = AmbariManagementControllerTest.getUniqueName();
        // !!! weird, but the assertions are banking on alphabetical order
        final String host1 = "a" + (AmbariManagementControllerTest.getUniqueName());
        final String host2 = "b" + (AmbariManagementControllerTest.getUniqueName());
        final String host3 = "c" + (AmbariManagementControllerTest.getUniqueName());
        setupClusterWithHosts(cluster1, "HDP-2.0.6", new ArrayList<String>() {
            {
                add(host1);
                add(host2);
                add(host3);
            }
        }, "centos6");
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-2.0.6"));
        cluster.setCurrentStackVersion(new StackId("HDP-2.0.6"));
        ConfigFactory cf = AmbariManagementControllerTest.injector.getInstance(ConfigFactory.class);
        Config config1 = cf.createNew(cluster, "global", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        Config config2 = cf.createNew(cluster, "core-site", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        Config config3 = cf.createNew(cluster, "yarn-site", "version1", new HashMap<String, String>() {
            {
                put("test.password", "supersecret");
            }
        }, new HashMap());
        RepositoryVersionEntity repositoryVersion = repositoryVersion206;
        Service hdfs = cluster.addService("HDFS", repositoryVersion);
        Service mapred = cluster.addService("YARN", repositoryVersion);
        hdfs.addServiceComponent(HDFS_CLIENT.name());
        hdfs.addServiceComponent(NAMENODE.name());
        hdfs.addServiceComponent(DATANODE.name());
        mapred.addServiceComponent(RESOURCEMANAGER.name());
        hdfs.getServiceComponent(HDFS_CLIENT.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(NAMENODE.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(host2);
        String actionDef1 = AmbariManagementControllerTest.getUniqueName();
        String actionDef2 = AmbariManagementControllerTest.getUniqueName();
        ActionDefinition a1 = new ActionDefinition(actionDef1, ActionType.SYSTEM, "test,[optional1]", "", "", "Does file exist", TargetHostType.SPECIFIC, Short.valueOf("100"), null);
        AmbariManagementControllerTest.controller.getAmbariMetaInfo().addActionDefinition(a1);
        AmbariManagementControllerTest.controller.getAmbariMetaInfo().addActionDefinition(new ActionDefinition(actionDef2, ActionType.SYSTEM, "", "HDFS", "DATANODE", "Does file exist", TargetHostType.ALL, Short.valueOf("1000"), null));
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("test", "test");
                put("pwd", "SECRET:yarn-site:1:test.password");
            }
        };
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        requestProperties.put("datanode", "abc");
        ArrayList<String> hosts = new ArrayList<String>() {
            {
                add(host1);
            }
        };
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HDFS", "DATANODE", hosts);
        List<RequestResourceFilter> resourceFilters = new ArrayList<>();
        resourceFilters.add(resourceFilter);
        ExecuteActionRequest actionRequest = new ExecuteActionRequest(cluster1, null, actionDef1, resourceFilters, null, params, false);
        RequestStatusResponse response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        Assert.assertEquals(1, response.getTasks().size());
        ShortTaskStatus taskStatus = response.getTasks().get(0);
        Assert.assertEquals(host1, taskStatus.getHostName());
        List<HostRoleCommand> storedTasks = actionDB.getRequestTasks(response.getRequestId());
        Stage stage = actionDB.getAllStages(response.getRequestId()).get(0);
        Assert.assertNotNull(stage);
        Assert.assertEquals(1, storedTasks.size());
        HostRoleCommand task = storedTasks.get(0);
        Assert.assertEquals(ACTIONEXECUTE, task.getRoleCommand());
        Assert.assertEquals(actionDef1, task.getRole().name());
        Assert.assertEquals(host1, task.getHostName());
        ExecutionCommand cmd = task.getExecutionCommandWrapper().getExecutionCommand();
        // h1 has only DATANODE, NAMENODE, CLIENT sch's
        Assert.assertEquals(host1, cmd.getHostname());
        Assert.assertFalse(cmd.getLocalComponents().isEmpty());
        Assert.assertTrue(cmd.getLocalComponents().contains(DATANODE.name()));
        Assert.assertTrue(cmd.getLocalComponents().contains(NAMENODE.name()));
        Assert.assertTrue(cmd.getLocalComponents().contains(HDFS_CLIENT.name()));
        Assert.assertFalse(cmd.getLocalComponents().contains(RESOURCEMANAGER.name()));
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> hostParametersStage = StageUtils.getGson().fromJson(stage.getHostParamsStage(), type);
        Map<String, String> commandParametersStage = StageUtils.getGson().fromJson(stage.getCommandParamsStage(), type);
        Assert.assertTrue(commandParametersStage.containsKey("test"));
        Assert.assertTrue(commandParametersStage.containsKey("pwd"));
        Assert.assertEquals(commandParametersStage.get("pwd"), "supersecret");
        Assert.assertEquals("HDFS", cmd.getServiceName());
        Assert.assertEquals("DATANODE", cmd.getComponentName());
        Assert.assertNotNull(hostParametersStage.get("jdk_location"));
        Assert.assertEquals("900", cmd.getCommandParams().get("command_timeout"));
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        // !!! test that the action execution helper is using the right timeout
        a1.setDefaultTimeout(((short) (1800)));
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef1, resourceFilters, null, params, false);
        response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        List<HostRoleCommand> storedTasks1 = actionDB.getRequestTasks(response.getRequestId());
        cmd = storedTasks1.get(0).getExecutionCommandWrapper().getExecutionCommand();
        Assert.assertEquals("1800", cmd.getCommandParams().get("command_timeout"));
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", "", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef2, resourceFilters, null, params, false);
        response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        Assert.assertEquals(2, response.getTasks().size());
        final List<HostRoleCommand> storedTasks2 = actionDB.getRequestTasks(response.getRequestId());
        task = storedTasks2.get(1);
        Assert.assertEquals(ACTIONEXECUTE, task.getRoleCommand());
        Assert.assertEquals(actionDef2, task.getRole().name());
        HashSet<String> expectedHosts = new HashSet<String>() {
            {
                add(host2);
                add(host1);
            }
        };
        HashSet<String> actualHosts = new HashSet<String>() {
            {
                add(storedTasks2.get(1).getHostName());
                add(storedTasks2.get(0).getHostName());
            }
        };
        Assert.assertEquals(expectedHosts, actualHosts);
        cmd = task.getExecutionCommandWrapper().getExecutionCommand();
        commandParametersStage = StageUtils.getGson().fromJson(stage.getCommandParamsStage(), type);
        Assert.assertTrue(commandParametersStage.containsKey("test"));
        Assert.assertTrue(commandParametersStage.containsKey("pwd"));
        Assert.assertEquals(commandParametersStage.get("pwd"), "supersecret");
        Assert.assertEquals("HDFS", cmd.getServiceName());
        Assert.assertEquals("DATANODE", cmd.getComponentName());
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        // h2 has only DATANODE sch
        Assert.assertEquals(host2, cmd.getHostname());
        Assert.assertFalse(cmd.getLocalComponents().isEmpty());
        Assert.assertTrue(cmd.getLocalComponents().contains(DATANODE.name()));
        Assert.assertFalse(cmd.getLocalComponents().contains(NAMENODE.name()));
        Assert.assertFalse(cmd.getLocalComponents().contains(HDFS_CLIENT.name()));
        Assert.assertFalse(cmd.getLocalComponents().contains(RESOURCEMANAGER.name()));
        hosts = new ArrayList<String>() {
            {
                add(host3);
            }
        };
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", "", hosts);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef1, resourceFilters, null, params, false);
        response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        Assert.assertEquals(1, response.getTasks().size());
        taskStatus = response.getTasks().get(0);
        Assert.assertEquals(host3, taskStatus.getHostName());
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
    }

    @Test
    public void testComponentCategorySentWithRestart() throws Exception, AuthorizationException {
        final String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        setupClusterWithHosts(cluster1, "HDP-2.0.7", new ArrayList<String>() {
            {
                add(host1);
            }
        }, "centos5");
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-2.0.7"));
        cluster.setCurrentStackVersion(new StackId("HDP-2.0.7"));
        ConfigFactory cf = AmbariManagementControllerTest.injector.getInstance(ConfigFactory.class);
        Config config1 = cf.createNew(cluster, "global", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        Config config2 = cf.createNew(cluster, "core-site", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        RepositoryVersionEntity repositoryVersion = repositoryVersion207;
        Service hdfs = cluster.addService("HDFS", repositoryVersion);
        hdfs.addServiceComponent(HDFS_CLIENT.name());
        hdfs.addServiceComponent(NAMENODE.name());
        hdfs.addServiceComponent(DATANODE.name());
        hdfs.getServiceComponent(HDFS_CLIENT.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(NAMENODE.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(host1);
        installService(cluster1, "HDFS", false, false);
        startService(cluster1, "HDFS", false, false);
        Cluster c = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = c.getService("HDFS");
        Assert.assertEquals(STARTED, s.getDesiredState());
        for (ServiceComponent sc : s.getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                if (sc.isClientComponent()) {
                    Assert.assertEquals(INSTALLED, sch.getDesiredState());
                } else {
                    Assert.assertEquals(STARTED, sch.getDesiredState());
                }
            }
        }
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("test", "test");
            }
        };
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HDFS", "HDFS_CLIENT", new ArrayList<String>() {
            {
                add(host1);
            }
        });
        ExecuteActionRequest actionRequest = new ExecuteActionRequest(cluster1, "RESTART", params, false);
        actionRequest.getResourceFilters().add(resourceFilter);
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        requestProperties.put("hdfs_client", "abc");
        RequestStatusResponse response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        List<Stage> stages = actionDB.getAllStages(response.getRequestId());
        Assert.assertNotNull(stages);
        HostRoleCommand hrc = null;
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        for (Stage stage : stages) {
            for (HostRoleCommand cmd : stage.getOrderedHostRoleCommands()) {
                if (cmd.getRole().equals(HDFS_CLIENT)) {
                    hrc = cmd;
                }
                Map<String, String> hostParamStage = StageUtils.getGson().fromJson(stage.getHostParamsStage(), type);
                Assert.assertTrue(hostParamStage.containsKey(DB_DRIVER_FILENAME));
                Assert.assertTrue(hostParamStage.containsKey(MYSQL_JDBC_URL));
                Assert.assertTrue(hostParamStage.containsKey(ORACLE_JDBC_URL));
            }
        }
        Assert.assertNotNull(hrc);
        Assert.assertEquals("RESTART HDFS/HDFS_CLIENT", hrc.getCommandDetail());
        Map<String, String> roleParams = hrc.getExecutionCommandWrapper().getExecutionCommand().getRoleParams();
        Assert.assertNotNull(roleParams);
        Assert.assertEquals("CLIENT", roleParams.get(COMPONENT_CATEGORY));
        Assert.assertTrue(hrc.getExecutionCommandWrapper().getExecutionCommand().getCommandParams().containsKey("hdfs_client"));
        Assert.assertEquals("abc", hrc.getExecutionCommandWrapper().getExecutionCommand().getCommandParams().get("hdfs_client"));
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
    }

    @SuppressWarnings("serial")
    @Test
    public void testCreateActionsFailures() throws Exception {
        final String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        setupClusterWithHosts(cluster1, "HDP-2.0.7", new ArrayList<String>() {
            {
                add(host1);
            }
        }, "centos5");
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-2.0.7"));
        cluster.setCurrentStackVersion(new StackId("HDP-2.0.7"));
        RepositoryVersionEntity repositoryVersion = repositoryVersion207;
        ConfigFactory cf = AmbariManagementControllerTest.injector.getInstance(ConfigFactory.class);
        Config config1 = cf.createNew(cluster, "global", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        Config config2 = cf.createNew(cluster, "core-site", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        cluster.addConfig(config1);
        cluster.addConfig(config2);
        cluster.addDesiredConfig("_test", Collections.singleton(config1));
        cluster.addDesiredConfig("_test", Collections.singleton(config2));
        Service hdfs = cluster.addService("HDFS", repositoryVersion);
        Service hive = cluster.addService("HIVE", repositoryVersion);
        hdfs.addServiceComponent(HDFS_CLIENT.name());
        hdfs.addServiceComponent(NAMENODE.name());
        hdfs.addServiceComponent(DATANODE.name());
        hive.addServiceComponent(HIVE_SERVER.name());
        hdfs.getServiceComponent(HDFS_CLIENT.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(NAMENODE.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(host1);
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("test", "test");
            }
        };
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HDFS", null, null);
        ExecuteActionRequest actionRequest = new ExecuteActionRequest(cluster1, "NON_EXISTENT_CHECK", params, false);
        actionRequest.getResourceFilters().add(resourceFilter);
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Unsupported action");
        // actionRequest = new ExecuteActionRequest(cluster1, "NON_EXISTENT_SERVICE_CHECK", "HDFS", params);
        // expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Unsupported action");
        actionRequest = new ExecuteActionRequest(cluster1, "DECOMMISSION_DATANODE", params, false);
        actionRequest.getResourceFilters().add(resourceFilter);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Unsupported action DECOMMISSION_DATANODE for Service: HDFS and Component: null");
        // actionRequest = new ExecuteActionRequest(cluster1, "DECOMMISSION", "HDFS", params);
        // expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Unsupported action DECOMMISSION for Service: HDFS and Component: null");
        resourceFilter = new RequestResourceFilter("HDFS", "HDFS_CLIENT", null);
        List<RequestResourceFilter> resourceFilters = new ArrayList<>();
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Unsupported action DECOMMISSION for Service: HDFS and Component: HDFS_CLIENT");
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", null, null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, "DECOMMISSION_DATANODE", resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Action DECOMMISSION_DATANODE does not exist");
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("YARN", "RESOURCEMANAGER", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Service not found, clusterName=" + cluster1) + ", serviceName=YARN"));
        Map<String, String> params2 = new HashMap<String, String>() {
            {
                put("included_hosts", "h1,h2");
                put("excluded_hosts", "h1,h3");
            }
        };
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", "NAMENODE", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, resourceFilters, null, params2, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Same host cannot be specified for inclusion as well as exclusion. Hosts: [h1]");
        params2 = new HashMap<String, String>() {
            {
                put("included_hosts", " h1,h2");
                put("excluded_hosts", "h4, h3");
                put("slave_type", "HDFS_CLIENT");
            }
        };
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", "NAMENODE", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, resourceFilters, null, params2, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Component HDFS_CLIENT is not supported for decommissioning.");
        List<String> hosts = new ArrayList<>();
        hosts.add("h6");
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", "NAMENODE", hosts);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, resourceFilters, null, params2, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Decommission command cannot be issued with target host(s) specified.");
        hdfs.getServiceComponent(DATANODE.name()).getServiceComponentHost(host1).setState(INSTALLED);
        params2 = new HashMap<String, String>() {
            {
                put("excluded_hosts", host1);
            }
        };
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", "NAMENODE", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, resourceFilters, null, params2, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Component DATANODE on host " + host1) + " cannot be decommissioned as its not in STARTED state"));
        params2 = new HashMap<String, String>() {
            {
                put("excluded_hosts", "h1 ");
                put("mark_draining_only", "true");
            }
        };
        actionRequest = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, resourceFilters, null, params2, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, "mark_draining_only is not a valid parameter for NAMENODE");
        String actionDef1 = AmbariManagementControllerTest.getUniqueName();
        String actionDef2 = AmbariManagementControllerTest.getUniqueName();
        String actionDef3 = AmbariManagementControllerTest.getUniqueName();
        String actionDef4 = AmbariManagementControllerTest.getUniqueName();
        AmbariManagementControllerTest.controller.getAmbariMetaInfo().addActionDefinition(new ActionDefinition(actionDef1, ActionType.SYSTEM, "test,dirName", "", "", "Does file exist", TargetHostType.SPECIFIC, Short.valueOf("100"), null));
        AmbariManagementControllerTest.controller.getAmbariMetaInfo().addActionDefinition(new ActionDefinition(actionDef2, ActionType.SYSTEM, "", "HDFS", "DATANODE", "Does file exist", TargetHostType.ANY, Short.valueOf("100"), null));
        AmbariManagementControllerTest.controller.getAmbariMetaInfo().addActionDefinition(new ActionDefinition("update_repo", ActionType.SYSTEM, "", "HDFS", "DATANODE", "Does file exist", TargetHostType.ANY, Short.valueOf("100"), null));
        AmbariManagementControllerTest.controller.getAmbariMetaInfo().addActionDefinition(new ActionDefinition(actionDef3, ActionType.SYSTEM, "", "MAPREDUCE", "MAPREDUCE_CLIENT", "Does file exist", TargetHostType.ANY, Short.valueOf("100"), null));
        AmbariManagementControllerTest.controller.getAmbariMetaInfo().addActionDefinition(new ActionDefinition(actionDef4, ActionType.SYSTEM, "", "HIVE", "", "Does file exist", TargetHostType.ANY, Short.valueOf("100"), null));
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef1, null, null, null, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Action " + actionDef1) + " requires input 'test' that is not provided"));
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef1, null, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Action " + actionDef1) + " requires input 'dirName' that is not provided"));
        params.put("dirName", "dirName");
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef1, null, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Action " + actionDef1) + " requires explicit target host(s)"));
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HIVE", null, null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef2, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Action " + actionDef2) + " targets service HIVE that does not match with expected HDFS"));
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", "HDFS_CLIENT", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef2, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Action " + actionDef2) + " targets component HDFS_CLIENT that does not match with expected DATANODE"));
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS2", "HDFS_CLIENT", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef1, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Service not found, clusterName=" + cluster1) + ", serviceName=HDFS2"));
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", "HDFS_CLIENT2", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef1, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("ServiceComponent not found, clusterName=" + cluster1) + ", serviceName=HDFS, serviceComponentName=HDFS_CLIENT2"));
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("", "HDFS_CLIENT2", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef1, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Action " + actionDef1) + " targets component HDFS_CLIENT2 without specifying the target service"));
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("", "", null);
        resourceFilters.add(resourceFilter);
        // targets a service that is not a member of the stack (e.g. MR not in HDP-2)
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef3, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, (("Service not found, clusterName=" + cluster1) + ", serviceName=MAPREDUCE"));
        hosts = new ArrayList<>();
        hosts.add("h6");
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", "", hosts);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef2, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, "Request specifies host h6 but it is not a valid host based on the target service=HDFS and component=DATANODE");
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HIVE", "", null);
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(cluster1, null, actionDef4, resourceFilters, null, params, false);
        expectActionCreationErrorWithMessage(actionRequest, requestProperties, ((("Suitable hosts not found, component=, service=HIVE, cluster=" + cluster1) + ", actionName=") + actionDef4));
    }

    @SuppressWarnings("serial")
    @Test
    public void testCreateServiceCheckActions() throws Exception {
        final String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        setupClusterWithHosts(cluster1, "HDP-0.1", new ArrayList<String>() {
            {
                add(host1);
                add(host2);
            }
        }, "centos5");
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-0.1"));
        cluster.setCurrentStackVersion(new StackId("HDP-0.1"));
        RepositoryVersionEntity repositoryVersion = repositoryVersion01;
        ConfigFactory cf = AmbariManagementControllerTest.injector.getInstance(ConfigFactory.class);
        Config config1 = cf.createNew(cluster, "global", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        config1.setPropertiesAttributes(new HashMap<String, Map<String, String>>() {
            {
                put("attr1", new HashMap<>());
            }
        });
        Config config2 = cf.createNew(cluster, "core-site", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        config2.setPropertiesAttributes(new HashMap<String, Map<String, String>>() {
            {
                put("attr2", new HashMap<>());
            }
        });
        cluster.addDesiredConfig("_test", Collections.singleton(config1));
        cluster.addDesiredConfig("_test", Collections.singleton(config2));
        Service hdfs = cluster.addService("HDFS", repositoryVersion);
        Service mapReduce = cluster.addService("MAPREDUCE", repositoryVersion);
        hdfs.addServiceComponent(HDFS_CLIENT.name());
        mapReduce.addServiceComponent(MAPREDUCE_CLIENT.name());
        hdfs.getServiceComponent(HDFS_CLIENT.name()).addServiceComponentHost(host1);
        mapReduce.getServiceComponent(MAPREDUCE_CLIENT.name()).addServiceComponentHost(host2);
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("test", "test");
            }
        };
        ExecuteActionRequest actionRequest = new ExecuteActionRequest(cluster1, HDFS_SERVICE_CHECK.name(), params, false);
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HDFS", null, null);
        actionRequest.getResourceFilters().add(resourceFilter);
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        RequestStatusResponse response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        Assert.assertEquals(1, response.getTasks().size());
        ShortTaskStatus task = response.getTasks().get(0);
        List<HostRoleCommand> storedTasks = actionDB.getRequestTasks(response.getRequestId());
        Stage stage = actionDB.getAllStages(response.getRequestId()).get(0);
        // Check configs not stored with execution command
        ExecutionCommandDAO executionCommandDAO = AmbariManagementControllerTest.injector.getInstance(ExecutionCommandDAO.class);
        ExecutionCommandEntity commandEntity = executionCommandDAO.findByPK(task.getTaskId());
        Gson gson = new Gson();
        ExecutionCommand executionCommand = gson.fromJson(new StringReader(new String(commandEntity.getCommand())), ExecutionCommand.class);
        Assert.assertTrue((((executionCommand.getConfigurations()) == null) || (executionCommand.getConfigurations().isEmpty())));
        Assert.assertEquals(1, storedTasks.size());
        HostRoleCommand hostRoleCommand = storedTasks.get(0);
        Assert.assertEquals("SERVICE_CHECK HDFS", hostRoleCommand.getCommandDetail());
        Assert.assertNull(hostRoleCommand.getCustomCommandName());
        Assert.assertEquals(task.getTaskId(), hostRoleCommand.getTaskId());
        Assert.assertNotNull(actionRequest.getResourceFilters());
        RequestResourceFilter requestResourceFilter = actionRequest.getResourceFilters().get(0);
        Assert.assertEquals(resourceFilter.getServiceName(), hostRoleCommand.getExecutionCommandWrapper().getExecutionCommand().getServiceName());
        Assert.assertEquals(actionRequest.getClusterName(), hostRoleCommand.getExecutionCommandWrapper().getExecutionCommand().getClusterName());
        Assert.assertEquals(actionRequest.getCommandName(), hostRoleCommand.getExecutionCommandWrapper().getExecutionCommand().getRole());
        Assert.assertEquals(HDFS_CLIENT.name(), hostRoleCommand.getEvent().getEvent().getServiceComponentName());
        Assert.assertEquals(actionRequest.getParameters(), hostRoleCommand.getExecutionCommandWrapper().getExecutionCommand().getRoleParams());
        Assert.assertNotNull(hostRoleCommand.getExecutionCommandWrapper().getExecutionCommand().getConfigurations());
        Assert.assertEquals(0, hostRoleCommand.getExecutionCommandWrapper().getExecutionCommand().getConfigurations().size());
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), stage.getRequestContext());
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        actionRequest = new ExecuteActionRequest(cluster1, MAPREDUCE_SERVICE_CHECK.name(), null, false);
        resourceFilter = new RequestResourceFilter("MAPREDUCE", null, null);
        actionRequest.getResourceFilters().add(resourceFilter);
        AmbariManagementControllerTest.injector.getInstance(ActionMetadata.class).addServiceCheckAction("MAPREDUCE");
        response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        Assert.assertEquals(1, response.getTasks().size());
        List<HostRoleCommand> tasks = actionDB.getRequestTasks(response.getRequestId());
        Assert.assertEquals(1, tasks.size());
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, null);
        response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        Assert.assertEquals(1, response.getTasks().size());
        Assert.assertEquals("", response.getRequestContext());
    }

    @Test
    public void testUpdateConfigForRunningService() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        // null service should work
        createServiceComponentHost(cluster1, null, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName1).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host2));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName3).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName3).getServiceComponentHost(host2));
        // Install
        ServiceRequest r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), INSTALLED.toString(), null);
        Set<ServiceRequest> requests = new HashSet<>();
        requests.add(r);
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertEquals(INSTALLED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        // manually change live state to installed as no running action manager
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                sch.setState(INSTALLED);
            }
        }
        // Start
        r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), STARTED.toString(), null);
        requests.clear();
        requests.add(r);
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        // manually change live state to started as no running action manager
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                sch.setState(STARTED);
            }
        }
        Assert.assertEquals(STARTED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            if (sc.getName().equals("HDFS_CLIENT")) {
                Assert.assertEquals(INSTALLED, sc.getDesiredState());
            } else {
                Assert.assertEquals(STARTED, sc.getDesiredState());
            }
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                if (sch.getServiceComponentName().equals("HDFS_CLIENT")) {
                    Assert.assertEquals(INSTALLED, sch.getDesiredState());
                } else {
                    Assert.assertEquals(STARTED, sch.getDesiredState());
                }
            }
        }
        Map<String, String> configs = new HashMap<>();
        configs.put("a", "b");
        ConfigurationRequest cr1;
        ConfigurationRequest cr2;
        ConfigurationRequest cr3;
        ConfigurationRequest cr4;
        ConfigurationRequest cr5;
        ConfigurationRequest cr6;
        ConfigurationRequest cr7;
        ConfigurationRequest cr8;
        cr1 = new ConfigurationRequest(cluster1, "typeA", "v1", configs, null);
        cr2 = new ConfigurationRequest(cluster1, "typeB", "v1", configs, null);
        cr3 = new ConfigurationRequest(cluster1, "typeC", "v1", configs, null);
        cr4 = new ConfigurationRequest(cluster1, "typeD", "v1", configs, null);
        cr5 = new ConfigurationRequest(cluster1, "typeA", "v2", configs, null);
        cr6 = new ConfigurationRequest(cluster1, "typeB", "v2", configs, null);
        cr7 = new ConfigurationRequest(cluster1, "typeC", "v2", configs, null);
        cr8 = new ConfigurationRequest(cluster1, "typeE", "v1", configs, null);
        AmbariManagementControllerTest.controller.createConfiguration(cr1);
        AmbariManagementControllerTest.controller.createConfiguration(cr2);
        AmbariManagementControllerTest.controller.createConfiguration(cr3);
        AmbariManagementControllerTest.controller.createConfiguration(cr4);
        AmbariManagementControllerTest.controller.createConfiguration(cr5);
        AmbariManagementControllerTest.controller.createConfiguration(cr6);
        AmbariManagementControllerTest.controller.createConfiguration(cr7);
        AmbariManagementControllerTest.controller.createConfiguration(cr8);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = cluster.getService(serviceName);
        ServiceComponent sc1 = s.getServiceComponent(componentName1);
        ServiceComponent sc2 = s.getServiceComponent(componentName2);
        ServiceComponentHost sch1 = sc1.getServiceComponentHost(host1);
        Set<ServiceComponentHostRequest> schReqs = new HashSet<>();
        Set<ServiceComponentRequest> scReqs = new HashSet<>();
        Set<ServiceRequest> sReqs = new HashSet<>();
        Map<String, String> configVersions = new HashMap<>();
        // update configs at SCH and SC level
        configVersions.clear();
        configVersions.put("typeA", "v1");
        configVersions.put("typeB", "v1");
        configVersions.put("typeC", "v1");
        schReqs.clear();
        schReqs.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, null));
        Assert.assertNull(updateHostComponents(schReqs, Collections.emptyMap(), true));
        configVersions.clear();
        configVersions.put("typeC", "v1");
        configVersions.put("typeD", "v1");
        scReqs.clear();
        scReqs.add(new ServiceComponentRequest(cluster1, serviceName, componentName2, null));
        Assert.assertNull(ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, scReqs, Collections.emptyMap(), true));
        // update configs at service level
        configVersions.clear();
        configVersions.put("typeA", "v2");
        configVersions.put("typeC", "v2");
        configVersions.put("typeE", "v1");
        sReqs.clear();
        sReqs.add(new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), null, null));
        Assert.assertNull(ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, sReqs, mapRequestProps, true, false));
        // update configs at SCH level
        configVersions.clear();
        configVersions.put("typeA", "v1");
        configVersions.put("typeB", "v1");
        configVersions.put("typeC", "v1");
        schReqs.clear();
        schReqs.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, null));
        Assert.assertNull(updateHostComponents(schReqs, Collections.emptyMap(), true));
        // update configs at SC level
        configVersions.clear();
        configVersions.put("typeC", "v2");
        configVersions.put("typeD", "v1");
        scReqs.clear();
        scReqs.add(new ServiceComponentRequest(cluster1, serviceName, componentName1, null));
        Assert.assertNull(ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, scReqs, Collections.emptyMap(), true));
    }

    @Test
    public void testConfigUpdates() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        // null service should work
        createServiceComponentHost(cluster1, null, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName1).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host2));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName3).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName3).getServiceComponentHost(host2));
        Map<String, String> configs = new HashMap<>();
        configs.put("a", "b");
        Map<String, Map<String, String>> configAttributes = new HashMap<>();
        configAttributes.put("final", new HashMap<>());
        configAttributes.get("final").put("a", "true");
        ConfigurationRequest cr1;
        ConfigurationRequest cr2;
        ConfigurationRequest cr3;
        ConfigurationRequest cr4;
        ConfigurationRequest cr5;
        ConfigurationRequest cr6;
        ConfigurationRequest cr7;
        ConfigurationRequest cr8;
        cr1 = new ConfigurationRequest(cluster1, "typeA", "v1", configs, configAttributes);
        cr2 = new ConfigurationRequest(cluster1, "typeB", "v1", configs, configAttributes);
        cr3 = new ConfigurationRequest(cluster1, "typeC", "v1", configs, configAttributes);
        cr4 = new ConfigurationRequest(cluster1, "typeD", "v1", configs, configAttributes);
        cr5 = new ConfigurationRequest(cluster1, "typeA", "v2", configs, configAttributes);
        cr6 = new ConfigurationRequest(cluster1, "typeB", "v2", configs, configAttributes);
        cr7 = new ConfigurationRequest(cluster1, "typeC", "v2", configs, configAttributes);
        cr8 = new ConfigurationRequest(cluster1, "typeE", "v1", configs, configAttributes);
        AmbariManagementControllerTest.controller.createConfiguration(cr1);
        AmbariManagementControllerTest.controller.createConfiguration(cr2);
        AmbariManagementControllerTest.controller.createConfiguration(cr3);
        AmbariManagementControllerTest.controller.createConfiguration(cr4);
        AmbariManagementControllerTest.controller.createConfiguration(cr5);
        AmbariManagementControllerTest.controller.createConfiguration(cr6);
        AmbariManagementControllerTest.controller.createConfiguration(cr7);
        AmbariManagementControllerTest.controller.createConfiguration(cr8);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = cluster.getService(serviceName);
        ServiceComponent sc1 = s.getServiceComponent(componentName1);
        ServiceComponent sc2 = s.getServiceComponent(componentName2);
        ServiceComponentHost sch1 = sc1.getServiceComponentHost(host1);
        Set<ServiceComponentHostRequest> schReqs = new HashSet<>();
        Set<ServiceComponentRequest> scReqs = new HashSet<>();
        Set<ServiceRequest> sReqs = new HashSet<>();
        Map<String, String> configVersions = new HashMap<>();
        // update configs at SCH and SC level
        configVersions.clear();
        configVersions.put("typeA", "v1");
        configVersions.put("typeB", "v1");
        configVersions.put("typeC", "v1");
        schReqs.clear();
        schReqs.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, null));
        Assert.assertNull(updateHostComponents(schReqs, Collections.emptyMap(), true));
        configVersions.clear();
        configVersions.put("typeC", "v1");
        configVersions.put("typeD", "v1");
        scReqs.clear();
        scReqs.add(new ServiceComponentRequest(cluster1, serviceName, componentName2, null));
        Assert.assertNull(ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, scReqs, Collections.emptyMap(), true));
        // update configs at service level
        configVersions.clear();
        configVersions.put("typeA", "v2");
        configVersions.put("typeC", "v2");
        configVersions.put("typeE", "v1");
        sReqs.clear();
        sReqs.add(new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), null, null));
        Assert.assertNull(ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, sReqs, mapRequestProps, true, false));
        // update configs at SCH level
        configVersions.clear();
        configVersions.put("typeA", "v1");
        configVersions.put("typeB", "v1");
        configVersions.put("typeC", "v1");
        schReqs.clear();
        schReqs.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, null));
        Assert.assertNull(updateHostComponents(schReqs, Collections.emptyMap(), true));
        // update configs at SC level
        configVersions.clear();
        configVersions.put("typeC", "v2");
        configVersions.put("typeD", "v1");
        scReqs.clear();
        scReqs.add(new ServiceComponentRequest(cluster1, serviceName, componentName1, null));
        Assert.assertNull(ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, scReqs, Collections.emptyMap(), true));
    }

    @Test
    public void testReConfigureService() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        // null service should work
        createServiceComponentHost(cluster1, null, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        // Install
        ServiceRequest r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), INSTALLED.toString());
        Set<ServiceRequest> requests = new HashSet<>();
        requests.add(r);
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertEquals(INSTALLED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        // manually change live state to installed as no running action manager
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                sch.setState(INSTALLED);
            }
        }
        // Create and attach config
        Map<String, String> configs = new HashMap<>();
        configs.put("a", "b");
        ConfigurationRequest cr1;
        ConfigurationRequest cr2;
        ConfigurationRequest cr3;
        cr1 = new ConfigurationRequest(cluster1, "core-site", "version1", configs, null);
        cr2 = new ConfigurationRequest(cluster1, "hdfs-site", "version1", configs, null);
        cr3 = new ConfigurationRequest(cluster1, "core-site", "version122", configs, null);
        AmbariManagementControllerTest.controller.createConfiguration(cr1);
        AmbariManagementControllerTest.controller.createConfiguration(cr2);
        AmbariManagementControllerTest.controller.createConfiguration(cr3);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = cluster.getService(serviceName);
        ServiceComponent sc1 = s.getServiceComponent(componentName1);
        ServiceComponent sc2 = s.getServiceComponent(componentName2);
        ServiceComponentHost sch1 = sc1.getServiceComponentHost(host1);
        Set<ServiceComponentHostRequest> schReqs = new HashSet<>();
        Set<ServiceComponentRequest> scReqs = new HashSet<>();
        Set<ServiceRequest> sReqs = new HashSet<>();
        Map<String, String> configVersions = new HashMap<>();
        // SCH level
        configVersions.clear();
        configVersions.put("core-site", "version1");
        configVersions.put("hdfs-site", "version1");
        schReqs.clear();
        schReqs.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, null));
        Assert.assertNull(updateHostComponents(schReqs, Collections.emptyMap(), true));
        // Reconfigure SCH level
        configVersions.clear();
        configVersions.put("core-site", "version122");
        schReqs.clear();
        schReqs.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, null));
        Assert.assertNull(updateHostComponents(schReqs, Collections.emptyMap(), true));
        // Clear Entity Manager
        entityManager.clear();
        // SC Level
        configVersions.clear();
        configVersions.put("core-site", "version1");
        configVersions.put("hdfs-site", "version1");
        scReqs.add(new ServiceComponentRequest(cluster1, serviceName, componentName2, null));
        Assert.assertNull(ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, scReqs, Collections.emptyMap(), true));
        scReqs.add(new ServiceComponentRequest(cluster1, serviceName, componentName1, null));
        Assert.assertNull(ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, scReqs, Collections.emptyMap(), true));
        // Reconfigure SC level
        configVersions.clear();
        configVersions.put("core-site", "version122");
        scReqs.clear();
        scReqs.add(new ServiceComponentRequest(cluster1, serviceName, componentName2, null));
        Assert.assertNull(ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, scReqs, Collections.emptyMap(), true));
        scReqs.clear();
        scReqs.add(new ServiceComponentRequest(cluster1, serviceName, componentName1, null));
        Assert.assertNull(ComponentResourceProviderTest.updateComponents(AmbariManagementControllerTest.controller, scReqs, Collections.emptyMap(), true));
        entityManager.clear();
        // S level
        configVersions.clear();
        configVersions.put("core-site", "version1");
        configVersions.put("hdfs-site", "version1");
        sReqs.clear();
        sReqs.add(new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), null));
        Assert.assertNull(ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, sReqs, mapRequestProps, true, false));
        // Reconfigure S Level
        configVersions.clear();
        configVersions.put("core-site", "version122");
        sReqs.clear();
        sReqs.add(new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), null));
        Assert.assertNull(ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, sReqs, mapRequestProps, true, false));
        entityManager.clear();
    }

    @Test
    public void testReconfigureClientWithServiceStarted() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        // Create and attach config
        Map<String, String> configs = new HashMap<>();
        configs.put("a", "b");
        Map<String, String> configs2 = new HashMap<>();
        configs2.put("c", "d");
        ConfigurationRequest cr1;
        ConfigurationRequest cr2;
        ConfigurationRequest cr3;
        cr1 = new ConfigurationRequest(cluster1, "core-site", "version1", configs, null);
        cr2 = new ConfigurationRequest(cluster1, "hdfs-site", "version1", configs, null);
        ClusterRequest crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr1));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr2));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        installService(cluster1, serviceName, false, false);
        startService(cluster1, serviceName, false, false);
        Cluster c = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = c.getService(serviceName);
        // Stop Sch only
        stopServiceComponentHosts(cluster1, serviceName);
        Assert.assertEquals(STARTED, s.getDesiredState());
        for (ServiceComponent sc : s.getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals(INSTALLED, sch.getDesiredState());
            }
        }
        // Reconfigure
        cr3 = new ConfigurationRequest(cluster1, "core-site", "version122", configs2, null);
        crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr3));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        long id = startService(cluster1, serviceName, false, true);
        List<Stage> stages = actionDB.getAllStages(id);
        HostRoleCommand clientHrc = null;
        for (Stage stage : stages) {
            for (HostRoleCommand hrc : stage.getOrderedHostRoleCommands()) {
                if ((hrc.getHostName().equals(host2)) && (hrc.getRole().toString().equals("HDFS_CLIENT"))) {
                    clientHrc = hrc;
                }
            }
        }
        Assert.assertNotNull(clientHrc);
    }

    @Test
    public void testClientServiceSmokeTests() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName = "PIG";
        createService(cluster1, serviceName, repositoryVersion01, null);
        String componentName1 = "PIG";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        // null service should work
        createServiceComponentHost(cluster1, null, componentName1, host1, null);
        createServiceComponentHost(cluster1, null, componentName1, host2, null);
        ServiceRequest r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), INSTALLED.toString());
        Set<ServiceRequest> requests = new HashSet<>();
        requests.add(r);
        RequestStatusResponse trackAction = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertEquals(INSTALLED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            Assert.assertEquals(INSTALLED, sc.getDesiredState());
            Assert.assertFalse(sc.isRecoveryEnabled());// default value of recoveryEnabled

            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals(INSTALLED, sch.getDesiredState());
                Assert.assertEquals(INIT, sch.getState());
            }
        }
        List<ShortTaskStatus> taskStatuses = trackAction.getTasks();
        Assert.assertEquals(2, taskStatuses.size());
        List<Stage> stages = actionDB.getAllStages(trackAction.getRequestId());
        Assert.assertEquals(1, stages.size());
        Assert.assertEquals("Called from a test", stages.get(0).getRequestContext());
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            sc.setRecoveryEnabled(true);
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                sch.setState(INSTALLED);
            }
        }
        r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), STARTED.toString());
        requests.clear();
        requests.add(r);
        AmbariManagementControllerTest.injector.getInstance(ActionMetadata.class).addServiceCheckAction("PIG");
        trackAction = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertNotNull(trackAction);
        Assert.assertEquals(INSTALLED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            Assert.assertEquals(INSTALLED, sc.getDesiredState());
            Assert.assertTrue(sc.isRecoveryEnabled());
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals(INSTALLED, sch.getDesiredState());
                Assert.assertEquals(INSTALLED, sch.getState());
            }
        }
        stages = actionDB.getAllStages(trackAction.getRequestId());
        for (Stage s : stages) {
            AmbariManagementControllerTest.LOG.info(("Stage dump : " + s));
        }
        Assert.assertEquals(1, stages.size());
        taskStatuses = trackAction.getTasks();
        Assert.assertEquals(1, taskStatuses.size());
        Assert.assertEquals(PIG_SERVICE_CHECK.toString(), taskStatuses.get(0).getRole());
    }

    @Test
    public void testSkipTaskOnUnhealthyHosts() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        String host3 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        addHostToCluster(host3, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host3, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // h1=HEALTHY, h2=HEARTBEAT_LOST, h3=WAITING_FOR_HOST_STATUS_UPDATES
        AmbariManagementControllerTest.clusters.getHost(host1).setState(HEALTHY);
        AmbariManagementControllerTest.clusters.getHost(host2).setState(HEALTHY);
        AmbariManagementControllerTest.clusters.getHost(host3).setState(HEARTBEAT_LOST);
        long requestId = startService(cluster1, serviceName, true, false);
        List<HostRoleCommand> commands = actionDB.getRequestTasks(requestId);
        Assert.assertEquals(3, commands.size());
        int commandCount = 0;
        for (HostRoleCommand command : commands) {
            if ((command.getRoleCommand()) == (RoleCommand.START)) {
                Assert.assertTrue(((command.getHostName().equals(host1)) || (command.getHostName().equals(host2))));
                commandCount++;
            }
        }
        Assert.assertEquals("Expect only two task.", 2, commandCount);
        stopService(cluster1, serviceName, false, false);
        // h1=HEARTBEAT_LOST, h2=HEARTBEAT_LOST, h3=HEALTHY
        AmbariManagementControllerTest.clusters.getHost(host1).setState(HEARTBEAT_LOST);
        AmbariManagementControllerTest.clusters.getHost(host2).setState(HEARTBEAT_LOST);
        AmbariManagementControllerTest.clusters.getHost(host3).setState(HEALTHY);
        requestId = startService(cluster1, serviceName, true, false);
        commands = actionDB.getRequestTasks(requestId);
        commandCount = 0;
        for (HostRoleCommand command : commands) {
            if ((command.getRoleCommand()) == (RoleCommand.START)) {
                Assert.assertTrue(command.getHostName().equals(host3));
                commandCount++;
            }
        }
        Assert.assertEquals("Expect only one task.", 1, commandCount);
        stopService(cluster1, serviceName, false, false);
        // h1=HEALTHY, h2=HEALTHY, h3=HEALTHY
        AmbariManagementControllerTest.clusters.getHost(host1).setState(HEALTHY);
        AmbariManagementControllerTest.clusters.getHost(host2).setState(HEALTHY);
        AmbariManagementControllerTest.clusters.getHost(host3).setState(HEALTHY);
        requestId = startService(cluster1, serviceName, true, false);
        commands = actionDB.getRequestTasks(requestId);
        commandCount = 0;
        for (HostRoleCommand command : commands) {
            if ((command.getRoleCommand()) == (RoleCommand.START)) {
                Assert.assertTrue((((command.getHostName().equals(host3)) || (command.getHostName().equals(host2))) || (command.getHostName().equals(host1))));
                commandCount++;
            }
        }
        Assert.assertEquals("Expect all three task.", 3, commandCount);
        // h1=HEALTHY, h2=HEARTBEAT_LOST, h3=HEALTHY
        AmbariManagementControllerTest.clusters.getHost(host2).setState(HEARTBEAT_LOST);
        requestId = stopService(cluster1, serviceName, false, false);
        commands = actionDB.getRequestTasks(requestId);
        Assert.assertEquals(2, commands.size());
        commandCount = 0;
        for (HostRoleCommand command : commands) {
            if ((command.getRoleCommand()) == (RoleCommand.STOP)) {
                Assert.assertTrue(((command.getHostName().equals(host3)) || (command.getHostName().equals(host1))));
                commandCount++;
            }
        }
        Assert.assertEquals("Expect only two task.", 2, commandCount);
        // Force a sch into INSTALL_FAILED
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = cluster.getService(serviceName);
        ServiceComponent sc3 = s.getServiceComponent(componentName2);
        for (ServiceComponentHost sch : sc3.getServiceComponentHosts().values()) {
            if (sch.getHostName().equals(host3)) {
                sch.setState(INSTALL_FAILED);
            }
        }
        // h1=HEALTHY, h2=HEALTHY, h3=HEARTBEAT_LOST
        AmbariManagementControllerTest.clusters.getHost(host3).setState(HEARTBEAT_LOST);
        AmbariManagementControllerTest.clusters.getHost(host2).setState(HEALTHY);
        requestId = installService(cluster1, serviceName, false, false);
        Assert.assertEquals((-1), requestId);
        // All healthy, INSTALL should succeed
        AmbariManagementControllerTest.clusters.getHost(host3).setState(HEALTHY);
        requestId = installService(cluster1, serviceName, false, false);
        commands = actionDB.getRequestTasks(requestId);
        Assert.assertEquals(1, commands.size());
        commandCount = 0;
        for (HostRoleCommand command : commands) {
            if ((command.getRoleCommand()) == (RoleCommand.INSTALL)) {
                Assert.assertTrue(command.getHostName().equals(host3));
                commandCount++;
            }
        }
        Assert.assertEquals("Expect only one task.", 1, commandCount);
    }

    @Test
    public void testServiceCheckWhenHostIsUnhealthy() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        String host3 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        addHostToCluster(host3, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host3, null);
        // Install
        installService(cluster1, serviceName, false, false);
        AmbariManagementControllerTest.clusters.getHost(host3).setState(UNHEALTHY);
        AmbariManagementControllerTest.clusters.getHost(host2).setState(HEALTHY);
        // Start
        long requestId = startService(cluster1, serviceName, true, false);
        List<HostRoleCommand> commands = actionDB.getRequestTasks(requestId);
        int commandCount = 0;
        for (HostRoleCommand command : commands) {
            if (((command.getRoleCommand()) == (RoleCommand.SERVICE_CHECK)) && ((command.getRole()) == (Role.HDFS_SERVICE_CHECK))) {
                Assert.assertTrue(command.getHostName().equals(host2));
                commandCount++;
            }
        }
        Assert.assertEquals("Expect only one service check.", 1, commandCount);
        stopService(cluster1, serviceName, false, false);
        AmbariManagementControllerTest.clusters.getHost(host3).setState(HEALTHY);
        AmbariManagementControllerTest.clusters.getHost(host2).setState(HEARTBEAT_LOST);
        requestId = startService(cluster1, serviceName, true, false);
        commands = actionDB.getRequestTasks(requestId);
        commandCount = 0;
        for (HostRoleCommand command : commands) {
            if (((command.getRoleCommand()) == (RoleCommand.SERVICE_CHECK)) && ((command.getRole()) == (Role.HDFS_SERVICE_CHECK))) {
                Assert.assertTrue(command.getHostName().equals(host3));
                commandCount++;
            }
        }
        Assert.assertEquals("Expect only one service check.", 1, commandCount);
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HDFS", null, null);
        ExecuteActionRequest actionRequest = new ExecuteActionRequest(cluster1, HDFS_SERVICE_CHECK.name(), null, false);
        actionRequest.getResourceFilters().add(resourceFilter);
        Map<String, String> requestProperties = new HashMap<>();
        RequestStatusResponse response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        commands = actionDB.getRequestTasks(response.getRequestId());
        commandCount = 0;
        for (HostRoleCommand command : commands) {
            if (((command.getRoleCommand()) == (RoleCommand.SERVICE_CHECK)) && ((command.getRole()) == (Role.HDFS_SERVICE_CHECK))) {
                Assert.assertTrue(command.getHostName().equals(host3));
                commandCount++;
            }
        }
        Assert.assertEquals("Expect only one service check.", 1, commandCount);
        // When both are unhealthy then it should raise an exception.
        AmbariManagementControllerTest.clusters.getHost(host3).setState(WAITING_FOR_HOST_STATUS_UPDATES);
        AmbariManagementControllerTest.clusters.getHost(host2).setState(HostState.INIT);
        try {
            AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
            Assert.assertTrue("Exception should have been raised.", false);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("there were no healthy eligible hosts"));
        }
    }

    @Test
    public void testReInstallForInstallFailedClient() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        String host3 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        addHostToCluster(host3, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host3, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Mark client as install failed.
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = cluster.getService(serviceName);
        ServiceComponent sc3 = s.getServiceComponent(componentName3);
        for (ServiceComponentHost sch : sc3.getServiceComponentHosts().values()) {
            if (sch.getHostName().equals(host3)) {
                sch.setState(INSTALL_FAILED);
            }
        }
        // Start
        long requestId = startService(cluster1, serviceName, false, true);
        List<Stage> stages = actionDB.getAllStages(requestId);
        HostRoleCommand clientReinstallCmd = null;
        for (Stage stage : stages) {
            for (HostRoleCommand hrc : stage.getOrderedHostRoleCommands()) {
                if ((hrc.getHostName().equals(host3)) && (hrc.getRole().toString().equals("HDFS_CLIENT"))) {
                    clientReinstallCmd = hrc;
                    break;
                }
            }
        }
        Assert.assertNotNull(clientReinstallCmd);
    }

    @Test
    public void testReInstallClientComponent() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-2.0.6"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        String host3 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        addHostToCluster(host3, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host3, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Reinstall SCH
        ServiceComponentHostRequest schr = new ServiceComponentHostRequest(cluster1, serviceName, componentName3, host3, INSTALLED.name());
        Set<ServiceComponentHostRequest> setReqs = new HashSet<>();
        setReqs.add(schr);
        RequestStatusResponse resp = updateHostComponents(setReqs, Collections.emptyMap(), false);
        Assert.assertNotNull(resp);
        Assert.assertTrue(((resp.getRequestId()) > 0));
        List<Stage> stages = actionDB.getAllStages(resp.getRequestId());
        HostRoleCommand clientReinstallCmd = null;
        for (Stage stage : stages) {
            for (HostRoleCommand hrc : stage.getOrderedHostRoleCommands()) {
                if ((hrc.getHostName().equals(host3)) && (hrc.getRole().toString().equals("HDFS_CLIENT"))) {
                    clientReinstallCmd = hrc;
                    break;
                }
            }
        }
        Assert.assertNotNull(clientReinstallCmd);
    }

    @Test
    public void testReInstallClientComponentFromServiceChange() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-2.0.6"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName, host2, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Start Service
        ServiceRequest sr = new ServiceRequest(cluster1, serviceName, repositoryVersion206.getId(), STARTED.name());
        Set<ServiceRequest> setReqs = new HashSet<>();
        setReqs.add(sr);
        RequestStatusResponse resp = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, setReqs, Collections.emptyMap(), false, true);
        Assert.assertNotNull(resp);
        Assert.assertTrue(((resp.getRequestId()) > 0));
        List<Stage> stages = actionDB.getAllStages(resp.getRequestId());
        Map<String, Role> hostsToRoles = new HashMap<>();
        for (Stage stage : stages) {
            for (HostRoleCommand hrc : stage.getOrderedHostRoleCommands()) {
                hostsToRoles.put(hrc.getHostName(), hrc.getRole());
            }
        }
        Map<String, Role> expectedHostsToRoles = new HashMap<>();
        expectedHostsToRoles.put(host1, HDFS_CLIENT);
        expectedHostsToRoles.put(host2, HDFS_CLIENT);
        Assert.assertEquals(expectedHostsToRoles, hostsToRoles);
    }

    @Test
    public void testDecommissonDatanodeAction() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-2.0.7"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        // !!! for whatever reason, the assertions are alphabetical to hostnames
        final String host1 = "d" + (AmbariManagementControllerTest.getUniqueName());
        final String host2 = "e" + (AmbariManagementControllerTest.getUniqueName());
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        RequestOperationLevel level = new RequestOperationLevel(Type.HostComponent, cluster1, null, null, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Create and attach config
        Map<String, String> configs = new HashMap<>();
        configs.put("a", "b");
        ConfigurationRequest cr1;
        cr1 = new ConfigurationRequest(cluster1, "hdfs-site", "version1", configs, null);
        ClusterRequest crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr1));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        // Start
        startService(cluster1, serviceName, false, false);
        cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = cluster.getService(serviceName);
        Assert.assertEquals(STARTED, s.getDesiredState());
        ServiceComponentHost scHost = s.getServiceComponent("DATANODE").getServiceComponentHost(host2);
        Assert.assertEquals(INSERVICE, scHost.getComponentAdminState());
        // Decommission one datanode
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("test", "test");
                put("excluded_hosts", host2);
                put("align_maintenance_state", "true");
            }
        };
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HDFS", "NAMENODE", null);
        ArrayList<RequestResourceFilter> filters = new ArrayList<>();
        filters.add(resourceFilter);
        ExecuteActionRequest request = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, filters, level, params, false);
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        RequestStatusResponse response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        List<HostRoleCommand> storedTasks = actionDB.getRequestTasks(response.getRequestId());
        ExecutionCommand execCmd = storedTasks.get(0).getExecutionCommandWrapper().getExecutionCommand();
        Assert.assertNotNull(storedTasks);
        Assert.assertEquals(1, storedTasks.size());
        Assert.assertEquals(DECOMMISSIONED, scHost.getComponentAdminState());
        Assert.assertEquals(ON, scHost.getMaintenanceState());
        HostRoleCommand command = storedTasks.get(0);
        Assert.assertEquals(NAMENODE, command.getRole());
        Assert.assertEquals(CUSTOM_COMMAND, command.getRoleCommand());
        Assert.assertEquals("DECOMMISSION", execCmd.getCommandParams().get("custom_command"));
        Assert.assertEquals(host2, execCmd.getCommandParams().get("all_decommissioned_hosts"));
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        // Decommission the other datanode
        params = new HashMap<String, String>() {
            {
                put("test", "test");
                put("excluded_hosts", host1);
                put("align_maintenance_state", "true");
            }
        };
        resourceFilter = new RequestResourceFilter("HDFS", "NAMENODE", null);
        filters = new ArrayList();
        filters.add(resourceFilter);
        request = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, filters, level, params, false);
        response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        storedTasks = actionDB.getRequestTasks(response.getRequestId());
        execCmd = storedTasks.get(0).getExecutionCommandWrapper().getExecutionCommand();
        Map<String, String> cmdParams = execCmd.getCommandParams();
        Assert.assertTrue(cmdParams.containsKey("update_files_only"));
        Assert.assertTrue(cmdParams.get("update_files_only").equals("false"));
        Assert.assertNotNull(storedTasks);
        Assert.assertEquals(1, storedTasks.size());
        Assert.assertEquals(DECOMMISSIONED, scHost.getComponentAdminState());
        Assert.assertEquals(ON, scHost.getMaintenanceState());
        Assert.assertEquals("DECOMMISSION", execCmd.getCommandParams().get("custom_command"));
        Assert.assertTrue(execCmd.getCommandParams().get("all_decommissioned_hosts").contains(host1));
        Assert.assertTrue(execCmd.getCommandParams().get("all_decommissioned_hosts").contains(host2));
        Assert.assertTrue(((execCmd.getCommandParams().get("all_decommissioned_hosts").equals(((host1 + ",") + host2))) || (execCmd.getCommandParams().get("all_decommissioned_hosts").equals(((host2 + ",") + host1)))));
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        // Recommission the other datanode  (while adding NameNode HA)
        createServiceComponentHost(cluster1, serviceName, componentName1, host2, null);
        ServiceComponentHostRequest r = new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host2, INSTALLED.toString());
        Set<ServiceComponentHostRequest> requests = new HashSet<>();
        requests.add(r);
        updateHostComponents(requests, Collections.emptyMap(), true);
        s.getServiceComponent(componentName1).getServiceComponentHost(host2).setState(INSTALLED);
        r = new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host2, STARTED.toString());
        requests.clear();
        requests.add(r);
        updateHostComponents(requests, Collections.emptyMap(), true);
        s.getServiceComponent(componentName1).getServiceComponentHost(host2).setState(STARTED);
        params = new HashMap<String, String>() {
            {
                put("test", "test");
                put("included_hosts", ((host1 + " , ") + host2));
                put("align_maintenance_state", "true");
            }
        };
        resourceFilter = new RequestResourceFilter("HDFS", "NAMENODE", null);
        filters = new ArrayList();
        filters.add(resourceFilter);
        request = new ExecuteActionRequest(cluster1, "DECOMMISSION", null, filters, level, params, false);
        response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        storedTasks = actionDB.getRequestTasks(response.getRequestId());
        Assert.assertNotNull(storedTasks);
        scHost = s.getServiceComponent("DATANODE").getServiceComponentHost(host2);
        Assert.assertEquals(INSERVICE, scHost.getComponentAdminState());
        Assert.assertEquals(OFF, scHost.getMaintenanceState());
        execCmd = storedTasks.get(0).getExecutionCommandWrapper().getExecutionCommand();
        Assert.assertNotNull(storedTasks);
        Assert.assertEquals(2, storedTasks.size());
        int countRefresh = 0;
        for (HostRoleCommand hrc : storedTasks) {
            Assert.assertEquals("DECOMMISSION", hrc.getCustomCommandName());
            // hostname order is not guaranteed
            Assert.assertTrue(hrc.getCommandDetail().contains("DECOMMISSION, Included: "));
            Assert.assertTrue(hrc.getCommandDetail().contains(host1));
            Assert.assertTrue(hrc.getCommandDetail().contains(host2));
            cmdParams = hrc.getExecutionCommandWrapper().getExecutionCommand().getCommandParams();
            if ((!(cmdParams.containsKey("update_files_only"))) || (!(cmdParams.get("update_files_only").equals("true")))) {
                countRefresh++;
            }
            Assert.assertEquals("", cmdParams.get("all_decommissioned_hosts"));
        }
        Assert.assertEquals(2, countRefresh);
        // Slave components will have admin state as INSERVICE even if the state in DB is null
        scHost.setComponentAdminState(null);
        Assert.assertEquals(INSERVICE, scHost.getComponentAdminState());
        Assert.assertEquals(OFF, scHost.getMaintenanceState());
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
    }

    @Test
    public void testResourceFiltersWithCustomActions() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        final String host3 = AmbariManagementControllerTest.getUniqueName();
        setupClusterWithHosts(cluster1, "HDP-2.0.6", new ArrayList<String>() {
            {
                add(host1);
                add(host2);
                add(host3);
            }
        }, "centos6");
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-2.0.6"));
        cluster.setCurrentStackVersion(new StackId("HDP-2.0.6"));
        RepositoryVersionEntity repositoryVersion = repositoryVersion206;
        ConfigFactory cf = AmbariManagementControllerTest.injector.getInstance(ConfigFactory.class);
        cf.createNew(cluster, "global", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        cf.createNew(cluster, "core-site", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        Service hdfs = cluster.addService("HDFS", repositoryVersion);
        Service mapred = cluster.addService("YARN", repositoryVersion);
        hdfs.addServiceComponent(HDFS_CLIENT.name());
        hdfs.addServiceComponent(NAMENODE.name());
        hdfs.addServiceComponent(DATANODE.name());
        mapred.addServiceComponent(RESOURCEMANAGER.name());
        hdfs.getServiceComponent(HDFS_CLIENT.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(NAMENODE.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(host2);
        String action1 = AmbariManagementControllerTest.getUniqueName();
        AmbariManagementControllerTest.controller.getAmbariMetaInfo().addActionDefinition(new ActionDefinition(action1, ActionType.SYSTEM, "", "HDFS", "", "Some custom action.", TargetHostType.ALL, Short.valueOf("10010"), null));
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("test", "test");
            }
        };
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        List<RequestResourceFilter> resourceFilters = new ArrayList<>();
        ArrayList<String> hosts = new ArrayList<String>() {
            {
                add(host2);
            }
        };
        RequestResourceFilter resourceFilter1 = new RequestResourceFilter("HDFS", "DATANODE", hosts);
        hosts = new ArrayList<String>() {
            {
                add(host1);
            }
        };
        RequestResourceFilter resourceFilter2 = new RequestResourceFilter("HDFS", "NAMENODE", hosts);
        resourceFilters.add(resourceFilter1);
        resourceFilters.add(resourceFilter2);
        ExecuteActionRequest actionRequest = new ExecuteActionRequest(cluster1, null, action1, resourceFilters, null, params, false);
        RequestStatusResponse response = null;
        try {
            AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        } catch (Exception ae) {
            AmbariManagementControllerTest.LOG.info("Expected exception.", ae);
            Assert.assertTrue(ae.getMessage().contains(("Custom action definition only " + "allows one resource filter to be specified")));
        }
        resourceFilters.remove(resourceFilter1);
        actionRequest = new ExecuteActionRequest(cluster1, null, action1, resourceFilters, null, params, false);
        response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        Assert.assertEquals(1, response.getTasks().size());
        HostRoleCommand nnCommand = null;
        for (HostRoleCommand hrc : actionDB.getRequestTasks(response.getRequestId())) {
            if (hrc.getHostName().equals(host1)) {
                nnCommand = hrc;
            }
        }
        Assert.assertNotNull(nnCommand);
        ExecutionCommand cmd = nnCommand.getExecutionCommandWrapper().getExecutionCommand();
        Assert.assertEquals(action1, cmd.getRole());
        Assert.assertEquals("10010", cmd.getCommandParams().get("command_timeout"));
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        for (Stage stage : actionDB.getAllStages(response.getRequestId())) {
            Map<String, String> commandParamsStage = StageUtils.getGson().fromJson(stage.getCommandParamsStage(), type);
            Assert.assertTrue(commandParamsStage.containsKey("test"));
        }
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
    }

    @Test
    public void testResourceFiltersWithCustomCommands() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        final String host3 = AmbariManagementControllerTest.getUniqueName();
        setupClusterWithHosts(cluster1, "HDP-2.0.6", new ArrayList<String>() {
            {
                add(host1);
                add(host2);
                add(host3);
            }
        }, "centos6");
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-2.0.6"));
        cluster.setCurrentStackVersion(new StackId("HDP-2.0.6"));
        RepositoryVersionEntity repositoryVersion = repositoryVersion206;
        ConfigFactory cf = AmbariManagementControllerTest.injector.getInstance(ConfigFactory.class);
        Config config1 = cf.createNew(cluster, "global", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        Config config2 = cf.createNew(cluster, "core-site", "version1", new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        }, new HashMap());
        Service hdfs = cluster.addService("HDFS", repositoryVersion);
        Service mapred = cluster.addService("YARN", repositoryVersion);
        hdfs.addServiceComponent(HDFS_CLIENT.name());
        hdfs.addServiceComponent(NAMENODE.name());
        hdfs.addServiceComponent(DATANODE.name());
        mapred.addServiceComponent(RESOURCEMANAGER.name());
        hdfs.getServiceComponent(HDFS_CLIENT.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(NAMENODE.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(host1);
        hdfs.getServiceComponent(DATANODE.name()).addServiceComponentHost(host2);
        mapred.getServiceComponent(RESOURCEMANAGER.name()).addServiceComponentHost(host2);
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("test", "test");
            }
        };
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        requestProperties.put("command_retry_enabled", "true");
        requestProperties.put("log_output", "false");
        // Test multiple restarts
        List<RequestResourceFilter> resourceFilters = new ArrayList<>();
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HDFS", DATANODE.name(), new ArrayList<String>() {
            {
                add(host1);
                add(host2);
            }
        });
        resourceFilters.add(resourceFilter);
        resourceFilter = new RequestResourceFilter("YARN", RESOURCEMANAGER.name(), new ArrayList<String>() {
            {
                add(host2);
            }
        });
        resourceFilters.add(resourceFilter);
        ExecuteActionRequest request = new ExecuteActionRequest(cluster1, "RESTART", null, resourceFilters, null, params, false);
        RequestStatusResponse response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        Assert.assertEquals(3, response.getTasks().size());
        List<HostRoleCommand> storedTasks = actionDB.getRequestTasks(response.getRequestId());
        Assert.assertNotNull(storedTasks);
        int expectedRestartCount = 0;
        for (HostRoleCommand hrc : storedTasks) {
            Assert.assertEquals("RESTART", hrc.getCustomCommandName());
            Map<String, String> cParams = hrc.getExecutionCommandWrapper().getExecutionCommand().getCommandParams();
            Assert.assertEquals("Expect retry to be set", true, cParams.containsKey("command_retry_enabled"));
            Assert.assertEquals("Expect max duration to be set", true, cParams.containsKey("max_duration_for_retries"));
            Assert.assertEquals("Expect max duration to be 600", "600", cParams.get("max_duration_for_retries"));
            Assert.assertEquals("Expect retry to be true", "true", cParams.get("command_retry_enabled"));
            Assert.assertEquals("Expect log_output to be set", true, cParams.containsKey("log_output"));
            Assert.assertEquals("Expect log_output to be false", "false", cParams.get("log_output"));
            if ((hrc.getHostName().equals(host1)) && (hrc.getRole().equals(DATANODE))) {
                expectedRestartCount++;
            } else
                if (hrc.getHostName().equals(host2)) {
                    if (hrc.getRole().equals(DATANODE)) {
                        expectedRestartCount++;
                    } else
                        if (hrc.getRole().equals(RESOURCEMANAGER)) {
                            expectedRestartCount++;
                        }

                }

        }
        Assert.assertEquals("Restart 2 datanodes and 1 Resourcemanager.", 3, expectedRestartCount);
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        requestProperties.put("max_duration_for_retries", "423");
        response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        Assert.assertEquals(3, response.getTasks().size());
        storedTasks = actionDB.getRequestTasks(response.getRequestId());
        Assert.assertNotNull(storedTasks);
        for (HostRoleCommand hrc : storedTasks) {
            Assert.assertEquals("RESTART", hrc.getCustomCommandName());
            Map<String, String> cParams = hrc.getExecutionCommandWrapper().getExecutionCommand().getCommandParams();
            Assert.assertEquals("Expect retry to be set", true, cParams.containsKey("command_retry_enabled"));
            Assert.assertEquals("Expect max duration to be set", true, cParams.containsKey("max_duration_for_retries"));
            Assert.assertEquals("Expect max duration to be 423", "423", cParams.get("max_duration_for_retries"));
            Assert.assertEquals("Expect retry to be true", "true", cParams.get("command_retry_enabled"));
        }
        requestProperties.remove("max_duration_for_retries");
        requestProperties.remove("command_retry_enabled");
        response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        Assert.assertEquals(3, response.getTasks().size());
        storedTasks = actionDB.getRequestTasks(response.getRequestId());
        Assert.assertNotNull(storedTasks);
        for (HostRoleCommand hrc : storedTasks) {
            Assert.assertEquals("RESTART", hrc.getCustomCommandName());
            Map<String, String> cParams = hrc.getExecutionCommandWrapper().getExecutionCommand().getCommandParams();
            Assert.assertEquals("Expect retry to be set", false, cParams.containsKey("command_retry_enabled"));
            Assert.assertEquals("Expect max duration to be set", false, cParams.containsKey("max_duration_for_retries"));
        }
        // Test service checks - specific host
        resourceFilters.clear();
        resourceFilter = new RequestResourceFilter("HDFS", null, new ArrayList<String>() {
            {
                add(host1);
            }
        });
        resourceFilters.add(resourceFilter);
        request = new ExecuteActionRequest(cluster1, HDFS_SERVICE_CHECK.name(), null, resourceFilters, null, null, false);
        response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        Assert.assertEquals(1, response.getTasks().size());
        storedTasks = actionDB.getRequestTasks(response.getRequestId());
        Assert.assertNotNull(storedTasks);
        Assert.assertEquals(HDFS_SERVICE_CHECK.name(), storedTasks.get(0).getRole().name());
        Assert.assertEquals(host1, storedTasks.get(0).getHostName());
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        Assert.assertEquals(STARTED, cluster.getService("HDFS").getServiceComponent(DATANODE.name()).getServiceComponentHost(host1).getDesiredState());
        Assert.assertEquals(STARTED, cluster.getService("HDFS").getServiceComponent(DATANODE.name()).getServiceComponentHost(host2).getDesiredState());
        Assert.assertEquals(STARTED, cluster.getService("YARN").getServiceComponent(RESOURCEMANAGER.name()).getServiceComponentHost(host2).getDesiredState());
    }

    @Test
    public void testConfigsAttachedToServiceChecks() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        // null service should work
        createServiceComponentHost(cluster1, null, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        // Create and attach config
        Map<String, String> configs = new HashMap<>();
        configs.put("a", "b");
        ConfigurationRequest cr1;
        ConfigurationRequest cr2;
        cr1 = new ConfigurationRequest(cluster1, "core-site", "version1", configs, null);
        cr2 = new ConfigurationRequest(cluster1, "hdfs-site", "version1", configs, null);
        ClusterRequest crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr1));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr2));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Start
        long requestId = startService(cluster1, serviceName, true, false);
        List<Stage> stages = actionDB.getAllStages(requestId);
        boolean serviceCheckFound = false;
        for (Stage stage : stages) {
            for (HostRoleCommand hrc : stage.getOrderedHostRoleCommands()) {
                if (hrc.getRole().equals(HDFS_SERVICE_CHECK)) {
                    serviceCheckFound = true;
                }
            }
        }
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        for (Stage stage : actionDB.getAllStages(requestId)) {
            Map<String, String> hostParamsStage = StageUtils.getGson().fromJson(stage.getHostParamsStage(), type);
            Assert.assertNotNull(hostParamsStage.get("jdk_location"));
        }
        Assert.assertEquals(true, serviceCheckFound);
    }

    @Test
    public void testHostLevelParamsSentWithCommands() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        String serviceName = "PIG";
        createService(cluster1, serviceName, repositoryVersion01, null);
        String componentName1 = "PIG";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        // null service should work
        createServiceComponentHost(cluster1, null, componentName1, host1, null);
        createServiceComponentHost(cluster1, null, componentName1, host2, null);
        ServiceRequest r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), INSTALLED.toString());
        Set<ServiceRequest> requests = new HashSet<>();
        requests.add(r);
        RequestStatusResponse trackAction = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertEquals(INSTALLED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        List<Stage> stages = actionDB.getAllStages(trackAction.getRequestId());
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        for (Stage stage : stages) {
            Map<String, String> params = StageUtils.getGson().fromJson(stage.getHostParamsStage(), type);
            Assert.assertEquals("0.1", params.get("stack_version"));
            Assert.assertNotNull(params.get("jdk_location"));
            Assert.assertNotNull(params.get("db_name"));
            Assert.assertNotNull(params.get("mysql_jdbc_url"));
            Assert.assertNotNull(params.get("oracle_jdbc_url"));
        }
        ExecutionCommand executionCommand = stages.get(0).getOrderedHostRoleCommands().get(0).getExecutionCommandWrapper().getExecutionCommand();
        Map<String, String> paramsCmd = executionCommand.getHostLevelParams();
        Assert.assertNotNull(executionCommand.getRepositoryFile());
        Assert.assertNotNull(paramsCmd.get("clientsToUpdateConfigs"));
    }

    @Test
    public void testConfigGroupOverridesWithDecommissionDatanode() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-2.0.7"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Create and attach config
        Map<String, String> configs = new HashMap<>();
        configs.put("a", "b");
        ConfigurationRequest cr1;
        ConfigurationRequest cr2;
        cr1 = new ConfigurationRequest(cluster1, "hdfs-site", "version1", configs, null);
        ClusterRequest crReq = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr1));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        // Start
        startService(cluster1, serviceName, false, false);
        // Create Config group for hdfs-site
        configs = new HashMap<>();
        configs.put("a", "c");
        String group1 = AmbariManagementControllerTest.getUniqueName();
        String tag1 = AmbariManagementControllerTest.getUniqueName();
        ConfigFactory configFactory = AmbariManagementControllerTest.injector.getInstance(ConfigFactory.class);
        final Config config = configFactory.createReadOnly("hdfs-site", "version122", configs, null);
        Long groupId = createConfigGroup(AmbariManagementControllerTest.clusters.getCluster(cluster1), serviceName, group1, tag1, new ArrayList<String>() {
            {
                add(host1);
                add(host2);
            }
        }, new ArrayList<Config>() {
            {
                add(config);
            }
        });
        Assert.assertNotNull(groupId);
        cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s = cluster.getService(serviceName);
        Assert.assertEquals(STARTED, s.getDesiredState());
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("test", "test");
                put("excluded_hosts", host1);
            }
        };
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HDFS", "NAMENODE", null);
        ExecuteActionRequest request = new ExecuteActionRequest(cluster1, "DECOMMISSION", params, false);
        request.getResourceFilters().add(resourceFilter);
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        RequestStatusResponse response = AmbariManagementControllerTest.controller.createAction(request, requestProperties);
        List<HostRoleCommand> storedTasks = actionDB.getRequestTasks(response.getRequestId());
        ExecutionCommand execCmd = storedTasks.get(0).getExecutionCommandWrapper().getExecutionCommand();
        Assert.assertNotNull(storedTasks);
        Assert.assertEquals(1, storedTasks.size());
        HostRoleCommand command = storedTasks.get(0);
        Assert.assertEquals(NAMENODE, command.getRole());
        Assert.assertEquals(CUSTOM_COMMAND, command.getRoleCommand());
        Assert.assertEquals("DECOMMISSION", execCmd.getCommandParams().get("custom_command"));
        Assert.assertEquals(host1, execCmd.getCommandParams().get("all_decommissioned_hosts"));
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
    }

    @Test
    public void testGetStacks() throws Exception {
        HashSet<String> availableStacks = new HashSet<>();
        for (StackInfo stackInfo : AmbariManagementControllerTest.ambariMetaInfo.getStacks()) {
            availableStacks.add(stackInfo.getName());
        }
        StackRequest request = new StackRequest(null);
        Set<StackResponse> responses = AmbariManagementControllerTest.controller.getStacks(Collections.singleton(request));
        Assert.assertEquals(availableStacks.size(), responses.size());
        StackRequest requestWithParams = new StackRequest(AmbariManagementControllerTest.STACK_NAME);
        Set<StackResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStacks(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getStackName(), AmbariManagementControllerTest.STACK_NAME);
        }
        StackRequest invalidRequest = new StackRequest(AmbariManagementControllerTest.NON_EXT_VALUE);
        try {
            AmbariManagementControllerTest.controller.getStacks(Collections.singleton(invalidRequest));
        } catch (StackAccessException e) {
            // do nothing
        }
    }

    @Test
    public void testGetStackVersions() throws Exception {
        StackVersionRequest request = new StackVersionRequest(AmbariManagementControllerTest.STACK_NAME, null);
        Set<StackVersionResponse> responses = AmbariManagementControllerTest.controller.getStackVersions(Collections.singleton(request));
        Assert.assertEquals(AmbariManagementControllerTest.STACK_VERSIONS_CNT, responses.size());
        StackVersionRequest requestWithParams = new StackVersionRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION);
        Set<StackVersionResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackVersions(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackVersionResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getStackVersion(), AmbariManagementControllerTest.STACK_VERSION);
        }
        StackVersionRequest invalidRequest = new StackVersionRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NON_EXT_VALUE);
        try {
            AmbariManagementControllerTest.controller.getStackVersions(Collections.singleton(invalidRequest));
        } catch (StackAccessException e) {
            // do nothing
        }
        // test that a stack response has upgrade packs
        requestWithParams = new StackVersionRequest(AmbariManagementControllerTest.STACK_NAME, "2.1.1");
        responsesWithParams = AmbariManagementControllerTest.controller.getStackVersions(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        StackVersionResponse resp = responsesWithParams.iterator().next();
        Assert.assertNotNull(resp.getUpgradePacks());
        Assert.assertTrue(((resp.getUpgradePacks().size()) > 0));
        Assert.assertTrue(resp.getUpgradePacks().contains("upgrade_test"));
    }

    @Test
    public void testGetStackVersionActiveAttr() throws Exception {
        for (StackInfo stackInfo : AmbariManagementControllerTest.ambariMetaInfo.getStacks(AmbariManagementControllerTest.STACK_NAME)) {
            if (stackInfo.getVersion().equalsIgnoreCase(AmbariManagementControllerTest.STACK_VERSION)) {
                stackInfo.setActive(true);
            }
        }
        StackVersionRequest requestWithParams = new StackVersionRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION);
        Set<StackVersionResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackVersions(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackVersionResponse responseWithParams : responsesWithParams) {
            Assert.assertTrue(responseWithParams.isActive());
        }
    }

    @Test
    public void testGetRepositories() throws Exception {
        RepositoryRequest request = new RepositoryRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.OS_TYPE, null, AmbariManagementControllerTest.REPO_NAME);
        Set<RepositoryResponse> responses = AmbariManagementControllerTest.controller.getRepositories(Collections.singleton(request));
        Assert.assertEquals(AmbariManagementControllerTest.REPOS_CNT, responses.size());
        RepositoryRequest requestWithParams = new RepositoryRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.OS_TYPE, AmbariManagementControllerTest.REPO_ID, AmbariManagementControllerTest.REPO_NAME);
        requestWithParams.setClusterVersionId(525L);
        Set<RepositoryResponse> responsesWithParams = AmbariManagementControllerTest.controller.getRepositories(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (RepositoryResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getRepoId(), AmbariManagementControllerTest.REPO_ID);
            Assert.assertEquals(525L, responseWithParams.getClusterVersionId().longValue());
        }
        RepositoryRequest invalidRequest = new RepositoryRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.OS_TYPE, AmbariManagementControllerTest.NON_EXT_VALUE, AmbariManagementControllerTest.REPO_NAME);
        try {
            AmbariManagementControllerTest.controller.getRepositories(Collections.singleton(invalidRequest));
        } catch (StackAccessException e) {
            // do nothing
        }
    }

    @Test
    public void testGetStackServices() throws Exception {
        StackServiceRequest request = new StackServiceRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, null);
        Set<StackServiceResponse> responses = AmbariManagementControllerTest.controller.getStackServices(Collections.singleton(request));
        Assert.assertEquals(12, responses.size());
        StackServiceRequest requestWithParams = new StackServiceRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME);
        Set<StackServiceResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackServices(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackServiceResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getServiceName(), AmbariManagementControllerTest.SERVICE_NAME);
            Assert.assertTrue(((responseWithParams.getConfigTypes().size()) > 0));
        }
        StackServiceRequest invalidRequest = new StackServiceRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.NON_EXT_VALUE);
        try {
            AmbariManagementControllerTest.controller.getStackServices(Collections.singleton(invalidRequest));
        } catch (StackAccessException e) {
            // do nothing
        }
    }

    @Test
    public void testConfigInComponent() throws Exception {
        StackServiceRequest requestWithParams = new StackServiceRequest(AmbariManagementControllerTest.STACK_NAME, "2.0.6", "YARN");
        Set<StackServiceResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackServices(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackServiceResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getServiceName(), "YARN");
            Assert.assertTrue(responseWithParams.getConfigTypes().containsKey("capacity-scheduler"));
        }
    }

    @Test
    public void testGetStackConfigurations() throws Exception {
        StackConfigurationRequest request = new StackConfigurationRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, null);
        Set<StackConfigurationResponse> responses = AmbariManagementControllerTest.controller.getStackConfigurations(Collections.singleton(request));
        Assert.assertEquals(AmbariManagementControllerTest.STACK_PROPERTIES_CNT, responses.size());
        StackConfigurationRequest requestWithParams = new StackConfigurationRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, AmbariManagementControllerTest.PROPERTY_NAME);
        Set<StackConfigurationResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackConfigurations(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackConfigurationResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getPropertyName(), AmbariManagementControllerTest.PROPERTY_NAME);
        }
        StackConfigurationRequest invalidRequest = new StackConfigurationRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, AmbariManagementControllerTest.NON_EXT_VALUE);
        try {
            AmbariManagementControllerTest.controller.getStackConfigurations(Collections.singleton(invalidRequest));
        } catch (StackAccessException e) {
            // do nothing
        }
    }

    @Test
    public void testGetStackComponents() throws Exception {
        StackServiceComponentRequest request = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, null);
        Set<StackServiceComponentResponse> responses = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(request));
        Assert.assertEquals(AmbariManagementControllerTest.STACK_COMPONENTS_CNT, responses.size());
        StackServiceComponentRequest requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, AmbariManagementControllerTest.COMPONENT_NAME);
        Set<StackServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME);
        }
        StackServiceComponentRequest invalidRequest = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, AmbariManagementControllerTest.NON_EXT_VALUE);
        try {
            AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(invalidRequest));
        } catch (StackAccessException e) {
            // do nothing
        }
    }

    @Test
    public void testGetStackOperatingSystems() throws Exception {
        OperatingSystemRequest request = new OperatingSystemRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, null);
        Set<OperatingSystemResponse> responses = AmbariManagementControllerTest.controller.getOperatingSystems(Collections.singleton(request));
        Assert.assertEquals(AmbariManagementControllerTest.OS_CNT, responses.size());
        OperatingSystemRequest requestWithParams = new OperatingSystemRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.OS_TYPE);
        Set<OperatingSystemResponse> responsesWithParams = AmbariManagementControllerTest.controller.getOperatingSystems(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (OperatingSystemResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getOsType(), AmbariManagementControllerTest.OS_TYPE);
        }
        OperatingSystemRequest invalidRequest = new OperatingSystemRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.NON_EXT_VALUE);
        try {
            AmbariManagementControllerTest.controller.getOperatingSystems(Collections.singleton(invalidRequest));
        } catch (StackAccessException e) {
            // do nothing
        }
    }

    @Test
    public void testGetStackOperatingSystemsWithRepository() throws Exception {
        RepositoryVersionDAO dao = AmbariManagementControllerTest.injector.getInstance(RepositoryVersionDAO.class);
        StackDAO stackDAO = AmbariManagementControllerTest.injector.getInstance(StackDAO.class);
        StackEntity stackEntity = stackDAO.find(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION);
        Assert.assertNotNull(stackEntity);
        RepositoryVersionEntity versionEntity = dao.create(stackEntity, "0.2.2", "HDP-0.2", ClusterStackVersionResourceProviderTest.REPO_OS_ENTITIES);
        OperatingSystemRequest request = new OperatingSystemRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, null);
        Set<OperatingSystemResponse> responses = AmbariManagementControllerTest.controller.getOperatingSystems(Collections.singleton(request));
        Assert.assertEquals(AmbariManagementControllerTest.OS_CNT, responses.size());
        OperatingSystemRequest requestWithParams = new OperatingSystemRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION, AmbariManagementControllerTest.OS_TYPE);
        requestWithParams.setVersionDefinitionId(versionEntity.getId().toString());
        Set<OperatingSystemResponse> responsesWithParams = AmbariManagementControllerTest.controller.getOperatingSystems(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
    }

    @Test
    public void testStackServiceCheckSupported() throws Exception {
        StackServiceRequest hdfsServiceRequest = new StackServiceRequest(AmbariManagementControllerTest.STACK_NAME, "2.0.8", AmbariManagementControllerTest.SERVICE_NAME);
        Set<StackServiceResponse> responses = AmbariManagementControllerTest.controller.getStackServices(Collections.singleton(hdfsServiceRequest));
        Assert.assertEquals(1, responses.size());
        StackServiceResponse response = responses.iterator().next();
        Assert.assertTrue(response.isServiceCheckSupported());
        StackServiceRequest fakeServiceRequest = new StackServiceRequest(AmbariManagementControllerTest.STACK_NAME, "2.0.8", AmbariManagementControllerTest.FAKE_SERVICE_NAME);
        responses = AmbariManagementControllerTest.controller.getStackServices(Collections.singleton(fakeServiceRequest));
        Assert.assertEquals(1, responses.size());
        response = responses.iterator().next();
        Assert.assertFalse(response.isServiceCheckSupported());
    }

    @Test
    public void testStackServiceComponentCustomCommands() throws Exception {
        StackServiceComponentRequest namenodeRequest = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, AmbariManagementControllerTest.COMPONENT_NAME);
        Set<StackServiceComponentResponse> responses = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(namenodeRequest));
        Assert.assertEquals(1, responses.size());
        StackServiceComponentResponse response = responses.iterator().next();
        Assert.assertNotNull(response.getCustomCommands());
        Assert.assertEquals(2, response.getCustomCommands().size());
        Assert.assertEquals("DECOMMISSION", response.getCustomCommands().get(0));
        Assert.assertEquals("REBALANCEHDFS", response.getCustomCommands().get(1));
        StackServiceComponentRequest journalNodeRequest = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, "JOURNALNODE");
        responses = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(journalNodeRequest));
        Assert.assertEquals(1, responses.size());
        response = responses.iterator().next();
        Assert.assertNotNull(response.getCustomCommands());
        Assert.assertEquals(0, response.getCustomCommands().size());
    }

    @Test
    public void testDecommissionAllowed() throws Exception {
        StackServiceComponentRequest requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME_HBASE, AmbariManagementControllerTest.COMPONENT_NAME_REGIONSERVER);
        Set<StackServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME_REGIONSERVER);
            Assert.assertTrue(responseWithParams.isDecommissionAlllowed());
        }
    }

    @Test
    public void testDecommissionAllowedInheritance() throws Exception {
        // parent has it, child doesn't
        StackServiceComponentRequest requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, AmbariManagementControllerTest.COMPONENT_NAME_DATANODE);
        Set<StackServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME_DATANODE);
            Assert.assertTrue(responseWithParams.isDecommissionAlllowed());
        }
    }

    @Test
    public void testDecommissionAllowedOverwrite() throws Exception {
        StackServiceComponentRequest requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, "2.0.5", AmbariManagementControllerTest.SERVICE_NAME_YARN, AmbariManagementControllerTest.COMPONENT_NAME_NODEMANAGER);
        Set<StackServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        // parent has it
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME_NODEMANAGER);
            Assert.assertFalse(responseWithParams.isDecommissionAlllowed());
        }
        requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME_YARN, AmbariManagementControllerTest.COMPONENT_NAME_NODEMANAGER);
        responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        // parent has it, child overwrites it
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME_NODEMANAGER);
            Assert.assertTrue(responseWithParams.isDecommissionAlllowed());
        }
    }

    @Test
    public void testRassignAllowed() throws Exception {
        StackServiceComponentRequest requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, "2.0.5", AmbariManagementControllerTest.SERVICE_NAME, AmbariManagementControllerTest.COMPONENT_NAME);
        Set<StackServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME);
            Assert.assertTrue(responseWithParams.isReassignAlllowed());
        }
        requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, "2.0.5", AmbariManagementControllerTest.SERVICE_NAME, AmbariManagementControllerTest.COMPONENT_NAME_DATANODE);
        responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME_DATANODE);
            Assert.assertFalse(responseWithParams.isReassignAlllowed());
        }
    }

    @Test
    public void testReassignAllowedInheritance() throws Exception {
        // parent has it, child doesn't
        StackServiceComponentRequest requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME_HIVE, AmbariManagementControllerTest.COMPONENT_NAME_HIVE_METASTORE);
        Set<StackServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME_HIVE_METASTORE);
            Assert.assertTrue(responseWithParams.isReassignAlllowed());
        }
    }

    @Test
    public void testReassignAllowedOverwrite() throws Exception {
        StackServiceComponentRequest requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, "2.0.5", AmbariManagementControllerTest.SERVICE_NAME_HIVE, AmbariManagementControllerTest.COMPONENT_NAME_HIVE_SERVER);
        Set<StackServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        // parent has it
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME_HIVE_SERVER);
            Assert.assertTrue(responseWithParams.isReassignAlllowed());
        }
        requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME_HIVE, AmbariManagementControllerTest.COMPONENT_NAME_HIVE_SERVER);
        responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        // parent has it, child overwrites it
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME_HIVE_SERVER);
            Assert.assertFalse(responseWithParams.isReassignAlllowed());
        }
    }

    @Test
    public void testBulkCommandsInheritence() throws Exception {
        // HDP 2.0.6 inherit HDFS configurations from HDP 2.0.5
        StackServiceComponentRequest requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME, AmbariManagementControllerTest.COMPONENT_NAME_DATANODE);
        Set<StackServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AmbariManagementControllerTest.COMPONENT_NAME_DATANODE);
            Assert.assertEquals(responseWithParams.getBulkCommandsDisplayName(), "DataNodes");
            Assert.assertEquals(responseWithParams.getBulkCommandsMasterComponentName(), "NAMENODE");
        }
    }

    @Test
    public void testBulkCommandsChildStackOverride() throws Exception {
        // Both HDP 2.0.6 and HDP 2.0.5 has HBase configurations
        StackServiceComponentRequest requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, "2.0.5", AmbariManagementControllerTest.SERVICE_NAME_HBASE, AmbariManagementControllerTest.COMPONENT_NAME_REGIONSERVER);
        Set<StackServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getBulkCommandsDisplayName(), "Region Servers");
            Assert.assertEquals(responseWithParams.getBulkCommandsMasterComponentName(), "HBASE_MASTER");
        }
        requestWithParams = new StackServiceComponentRequest(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.NEW_STACK_VERSION, AmbariManagementControllerTest.SERVICE_NAME_HBASE, AmbariManagementControllerTest.COMPONENT_NAME_REGIONSERVER);
        responsesWithParams = AmbariManagementControllerTest.controller.getStackComponents(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (StackServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getBulkCommandsDisplayName(), "HBase Region Servers");
            Assert.assertEquals(responseWithParams.getBulkCommandsMasterComponentName(), "HBASE_MASTER");
        }
    }

    @Test
    public void testUpdateClusterUpgradabilityCheck() throws Exception, AuthorizationException {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        StackId currentStackId = new StackId("HDP-0.2");
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        createCluster(cluster1);
        Cluster c = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        c.setDesiredStackVersion(currentStackId);
        ClusterRequest r = new ClusterRequest(c.getClusterId(), cluster1, "HDP-0.3", null);
        try {
            AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(r), mapRequestProps);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Illegal request to upgrade to"));
        }
        StackId unsupportedStackId = new StackId("HDP-2.2.0");
        c.setDesiredStackVersion(unsupportedStackId);
        c.setCurrentStackVersion(unsupportedStackId);
        c.refresh();
        r = new ClusterRequest(c.getClusterId(), cluster1, "HDP-0.2", null);
        try {
            AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(r), mapRequestProps);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Upgrade is not allowed from"));
        }
    }

    class ExpectedUpgradeTasks {
        private static final int ROLE_COUNT = 25;

        private static final String DEFAULT_HOST = "default_host";

        private ArrayList<Map<String, Boolean>> expectedList;

        private Map<Role, Integer> roleToIndex;

        public ExpectedUpgradeTasks(List<String> hosts) {
            roleToIndex = new HashMap();
            expectedList = new ArrayList<>(AmbariManagementControllerTest.ExpectedUpgradeTasks.ROLE_COUNT);
            fillRoleToIndex();
            fillExpectedHosts(hosts);
        }

        public void expectTask(Role role, String host) {
            expectedList.get(roleToIndex.get(role)).put(host, true);
        }

        public void expectTask(Role role) {
            Assert.assertEquals(AMBARI_SERVER_ACTION, role);
            expectTask(role, AmbariManagementControllerTest.ExpectedUpgradeTasks.DEFAULT_HOST);
        }

        public boolean isTaskExpected(Role role, String host) {
            return expectedList.get(roleToIndex.get(role)).get(host);
        }

        public boolean isTaskExpected(Role role) {
            Assert.assertEquals(AMBARI_SERVER_ACTION, role);
            return isTaskExpected(role, AmbariManagementControllerTest.ExpectedUpgradeTasks.DEFAULT_HOST);
        }

        public int getRoleOrder(Role role) {
            return roleToIndex.get(role);
        }

        public void resetAll() {
            for (Role role : roleToIndex.keySet()) {
                Map<String, Boolean> hostState = expectedList.get(roleToIndex.get(role));
                for (String host : hostState.keySet()) {
                    hostState.put(host, false);
                }
            }
        }

        private void fillExpectedHosts(List<String> hosts) {
            for (int index = 0; index < (AmbariManagementControllerTest.ExpectedUpgradeTasks.ROLE_COUNT); index++) {
                Map<String, Boolean> hostState = new HashMap<>();
                for (String host : hosts) {
                    hostState.put(host, false);
                }
                expectedList.add(hostState);
            }
        }

        private void fillRoleToIndex() {
            roleToIndex.put(NAMENODE, 0);
            roleToIndex.put(SECONDARY_NAMENODE, 1);
            roleToIndex.put(DATANODE, 2);
            roleToIndex.put(HDFS_CLIENT, 3);
            roleToIndex.put(JOBTRACKER, 4);
            roleToIndex.put(TASKTRACKER, 5);
            roleToIndex.put(MAPREDUCE_CLIENT, 6);
            roleToIndex.put(ZOOKEEPER_SERVER, 7);
            roleToIndex.put(ZOOKEEPER_CLIENT, 8);
            roleToIndex.put(HBASE_MASTER, 9);
            roleToIndex.put(HBASE_REGIONSERVER, 10);
            roleToIndex.put(HBASE_CLIENT, 11);
            roleToIndex.put(HIVE_SERVER, 12);
            roleToIndex.put(HIVE_METASTORE, 13);
            roleToIndex.put(HIVE_CLIENT, 14);
            roleToIndex.put(HCAT, 15);
            roleToIndex.put(OOZIE_SERVER, 16);
            roleToIndex.put(OOZIE_CLIENT, 17);
            roleToIndex.put(WEBHCAT_SERVER, 18);
            roleToIndex.put(PIG, 19);
            roleToIndex.put(SQOOP, 20);
            roleToIndex.put(GANGLIA_SERVER, 21);
            roleToIndex.put(GANGLIA_MONITOR, 22);
            roleToIndex.put(AMBARI_SERVER_ACTION, 23);
        }
    }

    @Test
    public void testServiceStopWhileStopping() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        // null service should work
        createServiceComponentHost(cluster1, null, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName1).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host2));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName3).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName3).getServiceComponentHost(host2));
        // Install
        ServiceRequest r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), INSTALLED.toString());
        Set<ServiceRequest> requests = new HashSet<>();
        requests.add(r);
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertEquals(INSTALLED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        // manually change live state to installed as no running action manager
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                sch.setState(INSTALLED);
            }
        }
        // Start
        r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), STARTED.toString());
        requests.clear();
        requests.add(r);
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        // manually change live state to started as no running action manager
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                if (!(sch.getServiceComponentName().equals("HDFS_CLIENT"))) {
                    sch.setState(STARTED);
                }
            }
        }
        Assert.assertEquals(STARTED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        // Set Current state to stopping
        AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).setDesiredState(STOPPING);
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                if (!(sch.getServiceComponentName().equals("HDFS_CLIENT"))) {
                    Assert.assertEquals(STARTED, sch.getDesiredState());
                    sch.setState(STOPPING);
                } else
                    if (sch.getServiceComponentName().equals("DATANODE")) {
                        ServiceComponentHostRequest r1 = new ServiceComponentHostRequest(cluster1, serviceName, sch.getServiceComponentName(), sch.getHostName(), INSTALLED.name());
                        Set<ServiceComponentHostRequest> reqs1 = new HashSet<>();
                        reqs1.add(r1);
                        updateHostComponents(reqs1, Collections.emptyMap(), true);
                        Assert.assertEquals(INSTALLED, sch.getDesiredState());
                    }

            }
        }
        // Stop all services
        r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), INSTALLED.toString());
        requests.clear();
        requests.add(r);
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        for (ServiceComponent sc : AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                if (!(sch.getServiceComponentName().equals("HDFS_CLIENT"))) {
                    Assert.assertEquals(INSTALLED, sch.getDesiredState());
                }
            }
        }
    }

    @Test
    public void testGetTasksByRequestId() throws Exception {
        ActionManager am = AmbariManagementControllerTest.injector.getInstance(ActionManager.class);
        final long requestId1 = am.getNextRequestId();
        final long requestId2 = am.getNextRequestId();
        final long requestId3 = am.getNextRequestId();
        final String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String hostName1 = AmbariManagementControllerTest.getUniqueName();
        final String context = "Test invocation";
        StackId stackID = new StackId("HDP-0.1");
        AmbariManagementControllerTest.clusters.addCluster(cluster1, stackID);
        Cluster c = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Long clusterId = c.getClusterId();
        helper.getOrCreateRepositoryVersion(stackID, stackID.getStackVersion());
        AmbariManagementControllerTest.clusters.addHost(hostName1);
        setOsFamily(AmbariManagementControllerTest.clusters.getHost(hostName1), "redhat", "5.9");
        AmbariManagementControllerTest.clusters.mapAndPublishHostsToCluster(new HashSet<String>() {
            {
                add(hostName1);
            }
        }, cluster1);
        List<Stage> stages = new ArrayList<>();
        stages.add(stageFactory.createNew(requestId1, "/a1", cluster1, clusterId, context, "", ""));
        stages.get(0).setStageId(1);
        stages.get(0).addHostRoleExecutionCommand(hostName1, HBASE_MASTER, START, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(HBASE_MASTER.toString(), hostName1, System.currentTimeMillis()), cluster1, "HBASE", false, false);
        stages.add(stageFactory.createNew(requestId1, "/a2", cluster1, clusterId, context, "", ""));
        stages.get(1).setStageId(2);
        stages.get(1).addHostRoleExecutionCommand(hostName1, HBASE_CLIENT, START, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(HBASE_CLIENT.toString(), hostName1, System.currentTimeMillis()), cluster1, "HBASE", false, false);
        stages.add(stageFactory.createNew(requestId1, "/a3", cluster1, clusterId, context, "", ""));
        stages.get(2).setStageId(3);
        stages.get(2).addHostRoleExecutionCommand(hostName1, HBASE_CLIENT, START, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(HBASE_CLIENT.toString(), hostName1, System.currentTimeMillis()), cluster1, "HBASE", false, false);
        Request request = new Request(stages, "", AmbariManagementControllerTest.clusters);
        actionDB.persistActions(request);
        stages.clear();
        stages.add(stageFactory.createNew(requestId2, "/a4", cluster1, clusterId, context, "", ""));
        stages.get(0).setStageId(4);
        stages.get(0).addHostRoleExecutionCommand(hostName1, HBASE_CLIENT, START, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(HBASE_CLIENT.toString(), hostName1, System.currentTimeMillis()), cluster1, "HBASE", false, false);
        stages.add(stageFactory.createNew(requestId2, "/a5", cluster1, clusterId, context, "", ""));
        stages.get(1).setStageId(5);
        stages.get(1).addHostRoleExecutionCommand(hostName1, HBASE_CLIENT, START, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(HBASE_CLIENT.toString(), hostName1, System.currentTimeMillis()), cluster1, "HBASE", false, false);
        request = new Request(stages, "", AmbariManagementControllerTest.clusters);
        actionDB.persistActions(request);
        // Add a stage to execute a task as server-side action on the Ambari server
        ServiceComponentHostServerActionEvent serviceComponentHostServerActionEvent = new ServiceComponentHostServerActionEvent(AMBARI_SERVER_ACTION.toString(), null, System.currentTimeMillis());
        stages.clear();
        stages.add(stageFactory.createNew(requestId3, "/a6", cluster1, clusterId, context, "", ""));
        stages.get(0).setStageId(6);
        stages.get(0).addServerActionCommand("some.action.class.name", null, AMBARI_SERVER_ACTION, EXECUTE, cluster1, serviceComponentHostServerActionEvent, null, null, null, null, false, false);
        Assert.assertEquals("_internal_ambari", stages.get(0).getOrderedHostRoleCommands().get(0).getHostName());
        request = new Request(stages, "", AmbariManagementControllerTest.clusters);
        actionDB.persistActions(request);
        org.apache.ambari.server.controller.spi.Request spiRequest = PropertyHelper.getReadRequest(TASK_CLUSTER_NAME_PROPERTY_ID, TASK_REQUEST_ID_PROPERTY_ID, TASK_STAGE_ID_PROPERTY_ID);
        // request ID 1 has 3 tasks
        Predicate predicate = new PredicateBuilder().property(TASK_REQUEST_ID_PROPERTY_ID).equals(requestId1).toPredicate();
        List<HostRoleCommandEntity> entities = hostRoleCommandDAO.findAll(spiRequest, predicate);
        Assert.assertEquals(3, entities.size());
        Long taskId = entities.get(0).getTaskId();
        // request just a task by ID
        predicate = new PredicateBuilder().property(TASK_REQUEST_ID_PROPERTY_ID).equals(requestId1).and().property(TASK_ID_PROPERTY_ID).equals(taskId).toPredicate();
        entities = hostRoleCommandDAO.findAll(spiRequest, predicate);
        Assert.assertEquals(1, entities.size());
        // request ID 2 has 2 tasks
        predicate = new PredicateBuilder().property(TASK_REQUEST_ID_PROPERTY_ID).equals(requestId2).toPredicate();
        entities = hostRoleCommandDAO.findAll(spiRequest, predicate);
        Assert.assertEquals(2, entities.size());
        // a single task from request 1 and all tasks from request 2 will total 3
        predicate = new PredicateBuilder().property(TASK_REQUEST_ID_PROPERTY_ID).equals(requestId1).and().property(TASK_ID_PROPERTY_ID).equals(taskId).or().property(TASK_REQUEST_ID_PROPERTY_ID).equals(requestId2).toPredicate();
        entities = hostRoleCommandDAO.findAll(spiRequest, predicate);
        Assert.assertEquals(3, entities.size());
    }

    @Test
    public void testUpdateHostComponentsBadState() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        // null service should work
        createServiceComponentHost(cluster1, null, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName1).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host2));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName3).getServiceComponentHost(host1));
        Assert.assertNotNull(AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getServiceComponent(componentName3).getServiceComponentHost(host2));
        // Install
        ServiceRequest r = new ServiceRequest(cluster1, serviceName, repositoryVersion01.getId(), INSTALLED.toString());
        Set<ServiceRequest> requests = new HashSet<>();
        requests.add(r);
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, requests, mapRequestProps, true, false);
        Assert.assertEquals(INSTALLED, AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName).getDesiredState());
        // set host components on host1 to INSTALLED
        for (ServiceComponentHost sch : AmbariManagementControllerTest.clusters.getCluster(cluster1).getServiceComponentHosts(host1)) {
            sch.setState(INSTALLED);
        }
        // set the host components on host2 to UNKNOWN state to simulate a lost host
        for (ServiceComponentHost sch : AmbariManagementControllerTest.clusters.getCluster(cluster1).getServiceComponentHosts(host2)) {
            sch.setState(UNKNOWN);
        }
        // issue an installed state request without failure
        ServiceComponentHostRequest schr = new ServiceComponentHostRequest(cluster1, "HDFS", "DATANODE", host2, "INSTALLED");
        Map<String, String> requestProps = new HashMap<>();
        requestProps.put("datanode", "dn_value");
        requestProps.put("namenode", "nn_value");
        RequestStatusResponse rsr = updateHostComponents(Collections.singleton(schr), requestProps, false);
        List<Stage> stages = actionDB.getAllStages(rsr.getRequestId());
        Assert.assertEquals(1, stages.size());
        Stage stage = stages.iterator().next();
        List<ExecutionCommandWrapper> execWrappers = stage.getExecutionCommands(host2);
        Assert.assertEquals(1, execWrappers.size());
        ExecutionCommandWrapper execWrapper = execWrappers.iterator().next();
        Assert.assertTrue(execWrapper.getExecutionCommand().getCommandParams().containsKey("datanode"));
        Assert.assertFalse(execWrapper.getExecutionCommand().getCommandParams().containsKey("namendode"));
        // set the host components on host2 to UNKNOWN state to simulate a lost host
        for (ServiceComponentHost sch : AmbariManagementControllerTest.clusters.getCluster(cluster1).getServiceComponentHosts(host2)) {
            Assert.assertEquals(UNKNOWN, sch.getState());
        }
    }

    @Test
    public void testServiceUpdateRecursiveBadHostComponent() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.2"));
        String serviceName1 = "HDFS";
        createService(cluster1, serviceName1, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName1, componentName1, INIT);
        createServiceComponent(cluster1, serviceName1, componentName2, INIT);
        createServiceComponent(cluster1, serviceName1, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        Set<ServiceComponentHostRequest> set1 = new HashSet<>();
        ServiceComponentHostRequest r1 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName1, host1, INIT.toString());
        ServiceComponentHostRequest r2 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName2, host1, INIT.toString());
        ServiceComponentHostRequest r3 = new ServiceComponentHostRequest(cluster1, serviceName1, componentName3, host1, INIT.toString());
        set1.add(r1);
        set1.add(r2);
        set1.add(r3);
        AmbariManagementControllerTest.controller.createHostComponents(set1);
        Cluster c1 = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s1 = c1.getService(serviceName1);
        ServiceComponent sc1 = s1.getServiceComponent(componentName1);
        ServiceComponent sc2 = s1.getServiceComponent(componentName2);
        ServiceComponent sc3 = s1.getServiceComponent(componentName3);
        ServiceComponentHost sch1 = sc1.getServiceComponentHost(host1);
        ServiceComponentHost sch2 = sc2.getServiceComponentHost(host1);
        ServiceComponentHost sch3 = sc3.getServiceComponentHost(host1);
        s1.setDesiredState(INSTALLED);
        sc1.setDesiredState(STARTED);
        sc2.setDesiredState(INIT);
        sc3.setDesiredState(INSTALLED);
        sch1.setDesiredState(INSTALLED);
        sch2.setDesiredState(INSTALLED);
        sch3.setDesiredState(INSTALLED);
        sch1.setState(INSTALLED);
        sch2.setState(UNKNOWN);
        sch3.setState(INSTALLED);
        // an UNKOWN failure will throw an exception
        ServiceRequest req = new ServiceRequest(cluster1, serviceName1, repositoryVersion02.getId(), INSTALLED.toString());
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, Collections.singleton(req), Collections.emptyMap(), true, false);
    }

    @Test
    public void testUpdateStacks() throws Exception {
        StackInfo stackInfo = AmbariManagementControllerTest.ambariMetaInfo.getStack(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION);
        for (RepositoryInfo repositoryInfo : stackInfo.getRepositories()) {
            Assert.assertFalse(AmbariManagementControllerTest.INCORRECT_BASE_URL.equals(repositoryInfo.getBaseUrl()));
            repositoryInfo.setBaseUrl(AmbariManagementControllerTest.INCORRECT_BASE_URL);
            Assert.assertTrue(AmbariManagementControllerTest.INCORRECT_BASE_URL.equals(repositoryInfo.getBaseUrl()));
        }
        stackManagerMock.invalidateCurrentPaths();
        AmbariManagementControllerTest.controller.updateStacks();
        stackInfo = AmbariManagementControllerTest.ambariMetaInfo.getStack(AmbariManagementControllerTest.STACK_NAME, AmbariManagementControllerTest.STACK_VERSION);
        for (RepositoryInfo repositoryInfo : stackInfo.getRepositories()) {
            Assert.assertFalse(AmbariManagementControllerTest.INCORRECT_BASE_URL.equals(repositoryInfo.getBaseUrl()));
        }
    }

    @Test
    public void testDeleteHostComponentInVariousStates() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-1.3.1"));
        String hdfs = "HDFS";
        String mapred = "MAPREDUCE";
        createService(cluster1, hdfs, null);
        createService(cluster1, mapred, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        String componentName4 = "JOBTRACKER";
        String componentName5 = "TASKTRACKER";
        String componentName6 = "MAPREDUCE_CLIENT";
        createServiceComponent(cluster1, hdfs, componentName1, INIT);
        createServiceComponent(cluster1, hdfs, componentName2, INIT);
        createServiceComponent(cluster1, hdfs, componentName3, INIT);
        createServiceComponent(cluster1, mapred, componentName4, INIT);
        createServiceComponent(cluster1, mapred, componentName5, INIT);
        createServiceComponent(cluster1, mapred, componentName6, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        createServiceComponentHost(cluster1, hdfs, componentName1, host1, null);
        createServiceComponentHost(cluster1, hdfs, componentName2, host1, null);
        createServiceComponentHost(cluster1, hdfs, componentName3, host1, null);
        createServiceComponentHost(cluster1, mapred, componentName4, host1, null);
        createServiceComponentHost(cluster1, mapred, componentName5, host1, null);
        createServiceComponentHost(cluster1, mapred, componentName6, host1, null);
        // Install
        installService(cluster1, hdfs, false, false);
        installService(cluster1, mapred, false, false);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service s1 = cluster.getService(hdfs);
        Service s2 = cluster.getService(mapred);
        ServiceComponent sc1 = s1.getServiceComponent(componentName1);
        sc1.getServiceComponentHosts().values().iterator().next().setState(STARTED);
        Set<ServiceComponentHostRequest> schRequests = new HashSet<>();
        // delete HC
        schRequests.clear();
        schRequests.add(new ServiceComponentHostRequest(cluster1, hdfs, componentName1, host1, null));
        try {
            AmbariManagementControllerTest.controller.deleteHostComponents(schRequests);
            Assert.fail("Expect failure while deleting.");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Host Component cannot be removed"));
        }
        sc1.getServiceComponentHosts().values().iterator().next().setDesiredState(STARTED);
        sc1.getServiceComponentHosts().values().iterator().next().setState(UNKNOWN);
        ServiceComponent sc2 = s1.getServiceComponent(componentName2);
        sc2.getServiceComponentHosts().values().iterator().next().setState(INIT);
        ServiceComponent sc3 = s1.getServiceComponent(componentName3);
        sc3.getServiceComponentHosts().values().iterator().next().setState(INSTALL_FAILED);
        ServiceComponent sc4 = s2.getServiceComponent(componentName4);
        sc4.getServiceComponentHosts().values().iterator().next().setDesiredState(INSTALLED);
        sc4.getServiceComponentHosts().values().iterator().next().setState(DISABLED);
        ServiceComponent sc5 = s2.getServiceComponent(componentName5);
        sc5.getServiceComponentHosts().values().iterator().next().setState(INSTALLED);
        ServiceComponent sc6 = s2.getServiceComponent(componentName6);
        sc6.getServiceComponentHosts().values().iterator().next().setState(INIT);
        schRequests.clear();
        schRequests.add(new ServiceComponentHostRequest(cluster1, hdfs, componentName1, host1, null));
        schRequests.add(new ServiceComponentHostRequest(cluster1, hdfs, componentName2, host1, null));
        schRequests.add(new ServiceComponentHostRequest(cluster1, hdfs, componentName3, host1, null));
        schRequests.add(new ServiceComponentHostRequest(cluster1, mapred, componentName4, host1, null));
        schRequests.add(new ServiceComponentHostRequest(cluster1, mapred, componentName5, host1, null));
        schRequests.add(new ServiceComponentHostRequest(cluster1, mapred, componentName6, host1, null));
        DeleteStatusMetaData deleteStatusMetaData = AmbariManagementControllerTest.controller.deleteHostComponents(schRequests);
        Assert.assertEquals(0, deleteStatusMetaData.getExceptionForKeys().size());
    }

    @Test
    public void testDeleteHostWithComponent() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();// Host will belong to the cluster and contain components

        addHostToCluster(host1, cluster1);
        // Add components to host1
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Treat host components on host1 as up and healthy
        Map<String, ServiceComponentHost> hostComponents = cluster.getService(serviceName).getServiceComponent(componentName1).getServiceComponentHosts();
        for (Map.Entry<String, ServiceComponentHost> entry : hostComponents.entrySet()) {
            ServiceComponentHost cHost = entry.getValue();
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostInstallEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis(), cluster.getDesiredStackVersion().getStackId()));
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostOpSucceededEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis()));
        }
        hostComponents = cluster.getService(serviceName).getServiceComponent(componentName2).getServiceComponentHosts();
        for (Map.Entry<String, ServiceComponentHost> entry : hostComponents.entrySet()) {
            ServiceComponentHost cHost = entry.getValue();
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostInstallEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis(), cluster.getDesiredStackVersion().getStackId()));
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostOpSucceededEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis()));
        }
        // Case 1: Attempt delete when some components are STARTED
        Set<HostRequest> requests = new HashSet<>();
        requests.clear();
        requests.add(new HostRequest(host1, cluster1));
        Service s = cluster.getService(serviceName);
        s.getServiceComponent("DATANODE").getServiceComponentHost(host1).setState(STARTED);
        try {
            HostResourceProviderTest.deleteHosts(AmbariManagementControllerTest.controller, requests, false);
            Assert.fail("Expect failure deleting hosts when components exist and have not been stopped.");
        } catch (Exception e) {
            AmbariManagementControllerTest.LOG.info(("Exception is - " + (e.getMessage())));
            Assert.assertTrue(e.getMessage().contains("these components are not in the removable state:"));
        }
        // Case 2: Attempt delete dryRun = true
        DeleteStatusMetaData data = null;
        AmbariManagementControllerTest.LOG.info("Test dry run of delete with all host components");
        s.getServiceComponent("DATANODE").getServiceComponentHost(host1).setState(INSTALLED);
        try {
            data = HostResourceProviderTest.deleteHosts(AmbariManagementControllerTest.controller, requests, true);
            Assert.assertTrue(((data.getDeletedKeys().size()) == 1));
        } catch (Exception e) {
            AmbariManagementControllerTest.LOG.info(("Exception is - " + (e.getMessage())));
            Assert.fail("Do not expect failure deleting hosts when components exist and are stopped.");
        }
        // Case 3: Attempt delete dryRun = false
        AmbariManagementControllerTest.LOG.info("Test successful delete with all host components");
        s.getServiceComponent("DATANODE").getServiceComponentHost(host1).setState(INSTALLED);
        try {
            data = HostResourceProviderTest.deleteHosts(AmbariManagementControllerTest.controller, requests, false);
            Assert.assertNotNull(data);
            Assert.assertTrue((4 == (data.getDeletedKeys().size())));
            Assert.assertTrue((0 == (data.getExceptionForKeys().size())));
        } catch (Exception e) {
            AmbariManagementControllerTest.LOG.info(("Exception is - " + (e.getMessage())));
            Assert.fail("Do not expect failure deleting hosts when components exist and are stopped.");
        }
        // Verify host does not exist
        try {
            AmbariManagementControllerTest.clusters.getHost(host1);
            Assert.fail("Expected a HostNotFoundException.");
        } catch (HostNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testDeleteHost() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();// Host will belong to the cluster and contain components

        String host2 = AmbariManagementControllerTest.getUniqueName();// Host will belong to the cluster and not contain any components

        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        String host3 = AmbariManagementControllerTest.getUniqueName();// Host is not registered

        // Add components to host1
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Treat host components on host1 as up and healthy
        Map<String, ServiceComponentHost> hostComponents = cluster.getService(serviceName).getServiceComponent(componentName1).getServiceComponentHosts();
        for (Map.Entry<String, ServiceComponentHost> entry : hostComponents.entrySet()) {
            ServiceComponentHost cHost = entry.getValue();
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostInstallEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis(), cluster.getDesiredStackVersion().getStackId()));
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostOpSucceededEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis()));
        }
        hostComponents = cluster.getService(serviceName).getServiceComponent(componentName2).getServiceComponentHosts();
        for (Map.Entry<String, ServiceComponentHost> entry : hostComponents.entrySet()) {
            ServiceComponentHost cHost = entry.getValue();
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostInstallEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis(), cluster.getDesiredStackVersion().getStackId()));
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostOpSucceededEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis()));
        }
        Set<HostRequest> requests = new HashSet<>();
        requests.clear();
        requests.add(new HostRequest(host1, cluster1));
        // Case 1: Delete host that is still part of cluster, but do not specify the cluster_name in the request
        Set<ServiceComponentHostRequest> schRequests = new HashSet<>();
        // Disable HC for non-clients
        schRequests.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, "DISABLED"));
        schRequests.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName2, host1, "DISABLED"));
        updateHostComponents(schRequests, new HashMap(), false);
        // Delete HC
        schRequests.clear();
        schRequests.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, null));
        schRequests.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName2, host1, null));
        schRequests.add(new ServiceComponentHostRequest(cluster1, serviceName, componentName3, host1, null));
        AmbariManagementControllerTest.controller.deleteHostComponents(schRequests);
        Assert.assertEquals(0, cluster.getServiceComponentHosts(host1).size());
        Assert.assertNull(topologyHostInfoDAO.findByHostname(host1));
        Long firstHostId = AmbariManagementControllerTest.clusters.getHost(host1).getHostId();
        // Deletion without specifying cluster should be successful
        requests.clear();
        requests.add(new HostRequest(host1, null));
        try {
            HostResourceProviderTest.deleteHosts(AmbariManagementControllerTest.controller, requests);
        } catch (Exception e) {
            Assert.fail(("Did not expect an error deleting the host from the cluster. Error: " + (e.getMessage())));
        }
        // Verify host is no longer part of the cluster
        Assert.assertFalse(AmbariManagementControllerTest.clusters.getHostsForCluster(cluster1).containsKey(host1));
        Assert.assertFalse(AmbariManagementControllerTest.clusters.getClustersForHost(host1).contains(cluster));
        Assert.assertNull(topologyHostInfoDAO.findByHostname(host1));
        // verify there are no host role commands for the host
        List<HostRoleCommandEntity> tasks = hostRoleCommandDAO.findByHostId(firstHostId);
        Assert.assertEquals(0, tasks.size());
        // Case 2: Delete host that is still part of the cluster, and specify the cluster_name in the request
        requests.clear();
        requests.add(new HostRequest(host2, cluster1));
        try {
            HostResourceProviderTest.deleteHosts(AmbariManagementControllerTest.controller, requests);
        } catch (Exception e) {
            Assert.fail(("Did not expect an error deleting the host from the cluster. Error: " + (e.getMessage())));
        }
        // Verify host is no longer part of the cluster
        Assert.assertFalse(AmbariManagementControllerTest.clusters.getHostsForCluster(cluster1).containsKey(host2));
        Assert.assertFalse(AmbariManagementControllerTest.clusters.getClustersForHost(host2).contains(cluster));
        Assert.assertNull(topologyHostInfoDAO.findByHostname(host2));
        // Case 3: Attempt to delete a host that has already been deleted
        requests.clear();
        requests.add(new HostRequest(host1, null));
        try {
            HostResourceProviderTest.deleteHosts(AmbariManagementControllerTest.controller, requests);
            Assert.fail("Expected a HostNotFoundException trying to remove a host that was already deleted.");
        } catch (HostNotFoundException e) {
            // expected
        }
        // Verify host does not exist
        try {
            AmbariManagementControllerTest.clusters.getHost(host1);
            Assert.fail("Expected a HostNotFoundException.");
        } catch (HostNotFoundException e) {
            // expected
        }
        // Case 4: Attempt to delete a host that was never added to the cluster
        requests.clear();
        requests.add(new HostRequest(host3, null));
        try {
            HostResourceProviderTest.deleteHosts(AmbariManagementControllerTest.controller, requests);
            Assert.fail("Expected a HostNotFoundException trying to remove a host that was never added.");
        } catch (HostNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testGetRootServices() throws Exception {
        RootServiceRequest request = new RootServiceRequest(null);
        Set<RootServiceResponse> responses = AmbariManagementControllerTest.controller.getRootServices(Collections.singleton(request));
        Assert.assertEquals(RootService.values().length, responses.size());
        RootServiceRequest requestWithParams = new RootServiceRequest(AMBARI.toString());
        Set<RootServiceResponse> responsesWithParams = AmbariManagementControllerTest.controller.getRootServices(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (RootServiceResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getServiceName(), AMBARI.toString());
        }
        RootServiceRequest invalidRequest = new RootServiceRequest(AmbariManagementControllerTest.NON_EXT_VALUE);
        try {
            AmbariManagementControllerTest.controller.getRootServices(Collections.singleton(invalidRequest));
        } catch (ObjectNotFoundException e) {
            // do nothing
        }
    }

    @Test
    public void testGetRootServiceComponents() throws Exception {
        RootServiceComponentRequest request = new RootServiceComponentRequest(AMBARI.toString(), null);
        Set<RootServiceComponentResponse> responses = AmbariManagementControllerTest.controller.getRootServiceComponents(Collections.singleton(request));
        Assert.assertEquals(AMBARI.getComponents().length, responses.size());
        RootServiceComponentRequest requestWithParams = new RootServiceComponentRequest(AMBARI.toString(), AMBARI.getComponents()[0].toString());
        Set<RootServiceComponentResponse> responsesWithParams = AmbariManagementControllerTest.controller.getRootServiceComponents(Collections.singleton(requestWithParams));
        Assert.assertEquals(1, responsesWithParams.size());
        for (RootServiceComponentResponse responseWithParams : responsesWithParams) {
            Assert.assertEquals(responseWithParams.getComponentName(), AMBARI.getComponents()[0].toString());
        }
        RootServiceComponentRequest invalidRequest = new RootServiceComponentRequest(AmbariManagementControllerTest.NON_EXT_VALUE, AmbariManagementControllerTest.NON_EXT_VALUE);
        try {
            AmbariManagementControllerTest.controller.getRootServiceComponents(Collections.singleton(invalidRequest));
        } catch (ObjectNotFoundException e) {
            // do nothing
        }
    }

    @Test
    public void testDeleteComponentsOnHost() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        createServiceComponentHost(cluster1, null, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // make them believe they are up
        Map<String, ServiceComponentHost> hostComponents = cluster.getService(serviceName).getServiceComponent(componentName1).getServiceComponentHosts();
        for (Map.Entry<String, ServiceComponentHost> entry : hostComponents.entrySet()) {
            ServiceComponentHost cHost = entry.getValue();
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostInstallEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis(), cluster.getDesiredStackVersion().getStackId()));
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostOpSucceededEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis()));
        }
        hostComponents = cluster.getService(serviceName).getServiceComponent(componentName2).getServiceComponentHosts();
        for (Map.Entry<String, ServiceComponentHost> entry : hostComponents.entrySet()) {
            ServiceComponentHost cHost = entry.getValue();
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostInstallEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis(), cluster.getDesiredStackVersion().getStackId()));
            cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostOpSucceededEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis()));
        }
        ServiceComponentHost sch = cluster.getService(serviceName).getServiceComponent(componentName2).getServiceComponentHost(host1);
        Assert.assertNotNull(sch);
        sch.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(sch.getServiceComponentName(), sch.getHostName(), System.currentTimeMillis()));
        sch.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartedEvent(sch.getServiceComponentName(), sch.getHostName(), System.currentTimeMillis()));
        Set<ServiceComponentHostRequest> schRequests = new HashSet<>();
        schRequests.add(new ServiceComponentHostRequest(cluster1, null, null, host1, null));
        DeleteStatusMetaData deleteStatusMetaData = AmbariManagementControllerTest.controller.deleteHostComponents(schRequests);
        Assert.assertEquals(1, deleteStatusMetaData.getExceptionForKeys().size());
        Assert.assertEquals(1, cluster.getServiceComponentHosts(host1).size());
        sch.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStopEvent(sch.getServiceComponentName(), sch.getHostName(), System.currentTimeMillis()));
        sch.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStoppedEvent(sch.getServiceComponentName(), sch.getHostName(), System.currentTimeMillis()));
        AmbariManagementControllerTest.controller.deleteHostComponents(schRequests);
        Assert.assertEquals(0, cluster.getServiceComponentHosts(host1).size());
    }

    @Test
    public void testExecutionCommandConfiguration() throws Exception {
        Map<String, Map<String, String>> config = new HashMap<>();
        config.put("type1", new HashMap<>());
        config.put("type3", new HashMap<>());
        config.get("type3").put("name1", "neverchange");
        configHelper.applyCustomConfig(config, "type1", "name1", "value11", false);
        Assert.assertEquals("value11", config.get("type1").get("name1"));
        config.put("type1", new HashMap<>());
        configHelper.applyCustomConfig(config, "type1", "name1", "value12", false);
        Assert.assertEquals("value12", config.get("type1").get("name1"));
        configHelper.applyCustomConfig(config, "type2", "name2", "value21", false);
        Assert.assertEquals("value21", config.get("type2").get("name2"));
        configHelper.applyCustomConfig(config, "type2", "name2", "", true);
        Assert.assertEquals("", config.get("type2").get("DELETED_name2"));
        Assert.assertEquals("neverchange", config.get("type3").get("name1"));
        Map<String, String> persistedClusterConfig = new HashMap<>();
        persistedClusterConfig.put("name1", "value11");
        persistedClusterConfig.put("name3", "value31");
        persistedClusterConfig.put("name4", "value41");
        Map<String, String> override = new HashMap<>();
        override.put("name1", "value12");
        override.put("name2", "value21");
        override.put("DELETED_name3", "value31");
        Map<String, String> mergedConfig = configHelper.getMergedConfig(persistedClusterConfig, override);
        Assert.assertEquals(3, mergedConfig.size());
        Assert.assertFalse(mergedConfig.containsKey("name3"));
        Assert.assertEquals("value12", mergedConfig.get("name1"));
        Assert.assertEquals("value21", mergedConfig.get("name2"));
        Assert.assertEquals("value41", mergedConfig.get("name4"));
    }

    @Test
    public void testApplyConfigurationWithTheSameTag() throws AuthorizationException {
        final String cluster1 = AmbariManagementControllerTest.getUniqueName();
        String tag = "version1";
        String type = "core-site";
        Exception exception = null;
        try {
            AmbariManagementController amc = AmbariManagementControllerTest.injector.getInstance(AmbariManagementController.class);
            Clusters clusters = AmbariManagementControllerTest.injector.getInstance(Clusters.class);
            Gson gson = new Gson();
            clusters.addHost("host1");
            clusters.addHost("host2");
            clusters.addHost("host3");
            Host host = clusters.getHost("host1");
            setOsFamily(host, "redhat", "6.3");
            host = clusters.getHost("host2");
            setOsFamily(host, "redhat", "6.3");
            host = clusters.getHost("host3");
            setOsFamily(host, "redhat", "6.3");
            ClusterRequest clusterRequest = new ClusterRequest(null, cluster1, "HDP-1.2.0", null);
            amc.createCluster(clusterRequest);
            Set<ServiceRequest> serviceRequests = new HashSet<>();
            serviceRequests.add(new ServiceRequest(cluster1, "HDFS", repositoryVersion120.getId(), null));
            ServiceResourceProviderTest.createServices(amc, repositoryVersionDAO, serviceRequests);
            Type confType = new TypeToken<Map<String, String>>() {}.getType();
            ConfigurationRequest configurationRequest = new ConfigurationRequest(cluster1, type, tag, gson.fromJson("{ \"fs.default.name\" : \"localhost:8020\"}", confType), null);
            amc.createConfiguration(configurationRequest);
            amc.createConfiguration(configurationRequest);
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        String exceptionMessage = MessageFormat.format("Configuration with tag ''{0}'' exists for ''{1}''", tag, type);
        Assert.assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    public void testDeleteClusterCreateHost() throws Exception {
        String STACK_ID = "HDP-2.0.1";
        String CLUSTER_NAME = AmbariManagementControllerTest.getUniqueName();
        String HOST1 = AmbariManagementControllerTest.getUniqueName();
        String HOST2 = AmbariManagementControllerTest.getUniqueName();
        Clusters clusters = AmbariManagementControllerTest.injector.getInstance(Clusters.class);
        clusters.addHost(HOST1);
        Host host = clusters.getHost(HOST1);
        setOsFamily(host, "redhat", "6.3");
        clusters.getHost(HOST1).setState(HEALTHY);
        clusters.updateHostMappings(host);
        clusters.addHost(HOST2);
        host = clusters.getHost(HOST2);
        clusters.updateHostMappings(host);
        setOsFamily(host, "redhat", "6.3");
        AmbariManagementController amc = AmbariManagementControllerTest.injector.getInstance(AmbariManagementController.class);
        ClusterRequest cr = new ClusterRequest(null, CLUSTER_NAME, STACK_ID, null);
        amc.createCluster(cr);
        long clusterId = clusters.getCluster(CLUSTER_NAME).getClusterId();
        ConfigurationRequest configRequest = new ConfigurationRequest(CLUSTER_NAME, "global", "version1", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, null);
        ClusterRequest ur = new ClusterRequest(clusterId, CLUSTER_NAME, STACK_ID, null);
        ur.setDesiredConfig(Collections.singletonList(configRequest));
        amc.updateClusters(Collections.singleton(ur), new HashMap());
        // add some hosts
        Set<HostRequest> hrs = new HashSet<>();
        hrs.add(new HostRequest(HOST1, CLUSTER_NAME));
        HostResourceProviderTest.createHosts(amc, hrs);
        Set<ServiceRequest> serviceRequests = new HashSet<>();
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "HDFS", repositoryVersion201.getId(), null));
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "MAPREDUCE2", repositoryVersion201.getId(), null));
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "YARN", repositoryVersion201.getId(), null));
        ServiceResourceProviderTest.createServices(amc, repositoryVersionDAO, serviceRequests);
        Set<ServiceComponentRequest> serviceComponentRequests = new HashSet<>();
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "HDFS", "NAMENODE", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "HDFS", "SECONDARY_NAMENODE", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "HDFS", "DATANODE", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "MAPREDUCE2", "HISTORYSERVER", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "YARN", "RESOURCEMANAGER", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "YARN", "NODEMANAGER", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "HDFS", "HDFS_CLIENT", null));
        ComponentResourceProviderTest.createComponents(amc, serviceComponentRequests);
        Set<ServiceComponentHostRequest> componentHostRequests = new HashSet<>();
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, "HDFS", "DATANODE", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, "HDFS", "NAMENODE", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, "HDFS", "SECONDARY_NAMENODE", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, "MAPREDUCE2", "HISTORYSERVER", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, "YARN", "RESOURCEMANAGER", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, "YARN", "NODEMANAGER", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, "HDFS", "HDFS_CLIENT", HOST1, null));
        amc.createHostComponents(componentHostRequests);
        RequestResourceFilter resourceFilter = new RequestResourceFilter("HDFS", null, null);
        ExecuteActionRequest ar = new ExecuteActionRequest(CLUSTER_NAME, HDFS_SERVICE_CHECK.name(), null, false);
        ar.getResourceFilters().add(resourceFilter);
        amc.createAction(ar, null);
        // change mind, delete the cluster
        amc.deleteCluster(cr);
        Assert.assertNotNull(clusters.getHost(HOST1));
        Assert.assertNotNull(clusters.getHost(HOST2));
        HostDAO dao = AmbariManagementControllerTest.injector.getInstance(HostDAO.class);
        Assert.assertNotNull(dao.findByName(HOST1));
        Assert.assertNotNull(dao.findByName(HOST2));
    }

    @Test
    public void testScheduleSmokeTest() throws Exception {
        final String HOST1 = AmbariManagementControllerTest.getUniqueName();
        final String OS_TYPE = "centos5";
        final String STACK_ID = "HDP-2.0.1";
        final String CLUSTER_NAME = AmbariManagementControllerTest.getUniqueName();
        final String HDFS_SERVICE_CHECK_ROLE = "HDFS_SERVICE_CHECK";
        final String MAPREDUCE2_SERVICE_CHECK_ROLE = "MAPREDUCE2_SERVICE_CHECK";
        final String YARN_SERVICE_CHECK_ROLE = "YARN_SERVICE_CHECK";
        Map<String, String> mapRequestProps = Collections.emptyMap();
        AmbariManagementController amc = AmbariManagementControllerTest.injector.getInstance(AmbariManagementController.class);
        Clusters clusters = AmbariManagementControllerTest.injector.getInstance(Clusters.class);
        clusters.addHost(HOST1);
        Host host = clusters.getHost(HOST1);
        setOsFamily(host, "redhat", "5.9");
        clusters.getHost(HOST1).setState(HEALTHY);
        clusters.updateHostMappings(host);
        ClusterRequest clusterRequest = new ClusterRequest(null, CLUSTER_NAME, STACK_ID, null);
        amc.createCluster(clusterRequest);
        Set<ServiceRequest> serviceRequests = new HashSet<>();
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "HDFS", repositoryVersion201.getId(), null));
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "MAPREDUCE2", repositoryVersion201.getId(), null));
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "YARN", repositoryVersion201.getId(), null));
        ServiceResourceProviderTest.createServices(amc, repositoryVersionDAO, serviceRequests);
        Set<ServiceComponentRequest> serviceComponentRequests = new HashSet<>();
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "HDFS", "NAMENODE", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "HDFS", "SECONDARY_NAMENODE", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "HDFS", "DATANODE", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "MAPREDUCE2", "HISTORYSERVER", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "YARN", "RESOURCEMANAGER", null));
        serviceComponentRequests.add(new ServiceComponentRequest(CLUSTER_NAME, "YARN", "NODEMANAGER", null));
        ComponentResourceProviderTest.createComponents(amc, serviceComponentRequests);
        Set<HostRequest> hostRequests = new HashSet<>();
        hostRequests.add(new HostRequest(HOST1, CLUSTER_NAME));
        HostResourceProviderTest.createHosts(amc, hostRequests);
        for (Host clusterHost : clusters.getHosts()) {
            clusters.updateHostMappings(clusterHost);
        }
        Set<ServiceComponentHostRequest> componentHostRequests = new HashSet<>();
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, null, "DATANODE", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, null, "NAMENODE", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, null, "SECONDARY_NAMENODE", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, null, "HISTORYSERVER", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, null, "RESOURCEMANAGER", HOST1, null));
        componentHostRequests.add(new ServiceComponentHostRequest(CLUSTER_NAME, null, "NODEMANAGER", HOST1, null));
        amc.createHostComponents(componentHostRequests);
        // Install services
        serviceRequests.clear();
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "HDFS", repositoryVersion201.getId(), INSTALLED.name()));
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "MAPREDUCE2", repositoryVersion201.getId(), INSTALLED.name()));
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "YARN", repositoryVersion201.getId(), INSTALLED.name()));
        ServiceResourceProviderTest.updateServices(amc, serviceRequests, mapRequestProps, true, false);
        Cluster cluster = clusters.getCluster(CLUSTER_NAME);
        for (String serviceName : cluster.getServices().keySet()) {
            for (String componentName : cluster.getService(serviceName).getServiceComponents().keySet()) {
                Map<String, ServiceComponentHost> serviceComponentHosts = cluster.getService(serviceName).getServiceComponent(componentName).getServiceComponentHosts();
                for (Map.Entry<String, ServiceComponentHost> entry : serviceComponentHosts.entrySet()) {
                    ServiceComponentHost cHost = entry.getValue();
                    cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostInstallEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis(), STACK_ID));
                    cHost.handleEvent(new org.apache.ambari.server.state.svccomphost.ServiceComponentHostOpSucceededEvent(cHost.getServiceComponentName(), cHost.getHostName(), System.currentTimeMillis()));
                }
            }
        }
        // Start services
        serviceRequests.clear();
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "HDFS", repositoryVersion201.getId(), STARTED.name()));
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "MAPREDUCE2", repositoryVersion201.getId(), STARTED.name()));
        serviceRequests.add(new ServiceRequest(CLUSTER_NAME, "YARN", repositoryVersion201.getId(), STARTED.name()));
        RequestStatusResponse response = ServiceResourceProviderTest.updateServices(amc, serviceRequests, mapRequestProps, true, false);
        Collection<?> hdfsSmokeTasks = CollectionUtils.select(response.getTasks(), new AmbariManagementControllerTest.RolePredicate(HDFS_SERVICE_CHECK_ROLE));
        // Ensure that smoke test task was created for HDFS
        Assert.assertEquals(1, hdfsSmokeTasks.size());
        Collection<?> mapreduce2SmokeTasks = CollectionUtils.select(response.getTasks(), new AmbariManagementControllerTest.RolePredicate(MAPREDUCE2_SERVICE_CHECK_ROLE));
        // Ensure that smoke test task was created for MAPREDUCE2
        Assert.assertEquals(1, mapreduce2SmokeTasks.size());
        Collection<?> yarnSmokeTasks = CollectionUtils.select(response.getTasks(), new AmbariManagementControllerTest.RolePredicate(YARN_SERVICE_CHECK_ROLE));
        // Ensure that smoke test task was created for YARN
        Assert.assertEquals(1, yarnSmokeTasks.size());
    }

    @Test
    public void testGetServices2() throws Exception {
        // member state mocks
        Injector injector = createStrictMock(Injector.class);
        Capture<AmbariManagementController> controllerCapture = EasyMock.newCapture();
        Clusters clusters = createNiceMock(Clusters.class);
        MaintenanceStateHelper maintHelper = createNiceMock(MaintenanceStateHelper.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service = createNiceMock(Service.class);
        ServiceResponse response = createNiceMock(ServiceResponse.class);
        // requests
        ServiceRequest request1 = new ServiceRequest("cluster1", "service1", null, null, null);
        Set<ServiceRequest> setRequests = new HashSet<>();
        setRequests.add(request1);
        // expectations
        // constructor init
        AmbariManagementControllerImplTest.constructorInit(injector, controllerCapture, null, maintHelper, createStrictMock(KerberosHelper.class), null, null);
        // getServices
        expect(clusters.getCluster("cluster1")).andReturn(cluster);
        expect(cluster.getService("service1")).andReturn(service);
        expect(service.convertToResponse()).andReturn(response);
        // replay mocks
        replay(maintHelper, injector, clusters, cluster, service, response);
        // test
        AmbariManagementController controller = new AmbariManagementControllerImpl(null, clusters, injector);
        Set<ServiceResponse> setResponses = ServiceResourceProviderTest.getServices(controller, setRequests);
        // assert and verify
        Assert.assertSame(controller, controllerCapture.getValue());
        Assert.assertEquals(1, setResponses.size());
        Assert.assertTrue(setResponses.contains(response));
        verify(injector, clusters, cluster, service, response);
    }

    /**
     * Ensure that ServiceNotFoundException is propagated in case where there is a single request.
     */
    @Test
    public void testGetServices___ServiceNotFoundException() throws Exception {
        // member state mocks
        Injector injector = createStrictMock(Injector.class);
        Capture<AmbariManagementController> controllerCapture = EasyMock.newCapture();
        Clusters clusters = createNiceMock(Clusters.class);
        MaintenanceStateHelper maintHelper = createNiceMock(MaintenanceStateHelper.class);
        Cluster cluster = createNiceMock(Cluster.class);
        // requests
        ServiceRequest request1 = new ServiceRequest("cluster1", "service1", null, null, null);
        Set<ServiceRequest> setRequests = new HashSet<>();
        setRequests.add(request1);
        // expectations
        // constructor init
        AmbariManagementControllerImplTest.constructorInit(injector, controllerCapture, null, maintHelper, createStrictMock(KerberosHelper.class), null, null);
        // getServices
        expect(clusters.getCluster("cluster1")).andReturn(cluster);
        expect(cluster.getService("service1")).andThrow(new ServiceNotFoundException("custer1", "service1"));
        // replay mocks
        replay(maintHelper, injector, clusters, cluster);
        // test
        AmbariManagementController controller = new AmbariManagementControllerImpl(null, clusters, injector);
        // assert that exception is thrown in case where there is a single request
        try {
            ServiceResourceProviderTest.getServices(controller, setRequests);
            Assert.fail("expected ServiceNotFoundException");
        } catch (ServiceNotFoundException e) {
            // expected
        }
        Assert.assertSame(controller, controllerCapture.getValue());
        verify(injector, clusters, cluster);
    }

    /**
     * Ensure that ServiceNotFoundException is handled where there are multiple requests as would be the
     * case when an OR predicate is provided in the query.
     */
    @Test
    public void testGetServices___OR_Predicate_ServiceNotFoundException() throws Exception {
        // member state mocks
        Injector injector = createStrictMock(Injector.class);
        Capture<AmbariManagementController> controllerCapture = EasyMock.newCapture();
        Clusters clusters = createNiceMock(Clusters.class);
        MaintenanceStateHelper maintHelper = createNiceMock(MaintenanceStateHelper.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service1 = createNiceMock(Service.class);
        Service service2 = createNiceMock(Service.class);
        ServiceResponse response = createNiceMock(ServiceResponse.class);
        ServiceResponse response2 = createNiceMock(ServiceResponse.class);
        // requests
        ServiceRequest request1 = new ServiceRequest("cluster1", "service1", null, null, null);
        ServiceRequest request2 = new ServiceRequest("cluster1", "service2", null, null, null);
        ServiceRequest request3 = new ServiceRequest("cluster1", "service3", null, null, null);
        ServiceRequest request4 = new ServiceRequest("cluster1", "service4", null, null, null);
        Set<ServiceRequest> setRequests = new HashSet<>();
        setRequests.add(request1);
        setRequests.add(request2);
        setRequests.add(request3);
        setRequests.add(request4);
        // expectations
        // constructor init
        AmbariManagementControllerImplTest.constructorInit(injector, controllerCapture, null, maintHelper, createStrictMock(KerberosHelper.class), null, null);
        // getServices
        expect(clusters.getCluster("cluster1")).andReturn(cluster).times(4);
        expect(cluster.getService("service1")).andReturn(service1);
        expect(cluster.getService("service2")).andThrow(new ServiceNotFoundException("cluster1", "service2"));
        expect(cluster.getService("service3")).andThrow(new ServiceNotFoundException("cluster1", "service3"));
        expect(cluster.getService("service4")).andReturn(service2);
        expect(service1.convertToResponse()).andReturn(response);
        expect(service2.convertToResponse()).andReturn(response2);
        // replay mocks
        replay(maintHelper, injector, clusters, cluster, service1, service2, response, response2);
        // test
        AmbariManagementController controller = new AmbariManagementControllerImpl(null, clusters, injector);
        Set<ServiceResponse> setResponses = ServiceResourceProviderTest.getServices(controller, setRequests);
        // assert and verify
        Assert.assertSame(controller, controllerCapture.getValue());
        Assert.assertEquals(2, setResponses.size());
        Assert.assertTrue(setResponses.contains(response));
        Assert.assertTrue(setResponses.contains(response2));
        verify(injector, clusters, cluster, service1, service2, response, response2);
    }

    private class RolePredicate implements org.apache.commons.collections.Predicate {
        private String role;

        public RolePredicate(String role) {
            this.role = role;
        }

        @Override
        public boolean evaluate(Object obj) {
            ShortTaskStatus task = ((ShortTaskStatus) (obj));
            return task.getRole().equals(role);
        }
    }

    @Test
    public void testReinstallClientSchSkippedInMaintenance() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        final String host3 = AmbariManagementControllerTest.getUniqueName();
        Cluster c1 = setupClusterWithHosts(cluster1, "HDP-1.2.0", new ArrayList<String>() {
            {
                add(host1);
                add(host2);
                add(host3);
            }
        }, "centos5");
        RepositoryVersionEntity repositoryVersion = repositoryVersion120;
        Service hdfs = c1.addService("HDFS", repositoryVersion);
        createServiceComponent(cluster1, "HDFS", "NAMENODE", INIT);
        createServiceComponent(cluster1, "HDFS", "DATANODE", INIT);
        createServiceComponent(cluster1, "HDFS", "HDFS_CLIENT", INIT);
        createServiceComponentHost(cluster1, "HDFS", "NAMENODE", host1, INIT);
        createServiceComponentHost(cluster1, "HDFS", "DATANODE", host1, INIT);
        createServiceComponentHost(cluster1, "HDFS", "HDFS_CLIENT", host1, INIT);
        createServiceComponentHost(cluster1, "HDFS", "HDFS_CLIENT", host2, INIT);
        createServiceComponentHost(cluster1, "HDFS", "HDFS_CLIENT", host3, INIT);
        installService(cluster1, "HDFS", false, false);
        AmbariManagementControllerTest.clusters.getHost(host3).setMaintenanceState(c1.getClusterId(), ON);
        Long id = startService(cluster1, "HDFS", false, true);
        Assert.assertNotNull(id);
        List<Stage> stages = actionDB.getAllStages(id);
        Assert.assertNotNull(stages);
        HostRoleCommand hrc1 = null;
        HostRoleCommand hrc2 = null;
        HostRoleCommand hrc3 = null;
        for (Stage s : stages) {
            for (HostRoleCommand hrc : s.getOrderedHostRoleCommands()) {
                if ((hrc.getRole().equals(HDFS_CLIENT)) && (hrc.getHostName().equals(host1))) {
                    hrc1 = hrc;
                } else
                    if ((hrc.getRole().equals(HDFS_CLIENT)) && (hrc.getHostName().equals(host2))) {
                        hrc2 = hrc;
                    } else
                        if ((hrc.getRole().equals(HDFS_CLIENT)) && (hrc.getHostName().equals(host3))) {
                            hrc3 = hrc;
                        }


            }
        }
        Assert.assertNotNull(hrc1);
        Assert.assertNotNull(hrc2);
        Assert.assertNull(hrc3);
    }

    @Test
    public void setMonitoringServicesRestartRequired() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        StackId stackId = new StackId("HDP-2.0.8");
        cluster.setDesiredStackVersion(stackId);
        cluster.setCurrentStackVersion(stackId);
        String hdfsService = "HDFS";
        String fakeMonitoringService = "FAKENAGIOS";
        createService(cluster1, hdfsService, repositoryVersion208, null);
        createService(cluster1, fakeMonitoringService, repositoryVersion208, null);
        String namenode = "NAMENODE";
        String datanode = "DATANODE";
        String hdfsClient = "HDFS_CLIENT";
        String fakeServer = "FAKE_MONITORING_SERVER";
        createServiceComponent(cluster1, hdfsService, namenode, INIT);
        createServiceComponent(cluster1, hdfsService, datanode, INIT);
        createServiceComponent(cluster1, fakeMonitoringService, fakeServer, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        createServiceComponentHost(cluster1, hdfsService, namenode, host1, null);
        createServiceComponentHost(cluster1, hdfsService, datanode, host1, null);
        createServiceComponentHost(cluster1, fakeMonitoringService, fakeServer, host1, null);
        ServiceComponentHost monitoringServiceComponentHost = null;
        for (ServiceComponentHost sch : cluster.getServiceComponentHosts(host1)) {
            if (sch.getServiceComponentName().equals(fakeServer)) {
                monitoringServiceComponentHost = sch;
            }
        }
        Assert.assertFalse(monitoringServiceComponentHost.isRestartRequired());
        createServiceComponent(cluster1, hdfsService, hdfsClient, INIT);
        createServiceComponentHost(cluster1, hdfsService, hdfsClient, host1, null);
        Assert.assertTrue(monitoringServiceComponentHost.isRestartRequired());
    }

    @Test
    public void setRestartRequiredAfterChangeService() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        StackId stackId = new StackId("HDP-2.0.7");
        cluster.setDesiredStackVersion(stackId);
        cluster.setCurrentStackVersion(stackId);
        String hdfsService = "HDFS";
        String zookeeperService = "ZOOKEEPER";
        createService(cluster1, hdfsService, repositoryVersion207, null);
        createService(cluster1, zookeeperService, repositoryVersion207, null);
        String namenode = "NAMENODE";
        String datanode = "DATANODE";
        String hdfsClient = "HDFS_CLIENT";
        String zookeeperServer = "ZOOKEEPER_SERVER";
        String zookeeperClient = "ZOOKEEPER_CLIENT";
        createServiceComponent(cluster1, hdfsService, namenode, INIT);
        createServiceComponent(cluster1, hdfsService, datanode, INIT);
        createServiceComponent(cluster1, zookeeperService, zookeeperServer, INIT);
        createServiceComponent(cluster1, zookeeperService, zookeeperClient, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        createServiceComponentHost(cluster1, hdfsService, namenode, host1, null);
        createServiceComponentHost(cluster1, hdfsService, datanode, host1, null);
        createServiceComponentHost(cluster1, zookeeperService, zookeeperServer, host1, null);
        createServiceComponentHost(cluster1, zookeeperService, zookeeperClient, host1, null);
        ServiceComponentHost zookeeperSch = null;
        for (ServiceComponentHost sch : cluster.getServiceComponentHosts(host1)) {
            if (sch.getServiceComponentName().equals(zookeeperServer)) {
                zookeeperSch = sch;
            }
        }
        Assert.assertFalse(zookeeperSch.isRestartRequired());
        addHostToCluster(host2, cluster1);
        createServiceComponentHost(cluster1, zookeeperService, zookeeperClient, host2, null);
        Assert.assertFalse(zookeeperSch.isRestartRequired());// No restart required if adding host

        createServiceComponentHost(cluster1, zookeeperService, zookeeperServer, host2, null);
        Assert.assertTrue(zookeeperSch.isRestartRequired());// Add zk server required restart

        deleteServiceComponentHost(cluster1, zookeeperService, zookeeperServer, host2, null);
        deleteServiceComponentHost(cluster1, zookeeperService, zookeeperClient, host2, null);
        deleteHost(host2);
        Assert.assertTrue(zookeeperSch.isRestartRequired());// Restart if removing host!

    }

    @Test
    public void testRestartIndicatorsAndSlaveFilesUpdateAtComponentsDelete() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        StackId stackId = new StackId("HDP-2.0.7");
        cluster.setDesiredStackVersion(stackId);
        cluster.setCurrentStackVersion(stackId);
        String hdfsService = "HDFS";
        String zookeeperService = "ZOOKEEPER";
        createService(cluster1, hdfsService, null);
        createService(cluster1, zookeeperService, null);
        String namenode = "NAMENODE";
        String datanode = "DATANODE";
        String zookeeperServer = "ZOOKEEPER_SERVER";
        String zookeeperClient = "ZOOKEEPER_CLIENT";
        createServiceComponent(cluster1, hdfsService, namenode, INIT);
        createServiceComponent(cluster1, hdfsService, datanode, INIT);
        createServiceComponent(cluster1, zookeeperService, zookeeperServer, INIT);
        createServiceComponent(cluster1, zookeeperService, zookeeperClient, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        createServiceComponentHost(cluster1, hdfsService, namenode, host1, null);
        createServiceComponentHost(cluster1, hdfsService, datanode, host1, null);
        createServiceComponentHost(cluster1, zookeeperService, zookeeperServer, host1, null);
        createServiceComponentHost(cluster1, zookeeperService, zookeeperClient, host1, null);
        ServiceComponentHost nameNodeSch = null;
        for (ServiceComponentHost sch : cluster.getServiceComponentHosts(host1)) {
            if (sch.getServiceComponentName().equals(namenode)) {
                nameNodeSch = sch;
            }
        }
        Assert.assertFalse(nameNodeSch.isRestartRequired());
        addHostToCluster(host2, cluster1);
        createServiceComponentHost(cluster1, hdfsService, datanode, host2, null);
        Assert.assertFalse(nameNodeSch.isRestartRequired());// No restart required if adding host

        deleteServiceComponentHost(cluster1, hdfsService, datanode, host2, null);
        deleteHost(host2);
        Assert.assertFalse(nameNodeSch.isRestartRequired());// NameNode doesn't need to be restarted!

        List<Long> requestIDs = actionDB.getRequestsByStatus(null, 1, false);
        Request request = actionDB.getRequest(requestIDs.get(0));
        Assert.assertEquals("Update Include/Exclude Files for [HDFS]", request.getRequestContext());
        Assert.assertEquals(false, request.isExclusive());
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestParams = StageUtils.getGson().fromJson(request.getInputs(), type);
        Assert.assertEquals(2, requestParams.size());
        Assert.assertEquals("true", requestParams.get("is_add_or_delete_slave_request"));
        Assert.assertEquals("true", requestParams.get("update_files_only"));
        Assert.assertEquals(1, request.getResourceFilters().size());
        RequestResourceFilter resourceFilter = request.getResourceFilters().get(0);
        Assert.assertEquals(resourceFilter.getServiceName(), hdfsService);
        Assert.assertEquals(resourceFilter.getComponentName(), namenode);
        Assert.assertEquals(resourceFilter.getHostNames(), new ArrayList<String>());
    }

    @Test
    public void testMaintenanceState() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-1.2.0"));
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("context", "Called from a test");
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service service = cluster.getService(serviceName);
        Map<String, Host> hosts = AmbariManagementControllerTest.clusters.getHostsForCluster(cluster1);
        MaintenanceStateHelper maintenanceStateHelper = MaintenanceStateHelperTest.getMaintenanceStateHelperInstance(AmbariManagementControllerTest.clusters);
        // test updating a service
        ServiceRequest sr = new ServiceRequest(cluster1, serviceName, repositoryVersion120.getId(), null);
        sr.setMaintenanceState(ON.name());
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, Collections.singleton(sr), requestProperties, false, false, maintenanceStateHelper);
        Assert.assertEquals(ON, service.getMaintenanceState());
        // check the host components implied state vs desired state
        for (ServiceComponent sc : service.getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals(IMPLIED_FROM_SERVICE, AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(sch));
                Assert.assertEquals(OFF, sch.getMaintenanceState());
            }
        }
        // reset
        sr.setMaintenanceState(OFF.name());
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, Collections.singleton(sr), requestProperties, false, false, maintenanceStateHelper);
        Assert.assertEquals(OFF, service.getMaintenanceState());
        // check the host components implied state vs desired state
        for (ServiceComponent sc : service.getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals(OFF, AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(sch));
                Assert.assertEquals(OFF, sch.getMaintenanceState());
            }
        }
        // passivate a host
        HostRequest hr = new HostRequest(host1, cluster1);
        hr.setMaintenanceState(ON.name());
        HostResourceProviderTest.updateHosts(AmbariManagementControllerTest.controller, Collections.singleton(hr));
        Host host = hosts.get(host1);
        Assert.assertEquals(ON, host.getMaintenanceState(cluster.getClusterId()));
        // check the host components implied state vs desired state, only for
        // affected hosts
        for (ServiceComponent sc : service.getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                MaintenanceState implied = AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(sch);
                if (sch.getHostName().equals(host1)) {
                    Assert.assertEquals(IMPLIED_FROM_HOST, implied);
                } else {
                    Assert.assertEquals(OFF, implied);
                }
                Assert.assertEquals(OFF, sch.getMaintenanceState());
            }
        }
        // reset
        hr.setMaintenanceState(OFF.name());
        HostResourceProviderTest.updateHosts(AmbariManagementControllerTest.controller, Collections.singleton(hr));
        host = hosts.get(host1);
        Assert.assertEquals(OFF, host.getMaintenanceState(cluster.getClusterId()));
        // check the host components active state vs desired state
        for (ServiceComponent sc : service.getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals(OFF, AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(sch));
                Assert.assertEquals(OFF, sch.getMaintenanceState());
            }
        }
        // passivate several hosts
        HostRequest hr1 = new HostRequest(host1, cluster1);
        hr1.setMaintenanceState(ON.name());
        HostRequest hr2 = new HostRequest(host2, cluster1);
        hr2.setMaintenanceState(ON.name());
        Set<HostRequest> set = new HashSet<>();
        set.add(hr1);
        set.add(hr2);
        HostResourceProviderTest.updateHosts(AmbariManagementControllerTest.controller, set);
        host = hosts.get(host1);
        Assert.assertEquals(ON, host.getMaintenanceState(cluster.getClusterId()));
        host = hosts.get(host2);
        Assert.assertEquals(ON, host.getMaintenanceState(cluster.getClusterId()));
        // reset
        hr1 = new HostRequest(host1, cluster1);
        hr1.setMaintenanceState(OFF.name());
        hr2 = new HostRequest(host2, cluster1);
        hr2.setMaintenanceState(OFF.name());
        set = new HashSet();
        set.add(hr1);
        set.add(hr2);
        HostResourceProviderTest.updateHosts(AmbariManagementControllerTest.controller, set);
        host = hosts.get(host1);
        Assert.assertEquals(OFF, host.getMaintenanceState(cluster.getClusterId()));
        host = hosts.get(host2);
        Assert.assertEquals(OFF, host.getMaintenanceState(cluster.getClusterId()));
        // only do one SCH
        ServiceComponentHost targetSch = service.getServiceComponent(componentName2).getServiceComponentHosts().get(host2);
        Assert.assertNotNull(targetSch);
        targetSch.setMaintenanceState(ON);
        // check the host components active state vs desired state
        Assert.assertEquals(ON, AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(targetSch));
        // update the service
        service.setMaintenanceState(ON);
        Assert.assertEquals(ON, AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(targetSch));
        // make SCH active
        targetSch.setMaintenanceState(OFF);
        Assert.assertEquals(IMPLIED_FROM_SERVICE, AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(targetSch));
        // update the service
        service.setMaintenanceState(OFF);
        Assert.assertEquals(OFF, AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(targetSch));
        host = hosts.get(host2);
        // update host
        host.setMaintenanceState(cluster.getClusterId(), ON);
        Assert.assertEquals(IMPLIED_FROM_HOST, AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(targetSch));
        targetSch.setMaintenanceState(ON);
        Assert.assertEquals(ON, AmbariManagementControllerTest.controller.getEffectiveMaintenanceState(targetSch));
        // check the host components active state vs desired state
        for (ServiceComponent sc : service.getServiceComponents().values()) {
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals(INIT, sch.getState());
            }
        }
        long id1 = installService(cluster1, serviceName, false, false, maintenanceStateHelper, null);
        List<HostRoleCommand> hdfsCmds = actionDB.getRequestTasks(id1);
        Assert.assertNotNull(hdfsCmds);
        HostRoleCommand datanodeCmd = null;
        for (HostRoleCommand cmd : hdfsCmds) {
            if (cmd.getRole().equals(DATANODE)) {
                datanodeCmd = cmd;
            }
        }
        Assert.assertNotNull(datanodeCmd);
        // verify passive sch was skipped
        for (ServiceComponent sc : service.getServiceComponents().values()) {
            if (!(sc.getName().equals(componentName2))) {
                continue;
            }
            for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                Assert.assertEquals((sch == targetSch ? State.INIT : State.INSTALLED), sch.getState());
            }
        }
    }

    @Test
    public void testCredentialStoreRelatedAPICallsToUpdateSettings() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-2.2.0"));
        String service1Name = "HDFS";
        String service2Name = "STORM";
        String service3Name = "ZOOKEEPER";
        createService(cluster1, service1Name, repositoryVersion220, null);
        createService(cluster1, service2Name, repositoryVersion220, null);
        createService(cluster1, service3Name, repositoryVersion220, null);
        String component1Name = "NAMENODE";
        String component2Name = "DRPC_SERVER";
        String component3Name = "ZOOKEEPER_SERVER";
        createServiceComponent(cluster1, service1Name, component1Name, INIT);
        createServiceComponent(cluster1, service2Name, component2Name, INIT);
        createServiceComponent(cluster1, service3Name, component3Name, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        createServiceComponentHost(cluster1, service1Name, component1Name, host1, null);
        createServiceComponentHost(cluster1, service2Name, component2Name, host1, null);
        createServiceComponentHost(cluster1, service3Name, component3Name, host1, null);
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("context", "Called from a test");
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        Service service1 = cluster.getService(service1Name);
        MaintenanceStateHelper maintenanceStateHelper = MaintenanceStateHelperTest.getMaintenanceStateHelperInstance(AmbariManagementControllerTest.clusters);
        // test updating a service
        ServiceRequest sr = new ServiceRequest(cluster1, service1Name, repositoryVersion220.getId(), null);
        sr.setCredentialStoreEnabled("true");
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, Collections.singleton(sr), requestProperties, false, false, maintenanceStateHelper);
        Assert.assertTrue(service1.isCredentialStoreEnabled());
        Assert.assertTrue(service1.isCredentialStoreSupported());
        Assert.assertFalse(service1.isCredentialStoreRequired());
        ServiceRequest sr2 = new ServiceRequest(cluster1, service2Name, repositoryVersion220.getId(), null);
        sr2.setCredentialStoreEnabled("true");
        try {
            ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, Collections.singleton(sr2), requestProperties, false, false, maintenanceStateHelper);
            Assert.assertTrue("Expected exception not thrown - service does not support cred store", true);
        } catch (IllegalArgumentException iaex) {
            Assert.assertTrue(iaex.getMessage(), iaex.getMessage().contains("Invalid arguments, cannot enable credential store as it is not supported by the service. Service=STORM"));
        }
        ServiceRequest sr3 = new ServiceRequest(cluster1, service3Name, repositoryVersion220.getId(), null);
        sr3.setCredentialStoreEnabled("false");
        try {
            ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, Collections.singleton(sr3), requestProperties, false, false, maintenanceStateHelper);
            Assert.assertTrue("Expected exception not thrown - service does not support disabling of cred store", true);
        } catch (IllegalArgumentException iaex) {
            Assert.assertTrue(iaex.getMessage(), iaex.getMessage().contains("Invalid arguments, cannot disable credential store as it is required by the service. Service=ZOOKEEPER"));
        }
        ServiceRequest sr4 = new ServiceRequest(cluster1, service3Name, repositoryVersion220.getId(), null);
        sr4.setCredentialStoreSupported("true");
        try {
            ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, Collections.singleton(sr4), requestProperties, false, false, maintenanceStateHelper);
            Assert.assertTrue("Expected exception not thrown - service does not support updating cred store support", true);
        } catch (IllegalArgumentException iaex) {
            Assert.assertTrue(iaex.getMessage(), iaex.getMessage().contains("Invalid arguments, cannot update credential_store_supported as it is set only via service definition. Service=ZOOKEEPER"));
        }
    }

    @Test
    public void testPassiveSkipServices() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("HDP-0.1"));
        String serviceName1 = "HDFS";
        String serviceName2 = "MAPREDUCE";
        createService(cluster1, serviceName1, null);
        createService(cluster1, serviceName2, null);
        String componentName1_1 = "NAMENODE";
        String componentName1_2 = "DATANODE";
        String componentName1_3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName1, componentName1_1, INIT);
        createServiceComponent(cluster1, serviceName1, componentName1_2, INIT);
        createServiceComponent(cluster1, serviceName1, componentName1_3, INIT);
        String componentName2_1 = "JOBTRACKER";
        String componentName2_2 = "TASKTRACKER";
        createServiceComponent(cluster1, serviceName2, componentName2_1, INIT);
        createServiceComponent(cluster1, serviceName2, componentName2_2, INIT);
        String host1 = AmbariManagementControllerTest.getUniqueName();
        String host2 = AmbariManagementControllerTest.getUniqueName();
        addHostToCluster(host1, cluster1);
        addHostToCluster(host2, cluster1);
        createServiceComponentHost(cluster1, serviceName1, componentName1_1, host1, null);
        createServiceComponentHost(cluster1, serviceName1, componentName1_2, host1, null);
        createServiceComponentHost(cluster1, serviceName1, componentName1_2, host2, null);
        createServiceComponentHost(cluster1, serviceName2, componentName2_1, host1, null);
        createServiceComponentHost(cluster1, serviceName2, componentName2_2, host2, null);
        MaintenanceStateHelper maintenanceStateHelper = MaintenanceStateHelperTest.getMaintenanceStateHelperInstance(AmbariManagementControllerTest.clusters);
        installService(cluster1, serviceName1, false, false, maintenanceStateHelper, null);
        installService(cluster1, serviceName2, false, false, maintenanceStateHelper, null);
        startService(cluster1, serviceName1, false, false, maintenanceStateHelper);
        startService(cluster1, serviceName2, false, false, maintenanceStateHelper);
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("context", "Called from a test");
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        for (Service service : cluster.getServices().values()) {
            Assert.assertEquals(STARTED, service.getDesiredState());
        }
        Service service2 = cluster.getService(serviceName2);
        service2.setMaintenanceState(ON);
        Set<ServiceRequest> srs = new HashSet<>();
        srs.add(new ServiceRequest(cluster1, serviceName1, repositoryVersion01.getId(), INSTALLED.name()));
        srs.add(new ServiceRequest(cluster1, serviceName2, repositoryVersion01.getId(), INSTALLED.name()));
        RequestStatusResponse rsr = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, srs, requestProperties, false, false, maintenanceStateHelper);
        for (ShortTaskStatus sts : rsr.getTasks()) {
            String role = sts.getRole();
            Assert.assertFalse(role.equals(componentName2_1));
            Assert.assertFalse(role.equals(componentName2_2));
        }
        for (Service service : cluster.getServices().values()) {
            if (service.getName().equals(serviceName2)) {
                Assert.assertEquals(STARTED, service.getDesiredState());
            } else {
                Assert.assertEquals(INSTALLED, service.getDesiredState());
            }
        }
        service2.setMaintenanceState(OFF);
        ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, srs, requestProperties, false, false, maintenanceStateHelper);
        for (Service service : cluster.getServices().values()) {
            Assert.assertEquals(INSTALLED, service.getDesiredState());
        }
        startService(cluster1, serviceName1, false, false, maintenanceStateHelper);
        startService(cluster1, serviceName2, false, false, maintenanceStateHelper);
        // test host
        Host h1 = AmbariManagementControllerTest.clusters.getHost(host1);
        h1.setMaintenanceState(cluster.getClusterId(), ON);
        srs = new HashSet();
        srs.add(new ServiceRequest(cluster1, serviceName1, repositoryVersion01.getId(), INSTALLED.name()));
        srs.add(new ServiceRequest(cluster1, serviceName2, repositoryVersion01.getId(), INSTALLED.name()));
        rsr = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, srs, requestProperties, false, false, maintenanceStateHelper);
        for (ShortTaskStatus sts : rsr.getTasks()) {
            Assert.assertFalse(sts.getHostName().equals(host1));
        }
        h1.setMaintenanceState(cluster.getClusterId(), OFF);
        startService(cluster1, serviceName2, false, false, maintenanceStateHelper);
        service2.setMaintenanceState(ON);
        ServiceRequest sr = new ServiceRequest(cluster1, serviceName2, repositoryVersion01.getId(), INSTALLED.name());
        rsr = ServiceResourceProviderTest.updateServices(AmbariManagementControllerTest.controller, Collections.singleton(sr), requestProperties, false, false, maintenanceStateHelper);
        Assert.assertTrue(("Service start request defaults to Cluster operation level," + "command does not create tasks"), ((rsr == null) || ((rsr.getTasks().size()) == 0)));
    }

    @Test
    public void testIsAttributeMapsEqual() {
        AmbariManagementControllerImpl controllerImpl = null;
        if ((AmbariManagementControllerTest.controller) instanceof AmbariManagementControllerImpl) {
            controllerImpl = ((AmbariManagementControllerImpl) (AmbariManagementControllerTest.controller));
        }
        Map<String, Map<String, String>> requestConfigAttributes = new HashMap<>();
        Map<String, Map<String, String>> clusterConfigAttributes = new HashMap<>();
        Assert.assertTrue(controllerImpl.isAttributeMapsEqual(requestConfigAttributes, clusterConfigAttributes));
        requestConfigAttributes.put("final", new HashMap<>());
        requestConfigAttributes.get("final").put("c", "true");
        clusterConfigAttributes.put("final", new HashMap<>());
        clusterConfigAttributes.get("final").put("c", "true");
        Assert.assertTrue(controllerImpl.isAttributeMapsEqual(requestConfigAttributes, clusterConfigAttributes));
        clusterConfigAttributes.put("final2", new HashMap<>());
        clusterConfigAttributes.get("final2").put("a", "true");
        Assert.assertFalse(controllerImpl.isAttributeMapsEqual(requestConfigAttributes, clusterConfigAttributes));
        requestConfigAttributes.put("final2", new HashMap<>());
        requestConfigAttributes.get("final2").put("a", "false");
        Assert.assertFalse(controllerImpl.isAttributeMapsEqual(requestConfigAttributes, clusterConfigAttributes));
    }

    @Test
    public void testEmptyConfigs() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        createCluster(cluster1);
        Cluster cluster = AmbariManagementControllerTest.clusters.getCluster(cluster1);
        cluster.setDesiredStackVersion(new StackId("HDP-0.1"));
        ClusterRequest cr = new ClusterRequest(cluster.getClusterId(), cluster.getClusterName(), null, null);
        // test null map with no prior
        cr.setDesiredConfig(Collections.singletonList(new ConfigurationRequest(cluster1, "typeA", "v1", null, null)));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(cr), new HashMap());
        Config config = cluster.getDesiredConfigByType("typeA");
        Assert.assertNull(config);
        // test empty map with no prior
        cr.setDesiredConfig(Collections.singletonList(new ConfigurationRequest(cluster1, "typeA", "v1", new HashMap(), new HashMap())));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(cr), new HashMap());
        config = cluster.getDesiredConfigByType("typeA");
        Assert.assertNotNull(config);
        // test empty properties on a new version
        cr.setDesiredConfig(Collections.singletonList(new ConfigurationRequest(cluster1, "typeA", "v2", new HashMap(), new HashMap())));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(cr), new HashMap());
        config = cluster.getDesiredConfigByType("typeA");
        Assert.assertNotNull(config);
        Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(config.getProperties().size()));
        // test new version
        Map<String, String> map = new HashMap<>();
        map.clear();
        map.put("c", "d");
        Map<String, Map<String, String>> attributesMap = new HashMap<>();
        attributesMap.put("final", new HashMap<>());
        attributesMap.get("final").put("c", "true");
        cr.setDesiredConfig(Collections.singletonList(new ConfigurationRequest(cluster1, "typeA", "v3", map, attributesMap)));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(cr), new HashMap());
        config = cluster.getDesiredConfigByType("typeA");
        Assert.assertNotNull(config);
        Assert.assertTrue(config.getProperties().containsKey("c"));
        // test reset to v2
        cr.setDesiredConfig(Collections.singletonList(new ConfigurationRequest(cluster1, "typeA", "v2", new HashMap(), new HashMap())));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(cr), new HashMap());
        config = cluster.getDesiredConfigByType("typeA");
        Assert.assertEquals("v2", config.getTag());
        Assert.assertNotNull(config);
        Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(config.getProperties().size()));
        // test v2, but with properties
        cr.setDesiredConfig(Collections.singletonList(new ConfigurationRequest(cluster1, "typeA", "v2", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, new HashMap<String, Map<String, String>>() {
            {
                put("final", new HashMap<String, String>() {
                    {
                        put("a", "true");
                    }
                });
            }
        })));
        try {
            AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(cr), new HashMap());
            Assert.fail("Expect failure when creating a config that exists");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testCreateCustomActionNoCluster() throws Exception {
        String hostname1 = AmbariManagementControllerTest.getUniqueName();
        String hostname2 = AmbariManagementControllerTest.getUniqueName();
        addHost(hostname1);
        addHost(hostname2);
        String action1 = AmbariManagementControllerTest.getUniqueName();
        AmbariManagementControllerTest.ambariMetaInfo.addActionDefinition(new ActionDefinition(action1, ActionType.SYSTEM, "", "", "", "action def description", TargetHostType.ANY, Short.valueOf("60"), null));
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY, "Called from a test");
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("some_custom_param", "abc");
        // !!! target single host
        List<String> hosts = Arrays.asList(hostname1);
        RequestResourceFilter resourceFilter = new RequestResourceFilter(null, null, hosts);
        List<RequestResourceFilter> resourceFilters = new ArrayList<>();
        resourceFilters.add(resourceFilter);
        ExecuteActionRequest actionRequest = new ExecuteActionRequest(null, null, action1, resourceFilters, null, requestParams, false);
        RequestStatusResponse response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        Assert.assertEquals(1, response.getTasks().size());
        ShortTaskStatus taskStatus = response.getTasks().get(0);
        Assert.assertEquals(hostname1, taskStatus.getHostName());
        Stage stage = actionDB.getAllStages(response.getRequestId()).get(0);
        Assert.assertNotNull(stage);
        Assert.assertEquals((-1L), stage.getClusterId());
        List<HostRoleCommand> storedTasks = actionDB.getRequestTasks(response.getRequestId());
        Assert.assertEquals(1, storedTasks.size());
        HostRoleCommand task = storedTasks.get(0);
        Assert.assertEquals(ACTIONEXECUTE, task.getRoleCommand());
        Assert.assertEquals(action1, task.getRole().name());
        Assert.assertEquals(hostname1, task.getHostName());
        ExecutionCommand cmd = task.getExecutionCommandWrapper().getExecutionCommand();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> commandParamsStage = StageUtils.getGson().fromJson(stage.getCommandParamsStage(), type);
        Assert.assertTrue(commandParamsStage.containsKey("some_custom_param"));
        Assert.assertEquals(null, cmd.getServiceName());
        Assert.assertEquals(null, cmd.getComponentName());
        Assert.assertTrue(cmd.getLocalComponents().isEmpty());
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
        // !!! target two hosts
        hosts = Arrays.asList(hostname1, hostname2);
        resourceFilter = new RequestResourceFilter(null, null, hosts);
        resourceFilters = new ArrayList();
        resourceFilters.add(resourceFilter);
        actionRequest = new ExecuteActionRequest(null, null, action1, resourceFilters, null, requestParams, false);
        response = AmbariManagementControllerTest.controller.createAction(actionRequest, requestProperties);
        Assert.assertEquals(2, response.getTasks().size());
        boolean host1Found = false;
        boolean host2Found = false;
        for (ShortTaskStatus sts : response.getTasks()) {
            if (sts.getHostName().equals(hostname1)) {
                host1Found = true;
            } else
                if (sts.getHostName().equals(hostname2)) {
                    host2Found = true;
                }

        }
        Assert.assertTrue(host1Found);
        Assert.assertTrue(host2Found);
        stage = actionDB.getAllStages(response.getRequestId()).get(0);
        Assert.assertNotNull(stage);
        Assert.assertEquals((-1L), stage.getClusterId());
        storedTasks = actionDB.getRequestTasks(response.getRequestId());
        Assert.assertEquals(2, storedTasks.size());
        task = storedTasks.get(0);
        Assert.assertEquals(ACTIONEXECUTE, task.getRoleCommand());
        Assert.assertEquals(action1, task.getRole().name());
        // order is not guaranteed
        Assert.assertTrue(((hostname1.equals(task.getHostName())) || (hostname2.equals(task.getHostName()))));
        cmd = task.getExecutionCommandWrapper().getExecutionCommand();
        commandParamsStage = StageUtils.getGson().fromJson(stage.getCommandParamsStage(), type);
        Assert.assertTrue(commandParamsStage.containsKey("some_custom_param"));
        Assert.assertEquals(null, cmd.getServiceName());
        Assert.assertEquals(null, cmd.getComponentName());
        Assert.assertTrue(cmd.getLocalComponents().isEmpty());
        Assert.assertEquals(requestProperties.get(AmbariManagementControllerTest.REQUEST_CONTEXT_PROPERTY), response.getRequestContext());
    }

    @Test
    public void testSecretReferences() throws Exception, AuthorizationException {
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        final String host2 = AmbariManagementControllerTest.getUniqueName();
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        Cluster cl = setupClusterWithHosts(cluster1, "HDP-2.0.5", new ArrayList<String>() {
            {
                add(host1);
                add(host2);
            }
        }, "centos5");
        Long clusterId = cl.getClusterId();
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        String componentName2 = "DATANODE";
        String componentName3 = "HDFS_CLIENT";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponent(cluster1, serviceName, componentName2, INIT);
        createServiceComponent(cluster1, serviceName, componentName3, INIT);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host1, null);
        createServiceComponentHost(cluster1, serviceName, componentName2, host2, null);
        createServiceComponentHost(cluster1, serviceName, componentName3, host2, null);
        // Install
        installService(cluster1, serviceName, false, false);
        ClusterRequest crReq;
        ConfigurationRequest cr;
        cr = new ConfigurationRequest(cluster1, "hdfs-site", "version1", new HashMap<String, String>() {
            {
                put("test.password", "first");
                put("test.password.empty", "");
            }
        }, new HashMap());
        crReq = new ClusterRequest(clusterId, cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        // update config with secret reference
        cr = new ConfigurationRequest(cluster1, "hdfs-site", "version2", new HashMap<String, String>() {
            {
                put("test.password", "SECRET:hdfs-site:1:test.password");
                put("new", "new");// need this to mark config as "changed"

            }
        }, new HashMap());
        crReq = new ClusterRequest(clusterId, cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        // change password to new value
        cr = new ConfigurationRequest(cluster1, "hdfs-site", "version3", new HashMap<String, String>() {
            {
                put("test.password", "brandNewPassword");
            }
        }, new HashMap());
        crReq = new ClusterRequest(clusterId, cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        // wrong secret reference
        cr = new ConfigurationRequest(cluster1, "hdfs-site", "version3", new HashMap<String, String>() {
            {
                put("test.password", "SECRET:hdfs-site:666:test.password");
            }
        }, new HashMap());
        crReq = new ClusterRequest(clusterId, cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr));
        try {
            AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
            Assert.fail("Request need to be failed with wrong secret reference");
        } catch (Exception e) {
        }
        // reference to config which does not contain requested property
        cr = new ConfigurationRequest(cluster1, "hdfs-site", "version4", new HashMap<String, String>() {
            {
                put("foo", "bar");
            }
        }, new HashMap());
        crReq = new ClusterRequest(clusterId, cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
        cr = new ConfigurationRequest(cluster1, "hdfs-site", "version5", new HashMap<String, String>() {
            {
                put("test.password", "SECRET:hdfs-site:4:test.password");
                put("new", "new");
            }
        }, new HashMap());
        crReq = new ClusterRequest(clusterId, cluster1, null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr));
        try {
            AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq), null);
            Assert.fail("Request need to be failed with wrong secret reference");
        } catch (Exception e) {
            Assert.assertEquals((("Error when parsing secret reference. Cluster: " + cluster1) + " ConfigType: hdfs-site ConfigVersion: 4 does not contain property 'test.password'"), e.getMessage());
        }
        cl.getAllConfigs();
        Assert.assertEquals(cl.getAllConfigs().size(), 4);
        Config v1 = cl.getConfigByVersion("hdfs-site", 1L);
        Config v2 = cl.getConfigByVersion("hdfs-site", 2L);
        Config v3 = cl.getConfigByVersion("hdfs-site", 3L);
        Config v4 = cl.getConfigByVersion("hdfs-site", 4L);
        Assert.assertEquals(v1.getProperties().get("test.password"), "first");
        Assert.assertEquals(v2.getProperties().get("test.password"), "first");
        Assert.assertEquals(v3.getProperties().get("test.password"), "brandNewPassword");
        Assert.assertFalse(v4.getProperties().containsKey("test.password"));
        // check if we have masked secret in responce
        final ConfigurationRequest configRequest = new ConfigurationRequest(cluster1, "hdfs-site", null, null, null);
        configRequest.setIncludeProperties(true);
        Set<ConfigurationResponse> requestedConfigs = AmbariManagementControllerTest.controller.getConfigurations(new HashSet<ConfigurationRequest>() {
            {
                add(configRequest);
            }
        });
        for (ConfigurationResponse resp : requestedConfigs) {
            String secretName = ("SECRET:hdfs-site:" + (resp.getVersion())) + ":test.password";
            if (resp.getConfigs().containsKey("test.password")) {
                Assert.assertEquals(resp.getConfigs().get("test.password"), secretName);
            }
            if (resp.getConfigs().containsKey("test.password.empty")) {
                Assert.assertEquals(resp.getConfigs().get("test.password.empty"), "");
            }
        }
    }

    @Test
    public void testTargetedProcessCommand() throws Exception {
        final String host1 = AmbariManagementControllerTest.getUniqueName();
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        Cluster cluster = setupClusterWithHosts(cluster1, "HDP-2.0.5", Arrays.asList(host1), "centos5");
        String serviceName = "HDFS";
        createService(cluster1, serviceName, null);
        String componentName1 = "NAMENODE";
        createServiceComponent(cluster1, serviceName, componentName1, INIT);
        createServiceComponentHost(cluster1, serviceName, componentName1, host1, null);
        // Install
        installService(cluster1, serviceName, false, false);
        // Create and attach config
        // hdfs-site will not have config-attributes
        Map<String, String> hdfsConfigs = new HashMap<>();
        hdfsConfigs.put("a", "b");
        Map<String, Map<String, String>> hdfsConfigAttributes = new HashMap<String, Map<String, String>>() {
            {
                put("final", new HashMap<String, String>() {
                    {
                        put("a", "true");
                    }
                });
            }
        };
        ConfigurationRequest cr1 = new ConfigurationRequest(cluster1, "hdfs-site", "version1", hdfsConfigs, hdfsConfigAttributes);
        ClusterRequest crReq1 = new ClusterRequest(cluster.getClusterId(), cluster1, null, null);
        crReq1.setDesiredConfig(Collections.singletonList(cr1));
        AmbariManagementControllerTest.controller.updateClusters(Collections.singleton(crReq1), null);
        // Start
        startService(cluster1, serviceName, false, false);
        ServiceComponentHostRequest req = new ServiceComponentHostRequest(cluster1, serviceName, componentName1, host1, "INSTALLED");
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("namenode", "p1");
        RequestStatusResponse resp = updateHostComponents(Collections.singleton(req), requestProperties, false);
        // succeed in creating a task
        Assert.assertNotNull(resp);
        // manually change live state to stopped as no running action manager
        for (ServiceComponentHost sch : AmbariManagementControllerTest.clusters.getCluster(cluster1).getServiceComponentHosts(host1)) {
            sch.setState(INSTALLED);
        }
        // no new commands since no targeted info
        resp = updateHostComponents(Collections.singleton(req), new HashMap(), false);
        Assert.assertNull(resp);
        // role commands added for targeted command
        resp = updateHostComponents(Collections.singleton(req), requestProperties, false);
        Assert.assertNotNull(resp);
    }

    @Test
    public void testGetPackagesForServiceHost() throws Exception {
        ServiceInfo service = AmbariManagementControllerTest.ambariMetaInfo.getStack("HDP", "2.0.1").getService("HIVE");
        HashMap<String, String> hostParams = new HashMap<>();
        Map<String, ServiceOsSpecific.Package> packages = new HashMap<>();
        String[] packageNames = new String[]{ "hive", "mysql-connector-java", "mysql", "mysql-server", "mysql-client" };
        for (String packageName : packageNames) {
            ServiceOsSpecific.Package pkg = new ServiceOsSpecific.Package();
            pkg.setName(packageName);
            packages.put(packageName, pkg);
        }
        List<ServiceOsSpecific.Package> rhel5Packages = AmbariManagementControllerTest.controller.getPackagesForServiceHost(service, hostParams, "redhat5");
        List<ServiceOsSpecific.Package> expectedRhel5 = Arrays.asList(packages.get("hive"), packages.get("mysql-connector-java"), packages.get("mysql"), packages.get("mysql-server"));
        List<ServiceOsSpecific.Package> sles11Packages = AmbariManagementControllerTest.controller.getPackagesForServiceHost(service, hostParams, "suse11");
        List<ServiceOsSpecific.Package> expectedSles11 = Arrays.asList(packages.get("hive"), packages.get("mysql-connector-java"), packages.get("mysql"), packages.get("mysql-client"));
        Assert.assertThat(rhel5Packages, CoreMatchers.is(expectedRhel5));
        Assert.assertThat(sles11Packages, CoreMatchers.is(expectedSles11));
    }

    @Test
    public void testServiceWidgetCreationOnServiceCreate() throws Exception {
        String cluster1 = AmbariManagementControllerTest.getUniqueName();
        ClusterRequest r = new ClusterRequest(null, cluster1, INSTALLED.name(), SecurityType.NONE, "OTHER-2.0", null);
        AmbariManagementControllerTest.controller.createCluster(r);
        String serviceName = "HBASE";
        AmbariManagementControllerTest.clusters.getCluster(cluster1).setDesiredStackVersion(new StackId("OTHER-2.0"));
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(new StackId("OTHER-2.0"), "2.0-1234");
        createService(cluster1, serviceName, repositoryVersion, INIT);
        Service s = AmbariManagementControllerTest.clusters.getCluster(cluster1).getService(serviceName);
        Assert.assertNotNull(s);
        Assert.assertEquals(serviceName, s.getName());
        Assert.assertEquals(cluster1, s.getCluster().getClusterName());
        WidgetDAO widgetDAO = AmbariManagementControllerTest.injector.getInstance(WidgetDAO.class);
        WidgetLayoutDAO widgetLayoutDAO = AmbariManagementControllerTest.injector.getInstance(WidgetLayoutDAO.class);
        List<WidgetEntity> widgetEntities = widgetDAO.findAll();
        List<WidgetLayoutEntity> layoutEntities = widgetLayoutDAO.findAll();
        Assert.assertNotNull(widgetEntities);
        Assert.assertFalse(widgetEntities.isEmpty());
        Assert.assertNotNull(layoutEntities);
        Assert.assertFalse(layoutEntities.isEmpty());
        WidgetEntity candidateVisibleEntity = null;
        for (WidgetEntity entity : widgetEntities) {
            if (entity.getWidgetName().equals("OPEN_CONNECTIONS")) {
                candidateVisibleEntity = entity;
            }
        }
        Assert.assertNotNull(candidateVisibleEntity);
        Assert.assertEquals("GRAPH", candidateVisibleEntity.getWidgetType());
        Assert.assertEquals("ambari", candidateVisibleEntity.getAuthor());
        Assert.assertEquals("CLUSTER", candidateVisibleEntity.getScope());
        Assert.assertNotNull(candidateVisibleEntity.getMetrics());
        Assert.assertNotNull(candidateVisibleEntity.getProperties());
        Assert.assertNotNull(candidateVisibleEntity.getWidgetValues());
        WidgetLayoutEntity candidateLayoutEntity = null;
        for (WidgetLayoutEntity entity : layoutEntities) {
            if (entity.getLayoutName().equals("default_hbase_layout")) {
                candidateLayoutEntity = entity;
            }
        }
        Assert.assertNotNull(candidateLayoutEntity);
        List<WidgetLayoutUserWidgetEntity> layoutUserWidgetEntities = candidateLayoutEntity.getListWidgetLayoutUserWidgetEntity();
        Assert.assertNotNull(layoutUserWidgetEntities);
        Assert.assertEquals(4, layoutUserWidgetEntities.size());
        Assert.assertEquals("RS_READS_WRITES", layoutUserWidgetEntities.get(0).getWidget().getWidgetName());
        Assert.assertEquals("OPEN_CONNECTIONS", layoutUserWidgetEntities.get(1).getWidget().getWidgetName());
        Assert.assertEquals("FILES_LOCAL", layoutUserWidgetEntities.get(2).getWidget().getWidgetName());
        Assert.assertEquals("UPDATED_BLOCKED_TIME", layoutUserWidgetEntities.get(3).getWidget().getWidgetName());
        Assert.assertEquals("HBASE_SUMMARY", layoutUserWidgetEntities.get(0).getWidget().getDefaultSectionName());
        File widgetsFile = AmbariManagementControllerTest.ambariMetaInfo.getCommonWidgetsDescriptorFile();
        Assert.assertNotNull(widgetsFile);
        Assert.assertEquals("src/test/resources/widgets.json", widgetsFile.getPath());
        Assert.assertTrue(widgetsFile.exists());
        candidateLayoutEntity = null;
        for (WidgetLayoutEntity entity : layoutEntities) {
            if (entity.getLayoutName().equals("default_system_heatmap")) {
                candidateLayoutEntity = entity;
                break;
            }
        }
        Assert.assertNotNull(candidateLayoutEntity);
        Assert.assertEquals("ambari", candidateVisibleEntity.getAuthor());
        Assert.assertEquals("CLUSTER", candidateVisibleEntity.getScope());
    }
}

