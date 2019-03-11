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
package org.apache.hadoop.yarn.util.timeline;


import YarnConfiguration.FLOW_NAME_DEFAULT_MAX_SIZE;
import YarnConfiguration.FLOW_NAME_MAX_SIZE;
import java.util.UUID;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for limiting flow name size.
 */
public class TestShortenedFlowName {
    private static final String TEST_FLOW_NAME = "TestFlowName";

    @Test
    public void testRemovingUUID() {
        String flowName = ((TestShortenedFlowName.TEST_FLOW_NAME) + "-") + (UUID.randomUUID());
        flowName = TimelineUtils.removeUUID(flowName);
        Assert.assertEquals(TestShortenedFlowName.TEST_FLOW_NAME, flowName);
    }

    @Test
    public void testShortenedFlowName() {
        YarnConfiguration conf = new YarnConfiguration();
        String flowName = (TestShortenedFlowName.TEST_FLOW_NAME) + (UUID.randomUUID());
        conf.setInt(FLOW_NAME_MAX_SIZE, 8);
        String shortenedFlowName = TimelineUtils.shortenFlowName(flowName, conf);
        Assert.assertEquals("TestFlow", shortenedFlowName);
        conf.setInt(FLOW_NAME_MAX_SIZE, FLOW_NAME_DEFAULT_MAX_SIZE);
        shortenedFlowName = TimelineUtils.shortenFlowName(flowName, conf);
        Assert.assertEquals(TestShortenedFlowName.TEST_FLOW_NAME, shortenedFlowName);
    }
}

