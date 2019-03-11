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
package org.apache.hadoop.resourceestimator.service;


import MediaType.APPLICATION_XML_TYPE;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.resourceestimator.common.api.RecurrenceId;
import org.apache.hadoop.resourceestimator.common.api.ResourceSkyline;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.RLESparseResourceAllocation;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test ResourceEstimatorService.
 */
public class TestResourceEstimatorService extends JerseyTest {
    private final String parseLogCommand = "resourceestimator/translator/" + "src/test/resources/resourceEstimatorService.txt";

    private final String getHistorySkylineCommand = "resourceestimator/skylinestore/history/tpch_q12/*";

    private final String getEstimatedSkylineCommand = "resourceestimator/skylinestore/estimation/tpch_q12";

    private final String makeEstimationCommand = "resourceestimator/estimator/tpch_q12";

    private final String deleteHistoryCommand = "resourceestimator/skylinestore/history/tpch_q12/tpch_q12_1";

    private static boolean setUpDone = false;

    private Resource containerSpec;

    private Gson gson;

    private long containerMemAlloc;

    private int containerCPUAlloc;

    public TestResourceEstimatorService() {
        super("org.apache.hadoop.resourceestimator.service");
    }

    @Test
    public void testGetPrediction() {
        // first, parse the log
        final String logFile = "resourceEstimatorService.txt";
        WebResource webResource = resource();
        webResource.path(parseLogCommand).type(APPLICATION_XML_TYPE).post(logFile);
        webResource = resource().path(getHistorySkylineCommand);
        String response = webResource.get(String.class);
        Map<RecurrenceId, List<ResourceSkyline>> jobHistory = gson.fromJson(response, new TypeToken<Map<RecurrenceId, List<ResourceSkyline>>>() {}.getType());
        checkResult("tpch_q12_0", jobHistory);
        checkResult("tpch_q12_1", jobHistory);
        // then, try to get estimated resource allocation from skyline store
        webResource = resource().path(getEstimatedSkylineCommand);
        response = webResource.get(String.class);
        Assert.assertEquals("null", response);
        // then, we call estimator module to make the prediction
        webResource = resource().path(makeEstimationCommand);
        response = webResource.get(String.class);
        RLESparseResourceAllocation skylineList = gson.fromJson(response, new TypeToken<RLESparseResourceAllocation>() {}.getType());
        Assert.assertEquals(1, ((skylineList.getCapacityAtTime(0).getMemorySize()) / (containerMemAlloc)));
        Assert.assertEquals(1058, ((skylineList.getCapacityAtTime(10).getMemorySize()) / (containerMemAlloc)));
        Assert.assertEquals(2538, ((skylineList.getCapacityAtTime(15).getMemorySize()) / (containerMemAlloc)));
        Assert.assertEquals(2484, ((skylineList.getCapacityAtTime(20).getMemorySize()) / (containerMemAlloc)));
        // then, we get estimated resource allocation for tpch_q12
        webResource = resource().path(getEstimatedSkylineCommand);
        response = webResource.get(String.class);
        final RLESparseResourceAllocation skylineList2 = gson.fromJson(response, new TypeToken<RLESparseResourceAllocation>() {}.getType());
        compareRLESparseResourceAllocation(skylineList, skylineList2);
        // then, we call estimator module again to directly get estimated resource
        // allocation from skyline store
        webResource = resource().path(makeEstimationCommand);
        response = webResource.get(String.class);
        final RLESparseResourceAllocation skylineList3 = gson.fromJson(response, new TypeToken<RLESparseResourceAllocation>() {}.getType());
        compareRLESparseResourceAllocation(skylineList, skylineList3);
        // finally, test delete
        webResource = resource().path(deleteHistoryCommand);
        webResource.delete();
        webResource = resource().path(getHistorySkylineCommand);
        response = webResource.get(String.class);
        jobHistory = gson.fromJson(response, new TypeToken<Map<RecurrenceId, List<ResourceSkyline>>>() {}.getType());
        // jobHistory should only have info for tpch_q12_0
        Assert.assertEquals(1, jobHistory.size());
        final String pipelineId = getRunId();
        Assert.assertEquals("tpch_q12_0", pipelineId);
    }
}

