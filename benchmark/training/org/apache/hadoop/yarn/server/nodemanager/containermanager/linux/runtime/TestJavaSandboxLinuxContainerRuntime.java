/**
 * Licensed to the Apache Software Foundation (ASF) under one
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
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime;


import ContainerRuntimeContext.Builder;
import JavaSandboxLinuxContainerRuntime.SandboxMode.enforcing;
import JavaSandboxLinuxContainerRuntime.SandboxMode.permissive;
import YarnConfiguration.YARN_CONTAINER_SANDBOX_WHITELIST_GROUP;
import java.io.File;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketPermission;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerExecutionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static NMContainerPolicyUtils.POLICY_APPEND_FLAG;


/**
 * Test policy file generation and policy enforcement for the
 * {@link JavaSandboxLinuxContainerRuntime}.
 */
public class TestJavaSandboxLinuxContainerRuntime {
    private static final String HADOOP_HOME = "hadoop.home.dir";

    private static final String HADOOP_HOME_DIR = System.getProperty(TestJavaSandboxLinuxContainerRuntime.HADOOP_HOME);

    private final Properties baseProps = new Properties(System.getProperties());

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static File grantFile;

    private static File denyFile;

    private static File policyFile;

    private static File grantDir;

    private static File denyDir;

    private static File containerDir;

    private static Path policyFilePath;

    private static SecurityManager securityManager;

    private Map<org.apache.hadoop.fs.Path, List<String>> resources;

    private Map<String, String> env;

    private List<String> whitelistGroup;

    private PrivilegedOperationExecutor mockExecutor;

    private JavaSandboxLinuxContainerRuntime runtime;

    private Builder runtimeContextBuilder;

    private Configuration conf;

    private static final String NORMAL_USER = System.getProperty("user.name");

    private static final String NORMAL_GROUP = "normalGroup";

    private static final String WHITELIST_USER = "picard";

    private static final String WHITELIST_GROUP = "captains";

    private static final String CONTAINER_ID = "container_1234567890";

    private static final String APPLICATION_ID = "application_1234567890";

    private File baseTestDirectory;

    public static final String SOCKET_PERMISSION_FORMAT = "grant { \n" + ("   permission %1s \"%2s\", \"%3s\";\n" + "};\n");

    public static final String RUNTIME_PERMISSION_FORMAT = "grant { \n" + ("   permission %1s \"%2s\";\n" + "};\n");

    @Test
    public void testGroupPolicies() throws IOException, ContainerExecutionException {
        // Generate new policy files each containing one grant
        File openSocketPolicyFile = File.createTempFile("openSocket", "policy", baseTestDirectory);
        File classLoaderPolicyFile = File.createTempFile("createClassLoader", "policy", baseTestDirectory);
        Permission socketPerm = new SocketPermission("localhost:0", "listen");
        Permission runtimePerm = new RuntimePermission("createClassLoader");
        StringBuilder socketPermString = new StringBuilder();
        Formatter openSocketPolicyFormatter = new Formatter(socketPermString);
        openSocketPolicyFormatter.format(TestJavaSandboxLinuxContainerRuntime.SOCKET_PERMISSION_FORMAT, socketPerm.getClass().getName(), socketPerm.getName(), socketPerm.getActions());
        FileWriter socketPermWriter = new FileWriter(openSocketPolicyFile);
        socketPermWriter.write(socketPermString.toString());
        socketPermWriter.close();
        StringBuilder classLoaderPermString = new StringBuilder();
        Formatter classLoaderPolicyFormatter = new Formatter(classLoaderPermString);
        classLoaderPolicyFormatter.format(TestJavaSandboxLinuxContainerRuntime.RUNTIME_PERMISSION_FORMAT, runtimePerm.getClass().getName(), runtimePerm.getName());
        FileWriter classLoaderPermWriter = new FileWriter(classLoaderPolicyFile);
        classLoaderPermWriter.write(classLoaderPermString.toString());
        classLoaderPermWriter.close();
        conf.set(((YarnConfiguration.YARN_CONTAINER_SANDBOX_POLICY_GROUP_PREFIX) + (TestJavaSandboxLinuxContainerRuntime.WHITELIST_GROUP)), openSocketPolicyFile.toString());
        conf.set(((YarnConfiguration.YARN_CONTAINER_SANDBOX_POLICY_GROUP_PREFIX) + (TestJavaSandboxLinuxContainerRuntime.NORMAL_GROUP)), classLoaderPolicyFile.toString());
        String[] inputCommand = new String[]{ "$JAVA_HOME/bin/java jar MyJob.jar" };
        List<String> commands = Arrays.asList(inputCommand);
        runtimeContextBuilder.setExecutionAttribute(LinuxContainerRuntimeConstants.USER, TestJavaSandboxLinuxContainerRuntime.WHITELIST_USER);
        runtimeContextBuilder.setExecutionAttribute(LinuxContainerRuntimeConstants.CONTAINER_RUN_CMDS, commands);
        runtime.prepareContainer(runtimeContextBuilder.build());
        // pull generated policy from cmd
        Matcher policyMatches = Pattern.compile(((POLICY_APPEND_FLAG) + "=?([^ ]+)")).matcher(commands.get(0));
        policyMatches.find();
        String generatedPolicy = policyMatches.group(1);
        // Test that generated policy file has included both policies
        Assert.assertTrue(Files.readAllLines(Paths.get(generatedPolicy)).contains(classLoaderPermString.toString().split("\n")[1]));
        Assert.assertTrue(Files.readAllLines(Paths.get(generatedPolicy)).contains(socketPermString.toString().split("\n")[1]));
    }

    @Test
    public void testGrant() throws Exception {
        FilePermission grantPermission = new FilePermission(TestJavaSandboxLinuxContainerRuntime.grantFile.getAbsolutePath(), "read");
        TestJavaSandboxLinuxContainerRuntime.securityManager.checkPermission(grantPermission);
    }

    @Test
    public void testDeny() throws Exception {
        FilePermission denyPermission = new FilePermission(TestJavaSandboxLinuxContainerRuntime.denyFile.getAbsolutePath(), "read");
        exception.expect(AccessControlException.class);
        TestJavaSandboxLinuxContainerRuntime.securityManager.checkPermission(denyPermission);
    }

    @Test
    public void testEnforcingMode() throws ContainerExecutionException {
        String[] nonJavaCommands = new String[]{ "bash malicious_script.sh", "python malicious_script.py" };
        List<String> commands = Arrays.asList(nonJavaCommands);
        exception.expect(ContainerExecutionException.class);
        JavaSandboxLinuxContainerRuntime.NMContainerPolicyUtils.appendSecurityFlags(commands, env, TestJavaSandboxLinuxContainerRuntime.policyFilePath, enforcing);
    }

    @Test
    public void testPermissiveMode() throws ContainerExecutionException {
        String[] nonJavaCommands = new String[]{ "bash non-java-script.sh", "python non-java-script.py" };
        List<String> commands = Arrays.asList(nonJavaCommands);
        JavaSandboxLinuxContainerRuntime.NMContainerPolicyUtils.appendSecurityFlags(commands, env, TestJavaSandboxLinuxContainerRuntime.policyFilePath, permissive);
    }

    @Test
    public void testDisabledSandboxWithWhitelist() throws ContainerExecutionException {
        String[] inputCommand = new String[]{ "java jar MyJob.jar" };
        List<String> commands = Arrays.asList(inputCommand);
        conf.set(YARN_CONTAINER_SANDBOX_WHITELIST_GROUP, TestJavaSandboxLinuxContainerRuntime.WHITELIST_GROUP);
        runtimeContextBuilder.setExecutionAttribute(LinuxContainerRuntimeConstants.USER, TestJavaSandboxLinuxContainerRuntime.WHITELIST_USER);
        runtimeContextBuilder.setExecutionAttribute(LinuxContainerRuntimeConstants.CONTAINER_RUN_CMDS, commands);
        runtime.prepareContainer(runtimeContextBuilder.build());
        Assert.assertTrue(("Command should not be modified when user is " + "member of whitelisted group"), inputCommand[0].equals(commands.get(0)));
    }

    @Test
    public void testEnabledSandboxWithWhitelist() throws ContainerExecutionException {
        String[] inputCommand = new String[]{ "$JAVA_HOME/bin/java jar -Djava.security.manager MyJob.jar" };
        List<String> commands = Arrays.asList(inputCommand);
        conf.set(YARN_CONTAINER_SANDBOX_WHITELIST_GROUP, TestJavaSandboxLinuxContainerRuntime.WHITELIST_GROUP);
        runtimeContextBuilder.setExecutionAttribute(LinuxContainerRuntimeConstants.USER, TestJavaSandboxLinuxContainerRuntime.WHITELIST_USER);
        runtimeContextBuilder.setExecutionAttribute(LinuxContainerRuntimeConstants.CONTAINER_RUN_CMDS, commands);
        runtime.prepareContainer(runtimeContextBuilder.build());
        Assert.assertTrue(("Command should be modified to include " + "policy file in whitelisted Sandbox mode"), ((commands.get(0).contains(NMContainerPolicyUtils.SECURITY_FLAG)) && (commands.get(0).contains(NMContainerPolicyUtils.POLICY_FLAG))));
    }

    @Test
    public void testDeniedWhitelistGroup() throws ContainerExecutionException {
        String[] inputCommand = new String[]{ "$JAVA_HOME/bin/java jar MyJob.jar" };
        List<String> commands = Arrays.asList(inputCommand);
        conf.set(YARN_CONTAINER_SANDBOX_WHITELIST_GROUP, TestJavaSandboxLinuxContainerRuntime.WHITELIST_GROUP);
        runtimeContextBuilder.setExecutionAttribute(LinuxContainerRuntimeConstants.USER, TestJavaSandboxLinuxContainerRuntime.NORMAL_USER);
        runtimeContextBuilder.setExecutionAttribute(LinuxContainerRuntimeConstants.CONTAINER_RUN_CMDS, commands);
        runtime.prepareContainer(runtimeContextBuilder.build());
        Assert.assertTrue(("Java security manager must be enabled for " + "unauthorized users"), commands.get(0).contains(NMContainerPolicyUtils.SECURITY_FLAG));
    }

    @Test
    public void testChainedCmdRegex() {
        String[] multiCmds = new String[]{ "cmd1 && cmd2", "cmd1 || cmd2", "cmd1 `cmd2`", "cmd1 $(cmd2)", "cmd1; \\\n cmd2", "cmd1; cmd2", "cmd1|&cmd2", "cmd1|cmd2", "cmd1&cmd2" };
        Arrays.stream(multiCmds).forEach(( cmd) -> Assert.assertTrue(cmd.matches(NMContainerPolicyUtils.MULTI_COMMAND_REGEX)));
        Assert.assertFalse("cmd1 &> logfile".matches(NMContainerPolicyUtils.MULTI_COMMAND_REGEX));
    }

    @Test
    public void testContainsJavaRegex() {
        String[] javaCmds = new String[]{ "$JAVA_HOME/bin/java -cp App.jar AppClass", "$JAVA_HOME/bin/java -jar App.jar AppClass &> logfile" };
        String[] nonJavaCmds = new String[]{ "$JAVA_HOME/bin/jajavava -cp App.jar AppClass", "/nm/app/container/usercache/badjava -cp Bad.jar ChaosClass" };
        for (String javaCmd : javaCmds) {
            Assert.assertTrue(javaCmd.matches(NMContainerPolicyUtils.CONTAINS_JAVA_CMD));
        }
        for (String nonJavaCmd : nonJavaCmds) {
            Assert.assertFalse(nonJavaCmd.matches(NMContainerPolicyUtils.CONTAINS_JAVA_CMD));
        }
    }

    @Test
    public void testCleanCmdRegex() {
        String[] securityManagerCmds = new String[]{ "/usr/bin/java -Djava.security.manager -cp $CLASSPATH $MainClass", "-Djava.security.manager -Djava.security.policy==testpolicy keepThis" };
        String[] cleanedCmdsResult = new String[]{ "/usr/bin/java  -cp $CLASSPATH $MainClass", "keepThis" };
        for (int i = 0; i < (securityManagerCmds.length); i++) {
            Assert.assertEquals(securityManagerCmds[i].replaceAll(NMContainerPolicyUtils.CLEAN_CMD_REGEX, "").trim(), cleanedCmdsResult[i]);
        }
    }

    @Test
    public void testAppendSecurityFlags() throws ContainerExecutionException {
        String securityString = "-Djava.security.manager -Djava.security.policy==" + (TestJavaSandboxLinuxContainerRuntime.policyFile.getAbsolutePath());
        String[] badCommands = new String[]{ "$JAVA_HOME/bin/java -Djava.security.manager " + "-Djava.security.policy=/home/user/java.policy", "$JAVA_HOME/bin/java -cp MyApp.jar MrAppMaster" };
        String[] cleanCommands = new String[]{ "$JAVA_HOME/bin/java " + securityString, ("$JAVA_HOME/bin/java " + securityString) + " -cp MyApp.jar MrAppMaster" };
        List<String> commands = Arrays.asList(badCommands);
        JavaSandboxLinuxContainerRuntime.NMContainerPolicyUtils.appendSecurityFlags(commands, env, TestJavaSandboxLinuxContainerRuntime.policyFilePath, enforcing);
        for (int i = 0; i < (commands.size()); i++) {
            Assert.assertTrue(commands.get(i).trim().equals(cleanCommands[i].trim()));
        }
    }
}

