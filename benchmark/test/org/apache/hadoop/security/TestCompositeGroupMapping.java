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
package org.apache.hadoop.security;


import CommonConfigurationKeys.HADOOP_SECURITY_GROUP_MAPPING;
import CompositeGroupsMapping.MAPPING_PROVIDERS_COMBINED_CONFIG_KEY;
import CompositeGroupsMapping.MAPPING_PROVIDERS_CONFIG_KEY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CompositeGroupsMapping.MAPPING_PROVIDER_CONFIG_PREFIX;
import static GroupMappingServiceProvider.GROUP_MAPPING_CONFIG_PREFIX;


public class TestCompositeGroupMapping {
    public static final Logger LOG = LoggerFactory.getLogger(TestCompositeGroupMapping.class);

    private static Configuration conf = new Configuration();

    private static class TestUser {
        String name;

        String group;

        String group2;

        public TestUser(String name, String group) {
            this.name = name;
            this.group = group;
        }

        public TestUser(String name, String group, String group2) {
            this(name, group);
            this.group2 = group2;
        }
    }

    private static TestCompositeGroupMapping.TestUser john = new TestCompositeGroupMapping.TestUser("John", "user-group");

    private static TestCompositeGroupMapping.TestUser hdfs = new TestCompositeGroupMapping.TestUser("hdfs", "supergroup");

    private static TestCompositeGroupMapping.TestUser jack = new TestCompositeGroupMapping.TestUser("Jack", "user-group", "dev-group-1");

    private static final String PROVIDER_SPECIFIC_CONF = ".test.prop";

    private static final String PROVIDER_SPECIFIC_CONF_KEY = (GROUP_MAPPING_CONFIG_PREFIX) + (TestCompositeGroupMapping.PROVIDER_SPECIFIC_CONF);

    private static final String PROVIDER_SPECIFIC_CONF_VALUE_FOR_USER = "value-for-user";

    private static final String PROVIDER_SPECIFIC_CONF_VALUE_FOR_CLUSTER = "value-for-cluster";

    private abstract static class GroupMappingProviderBase implements Configurable , GroupMappingServiceProvider {
        private Configuration conf;

        @Override
        public void setConf(Configuration conf) {
            this.conf = conf;
        }

        @Override
        public Configuration getConf() {
            return this.conf;
        }

        @Override
        public void cacheGroupsRefresh() throws IOException {
        }

        @Override
        public void cacheGroupsAdd(List<String> groups) throws IOException {
        }

        protected List<String> toList(String group) {
            if (group != null) {
                return Arrays.asList(new String[]{ group });
            }
            return new ArrayList<String>();
        }

        protected void checkTestConf(String expectedValue) {
            String configValue = getConf().get(TestCompositeGroupMapping.PROVIDER_SPECIFIC_CONF_KEY);
            if ((configValue == null) || (!(configValue.equals(expectedValue)))) {
                throw new RuntimeException(("Failed to find mandatory configuration of " + (TestCompositeGroupMapping.PROVIDER_SPECIFIC_CONF_KEY)));
            }
        }
    }

    private static class UserProvider extends TestCompositeGroupMapping.GroupMappingProviderBase {
        @Override
        public List<String> getGroups(String user) throws IOException {
            checkTestConf(TestCompositeGroupMapping.PROVIDER_SPECIFIC_CONF_VALUE_FOR_USER);
            String group = null;
            if (user.equals(TestCompositeGroupMapping.john.name)) {
                group = TestCompositeGroupMapping.john.group;
            } else
                if (user.equals(TestCompositeGroupMapping.jack.name)) {
                    group = TestCompositeGroupMapping.jack.group;
                }

            return toList(group);
        }
    }

    private static class ClusterProvider extends TestCompositeGroupMapping.GroupMappingProviderBase {
        @Override
        public List<String> getGroups(String user) throws IOException {
            checkTestConf(TestCompositeGroupMapping.PROVIDER_SPECIFIC_CONF_VALUE_FOR_CLUSTER);
            String group = null;
            if (user.equals(TestCompositeGroupMapping.hdfs.name)) {
                group = TestCompositeGroupMapping.hdfs.group;
            } else
                if (user.equals(TestCompositeGroupMapping.jack.name)) {
                    // jack has another group from clusterProvider
                    group = TestCompositeGroupMapping.jack.group2;
                }

            return toList(group);
        }
    }

    static {
        TestCompositeGroupMapping.conf.setClass(HADOOP_SECURITY_GROUP_MAPPING, CompositeGroupsMapping.class, GroupMappingServiceProvider.class);
        TestCompositeGroupMapping.conf.set(MAPPING_PROVIDERS_CONFIG_KEY, "userProvider,clusterProvider");
        TestCompositeGroupMapping.conf.setClass(((MAPPING_PROVIDER_CONFIG_PREFIX) + ".userProvider"), TestCompositeGroupMapping.UserProvider.class, GroupMappingServiceProvider.class);
        TestCompositeGroupMapping.conf.setClass(((MAPPING_PROVIDER_CONFIG_PREFIX) + ".clusterProvider"), TestCompositeGroupMapping.ClusterProvider.class, GroupMappingServiceProvider.class);
        TestCompositeGroupMapping.conf.set((((MAPPING_PROVIDER_CONFIG_PREFIX) + ".clusterProvider") + (TestCompositeGroupMapping.PROVIDER_SPECIFIC_CONF)), TestCompositeGroupMapping.PROVIDER_SPECIFIC_CONF_VALUE_FOR_CLUSTER);
        TestCompositeGroupMapping.conf.set((((MAPPING_PROVIDER_CONFIG_PREFIX) + ".userProvider") + (TestCompositeGroupMapping.PROVIDER_SPECIFIC_CONF)), TestCompositeGroupMapping.PROVIDER_SPECIFIC_CONF_VALUE_FOR_USER);
    }

    @Test
    public void TestMultipleGroupsMapping() throws Exception {
        Groups groups = new Groups(TestCompositeGroupMapping.conf);
        Assert.assertTrue(groups.getGroups(TestCompositeGroupMapping.john.name).get(0).equals(TestCompositeGroupMapping.john.group));
        Assert.assertTrue(groups.getGroups(TestCompositeGroupMapping.hdfs.name).get(0).equals(TestCompositeGroupMapping.hdfs.group));
    }

    @Test
    public void TestMultipleGroupsMappingWithCombined() throws Exception {
        TestCompositeGroupMapping.conf.set(MAPPING_PROVIDERS_COMBINED_CONFIG_KEY, "true");
        Groups groups = new Groups(TestCompositeGroupMapping.conf);
        Assert.assertTrue(((groups.getGroups(TestCompositeGroupMapping.jack.name).size()) == 2));
        // the configured providers list in order is "userProvider,clusterProvider"
        // group -> userProvider, group2 -> clusterProvider
        Assert.assertTrue(groups.getGroups(TestCompositeGroupMapping.jack.name).contains(TestCompositeGroupMapping.jack.group));
        Assert.assertTrue(groups.getGroups(TestCompositeGroupMapping.jack.name).contains(TestCompositeGroupMapping.jack.group2));
    }

    @Test
    public void TestMultipleGroupsMappingWithoutCombined() throws Exception {
        TestCompositeGroupMapping.conf.set(MAPPING_PROVIDERS_COMBINED_CONFIG_KEY, "false");
        Groups groups = new Groups(TestCompositeGroupMapping.conf);
        // the configured providers list in order is "userProvider,clusterProvider"
        // group -> userProvider, group2 -> clusterProvider
        Assert.assertTrue(((groups.getGroups(TestCompositeGroupMapping.jack.name).size()) == 1));
        Assert.assertTrue(groups.getGroups(TestCompositeGroupMapping.jack.name).get(0).equals(TestCompositeGroupMapping.jack.group));
    }
}

