/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker;


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the docker run command and its command
 * line arguments.
 */
public class TestDockerRunCommand {
    private DockerRunCommand dockerRunCommand;

    private static final String CONTAINER_NAME = "foo";

    private static final String USER_ID = "user_id";

    private static final String IMAGE_NAME = "image_name";

    private static final String CLIENT_CONFIG_PATH = "/path/to/client.json";

    @Test
    public void testGetCommandOption() {
        Assert.assertEquals("run", dockerRunCommand.getCommandOption());
    }

    @Test
    public void testCommandArguments() {
        String sourceDevice = "source";
        String destDevice = "dest";
        dockerRunCommand.addDevice(sourceDevice, destDevice);
        List<String> commands = new ArrayList<>();
        commands.add("launch_command");
        dockerRunCommand.setOverrideCommandWithArgs(commands);
        dockerRunCommand.removeContainerOnExit();
        dockerRunCommand.addTmpfsMount("/run");
        String portsMapping = "127.0.0.1:8080:80,1234:1234,:2222";
        for (String mapping : portsMapping.split(",")) {
            dockerRunCommand.addPortsMapping(mapping);
        }
        dockerRunCommand.addRuntime("nvidia");
        Assert.assertEquals("run", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("docker-command")));
        Assert.assertEquals("foo", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("name")));
        Assert.assertEquals("user_id", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("user")));
        Assert.assertEquals("image_name", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("image")));
        Assert.assertEquals("source:dest", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("devices")));
        Assert.assertEquals("true", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("rm")));
        Assert.assertEquals("launch_command", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("launch-command")));
        Assert.assertEquals("/run", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("tmpfs")));
        Assert.assertEquals("127.0.0.1:8080:80,1234:1234,:2222", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("ports-mapping")));
        Assert.assertEquals("nvidia", StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("runtime")));
        Assert.assertEquals(10, dockerRunCommand.getDockerCommandWithArguments().size());
    }

    @Test
    public void testSetClientConfigDir() {
        dockerRunCommand.setClientConfigDir(TestDockerRunCommand.CLIENT_CONFIG_PATH);
        Assert.assertEquals(TestDockerRunCommand.CLIENT_CONFIG_PATH, StringUtils.join(",", dockerRunCommand.getDockerCommandWithArguments().get("docker-config")));
    }
}

