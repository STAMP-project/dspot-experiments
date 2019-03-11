package com.orientechnologies.orient.distributed.impl;


import org.junit.Test;


public class OUDPMulticastNodeManagerIT {
    class MockDiscoveryListener implements ODiscoveryListener {
        int totalNodes = 0;

        @Override
        public synchronized void nodeJoined(NodeData data) {
            (totalNodes)++;
        }

        @Override
        public synchronized void nodeLeft(NodeData data) {
            (totalNodes)--;
        }
    }

    @Test
    public void testMasterElection() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            testMasterElectionWith(3, 2);
            testMasterElectionWith(5, 3);
            testMasterElectionWith(5, 5);
            testMasterElectionWith(10, 6);
        }
    }

    @Test
    public void testJoinAfterMasterElection() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            testJoinAfterMasterElection(3, 2);
            testJoinAfterMasterElection(5, 3);
            testJoinAfterMasterElection(10, 6);
        }
    }
}

