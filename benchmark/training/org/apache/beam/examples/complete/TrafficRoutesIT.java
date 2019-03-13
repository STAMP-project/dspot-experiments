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
package org.apache.beam.examples.complete;


import com.google.api.services.bigquery.model.QueryResponse;
import org.apache.beam.examples.complete.TrafficRoutes.FormatStatsFn;
import org.apache.beam.examples.complete.TrafficRoutes.TrafficRoutesOptions;
import org.apache.beam.sdk.io.gcp.testing.BigqueryClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * End-to-end tests of TrafficRoutes.
 */
@RunWith(JUnit4.class)
public class TrafficRoutesIT {
    private TrafficRoutesOptions options;

    private final String timestamp = Long.toString(System.currentTimeMillis());

    private final String outputDatasetId = "traffic_routes_" + (timestamp);

    private final String outputTable = "traffic_routes_table";

    private String projectId;

    private BigqueryClient bqClient;

    @Test
    public void testE2ETrafficRoutes() throws Exception {
        this.options.setBigQuerySchema(FormatStatsFn.getSchema());
        this.options.setProject(this.projectId);
        this.options.setBigQueryDataset(this.outputDatasetId);
        this.options.setBigQueryTable(this.outputTable);
        TrafficRoutes.runTrafficRoutes(options);
        QueryResponse response = this.bqClient.queryWithRetries(String.format("SELECT count(*) as total FROM [%s:%s.%s]", this.projectId, this.outputDatasetId, this.outputTable), this.projectId);
        String res = response.getRows().get(0).getF().get(0).getV().toString();
        Assert.assertEquals("27", res);
    }
}

