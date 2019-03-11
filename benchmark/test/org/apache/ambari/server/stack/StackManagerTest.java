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
package org.apache.ambari.server.stack;


import CommandScriptDefinition.Type.PYTHON;
import ServiceOsSpecific.Package;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.AmbariManagementHelper;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.orm.dao.ExtensionDAO;
import org.apache.ambari.server.orm.dao.ExtensionLinkDAO;
import org.apache.ambari.server.orm.dao.MetainfoDAO;
import org.apache.ambari.server.orm.dao.StackDAO;
import org.apache.ambari.server.orm.entities.ExtensionEntity;
import org.apache.ambari.server.orm.entities.ExtensionLinkEntity;
import org.apache.ambari.server.stack.upgrade.ConfigUpgradePack;
import org.apache.ambari.server.stack.upgrade.UpgradePack;
import org.apache.ambari.server.state.ClientConfigFileDefinition;
import org.apache.ambari.server.state.CommandScriptDefinition;
import org.apache.ambari.server.state.ComponentInfo;
import org.apache.ambari.server.state.PropertyInfo;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.ServiceOsSpecific;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.server.state.stack.MetricDefinition;
import org.apache.ambari.server.state.stack.OsFamily;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * StackManager unit tests.
 */
public class StackManagerTest {
    private static StackManager stackManager;

    private static MetainfoDAO metaInfoDao;

    private static ActionMetadata actionMetadata;

    private static OsFamily osFamily;

    private static StackDAO stackDao;

    private static ExtensionDAO extensionDao;

    private static ExtensionLinkDAO linkDao;

    @Test
    public void testGetsStacks() throws Exception {
        Collection<StackInfo> stacks = StackManagerTest.stackManager.getStacks();
        Assert.assertEquals(21, stacks.size());
    }

    @Test
    public void testGetStacksByName() {
        Collection<StackInfo> stacks = StackManagerTest.stackManager.getStacks("HDP");
        Assert.assertEquals(17, stacks.size());
        stacks = StackManagerTest.stackManager.getStacks("OTHER");
        Assert.assertEquals(2, stacks.size());
    }

    @Test
    public void testHCFSServiceType() {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.2.0.ECS");
        ServiceInfo service = stack.getService("ECS");
        Assert.assertEquals(service.getServiceType(), "HCFS");
        service = stack.getService("HDFS");
        Assert.assertNull(service);
    }

    @Test
    public void testServiceRemoved() {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.8");
        ServiceInfo service = stack.getService("SPARK");
        Assert.assertNull(service);
        service = stack.getService("SPARK2");
        Assert.assertNull(service);
        List<String> removedServices = stack.getRemovedServices();
        Assert.assertEquals(removedServices.size(), 2);
        HashSet<String> expectedServices = new HashSet<>();
        expectedServices.add("SPARK");
        expectedServices.add("SPARK2");
        for (String s : removedServices) {
            Assert.assertTrue(expectedServices.remove(s));
        }
        Assert.assertTrue(expectedServices.isEmpty());
    }

    @Test
    public void testSerivcesWithNoConfigs() {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.8");
        List<String> servicesWithNoConfigs = stack.getServicesWithNoConfigs();
        // Via inheritance, Hive should have config types
        // Via inheritance, SystemML should still have no config types
        Assert.assertTrue(servicesWithNoConfigs.contains("SYSTEMML"));
        Assert.assertFalse(servicesWithNoConfigs.contains("HIVE"));
        stack = StackManagerTest.stackManager.getStack("HDP", "2.0.7");
        // Directly from the stack, SystemML should have no config types
        servicesWithNoConfigs = stack.getServicesWithNoConfigs();
        Assert.assertTrue(servicesWithNoConfigs.contains("SYSTEMML"));
    }

    @Test
    public void testGetStack() {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "0.1");
        Assert.assertNotNull(stack);
        Assert.assertEquals("HDP", stack.getName());
        Assert.assertEquals("0.1", stack.getVersion());
        Collection<ServiceInfo> services = stack.getServices();
        Assert.assertEquals(3, services.size());
        Map<String, ServiceInfo> serviceMap = new HashMap<>();
        for (ServiceInfo service : services) {
            serviceMap.put(service.getName(), service);
        }
        ServiceInfo hdfsService = serviceMap.get("HDFS");
        Assert.assertNotNull(hdfsService);
        List<ComponentInfo> components = hdfsService.getComponents();
        Assert.assertEquals(6, components.size());
        List<PropertyInfo> properties = hdfsService.getProperties();
        Assert.assertEquals(64, properties.size());
        // test a couple of the properties for filename
        boolean hdfsPropFound = false;
        boolean hbasePropFound = false;
        for (PropertyInfo p : properties) {
            if (p.getName().equals("hbase.regionserver.msginterval")) {
                Assert.assertEquals("hbase-site.xml", p.getFilename());
                hbasePropFound = true;
            } else
                if (p.getName().equals("dfs.name.dir")) {
                    Assert.assertEquals("hdfs-site.xml", p.getFilename());
                    hdfsPropFound = true;
                }

        }
        Assert.assertTrue(hbasePropFound);
        Assert.assertTrue(hdfsPropFound);
        ServiceInfo mrService = serviceMap.get("MAPREDUCE");
        Assert.assertNotNull(mrService);
        components = mrService.getComponents();
        Assert.assertEquals(3, components.size());
        ServiceInfo pigService = serviceMap.get("PIG");
        Assert.assertNotNull(pigService);
        Assert.assertEquals("PIG", pigService.getName());
        Assert.assertEquals("1.0", pigService.getVersion());
        Assert.assertEquals("This is comment for PIG service", pigService.getComment());
        components = pigService.getComponents();
        Assert.assertEquals(1, components.size());
        CommandScriptDefinition commandScript = pigService.getCommandScript();
        Assert.assertEquals("scripts/service_check.py", commandScript.getScript());
        Assert.assertEquals(PYTHON, commandScript.getScriptType());
        Assert.assertEquals(300, commandScript.getTimeout());
        List<String> configDependencies = pigService.getConfigDependencies();
        Assert.assertEquals(1, configDependencies.size());
        Assert.assertEquals("global", configDependencies.get(0));
        Assert.assertEquals("global", pigService.getConfigDependenciesWithComponents().get(0));
        ComponentInfo client = pigService.getClientComponent();
        Assert.assertNotNull(client);
        Assert.assertEquals("PIG", client.getName());
        Assert.assertEquals("0+", client.getCardinality());
        Assert.assertEquals("CLIENT", client.getCategory());
        Assert.assertEquals("configuration", pigService.getConfigDir());
        Assert.assertEquals("2.0", pigService.getSchemaVersion());
        Map<String, ServiceOsSpecific> osInfoMap = pigService.getOsSpecifics();
        Assert.assertEquals(1, osInfoMap.size());
        ServiceOsSpecific osSpecific = osInfoMap.get("centos6");
        Assert.assertNotNull(osSpecific);
        Assert.assertEquals("centos6", osSpecific.getOsFamily());
        Assert.assertNull(osSpecific.getRepo());
        List<ServiceOsSpecific.Package> packages = osSpecific.getPackages();
        Assert.assertEquals(1, packages.size());
        ServiceOsSpecific.Package pkg = packages.get(0);
        Assert.assertEquals("pig", pkg.getName());
        Assert.assertNull(pigService.getParent());
    }

    @Test
    public void testStackVersionInheritance() {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.1.1");
        Assert.assertNotNull(stack);
        Assert.assertEquals("HDP", stack.getName());
        Assert.assertEquals("2.1.1", stack.getVersion());
        Collection<ServiceInfo> services = stack.getServices();
        ServiceInfo si = stack.getService("SPARK");
        Assert.assertNull(si);
        si = stack.getService("SPARK2");
        Assert.assertNull(si);
        si = stack.getService("SPARK3");
        Assert.assertNotNull(si);
        // should include all stacks in hierarchy
        Assert.assertEquals(18, services.size());
        HashSet<String> expectedServices = new HashSet<>();
        expectedServices.add("GANGLIA");
        expectedServices.add("HBASE");
        expectedServices.add("HCATALOG");
        expectedServices.add("HDFS");
        expectedServices.add("HIVE");
        expectedServices.add("MAPREDUCE2");
        expectedServices.add("OOZIE");
        expectedServices.add("PIG");
        expectedServices.add("SQOOP");
        expectedServices.add("YARN");
        expectedServices.add("ZOOKEEPER");
        expectedServices.add("STORM");
        expectedServices.add("FLUME");
        expectedServices.add("FAKENAGIOS");
        expectedServices.add("TEZ");
        expectedServices.add("AMBARI_METRICS");
        expectedServices.add("SPARK3");
        expectedServices.add("SYSTEMML");
        ServiceInfo pigService = null;
        for (ServiceInfo service : services) {
            if (service.getName().equals("PIG")) {
                pigService = service;
            }
            Assert.assertTrue(expectedServices.remove(service.getName()));
        }
        Assert.assertTrue(expectedServices.isEmpty());
        // extended values
        Assert.assertNotNull(pigService);
        Assert.assertEquals("0.12.1.2.1.1", pigService.getVersion());
        Assert.assertEquals("Scripting platform for analyzing large datasets (Extended)", pigService.getComment());
        // base value
        ServiceInfo basePigService = StackManagerTest.stackManager.getStack("HDP", "2.0.5").getService("PIG");
        Assert.assertEquals("0.11.1.2.0.5.0", basePigService.getVersion());
        Assert.assertEquals(1, basePigService.getComponents().size());
        // new component added in extended version
        Assert.assertEquals(2, pigService.getComponents().size());
        // no properties in base service
        Assert.assertEquals(0, basePigService.getProperties().size());
        Assert.assertEquals(1, pigService.getProperties().size());
        Assert.assertEquals("content", pigService.getProperties().get(0).getName());
    }

    @Test
    public void testStackServiceExtension() {
        StackInfo stack = StackManagerTest.stackManager.getStack("OTHER", "1.0");
        Assert.assertNotNull(stack);
        Assert.assertEquals("OTHER", stack.getName());
        Assert.assertEquals("1.0", stack.getVersion());
        Collection<ServiceInfo> services = stack.getServices();
        Assert.assertEquals(4, services.size());
        // hdfs service
        Assert.assertEquals(6, stack.getService("HDFS").getComponents().size());
        // Extended Sqoop service via explicit service extension
        ServiceInfo sqoopService = stack.getService("SQOOP2");
        Assert.assertNotNull(sqoopService);
        Assert.assertEquals("Extended SQOOP", sqoopService.getComment());
        Assert.assertEquals("Extended Version", sqoopService.getVersion());
        Assert.assertNull(sqoopService.getServicePackageFolder());
        Collection<ComponentInfo> components = sqoopService.getComponents();
        Assert.assertEquals(1, components.size());
        ComponentInfo component = components.iterator().next();
        Assert.assertEquals("SQOOP", component.getName());
        // Get the base sqoop service
        StackInfo baseStack = StackManagerTest.stackManager.getStack("HDP", "2.1.1");
        ServiceInfo baseSqoopService = baseStack.getService("SQOOP");
        // values from base service
        Assert.assertEquals(baseSqoopService.isDeleted(), sqoopService.isDeleted());
        Assert.assertEquals(baseSqoopService.getAlertsFile(), sqoopService.getAlertsFile());
        Assert.assertEquals(baseSqoopService.getClientComponent(), sqoopService.getClientComponent());
        Assert.assertEquals(baseSqoopService.getCommandScript(), sqoopService.getCommandScript());
        Assert.assertEquals(baseSqoopService.getConfigDependencies(), sqoopService.getConfigDependencies());
        Assert.assertEquals(baseSqoopService.getConfigDir(), sqoopService.getConfigDir());
        Assert.assertEquals(baseSqoopService.getConfigDependenciesWithComponents(), sqoopService.getConfigDependenciesWithComponents());
        Assert.assertEquals(baseSqoopService.getConfigTypeAttributes(), sqoopService.getConfigTypeAttributes());
        Assert.assertEquals(baseSqoopService.getCustomCommands(), sqoopService.getCustomCommands());
        Assert.assertEquals(baseSqoopService.getExcludedConfigTypes(), sqoopService.getExcludedConfigTypes());
        Assert.assertEquals(baseSqoopService.getProperties(), sqoopService.getProperties());
        Assert.assertEquals(baseSqoopService.getMetrics(), sqoopService.getMetrics());
        Assert.assertNull(baseSqoopService.getMetricsFile());
        Assert.assertNull(sqoopService.getMetricsFile());
        Assert.assertEquals(baseSqoopService.getOsSpecifics(), sqoopService.getOsSpecifics());
        Assert.assertEquals(baseSqoopService.getRequiredServices(), sqoopService.getRequiredServices());
        Assert.assertEquals(baseSqoopService.getSchemaVersion(), sqoopService.getSchemaVersion());
        // extended Storm service via explicit service extension
        ServiceInfo stormService = stack.getService("STORM");
        Assert.assertNotNull(stormService);
        Assert.assertEquals("STORM", stormService.getName());
        // base storm service
        ServiceInfo baseStormService = baseStack.getService("STORM");
        // overridden value
        Assert.assertEquals("Apache Hadoop Stream processing framework (Extended)", stormService.getComment());
        Assert.assertEquals("New version", stormService.getVersion());
        String packageDir = StringUtils.join(new String[]{ "stacks", "OTHER", "1.0", "services", "STORM", "package" }, File.separator);
        Assert.assertEquals(packageDir, stormService.getServicePackageFolder());
        // compare components
        List<ComponentInfo> stormServiceComponents = stormService.getComponents();
        List<ComponentInfo> baseStormServiceComponents = baseStormService.getComponents();
        Assert.assertEquals(new HashSet(stormServiceComponents), new HashSet(baseStormServiceComponents));
        // values from base service
        Assert.assertEquals(baseStormService.isDeleted(), stormService.isDeleted());
        // todo: specify alerts file in stack
        Assert.assertEquals(baseStormService.getAlertsFile(), stormService.getAlertsFile());
        Assert.assertEquals(baseStormService.getClientComponent(), stormService.getClientComponent());
        Assert.assertEquals(baseStormService.getCommandScript(), stormService.getCommandScript());
        Assert.assertEquals(baseStormService.getConfigDependencies(), stormService.getConfigDependencies());
        Assert.assertEquals(baseStormService.getConfigDir(), stormService.getConfigDir());
        Assert.assertEquals(baseStormService.getConfigDependenciesWithComponents(), stormService.getConfigDependenciesWithComponents());
        Assert.assertEquals(baseStormService.getConfigTypeAttributes(), stormService.getConfigTypeAttributes());
        Assert.assertEquals(baseStormService.getCustomCommands(), stormService.getCustomCommands());
        Assert.assertEquals(baseStormService.getExcludedConfigTypes(), stormService.getExcludedConfigTypes());
        Assert.assertEquals(baseStormService.getProperties(), stormService.getProperties());
        Assert.assertEquals(baseStormService.getMetrics(), stormService.getMetrics());
        Assert.assertNotNull(baseStormService.getMetricsFile());
        Assert.assertNotNull(stormService.getMetricsFile());
        Assert.assertFalse(baseStormService.getMetricsFile().equals(stormService.getMetricsFile()));
        Assert.assertEquals(baseStormService.getOsSpecifics(), stormService.getOsSpecifics());
        Assert.assertEquals(baseStormService.getRequiredServices(), stormService.getRequiredServices());
        Assert.assertEquals(baseStormService.getSchemaVersion(), stormService.getSchemaVersion());
    }

    @Test
    public void testGetStackServiceInheritance() {
        StackInfo baseStack = StackManagerTest.stackManager.getStack("OTHER", "1.0");
        StackInfo stack = StackManagerTest.stackManager.getStack("OTHER", "2.0");
        Assert.assertEquals(5, stack.getServices().size());
        ServiceInfo service = stack.getService("SQOOP2");
        ServiceInfo baseSqoopService = baseStack.getService("SQOOP2");
        Assert.assertEquals("SQOOP2", service.getName());
        Assert.assertEquals("Inherited from parent", service.getComment());
        Assert.assertEquals("Extended from parent version", service.getVersion());
        Assert.assertNull(service.getServicePackageFolder());
        // compare components
        List<ComponentInfo> serviceComponents = service.getComponents();
        List<ComponentInfo> baseStormServiceCompoents = baseSqoopService.getComponents();
        Assert.assertEquals(serviceComponents, baseStormServiceCompoents);
        // values from base service
        Assert.assertEquals(baseSqoopService.isDeleted(), service.isDeleted());
        Assert.assertEquals(baseSqoopService.getAlertsFile(), service.getAlertsFile());
        Assert.assertEquals(baseSqoopService.getClientComponent(), service.getClientComponent());
        Assert.assertEquals(baseSqoopService.getCommandScript(), service.getCommandScript());
        Assert.assertEquals(baseSqoopService.getConfigDependencies(), service.getConfigDependencies());
        Assert.assertEquals(baseSqoopService.getConfigDir(), service.getConfigDir());
        Assert.assertEquals(baseSqoopService.getConfigDependenciesWithComponents(), service.getConfigDependenciesWithComponents());
        Assert.assertEquals(baseSqoopService.getConfigTypeAttributes(), service.getConfigTypeAttributes());
        Assert.assertEquals(baseSqoopService.getCustomCommands(), service.getCustomCommands());
        Assert.assertEquals(baseSqoopService.getExcludedConfigTypes(), service.getExcludedConfigTypes());
        Assert.assertEquals(baseSqoopService.getProperties(), service.getProperties());
        Assert.assertEquals(baseSqoopService.getMetrics(), service.getMetrics());
        Assert.assertNull(baseSqoopService.getMetricsFile());
        Assert.assertNull(service.getMetricsFile());
        Assert.assertEquals(baseSqoopService.getOsSpecifics(), service.getOsSpecifics());
        Assert.assertEquals(baseSqoopService.getRequiredServices(), service.getRequiredServices());
        Assert.assertEquals(baseSqoopService.getSchemaVersion(), service.getSchemaVersion());
    }

    @Test
    public void testConfigDependenciesInheritance() throws Exception {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.6");
        ServiceInfo hdfsService = stack.getService("HDFS");
        Assert.assertEquals(5, hdfsService.getConfigDependencies().size());
        Assert.assertEquals(4, hdfsService.getConfigTypeAttributes().size());
        Assert.assertTrue(hdfsService.getConfigDependencies().contains("core-site"));
        Assert.assertTrue(hdfsService.getConfigDependencies().contains("global"));
        Assert.assertTrue(hdfsService.getConfigDependencies().contains("hdfs-site"));
        Assert.assertTrue(hdfsService.getConfigDependencies().contains("hdfs-log4j"));
        Assert.assertTrue(hdfsService.getConfigDependencies().contains("hadoop-policy"));
        Assert.assertTrue(Boolean.valueOf(hdfsService.getConfigTypeAttributes().get("core-site").get("supports").get("final")));
        Assert.assertFalse(Boolean.valueOf(hdfsService.getConfigTypeAttributes().get("global").get("supports").get("final")));
    }

    @Test
    public void testClientConfigFilesInheritance() throws Exception {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.6");
        ServiceInfo zkService = stack.getService("ZOOKEEPER");
        List<ComponentInfo> components = zkService.getComponents();
        Assert.assertTrue(((components.size()) == 2));
        ComponentInfo componentInfo = components.get(1);
        List<ClientConfigFileDefinition> clientConfigs = componentInfo.getClientConfigFiles();
        Assert.assertEquals(2, clientConfigs.size());
        Assert.assertEquals("zookeeper-env", clientConfigs.get(0).getDictionaryName());
        Assert.assertEquals("zookeeper-env.sh", clientConfigs.get(0).getFileName());
        Assert.assertEquals("env", clientConfigs.get(0).getType());
        Assert.assertEquals("zookeeper-log4j", clientConfigs.get(1).getDictionaryName());
        Assert.assertEquals("log4j.properties", clientConfigs.get(1).getFileName());
        Assert.assertEquals("env", clientConfigs.get(1).getType());
    }

    @Test
    public void testPackageInheritance() throws Exception {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.7");
        Assert.assertNotNull(stack.getService("HBASE"));
        ServiceInfo hbase = stack.getService("HBASE");
        Assert.assertNotNull(("Package dir is " + (hbase.getServicePackageFolder())), hbase.getServicePackageFolder());
        stack = StackManagerTest.stackManager.getStack("HDP", "2.0.8");
        Assert.assertNotNull(stack.getService("HBASE"));
        hbase = stack.getService("HBASE");
        Assert.assertNotNull(("Package dir is " + (hbase.getServicePackageFolder())), hbase.getServicePackageFolder());
    }

    @Test
    public void testMonitoringServicePropertyInheritance() throws Exception {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.8");
        Collection<ServiceInfo> allServices = stack.getServices();
        Assert.assertEquals(15, allServices.size());
        boolean monitoringServiceFound = false;
        for (ServiceInfo serviceInfo : allServices) {
            if (serviceInfo.getName().equals("FAKENAGIOS")) {
                monitoringServiceFound = true;
                Assert.assertTrue(serviceInfo.isMonitoringService());
            } else {
                Assert.assertNull(serviceInfo.isMonitoringService());
            }
        }
        Assert.assertTrue(monitoringServiceFound);
    }

    @Test
    public void testServiceDeletion() {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.6");
        Collection<ServiceInfo> allServices = stack.getServices();
        Assert.assertEquals(12, allServices.size());
        HashSet<String> expectedServices = new HashSet<>();
        expectedServices.add("GANGLIA");
        expectedServices.add("HBASE");
        expectedServices.add("HCATALOG");
        expectedServices.add("HDFS");
        expectedServices.add("HIVE");
        expectedServices.add("MAPREDUCE2");
        expectedServices.add("OOZIE");
        expectedServices.add("PIG");
        expectedServices.add("SPARK");
        expectedServices.add("ZOOKEEPER");
        expectedServices.add("FLUME");
        expectedServices.add("YARN");
        for (ServiceInfo service : allServices) {
            Assert.assertTrue(expectedServices.remove(service.getName()));
        }
        Assert.assertTrue(expectedServices.isEmpty());
    }

    @Test
    public void testComponentDeletion() {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.6");
        ServiceInfo yarnService = stack.getService("YARN");
        Assert.assertNull(yarnService.getComponentByName("YARN_CLIENT"));
        stack = StackManagerTest.stackManager.getStack("HDP", "2.0.6.1");
        yarnService = stack.getService("YARN");
        Assert.assertNull(yarnService.getComponentByName("YARN_CLIENT"));
        stack = StackManagerTest.stackManager.getStack("HDP", "2.0.7");
        yarnService = stack.getService("YARN");
        Assert.assertNotNull(yarnService.getComponentByName("YARN_CLIENT"));
    }

    @Test
    public void testInheritanceAfterComponentDeletion() {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.6");
        ServiceInfo yarnService = stack.getService("HBASE");
        Assert.assertNull(yarnService.getComponentByName("HBASE_CLIENT"));
        stack = StackManagerTest.stackManager.getStack("HDP", "2.0.6.1");
        yarnService = stack.getService("HBASE");
        Assert.assertNull(yarnService.getComponentByName("HBASE_CLIENT"));
        stack = StackManagerTest.stackManager.getStack("HDP", "2.0.8");
        yarnService = stack.getService("HBASE");
        Assert.assertNotNull(yarnService.getComponentByName("HBASE_CLIENT"));
    }

    @Test
    public void testPopulateConfigTypes() throws Exception {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.7");
        ServiceInfo hdfsService = stack.getService("HDFS");
        Map<String, Map<String, Map<String, String>>> configTypes = hdfsService.getConfigTypeAttributes();
        Assert.assertEquals(4, configTypes.size());
        Map<String, Map<String, String>> configType = configTypes.get("global");
        Assert.assertEquals(1, configType.size());
        Map<String, String> supportsMap = configType.get("supports");
        Assert.assertEquals(3, supportsMap.size());
        Assert.assertEquals("true", supportsMap.get("final"));
        Assert.assertEquals("false", supportsMap.get("adding_forbidden"));
        Assert.assertEquals("false", supportsMap.get("do_not_extend"));
        configType = configTypes.get("hdfs-site");
        Assert.assertEquals(1, configType.size());
        supportsMap = configType.get("supports");
        Assert.assertEquals(3, supportsMap.size());
        Assert.assertEquals("false", supportsMap.get("final"));
        Assert.assertEquals("false", supportsMap.get("adding_forbidden"));
        Assert.assertEquals("false", supportsMap.get("do_not_extend"));
        configType = configTypes.get("core-site");
        Assert.assertEquals(1, configType.size());
        supportsMap = configType.get("supports");
        Assert.assertEquals(3, supportsMap.size());
        Assert.assertEquals("false", supportsMap.get("final"));
        Assert.assertEquals("false", supportsMap.get("adding_forbidden"));
        Assert.assertEquals("false", supportsMap.get("do_not_extend"));
        configType = configTypes.get("hadoop-policy");
        Assert.assertEquals(1, configType.size());
        supportsMap = configType.get("supports");
        Assert.assertEquals(3, supportsMap.size());
        Assert.assertEquals("false", supportsMap.get("final"));
        Assert.assertEquals("false", supportsMap.get("adding_forbidden"));
        Assert.assertEquals("false", supportsMap.get("do_not_extend"));
        ServiceInfo yarnService = stack.getService("YARN");
        configTypes = yarnService.getConfigTypeAttributes();
        Assert.assertEquals(4, configTypes.size());
        Assert.assertTrue(configTypes.containsKey("yarn-site"));
        Assert.assertTrue(configTypes.containsKey("core-site"));
        Assert.assertTrue(configTypes.containsKey("global"));
        Assert.assertTrue(configTypes.containsKey("capacity-scheduler"));
        configType = configTypes.get("yarn-site");
        supportsMap = configType.get("supports");
        Assert.assertEquals(3, supportsMap.size());
        Assert.assertEquals("false", supportsMap.get("final"));
        Assert.assertEquals("true", supportsMap.get("adding_forbidden"));
        Assert.assertEquals("true", supportsMap.get("do_not_extend"));
        ServiceInfo mrService = stack.getService("MAPREDUCE2");
        configTypes = mrService.getConfigTypeAttributes();
        Assert.assertEquals(3, configTypes.size());
        Assert.assertTrue(configTypes.containsKey("mapred-site"));
        Assert.assertTrue(configTypes.containsKey("core-site"));
        Assert.assertTrue(configTypes.containsKey("mapred-queue-acls"));
    }

    @Test
    public void testExcludedConfigTypes() {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.8");
        ServiceInfo service = stack.getService("HBASE");
        Assert.assertFalse(service.hasConfigType("global"));
        Map<String, Map<String, Map<String, String>>> configTypes = service.getConfigTypeAttributes();
        Assert.assertEquals(2, configTypes.size());
        Assert.assertTrue(configTypes.containsKey("hbase-site"));
        Assert.assertTrue(configTypes.containsKey("hbase-policy"));
        // test version that inherits the service via version inheritance
        stack = StackManagerTest.stackManager.getStack("HDP", "2.1.1");
        service = stack.getService("HBASE");
        Assert.assertFalse(service.hasConfigType("global"));
        configTypes = service.getConfigTypeAttributes();
        Assert.assertEquals(2, configTypes.size());
        Assert.assertTrue(configTypes.containsKey("hbase-site"));
        Assert.assertTrue(configTypes.containsKey("hbase-policy"));
        Assert.assertFalse(configTypes.containsKey("global"));
        // test version that inherits the service explicit service extension
        // the new version also excludes hbase-policy
        stack = StackManagerTest.stackManager.getStack("OTHER", "2.0");
        service = stack.getService("HBASE");
        Assert.assertFalse(service.hasConfigType("hbase-policy"));
        Assert.assertFalse(service.hasConfigType("global"));
        configTypes = service.getConfigTypeAttributes();
        Assert.assertEquals(1, configTypes.size());
        Assert.assertTrue(configTypes.containsKey("hbase-site"));
    }

    @Test
    public void testHDFSServiceContainsMetricsFile() throws Exception {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.0.6");
        ServiceInfo hdfsService = stack.getService("HDFS");
        Assert.assertEquals("HDFS", hdfsService.getName());
        Assert.assertNotNull(hdfsService.getMetricsFile());
    }

    @Test
    public void testMergeRoleCommandOrder() throws Exception {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.1.1");
        // merged role command order with parent stacks
        Map<String, Object> roleCommandOrder = stack.getRoleCommandOrder().getContent();
        Assert.assertTrue(roleCommandOrder.containsKey("optional_glusterfs"));
        Assert.assertTrue(roleCommandOrder.containsKey("general_deps"));
        Assert.assertTrue(roleCommandOrder.containsKey("optional_no_glusterfs"));
        Assert.assertTrue(roleCommandOrder.containsKey("namenode_optional_ha"));
        Assert.assertTrue(roleCommandOrder.containsKey("resourcemanager_optional_ha"));
        Map<String, Object> generalDeps = ((Map<String, Object>) (roleCommandOrder.get("general_deps")));
        Assert.assertTrue(generalDeps.containsKey("HBASE_MASTER-START"));
        Assert.assertTrue(generalDeps.containsKey("HBASE_REGIONSERVER-START"));
        Map<String, Object> optionalNoGlusterfs = ((Map<String, Object>) (roleCommandOrder.get("optional_no_glusterfs")));
        Assert.assertTrue(optionalNoGlusterfs.containsKey("SECONDARY_NAMENODE-START"));
        ArrayList<String> hbaseMasterStartValues = ((ArrayList<String>) (generalDeps.get("HBASE_MASTER-START")));
        Assert.assertTrue(hbaseMasterStartValues.get(0).equals("ZOOKEEPER_SERVER-START"));
        ServiceInfo service = stack.getService("PIG");
        Assert.assertNotNull("PIG's roll command order is null", service.getRoleCommandOrder());
        Assert.assertTrue(optionalNoGlusterfs.containsKey("NAMENODE-STOP"));
        ArrayList<String> nameNodeStopValues = ((ArrayList<String>) (optionalNoGlusterfs.get("NAMENODE-STOP")));
        Assert.assertTrue(nameNodeStopValues.contains("JOBTRACKER-STOP"));
        Assert.assertTrue(nameNodeStopValues.contains("CUSTOM_MASTER-STOP"));
        Assert.assertTrue(generalDeps.containsKey("CUSTOM_MASTER-START"));
        ArrayList<String> customMasterStartValues = ((ArrayList<String>) (generalDeps.get("CUSTOM_MASTER-START")));
        Assert.assertTrue(customMasterStartValues.contains("ZOOKEEPER_SERVER-START"));
        Assert.assertTrue(customMasterStartValues.contains("NAMENODE-START"));
    }

    /**
     * Tests that {@link UpgradePack} and {@link ConfigUpgradePack} instances are correctly initialized
     * post-unmarshalling.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUpgradePacksInitializedAfterUnmarshalling() throws Exception {
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.2.0");
        Map<String, UpgradePack> upgradePacks = stack.getUpgradePacks();
        for (UpgradePack upgradePack : upgradePacks.values()) {
            Assert.assertNotNull(upgradePack);
            Assert.assertNotNull(upgradePack.getTasks());
            Assert.assertTrue(((upgradePack.getTasks().size()) > 0));
            // reference equality (make sure it's the same list)
            Assert.assertTrue(((upgradePack.getTasks()) == (upgradePack.getTasks())));
        }
        ConfigUpgradePack configUpgradePack = stack.getConfigUpgradePack();
        Assert.assertNotNull(configUpgradePack);
        Assert.assertNotNull(configUpgradePack.services);
    }

    @Test
    public void testMetricsLoaded() throws Exception {
        URL rootDirectoryURL = StackManagerTest.class.getResource("/");
        org.springframework.util.Assert.notNull(rootDirectoryURL);
        File resourcesDirectory = new File(new File(rootDirectoryURL.getFile()).getParentFile().getParentFile(), "src/test/resources");
        File stackRoot = new File(resourcesDirectory, "stacks");
        File commonServices = new File(resourcesDirectory, "common-services");
        File extensions = null;
        try {
            URL extensionsURL = ClassLoader.getSystemClassLoader().getResource("extensions");
            if (extensionsURL != null) {
                extensions = new File(extensionsURL.getPath().replace("test-classes", "classes"));
            }
        } catch (Exception e) {
        }
        MetainfoDAO metaInfoDao = createNiceMock(MetainfoDAO.class);
        StackDAO stackDao = createNiceMock(StackDAO.class);
        ExtensionDAO extensionDao = createNiceMock(ExtensionDAO.class);
        ExtensionLinkDAO linkDao = createNiceMock(ExtensionLinkDAO.class);
        ActionMetadata actionMetadata = createNiceMock(ActionMetadata.class);
        Configuration config = createNiceMock(Configuration.class);
        ExtensionEntity extensionEntity = createNiceMock(ExtensionEntity.class);
        expect(config.getSharedResourcesDirPath()).andReturn(ClassLoader.getSystemClassLoader().getResource("").getPath()).anyTimes();
        expect(extensionDao.find(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(extensionEntity).atLeastOnce();
        List<ExtensionLinkEntity> list = Collections.emptyList();
        expect(linkDao.findByStack(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(list).atLeastOnce();
        replay(config, metaInfoDao, stackDao, extensionDao, linkDao, actionMetadata);
        OsFamily osFamily = new OsFamily(config);
        AmbariManagementHelper helper = new AmbariManagementHelper(stackDao, extensionDao, linkDao);
        StackManager stackManager = new StackManager(stackRoot, commonServices, extensions, osFamily, false, metaInfoDao, actionMetadata, stackDao, extensionDao, linkDao, helper);
        for (StackInfo stackInfo : stackManager.getStacks()) {
            for (ServiceInfo serviceInfo : stackInfo.getServices()) {
                Type type = new TypeToken<Map<String, Map<String, List<MetricDefinition>>>>() {}.getType();
                Gson gson = new Gson();
                Map<String, Map<String, List<MetricDefinition>>> map = null;
                if ((serviceInfo.getMetricsFile()) != null) {
                    try {
                        map = gson.fromJson(new FileReader(serviceInfo.getMetricsFile()), type);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new org.apache.ambari.server.AmbariException(("Failed to load metrics from file " + (serviceInfo.getMetricsFile().getAbsolutePath())));
                    }
                }
            }
        }
    }

    @Test
    public void testVersionDefinitionStackRepoUpdateLinkExists() {
        // Get the base sqoop service
        StackInfo stack = StackManagerTest.stackManager.getStack("HDP", "2.1.1");
        String latestUri = stack.getRepositoryXml().getLatestURI();
        Assert.assertTrue((latestUri != null));
        stack = StackManagerTest.stackManager.getStack("HDP", "2.0.8");
        latestUri = stack.getRepositoryXml().getLatestURI();
        Assert.assertTrue((latestUri == null));
    }
}

