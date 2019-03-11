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


import SimpleMonitoringInfoBuilder.ELEMENT_COUNT_URN;
import SimpleMonitoringInfoBuilder.SUM_INT64_TYPE_URN;
import org.apache.beam.model.pipeline.v1.MetricsApi.MonitoringInfo;
import org.junit.Assert;
import org.junit.Test;

import static SimpleMonitoringInfoBuilder.USER_COUNTER_URN_PREFIX;


/**
 * Relevant tests.
 */
public class SpecMonitoringInfoValidatorTest {
    SpecMonitoringInfoValidator testObject = null;

    @Test
    public void validateReturnsErrorOnInvalidMonitoringInfoType() {
        MonitoringInfo testInput = MonitoringInfo.newBuilder().setUrn("beam:metric:user:someCounter").setType("beam:metrics:bad_value").build();
        Assert.assertTrue(testObject.validate(testInput).isPresent());
    }

    @Test
    public void validateReturnsNoErrorOnValidMonitoringInfo() {
        MonitoringInfo testInput = MonitoringInfo.newBuilder().setUrn(((USER_COUNTER_URN_PREFIX) + "someCounter")).setType(SUM_INT64_TYPE_URN).putLabels("dummy", "value").build();
        Assert.assertFalse(testObject.validate(testInput).isPresent());
        testInput = MonitoringInfo.newBuilder().setUrn(ELEMENT_COUNT_URN).setType(SUM_INT64_TYPE_URN).putLabels("PTRANSFORM", "value").putLabels("PCOLLECTION", "anotherValue").build();
        Assert.assertFalse(testObject.validate(testInput).isPresent());
    }

    @Test
    public void validateReturnsErrorOnInvalidMonitoringInfoLabels() {
        MonitoringInfo testInput = MonitoringInfo.newBuilder().setUrn(ELEMENT_COUNT_URN).setType(SUM_INT64_TYPE_URN).putLabels("PTRANSFORM", "unexpectedLabel").build();
        Assert.assertTrue(testObject.validate(testInput).isPresent());
    }
}

