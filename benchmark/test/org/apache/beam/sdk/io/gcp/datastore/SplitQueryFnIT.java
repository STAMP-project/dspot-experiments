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
package org.apache.beam.sdk.io.gcp.datastore;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Read.NUM_QUERY_SPLITS_MIN;


/**
 * Integration tests for {@link DatastoreV1.Read.SplitQueryFn}.
 *
 * <p>It is hard to mock the exact behavior of Cloud Datastore, especially for the statistics
 * queries. Also the fact that DatastoreIO falls back gracefully when querying statistics fails,
 * makes it hard to catch these issues in production. This test here ensures we interact with the
 * Cloud Datastore directly, query the actual stats and verify that the SplitQueryFn generates the
 * expected number of query splits.
 *
 * <p>These tests are brittle as they rely on statistics data in Cloud Datastore. If the data gets
 * lost or changes then they will begin failing and this test should be disabled. At the time of
 * writing, the Cloud Datastore has the following statistics,
 *
 * <ul>
 *   <li>kind = sort_1G, entity_bytes = 2130000000, count = 10000000
 *   <li>kind = shakespeare, entity_bytes = 26383451, count = 172948
 * </ul>
 */
// TODO (vikasrk): Create datasets under a different namespace and add tests.
@RunWith(JUnit4.class)
public class SplitQueryFnIT {
    /**
     * Tests {@link SplitQueryFn} to generate expected number of splits for a large dataset.
     */
    @Test
    public void testSplitQueryFnWithLargeDataset() throws Exception {
        String projectId = "apache-beam-testing";
        String kind = "sort_1G";
        String namespace = null;
        // Num splits is computed based on the entity_bytes size of the input_sort_1G kind reported by
        // Datastore stats.
        int expectedNumSplits = 32;
        testSplitQueryFn(projectId, kind, namespace, expectedNumSplits);
    }

    /**
     * Tests {@link SplitQueryFn} to fallback to NUM_QUERY_SPLITS_MIN for a small dataset.
     */
    @Test
    public void testSplitQueryFnWithSmallDataset() throws Exception {
        String projectId = "apache-beam-testing";
        String kind = "shakespeare";
        String namespace = null;
        int expectedNumSplits = NUM_QUERY_SPLITS_MIN;
        testSplitQueryFn(projectId, kind, namespace, expectedNumSplits);
    }
}

