/**
 * Copyright (c) 2016, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.als;


import org.junit.Test;


public final class SolverCacheTest extends AbstractFeatureVectorTest {
    @Test
    public void testCache() throws Exception {
        FeatureVectors vectors = new FeatureVectorsPartition();
        SolverCacheTest.addVectors(vectors);
        SolverCacheTest.doTestCache(new SolverCache(getExecutor(), vectors));
        FeatureVectors partitioned = new PartitionedFeatureVectors(2, getExecutor());
        SolverCacheTest.addVectors(partitioned);
        SolverCacheTest.doTestCache(new SolverCache(getExecutor(), partitioned));
    }

    @Test
    public void testSolveFailure() {
        // Test this doesn't hang
        get(true);
        // Will fail to solve
        FeatureVectors vectors = new FeatureVectorsPartition();
        vectors.setVector("A", new float[]{ 1.0F, 3.0F, 6.0F });
        vectors.setVector("B", new float[]{ 0.0F, 1.0F, 2.0F });
        // Test this doesn't hang
        get(true);
    }
}

