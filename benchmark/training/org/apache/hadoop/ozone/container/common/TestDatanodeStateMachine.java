/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.container.common;


import DatanodeDetails.Port;
import DatanodeDetails.Port.Name.STANDALONE;
import DatanodeStateMachine.DatanodeStates;
import DatanodeStateMachine.DatanodeStates.INIT;
import DatanodeStateMachine.DatanodeStates.RUNNING;
import DatanodeStateMachine.DatanodeStates.SHUTDOWN;
import EndpointStateMachine.EndPointStates.GETVERSION;
import EndpointStateMachine.EndPointStates.REGISTER;
import OzoneConfigKeys.DFS_CONTAINER_IPC_PORT_DEFAULT;
import ScmConfigKeys.OZONE_SCM_DATANODE_ID;
import ScmConfigKeys.OZONE_SCM_NAMES;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ozone.container.common.helpers.ContainerUtils;
import org.apache.hadoop.ozone.container.common.statemachine.DatanodeStateMachine;
import org.apache.hadoop.ozone.container.common.statemachine.EndpointStateMachine;
import org.apache.hadoop.ozone.container.common.statemachine.SCMConnectionManager;
import org.apache.hadoop.ozone.container.common.states.DatanodeState;
import org.apache.hadoop.ozone.container.common.states.datanode.InitDatanodeState;
import org.apache.hadoop.ozone.container.common.states.datanode.RunningDatanodeState;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the datanode state machine class and its states.
 */
public class TestDatanodeStateMachine {
    private static final Logger LOG = LoggerFactory.getLogger(TestDatanodeStateMachine.class);

    // Changing it to 1, as current code checks for multiple scm directories,
    // and fail if exists
    private final int scmServerCount = 1;

    private List<String> serverAddresses;

    private List<RPC.Server> scmServers;

    private List<ScmTestMock> mockServers;

    private ExecutorService executorService;

    private Configuration conf;

    private File testRoot;

    /**
     * Assert that starting statemachine executes the Init State.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testStartStopDatanodeStateMachine() throws IOException, InterruptedException, TimeoutException {
        try (DatanodeStateMachine stateMachine = new DatanodeStateMachine(getNewDatanodeDetails(), conf)) {
            stateMachine.startDaemon();
            SCMConnectionManager connectionManager = stateMachine.getConnectionManager();
            GenericTestUtils.waitFor(() -> (connectionManager.getValues().size()) == 1, 1000, 30000);
            stateMachine.stopDaemon();
            Assert.assertTrue(stateMachine.isDaemonStopped());
        }
    }

    /**
     * This test explores the state machine by invoking each call in sequence just
     * like as if the state machine would call it. Because this is a test we are
     * able to verify each of the assumptions.
     * <p>
     * Here is what happens at High level.
     * <p>
     * 1. We start the datanodeStateMachine in the INIT State.
     * <p>
     * 2. We invoke the INIT state task.
     * <p>
     * 3. That creates a set of RPC endpoints that are ready to connect to SCMs.
     * <p>
     * 4. We assert that we have moved to the running state for the
     * DatanodeStateMachine.
     * <p>
     * 5. We get the task for the Running State - Executing that running state,
     * makes the first network call in of the state machine. The Endpoint is in
     * the GETVERSION State and we invoke the task.
     * <p>
     * 6. We assert that this call was a success by checking that each of the
     * endponts now have version response that it got from the SCM server that it
     * was talking to and also each of the mock server serviced one RPC call.
     * <p>
     * 7. Since the Register is done now, next calls to get task will return
     * HeartbeatTask, which sends heartbeats to SCM. We assert that we get right
     * task from sub-system below.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDatanodeStateContext() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // There is no mini cluster started in this test,
        // create a ID file so that state machine could load a fake datanode ID.
        File idPath = new File(conf.get(OZONE_SCM_DATANODE_ID));
        idPath.delete();
        DatanodeDetails datanodeDetails = getNewDatanodeDetails();
        DatanodeDetails.Port port = DatanodeDetails.newPort(STANDALONE, DFS_CONTAINER_IPC_PORT_DEFAULT);
        datanodeDetails.setPort(port);
        ContainerUtils.writeDatanodeDetailsTo(datanodeDetails, idPath);
        try (DatanodeStateMachine stateMachine = new DatanodeStateMachine(datanodeDetails, conf)) {
            DatanodeStateMachine.DatanodeStates currentState = stateMachine.getContext().getState();
            Assert.assertEquals(INIT, currentState);
            DatanodeState<DatanodeStateMachine.DatanodeStates> task = stateMachine.getContext().getTask();
            Assert.assertEquals(InitDatanodeState.class, task.getClass());
            task.execute(executorService);
            DatanodeStateMachine.DatanodeStates newState = task.await(2, TimeUnit.SECONDS);
            for (EndpointStateMachine endpoint : stateMachine.getConnectionManager().getValues()) {
                // We assert that each of the is in State GETVERSION.
                Assert.assertEquals(GETVERSION, endpoint.getState());
            }
            // The Datanode has moved into Running State, since endpoints are created.
            // We move to running state when we are ready to issue RPC calls to SCMs.
            Assert.assertEquals(RUNNING, newState);
            // If we had called context.execute instead of calling into each state
            // this would have happened automatically.
            stateMachine.getContext().setState(newState);
            task = stateMachine.getContext().getTask();
            Assert.assertEquals(RunningDatanodeState.class, task.getClass());
            // This execute will invoke getVersion calls against all SCM endpoints
            // that we know of.
            task.execute(executorService);
            newState = task.await(10, TimeUnit.SECONDS);
            // If we are in running state, we should be in running.
            Assert.assertEquals(RUNNING, newState);
            for (EndpointStateMachine endpoint : stateMachine.getConnectionManager().getValues()) {
                // Since the earlier task.execute called into GetVersion, the
                // endPointState Machine should move to REGISTER state.
                Assert.assertEquals(REGISTER, endpoint.getState());
                // We assert that each of the end points have gotten a version from the
                // SCM Server.
                Assert.assertNotNull(endpoint.getVersion());
            }
            // We can also assert that all mock servers have received only one RPC
            // call at this point of time.
            for (ScmTestMock mock : mockServers) {
                Assert.assertEquals(1, mock.getRpcCount());
            }
            // This task is the Running task, but running task executes tasks based
            // on the state of Endpoints, hence this next call will be a Register at
            // the endpoint RPC level.
            task = stateMachine.getContext().getTask();
            task.execute(executorService);
            newState = task.await(2, TimeUnit.SECONDS);
            // If we are in running state, we should be in running.
            Assert.assertEquals(RUNNING, newState);
            for (ScmTestMock mock : mockServers) {
                Assert.assertEquals(2, mock.getRpcCount());
            }
            // This task is the Running task, but running task executes tasks based
            // on the state of Endpoints, hence this next call will be a
            // HeartbeatTask at the endpoint RPC level.
            task = stateMachine.getContext().getTask();
            task.execute(executorService);
            newState = task.await(2, TimeUnit.SECONDS);
            // If we are in running state, we should be in running.
            Assert.assertEquals(RUNNING, newState);
            for (ScmTestMock mock : mockServers) {
                Assert.assertEquals(1, mock.getHeartbeatCount());
            }
        }
    }

    @Test
    public void testDatanodeStateMachineWithIdWriteFail() throws Exception {
        File idPath = new File(conf.get(OZONE_SCM_DATANODE_ID));
        idPath.delete();
        DatanodeDetails datanodeDetails = getNewDatanodeDetails();
        DatanodeDetails.Port port = DatanodeDetails.newPort(STANDALONE, DFS_CONTAINER_IPC_PORT_DEFAULT);
        datanodeDetails.setPort(port);
        try (DatanodeStateMachine stateMachine = new DatanodeStateMachine(datanodeDetails, conf)) {
            DatanodeStateMachine.DatanodeStates currentState = stateMachine.getContext().getState();
            Assert.assertEquals(INIT, currentState);
            DatanodeState<DatanodeStateMachine.DatanodeStates> task = stateMachine.getContext().getTask();
            Assert.assertEquals(InitDatanodeState.class, task.getClass());
            // Set the idPath to read only, state machine will fail to write
            // datanodeId file and set the state to shutdown.
            idPath.getParentFile().mkdirs();
            idPath.getParentFile().setReadOnly();
            task.execute(executorService);
            DatanodeStateMachine.DatanodeStates newState = task.await(2, TimeUnit.SECONDS);
            // As, we have changed the permission of idPath to readable, writing
            // will fail and it will set the state to shutdown.
            Assert.assertEquals(SHUTDOWN, newState);
            // Setting back to writable.
            idPath.getParentFile().setWritable(true);
        }
    }

    /**
     * Test state transition with a list of invalid scm configurations,
     * and verify the state transits to SHUTDOWN each time.
     */
    @Test
    public void testDatanodeStateMachineWithInvalidConfiguration() throws Exception {
        List<Map.Entry<String, String>> confList = new ArrayList<>();
        confList.add(Maps.immutableEntry(OZONE_SCM_NAMES, ""));
        // Invalid ozone.scm.names
        /**
         * Empty *
         */
        confList.add(Maps.immutableEntry(OZONE_SCM_NAMES, ""));
        /**
         * Invalid schema *
         */
        confList.add(Maps.immutableEntry(OZONE_SCM_NAMES, "x..y"));
        /**
         * Invalid port *
         */
        confList.add(Maps.immutableEntry(OZONE_SCM_NAMES, "scm:xyz"));
        /**
         * Port out of range *
         */
        confList.add(Maps.immutableEntry(OZONE_SCM_NAMES, "scm:123456"));
        // Invalid ozone.scm.datanode.id
        /**
         * Empty *
         */
        confList.add(Maps.immutableEntry(OZONE_SCM_DATANODE_ID, ""));
        confList.forEach(( entry) -> {
            Configuration perTestConf = new Configuration(conf);
            perTestConf.setStrings(entry.getKey(), entry.getValue());
            TestDatanodeStateMachine.LOG.info("Test with {} = {}", entry.getKey(), entry.getValue());
            try (DatanodeStateMachine stateMachine = new DatanodeStateMachine(getNewDatanodeDetails(), perTestConf)) {
                DatanodeStateMachine.DatanodeStates currentState = stateMachine.getContext().getState();
                Assert.assertEquals(INIT, currentState);
                DatanodeState<DatanodeStateMachine.DatanodeStates> task = stateMachine.getContext().getTask();
                task.execute(executorService);
                DatanodeStateMachine.DatanodeStates newState = task.await(2, TimeUnit.SECONDS);
                Assert.assertEquals(SHUTDOWN, newState);
            } catch (Exception e) {
                Assert.fail("Unexpected exception found");
            }
        });
    }
}

