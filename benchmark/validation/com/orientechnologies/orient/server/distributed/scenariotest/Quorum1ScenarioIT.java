/**
 * Copyright 2015 OrientDB LTD (info--at--orientdb.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.server.distributed.scenariotest;


import com.orientechnologies.orient.server.distributed.AbstractServerClusterInsertTest;
import com.orientechnologies.orient.server.distributed.AbstractServerClusterTest;
import com.orientechnologies.orient.server.distributed.ServerRun;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


/**
 * It checks the consistency in the cluster with the following scenario:
 * - 3 server (quorum=1)
 * - server3 is isolated (simulated by shutdown)
 * - 5 threads on both server1 and server2 write 100 records
 * - server3 joins the cluster
 * - server3 receive the delta from the cluster
 * - check consistency
 *
 * @author Gabriele Ponzi
 * @unknown <gabriele.ponzi--at--gmail.com>
 */
public class Quorum1ScenarioIT extends AbstractScenarioTest {
    @Test
    public void test() throws Exception {
        useTransactions = true;
        maxRetries = 10;
        init(AbstractScenarioTest.SERVERS);
        prepare(false);
        // execute writes only on server1 and server2
        executeTestsOnServers = new ArrayList<ServerRun>();
        executeTestsOnServers.add(serverInstance.get(0));
        executeTestsOnServers.add(serverInstance.get(1));
        execute();
    }
}

