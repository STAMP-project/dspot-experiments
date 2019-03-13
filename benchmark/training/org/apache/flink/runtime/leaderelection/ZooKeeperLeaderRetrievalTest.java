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
package org.apache.flink.runtime.leaderelection;


import HighAvailabilityServices.DEFAULT_JOB_ID;
import HighAvailabilityServicesUtils.AddressResolution.NO_ADDRESS_RESOLUTION;
import JobMaster.JOB_MANAGER_NAME;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import org.apache.curator.test.TestingServer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalService;
import org.apache.flink.runtime.rpc.akka.AkkaRpcServiceUtils;
import org.apache.flink.runtime.util.LeaderRetrievalUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import scala.concurrent.duration.FiniteDuration;


/**
 * Tests for the ZooKeeper based leader election and retrieval.
 */
public class ZooKeeperLeaderRetrievalTest extends TestLogger {
    private TestingServer testingServer;

    private Configuration config;

    private HighAvailabilityServices highAvailabilityServices;

    /**
     * Tests that LeaderRetrievalUtils.findConnectingAddress finds the correct connecting address
     * in case of an old leader address in ZooKeeper and a subsequent election of a new leader.
     * The findConnectingAddress should block until the new leader has been elected and his
     * address has been written to ZooKeeper.
     */
    @Test
    public void testConnectingAddressRetrievalWithDelayedLeaderElection() throws Exception {
        FiniteDuration timeout = new FiniteDuration(1, TimeUnit.MINUTES);
        long sleepingTime = 1000;
        LeaderElectionService leaderElectionService = null;
        LeaderElectionService faultyLeaderElectionService;
        ServerSocket serverSocket;
        InetAddress localHost;
        Thread thread;
        try {
            String wrongAddress = AkkaRpcServiceUtils.getRpcUrl("1.1.1.1", 1234, "foobar", NO_ADDRESS_RESOLUTION, config);
            try {
                localHost = InetAddress.getLocalHost();
                serverSocket = new ServerSocket(0, 50, localHost);
            } catch (UnknownHostException e) {
                // may happen if disconnected. skip test.
                System.err.println("Skipping 'testNetworkInterfaceSelection' test.");
                return;
            } catch (IOException e) {
                // may happen in certain test setups, skip test.
                System.err.println("Skipping 'testNetworkInterfaceSelection' test.");
                return;
            }
            InetSocketAddress correctInetSocketAddress = new InetSocketAddress(localHost, serverSocket.getLocalPort());
            String correctAddress = AkkaRpcServiceUtils.getRpcUrl(localHost.getHostName(), correctInetSocketAddress.getPort(), JOB_MANAGER_NAME, NO_ADDRESS_RESOLUTION, config);
            faultyLeaderElectionService = highAvailabilityServices.getJobManagerLeaderElectionService(DEFAULT_JOB_ID);
            TestingContender wrongLeaderAddressContender = new TestingContender(wrongAddress, faultyLeaderElectionService);
            faultyLeaderElectionService.start(wrongLeaderAddressContender);
            ZooKeeperLeaderRetrievalTest.FindConnectingAddress findConnectingAddress = new ZooKeeperLeaderRetrievalTest.FindConnectingAddress(timeout, highAvailabilityServices.getJobManagerLeaderRetriever(DEFAULT_JOB_ID));
            thread = new Thread(findConnectingAddress);
            thread.start();
            leaderElectionService = highAvailabilityServices.getJobManagerLeaderElectionService(DEFAULT_JOB_ID);
            TestingContender correctLeaderAddressContender = new TestingContender(correctAddress, leaderElectionService);
            Thread.sleep(sleepingTime);
            faultyLeaderElectionService.stop();
            leaderElectionService.start(correctLeaderAddressContender);
            thread.join();
            InetAddress result = findConnectingAddress.getInetAddress();
            // check that we can connect to the localHost
            Socket socket = new Socket();
            try {
                // port 0 = let the OS choose the port
                SocketAddress bindP = new InetSocketAddress(result, 0);
                // machine
                socket.bind(bindP);
                socket.connect(correctInetSocketAddress, 1000);
            } finally {
                socket.close();
            }
        } finally {
            if (leaderElectionService != null) {
                leaderElectionService.stop();
            }
        }
    }

    /**
     * Tests that the LeaderRetrievalUtils.findConnectingAddress stops trying to find the
     * connecting address if no leader address has been specified. The call should return
     * then InetAddress.getLocalHost().
     */
    @Test
    public void testTimeoutOfFindConnectingAddress() throws Exception {
        FiniteDuration timeout = new FiniteDuration(1L, TimeUnit.SECONDS);
        LeaderRetrievalService leaderRetrievalService = highAvailabilityServices.getJobManagerLeaderRetriever(DEFAULT_JOB_ID);
        InetAddress result = LeaderRetrievalUtils.findConnectingAddress(leaderRetrievalService, timeout);
        Assert.assertEquals(InetAddress.getLocalHost(), result);
    }

    static class FindConnectingAddress implements Runnable {
        private final FiniteDuration timeout;

        private final LeaderRetrievalService leaderRetrievalService;

        private InetAddress result;

        private Exception exception;

        public FindConnectingAddress(FiniteDuration timeout, LeaderRetrievalService leaderRetrievalService) {
            this.timeout = timeout;
            this.leaderRetrievalService = leaderRetrievalService;
        }

        @Override
        public void run() {
            try {
                result = LeaderRetrievalUtils.findConnectingAddress(leaderRetrievalService, timeout);
            } catch (Exception e) {
                exception = e;
            }
        }

        public InetAddress getInetAddress() throws Exception {
            if ((exception) != null) {
                throw exception;
            } else {
                return result;
            }
        }
    }
}

