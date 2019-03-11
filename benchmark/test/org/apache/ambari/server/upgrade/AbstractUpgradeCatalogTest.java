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
package org.apache.ambari.server.upgrade;


import com.google.inject.Injector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.StackId;
import org.junit.Test;


public class AbstractUpgradeCatalogTest {
    private static final String CONFIG_TYPE = "hdfs-site.xml";

    private final String CLUSTER_NAME = "c1";

    private final String SERVICE_NAME = "HDFS";

    private AmbariManagementController amc;

    private ConfigHelper configHelper;

    private Injector injector;

    private Cluster cluster;

    private Clusters clusters;

    private ServiceInfo serviceInfo;

    private Config oldConfig;

    private AbstractUpgradeCatalog upgradeCatalog;

    private HashMap<String, String> oldProperties;

    @Test
    public void shouldAddConfigurationFromXml() throws AmbariException {
        oldProperties.put("prop1", "v1-old");
        Map<String, Map<String, String>> tags = Collections.emptyMap();
        Map<String, String> mergedProperties = new HashMap<>();
        mergedProperties.put("prop1", "v1-old");
        mergedProperties.put("prop4", "v4");
        expect(amc.createConfig(eq(cluster), anyObject(StackId.class), eq("hdfs-site"), eq(mergedProperties), anyString(), eq(tags))).andReturn(null);
        replay(injector, configHelper, amc, cluster, clusters, serviceInfo, oldConfig);
        upgradeCatalog.addNewConfigurationsFromXml();
        verify(configHelper, amc, cluster, clusters, serviceInfo, oldConfig);
    }

    @Test
    public void shouldUpdateConfigurationFromXml() throws AmbariException {
        oldProperties.put("prop1", "v1-old");
        oldProperties.put("prop2", "v2-old");
        oldProperties.put("prop3", "v3-old");
        Map<String, Map<String, String>> tags = Collections.emptyMap();
        Map<String, String> mergedProperties = new HashMap<>();
        mergedProperties.put("prop1", "v1-old");
        mergedProperties.put("prop2", "v2");
        mergedProperties.put("prop3", "v3-old");
        expect(amc.createConfig(eq(cluster), anyObject(StackId.class), eq("hdfs-site"), eq(mergedProperties), anyString(), eq(tags))).andReturn(null);
        replay(injector, configHelper, amc, cluster, clusters, serviceInfo, oldConfig);
        upgradeCatalog.addNewConfigurationsFromXml();
        verify(configHelper, amc, cluster, clusters, serviceInfo, oldConfig);
    }

    @Test
    public void shouldDeleteConfigurationFromXml() throws AmbariException {
        oldProperties.put("prop1", "v1-old");
        oldProperties.put("prop3", "v3-old");
        Map<String, Map<String, String>> tags = Collections.emptyMap();
        Map<String, String> mergedProperties = new HashMap<>();
        mergedProperties.put("prop1", "v1-old");
        expect(amc.createConfig(eq(cluster), anyObject(StackId.class), eq("hdfs-site"), eq(mergedProperties), anyString(), eq(tags))).andReturn(null);
        replay(injector, configHelper, amc, cluster, clusters, serviceInfo, oldConfig);
        upgradeCatalog.addNewConfigurationsFromXml();
        verify(configHelper, amc, cluster, clusters, serviceInfo, oldConfig);
    }
}

