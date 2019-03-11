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


import ClusterMapSnapshotConstants.TIMESTAMP_MS;
import RestUtils.Headers.CONTENT_LENGTH;
import RestUtils.Headers.CONTENT_TYPE;
import RestUtils.Headers.DATE;
import RestUtils.JSON_CONTENT_TYPE;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.rest.MockRestResponseChannel;
import com.github.ambry.rest.RestRequest;
import com.github.ambry.rest.RestResponseChannel;
import com.github.ambry.rest.RestTestUtils;
import com.github.ambry.router.ReadableStreamChannel;
import java.io.IOException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static com.github.ambry.frontend.FrontendTestSecurityServiceFactory.Mode.PostProcessRequest;
import static com.github.ambry.frontend.FrontendTestSecurityServiceFactory.Mode.ProcessRequest;


/**
 * Tests for {@link GetClusterMapSnapshotHandler}
 */
public class GetClusterMapSnapshotHandlerTest {
    private final MockClusterMap clusterMap;

    private final FrontendTestSecurityServiceFactory securityServiceFactory;

    private final GetClusterMapSnapshotHandler handler;

    public GetClusterMapSnapshotHandlerTest() throws IOException {
        FrontendMetrics metrics = new FrontendMetrics(new MetricRegistry());
        clusterMap = new MockClusterMap();
        securityServiceFactory = new FrontendTestSecurityServiceFactory();
        handler = new GetClusterMapSnapshotHandler(securityServiceFactory.getSecurityService(), metrics, clusterMap);
    }

    /**
     * Handles the case where everything works as expected
     *
     * @throws Exception
     * 		
     */
    @Test
    public void handleGoodCaseTest() throws Exception {
        RestRequest restRequest = createRestRequest();
        RestResponseChannel restResponseChannel = new MockRestResponseChannel();
        ReadableStreamChannel channel = sendRequestGetResponse(restRequest, restResponseChannel);
        Assert.assertNotNull("There should be a response", channel);
        Assert.assertNotNull("Date has not been set", restResponseChannel.getHeader(DATE));
        Assert.assertEquals("Content-type is not as expected", JSON_CONTENT_TYPE, restResponseChannel.getHeader(CONTENT_TYPE));
        Assert.assertEquals("Content-length is not as expected", channel.getSize(), Integer.parseInt(((String) (restResponseChannel.getHeader(CONTENT_LENGTH)))));
        JSONObject expected = clusterMap.getSnapshot();
        JSONObject actual = RestTestUtils.getJsonizedResponseBody(channel);
        // remove timestamps because they may differ
        expected.remove(TIMESTAMP_MS);
        actual.remove(TIMESTAMP_MS);
        Assert.assertEquals("Snapshot does not match expected", expected.toString(), actual.toString());
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
        securityServiceFactory.mode = PostProcessRequest;
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
        clusterMap.setExceptionOnSnapshot(new RuntimeException(msg));
        verifyFailureWithMsg(msg);
        clusterMap.setExceptionOnSnapshot(null);
    }
}

