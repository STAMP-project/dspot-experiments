/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce;


import org.apache.camel.component.salesforce.api.dto.Limits;
import org.junit.Test;


public class LimitsIntegrationTest extends AbstractSalesforceTestBase {
    private static final Object NOT_USED = null;

    @Test
    public void shouldFetchLimitsForOrganization() {
        final Limits limits = template.requestBody("direct:test-limits", LimitsIntegrationTest.NOT_USED, Limits.class);
        assertNotNull("Should fetch limits from Salesforce REST API", limits);
        LimitsIntegrationTest.assertLimitIsFetched("ConcurrentAsyncGetReportInstances", limits.getConcurrentAsyncGetReportInstances());
        LimitsIntegrationTest.assertLimitIsFetched("ConcurrentSyncReportRuns", limits.getConcurrentSyncReportRuns());
        LimitsIntegrationTest.assertLimitIsFetched("DailyApiRequests", limits.getDailyApiRequests());
        LimitsIntegrationTest.assertLimitIsFetched("DailyAsyncApexExecutions", limits.getDailyAsyncApexExecutions());
        LimitsIntegrationTest.assertLimitIsFetched("DailyBulkApiRequests", limits.getDailyBulkApiRequests());
        LimitsIntegrationTest.assertLimitIsFetched("DailyDurableGenericStreamingApiEvents", limits.getDailyDurableGenericStreamingApiEvents());
        LimitsIntegrationTest.assertLimitIsFetched("DailyDurableStreamingApiEvents", limits.getDailyDurableStreamingApiEvents());
        LimitsIntegrationTest.assertLimitIsFetched("DailyGenericStreamingApiEvents", limits.getDailyGenericStreamingApiEvents());
        LimitsIntegrationTest.assertLimitIsFetched("DailyStreamingApiEvents", limits.getDailyStreamingApiEvents());
        LimitsIntegrationTest.assertLimitIsFetched("DailyWorkflowEmails", limits.getDailyWorkflowEmails());
        LimitsIntegrationTest.assertLimitIsFetched("DataStorageMB", limits.getDataStorageMB());
        LimitsIntegrationTest.assertLimitIsFetched("DurableStreamingApiConcurrentClients", limits.getDurableStreamingApiConcurrentClients());
        LimitsIntegrationTest.assertLimitIsFetched("FileStorageMB", limits.getFileStorageMB());
        LimitsIntegrationTest.assertLimitIsFetched("HourlyAsyncReportRuns", limits.getHourlyAsyncReportRuns());
        LimitsIntegrationTest.assertLimitIsFetched("HourlyDashboardRefreshes", limits.getHourlyDashboardRefreshes());
        LimitsIntegrationTest.assertLimitIsFetched("HourlyDashboardResults", limits.getHourlyDashboardResults());
        LimitsIntegrationTest.assertLimitIsFetched("HourlyDashboardStatuses", limits.getHourlyDashboardStatuses());
        LimitsIntegrationTest.assertLimitIsFetched("HourlyODataCallout", limits.getHourlyODataCallout());
        LimitsIntegrationTest.assertLimitIsFetched("HourlySyncReportRuns", limits.getHourlySyncReportRuns());
        LimitsIntegrationTest.assertLimitIsFetched("HourlyTimeBasedWorkflow", limits.getHourlyTimeBasedWorkflow());
        LimitsIntegrationTest.assertLimitIsFetched("MassEmail", limits.getMassEmail());
        LimitsIntegrationTest.assertLimitIsFetched("SingleEmail", limits.getSingleEmail());
        LimitsIntegrationTest.assertLimitIsFetched("StreamingApiConcurrentClients", limits.getStreamingApiConcurrentClients());
    }
}

