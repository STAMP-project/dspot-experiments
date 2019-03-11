/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.gcp.bigquery;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Base class for BigQuery Unit Tests. Provides a framework for creating a TestRunner instance with always-required credentials.
 */
public abstract class AbstractBQTest {
    private static final String PROJECT_ID = System.getProperty("test.gcp.project.id", "nifi-test-gcp-project");

    private static final Integer RETRIES = 9;

    static final String DATASET = RemoteBigQueryHelper.generateDatasetName();

    @Mock
    protected BigQuery bq;

    @Test
    public void testBiqQueryOptionsConfiguration() throws Exception {
        Mockito.reset(bq);
        final TestRunner runner = AbstractBQTest.buildNewRunner(getProcessor());
        final AbstractBigQueryProcessor processor = getProcessor();
        final GoogleCredentials mockCredentials = Mockito.mock(GoogleCredentials.class);
        final BigQueryOptions options = processor.getServiceOptions(runner.getProcessContext(), mockCredentials);
        Assert.assertEquals("Project IDs should match", AbstractBQTest.PROJECT_ID, options.getProjectId());
        Assert.assertEquals("Retry counts should match", AbstractBQTest.RETRIES.intValue(), options.getRetrySettings().getMaxAttempts());
        Assert.assertSame("Credentials should be configured correctly", mockCredentials, options.getCredentials());
    }
}

