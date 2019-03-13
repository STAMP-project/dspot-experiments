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
import org.apache.drill.exec.resourcemgr.NodeResources;
import org.apache.drill.exec.resourcemgr.config.exception.QueueSelectionException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(ResourceManagerTest.class)
public final class TestBestFitSelectionPolicy {
    private static QueueSelectionPolicy selectionPolicy;

    private static final QueryContext queryContext = Mockito.mock(QueryContext.class);

    private static final NodeResources queryMaxResources = new NodeResources(((1500 * 1024L) * 1024L), 2);

    private static final List<Long> poolMemory = new ArrayList<>();

    @Test(expected = QueueSelectionException.class)
    public void testWithNoPool() throws Exception {
        testCommonHelper(0);
    }

    @Test
    public void testWithSinglePool() throws Exception {
        TestBestFitSelectionPolicy.poolMemory.add(1000L);
        testCommonHelper(1000);
    }

    @Test
    public void testWithMultiplePoolWithGreaterMaxNodeMemory() throws Exception {
        TestBestFitSelectionPolicy.poolMemory.add(2500L);
        TestBestFitSelectionPolicy.poolMemory.add(2000L);
        testCommonHelper(2000);
    }

    @Test
    public void testWithMultiplePoolWithLesserMaxNodeMemory() throws Exception {
        TestBestFitSelectionPolicy.poolMemory.add(700L);
        TestBestFitSelectionPolicy.poolMemory.add(500L);
        testCommonHelper(700);
    }

    @Test
    public void testMixOfPoolLess_Greater_MaxNodeMemory() throws Exception {
        TestBestFitSelectionPolicy.poolMemory.add(1000L);
        TestBestFitSelectionPolicy.poolMemory.add(2000L);
        testCommonHelper(2000);
    }

    @Test
    public void testMixOfPoolLess_Greater_EqualMaxNodeMemory() throws Exception {
        TestBestFitSelectionPolicy.poolMemory.add(1000L);
        TestBestFitSelectionPolicy.poolMemory.add(2000L);
        TestBestFitSelectionPolicy.poolMemory.add(1500L);
        testCommonHelper(1500);
    }
}

