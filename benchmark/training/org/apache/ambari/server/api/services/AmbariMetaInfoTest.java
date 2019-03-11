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
package org.apache.ambari.server.api.services;


import PropertyInfo.PropertyType.PASSWORD;
import Resource.Type.Component;
import SourceType.METRIC;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.StackAccessException;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.internal.DeleteHostComponentStatusMetaData;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.metadata.AmbariServiceAlertDefinitions;
import org.apache.ambari.server.mpack.MpackManagerFactory;
import org.apache.ambari.server.orm.GuiceJpaInitializer;
import org.apache.ambari.server.orm.InMemoryDefaultTestModule;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.dao.MetainfoDAO;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.stack.StackManager;
import org.apache.ambari.server.stack.StackManagerFactory;
import org.apache.ambari.server.state.AutoDeployInfo;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ComponentInfo;
import org.apache.ambari.server.state.CustomCommandDefinition;
import org.apache.ambari.server.state.DependencyInfo;
import org.apache.ambari.server.state.OperatingSystemInfo;
import org.apache.ambari.server.state.PropertyDependencyInfo;
import org.apache.ambari.server.state.PropertyInfo;
import org.apache.ambari.server.state.RepositoryInfo;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.server.state.alert.AlertDefinition;
import org.apache.ambari.server.state.alert.AlertDefinitionFactory;
import org.apache.ambari.server.state.alert.MetricSource;
import org.apache.ambari.server.state.alert.Reporting;
import org.apache.ambari.server.state.alert.Source;
import org.apache.ambari.server.state.kerberos.KerberosDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosDescriptorFactory;
import org.apache.ambari.server.state.kerberos.KerberosServiceDescriptorFactory;
import org.apache.ambari.server.state.repository.VersionDefinitionXml;
import org.apache.ambari.server.state.stack.MetricDefinition;
import org.apache.ambari.server.state.stack.OsFamily;
import org.apache.ambari.server.utils.EventBusSynchronizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


public class AmbariMetaInfoTest {
    private static final String STACK_NAME_HDP = "HDP";

    private static final String STACK_VERSION_HDP = "0.1";

    private static final String EXT_STACK_NAME = "2.0.6";

    private static final String STACK_VERSION_HDP_02 = "0.2";

    private static final String STACK_MINIMAL_VERSION_HDP = "0.0";

    private static final String SERVICE_NAME_HDFS = "HDFS";

    private static final String SERVICE_NAME_MAPRED2 = "MAPREDUCE2";

    private static final String SERVICE_COMPONENT_NAME = "NAMENODE";

    private static final String OS_TYPE = "centos5";

    private static final String REPO_ID = "HDP-UTILS-1.1.0.15";

    private static final String PROPERTY_NAME = "hbase.regionserver.msginterval";

    private static final String SHARED_PROPERTY_NAME = "content";

    private static final String NON_EXT_VALUE = "XXX";

    private static final int REPOS_CNT = 3;

    private static final int PROPERTIES_CNT = 64;

    private static final int OS_CNT = 4;

    private static AmbariMetaInfoTest.TestAmbariMetaInfo metaInfo = null;

    private static final Logger LOG = LoggerFactory.getLogger(AmbariMetaInfoTest.class);

    private static final String FILE_NAME = "hbase-site.xml";

    private static final String HADOOP_ENV_FILE_NAME = "hadoop-env.xml";

    private static final String HDFS_LOG4J_FILE_NAME = "hdfs-log4j.xml";

    // private Injector injector;
    // todo: add fail() for cases where an exception is expected such as getService, getComponent ...
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public class MockModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ActionMetadata.class);
        }
    }

    @Test
    public void getRestartRequiredServicesNames() throws AmbariException {
        Set<String> res = AmbariMetaInfoTest.metaInfo.getRestartRequiredServicesNames(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7");
        Assert.assertEquals(1, res.size());
    }

    @Test
    public void testGetRackSensitiveServicesNames() throws AmbariException {
        Set<String> res = getRackSensitiveServicesNames(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7");
        Assert.assertEquals(1, res.size());
        Assert.assertEquals("HDFS", res.iterator().next());
    }

    @Test
    public void getComponentsByService() throws AmbariException {
        List<ComponentInfo> components = AmbariMetaInfoTest.metaInfo.getComponentsByService(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_NAME_HDFS);
        Assert.assertNotNull(components);
        Assert.assertTrue(((components.size()) > 0));
    }

    @Test
    public void getLogs() throws AmbariException {
        ComponentInfo component;
        component = getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, "2.1.1", AmbariMetaInfoTest.SERVICE_NAME_HDFS, "NAMENODE");
        Assert.assertNotNull(component);
        Assert.assertNotNull(component.getLogs());
        Assert.assertTrue(((component.getLogs().size()) == 2));
        Assert.assertEquals(component.getLogs().get(0).getLogId(), "hdfs_namenode");
        Assert.assertEquals(component.getLogs().get(1).getLogId(), "hdfs_audit");
        Assert.assertTrue(component.getLogs().get(0).isPrimary());
        Assert.assertFalse(component.getLogs().get(1).isPrimary());
        component = getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, "2.1.1", AmbariMetaInfoTest.SERVICE_NAME_HDFS, "DATANODE");
        Assert.assertNotNull(component);
        Assert.assertNotNull(component.getLogs());
        Assert.assertTrue(((component.getLogs().size()) == 1));
        Assert.assertEquals(component.getLogs().get(0).getLogId(), "hdfs_datanode");
        Assert.assertTrue(component.getLogs().get(0).isPrimary());
        component = getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, "2.1.1", AmbariMetaInfoTest.SERVICE_NAME_HDFS, "HDFS_CLIENT");
        Assert.assertNotNull(component);
        Assert.assertNotNull(component.getLogs());
        Assert.assertTrue(component.getLogs().isEmpty());
    }

    @Test
    public void getRepository() throws AmbariException {
        Map<String, List<RepositoryInfo>> repository = AmbariMetaInfoTest.metaInfo.getRepository(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP);
        Assert.assertNotNull(repository);
        Assert.assertFalse(repository.get("centos5").isEmpty());
        Assert.assertFalse(repository.get("centos6").isEmpty());
    }

    @Test
    public void testGetRepositoryNoInternetDefault() throws Exception {
        // Scenario: user has no internet and does nothing to repos via api
        // use the default
        String buildDir = tmpFolder.getRoot().getAbsolutePath();
        setupTempAmbariMetaInfoDirs(buildDir);
        // The current stack already has (HDP, 2.1.1, redhat6).
        // Deleting the json file referenced by the latestBaseUrl to simulate No
        // Internet.
        File latestUrlFile = new File(buildDir, "ambari-metaInfo/HDP/2.1.1/repos/hdp.json");
        if (System.getProperty("os.name").contains("Windows")) {
            latestUrlFile.deleteOnExit();
        } else {
            FileUtils.deleteQuietly(latestUrlFile);
            Assert.assertTrue((!(latestUrlFile.exists())));
        }
        AmbariMetaInfo ambariMetaInfo = setupTempAmbariMetaInfoExistingDirs(buildDir);
        List<RepositoryInfo> redhat6Repo = ambariMetaInfo.getRepositories(AmbariMetaInfoTest.STACK_NAME_HDP, "2.1.1", "redhat6");
        Assert.assertNotNull(redhat6Repo);
        for (RepositoryInfo ri : redhat6Repo) {
            if (AmbariMetaInfoTest.STACK_NAME_HDP.equals(ri.getRepoName())) {
                // baseUrl should be same as defaultBaseUrl since No Internet to load the
                // latestBaseUrl from the json file.
                Assert.assertEquals(ri.getBaseUrl(), ri.getDefaultBaseUrl());
            }
        }
    }

    @Test
    public void isSupportedStack() throws AmbariException {
        boolean supportedStack = AmbariMetaInfoTest.metaInfo.isSupportedStack(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP);
        Assert.assertTrue(supportedStack);
        boolean notSupportedStack = AmbariMetaInfoTest.metaInfo.isSupportedStack(AmbariMetaInfoTest.NON_EXT_VALUE, AmbariMetaInfoTest.NON_EXT_VALUE);
        Assert.assertFalse(notSupportedStack);
    }

    @Test
    public void isValidService() throws AmbariException {
        boolean valid = AmbariMetaInfoTest.metaInfo.isValidService(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_NAME_HDFS);
        Assert.assertTrue(valid);
        boolean invalid = AmbariMetaInfoTest.metaInfo.isValidService(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.NON_EXT_VALUE);
        Assert.assertFalse(invalid);
    }

    @Test
    public void isServiceWithNoConfigs() throws AmbariException {
        Assert.assertTrue(AmbariMetaInfoTest.metaInfo.isServiceWithNoConfigs(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "SYSTEMML"));
        Assert.assertTrue(AmbariMetaInfoTest.metaInfo.isServiceWithNoConfigs(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "SYSTEMML"));
        Assert.assertFalse(AmbariMetaInfoTest.metaInfo.isServiceWithNoConfigs(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HIVE"));
    }

    @Test
    public void testServiceNameUsingComponentName() throws AmbariException {
        String serviceName = getComponentToService(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_COMPONENT_NAME);
        Assert.assertEquals("HDFS", serviceName);
    }

    /**
     * Method: Map<String, ServiceInfo> getServices(String stackName, String
     * version, String serviceName)
     *
     * @throws AmbariException
     * 		
     */
    @Test
    public void getServices() throws AmbariException {
        Map<String, ServiceInfo> services = AmbariMetaInfoTest.metaInfo.getServices(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP);
        AmbariMetaInfoTest.LOG.info("Getting all the services ");
        for (Map.Entry<String, ServiceInfo> entry : services.entrySet()) {
            AmbariMetaInfoTest.LOG.info(((("Service Name " + (entry.getKey())) + " values ") + (entry.getValue())));
        }
        Assert.assertTrue(services.containsKey("HDFS"));
        Assert.assertTrue(services.containsKey("MAPREDUCE"));
        Assert.assertNotNull(services);
        Assert.assertFalse(((services.keySet().size()) == 0));
    }

    /**
     * Method: getServiceInfo(String stackName, String version, String
     * serviceName)
     */
    @Test
    public void getServiceInfo() throws Exception {
        ServiceInfo si = getService(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_NAME_HDFS);
        Assert.assertNotNull(si);
    }

    @Test
    public void testConfigDependencies() throws Exception {
        ServiceInfo serviceInfo = getService(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.EXT_STACK_NAME, AmbariMetaInfoTest.SERVICE_NAME_MAPRED2);
        Assert.assertNotNull(serviceInfo);
        Assert.assertTrue((!(serviceInfo.getConfigDependencies().isEmpty())));
    }

    @Test
    public void testGetRepos() throws Exception {
        Map<String, List<RepositoryInfo>> repos = AmbariMetaInfoTest.metaInfo.getRepository(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP);
        Set<String> centos5Cnt = new HashSet<>();
        Set<String> centos6Cnt = new HashSet<>();
        Set<String> redhat6cnt = new HashSet<>();
        Set<String> redhat5cnt = new HashSet<>();
        for (List<RepositoryInfo> vals : repos.values()) {
            for (RepositoryInfo repo : vals) {
                AmbariMetaInfoTest.LOG.debug("Dumping repo info : {}", repo);
                if (repo.getOsType().equals("centos5")) {
                    centos5Cnt.add(repo.getRepoId());
                } else
                    if (repo.getOsType().equals("centos6")) {
                        centos6Cnt.add(repo.getRepoId());
                    } else
                        if (repo.getOsType().equals("redhat6")) {
                            redhat6cnt.add(repo.getRepoId());
                        } else
                            if (repo.getOsType().equals("redhat5")) {
                                redhat5cnt.add(repo.getRepoId());
                            } else {
                                Assert.fail(("Found invalid os " + (repo.getOsType())));
                            }



                if (repo.getRepoId().equals("epel")) {
                    Assert.assertFalse(repo.getMirrorsList().isEmpty());
                    Assert.assertNull(repo.getBaseUrl());
                } else {
                    Assert.assertNull(repo.getMirrorsList());
                    Assert.assertFalse(repo.getBaseUrl().isEmpty());
                }
            }
        }
        Assert.assertEquals(3, centos5Cnt.size());
        Assert.assertEquals(3, redhat6cnt.size());
        Assert.assertEquals(3, redhat5cnt.size());
        Assert.assertEquals(3, centos6Cnt.size());
    }

    /**
     * Make sure global mapping is avaliable when global.xml is
     * in the path.
     */
    @Test
    public void testGlobalMapping() throws Exception {
        ServiceInfo sinfo = getService("HDP", "0.2", "HDFS");
        List<PropertyInfo> pinfo = sinfo.getProperties();
        // check all the config knobs and make sure the global one is there
        boolean checkforglobal = false;
        for (PropertyInfo pinfol : pinfo) {
            if ("global.xml".equals(pinfol.getFilename())) {
                checkforglobal = true;
            }
        }
        assertTrue(checkforglobal);
        sinfo = AmbariMetaInfoTest.metaInfo.getService("HDP", "0.2", "MAPREDUCE");
        boolean checkforhadoopheapsize = false;
        pinfo = sinfo.getProperties();
        for (PropertyInfo pinfol : pinfo) {
            if ("global.xml".equals(pinfol.getFilename())) {
                if ("hadoop_heapsize".equals(pinfol.getName())) {
                    checkforhadoopheapsize = true;
                }
            }
        }
        assertTrue(checkforhadoopheapsize);
    }

    @Test
    public void testMetaInfoFileFilter() throws Exception {
        String buildDir = tmpFolder.getRoot().getAbsolutePath();
        File stackRoot = new File("src/test/resources/stacks");
        File version = new File("src/test/resources/version");
        if (System.getProperty("os.name").contains("Windows")) {
            stackRoot = new File(ClassLoader.getSystemClassLoader().getResource("stacks").getPath());
            version = new File(new File(ClassLoader.getSystemClassLoader().getResource("").getPath()).getParent(), "version");
        }
        File stackRootTmp = getStackRootTmp(buildDir);
        stackRootTmp.mkdir();
        FileUtils.copyDirectory(stackRoot, stackRootTmp);
        // todo
        // ambariMetaInfo.injector = injector;
        File f1;
        File f2;
        File f3;
        f1 = new File(((stackRootTmp.getAbsolutePath()) + "/001.svn"));
        f1.createNewFile();
        f2 = new File(((stackRootTmp.getAbsolutePath()) + "/abcd.svn/001.svn"));
        f2.mkdirs();
        f2.createNewFile();
        f3 = new File(((stackRootTmp.getAbsolutePath()) + "/.svn"));
        if (!(f3.exists())) {
            f3.createNewFile();
        }
        AmbariMetaInfo ambariMetaInfo = AmbariMetaInfoTest.createAmbariMetaInfo(stackRootTmp, version, new File(""));
        // Tests the stack is loaded as expected
        getServices();
        getComponentsByService();
        // Check .svn is not part of the stack but abcd.svn is
        junit.framework.Assert.assertNotNull(ambariMetaInfo.getStack("abcd.svn", "001.svn"));
        assertFalse(ambariMetaInfo.isSupportedStack(".svn", ""));
        assertFalse(ambariMetaInfo.isSupportedStack(".svn", ""));
    }

    @Test
    public void testGetComponent() throws Exception {
        ComponentInfo component = AmbariMetaInfoTest.metaInfo.getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_NAME_HDFS, AmbariMetaInfoTest.SERVICE_COMPONENT_NAME);
        assertEquals(component.getName(), AmbariMetaInfoTest.SERVICE_COMPONENT_NAME);
        try {
            getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_NAME_HDFS, AmbariMetaInfoTest.NON_EXT_VALUE);
        } catch (StackAccessException e) {
        }
    }

    @Test
    public void testGetRepositories() throws Exception {
        List<RepositoryInfo> repositories = getRepositories(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.OS_TYPE);
        junit.framework.Assert.assertEquals(repositories.size(), AmbariMetaInfoTest.REPOS_CNT);
    }

    @Test
    public void testGetRepository() throws Exception {
        RepositoryInfo repository = AmbariMetaInfoTest.metaInfo.getRepository(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.OS_TYPE, AmbariMetaInfoTest.REPO_ID);
        assertEquals(repository.getRepoId(), AmbariMetaInfoTest.REPO_ID);
        try {
            AmbariMetaInfoTest.metaInfo.getRepository(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.OS_TYPE, AmbariMetaInfoTest.NON_EXT_VALUE);
        } catch (StackAccessException e) {
        }
    }

    @Test
    public void testGetService() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_NAME_HDFS);
        assertEquals(service.getName(), AmbariMetaInfoTest.SERVICE_NAME_HDFS);
        try {
            AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.NON_EXT_VALUE);
        } catch (StackAccessException e) {
        }
    }

    @Test
    public void testGetStacks() {
        // Collection<StackInfo> stacks = metaInfo.getStacks();
        // todo: complete test
    }

    @Test
    public void testGetStackInfo() throws Exception {
        StackInfo stackInfo = getStack(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP);
        assertEquals(stackInfo.getName(), AmbariMetaInfoTest.STACK_NAME_HDP);
        assertEquals(stackInfo.getVersion(), AmbariMetaInfoTest.STACK_VERSION_HDP);
        try {
            AmbariMetaInfoTest.metaInfo.getStack(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.NON_EXT_VALUE);
        } catch (StackAccessException e) {
        }
    }

    @Test
    public void testGetStackParentVersions() throws Exception {
        List<String> parents = getStackParentVersions(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8");
        junit.framework.Assert.assertEquals(3, parents.size());
        assertEquals("2.0.7", parents.get(0));
        assertEquals("2.0.6", parents.get(1));
        assertEquals("2.0.5", parents.get(2));
    }

    @Test
    public void testGetProperties() throws Exception {
        Set<PropertyInfo> properties = getServiceProperties(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_NAME_HDFS);
        junit.framework.Assert.assertEquals(properties.size(), AmbariMetaInfoTest.PROPERTIES_CNT);
    }

    @Test
    public void testGetPropertiesNoName() throws Exception {
        Set<PropertyInfo> properties = getPropertiesByName(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_NAME_HDFS, AmbariMetaInfoTest.PROPERTY_NAME);
        junit.framework.Assert.assertEquals(1, properties.size());
        for (PropertyInfo propertyInfo : properties) {
            assertEquals(AmbariMetaInfoTest.PROPERTY_NAME, propertyInfo.getName());
            assertEquals(AmbariMetaInfoTest.FILE_NAME, propertyInfo.getFilename());
        }
        try {
            AmbariMetaInfoTest.metaInfo.getPropertiesByName(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.SERVICE_NAME_HDFS, AmbariMetaInfoTest.NON_EXT_VALUE);
        } catch (StackAccessException e) {
        }
    }

    @Test
    public void testGetPropertiesSharedName() throws Exception {
        Set<PropertyInfo> properties = getPropertiesByName(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP_02, AmbariMetaInfoTest.SERVICE_NAME_HDFS, AmbariMetaInfoTest.SHARED_PROPERTY_NAME);
        junit.framework.Assert.assertEquals(2, properties.size());
        for (PropertyInfo propertyInfo : properties) {
            assertEquals(AmbariMetaInfoTest.SHARED_PROPERTY_NAME, propertyInfo.getName());
            assertTrue(((propertyInfo.getFilename().equals(AmbariMetaInfoTest.HADOOP_ENV_FILE_NAME)) || (propertyInfo.getFilename().equals(AmbariMetaInfoTest.HDFS_LOG4J_FILE_NAME))));
        }
    }

    @Test
    public void testGetOperatingSystems() throws Exception {
        Set<OperatingSystemInfo> operatingSystems = getOperatingSystems(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP);
        junit.framework.Assert.assertEquals(AmbariMetaInfoTest.OS_CNT, operatingSystems.size());
    }

    @Test
    public void testGetOperatingSystem() throws Exception {
        OperatingSystemInfo operatingSystem = getOperatingSystem(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.OS_TYPE);
        assertEquals(operatingSystem.getOsType(), AmbariMetaInfoTest.OS_TYPE);
        try {
            AmbariMetaInfoTest.metaInfo.getOperatingSystem(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP, AmbariMetaInfoTest.NON_EXT_VALUE);
        } catch (StackAccessException e) {
        }
    }

    @Test
    public void isOsSupported() throws Exception {
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("redhat5"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("centos5"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("oraclelinux5"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("redhat6"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("centos6"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("oraclelinux6"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("suse11"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("sles11"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("ubuntu12"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("win2008server6"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("win2008serverr26"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("win2012server6"));
        assertTrue(AmbariMetaInfoTest.metaInfo.isOsSupported("win2012serverr26"));
    }

    @Test
    public void testExtendedStackDefinition() throws Exception {
        StackInfo stackInfo = getStack(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.EXT_STACK_NAME);
        assertTrue((stackInfo != null));
        Collection<ServiceInfo> serviceInfos = stackInfo.getServices();
        assertFalse(serviceInfos.isEmpty());
        assertTrue(((serviceInfos.size()) > 1));
        ServiceInfo deletedService = null;
        ServiceInfo redefinedService = null;
        for (ServiceInfo serviceInfo : serviceInfos) {
            if (serviceInfo.getName().equals("SQOOP")) {
                deletedService = serviceInfo;
            }
            if (serviceInfo.getName().equals("YARN")) {
                redefinedService = serviceInfo;
            }
        }
        assertNull(("SQOOP is a deleted service, should not be a part of " + "the extended stack."), deletedService);
        junit.framework.Assert.assertNotNull(redefinedService);
        // Components
        junit.framework.Assert.assertEquals(("YARN service is expected to be defined with 4 active" + " components."), 4, redefinedService.getComponents().size());
        junit.framework.Assert.assertEquals(("TEZ is expected to be a part of extended stack " + "definition"), "TEZ", redefinedService.getClientComponent().getName());
        junit.framework.Assert.assertFalse("YARN CLIENT is a deleted component.", redefinedService.getClientComponent().getName().equals("YARN_CLIENT"));
        // Properties
        junit.framework.Assert.assertNotNull(redefinedService.getProperties());
        assertTrue(((redefinedService.getProperties().size()) > 4));
        PropertyInfo deleteProperty1 = null;
        PropertyInfo deleteProperty2 = null;
        PropertyInfo redefinedProperty1 = null;
        PropertyInfo redefinedProperty2 = null;
        PropertyInfo redefinedProperty3 = null;
        PropertyInfo inheritedProperty = null;
        PropertyInfo newProperty = null;
        PropertyInfo newEnhancedProperty = null;
        PropertyInfo propertyWithExtraValueAttributes = null;
        PropertyInfo originalProperty = null;
        PropertyDependencyInfo propertyDependencyInfo = new PropertyDependencyInfo("yarn-site", "new-enhanced-yarn-property");
        for (PropertyInfo propertyInfo : redefinedService.getProperties()) {
            if (propertyInfo.getName().equals("yarn.resourcemanager.resource-tracker.address")) {
                deleteProperty1 = propertyInfo;
            } else
                if (propertyInfo.getName().equals("yarn.resourcemanager.scheduler.address")) {
                    deleteProperty2 = propertyInfo;
                } else
                    if (propertyInfo.getName().equals("yarn.resourcemanager.address")) {
                        redefinedProperty1 = propertyInfo;
                    } else
                        if (propertyInfo.getName().equals("yarn.resourcemanager.admin.address")) {
                            redefinedProperty2 = propertyInfo;
                        } else
                            if (propertyInfo.getName().equals("yarn.nodemanager.health-checker.interval-ms")) {
                                redefinedProperty3 = propertyInfo;
                            } else
                                if (propertyInfo.getName().equals("yarn.nodemanager.address")) {
                                    inheritedProperty = propertyInfo;
                                } else
                                    if (propertyInfo.getName().equals("new-yarn-property")) {
                                        newProperty = propertyInfo;
                                    } else
                                        if (propertyInfo.getName().equals("new-enhanced-yarn-property")) {
                                            newEnhancedProperty = propertyInfo;
                                        } else
                                            if (propertyInfo.getName().equals("yarn.nodemanager.aux-services")) {
                                                originalProperty = propertyInfo;
                                            } else
                                                if (propertyInfo.getName().equals("property.with.extra.value.attributes")) {
                                                    propertyWithExtraValueAttributes = propertyInfo;
                                                }









        }
        junit.framework.Assert.assertNull(deleteProperty1);
        junit.framework.Assert.assertNull(deleteProperty2);
        junit.framework.Assert.assertNotNull(redefinedProperty1);
        junit.framework.Assert.assertNotNull(redefinedProperty2);
        assertNotNull(("yarn.nodemanager.address expected to be inherited " + "from parent"), inheritedProperty);
        assertEquals("localhost:100009", redefinedProperty1.getValue());
        // Parent property value will result in property being present in the child stack
        junit.framework.Assert.assertNotNull(redefinedProperty3);
        assertEquals("135000", redefinedProperty3.getValue());
        // Child can override parent property to empty value
        assertEquals("", redefinedProperty2.getValue());
        // New property
        junit.framework.Assert.assertNotNull(newProperty);
        assertEquals("some-value", newProperty.getValue());
        assertEquals("some description.", newProperty.getDescription());
        assertEquals("yarn-site.xml", newProperty.getFilename());
        junit.framework.Assert.assertEquals(1, newProperty.getDependedByProperties().size());
        assertTrue(newProperty.getDependedByProperties().contains(propertyDependencyInfo));
        // New enhanced property
        junit.framework.Assert.assertNotNull(newEnhancedProperty);
        assertEquals("1024", newEnhancedProperty.getValue());
        assertEquals("some enhanced description.", newEnhancedProperty.getDescription());
        assertEquals("yarn-site.xml", newEnhancedProperty.getFilename());
        junit.framework.Assert.assertEquals(2, newEnhancedProperty.getDependsOnProperties().size());
        assertTrue(newEnhancedProperty.getDependsOnProperties().contains(new PropertyDependencyInfo("yarn-site", "new-yarn-property")));
        assertTrue(newEnhancedProperty.getDependsOnProperties().contains(new PropertyDependencyInfo("global", "yarn_heapsize")));
        assertEquals("MB", newEnhancedProperty.getPropertyValueAttributes().getUnit());
        assertEquals("int", newEnhancedProperty.getPropertyValueAttributes().getType());
        assertEquals("512", newEnhancedProperty.getPropertyValueAttributes().getMinimum());
        assertEquals("15360", newEnhancedProperty.getPropertyValueAttributes().getMaximum());
        assertEquals("256", newEnhancedProperty.getPropertyValueAttributes().getIncrementStep());
        junit.framework.Assert.assertNull(newEnhancedProperty.getPropertyValueAttributes().getEntries());
        junit.framework.Assert.assertNull(newEnhancedProperty.getPropertyValueAttributes().getEntriesEditable());
        // property with extra value attributes
        assertTrue(propertyWithExtraValueAttributes.getPropertyValueAttributes().getEmptyValueValid());
        assertTrue(propertyWithExtraValueAttributes.getPropertyValueAttributes().getVisible());
        assertTrue(propertyWithExtraValueAttributes.getPropertyValueAttributes().getReadOnly());
        junit.framework.Assert.assertEquals(Boolean.FALSE, propertyWithExtraValueAttributes.getPropertyValueAttributes().getEditableOnlyAtInstall());
        junit.framework.Assert.assertEquals(Boolean.FALSE, propertyWithExtraValueAttributes.getPropertyValueAttributes().getOverridable());
        junit.framework.Assert.assertEquals(Boolean.FALSE, propertyWithExtraValueAttributes.getPropertyValueAttributes().getShowPropertyName());
        // Original property
        junit.framework.Assert.assertNotNull(originalProperty);
        assertEquals("mapreduce.shuffle", originalProperty.getValue());
        assertEquals("Auxilliary services of NodeManager", originalProperty.getDescription());
        junit.framework.Assert.assertEquals(6, redefinedService.getConfigDependencies().size());
        junit.framework.Assert.assertEquals(7, redefinedService.getConfigDependenciesWithComponents().size());
    }

    @Test
    public void testPropertyCount() throws Exception {
        Set<PropertyInfo> properties = getServiceProperties(AmbariMetaInfoTest.STACK_NAME_HDP, AmbariMetaInfoTest.STACK_VERSION_HDP_02, AmbariMetaInfoTest.SERVICE_NAME_HDFS);
        // 3 empty properties
        junit.framework.Assert.assertEquals(103, properties.size());
    }

    @Test
    public void testBadStack() throws Exception {
        File stackRoot = new File("src/test/resources/bad-stacks");
        File version = new File("src/test/resources/version");
        if (System.getProperty("os.name").contains("Windows")) {
            stackRoot = new File(ClassLoader.getSystemClassLoader().getResource("bad-stacks").getPath());
            version = new File(new File(ClassLoader.getSystemClassLoader().getResource("").getPath()).getParent(), "version");
        }
        AmbariMetaInfoTest.LOG.info(("Stacks file " + (stackRoot.getAbsolutePath())));
        AmbariMetaInfoTest.TestAmbariMetaInfo ambariMetaInfo = AmbariMetaInfoTest.createAmbariMetaInfo(stackRoot, version, new File(""));
        junit.framework.Assert.assertEquals(1, getStackManager().getStacks().size());
        junit.framework.Assert.assertEquals(false, getStackManager().getStack("HDP", "0.1").isValid());
        junit.framework.Assert.assertEquals(2, getStackManager().getStack("HDP", "0.1").getErrors().size());
    }

    @Test
    public void testMetricsJson() throws Exception {
        ServiceInfo svc = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.5", "HDFS");
        junit.framework.Assert.assertNotNull(svc);
        junit.framework.Assert.assertNotNull(svc.getMetricsFile());
        svc = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.6", "HDFS");
        junit.framework.Assert.assertNotNull(svc);
        junit.framework.Assert.assertNotNull(svc.getMetricsFile());
        List<MetricDefinition> list = AmbariMetaInfoTest.metaInfo.getMetrics(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.5", "HDFS", AmbariMetaInfoTest.SERVICE_COMPONENT_NAME, Component.name());
        junit.framework.Assert.assertNotNull(list);
        AmbariMetaInfoTest.checkNoAggregatedFunctionsForJmx(list);
        list = AmbariMetaInfoTest.metaInfo.getMetrics(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.5", "HDFS", "DATANODE", Component.name());
        junit.framework.Assert.assertNull(list);
        List<MetricDefinition> list0 = AmbariMetaInfoTest.metaInfo.getMetrics(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.5", "HDFS", "DATANODE", Component.name());
        junit.framework.Assert.assertNull(list0);
        junit.framework.Assert.assertTrue("Expecting subsequent calls to use a cached value for the definition", (list == list0));
        // not explicitly defined, uses 2.0.5
        list = AmbariMetaInfoTest.metaInfo.getMetrics(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.6", "HDFS", "DATANODE", Component.name());
        junit.framework.Assert.assertNull(list);
    }

    @Test
    public void testKerberosJson() throws Exception {
        ServiceInfo svc;
        svc = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HDFS");
        junit.framework.Assert.assertNotNull(svc);
        File kerberosDescriptorFile1 = svc.getKerberosDescriptorFile();
        junit.framework.Assert.assertNotNull(kerberosDescriptorFile1);
        assertTrue(kerberosDescriptorFile1.exists());
        svc = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.1.1", "HDFS");
        junit.framework.Assert.assertNotNull(svc);
        File kerberosDescriptorFile2 = svc.getKerberosDescriptorFile();
        junit.framework.Assert.assertNotNull(kerberosDescriptorFile1);
        assertTrue(kerberosDescriptorFile1.exists());
        junit.framework.Assert.assertEquals(kerberosDescriptorFile1, kerberosDescriptorFile2);
        svc = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HDFS");
        junit.framework.Assert.assertNotNull(svc);
        File kerberosDescriptorFile3 = svc.getKerberosDescriptorFile();
        junit.framework.Assert.assertNull(kerberosDescriptorFile3);
    }

    @Test
    public void testGanglia134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "GANGLIA");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(2, componentList.size());
        for (ComponentInfo component : componentList) {
            String name = component.getName();
            if (name.equals("GANGLIA_SERVER")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("GANGLIA_MONITOR")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                assertTrue(component.getAutoDeploy().isEnabled());
                // cardinality
                assertEquals("ALL", component.getCardinality());
            }
        }
    }

    @Test
    public void testHBase134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "HBASE");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(3, componentList.size());
        for (ComponentInfo component : componentList) {
            String name = component.getName();
            if (name.equals("HBASE_MASTER")) {
                // dependencies
                List<DependencyInfo> dependencyList = component.getDependencies();
                junit.framework.Assert.assertEquals(2, dependencyList.size());
                for (DependencyInfo dependency : dependencyList) {
                    if (dependency.getName().equals("HDFS/HDFS_CLIENT")) {
                        assertEquals("host", dependency.getScope());
                        junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
                    } else
                        if (dependency.getName().equals("ZOOKEEPER/ZOOKEEPER_SERVER")) {
                            assertEquals("cluster", dependency.getScope());
                            AutoDeployInfo autoDeploy = dependency.getAutoDeploy();
                            junit.framework.Assert.assertEquals(true, autoDeploy.isEnabled());
                            assertEquals("HBASE/HBASE_MASTER", autoDeploy.getCoLocate());
                        } else {
                            fail("Unexpected dependency");
                        }

                }
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("HBASE_REGIONSERVER")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1+", component.getCardinality());
            }
            if (name.equals("HBASE_CLIENT")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("0+", component.getCardinality());
            }
        }
    }

    @Test
    public void testHDFS134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "HDFS");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(4, componentList.size());
        for (ComponentInfo component : componentList) {
            String name = component.getName();
            if (name.equals("NAMENODE")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("DATANODE")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1+", component.getCardinality());
            }
            if (name.equals("SECONDARY_NAMENODE")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("HDFS_CLIENT")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("0+", component.getCardinality());
            }
        }
    }

    @Test
    public void testHive134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "HIVE");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(4, componentList.size());
        for (ComponentInfo component : componentList) {
            String name = component.getName();
            if (name.equals("HIVE_METASTORE")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                AutoDeployInfo autoDeploy = component.getAutoDeploy();
                assertTrue(autoDeploy.isEnabled());
                assertEquals("HIVE/HIVE_SERVER", autoDeploy.getCoLocate());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("HIVE_SERVER")) {
                // dependencies
                List<DependencyInfo> dependencyList = component.getDependencies();
                junit.framework.Assert.assertEquals(1, dependencyList.size());
                DependencyInfo dependency = dependencyList.get(0);
                assertEquals("ZOOKEEPER/ZOOKEEPER_SERVER", dependency.getName());
                assertEquals("cluster", dependency.getScope());
                AutoDeployInfo autoDeploy = dependency.getAutoDeploy();
                assertTrue(autoDeploy.isEnabled());
                assertEquals("HIVE/HIVE_SERVER", autoDeploy.getCoLocate());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("MYSQL_SERVER")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                AutoDeployInfo autoDeploy = component.getAutoDeploy();
                assertTrue(autoDeploy.isEnabled());
                assertEquals("HIVE/HIVE_SERVER", autoDeploy.getCoLocate());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("HIVE_CLIENT")) {
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("0+", component.getCardinality());
            }
        }
    }

    @Test
    public void testHue134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "HUE");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(1, componentList.size());
        ComponentInfo component = componentList.get(0);
        assertEquals("HUE_SERVER", component.getName());
        // dependencies
        junit.framework.Assert.assertEquals(0, component.getDependencies().size());
        // component auto deploy
        junit.framework.Assert.assertNull(component.getAutoDeploy());
        // cardinality
        assertEquals("1", component.getCardinality());
    }

    @Test
    public void testMapReduce134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "MAPREDUCE");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(4, componentList.size());
        for (ComponentInfo component : componentList) {
            String name = component.getName();
            if (name.equals("JOBTRACKER")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("TASKTRACKER")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1+", component.getCardinality());
            }
            if (name.equals("HISTORYSERVER")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                AutoDeployInfo autoDeploy = component.getAutoDeploy();
                assertTrue(autoDeploy.isEnabled());
                assertEquals("MAPREDUCE/JOBTRACKER", autoDeploy.getCoLocate());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("MAPREDUCE_CLIENT")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("0+", component.getCardinality());
            }
        }
    }

    @Test
    public void testOozie134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "OOZIE");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(2, componentList.size());
        for (ComponentInfo component : componentList) {
            String name = component.getName();
            if (name.equals("OOZIE_SERVER")) {
                // dependencies
                List<DependencyInfo> dependencyList = component.getDependencies();
                junit.framework.Assert.assertEquals(2, dependencyList.size());
                for (DependencyInfo dependency : dependencyList) {
                    if (dependency.getName().equals("HDFS/HDFS_CLIENT")) {
                        assertEquals("host", dependency.getScope());
                        junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
                    } else
                        if (dependency.getName().equals("MAPREDUCE/MAPREDUCE_CLIENT")) {
                            assertEquals("host", dependency.getScope());
                            junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
                        } else {
                            fail("Unexpected dependency");
                        }

                }
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("OOZIE_CLIENT")) {
                // dependencies
                List<DependencyInfo> dependencyList = component.getDependencies();
                junit.framework.Assert.assertEquals(2, dependencyList.size());
                for (DependencyInfo dependency : dependencyList) {
                    if (dependency.getName().equals("HDFS/HDFS_CLIENT")) {
                        assertEquals("host", dependency.getScope());
                        junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
                    } else
                        if (dependency.getName().equals("MAPREDUCE/MAPREDUCE_CLIENT")) {
                            assertEquals("host", dependency.getScope());
                            junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
                        } else {
                            fail("Unexpected dependency");
                        }

                }
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("0+", component.getCardinality());
            }
        }
    }

    @Test
    public void testPig134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "PIG");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(1, componentList.size());
        ComponentInfo component = componentList.get(0);
        assertEquals("PIG", component.getName());
        // dependencies
        junit.framework.Assert.assertEquals(0, component.getDependencies().size());
        // component auto deploy
        junit.framework.Assert.assertNull(component.getAutoDeploy());
        // cardinality
        assertEquals("0+", component.getCardinality());
    }

    @Test
    public void testSqoop134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "SQOOP");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(1, componentList.size());
        ComponentInfo component = componentList.get(0);
        assertEquals("SQOOP", component.getName());
        // dependencies
        List<DependencyInfo> dependencyList = component.getDependencies();
        junit.framework.Assert.assertEquals(2, dependencyList.size());
        for (DependencyInfo dependency : dependencyList) {
            if (dependency.getName().equals("HDFS/HDFS_CLIENT")) {
                assertEquals("host", dependency.getScope());
                junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
            } else
                if (dependency.getName().equals("MAPREDUCE/MAPREDUCE_CLIENT")) {
                    assertEquals("host", dependency.getScope());
                    junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
                } else {
                    fail("Unexpected dependency");
                }

        }
        // component auto deploy
        junit.framework.Assert.assertNull(component.getAutoDeploy());
        // cardinality
        assertEquals("0+", component.getCardinality());
    }

    @Test
    public void testWebHCat134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "WEBHCAT");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(1, componentList.size());
        ComponentInfo component = componentList.get(0);
        assertEquals("WEBHCAT_SERVER", component.getName());
        // dependencies
        List<DependencyInfo> dependencyList = component.getDependencies();
        junit.framework.Assert.assertEquals(4, dependencyList.size());
        for (DependencyInfo dependency : dependencyList) {
            if (dependency.getName().equals("HDFS/HDFS_CLIENT")) {
                assertEquals("host", dependency.getScope());
                junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
            } else
                if (dependency.getName().equals("MAPREDUCE/MAPREDUCE_CLIENT")) {
                    assertEquals("host", dependency.getScope());
                    junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
                } else
                    if (dependency.getName().equals("ZOOKEEPER/ZOOKEEPER_SERVER")) {
                        assertEquals("cluster", dependency.getScope());
                        AutoDeployInfo autoDeploy = dependency.getAutoDeploy();
                        junit.framework.Assert.assertEquals(true, autoDeploy.isEnabled());
                        assertEquals("WEBHCAT/WEBHCAT_SERVER", autoDeploy.getCoLocate());
                    } else
                        if (dependency.getName().equals("ZOOKEEPER/ZOOKEEPER_CLIENT")) {
                            assertEquals("host", dependency.getScope());
                            junit.framework.Assert.assertEquals(true, dependency.getAutoDeploy().isEnabled());
                        } else {
                            fail("Unexpected dependency");
                        }



        }
        // component auto deploy
        junit.framework.Assert.assertNull(component.getAutoDeploy());
        // cardinality
        assertEquals("1", component.getCardinality());
    }

    @Test
    public void testZooKeeper134Dependencies() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "ZOOKEEPER");
        List<ComponentInfo> componentList = service.getComponents();
        junit.framework.Assert.assertEquals(2, componentList.size());
        for (ComponentInfo component : componentList) {
            String name = component.getName();
            if (name.equals("ZOOKEEPER_SERVER")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("1", component.getCardinality());
            }
            if (name.equals("ZOOKEEPER_CLIENT")) {
                // dependencies
                junit.framework.Assert.assertEquals(0, component.getDependencies().size());
                // component auto deploy
                junit.framework.Assert.assertNull(component.getAutoDeploy());
                // cardinality
                assertEquals("0+", component.getCardinality());
            }
        }
    }

    @Test
    public void testServicePackageDirInheritance() throws Exception {
        String assertionTemplate07 = StringUtils.join(new String[]{ "stacks", "HDP", "2.0.7", "services", "%s", "package" }, File.separator);
        String assertionTemplate08 = StringUtils.join(new String[]{ "stacks", "HDP", "2.0.8", "services", "%s", "package" }, File.separator);
        // Test service package dir determination in parent
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HBASE");
        assertEquals(String.format(assertionTemplate07, "HBASE"), service.getServicePackageFolder());
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HDFS");
        assertEquals(String.format(assertionTemplate07, "HDFS"), service.getServicePackageFolder());
        // Test service package dir inheritance
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HBASE");
        assertEquals(String.format(assertionTemplate07, "HBASE"), service.getServicePackageFolder());
        // Test service package dir override
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HDFS");
        assertEquals(String.format(assertionTemplate08, "HDFS"), service.getServicePackageFolder());
    }

    @Test
    public void testServiceCommandScriptInheritance() throws Exception {
        // Test command script determination in parent
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HDFS");
        assertEquals("scripts/service_check_1.py", service.getCommandScript().getScript());
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HBASE");
        assertEquals("scripts/service_check.py", service.getCommandScript().getScript());
        // Test command script inheritance
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HBASE");
        assertEquals("scripts/service_check.py", service.getCommandScript().getScript());
        // Test command script override
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HDFS");
        assertEquals("scripts/service_check_2.py", service.getCommandScript().getScript());
    }

    @Test
    public void testComponentCommandScriptInheritance() throws Exception {
        // Test command script determination in parent
        ComponentInfo component = AmbariMetaInfoTest.metaInfo.getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HDFS", "HDFS_CLIENT");
        assertEquals("scripts/hdfs_client.py", component.getCommandScript().getScript());
        component = getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HBASE", "HBASE_MASTER");
        assertEquals("scripts/hbase_master.py", component.getCommandScript().getScript());
        // Test command script inheritance
        component = getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HBASE", "HBASE_MASTER");
        assertEquals("scripts/hbase_master.py", component.getCommandScript().getScript());
        // Test command script override
        component = getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HDFS", "HDFS_CLIENT");
        assertEquals("scripts/hdfs_client_overridden.py", component.getCommandScript().getScript());
    }

    @Test
    public void testServiceCustomCommandScriptInheritance() throws Exception {
        // Test custom command script determination in parent
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HDFS");
        CustomCommandDefinition ccd = findCustomCommand("RESTART", service);
        assertEquals("scripts/restart_parent.py", ccd.getCommandScript().getScript());
        ccd = findCustomCommand("YET_ANOTHER_PARENT_SRV_COMMAND", service);
        assertEquals("scripts/yet_another_parent_srv_command.py", ccd.getCommandScript().getScript());
        junit.framework.Assert.assertEquals(2, service.getCustomCommands().size());
        // Test custom command script inheritance
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HDFS");
        junit.framework.Assert.assertEquals(3, service.getCustomCommands().size());
        ccd = findCustomCommand("YET_ANOTHER_PARENT_SRV_COMMAND", service);
        assertEquals("scripts/yet_another_parent_srv_command.py", ccd.getCommandScript().getScript());
        // Test custom command script override
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HDFS");
        ccd = findCustomCommand("RESTART", service);
        assertEquals("scripts/restart_child.py", ccd.getCommandScript().getScript());
        ccd = findCustomCommand("YET_ANOTHER_CHILD_SRV_COMMAND", service);
        assertEquals("scripts/yet_another_child_srv_command.py", ccd.getCommandScript().getScript());
    }

    @Test
    public void testChildCustomCommandScriptInheritance() throws Exception {
        // Test custom command script determination in parent
        ComponentInfo component = AmbariMetaInfoTest.metaInfo.getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HDFS", "NAMENODE");
        CustomCommandDefinition ccd = findCustomCommand("DECOMMISSION", component);
        assertEquals("scripts/namenode_dec.py", ccd.getCommandScript().getScript());
        ccd = findCustomCommand("YET_ANOTHER_PARENT_COMMAND", component);
        assertEquals("scripts/yet_another_parent_command.py", ccd.getCommandScript().getScript());
        ccd = findCustomCommand("REBALANCEHDFS", component);
        assertEquals("scripts/namenode.py", ccd.getCommandScript().getScript());
        assertTrue(ccd.isBackground());
        junit.framework.Assert.assertEquals(3, component.getCustomCommands().size());
        // Test custom command script inheritance
        component = getComponent(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HDFS", "NAMENODE");
        junit.framework.Assert.assertEquals(4, component.getCustomCommands().size());
        ccd = findCustomCommand("YET_ANOTHER_PARENT_COMMAND", component);
        assertEquals("scripts/yet_another_parent_command.py", ccd.getCommandScript().getScript());
        // Test custom command script override
        ccd = findCustomCommand("DECOMMISSION", component);
        assertEquals("scripts/namenode_dec_overr.py", ccd.getCommandScript().getScript());
        ccd = findCustomCommand("YET_ANOTHER_CHILD_COMMAND", component);
        assertEquals("scripts/yet_another_child_command.py", ccd.getCommandScript().getScript());
    }

    @Test
    public void testServiceOsSpecificsInheritance() throws Exception {
        // Test command script determination in parent
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HDFS");
        assertEquals("parent-package-def", service.getOsSpecifics().get("any").getPackages().get(0).getName());
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "HBASE");
        junit.framework.Assert.assertEquals(2, service.getOsSpecifics().keySet().size());
        // Test command script inheritance
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HBASE");
        junit.framework.Assert.assertEquals(2, service.getOsSpecifics().keySet().size());
        // Test command script override
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HDFS");
        assertEquals("child-package-def", service.getOsSpecifics().get("any").getPackages().get(0).getName());
    }

    @Test
    public void testServiceSchemaVersionInheritance() throws Exception {
        // Check parent
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "SQOOP");
        assertEquals("2.0", service.getSchemaVersion());
        // Test child service metainfo after merge
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "SQOOP");
        assertEquals("2.0", service.getSchemaVersion());
    }

    @Test
    public void testCustomConfigDir() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.7", "MAPREDUCE2");
        // assert that the property was found in a differently-named directory
        // cannot check the dirname itself since extended stacks won't carry over
        // the name
        boolean found = false;
        for (PropertyInfo pi : service.getProperties()) {
            if (pi.getName().equals("mr2-prop")) {
                assertEquals("some-mr2-value", pi.getValue());
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testLatestRepo() throws Exception {
        // ensure that all of the latest repo retrieval tasks have completed
        StackManager sm = AmbariMetaInfoTest.metaInfo.getStackManager();
        int maxWait = 45000;
        int waitTime = 0;
        while ((waitTime < maxWait) && (!(sm.haveAllRepoUrlsBeenResolved()))) {
            Thread.sleep(5);
            waitTime += 5;
        } 
        if (waitTime >= maxWait) {
            Assert.fail("Latest Repo tasks did not complete");
        }
        for (RepositoryInfo ri : AmbariMetaInfoTest.metaInfo.getRepositories("HDP", "2.1.1", "centos6")) {
            junit.framework.Assert.assertEquals("Expected the default URL to be the same as in the xml file", "http://public-repo-1.hortonworks.com/HDP/centos6/2.x/updates/2.0.6.0", ri.getDefaultBaseUrl());
        }
    }

    @Test
    public void testLatestVdf() throws Exception {
        // ensure that all of the latest repo retrieval tasks have completed
        StackManager sm = AmbariMetaInfoTest.metaInfo.getStackManager();
        int maxWait = 45000;
        int waitTime = 0;
        while ((waitTime < maxWait) && (!(sm.haveAllRepoUrlsBeenResolved()))) {
            Thread.sleep(5);
            waitTime += 5;
        } 
        if (waitTime >= maxWait) {
            Assert.fail("Latest Repo tasks did not complete");
        }
        // !!! default stack version is from latest-vdf.  2.2.0 only has one entry
        VersionDefinitionXml vdf = getVersionDefinition("HDP-2.2.0");
        Assert.assertNotNull(vdf);
        Assert.assertEquals(1, vdf.repositoryInfo.getOses().size());
        // !!! this stack has no "manifests" and no "latest-vdf".  So the default VDF should contain
        // information from repoinfo.xml and the "latest" structure
        vdf = AmbariMetaInfoTest.metaInfo.getVersionDefinition("HDP-2.2.1");
        Assert.assertNotNull(vdf);
        Assert.assertEquals(2, vdf.repositoryInfo.getOses().size());
    }

    @Test
    public void testGetComponentDependency() throws AmbariException {
        DependencyInfo dependency = getComponentDependency("HDP", "1.3.4", "HIVE", "HIVE_SERVER", "ZOOKEEPER_SERVER");
        Assert.assertEquals("ZOOKEEPER/ZOOKEEPER_SERVER", dependency.getName());
        Assert.assertEquals("ZOOKEEPER_SERVER", dependency.getComponentName());
        Assert.assertEquals("ZOOKEEPER", dependency.getServiceName());
        Assert.assertEquals("cluster", dependency.getScope());
    }

    @Test
    public void testGetComponentDependencies() throws AmbariException {
        List<DependencyInfo> dependencies = getComponentDependencies("HDP", "1.3.4", "HBASE", "HBASE_MASTER");
        Assert.assertEquals(2, dependencies.size());
        DependencyInfo dependency = dependencies.get(0);
        Assert.assertEquals("HDFS/HDFS_CLIENT", dependency.getName());
        Assert.assertEquals("HDFS_CLIENT", dependency.getComponentName());
        Assert.assertEquals("HDFS", dependency.getServiceName());
        Assert.assertEquals("host", dependency.getScope());
        dependency = dependencies.get(1);
        Assert.assertEquals("ZOOKEEPER/ZOOKEEPER_SERVER", dependency.getName());
        Assert.assertEquals("ZOOKEEPER_SERVER", dependency.getComponentName());
        Assert.assertEquals("ZOOKEEPER", dependency.getServiceName());
        Assert.assertEquals("cluster", dependency.getScope());
    }

    @Test
    public void testPasswordPropertyAttribute() throws Exception {
        ServiceInfo service = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.1", "HIVE");
        List<PropertyInfo> propertyInfoList = service.getProperties();
        junit.framework.Assert.assertNotNull(propertyInfoList);
        PropertyInfo passwordProperty = null;
        for (PropertyInfo propertyInfo : propertyInfoList) {
            if ((propertyInfo.isRequireInput()) && (propertyInfo.getPropertyTypes().contains(PASSWORD))) {
                passwordProperty = propertyInfo;
            } else {
                assertTrue(propertyInfo.getPropertyTypes().isEmpty());
            }
        }
        junit.framework.Assert.assertNotNull(passwordProperty);
        assertEquals("javax.jdo.option.ConnectionPassword", passwordProperty.getName());
    }

    @Test
    public void testAlertsJson() throws Exception {
        ServiceInfo svc = getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.5", "HDFS");
        junit.framework.Assert.assertNotNull(svc);
        junit.framework.Assert.assertNotNull(svc.getAlertsFile());
        svc = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.6", "HDFS");
        junit.framework.Assert.assertNotNull(svc);
        junit.framework.Assert.assertNotNull(svc.getAlertsFile());
        svc = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "1.3.4", "HDFS");
        junit.framework.Assert.assertNotNull(svc);
        junit.framework.Assert.assertNull(svc.getAlertsFile());
        Set<AlertDefinition> set = getAlertDefinitions(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.5", "HDFS");
        junit.framework.Assert.assertNotNull(set);
        assertTrue(((set.size()) > 0));
        // find two different definitions and test each one
        AlertDefinition nameNodeProcess = null;
        AlertDefinition nameNodeCpu = null;
        AlertDefinition datanodeStorage = null;
        AlertDefinition ignoreHost = null;
        Iterator<AlertDefinition> iterator = set.iterator();
        while (iterator.hasNext()) {
            AlertDefinition definition = iterator.next();
            if (definition.getName().equals("namenode_process")) {
                nameNodeProcess = definition;
            }
            if (definition.getName().equals("namenode_cpu")) {
                nameNodeCpu = definition;
            }
            if (definition.getName().equals("datanode_storage")) {
                datanodeStorage = definition;
            }
            if (definition.getName().equals("hdfs_ignore_host_test")) {
                ignoreHost = definition;
            }
        } 
        Assert.assertNotNull(nameNodeProcess);
        Assert.assertNotNull(nameNodeCpu);
        Assert.assertNotNull(ignoreHost);
        Assert.assertEquals("NameNode Host CPU Utilization", nameNodeCpu.getLabel());
        // test namenode_process
        Assert.assertFalse(nameNodeProcess.isHostIgnored());
        Assert.assertEquals("A description of namenode_process", nameNodeProcess.getDescription());
        Source source = nameNodeProcess.getSource();
        Assert.assertNotNull(source);
        Assert.assertNotNull(getPort());
        Reporting reporting = source.getReporting();
        Assert.assertNotNull(reporting);
        Assert.assertNotNull(reporting.getOk());
        Assert.assertNotNull(reporting.getOk().getText());
        Assert.assertNull(reporting.getOk().getValue());
        Assert.assertNotNull(reporting.getCritical());
        Assert.assertNotNull(reporting.getCritical().getText());
        Assert.assertNull(reporting.getCritical().getValue());
        Assert.assertNull(reporting.getWarning());
        // test namenode_cpu
        Assert.assertFalse(nameNodeCpu.isHostIgnored());
        Assert.assertEquals("A description of namenode_cpu", nameNodeCpu.getDescription());
        source = nameNodeCpu.getSource();
        Assert.assertNotNull(source);
        reporting = source.getReporting();
        Assert.assertNotNull(reporting);
        Assert.assertNotNull(reporting.getOk());
        Assert.assertNotNull(reporting.getOk().getText());
        Assert.assertNull(reporting.getOk().getValue());
        Assert.assertNotNull(reporting.getCritical());
        Assert.assertNotNull(reporting.getCritical().getText());
        Assert.assertNotNull(reporting.getCritical().getValue());
        Assert.assertNotNull(reporting.getWarning());
        Assert.assertNotNull(reporting.getWarning().getText());
        Assert.assertNotNull(reporting.getWarning().getValue());
        // test a metric alert
        Assert.assertNotNull(datanodeStorage);
        Assert.assertEquals("A description of datanode_storage", datanodeStorage.getDescription());
        Assert.assertFalse(datanodeStorage.isHostIgnored());
        MetricSource metricSource = ((MetricSource) (datanodeStorage.getSource()));
        Assert.assertNotNull(metricSource.getUri());
        Assert.assertNotNull(metricSource.getUri().getHttpsProperty());
        Assert.assertNotNull(metricSource.getUri().getHttpsPropertyValue());
        Assert.assertNotNull(metricSource.getUri().getHttpsUri());
        Assert.assertNotNull(metricSource.getUri().getHttpUri());
        Assert.assertEquals(12345, metricSource.getUri().getDefaultPort().intValue());
        // 
        // ignore host
        Assert.assertTrue(ignoreHost.isHostIgnored());
    }

    // todo: refactor test to use mocks instead of injector
    /**
     * Tests merging stack-based with existing definitions works
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAlertDefinitionMerging() throws Exception {
        final String stackVersion = "2.0.6";
        final String repoVersion = "2.0.6-1234";
        Injector injector = Guice.createInjector(Modules.override(new InMemoryDefaultTestModule()).with(new AmbariMetaInfoTest.MockModule()));
        EventBusSynchronizer.synchronizeAmbariEventPublisher(injector);
        injector.getInstance(GuiceJpaInitializer.class);
        injector.getInstance(EntityManager.class);
        OrmTestHelper ormHelper = injector.getInstance(OrmTestHelper.class);
        long clusterId = ormHelper.createCluster(("cluster" + (System.currentTimeMillis())));
        Class<?> c = AmbariMetaInfoTest.metaInfo.getClass().getSuperclass();
        Field f = c.getDeclaredField("alertDefinitionDao");
        f.setAccessible(true);
        f.set(AmbariMetaInfoTest.metaInfo, injector.getInstance(AlertDefinitionDAO.class));
        f = c.getDeclaredField("ambariServiceAlertDefinitions");
        f.setAccessible(true);
        f.set(AmbariMetaInfoTest.metaInfo, injector.getInstance(AmbariServiceAlertDefinitions.class));
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = clusters.getClusterById(clusterId);
        cluster.setDesiredStackVersion(new StackId(AmbariMetaInfoTest.STACK_NAME_HDP, stackVersion));
        RepositoryVersionEntity repositoryVersion = ormHelper.getOrCreateRepositoryVersion(cluster.getCurrentStackVersion(), repoVersion);
        cluster.addService("HDFS", repositoryVersion);
        AmbariMetaInfoTest.metaInfo.reconcileAlertDefinitions(clusters, false);
        AlertDefinitionDAO dao = injector.getInstance(AlertDefinitionDAO.class);
        List<AlertDefinitionEntity> definitions = dao.findAll(clusterId);
        Assert.assertEquals(13, definitions.size());
        // figure out how many of these alerts were merged into from the
        // non-stack alerts.json
        int hostAlertCount = 0;
        for (AlertDefinitionEntity definition : definitions) {
            if ((definition.getServiceName().equals("AMBARI")) && (definition.getComponentName().equals("AMBARI_AGENT"))) {
                hostAlertCount++;
            }
        }
        Assert.assertEquals(3, hostAlertCount);
        Assert.assertEquals(10, ((definitions.size()) - hostAlertCount));
        for (AlertDefinitionEntity definition : definitions) {
            definition.setScheduleInterval(28);
            dao.merge(definition);
        }
        AmbariMetaInfoTest.metaInfo.reconcileAlertDefinitions(clusters, false);
        definitions = dao.findAll();
        Assert.assertEquals(13, definitions.size());
        for (AlertDefinitionEntity definition : definitions) {
            Assert.assertEquals(28, definition.getScheduleInterval().intValue());
        }
        // find all enabled for the cluster should find 6 (the ones from HDFS;
        // it will not find the agent alert since it's not bound to the cluster)
        definitions = dao.findAllEnabled(cluster.getClusterId());
        Assert.assertEquals(12, definitions.size());
        // create new definition
        AlertDefinitionEntity entity = new AlertDefinitionEntity();
        entity.setClusterId(clusterId);
        entity.setDefinitionName("bad_hdfs_alert");
        entity.setLabel("Bad HDFS Alert");
        entity.setDescription("A way to fake a component being removed");
        entity.setEnabled(true);
        entity.setHash(UUID.randomUUID().toString());
        entity.setScheduleInterval(1);
        entity.setServiceName("HDFS");
        entity.setComponentName("BAD_COMPONENT");
        entity.setSourceType(METRIC);
        entity.setSource("{\"type\" : \"METRIC\"}");
        dao.create(entity);
        // verify the new definition is found (6 HDFS + 1 new one)
        definitions = dao.findAllEnabled(cluster.getClusterId());
        Assert.assertEquals(13, definitions.size());
        // reconcile, which should disable our bad definition
        AmbariMetaInfoTest.metaInfo.reconcileAlertDefinitions(clusters, false);
        // find all enabled for the cluster should find 6
        definitions = dao.findAllEnabled(cluster.getClusterId());
        Assert.assertEquals(12, definitions.size());
        // find all should find 6 HDFS + 1 disabled + 1 agent alert + 2 server
        // alerts
        definitions = dao.findAll();
        Assert.assertEquals(14, definitions.size());
        entity = dao.findById(entity.getDefinitionId());
        Assert.assertFalse(entity.getEnabled());
    }

    /**
     * Test scenario when service were removed and not mapped alerts need to be disabled
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAlertDefinitionMergingRemoveScenario() throws Exception {
        final String repoVersion = "2.0.6-1234";
        final String stackVersion = "2.0.6";
        Injector injector = Guice.createInjector(Modules.override(new InMemoryDefaultTestModule()).with(new AmbariMetaInfoTest.MockModule()));
        EventBusSynchronizer.synchronizeAmbariEventPublisher(injector);
        injector.getInstance(GuiceJpaInitializer.class);
        injector.getInstance(EntityManager.class);
        OrmTestHelper ormHelper = injector.getInstance(OrmTestHelper.class);
        long clusterId = ormHelper.createCluster(("cluster" + (System.currentTimeMillis())));
        Class<?> c = AmbariMetaInfoTest.metaInfo.getClass().getSuperclass();
        Field f = c.getDeclaredField("alertDefinitionDao");
        f.setAccessible(true);
        f.set(AmbariMetaInfoTest.metaInfo, injector.getInstance(AlertDefinitionDAO.class));
        f = c.getDeclaredField("ambariServiceAlertDefinitions");
        f.setAccessible(true);
        f.set(AmbariMetaInfoTest.metaInfo, injector.getInstance(AmbariServiceAlertDefinitions.class));
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = clusters.getClusterById(clusterId);
        cluster.setDesiredStackVersion(new StackId(AmbariMetaInfoTest.STACK_NAME_HDP, stackVersion));
        RepositoryVersionEntity repositoryVersion = ormHelper.getOrCreateRepositoryVersion(cluster.getCurrentStackVersion(), repoVersion);
        cluster.addService("HDFS", repositoryVersion);
        AmbariMetaInfoTest.metaInfo.reconcileAlertDefinitions(clusters, false);
        AlertDefinitionDAO dao = injector.getInstance(AlertDefinitionDAO.class);
        List<AlertDefinitionEntity> definitions = dao.findAll(clusterId);
        Assert.assertEquals(13, definitions.size());
        cluster.deleteService("HDFS", new DeleteHostComponentStatusMetaData());
        AmbariMetaInfoTest.metaInfo.reconcileAlertDefinitions(clusters, false);
        List<AlertDefinitionEntity> updatedDefinitions = dao.findAll(clusterId);
        Assert.assertEquals(7, updatedDefinitions.size());
    }

    @Test
    public void testKerberosDescriptor() throws Exception {
        ServiceInfo service;
        // Test that kerberos descriptor file is not available when not supplied in service definition
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.1.1", "PIG");
        junit.framework.Assert.assertNotNull(service);
        junit.framework.Assert.assertNull(service.getKerberosDescriptorFile());
        // Test that kerberos descriptor file is available when supplied in service definition
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", "HDFS");
        junit.framework.Assert.assertNotNull(service);
        junit.framework.Assert.assertNotNull(service.getKerberosDescriptorFile());
        // Test that kerberos descriptor file is available from inherited stack version
        service = AmbariMetaInfoTest.metaInfo.getService(AmbariMetaInfoTest.STACK_NAME_HDP, "2.1.1", "HDFS");
        junit.framework.Assert.assertNotNull(service);
        junit.framework.Assert.assertNotNull(service.getKerberosDescriptorFile());
        // Test that kerberos.json file can be parsed into mapped data
        Map<?, ?> kerberosDescriptorData = new Gson().fromJson(new FileReader(service.getKerberosDescriptorFile()), Map.class);
        junit.framework.Assert.assertNotNull(kerberosDescriptorData);
        junit.framework.Assert.assertEquals(1, kerberosDescriptorData.size());
    }

    @Test
    public void testReadKerberosDescriptorFromFile() throws AmbariException {
        String path = getCommonKerberosDescriptorFileLocation();
        KerberosDescriptor descriptor = readKerberosDescriptorFromFile(path);
        junit.framework.Assert.assertNotNull(descriptor);
        junit.framework.Assert.assertNotNull(descriptor.getProperties());
        junit.framework.Assert.assertEquals(3, descriptor.getProperties().size());
        junit.framework.Assert.assertNotNull(descriptor.getIdentities());
        junit.framework.Assert.assertEquals(1, descriptor.getIdentities().size());
        assertEquals("spnego", descriptor.getIdentities().get(0).getName());
        junit.framework.Assert.assertNotNull(descriptor.getConfigurations());
        junit.framework.Assert.assertEquals(1, descriptor.getConfigurations().size());
        junit.framework.Assert.assertNotNull(descriptor.getConfigurations().get("core-site"));
        junit.framework.Assert.assertNotNull(descriptor.getConfiguration("core-site"));
        junit.framework.Assert.assertNull(descriptor.getServices());
    }

    @Test
    public void testGetKerberosDescriptor() throws AmbariException {
        KerberosDescriptor descriptor = getKerberosDescriptor(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", false);
        junit.framework.Assert.assertNotNull(descriptor);
        junit.framework.Assert.assertNotNull(descriptor.getProperties());
        junit.framework.Assert.assertEquals(3, descriptor.getProperties().size());
        junit.framework.Assert.assertNotNull(descriptor.getIdentities());
        junit.framework.Assert.assertEquals(1, descriptor.getIdentities().size());
        assertEquals("spnego", descriptor.getIdentities().get(0).getName());
        junit.framework.Assert.assertNotNull(descriptor.getConfigurations());
        junit.framework.Assert.assertEquals(1, descriptor.getConfigurations().size());
        junit.framework.Assert.assertNotNull(descriptor.getConfigurations().get("core-site"));
        junit.framework.Assert.assertNotNull(descriptor.getConfiguration("core-site"));
        junit.framework.Assert.assertNotNull(descriptor.getServices());
        junit.framework.Assert.assertEquals(1, descriptor.getServices().size());
        junit.framework.Assert.assertNotNull(descriptor.getServices().get("HDFS"));
        junit.framework.Assert.assertNotNull(descriptor.getService("HDFS"));
        assertFalse(descriptor.getService("HDFS").shouldPreconfigure());
    }

    @Test
    public void testGetKerberosDescriptorWithPreconfigure() throws AmbariException {
        KerberosDescriptor descriptor = getKerberosDescriptor(AmbariMetaInfoTest.STACK_NAME_HDP, "2.0.8", true);
        junit.framework.Assert.assertNotNull(descriptor);
        junit.framework.Assert.assertNotNull(descriptor.getProperties());
        junit.framework.Assert.assertEquals(3, descriptor.getProperties().size());
        junit.framework.Assert.assertNotNull(descriptor.getIdentities());
        junit.framework.Assert.assertEquals(1, descriptor.getIdentities().size());
        assertEquals("spnego", descriptor.getIdentities().get(0).getName());
        junit.framework.Assert.assertNotNull(descriptor.getConfigurations());
        junit.framework.Assert.assertEquals(1, descriptor.getConfigurations().size());
        junit.framework.Assert.assertNotNull(descriptor.getConfigurations().get("core-site"));
        junit.framework.Assert.assertNotNull(descriptor.getConfiguration("core-site"));
        junit.framework.Assert.assertNotNull(descriptor.getServices());
        junit.framework.Assert.assertEquals(2, descriptor.getServices().size());
        junit.framework.Assert.assertNotNull(descriptor.getServices().get("HDFS"));
        junit.framework.Assert.assertNotNull(descriptor.getService("HDFS"));
        assertTrue(descriptor.getService("HDFS").shouldPreconfigure());
        junit.framework.Assert.assertNotNull(descriptor.getServices().get("HDFS"));
        junit.framework.Assert.assertNotNull(descriptor.getService("HDFS"));
        assertTrue(descriptor.getService("HDFS").shouldPreconfigure());
        junit.framework.Assert.assertNotNull(descriptor.getServices().get("NEW_SERVICE"));
        junit.framework.Assert.assertNotNull(descriptor.getService("NEW_SERVICE"));
        assertTrue(descriptor.getService("NEW_SERVICE").shouldPreconfigure());
    }

    @Test
    public void testGetCommonWidgetsFile() throws AmbariException {
        File widgetsFile = getCommonWidgetsDescriptorFile();
        junit.framework.Assert.assertNotNull(widgetsFile);
        assertEquals("src/test/resources/widgets.json", widgetsFile.getPath());
    }

    @Test
    public void testGetVersionDefinitionsForDisabledStack() throws AmbariException {
        Map<String, VersionDefinitionXml> versionDefinitions = getVersionDefinitions();
        junit.framework.Assert.assertNotNull(versionDefinitions);
        // Check presence
        Map.Entry<String, VersionDefinitionXml> vdfEntry = null;
        for (Map.Entry<String, VersionDefinitionXml> entry : versionDefinitions.entrySet()) {
            if (entry.getKey().equals("HDP-2.2.1")) {
                vdfEntry = entry;
            }
        }
        assertNotNull("Candidate stack and vdf for test case.", vdfEntry);
        StackInfo stackInfo = getStack("HDP", "2.2.1");
        // Strange that this is not immutable but works for this test !
        stackInfo.setActive(false);
        // Hate to use reflection hence changed contract to be package private
        AmbariMetaInfoTest.metaInfo.versionDefinitions = null;
        versionDefinitions = AmbariMetaInfoTest.metaInfo.getVersionDefinitions();
        vdfEntry = null;
        for (Map.Entry<String, VersionDefinitionXml> entry : versionDefinitions.entrySet()) {
            if (entry.getKey().equals("HDP-2.2.1")) {
                vdfEntry = entry;
            }
        }
        assertNull("Disabled stack should not be returned by the API", vdfEntry);
    }

    private static class TestAmbariMetaInfo extends AmbariMetaInfo {
        AlertDefinitionDAO alertDefinitionDAO;

        AlertDefinitionFactory alertDefinitionFactory;

        OsFamily osFamily;

        Injector injector;

        public TestAmbariMetaInfo(Configuration configuration) throws Exception {
            super(configuration);
            injector = Guice.createInjector(Modules.override(new InMemoryDefaultTestModule()).with(new AmbariMetaInfoTest.TestAmbariMetaInfo.MockModule()));
            injector.getInstance(GuiceJpaInitializer.class);
            injector.getInstance(EntityManager.class);
            Class<?> c = getClass().getSuperclass();
            // StackManagerFactory
            StackManagerFactory stackManagerFactory = injector.getInstance(StackManagerFactory.class);
            Field f = c.getDeclaredField("stackManagerFactory");
            f.setAccessible(true);
            f.set(this, stackManagerFactory);
            // MpackManagerFactory
            MpackManagerFactory mpackManagerFactory = injector.getInstance(MpackManagerFactory.class);
            f = c.getDeclaredField("mpackManagerFactory");
            f.setAccessible(true);
            f.set(this, mpackManagerFactory);
            // AlertDefinitionDAO
            alertDefinitionDAO = createNiceMock(AlertDefinitionDAO.class);
            f = c.getDeclaredField("alertDefinitionDao");
            f.setAccessible(true);
            f.set(this, alertDefinitionDAO);
            // AlertDefinitionFactory
            // alertDefinitionFactory = createNiceMock(AlertDefinitionFactory.class);
            alertDefinitionFactory = new AlertDefinitionFactory();
            f = c.getDeclaredField("alertDefinitionFactory");
            f.setAccessible(true);
            f.set(this, alertDefinitionFactory);
            // AmbariEventPublisher
            AmbariEventPublisher ambariEventPublisher = new AmbariEventPublisher();
            f = c.getDeclaredField("eventPublisher");
            f.setAccessible(true);
            f.set(this, ambariEventPublisher);
            // KerberosDescriptorFactory
            KerberosDescriptorFactory kerberosDescriptorFactory = new KerberosDescriptorFactory();
            f = c.getDeclaredField("kerberosDescriptorFactory");
            f.setAccessible(true);
            f.set(this, kerberosDescriptorFactory);
            // KerberosServiceDescriptorFactory
            KerberosServiceDescriptorFactory kerberosServiceDescriptorFactory = new KerberosServiceDescriptorFactory();
            f = c.getDeclaredField("kerberosServiceDescriptorFactory");
            f.setAccessible(true);
            f.set(this, kerberosServiceDescriptorFactory);
            // OSFamily
            Configuration config = createNiceMock(Configuration.class);
            if (System.getProperty("os.name").contains("Windows")) {
                expect(config.getSharedResourcesDirPath()).andReturn(ClassLoader.getSystemClassLoader().getResource("").getPath()).anyTimes();
                expect(config.getResourceDirPath()).andReturn(ClassLoader.getSystemClassLoader().getResource("").getPath()).anyTimes();
            } else {
                expect(config.getSharedResourcesDirPath()).andReturn("./src/test/resources").anyTimes();
                expect(config.getResourceDirPath()).andReturn("./src/test/resources").anyTimes();
            }
            replay(config);
            osFamily = new OsFamily(config);
            f = c.getDeclaredField("osFamily");
            f.setAccessible(true);
            f.set(this, osFamily);
        }

        public void replayAllMocks() {
            replay(alertDefinitionDAO);
        }

        public class MockModule extends AbstractModule {
            @Override
            protected void configure() {
                bind(ActionMetadata.class);
                // create a mock metainfo DAO for the entire system so that injectables
                // can use the mock as well
                bind(MetainfoDAO.class).toInstance(createNiceMock(MetainfoDAO.class));
            }
        }
    }
}

