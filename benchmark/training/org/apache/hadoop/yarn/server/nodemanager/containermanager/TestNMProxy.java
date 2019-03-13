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
package org.apache.hadoop.yarn.server.nodemanager.containermanager;


import CommonConfigurationKeysPublic.IPC_CLIENT_CONNECT_MAX_RETRIES_KEY;
import CommonConfigurationKeysPublic.IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY;
import YarnConfiguration.CLIENT_NM_CONNECT_MAX_WAIT_MS;
import YarnConfiguration.CLIENT_NM_CONNECT_RETRY_INTERVAL_MS;
import YarnConfiguration.NM_ADDRESS;
import java.io.IOException;
import java.net.SocketException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;


public class TestNMProxy extends BaseContainerManagerTest {
    public TestNMProxy() throws UnsupportedFileSystemException {
        super();
    }

    int retryCount = 0;

    @Test(timeout = 20000)
    public void testNMProxyRetry() throws Exception {
        conf.setLong(CLIENT_NM_CONNECT_MAX_WAIT_MS, 10000);
        conf.setLong(CLIENT_NM_CONNECT_RETRY_INTERVAL_MS, 100);
        StartContainersRequest allRequests = Records.newRecord(StartContainersRequest.class);
        ContainerManagementProtocol proxy = getNMProxy(conf);
        proxy.startContainers(allRequests);
        Assert.assertEquals(5, retryCount);
        retryCount = 0;
        proxy.stopContainers(Records.newRecord(StopContainersRequest.class));
        Assert.assertEquals(5, retryCount);
        retryCount = 0;
        proxy.getContainerStatuses(Records.newRecord(GetContainerStatusesRequest.class));
        Assert.assertEquals(5, retryCount);
    }

    @Test(timeout = 20000, expected = IOException.class)
    public void testShouldNotRetryForeverForNonNetworkExceptionsOnNMConnections() throws Exception {
        conf.setLong(CLIENT_NM_CONNECT_MAX_WAIT_MS, (-1));
        StartContainersRequest allRequests = Records.newRecord(StartContainersRequest.class);
        ContainerManagementProtocol proxy = getNMProxy(conf);
        retryCount = 0;
        proxy.startContainers(allRequests);
    }

    @Test(timeout = 20000)
    public void testNMProxyRPCRetry() throws Exception {
        conf.setLong(CLIENT_NM_CONNECT_MAX_WAIT_MS, 1000);
        conf.setLong(CLIENT_NM_CONNECT_RETRY_INTERVAL_MS, 100);
        StartContainersRequest allRequests = Records.newRecord(StartContainersRequest.class);
        Configuration newConf = new org.apache.hadoop.yarn.conf.YarnConfiguration(conf);
        newConf.setInt(IPC_CLIENT_CONNECT_MAX_RETRIES_KEY, 100);
        newConf.setInt(IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY, 100);
        // connect to some dummy address so that it can trigger
        // connection failure and RPC level retires.
        newConf.set(NM_ADDRESS, "0.0.0.0:1234");
        ContainerManagementProtocol proxy = getNMProxy(newConf);
        try {
            proxy.startContainers(allRequests);
            Assert.fail("should get socket exception");
        } catch (IOException e) {
            // socket exception should be thrown immediately, without RPC retries.
            Assert.assertTrue((e instanceof SocketException));
        }
    }
}

