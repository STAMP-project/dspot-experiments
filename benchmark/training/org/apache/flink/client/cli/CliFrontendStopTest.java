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
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the STOP command.
 */
public class CliFrontendStopTest extends CliFrontendTestBase {
    @Test
    public void testStop() throws Exception {
        // test stop properly
        JobID jid = new JobID();
        String jidString = jid.toString();
        String[] parameters = new String[]{ jidString };
        final ClusterClient<String> clusterClient = CliFrontendStopTest.createClusterClient(null);
        MockedCliFrontend testFrontend = new MockedCliFrontend(clusterClient);
        stop(parameters);
        Mockito.verify(clusterClient, Mockito.times(1)).stop(ArgumentMatchers.any(JobID.class));
    }

    @Test(expected = CliArgsException.class)
    public void testUnrecognizedOption() throws Exception {
        // test unrecognized option
        String[] parameters = new String[]{ "-v", "-l" };
        Configuration configuration = getConfiguration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.stop(parameters);
    }

    @Test(expected = CliArgsException.class)
    public void testMissingJobId() throws Exception {
        // test missing job id
        String[] parameters = new String[]{  };
        Configuration configuration = getConfiguration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.stop(parameters);
    }

    @Test
    public void testUnknownJobId() throws Exception {
        // test unknown job Id
        JobID jid = new JobID();
        String[] parameters = new String[]{ jid.toString() };
        String expectedMessage = "Test exception";
        FlinkException testException = new FlinkException(expectedMessage);
        final ClusterClient<String> clusterClient = CliFrontendStopTest.createClusterClient(testException);
        MockedCliFrontend testFrontend = new MockedCliFrontend(clusterClient);
        try {
            stop(parameters);
            Assert.fail("Should have failed.");
        } catch (FlinkException e) {
            Assert.assertTrue(ExceptionUtils.findThrowableWithMessage(e, expectedMessage).isPresent());
        }
    }
}

