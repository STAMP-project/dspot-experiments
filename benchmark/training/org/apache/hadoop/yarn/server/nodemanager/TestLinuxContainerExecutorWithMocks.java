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
package org.apache.hadoop.yarn.server.nodemanager;


import ContainerExecutor.Signal;
import PrivilegedOperation.LINUX_FILE_PATH_SEPARATOR;
import PrivilegedOperation.RunAsUserCommand.DELETE_AS_USER;
import PrivilegedOperation.RunAsUserCommand.LAUNCH_CONTAINER;
import PrivilegedOperation.RunAsUserCommand.SIGNAL_CONTAINER;
import YarnConfiguration.DEFAULT_NM_NONSECURE_MODE_LOCAL_USER;
import YarnConfiguration.NM_CONTAINER_EXECUTOR_SCHED_PRIORITY;
import YarnConfiguration.NM_LOCAL_DIRS;
import YarnConfiguration.NM_LOG_DIRS;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.exceptions.ConfigurationException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerDiagnosticsUpdateEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.LinuxContainerRuntime;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerExecutionException;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerSignalContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerStartContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.DeletionAsUserContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.LocalizerStartContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestLinuxContainerExecutorWithMocks {
    private static final Logger LOG = LoggerFactory.getLogger(TestLinuxContainerExecutorWithMocks.class);

    private static final String MOCK_EXECUTOR = "mock-container-executor";

    private static final String MOCK_EXECUTOR_WITH_ERROR = "mock-container-executer-with-error";

    private static final String MOCK_EXECUTOR_WITH_CONFIG_ERROR = "mock-container-executer-with-configuration-error";

    private String tmpMockExecutor;

    private LinuxContainerExecutor mockExec = null;

    private LinuxContainerExecutor mockExecMockRuntime = null;

    private PrivilegedOperationExecutor mockPrivilegedExec;

    private final File mockParamFile = new File("./params.txt");

    private LocalDirsHandlerService dirsHandler;

    @Test
    public void testContainerLaunchWithoutHTTPS() throws IOException, ConfigurationException {
        testContainerLaunch(false);
    }

    @Test
    public void testContainerLaunchWithHTTPS() throws IOException, ConfigurationException {
        testContainerLaunch(true);
    }

    @Test(timeout = 5000)
    public void testContainerLaunchWithPriority() throws IOException, URISyntaxException, ConfigurationException {
        // set the scheduler priority to make sure still works with nice -n prio
        Configuration conf = new Configuration();
        setupMockExecutor(TestLinuxContainerExecutorWithMocks.MOCK_EXECUTOR, conf);
        conf.setInt(NM_CONTAINER_EXECUTOR_SCHED_PRIORITY, 2);
        mockExec.setConf(conf);
        List<String> command = new ArrayList<String>();
        mockExec.addSchedPriorityCommand(command);
        Assert.assertEquals("first should be nice", "nice", command.get(0));
        Assert.assertEquals("second should be -n", "-n", command.get(1));
        Assert.assertEquals("third should be the priority", Integer.toString(2), command.get(2));
        testContainerLaunchWithoutHTTPS();
    }

    @Test(timeout = 5000)
    public void testLaunchCommandWithoutPriority() throws IOException {
        // make sure the command doesn't contain the nice -n since priority
        // not specified
        List<String> command = new ArrayList<String>();
        mockExec.addSchedPriorityCommand(command);
        Assert.assertEquals("addSchedPriority should be empty", 0, command.size());
    }

    @Test(timeout = 5000)
    public void testStartLocalizer() throws IOException {
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 8040);
        Path nmPrivateCTokensPath = new Path("file:///bin/nmPrivateCTokensPath");
        try {
            mockExec.startLocalizer(new LocalizerStartContext.Builder().setNmPrivateContainerTokens(nmPrivateCTokensPath).setNmAddr(address).setUser("test").setAppId("application_0").setLocId("12345").setDirsHandler(dirsHandler).build());
            List<String> result = readMockParams();
            Assert.assertEquals(result.size(), 26);
            Assert.assertEquals(result.get(0), DEFAULT_NM_NONSECURE_MODE_LOCAL_USER);
            Assert.assertEquals(result.get(1), "test");
            Assert.assertEquals(result.get(2), "0");
            Assert.assertEquals(result.get(3), "application_0");
            Assert.assertEquals(result.get(4), "12345");
            Assert.assertEquals(result.get(5), "/bin/nmPrivateCTokensPath");
            Assert.assertEquals(result.get(9), "-classpath");
            Assert.assertEquals(result.get(12), "-Xmx256m");
            Assert.assertEquals(result.get(13), "-Dlog4j.configuration=container-log4j.properties");
            Assert.assertEquals(result.get(14), String.format("-Dyarn.app.container.log.dir=%s/application_0/12345", mockExec.getConf().get(NM_LOG_DIRS)));
            Assert.assertEquals(result.get(15), "-Dyarn.app.container.log.filesize=0");
            Assert.assertEquals(result.get(16), "-Dhadoop.root.logger=INFO,CLA");
            Assert.assertEquals(result.get(17), "-Dhadoop.root.logfile=container-localizer-syslog");
            Assert.assertEquals(result.get(18), "org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ContainerLocalizer");
            Assert.assertEquals(result.get(19), "test");
            Assert.assertEquals(result.get(20), "application_0");
            Assert.assertEquals(result.get(21), "12345");
            Assert.assertEquals(result.get(22), "localhost");
            Assert.assertEquals(result.get(23), "8040");
            Assert.assertEquals(result.get(24), "nmPrivateCTokensPath");
        } catch (InterruptedException e) {
            TestLinuxContainerExecutorWithMocks.LOG.error(("Error:" + (e.getMessage())), e);
            Assert.fail();
        }
    }

    @Test
    public void testContainerLaunchError() throws IOException, URISyntaxException, ContainerExecutionException {
        final String[] expecetedMessage = new String[]{ "badcommand", "Exit code: 24" };
        final String[] executor = new String[]{ TestLinuxContainerExecutorWithMocks.MOCK_EXECUTOR_WITH_ERROR, TestLinuxContainerExecutorWithMocks.MOCK_EXECUTOR_WITH_CONFIG_ERROR };
        for (int i = 0; i < (expecetedMessage.length); ++i) {
            final int j = i;
            // reinitialize executer
            Configuration conf = new Configuration();
            setupMockExecutor(executor[j], conf);
            conf.set(NM_LOCAL_DIRS, "file:///bin/echo");
            conf.set(NM_LOG_DIRS, "file:///dev/null");
            LinuxContainerExecutor exec;
            LinuxContainerRuntime linuxContainerRuntime = new org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.DefaultLinuxContainerRuntime(PrivilegedOperationExecutor.getInstance(conf));
            linuxContainerRuntime.initialize(conf, null);
            exec = new LinuxContainerExecutor(linuxContainerRuntime);
            mockExec = Mockito.spy(exec);
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                    String diagnostics = ((String) (invocationOnMock.getArguments()[0]));
                    Assert.assertTrue(("Invalid Diagnostics message: " + diagnostics), diagnostics.contains(expecetedMessage[j]));
                    return null;
                }
            }).when(mockExec).logOutput(ArgumentMatchers.any(String.class));
            dirsHandler = new LocalDirsHandlerService();
            dirsHandler.init(conf);
            mockExec.setConf(conf);
            String appSubmitter = "nobody";
            String cmd = String.valueOf(LAUNCH_CONTAINER.getValue());
            String appId = "APP_ID";
            String containerId = "CONTAINER_ID";
            Container container = Mockito.mock(Container.class);
            ContainerId cId = Mockito.mock(ContainerId.class);
            ContainerLaunchContext context = Mockito.mock(ContainerLaunchContext.class);
            HashMap<String, String> env = new HashMap<String, String>();
            Mockito.when(container.getContainerId()).thenReturn(cId);
            Mockito.when(container.getLaunchContext()).thenReturn(context);
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                    ContainerDiagnosticsUpdateEvent event = ((ContainerDiagnosticsUpdateEvent) (invocationOnMock.getArguments()[0]));
                    Assert.assertTrue(("Invalid Diagnostics message: " + (event.getDiagnosticsUpdate())), event.getDiagnosticsUpdate().contains(expecetedMessage[j]));
                    return null;
                }
            }).when(container).handle(ArgumentMatchers.any(ContainerDiagnosticsUpdateEvent.class));
            Mockito.when(cId.toString()).thenReturn(containerId);
            Mockito.when(context.getEnvironment()).thenReturn(env);
            Path scriptPath = new Path("file:///bin/echo");
            Path tokensPath = new Path("file:///dev/null");
            Path workDir = new Path("/tmp");
            Path pidFile = new Path(workDir, "pid.txt");
            mockExec.activateContainer(cId, pidFile);
            try {
                int ret = mockExec.launchContainer(new ContainerStartContext.Builder().setContainer(container).setNmPrivateContainerScriptPath(scriptPath).setNmPrivateTokensPath(tokensPath).setUser(appSubmitter).setAppId(appId).setContainerWorkDir(workDir).setLocalDirs(dirsHandler.getLocalDirs()).setLogDirs(dirsHandler.getLogDirs()).setFilecacheDirs(new ArrayList()).setUserLocalDirs(new ArrayList()).setContainerLocalDirs(new ArrayList()).setContainerLogDirs(new ArrayList()).setUserFilecacheDirs(new ArrayList()).setApplicationLocalDirs(new ArrayList()).build());
                Assert.assertNotSame(0, ret);
                Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, appId, containerId, workDir.toString(), "/bin/echo", "/dev/null", "--http", pidFile.toString(), StringUtils.join(LINUX_FILE_PATH_SEPARATOR, dirsHandler.getLocalDirs()), StringUtils.join(LINUX_FILE_PATH_SEPARATOR, dirsHandler.getLogDirs()), "cgroups=none"), readMockParams());
                Assert.assertNotEquals("Expected YarnRuntimeException", TestLinuxContainerExecutorWithMocks.MOCK_EXECUTOR_WITH_CONFIG_ERROR, executor[i]);
            } catch (ConfigurationException ex) {
                Assert.assertEquals(TestLinuxContainerExecutorWithMocks.MOCK_EXECUTOR_WITH_CONFIG_ERROR, executor[i]);
                Assert.assertEquals(("Linux Container Executor reached unrecoverable " + "exception"), ex.getMessage());
            }
        }
    }

    @Test
    public void testInit() throws Exception {
        mockExec.init(Mockito.mock(Context.class));
        Assert.assertEquals(Arrays.asList("--checksetup"), readMockParams());
    }

    @Test
    public void testContainerKill() throws IOException {
        String appSubmitter = "nobody";
        String cmd = String.valueOf(SIGNAL_CONTAINER.getValue());
        ContainerExecutor.Signal signal = Signal.QUIT;
        String sigVal = String.valueOf(signal.getValue());
        Container container = Mockito.mock(Container.class);
        ContainerId cId = Mockito.mock(ContainerId.class);
        ContainerLaunchContext context = Mockito.mock(ContainerLaunchContext.class);
        Mockito.when(container.getContainerId()).thenReturn(cId);
        Mockito.when(container.getLaunchContext()).thenReturn(context);
        mockExec.signalContainer(new ContainerSignalContext.Builder().setContainer(container).setUser(appSubmitter).setPid("1000").setSignal(signal).build());
        Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, "1000", sigVal), readMockParams());
    }

    @Test
    public void testDeleteAsUser() throws IOException, URISyntaxException {
        String appSubmitter = "nobody";
        String cmd = String.valueOf(DELETE_AS_USER.getValue());
        Path dir = new Path("/tmp/testdir");
        Path testFile = new Path("testfile");
        Path baseDir0 = new Path("/grid/0/BaseDir");
        Path baseDir1 = new Path("/grid/1/BaseDir");
        mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(dir).build());
        Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, "/tmp/testdir"), readMockParams());
        mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).build());
        Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, ""), readMockParams());
        mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(testFile).setBasedirs(baseDir0, baseDir1).build());
        Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, testFile.toString(), baseDir0.toString(), baseDir1.toString()), readMockParams());
        mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setBasedirs(baseDir0, baseDir1).build());
        Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, "", baseDir0.toString(), baseDir1.toString()), readMockParams());
        Configuration conf = new Configuration();
        setupMockExecutor(TestLinuxContainerExecutorWithMocks.MOCK_EXECUTOR, conf);
        mockExec.setConf(conf);
        mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(dir).build());
        Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, "/tmp/testdir"), readMockParams());
        mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).build());
        Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, ""), readMockParams());
        mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(testFile).setBasedirs(baseDir0, baseDir1).build());
        Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, testFile.toString(), baseDir0.toString(), baseDir1.toString()), readMockParams());
        mockExec.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setBasedirs(baseDir0, baseDir1).build());
        Assert.assertEquals(Arrays.asList(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, appSubmitter, cmd, "", baseDir0.toString(), baseDir1.toString()), readMockParams());
    }

    @Test
    public void testNoExitCodeFromPrivilegedOperation() throws Exception {
        Configuration conf = new Configuration();
        final PrivilegedOperationExecutor spyPrivilegedExecutor = Mockito.spy(PrivilegedOperationExecutor.getInstance(conf));
        Mockito.doThrow(new PrivilegedOperationException("interrupted")).when(spyPrivilegedExecutor).executePrivilegedOperation(ArgumentMatchers.any(), ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        LinuxContainerRuntime runtime = new org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.DefaultLinuxContainerRuntime(spyPrivilegedExecutor);
        runtime.initialize(conf, null);
        mockExec = new LinuxContainerExecutor(runtime);
        mockExec.setConf(conf);
        LinuxContainerExecutor lce = new LinuxContainerExecutor(runtime) {
            @Override
            protected PrivilegedOperationExecutor getPrivilegedOperationExecutor() {
                return spyPrivilegedExecutor;
            }
        };
        lce.setConf(conf);
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 8040);
        Path nmPrivateCTokensPath = new Path("file:///bin/nmPrivateCTokensPath");
        LocalDirsHandlerService dirService = new LocalDirsHandlerService();
        dirService.init(conf);
        String appSubmitter = "nobody";
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cid = ContainerId.newContainerId(attemptId, 1);
        HashMap<String, String> env = new HashMap<>();
        Container container = Mockito.mock(Container.class);
        ContainerLaunchContext context = Mockito.mock(ContainerLaunchContext.class);
        Mockito.when(container.getContainerId()).thenReturn(cid);
        Mockito.when(container.getLaunchContext()).thenReturn(context);
        Mockito.when(context.getEnvironment()).thenReturn(env);
        Path workDir = new Path("/tmp");
        try {
            lce.startLocalizer(new LocalizerStartContext.Builder().setNmPrivateContainerTokens(nmPrivateCTokensPath).setNmAddr(address).setUser(appSubmitter).setAppId(appId.toString()).setLocId("12345").setDirsHandler(dirService).build());
            Assert.fail("startLocalizer should have thrown an exception");
        } catch (IOException e) {
            Assert.assertTrue(("Unexpected exception " + e), e.getMessage().contains("exitCode"));
        }
        lce.activateContainer(cid, new Path(workDir, "pid.txt"));
        lce.launchContainer(new ContainerStartContext.Builder().setContainer(container).setNmPrivateContainerScriptPath(new Path("file:///bin/echo")).setNmPrivateTokensPath(new Path("file:///dev/null")).setUser(appSubmitter).setAppId(appId.toString()).setContainerWorkDir(workDir).setLocalDirs(dirsHandler.getLocalDirs()).setLogDirs(dirsHandler.getLogDirs()).setFilecacheDirs(new ArrayList()).setUserLocalDirs(new ArrayList()).setContainerLocalDirs(new ArrayList()).setContainerLogDirs(new ArrayList()).setUserFilecacheDirs(new ArrayList()).setApplicationLocalDirs(new ArrayList()).build());
        lce.deleteAsUser(new DeletionAsUserContext.Builder().setUser(appSubmitter).setSubDir(new Path("/tmp/testdir")).build());
        try {
            lce.mountCgroups(new ArrayList<String>(), "hierarchy");
            Assert.fail("mountCgroups should have thrown an exception");
        } catch (IOException e) {
            Assert.assertTrue(("Unexpected exception " + e), e.getMessage().contains("exit code"));
        }
    }

    @Test
    public void testContainerLaunchEnvironment() throws IOException, ConfigurationException, PrivilegedOperationException {
        String appSubmitter = "nobody";
        String appId = "APP_ID";
        String containerId = "CONTAINER_ID";
        Container container = Mockito.mock(Container.class);
        ContainerId cId = Mockito.mock(ContainerId.class);
        ContainerLaunchContext context = Mockito.mock(ContainerLaunchContext.class);
        HashMap<String, String> env = new HashMap<String, String>();
        env.put("FROM_CLIENT", "1");
        Mockito.when(container.getContainerId()).thenReturn(cId);
        Mockito.when(container.getLaunchContext()).thenReturn(context);
        Mockito.when(cId.toString()).thenReturn(containerId);
        Mockito.when(context.getEnvironment()).thenReturn(env);
        Path scriptPath = new Path("file:///bin/echo");
        Path tokensPath = new Path("file:///dev/null");
        Path workDir = new Path("/tmp");
        Path pidFile = new Path(workDir, "pid.txt");
        mockExecMockRuntime.activateContainer(cId, pidFile);
        mockExecMockRuntime.launchContainer(new ContainerStartContext.Builder().setContainer(container).setNmPrivateContainerScriptPath(scriptPath).setNmPrivateTokensPath(tokensPath).setUser(appSubmitter).setAppId(appId).setContainerWorkDir(workDir).setLocalDirs(dirsHandler.getLocalDirs()).setLogDirs(dirsHandler.getLogDirs()).setFilecacheDirs(new ArrayList()).setUserLocalDirs(new ArrayList()).setContainerLocalDirs(new ArrayList()).setContainerLogDirs(new ArrayList()).setUserFilecacheDirs(new ArrayList()).setApplicationLocalDirs(new ArrayList()).build());
        ArgumentCaptor<PrivilegedOperation> opCaptor = ArgumentCaptor.forClass(PrivilegedOperation.class);
        // Verify that
        Mockito.verify(mockPrivilegedExec, Mockito.times(1)).executePrivilegedOperation(ArgumentMatchers.any(), opCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq(null), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }
}

