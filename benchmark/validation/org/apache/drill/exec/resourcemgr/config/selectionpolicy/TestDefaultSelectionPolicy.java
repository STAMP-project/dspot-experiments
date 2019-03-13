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
package org.apache.drill.exec.resourcemgr.config.selectionpolicy;


import java.util.ArrayList;
import java.util.List;
import org.apache.drill.categories.ResourceManagerTest;
import org.apache.drill.exec.ops.QueryContext;
import org.apache.drill.exec.resourcemgr.config.ResourcePool;
import org.apache.drill.exec.resourcemgr.config.exception.QueueSelectionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(ResourceManagerTest.class)
public final class TestDefaultSelectionPolicy {
    private static QueueSelectionPolicy selectionPolicy;

    private static final QueryContext queryContext = Mockito.mock(QueryContext.class);

    @Test(expected = QueueSelectionException.class)
    public void testWithNoDefaultPool() throws Exception {
        List<ResourcePool> inputPools = new ArrayList<>();
        final ResourcePool testPool1 = Mockito.mock(ResourcePool.class);
        Mockito.when(testPool1.isDefaultPool()).thenReturn(false);
        final ResourcePool testPool2 = Mockito.mock(ResourcePool.class);
        Mockito.when(testPool2.isDefaultPool()).thenReturn(false);
        inputPools.add(testPool1);
        inputPools.add(testPool2);
        TestDefaultSelectionPolicy.selectionPolicy.selectQueue(inputPools, TestDefaultSelectionPolicy.queryContext, null);
    }

    @Test
    public void testWithSingleDefaultPool() throws Exception {
        List<ResourcePool> inputPools = new ArrayList<>();
        final ResourcePool testPool1 = Mockito.mock(ResourcePool.class);
        Mockito.when(testPool1.isDefaultPool()).thenReturn(true);
        inputPools.add(testPool1);
        final ResourcePool selectedPool = TestDefaultSelectionPolicy.selectionPolicy.selectQueue(inputPools, TestDefaultSelectionPolicy.queryContext, null);
        Assert.assertEquals("Selected Pool and expected pool is different", testPool1, selectedPool);
    }

    @Test
    public void testWithMultipleDefaultPool() throws Exception {
        List<ResourcePool> inputPools = new ArrayList<>();
        final ResourcePool testPool1 = Mockito.mock(ResourcePool.class);
        Mockito.when(testPool1.isDefaultPool()).thenReturn(true);
        final ResourcePool testPool2 = Mockito.mock(ResourcePool.class);
        Mockito.when(testPool2.isDefaultPool()).thenReturn(true);
        inputPools.add(testPool1);
        inputPools.add(testPool2);
        final ResourcePool selectedPool = TestDefaultSelectionPolicy.selectionPolicy.selectQueue(inputPools, TestDefaultSelectionPolicy.queryContext, null);
        Assert.assertEquals("Selected Pool and expected pool is different", testPool1, selectedPool);
    }

    @Test
    public void testMixOfDefaultAndNonDefaultPool() throws Exception {
        List<ResourcePool> inputPools = new ArrayList<>();
        final ResourcePool testPool1 = Mockito.mock(ResourcePool.class);
        Mockito.when(testPool1.isDefaultPool()).thenReturn(false);
        final ResourcePool testPool2 = Mockito.mock(ResourcePool.class);
        Mockito.when(testPool2.isDefaultPool()).thenReturn(true);
        inputPools.add(testPool1);
        inputPools.add(testPool2);
        final ResourcePool selectedPool = TestDefaultSelectionPolicy.selectionPolicy.selectQueue(inputPools, TestDefaultSelectionPolicy.queryContext, null);
        Assert.assertEquals("Selected Pool and expected pool is different", testPool2, selectedPool);
    }
}

