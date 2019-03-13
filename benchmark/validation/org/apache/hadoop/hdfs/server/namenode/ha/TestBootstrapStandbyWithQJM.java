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
package org.apache.hadoop.hdfs.server.namenode.ha;


import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.qjournal.MiniJournalCluster;
import org.junit.Test;


/**
 * Test BootstrapStandby when QJM is used for shared edits.
 */
public class TestBootstrapStandbyWithQJM {
    enum UpgradeState {

        NORMAL,
        RECOVER,
        FORMAT;}

    private MiniDFSCluster cluster;

    private MiniJournalCluster jCluster;

    private int nnCount = 3;

    /**
     * BootstrapStandby when the existing NN is standby
     */
    @Test
    public void testBootstrapStandbyWithStandbyNN() throws Exception {
        // make the first NN in standby state
        cluster.transitionToStandby(0);
        bootstrapStandbys();
    }

    /**
     * BootstrapStandby when the existing NN is active
     */
    @Test
    public void testBootstrapStandbyWithActiveNN() throws Exception {
        // make the first NN in active state
        cluster.transitionToActive(0);
        bootstrapStandbys();
    }

    /**
     * Test the bootstrapstandby while the other namenode is in upgrade state.
     * Make sure a previous directory can be created.
     */
    @Test
    public void testUpgrade() throws Exception {
        testUpgrade(TestBootstrapStandbyWithQJM.UpgradeState.NORMAL);
    }

    /**
     * Similar with testUpgrade, but rename nn1's current directory to
     * previous.tmp before bootstrapStandby, and make sure the nn1 is recovered
     * first then converted into upgrade state.
     */
    @Test
    public void testUpgradeWithRecover() throws Exception {
        testUpgrade(TestBootstrapStandbyWithQJM.UpgradeState.RECOVER);
    }

    /**
     * Similar with testUpgrade, but rename nn1's current directory to a random
     * name so that it's not formatted. Make sure the nn1 is formatted and then
     * converted into upgrade state.
     */
    @Test
    public void testUpgradeWithFormat() throws Exception {
        testUpgrade(TestBootstrapStandbyWithQJM.UpgradeState.FORMAT);
    }
}

