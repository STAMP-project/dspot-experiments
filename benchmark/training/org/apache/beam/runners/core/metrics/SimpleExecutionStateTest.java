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


import SimpleMonitoringInfoBuilder.PTRANSFORM_LABEL;
import java.util.HashMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SimpleExecutionState}.
 */
@RunWith(JUnit4.class)
public class SimpleExecutionStateTest {
    @Test
    public void testLabelsAndNameAreExtracted() {
        String stateName = "myState";
        HashMap<String, String> labelsMetadata = new HashMap<String, String>();
        labelsMetadata.put("k1", "v1");
        labelsMetadata.put("k2", "v2");
        SimpleExecutionState testObject = new SimpleExecutionState(stateName, null, labelsMetadata);
        Assert.assertEquals(testObject.getStateName(), stateName);
        Assert.assertEquals(2, testObject.getLabels().size());
        MatcherAssert.assertThat(testObject.getLabels(), Matchers.hasEntry("k1", "v1"));
        MatcherAssert.assertThat(testObject.getLabels(), Matchers.hasEntry("k2", "v2"));
    }

    @Test
    public void testTakeSampleIncrementsTotal() {
        SimpleExecutionState testObject = new SimpleExecutionState("myState", null, null);
        Assert.assertEquals(0, testObject.getTotalMillis());
        testObject.takeSample(10);
        Assert.assertEquals(10, testObject.getTotalMillis());
        testObject.takeSample(5);
        Assert.assertEquals(15, testObject.getTotalMillis());
    }

    @Test
    public void testGetLullReturnsARelevantMessageWithStepName() {
        HashMap<String, String> labelsMetadata = new HashMap<String, String>();
        labelsMetadata.put(PTRANSFORM_LABEL, "myPTransform");
        SimpleExecutionState testObject = new SimpleExecutionState("myState", null, labelsMetadata);
        String message = testObject.getLullMessage(new Thread(), Duration.millis(100000));
        MatcherAssert.assertThat(message, Matchers.containsString("myState"));
        MatcherAssert.assertThat(message, Matchers.containsString("myPTransform"));
    }

    @Test
    public void testGetLullReturnsARelevantMessageWithoutStepNameWithNullLabels() {
        SimpleExecutionState testObject = new SimpleExecutionState("myState", null, null);
        String message = testObject.getLullMessage(new Thread(), Duration.millis(100000));
        MatcherAssert.assertThat(message, Matchers.containsString("myState"));
    }

    @Test
    public void testGetLullReturnsARelevantMessageWithoutStepName() {
        HashMap<String, String> labelsMetadata = new HashMap<String, String>();
        SimpleExecutionState testObject = new SimpleExecutionState("myState", null, labelsMetadata);
        String message = testObject.getLullMessage(new Thread(), Duration.millis(100000));
        MatcherAssert.assertThat(message, Matchers.containsString("myState"));
    }
}

