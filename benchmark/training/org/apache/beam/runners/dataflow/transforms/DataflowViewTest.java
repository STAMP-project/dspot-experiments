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
package org.apache.beam.runners.dataflow.transforms;


import com.google.api.services.dataflow.Dataflow;
import org.apache.beam.sdk.transforms.View;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link View} for a {@link DataflowRunner}.
 */
@RunWith(JUnit4.class)
public class DataflowViewTest {
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Mock
    private Dataflow dataflow;

    @Test
    public void testViewUnboundedAsSingletonBatch() {
        testViewUnbounded(createTestBatchRunner(), View.asSingleton());
    }

    @Test
    public void testViewUnboundedAsSingletonStreaming() {
        testViewUnbounded(createTestStreamingRunner(), View.asSingleton());
    }

    @Test
    public void testViewUnboundedAsIterableBatch() {
        testViewUnbounded(createTestBatchRunner(), View.asIterable());
    }

    @Test
    public void testViewUnboundedAsIterableStreaming() {
        testViewUnbounded(createTestStreamingRunner(), View.asIterable());
    }

    @Test
    public void testViewUnboundedAsListBatch() {
        testViewUnbounded(createTestBatchRunner(), View.asList());
    }

    @Test
    public void testViewUnboundedAsListStreaming() {
        testViewUnbounded(createTestStreamingRunner(), View.asList());
    }

    @Test
    public void testViewUnboundedAsMapBatch() {
        testViewUnbounded(createTestBatchRunner(), View.asMap());
    }

    @Test
    public void testViewUnboundedAsMapStreaming() {
        testViewUnbounded(createTestStreamingRunner(), View.asMap());
    }

    @Test
    public void testViewUnboundedAsMultimapBatch() {
        testViewUnbounded(createTestBatchRunner(), View.asMultimap());
    }

    @Test
    public void testViewUnboundedAsMultimapStreaming() {
        testViewUnbounded(createTestStreamingRunner(), View.asMultimap());
    }

    @Test
    public void testViewNonmergingAsSingletonBatch() {
        testViewNonmerging(createTestBatchRunner(), View.asSingleton());
    }

    @Test
    public void testViewNonmergingAsSingletonStreaming() {
        testViewNonmerging(createTestStreamingRunner(), View.asSingleton());
    }

    @Test
    public void testViewNonmergingAsIterableBatch() {
        testViewNonmerging(createTestBatchRunner(), View.asIterable());
    }

    @Test
    public void testViewNonmergingAsIterableStreaming() {
        testViewNonmerging(createTestStreamingRunner(), View.asIterable());
    }

    @Test
    public void testViewNonmergingAsListBatch() {
        testViewNonmerging(createTestBatchRunner(), View.asList());
    }

    @Test
    public void testViewNonmergingAsListStreaming() {
        testViewNonmerging(createTestStreamingRunner(), View.asList());
    }

    @Test
    public void testViewNonmergingAsMapBatch() {
        testViewNonmerging(createTestBatchRunner(), View.asMap());
    }

    @Test
    public void testViewNonmergingAsMapStreaming() {
        testViewNonmerging(createTestStreamingRunner(), View.asMap());
    }

    @Test
    public void testViewNonmergingAsMultimapBatch() {
        testViewNonmerging(createTestBatchRunner(), View.asMultimap());
    }

    @Test
    public void testViewNonmergingAsMultimapStreaming() {
        testViewNonmerging(createTestStreamingRunner(), View.asMultimap());
    }
}

