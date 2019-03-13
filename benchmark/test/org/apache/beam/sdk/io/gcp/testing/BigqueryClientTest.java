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
package org.apache.beam.sdk.io.gcp.testing;


import Bigquery.Jobs;
import Bigquery.Jobs.Query;
import com.google.api.services.bigquery.Bigquery;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


/**
 * Tests for {@link BigqueryClient}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(BigqueryClient.class)
public class BigqueryClientTest {
    private final String projectId = "test-project";

    private final String query = "test-query";

    private BigqueryClient bqClient;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Bigquery mockBigqueryClient;

    @Mock
    private Jobs mockJobs;

    @Mock
    private Query mockQuery;

    @Test
    public void testQueryWithRetriesWhenServiceFails() throws Exception {
        Mockito.when(mockQuery.execute()).thenThrow(new IOException());
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Unable to get BigQuery response after retrying");
        try {
            bqClient.queryWithRetries(query, projectId);
        } finally {
            Mockito.verify(mockJobs, Mockito.atLeast(BigqueryClient.MAX_QUERY_RETRIES)).query(eq(projectId), any(com.google.api.services.bigquery.model.QueryRequest.class));
        }
    }

    @Test
    public void testQueryWithRetriesWhenQueryResponseNull() throws Exception {
        Mockito.when(mockQuery.execute()).thenReturn(null);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Unable to get BigQuery response after retrying");
        try {
            bqClient.queryWithRetries(query, projectId);
        } finally {
            Mockito.verify(mockJobs, Mockito.atLeast(BigqueryClient.MAX_QUERY_RETRIES)).query(eq(projectId), any(com.google.api.services.bigquery.model.QueryRequest.class));
        }
    }
}

