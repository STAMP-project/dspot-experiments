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
package org.apache.beam.sdk.io.solr;


import ObjectReleaseTracker.OBJECTS;
import Pipeline.PipelineExecutionException;
import SolrException.ErrorCode;
import SolrException.ErrorCode.SERVICE_UNAVAILABLE;
import SolrIO.ConnectionConfiguration;
import SolrIO.RetryConfiguration;
import SolrIO.Write;
import SolrIO.Write.WriteFn.RETRY_ATTEMPT_LOG;
import SolrTestCaseJ4.SuppressSSL;
import ThreadLeakScope.Scope;
import com.carrotsearch.ant.tasks.junit4.dependencies.com.google.common.collect.ImmutableSet;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.apache.beam.sdk.values.PCollection;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.cloud.SolrCloudTestCase;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.solr.common.util.ObjectReleaseTracker;
import org.joda.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A test of {@link SolrIO} on an independent Solr instance.
 */
@ThreadLeakScope(Scope.NONE)
@SolrTestCaseJ4.SuppressSSL
public class SolrIOTest extends SolrCloudTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(SolrIOTest.class);

    private static final String SOLR_COLLECTION = "beam";

    private static final int NUM_SHARDS = 3;

    private static final long NUM_DOCS = 400L;

    private static final int NUM_SCIENTISTS = 10;

    private static final int BATCH_SIZE = 200;

    private static final int DEFAULT_BATCH_SIZE = 1000;

    private static AuthorizedSolrClient<CloudSolrClient> solrClient;

    private static ConnectionConfiguration connectionConfiguration;

    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    @Rule
    public final transient ExpectedLogs expectedLogs = ExpectedLogs.none(SolrIO.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBadCredentials() throws IOException {
        thrown.expect(SolrException.class);
        String zkAddress = cluster.getZkServer().getZkAddress();
        SolrIO.ConnectionConfiguration connectionConfiguration = ConnectionConfiguration.create(zkAddress).withBasicCredentials("solr", "wrongpassword");
        try (AuthorizedSolrClient solrClient = connectionConfiguration.createClient()) {
            SolrIOTestUtils.insertTestDocuments(SolrIOTest.SOLR_COLLECTION, SolrIOTest.NUM_DOCS, solrClient);
        }
    }

    @Test
    public void testRead() throws Exception {
        SolrIOTestUtils.insertTestDocuments(SolrIOTest.SOLR_COLLECTION, SolrIOTest.NUM_DOCS, SolrIOTest.solrClient);
        PCollection<SolrDocument> output = pipeline.apply(SolrIO.read().withConnectionConfiguration(SolrIOTest.connectionConfiguration).from(SolrIOTest.SOLR_COLLECTION).withBatchSize(101));
        PAssert.thatSingleton(output.apply("Count", Count.globally())).isEqualTo(SolrIOTest.NUM_DOCS);
        pipeline.run();
    }

    @Test
    public void testReadWithQuery() throws Exception {
        SolrIOTestUtils.insertTestDocuments(SolrIOTest.SOLR_COLLECTION, SolrIOTest.NUM_DOCS, SolrIOTest.solrClient);
        PCollection<SolrDocument> output = pipeline.apply(SolrIO.read().withConnectionConfiguration(SolrIOTest.connectionConfiguration).from(SolrIOTest.SOLR_COLLECTION).withQuery("scientist:Franklin"));
        PAssert.thatSingleton(output.apply("Count", Count.globally())).isEqualTo(((SolrIOTest.NUM_DOCS) / (SolrIOTest.NUM_SCIENTISTS)));
        pipeline.run();
    }

    @Test
    public void testWrite() throws Exception {
        List<SolrInputDocument> data = SolrIOTestUtils.createDocuments(SolrIOTest.NUM_DOCS);
        SolrIO.Write write = SolrIO.write().withConnectionConfiguration(SolrIOTest.connectionConfiguration).to(SolrIOTest.SOLR_COLLECTION);
        pipeline.apply(Create.of(data)).apply(write);
        pipeline.run();
        long currentNumDocs = SolrIOTestUtils.commitAndGetCurrentNumDocs(SolrIOTest.SOLR_COLLECTION, SolrIOTest.solrClient);
        assertEquals(SolrIOTest.NUM_DOCS, currentNumDocs);
        QueryResponse response = SolrIOTest.solrClient.query(SolrIOTest.SOLR_COLLECTION, new SolrQuery("scientist:Lovelace"));
        assertEquals(((SolrIOTest.NUM_DOCS) / (SolrIOTest.NUM_SCIENTISTS)), response.getResults().getNumFound());
    }

    @Test
    public void testWriteWithMaxBatchSize() throws Exception {
        SolrIO.Write write = SolrIO.write().withConnectionConfiguration(SolrIOTest.connectionConfiguration).to(SolrIOTest.SOLR_COLLECTION).withMaxBatchSize(SolrIOTest.BATCH_SIZE);
        // write bundles size is the runner decision, we cannot force a bundle size,
        // so we test the Writer as a DoFn outside of a runner.
        try (DoFnTester<SolrInputDocument, Void> fnTester = DoFnTester.of(new SolrIO.Write.WriteFn(write))) {
            List<SolrInputDocument> input = SolrIOTestUtils.createDocuments(SolrIOTest.NUM_DOCS);
            long numDocsProcessed = 0;
            long numDocsInserted = 0;
            for (SolrInputDocument document : input) {
                fnTester.processElement(document);
                numDocsProcessed++;
                // test every 100 docs to avoid overloading Solr
                if ((numDocsProcessed % 100) == 0) {
                    // force the index to upgrade after inserting for the inserted docs
                    // to be searchable immediately
                    long currentNumDocs = SolrIOTestUtils.commitAndGetCurrentNumDocs(SolrIOTest.SOLR_COLLECTION, SolrIOTest.solrClient);
                    if ((numDocsProcessed % (SolrIOTest.BATCH_SIZE)) == 0) {
                        /* bundle end */
                        assertEquals("we are at the end of a bundle, we should have inserted all processed documents", numDocsProcessed, currentNumDocs);
                        numDocsInserted = currentNumDocs;
                    } else {
                        /* not bundle end */
                        assertEquals("we are not at the end of a bundle, we should have inserted no more documents", numDocsInserted, currentNumDocs);
                    }
                }
            }
        }
    }

    /**
     * Test that retries are invoked when Solr returns error. We invoke this by calling a non existing
     * collection, and use a strategy that will retry on any SolrException. The logger is used to
     * verify expected behavior.
     */
    @Test
    public void testWriteRetry() throws Throwable {
        thrown.expect(IOException.class);
        thrown.expectMessage("Error writing to Solr");
        // entry state of the release tracker to ensure we only unregister newly created objects
        @SuppressWarnings("unchecked")
        Set<Object> entryState = ImmutableSet.copyOf(OBJECTS.keySet());
        SolrIO.Write write = SolrIO.write().withConnectionConfiguration(SolrIOTest.connectionConfiguration).withRetryConfiguration(RetryConfiguration.create(3, Duration.standardMinutes(3)).withRetryPredicate(new SolrIOTestUtils.LenientRetryPredicate())).to("wrong-collection");
        List<SolrInputDocument> data = SolrIOTestUtils.createDocuments(SolrIOTest.NUM_DOCS);
        pipeline.apply(Create.of(data)).apply(write);
        try {
            pipeline.run();
        } catch (final Pipeline e) {
            // Hack: await all worker threads completing (BEAM-4040)
            int waitAttempts = 30;// defensive coding

            while ((SolrIOTestUtils.namedThreadIsAlive("direct-runner-worker")) && ((waitAttempts--) >= 0)) {
                SolrIOTest.LOG.info("Pausing to allow direct-runner-worker threads to finish");
                Thread.sleep(1000);
            } 
            // remove solrClients created by us as there are no guarantees on Teardown here
            for (Object o : OBJECTS.keySet()) {
                if ((o instanceof SolrZkClient) && (!(entryState.contains(o)))) {
                    SolrIOTest.LOG.info("Removing unreleased SolrZkClient");
                    ObjectReleaseTracker.release(o);
                }
            }
            // check 2 retries were initiated by inspecting the log before passing on the exception
            expectedLogs.verifyWarn(String.format(RETRY_ATTEMPT_LOG, 1));
            expectedLogs.verifyWarn(String.format(RETRY_ATTEMPT_LOG, 2));
            throw e.getCause();
        }
        fail("Pipeline should not have run to completion");
    }

    /**
     * Tests predicate performs as documented.
     */
    @Test
    public void testDefaultRetryPredicate() {
        assertTrue(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new IOException("test")));
        assertTrue(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrServerException("test")));
        assertTrue(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.CONFLICT, "test")));
        assertTrue(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.SERVER_ERROR, "test")));
        assertTrue(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.SERVICE_UNAVAILABLE, "test")));
        assertTrue(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.INVALID_STATE, "test")));
        assertTrue(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.UNKNOWN, "test")));
        assertTrue(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException("localhost", SERVICE_UNAVAILABLE.code, "test", new Exception())));
        assertFalse(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.BAD_REQUEST, "test")));
        assertFalse(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.FORBIDDEN, "test")));
        assertFalse(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.NOT_FOUND, "test")));
        assertFalse(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.UNAUTHORIZED, "test")));
        assertFalse(RetryConfiguration.DEFAULT_RETRY_PREDICATE.test(new SolrException(ErrorCode.UNSUPPORTED_MEDIA_TYPE, "test")));
    }

    /**
     * Tests batch size default and changed value.
     */
    @Test
    public void testBatchSize() {
        SolrIO.Write write1 = SolrIO.write().withConnectionConfiguration(SolrIOTest.connectionConfiguration).withMaxBatchSize(SolrIOTest.BATCH_SIZE);
        assertTrue(((write1.getMaxBatchSize()) == (SolrIOTest.BATCH_SIZE)));
        SolrIO.Write write2 = SolrIO.write().withConnectionConfiguration(SolrIOTest.connectionConfiguration);
        assertTrue(((write2.getMaxBatchSize()) == (SolrIOTest.DEFAULT_BATCH_SIZE)));
    }
}

