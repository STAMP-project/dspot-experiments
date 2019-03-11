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
 * - 3 server (quorum=2)
 * - 5 threads write 100 records on server1 and server2
 * - meanwhile after 1/3 of to-write records server3 goes in deadlock (backup), and after 2/3 of to-write records goes up.
 * - check that changes are propagated on server2
 * - deadlock-ending on server3
 * - after a while check that last
 * changes are propagated on server3.
 *
 * @author Gabriele Ponzi
 * @unknown <gabriele.ponzi--at--gmail.com>
 */
public class NodeInDeadlockScenarioIT extends AbstractScenarioTest {
    volatile boolean inserting = true;

    volatile int serverStarted = 0;

    volatile boolean backupInProgress = false;

    @Test
    public void test() throws Exception {
        maxRetries = 10;
        init(AbstractScenarioTest.SERVERS);
        prepare(false);
        // EXECUTE TESTS ONLY ON FIRST 2 NODES LEAVING NODE3 AD BACKUP ONLY REPLICA
        executeTestsOnServers = new ArrayList<ServerRun>();
        for (int i = 0; i < ((serverInstance.size()) - 1); ++i) {
            executeTestsOnServers.add(serverInstance.get(i));
        }
        execute();
    }
}

