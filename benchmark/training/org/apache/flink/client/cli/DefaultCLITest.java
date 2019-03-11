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
package org.apache.flink.client.cli;


import JobManagerOptions.ADDRESS;
import JobManagerOptions.PORT;
import org.apache.commons.cli.CommandLine;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.util.LeaderConnectionInfo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link DefaultCLI}.
 */
public class DefaultCLITest extends CliFrontendTestBase {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Tests that the configuration is properly passed via the DefaultCLI to the
     * created ClusterDescriptor.
     */
    @Test
    public void testConfigurationPassing() throws Exception {
        final Configuration configuration = getConfiguration();
        final String localhost = "localhost";
        final int port = 1234;
        configuration.setString(ADDRESS, localhost);
        configuration.setInteger(PORT, port);
        @SuppressWarnings("unchecked")
        final AbstractCustomCommandLine<StandaloneClusterId> defaultCLI = ((AbstractCustomCommandLine<StandaloneClusterId>) (CliFrontendTestBase.getCli(configuration)));
        final String[] args = new String[]{  };
        CommandLine commandLine = defaultCLI.parseCommandLineOptions(args, false);
        final ClusterDescriptor<StandaloneClusterId> clusterDescriptor = defaultCLI.createClusterDescriptor(commandLine);
        final ClusterClient<?> clusterClient = clusterDescriptor.retrieve(defaultCLI.getClusterId(commandLine));
        final LeaderConnectionInfo clusterConnectionInfo = clusterClient.getClusterConnectionInfo();
        Assert.assertThat(clusterConnectionInfo.getHostname(), Matchers.equalTo(localhost));
        Assert.assertThat(clusterConnectionInfo.getPort(), Matchers.equalTo(port));
    }

    /**
     * Tests that command line options override the configuration settings.
     */
    @Test
    public void testManualConfigurationOverride() throws Exception {
        final String localhost = "localhost";
        final int port = 1234;
        final Configuration configuration = getConfiguration();
        configuration.setString(ADDRESS, localhost);
        configuration.setInteger(PORT, port);
        @SuppressWarnings("unchecked")
        final AbstractCustomCommandLine<StandaloneClusterId> defaultCLI = ((AbstractCustomCommandLine<StandaloneClusterId>) (CliFrontendTestBase.getCli(configuration)));
        final String manualHostname = "123.123.123.123";
        final int manualPort = 4321;
        final String[] args = new String[]{ "-m", (manualHostname + ':') + manualPort };
        CommandLine commandLine = defaultCLI.parseCommandLineOptions(args, false);
        final ClusterDescriptor<StandaloneClusterId> clusterDescriptor = defaultCLI.createClusterDescriptor(commandLine);
        final ClusterClient<?> clusterClient = clusterDescriptor.retrieve(defaultCLI.getClusterId(commandLine));
        final LeaderConnectionInfo clusterConnectionInfo = clusterClient.getClusterConnectionInfo();
        Assert.assertThat(clusterConnectionInfo.getHostname(), Matchers.equalTo(manualHostname));
        Assert.assertThat(clusterConnectionInfo.getPort(), Matchers.equalTo(manualPort));
    }
}

