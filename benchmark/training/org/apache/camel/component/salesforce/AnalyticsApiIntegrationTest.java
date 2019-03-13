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


import java.util.List;
import org.apache.camel.component.salesforce.api.SalesforceReportResultsToListConverter;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration test for Salesforce analytics API endpoints.
 */
@RunWith(Theories.class)
public class AnalyticsApiIntegrationTest extends AbstractSalesforceTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsApiIntegrationTest.class);

    private static final int RETRY_DELAY = 5000;

    private static final int REPORT_RESULT_RETRIES = 5;

    private static final String[] REPORT_OPTIONS = new String[]{ SalesforceReportResultsToListConverter.INCLUDE_HEADERS, SalesforceReportResultsToListConverter.INCLUDE_DETAILS, SalesforceReportResultsToListConverter.INCLUDE_SUMMARY };

    private static final int NUM_OPTIONS = AnalyticsApiIntegrationTest.REPORT_OPTIONS.length;

    private static final int[] POWERS = new int[]{ 4, 2, 1 };

    private static final String TEST_REPORT_NAME = "Test_Report";

    private boolean bodyMetadata;

    @Test
    public void testGetRecentReports() throws Exception {
        final List recentReports = template().requestBody("direct:getRecentReports", null, List.class);
        assertNotNull("getRecentReports", recentReports);
        assertFalse("getRecentReports empty", recentReports.isEmpty());
        AnalyticsApiIntegrationTest.LOG.debug("getRecentReports: {}", recentReports);
    }
}

