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
import org.apache.flink.client.cli.util.MockedCliFrontend;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for the LIST command.
 */
public class CliFrontendListTest extends CliFrontendTestBase {
    @Test
    public void testListOptions() throws Exception {
        // test configure all job
        {
            String[] parameters = new String[]{ "-a" };
            ListOptions options = new ListOptions(CliFrontendParser.parse(CliFrontendParser.getListCommandOptions(), parameters, true));
            Assert.assertTrue(options.showAll());
            Assert.assertFalse(options.showRunning());
            Assert.assertFalse(options.showScheduled());
        }
        // test configure running job
        {
            String[] parameters = new String[]{ "-r" };
            ListOptions options = new ListOptions(CliFrontendParser.parse(CliFrontendParser.getListCommandOptions(), parameters, true));
            Assert.assertFalse(options.showAll());
            Assert.assertTrue(options.showRunning());
            Assert.assertFalse(options.showScheduled());
        }
        // test configure scheduled job
        {
            String[] parameters = new String[]{ "-s" };
            ListOptions options = new ListOptions(CliFrontendParser.parse(CliFrontendParser.getListCommandOptions(), parameters, true));
            Assert.assertFalse(options.showAll());
            Assert.assertFalse(options.showRunning());
            Assert.assertTrue(options.showScheduled());
        }
    }

    @Test(expected = CliArgsException.class)
    public void testUnrecognizedOption() throws Exception {
        String[] parameters = new String[]{ "-v", "-k" };
        Configuration configuration = getConfiguration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.list(parameters);
    }

    @Test
    public void testList() throws Exception {
        // test list properly
        {
            String[] parameters = new String[]{ "-r", "-s", "-a" };
            ClusterClient<String> clusterClient = CliFrontendListTest.createClusterClient();
            MockedCliFrontend testFrontend = new MockedCliFrontend(clusterClient);
            list(parameters);
            Mockito.verify(clusterClient, Mockito.times(1)).listJobs();
        }
    }
}

