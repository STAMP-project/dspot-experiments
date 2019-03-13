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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.ptest.execution.context;


import Status.ERROR;
import java.util.Set;
import org.jclouds.compute.domain.NodeMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestCloudComputeService {
    private static final String GROUP_NAME = "grp";

    private static final String GROUP_TAG = "group=" + (TestCloudComputeService.GROUP_NAME);

    private NodeMetadata node;

    private Set<String> tags;

    @Test
    public void testNotStarted() throws Exception {
        Mockito.when(node.getStatus()).thenReturn(ERROR);
        Assert.assertFalse("Node is not running, should be filtered out", CloudComputeService.createFilterPTestPredicate(TestCloudComputeService.GROUP_NAME, TestCloudComputeService.GROUP_TAG).apply(node));
    }

    @Test
    public void testBadName() throws Exception {
        Mockito.when(node.getName()).thenReturn(null);
        Assert.assertTrue("Node should be filtered in by group or tag", CloudComputeService.createFilterPTestPredicate(TestCloudComputeService.GROUP_NAME, TestCloudComputeService.GROUP_TAG).apply(node));
    }

    @Test
    public void testBadGroup() throws Exception {
        Mockito.when(node.getGroup()).thenReturn(null);
        Assert.assertTrue("Node should be filtered in by name or tag", CloudComputeService.createFilterPTestPredicate(TestCloudComputeService.GROUP_NAME, TestCloudComputeService.GROUP_TAG).apply(node));
    }

    @Test
    public void testBadTag() throws Exception {
        tags.clear();
        Assert.assertTrue("Node should be filtered in by name or group", CloudComputeService.createFilterPTestPredicate(TestCloudComputeService.GROUP_NAME, TestCloudComputeService.GROUP_TAG).apply(node));
    }
}

