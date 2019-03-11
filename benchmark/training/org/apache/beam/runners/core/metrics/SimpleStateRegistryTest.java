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
package org.apache.beam.runners.core.metrics;


import SimpleMonitoringInfoBuilder.FINISH_BUNDLE_MSECS_URN;
import SimpleMonitoringInfoBuilder.PROCESS_BUNDLE_MSECS_URN;
import SimpleMonitoringInfoBuilder.PTRANSFORM_LABEL;
import SimpleMonitoringInfoBuilder.START_BUNDLE_MSECS_URN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.beam.model.pipeline.v1.MetricsApi.MonitoringInfo;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static ExecutionStateTracker.FINISH_STATE_NAME;
import static ExecutionStateTracker.PROCESS_STATE_NAME;
import static ExecutionStateTracker.START_STATE_NAME;
import static SimpleMonitoringInfoBuilder.FINISH_BUNDLE_MSECS_URN;
import static SimpleMonitoringInfoBuilder.PROCESS_BUNDLE_MSECS_URN;
import static SimpleMonitoringInfoBuilder.START_BUNDLE_MSECS_URN;


/**
 * Tests for {@link SimpleStateRegistryTest}.
 */
public class SimpleStateRegistryTest {
    @Test
    public void testExecutionTimeUrnsBuildMonitoringInfos() throws Exception {
        String testPTransformId = "pTransformId";
        HashMap<String, String> labelsMetadata = new HashMap<String, String>();
        labelsMetadata.put(PTRANSFORM_LABEL, testPTransformId);
        SimpleExecutionState startState = new SimpleExecutionState(START_STATE_NAME, START_BUNDLE_MSECS_URN, labelsMetadata);
        SimpleExecutionState processState = new SimpleExecutionState(PROCESS_STATE_NAME, PROCESS_BUNDLE_MSECS_URN, labelsMetadata);
        SimpleExecutionState finishState = new SimpleExecutionState(FINISH_STATE_NAME, FINISH_BUNDLE_MSECS_URN, labelsMetadata);
        SimpleStateRegistry testObject = new SimpleStateRegistry();
        testObject.register(startState);
        testObject.register(processState);
        testObject.register(finishState);
        List<MonitoringInfo> testOutput = testObject.getExecutionTimeMonitoringInfos();
        List<Matcher<MonitoringInfo>> matchers = new ArrayList<Matcher<MonitoringInfo>>();
        SimpleMonitoringInfoBuilder builder = new SimpleMonitoringInfoBuilder();
        builder.setUrn(START_BUNDLE_MSECS_URN);
        builder.setInt64Value(0);
        builder.setPTransformLabel(testPTransformId);
        matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
        // Check for execution time metrics for the testPTransformId
        builder = new SimpleMonitoringInfoBuilder();
        builder.setUrn(PROCESS_BUNDLE_MSECS_URN);
        builder.setInt64Value(0);
        builder.setPTransformLabel(testPTransformId);
        matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
        builder = new SimpleMonitoringInfoBuilder();
        builder.setUrn(FINISH_BUNDLE_MSECS_URN);
        builder.setInt64Value(0);
        builder.setPTransformLabel(testPTransformId);
        matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
        for (Matcher<MonitoringInfo> matcher : matchers) {
            Assert.assertThat(testOutput, Matchers.hasItem(matcher));
        }
    }
}

