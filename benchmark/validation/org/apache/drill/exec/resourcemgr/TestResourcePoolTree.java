/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.resourcemgr;


import ExecConstants.RM_QUERY_TAGS_KEY;
import OptionValue.AccessibleScopes.SESSION_AND_QUERY;
import OptionValue.OptionScope.SESSION;
import ResourcePoolImpl.POOL_CHILDREN_POOLS_KEY;
import ResourcePoolImpl.POOL_MEMORY_SHARE_KEY;
import ResourcePoolImpl.POOL_NAME_KEY;
import ResourcePoolImpl.POOL_QUEUE_KEY;
import ResourcePoolImpl.POOL_SELECTOR_KEY;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.drill.categories.ResourceManagerTest;
import org.apache.drill.exec.ops.QueryContext;
import org.apache.drill.exec.resourcemgr.config.QueueAssignmentResult;
import org.apache.drill.exec.resourcemgr.config.RMCommonDefaults;
import org.apache.drill.exec.resourcemgr.config.ResourcePool;
import org.apache.drill.exec.resourcemgr.config.ResourcePoolTree;
import org.apache.drill.exec.resourcemgr.config.exception.RMConfigException;
import org.apache.drill.exec.server.options.OptionValue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(ResourceManagerTest.class)
public final class TestResourcePoolTree {
    private static final Map<String, Object> poolTreeConfig = new HashMap<>();

    private static final Map<String, Object> pool1 = new HashMap<>();

    private static final Map<String, Object> pool2 = new HashMap<>();

    private static final Map<String, Object> queue1 = new HashMap<>();

    private static final List<Object> childResourcePools = new ArrayList<>();

    private static final Map<String, Object> tagSelectorConfig1 = new HashMap<>();

    private static final Map<String, Object> tagSelectorConfig2 = new HashMap<>();

    private static final QueryContext mockContext = Mockito.mock(QueryContext.class);

    @Test
    public void testTreeWith2LeafPool() throws Exception {
        // pool with tag selector
        TestResourcePoolTree.pool1.put(POOL_QUEUE_KEY, TestResourcePoolTree.queue1);
        TestResourcePoolTree.pool1.put(POOL_SELECTOR_KEY, TestResourcePoolTree.tagSelectorConfig1);
        // pool with default selector
        TestResourcePoolTree.pool2.put(POOL_QUEUE_KEY, TestResourcePoolTree.queue1);
        TestResourcePoolTree.childResourcePools.add(TestResourcePoolTree.pool1);
        TestResourcePoolTree.childResourcePools.add(TestResourcePoolTree.pool2);
        ResourcePoolTree configTree = getPoolTreeConfig();
        // get all leaf queues names
        Set<String> expectedLeafQueue = new HashSet<>();
        expectedLeafQueue.add(((String) (TestResourcePoolTree.pool1.get("pool_name"))));
        expectedLeafQueue.add(((String) (TestResourcePoolTree.pool2.get("pool_name"))));
        Assert.assertEquals("Root pool is different than expected", "drill", configTree.getRootPool().getPoolName());
        Assert.assertEquals("Expected and actual leaf queue names are different", expectedLeafQueue, configTree.getAllLeafQueues().keySet());
        Assert.assertEquals("Unexpected Selection policy is in use", RMCommonDefaults.ROOT_POOL_DEFAULT_QUEUE_SELECTION_POLICY, configTree.getSelectionPolicyInUse().getSelectionPolicy());
    }

    @Test(expected = RMConfigException.class)
    public void testDuplicateLeafPool() throws Exception {
        // leaf pool
        TestResourcePoolTree.pool1.put(POOL_QUEUE_KEY, TestResourcePoolTree.queue1);
        TestResourcePoolTree.childResourcePools.add(TestResourcePoolTree.pool1);
        TestResourcePoolTree.childResourcePools.add(TestResourcePoolTree.pool1);
        getPoolTreeConfig();
    }

    @Test(expected = RMConfigException.class)
    public void testMissingQueueAtLeafPool() throws Exception {
        // leaf pool with queue
        TestResourcePoolTree.pool1.put(POOL_QUEUE_KEY, TestResourcePoolTree.queue1);
        TestResourcePoolTree.pool1.put(POOL_SELECTOR_KEY, TestResourcePoolTree.tagSelectorConfig1);
        TestResourcePoolTree.childResourcePools.add(TestResourcePoolTree.pool1);
        TestResourcePoolTree.childResourcePools.add(TestResourcePoolTree.pool2);
        getPoolTreeConfig();
    }

    @Test(expected = RMConfigException.class)
    public void testInvalidQueueAtLeafPool() throws Exception {
        // leaf pool with invalid queue
        int initialValue = ((Integer) (TestResourcePoolTree.queue1.remove("max_query_memory_per_node")));
        try {
            TestResourcePoolTree.pool1.put(POOL_QUEUE_KEY, TestResourcePoolTree.queue1);
            TestResourcePoolTree.pool1.put(POOL_SELECTOR_KEY, TestResourcePoolTree.tagSelectorConfig1);
            TestResourcePoolTree.childResourcePools.add(TestResourcePoolTree.pool1);
            getPoolTreeConfig();
        } finally {
            TestResourcePoolTree.queue1.put("max_query_memory_per_node", initialValue);
        }
    }

    @Test
    public void testRootPoolAsLeaf() throws Exception {
        // leaf pool with queue
        TestResourcePoolTree.poolTreeConfig.put(POOL_NAME_KEY, "drill");
        TestResourcePoolTree.poolTreeConfig.put(POOL_QUEUE_KEY, TestResourcePoolTree.queue1);
        TestResourcePoolTree.poolTreeConfig.put(POOL_SELECTOR_KEY, TestResourcePoolTree.tagSelectorConfig1);
        Config rmConfig = ConfigFactory.empty().withValue("drill.exec.rm", ConfigValueFactory.fromMap(TestResourcePoolTree.poolTreeConfig));
        ResourcePoolTree poolTree = new org.apache.drill.exec.resourcemgr.config.ResourcePoolTreeImpl(rmConfig, 10000, 10, 2);
        Assert.assertTrue("Root pool is not a leaf pool", poolTree.getRootPool().isLeafPool());
        Assert.assertEquals("Root pool name is not drill", "drill", poolTree.getRootPool().getPoolName());
        Assert.assertTrue("Root pool is not the only leaf pool", ((poolTree.getAllLeafQueues().size()) == 1));
        Assert.assertTrue("Root pool name is not same as leaf pool name", poolTree.getAllLeafQueues().containsKey("drill"));
        Assert.assertFalse("Root pool should not be a default pool", poolTree.getRootPool().isDefaultPool());
    }

    @Test
    public void testTreeWithLeafAndIntermediatePool() throws Exception {
        // left leaf pool1 with tag selector
        TestResourcePoolTree.pool1.put(POOL_QUEUE_KEY, TestResourcePoolTree.queue1);
        TestResourcePoolTree.pool1.put(POOL_SELECTOR_KEY, TestResourcePoolTree.tagSelectorConfig1);
        // left leaf pool2 with default selector
        TestResourcePoolTree.pool2.put(POOL_QUEUE_KEY, TestResourcePoolTree.queue1);
        // intermediate left pool1 with 2 leaf pools (pool1, pool2)
        Map<String, Object> interPool1 = new HashMap<>();
        List<Object> childPools1 = new ArrayList<>();
        childPools1.add(TestResourcePoolTree.pool1);
        childPools1.add(TestResourcePoolTree.pool2);
        interPool1.put(POOL_NAME_KEY, "eng");
        interPool1.put(POOL_MEMORY_SHARE_KEY, 0.9);
        interPool1.put(POOL_CHILDREN_POOLS_KEY, childPools1);
        // right leaf pool
        Map<String, Object> rightLeafPool = new HashMap<>();
        rightLeafPool.put(POOL_NAME_KEY, "marketing");
        rightLeafPool.put(POOL_MEMORY_SHARE_KEY, 0.1);
        rightLeafPool.put(POOL_QUEUE_KEY, TestResourcePoolTree.queue1);
        rightLeafPool.put(POOL_SELECTOR_KEY, TestResourcePoolTree.tagSelectorConfig2);
        TestResourcePoolTree.childResourcePools.add(interPool1);
        TestResourcePoolTree.childResourcePools.add(rightLeafPool);
        ResourcePoolTree configTree = getPoolTreeConfig();
        // Test successful selection of all leaf pools
        OptionValue testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small,large", SESSION);
        Mockito.when(TestResourcePoolTree.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        QueueAssignmentResult assignmentResult = configTree.selectAllQueues(TestResourcePoolTree.mockContext);
        List<ResourcePool> selectedPools = assignmentResult.getSelectedLeafPools();
        List<String> expectedPools = new ArrayList<>();
        expectedPools.add("dev");
        expectedPools.add("qa");
        expectedPools.add("marketing");
        Assert.assertTrue("All leaf pools are not selected", ((selectedPools.size()) == 3));
        Assert.assertTrue("Selected leaf pools and expected pools are different", checkExpectedVsActualPools(selectedPools, expectedPools));
        // Test successful selection of multiple leaf pools
        expectedPools.clear();
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "small", SESSION);
        Mockito.when(TestResourcePoolTree.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        assignmentResult = configTree.selectAllQueues(TestResourcePoolTree.mockContext);
        selectedPools = assignmentResult.getSelectedLeafPools();
        expectedPools.add("qa");
        expectedPools.add("dev");
        Assert.assertTrue("Expected 2 pools to be selected", ((selectedPools.size()) == 2));
        Assert.assertTrue("Selected leaf pools and expected pools are different", checkExpectedVsActualPools(selectedPools, expectedPools));
        // Test successful selection of only left default pool
        expectedPools.clear();
        testOption = OptionValue.create(SESSION_AND_QUERY, RM_QUERY_TAGS_KEY, "medium", SESSION);
        Mockito.when(TestResourcePoolTree.mockContext.getOption(RM_QUERY_TAGS_KEY)).thenReturn(testOption);
        assignmentResult = configTree.selectAllQueues(TestResourcePoolTree.mockContext);
        selectedPools = assignmentResult.getSelectedLeafPools();
        expectedPools.add("qa");
        Assert.assertTrue("More than one leaf pool is selected", ((selectedPools.size()) == 1));
        Assert.assertTrue("Selected leaf pools and expected pools are different", checkExpectedVsActualPools(selectedPools, expectedPools));
        // cleanup
        interPool1.clear();
        rightLeafPool.clear();
        expectedPools.clear();
    }
}

