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


import WorkerCustomSources.DEFAULT_NUM_BUNDLES_LIMIT;
import com.google.api.services.dataflow.model.DerivedSource;
import com.google.api.services.dataflow.model.Source;
import com.google.api.services.dataflow.model.SourceSplitResponse;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.dataflow.worker.WorkerCustomSources.SplittableOnlyBoundedSource;
import org.apache.beam.sdk.io.CountingSource;
import org.apache.beam.sdk.io.OffsetBasedSource;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Combinatorial tests for {@link WorkerCustomSources#limitNumberOfBundles} which checks that all
 * splits were returned.
 */
@RunWith(Parameterized.class)
public class WorkerCustomSourcesSplitOnlySourceTest {
    @Parameterized.Parameter
    public int numberOfSplits;

    @Test
    public void testAllSplitsAreReturned() throws Exception {
        final long apiSizeLimitForTest = 500 * 1024;
        DataflowPipelineOptions options = PipelineOptionsFactory.create().as(DataflowPipelineOptions.class);
        // Generate a CountingSource and split it into the desired number of splits
        // (desired size = 1 byte), triggering the re-split with a larger bundle size.
        // Thus below we expect to produce 'numberOfSplits' splits.
        Source source = WorkerCustomSourcesTest.translateIOToCloudSource(CountingSource.upTo(numberOfSplits), options);
        SourceSplitResponse split = /* numBundles limit */
        WorkerCustomSourcesTest.performSplit(source, options, 1L, null, apiSizeLimitForTest);
        MatcherAssert.assertThat(split.getBundles().size(), Matchers.lessThanOrEqualTo(DEFAULT_NUM_BUNDLES_LIMIT));
        List<OffsetBasedSource<?>> originalSplits = new ArrayList<>(numberOfSplits);
        // Collect all the splits
        for (DerivedSource derivedSource : split.getBundles()) {
            Object deserializedSource = WorkerCustomSources.deserializeFromCloudSource(derivedSource.getSource().getSpec());
            if (deserializedSource instanceof SplittableOnlyBoundedSource) {
                SplittableOnlyBoundedSource<?> splittableOnlySource = ((SplittableOnlyBoundedSource<?>) (deserializedSource));
                originalSplits.addAll(((List) (splittableOnlySource.split(1L, options))));
            } else {
                originalSplits.add(((OffsetBasedSource<?>) (deserializedSource)));
            }
        }
        Assert.assertEquals(numberOfSplits, originalSplits.size());
        for (int i = 0; i < (originalSplits.size()); i++) {
            OffsetBasedSource<?> offsetBasedSource = ((OffsetBasedSource<?>) (originalSplits.get(i)));
            Assert.assertEquals(i, offsetBasedSource.getStartOffset());
            Assert.assertEquals((i + 1), offsetBasedSource.getEndOffset());
        }
    }
}

