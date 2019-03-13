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
package org.apache.flink.yarn.highavailability;


import java.util.Random;
import java.util.UUID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.leaderelection.LeaderContender;
import org.apache.flink.runtime.leaderelection.LeaderElectionService;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalListener;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalService;
import org.apache.flink.util.TestLogger;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for YarnIntraNonHaMasterServices.
 */
public class YarnIntraNonHaMasterServicesTest extends TestLogger {
    private static final Random RND = new Random();

    @ClassRule
    public static final TemporaryFolder TEMP_DIR = new TemporaryFolder();

    private static MiniDFSCluster hdfsCluster;

    private static Path hdfsRootPath;

    private Configuration hadoopConfig;

    // ------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------
    @Test
    public void testRepeatedClose() throws Exception {
        final Configuration flinkConfig = new Configuration();
        final YarnHighAvailabilityServices services = new YarnIntraNonHaMasterServices(flinkConfig, hadoopConfig);
        services.closeAndCleanupAllData();
        // this should not throw an exception
        services.close();
    }

    @Test
    public void testClosingReportsToLeader() throws Exception {
        final Configuration flinkConfig = new Configuration();
        try (YarnHighAvailabilityServices services = new YarnIntraNonHaMasterServices(flinkConfig, hadoopConfig)) {
            final LeaderElectionService elector = services.getResourceManagerLeaderElectionService();
            final LeaderRetrievalService retrieval = services.getResourceManagerLeaderRetriever();
            final LeaderContender contender = YarnIntraNonHaMasterServicesTest.mockContender(elector);
            final LeaderRetrievalListener listener = Mockito.mock(LeaderRetrievalListener.class);
            elector.start(contender);
            retrieval.start(listener);
            // wait until the contender has become the leader
            Mockito.verify(listener, Mockito.timeout(1000L).times(1)).notifyLeaderAddress(ArgumentMatchers.anyString(), ArgumentMatchers.any(UUID.class));
            // now we can close the election service
            services.close();
            Mockito.verify(contender, Mockito.timeout(1000L).times(1)).handleError(ArgumentMatchers.any(Exception.class));
        }
    }
}

