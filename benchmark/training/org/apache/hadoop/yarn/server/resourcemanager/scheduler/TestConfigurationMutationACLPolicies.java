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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler;


import YarnConfiguration.RM_SCHEDULER_MUTATION_ACL_POLICY_CLASS;
import YarnConfiguration.YARN_ADMIN_ACL;
import java.util.Collections;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.conf.QueueAdminConfigurationMutationACLPolicy;
import org.apache.hadoop.yarn.webapp.dao.QueueConfigInfo;
import org.apache.hadoop.yarn.webapp.dao.SchedConfUpdateInfo;
import org.junit.Assert;
import org.junit.Test;


public class TestConfigurationMutationACLPolicies {
    private ConfigurationMutationACLPolicy policy;

    private RMContext rmContext;

    private MutableConfScheduler scheduler;

    private static final UserGroupInformation GOOD_USER = UserGroupInformation.createUserForTesting("goodUser", new String[]{  });

    private static final UserGroupInformation BAD_USER = UserGroupInformation.createUserForTesting("badUser", new String[]{  });

    private static final Map<String, String> EMPTY_MAP = Collections.<String, String>emptyMap();

    @Test
    public void testDefaultPolicy() {
        Configuration conf = new Configuration();
        conf.set(YARN_ADMIN_ACL, TestConfigurationMutationACLPolicies.GOOD_USER.getShortUserName());
        conf.setClass(RM_SCHEDULER_MUTATION_ACL_POLICY_CLASS, DefaultConfigurationMutationACLPolicy.class, ConfigurationMutationACLPolicy.class);
        policy = ConfigurationMutationACLPolicyFactory.getPolicy(conf);
        policy.init(conf, rmContext);
        Assert.assertTrue(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.GOOD_USER, null));
        Assert.assertFalse(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.BAD_USER, null));
    }

    @Test
    public void testQueueAdminBasedPolicy() {
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER_MUTATION_ACL_POLICY_CLASS, QueueAdminConfigurationMutationACLPolicy.class, ConfigurationMutationACLPolicy.class);
        policy = ConfigurationMutationACLPolicyFactory.getPolicy(conf);
        policy.init(conf, rmContext);
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        QueueConfigInfo configInfo = new QueueConfigInfo("root.a", TestConfigurationMutationACLPolicies.EMPTY_MAP);
        updateInfo.getUpdateQueueInfo().add(configInfo);
        Assert.assertTrue(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.GOOD_USER, updateInfo));
        Assert.assertFalse(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.BAD_USER, updateInfo));
    }

    @Test
    public void testQueueAdminPolicyAddQueue() {
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER_MUTATION_ACL_POLICY_CLASS, QueueAdminConfigurationMutationACLPolicy.class, ConfigurationMutationACLPolicy.class);
        policy = ConfigurationMutationACLPolicyFactory.getPolicy(conf);
        policy.init(conf, rmContext);
        // Add root.b.b1. Should check ACL of root.b queue.
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        QueueConfigInfo configInfo = new QueueConfigInfo("root.b.b2", TestConfigurationMutationACLPolicies.EMPTY_MAP);
        updateInfo.getAddQueueInfo().add(configInfo);
        Assert.assertTrue(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.GOOD_USER, updateInfo));
        Assert.assertFalse(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.BAD_USER, updateInfo));
    }

    @Test
    public void testQueueAdminPolicyAddNestedQueue() {
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER_MUTATION_ACL_POLICY_CLASS, QueueAdminConfigurationMutationACLPolicy.class, ConfigurationMutationACLPolicy.class);
        policy = ConfigurationMutationACLPolicyFactory.getPolicy(conf);
        policy.init(conf, rmContext);
        // Add root.b.b1.b11. Should check ACL of root.b queue.
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        QueueConfigInfo configInfo = new QueueConfigInfo("root.b.b2.b21", TestConfigurationMutationACLPolicies.EMPTY_MAP);
        updateInfo.getAddQueueInfo().add(configInfo);
        Assert.assertTrue(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.GOOD_USER, updateInfo));
        Assert.assertFalse(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.BAD_USER, updateInfo));
    }

    @Test
    public void testQueueAdminPolicyRemoveQueue() {
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER_MUTATION_ACL_POLICY_CLASS, QueueAdminConfigurationMutationACLPolicy.class, ConfigurationMutationACLPolicy.class);
        policy = ConfigurationMutationACLPolicyFactory.getPolicy(conf);
        policy.init(conf, rmContext);
        // Remove root.b.b1.
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        updateInfo.getRemoveQueueInfo().add("root.b.b1");
        Assert.assertTrue(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.GOOD_USER, updateInfo));
        Assert.assertFalse(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.BAD_USER, updateInfo));
    }

    @Test
    public void testQueueAdminPolicyGlobal() {
        Configuration conf = new Configuration();
        conf.set(YARN_ADMIN_ACL, TestConfigurationMutationACLPolicies.GOOD_USER.getShortUserName());
        conf.setClass(RM_SCHEDULER_MUTATION_ACL_POLICY_CLASS, QueueAdminConfigurationMutationACLPolicy.class, ConfigurationMutationACLPolicy.class);
        policy = ConfigurationMutationACLPolicyFactory.getPolicy(conf);
        policy.init(conf, rmContext);
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        Assert.assertTrue(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.GOOD_USER, updateInfo));
        Assert.assertTrue(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.BAD_USER, updateInfo));
        updateInfo.getGlobalParams().put("globalKey", "globalValue");
        Assert.assertTrue(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.GOOD_USER, updateInfo));
        Assert.assertFalse(policy.isMutationAllowed(TestConfigurationMutationACLPolicies.BAD_USER, updateInfo));
    }
}

