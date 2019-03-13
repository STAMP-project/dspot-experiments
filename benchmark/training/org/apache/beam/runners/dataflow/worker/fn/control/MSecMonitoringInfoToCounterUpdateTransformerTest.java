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


public class MSecMonitoringInfoToCounterUpdateTransformerTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    private SpecMonitoringInfoValidator mockSpecValidator;

    @Test
    public void testTransformReturnsNullIfSpecValidationFails() {
        Map<String, String> counterNameMapping = new HashMap<>();
        counterNameMapping.put("beam:counter:supported", "supportedCounter");
        Map<String, DataflowStepContext> stepContextMapping = new HashMap<>();
        MSecMonitoringInfoToCounterUpdateTransformer testObject = new MSecMonitoringInfoToCounterUpdateTransformer(mockSpecValidator, stepContextMapping, counterNameMapping);
        Optional<String> error = Optional.of("Error text");
        Mockito.when(mockSpecValidator.validate(ArgumentMatchers.any())).thenReturn(error);
        MonitoringInfo monitoringInfo = MonitoringInfo.newBuilder().setUrn("beam:metric:pardo_execution_time:start_bundle_msecs:v1:invalid").build();
        Assert.assertEquals(null, testObject.transform(monitoringInfo));
    }

    @Test
    public void testTransformThrowsIfMonitoringInfoWithUnknownUrnReceived() {
        Map<String, String> counterNameMapping = new HashMap<>();
        counterNameMapping.put("beam:counter:supported", "supportedCounter");
        Map<String, DataflowStepContext> stepContextMapping = new HashMap<>();
        MonitoringInfo monitoringInfo = MonitoringInfo.newBuilder().setUrn("beam:metric:pardo_execution_time:start_bundle_msecs:v1:invalid").build();
        MSecMonitoringInfoToCounterUpdateTransformer testObject = new MSecMonitoringInfoToCounterUpdateTransformer(mockSpecValidator, stepContextMapping, counterNameMapping);
        Mockito.when(mockSpecValidator.validate(ArgumentMatchers.any())).thenReturn(Optional.empty());
        exception.expect(RuntimeException.class);
        testObject.transform(monitoringInfo);
    }

    @Test
    public void testTransformThrowsIfMonitoringInfoWithUnknownPTransformLabelPresent() {
        Map<String, String> counterNameMapping = new HashMap<>();
        counterNameMapping.put("beam:counter:supported", "supportedCounter");
        Map<String, DataflowStepContext> stepContextMapping = new HashMap<>();
        MSecMonitoringInfoToCounterUpdateTransformer testObject = new MSecMonitoringInfoToCounterUpdateTransformer(mockSpecValidator, stepContextMapping);
        Mockito.when(mockSpecValidator.validate(ArgumentMatchers.any())).thenReturn(Optional.empty());
        MonitoringInfo monitoringInfo = MonitoringInfo.newBuilder().setUrn("beam:counter:unsupported").putLabels("PTRANSFORM", "anyValue").build();
        exception.expect(RuntimeException.class);
        testObject.transform(monitoringInfo);
    }

    @Test
    public void testTransformReturnsValidCounterUpdateWhenValidMSecMonitoringInfoReceived() {
        // Setup
        Map<String, String> counterNameMapping = new HashMap<>();
        counterNameMapping.put("beam:counter:supported", "supportedCounter");
        Map<String, DataflowStepContext> stepContextMapping = new HashMap<>();
        NameContext nc = NameContext.create("anyStageName", "anyOriginalName", "anySystemName", "anyUserName");
        DataflowStepContext dsc = Mockito.mock(DataflowStepContext.class);
        Mockito.when(dsc.getNameContext()).thenReturn(nc);
        stepContextMapping.put("anyValue", dsc);
        MSecMonitoringInfoToCounterUpdateTransformer testObject = new MSecMonitoringInfoToCounterUpdateTransformer(mockSpecValidator, stepContextMapping, counterNameMapping);
        Mockito.when(mockSpecValidator.validate(ArgumentMatchers.any())).thenReturn(Optional.empty());
        // Execute
        MonitoringInfo monitoringInfo = MonitoringInfo.newBuilder().setUrn("beam:counter:supported").putLabels("PTRANSFORM", "anyValue").build();
        CounterUpdate result = testObject.transform(monitoringInfo);
        // Validate
        Assert.assertNotEquals(null, result);
        Assert.assertEquals(("{cumulative=true, integer={highBits=0, lowBits=0}, " + (("structuredNameAndMetadata={metadata={kind=SUM}, " + "name={executionStepName=anyStageName, name=supportedCounter, origin=SYSTEM, ") + "originalStepName=anyOriginalName}}}")), result.toString());
    }

    @Test
    public void testCreateKnownUrnToCounterNameMappingReturnsExpectedValues() {
        Map<String, DataflowStepContext> stepContextMapping = new HashMap<>();
        MSecMonitoringInfoToCounterUpdateTransformer testObject = new MSecMonitoringInfoToCounterUpdateTransformer(mockSpecValidator, stepContextMapping);
        Map<String, String> result = testObject.createKnownUrnToCounterNameMapping();
        Assert.assertEquals("process-msecs", result.get("beam:metric:pardo_execution_time:process_bundle_msecs:v1"));
        Assert.assertEquals("finish-msecs", result.get("beam:metric:pardo_execution_time:finish_bundle_msecs:v1"));
        Assert.assertEquals("start-msecs", result.get("beam:metric:pardo_execution_time:start_bundle_msecs:v1"));
    }
}

