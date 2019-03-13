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
package org.apache.ambari.server.state.stack;


import ConfigUpgradeChangeDefinition.Transfer;
import TransferOperation.COPY;
import TransferOperation.DELETE;
import TransferOperation.MOVE;
import category.StackUpgradeTest;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Map;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.stack.upgrade.ConfigUpgradeChangeDefinition;
import org.apache.ambari.server.stack.upgrade.ConfigUpgradePack;
import org.apache.ambari.server.stack.upgrade.ConfigUpgradePack.AffectedComponent;
import org.apache.ambari.server.stack.upgrade.ConfigUpgradePack.AffectedService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for the config upgrade pack
 */
@Category({ StackUpgradeTest.class })
public class ConfigUpgradePackTest {
    private Injector injector;

    private AmbariMetaInfo ambariMetaInfo;

    @Test
    public void testMerge() {
        // Generate test data - 3 config upgrade packs, 2 services, 2 components, 2 config changes each
        ArrayList<ConfigUpgradePack> cups = new ArrayList<>();
        for (int cupIndex = 0; cupIndex < 3; cupIndex++) {
            ArrayList<AffectedService> services = new ArrayList<>();
            for (int serviceIndex = 0; serviceIndex < 2; serviceIndex++) {
                String serviceName;
                if (serviceIndex == 0) {
                    serviceName = "HDFS";// For checking merge of existing services

                } else {
                    serviceName = String.format("SOME_SERVICE_%s", cupIndex);
                }
                ArrayList<AffectedComponent> components = new ArrayList<>();
                for (int componentIndex = 0; componentIndex < 2; componentIndex++) {
                    String componentName;
                    if (componentIndex == 0) {
                        componentName = "NAMENODE";// For checking merge of existing components

                    } else {
                        componentName = "SOME_COMPONENT_" + cupIndex;
                    }
                    ArrayList<ConfigUpgradeChangeDefinition> changeDefinitions = new ArrayList<>();
                    for (int changeIndex = 0; changeIndex < 2; changeIndex++) {
                        String change_id = String.format("CHANGE_%s_%s_%s_%s", cupIndex, serviceIndex, componentIndex, changeIndex);
                        ConfigUpgradeChangeDefinition changeDefinition = new ConfigUpgradeChangeDefinition();
                        changeDefinition.id = change_id;
                        changeDefinitions.add(changeDefinition);
                    }
                    AffectedComponent component = new AffectedComponent();
                    component.name = componentName;
                    component.changes = changeDefinitions;
                    components.add(component);
                }
                AffectedService service = new AffectedService();
                service.name = serviceName;
                service.components = components;
                services.add(service);
            }
            ConfigUpgradePack cupI = new ConfigUpgradePack();
            cupI.services = services;
            cups.add(cupI);
        }
        // Merge
        ConfigUpgradePack result = ConfigUpgradePack.merge(cups);
        // Check test results
        Assert.assertEquals(result.enumerateConfigChangesByID().entrySet().size(), 24);
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("NAMENODE").changes.get(0).id, "CHANGE_0_0_0_0");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("NAMENODE").changes.get(1).id, "CHANGE_0_0_0_1");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("NAMENODE").changes.get(2).id, "CHANGE_1_0_0_0");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("NAMENODE").changes.get(3).id, "CHANGE_1_0_0_1");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("NAMENODE").changes.get(4).id, "CHANGE_2_0_0_0");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("NAMENODE").changes.get(5).id, "CHANGE_2_0_0_1");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("SOME_COMPONENT_0").changes.get(0).id, "CHANGE_0_0_1_0");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("SOME_COMPONENT_0").changes.get(1).id, "CHANGE_0_0_1_1");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("SOME_COMPONENT_1").changes.get(0).id, "CHANGE_1_0_1_0");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("SOME_COMPONENT_1").changes.get(1).id, "CHANGE_1_0_1_1");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("SOME_COMPONENT_2").changes.get(0).id, "CHANGE_2_0_1_0");
        Assert.assertEquals(result.getServiceMap().get("HDFS").getComponentMap().get("SOME_COMPONENT_2").changes.get(1).id, "CHANGE_2_0_1_1");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_0").getComponentMap().get("NAMENODE").changes.get(0).id, "CHANGE_0_1_0_0");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_0").getComponentMap().get("NAMENODE").changes.get(1).id, "CHANGE_0_1_0_1");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_0").getComponentMap().get("SOME_COMPONENT_0").changes.get(0).id, "CHANGE_0_1_1_0");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_0").getComponentMap().get("SOME_COMPONENT_0").changes.get(1).id, "CHANGE_0_1_1_1");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_1").getComponentMap().get("NAMENODE").changes.get(0).id, "CHANGE_1_1_0_0");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_1").getComponentMap().get("NAMENODE").changes.get(1).id, "CHANGE_1_1_0_1");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_1").getComponentMap().get("SOME_COMPONENT_1").changes.get(0).id, "CHANGE_1_1_1_0");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_1").getComponentMap().get("SOME_COMPONENT_1").changes.get(1).id, "CHANGE_1_1_1_1");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_2").getComponentMap().get("NAMENODE").changes.get(0).id, "CHANGE_2_1_0_0");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_2").getComponentMap().get("NAMENODE").changes.get(1).id, "CHANGE_2_1_0_1");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_2").getComponentMap().get("SOME_COMPONENT_2").changes.get(0).id, "CHANGE_2_1_1_0");
        Assert.assertEquals(result.getServiceMap().get("SOME_SERVICE_2").getComponentMap().get("SOME_COMPONENT_2").changes.get(1).id, "CHANGE_2_1_1_1");
    }

    @Test
    public void testConfigUpgradeDefinitionParsing() throws Exception {
        ConfigUpgradePack cup = ambariMetaInfo.getConfigUpgradePack("HDP", "2.1.1");
        Map<String, ConfigUpgradeChangeDefinition> changesByID = cup.enumerateConfigChangesByID();
        ConfigUpgradeChangeDefinition hdp_2_1_1_nm_pre_upgrade = changesByID.get("hdp_2_1_1_nm_pre_upgrade");
        Assert.assertEquals("core-site", hdp_2_1_1_nm_pre_upgrade.getConfigType());
        Assert.assertEquals(4, hdp_2_1_1_nm_pre_upgrade.getTransfers().size());
        /* <transfer operation="COPY" from-key="copy-key" to-key="copy-key-to" />
        <transfer operation="COPY" from-type="my-site" from-key="my-copy-key" to-key="my-copy-key-to" />
        <transfer operation="MOVE" from-key="move-key" to-key="move-key-to" />
        <transfer operation="DELETE" delete-key="delete-key">
        <keep-key>important-key</keep-key>
        </transfer>
         */
        ConfigUpgradeChangeDefinition.Transfer t1 = hdp_2_1_1_nm_pre_upgrade.getTransfers().get(0);
        Assert.assertEquals(COPY, t1.operation);
        Assert.assertEquals("copy-key", t1.fromKey);
        Assert.assertEquals("copy-key-to", t1.toKey);
        ConfigUpgradeChangeDefinition.Transfer t2 = hdp_2_1_1_nm_pre_upgrade.getTransfers().get(1);
        Assert.assertEquals(COPY, t2.operation);
        Assert.assertEquals("my-site", t2.fromType);
        Assert.assertEquals("my-copy-key", t2.fromKey);
        Assert.assertEquals("my-copy-key-to", t2.toKey);
        Assert.assertTrue(t2.keepKeys.isEmpty());
        ConfigUpgradeChangeDefinition.Transfer t3 = hdp_2_1_1_nm_pre_upgrade.getTransfers().get(2);
        Assert.assertEquals(MOVE, t3.operation);
        Assert.assertEquals("move-key", t3.fromKey);
        Assert.assertEquals("move-key-to", t3.toKey);
        ConfigUpgradeChangeDefinition.Transfer t4 = hdp_2_1_1_nm_pre_upgrade.getTransfers().get(3);
        Assert.assertEquals(DELETE, t4.operation);
        Assert.assertEquals("delete-key", t4.deleteKey);
        Assert.assertNull(t4.toKey);
        Assert.assertTrue(t4.preserveEdits);
        Assert.assertEquals(1, t4.keepKeys.size());
        Assert.assertEquals("important-key", t4.keepKeys.get(0));
    }
}

