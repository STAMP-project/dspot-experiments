/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.cli;


import CLIManager.ALTERNATE_POM_FILE;
import CLIManager.BUILDER;
import CLIManager.THREADS;
import MavenCli.MULTIMODULE_PROJECT_DIRECTORY;
import java.io.File;
import org.apache.commons.cli.ParseException;
import org.apache.maven.Maven;
import org.apache.maven.eventspy.internal.EventSpyDispatcher;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.apache.maven.toolchain.building.ToolchainsBuildingRequest;
import org.apache.maven.toolchain.building.ToolchainsBuildingResult;
import org.codehaus.plexus.PlexusContainer;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class MavenCliTest {
    private MavenCli cli;

    private String origBasedir;

    @Test
    public void testCalculateDegreeOfConcurrencyWithCoreMultiplier() {
        int cores = Runtime.getRuntime().availableProcessors();
        // -T2.2C
        Assert.assertEquals(((int) (cores * 2.2)), cli.calculateDegreeOfConcurrencyWithCoreMultiplier("C2.2"));
        // -TC2.2
        Assert.assertEquals(((int) (cores * 2.2)), cli.calculateDegreeOfConcurrencyWithCoreMultiplier("2.2C"));
        try {
            cli.calculateDegreeOfConcurrencyWithCoreMultiplier("CXXX");
            Assert.fail("Should have failed with a NumberFormatException");
        } catch (NumberFormatException e) {
            // carry on
        }
    }

    @Test
    public void testMavenConfig() throws Exception {
        System.setProperty(MULTIMODULE_PROJECT_DIRECTORY, new File("src/test/projects/config").getCanonicalPath());
        CliRequest request = new CliRequest(new String[0], null);
        // read .mvn/maven.config
        cli.initialize(request);
        cli.cli(request);
        Assert.assertEquals("multithreaded", request.commandLine.getOptionValue(BUILDER));
        Assert.assertEquals("8", request.commandLine.getOptionValue(THREADS));
        // override from command line
        request = new CliRequest(new String[]{ "--builder", "foobar" }, null);
        cli.cli(request);
        Assert.assertEquals("foobar", request.commandLine.getOptionValue("builder"));
    }

    @Test
    public void testMavenConfigInvalid() throws Exception {
        System.setProperty(MULTIMODULE_PROJECT_DIRECTORY, new File("src/test/projects/config-illegal").getCanonicalPath());
        CliRequest request = new CliRequest(new String[0], null);
        cli.initialize(request);
        try {
            cli.cli(request);
            Assert.fail();
        } catch (ParseException expected) {
        }
    }

    /**
     * Read .mvn/maven.config with the following definitions:
     * <pre>
     *   -T 3
     *   -Drevision=1.3.0
     * </pre>
     * and check if the {@code -T 3} option can be overwritten via command line
     * argument.
     *
     * @throws Exception
     * 		in case of failure.
     */
    @Test
    public void testMVNConfigurationThreadCanBeOverwrittenViaCommandLine() throws Exception {
        System.setProperty(MULTIMODULE_PROJECT_DIRECTORY, new File("src/test/projects/mavenConfigProperties").getCanonicalPath());
        CliRequest request = new CliRequest(new String[]{ "-T", "5" }, null);
        cli.initialize(request);
        // read .mvn/maven.config
        cli.cli(request);
        Assert.assertEquals("5", request.commandLine.getOptionValue(THREADS));
    }

    /**
     * Read .mvn/maven.config with the following definitions:
     * <pre>
     *   -T 3
     *   -Drevision=1.3.0
     * </pre>
     * and check if the {@code -Drevision-1.3.0} option can be overwritten via command line
     * argument.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMVNConfigurationDefinedPropertiesCanBeOverwrittenViaCommandLine() throws Exception {
        System.setProperty(MULTIMODULE_PROJECT_DIRECTORY, new File("src/test/projects/mavenConfigProperties").getCanonicalPath());
        CliRequest request = new CliRequest(new String[]{ "-Drevision=8.1.0" }, null);
        cli.initialize(request);
        // read .mvn/maven.config
        cli.cli(request);
        cli.properties(request);
        String revision = System.getProperty("revision");
        Assert.assertEquals("8.1.0", revision);
    }

    /**
     * Read .mvn/maven.config with the following definitions:
     * <pre>
     *   -T 3
     *   -Drevision=1.3.0
     * </pre>
     * and check if the {@code -Drevision-1.3.0} option can be overwritten via command line
     * argument.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMVNConfigurationCLIRepeatedPropertiesLastWins() throws Exception {
        System.setProperty(MULTIMODULE_PROJECT_DIRECTORY, new File("src/test/projects/mavenConfigProperties").getCanonicalPath());
        CliRequest request = new CliRequest(new String[]{ "-Drevision=8.1.0", "-Drevision=8.2.0" }, null);
        cli.initialize(request);
        // read .mvn/maven.config
        cli.cli(request);
        cli.properties(request);
        String revision = System.getProperty("revision");
        Assert.assertEquals("8.2.0", revision);
    }

    /**
     * Read .mvn/maven.config with the following definitions:
     * <pre>
     *   -T 3
     *   -Drevision=1.3.0
     * </pre>
     * and check if the {@code -Drevision-1.3.0} option can be overwritten via command line argument when there are
     * funky arguments present.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMVNConfigurationFunkyArguments() throws Exception {
        System.setProperty(MULTIMODULE_PROJECT_DIRECTORY, new File("src/test/projects/mavenConfigProperties").getCanonicalPath());
        CliRequest request = new CliRequest(new String[]{ "-Drevision=8.1.0", "--file=-Dpom.xml", "\"-Dfoo=bar ", "\"-Dfoo2=bar two\"", "-Drevision=8.2.0" }, null);
        cli.initialize(request);
        // read .mvn/maven.config
        cli.cli(request);
        cli.properties(request);
        String revision = System.getProperty("revision");
        Assert.assertEquals("8.2.0", revision);
        Assert.assertEquals("bar ", request.getSystemProperties().getProperty("foo"));
        Assert.assertEquals("bar two", request.getSystemProperties().getProperty("foo2"));
        Assert.assertEquals("-Dpom.xml", request.getCommandLine().getOptionValue(ALTERNATE_POM_FILE));
    }

    @Test
    public void testStyleColors() throws Exception {
        Assume.assumeTrue("ANSI not supported", MessageUtils.isColorEnabled());
        CliRequest request;
        MessageUtils.setColorEnabled(true);
        request = new CliRequest(new String[]{ "-B" }, null);
        cli.cli(request);
        cli.properties(request);
        cli.logging(request);
        Assert.assertFalse(MessageUtils.isColorEnabled());
        MessageUtils.setColorEnabled(true);
        request = new CliRequest(new String[]{ "-l", "target/temp/mvn.log" }, null);
        cli.cli(request);
        cli.properties(request);
        cli.logging(request);
        Assert.assertFalse(MessageUtils.isColorEnabled());
        MessageUtils.setColorEnabled(false);
        request = new CliRequest(new String[]{ "-Dstyle.color=always" }, null);
        cli.cli(request);
        cli.properties(request);
        cli.logging(request);
        Assert.assertTrue(MessageUtils.isColorEnabled());
        MessageUtils.setColorEnabled(true);
        request = new CliRequest(new String[]{ "-Dstyle.color=never" }, null);
        cli.cli(request);
        cli.properties(request);
        cli.logging(request);
        Assert.assertFalse(MessageUtils.isColorEnabled());
        MessageUtils.setColorEnabled(false);
        request = new CliRequest(new String[]{ "-Dstyle.color=always", "-B", "-l", "target/temp/mvn.log" }, null);
        cli.cli(request);
        cli.properties(request);
        cli.logging(request);
        Assert.assertTrue(MessageUtils.isColorEnabled());
        try {
            MessageUtils.setColorEnabled(false);
            request = new CliRequest(new String[]{ "-Dstyle.color=maybe", "-B", "-l", "target/temp/mvn.log" }, null);
            cli.cli(request);
            cli.properties(request);
            cli.logging(request);
            Assert.fail("maybe is not a valid option");
        } catch (IllegalArgumentException e) {
            // noop
        }
    }

    /**
     * Verifies MNG-6558
     */
    @Test
    public void testToolchainsBuildingEvents() throws Exception {
        final EventSpyDispatcher eventSpyDispatcherMock = Mockito.mock(EventSpyDispatcher.class);
        MavenCli customizedMavenCli = new MavenCli() {
            @Override
            protected void customizeContainer(PlexusContainer container) {
                super.customizeContainer(container);
                container.addComponent(eventSpyDispatcherMock, "org.apache.maven.eventspy.internal.EventSpyDispatcher");
                container.addComponent(Mockito.mock(Maven.class), "org.apache.maven.Maven");
            }
        };
        CliRequest cliRequest = new CliRequest(new String[]{  }, null);
        customizedMavenCli.cli(cliRequest);
        customizedMavenCli.logging(cliRequest);
        customizedMavenCli.container(cliRequest);
        customizedMavenCli.toolchains(cliRequest);
        InOrder orderdEventSpyDispatcherMock = Mockito.inOrder(eventSpyDispatcherMock);
        orderdEventSpyDispatcherMock.verify(eventSpyDispatcherMock, Mockito.times(1)).onEvent(ArgumentMatchers.any(ToolchainsBuildingRequest.class));
        orderdEventSpyDispatcherMock.verify(eventSpyDispatcherMock, Mockito.times(1)).onEvent(ArgumentMatchers.any(ToolchainsBuildingResult.class));
    }
}

