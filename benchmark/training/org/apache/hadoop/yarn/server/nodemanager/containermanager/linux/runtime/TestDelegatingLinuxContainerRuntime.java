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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime;


import ContainerRuntimeConstants.CONTAINER_RUNTIME_DOCKER;
import ContainerRuntimeConstants.ENV_CONTAINER_TYPE;
import LinuxContainerRuntimeConstants.RuntimeType.DEFAULT;
import LinuxContainerRuntimeConstants.RuntimeType.DOCKER;
import LinuxContainerRuntimeConstants.RuntimeType.JAVASANDBOX;
import YarnConfiguration.LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES;
import YarnConfiguration.LINUX_CONTAINER_RUNTIME_CLASS_FMT;
import YarnConfiguration.YARN_CONTAINER_SANDBOX;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerExecutionException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerRuntime;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test container runtime delegation.
 */
public class TestDelegatingLinuxContainerRuntime {
    private DelegatingLinuxContainerRuntime delegatingLinuxContainerRuntime;

    private Configuration conf;

    private Map<String, String> env = new HashMap<>();

    @Test
    public void testIsRuntimeAllowedDefault() throws Exception {
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, YarnConfiguration.DEFAULT_LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES[0]);
        System.out.println(conf.get(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES));
        delegatingLinuxContainerRuntime.initialize(conf, null);
        Assert.assertTrue(delegatingLinuxContainerRuntime.isRuntimeAllowed(DEFAULT.name()));
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(DOCKER.name()));
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(JAVASANDBOX.name()));
    }

    @Test
    public void testIsRuntimeAllowedDocker() throws Exception {
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, CONTAINER_RUNTIME_DOCKER);
        delegatingLinuxContainerRuntime.initialize(conf, null);
        Assert.assertTrue(delegatingLinuxContainerRuntime.isRuntimeAllowed(DOCKER.name()));
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(DEFAULT.name()));
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(JAVASANDBOX.name()));
    }

    @Test
    public void testIsRuntimeAllowedJavaSandbox() throws Exception {
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, "javasandbox");
        delegatingLinuxContainerRuntime.initialize(conf, null);
        Assert.assertTrue(delegatingLinuxContainerRuntime.isRuntimeAllowed(JAVASANDBOX.name()));
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(DEFAULT.name()));
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(DOCKER.name()));
    }

    @Test
    public void testIsRuntimeAllowedMultiple() throws Exception {
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, "docker,javasandbox");
        delegatingLinuxContainerRuntime.initialize(conf, null);
        Assert.assertTrue(delegatingLinuxContainerRuntime.isRuntimeAllowed(DOCKER.name()));
        Assert.assertTrue(delegatingLinuxContainerRuntime.isRuntimeAllowed(JAVASANDBOX.name()));
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(DEFAULT.name()));
    }

    @Test
    public void testIsRuntimeAllowedAll() throws Exception {
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, "default,docker,javasandbox");
        delegatingLinuxContainerRuntime.initialize(conf, null);
        Assert.assertTrue(delegatingLinuxContainerRuntime.isRuntimeAllowed(DEFAULT.name()));
        Assert.assertTrue(delegatingLinuxContainerRuntime.isRuntimeAllowed(DOCKER.name()));
        Assert.assertTrue(delegatingLinuxContainerRuntime.isRuntimeAllowed(JAVASANDBOX.name()));
    }

    @Test
    public void testInitializeMissingRuntimeClass() throws Exception {
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, "mock");
        try {
            delegatingLinuxContainerRuntime.initialize(conf, null);
            Assert.fail("initialize should fail");
        } catch (ContainerExecutionException e) {
            assert e.getMessage().contains("Invalid runtime set");
        }
    }

    @Test
    public void testIsRuntimeAllowedMock() throws Exception {
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, "mock");
        conf.set(String.format(LINUX_CONTAINER_RUNTIME_CLASS_FMT, "mock"), MockLinuxContainerRuntime.class.getName());
        delegatingLinuxContainerRuntime.initialize(conf, null);
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(DEFAULT.name()));
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(DOCKER.name()));
        Assert.assertFalse(delegatingLinuxContainerRuntime.isRuntimeAllowed(JAVASANDBOX.name()));
        Assert.assertTrue(delegatingLinuxContainerRuntime.isRuntimeAllowed("mock"));
    }

    @Test
    public void testJavaSandboxNotAllowedButPermissive() throws Exception {
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, "default,docker");
        conf.set(YARN_CONTAINER_SANDBOX, "permissive");
        delegatingLinuxContainerRuntime.initialize(conf, null);
        ContainerRuntime runtime = delegatingLinuxContainerRuntime.pickContainerRuntime(env);
        Assert.assertTrue((runtime instanceof DefaultLinuxContainerRuntime));
    }

    @Test
    public void testJavaSandboxNotAllowedButPermissiveDockerRequested() throws Exception {
        env.put(ENV_CONTAINER_TYPE, CONTAINER_RUNTIME_DOCKER);
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, "default,docker");
        conf.set(YARN_CONTAINER_SANDBOX, "permissive");
        delegatingLinuxContainerRuntime.initialize(conf, null);
        ContainerRuntime runtime = delegatingLinuxContainerRuntime.pickContainerRuntime(env);
        Assert.assertTrue((runtime instanceof DockerLinuxContainerRuntime));
    }

    @Test
    public void testMockRuntimeSelected() throws Exception {
        env.put(ENV_CONTAINER_TYPE, "mock");
        conf.set(String.format(LINUX_CONTAINER_RUNTIME_CLASS_FMT, "mock"), MockLinuxContainerRuntime.class.getName());
        conf.set(LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES, "mock");
        delegatingLinuxContainerRuntime.initialize(conf, null);
        ContainerRuntime runtime = delegatingLinuxContainerRuntime.pickContainerRuntime(env);
        Assert.assertTrue((runtime instanceof MockLinuxContainerRuntime));
    }
}

