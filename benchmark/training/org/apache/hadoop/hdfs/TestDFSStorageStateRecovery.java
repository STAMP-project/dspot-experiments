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
package org.apache.hadoop.hdfs;


import DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY;
import StartupOption.REGULAR;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test ensures the appropriate response (successful or failure) from
 * the system when the system is started under various storage state and
 * version conditions.
 */
public class TestDFSStorageStateRecovery {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestDFSStorageStateRecovery");

    private Configuration conf = null;

    private int testCounter = 0;

    private MiniDFSCluster cluster = null;

    // Constants for indexes into test case table below.
    private static final int CURRENT_EXISTS = 0;

    private static final int PREVIOUS_EXISTS = 1;

    private static final int PREVIOUS_TMP_EXISTS = 2;

    private static final int REMOVED_TMP_EXISTS = 3;

    private static final int SHOULD_RECOVER = 4;

    private static final int CURRENT_SHOULD_EXIST_AFTER_RECOVER = 5;

    private static final int PREVIOUS_SHOULD_EXIST_AFTER_RECOVER = 6;

    /**
     * The test case table.  Each row represents a test case.  This table is
     * taken from the table in Apendix A of the HDFS Upgrade Test Plan
     * (TestPlan-HdfsUpgrade.html) attached to
     * http://issues.apache.org/jira/browse/HADOOP-702
     *
     * It has been slightly modified since previouscheckpoint.tmp no longer
     * exists.
     *
     * The column meanings are:
     *  0) current directory exists
     *  1) previous directory exists
     *  2) previous.tmp directory exists
     *  3) removed.tmp directory exists
     *  4) node should recover and startup
     *  5) current directory should exist after recovery but before startup
     *  6) previous directory should exist after recovery but before startup
     */
    static final boolean[][] testCases = new boolean[][]{ new boolean[]{ true, false, false, false, true, true, false }// 1
    // 1
    // 1
    , new boolean[]{ true, true, false, false, true, true, true }// 2
    // 2
    // 2
    , new boolean[]{ true, false, true, false, true, true, true }// 3
    // 3
    // 3
    , new boolean[]{ true, true, true, true, false, false, false }// 4
    // 4
    // 4
    , new boolean[]{ true, true, true, false, false, false, false }// 4
    // 4
    // 4
    , new boolean[]{ false, true, true, true, false, false, false }// 4
    // 4
    // 4
    , new boolean[]{ false, true, true, false, false, false, false }// 4
    // 4
    // 4
    , new boolean[]{ false, false, false, false, false, false, false }// 5
    // 5
    // 5
    , new boolean[]{ false, true, false, false, false, false, false }// 6
    // 6
    // 6
    , new boolean[]{ false, false, true, false, true, true, false }// 7
    // 7
    // 7
    , new boolean[]{ true, false, false, true, true, true, false }// 8
    // 8
    // 8
    , new boolean[]{ true, true, false, true, false, false, false }// 9
    // 9
    // 9
    , new boolean[]{ true, true, true, true, false, false, false }// 10
    // 10
    // 10
    , new boolean[]{ true, false, true, true, false, false, false }// 10
    // 10
    // 10
    , new boolean[]{ false, true, true, true, false, false, false }// 10
    // 10
    // 10
    , new boolean[]{ false, false, true, true, false, false, false }// 10
    // 10
    // 10
    , new boolean[]{ false, false, false, true, false, false, false }// 11
    // 11
    // 11
    , new boolean[]{ false, true, false, true, true, true, true }// 12
    // 12
    // 12
    , // name-node specific cases
    new boolean[]{ true, true, false, false, true, true, false }// 13
    // 13
    // 13
     };

    private static final int NUM_NN_TEST_CASES = TestDFSStorageStateRecovery.testCases.length;

    private static final int NUM_DN_TEST_CASES = 18;

    /**
     * This test iterates over the testCases table and attempts
     * to startup the NameNode normally.
     */
    @Test
    public void testNNStorageStates() throws Exception {
        String[] baseDirs;
        for (int numDirs = 1; numDirs <= 2; numDirs++) {
            conf = new HdfsConfiguration();
            conf.setInt(DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, (-1));
            conf = UpgradeUtilities.initializeStorageStateConf(numDirs, conf);
            for (int i = 0; i < (TestDFSStorageStateRecovery.NUM_NN_TEST_CASES); i++) {
                boolean[] testCase = TestDFSStorageStateRecovery.testCases[i];
                boolean shouldRecover = testCase[TestDFSStorageStateRecovery.SHOULD_RECOVER];
                boolean curAfterRecover = testCase[TestDFSStorageStateRecovery.CURRENT_SHOULD_EXIST_AFTER_RECOVER];
                boolean prevAfterRecover = testCase[TestDFSStorageStateRecovery.PREVIOUS_SHOULD_EXIST_AFTER_RECOVER];
                log("NAME_NODE recovery", numDirs, i, testCase);
                baseDirs = createNameNodeStorageState(testCase);
                if (shouldRecover) {
                    cluster = createCluster(conf);
                    checkResultNameNode(baseDirs, curAfterRecover, prevAfterRecover);
                    cluster.shutdown();
                } else {
                    try {
                        cluster = createCluster(conf);
                        throw new AssertionError("NameNode should have failed to start");
                    } catch (IOException expected) {
                        // the exception is expected
                        // check that the message says "not formatted"
                        // when storage directory is empty (case #5)
                        if ((((!(TestDFSStorageStateRecovery.testCases[i][TestDFSStorageStateRecovery.CURRENT_EXISTS])) && (!(TestDFSStorageStateRecovery.testCases[i][TestDFSStorageStateRecovery.PREVIOUS_TMP_EXISTS]))) && (!(TestDFSStorageStateRecovery.testCases[i][TestDFSStorageStateRecovery.PREVIOUS_EXISTS]))) && (!(TestDFSStorageStateRecovery.testCases[i][TestDFSStorageStateRecovery.REMOVED_TMP_EXISTS]))) {
                            Assert.assertTrue(expected.getLocalizedMessage().contains("NameNode is not formatted"));
                        }
                    }
                }
                cluster.shutdown();
            }// end testCases loop

        }// end numDirs loop

    }

    /**
     * This test iterates over the testCases table for Datanode storage and
     * attempts to startup the DataNode normally.
     */
    @Test
    public void testDNStorageStates() throws Exception {
        String[] baseDirs;
        // First setup the datanode storage directory
        for (int numDirs = 1; numDirs <= 2; numDirs++) {
            conf = new HdfsConfiguration();
            conf.setInt(DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, (-1));
            conf = UpgradeUtilities.initializeStorageStateConf(numDirs, conf);
            for (int i = 0; i < (TestDFSStorageStateRecovery.NUM_DN_TEST_CASES); i++) {
                boolean[] testCase = TestDFSStorageStateRecovery.testCases[i];
                boolean shouldRecover = testCase[TestDFSStorageStateRecovery.SHOULD_RECOVER];
                boolean curAfterRecover = testCase[TestDFSStorageStateRecovery.CURRENT_SHOULD_EXIST_AFTER_RECOVER];
                boolean prevAfterRecover = testCase[TestDFSStorageStateRecovery.PREVIOUS_SHOULD_EXIST_AFTER_RECOVER];
                log("DATA_NODE recovery", numDirs, i, testCase);
                createNameNodeStorageState(new boolean[]{ true, true, false, false, false });
                cluster = createCluster(conf);
                baseDirs = createDataNodeStorageState(testCase);
                if ((((!(testCase[TestDFSStorageStateRecovery.CURRENT_EXISTS])) && (!(testCase[TestDFSStorageStateRecovery.PREVIOUS_EXISTS]))) && (!(testCase[TestDFSStorageStateRecovery.PREVIOUS_TMP_EXISTS]))) && (!(testCase[TestDFSStorageStateRecovery.REMOVED_TMP_EXISTS]))) {
                    // DataNode will create and format current if no directories exist
                    cluster.startDataNodes(conf, 1, false, REGULAR, null);
                } else {
                    if (shouldRecover) {
                        cluster.startDataNodes(conf, 1, false, REGULAR, null);
                        checkResultDataNode(baseDirs, curAfterRecover, prevAfterRecover);
                    } else {
                        cluster.startDataNodes(conf, 1, false, REGULAR, null);
                        Assert.assertFalse(cluster.getDataNodes().get(0).isDatanodeUp());
                    }
                }
                cluster.shutdown();
            }// end testCases loop

        }// end numDirs loop

    }

    /**
     * This test iterates over the testCases table for block pool storage and
     * attempts to startup the DataNode normally.
     */
    @Test
    public void testBlockPoolStorageStates() throws Exception {
        String[] baseDirs;
        // First setup the datanode storage directory
        String bpid = UpgradeUtilities.getCurrentBlockPoolID(null);
        for (int numDirs = 1; numDirs <= 2; numDirs++) {
            conf = new HdfsConfiguration();
            conf.setInt("dfs.datanode.scan.period.hours", (-1));
            conf = UpgradeUtilities.initializeStorageStateConf(numDirs, conf);
            for (int i = 0; i < (TestDFSStorageStateRecovery.NUM_DN_TEST_CASES); i++) {
                boolean[] testCase = TestDFSStorageStateRecovery.testCases[i];
                boolean shouldRecover = testCase[TestDFSStorageStateRecovery.SHOULD_RECOVER];
                boolean curAfterRecover = testCase[TestDFSStorageStateRecovery.CURRENT_SHOULD_EXIST_AFTER_RECOVER];
                boolean prevAfterRecover = testCase[TestDFSStorageStateRecovery.PREVIOUS_SHOULD_EXIST_AFTER_RECOVER];
                log("BLOCK_POOL recovery", numDirs, i, testCase);
                createNameNodeStorageState(new boolean[]{ true, true, false, false, false });
                cluster = createCluster(conf);
                baseDirs = createBlockPoolStorageState(bpid, testCase);
                if ((((!(testCase[TestDFSStorageStateRecovery.CURRENT_EXISTS])) && (!(testCase[TestDFSStorageStateRecovery.PREVIOUS_EXISTS]))) && (!(testCase[TestDFSStorageStateRecovery.PREVIOUS_TMP_EXISTS]))) && (!(testCase[TestDFSStorageStateRecovery.REMOVED_TMP_EXISTS]))) {
                    // DataNode will create and format current if no directories exist
                    cluster.startDataNodes(conf, 1, false, REGULAR, null);
                } else {
                    if (shouldRecover) {
                        cluster.startDataNodes(conf, 1, false, REGULAR, null);
                        checkResultBlockPool(baseDirs, curAfterRecover, prevAfterRecover);
                    } else {
                        cluster.startDataNodes(conf, 1, false, REGULAR, null);
                        Assert.assertFalse(cluster.getDataNodes().get(0).isBPServiceAlive(bpid));
                    }
                }
                cluster.shutdown();
            }// end testCases loop

        }// end numDirs loop

    }
}

