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
package org.apache.hadoop.hdfs.tools;


import DFSConfigKeys.DFS_HA_AUTO_FAILOVER_ENABLED_KEY;
import DFSConfigKeys.DFS_HA_FENCE_METHODS_KEY;
import RequestSource.REQUEST_BY_USER;
import RequestSource.REQUEST_BY_USER_FORCED;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.ha.HAServiceProtocol.StateChangeRequestInfo;
import org.apache.hadoop.ha.HAServiceStatus;
import org.apache.hadoop.ha.HealthCheckFailedException;
import org.apache.hadoop.ha.ZKFCProtocol;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDFSHAAdmin {
    private static final Logger LOG = LoggerFactory.getLogger(TestDFSHAAdmin.class);

    private DFSHAAdmin tool;

    private final ByteArrayOutputStream errOutBytes = new ByteArrayOutputStream();

    private final ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

    private String errOutput;

    private String output;

    private HAServiceProtocol mockProtocol;

    private ZKFCProtocol mockZkfcProtocol;

    private static final String NSID = "ns1";

    private static final HAServiceStatus STANDBY_READY_RESULT = setReadyToBecomeActive();

    private final ArgumentCaptor<StateChangeRequestInfo> reqInfoCaptor = ArgumentCaptor.forClass(StateChangeRequestInfo.class);

    private static final String HOST_A = "1.2.3.1";

    private static final String HOST_B = "1.2.3.2";

    // Fencer shell commands that always return true and false respectively
    // on Unix.
    private static final String FENCER_TRUE_COMMAND_UNIX = "shell(true)";

    private static final String FENCER_FALSE_COMMAND_UNIX = "shell(false)";

    // Fencer shell commands that always return true and false respectively
    // on Windows. Lacking POSIX 'true' and 'false' commands we use the DOS
    // commands 'rem' and 'help.exe'.
    private static final String FENCER_TRUE_COMMAND_WINDOWS = "shell(rem)";

    private static final String FENCER_FALSE_COMMAND_WINDOWS = "shell(help.exe /? >NUL)";

    @Test
    public void testNameserviceOption() throws Exception {
        Assert.assertEquals((-1), runTool("-ns"));
        assertOutputContains("Missing nameservice ID");
        Assert.assertEquals((-1), runTool("-ns", "ns1"));
        assertOutputContains("Missing command");
        // "ns1" isn't defined but we check this lazily and help doesn't use the ns
        Assert.assertEquals(0, runTool("-ns", "ns1", "-help", "transitionToActive"));
        assertOutputContains("Transitions the service into Active");
    }

    @Test
    public void testNamenodeResolution() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        Assert.assertEquals(0, runTool("-getServiceState", "nn1"));
        Mockito.verify(mockProtocol).getServiceStatus();
        Assert.assertEquals((-1), runTool("-getServiceState", "undefined"));
        assertOutputContains("Unable to determine service address for namenode 'undefined'");
    }

    @Test
    public void testHelp() throws Exception {
        Assert.assertEquals(0, runTool("-help"));
        Assert.assertEquals(0, runTool("-help", "transitionToActive"));
        assertOutputContains("Transitions the service into Active");
    }

    @Test
    public void testGetAllServiceState() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        Assert.assertEquals(0, runTool("-getAllServiceState"));
        assertOutputContains(String.format("%-50s %-10s", (((TestDFSHAAdmin.HOST_A) + ":") + 12345), TestDFSHAAdmin.STANDBY_READY_RESULT.getState()));
        assertOutputContains(String.format("%-50s %-10s", (((TestDFSHAAdmin.HOST_B) + ":") + 12345), TestDFSHAAdmin.STANDBY_READY_RESULT.getState()));
    }

    @Test
    public void testTransitionToActive() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        Assert.assertEquals(0, runTool("-transitionToActive", "nn1"));
        Mockito.verify(mockProtocol).transitionToActive(reqInfoCaptor.capture());
        Assert.assertEquals(REQUEST_BY_USER, reqInfoCaptor.getValue().getSource());
    }

    /**
     * Test that, if automatic HA is enabled, none of the mutative operations
     * will succeed, unless the -forcemanual flag is specified.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMutativeOperationsWithAutoHaEnabled() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        // Turn on auto-HA in the config
        HdfsConfiguration conf = getHAConf();
        conf.setBoolean(DFS_HA_AUTO_FAILOVER_ENABLED_KEY, true);
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        // Should fail without the forcemanual flag
        Assert.assertEquals((-1), runTool("-transitionToActive", "nn1"));
        Assert.assertTrue(errOutput.contains("Refusing to manually manage"));
        Assert.assertEquals((-1), runTool("-transitionToStandby", "nn1"));
        Assert.assertTrue(errOutput.contains("Refusing to manually manage"));
        Assert.assertEquals((-1), runTool("-transitionToObserver", "nn1"));
        Assert.assertTrue(errOutput.contains("Refusing to manually manage"));
        Mockito.verify(mockProtocol, Mockito.never()).transitionToActive(anyReqInfo());
        Mockito.verify(mockProtocol, Mockito.never()).transitionToStandby(anyReqInfo());
        Mockito.verify(mockProtocol, Mockito.never()).transitionToObserver(anyReqInfo());
        // Force flag should bypass the check and change the request source
        // for the RPC
        TestDFSHAAdmin.setupConfirmationOnSystemIn();
        Assert.assertEquals(0, runTool("-transitionToActive", "-forcemanual", "nn1"));
        TestDFSHAAdmin.setupConfirmationOnSystemIn();
        Assert.assertEquals(0, runTool("-transitionToStandby", "-forcemanual", "nn1"));
        TestDFSHAAdmin.setupConfirmationOnSystemIn();
        Assert.assertEquals(0, runTool("-transitionToObserver", "-forcemanual", "nn1"));
        Mockito.verify(mockProtocol, Mockito.times(1)).transitionToActive(reqInfoCaptor.capture());
        Mockito.verify(mockProtocol, Mockito.times(1)).transitionToStandby(reqInfoCaptor.capture());
        Mockito.verify(mockProtocol, Mockito.times(1)).transitionToObserver(reqInfoCaptor.capture());
        // All of the RPCs should have had the "force" source
        for (StateChangeRequestInfo ri : reqInfoCaptor.getAllValues()) {
            Assert.assertEquals(REQUEST_BY_USER_FORCED, ri.getSource());
        }
    }

    /**
     * Test that, even if automatic HA is enabled, the monitoring operations
     * still function correctly.
     */
    @Test
    public void testMonitoringOperationsWithAutoHaEnabled() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        // Turn on auto-HA
        HdfsConfiguration conf = getHAConf();
        conf.setBoolean(DFS_HA_AUTO_FAILOVER_ENABLED_KEY, true);
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-checkHealth", "nn1"));
        Mockito.verify(mockProtocol).monitorHealth();
        Assert.assertEquals(0, runTool("-getServiceState", "nn1"));
        Mockito.verify(mockProtocol).getServiceStatus();
    }

    @Test
    public void testTransitionToStandby() throws Exception {
        Assert.assertEquals(0, runTool("-transitionToStandby", "nn1"));
        Mockito.verify(mockProtocol).transitionToStandby(anyReqInfo());
    }

    @Test
    public void testTransitionToObserver() throws Exception {
        Assert.assertEquals(0, runTool("-transitionToObserver", "nn1"));
        Mockito.verify(mockProtocol).transitionToObserver(anyReqInfo());
    }

    @Test
    public void testFailoverWithNoFencerConfigured() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        Assert.assertEquals((-1), runTool("-failover", "nn1", "nn2"));
    }

    @Test
    public void testFailoverWithFencerConfigured() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        HdfsConfiguration conf = getHAConf();
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-failover", "nn1", "nn2"));
    }

    @Test
    public void testFailoverWithFencerAndNameservice() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        HdfsConfiguration conf = getHAConf();
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-ns", "ns1", "-failover", "nn1", "nn2"));
    }

    @Test
    public void testFailoverWithFencerConfiguredAndForce() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        HdfsConfiguration conf = getHAConf();
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-failover", "nn1", "nn2", "--forcefence"));
    }

    @Test
    public void testFailoverWithForceActive() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        HdfsConfiguration conf = getHAConf();
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-failover", "nn1", "nn2", "--forceactive"));
    }

    @Test
    public void testFailoverWithInvalidFenceArg() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        HdfsConfiguration conf = getHAConf();
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals((-1), runTool("-failover", "nn1", "nn2", "notforcefence"));
    }

    @Test
    public void testFailoverWithFenceButNoFencer() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        Assert.assertEquals((-1), runTool("-failover", "nn1", "nn2", "--forcefence"));
    }

    @Test
    public void testFailoverWithFenceAndBadFencer() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        HdfsConfiguration conf = getHAConf();
        conf.set(DFS_HA_FENCE_METHODS_KEY, "foobar!");
        tool.setConf(conf);
        Assert.assertEquals((-1), runTool("-failover", "nn1", "nn2", "--forcefence"));
    }

    @Test
    public void testFailoverWithAutoHa() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        // Turn on auto-HA in the config
        HdfsConfiguration conf = getHAConf();
        conf.setBoolean(DFS_HA_AUTO_FAILOVER_ENABLED_KEY, true);
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-failover", "nn1", "nn2"));
        Mockito.verify(mockZkfcProtocol).gracefulFailover();
    }

    @Test
    public void testForceFenceOptionListedBeforeArgs() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        HdfsConfiguration conf = getHAConf();
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-failover", "--forcefence", "nn1", "nn2"));
    }

    @Test
    public void testGetServiceStatus() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        Assert.assertEquals(0, runTool("-getServiceState", "nn1"));
        Mockito.verify(mockProtocol).getServiceStatus();
    }

    @Test
    public void testCheckHealth() throws Exception {
        Assert.assertEquals(0, runTool("-checkHealth", "nn1"));
        Mockito.verify(mockProtocol).monitorHealth();
        Mockito.doThrow(new HealthCheckFailedException("fake health check failure")).when(mockProtocol).monitorHealth();
        Assert.assertEquals((-1), runTool("-checkHealth", "nn1"));
        assertOutputContains("Health check failed: fake health check failure");
    }

    /**
     * Test that the fencing configuration can be overridden per-nameservice
     * or per-namenode
     */
    @Test
    public void testFencingConfigPerNameNode() throws Exception {
        Mockito.doReturn(TestDFSHAAdmin.STANDBY_READY_RESULT).when(mockProtocol).getServiceStatus();
        final String nsSpecificKey = ((DFSConfigKeys.DFS_HA_FENCE_METHODS_KEY) + ".") + (TestDFSHAAdmin.NSID);
        final String nnSpecificKey = nsSpecificKey + ".nn1";
        HdfsConfiguration conf = getHAConf();
        // Set the default fencer to succeed
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-failover", "nn1", "nn2", "--forcefence"));
        // Set the NN-specific fencer to fail. Should fail to fence.
        conf.set(nnSpecificKey, TestDFSHAAdmin.getFencerFalseCommand());
        tool.setConf(conf);
        Assert.assertEquals((-1), runTool("-failover", "nn1", "nn2", "--forcefence"));
        conf.unset(nnSpecificKey);
        // Set an NS-specific fencer to fail. Should fail.
        conf.set(nsSpecificKey, TestDFSHAAdmin.getFencerFalseCommand());
        tool.setConf(conf);
        Assert.assertEquals((-1), runTool("-failover", "nn1", "nn2", "--forcefence"));
        // Set the NS-specific fencer to succeed. Should succeed
        conf.set(nsSpecificKey, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-failover", "nn1", "nn2", "--forcefence"));
    }
}

