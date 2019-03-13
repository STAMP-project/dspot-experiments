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
package org.apache.hadoop.yarn.server.resourcemanager.recovery;


import org.apache.curator.test.TestingServer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.security.AMRMTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestZKRMStateStorePerf extends RMStateStoreTestBase implements Tool {
    public static final Logger LOG = LoggerFactory.getLogger(TestZKRMStateStore.class);

    final String version = "0.1";

    // Configurable variables for performance test
    private int ZK_PERF_NUM_APP_DEFAULT = 1000;

    private int ZK_PERF_NUM_APPATTEMPT_PER_APP = 10;

    private final long clusterTimeStamp = 1352994193343L;

    private static final String USAGE = (((("Usage: " + (TestZKRMStateStorePerf.class.getSimpleName())) + " -appSize numberOfApplications") + " -appAttemptSize numberOfApplicationAttempts") + " [-hostPort Host:Port]") + " [-workingZnode rootZnodeForTesting]\n";

    private YarnConfiguration conf = null;

    private String workingZnode = "/Test";

    private ZKRMStateStore store;

    private AMRMTokenSecretManager appTokenMgr;

    private ClientToAMTokenSecretManagerInRM clientToAMTokenMgr;

    private TestingServer curatorTestingServer;

    @Test
    public void perfZKRMStateStore() throws Exception {
        String[] args = new String[]{ "-appSize", String.valueOf(ZK_PERF_NUM_APP_DEFAULT), "-appAttemptSize", String.valueOf(ZK_PERF_NUM_APPATTEMPT_PER_APP) };
        run(args);
    }
}

