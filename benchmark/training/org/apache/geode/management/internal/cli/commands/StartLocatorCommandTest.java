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
import LocatorLauncher.Command.START;
import StartMemberUtils.CORE_DEPENDENCIES_JAR_PATHNAME;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.geode.distributed.LocatorLauncher;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StartLocatorCommandTest {
    private StartLocatorCommand startLocatorCommand;

    @Test
    public void testLocatorClasspathOrder() {
        String userClasspath = "/path/to/user/lib/app.jar:/path/to/user/classes";
        String expectedClasspath = StartMemberUtils.getGemFireJarPath().concat(File.pathSeparator).concat(userClasspath).concat(File.pathSeparator).concat(System.getProperty("java.class.path")).concat(File.pathSeparator).concat(CORE_DEPENDENCIES_JAR_PATHNAME);
        String actualClasspath = startLocatorCommand.getLocatorClasspath(true, userClasspath);
        Assert.assertEquals(expectedClasspath, actualClasspath);
    }

    @Test
    public void testLocatorCommandLineWithRestAPI() throws Exception {
        LocatorLauncher locatorLauncher = new LocatorLauncher.Builder().setCommand(START).setMemberName("testLocatorCommandLineWithRestAPI").setBindAddress("localhost").setPort(11111).build();
        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty(HTTP_SERVICE_PORT, "8089");
        gemfireProperties.setProperty(HTTP_SERVICE_BIND_ADDRESS, "localhost");
        String[] commandLineElements = startLocatorCommand.createStartLocatorCommandLine(locatorLauncher, null, null, gemfireProperties, null, false, new String[0], null, null);
        Assert.assertNotNull(commandLineElements);
        Assert.assertTrue(((commandLineElements.length) > 0));
        Set<String> expectedCommandLineElements = new HashSet<>(6);
        expectedCommandLineElements.add(locatorLauncher.getCommand().getName());
        expectedCommandLineElements.add(locatorLauncher.getMemberName().toLowerCase());
        expectedCommandLineElements.add(String.format("--port=%1$d", locatorLauncher.getPort()));
        expectedCommandLineElements.add(((((("-d" + (DistributionConfig.GEMFIRE_PREFIX)) + "") + (HTTP_SERVICE_PORT)) + "=") + "8089"));
        expectedCommandLineElements.add(((((("-d" + (DistributionConfig.GEMFIRE_PREFIX)) + "") + (HTTP_SERVICE_BIND_ADDRESS)) + "=") + "localhost"));
        for (String commandLineElement : commandLineElements) {
            expectedCommandLineElements.remove(commandLineElement.toLowerCase());
        }
        Assert.assertTrue(String.format("Expected ([]); but was (%1$s)", expectedCommandLineElements), expectedCommandLineElements.isEmpty());
    }

    @Test
    public void testCreateStartLocatorCommandLine() throws Exception {
        LocatorLauncher locatorLauncher = new LocatorLauncher.Builder().setMemberName("defaultLocator").setCommand(START).build();
        String[] commandLineElements = startLocatorCommand.createStartLocatorCommandLine(locatorLauncher, null, null, new Properties(), null, false, null, null, null);
        Set<String> expectedCommandLineElements = new HashSet<>();
        expectedCommandLineElements.add(StartMemberUtils.getJavaPath());
        expectedCommandLineElements.add("-server");
        expectedCommandLineElements.add("-classpath");
        expectedCommandLineElements.add(StartMemberUtils.getGemFireJarPath().concat(File.pathSeparator).concat(CORE_DEPENDENCIES_JAR_PATHNAME));
        expectedCommandLineElements.add("-Dgemfire.launcher.registerSignalHandlers=true");
        expectedCommandLineElements.add("-Djava.awt.headless=true");
        expectedCommandLineElements.add("-Dsun.rmi.dgc.server.gcInterval=9223372036854775806");
        expectedCommandLineElements.add("org.apache.geode.distributed.LocatorLauncher");
        expectedCommandLineElements.add("start");
        expectedCommandLineElements.add("defaultLocator");
        expectedCommandLineElements.add("--port=10334");
        Assert.assertNotNull(commandLineElements);
        Assert.assertTrue(((commandLineElements.length) > 0));
        Assert.assertEquals(commandLineElements.length, expectedCommandLineElements.size());
        for (String commandLineElement : commandLineElements) {
            expectedCommandLineElements.remove(commandLineElement);
        }
        Assert.assertTrue(String.format("Expected ([]); but was (%1$s)", expectedCommandLineElements), expectedCommandLineElements.isEmpty());
    }

    @Test
    public void testCreateStartLocatorCommandLineWithAllOptions() throws Exception {
        LocatorLauncher locatorLauncher = new LocatorLauncher.Builder().setCommand(START).setDebug(Boolean.TRUE).setDeletePidFileOnStop(Boolean.TRUE).setForce(Boolean.TRUE).setHostnameForClients("localhost").setMemberName("customLocator").setPort(10101).setRedirectOutput(Boolean.TRUE).build();
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
        String[] commandLineElements = startLocatorCommand.createStartLocatorCommandLine(locatorLauncher, gemfirePropertiesFile, gemfireSecurityPropertiesFile, gemfireProperties, customClasspath, Boolean.FALSE, jvmArguments, heapSize, heapSize);
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
        expectedCommandLineElements.add("org.apache.geode.distributed.LocatorLauncher");
        expectedCommandLineElements.add("start");
        expectedCommandLineElements.add("customLocator");
        expectedCommandLineElements.add("--debug");
        expectedCommandLineElements.add("--force");
        expectedCommandLineElements.add("--hostname-for-clients=localhost");
        expectedCommandLineElements.add("--port=10101");
        expectedCommandLineElements.add("--redirect-output");
        Assert.assertNotNull(commandLineElements);
        Assert.assertTrue(((commandLineElements.length) > 0));
        assertThat(commandLineElements).containsAll(expectedCommandLineElements);
    }
}

