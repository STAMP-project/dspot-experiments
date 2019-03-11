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
package org.apache.beam.runners.dataflow.worker;


import GlobalWindow.Coder.INSTANCE;
import PropertyNames.ENCODING;
import PropertyNames.OBJECT_TYPE_NAME;
import StreamingModeExecutionContext.StepContext;
import WorkerPropertyNames.SIDE_INPUT_ID;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.util.CloudObjects;
import org.apache.beam.runners.dataflow.worker.DataflowExecutionContext.DataflowStepContext;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ParDoFn;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link StreamingPCollectionViewWriterDoFnFactory}.
 */
@RunWith(JUnit4.class)
public class StreamingPCollectionViewWriterDoFnFactoryTest {
    @Test
    public void testConstruction() throws Exception {
        DataflowOperationContext mockOperationContext = Mockito.mock(DataflowOperationContext.class);
        DataflowExecutionContext mockExecutionContext = Mockito.mock(DataflowExecutionContext.class);
        DataflowStepContext mockStepContext = Mockito.mock(StepContext.class);
        Mockito.when(mockExecutionContext.getStepContext(mockOperationContext)).thenReturn(mockStepContext);
        CloudObject coder = /* sdkComponents= */
        CloudObjects.asCloudObject(WindowedValue.getFullCoder(BigEndianIntegerCoder.of(), INSTANCE), null);
        ParDoFn parDoFn = /* pipeline options */
        /* side input infos */
        /* main output tag */
        /* output tag to receiver index */
        new StreamingPCollectionViewWriterDoFnFactory().create(null, CloudObject.fromSpec(ImmutableMap.of(OBJECT_TYPE_NAME, "StreamingPCollectionViewWriterDoFn", ENCODING, coder, SIDE_INPUT_ID, "test-side-input-id")), null, null, null, mockExecutionContext, mockOperationContext);
        Assert.assertThat(parDoFn, Matchers.instanceOf(StreamingPCollectionViewWriterParDoFn.class));
    }
}

