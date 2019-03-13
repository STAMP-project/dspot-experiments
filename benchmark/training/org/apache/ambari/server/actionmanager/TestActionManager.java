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


import HostRoleStatus.QUEUED;
import com.google.inject.Injector;
import com.google.inject.persist.UnitOfWork;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.events.publishers.JPAEventPublisher;
import org.apache.ambari.server.orm.dao.StageDAO;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.StageEntity;
import org.apache.ambari.server.orm.entities.StageEntityPK;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.utils.CommandUtils;
import org.apache.ambari.server.utils.StageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


public class TestActionManager {
    private long requestId = 23;

    private long stageId = 31;

    private Injector injector;

    private String hostname = "host1";

    private String clusterName = "cluster1";

    private Clusters clusters;

    private UnitOfWork unitOfWork;

    private StageFactory stageFactory;

    @Test
    public void testActionResponse() throws AmbariException {
        ActionDBAccessor db = injector.getInstance(ActionDBAccessorImpl.class);
        ActionManager am = injector.getInstance(ActionManager.class);
        populateActionDB(db, hostname);
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
        cr.setStdErr("ERROR");
        cr.setStdOut("OUTPUT");
        cr.setStructuredOut("STRUCTURED_OUTPUT");
        cr.setExitCode(215);
        reports.add(cr);
        am.processTaskResponse(hostname, reports, CommandUtils.convertToTaskIdCommandMap(stage.getOrderedHostRoleCommands()));
        assertEquals(215, am.getAction(requestId, stageId).getExitCode(hostname, "HBASE_MASTER"));
        org.junit.Assert.assertEquals(HostRoleStatus.COMPLETED, am.getAction(requestId, stageId).getHostRoleStatus(hostname, "HBASE_MASTER"));
        assertEquals("ERROR", am.getAction(requestId, stageId).getHostRoleCommand(hostname, "HBASE_MASTER").getStderr());
        assertEquals("OUTPUT", am.getAction(requestId, stageId).getHostRoleCommand(hostname, "HBASE_MASTER").getStdout());
        assertEquals("STRUCTURED_OUTPUT", am.getAction(requestId, stageId).getHostRoleCommand(hostname, "HBASE_MASTER").getStructuredOut());
        assertNotNull(db.getRequest(requestId));
        assertFalse(((db.getRequest(requestId).getEndTime()) == (-1)));
    }

    @Test
    public void testActionResponsesUnsorted() throws AmbariException {
        ActionDBAccessor db = injector.getInstance(ActionDBAccessorImpl.class);
        ActionManager am = injector.getInstance(ActionManager.class);
        populateActionDBWithTwoCommands(db, hostname);
        Stage stage = db.getAllStages(requestId).get(0);
        Assert.assertEquals(stageId, stage.getStageId());
        stage.setHostRoleStatus(hostname, "HBASE_MASTER", QUEUED);
        db.hostRoleScheduled(stage, hostname, "HBASE_MASTER");
        List<CommandReport> reports = new ArrayList<>();
        CommandReport cr = new CommandReport();
        cr.setTaskId(2);
        cr.setActionId(StageUtils.getActionId(requestId, stageId));
        cr.setRole("HBASE_REGIONSERVER");
        cr.setStatus("COMPLETED");
        cr.setStdErr("ERROR");
        cr.setStdOut("OUTPUT");
        cr.setStructuredOut("STRUCTURED_OUTPUT");
        cr.setExitCode(215);
        reports.add(cr);
        CommandReport cr2 = new CommandReport();
        cr2.setTaskId(1);
        cr2.setActionId(StageUtils.getActionId(requestId, stageId));
        cr2.setRole("HBASE_MASTER");
        cr2.setStatus("IN_PROGRESS");
        cr2.setStdErr("ERROR");
        cr2.setStdOut("OUTPUT");
        cr2.setStructuredOut("STRUCTURED_OUTPUT");
        cr2.setExitCode(215);
        reports.add(cr2);
        am.processTaskResponse(hostname, reports, CommandUtils.convertToTaskIdCommandMap(am.getTasks(Arrays.asList(new Long[]{ 1L, 2L }))));
        org.junit.Assert.assertEquals(HostRoleStatus.IN_PROGRESS, am.getAction(requestId, stageId).getHostRoleStatus(hostname, "HBASE_MASTER"));
        org.junit.Assert.assertEquals(HostRoleStatus.PENDING, am.getAction(requestId, stageId).getHostRoleStatus(hostname, "HBASE_REGIONSERVER"));
    }

    @Test
    public void testLargeLogs() throws AmbariException {
        ActionDBAccessor db = injector.getInstance(ActionDBAccessorImpl.class);
        ActionManager am = injector.getInstance(ActionManager.class);
        populateActionDB(db, hostname);
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
        String errLog = Arrays.toString(new byte[100000]);
        String outLog = Arrays.toString(new byte[110000]);
        cr.setStdErr(errLog);
        cr.setStdOut(outLog);
        cr.setStructuredOut(outLog);
        cr.setExitCode(215);
        reports.add(cr);
        am.processTaskResponse(hostname, reports, CommandUtils.convertToTaskIdCommandMap(stage.getOrderedHostRoleCommands()));
        assertEquals(215, am.getAction(requestId, stageId).getExitCode(hostname, "HBASE_MASTER"));
        org.junit.Assert.assertEquals(HostRoleStatus.COMPLETED, am.getAction(requestId, stageId).getHostRoleStatus(hostname, "HBASE_MASTER"));
        assertEquals(errLog.length(), am.getAction(requestId, stageId).getHostRoleCommand(hostname, "HBASE_MASTER").getStderr().length());
        assertEquals(outLog.length(), am.getAction(requestId, stageId).getHostRoleCommand(hostname, "HBASE_MASTER").getStdout().length());
        assertEquals(outLog.length(), am.getAction(requestId, stageId).getHostRoleCommand(hostname, "HBASE_MASTER").getStructuredOut().length());
    }

    @Test
    public void testGetActions() throws Exception {
        int requestId = 500;
        ActionDBAccessor db = createStrictMock(ActionDBAccessor.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Stage stage1 = createNiceMock(Stage.class);
        Stage stage2 = createNiceMock(Stage.class);
        List<Stage> listStages = new ArrayList<>();
        listStages.add(stage1);
        listStages.add(stage2);
        // mock expectations
        expect(db.getLastPersistedRequestIdWhenInitialized()).andReturn(Long.valueOf(1000));
        expect(db.getAllStages(requestId)).andReturn(listStages);
        replay(db, clusters);
        ActionScheduler actionScheduler = new ActionScheduler(0, 0, db, createNiceMock(JPAEventPublisher.class));
        ActionManager manager = new ActionManager(db, injector.getInstance(RequestFactory.class), actionScheduler);
        assertSame(listStages, manager.getActions(requestId));
        verify(db, clusters);
    }

    /**
     * Tests whether {@link ActionDBAccessor#persistActions(Request)} associates tasks with their
     * stages.  Improvements to {@code Stage} processing exposed the fact that the association wasn't
     * being made, and JPA didn't know of the Stage-to-Tasks child relationship.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPersistCommandsWithStages() throws Exception {
        ActionDBAccessor db = injector.getInstance(ActionDBAccessorImpl.class);
        populateActionDBWithTwoCommands(db, hostname);
        List<Stage> stages = db.getAllStages(requestId);
        assertEquals(1, stages.size());
        Stage stage = stages.get(0);
        StageEntityPK pk = new StageEntityPK();
        pk.setRequestId(stage.getRequestId());
        pk.setStageId(stage.getStageId());
        StageDAO dao = injector.getInstance(StageDAO.class);
        StageEntity stageEntity = dao.findByPK(pk);
        assertNotNull(stageEntity);
        Collection<HostRoleCommandEntity> commandEntities = stageEntity.getHostRoleCommands();
        assertTrue(CollectionUtils.isNotEmpty(commandEntities));
    }
}

