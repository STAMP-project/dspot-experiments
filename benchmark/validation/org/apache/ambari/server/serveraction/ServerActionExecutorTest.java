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
package org.apache.ambari.server.serveraction;


import HostRoleStatus.COMPLETED;
import HostRoleStatus.FAILED;
import HostRoleStatus.HOLDING;
import HostRoleStatus.QUEUED;
import HostRoleStatus.TIMEDOUT;
import Role.AMBARI_SERVER_ACTION;
import RoleCommand.EXECUTE;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.ambari.server.actionmanager.ActionDBAccessor;
import org.apache.ambari.server.actionmanager.Request;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.actionmanager.StageFactory;
import org.apache.ambari.server.serveraction.upgrades.ManualStageAction;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.utils.StageUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


// TODO, fix this test later.
@Ignore
public class ServerActionExecutorTest {
    private static final int MAX_CYCLE_ITERATIONS = 1000;

    private static final String SERVER_HOST_NAME = StageUtils.getHostName();

    private static final String CLUSTER_HOST_INFO = ((("{all_hosts=[" + (ServerActionExecutorTest.SERVER_HOST_NAME)) + "], slave_hosts=[") + (ServerActionExecutorTest.SERVER_HOST_NAME)) + "]}";

    private static Injector injector;

    @Inject
    static StageFactory stageFactory;

    /**
     * Test a normal server action
     */
    @Test
    public void testServerAction() throws Exception {
        final Request request = createMockRequest();
        final Stage s = ServerActionExecutorTest.getStageWithServerAction(1, 977, null, "test", 300);
        final List<Stage> stages = new ArrayList<Stage>() {
            {
                add(s);
            }
        };
        ActionDBAccessor db = createMockActionDBAccessor(request, stages);
        ServerActionExecutor.init(ServerActionExecutorTest.injector);
        ServerActionExecutor executor = new ServerActionExecutor(db, 10000);
        // Force the task to be QUEUED
        s.getHostRoleCommand(ServerActionExecutorTest.SERVER_HOST_NAME, AMBARI_SERVER_ACTION.toString()).setStatus(QUEUED);
        int cycleCount = 0;
        while ((!(getTaskStatus(s).isCompletedState())) && ((cycleCount++) <= (ServerActionExecutorTest.MAX_CYCLE_ITERATIONS))) {
            executor.doWork();
        } 
        Assert.assertEquals(COMPLETED, getTaskStatus(s));
    }

    /**
     * Test a manual stage
     */
    @Test
    public void testServerActionManualStage() throws Exception {
        final Request request = createMockRequest();
        ServerActionExecutorTest.stageFactory = createNiceMock(StageFactory.class);
        final Stage stage = ServerActionExecutorTest.stageFactory.createNew(1, "/tmp", "cluster1", 978, "context", "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        stage.addServerActionCommand(ManualStageAction.class.getName(), null, AMBARI_SERVER_ACTION, EXECUTE, "cluster1", new org.apache.ambari.server.state.svccomphost.ServiceComponentHostServerActionEvent(StageUtils.getHostName(), System.currentTimeMillis()), Collections.emptyMap(), null, null, 1200, false, false);
        final List<Stage> stages = new ArrayList<Stage>() {
            {
                add(stage);
            }
        };
        ActionDBAccessor db = createMockActionDBAccessor(request, stages);
        ServerActionExecutor.init(ServerActionExecutorTest.injector);
        ServerActionExecutor executor = new ServerActionExecutor(db, 10000);
        // Force the task to be QUEUED
        stage.getHostRoleCommand(ServerActionExecutorTest.SERVER_HOST_NAME, AMBARI_SERVER_ACTION.toString()).setStatus(QUEUED);
        int cycleCount = 0;
        while ((!(getTaskStatus(stage).isHoldingState())) && ((cycleCount++) <= (ServerActionExecutorTest.MAX_CYCLE_ITERATIONS))) {
            executor.doWork();
        } 
        Assert.assertEquals(HOLDING, getTaskStatus(stage));
    }

    /**
     * Test a timeout server action
     */
    @Test
    public void testServerActionTimeout() throws Exception {
        final Request request = createMockRequest();
        final Stage s = ServerActionExecutorTest.getStageWithServerAction(1, 977, new HashMap<String, String>() {
            {
                put(MockServerAction.PAYLOAD_FORCE_FAIL, "timeout");
            }
        }, "test", 1);
        final List<Stage> stages = new ArrayList<Stage>() {
            {
                add(s);
            }
        };
        ActionDBAccessor db = createMockActionDBAccessor(request, stages);
        ServerActionExecutor.init(ServerActionExecutorTest.injector);
        ServerActionExecutor executor = new ServerActionExecutor(db, 10000);
        // Force the task to be QUEUED
        s.getHostRoleCommand(ServerActionExecutorTest.SERVER_HOST_NAME, AMBARI_SERVER_ACTION.toString()).setStatus(QUEUED);
        int cycleCount = 0;
        while ((!(getTaskStatus(s).isCompletedState())) && ((cycleCount++) <= (ServerActionExecutorTest.MAX_CYCLE_ITERATIONS))) {
            executor.doWork();
        } 
        Assert.assertEquals(TIMEDOUT, getTaskStatus(s));
    }

    /**
     * Test a timeout server action
     */
    @Test
    public void testServerActionFailedException() throws Exception {
        final Request request = createMockRequest();
        final Stage s = ServerActionExecutorTest.getStageWithServerAction(1, 977, new HashMap<String, String>() {
            {
                put(MockServerAction.PAYLOAD_FORCE_FAIL, "exception");
            }
        }, "test", 1);
        final List<Stage> stages = new ArrayList<Stage>() {
            {
                add(s);
            }
        };
        ActionDBAccessor db = createMockActionDBAccessor(request, stages);
        ServerActionExecutor.init(ServerActionExecutorTest.injector);
        ServerActionExecutor executor = new ServerActionExecutor(db, 10000);
        // Force the task to be QUEUED
        s.getHostRoleCommand(ServerActionExecutorTest.SERVER_HOST_NAME, AMBARI_SERVER_ACTION.toString()).setStatus(QUEUED);
        int cycleCount = 0;
        while ((!(getTaskStatus(s).isCompletedState())) && ((cycleCount++) <= (ServerActionExecutorTest.MAX_CYCLE_ITERATIONS))) {
            executor.doWork();
        } 
        Assert.assertEquals(FAILED, getTaskStatus(s));
    }

    /**
     * Test a timeout server action
     */
    @Test
    public void testServerActionFailedReport() throws Exception {
        final Request request = createMockRequest();
        final Stage s = ServerActionExecutorTest.getStageWithServerAction(1, 977, new HashMap<String, String>() {
            {
                put(MockServerAction.PAYLOAD_FORCE_FAIL, "report");
            }
        }, "test", 1);
        final List<Stage> stages = new ArrayList<Stage>() {
            {
                add(s);
            }
        };
        ActionDBAccessor db = createMockActionDBAccessor(request, stages);
        ServerActionExecutor.init(ServerActionExecutorTest.injector);
        ServerActionExecutor executor = new ServerActionExecutor(db, 10000);
        // Force the task to be QUEUED
        s.getHostRoleCommand(ServerActionExecutorTest.SERVER_HOST_NAME, AMBARI_SERVER_ACTION.toString()).setStatus(QUEUED);
        int cycleCount = 0;
        while ((!(getTaskStatus(s).isCompletedState())) && ((cycleCount++) <= (ServerActionExecutorTest.MAX_CYCLE_ITERATIONS))) {
            executor.doWork();
        } 
        Assert.assertEquals(FAILED, getTaskStatus(s));
    }

    public static class MockModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Clusters.class).toInstance(Mockito.mock(Clusters.class));
        }
    }
}

