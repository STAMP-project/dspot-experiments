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
package org.apache.hadoop.yarn.client;


import HAServiceProtocol.RequestSource;
import HAServiceProtocol.StateChangeRequestInfo;
import STATE.INITED;
import STATE.STARTED;
import YarnConfiguration.AUTO_FAILOVER_ENABLED;
import YarnConfiguration.PROXY_ADDRESS;
import YarnConfiguration.RM_CLUSTER_ID;
import YarnConfiguration.RM_ZK_ADDRESS;
import YarnConfiguration.RM_ZK_TIMEOUT_MS;
import YarnWebParams.NEXT_REFRESH_INTERVAL;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.ClientBaseWithFixes;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.RMCriticalThreadUncaughtExceptionHandler;
import org.apache.hadoop.yarn.server.resourcemanager.RMFatalEventType;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.webproxy.WebAppProxyServer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRMFailover extends ClientBaseWithFixes {
    private static final Logger LOG = LoggerFactory.getLogger(TestRMFailover.class.getName());

    private static final StateChangeRequestInfo req = new org.apache.hadoop.ha.HAServiceProtocol.StateChangeRequestInfo(RequestSource.REQUEST_BY_USER);

    private static final String RM1_NODE_ID = "rm1";

    private static final int RM1_PORT_BASE = 10000;

    private static final String RM2_NODE_ID = "rm2";

    private static final int RM2_PORT_BASE = 20000;

    private Configuration conf;

    private MiniYARNCluster cluster;

    private ApplicationId fakeAppId;

    @Test
    public void testExplicitFailover() throws IOException, InterruptedException, YarnException {
        conf.setBoolean(AUTO_FAILOVER_ENABLED, false);
        cluster.init(conf);
        cluster.start();
        Assert.assertFalse("RM never turned active", ((-1) == (cluster.getActiveRMIndex())));
        verifyConnections();
        explicitFailover();
        verifyConnections();
        explicitFailover();
        verifyConnections();
    }

    @Test
    public void testAutomaticFailover() throws IOException, InterruptedException, YarnException {
        conf.set(RM_CLUSTER_ID, "yarn-test-cluster");
        conf.set(RM_ZK_ADDRESS, hostPort);
        conf.setInt(RM_ZK_TIMEOUT_MS, 2000);
        cluster.init(conf);
        cluster.start();
        Assert.assertFalse("RM never turned active", ((-1) == (cluster.getActiveRMIndex())));
        verifyConnections();
        failover();
        verifyConnections();
        failover();
        verifyConnections();
        // Make the current Active handle an RMFatalEvent,
        // so it transitions to standby.
        ResourceManager rm = cluster.getResourceManager(cluster.getActiveRMIndex());
        rm.getRMContext().getDispatcher().getEventHandler().handle(new org.apache.hadoop.yarn.server.resourcemanager.RMFatalEvent(RMFatalEventType.STATE_STORE_FENCED, "test"));
        verifyRMTransitionToStandby(rm);
        verifyConnections();
    }

    @Test
    public void testWebAppProxyInStandAloneMode() throws IOException, InterruptedException, YarnException {
        conf.setBoolean(AUTO_FAILOVER_ENABLED, false);
        WebAppProxyServer webAppProxyServer = new WebAppProxyServer();
        try {
            conf.set(PROXY_ADDRESS, "0.0.0.0:9099");
            cluster.init(conf);
            cluster.start();
            getAdminService(0).transitionToActive(TestRMFailover.req);
            Assert.assertFalse("RM never turned active", ((-1) == (cluster.getActiveRMIndex())));
            verifyConnections();
            webAppProxyServer.init(conf);
            // Start webAppProxyServer
            Assert.assertEquals(INITED, webAppProxyServer.getServiceState());
            webAppProxyServer.start();
            Assert.assertEquals(STARTED, webAppProxyServer.getServiceState());
            // send httpRequest with fakeApplicationId
            // expect to get "Not Found" response and 404 response code
            URL wrongUrl = new URL(("http://0.0.0.0:9099/proxy/" + (fakeAppId)));
            HttpURLConnection proxyConn = ((HttpURLConnection) (wrongUrl.openConnection()));
            proxyConn.connect();
            verifyResponse(proxyConn);
            explicitFailover();
            verifyConnections();
            proxyConn.connect();
            verifyResponse(proxyConn);
        } finally {
            webAppProxyServer.stop();
        }
    }

    @Test
    public void testEmbeddedWebAppProxy() throws IOException, InterruptedException, YarnException {
        conf.setBoolean(AUTO_FAILOVER_ENABLED, false);
        cluster.init(conf);
        cluster.start();
        Assert.assertFalse("RM never turned active", ((-1) == (cluster.getActiveRMIndex())));
        verifyConnections();
        // send httpRequest with fakeApplicationId
        // expect to get "Not Found" response and 404 response code
        URL wrongUrl = new URL(("http://0.0.0.0:18088/proxy/" + (fakeAppId)));
        HttpURLConnection proxyConn = ((HttpURLConnection) (wrongUrl.openConnection()));
        proxyConn.connect();
        verifyResponse(proxyConn);
        explicitFailover();
        verifyConnections();
        proxyConn.connect();
        verifyResponse(proxyConn);
    }

    @Test
    public void testRMWebAppRedirect() throws IOException, InterruptedException, YarnException {
        cluster = new MiniYARNCluster(TestRMFailover.class.getName(), 2, 0, 1, 1);
        conf.setBoolean(AUTO_FAILOVER_ENABLED, false);
        cluster.init(conf);
        cluster.start();
        getAdminService(0).transitionToActive(TestRMFailover.req);
        String rm1Url = "http://0.0.0.0:18088";
        String rm2Url = "http://0.0.0.0:28088";
        String redirectURL = TestRMFailover.getRedirectURL(rm2Url);
        // if uri is null, RMWebAppFilter will append a slash at the trail of the redirection url
        Assert.assertEquals(redirectURL, (rm1Url + "/"));
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/metrics"));
        Assert.assertEquals(redirectURL, (rm1Url + "/metrics"));
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/jmx?param1=value1+x&param2=y"));
        Assert.assertEquals((rm1Url + "/jmx?param1=value1+x&param2=y"), redirectURL);
        // standby RM links /conf, /stacks, /logLevel, /static, /logs,
        // /cluster/cluster as well as webService
        // /ws/v1/cluster/info should not be redirected to active RM
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/cluster/cluster"));
        Assert.assertNull(redirectURL);
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/conf"));
        Assert.assertNull(redirectURL);
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/stacks"));
        Assert.assertNull(redirectURL);
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/logLevel"));
        Assert.assertNull(redirectURL);
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/static"));
        Assert.assertNull(redirectURL);
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/logs"));
        Assert.assertNull(redirectURL);
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/ws/v1/cluster/info"));
        Assert.assertNull(redirectURL);
        redirectURL = TestRMFailover.getRedirectURL((rm2Url + "/ws/v1/cluster/apps"));
        Assert.assertEquals(redirectURL, (rm1Url + "/ws/v1/cluster/apps"));
        redirectURL = TestRMFailover.getRedirectURL(((rm2Url + "/proxy/") + (fakeAppId)));
        Assert.assertNull(redirectURL);
        // transit the active RM to standby
        // Both of RMs are in standby mode
        getAdminService(0).transitionToStandby(TestRMFailover.req);
        // RM2 is expected to send the httpRequest to itself.
        // The Header Field: Refresh is expected to be set.
        redirectURL = TestRMFailover.getRefreshURL(rm2Url);
        Assert.assertTrue((((redirectURL != null) && (redirectURL.contains(NEXT_REFRESH_INTERVAL))) && (redirectURL.contains(rm2Url))));
    }

    /**
     * Throw {@link RuntimeException} inside a thread of
     * {@link ResourceManager} with HA enabled and check if the
     * {@link ResourceManager} is transited to standby state.
     *
     * @throws InterruptedException
     * 		if any
     */
    @Test
    public void testUncaughtExceptionHandlerWithHAEnabled() throws InterruptedException {
        conf.set(RM_CLUSTER_ID, "yarn-test-cluster");
        conf.set(RM_ZK_ADDRESS, hostPort);
        cluster.init(conf);
        cluster.start();
        Assert.assertFalse("RM never turned active", ((-1) == (cluster.getActiveRMIndex())));
        ResourceManager resourceManager = cluster.getResourceManager(cluster.getActiveRMIndex());
        final RMCriticalThreadUncaughtExceptionHandler exHandler = new RMCriticalThreadUncaughtExceptionHandler(resourceManager.getRMContext());
        // Create a thread and throw a RTE inside it
        final RuntimeException rte = new RuntimeException("TestRuntimeException");
        final Thread testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw rte;
            }
        });
        testThread.setName("TestThread");
        testThread.setUncaughtExceptionHandler(exHandler);
        testThread.start();
        testThread.join();
        verifyRMTransitionToStandby(resourceManager);
    }

    /**
     * Throw {@link RuntimeException} inside a thread of
     * {@link ResourceManager} with HA disabled and check
     * {@link RMCriticalThreadUncaughtExceptionHandler} instance.
     *
     * Used {@link ExitUtil} class to avoid jvm exit through
     * {@code System.exit(-1)}.
     *
     * @throws InterruptedException
     * 		if any
     */
    @Test
    public void testUncaughtExceptionHandlerWithoutHA() throws InterruptedException {
        ExitUtil.disableSystemExit();
        // Create a MockRM and start it
        ResourceManager resourceManager = new MockRM();
        start();
        start();
        resourceManager.getRMContext().getContainerTokenSecretManager().rollMasterKey();
        final RMCriticalThreadUncaughtExceptionHandler exHandler = new RMCriticalThreadUncaughtExceptionHandler(resourceManager.getRMContext());
        final RMCriticalThreadUncaughtExceptionHandler spyRTEHandler = Mockito.spy(exHandler);
        // Create a thread and throw a RTE inside it
        final RuntimeException rte = new RuntimeException("TestRuntimeException");
        final Thread testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw rte;
            }
        });
        testThread.setName("TestThread");
        testThread.setUncaughtExceptionHandler(spyRTEHandler);
        Assert.assertSame(spyRTEHandler, testThread.getUncaughtExceptionHandler());
        testThread.start();
        testThread.join();
        Mockito.verify(spyRTEHandler).uncaughtException(testThread, rte);
    }
}

