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
import RepositoryInfo.GET_REPO_ID_FUNCTION;
import ServiceOsSpecific.Package;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.orm.dao.ExtensionDAO;
import org.apache.ambari.server.orm.dao.ExtensionLinkDAO;
import org.apache.ambari.server.orm.dao.MetainfoDAO;
import org.apache.ambari.server.orm.dao.StackDAO;
import org.apache.ambari.server.state.CommandScriptDefinition;
import org.apache.ambari.server.state.ComponentInfo;
import org.apache.ambari.server.state.PropertyInfo;
import org.apache.ambari.server.state.RepositoryInfo;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.ServiceOsSpecific;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.server.state.stack.OsFamily;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * StackManager unit tests.
 */
public class StackManagerCommonServicesTest {
    private static StackManager stackManager;

    private static MetainfoDAO metaInfoDao;

    private static StackDAO stackDao;

    private static ExtensionDAO extensionDao;

    private static ExtensionLinkDAO linkDao;

    private static ActionMetadata actionMetadata;

    private static OsFamily osFamily;

    @Test
    public void testGetStacksCount() throws Exception {
        Collection<StackInfo> stacks = StackManagerCommonServicesTest.stackManager.getStacks();
        Assert.assertEquals(2, stacks.size());
    }

    @Test
    public void testGetStacksByName() {
        Collection<StackInfo> stacks = StackManagerCommonServicesTest.stackManager.getStacks("HDP");
        Assert.assertEquals(2, stacks.size());
    }

    @Test
    public void testAddOnServiceRepoIsLoaded() {
        Collection<StackInfo> stacks = StackManagerCommonServicesTest.stackManager.getStacks("HDP");
        StackInfo stack = null;
        for (StackInfo stackInfo : StackManagerCommonServicesTest.stackManager.getStacks()) {
            if ("0.2".equals(stackInfo.getVersion())) {
                stack = stackInfo;
                break;
            }
        }
        List<RepositoryInfo> repos = stack.getRepositoriesByOs().get("redhat6");
        ImmutableSet<String> repoIds = ImmutableSet.copyOf(Lists.transform(repos, GET_REPO_ID_FUNCTION));
        Assert.assertTrue("Repos are expected to contain MSFT_R-8.1", repoIds.contains("ADDON_REPO-1.0"));
    }

    @Test
    public void testGetStack() {
        StackInfo stack = StackManagerCommonServicesTest.stackManager.getStack("HDP", "0.1");
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
        Assert.assertEquals(62, properties.size());
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
        Assert.assertEquals(2, packages.size());
        ServiceOsSpecific.Package pkg = packages.get(0);
        Assert.assertEquals("pig", pkg.getName());
        Assert.assertFalse(pkg.getSkipUpgrade());
        ServiceOsSpecific.Package lzoPackage = packages.get(1);
        Assert.assertEquals("lzo", lzoPackage.getName());
        Assert.assertTrue(lzoPackage.getSkipUpgrade());
        Assert.assertEquals(pigService.getParent(), "common-services/PIG/1.0");
    }

    @Test
    public void testGetServicePackageFolder() {
        StackInfo stack = StackManagerCommonServicesTest.stackManager.getStack("HDP", "0.1");
        Assert.assertNotNull(stack);
        Assert.assertEquals("HDP", stack.getName());
        Assert.assertEquals("0.1", stack.getVersion());
        ServiceInfo hdfsService1 = stack.getService("HDFS");
        Assert.assertNotNull(hdfsService1);
        stack = StackManagerCommonServicesTest.stackManager.getStack("HDP", "0.2");
        Assert.assertNotNull(stack);
        Assert.assertEquals("HDP", stack.getName());
        Assert.assertEquals("0.2", stack.getVersion());
        ServiceInfo hdfsService2 = stack.getService("HDFS");
        Assert.assertNotNull(hdfsService2);
        String packageDir1 = StringUtils.join(new String[]{ "common-services", "HDFS", "1.0", "package" }, File.separator);
        String packageDir2 = StringUtils.join(new String[]{ "stacks_with_common_services", "HDP", "0.2", "services", "HDFS", "package" }, File.separator);
        Assert.assertEquals(packageDir1, hdfsService1.getServicePackageFolder());
        Assert.assertEquals(packageDir2, hdfsService2.getServicePackageFolder());
    }
}

