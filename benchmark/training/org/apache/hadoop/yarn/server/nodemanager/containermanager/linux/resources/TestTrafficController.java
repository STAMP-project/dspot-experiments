/**
 * *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements. See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership. The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources;


import PrivilegedOperation.OperationType;
import PrivilegedOperation.OperationType.TC_MODIFY_STATE;
import PrivilegedOperation.OperationType.TC_READ_STATE;
import YarnConfiguration.NM_RECOVERY_ENABLED;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestTrafficController {
    private static final Logger LOG = LoggerFactory.getLogger(TestTrafficController.class);

    private static final int ROOT_BANDWIDTH_MBIT = 100;

    private static final int YARN_BANDWIDTH_MBIT = 70;

    private static final int CONTAINER_BANDWIDTH_MBIT = 10;

    // These constants are closely tied to the implementation of TrafficController
    // and will have to be modified in tandem with any related TrafficController
    // changes.
    private static final String DEVICE = "eth0";

    private static final String WIPE_STATE_CMD = "qdisc del dev eth0 parent root";

    private static final String ADD_ROOT_QDISC_CMD = "qdisc add dev eth0 root handle 42: htb default 2";

    private static final String ADD_CGROUP_FILTER_CMD = "filter add dev eth0 parent 42: protocol ip prio 10 handle 1: cgroup";

    private static final String ADD_ROOT_CLASS_CMD = "class add dev eth0 parent 42:0 classid 42:1 htb rate 100mbit ceil 100mbit";

    private static final String ADD_DEFAULT_CLASS_CMD = "class add dev eth0 parent 42:1 classid 42:2 htb rate 30mbit ceil 100mbit";

    private static final String ADD_YARN_CLASS_CMD = "class add dev eth0 parent 42:1 classid 42:3 htb rate 70mbit ceil 70mbit";

    private static final String DEFAULT_TC_STATE_EXAMPLE = "qdisc pfifo_fast 0: root refcnt 2 bands 3 priomap  1 2 2 2 1 2 0 0 1 1 1 1 1 1 1 1";

    private static final String READ_QDISC_CMD = "qdisc show dev eth0";

    private static final String READ_FILTER_CMD = "filter show dev eth0";

    private static final String READ_CLASS_CMD = "class show dev eth0";

    private static final int MIN_CONTAINER_CLASS_ID = 4;

    private static final String FORMAT_CONTAINER_CLASS_STR = "0x0042%04d";

    private static final String FORMAT_ADD_CONTAINER_CLASS_TO_DEVICE = "class add dev eth0 parent 42:3 classid 42:%d htb rate 10mbit ceil %dmbit";

    private static final String FORAMT_DELETE_CONTAINER_CLASS_FROM_DEVICE = "class del dev eth0 classid 42:%d";

    private static final int TEST_CLASS_ID = 97;

    // decimal form of 0x00420097 - when reading a classid file, it is read out
    // as decimal
    private static final String TEST_CLASS_ID_DECIMAL_STR = "4325527";

    private Configuration conf;

    private String tmpPath;

    private PrivilegedOperationExecutor privilegedOperationExecutorMock;

    @Test
    public void testBootstrapRecoveryDisabled() {
        conf.setBoolean(NM_RECOVERY_ENABLED, false);
        TrafficController trafficController = new TrafficController(conf, privilegedOperationExecutorMock);
        try {
            trafficController.bootstrap(TestTrafficController.DEVICE, TestTrafficController.ROOT_BANDWIDTH_MBIT, TestTrafficController.YARN_BANDWIDTH_MBIT);
            ArgumentCaptor<PrivilegedOperation> opCaptor = ArgumentCaptor.forClass(PrivilegedOperation.class);
            // NM_RECOVERY_DISABLED - so we expect two privileged operation executions
            // one for wiping tc state - a second for initializing state
            Mockito.verify(privilegedOperationExecutorMock, Mockito.times(2)).executePrivilegedOperation(opCaptor.capture(), ArgumentMatchers.eq(false));
            // Now verify that the two operations were correct
            List<PrivilegedOperation> ops = opCaptor.getAllValues();
            verifyTrafficControlOperation(ops.get(0), TC_MODIFY_STATE, Arrays.asList(TestTrafficController.WIPE_STATE_CMD));
            verifyTrafficControlOperation(ops.get(1), TC_MODIFY_STATE, Arrays.asList(TestTrafficController.ADD_ROOT_QDISC_CMD, TestTrafficController.ADD_CGROUP_FILTER_CMD, TestTrafficController.ADD_ROOT_CLASS_CMD, TestTrafficController.ADD_DEFAULT_CLASS_CMD, TestTrafficController.ADD_YARN_CLASS_CMD));
        } catch (ResourceHandlerException | PrivilegedOperationException | IOException e) {
            TestTrafficController.LOG.error(("Unexpected exception: " + e));
            Assert.fail(("Caught unexpected exception: " + (e.getClass().getSimpleName())));
        }
    }

    @Test
    public void testBootstrapRecoveryEnabled() {
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        TrafficController trafficController = new TrafficController(conf, privilegedOperationExecutorMock);
        try {
            // Return a default tc state when attempting to read state
            Mockito.when(privilegedOperationExecutorMock.executePrivilegedOperation(ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.eq(true))).thenReturn(TestTrafficController.DEFAULT_TC_STATE_EXAMPLE);
            trafficController.bootstrap(TestTrafficController.DEVICE, TestTrafficController.ROOT_BANDWIDTH_MBIT, TestTrafficController.YARN_BANDWIDTH_MBIT);
            ArgumentCaptor<PrivilegedOperation> readOpCaptor = ArgumentCaptor.forClass(PrivilegedOperation.class);
            // NM_RECOVERY_ENABLED - so we expect three privileged operation executions
            // 1) read tc state 2) wipe tc state 3) init tc state
            // one for wiping tc state - a second for initializing state
            // First, verify read op
            Mockito.verify(privilegedOperationExecutorMock, Mockito.times(1)).executePrivilegedOperation(readOpCaptor.capture(), ArgumentMatchers.eq(true));
            List<PrivilegedOperation> readOps = readOpCaptor.getAllValues();
            verifyTrafficControlOperation(readOps.get(0), TC_READ_STATE, Arrays.asList(TestTrafficController.READ_QDISC_CMD, TestTrafficController.READ_FILTER_CMD, TestTrafficController.READ_CLASS_CMD));
            ArgumentCaptor<PrivilegedOperation> writeOpCaptor = ArgumentCaptor.forClass(PrivilegedOperation.class);
            Mockito.verify(privilegedOperationExecutorMock, Mockito.times(2)).executePrivilegedOperation(writeOpCaptor.capture(), ArgumentMatchers.eq(false));
            // Now verify that the two write operations were correct
            List<PrivilegedOperation> writeOps = writeOpCaptor.getAllValues();
            verifyTrafficControlOperation(writeOps.get(0), TC_MODIFY_STATE, Arrays.asList(TestTrafficController.WIPE_STATE_CMD));
            verifyTrafficControlOperation(writeOps.get(1), TC_MODIFY_STATE, Arrays.asList(TestTrafficController.ADD_ROOT_QDISC_CMD, TestTrafficController.ADD_CGROUP_FILTER_CMD, TestTrafficController.ADD_ROOT_CLASS_CMD, TestTrafficController.ADD_DEFAULT_CLASS_CMD, TestTrafficController.ADD_YARN_CLASS_CMD));
        } catch (ResourceHandlerException | PrivilegedOperationException | IOException e) {
            TestTrafficController.LOG.error(("Unexpected exception: " + e));
            Assert.fail(("Caught unexpected exception: " + (e.getClass().getSimpleName())));
        }
    }

    @Test
    public void testInvalidBuilder() {
        conf.setBoolean(NM_RECOVERY_ENABLED, false);
        TrafficController trafficController = new TrafficController(conf, privilegedOperationExecutorMock);
        try {
            trafficController.bootstrap(TestTrafficController.DEVICE, TestTrafficController.ROOT_BANDWIDTH_MBIT, TestTrafficController.YARN_BANDWIDTH_MBIT);
            try {
                // Invalid op type for TC batch builder
                TrafficController.BatchBuilder invalidBuilder = trafficController.new BatchBuilder(OperationType.ADD_PID_TO_CGROUP);
                Assert.fail("Invalid builder check failed!");
            } catch (ResourceHandlerException e) {
                // expected
            }
        } catch (ResourceHandlerException e) {
            TestTrafficController.LOG.error(("Unexpected exception: " + e));
            Assert.fail(("Caught unexpected exception: " + (e.getClass().getSimpleName())));
        }
    }

    @Test
    public void testClassIdFileContentParsing() {
        conf.setBoolean(NM_RECOVERY_ENABLED, false);
        TrafficController trafficController = new TrafficController(conf, privilegedOperationExecutorMock);
        // Verify that classid file contents are parsed correctly
        // This call strips the QDISC prefix and returns the classid asociated with
        // the container
        int parsedClassId = trafficController.getClassIdFromFileContents(TestTrafficController.TEST_CLASS_ID_DECIMAL_STR);
        Assert.assertEquals(TestTrafficController.TEST_CLASS_ID, parsedClassId);
    }

    @Test
    public void testContainerOperations() {
        conf.setBoolean(NM_RECOVERY_ENABLED, false);
        TrafficController trafficController = new TrafficController(conf, privilegedOperationExecutorMock);
        try {
            trafficController.bootstrap(TestTrafficController.DEVICE, TestTrafficController.ROOT_BANDWIDTH_MBIT, TestTrafficController.YARN_BANDWIDTH_MBIT);
            int classId = trafficController.getNextClassId();
            Assert.assertTrue((classId >= (TestTrafficController.MIN_CONTAINER_CLASS_ID)));
            Assert.assertEquals(String.format(TestTrafficController.FORMAT_CONTAINER_CLASS_STR, classId), trafficController.getStringForNetClsClassId(classId));
            // Verify that the operation is setup correctly with strictMode = false
            TrafficController.BatchBuilder builder = trafficController.new BatchBuilder(OperationType.TC_MODIFY_STATE).addContainerClass(classId, TestTrafficController.CONTAINER_BANDWIDTH_MBIT, false);
            PrivilegedOperation addClassOp = builder.commitBatchToTempFile();
            String expectedAddClassCmd = String.format(TestTrafficController.FORMAT_ADD_CONTAINER_CLASS_TO_DEVICE, classId, TestTrafficController.YARN_BANDWIDTH_MBIT);
            verifyTrafficControlOperation(addClassOp, TC_MODIFY_STATE, Arrays.asList(expectedAddClassCmd));
            // Verify that the operation is setup correctly with strictMode = true
            TrafficController.BatchBuilder strictModeBuilder = trafficController.new BatchBuilder(OperationType.TC_MODIFY_STATE).addContainerClass(classId, TestTrafficController.CONTAINER_BANDWIDTH_MBIT, true);
            PrivilegedOperation addClassStrictModeOp = strictModeBuilder.commitBatchToTempFile();
            String expectedAddClassStrictModeCmd = String.format(TestTrafficController.FORMAT_ADD_CONTAINER_CLASS_TO_DEVICE, classId, TestTrafficController.CONTAINER_BANDWIDTH_MBIT);
            verifyTrafficControlOperation(addClassStrictModeOp, TC_MODIFY_STATE, Arrays.asList(expectedAddClassStrictModeCmd));
            TrafficController.BatchBuilder deleteBuilder = trafficController.new BatchBuilder(OperationType.TC_MODIFY_STATE).deleteContainerClass(classId);
            PrivilegedOperation deleteClassOp = deleteBuilder.commitBatchToTempFile();
            String expectedDeleteClassCmd = String.format(TestTrafficController.FORAMT_DELETE_CONTAINER_CLASS_FROM_DEVICE, classId);
            verifyTrafficControlOperation(deleteClassOp, TC_MODIFY_STATE, Arrays.asList(expectedDeleteClassCmd));
        } catch (ResourceHandlerException | IOException e) {
            TestTrafficController.LOG.error(("Unexpected exception: " + e));
            Assert.fail(("Caught unexpected exception: " + (e.getClass().getSimpleName())));
        }
    }
}

