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
package org.apache.beam.sdk.io.elasticsearch;


import ThreadLeakScope.Scope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import java.io.IOException;
import java.io.Serializable;
import org.apache.beam.sdk.testing.TestPipeline;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/* Cannot use @RunWith(JUnit4.class) with ESIntegTestCase
Cannot have @BeforeClass @AfterClass with ESIntegTestCase
 */
/**
 * Tests for {@link ElasticsearchIO} version 6.
 */
@ThreadLeakScope(Scope.NONE)
public class ElasticsearchIOTest extends ESIntegTestCase implements Serializable {
    private ElasticsearchIOTestCommon elasticsearchIOTestCommon;

    private ElasticsearchIO.ConnectionConfiguration connectionConfiguration;

    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testSizes() throws Exception {
        // need to create the index using the helper method (not create it at first insertion)
        // for the indexSettings() to be run
        createIndex(ElasticsearchIOTestCommon.getEsIndex());
        elasticsearchIOTestCommon.testSizes();
    }

    @Test
    public void testRead() throws Exception {
        // need to create the index using the helper method (not create it at first insertion)
        // for the indexSettings() to be run
        createIndex(ElasticsearchIOTestCommon.getEsIndex());
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testRead();
    }

    @Test
    public void testReadWithQuery() throws Exception {
        // need to create the index using the helper method (not create it at first insertion)
        // for the indexSettings() to be run
        createIndex(ElasticsearchIOTestCommon.getEsIndex());
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testReadWithQuery();
    }

    @Test
    public void testWrite() throws Exception {
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testWrite();
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testWriteWithErrors() throws Exception {
        elasticsearchIOTestCommon.setExpectedException(expectedException);
        elasticsearchIOTestCommon.testWriteWithErrors();
    }

    @Test
    public void testWriteWithMaxBatchSize() throws Exception {
        elasticsearchIOTestCommon.testWriteWithMaxBatchSize();
    }

    @Test
    public void testWriteWithMaxBatchSizeBytes() throws Exception {
        elasticsearchIOTestCommon.testWriteWithMaxBatchSizeBytes();
    }

    @Test
    public void testSplit() throws Exception {
        // need to create the index using the helper method (not create it at first insertion)
        // for the indexSettings() to be run
        createIndex(ElasticsearchIOTestCommon.getEsIndex());
        elasticsearchIOTestCommon.testSplit(2000);
    }

    @Test
    public void testWriteWithIdFn() throws Exception {
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testWriteWithIdFn();
    }

    @Test
    public void testWriteWithIndexFn() throws Exception {
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testWriteWithIndexFn();
    }

    @Test
    public void testWriteFullAddressing() throws Exception {
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testWriteWithFullAddressing();
    }

    @Test
    public void testWritePartialUpdate() throws Exception {
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testWritePartialUpdate();
    }

    @Test
    public void testReadWithMetadata() throws Exception {
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testReadWithMetadata();
    }

    @Test
    public void testDefaultRetryPredicate() throws IOException {
        elasticsearchIOTestCommon.testDefaultRetryPredicate(getRestClient());
    }

    @Test
    public void testWriteRetry() throws Throwable {
        elasticsearchIOTestCommon.setExpectedException(expectedException);
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testWriteRetry();
    }

    @Test
    public void testWriteRetryValidRequest() throws Throwable {
        elasticsearchIOTestCommon.setPipeline(pipeline);
        elasticsearchIOTestCommon.testWriteRetryValidRequest();
    }
}

