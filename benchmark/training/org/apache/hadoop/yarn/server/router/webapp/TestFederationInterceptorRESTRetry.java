/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.router.webapp;


import FederationPolicyUtils.NO_ACTIVE_SUBCLUSTER_AVAILABLE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.federation.store.impl.MemoryFederationStateStore;
import org.apache.hadoop.yarn.server.federation.store.records.GetApplicationHomeSubClusterRequest;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterId;
import org.apache.hadoop.yarn.server.federation.utils.FederationStateStoreTestUtil;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationSubmissionContextInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterMetricsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NewApplication;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodesInfo;
import org.apache.hadoop.yarn.webapp.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Extends the {@code BaseRouterWebServicesTest} and overrides methods in order
 * to use the {@code RouterWebServices} pipeline test cases for testing the
 * {@code FederationInterceptorREST} class. The tests for
 * {@code RouterWebServices} has been written cleverly so that it can be reused
 * to validate different request interceptor chains.
 * <p>
 * It tests the case with SubClusters down and the Router logic of retries. We
 * have 1 good SubCluster and 2 bad ones for all the tests.
 */
public class TestFederationInterceptorRESTRetry extends BaseRouterWebServicesTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestFederationInterceptorRESTRetry.class);

    private static final int SERVICE_UNAVAILABLE = 503;

    private static final int ACCEPTED = 202;

    private static final int OK = 200;

    // running and registered
    private static SubClusterId good;

    // registered but not running
    private static SubClusterId bad1;

    private static SubClusterId bad2;

    private static List<SubClusterId> scs = new ArrayList<SubClusterId>();

    private TestableFederationInterceptorREST interceptor;

    private MemoryFederationStateStore stateStore;

    private FederationStateStoreTestUtil stateStoreUtil;

    private String user = "test-user";

    /**
     * This test validates the correctness of GetNewApplication in case the
     * cluster is composed of only 1 bad SubCluster.
     */
    @Test
    public void testGetNewApplicationOneBadSC() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad2));
        Response response = interceptor.createNewApplication(null);
        Assert.assertEquals(TestFederationInterceptorRESTRetry.SERVICE_UNAVAILABLE, response.getStatus());
        Assert.assertEquals(NO_ACTIVE_SUBCLUSTER_AVAILABLE, response.getEntity());
    }

    /**
     * This test validates the correctness of GetNewApplication in case the
     * cluster is composed of only 2 bad SubClusters.
     */
    @Test
    public void testGetNewApplicationTwoBadSCs() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad1, TestFederationInterceptorRESTRetry.bad2));
        Response response = interceptor.createNewApplication(null);
        Assert.assertEquals(TestFederationInterceptorRESTRetry.SERVICE_UNAVAILABLE, response.getStatus());
        Assert.assertEquals(NO_ACTIVE_SUBCLUSTER_AVAILABLE, response.getEntity());
    }

    /**
     * This test validates the correctness of GetNewApplication in case the
     * cluster is composed of only 1 bad SubCluster and 1 good one.
     */
    @Test
    public void testGetNewApplicationOneBadOneGood() throws IOException, InterruptedException, YarnException {
        System.out.println("Test getNewApplication with one bad, one good SC");
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.good, TestFederationInterceptorRESTRetry.bad2));
        Response response = interceptor.createNewApplication(null);
        Assert.assertEquals(TestFederationInterceptorRESTRetry.OK, response.getStatus());
        NewApplication newApp = ((NewApplication) (response.getEntity()));
        ApplicationId appId = ApplicationId.fromString(newApp.getApplicationId());
        Assert.assertEquals(Integer.parseInt(TestFederationInterceptorRESTRetry.good.getId()), appId.getClusterTimestamp());
    }

    /**
     * This test validates the correctness of SubmitApplication in case the
     * cluster is composed of only 1 bad SubCluster.
     */
    @Test
    public void testSubmitApplicationOneBadSC() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad2));
        ApplicationId appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        ApplicationSubmissionContextInfo context = new ApplicationSubmissionContextInfo();
        context.setApplicationId(appId.toString());
        Response response = interceptor.submitApplication(context, null);
        Assert.assertEquals(TestFederationInterceptorRESTRetry.SERVICE_UNAVAILABLE, response.getStatus());
        Assert.assertEquals(NO_ACTIVE_SUBCLUSTER_AVAILABLE, response.getEntity());
    }

    /**
     * This test validates the correctness of SubmitApplication in case the
     * cluster is composed of only 2 bad SubClusters.
     */
    @Test
    public void testSubmitApplicationTwoBadSCs() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad1, TestFederationInterceptorRESTRetry.bad2));
        ApplicationId appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        ApplicationSubmissionContextInfo context = new ApplicationSubmissionContextInfo();
        context.setApplicationId(appId.toString());
        Response response = interceptor.submitApplication(context, null);
        Assert.assertEquals(TestFederationInterceptorRESTRetry.SERVICE_UNAVAILABLE, response.getStatus());
        Assert.assertEquals(NO_ACTIVE_SUBCLUSTER_AVAILABLE, response.getEntity());
    }

    /**
     * This test validates the correctness of SubmitApplication in case the
     * cluster is composed of only 1 bad SubCluster and a good one.
     */
    @Test
    public void testSubmitApplicationOneBadOneGood() throws IOException, InterruptedException, YarnException {
        System.out.println("Test submitApplication with one bad, one good SC");
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.good, TestFederationInterceptorRESTRetry.bad2));
        ApplicationId appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        ApplicationSubmissionContextInfo context = new ApplicationSubmissionContextInfo();
        context.setApplicationId(appId.toString());
        Response response = interceptor.submitApplication(context, null);
        Assert.assertEquals(TestFederationInterceptorRESTRetry.ACCEPTED, response.getStatus());
        Assert.assertEquals(TestFederationInterceptorRESTRetry.good, stateStore.getApplicationHomeSubCluster(GetApplicationHomeSubClusterRequest.newInstance(appId)).getApplicationHomeSubCluster().getHomeSubCluster());
    }

    /**
     * This test validates the correctness of GetApps in case the cluster is
     * composed of only 1 bad SubCluster.
     */
    @Test
    public void testGetAppsOneBadSC() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad2));
        AppsInfo response = interceptor.getApps(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        Assert.assertNull(response);
    }

    /**
     * This test validates the correctness of GetApps in case the cluster is
     * composed of only 2 bad SubClusters.
     */
    @Test
    public void testGetAppsTwoBadSCs() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad1, TestFederationInterceptorRESTRetry.bad2));
        AppsInfo response = interceptor.getApps(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        Assert.assertNull(response);
    }

    /**
     * This test validates the correctness of GetApps in case the cluster is
     * composed of only 1 bad SubCluster and a good one.
     */
    @Test
    public void testGetAppsOneBadOneGood() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.good, TestFederationInterceptorRESTRetry.bad2));
        AppsInfo response = interceptor.getApps(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getApps().size());
    }

    /**
     * This test validates the correctness of GetNode in case the cluster is
     * composed of only 1 bad SubCluster.
     */
    @Test
    public void testGetNodeOneBadSC() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad2));
        try {
            interceptor.getNode("testGetNodeOneBadSC");
            Assert.fail();
        } catch (NotFoundException e) {
            Assert.assertTrue(e.getMessage().contains("nodeId, testGetNodeOneBadSC, is not found"));
        }
    }

    /**
     * This test validates the correctness of GetNode in case the cluster is
     * composed of only 2 bad SubClusters.
     */
    @Test
    public void testGetNodeTwoBadSCs() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad1, TestFederationInterceptorRESTRetry.bad2));
        try {
            interceptor.getNode("testGetNodeTwoBadSCs");
            Assert.fail();
        } catch (NotFoundException e) {
            Assert.assertTrue(e.getMessage().contains("nodeId, testGetNodeTwoBadSCs, is not found"));
        }
    }

    /**
     * This test validates the correctness of GetNode in case the cluster is
     * composed of only 1 bad SubCluster and a good one.
     */
    @Test
    public void testGetNodeOneBadOneGood() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.good, TestFederationInterceptorRESTRetry.bad2));
        NodeInfo response = interceptor.getNode(null);
        Assert.assertNotNull(response);
        // Check if the only node came from Good SubCluster
        Assert.assertEquals(TestFederationInterceptorRESTRetry.good.getId(), Long.toString(response.getLastHealthUpdate()));
    }

    /**
     * This test validates the correctness of GetNodes in case the cluster is
     * composed of only 1 bad SubCluster.
     */
    @Test
    public void testGetNodesOneBadSC() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad2));
        NodesInfo response = interceptor.getNodes(null);
        Assert.assertNotNull(response);
        Assert.assertEquals(0, response.getNodes().size());
        // The remove duplicate operations is tested in TestRouterWebServiceUtil
    }

    /**
     * This test validates the correctness of GetNodes in case the cluster is
     * composed of only 2 bad SubClusters.
     */
    @Test
    public void testGetNodesTwoBadSCs() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad1, TestFederationInterceptorRESTRetry.bad2));
        NodesInfo response = interceptor.getNodes(null);
        Assert.assertNotNull(response);
        Assert.assertEquals(0, response.getNodes().size());
        // The remove duplicate operations is tested in TestRouterWebServiceUtil
    }

    /**
     * This test validates the correctness of GetNodes in case the cluster is
     * composed of only 1 bad SubCluster and a good one.
     */
    @Test
    public void testGetNodesOneBadOneGood() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.good, TestFederationInterceptorRESTRetry.bad2));
        NodesInfo response = interceptor.getNodes(null);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getNodes().size());
        // Check if the only node came from Good SubCluster
        Assert.assertEquals(TestFederationInterceptorRESTRetry.good.getId(), Long.toString(response.getNodes().get(0).getLastHealthUpdate()));
        // The remove duplicate operations is tested in TestRouterWebServiceUtil
    }

    /**
     * This test validates the correctness of GetNodes in case the cluster is
     * composed of only 1 bad SubCluster. The excepted result would be a
     * ClusterMetricsInfo with all its values set to 0.
     */
    @Test
    public void testGetClusterMetricsOneBadSC() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad2));
        ClusterMetricsInfo response = interceptor.getClusterMetricsInfo();
        Assert.assertNotNull(response);
        // check if we got an empty metrics
        checkEmptyMetrics(response);
    }

    /**
     * This test validates the correctness of GetClusterMetrics in case the
     * cluster is composed of only 2 bad SubClusters. The excepted result would be
     * a ClusterMetricsInfo with all its values set to 0.
     */
    @Test
    public void testGetClusterMetricsTwoBadSCs() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.bad1, TestFederationInterceptorRESTRetry.bad2));
        ClusterMetricsInfo response = interceptor.getClusterMetricsInfo();
        Assert.assertNotNull(response);
        // check if we got an empty metrics
        Assert.assertEquals(0, response.getAppsSubmitted());
    }

    /**
     * This test validates the correctness of GetClusterMetrics in case the
     * cluster is composed of only 1 bad SubCluster and a good one. The good
     * SubCluster provided a ClusterMetricsInfo with appsSubmitted set to its
     * SubClusterId. The expected result would be appSubmitted equals to its
     * SubClusterId. SubClusterId in this case is an integer.
     */
    @Test
    public void testGetClusterMetricsOneBadOneGood() throws IOException, InterruptedException, YarnException {
        setupCluster(Arrays.asList(TestFederationInterceptorRESTRetry.good, TestFederationInterceptorRESTRetry.bad2));
        ClusterMetricsInfo response = interceptor.getClusterMetricsInfo();
        Assert.assertNotNull(response);
        checkMetricsFromGoodSC(response);
        // The merge operations is tested in TestRouterWebServiceUtil
    }
}

