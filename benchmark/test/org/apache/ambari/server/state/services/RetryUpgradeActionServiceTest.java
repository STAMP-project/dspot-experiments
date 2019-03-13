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
package org.apache.ambari.server.state.services;


import HostRoleStatus.HOLDING_FAILED;
import HostRoleStatus.HOLDING_TIMEDOUT;
import HostRoleStatus.PENDING;
import Role.ZOOKEEPER_SERVER;
import RoleCommand.RESTART;
import com.google.inject.Injector;
import java.util.List;
import org.apache.ambari.server.actionmanager.HostRoleStatus;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.RequestDAO;
import org.apache.ambari.server.orm.dao.StageDAO;
import org.apache.ambari.server.orm.dao.UpgradeDAO;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.StackEntity;
import org.apache.ambari.server.orm.entities.StageEntity;
import org.apache.ambari.server.orm.entities.StageEntityPK;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link org.apache.ambari.server.state.services.RetryUpgradeActionService}.
 */
public class RetryUpgradeActionServiceTest {
    private Injector injector;

    private Clusters clusters;

    private RepositoryVersionDAO repoVersionDAO;

    private UpgradeDAO upgradeDAO;

    private RequestDAO requestDAO;

    private StageDAO stageDAO;

    private HostRoleCommandDAO hostRoleCommandDAO;

    private OrmTestHelper helper;

    // Instance variables shared by all tests
    String clusterName = "c1";

    Cluster cluster;

    StackId stack220 = new StackId("HDP-2.2.0");

    StackEntity stackEntity220;

    Long upgradeRequestId = 1L;

    Long stageId = 1L;

    /**
     * Test the gauva service allows retrying certain failed actions during a stack upgrade.
     * Case 1: No cluster => no-op
     * Case 2: Cluster and valid timeout, but no active upgrade => no-op
     * Case 3: Cluster with an active upgrade, but no HOLDING_FAILED|HOLDING_TIMEDOUT commands => no-op
     * Case 4: Cluster with an active upgrade that contains a failed task in HOLDING_FAILED that
     * does NOT meet conditions to be retried => no-op
     * Case 5: Cluster with an active upgrade that contains a failed task in HOLDING_FAILED that
     * DOES meet conditions to be retried and has values for start time and original start time => retries the task
     * * Case 6: Cluster with an active upgrade that contains a failed task in HOLDING_TIMEDOUT that
     * DOES meet conditions to be retriedand does not have values for start time or original start time => retries the task
     * Case 7: Cluster with an active upgrade that contains a failed task in HOLDING_FAILED that
     * was already retried and has now expired => no-op
     * Case 8: Cluster with an active upgrade that contains a failed task in HOLDING_FAILED, but it is a critical task
     * during Finalize Cluster, which should not be retried => no-op
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test() throws Exception {
        int timeoutMins = 1;
        RetryUpgradeActionService service = injector.getInstance(RetryUpgradeActionService.class);
        service.startUp();
        // Case 1: No cluster
        service.runOneIteration();
        // Case 2: Cluster and valid timeout, but no active upgrade
        createCluster();
        service.setMaxTimeout(timeoutMins);
        service.runOneIteration();
        // Case 3: Cluster with an active upgrade, but no HOLDING_FAILED|HOLDING_TIMEDOUT commands.
        prepareUpgrade();
        // Run the service
        service.runOneIteration();
        // Assert all commands in PENDING
        List<HostRoleCommandEntity> commands = hostRoleCommandDAO.findAll();
        Assert.assertTrue((!(commands.isEmpty())));
        for (HostRoleCommandEntity hrc : commands) {
            if ((hrc.getStatus()) == (HostRoleStatus.PENDING)) {
                Assert.fail("Did not expect any HostRoleCommands to be PENDING");
            }
        }
        // Case 4: Cluster with an active upgrade that contains a failed task in HOLDING_FAILED that does NOT meet conditions to be retried.
        StageEntityPK primaryKey = new StageEntityPK();
        primaryKey.setRequestId(upgradeRequestId);
        primaryKey.setStageId(stageId);
        StageEntity stageEntity = stageDAO.findByPK(primaryKey);
        HostRoleCommandEntity hrc2 = new HostRoleCommandEntity();
        hrc2.setStage(stageEntity);
        hrc2.setStatus(HOLDING_FAILED);
        hrc2.setRole(ZOOKEEPER_SERVER);
        hrc2.setRoleCommand(RESTART);
        hrc2.setRetryAllowed(false);
        hrc2.setAutoSkipOnFailure(false);
        stageEntity.getHostRoleCommands().add(hrc2);
        hostRoleCommandDAO.create(hrc2);
        stageDAO.merge(stageEntity);
        // Run the service
        service.runOneIteration();
        commands = hostRoleCommandDAO.findAll();
        Assert.assertTrue(((!(commands.isEmpty())) && ((commands.size()) == 2)));
        for (HostRoleCommandEntity hrc : commands) {
            if ((hrc.getStatus()) == (HostRoleStatus.PENDING)) {
                Assert.fail("Did not expect any HostRoleCommands to be PENDING");
            }
        }
        // Case 5: Cluster with an active upgrade that contains a failed task in HOLDING_FAILED that DOES meet conditions to be retried.
        long now = System.currentTimeMillis();
        hrc2.setRetryAllowed(true);
        hrc2.setOriginalStartTime(now);
        hostRoleCommandDAO.merge(hrc2);
        // Run the service
        service.runOneIteration();
        // Ensure that task 2 transitioned from HOLDING_FAILED to PENDING
        Assert.assertEquals(PENDING, hostRoleCommandDAO.findByPK(hrc2.getTaskId()).getStatus());
        // Case 6: Cluster with an active upgrade that contains a failed task in HOLDING_FAILED that DOES meet conditions to be retried.
        hrc2.setStatus(HOLDING_TIMEDOUT);
        hrc2.setRetryAllowed(true);
        hrc2.setOriginalStartTime((-1L));
        hrc2.setStartTime((-1L));
        hrc2.setLastAttemptTime((-1L));
        hrc2.setEndTime((-1L));
        hrc2.setAttemptCount(((short) (0)));
        hostRoleCommandDAO.merge(hrc2);
        // Run the service
        service.runOneIteration();
        // Ensure that task 2 transitioned from HOLDING_TIMEDOUT to PENDING
        Assert.assertEquals(PENDING, hostRoleCommandDAO.findByPK(hrc2.getTaskId()).getStatus());
        // Case 7: Cluster with an active upgrade that contains a failed task in HOLDING_FAILED that was already retried and has now expired.
        now = System.currentTimeMillis();
        hrc2.setOriginalStartTime(((now - (timeoutMins * 60000)) - 1));
        hrc2.setStatus(HOLDING_FAILED);
        hostRoleCommandDAO.merge(hrc2);
        // Run the service
        service.runOneIteration();
        Assert.assertEquals(HOLDING_FAILED, hostRoleCommandDAO.findByPK(hrc2.getTaskId()).getStatus());
        // Case 8: Cluster with an active upgrade that contains a failed task in HOLDING_FAILED, but it is a critical task
        // during Finalize Cluster, which should not be retried.
        now = System.currentTimeMillis();
        hrc2.setOriginalStartTime(now);
        hrc2.setStatus(HOLDING_FAILED);
        hrc2.setCustomCommandName("org.apache.ambari.server.serveraction.upgrades.FinalizeUpgradeAction");
        hostRoleCommandDAO.merge(hrc2);
        // Run the service
        service.runOneIteration();
        Assert.assertEquals(HOLDING_FAILED, hostRoleCommandDAO.findByPK(hrc2.getTaskId()).getStatus());
    }
}

