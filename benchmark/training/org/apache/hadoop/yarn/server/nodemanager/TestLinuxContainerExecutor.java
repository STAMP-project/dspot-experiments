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


import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import ContainerExecutor.TOKEN_FILE_NAME_FMT;
import ContainerReapContext.Builder;
import Signal.TERM;
import YarnConfiguration.DEFAULT_NM_LOCALIZER_ADDRESS;
import YarnConfiguration.DEFAULT_NM_LOCALIZER_PORT;
import YarnConfiguration.DEFAULT_NM_NONSECURE_MODE_LOCAL_USER;
import YarnConfiguration.NM_BIND_HOST;
import YarnConfiguration.NM_LINUX_CONTAINER_RESOURCES_HANDLER;
import YarnConfiguration.NM_LOCALIZER_ADDRESS;
import YarnConfiguration.NM_NONSECURE_MODE_LIMIT_USERS;
import YarnConfiguration.NM_NONSECURE_MODE_LOCAL_USER_KEY;
import YarnConfiguration.NM_NONSECURE_MODE_USER_PATTERN_KEY;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.ConfigurationException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.LinuxContainerRuntime;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ResourceLocalizationService;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerExecContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerReacquisitionContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerReapContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerSignalContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerStartContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.LocalizerStartContext;
import org.apache.hadoop.yarn.server.nodemanager.util.LCEResourcesHandler;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is intended to test the LinuxContainerExecutor code, but because of some
 * security restrictions this can only be done with some special setup first. <br>
 * <ol>
 * <li>Compile the code with container-executor.conf.dir set to the location you
 * want for testing. <br>
 *
 * <pre>
 * <code>
 * > mvn clean install -Pnative -Dcontainer-executor.conf.dir=/etc/hadoop
 *                          -DskipTests
 * </code>
 * </pre>
 *
 * <li>Set up <code>${container-executor.conf.dir}/container-executor.cfg</code>
 * container-executor.cfg needs to be owned by root and have in it the proper
 * config values. <br>
 *
 * <pre>
 * <code>
 * > cat /etc/hadoop/container-executor.cfg
 * yarn.nodemanager.linux-container-executor.group=mapred
 * #depending on the user id of the application.submitter option
 * min.user.id=1
 * > sudo chown root:root /etc/hadoop/container-executor.cfg
 * > sudo chmod 444 /etc/hadoop/container-executor.cfg
 * </code>
 * </pre>
 *
 * <li>Move the binary and set proper permissions on it. It needs to be owned by
 * root, the group needs to be the group configured in container-executor.cfg,
 * and it needs the setuid bit set. (The build will also overwrite it so you
 * need to move it to a place that you can support it. <br>
 *
 * <pre>
 * <code>
 * > cp ./hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-nodemanager/src/main/c/container-executor/container-executor /tmp/
 * > sudo chown root:mapred /tmp/container-executor
 * > sudo chmod 4050 /tmp/container-executor
 * </code>
 * </pre>
 *
 * <li>Run the tests with the execution enabled (The user you run the tests as
 * needs to be part of the group from the config. <br>
 *
 * <pre>
 * <code>
 * mvn test -Dtest=TestLinuxContainerExecutor -Dapplication.submitter=nobody -Dcontainer-executor.path=/tmp/container-executor
 * </code>
 * </pre>
 *
 * <li>The test suite also contains tests to test mounting of CGroups. By
 * default, these tests are not run. To run them, add -Dcgroups.mount=<mount-point>
 * Please note that the test does not unmount the CGroups at the end of the test,
 * since that requires root permissions. <br>
 *
 * <li>The tests that are run are sensitive to directory permissions. All parent
 * directories must be searchable by the user that the tasks are run as. If you
 * wish to run the tests in a different directory, please set it using
 * -Dworkspace.dir
 *
 * </ol>
 */
public class TestLinuxContainerExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(TestLinuxContainerExecutor.class);

    private static File workSpace;

    static {
        String basedir = System.getProperty("workspace.dir");
        if ((basedir == null) || (basedir.isEmpty())) {
            basedir = "target";
        }
        TestLinuxContainerExecutor.workSpace = new File(basedir, ((TestLinuxContainerExecutor.class.getName()) + "-workSpace"));
    }

    private LinuxContainerExecutor exec = null;

    private String appSubmitter = null;

    private LocalDirsHandlerService dirsHandler;

    private Configuration conf;

    private FileContext files;

    private int id = 0;

    @Test
    public void testContainerLocalizer() throws Exception {
        Assume.assumeTrue(shouldRun());
        String locId = "container_01_01";
        Path nmPrivateContainerTokensPath = dirsHandler.getLocalPathForWrite((((ResourceLocalizationService.NM_PRIVATE_DIR) + (Path.SEPARATOR)) + (String.format(TOKEN_FILE_NAME_FMT, locId))));
        files.create(nmPrivateContainerTokensPath, EnumSet.of(CreateFlag.CREATE, CreateFlag.OVERWRITE));
        Configuration config = new YarnConfiguration(conf);
        InetSocketAddress nmAddr = config.getSocketAddr(NM_BIND_HOST, NM_LOCALIZER_ADDRESS, DEFAULT_NM_LOCALIZER_ADDRESS, DEFAULT_NM_LOCALIZER_PORT);
        String appId = "application_01_01";
        exec = new LinuxContainerExecutor() {
            @Override
            public void buildMainArgs(List<String> command, String user, String appId, String locId, InetSocketAddress nmAddr, String tokenFileName, List<String> localDirs) {
                MockContainerLocalizer.buildMainArgs(command, user, appId, locId, nmAddr, localDirs);
            }
        };
        exec.setConf(conf);
        exec.startLocalizer(new LocalizerStartContext.Builder().setNmPrivateContainerTokens(nmPrivateContainerTokensPath).setNmAddr(nmAddr).setUser(appSubmitter).setAppId(appId).setLocId(locId).setDirsHandler(dirsHandler).build());
        String locId2 = "container_01_02";
        Path nmPrivateContainerTokensPath2 = dirsHandler.getLocalPathForWrite((((ResourceLocalizationService.NM_PRIVATE_DIR) + (Path.SEPARATOR)) + (String.format(TOKEN_FILE_NAME_FMT, locId2))));
        files.create(nmPrivateContainerTokensPath2, EnumSet.of(CreateFlag.CREATE, CreateFlag.OVERWRITE));
        exec.startLocalizer(new LocalizerStartContext.Builder().setNmPrivateContainerTokens(nmPrivateContainerTokensPath2).setNmAddr(nmAddr).setUser(appSubmitter).setAppId(appId).setLocId(locId2).setDirsHandler(dirsHandler).build());
        cleanupUserAppCache(appSubmitter);
    }

    @Test
    public void testContainerLaunch() throws Exception {
        Assume.assumeTrue(shouldRun());
        String expectedRunAsUser = conf.get(NM_NONSECURE_MODE_LOCAL_USER_KEY, DEFAULT_NM_NONSECURE_MODE_LOCAL_USER);
        File touchFile = new File(TestLinuxContainerExecutor.workSpace, "touch-file");
        int ret = runAndBlock("touch", touchFile.getAbsolutePath());
        Assert.assertEquals(0, ret);
        FileStatus fileStatus = FileContext.getLocalFSFileContext().getFileStatus(new Path(touchFile.getAbsolutePath()));
        Assert.assertEquals(expectedRunAsUser, fileStatus.getOwner());
        cleanupAppFiles(expectedRunAsUser);
    }

    @Test
    public void testNonSecureRunAsSubmitter() throws Exception {
        Assume.assumeTrue(shouldRun());
        Assume.assumeFalse(UserGroupInformation.isSecurityEnabled());
        String expectedRunAsUser = appSubmitter;
        conf.set(NM_NONSECURE_MODE_LIMIT_USERS, "false");
        exec.setConf(conf);
        File touchFile = new File(TestLinuxContainerExecutor.workSpace, "touch-file");
        int ret = runAndBlock("touch", touchFile.getAbsolutePath());
        Assert.assertEquals(0, ret);
        FileStatus fileStatus = FileContext.getLocalFSFileContext().getFileStatus(new Path(touchFile.getAbsolutePath()));
        Assert.assertEquals(expectedRunAsUser, fileStatus.getOwner());
        cleanupAppFiles(expectedRunAsUser);
        // reset conf
        conf.unset(NM_NONSECURE_MODE_LIMIT_USERS);
        exec.setConf(conf);
    }

    @Test
    public void testContainerKill() throws Exception {
        Assume.assumeTrue(shouldRun());
        final ContainerId sleepId = getNextContainerId();
        Thread t = new Thread() {
            public void run() {
                try {
                    runAndBlock(sleepId, "sleep", "100");
                } catch (IOException | ConfigurationException e) {
                    TestLinuxContainerExecutor.LOG.warn("Caught exception while running sleep", e);
                }
            }
        };
        t.setDaemon(true);// If it does not exit we shouldn't block the test.

        t.start();
        Assert.assertTrue(t.isAlive());
        String pid = null;
        int count = 10;
        while (((pid = exec.getProcessId(sleepId)) == null) && (count > 0)) {
            TestLinuxContainerExecutor.LOG.info("Sleeping for 200 ms before checking for pid ");
            Thread.sleep(200);
            count--;
        } 
        Assert.assertNotNull(pid);
        TestLinuxContainerExecutor.LOG.info("Going to killing the process.");
        exec.signalContainer(new ContainerSignalContext.Builder().setUser(appSubmitter).setPid(pid).setSignal(TERM).build());
        TestLinuxContainerExecutor.LOG.info("sleeping for 100ms to let the sleep be killed");
        Thread.sleep(100);
        Assert.assertFalse(t.isAlive());
        cleanupAppFiles(appSubmitter);
    }

    @Test
    public void testCGroups() throws Exception {
        Assume.assumeTrue(shouldRun());
        String cgroupsMount = System.getProperty("cgroups.mount");
        Assume.assumeTrue(((cgroupsMount != null) && (!(cgroupsMount.isEmpty()))));
        Assert.assertTrue("Cgroups mount point does not exist", new File(cgroupsMount).exists());
        List<String> cgroupKVs = new ArrayList<>();
        String hierarchy = "hadoop-yarn";
        String[] controllers = new String[]{ "cpu", "net_cls" };
        for (String controller : controllers) {
            cgroupKVs.add(((((controller + "=") + cgroupsMount) + "/") + controller));
            Assert.assertTrue(new File(cgroupsMount, controller).exists());
        }
        try {
            exec.mountCgroups(cgroupKVs, hierarchy);
            for (String controller : controllers) {
                Assert.assertTrue((controller + " cgroup not mounted"), new File((((cgroupsMount + "/") + controller) + "/tasks")).exists());
                Assert.assertTrue((controller + " cgroup hierarchy not created"), new File(((((cgroupsMount + "/") + controller) + "/") + hierarchy)).exists());
                Assert.assertTrue((controller + " cgroup hierarchy created incorrectly"), new File((((((cgroupsMount + "/") + controller) + "/") + hierarchy) + "/tasks")).exists());
            }
        } catch (IOException ie) {
            Assert.fail(("Couldn't mount cgroups " + (ie.toString())));
            throw ie;
        }
    }

    @Test
    public void testLocalUser() throws Exception {
        Assume.assumeTrue(shouldRun());
        try {
            // nonsecure default
            Configuration conf = new YarnConfiguration();
            conf.set(HADOOP_SECURITY_AUTHENTICATION, "simple");
            UserGroupInformation.setConfiguration(conf);
            LinuxContainerExecutor lce = new LinuxContainerExecutor();
            lce.setConf(conf);
            Assert.assertEquals(DEFAULT_NM_NONSECURE_MODE_LOCAL_USER, lce.getRunAsUser("foo"));
            // nonsecure custom setting
            conf.set(NM_NONSECURE_MODE_LOCAL_USER_KEY, "bar");
            lce = new LinuxContainerExecutor();
            lce.setConf(conf);
            Assert.assertEquals("bar", lce.getRunAsUser("foo"));
            // nonsecure without limits
            conf.set(NM_NONSECURE_MODE_LOCAL_USER_KEY, "bar");
            conf.setBoolean(NM_NONSECURE_MODE_LIMIT_USERS, false);
            lce = new LinuxContainerExecutor();
            lce.setConf(conf);
            Assert.assertEquals("foo", lce.getRunAsUser("foo"));
            // secure
            conf = new YarnConfiguration();
            conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
            UserGroupInformation.setConfiguration(conf);
            lce = new LinuxContainerExecutor();
            lce.setConf(conf);
            Assert.assertEquals("foo", lce.getRunAsUser("foo"));
        } finally {
            Configuration conf = new YarnConfiguration();
            conf.set(HADOOP_SECURITY_AUTHENTICATION, "simple");
            UserGroupInformation.setConfiguration(conf);
        }
    }

    @Test
    public void testNonsecureUsernamePattern() throws Exception {
        Assume.assumeTrue(shouldRun());
        try {
            // nonsecure default
            Configuration conf = new YarnConfiguration();
            conf.set(HADOOP_SECURITY_AUTHENTICATION, "simple");
            UserGroupInformation.setConfiguration(conf);
            LinuxContainerExecutor lce = new LinuxContainerExecutor();
            lce.setConf(conf);
            lce.verifyUsernamePattern("foo");
            try {
                lce.verifyUsernamePattern("foo/x");
                Assert.fail();
            } catch (IllegalArgumentException ex) {
                // NOP
            } catch (Throwable ex) {
                Assert.fail(ex.toString());
            }
            // nonsecure custom setting
            conf.set(NM_NONSECURE_MODE_USER_PATTERN_KEY, "foo");
            lce = new LinuxContainerExecutor();
            lce.setConf(conf);
            lce.verifyUsernamePattern("foo");
            try {
                lce.verifyUsernamePattern("bar");
                Assert.fail();
            } catch (IllegalArgumentException ex) {
                // NOP
            } catch (Throwable ex) {
                Assert.fail(ex.toString());
            }
            // secure, pattern matching does not kick in.
            conf = new YarnConfiguration();
            conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
            UserGroupInformation.setConfiguration(conf);
            lce = new LinuxContainerExecutor();
            lce.setConf(conf);
            lce.verifyUsernamePattern("foo");
            lce.verifyUsernamePattern("foo/w");
        } finally {
            Configuration conf = new YarnConfiguration();
            conf.set(HADOOP_SECURITY_AUTHENTICATION, "simple");
            UserGroupInformation.setConfiguration(conf);
        }
    }

    @Test(timeout = 10000)
    public void testPostExecuteAfterReacquisition() throws Exception {
        Assume.assumeTrue(shouldRun());
        // make up some bogus container ID
        ApplicationId appId = ApplicationId.newInstance(12345, 67890);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 54321);
        ContainerId cid = ContainerId.newContainerId(attemptId, 9876);
        Configuration conf = new YarnConfiguration();
        conf.setClass(NM_LINUX_CONTAINER_RESOURCES_HANDLER, TestLinuxContainerExecutor.TestResourceHandler.class, LCEResourcesHandler.class);
        LinuxContainerExecutor lce = new LinuxContainerExecutor();
        lce.setConf(conf);
        try {
            lce.init(null);
        } catch (IOException e) {
            // expected if LCE isn't setup right, but not necessary for this test
        }
        Container container = Mockito.mock(Container.class);
        ContainerLaunchContext context = Mockito.mock(ContainerLaunchContext.class);
        HashMap<String, String> env = new HashMap<>();
        Mockito.when(container.getLaunchContext()).thenReturn(context);
        Mockito.when(context.getEnvironment()).thenReturn(env);
        lce.reacquireContainer(new ContainerReacquisitionContext.Builder().setContainer(container).setUser("foouser").setContainerId(cid).build());
        Assert.assertTrue("postExec not called after reacquisition", TestLinuxContainerExecutor.TestResourceHandler.postExecContainers.contains(cid));
    }

    @Test
    public void testRemoveDockerContainer() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(12345, 67890);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 54321);
        String cid = ContainerId.newContainerId(attemptId, 9876).toString();
        LinuxContainerExecutor lce = Mockito.mock(LinuxContainerExecutor.class);
        lce.removeDockerContainer(cid);
        Mockito.verify(lce, Mockito.times(1)).removeDockerContainer(cid);
    }

    @Test
    public void testReapContainer() throws Exception {
        Container container = Mockito.mock(Container.class);
        LinuxContainerRuntime containerRuntime = Mockito.mock(LinuxContainerRuntime.class);
        LinuxContainerExecutor lce = Mockito.spy(new LinuxContainerExecutor(containerRuntime));
        ContainerReapContext.Builder builder = new ContainerReapContext.Builder();
        builder.setContainer(container).setUser("foo");
        ContainerReapContext ctx = builder.build();
        lce.reapContainer(ctx);
        Mockito.verify(lce, Mockito.times(1)).reapContainer(ctx);
        Mockito.verify(lce, Mockito.times(1)).postComplete(ArgumentMatchers.any());
    }

    @Test
    public void testRelaunchContainer() throws Exception {
        Container container = Mockito.mock(Container.class);
        LinuxContainerExecutor lce = Mockito.mock(LinuxContainerExecutor.class);
        ContainerStartContext.Builder builder = new ContainerStartContext.Builder();
        builder.setContainer(container).setUser("foo");
        ContainerStartContext ctx = builder.build();
        lce.relaunchContainer(ctx);
        Mockito.verify(lce, Mockito.times(1)).relaunchContainer(ctx);
    }

    @Test
    public void testExecContainer() throws Exception {
        Container container = Mockito.mock(Container.class);
        LinuxContainerExecutor lce = Mockito.mock(LinuxContainerExecutor.class);
        ContainerExecContext.Builder builder = new ContainerExecContext.Builder();
        builder.setUser("foo").setAppId("app1").setContainer(container);
        ContainerExecContext ctx = builder.build();
        lce.execContainer(ctx);
        Mockito.verify(lce, Mockito.times(1)).execContainer(ctx);
    }

    @Test
    public void testUpdateYarnSysFS() throws Exception {
        String user = System.getProperty("user.name");
        String appId = "app-1";
        String spec = "";
        Context ctx = Mockito.mock(Context.class);
        LinuxContainerExecutor lce = Mockito.mock(LinuxContainerExecutor.class);
        lce.updateYarnSysFS(ctx, user, appId, spec);
        Mockito.verify(lce, Mockito.times(1)).updateYarnSysFS(ctx, user, appId, spec);
    }

    private static class TestResourceHandler implements LCEResourcesHandler {
        static Set<ContainerId> postExecContainers = new HashSet<ContainerId>();

        @Override
        public void setConf(Configuration conf) {
        }

        @Override
        public Configuration getConf() {
            return null;
        }

        @Override
        public void init(LinuxContainerExecutor lce) throws IOException {
        }

        @Override
        public void preExecute(ContainerId containerId, Resource containerResource) throws IOException {
        }

        @Override
        public void postExecute(ContainerId containerId) {
            TestLinuxContainerExecutor.TestResourceHandler.postExecContainers.add(containerId);
        }

        @Override
        public String getResourcesOption(ContainerId containerId) {
            return null;
        }
    }
}

