/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu;


import ResourceInformation.GPU_URI;
import ResourceMappings.AssignedResources;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ResourceMappings;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker.DockerRunCommand;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * test for NvidiaDockerV2CommandPlugin.
 */
public class TestNvidiaDockerV2CommandPlugin {
    static class MyNvidiaDockerV2CommandPlugin extends NvidiaDockerV2CommandPlugin {
        private boolean requestsGpu = false;

        MyNvidiaDockerV2CommandPlugin() {
        }

        public void setRequestsGpu(boolean r) {
            requestsGpu = r;
        }

        @Override
        protected boolean requestsGpu(Container container) {
            return requestsGpu;
        }
    }

    @Test
    public void testPlugin() throws Exception {
        DockerRunCommand runCommand = new DockerRunCommand("container_1", "user", "fakeimage");
        Map<String, List<String>> originalCommandline = copyCommandLine(runCommand.getDockerCommandWithArguments());
        TestNvidiaDockerV2CommandPlugin.MyNvidiaDockerV2CommandPlugin commandPlugin = new TestNvidiaDockerV2CommandPlugin.MyNvidiaDockerV2CommandPlugin();
        Container nmContainer = Mockito.mock(Container.class);
        // getResourceMapping is null, so commandline won't be updated
        commandPlugin.updateDockerRunCommand(runCommand, nmContainer);
        Assert.assertTrue(commandlinesEquals(originalCommandline, runCommand.getDockerCommandWithArguments()));
        // no GPU resource assigned, so commandline won't be updated
        ResourceMappings resourceMappings = new ResourceMappings();
        Mockito.when(nmContainer.getResourceMappings()).thenReturn(resourceMappings);
        commandPlugin.updateDockerRunCommand(runCommand, nmContainer);
        Assert.assertTrue(commandlinesEquals(originalCommandline, runCommand.getDockerCommandWithArguments()));
        // Assign GPU resource
        ResourceMappings.AssignedResources assigned = new ResourceMappings.AssignedResources();
        assigned.updateAssignedResources(ImmutableList.of(new GpuDevice(0, 0), new GpuDevice(1, 1)));
        resourceMappings.addAssignedResources(GPU_URI, assigned);
        commandPlugin.setRequestsGpu(true);
        commandPlugin.updateDockerRunCommand(runCommand, nmContainer);
        Map<String, List<String>> newCommandLine = runCommand.getDockerCommandWithArguments();
        // Command line will be updated
        Assert.assertFalse(commandlinesEquals(originalCommandline, newCommandLine));
        // NVIDIA_VISIBLE_DEVICES will be set
        Assert.assertTrue(runCommand.getEnv().get("NVIDIA_VISIBLE_DEVICES").equals("0,1"));
        // runtime should exist
        Assert.assertTrue(newCommandLine.containsKey("runtime"));
    }
}

