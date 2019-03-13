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
package org.apache.beam.runners.dataflow.worker.fn.control;


import com.google.api.services.dataflow.model.CounterUpdate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.beam.model.pipeline.v1.MetricsApi.MonitoringInfo;
import org.apache.beam.runners.core.metrics.SpecMonitoringInfoValidator;
import org.apache.beam.runners.dataflow.worker.DataflowExecutionContext.DataflowStepContext;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class UserMonitoringInfoToCounterUpdateTransformerTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    private SpecMonitoringInfoValidator mockSpecValidator;

    @Test
    public void testTransformReturnsNullIfSpecValidationFails() {
        Map<String, DataflowStepContext> stepContextMapping = new HashMap<>();
        UserMonitoringInfoToCounterUpdateTransformer testObject = new UserMonitoringInfoToCounterUpdateTransformer(mockSpecValidator, stepContextMapping);
        Optional<String> error = Optional.of("Error text");
        Mockito.when(mockSpecValidator.validate(ArgumentMatchers.any())).thenReturn(error);
        Assert.assertEquals(null, testObject.transform(null));
    }

    @Test
    public void testTransformThrowsIfMonitoringInfoWithWrongUrnPrefixReceived() {
        Map<String, DataflowStepContext> stepContextMapping = new HashMap<>();
        MonitoringInfo monitoringInfo = MonitoringInfo.newBuilder().setUrn("beam:metric:element_count:v1").build();
        UserMonitoringInfoToCounterUpdateTransformer testObject = new UserMonitoringInfoToCounterUpdateTransformer(mockSpecValidator, stepContextMapping);
        Mockito.when(mockSpecValidator.validate(ArgumentMatchers.any())).thenReturn(Optional.empty());
        exception.expect(RuntimeException.class);
        testObject.transform(monitoringInfo);
    }

    @Test
    public void testTransformReturnsNullIfMonitoringInfoWithUnknownPTransformLabelPresent() {
        Map<String, DataflowStepContext> stepContextMapping = new HashMap<>();
        MonitoringInfo monitoringInfo = MonitoringInfo.newBuilder().setUrn("beam:metric:user:anyNamespace:anyName").putLabels("PTRANSFORM", "anyValue").build();
        UserMonitoringInfoToCounterUpdateTransformer testObject = new UserMonitoringInfoToCounterUpdateTransformer(mockSpecValidator, stepContextMapping);
        Mockito.when(mockSpecValidator.validate(ArgumentMatchers.any())).thenReturn(Optional.empty());
        Assert.assertEquals(null, testObject.transform(monitoringInfo));
    }

    @Test
    public void testTransformReturnsValidCounterUpdateWhenValidUserMonitoringInfoReceived() {
        Map<String, DataflowStepContext> stepContextMapping = new HashMap<>();
        NameContext nc = NameContext.create("anyStageName", "anyOriginalName", "anySystemName", "anyUserName");
        DataflowStepContext dsc = Mockito.mock(DataflowStepContext.class);
        Mockito.when(dsc.getNameContext()).thenReturn(nc);
        stepContextMapping.put("anyValue", dsc);
        MonitoringInfo monitoringInfo = MonitoringInfo.newBuilder().setUrn("beam:metric:user:anyNamespace:anyName").putLabels("PTRANSFORM", "anyValue").build();
        UserMonitoringInfoToCounterUpdateTransformer testObject = new UserMonitoringInfoToCounterUpdateTransformer(mockSpecValidator, stepContextMapping);
        Mockito.when(mockSpecValidator.validate(ArgumentMatchers.any())).thenReturn(Optional.empty());
        CounterUpdate result = testObject.transform(monitoringInfo);
        Assert.assertNotEquals(null, result);
        Assert.assertEquals(("{cumulative=true, integer={highBits=0, lowBits=0}, " + (("structuredNameAndMetadata={metadata={kind=SUM}, " + "name={name=anyName, origin=USER, originNamespace=anyNamespace, ") + "originalStepName=anyOriginalName}}}")), result.toString());
    }
}

