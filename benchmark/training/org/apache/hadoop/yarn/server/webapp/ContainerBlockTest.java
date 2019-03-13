/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.webapp;


import ResourceInformation.GPU_URI;
import View.ViewContext;
import com.google.common.collect.ImmutableMap;
import org.apache.hadoop.yarn.api.ApplicationBaseProtocol;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.resourcetypes.ResourceTypesTestHelper;
import org.apache.hadoop.yarn.server.webapp.dao.ContainerInfo;
import org.apache.hadoop.yarn.util.resource.CustomResourceTypesConfigurationProvider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for ContainerBlock.
 */
public class ContainerBlockTest {
    @Test
    public void testRenderResourcesString() {
        CustomResourceTypesConfigurationProvider.initResourceTypes(GPU_URI);
        Resource resource = ResourceTypesTestHelper.newResource(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, ImmutableMap.<String, String>builder().put(GPU_URI, "5").build());
        ContainerBlock block = new ContainerBlock(Mockito.mock(ApplicationBaseProtocol.class), Mockito.mock(ViewContext.class));
        ContainerReport containerReport = createContainerReport();
        containerReport.setAllocatedResource(resource);
        ContainerInfo containerInfo = new ContainerInfo(containerReport);
        String resources = block.getResources(containerInfo);
        Assert.assertEquals("8192 Memory, 4 VCores, 5 yarn.io/gpu", resources);
    }
}

