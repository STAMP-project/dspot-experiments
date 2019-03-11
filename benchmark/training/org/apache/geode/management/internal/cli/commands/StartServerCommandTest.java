/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.commands;


import ConfigurationProperties.DISABLE_AUTO_RECONNECT;
import ConfigurationProperties.STATISTIC_SAMPLE_RATE;
import ServerLauncher.Command.START;
import StartMemberUtils.CORE_DEPENDENCIES_JAR_PATHNAME;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.geode.distributed.ServerLauncher;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.internal.lang.SystemUtils;
import org.apache.geode.pdx.PdxSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StartServerCommandTest {
    private StartServerCommand serverCommands;

    @Test
    public void testServerClasspathOrder() {
        String userClasspath = "/path/to/user/lib/app.jar:/path/to/user/classes";
        String expectedClasspath = StartMemberUtils.getGemFireJarPath().concat(File.pathSeparator).concat(userClasspath).concat(File.pathSeparator).concat(CORE_DEPENDENCIES_JAR_PATHNAME);
        String actualClasspath = serverCommands.getServerClasspath(false, userClasspath);
        Assert.assertEquals(expectedClasspath, actualClasspath);
    }

    @Test
    public void testAddJvmOptionsForOutOfMemoryErrors() {
        final List<String> jvmOptions = new ArrayList<>(1);
        StartServerCommand.addJvmOptionsForOutOfMemoryErrors(jvmOptions);
        if (SystemUtils.isHotSpotVM()) {
            if (SystemUtils.isWindows()) {
                Assert.assertTrue(jvmOptions.contains("-XX:OnOutOfMemoryError=taskkill /F /PID %p"));
            } else {
                Assert.assertTrue(jvmOptions.contains("-XX:OnOutOfMemoryError=kill -KILL %p"));
            }
        } else
            if (SystemUtils.isJ9VM()) {
                Assert.assertEquals(1, jvmOptions.size());
                Assert.assertTrue(jvmOptions.contains("-Xcheck:memory"));
            } else
                if (SystemUtils.isJRockitVM()) {
                    Assert.assertEquals(1, jvmOptions.size());
                    Assert.assertTrue(jvmOptions.contains("-XXexitOnOutOfMemory"));
                } else {
                    Assert.assertTrue(jvmOptions.isEmpty());
                }


    }

    @Test
    public void testCreateServerCommandLine() throws Exception {
        ServerLauncher serverLauncher = new ServerLauncher.Builder().setCommand(START).setDisableDefaultServer(true).setMemberName("testCreateServerCommandLine").setRebalance(true).setServerPort(41214).setCriticalHeapPercentage(95.5F).setEvictionHeapPercentage(85.0F).setSocketBufferSize((1024 * 1024)).setMessageTimeToLive(93).build();
        String[] commandLineElements = serverCommands.createStartServerCommandLine(serverLauncher, null, null, new Properties(), null, false, new String[0], false, null, null);
        Assert.assertNotNull(commandLineElements);
        Assert.assertTrue(((commandLineElements.length) > 0));
        Set<String> expectedCommandLineElements = new HashSet<>(6);
        expectedCommandLineElements.add(serverLauncher.getCommand().getName());
        expectedCommandLineElements.add("--disable-default-server");
        expectedCommandLineElements.add(serverLauncher.getMemberName().toLowerCase());
        expectedCommandLineElements.add("--rebalance");
        expectedCommandLineElements.add(String.format("--server-port=%1$d", serverLauncher.getServerPort()));
        expectedCommandLineElements.add(String.format("--critical-heap-percentage=%1$s", serverLauncher.getCriticalHeapPercentage()));
        expectedCommandLineElements.add(String.format("--eviction-heap-percentage=%1$s", serverLauncher.getEvictionHeapPercentage()));
        expectedCommandLineElements.add(String.format("--socket-buffer-size=%1$d", serverLauncher.getSocketBufferSize()));
        expectedCommandLineElements.add(String.format("--message-time-to-live=%1$d", serverLauncher.getMessageTimeToLive()));
        for (String commandLineElement : commandLineElements) {
            expectedCommandLineElements.remove(commandLineElement.toLowerCase());
        }
        Assert.assertTrue(String.format("Expected ([]); but was (%1$s)", expectedCommandLineElements), expectedCommandLineElements.isEmpty());
    }

    @Test
    public void testCreateServerCommandLineWithRestAPI() throws Exception {
        ServerLauncher serverLauncher = new ServerLauncher.Builder().setCommand(START).setDisableDefaultServer(true).setMemberName("testCreateServerCommandLine").setRebalance(true).setServerPort(41214).setCriticalHeapPercentage(95.5F).setEvictionHeapPercentage(85.0F).build();
        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty(START_DEV_REST_API, "true");
        gemfireProperties.setProperty(HTTP_SERVICE_PORT, "8080");
        gemfireProperties.setProperty(HTTP_SERVICE_BIND_ADDRESS, "localhost");
        String[] commandLineElements = serverCommands.createStartServerCommandLine(serverLauncher, null, null, gemfireProperties, null, false, new String[0], false, null, null);
        Assert.assertNotNull(commandLineElements);
        Assert.assertTrue(((commandLineElements.length) > 0));
        Set<String> expectedCommandLineElements = new HashSet<>(6);
        expectedCommandLineElements.add(serverLauncher.getCommand().getName());
        expectedCommandLineElements.add("--disable-default-server");
        expectedCommandLineElements.add(serverLauncher.getMemberName().toLowerCase());
        expectedCommandLineElements.add("--rebalance");
        expectedCommandLineElements.add(String.format("--server-port=%1$d", serverLauncher.getServerPort()));
        expectedCommandLineElements.add(String.format("--critical-heap-percentage=%1$s", serverLauncher.getCriticalHeapPercentage()));
        expectedCommandLineElements.add(String.format("--eviction-heap-percentage=%1$s", serverLauncher.getEvictionHeapPercentage()));
        expectedCommandLineElements.add(((((("-d" + (DistributionConfig.GEMFIRE_PREFIX)) + "") + (START_DEV_REST_API)) + "=") + "true"));
        expectedCommandLineElements.add(((((("-d" + (DistributionConfig.GEMFIRE_PREFIX)) + "") + (HTTP_SERVICE_PORT)) + "=") + "8080"));
        expectedCommandLineElements.add(((((("-d" + (DistributionConfig.GEMFIRE_PREFIX)) + "") + (HTTP_SERVICE_BIND_ADDRESS)) + "=") + "localhost"));
        for (String commandLineElement : commandLineElements) {
            expectedCommandLineElements.remove(commandLineElement.toLowerCase());
        }
        Assert.assertTrue(String.format("Expected ([]); but was (%1$s)", expectedCommandLineElements), expectedCommandLineElements.isEmpty());
    }

    @Test
    public void testCreateStartServerCommandLineWithAllOptions() throws Exception {
        ServerLauncher serverLauncher = new ServerLauncher.Builder().setAssignBuckets(Boolean.TRUE).setCommand(START).setCriticalHeapPercentage(95.5F).setCriticalOffHeapPercentage(95.5F).setDebug(Boolean.TRUE).setDisableDefaultServer(Boolean.TRUE).setDeletePidFileOnStop(Boolean.TRUE).setEvictionHeapPercentage(85.0F).setEvictionOffHeapPercentage(85.0F).setForce(Boolean.TRUE).setHostNameForClients("localhost").setMaxConnections(800).setMaxMessageCount(500).setMaxThreads(100).setMemberName("fullServer").setMessageTimeToLive(93).setPdxDiskStore("pdxDiskStore").setPdxIgnoreUnreadFields(Boolean.TRUE).setPdxPersistent(Boolean.TRUE).setPdxReadSerialized(Boolean.TRUE).setPdxSerializer(Mockito.mock(PdxSerializer.class)).setRebalance(Boolean.TRUE).setRedirectOutput(Boolean.TRUE).setRebalance(true).setServerPort(41214).setSocketBufferSize((1024 * 1024)).setSpringXmlLocation("/config/spring-server.xml").build();
        File gemfirePropertiesFile = Mockito.spy(Mockito.mock(File.class));
        Mockito.when(gemfirePropertiesFile.getAbsolutePath()).thenReturn("/config/customGemfire.properties");
        File gemfireSecurityPropertiesFile = Mockito.spy(Mockito.mock(File.class));
        Mockito.when(gemfireSecurityPropertiesFile.getAbsolutePath()).thenReturn("/config/customGemfireSecurity.properties");
        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty(STATISTIC_SAMPLE_RATE, "1500");
        gemfireProperties.setProperty(DISABLE_AUTO_RECONNECT, "true");
        String heapSize = "1024m";
        String customClasspath = "/temp/domain-1.0.0.jar";
        String[] jvmArguments = new String[]{ "-verbose:gc", "-Xloggc:member-gc.log", "-XX:+PrintGCDateStamps", "-XX:+PrintGCDetails" };
        String[] commandLineElements = serverCommands.createStartServerCommandLine(serverLauncher, gemfirePropertiesFile, gemfireSecurityPropertiesFile, gemfireProperties, customClasspath, Boolean.FALSE, jvmArguments, Boolean.FALSE, heapSize, heapSize);
        Set<String> expectedCommandLineElements = new HashSet<>();
        expectedCommandLineElements.add(StartMemberUtils.getJavaPath());
        expectedCommandLineElements.add("-server");
        expectedCommandLineElements.add("-classpath");
        expectedCommandLineElements.add(StartMemberUtils.getGemFireJarPath().concat(File.pathSeparator).concat(customClasspath).concat(File.pathSeparator).concat(CORE_DEPENDENCIES_JAR_PATHNAME));
        expectedCommandLineElements.add("-DgemfirePropertyFile=".concat(gemfirePropertiesFile.getAbsolutePath()));
        expectedCommandLineElements.add("-DgemfireSecurityPropertyFile=".concat(gemfireSecurityPropertiesFile.getAbsolutePath()));
        expectedCommandLineElements.add("-Dgemfire.statistic-sample-rate=1500");
        expectedCommandLineElements.add("-Dgemfire.disable-auto-reconnect=true");
        expectedCommandLineElements.addAll(Arrays.asList(jvmArguments));
        expectedCommandLineElements.add("org.apache.geode.distributed.ServerLauncher");
        expectedCommandLineElements.add("start");
        expectedCommandLineElements.add("fullServer");
        expectedCommandLineElements.add("--assign-buckets");
        expectedCommandLineElements.add("--debug");
        expectedCommandLineElements.add("--disable-default-server");
        expectedCommandLineElements.add("--force");
        expectedCommandLineElements.add("--rebalance");
        expectedCommandLineElements.add("--redirect-output");
        expectedCommandLineElements.add("--server-port=41214");
        expectedCommandLineElements.add("--spring-xml-location=/config/spring-server.xml");
        expectedCommandLineElements.add("--critical-heap-percentage=95.5");
        expectedCommandLineElements.add("--eviction-heap-percentage=85.0");
        expectedCommandLineElements.add("--critical-off-heap-percentage=95.5");
        expectedCommandLineElements.add("--eviction-off-heap-percentage=85.0");
        expectedCommandLineElements.add("--max-connections=800");
        expectedCommandLineElements.add("--max-message-count=500");
        expectedCommandLineElements.add("--max-threads=100");
        expectedCommandLineElements.add("--message-time-to-live=93");
        expectedCommandLineElements.add("--socket-buffer-size=1048576");
        expectedCommandLineElements.add("--hostname-for-clients=localhost");
        Assert.assertNotNull(commandLineElements);
        Assert.assertTrue(((commandLineElements.length) > 0));
        assertThat(commandLineElements).containsAll(expectedCommandLineElements);
    }
}

