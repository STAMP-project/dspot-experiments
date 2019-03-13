/**
 * -
 * -\-\-
 * Helios Tools
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.cli;


import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class CliParserTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String SUBCOMMAND = "jobs";

    private static final String[] ENDPOINTS = new String[]{ "http://master-a1.nyc.com:80", "http://master-a2.nyc.com:80", "http://master-a3.nyc.com:80" };

    private static final String[] DOMAINS = new String[]{ "foo", "bar", "baz" };

    private static final String SERVICE = "foo-service";

    private static final String SRV = "helios";

    private final ImmutableList<String> singleEndpointArgs = ImmutableList.of(CliParserTest.SUBCOMMAND, "--master", CliParserTest.ENDPOINTS[0], CliParserTest.SERVICE);

    @Test
    public void testComputeTargetsSingleEndpoint() throws Exception {
        final CliParser cliParser = new CliParser(CliParserTest.toArray(singleEndpointArgs));
        final List<Target> targets = cliParser.getTargets();
        // We expect the specified master endpoint target
        final List<Target> expectedTargets = ImmutableList.of(Target.from(URI.create(CliParserTest.ENDPOINTS[0])));
        Assert.assertEquals(expectedTargets, targets);
    }

    @Test
    public void testComputeTargetsMultipleEndpoints() throws Exception {
        final List<String> argsList = Lists.newArrayList(CliParserTest.SUBCOMMAND, "-d", Joiner.on(",").join(CliParserTest.DOMAINS));
        for (final String endpoint : CliParserTest.ENDPOINTS) {
            argsList.add("--master");
            argsList.add(endpoint);
        }
        argsList.add(CliParserTest.SERVICE);
        final CliParser cliParser = new CliParser(CliParserTest.toArray(argsList));
        final List<Target> targets = cliParser.getTargets();
        // We expect only the specified master endpoint targets since they take precedence over domains
        final List<Target> expectedTargets = Lists.newArrayListWithExpectedSize(CliParserTest.ENDPOINTS.length);
        for (final String endpoint : CliParserTest.ENDPOINTS) {
            expectedTargets.add(Target.from(URI.create(endpoint)));
        }
        Assert.assertEquals(expectedTargets, targets);
    }

    @Test
    public void testComputeTargetsSingleDomain() throws Exception {
        final String[] args = new String[]{ CliParserTest.SUBCOMMAND, "-d", CliParserTest.DOMAINS[0], CliParserTest.SERVICE };
        final CliParser cliParser = new CliParser(args);
        final List<Target> targets = cliParser.getTargets();
        // We expect the specified domain
        final List<Target> expectedTargets = Target.from(CliParserTest.SRV, ImmutableList.of(CliParserTest.DOMAINS[0]));
        Assert.assertEquals(expectedTargets, targets);
    }

    @Test
    public void testComputeTargetsMultiDomain() throws Exception {
        final String[] args = new String[]{ CliParserTest.SUBCOMMAND, "-d", Joiner.on(",").join(CliParserTest.DOMAINS), CliParserTest.SERVICE };
        final CliParser cliParser = new CliParser(args);
        final List<Target> targets = cliParser.getTargets();
        // We expect the specified domains
        final List<Target> expectedTargets = Target.from(CliParserTest.SRV, Lists.newArrayList(CliParserTest.DOMAINS));
        Assert.assertEquals(expectedTargets, targets);
    }

    @Test
    public void testComputeTargetsMultipleEndpointsFromConfig() throws Exception {
        final String[] args = new String[]{ CliParserTest.SUBCOMMAND, CliParserTest.SERVICE };
        // Create a "~/.helios/config" file, which is the path CliConfig reads by default
        final File configDir = temporaryFolder.newFolder(CliConfig.getConfigDirName());
        final File configFile = new File((((configDir.getAbsolutePath()) + (File.separator)) + (CliConfig.getConfigFileName())));
        // Write configuration to that file
        try (final FileOutputStream outFile = new FileOutputStream(configFile)) {
            final ByteBuffer byteBuffer = Charsets.UTF_8.encode((((((((("{\"masterEndpoints\":[\"" + (CliParserTest.ENDPOINTS[0])) + "\", \"") + (CliParserTest.ENDPOINTS[1])) + "\", \"") + (CliParserTest.ENDPOINTS[2])) + "\"], \"domains\":[\"") + (CliParserTest.DOMAINS[0])) + "\"]}"));
            outFile.write(byteBuffer.array(), 0, byteBuffer.remaining());
            // Set user's home directory to this temporary folder
            System.setProperty("user.home", temporaryFolder.getRoot().getAbsolutePath());
            final CliParser cliParser = new CliParser(args);
            final List<Target> targets = cliParser.getTargets();
            // We expect only the specified master endpoint targets since they take precedence over
            // domains
            final List<Target> expectedTargets = ImmutableList.of(Target.from(URI.create(CliParserTest.ENDPOINTS[0])), Target.from(URI.create(CliParserTest.ENDPOINTS[1])), Target.from(URI.create(CliParserTest.ENDPOINTS[2])));
            Assert.assertEquals(expectedTargets, targets);
        }
    }

    @Test
    public void testComputeTargetsMultipleDomainsFromConfig() throws Exception {
        final String[] args = new String[]{ CliParserTest.SUBCOMMAND, CliParserTest.SERVICE };
        // Create a "~/.helios/config" file, which is the path CliConfig reads by default
        final File configDir = temporaryFolder.newFolder(CliConfig.getConfigDirName());
        final File configFile = new File((((configDir.getAbsolutePath()) + (File.separator)) + (CliConfig.getConfigFileName())));
        // Write configuration to that file
        try (final FileOutputStream outFile = new FileOutputStream(configFile)) {
            final ByteBuffer byteBuffer = Charsets.UTF_8.encode((((((("{\"domains\":[\"" + (CliParserTest.DOMAINS[0])) + "\", \"") + (CliParserTest.DOMAINS[1])) + "\", \"") + (CliParserTest.DOMAINS[2])) + "\"]}"));
            outFile.write(byteBuffer.array(), 0, byteBuffer.remaining());
            // Set user's home directory to this temporary folder
            System.setProperty("user.home", temporaryFolder.getRoot().getAbsolutePath());
            final CliParser cliParser = new CliParser(args);
            final List<Target> targets = cliParser.getTargets();
            // We expect the specified domains
            final List<Target> expectedTargets = Target.from(CliParserTest.SRV, ImmutableList.of(CliParserTest.DOMAINS[0], CliParserTest.DOMAINS[1], CliParserTest.DOMAINS[2]));
            Assert.assertEquals(expectedTargets, targets);
        }
    }

    @Test
    public void testInsecureHttpsDisabledByDefault() throws Exception {
        final CliParser parser = new CliParser(CliParserTest.toArray(singleEndpointArgs));
        Assert.assertFalse("GlobalArg 'insecure' should default to false", parser.getNamespace().getBoolean("insecure"));
    }

    @Test
    public void testInsecureHttpsEnable() throws Exception {
        final CliParser parser = new CliParser(CliParserTest.toArray(singleEndpointArgs, "--insecure"));
        Assert.assertTrue(parser.getNamespace().getBoolean("insecure"));
    }

    @Test
    public void testGoogleCredentialsEnabledByDefault() throws Exception {
        final CliParser parser = new CliParser(CliParserTest.toArray(singleEndpointArgs));
        Assert.assertTrue(parser.getNamespace().getBoolean("google_credentials"));
    }

    @Test
    public void testDisableGoogleCredentials() throws Exception {
        final CliParser parser = new CliParser(CliParserTest.toArray(singleEndpointArgs, "--google-credentials=false"));
        Assert.assertFalse(parser.getNamespace().getBoolean("google_credentials"));
    }
}

