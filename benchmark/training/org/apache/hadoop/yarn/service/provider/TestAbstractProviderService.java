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
package org.apache.hadoop.yarn.service.provider;


import ContainerLaunchService.ComponentLaunchContext;
import com.google.common.collect.Lists;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.service.ServiceContext;
import org.apache.hadoop.yarn.service.ServiceTestUtils;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.apache.hadoop.yarn.service.component.Component;
import org.apache.hadoop.yarn.service.component.instance.ComponentInstance;
import org.apache.hadoop.yarn.service.containerlaunch.AbstractLauncher;
import org.apache.hadoop.yarn.service.containerlaunch.ContainerLaunchService;
import org.apache.hadoop.yarn.service.provider.docker.DockerProviderService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link AbstractProviderService}
 */
public class TestAbstractProviderService {
    private ServiceContext serviceContext;

    private Service testService;

    private AbstractLauncher launcher;

    @Rule
    public ServiceTestUtils.ServiceFSWatcher rule = new ServiceTestUtils.ServiceFSWatcher();

    @Test
    public void testBuildContainerLaunchCommand() throws Exception {
        AbstractProviderService providerService = new DockerProviderService();
        Component component = serviceContext.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        ContainerLaunchService.ComponentLaunchContext clc = TestAbstractProviderService.createEntryPointCLCFor(testService, component);
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        Container container = Mockito.mock(Container.class);
        providerService.buildContainerLaunchCommand(launcher, testService, instance, rule.getFs(), serviceContext.scheduler.getConfig(), container, clc, null);
        Assert.assertEquals("commands", Lists.newArrayList(clc.getLaunchCommand()), launcher.getCommands());
    }

    @Test
    public void testBuildContainerLaunchContext() throws Exception {
        AbstractProviderService providerService = new DockerProviderService();
        Component component = serviceContext.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        ContainerLaunchService.ComponentLaunchContext clc = TestAbstractProviderService.createEntryPointCLCFor(testService, component);
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        Container container = Mockito.mock(Container.class);
        ContainerId containerId = ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(System.currentTimeMillis(), 1), 1), 1L);
        Mockito.when(container.getId()).thenReturn(containerId);
        providerService.buildContainerLaunchContext(launcher, testService, instance, rule.getFs(), serviceContext.scheduler.getConfig(), container, clc);
        Assert.assertEquals("artifact", clc.getArtifact().getId(), launcher.getDockerImage());
    }
}

