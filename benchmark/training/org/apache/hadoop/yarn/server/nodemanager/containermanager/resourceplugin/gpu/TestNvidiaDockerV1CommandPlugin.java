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
import YarnConfiguration.NVIDIA_DOCKER_PLUGIN_V1_ENDPOINT;
import com.google.common.collect.ImmutableList;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ResourceMappings;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker.DockerRunCommand;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker.DockerVolumeCommand;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestNvidiaDockerV1CommandPlugin {
    static class MyHandler implements HttpHandler {
        String response = "This is the response";

        @Override
        public void handle(HttpExchange t) throws IOException {
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class MyNvidiaDockerV1CommandPlugin extends NvidiaDockerV1CommandPlugin {
        private boolean requestsGpu = false;

        public MyNvidiaDockerV1CommandPlugin(Configuration conf) {
            super(conf);
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
        Configuration conf = new Configuration();
        DockerRunCommand runCommand = new DockerRunCommand("container_1", "user", "fakeimage");
        Map<String, List<String>> originalCommandline = copyCommandLine(runCommand.getDockerCommandWithArguments());
        TestNvidiaDockerV1CommandPlugin.MyNvidiaDockerV1CommandPlugin commandPlugin = new TestNvidiaDockerV1CommandPlugin.MyNvidiaDockerV1CommandPlugin(conf);
        Container nmContainer = Mockito.mock(Container.class);
        // getResourceMapping is null, so commandline won't be updated
        commandPlugin.updateDockerRunCommand(runCommand, nmContainer);
        Assert.assertTrue(commandlinesEquals(originalCommandline, runCommand.getDockerCommandWithArguments()));
        // no GPU resource assigned, so commandline won't be updated
        ResourceMappings resourceMappings = new ResourceMappings();
        Mockito.when(nmContainer.getResourceMappings()).thenReturn(resourceMappings);
        commandPlugin.updateDockerRunCommand(runCommand, nmContainer);
        Assert.assertTrue(commandlinesEquals(originalCommandline, runCommand.getDockerCommandWithArguments()));
        // Assign GPU resource, init will be invoked
        ResourceMappings.AssignedResources assigned = new ResourceMappings.AssignedResources();
        assigned.updateAssignedResources(ImmutableList.of(new GpuDevice(0, 0), new GpuDevice(1, 1)));
        resourceMappings.addAssignedResources(GPU_URI, assigned);
        commandPlugin.setRequestsGpu(true);
        // Since there's no HTTP server running, so we will see exception
        boolean caughtException = false;
        try {
            commandPlugin.updateDockerRunCommand(runCommand, nmContainer);
        } catch (ContainerExecutionException e) {
            caughtException = true;
        }
        Assert.assertTrue(caughtException);
        // Start HTTP server
        TestNvidiaDockerV1CommandPlugin.MyHandler handler = new TestNvidiaDockerV1CommandPlugin.MyHandler();
        HttpServer server = HttpServer.create(new InetSocketAddress(60111), 0);
        server.createContext("/test", handler);
        server.start();
        String hostName = server.getAddress().getHostName();
        int port = server.getAddress().getPort();
        String httpUrl = ((("http://" + hostName) + ":") + port) + "/test";
        conf.set(NVIDIA_DOCKER_PLUGIN_V1_ENDPOINT, httpUrl);
        commandPlugin = new TestNvidiaDockerV1CommandPlugin.MyNvidiaDockerV1CommandPlugin(conf);
        // Start use invalid options
        handler.response = "INVALID_RESPONSE";
        try {
            commandPlugin.updateDockerRunCommand(runCommand, nmContainer);
        } catch (ContainerExecutionException e) {
            caughtException = true;
        }
        Assert.assertTrue(caughtException);
        // Start use invalid options
        handler.response = "INVALID_RESPONSE";
        try {
            commandPlugin.updateDockerRunCommand(runCommand, nmContainer);
        } catch (ContainerExecutionException e) {
            caughtException = true;
        }
        Assert.assertTrue(caughtException);
        /* Test get docker run command */
        handler.response = "--device=/dev/nvidiactl --device=/dev/nvidia-uvm " + (("--device=/dev/nvidia0 --device=/dev/nvidia1 " + "--volume-driver=nvidia-docker ") + "--volume=nvidia_driver_352.68:/usr/local/nvidia:ro");
        commandPlugin.setRequestsGpu(true);
        commandPlugin.updateDockerRunCommand(runCommand, nmContainer);
        Map<String, List<String>> newCommandLine = runCommand.getDockerCommandWithArguments();
        // Command line will be updated
        Assert.assertFalse(commandlinesEquals(originalCommandline, newCommandLine));
        // Volume driver should not be included by final commandline
        Assert.assertFalse(newCommandLine.containsKey("volume-driver"));
        Assert.assertTrue(newCommandLine.containsKey("devices"));
        Assert.assertTrue(newCommandLine.containsKey("mounts"));
        /* Test get docker volume command */
        commandPlugin = new TestNvidiaDockerV1CommandPlugin.MyNvidiaDockerV1CommandPlugin(conf);
        // When requests Gpu == false, returned docker volume command is null,
        Assert.assertNull(commandPlugin.getCreateDockerVolumeCommand(nmContainer));
        // set requests Gpu to true
        commandPlugin.setRequestsGpu(true);
        DockerVolumeCommand dockerVolumeCommand = commandPlugin.getCreateDockerVolumeCommand(nmContainer);
        Assert.assertEquals(("volume docker-command=volume " + (("driver=nvidia-docker " + "sub-command=create ") + "volume=nvidia_driver_352.68")), dockerVolumeCommand.toString());
    }
}

