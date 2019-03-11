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
package org.apache.ambari.server.state;


import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.state.ConfigMergeHelper.ThreeWayValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link ConfigMergeHelper} class
 */
public class ConfigMergeHelperTest {
    private static final StackId currentStackId = new StackId("HDP-2.1.1");

    private static final StackId newStackId = new StackId("HPD-2.2.0");

    private Injector injector;

    private Clusters clustersMock;

    private AmbariMetaInfo ambariMetaInfoMock;

    @Test
    public void testGetConflicts() throws Exception {
        Cluster clusterMock = createNiceMock(Cluster.class);
        expect(clustersMock.getCluster(anyString())).andReturn(clusterMock);
        expect(clusterMock.getCurrentStackVersion()).andReturn(ConfigMergeHelperTest.currentStackId);
        expect(clusterMock.getServices()).andReturn(new HashMap<String, Service>() {
            {
                put("HDFS", createNiceMock(Service.class));
                put("ZK", createNiceMock(Service.class));
            }
        });
        Set<PropertyInfo> currentHDFSProperties = new HashSet<PropertyInfo>() {
            {
                add(createPropertyInfo("hdfs-env.xml", "equal.key", "equal-value"));
            }
        };
        Set<PropertyInfo> currentZKProperties = new HashSet<PropertyInfo>() {
            {
                add(createPropertyInfo("zk-env.xml", "different.key", "different-value-1"));
            }
        };
        Set<PropertyInfo> currentStackProperties = new HashSet<PropertyInfo>() {
            {
                add(createPropertyInfo("hadoop-env.xml", "equal.key", "modified.value"));
            }
        };
        expect(ambariMetaInfoMock.getServiceProperties(ConfigMergeHelperTest.currentStackId.getStackName(), ConfigMergeHelperTest.currentStackId.getStackVersion(), "HDFS")).andReturn(currentHDFSProperties);
        expect(ambariMetaInfoMock.getServiceProperties(ConfigMergeHelperTest.currentStackId.getStackName(), ConfigMergeHelperTest.currentStackId.getStackVersion(), "ZK")).andReturn(currentZKProperties);
        expect(ambariMetaInfoMock.getStackProperties(ConfigMergeHelperTest.currentStackId.getStackName(), ConfigMergeHelperTest.currentStackId.getStackVersion())).andReturn(currentStackProperties);
        Set<PropertyInfo> newHDFSProperties = new HashSet<PropertyInfo>() {
            {
                add(createPropertyInfo("hdfs-env.xml", "equal.key", "equal-value"));
                add(createPropertyInfo("new-hdfs-config.xml", "equal.key", "equal-value"));
            }
        };
        Set<PropertyInfo> newZKProperties = new HashSet<PropertyInfo>() {
            {
                add(createPropertyInfo("zk-env.xml", "equal.key", "different-value-2"));
                add(createPropertyInfo("zk-env.xml", "new.key", "new-value-2"));
            }
        };
        Set<PropertyInfo> newStackProperties = new HashSet<PropertyInfo>() {
            {
                add(createPropertyInfo("hadoop-env.xml", "equal.key", "another.value"));
            }
        };
        expect(ambariMetaInfoMock.getServiceProperties(ConfigMergeHelperTest.newStackId.getStackName(), ConfigMergeHelperTest.newStackId.getStackVersion(), "HDFS")).andReturn(newHDFSProperties);
        expect(ambariMetaInfoMock.getServiceProperties(ConfigMergeHelperTest.newStackId.getStackName(), ConfigMergeHelperTest.newStackId.getStackVersion(), "ZK")).andReturn(newZKProperties);
        expect(ambariMetaInfoMock.getStackProperties(ConfigMergeHelperTest.newStackId.getStackName(), ConfigMergeHelperTest.newStackId.getStackVersion())).andReturn(newStackProperties);
        // desired config of hdfs-env.xml
        Map<String, String> desiredHdfsEnvProperties = new HashMap<>();
        expect(clusterMock.getDesiredConfigByType("hdfs-env.xml")).andReturn(createConfigMock(desiredHdfsEnvProperties));
        // desired config of zk-env.xml
        Map<String, String> desiredZkEnvProperties = new HashMap<>();
        expect(clusterMock.getDesiredConfigByType("hdfs-env.xml")).andReturn(createConfigMock(desiredZkEnvProperties));
        // desired config of hadoop-env.xml
        Map<String, String> desiredHadoopEnvProperties = new HashMap<>();
        expect(clusterMock.getDesiredConfigByType("hadoop-env.xml")).andReturn(createConfigMock(desiredHadoopEnvProperties));
        replay(clusterMock, clustersMock, ambariMetaInfoMock);
        ConfigMergeHelper configMergeHelper = injector.getInstance(ConfigMergeHelper.class);
        Map<String, Map<String, ThreeWayValue>> conflicts = configMergeHelper.getConflicts("clustername", ConfigMergeHelperTest.newStackId);
        Assert.assertNotNull(conflicts);
        Assert.assertEquals(2, conflicts.size());
        for (String key : conflicts.keySet()) {
            if (key.equals("hdfs-env")) {
                Map<String, ThreeWayValue> stringThreeWayValueMap = conflicts.get(key);
                Assert.assertEquals(1, stringThreeWayValueMap.size());
                Assert.assertEquals("equal-value", stringThreeWayValueMap.get("equal.key").oldStackValue);
                Assert.assertEquals("equal-value", stringThreeWayValueMap.get("equal.key").newStackValue);
                Assert.assertEquals("", stringThreeWayValueMap.get("equal.key").savedValue);
            } else
                if (key.equals("hadoop-env")) {
                    Map<String, ThreeWayValue> stringThreeWayValueMap = conflicts.get(key);
                    Assert.assertEquals(1, stringThreeWayValueMap.size());
                    Assert.assertEquals("modified.value", stringThreeWayValueMap.get("equal.key").oldStackValue);
                    Assert.assertEquals("another.value", stringThreeWayValueMap.get("equal.key").newStackValue);
                    Assert.assertEquals("", stringThreeWayValueMap.get("equal.key").savedValue);
                } else {
                    Assert.fail("Unexpected key");
                }

        }
        Assert.assertEquals(2, conflicts.size());
    }

    @Test
    public void testNormalizeValue() throws Exception {
        // If template not defined
        String normalizedValue = ConfigMergeHelper.normalizeValue(null, "2048m");
        Assert.assertEquals("2048m", normalizedValue);
        // Template does not define heap
        normalizedValue = ConfigMergeHelper.normalizeValue("3k", "2048");
        Assert.assertEquals("2048", normalizedValue);
        // Template - megabytes
        normalizedValue = ConfigMergeHelper.normalizeValue("1024m", "2048");
        Assert.assertEquals("2048m", normalizedValue);
        normalizedValue = ConfigMergeHelper.normalizeValue("1024M", "2048");
        Assert.assertEquals("2048M", normalizedValue);
        // Template - gigabytes
        normalizedValue = ConfigMergeHelper.normalizeValue("4g", "2");
        Assert.assertEquals("2g", normalizedValue);
        normalizedValue = ConfigMergeHelper.normalizeValue("4G", "2");
        Assert.assertEquals("2G", normalizedValue);
    }

    private class MockModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(Clusters.class).toInstance(clustersMock);
            binder.bind(AmbariMetaInfo.class).toInstance(ambariMetaInfoMock);
        }
    }
}

