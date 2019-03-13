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
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY;
import DFSConfigKeys.DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY_DEFAULT;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicyInfo;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Test that ErasureCodingPolicyManager correctly parses the set of enabled
 * erasure coding policies from configuration and exposes this information.
 */
public class TestEnabledECPolicies {
    @Rule
    public Timeout testTimeout = new Timeout(60000);

    @Test
    public void testDefaultPolicy() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        String defaultECPolicies = conf.get(DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY, DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY_DEFAULT);
        expectValidPolicy(defaultECPolicies, 1);
    }

    @Test
    public void testInvalid() throws Exception {
        // Test first with an invalid policy
        expectInvalidPolicy("not-a-policy");
        // Test with an invalid policy and a valid policy
        expectInvalidPolicy(("not-a-policy," + (StripedFileTestUtil.getDefaultECPolicy().getName())));
        // Test with a valid and an invalid policy
        expectInvalidPolicy(((StripedFileTestUtil.getDefaultECPolicy().getName()) + ", not-a-policy"));
        // Some more invalid values
        expectInvalidPolicy("not-a-policy, ");
        expectInvalidPolicy("     ,not-a-policy, ");
    }

    @Test
    public void testValid() throws Exception {
        String ecPolicyName = StripedFileTestUtil.getDefaultECPolicy().getName();
        expectValidPolicy(ecPolicyName, 1);
    }

    @Test
    public void testGetPolicies() throws Exception {
        ErasureCodingPolicy[] enabledPolicies;
        // Enable no policies
        enabledPolicies = new ErasureCodingPolicy[]{  };
        testGetPolicies(enabledPolicies);
        // Enable one policy
        enabledPolicies = new ErasureCodingPolicy[]{ SystemErasureCodingPolicies.getPolicies().get(1) };
        testGetPolicies(enabledPolicies);
        // Enable two policies
        enabledPolicies = new ErasureCodingPolicy[]{ SystemErasureCodingPolicies.getPolicies().get(1), SystemErasureCodingPolicies.getPolicies().get(2) };
        testGetPolicies(enabledPolicies);
    }

    @Test
    public void testChangeDefaultPolicy() throws Exception {
        final HdfsConfiguration conf = new HdfsConfiguration();
        final String testPolicy = "RS-3-2-1024k";
        final String defaultPolicy = conf.getTrimmed(DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY, DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY_DEFAULT);
        Assert.assertNotEquals(("The default policy and the next default policy " + "should not be the same!"), testPolicy, defaultPolicy);
        ErasureCodingPolicyManager manager = ErasureCodingPolicyManager.getInstance();
        // Change the default policy to a new one
        conf.set(DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY, testPolicy);
        manager.init(conf);
        // Load policies similar to when fsimage is loaded at namenode startup
        manager.loadPolicies(constructAllDisabledInitialPolicies(), conf);
        ErasureCodingPolicyInfo[] getPoliciesResult = manager.getPolicies();
        boolean isEnabled = isPolicyEnabled(testPolicy, getPoliciesResult);
        Assert.assertTrue(("The new default policy should be " + "in enabled state!"), isEnabled);
        ErasureCodingPolicyInfo[] getPersistedPoliciesResult = manager.getPersistedPolicies();
        isEnabled = isPolicyEnabled(testPolicy, getPersistedPoliciesResult);
        Assert.assertFalse(("The new default policy should be " + "in disabled state in the persisted list!"), isEnabled);
        manager.disablePolicy(testPolicy);
        getPoliciesResult = manager.getPolicies();
        isEnabled = isPolicyEnabled(testPolicy, getPoliciesResult);
        Assert.assertFalse(("The new default policy should be " + "in disabled state!"), isEnabled);
        getPersistedPoliciesResult = manager.getPersistedPolicies();
        isEnabled = isPolicyEnabled(testPolicy, getPersistedPoliciesResult);
        Assert.assertFalse(("The new default policy should be " + "in disabled state in the persisted list!"), isEnabled);
        manager.enablePolicy(testPolicy);
        getPoliciesResult = manager.getPolicies();
        isEnabled = isPolicyEnabled(testPolicy, getPoliciesResult);
        Assert.assertTrue(("The new default policy should be " + "in enabled state!"), isEnabled);
        getPersistedPoliciesResult = manager.getPersistedPolicies();
        isEnabled = isPolicyEnabled(testPolicy, getPersistedPoliciesResult);
        Assert.assertTrue(("The new default policy should be " + "in enabled state in the persisted list!"), isEnabled);
        final String emptyPolicy = "";
        // Change the default policy to a empty
        conf.set(DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY, emptyPolicy);
        manager.init(conf);
        // Load policies similar to when fsimage is loaded at namenode startup
        manager.loadPolicies(constructAllDisabledInitialPolicies(), conf);
        // All the policies are disabled if the default policy is empty
        getPoliciesResult = manager.getPolicies();
        assertAllPoliciesAreDisabled(getPoliciesResult);
    }
}

