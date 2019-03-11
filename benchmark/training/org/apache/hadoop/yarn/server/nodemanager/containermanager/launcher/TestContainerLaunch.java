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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher;


import ContainerEventType.CONTAINER_EXITED_WITH_FAILURE;
import ContainerLaunch.CONTAINER_PRE_LAUNCH_STDERR;
import ContainerLaunch.CONTAINER_PRE_LAUNCH_STDOUT;
import ContainerState.COMPLETE;
import Environment.CLASSPATH;
import Environment.CLASSPATH_PREPEND_DISTCACHE;
import Environment.CONTAINER_ID;
import Environment.HADOOP_CONF_DIR;
import Environment.HOME;
import Environment.LOCAL_DIRS;
import Environment.LOGNAME;
import Environment.NM_HOST;
import Environment.NM_HTTP_PORT;
import Environment.NM_PORT;
import Environment.PWD;
import Environment.USER;
import ExitCode.FORCE_KILLED;
import LocalResourceType.FILE;
import LocalResourceVisibility.APPLICATION;
import Shell.OSType.OS_TYPE_LINUX;
import Shell.OSType.OS_TYPE_WIN;
import Shell.ShellCommandExecutor;
import Shell.WINDOWS_MAX_SHELL_LENGTH;
import Shell.isSetsidAvailable;
import YarnConfiguration.DEFAULT_NM_USER_HOME_DIR;
import YarnConfiguration.NM_CONTAINER_STDERR_PATTERN;
import YarnConfiguration.NM_ENV_WHITELIST;
import YarnConfiguration.NM_LOG_CONTAINER_DEBUG_INFO;
import YarnConfiguration.NM_USER_HOME_DIR;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.util.Shell.ExitCodeException;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerExitStatus;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.exceptions.ConfigurationException;
import org.apache.hadoop.yarn.server.nodemanager.ContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.DefaultContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager.NMContext;
import org.apache.hadoop.yarn.server.nodemanager.NodeStatusUpdater;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.BaseContainerManagerTest;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.Application;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerExitEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher.ContainerLaunch.ShellScriptBuilder;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.DockerLinuxContainerRuntime;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ContainerLocalizer;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerStartContext;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMNullStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.security.NMTokenSecretManagerInNM;
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.util.AuxiliaryServiceHelper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ContainerLaunch.CONTAINER_PRE_LAUNCH_STDERR;
import static ContainerLaunch.CONTAINER_PRE_LAUNCH_STDOUT;
import static org.apache.commons.lang3.StringUtils.repeat;


public class TestContainerLaunch extends BaseContainerManagerTest {
    private static final String INVALID_JAVA_HOME = "/no/jvm/here";

    private NMContext distContext = new NMContext(new org.apache.hadoop.yarn.server.nodemanager.security.NMContainerTokenSecretManager(conf), new NMTokenSecretManagerInNM(), null, new org.apache.hadoop.yarn.server.security.ApplicationACLsManager(conf), new NMNullStateStoreService(), false, conf) {
        public int getHttpPort() {
            return BaseContainerManagerTest.HTTP_PORT;
        }

        public NodeId getNodeId() {
            return NodeId.newInstance("ahost", 1234);
        }
    };

    public TestContainerLaunch() throws UnsupportedFileSystemException {
        super();
    }

    @Test
    public void testSpecialCharSymlinks() throws IOException {
        File shellFile = null;
        File tempFile = null;
        String badSymlink = (Shell.WINDOWS) ? "foo@zz_#!-+bar.cmd" : "-foo@zz%_#*&!-+= bar()";
        File symLinkFile = null;
        try {
            shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "hello");
            tempFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "temp");
            String timeoutCommand = (Shell.WINDOWS) ? "@echo \"hello\"" : "echo \"hello\"";
            PrintWriter writer = new PrintWriter(new FileOutputStream(shellFile));
            FileUtil.setExecutable(shellFile, true);
            writer.println(timeoutCommand);
            writer.close();
            Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
            Path path = new Path(shellFile.getAbsolutePath());
            resources.put(path, Arrays.asList(badSymlink));
            FileOutputStream fos = new FileOutputStream(tempFile);
            Map<String, String> env = new HashMap<String, String>();
            List<String> commands = new ArrayList<String>();
            if (Shell.WINDOWS) {
                commands.add("cmd");
                commands.add("/c");
                commands.add((("\"" + badSymlink) + "\""));
            } else {
                commands.add((("/bin/sh ./\\\"" + badSymlink) + "\\\""));
            }
            DefaultContainerExecutor defaultContainerExecutor = new DefaultContainerExecutor();
            defaultContainerExecutor.setConf(new YarnConfiguration());
            LinkedHashSet<String> nmVars = new LinkedHashSet<>();
            defaultContainerExecutor.writeLaunchEnv(fos, env, resources, commands, new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath()), "user", tempFile.getName(), nmVars);
            fos.flush();
            fos.close();
            FileUtil.setExecutable(tempFile, true);
            Shell.ShellCommandExecutor shexc = new Shell.ShellCommandExecutor(new String[]{ tempFile.getAbsolutePath() }, BaseContainerManagerTest.tmpDir);
            shexc.execute();
            Assert.assertEquals(shexc.getExitCode(), 0);
            // Capture output from prelaunch.out
            List<String> output = Files.readAllLines(Paths.get(BaseContainerManagerTest.localLogDir.getAbsolutePath(), CONTAINER_PRE_LAUNCH_STDOUT), Charset.forName("UTF-8"));
            assert output.contains("hello");
            symLinkFile = new File(BaseContainerManagerTest.tmpDir, badSymlink);
        } finally {
            // cleanup
            if ((shellFile != null) && (shellFile.exists())) {
                shellFile.delete();
            }
            if ((tempFile != null) && (tempFile.exists())) {
                tempFile.delete();
            }
            if ((symLinkFile != null) && (symLinkFile.exists())) {
                symLinkFile.delete();
            }
        }
    }

    // test the diagnostics are generated
    @Test(timeout = 20000)
    public void testInvalidSymlinkDiagnostics() throws IOException {
        File shellFile = null;
        File tempFile = null;
        String symLink = (Shell.WINDOWS) ? "test.cmd" : "test";
        File symLinkFile = null;
        try {
            shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "hello");
            tempFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "temp");
            String timeoutCommand = (Shell.WINDOWS) ? "@echo \"hello\"" : "echo \"hello\"";
            PrintWriter writer = new PrintWriter(new FileOutputStream(shellFile));
            FileUtil.setExecutable(shellFile, true);
            writer.println(timeoutCommand);
            writer.close();
            Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
            // This is an invalid path and should throw exception because of No such file.
            Path invalidPath = new Path(((shellFile.getAbsolutePath()) + "randomPath"));
            resources.put(invalidPath, Arrays.asList(symLink));
            FileOutputStream fos = new FileOutputStream(tempFile);
            Map<String, String> env = new HashMap<String, String>();
            List<String> commands = new ArrayList<String>();
            if (Shell.WINDOWS) {
                commands.add("cmd");
                commands.add("/c");
                commands.add((("\"" + symLink) + "\""));
            } else {
                commands.add((("/bin/sh ./\\\"" + symLink) + "\\\""));
            }
            DefaultContainerExecutor defaultContainerExecutor = new DefaultContainerExecutor();
            defaultContainerExecutor.setConf(new YarnConfiguration());
            LinkedHashSet<String> nmVars = new LinkedHashSet<>();
            defaultContainerExecutor.writeLaunchEnv(fos, env, resources, commands, new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath()), "user", nmVars);
            fos.flush();
            fos.close();
            FileUtil.setExecutable(tempFile, true);
            Shell.ShellCommandExecutor shexc = new Shell.ShellCommandExecutor(new String[]{ tempFile.getAbsolutePath() }, BaseContainerManagerTest.tmpDir);
            String diagnostics = null;
            try {
                shexc.execute();
                Assert.fail("Should catch exception");
            } catch (ExitCodeException e) {
                diagnostics = e.getMessage();
            }
            Assert.assertNotNull(diagnostics);
            Assert.assertTrue(((shexc.getExitCode()) != 0));
            symLinkFile = new File(BaseContainerManagerTest.tmpDir, symLink);
        } finally {
            // cleanup
            if ((shellFile != null) && (shellFile.exists())) {
                shellFile.delete();
            }
            if ((tempFile != null) && (tempFile.exists())) {
                tempFile.delete();
            }
            if ((symLinkFile != null) && (symLinkFile.exists())) {
                symLinkFile.delete();
            }
        }
    }

    @Test(timeout = 20000)
    public void testWriteEnvExport() throws Exception {
        // Valid only for unix
        assumeNotWindows();
        File shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "hello");
        Map<String, String> env = new HashMap<String, String>();
        env.put("HADOOP_COMMON_HOME", "/opt/hadoopcommon");
        env.put("HADOOP_MAPRED_HOME", "/opt/hadoopbuild");
        Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
        FileOutputStream fos = new FileOutputStream(shellFile);
        List<String> commands = new ArrayList<String>();
        final Map<String, String> nmEnv = new HashMap<>();
        nmEnv.put("HADOOP_COMMON_HOME", "nodemanager_common_home");
        nmEnv.put("HADOOP_HDFS_HOME", "nodemanager_hdfs_home");
        nmEnv.put("HADOOP_YARN_HOME", "nodemanager_yarn_home");
        nmEnv.put("HADOOP_MAPRED_HOME", "nodemanager_mapred_home");
        DefaultContainerExecutor defaultContainerExecutor = new DefaultContainerExecutor() {
            @Override
            protected String getNMEnvVar(String varname) {
                return nmEnv.get(varname);
            }
        };
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(NM_ENV_WHITELIST, "HADOOP_MAPRED_HOME,HADOOP_YARN_HOME");
        defaultContainerExecutor.setConf(conf);
        LinkedHashSet<String> nmVars = new LinkedHashSet<>();
        defaultContainerExecutor.writeLaunchEnv(fos, env, resources, commands, new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath()), "user", nmVars);
        String shellContent = new String(Files.readAllBytes(Paths.get(shellFile.getAbsolutePath())), StandardCharsets.UTF_8);
        Assert.assertTrue(shellContent.contains("export HADOOP_COMMON_HOME=\"/opt/hadoopcommon\""));
        // Whitelisted variable overridden by container
        Assert.assertTrue(shellContent.contains("export HADOOP_MAPRED_HOME=\"/opt/hadoopbuild\""));
        // Available in env but not in whitelist
        Assert.assertFalse(shellContent.contains("HADOOP_HDFS_HOME"));
        // Available in env and in whitelist
        Assert.assertTrue(shellContent.contains("export HADOOP_YARN_HOME=${HADOOP_YARN_HOME:-\"nodemanager_yarn_home\"}"));
        fos.flush();
        fos.close();
    }

    @Test(timeout = 20000)
    public void testWriteEnvExportDocker() throws Exception {
        // Valid only for unix
        assumeNotWindows();
        File shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "hello");
        Map<String, String> env = new HashMap<String, String>();
        env.put("HADOOP_COMMON_HOME", "/opt/hadoopcommon");
        env.put("HADOOP_MAPRED_HOME", "/opt/hadoopbuild");
        Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
        FileOutputStream fos = new FileOutputStream(shellFile);
        List<String> commands = new ArrayList<String>();
        final Map<String, String> nmEnv = new HashMap<>();
        nmEnv.put("HADOOP_COMMON_HOME", "nodemanager_common_home");
        nmEnv.put("HADOOP_HDFS_HOME", "nodemanager_hdfs_home");
        nmEnv.put("HADOOP_YARN_HOME", "nodemanager_yarn_home");
        nmEnv.put("HADOOP_MAPRED_HOME", "nodemanager_mapred_home");
        DockerLinuxContainerRuntime dockerRuntime = new DockerLinuxContainerRuntime(Mockito.mock(PrivilegedOperationExecutor.class));
        LinuxContainerExecutor lce = new LinuxContainerExecutor(dockerRuntime) {
            @Override
            protected String getNMEnvVar(String varname) {
                return nmEnv.get(varname);
            }
        };
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(NM_ENV_WHITELIST, "HADOOP_MAPRED_HOME,HADOOP_YARN_HOME");
        lce.setConf(conf);
        LinkedHashSet<String> nmVars = new LinkedHashSet<>();
        lce.writeLaunchEnv(fos, env, resources, commands, new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath()), "user", nmVars);
        String shellContent = new String(Files.readAllBytes(Paths.get(shellFile.getAbsolutePath())), StandardCharsets.UTF_8);
        Assert.assertTrue(shellContent.contains("export HADOOP_COMMON_HOME=\"/opt/hadoopcommon\""));
        // Whitelisted variable overridden by container
        Assert.assertTrue(shellContent.contains("export HADOOP_MAPRED_HOME=\"/opt/hadoopbuild\""));
        // Available in env but not in whitelist
        Assert.assertFalse(shellContent.contains("HADOOP_HDFS_HOME"));
        // Available in env and in whitelist
        Assert.assertTrue(shellContent.contains("export HADOOP_YARN_HOME=${HADOOP_YARN_HOME:-\"nodemanager_yarn_home\"}"));
        fos.flush();
        fos.close();
    }

    @Test(timeout = 20000)
    public void testWriteEnvOrder() throws Exception {
        // Valid only for unix
        assumeNotWindows();
        List<String> commands = new ArrayList<String>();
        // Setup user-defined environment
        Map<String, String> env = new HashMap<String, String>();
        env.put("USER_VAR_1", "1");
        env.put("USER_VAR_2", "2");
        env.put("NM_MODIFIED_VAR_1", "nm 1");
        env.put("NM_MODIFIED_VAR_2", "nm 2");
        // These represent vars explicitly set by NM
        LinkedHashSet<String> trackedNmVars = new LinkedHashSet<>();
        trackedNmVars.add("NM_MODIFIED_VAR_1");
        trackedNmVars.add("NM_MODIFIED_VAR_2");
        // Setup Nodemanager environment
        final Map<String, String> nmEnv = new HashMap<>();
        nmEnv.put("WHITELIST_VAR_1", "wl 1");
        nmEnv.put("WHITELIST_VAR_2", "wl 2");
        nmEnv.put("NON_WHITELIST_VAR_1", "nwl 1");
        nmEnv.put("NON_WHITELIST_VAR_2", "nwl 2");
        DefaultContainerExecutor defaultContainerExecutor = new DefaultContainerExecutor() {
            @Override
            protected String getNMEnvVar(String varname) {
                return nmEnv.get(varname);
            }
        };
        // Setup conf with whitelisted variables
        ArrayList<String> whitelistVars = new ArrayList<>();
        whitelistVars.add("WHITELIST_VAR_1");
        whitelistVars.add("WHITELIST_VAR_2");
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(NM_ENV_WHITELIST, (((whitelistVars.get(0)) + ",") + (whitelistVars.get(1))));
        // These are in the NM env, but not in the whitelist.
        ArrayList<String> nonWhiteListEnv = new ArrayList<>();
        nonWhiteListEnv.add("NON_WHITELIST_VAR_1");
        nonWhiteListEnv.add("NON_WHITELIST_VAR_2");
        // Write the launch script
        File shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "hello");
        Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
        FileOutputStream fos = new FileOutputStream(shellFile);
        defaultContainerExecutor.setConf(conf);
        defaultContainerExecutor.writeLaunchEnv(fos, env, resources, commands, new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath()), "user", trackedNmVars);
        fos.flush();
        fos.close();
        // Examine the script
        String shellContent = new String(Files.readAllBytes(Paths.get(shellFile.getAbsolutePath())), StandardCharsets.UTF_8);
        // First make sure everything is there that's supposed to be
        for (String envVar : env.keySet()) {
            Assert.assertTrue(shellContent.contains((envVar + "=")));
        }
        for (String wlVar : whitelistVars) {
            Assert.assertTrue(shellContent.contains((wlVar + "=")));
        }
        for (String nwlVar : nonWhiteListEnv) {
            Assert.assertFalse(shellContent.contains((nwlVar + "=")));
        }
        // Explicitly Set NM vars should be before user vars
        for (String nmVar : trackedNmVars) {
            for (String userVar : env.keySet()) {
                // Need to skip nm vars and whitelist vars
                if ((!(trackedNmVars.contains(userVar))) && (!(whitelistVars.contains(userVar)))) {
                    Assert.assertTrue(((shellContent.indexOf((nmVar + "="))) < (shellContent.indexOf((userVar + "=")))));
                }
            }
        }
        // Whitelisted vars should be before explicitly set NM vars
        for (String wlVar : whitelistVars) {
            for (String nmVar : trackedNmVars) {
                Assert.assertTrue(((shellContent.indexOf((wlVar + "="))) < (shellContent.indexOf((nmVar + "=")))));
            }
        }
    }

    @Test(timeout = 20000)
    public void testInvalidEnvSyntaxDiagnostics() throws IOException {
        File shellFile = null;
        try {
            shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "hello");
            Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
            FileOutputStream fos = new FileOutputStream(shellFile);
            FileUtil.setExecutable(shellFile, true);
            Map<String, String> env = new HashMap<String, String>();
            // invalid env
            env.put("APPLICATION_WORKFLOW_CONTEXT", ("{\"workflowId\":\"609f91c5cd83\"," + ("\"workflowName\":\"\n\ninsert table " + "\npartition (cd_education_status)\nselect cd_demo_sk, cd_gender, ")));
            List<String> commands = new ArrayList<String>();
            DefaultContainerExecutor defaultContainerExecutor = new DefaultContainerExecutor();
            defaultContainerExecutor.setConf(new YarnConfiguration());
            LinkedHashSet<String> nmVars = new LinkedHashSet<>();
            defaultContainerExecutor.writeLaunchEnv(fos, env, resources, commands, new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath()), "user", nmVars);
            fos.flush();
            fos.close();
            // It is supposed that LANG is set as C.
            Map<String, String> cmdEnv = new HashMap<String, String>();
            cmdEnv.put("LANG", "C");
            Shell.ShellCommandExecutor shexc = new Shell.ShellCommandExecutor(new String[]{ shellFile.getAbsolutePath() }, BaseContainerManagerTest.tmpDir, cmdEnv);
            String diagnostics = null;
            try {
                shexc.execute();
                Assert.fail("Should catch exception");
            } catch (ExitCodeException e) {
                // Capture diagnostics from prelaunch.stderr
                List<String> error = Files.readAllLines(Paths.get(BaseContainerManagerTest.localLogDir.getAbsolutePath(), CONTAINER_PRE_LAUNCH_STDERR), Charset.forName("UTF-8"));
                diagnostics = StringUtils.join("\n", error);
            }
            Assert.assertTrue(diagnostics.contains((Shell.WINDOWS ? "is not recognized as an internal or external command" : "command not found")));
            Assert.assertTrue(((shexc.getExitCode()) != 0));
        } finally {
            // cleanup
            if ((shellFile != null) && (shellFile.exists())) {
                shellFile.delete();
            }
        }
    }

    @Test(timeout = 10000)
    public void testEnvExpansion() throws IOException {
        Path logPath = new Path("/nm/container/logs");
        String input = (((((((Apps.crossPlatformify("HADOOP_HOME")) + "/share/hadoop/common/*") + (ApplicationConstants.CLASS_PATH_SEPARATOR)) + (Apps.crossPlatformify("HADOOP_HOME"))) + "/share/hadoop/common/lib/*") + (ApplicationConstants.CLASS_PATH_SEPARATOR)) + (Apps.crossPlatformify("HADOOP_LOG_HOME"))) + (ApplicationConstants.LOG_DIR_EXPANSION_VAR);
        String res = ContainerLaunch.expandEnvironment(input, logPath);
        if (Shell.WINDOWS) {
            Assert.assertEquals(("%HADOOP_HOME%/share/hadoop/common/*;" + ("%HADOOP_HOME%/share/hadoop/common/lib/*;" + "%HADOOP_LOG_HOME%/nm/container/logs")), res);
        } else {
            Assert.assertEquals(("$HADOOP_HOME/share/hadoop/common/*:" + ("$HADOOP_HOME/share/hadoop/common/lib/*:" + "$HADOOP_LOG_HOME/nm/container/logs")), res);
        }
        System.out.println(res);
    }

    @Test(timeout = 20000)
    public void testContainerLaunchStdoutAndStderrDiagnostics() throws IOException {
        File shellFile = null;
        try {
            shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "hello");
            // echo "hello" to stdout and "error" to stderr and exit code with 2;
            String command = (Shell.WINDOWS) ? "@echo \"hello\" & @echo \"error\" 1>&2 & exit /b 2" : "echo \"hello\"; echo \"error\" 1>&2; exit 2;";
            PrintWriter writer = new PrintWriter(new FileOutputStream(shellFile));
            FileUtil.setExecutable(shellFile, true);
            writer.println(command);
            writer.close();
            Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
            FileOutputStream fos = new FileOutputStream(shellFile, true);
            Map<String, String> env = new HashMap<String, String>();
            List<String> commands = new ArrayList<String>();
            commands.add(command);
            ContainerExecutor exec = new DefaultContainerExecutor();
            exec.setConf(new YarnConfiguration());
            LinkedHashSet<String> nmVars = new LinkedHashSet<>();
            exec.writeLaunchEnv(fos, env, resources, commands, new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath()), "user", nmVars);
            fos.flush();
            fos.close();
            Shell.ShellCommandExecutor shexc = new Shell.ShellCommandExecutor(new String[]{ shellFile.getAbsolutePath() }, BaseContainerManagerTest.tmpDir);
            String diagnostics = null;
            try {
                shexc.execute();
                Assert.fail("Should catch exception");
            } catch (ExitCodeException e) {
                diagnostics = e.getMessage();
            }
            // test stderr
            Assert.assertTrue(diagnostics.contains("error"));
            // test stdout
            Assert.assertTrue(shexc.getOutput().contains("hello"));
            Assert.assertTrue(((shexc.getExitCode()) == 2));
        } finally {
            // cleanup
            if ((shellFile != null) && (shellFile.exists())) {
                shellFile.delete();
            }
        }
    }

    @Test
    public void testPrependDistcache() throws Exception {
        // Test is only relevant on Windows
        assumeWindows();
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        ApplicationId appId = ApplicationId.newInstance(0, 0);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cId = ContainerId.newContainerId(appAttemptId, 0);
        Map<String, String> userSetEnv = new HashMap<String, String>();
        userSetEnv.put(CONTAINER_ID.name(), "user_set_container_id");
        userSetEnv.put(NM_HOST.name(), "user_set_NM_HOST");
        userSetEnv.put(NM_PORT.name(), "user_set_NM_PORT");
        userSetEnv.put(NM_HTTP_PORT.name(), "user_set_NM_HTTP_PORT");
        userSetEnv.put(LOCAL_DIRS.name(), "user_set_LOCAL_DIR");
        userSetEnv.put(USER.key(), ("user_set_" + (USER.key())));
        userSetEnv.put(LOGNAME.name(), "user_set_LOGNAME");
        userSetEnv.put(PWD.name(), "user_set_PWD");
        userSetEnv.put(HOME.name(), "user_set_HOME");
        userSetEnv.put(CLASSPATH.name(), "APATH");
        containerLaunchContext.setEnvironment(userSetEnv);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getContainerId()).thenReturn(cId);
        Mockito.when(container.getLaunchContext()).thenReturn(containerLaunchContext);
        Mockito.when(container.getLocalizedResources()).thenReturn(null);
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        EventHandler<Event> eventHandler = new EventHandler<Event>() {
            public void handle(Event event) {
                Assert.assertTrue((event instanceof ContainerExitEvent));
                ContainerExitEvent exitEvent = ((ContainerExitEvent) (event));
                Assert.assertEquals(CONTAINER_EXITED_WITH_FAILURE, exitEvent.getType());
            }
        };
        Mockito.when(dispatcher.getEventHandler()).thenReturn(eventHandler);
        Configuration conf = new Configuration();
        ContainerLaunch launch = new ContainerLaunch(distContext, conf, dispatcher, exec, null, container, dirsHandler, containerManager);
        String testDir = System.getProperty("test.build.data", "target/test-dir");
        Path pwd = new Path(testDir);
        List<Path> appDirs = new ArrayList<Path>();
        List<String> userLocalDirs = new ArrayList<>();
        List<String> containerLogs = new ArrayList<String>();
        Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
        Path userjar = new Path("user.jar");
        List<String> lpaths = new ArrayList<String>();
        lpaths.add("userjarlink.jar");
        resources.put(userjar, lpaths);
        Path nmp = new Path(testDir);
        Set<String> nmEnvTrack = new LinkedHashSet<>();
        launch.sanitizeEnv(userSetEnv, pwd, appDirs, userLocalDirs, containerLogs, resources, nmp, nmEnvTrack);
        List<String> result = TestContainerLaunch.getJarManifestClasspath(userSetEnv.get(CLASSPATH.name()));
        Assert.assertTrue(((result.size()) > 1));
        Assert.assertTrue(result.get(((result.size()) - 1)).endsWith("userjarlink.jar"));
        // Then, with user classpath first
        userSetEnv.put(CLASSPATH_PREPEND_DISTCACHE.name(), "true");
        cId = ContainerId.newContainerId(appAttemptId, 1);
        Mockito.when(container.getContainerId()).thenReturn(cId);
        launch = new ContainerLaunch(distContext, conf, dispatcher, exec, null, container, dirsHandler, containerManager);
        launch.sanitizeEnv(userSetEnv, pwd, appDirs, userLocalDirs, containerLogs, resources, nmp, nmEnvTrack);
        result = TestContainerLaunch.getJarManifestClasspath(userSetEnv.get(CLASSPATH.name()));
        Assert.assertTrue(((result.size()) > 1));
        Assert.assertTrue(result.get(0).endsWith("userjarlink.jar"));
    }

    @Test
    public void testSanitizeNMEnvVars() throws Exception {
        // Valid only for unix
        assumeNotWindows();
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        ApplicationId appId = ApplicationId.newInstance(0, 0);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cId = ContainerId.newContainerId(appAttemptId, 0);
        Map<String, String> userSetEnv = new HashMap<String, String>();
        Set<String> nmEnvTrack = new LinkedHashSet<>();
        userSetEnv.put(CONTAINER_ID.name(), "user_set_container_id");
        userSetEnv.put(NM_HOST.name(), "user_set_NM_HOST");
        userSetEnv.put(NM_PORT.name(), "user_set_NM_PORT");
        userSetEnv.put(NM_HTTP_PORT.name(), "user_set_NM_HTTP_PORT");
        userSetEnv.put(LOCAL_DIRS.name(), "user_set_LOCAL_DIR");
        userSetEnv.put(USER.key(), ("user_set_" + (USER.key())));
        userSetEnv.put(LOGNAME.name(), "user_set_LOGNAME");
        userSetEnv.put(PWD.name(), "user_set_PWD");
        userSetEnv.put(HOME.name(), "user_set_HOME");
        userSetEnv.put(CLASSPATH.name(), "APATH");
        // This one should be appended to.
        String userMallocArenaMaxVal = "test_user_max_val";
        userSetEnv.put("MALLOC_ARENA_MAX", userMallocArenaMaxVal);
        containerLaunchContext.setEnvironment(userSetEnv);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getContainerId()).thenReturn(cId);
        Mockito.when(container.getLaunchContext()).thenReturn(containerLaunchContext);
        Mockito.when(container.getLocalizedResources()).thenReturn(null);
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        EventHandler<Event> eventHandler = new EventHandler<Event>() {
            public void handle(Event event) {
                Assert.assertTrue((event instanceof ContainerExitEvent));
                ContainerExitEvent exitEvent = ((ContainerExitEvent) (event));
                Assert.assertEquals(CONTAINER_EXITED_WITH_FAILURE, exitEvent.getType());
            }
        };
        Mockito.when(dispatcher.getEventHandler()).thenReturn(eventHandler);
        // these should eclipse anything in the user environment
        YarnConfiguration conf = new YarnConfiguration();
        String mallocArenaMaxVal = "test_nm_max_val";
        conf.set("yarn.nodemanager.admin-env", ("MALLOC_ARENA_MAX=" + mallocArenaMaxVal));
        String testKey1 = "TEST_KEY1";
        String testVal1 = "testVal1";
        conf.set(("yarn.nodemanager.admin-env." + testKey1), testVal1);
        String testKey2 = "TEST_KEY2";
        String testVal2 = "testVal2";
        conf.set(("yarn.nodemanager.admin-env." + testKey2), testVal2);
        String testKey3 = "MOUNT_LIST";
        String testVal3 = "/home/a/b/c,/home/d/e/f,/home/g/e/h";
        conf.set(("yarn.nodemanager.admin-env." + testKey3), testVal3);
        ContainerLaunch launch = new ContainerLaunch(distContext, conf, dispatcher, exec, null, container, dirsHandler, containerManager);
        String testDir = System.getProperty("test.build.data", "target/test-dir");
        Path pwd = new Path(testDir);
        List<Path> appDirs = new ArrayList<Path>();
        List<String> userLocalDirs = new ArrayList<>();
        List<String> containerLogs = new ArrayList<String>();
        Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
        Path userjar = new Path("user.jar");
        List<String> lpaths = new ArrayList<String>();
        lpaths.add("userjarlink.jar");
        resources.put(userjar, lpaths);
        Path nmp = new Path(testDir);
        launch.sanitizeEnv(userSetEnv, pwd, appDirs, userLocalDirs, containerLogs, resources, nmp, nmEnvTrack);
        Assert.assertTrue(userSetEnv.containsKey("MALLOC_ARENA_MAX"));
        Assert.assertTrue(userSetEnv.containsKey(testKey1));
        Assert.assertTrue(userSetEnv.containsKey(testKey2));
        Assert.assertTrue(userSetEnv.containsKey(testKey3));
        Assert.assertTrue(nmEnvTrack.contains("MALLOC_ARENA_MAX"));
        Assert.assertTrue(nmEnvTrack.contains("MOUNT_LIST"));
        Assert.assertEquals(((userMallocArenaMaxVal + (File.pathSeparator)) + mallocArenaMaxVal), userSetEnv.get("MALLOC_ARENA_MAX"));
        Assert.assertEquals(testVal1, userSetEnv.get(testKey1));
        Assert.assertEquals(testVal2, userSetEnv.get(testKey2));
        Assert.assertEquals(testVal3, userSetEnv.get(testKey3));
    }

    @Test
    public void testErrorLogOnContainerExit() throws Exception {
        verifyTailErrorLogOnContainerExit(new Configuration(), "/stderr", false);
    }

    @Test
    public void testErrorLogOnContainerExitForCase() throws Exception {
        verifyTailErrorLogOnContainerExit(new Configuration(), "/STDERR.log", false);
    }

    @Test
    public void testErrorLogOnContainerExitForExt() throws Exception {
        verifyTailErrorLogOnContainerExit(new Configuration(), "/AppMaster.stderr", false);
    }

    @Test
    public void testErrorLogOnContainerExitWithCustomPattern() throws Exception {
        Configuration conf = new Configuration();
        conf.setStrings(NM_CONTAINER_STDERR_PATTERN, "{*stderr*,*log*}");
        verifyTailErrorLogOnContainerExit(conf, "/error.log", false);
    }

    @Test
    public void testErrorLogOnContainerExitWithMultipleFiles() throws Exception {
        Configuration conf = new Configuration();
        conf.setStrings(NM_CONTAINER_STDERR_PATTERN, "{*stderr*,*stdout*}");
        verifyTailErrorLogOnContainerExit(conf, "/stderr.log", true);
    }

    private static class ContainerExitHandler implements EventHandler<Event> {
        private boolean testForMultiFile;

        ContainerExitHandler(boolean testForMultiFile) {
            this.testForMultiFile = testForMultiFile;
        }

        boolean containerExitEventOccurred = false;

        public boolean isContainerExitEventOccurred() {
            return containerExitEventOccurred;
        }

        public void handle(Event event) {
            if (event instanceof ContainerExitEvent) {
                containerExitEventOccurred = true;
                ContainerExitEvent exitEvent = ((ContainerExitEvent) (event));
                Assert.assertEquals(CONTAINER_EXITED_WITH_FAILURE, exitEvent.getType());
                BaseContainerManagerTest.LOG.info(("Diagnostic Info : " + (exitEvent.getDiagnosticInfo())));
                if (testForMultiFile) {
                    Assert.assertTrue("Should contain the Multi file information", exitEvent.getDiagnosticInfo().contains("Error files: "));
                }
                Assert.assertTrue("Should contain the error Log message with tail size info", exitEvent.getDiagnosticInfo().contains((("Last " + (YarnConfiguration.DEFAULT_NM_CONTAINER_STDERR_BYTES)) + " bytes of")));
                Assert.assertTrue("Should contain contents of error Log", exitEvent.getDiagnosticInfo().contains(((TestContainerLaunch.INVALID_JAVA_HOME) + "/bin/java")));
            }
        }
    }

    /**
     * See if environment variable is forwarded using sanitizeEnv.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testContainerEnvVariables() throws Exception {
        containerManager.start();
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        // ////// Construct the Container-id
        ApplicationId appId = ApplicationId.newInstance(0, 0);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cId = ContainerId.newContainerId(appAttemptId, 0);
        Map<String, String> userSetEnv = new HashMap<String, String>();
        userSetEnv.put(CONTAINER_ID.name(), "user_set_container_id");
        userSetEnv.put(NM_HOST.name(), "user_set_NM_HOST");
        userSetEnv.put(NM_PORT.name(), "user_set_NM_PORT");
        userSetEnv.put(NM_HTTP_PORT.name(), "user_set_NM_HTTP_PORT");
        userSetEnv.put(LOCAL_DIRS.name(), "user_set_LOCAL_DIR");
        userSetEnv.put(USER.key(), ("user_set_" + (USER.key())));
        userSetEnv.put(LOGNAME.name(), "user_set_LOGNAME");
        userSetEnv.put(PWD.name(), "user_set_PWD");
        userSetEnv.put(HOME.name(), "user_set_HOME");
        final String userConfDir = "user_set_HADOOP_CONF_DIR";
        userSetEnv.put(HADOOP_CONF_DIR.name(), userConfDir);
        containerLaunchContext.setEnvironment(userSetEnv);
        File scriptFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "scriptFile");
        PrintWriter fileWriter = new PrintWriter(scriptFile);
        File processStartFile = new File(BaseContainerManagerTest.tmpDir, "env_vars.tmp").getAbsoluteFile();
        final File processFinalFile = new File(BaseContainerManagerTest.tmpDir, "env_vars.txt").getAbsoluteFile();
        if (Shell.WINDOWS) {
            fileWriter.println(((("@echo " + (CONTAINER_ID.$())) + "> ") + processStartFile));
            fileWriter.println(((("@echo " + (NM_HOST.$())) + ">> ") + processStartFile));
            fileWriter.println(((("@echo " + (NM_PORT.$())) + ">> ") + processStartFile));
            fileWriter.println(((("@echo " + (NM_HTTP_PORT.$())) + ">> ") + processStartFile));
            fileWriter.println(((("@echo " + (LOCAL_DIRS.$())) + ">> ") + processStartFile));
            fileWriter.println(((("@echo " + (USER.$())) + ">> ") + processStartFile));
            fileWriter.println(((("@echo " + (LOGNAME.$())) + ">> ") + processStartFile));
            fileWriter.println(((("@echo " + (PWD.$())) + ">> ") + processStartFile));
            fileWriter.println(((("@echo " + (HOME.$())) + ">> ") + processStartFile));
            fileWriter.println(((("@echo " + (HADOOP_CONF_DIR.$())) + ">> ") + processStartFile));
            for (String serviceName : containerManager.getAuxServiceMetaData().keySet()) {
                fileWriter.println((((("@echo %" + (AuxiliaryServiceHelper.NM_AUX_SERVICE)) + serviceName) + "%>> ") + processStartFile));
            }
            fileWriter.println(((("@echo " + cId) + ">> ") + processStartFile));
            fileWriter.println(((("@move /Y " + processStartFile) + " ") + processFinalFile));
            fileWriter.println("@ping -n 100 127.0.0.1 >nul");
        } else {
            fileWriter.write("\numask 0");// So that start file is readable by the test

            fileWriter.write(((("\necho $" + (CONTAINER_ID.name())) + " > ") + processStartFile));
            fileWriter.write(((("\necho $" + (NM_HOST.name())) + " >> ") + processStartFile));
            fileWriter.write(((("\necho $" + (NM_PORT.name())) + " >> ") + processStartFile));
            fileWriter.write(((("\necho $" + (NM_HTTP_PORT.name())) + " >> ") + processStartFile));
            fileWriter.write(((("\necho $" + (LOCAL_DIRS.name())) + " >> ") + processStartFile));
            fileWriter.write(((("\necho $" + (USER.name())) + " >> ") + processStartFile));
            fileWriter.write(((("\necho $" + (LOGNAME.name())) + " >> ") + processStartFile));
            fileWriter.write(((("\necho $" + (PWD.name())) + " >> ") + processStartFile));
            fileWriter.write(((("\necho $" + (HOME.name())) + " >> ") + processStartFile));
            fileWriter.write(((("\necho $" + (HADOOP_CONF_DIR.name())) + " >> ") + processStartFile));
            for (String serviceName : containerManager.getAuxServiceMetaData().keySet()) {
                fileWriter.write((((("\necho $" + (AuxiliaryServiceHelper.NM_AUX_SERVICE)) + serviceName) + " >> ") + processStartFile));
            }
            fileWriter.write(("\necho $$ >> " + processStartFile));
            fileWriter.write(((("\nmv " + processStartFile) + " ") + processFinalFile));
            fileWriter.write("\nexec sleep 100");
        }
        fileWriter.close();
        // upload the script file so that the container can run it
        URL resource_alpha = URL.fromPath(BaseContainerManagerTest.localFS.makeQualified(new Path(scriptFile.getAbsolutePath())));
        LocalResource rsrc_alpha = BaseContainerManagerTest.recordFactory.newRecordInstance(LocalResource.class);
        rsrc_alpha.setResource(resource_alpha);
        rsrc_alpha.setSize((-1));
        rsrc_alpha.setVisibility(APPLICATION);
        rsrc_alpha.setType(FILE);
        rsrc_alpha.setTimestamp(scriptFile.lastModified());
        String destinationFile = "dest_file";
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put(destinationFile, rsrc_alpha);
        containerLaunchContext.setLocalResources(localResources);
        // set up the rest of the container
        List<String> commands = Arrays.asList(Shell.getRunScriptCommand(scriptFile));
        containerLaunchContext.setCommands(commands);
        StartContainerRequest scRequest = StartContainerRequest.newInstance(containerLaunchContext, createContainerToken(cId, Priority.newInstance(0), 0));
        List<StartContainerRequest> list = new ArrayList<StartContainerRequest>();
        list.add(scRequest);
        StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        containerManager.startContainers(allRequests);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return processFinalFile.exists();
            }
        }, 10, 20000);
        // Now verify the contents of the file
        List<String> localDirs = dirsHandler.getLocalDirs();
        List<String> logDirs = dirsHandler.getLogDirs();
        List<Path> appDirs = new ArrayList<Path>(localDirs.size());
        for (String localDir : localDirs) {
            Path usersdir = new Path(localDir, ContainerLocalizer.USERCACHE);
            Path userdir = new Path(usersdir, user);
            Path appsdir = new Path(userdir, ContainerLocalizer.APPCACHE);
            appDirs.add(new Path(appsdir, appId.toString()));
        }
        List<String> containerLogDirs = new ArrayList<String>();
        String relativeContainerLogDir = ContainerLaunch.getRelativeContainerLogDir(appId.toString(), cId.toString());
        for (String logDir : logDirs) {
            containerLogDirs.add(((logDir + (Path.SEPARATOR)) + relativeContainerLogDir));
        }
        BufferedReader reader = new BufferedReader(new FileReader(processFinalFile));
        Assert.assertEquals(cId.toString(), reader.readLine());
        Assert.assertEquals(context.getNodeId().getHost(), reader.readLine());
        Assert.assertEquals(String.valueOf(context.getNodeId().getPort()), reader.readLine());
        Assert.assertEquals(String.valueOf(BaseContainerManagerTest.HTTP_PORT), reader.readLine());
        Assert.assertEquals(StringUtils.join(",", appDirs), reader.readLine());
        Assert.assertEquals(user, reader.readLine());
        Assert.assertEquals(user, reader.readLine());
        String obtainedPWD = reader.readLine();
        boolean found = false;
        for (Path localDir : appDirs) {
            if (new Path(localDir, cId.toString()).toString().equals(obtainedPWD)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(("Wrong local-dir found : " + obtainedPWD), found);
        Assert.assertEquals(conf.get(NM_USER_HOME_DIR, DEFAULT_NM_USER_HOME_DIR), reader.readLine());
        Assert.assertEquals(userConfDir, reader.readLine());
        for (String serviceName : containerManager.getAuxServiceMetaData().keySet()) {
            Assert.assertEquals(containerManager.getAuxServiceMetaData().get(serviceName), ByteBuffer.wrap(Base64.decodeBase64(reader.readLine().getBytes())));
        }
        Assert.assertEquals(cId.toString(), containerLaunchContext.getEnvironment().get(CONTAINER_ID.name()));
        Assert.assertEquals(context.getNodeId().getHost(), containerLaunchContext.getEnvironment().get(NM_HOST.name()));
        Assert.assertEquals(String.valueOf(context.getNodeId().getPort()), containerLaunchContext.getEnvironment().get(NM_PORT.name()));
        Assert.assertEquals(String.valueOf(BaseContainerManagerTest.HTTP_PORT), containerLaunchContext.getEnvironment().get(NM_HTTP_PORT.name()));
        Assert.assertEquals(StringUtils.join(",", appDirs), containerLaunchContext.getEnvironment().get(LOCAL_DIRS.name()));
        Assert.assertEquals(StringUtils.join(",", containerLogDirs), containerLaunchContext.getEnvironment().get(Environment.LOG_DIRS.name()));
        Assert.assertEquals(user, containerLaunchContext.getEnvironment().get(USER.name()));
        Assert.assertEquals(user, containerLaunchContext.getEnvironment().get(LOGNAME.name()));
        found = false;
        obtainedPWD = containerLaunchContext.getEnvironment().get(PWD.name());
        for (Path localDir : appDirs) {
            if (new Path(localDir, cId.toString()).toString().equals(obtainedPWD)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(("Wrong local-dir found : " + obtainedPWD), found);
        Assert.assertEquals(conf.get(NM_USER_HOME_DIR, DEFAULT_NM_USER_HOME_DIR), containerLaunchContext.getEnvironment().get(HOME.name()));
        Assert.assertEquals(userConfDir, containerLaunchContext.getEnvironment().get(HADOOP_CONF_DIR.name()));
        // Get the pid of the process
        String pid = reader.readLine().trim();
        // No more lines
        Assert.assertEquals(null, reader.readLine());
        // Now test the stop functionality.
        // Assert that the process is alive
        Assert.assertTrue("Process is not alive!", DefaultContainerExecutor.containerIsAlive(pid));
        // Once more
        Assert.assertTrue("Process is not alive!", DefaultContainerExecutor.containerIsAlive(pid));
        // Now test the stop functionality.
        List<ContainerId> containerIds = new ArrayList<ContainerId>();
        containerIds.add(cId);
        StopContainersRequest stopRequest = StopContainersRequest.newInstance(containerIds);
        containerManager.stopContainers(stopRequest);
        BaseContainerManagerTest.waitForContainerState(containerManager, cId, COMPLETE);
        GetContainerStatusesRequest gcsRequest = GetContainerStatusesRequest.newInstance(containerIds);
        ContainerStatus containerStatus = containerManager.getContainerStatuses(gcsRequest).getContainerStatuses().get(0);
        int expectedExitCode = ContainerExitStatus.KILLED_BY_APPMASTER;
        Assert.assertEquals(expectedExitCode, containerStatus.getExitStatus());
        // Assert that the process is not alive anymore
        Assert.assertFalse("Process is still alive!", DefaultContainerExecutor.containerIsAlive(pid));
    }

    @Test(timeout = 5000)
    public void testAuxiliaryServiceHelper() throws Exception {
        Map<String, String> env = new HashMap<String, String>();
        String serviceName = "testAuxiliaryService";
        ByteBuffer bb = ByteBuffer.wrap("testAuxiliaryService".getBytes());
        AuxiliaryServiceHelper.setServiceDataIntoEnv(serviceName, bb, env);
        Assert.assertEquals(bb, AuxiliaryServiceHelper.getServiceDataFromEnv(serviceName, env));
    }

    @Test(timeout = 30000)
    public void testDelayedKill() throws Exception {
        internalKillTest(true);
    }

    @Test(timeout = 30000)
    public void testImmediateKill() throws Exception {
        internalKillTest(false);
    }

    @SuppressWarnings("rawtypes")
    @Test(timeout = 10000)
    public void testCallFailureWithNullLocalizedResources() {
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getContainerId()).thenReturn(ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(System.currentTimeMillis(), 1), 1), 1));
        ContainerLaunchContext clc = Mockito.mock(ContainerLaunchContext.class);
        Mockito.when(clc.getCommands()).thenReturn(Collections.<String>emptyList());
        Mockito.when(container.getLaunchContext()).thenReturn(clc);
        Mockito.when(container.getLocalizedResources()).thenReturn(null);
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        EventHandler<Event> eventHandler = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Assert.assertTrue((event instanceof ContainerExitEvent));
                ContainerExitEvent exitEvent = ((ContainerExitEvent) (event));
                Assert.assertEquals(CONTAINER_EXITED_WITH_FAILURE, exitEvent.getType());
            }
        };
        Mockito.when(dispatcher.getEventHandler()).thenReturn(eventHandler);
        ContainerLaunch launch = new ContainerLaunch(context, new Configuration(), dispatcher, exec, null, container, dirsHandler, containerManager);
        launch.call();
    }

    /**
     * Test that script exists with non-zero exit code when command fails.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 10000)
    public void testShellScriptBuilderNonZeroExitCode() throws IOException {
        ShellScriptBuilder builder = ShellScriptBuilder.create();
        builder.command(Arrays.asList(new String[]{ "unknownCommand" }));
        File shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "testShellScriptBuilderError");
        PrintStream writer = new PrintStream(new FileOutputStream(shellFile));
        builder.write(writer);
        writer.close();
        try {
            FileUtil.setExecutable(shellFile, true);
            Shell.ShellCommandExecutor shexc = new Shell.ShellCommandExecutor(new String[]{ shellFile.getAbsolutePath() }, BaseContainerManagerTest.tmpDir);
            try {
                shexc.execute();
                Assert.fail("builder shell command was expected to throw");
            } catch (IOException e) {
                // expected
                System.out.println(("Received an expected exception: " + (e.getMessage())));
            }
        } finally {
            FileUtil.fullyDelete(shellFile);
        }
    }

    private static final String expectedMessage = "The command line has a length of";

    @Test(timeout = 10000)
    public void testWindowsShellScriptBuilderCommand() throws IOException {
        String callCmd = "@call ";
        // Test is only relevant on Windows
        assumeWindows();
        // The tests are built on assuming 8191 max command line length
        Assert.assertEquals(8191, WINDOWS_MAX_SHELL_LENGTH);
        ShellScriptBuilder builder = ShellScriptBuilder.create();
        // Basic tests: less length, exact length, max+1 length
        builder.command(Arrays.asList(repeat("A", 1024)));
        builder.command(Arrays.asList(repeat("E", ((Shell.WINDOWS_MAX_SHELL_LENGTH) - (callCmd.length())))));
        try {
            builder.command(Arrays.asList(repeat("X", (((Shell.WINDOWS_MAX_SHELL_LENGTH) - (callCmd.length())) + 1))));
            Assert.fail("longCommand was expected to throw");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(TestContainerLaunch.expectedMessage));
        }
        // Composite tests, from parts: less, exact and +
        builder.command(Arrays.asList(repeat("A", 1024), repeat("A", 1024), repeat("A", 1024)));
        // buildr.command joins the command parts with an extra space
        builder.command(Arrays.asList(repeat("E", 4095), repeat("E", 2047), repeat("E", (2047 - (callCmd.length())))));
        try {
            builder.command(Arrays.asList(repeat("X", 4095), repeat("X", 2047), repeat("X", (2048 - (callCmd.length())))));
            Assert.fail("long commands was expected to throw");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(TestContainerLaunch.expectedMessage));
        }
    }

    @Test(timeout = 10000)
    public void testWindowsShellScriptBuilderEnv() throws IOException {
        // Test is only relevant on Windows
        assumeWindows();
        // The tests are built on assuming 8191 max command line length
        Assert.assertEquals(8191, WINDOWS_MAX_SHELL_LENGTH);
        ShellScriptBuilder builder = ShellScriptBuilder.create();
        // test env
        builder.env("somekey", repeat("A", 1024));
        builder.env("somekey", repeat("A", ((Shell.WINDOWS_MAX_SHELL_LENGTH) - ("@set somekey=".length()))));
        try {
            builder.env("somekey", ((repeat("A", ((Shell.WINDOWS_MAX_SHELL_LENGTH) - ("@set somekey=".length())))) + 1));
            Assert.fail("long env was expected to throw");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(TestContainerLaunch.expectedMessage));
        }
    }

    @Test(timeout = 10000)
    public void testWindowsShellScriptBuilderMkdir() throws IOException {
        String mkDirCmd = "@if not exist \"\" mkdir \"\"";
        // Test is only relevant on Windows
        assumeWindows();
        // The tests are built on assuming 8191 max command line length
        Assert.assertEquals(8191, WINDOWS_MAX_SHELL_LENGTH);
        ShellScriptBuilder builder = ShellScriptBuilder.create();
        // test mkdir
        builder.mkdir(new Path(repeat("A", 1024)));
        builder.mkdir(new Path(repeat("E", (((Shell.WINDOWS_MAX_SHELL_LENGTH) - (mkDirCmd.length())) / 2))));
        try {
            builder.mkdir(new Path(repeat("X", ((((Shell.WINDOWS_MAX_SHELL_LENGTH) - (mkDirCmd.length())) / 2) + 1))));
            Assert.fail("long mkdir was expected to throw");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(TestContainerLaunch.expectedMessage));
        }
    }

    @Test(timeout = 10000)
    public void testWindowsShellScriptBuilderLink() throws IOException {
        // Test is only relevant on Windows
        assumeWindows();
        String linkCmd = ("@" + (Shell.getWinUtilsPath())) + " symlink \"\" \"\"";
        // The tests are built on assuming 8191 max command line length
        Assert.assertEquals(8191, WINDOWS_MAX_SHELL_LENGTH);
        ShellScriptBuilder builder = ShellScriptBuilder.create();
        // test link
        builder.link(new Path(repeat("A", 1024)), new Path(repeat("B", 1024)));
        builder.link(new Path(repeat("E", (((Shell.WINDOWS_MAX_SHELL_LENGTH) - (linkCmd.length())) / 2))), new Path(repeat("F", (((Shell.WINDOWS_MAX_SHELL_LENGTH) - (linkCmd.length())) / 2))));
        try {
            builder.link(new Path(repeat("X", ((((Shell.WINDOWS_MAX_SHELL_LENGTH) - (linkCmd.length())) / 2) + 1))), new Path(((repeat("Y", (((Shell.WINDOWS_MAX_SHELL_LENGTH) - (linkCmd.length())) / 2))) + 1)));
            Assert.fail("long link was expected to throw");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(TestContainerLaunch.expectedMessage));
        }
    }

    @Test
    public void testKillProcessGroup() throws Exception {
        Assume.assumeTrue(isSetsidAvailable);
        containerManager.start();
        // Construct the Container-id
        ApplicationId appId = ApplicationId.newInstance(2, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cId = ContainerId.newContainerId(appAttemptId, 0);
        File processStartFile = new File(BaseContainerManagerTest.tmpDir, "pid.txt").getAbsoluteFile();
        File childProcessStartFile = new File(BaseContainerManagerTest.tmpDir, "child_pid.txt").getAbsoluteFile();
        // setup a script that can handle sigterm gracefully
        File scriptFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "testscript");
        PrintWriter writer = new PrintWriter(new FileOutputStream(scriptFile));
        writer.println("#!/bin/bash\n\n");
        writer.println("echo \"Running testscript for forked process\"");
        writer.println("umask 0");
        writer.println(("echo $$ >> " + processStartFile));
        writer.println("while true;\ndo sleep 1s;\ndone > /dev/null 2>&1 &");
        writer.println(("echo $! >> " + childProcessStartFile));
        writer.println("while true;\ndo sleep 1s;\ndone");
        writer.close();
        FileUtil.setExecutable(scriptFile, true);
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        // upload the script file so that the container can run it
        URL resource_alpha = URL.fromPath(BaseContainerManagerTest.localFS.makeQualified(new Path(scriptFile.getAbsolutePath())));
        LocalResource rsrc_alpha = BaseContainerManagerTest.recordFactory.newRecordInstance(LocalResource.class);
        rsrc_alpha.setResource(resource_alpha);
        rsrc_alpha.setSize((-1));
        rsrc_alpha.setVisibility(APPLICATION);
        rsrc_alpha.setType(FILE);
        rsrc_alpha.setTimestamp(scriptFile.lastModified());
        String destinationFile = "dest_file.sh";
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put(destinationFile, rsrc_alpha);
        containerLaunchContext.setLocalResources(localResources);
        // set up the rest of the container
        List<String> commands = Arrays.asList(Shell.getRunScriptCommand(scriptFile));
        containerLaunchContext.setCommands(commands);
        Priority priority = Priority.newInstance(10);
        long createTime = 1234;
        Token containerToken = createContainerToken(cId, priority, createTime);
        StartContainerRequest scRequest = StartContainerRequest.newInstance(containerLaunchContext, containerToken);
        List<StartContainerRequest> list = new ArrayList<StartContainerRequest>();
        list.add(scRequest);
        StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        containerManager.startContainers(allRequests);
        int timeoutSecs = 0;
        while ((!(processStartFile.exists())) && ((timeoutSecs++) < 20)) {
            Thread.sleep(1000);
            BaseContainerManagerTest.LOG.info("Waiting for process start-file to be created");
        } 
        Assert.assertTrue("ProcessStartFile doesn't exist!", processStartFile.exists());
        BufferedReader reader = new BufferedReader(new FileReader(processStartFile));
        // Get the pid of the process
        String pid = reader.readLine().trim();
        // No more lines
        Assert.assertEquals(null, reader.readLine());
        reader.close();
        reader = new BufferedReader(new FileReader(childProcessStartFile));
        // Get the pid of the child process
        String child = reader.readLine().trim();
        // No more lines
        Assert.assertEquals(null, reader.readLine());
        reader.close();
        BaseContainerManagerTest.LOG.info(((("Manually killing pid " + pid) + ", but not child pid ") + child));
        Shell.execCommand(new String[]{ "kill", "-9", pid });
        BaseContainerManagerTest.waitForContainerState(containerManager, cId, COMPLETE);
        Assert.assertFalse("Process is still alive!", DefaultContainerExecutor.containerIsAlive(pid));
        List<ContainerId> containerIds = new ArrayList<ContainerId>();
        containerIds.add(cId);
        GetContainerStatusesRequest gcsRequest = GetContainerStatusesRequest.newInstance(containerIds);
        ContainerStatus containerStatus = containerManager.getContainerStatuses(gcsRequest).getContainerStatuses().get(0);
        Assert.assertEquals(FORCE_KILLED.getExitCode(), containerStatus.getExitStatus());
    }

    @Test
    public void testDebuggingInformation() throws IOException {
        File shellFile = null;
        File tempFile = null;
        Configuration conf = new YarnConfiguration();
        try {
            shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "hello");
            tempFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "temp");
            String testCommand = (Shell.WINDOWS) ? "@echo \"hello\"" : "echo \"hello\"";
            PrintWriter writer = new PrintWriter(new FileOutputStream(shellFile));
            FileUtil.setExecutable(shellFile, true);
            writer.println(testCommand);
            writer.close();
            Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
            Map<String, String> env = new HashMap<String, String>();
            List<String> commands = new ArrayList<String>();
            if (Shell.WINDOWS) {
                commands.add("cmd");
                commands.add("/c");
                commands.add((("\"" + (shellFile.getAbsolutePath())) + "\""));
            } else {
                commands.add((("/bin/sh \\\"" + (shellFile.getAbsolutePath())) + "\\\""));
            }
            boolean[] debugLogsExistArray = new boolean[]{ false, true };
            for (boolean debugLogsExist : debugLogsExistArray) {
                conf.setBoolean(NM_LOG_CONTAINER_DEBUG_INFO, debugLogsExist);
                FileOutputStream fos = new FileOutputStream(tempFile);
                ContainerExecutor exec = new DefaultContainerExecutor();
                exec.setConf(conf);
                LinkedHashSet<String> nmVars = new LinkedHashSet<>();
                exec.writeLaunchEnv(fos, env, resources, commands, new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath()), "user", tempFile.getName(), nmVars);
                fos.flush();
                fos.close();
                FileUtil.setExecutable(tempFile, true);
                Shell.ShellCommandExecutor shexc = new Shell.ShellCommandExecutor(new String[]{ tempFile.getAbsolutePath() }, BaseContainerManagerTest.tmpDir);
                shexc.execute();
                Assert.assertEquals(shexc.getExitCode(), 0);
                File directorInfo = new File(BaseContainerManagerTest.localLogDir, ContainerExecutor.DIRECTORY_CONTENTS);
                File scriptCopy = new File(BaseContainerManagerTest.localLogDir, tempFile.getName());
                Assert.assertEquals("Directory info file missing", debugLogsExist, directorInfo.exists());
                Assert.assertEquals("Copy of launch script missing", debugLogsExist, scriptCopy.exists());
                if (debugLogsExist) {
                    Assert.assertTrue("Directory info file size is 0", ((directorInfo.length()) > 0));
                    Assert.assertTrue("Size of copy of launch script is 0", ((scriptCopy.length()) > 0));
                }
            }
        } finally {
            // cleanup
            if ((shellFile != null) && (shellFile.exists())) {
                shellFile.delete();
            }
            if ((tempFile != null) && (tempFile.exists())) {
                tempFile.delete();
            }
        }
    }

    /**
     * Test container launch fault.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testContainerLaunchOnConfigurationError() throws Exception {
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        EventHandler handler = Mockito.mock(EventHandler.class);
        Mockito.when(dispatcher.getEventHandler()).thenReturn(handler);
        Application app = Mockito.mock(Application.class);
        ApplicationId appId = Mockito.mock(ApplicationId.class);
        Mockito.when(appId.toString()).thenReturn("1");
        Mockito.when(app.getAppId()).thenReturn(appId);
        Container container = Mockito.mock(Container.class);
        ContainerId id = Mockito.mock(ContainerId.class);
        Mockito.when(id.toString()).thenReturn("1");
        Mockito.when(container.getContainerId()).thenReturn(id);
        Mockito.when(container.getUser()).thenReturn("user");
        ContainerLaunchContext clc = Mockito.mock(ContainerLaunchContext.class);
        Mockito.when(clc.getCommands()).thenReturn(Lists.newArrayList());
        Mockito.when(container.getLaunchContext()).thenReturn(clc);
        Credentials credentials = Mockito.mock(Credentials.class);
        Mockito.when(container.getCredentials()).thenReturn(credentials);
        // Configuration errors should result in node shutdown...
        ContainerExecutor returnConfigError = Mockito.mock(ContainerExecutor.class);
        Mockito.when(returnConfigError.launchContainer(ArgumentMatchers.any())).thenThrow(new ConfigurationException("Mock configuration error"));
        ContainerLaunch launchConfigError = new ContainerLaunch(distContext, conf, dispatcher, returnConfigError, app, container, dirsHandler, containerManager);
        NodeStatusUpdater updater = Mockito.mock(NodeStatusUpdater.class);
        distContext.setNodeStatusUpdater(updater);
        launchConfigError.call();
        Mockito.verify(updater, Mockito.atLeastOnce()).reportException(ArgumentMatchers.any());
        // ... any other error should continue.
        ContainerExecutor returnOtherError = Mockito.mock(ContainerExecutor.class);
        Mockito.when(returnOtherError.launchContainer(ArgumentMatchers.any())).thenThrow(new IOException("Mock configuration error"));
        ContainerLaunch launchOtherError = new ContainerLaunch(distContext, conf, dispatcher, returnOtherError, app, container, dirsHandler, containerManager);
        NodeStatusUpdater updaterNoCall = Mockito.mock(NodeStatusUpdater.class);
        distContext.setNodeStatusUpdater(updaterNoCall);
        launchOtherError.call();
        Mockito.verify(updaterNoCall, Mockito.never()).reportException(ArgumentMatchers.any());
    }

    /**
     * Test that script exists with non-zero exit code when command fails.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testShellScriptBuilderStdOutandErrRedirection() throws IOException {
        ShellScriptBuilder builder = ShellScriptBuilder.create();
        Path logDir = new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath());
        File stdout = new File(logDir.toString(), CONTAINER_PRE_LAUNCH_STDOUT);
        File stderr = new File(logDir.toString(), CONTAINER_PRE_LAUNCH_STDERR);
        builder.stdout(logDir, CONTAINER_PRE_LAUNCH_STDOUT);
        builder.stderr(logDir, CONTAINER_PRE_LAUNCH_STDERR);
        // should redirect to specified stdout path
        String TEST_STDOUT_ECHO = "Test stdout redirection";
        builder.echo(TEST_STDOUT_ECHO);
        // should fail and redirect to stderr
        builder.mkdir(new Path("/invalidSrcDir"));
        builder.command(Arrays.asList(new String[]{ "unknownCommand" }));
        File shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "testShellScriptBuilderStdOutandErrRedirection");
        PrintStream writer = new PrintStream(new FileOutputStream(shellFile));
        builder.write(writer);
        writer.close();
        try {
            FileUtil.setExecutable(shellFile, true);
            Shell.ShellCommandExecutor shexc = new Shell.ShellCommandExecutor(new String[]{ shellFile.getAbsolutePath() }, BaseContainerManagerTest.tmpDir);
            try {
                shexc.execute();
                Assert.fail("builder shell command was expected to throw");
            } catch (IOException e) {
                // expected
                System.out.println(("Received an expected exception: " + (e.getMessage())));
                Assert.assertEquals(true, stdout.exists());
                BufferedReader stdoutReader = new BufferedReader(new FileReader(stdout));
                // Get the pid of the process
                String line = stdoutReader.readLine().trim();
                Assert.assertEquals(TEST_STDOUT_ECHO, line);
                // No more lines
                Assert.assertEquals(null, stdoutReader.readLine());
                stdoutReader.close();
                Assert.assertEquals(true, stderr.exists());
                Assert.assertTrue(((stderr.length()) > 0));
            }
        } finally {
            FileUtil.fullyDelete(shellFile);
            FileUtil.fullyDelete(stdout);
            FileUtil.fullyDelete(stderr);
        }
    }

    /**
     * Test that script exists with non-zero exit code when command fails.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testShellScriptBuilderWithNoRedirection() throws IOException {
        ShellScriptBuilder builder = ShellScriptBuilder.create();
        Path logDir = new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath());
        File stdout = new File(logDir.toString(), CONTAINER_PRE_LAUNCH_STDOUT);
        File stderr = new File(logDir.toString(), CONTAINER_PRE_LAUNCH_STDERR);
        // should redirect to specified stdout path
        String TEST_STDOUT_ECHO = "Test stdout redirection";
        builder.echo(TEST_STDOUT_ECHO);
        // should fail and redirect to stderr
        builder.mkdir(new Path("/invalidSrcDir"));
        builder.command(Arrays.asList(new String[]{ "unknownCommand" }));
        File shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "testShellScriptBuilderStdOutandErrRedirection");
        PrintStream writer = new PrintStream(new FileOutputStream(shellFile));
        builder.write(writer);
        writer.close();
        try {
            FileUtil.setExecutable(shellFile, true);
            Shell.ShellCommandExecutor shexc = new Shell.ShellCommandExecutor(new String[]{ shellFile.getAbsolutePath() }, BaseContainerManagerTest.tmpDir);
            try {
                shexc.execute();
                Assert.fail("builder shell command was expected to throw");
            } catch (IOException e) {
                // expected
                System.out.println(("Received an expected exception: " + (e.getMessage())));
                Assert.assertEquals(false, stdout.exists());
                Assert.assertEquals(false, stderr.exists());
            }
        } finally {
            FileUtil.fullyDelete(shellFile);
        }
    }

    /* ${foo.version} is substituted to suffix a specific version number */
    @Test
    public void testInvalidEnvVariableSubstitutionType1() throws IOException {
        Map<String, String> env = new HashMap<String, String>();
        // invalid env
        String invalidEnv = "version${foo.version}";
        if (Shell.WINDOWS) {
            invalidEnv = "version%foo%<>^&|=:version%";
        }
        env.put("testVar", invalidEnv);
        validateShellExecutorForDifferentEnvs(env);
    }

    /* Multiple paths are substituted in a path variable */
    @Test
    public void testInvalidEnvVariableSubstitutionType2() throws IOException {
        Map<String, String> env = new HashMap<String, String>();
        // invalid env
        String invalidEnv = "/abc:/${foo.path}:/$bar";
        if (Shell.WINDOWS) {
            invalidEnv = "/abc:/%foo%<>^&|=:path%:/%bar%";
        }
        env.put("testPath", invalidEnv);
        validateShellExecutorForDifferentEnvs(env);
    }

    @Test
    public void testValidEnvVariableSubstitution() throws IOException {
        File shellFile = null;
        try {
            shellFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "hello");
            Map<Path, List<String>> resources = new HashMap<Path, List<String>>();
            FileOutputStream fos = new FileOutputStream(shellFile);
            FileUtil.setExecutable(shellFile, true);
            Map<String, String> env = new LinkedHashMap<String, String>();
            // valid env
            env.put("foo", "2.4.6");
            env.put("testVar", "version${foo}");
            List<String> commands = new ArrayList<String>();
            DefaultContainerExecutor executor = new DefaultContainerExecutor();
            Configuration execConf = new Configuration();
            execConf.setBoolean(NM_LOG_CONTAINER_DEBUG_INFO, false);
            executor.setConf(execConf);
            LinkedHashSet<String> nmVars = new LinkedHashSet<>();
            executor.writeLaunchEnv(fos, env, resources, commands, new Path(BaseContainerManagerTest.localLogDir.getAbsolutePath()), user, nmVars);
            fos.flush();
            fos.close();
            // It is supposed that LANG is set as C.
            Map<String, String> cmdEnv = new HashMap<String, String>();
            cmdEnv.put("LANG", "C");
            Shell.ShellCommandExecutor shexc = new Shell.ShellCommandExecutor(new String[]{ shellFile.getAbsolutePath() }, BaseContainerManagerTest.tmpDir, cmdEnv);
            try {
                shexc.execute();
            } catch (ExitCodeException e) {
                Assert.fail("Should not catch exception");
            }
            Assert.assertTrue(((shexc.getExitCode()) == 0));
        } finally {
            // cleanup
            if ((shellFile != null) && (shellFile.exists())) {
                shellFile.delete();
            }
        }
    }

    @Test(timeout = 1000)
    public void testGetEnvDependencies() {
        final Set<String> expected = new HashSet<>();
        final ContainerLaunch.ShellScriptBuilder bash = ContainerLaunch.ShellScriptBuilder.create(OS_TYPE_LINUX);
        String s;
        s = null;
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "A";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "\\$A";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "$$";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "$1";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "handle \"\'$A\'\" simple quotes";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "$ crash test for StringArrayOutOfBoundException";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "${ crash test for StringArrayOutOfBoundException";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "${# crash test for StringArrayOutOfBoundException";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "crash test for StringArrayOutOfBoundException $";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "crash test for StringArrayOutOfBoundException ${";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "crash test for StringArrayOutOfBoundException ${#";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        expected.add("A");
        s = "$A";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "${A}";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "${#A[*]}";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "in the $A midlle";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        expected.add("B");
        s = "${A:-$B} var in var";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "${A}$B var outside var";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        expected.add("C");
        s = "$A:$B:$C:pathlist var";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        s = "${A}/foo/bar:$B:${C}:pathlist var";
        Assert.assertEquals(("failed to parse " + s), expected, bash.getEnvDependencies(s));
        ContainerLaunch.ShellScriptBuilder win = ContainerLaunch.ShellScriptBuilder.create(OS_TYPE_WIN);
        expected.clear();
        s = null;
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "A";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%%%%%%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%%A%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%A";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%A:";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        expected.add("A");
        s = "%A%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%%%A%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%%C%A%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%A:~-1%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%A%B%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%A%%%%%B%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        expected.add("B");
        s = "%A%%B%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%A%%%%B%";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        expected.add("C");
        s = "%A%:%B%:%C%:pathlist var";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
        s = "%A%\\foo\\bar:%B%:%C%:pathlist var";
        Assert.assertEquals(("failed to parse " + s), expected, win.getEnvDependencies(s));
    }

    @Test(timeout = 5000)
    public void testOrderEnvByDependencies() {
        final Map<String, Set<String>> fakeDeps = new HashMap<>();
        fakeDeps.put("Aval", Collections.emptySet());// A has no dependencies

        fakeDeps.put("Bval", asSet("A"));// B depends on A

        fakeDeps.put("Cval", asSet("B"));// C depends on B

        fakeDeps.put("Dval", asSet("A", "B"));// C depends on B

        fakeDeps.put("cyclic_Aval", asSet("cyclic_B"));
        fakeDeps.put("cyclic_Bval", asSet("cyclic_C"));
        fakeDeps.put("cyclic_Cval", asSet("cyclic_A", "C"));
        final ContainerLaunch.ShellScriptBuilder sb = new ContainerLaunch.ShellScriptBuilder() {
            @Override
            public Set<String> getEnvDependencies(final String envVal) {
                return fakeDeps.get(envVal);
            }

            @Override
            protected void mkdir(Path path) throws IOException {
            }

            @Override
            public void listDebugInformation(Path output) throws IOException {
            }

            @Override
            protected void link(Path src, Path dst) throws IOException {
            }

            @Override
            public void env(String key, String value) throws IOException {
            }

            @Override
            public void whitelistedEnv(String key, String value) throws IOException {
            }

            @Override
            public void copyDebugInformation(Path src, Path dst) throws IOException {
            }

            @Override
            public void command(List<String> command) throws IOException {
            }

            @Override
            public void setStdOut(Path stdout) throws IOException {
            }

            @Override
            public void setStdErr(Path stdout) throws IOException {
            }

            @Override
            public void echo(String echoStr) throws IOException {
            }
        };
        try {
            Assert.assertNull("Ordering a null env map must return a null value.", sb.orderEnvByDependencies(null));
        } catch (Exception e) {
            Assert.fail("null value is to be supported");
        }
        try {
            Assert.assertEquals("Ordering an empty env map must return an empty map.", 0, sb.orderEnvByDependencies(Collections.emptyMap()).size());
        } catch (Exception e) {
            Assert.fail("Empty map is to be supported");
        }
        final Map<String, String> combination = new LinkedHashMap<>();
        // to test all possible cases, we create all possible combinations and test
        // each of them
        class TestEnv {
            private final String key;

            private final String value;

            private boolean used = false;

            TestEnv(String key, String value) {
                this.key = key;
                this.value = value;
            }

            void generateCombinationAndTest(int nbItems, final ArrayList<TestEnv> keylist) {
                used = true;
                combination.put(key, value);
                try {
                    if (nbItems == 0) {
                        // LOG.info("Combo : " + combination);
                        TestContainerLaunch.assertOrderEnvByDependencies(combination, sb);
                        return;
                    }
                    for (TestEnv localEnv : keylist) {
                        if (!(localEnv.used)) {
                            localEnv.generateCombinationAndTest((nbItems - 1), keylist);
                        }
                    }
                } finally {
                    combination.remove(key);
                    used = false;
                }
            }
        }
        final ArrayList<TestEnv> keys = new ArrayList<>();
        for (String key : new String[]{ "A", "B", "C", "D", "cyclic_A", "cyclic_B", "cyclic_C" }) {
            keys.add(new TestEnv(key, (key + "val")));
        }
        for (int count = keys.size(); count > 0; count--) {
            for (TestEnv env : keys) {
                env.generateCombinationAndTest(count, keys);
            }
        }
    }

    @Test
    public void testDistributedCacheDirs() throws Exception {
        Container container = Mockito.mock(Container.class);
        ApplicationId appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        ContainerId containerId = ContainerId.newContainerId(ApplicationAttemptId.newInstance(appId, 1), 1);
        Mockito.when(container.getContainerId()).thenReturn(containerId);
        Mockito.when(container.getUser()).thenReturn("test");
        Mockito.when(container.getLocalizedResources()).thenReturn(Collections.<Path, List<String>>emptyMap());
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        ContainerLaunchContext clc = Mockito.mock(ContainerLaunchContext.class);
        Mockito.when(clc.getCommands()).thenReturn(Collections.<String>emptyList());
        Mockito.when(container.getLaunchContext()).thenReturn(clc);
        @SuppressWarnings("rawtypes")
        TestContainerLaunch.ContainerExitHandler eventHandler = Mockito.mock(TestContainerLaunch.ContainerExitHandler.class);
        Mockito.when(dispatcher.getEventHandler()).thenReturn(eventHandler);
        Application app = Mockito.mock(Application.class);
        Mockito.when(app.getAppId()).thenReturn(appId);
        Mockito.when(app.getUser()).thenReturn("test");
        Credentials creds = Mockito.mock(Credentials.class);
        Mockito.when(container.getCredentials()).thenReturn(creds);
        ((NMContext) (context)).setNodeId(NodeId.newInstance("127.0.0.1", BaseContainerManagerTest.HTTP_PORT));
        ContainerExecutor mockExecutor = Mockito.mock(ContainerExecutor.class);
        LocalDirsHandlerService mockDirsHandler = Mockito.mock(LocalDirsHandlerService.class);
        List<String> localDirsForRead = new ArrayList<String>();
        String localDir1 = new File("target", ((this.getClass().getSimpleName()) + "-localDir1")).getAbsoluteFile().toString();
        String localDir2 = new File("target", ((this.getClass().getSimpleName()) + "-localDir2")).getAbsoluteFile().toString();
        localDirsForRead.add(localDir1);
        localDirsForRead.add(localDir2);
        List<String> localDirs = new ArrayList();
        localDirs.add(localDir1);
        Path logPathForWrite = new Path(localDirs.get(0));
        Mockito.when(mockDirsHandler.areDisksHealthy()).thenReturn(true);
        Mockito.when(mockDirsHandler.getLocalDirsForRead()).thenReturn(localDirsForRead);
        Mockito.when(mockDirsHandler.getLocalDirs()).thenReturn(localDirs);
        Mockito.when(mockDirsHandler.getLogDirs()).thenReturn(localDirs);
        Mockito.when(mockDirsHandler.getLogPathForWrite(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(logPathForWrite);
        Mockito.when(mockDirsHandler.getLocalPathForWrite(ArgumentMatchers.anyString())).thenReturn(logPathForWrite);
        Mockito.when(mockDirsHandler.getLocalPathForWrite(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(logPathForWrite);
        ContainerLaunch launch = new ContainerLaunch(context, conf, dispatcher, mockExecutor, app, container, mockDirsHandler, containerManager);
        launch.call();
        ArgumentCaptor<ContainerStartContext> ctxCaptor = ArgumentCaptor.forClass(ContainerStartContext.class);
        Mockito.verify(mockExecutor, Mockito.times(1)).launchContainer(ctxCaptor.capture());
        ContainerStartContext ctx = ctxCaptor.getValue();
        Assert.assertEquals(StringUtils.join(",", launch.getNMFilecacheDirs(localDirsForRead)), StringUtils.join(",", ctx.getFilecacheDirs()));
        Assert.assertEquals(StringUtils.join(",", launch.getUserFilecacheDirs(localDirsForRead)), StringUtils.join(",", ctx.getUserFilecacheDirs()));
    }

    @Test(timeout = 20000)
    public void testFilesAndEnvWithoutHTTPS() throws Exception {
        testFilesAndEnv(false);
    }

    @Test(timeout = 20000)
    public void testFilesAndEnvWithHTTPS() throws Exception {
        testFilesAndEnv(true);
    }
}

