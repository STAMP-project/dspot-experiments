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
package org.apache.ambari.server.actionmanager;


import BaseRequest.DEFAULT_PAGE_SIZE;
import HostRoleStatus.ABORTED;
import HostRoleStatus.COMPLETED;
import HostRoleStatus.HOLDING_TIMEDOUT;
import HostRoleStatus.IN_PROGRESS;
import HostRoleStatus.PENDING;
import HostRoleStatus.QUEUED;
import HostRoleStatus.TIMEDOUT;
import Role.AMBARI_SERVER_ACTION;
import Role.HBASE_MASTER;
import Role.HBASE_REGIONSERVER;
import RoleCommand.START;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.orm.DBAccessor;
import org.apache.ambari.server.orm.DBAccessorImpl;
import org.apache.ambari.server.orm.dao.ExecutionCommandDAO;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.serveraction.MockServerAction;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.utils.CommandUtils;
import org.apache.ambari.server.utils.StageUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestActionDBAccessorImpl {
    private static final Logger log = LoggerFactory.getLogger(TestActionDBAccessorImpl.class);

    private long requestId = 23;

    private long stageId = 31;

    private String hostName = "host1";

    private String clusterName = "cluster1";

    private String actionName = "validate_kerberos";

    private String serverHostName = StageUtils.getHostName();// "_localhost_";


    private String serverActionName = MockServerAction.class.getName();

    private Injector injector;

    ActionDBAccessor db;

    ActionManager am;

    @Inject
    private Clusters clusters;

    @Inject
    private ExecutionCommandDAO executionCommandDAO;

    @Inject
    private HostRoleCommandDAO hostRoleCommandDAO;

    @Inject
    private StageFactory stageFactory;

    @Test
    public void testActionResponse() throws AmbariException {
        String hostname = "host1";
        populateActionDB(db, hostname, requestId, stageId, false);
        Stage stage = db.getAllStages(requestId).get(0);
        Assert.assertEquals(stageId, stage.getStageId());
        stage.setHostRoleStatus(hostname, "HBASE_MASTER", QUEUED);
        db.hostRoleScheduled(stage, hostname, "HBASE_MASTER");
        List<CommandReport> reports = new ArrayList<>();
        CommandReport cr = new CommandReport();
        cr.setTaskId(1);
        cr.setActionId(StageUtils.getActionId(requestId, stageId));
        cr.setRole("HBASE_MASTER");
        cr.setStatus("COMPLETED");
        cr.setStdErr("");
        cr.setStdOut("");
        cr.setExitCode(215);
        reports.add(cr);
        am.processTaskResponse(hostname, reports, CommandUtils.convertToTaskIdCommandMap(stage.getOrderedHostRoleCommands()));
        am.processTaskResponse(hostname, reports, CommandUtils.convertToTaskIdCommandMap(stage.getOrderedHostRoleCommands()));
        Assert.assertEquals(215, am.getAction(requestId, stageId).getExitCode(hostname, "HBASE_MASTER"));
        Assert.assertEquals(COMPLETED, am.getAction(requestId, stageId).getHostRoleStatus(hostname, "HBASE_MASTER"));
        Stage s = db.getAllStages(requestId).get(0);
        Assert.assertEquals(COMPLETED, s.getHostRoleStatus(hostname, "HBASE_MASTER"));
    }

    @Test
    public void testCancelCommandReport() throws AmbariException {
        String hostname = "host1";
        populateActionDB(db, hostname, requestId, stageId, false);
        Stage stage = db.getAllStages(requestId).get(0);
        Assert.assertEquals(stageId, stage.getStageId());
        stage.setHostRoleStatus(hostname, "HBASE_MASTER", ABORTED);
        db.hostRoleScheduled(stage, hostname, "HBASE_MASTER");
        List<CommandReport> reports = new ArrayList<>();
        CommandReport cr = new CommandReport();
        cr.setTaskId(1);
        cr.setActionId(StageUtils.getActionId(requestId, stageId));
        cr.setRole("HBASE_MASTER");
        cr.setStatus("COMPLETED");
        cr.setStdErr("");
        cr.setStdOut("");
        cr.setExitCode(0);
        reports.add(cr);
        am.processTaskResponse(hostname, reports, CommandUtils.convertToTaskIdCommandMap(stage.getOrderedHostRoleCommands()));
        Assert.assertEquals(0, am.getAction(requestId, stageId).getExitCode(hostname, "HBASE_MASTER"));
        Assert.assertEquals(("HostRoleStatus should remain ABORTED " + "(command report status should be ignored)"), ABORTED, am.getAction(requestId, stageId).getHostRoleStatus(hostname, "HBASE_MASTER"));
        Stage s = db.getAllStages(requestId).get(0);
        Assert.assertEquals(("HostRoleStatus should remain ABORTED " + "(command report status should be ignored)"), ABORTED, s.getHostRoleStatus(hostname, "HBASE_MASTER"));
    }

    @Test
    public void testGetStagesInProgress() throws AmbariException {
        List<Stage> stages = new ArrayList<>();
        stages.add(createStubStage(hostName, requestId, stageId, false));
        stages.add(createStubStage(hostName, requestId, ((stageId) + 1), false));
        Request request = new Request(stages, "", clusters);
        db.persistActions(request);
        Assert.assertEquals(2, stages.size());
    }

    @Test
    public void testGetStagesInProgressWithFailures() throws AmbariException {
        populateActionDB(db, hostName, requestId, stageId, false);
        populateActionDB(db, hostName, ((requestId) + 1), stageId, false);
        List<Stage> stages = db.getFirstStageInProgressPerRequest();
        Assert.assertEquals(2, stages.size());
        db.abortOperation(requestId);
        stages = db.getFirstStageInProgressPerRequest();
        Assert.assertEquals(1, stages.size());
        Assert.assertEquals(((requestId) + 1), stages.get(0).getRequestId());
    }

    @Test
    public void testGetStagesInProgressWithManyStages() throws AmbariException {
        // create 3 request; each request will have 3 stages, each stage 2 commands
        populateActionDBMultipleStages(3, db, hostName, requestId, stageId);
        populateActionDBMultipleStages(3, db, hostName, ((requestId) + 1), ((stageId) + 3));
        populateActionDBMultipleStages(3, db, hostName, ((requestId) + 2), ((stageId) + 3));
        // verify stages and proper ordering
        int commandsInProgressCount = db.getCommandsInProgressCount();
        List<Stage> stages = db.getFirstStageInProgressPerRequest();
        Assert.assertEquals(18, commandsInProgressCount);
        Assert.assertEquals(3, stages.size());
        long lastRequestId = Integer.MIN_VALUE;
        for (Stage stage : stages) {
            Assert.assertTrue(((stage.getRequestId()) >= lastRequestId));
            lastRequestId = stage.getRequestId();
        }
        // cancel the first one, removing 3 stages
        db.abortOperation(requestId);
        // verify stages and proper ordering
        commandsInProgressCount = db.getCommandsInProgressCount();
        stages = db.getFirstStageInProgressPerRequest();
        Assert.assertEquals(12, commandsInProgressCount);
        Assert.assertEquals(2, stages.size());
        // find the first stage, and change one command to COMPLETED
        stages.get(0).setHostRoleStatus(hostName, HBASE_MASTER.toString(), COMPLETED);
        db.hostRoleScheduled(stages.get(0), hostName, HBASE_MASTER.toString());
        // the first stage still has at least 1 command IN_PROGRESS
        commandsInProgressCount = db.getCommandsInProgressCount();
        stages = db.getFirstStageInProgressPerRequest();
        Assert.assertEquals(11, commandsInProgressCount);
        Assert.assertEquals(2, stages.size());
        // find the first stage, and change the other command to COMPLETED
        stages.get(0).setHostRoleStatus(hostName, HBASE_REGIONSERVER.toString(), COMPLETED);
        db.hostRoleScheduled(stages.get(0), hostName, HBASE_REGIONSERVER.toString());
        // verify stages and proper ordering
        commandsInProgressCount = db.getCommandsInProgressCount();
        stages = db.getFirstStageInProgressPerRequest();
        Assert.assertEquals(10, commandsInProgressCount);
        Assert.assertEquals(2, stages.size());
    }

    @Test
    public void testGetStagesInProgressWithManyCommands() throws AmbariException {
        // 1000 hosts
        for (int i = 0; i < 1000; i++) {
            String hostName = "c64-" + i;
            clusters.addHost(hostName);
        }
        // create 1 request, 3 stages per host, each with 2 commands
        int requestCount = 1000;
        for (int i = 0; i < requestCount; i++) {
            String hostName = "c64-" + i;
            populateActionDBMultipleStages(3, db, hostName, ((requestId) + i), stageId);
        }
        int commandsInProgressCount = db.getCommandsInProgressCount();
        List<Stage> stages = db.getFirstStageInProgressPerRequest();
        Assert.assertEquals(6000, commandsInProgressCount);
        Assert.assertEquals(requestCount, stages.size());
    }

    @Test
    public void testPersistActions() throws AmbariException {
        populateActionDB(db, hostName, requestId, stageId, false);
        for (Stage stage : db.getAllStages(requestId)) {
            TestActionDBAccessorImpl.log.info(("taskId={}" + (stage.getExecutionCommands(hostName).get(0).getExecutionCommand().getTaskId())));
            Assert.assertTrue(((stage.getExecutionCommands(hostName).get(0).getExecutionCommand().getTaskId()) > 0));
            Assert.assertTrue(((executionCommandDAO.findByPK(stage.getExecutionCommands(hostName).get(0).getExecutionCommand().getTaskId())) != null));
        }
    }

    @Test
    public void testHostRoleScheduled() throws InterruptedException, AmbariException {
        populateActionDB(db, hostName, requestId, stageId, false);
        Stage stage = db.getStage(StageUtils.getActionId(requestId, stageId));
        Assert.assertEquals(PENDING, stage.getHostRoleStatus(hostName, HBASE_MASTER.toString()));
        List<HostRoleCommandEntity> entities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, HBASE_MASTER.toString());
        Assert.assertEquals(PENDING, entities.get(0).getStatus());
        stage.setHostRoleStatus(hostName, HBASE_MASTER.toString(), QUEUED);
        entities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, HBASE_MASTER.toString());
        Assert.assertEquals(QUEUED, stage.getHostRoleStatus(hostName, HBASE_MASTER.toString()));
        Assert.assertEquals(PENDING, entities.get(0).getStatus());
        db.hostRoleScheduled(stage, hostName, HBASE_MASTER.toString());
        entities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, HBASE_MASTER.toString());
        Assert.assertEquals(QUEUED, entities.get(0).getStatus());
        Thread thread = new Thread() {
            @Override
            public void run() {
                Stage stage1 = db.getStage("23-31");
                stage1.setHostRoleStatus(hostName, HBASE_MASTER.toString(), COMPLETED);
                db.hostRoleScheduled(stage1, hostName, HBASE_MASTER.toString());
                injector.getInstance(EntityManager.class).clear();
            }
        };
        thread.start();
        thread.join();
        injector.getInstance(EntityManager.class).clear();
        entities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, HBASE_MASTER.toString());
        Assert.assertEquals("Concurrent update failed", COMPLETED, entities.get(0).getStatus());
    }

    @Test
    public void testCustomActionScheduled() throws InterruptedException, AmbariException {
        populateActionDBWithCustomAction(db, hostName, requestId, stageId);
        Stage stage = db.getStage(StageUtils.getActionId(requestId, stageId));
        Assert.assertEquals(PENDING, stage.getHostRoleStatus(hostName, actionName));
        List<HostRoleCommandEntity> entities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, actionName);
        Assert.assertEquals(PENDING, entities.get(0).getStatus());
        stage.setHostRoleStatus(hostName, actionName, QUEUED);
        entities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, actionName);
        Assert.assertEquals(QUEUED, stage.getHostRoleStatus(hostName, actionName));
        Assert.assertEquals(PENDING, entities.get(0).getStatus());
        db.hostRoleScheduled(stage, hostName, actionName);
        entities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, actionName);
        Assert.assertEquals(QUEUED, entities.get(0).getStatus());
        Thread thread = new Thread() {
            @Override
            public void run() {
                Stage stage1 = db.getStage("23-31");
                stage1.setHostRoleStatus(hostName, actionName, COMPLETED);
                db.hostRoleScheduled(stage1, hostName, actionName);
                injector.getInstance(EntityManager.class).clear();
            }
        };
        thread.start();
        thread.join();
        injector.getInstance(EntityManager.class).clear();
        entities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, actionName);
        Assert.assertEquals("Concurrent update failed", COMPLETED, entities.get(0).getStatus());
    }

    @Test
    public void testServerActionScheduled() throws InterruptedException, AmbariException {
        populateActionDBWithServerAction(db, null, requestId, stageId);
        final String roleName = AMBARI_SERVER_ACTION.toString();
        Stage stage = db.getStage(StageUtils.getActionId(requestId, stageId));
        Assert.assertEquals(PENDING, stage.getHostRoleStatus(null, roleName));
        List<HostRoleCommandEntity> entities = hostRoleCommandDAO.findByHostRole(null, requestId, stageId, roleName);
        Assert.assertEquals(PENDING, entities.get(0).getStatus());
        stage.setHostRoleStatus(null, roleName, QUEUED);
        entities = hostRoleCommandDAO.findByHostRole(null, requestId, stageId, roleName);
        Assert.assertEquals(QUEUED, stage.getHostRoleStatus(null, roleName));
        Assert.assertEquals(PENDING, entities.get(0).getStatus());
        db.hostRoleScheduled(stage, null, roleName);
        entities = hostRoleCommandDAO.findByHostRole(null, requestId, stageId, roleName);
        Assert.assertEquals(QUEUED, entities.get(0).getStatus());
        Thread thread = new Thread() {
            @Override
            public void run() {
                Stage stage1 = db.getStage("23-31");
                stage1.setHostRoleStatus(null, roleName, COMPLETED);
                db.hostRoleScheduled(stage1, null, roleName);
                injector.getInstance(EntityManager.class).clear();
            }
        };
        thread.start();
        thread.join();
        injector.getInstance(EntityManager.class).clear();
        entities = hostRoleCommandDAO.findByHostRole(null, requestId, stageId, roleName);
        Assert.assertEquals("Concurrent update failed", COMPLETED, entities.get(0).getStatus());
    }

    @Test
    public void testUpdateHostRole() throws Exception {
        populateActionDB(db, hostName, requestId, stageId, false);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50000; i++) {
            sb.append("1234567890");
        }
        String largeString = sb.toString();
        CommandReport commandReport = new CommandReport();
        commandReport.setStatus(COMPLETED.toString());
        commandReport.setStdOut(largeString);
        commandReport.setStdErr(largeString);
        commandReport.setStructuredOut(largeString);
        commandReport.setExitCode(123);
        db.updateHostRoleState(hostName, requestId, stageId, HBASE_MASTER.toString(), commandReport);
        List<HostRoleCommandEntity> commandEntities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, HBASE_MASTER.toString());
        Assert.assertEquals(1, commandEntities.size());
        HostRoleCommandEntity commandEntity = commandEntities.get(0);
        HostRoleCommand command = db.getTask(commandEntity.getTaskId());
        Assert.assertNotNull(command);
        Assert.assertEquals(largeString, command.getStdout());
        Assert.assertEquals(largeString, command.getStructuredOut());
        // endTime for completed commands should be set
        Assert.assertTrue(((command.getEndTime()) != (-1)));
    }

    @Test
    public void testUpdateHostRoleTimeoutRetry() throws Exception {
        populateActionDB(db, hostName, requestId, stageId, true);
        CommandReport commandReport = new CommandReport();
        commandReport.setStatus(TIMEDOUT.toString());
        commandReport.setStdOut("");
        commandReport.setStdErr("");
        commandReport.setStructuredOut("");
        commandReport.setExitCode(123);
        db.updateHostRoleState(hostName, requestId, stageId, HBASE_MASTER.toString(), commandReport);
        List<HostRoleCommandEntity> commandEntities = hostRoleCommandDAO.findByHostRole(hostName, requestId, stageId, HBASE_MASTER.toString());
        HostRoleCommandEntity commandEntity = commandEntities.get(0);
        HostRoleCommand command = db.getTask(commandEntity.getTaskId());
        Assert.assertNotNull(command);
        Assert.assertEquals(HOLDING_TIMEDOUT, command.getStatus());
    }

    @Test
    public void testGetRequestsByStatus() throws AmbariException {
        List<Long> requestIds = new ArrayList<>();
        requestIds.add(((requestId) + 1));
        requestIds.add(requestId);
        populateActionDB(db, hostName, requestId, stageId, false);
        clusters.addHost("host2");
        populateActionDB(db, hostName, ((requestId) + 1), stageId, false);
        List<Long> requestIdsResult = db.getRequestsByStatus(null, DEFAULT_PAGE_SIZE, false);
        Assert.assertNotNull("List of request IDs is null", requestIdsResult);
        Assert.assertEquals("Request IDs not matches", requestIds, requestIdsResult);
    }

    /**
     * Tests getting requests which are fully COMPLETED out the database. This
     * will test for partial completions as well.
     *
     * @throws AmbariException
     * 		
     */
    @Test
    public void testGetCompletedRequests() throws AmbariException {
        List<Long> requestIds = new ArrayList<>();
        requestIds.add(requestId);
        requestIds.add(((requestId) + 1));
        // populate with a completed request
        populateActionDBWithCompletedRequest(db, hostName, requestId, stageId);
        // only 1 should come back
        List<Long> requestIdsResult = db.getRequestsByStatus(RequestStatus.COMPLETED, DEFAULT_PAGE_SIZE, false);
        Assert.assertEquals(1, requestIdsResult.size());
        Assert.assertTrue(requestIdsResult.contains(requestId));
        // populate with a partially completed request
        populateActionDBWithPartiallyCompletedRequest(db, hostName, ((requestId) + 1), stageId);
        // the new request should not come back
        requestIdsResult = db.getRequestsByStatus(RequestStatus.COMPLETED, DEFAULT_PAGE_SIZE, false);
        Assert.assertEquals(1, requestIdsResult.size());
        Assert.assertTrue(requestIdsResult.contains(requestId));
    }

    @Test
    public void testGetRequestsByStatusWithParams() throws AmbariException {
        List<Long> ids = new ArrayList<>();
        for (long l = 1; l <= 10; l++) {
            ids.add(l);
        }
        for (Long id : ids) {
            populateActionDB(db, hostName, id, stageId, false);
        }
        List<Long> expected = null;
        List<Long> actual = null;
        // Select all requests
        actual = db.getRequestsByStatus(null, DEFAULT_PAGE_SIZE, false);
        expected = reverse(new ArrayList<>(ids));
        Assert.assertEquals("Request IDs not matches", expected, actual);
        actual = db.getRequestsByStatus(null, 4, false);
        expected = reverse(new ArrayList<>(ids.subList(((ids.size()) - 4), ids.size())));
        Assert.assertEquals("Request IDs not matches", expected, actual);
        actual = db.getRequestsByStatus(null, 7, true);
        expected = new ArrayList<>(ids.subList(0, 7));
        Assert.assertEquals("Request IDs not matches", expected, actual);
    }

    @Test
    public void testAbortRequest() throws AmbariException {
        Stage s = stageFactory.createNew(requestId, "/a/b", "cluster1", 1L, "action db accessor test", "commandParamsStage", "hostParamsStage");
        s.setStageId(stageId);
        clusters.addHost("host2");
        clusters.addHost("host3");
        clusters.addHost("host4");
        s.addHostRoleExecutionCommand("host1", HBASE_MASTER, START, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(HBASE_MASTER.toString(), "host1", System.currentTimeMillis()), "cluster1", "HBASE", false, false);
        s.addHostRoleExecutionCommand("host2", HBASE_MASTER, START, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(HBASE_MASTER.toString(), "host2", System.currentTimeMillis()), "cluster1", "HBASE", false, false);
        s.addHostRoleExecutionCommand("host3", HBASE_REGIONSERVER, START, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(HBASE_REGIONSERVER.toString(), "host3", System.currentTimeMillis()), "cluster1", "HBASE", false, false);
        s.addHostRoleExecutionCommand("host4", HBASE_REGIONSERVER, START, new org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent(HBASE_REGIONSERVER.toString(), "host4", System.currentTimeMillis()), "cluster1", "HBASE", false, false);
        List<Stage> stages = new ArrayList<>();
        stages.add(s);
        s.getOrderedHostRoleCommands().get(0).setStatus(PENDING);
        s.getOrderedHostRoleCommands().get(1).setStatus(IN_PROGRESS);
        s.getOrderedHostRoleCommands().get(2).setStatus(QUEUED);
        HostRoleCommand cmd = s.getOrderedHostRoleCommands().get(3);
        String hostName = cmd.getHostName();
        cmd.setStatus(COMPLETED);
        Request request = new Request(stages, "", clusters);
        request.setClusterHostInfo("clusterHostInfo");
        db.persistActions(request);
        db.abortOperation(requestId);
        List<Long> aborted = new ArrayList<>();
        List<HostRoleCommand> commands = db.getRequestTasks(requestId);
        for (HostRoleCommand command : commands) {
            if (command.getHostName().equals(hostName)) {
                Assert.assertEquals(COMPLETED, command.getStatus());
            } else {
                Assert.assertEquals(ABORTED, command.getStatus());
                aborted.add(command.getTaskId());
            }
        }
        db.resubmitTasks(aborted);
        commands = db.getRequestTasks(requestId);
        for (HostRoleCommand command : commands) {
            if (command.getHostName().equals(hostName)) {
                Assert.assertEquals(COMPLETED, command.getStatus());
            } else {
                Assert.assertEquals(PENDING, command.getStatus());
            }
        }
    }

    /**
     * Tests that entities created int he {@link ActionDBAccessor} can be
     * retrieved with their IDs intact. EclipseLink seems to execute the
     * {@link NamedQuery} but then use its cached entities to fill in the data.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEntitiesCreatedWithIDs() throws Exception {
        List<Stage> stages = new ArrayList<>();
        Stage stage = createStubStage(hostName, requestId, stageId, false);
        stages.add(stage);
        Request request = new Request(stages, "", clusters);
        // persist entities
        db.persistActions(request);
        // query entities immediately to ensure IDs are populated
        List<HostRoleCommandEntity> commandEntities = hostRoleCommandDAO.findByRequest(requestId);
        Assert.assertEquals(2, commandEntities.size());
        for (HostRoleCommandEntity entity : commandEntities) {
            Assert.assertEquals(Long.valueOf(requestId), entity.getRequestId());
            Assert.assertEquals(Long.valueOf(stageId), entity.getStageId());
        }
    }

    private static class TestActionDBAccessorModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(DBAccessor.class).to(TestActionDBAccessorImpl.TestDBAccessorImpl.class);
        }
    }

    @Singleton
    static class TestDBAccessorImpl extends DBAccessorImpl {
        private DbType dbTypeOverride = null;

        @Inject
        public TestDBAccessorImpl(Configuration configuration) {
            super(configuration);
        }

        @Override
        public DbType getDbType() {
            if ((dbTypeOverride) != null) {
                return dbTypeOverride;
            }
            return super.getDbType();
        }

        public void setDbTypeOverride(DbType dbTypeOverride) {
            this.dbTypeOverride = dbTypeOverride;
        }
    }

    @Test
    public void testGet1000TasksFromOracleDB() throws Exception {
        Stage s = stageFactory.createNew(requestId, "/a/b", "cluster1", 1L, "action db accessor test", "commandParamsStage", "hostParamsStage");
        s.setStageId(stageId);
        for (int i = 1000; i < 2002; i++) {
            String host = "host" + i;
            clusters.addHost(host);
            s.addHostRoleExecutionCommand(("host" + i), HBASE_MASTER, START, null, "cluster1", "HBASE", false, false);
        }
        List<Stage> stages = new ArrayList<>();
        stages.add(s);
        Request request = new Request(stages, "", clusters);
        request.setClusterHostInfo("clusterHostInfo");
        db.persistActions(request);
        List<HostRoleCommandEntity> entities = hostRoleCommandDAO.findByRequest(request.getRequestId());
        Assert.assertEquals(1002, entities.size());
        List<Long> taskIds = new ArrayList<>();
        for (HostRoleCommandEntity entity : entities) {
            taskIds.add(entity.getTaskId());
        }
        TestActionDBAccessorImpl.TestDBAccessorImpl testDBAccessorImpl = ((TestActionDBAccessorImpl.TestDBAccessorImpl) (injector.getInstance(DBAccessor.class)));
        testDBAccessorImpl.setDbTypeOverride(DbType.ORACLE);
        Assert.assertEquals(DbType.ORACLE, injector.getInstance(DBAccessor.class).getDbType());
        entities = hostRoleCommandDAO.findByPKs(taskIds);
        Assert.assertEquals("Tasks returned from DB match the ones created", taskIds.size(), entities.size());
    }
}

