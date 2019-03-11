/**
 * *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime;


import ContainerExecutor.Signal.KILL;
import ContainerExecutor.Signal.NULL;
import ContainerExecutor.Signal.QUIT;
import ContainerExecutor.Signal.TERM;
import ContainerRuntimeConstants.CONTAINER_RUNTIME_DOCKER;
import ContainerRuntimeConstants.ENV_CONTAINER_TYPE;
import ContainerRuntimeContext.Builder;
import DockerCommandExecutor.DockerContainerStatus.RUNNING;
import DockerCommandExecutor.DockerContainerStatus.STOPPED;
import DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_DELAYED_REMOVAL;
import DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_HOSTNAME;
import DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_IMAGE;
import DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_MOUNTS;
import DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_NETWORK;
import DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_PID_NAMESPACE;
import DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER;
import DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_TMPFS_MOUNTS;
import PrivilegedOperation.OperationType.LAUNCH_DOCKER_CONTAINER;
import PrivilegedOperation.OperationType.RUN_DOCKER_CMD;
import PrivilegedOperation.ResultCode.INVALID_CONTAINER_PID;
import PrivilegedOperation.RunAsUserCommand.SIGNAL_CONTAINER;
import RegistryConstants.KEY_DNS_ENABLED;
import TestDockerClientConfigHandler.JSON;
import YarnConfiguration.LINUX_CONTAINER_RUNTIME_TYPE;
import YarnConfiguration.NM_DOCKER_ALLOWED_CONTAINER_NETWORKS;
import YarnConfiguration.NM_DOCKER_ALLOW_DELAYED_REMOVAL;
import YarnConfiguration.NM_DOCKER_ALLOW_HOST_PID_NAMESPACE;
import YarnConfiguration.NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS;
import YarnConfiguration.NM_DOCKER_CONTAINER_CAPABILITIES;
import YarnConfiguration.NM_DOCKER_DEFAULT_CONTAINER_NETWORK;
import YarnConfiguration.NM_DOCKER_ENABLE_USER_REMAPPING;
import YarnConfiguration.NM_DOCKER_IMAGE_NAME;
import YarnConfiguration.NM_DOCKER_IMAGE_UPDATE;
import YarnConfiguration.NM_DOCKER_PRIVILEGED_CONTAINERS_ACL;
import YarnConfiguration.NM_LINUX_CONTAINER_CGROUPS_HIERARCHY;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.CGroupsHandler;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker.DockerRunCommand;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker.DockerVolumeCommand;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.DockerCommandPlugin;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.ResourcePlugin;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.ResourcePluginManager;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerExecutionException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerRuntimeContext;
import org.apache.hadoop.yarn.util.DockerClientConfigHandler;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestDockerContainerRuntime {
    private static final Logger LOG = LoggerFactory.getLogger(TestDockerContainerRuntime.class);

    private Configuration conf;

    private PrivilegedOperationExecutor mockExecutor;

    private CGroupsHandler mockCGroupsHandler;

    private String containerId;

    private Container container;

    private ContainerId cId;

    private ApplicationAttemptId appAttemptId;

    private ContainerLaunchContext context;

    private Context nmContext;

    private HashMap<String, String> env;

    private String image;

    private String uidGidPair;

    private String runAsUser = System.getProperty("user.name");

    private String[] groups = new String[]{  };

    private String user;

    private String appId;

    private String containerIdStr = containerId;

    private Path containerWorkDir;

    private Path nmPrivateContainerScriptPath;

    private Path nmPrivateTokensPath;

    private Path nmPrivateKeystorePath;

    private Path nmPrivateTruststorePath;

    private Path pidFilePath;

    private List<String> localDirs;

    private List<String> logDirs;

    private List<String> filecacheDirs;

    private List<String> userFilecacheDirs;

    private List<String> applicationLocalDirs;

    private List<String> containerLogDirs;

    private Map<Path, List<String>> localizedResources;

    private String resourcesOptions;

    private Builder builder;

    private final String submittingUser = "anakin";

    private final String whitelistedUser = "yoda";

    private String[] testCapabilities;

    private final String signalPid = "1234";

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Parameterized.Parameter
    public boolean https;

    @Test
    public void testSelectDockerContainerType() {
        Map<String, String> envDockerType = new HashMap<>();
        Map<String, String> envOtherType = new HashMap<>();
        envDockerType.put(ENV_CONTAINER_TYPE, CONTAINER_RUNTIME_DOCKER);
        envOtherType.put(ENV_CONTAINER_TYPE, "other");
        Assert.assertEquals(false, DockerLinuxContainerRuntime.isDockerContainerRequested(conf, null));
        Assert.assertEquals(true, DockerLinuxContainerRuntime.isDockerContainerRequested(conf, envDockerType));
        Assert.assertEquals(false, DockerLinuxContainerRuntime.isDockerContainerRequested(conf, envOtherType));
    }

    @Test
    public void testSelectDockerContainerTypeWithDockerAsDefault() {
        Map<String, String> envDockerType = new HashMap<>();
        Map<String, String> envOtherType = new HashMap<>();
        conf.set(LINUX_CONTAINER_RUNTIME_TYPE, CONTAINER_RUNTIME_DOCKER);
        envDockerType.put(ENV_CONTAINER_TYPE, CONTAINER_RUNTIME_DOCKER);
        envOtherType.put(ENV_CONTAINER_TYPE, "other");
        Assert.assertEquals(true, DockerLinuxContainerRuntime.isDockerContainerRequested(conf, null));
        Assert.assertEquals(true, DockerLinuxContainerRuntime.isDockerContainerRequested(conf, envDockerType));
        Assert.assertEquals(false, DockerLinuxContainerRuntime.isDockerContainerRequested(conf, envOtherType));
    }

    @Test
    public void testSelectDockerContainerTypeWithDefaultSet() {
        Map<String, String> envDockerType = new HashMap<>();
        Map<String, String> envOtherType = new HashMap<>();
        conf.set(LINUX_CONTAINER_RUNTIME_TYPE, "default");
        envDockerType.put(ENV_CONTAINER_TYPE, CONTAINER_RUNTIME_DOCKER);
        envOtherType.put(ENV_CONTAINER_TYPE, "other");
        Assert.assertEquals(false, DockerLinuxContainerRuntime.isDockerContainerRequested(conf, null));
        Assert.assertEquals(true, DockerLinuxContainerRuntime.isDockerContainerRequested(conf, envDockerType));
        Assert.assertEquals(false, DockerLinuxContainerRuntime.isDockerContainerRequested(conf, envOtherType));
    }

    @Test
    public void testDockerContainerLaunch() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testDockerContainerLaunchWithDefaultImage() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        conf.set(NM_DOCKER_IMAGE_NAME, "busybox:1.2.3");
        env.remove(ENV_DOCKER_CONTAINER_IMAGE);
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:1.2.3", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testDockerContainerLaunchWithoutDefaultImageUpdate() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        conf.setBoolean(NM_DOCKER_IMAGE_UPDATE, false);
        runtime.initialize(conf, nmContext);
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        Assert.assertEquals(false, conf.getBoolean(NM_DOCKER_IMAGE_UPDATE, false));
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testDockerContainerLaunchWithDefaultImageUpdate() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        conf.setBoolean(NM_DOCKER_IMAGE_UPDATE, true);
        runtime.initialize(conf, nmContext);
        runtime.launchContainer(builder.build());
        ArgumentCaptor<PrivilegedOperation> opCaptor = ArgumentCaptor.forClass(PrivilegedOperation.class);
        // Two invocations expected.
        Mockito.verify(mockExecutor, Mockito.times(2)).executePrivilegedOperation(ArgumentMatchers.any(), opCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        List<PrivilegedOperation> allCaptures = opCaptor.getAllValues();
        // pull image from remote hub firstly
        PrivilegedOperation op = allCaptures.get(0);
        Assert.assertEquals(RUN_DOCKER_CMD, op.getOperationType());
        File commandFile = new File(StringUtils.join(",", op.getArguments()));
        FileInputStream fileInputStream = new FileInputStream(commandFile);
        String fileContent = new String(IOUtils.toByteArray(fileInputStream));
        Assert.assertEquals(("[docker-command-execution]\n" + ("  docker-command=pull\n" + "  image=busybox:latest\n")), fileContent);
        fileInputStream.close();
        // launch docker container
        List<String> dockerCommands = readDockerCommands(2);
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testContainerLaunchWithUserRemapping() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        conf.setBoolean(NM_DOCKER_ENABLE_USER_REMAPPING, true);
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        Assert.assertEquals(13, dockerCommands.size());
        int counter = 0;
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testAllowedNetworksConfiguration() throws ContainerExecutionException {
        // the default network configuration should cause
        // no exception should be thrown.
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        // invalid default network configuration - sdn2 is included in allowed
        // networks
        String[] networks = new String[]{ "host", "none", "bridge", "sdn1" };
        String invalidDefaultNetwork = "sdn2";
        conf.setStrings(NM_DOCKER_ALLOWED_CONTAINER_NETWORKS, networks);
        conf.set(NM_DOCKER_DEFAULT_CONTAINER_NETWORK, invalidDefaultNetwork);
        try {
            runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
            runtime.initialize(conf, nmContext);
            Assert.fail(("Invalid default network configuration should did not " + "trigger initialization failure."));
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
        // valid default network configuration - sdn1 is included in allowed
        // networks - no exception should be thrown.
        String validDefaultNetwork = "sdn1";
        conf.set(NM_DOCKER_DEFAULT_CONTAINER_NETWORK, validDefaultNetwork);
        runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContainerLaunchWithNetworkingDefaults() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        Random randEngine = new Random();
        String disallowedNetwork = "sdn" + (Integer.toString(randEngine.nextInt()));
        try {
            env.put(ENV_DOCKER_CONTAINER_NETWORK, disallowedNetwork);
            runtime.launchContainer(builder.build());
            Assert.fail(("Network was expected to be disallowed: " + disallowedNetwork));
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception: " + e));
        }
        String allowedNetwork = "bridge";
        env.put(ENV_DOCKER_CONTAINER_NETWORK, allowedNetwork);
        String expectedHostname = "test.hostname";
        env.put(ENV_DOCKER_CONTAINER_HOSTNAME, expectedHostname);
        // this should cause no failures.
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        // This is the expected docker invocation for this case
        int expected = 14;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  hostname=test.hostname", dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals(("  net=" + allowedNetwork), dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContainerLaunchWithHostDnsNetwork() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        // Make it look like Registry DNS is enabled so we can test whether
        // hostname goes through
        conf.setBoolean(KEY_DNS_ENABLED, true);
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        String expectedHostname = "test.hostname";
        env.put(ENV_DOCKER_CONTAINER_HOSTNAME, expectedHostname);
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        // This is the expected docker invocation for this case
        int expected = 14;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  hostname=test.hostname", dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContainerLaunchWithCustomNetworks() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        String customNetwork1 = "sdn1";
        String customNetwork2 = "sdn2";
        String customNetwork3 = "sdn3";
        String[] networks = new String[]{ "host", "none", "bridge", customNetwork1, customNetwork2 };
        // customized set of allowed networks
        conf.setStrings(NM_DOCKER_ALLOWED_CONTAINER_NETWORKS, networks);
        // default network is "sdn1"
        conf.set(NM_DOCKER_DEFAULT_CONTAINER_NETWORK, customNetwork1);
        // this should cause no failures.
        runtime.initialize(conf, nmContext);
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        // This is the expected docker invocation for this case. customNetwork1
        // ("sdn1") is the expected network to be used in this case
        int expected = 14;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  hostname=ctr-e11-1518975676334-14532816-01-000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=sdn1", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
        // now set an explicit (non-default) allowedNetwork and ensure that it is
        // used.
        env.put(ENV_DOCKER_CONTAINER_NETWORK, customNetwork2);
        runtime.launchContainer(builder.build());
        dockerCommands = readDockerCommands();
        // This is the expected docker invocation for this case. customNetwork2
        // ("sdn2") is the expected network to be used in this case
        counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  hostname=ctr-e11-1518975676334-14532816-01-000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=sdn2", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
        // disallowed network should trigger a launch failure
        env.put(ENV_DOCKER_CONTAINER_NETWORK, customNetwork3);
        try {
            runtime.launchContainer(builder.build());
            Assert.fail((("Disallowed network : " + customNetwork3) + "did not trigger launch failure."));
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testLaunchPidNamespaceContainersInvalidEnvVar() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_PID_NAMESPACE, "invalid-value");
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        Assert.assertEquals(expected, dockerCommands.size());
        String command = dockerCommands.get(0);
        // ensure --pid isn't in the invocation
        Assert.assertTrue(("Unexpected --pid in docker run args : " + command), (!(command.contains("--pid"))));
    }

    @Test
    public void testLaunchPidNamespaceContainersWithDisabledSetting() throws ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_PID_NAMESPACE, "host");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a pid host disabled container failure.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testLaunchPidNamespaceContainersEnabled() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        // Enable host pid namespace containers.
        conf.setBoolean(NM_DOCKER_ALLOW_HOST_PID_NAMESPACE, true);
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_PID_NAMESPACE, "host");
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 14;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals("  pid=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testLaunchPrivilegedContainersInvalidEnvVar() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER, "invalid-value");
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        Assert.assertEquals(expected, dockerCommands.size());
        String command = dockerCommands.get(0);
        // ensure --privileged isn't in the invocation
        Assert.assertTrue(("Unexpected --privileged in docker run args : " + command), (!(command.contains("--privileged"))));
    }

    @Test
    public void testLaunchPrivilegedContainersWithDisabledSetting() throws ContainerExecutionException {
        conf.setBoolean(NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS, false);
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER, "true");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a privileged launch container failure.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testLaunchPrivilegedContainersWithEnabledSettingAndDefaultACL() throws ContainerExecutionException {
        // Enable privileged containers.
        conf.setBoolean(NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS, true);
        conf.set(NM_DOCKER_PRIVILEGED_CONTAINERS_ACL, "");
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER, "true");
        // By default
        // yarn.nodemanager.runtime.linux.docker.privileged-containers.acl
        // is empty. So we expect this launch to fail.
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a privileged launch container failure.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testLaunchPrivilegedContainersEnabledAndUserNotInWhitelist() throws ContainerExecutionException {
        // Enable privileged containers.
        conf.setBoolean(NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS, true);
        // set whitelist of users.
        conf.set(NM_DOCKER_PRIVILEGED_CONTAINERS_ACL, whitelistedUser);
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER, "true");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a privileged launch container failure.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testLaunchPrivilegedContainersEnabledAndUserInWhitelist() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        // Enable privileged containers.
        conf.setBoolean(NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS, true);
        // Add submittingUser to whitelist.
        conf.set(NM_DOCKER_PRIVILEGED_CONTAINERS_ACL, submittingUser);
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER, "true");
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals("  privileged=true", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (submittingUser)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testCGroupParent() throws ContainerExecutionException {
        String hierarchy = "hadoop-yarn-test";
        conf.set(NM_LINUX_CONTAINER_CGROUPS_HIERARCHY, hierarchy);
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        String resourceOptionsNone = "cgroups=none";
        DockerRunCommand command = Mockito.mock(DockerRunCommand.class);
        Mockito.when(mockCGroupsHandler.getRelativePathForCGroup(containerId)).thenReturn(((hierarchy + "/") + (containerIdStr)));
        runtime.addCGroupParentIfRequired(resourceOptionsNone, containerIdStr, command);
        // no --cgroup-parent should be added here
        Mockito.verifyZeroInteractions(command);
        String resourceOptionsCpu = ("/sys/fs/cgroup/cpu/" + hierarchy) + (containerIdStr);
        runtime.addCGroupParentIfRequired(resourceOptionsCpu, containerIdStr, command);
        // --cgroup-parent should be added for the containerId in question
        String expectedPath = (("/" + hierarchy) + "/") + (containerIdStr);
        Mockito.verify(command).setCGroupParent(expectedPath);
        // create a runtime with a 'null' cgroups handler - i.e no
        // cgroup-based resource handlers are in use.
        runtime = new DockerLinuxContainerRuntime(mockExecutor, null);
        runtime.initialize(conf, nmContext);
        runtime.addCGroupParentIfRequired(resourceOptionsNone, containerIdStr, command);
        runtime.addCGroupParentIfRequired(resourceOptionsCpu, containerIdStr, command);
        // no --cgroup-parent should be added in either case
        Mockito.verifyZeroInteractions(command);
    }

    @Test
    public void testMountSourceOnly() throws ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_MOUNTS, "/source");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to invalid mount.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testMountSourceTarget() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_MOUNTS, "test_dir/test_resource_file:test_mount:ro");
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + (((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro,") + "/test_local_dir/test_resource_file:test_mount:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testMountMultiple() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_MOUNTS, ("test_dir/test_resource_file:test_mount1:ro," + "test_dir/test_resource_file:test_mount2:ro"));
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro,") + "/test_local_dir/test_resource_file:test_mount1:ro,") + "/test_local_dir/test_resource_file:test_mount2:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testUserMounts() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_MOUNTS, ("/tmp/foo:/tmp/foo:ro,/tmp/bar:/tmp/bar:rw,/tmp/baz:/tmp/baz," + "/a:/a:shared,/b:/b:ro+shared,/c:/c:rw+rshared,/d:/d:private"));
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + (((((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro,") + "/tmp/foo:/tmp/foo:ro,") + "/tmp/bar:/tmp/bar:rw,/tmp/baz:/tmp/baz:rw,/a:/a:rw+shared,") + "/b:/b:ro+shared,/c:/c:rw+rshared,/d:/d:rw+private")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testUserMountInvalid() throws ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_MOUNTS, "/source:target:ro,/source:target:other,/source:target:rw");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to invalid mount.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testUserMountModeInvalid() throws ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_MOUNTS, "/source:target:other");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to invalid mode.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testUserMountModeNulInvalid() throws ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_MOUNTS, "/s\u0000ource:target:ro");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to NUL in mount.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testTmpfsMount() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_TMPFS_MOUNTS, "/run");
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        Assert.assertTrue(dockerCommands.contains("  tmpfs=/run"));
    }

    @Test
    public void testTmpfsMountMulti() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_TMPFS_MOUNTS, "/run,/tmp");
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        Assert.assertTrue(dockerCommands.contains("  tmpfs=/run,/tmp"));
    }

    @Test
    public void testDefaultTmpfsMounts() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        conf.setStrings(NM_DOCKER_DEFAULT_TMPFS_MOUNTS, "/run,/var/run");
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_TMPFS_MOUNTS, "/tmpfs");
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        Assert.assertTrue(dockerCommands.contains("  tmpfs=/tmpfs,/run,/var/run"));
    }

    @Test
    public void testDefaultTmpfsMountsInvalid() throws ContainerExecutionException {
        conf.setStrings(NM_DOCKER_DEFAULT_TMPFS_MOUNTS, "run,var/run");
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_TMPFS_MOUNTS, "/tmpfs");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to non-absolute path.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testTmpfsRelativeInvalid() throws ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_TMPFS_MOUNTS, "run");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to non-absolute path.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testTmpfsColonInvalid() throws ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_TMPFS_MOUNTS, "/run:");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to invalid character.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testTmpfsNulInvalid() throws ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        env.put(ENV_DOCKER_CONTAINER_TMPFS_MOUNTS, "/ru\u0000n");
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to NUL in tmpfs mount.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testDefaultROMounts() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        conf.setStrings(NM_DOCKER_DEFAULT_RO_MOUNTS, "/tmp/foo:/tmp/foo,/tmp/bar:/tmp/bar");
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + (((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro,") + "/tmp/foo:/tmp/foo:ro,/tmp/bar:/tmp/bar:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testDefaultROMountsInvalid() throws ContainerExecutionException {
        conf.setStrings(NM_DOCKER_DEFAULT_RO_MOUNTS, "source,target");
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to invalid mount.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testDefaultRWMounts() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        conf.setStrings(NM_DOCKER_DEFAULT_RW_MOUNTS, "/tmp/foo:/tmp/foo,/tmp/bar:/tmp/bar");
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        runtime.launchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands();
        int expected = 13;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + (((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro,") + "/tmp/foo:/tmp/foo:rw,/tmp/bar:/tmp/bar:rw")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testDefaultRWMountsInvalid() throws ContainerExecutionException {
        conf.setStrings(NM_DOCKER_DEFAULT_RW_MOUNTS, "source,target");
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        try {
            runtime.launchContainer(builder.build());
            Assert.fail("Expected a launch container failure due to invalid mount.");
        } catch (ContainerExecutionException e) {
            TestDockerContainerRuntime.LOG.info(("Caught expected exception : " + e));
        }
    }

    @Test
    public void testContainerLivelinessFileExistsNoException() throws Exception {
        File testTempDir = tempDir.newFolder();
        File procPidPath = new File(((testTempDir + (File.separator)) + (signalPid)));
        procPidPath.createNewFile();
        procPidPath.deleteOnExit();
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        builder.setExecutionAttribute(LinuxContainerRuntimeConstants.RUN_AS_USER, runAsUser).setExecutionAttribute(LinuxContainerRuntimeConstants.USER, user).setExecutionAttribute(LinuxContainerRuntimeConstants.PID, signalPid).setExecutionAttribute(LinuxContainerRuntimeConstants.SIGNAL, NULL).setExecutionAttribute(LinuxContainerRuntimeConstants.PROCFS, testTempDir.getAbsolutePath());
        runtime.initialize(TestDockerContainerRuntime.enableMockContainerExecutor(conf), null);
        runtime.signalContainer(builder.build());
    }

    @Test
    public void testContainerLivelinessNoFileException() throws Exception {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        builder.setExecutionAttribute(LinuxContainerRuntimeConstants.RUN_AS_USER, runAsUser).setExecutionAttribute(LinuxContainerRuntimeConstants.USER, user).setExecutionAttribute(LinuxContainerRuntimeConstants.PID, signalPid).setExecutionAttribute(LinuxContainerRuntimeConstants.SIGNAL, NULL);
        runtime.initialize(TestDockerContainerRuntime.enableMockContainerExecutor(conf), null);
        try {
            runtime.signalContainer(builder.build());
        } catch (ContainerExecutionException e) {
            Assert.assertEquals(INVALID_CONTAINER_PID.getValue(), e.getExitCode());
        }
    }

    @Test
    public void testDockerStopOnTermSignalWhenRunning() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        Mockito.when(mockExecutor.executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(RUNNING.getName());
        List<String> dockerCommands = getDockerCommandsForDockerStop(TERM);
        TestDockerContainerRuntime.verifyStopCommand(dockerCommands, TERM.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDockerStopWithQuitSignalWhenRunning() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        Mockito.when(mockExecutor.executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(((RUNNING.getName()) + ",SIGQUIT"));
        List<String> dockerCommands = getDockerCommandsForDockerStop(TERM);
        TestDockerContainerRuntime.verifyStopCommand(dockerCommands, "SIGQUIT");
    }

    @Test
    public void testDockerStopOnKillSignalWhenRunning() throws PrivilegedOperationException, ContainerExecutionException {
        List<String> dockerCommands = getDockerCommandsForSignal(KILL);
        Assert.assertEquals(5, dockerCommands.size());
        Assert.assertEquals(runAsUser, dockerCommands.get(0));
        Assert.assertEquals(user, dockerCommands.get(1));
        Assert.assertEquals(Integer.toString(SIGNAL_CONTAINER.getValue()), dockerCommands.get(2));
        Assert.assertEquals(signalPid, dockerCommands.get(3));
        Assert.assertEquals(Integer.toString(KILL.getValue()), dockerCommands.get(4));
    }

    @Test
    public void testDockerKillOnQuitSignalWhenRunning() throws Exception {
        List<String> dockerCommands = getDockerCommandsForSignal(QUIT);
        Assert.assertEquals(5, dockerCommands.size());
        Assert.assertEquals(runAsUser, dockerCommands.get(0));
        Assert.assertEquals(user, dockerCommands.get(1));
        Assert.assertEquals(Integer.toString(SIGNAL_CONTAINER.getValue()), dockerCommands.get(2));
        Assert.assertEquals(signalPid, dockerCommands.get(3));
        Assert.assertEquals(Integer.toString(QUIT.getValue()), dockerCommands.get(4));
    }

    @Test
    public void testDockerStopOnTermSignalWhenRunningPrivileged() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        conf.set(NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS, "true");
        conf.set(NM_DOCKER_PRIVILEGED_CONTAINERS_ACL, submittingUser);
        env.put(DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER, "true");
        Mockito.when(mockExecutor.executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(RUNNING.getName());
        List<String> dockerCommands = getDockerCommandsForDockerStop(TERM);
        TestDockerContainerRuntime.verifyStopCommand(dockerCommands, TERM.toString());
    }

    @Test
    public void testDockerStopOnKillSignalWhenRunningPrivileged() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        conf.set(NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS, "true");
        conf.set(NM_DOCKER_PRIVILEGED_CONTAINERS_ACL, submittingUser);
        env.put(DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER, "true");
        Mockito.when(mockExecutor.executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(RUNNING.getName());
        List<String> dockerCommands = getDockerCommandsForDockerStop(KILL);
        Assert.assertEquals(4, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get(0));
        Assert.assertEquals("  docker-command=kill", dockerCommands.get(1));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get(2));
        Assert.assertEquals("  signal=KILL", dockerCommands.get(3));
    }

    @Test
    public void testDockerKillOnQuitSignalWhenRunningPrivileged() throws Exception {
        conf.set(NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS, "true");
        conf.set(NM_DOCKER_PRIVILEGED_CONTAINERS_ACL, submittingUser);
        env.put(DockerLinuxContainerRuntime.ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER, "true");
        Mockito.when(mockExecutor.executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(RUNNING.getName());
        List<String> dockerCommands = getDockerCommandsForDockerStop(QUIT);
        Assert.assertEquals(4, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get(0));
        Assert.assertEquals("  docker-command=kill", dockerCommands.get(1));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get(2));
        Assert.assertEquals("  signal=QUIT", dockerCommands.get(3));
    }

    @Test
    public void testDockerRmOnWhenExited() throws Exception {
        env.put(ENV_DOCKER_CONTAINER_DELAYED_REMOVAL, "false");
        conf.set(NM_DOCKER_ALLOW_DELAYED_REMOVAL, "true");
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        builder.setExecutionAttribute(LinuxContainerRuntimeConstants.RUN_AS_USER, runAsUser).setExecutionAttribute(LinuxContainerRuntimeConstants.USER, user);
        runtime.initialize(TestDockerContainerRuntime.enableMockContainerExecutor(conf), null);
        runtime.reapContainer(builder.build());
        Mockito.verify(mockExecutor, Mockito.times(1)).executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testNoDockerRmWhenDelayedDeletionEnabled() throws Exception {
        env.put(ENV_DOCKER_CONTAINER_DELAYED_REMOVAL, "true");
        conf.set(NM_DOCKER_ALLOW_DELAYED_REMOVAL, "true");
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        builder.setExecutionAttribute(LinuxContainerRuntimeConstants.RUN_AS_USER, runAsUser).setExecutionAttribute(LinuxContainerRuntimeConstants.USER, user);
        runtime.initialize(TestDockerContainerRuntime.enableMockContainerExecutor(conf), null);
        runtime.reapContainer(builder.build());
        Mockito.verify(mockExecutor, Mockito.never()).executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyMap(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDockerImageNamePattern() throws Exception {
        String[] validNames = new String[]{ "ubuntu", "fedora/httpd:version1.0", "fedora/httpd:version1.0.test", "fedora/httpd:version1.0.TEST", "myregistryhost:5000/ubuntu", "myregistryhost:5000/fedora/httpd:version1.0", "myregistryhost:5000/fedora/httpd:version1.0.test", "myregistryhost:5000/fedora/httpd:version1.0.TEST" };
        String[] invalidNames = new String[]{ "Ubuntu", "ubuntu || fedora", "ubuntu#", "myregistryhost:50AB0/ubuntu", "myregistry#host:50AB0/ubuntu", ":8080/ubuntu" };
        for (String name : validNames) {
            DockerLinuxContainerRuntime.validateImageName(name);
        }
        for (String name : invalidNames) {
            try {
                DockerLinuxContainerRuntime.validateImageName(name);
                Assert.fail((name + " is an invalid name and should fail the regex"));
            } catch (ContainerExecutionException ce) {
                continue;
            }
        }
    }

    @Test
    public void testDockerHostnamePattern() throws Exception {
        String[] validNames = new String[]{ "ab", "a.b.c.d", "a1-b.cd.ef", "0AB.", "C_D-" };
        String[] invalidNames = new String[]{ "a", "a#.b.c", "-a.b.c", "a@b.c", "a/b/c" };
        for (String name : validNames) {
            DockerLinuxContainerRuntime.validateHostname(name);
        }
        for (String name : invalidNames) {
            try {
                DockerLinuxContainerRuntime.validateHostname(name);
                Assert.fail((name + " is an invalid hostname and should fail the regex"));
            } catch (ContainerExecutionException ce) {
                continue;
            }
        }
    }

    @Test
    public void testValidDockerHostnameLength() throws Exception {
        String validLength = "example.test.site";
        DockerLinuxContainerRuntime.validateHostname(validLength);
    }

    @Test(expected = ContainerExecutionException.class)
    public void testInvalidDockerHostnameLength() throws Exception {
        String invalidLength = "exampleexampleexampleexampleexampleexampleexampleexample.test.site";
        DockerLinuxContainerRuntime.validateHostname(invalidLength);
    }

    private static class MockDockerCommandPlugin implements DockerCommandPlugin {
        private final String volume;

        private final String driver;

        public MockDockerCommandPlugin(String volume, String driver) {
            this.volume = volume;
            this.driver = driver;
        }

        @Override
        public void updateDockerRunCommand(DockerRunCommand dockerRunCommand, Container container) throws ContainerExecutionException {
            dockerRunCommand.setVolumeDriver("driver-1");
            dockerRunCommand.addReadOnlyMountLocation("/source/path", "/destination/path", true);
        }

        @Override
        public DockerVolumeCommand getCreateDockerVolumeCommand(Container container) throws ContainerExecutionException {
            return new DockerVolumeCommand("create").setVolumeName(volume).setDriverName(driver);
        }

        @Override
        public DockerVolumeCommand getCleanupDockerVolumesCommand(Container container) throws ContainerExecutionException {
            return null;
        }
    }

    @Test
    public void testDockerCommandPluginCheckVolumeAfterCreation() throws Exception {
        // For following tests, we expect to have volume1,local in output
        // Failure cases
        testDockerCommandPluginWithVolumesOutput("DRIVER              VOLUME NAME\n", true);
        testDockerCommandPluginWithVolumesOutput("", true);
        testDockerCommandPluginWithVolumesOutput("volume1", true);
        testDockerCommandPluginWithVolumesOutput(("DRIVER              VOLUME NAME\n" + "nvidia-docker       nvidia_driver_375.66\n"), true);
        testDockerCommandPluginWithVolumesOutput(("DRIVER              VOLUME NAME\n" + "                    volume1\n"), true);
        testDockerCommandPluginWithVolumesOutput("local", true);
        testDockerCommandPluginWithVolumesOutput("volume2,local", true);
        testDockerCommandPluginWithVolumesOutput(("DRIVER              VOLUME NAME\n" + "local               volume2\n"), true);
        testDockerCommandPluginWithVolumesOutput("volum1,something", true);
        testDockerCommandPluginWithVolumesOutput(("DRIVER              VOLUME NAME\n" + "something               volume1\n"), true);
        testDockerCommandPluginWithVolumesOutput("volum1,something\nvolum2,local", true);
        // Success case
        testDockerCommandPluginWithVolumesOutput(("DRIVER              VOLUME NAME\n" + ("nvidia-docker       nvidia_driver_375.66\n" + "local               volume1\n")), false);
        testDockerCommandPluginWithVolumesOutput("volume_xyz,nvidia\nvolume1,local\n\n", false);
        testDockerCommandPluginWithVolumesOutput(" volume1,  local \n", false);
        testDockerCommandPluginWithVolumesOutput("volume_xyz,\tnvidia\n   volume1,\tlocal\n\n", false);
    }

    @Test
    public void testDockerCommandPlugin() throws Exception {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        Mockito.when(mockExecutor.executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(null, "volume1,local");
        Context mockNMContext = createMockNMContext();
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Map<String, ResourcePlugin> pluginsMap = new HashMap<>();
        ResourcePlugin plugin1 = Mockito.mock(ResourcePlugin.class);
        // Create the docker command plugin logic, which will set volume driver
        DockerCommandPlugin dockerCommandPlugin = new TestDockerContainerRuntime.MockDockerCommandPlugin("volume1", "local");
        Mockito.when(plugin1.getDockerCommandPluginInstance()).thenReturn(dockerCommandPlugin);
        ResourcePlugin plugin2 = Mockito.mock(ResourcePlugin.class);
        pluginsMap.put("plugin1", plugin1);
        pluginsMap.put("plugin2", plugin2);
        Mockito.when(rpm.getNameToPlugins()).thenReturn(pluginsMap);
        Mockito.when(mockNMContext.getResourcePluginManager()).thenReturn(rpm);
        runtime.initialize(conf, mockNMContext);
        ContainerRuntimeContext containerRuntimeContext = builder.build();
        runtime.prepareContainer(containerRuntimeContext);
        runtime.launchContainer(containerRuntimeContext);
        checkVolumeCreateCommand();
        List<String> dockerCommands = readDockerCommands(3);
        int expected = 14;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + (((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro,") + "/source/path:/destination/path:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        // Verify volume-driver is set to expected value.
        Assert.assertEquals("  volume-driver=driver-1", dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get(counter));
    }

    @Test
    public void testDockerCapabilities() throws ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        try {
            conf.setStrings(NM_DOCKER_CONTAINER_CAPABILITIES, "none", "CHOWN", "DAC_OVERRIDE");
            runtime.initialize(conf, nmContext);
            Assert.fail(("Initialize didn't fail with invalid capabilities " + "'none', 'CHOWN', 'DAC_OVERRIDE'"));
        } catch (ContainerExecutionException e) {
        }
        try {
            conf.setStrings(NM_DOCKER_CONTAINER_CAPABILITIES, "CHOWN", "DAC_OVERRIDE", "NONE");
            runtime.initialize(conf, nmContext);
            Assert.fail(("Initialize didn't fail with invalid capabilities " + "'CHOWN', 'DAC_OVERRIDE', 'NONE'"));
        } catch (ContainerExecutionException e) {
        }
        conf.setStrings(NM_DOCKER_CONTAINER_CAPABILITIES, "NONE");
        runtime.initialize(conf, nmContext);
        Assert.assertEquals(0, runtime.getCapabilities().size());
        conf.setStrings(NM_DOCKER_CONTAINER_CAPABILITIES, "none");
        runtime.initialize(conf, nmContext);
        Assert.assertEquals(0, runtime.getCapabilities().size());
        conf.setStrings(NM_DOCKER_CONTAINER_CAPABILITIES, "CHOWN", "DAC_OVERRIDE");
        runtime.initialize(conf, nmContext);
        Iterator<String> it = runtime.getCapabilities().iterator();
        Assert.assertEquals("CHOWN", it.next());
        Assert.assertEquals("DAC_OVERRIDE", it.next());
    }

    @Test
    public void testLaunchContainerWithDockerTokens() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        // Write the JSOn to a temp file.
        File file = File.createTempFile("docker-client-config", "runtime-test");
        file.deleteOnExit();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(JSON);
        bw.close();
        // Get the credentials object with the Tokens.
        Credentials credentials = DockerClientConfigHandler.readCredentialsFromConfigFile(new Path(file.toURI()), conf, appId);
        DataOutputBuffer dob = new DataOutputBuffer();
        credentials.writeTokenStorageToStream(dob);
        ByteBuffer tokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        // Configure the runtime and launch the container
        Mockito.when(context.getTokens()).thenReturn(tokens);
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        runtime.initialize(conf, nmContext);
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr--");
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
        Path outDir = new Path(((Files.createTempDirectory("docker-client-config-out", attr).toUri().getPath()) + "/launch_container.sh"));
        builder.setExecutionAttribute(LinuxContainerRuntimeConstants.NM_PRIVATE_CONTAINER_SCRIPT_PATH, outDir);
        runtime.launchContainer(builder.build());
        PrivilegedOperation op = capturePrivilegedOperation();
        Assert.assertEquals(LAUNCH_DOCKER_CONTAINER, op.getOperationType());
        List<String> args = op.getArguments();
        int expectedArgs = (https) ? 15 : 13;
        int argsCounter = 0;
        Assert.assertEquals(expectedArgs, args.size());
        Assert.assertEquals(runAsUser, args.get((argsCounter++)));
        Assert.assertEquals(user, args.get((argsCounter++)));
        Assert.assertEquals(Integer.toString(PrivilegedOperation.RunAsUserCommand.LAUNCH_DOCKER_CONTAINER.getValue()), args.get((argsCounter++)));
        Assert.assertEquals(appId, args.get((argsCounter++)));
        Assert.assertEquals(containerId, args.get((argsCounter++)));
        Assert.assertEquals(containerWorkDir.toString(), args.get((argsCounter++)));
        Assert.assertEquals(outDir.toUri().getPath(), args.get((argsCounter++)));
        Assert.assertEquals(nmPrivateTokensPath.toUri().getPath(), args.get((argsCounter++)));
        if (https) {
            Assert.assertEquals("--https", args.get((argsCounter++)));
            Assert.assertEquals(nmPrivateKeystorePath.toUri().toString(), args.get((argsCounter++)));
            Assert.assertEquals(nmPrivateTruststorePath.toUri().toString(), args.get((argsCounter++)));
        } else {
            Assert.assertEquals("--http", args.get((argsCounter++)));
        }
        Assert.assertEquals(pidFilePath.toString(), args.get((argsCounter++)));
        Assert.assertEquals(localDirs.get(0), args.get((argsCounter++)));
        Assert.assertEquals(logDirs.get(0), args.get((argsCounter++)));
        String dockerCommandFile = args.get((argsCounter++));
        List<String> dockerCommands = Files.readAllLines(Paths.get(dockerCommandFile), Charset.forName("UTF-8"));
        int expected = 14;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-add=SYS_CHROOT,NET_BIND_SERVICE", dockerCommands.get((counter++)));
        Assert.assertEquals("  cap-drop=ALL", dockerCommands.get((counter++)));
        Assert.assertEquals("  detach=true", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=run", dockerCommands.get((counter++)));
        Assert.assertEquals(("  docker-config=" + (outDir.getParent())), dockerCommands.get((counter++)));
        Assert.assertEquals(("  group-add=" + (String.join(",", groups))), dockerCommands.get((counter++)));
        Assert.assertEquals("  image=busybox:latest", dockerCommands.get((counter++)));
        Assert.assertEquals("  launch-command=bash,/test_container_work_dir/launch_container.sh", dockerCommands.get((counter++)));
        Assert.assertEquals(("  mounts=" + ((("/test_container_log_dir:/test_container_log_dir:rw," + "/test_application_local_dir:/test_application_local_dir:rw,") + "/test_filecache_dir:/test_filecache_dir:ro,") + "/test_user_filecache_dir:/test_user_filecache_dir:ro")), dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get((counter++)));
        Assert.assertEquals("  net=host", dockerCommands.get((counter++)));
        Assert.assertEquals(("  user=" + (uidGidPair)), dockerCommands.get((counter++)));
        Assert.assertEquals("  workdir=/test_container_work_dir", dockerCommands.get((counter++)));
    }

    @Test
    public void testDockerContainerRelaunch() throws IOException, PrivilegedOperationException, ContainerExecutionException {
        DockerLinuxContainerRuntime runtime = new DockerLinuxContainerRuntime(mockExecutor, mockCGroupsHandler);
        Mockito.when(mockExecutor.executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(STOPPED.getName());
        runtime.initialize(conf, nmContext);
        runtime.relaunchContainer(builder.build());
        List<String> dockerCommands = readDockerCommands(2);
        int expected = 3;
        int counter = 0;
        Assert.assertEquals(expected, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get((counter++)));
        Assert.assertEquals("  docker-command=start", dockerCommands.get((counter++)));
        Assert.assertEquals("  name=container_e11_1518975676334_14532816_01_000001", dockerCommands.get(counter));
    }
}

