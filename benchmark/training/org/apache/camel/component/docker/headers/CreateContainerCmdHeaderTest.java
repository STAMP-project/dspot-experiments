/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.docker.headers;


import DockerConstants.DOCKER_ATTACH_STD_ERR;
import DockerConstants.DOCKER_ATTACH_STD_IN;
import DockerConstants.DOCKER_ATTACH_STD_OUT;
import DockerConstants.DOCKER_CAP_ADD;
import DockerConstants.DOCKER_CAP_DROP;
import DockerConstants.DOCKER_CMD;
import DockerConstants.DOCKER_CPU_SHARES;
import DockerConstants.DOCKER_DISABLE_NETWORK;
import DockerConstants.DOCKER_DNS;
import DockerConstants.DOCKER_DOMAIN_NAME;
import DockerConstants.DOCKER_ENTRYPOINT;
import DockerConstants.DOCKER_ENV;
import DockerConstants.DOCKER_EXPOSED_PORTS;
import DockerConstants.DOCKER_HOSTNAME;
import DockerConstants.DOCKER_HOST_CONFIG;
import DockerConstants.DOCKER_IMAGE;
import DockerConstants.DOCKER_MEMORY_LIMIT;
import DockerConstants.DOCKER_MEMORY_SWAP;
import DockerConstants.DOCKER_NAME;
import DockerConstants.DOCKER_PORT_SPECS;
import DockerConstants.DOCKER_STD_IN_ONCE;
import DockerConstants.DOCKER_STD_IN_OPEN;
import DockerConstants.DOCKER_TTY;
import DockerConstants.DOCKER_USER;
import DockerConstants.DOCKER_VOLUMES;
import DockerConstants.DOCKER_VOLUMES_FROM;
import DockerConstants.DOCKER_WORKING_DIR;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Capability;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.api.model.VolumesFrom;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Validates Create Container Request headers are parsed properly
 */
public class CreateContainerCmdHeaderTest extends BaseDockerHeaderTest<CreateContainerCmd> {
    @Mock
    private CreateContainerCmd mockObject;

    @Test
    public void createContainerHeaderTest() {
        String image = "busybox";
        ExposedPort exposedPort = ExposedPort.tcp(22);
        boolean tty = true;
        String name = "cameldocker";
        String workingDir = "/opt";
        boolean disableNetwork = false;
        String domainName = "apache.org";
        String hostname = "dockerjava";
        String user = "docker";
        boolean stdInOpen = false;
        boolean stdInOnce = false;
        boolean attachStdErr = true;
        boolean attachStdOut = true;
        boolean attachStdIn = false;
        Long memoryLimit = 2048L;
        Long swapMemory = 512L;
        Integer cpuShares = 512;
        Volume volumes = new Volume("/example");
        VolumesFrom volumesFromContainer = new VolumesFrom("/etc");
        String env = "FOO=bar";
        String cmd = "whoami";
        HostConfig hostConfig = new HostConfig();
        Capability capAdd = Capability.NET_BROADCAST;
        Capability capDrop = Capability.BLOCK_SUSPEND;
        String[] entrypoint = new String[]{ "sleep", "9999" };
        String portSpecs = "80";
        String dns = "8.8.8.8";
        Map<String, Object> headers = getDefaultParameters();
        headers.put(DOCKER_IMAGE, image);
        headers.put(DOCKER_EXPOSED_PORTS, exposedPort);
        headers.put(DOCKER_TTY, tty);
        headers.put(DOCKER_NAME, name);
        headers.put(DOCKER_WORKING_DIR, workingDir);
        headers.put(DOCKER_DISABLE_NETWORK, disableNetwork);
        headers.put(DOCKER_HOSTNAME, hostname);
        headers.put(DOCKER_USER, user);
        headers.put(DOCKER_STD_IN_OPEN, stdInOpen);
        headers.put(DOCKER_STD_IN_ONCE, stdInOnce);
        headers.put(DOCKER_ATTACH_STD_IN, attachStdIn);
        headers.put(DOCKER_ATTACH_STD_ERR, attachStdErr);
        headers.put(DOCKER_ATTACH_STD_OUT, attachStdOut);
        headers.put(DOCKER_MEMORY_LIMIT, memoryLimit);
        headers.put(DOCKER_MEMORY_SWAP, swapMemory);
        headers.put(DOCKER_CPU_SHARES, cpuShares);
        headers.put(DOCKER_VOLUMES, volumes);
        headers.put(DOCKER_VOLUMES_FROM, volumesFromContainer);
        headers.put(DOCKER_ENV, env);
        headers.put(DOCKER_CMD, cmd);
        headers.put(DOCKER_HOST_CONFIG, hostConfig);
        headers.put(DOCKER_CAP_ADD, capAdd);
        headers.put(DOCKER_CAP_DROP, capDrop);
        headers.put(DOCKER_ENTRYPOINT, entrypoint);
        headers.put(DOCKER_PORT_SPECS, portSpecs);
        headers.put(DOCKER_DNS, dns);
        headers.put(DOCKER_DOMAIN_NAME, domainName);
        template.sendBodyAndHeaders("direct:in", "", headers);
        Mockito.verify(dockerClient, Mockito.times(1)).createContainerCmd(image);
        Mockito.verify(mockObject, Mockito.times(1)).withExposedPorts(ArgumentMatchers.eq(exposedPort));
        Mockito.verify(mockObject, Mockito.times(1)).withTty(ArgumentMatchers.eq(tty));
        Mockito.verify(mockObject, Mockito.times(1)).withName(ArgumentMatchers.eq(name));
        Mockito.verify(mockObject, Mockito.times(1)).withWorkingDir(workingDir);
        Mockito.verify(mockObject, Mockito.times(1)).withNetworkDisabled(disableNetwork);
        Mockito.verify(mockObject, Mockito.times(1)).withHostName(hostname);
        Mockito.verify(mockObject, Mockito.times(1)).withUser(user);
        Mockito.verify(mockObject, Mockito.times(1)).withStdinOpen(stdInOpen);
        Mockito.verify(mockObject, Mockito.times(1)).withStdInOnce(stdInOnce);
        Mockito.verify(mockObject, Mockito.times(1)).withAttachStderr(attachStdErr);
        Mockito.verify(mockObject, Mockito.times(1)).withAttachStdin(attachStdIn);
        Mockito.verify(mockObject, Mockito.times(1)).withAttachStdout(attachStdOut);
        Mockito.verify(mockObject, Mockito.times(1)).withMemory(memoryLimit);
        Mockito.verify(mockObject, Mockito.times(1)).withMemorySwap(swapMemory);
        Mockito.verify(mockObject, Mockito.times(1)).withCpuShares(cpuShares);
        Mockito.verify(mockObject, Mockito.times(1)).withVolumes(volumes);
        Mockito.verify(mockObject, Mockito.times(1)).withVolumesFrom(volumesFromContainer);
        Mockito.verify(mockObject, Mockito.times(1)).withEnv(env);
        Mockito.verify(mockObject, Mockito.times(1)).withCmd(cmd);
        Mockito.verify(mockObject, Mockito.times(1)).withHostConfig(hostConfig);
        Mockito.verify(mockObject, Mockito.times(1)).withCapAdd(capAdd);
        Mockito.verify(mockObject, Mockito.times(1)).withCapDrop(capDrop);
        Mockito.verify(mockObject, Mockito.times(1)).withEntrypoint(entrypoint);
        Mockito.verify(mockObject, Mockito.times(1)).withPortSpecs(portSpecs);
        Mockito.verify(mockObject, Mockito.times(1)).withDns(dns);
        Mockito.verify(mockObject, Mockito.times(1)).withDomainName(domainName);
    }
}

