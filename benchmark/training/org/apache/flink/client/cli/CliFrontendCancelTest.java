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


import java.util.Collections;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.cli.util.MockedCliFrontend;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the CANCEL command.
 */
public class CliFrontendCancelTest extends CliFrontendTestBase {
    @Test
    public void testCancel() throws Exception {
        // test cancel properly
        JobID jid = new JobID();
        String[] parameters = new String[]{ jid.toString() };
        final ClusterClient<String> clusterClient = CliFrontendCancelTest.createClusterClient();
        MockedCliFrontend testFrontend = new MockedCliFrontend(clusterClient);
        cancel(parameters);
        Mockito.verify(clusterClient, Mockito.times(1)).cancel(ArgumentMatchers.any(JobID.class));
    }

    @Test(expected = CliArgsException.class)
    public void testMissingJobId() throws Exception {
        String[] parameters = new String[]{  };
        Configuration configuration = getConfiguration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.cancel(parameters);
    }

    @Test(expected = CliArgsException.class)
    public void testUnrecognizedOption() throws Exception {
        String[] parameters = new String[]{ "-v", "-l" };
        Configuration configuration = getConfiguration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.cancel(parameters);
    }

    /**
     * Tests cancelling with the savepoint option.
     */
    @Test
    public void testCancelWithSavepoint() throws Exception {
        {
            // Cancel with savepoint (no target directory)
            JobID jid = new JobID();
            String[] parameters = new String[]{ "-s", jid.toString() };
            final ClusterClient<String> clusterClient = CliFrontendCancelTest.createClusterClient();
            MockedCliFrontend testFrontend = new MockedCliFrontend(clusterClient);
            cancel(parameters);
            Mockito.verify(clusterClient, Mockito.times(1)).cancelWithSavepoint(ArgumentMatchers.any(JobID.class), ArgumentMatchers.isNull(String.class));
        }
        {
            // Cancel with savepoint (with target directory)
            JobID jid = new JobID();
            String[] parameters = new String[]{ "-s", "targetDirectory", jid.toString() };
            final ClusterClient<String> clusterClient = CliFrontendCancelTest.createClusterClient();
            MockedCliFrontend testFrontend = new MockedCliFrontend(clusterClient);
            cancel(parameters);
            Mockito.verify(clusterClient, Mockito.times(1)).cancelWithSavepoint(ArgumentMatchers.any(JobID.class), ArgumentMatchers.notNull(String.class));
        }
    }

    @Test(expected = CliArgsException.class)
    public void testCancelWithSavepointWithoutJobId() throws Exception {
        // Cancel with savepoint (with target directory), but no job ID
        String[] parameters = new String[]{ "-s", "targetDirectory" };
        Configuration configuration = getConfiguration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.cancel(parameters);
    }

    @Test(expected = CliArgsException.class)
    public void testCancelWithSavepointWithoutParameters() throws Exception {
        // Cancel with savepoint (no target directory) and no job ID
        String[] parameters = new String[]{ "-s" };
        Configuration configuration = getConfiguration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.cancel(parameters);
    }
}

