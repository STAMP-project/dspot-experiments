/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.frontend;


import RestServiceErrorCode.InvalidArgs;
import RestServiceErrorCode.MissingArgs;
import RestServiceErrorCode.NotFound;
import RestUtils.Headers.CONTENT_LENGTH;
import RestUtils.Headers.CONTENT_TYPE;
import RestUtils.Headers.DATE;
import RestUtils.JSON_CONTENT_TYPE;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.rest.MockRestResponseChannel;
import com.github.ambry.rest.RestRequest;
import com.github.ambry.rest.RestResponseChannel;
import com.github.ambry.rest.RestTestUtils;
import com.github.ambry.router.ReadableStreamChannel;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static com.github.ambry.frontend.FrontendTestSecurityServiceFactory.Mode.PostProcessRequest;
import static com.github.ambry.frontend.FrontendTestSecurityServiceFactory.Mode.ProcessRequest;


/**
 * Tests for {@link GetPeersHandler}.
 */
public class GetPeersHandlerTest {
    private final TailoredPeersClusterMap clusterMap;

    private final FrontendTestSecurityServiceFactory securityServiceFactory;

    private final GetPeersHandler getPeersHandler;

    public GetPeersHandlerTest() {
        FrontendMetrics metrics = new FrontendMetrics(new MetricRegistry());
        clusterMap = new TailoredPeersClusterMap();
        securityServiceFactory = new FrontendTestSecurityServiceFactory();
        getPeersHandler = new GetPeersHandler(clusterMap, securityServiceFactory.getSecurityService(), metrics);
    }

    /**
     * Tests for the good cases where the datanodes are correct. The peers obtained from the response is compared
     * against the ground truth.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void handleGoodCaseTest() throws Exception {
        for (String datanode : TailoredPeersClusterMap.DATANODE_NAMES) {
            RestRequest restRequest = getRestRequest(datanode);
            RestResponseChannel restResponseChannel = new MockRestResponseChannel();
            ReadableStreamChannel channel = sendRequestGetResponse(restRequest, restResponseChannel);
            Assert.assertNotNull("There should be a response", channel);
            Assert.assertNotNull("Date has not been set", restResponseChannel.getHeader(DATE));
            Assert.assertEquals("Content-type is not as expected", JSON_CONTENT_TYPE, restResponseChannel.getHeader(CONTENT_TYPE));
            Assert.assertEquals("Content-length is not as expected", channel.getSize(), Integer.parseInt(((String) (restResponseChannel.getHeader(CONTENT_LENGTH)))));
            Set<String> expectedPeers = clusterMap.getPeers(datanode);
            Set<String> peersFromResponse = GetPeersHandlerTest.getPeersFromResponse(RestTestUtils.getJsonizedResponseBody(channel));
            Assert.assertEquals(("Peer list returned does not match expected for " + datanode), expectedPeers, peersFromResponse);
        }
    }

    /**
     * Tests the case where the {@link SecurityService} denies the request.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void securityServiceDenialTest() throws Exception {
        String msg = "@@expected";
        securityServiceFactory.exceptionToReturn = new IllegalStateException(msg);
        securityServiceFactory.mode = ProcessRequest;
        verifyFailureWithMsg(msg);
        securityServiceFactory.mode = PostProcessRequest;
        verifyFailureWithMsg(msg);
        securityServiceFactory.exceptionToThrow = new IllegalStateException(msg);
        securityServiceFactory.exceptionToReturn = null;
        verifyFailureWithMsg(msg);
        securityServiceFactory.mode = ProcessRequest;
        verifyFailureWithMsg(msg);
    }

    /**
     * Tests the case where the {@link ClusterMap} throws exceptions.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badClusterMapTest() throws Exception {
        String msg = "@@expected";
        clusterMap.exceptionToThrow = new IllegalStateException(msg);
        verifyFailureWithMsg(msg);
    }

    /**
     * Tests cases where the arguments are either missing, incorrect or do no refer to known datanodes.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badArgsTest() throws Exception {
        doBadArgsTest(null, "100", MissingArgs);
        doBadArgsTest("host", null, MissingArgs);
        doBadArgsTest("host", "abc", InvalidArgs);
        doBadArgsTest("non-existent-host", "100", NotFound);
        String host = TailoredPeersClusterMap.DATANODE_NAMES[0].split(":")[0];
        doBadArgsTest(host, "-1", NotFound);
    }
}

