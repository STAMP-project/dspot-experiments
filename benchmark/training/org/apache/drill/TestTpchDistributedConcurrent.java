/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill;


import UserBitShared.QueryType.SQL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.exec.proto.UserBitShared.QueryResult.QueryState;
import org.apache.drill.exec.rpc.user.UserResultsListener;
import org.apache.drill.shaded.guava.com.google.common.collect.Sets;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.QueryTestUtil;
import org.apache.drill.test.TestTools;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* Note that the real interest here is that the drillbit doesn't become
unstable from running a lot of queries concurrently -- it's not about
any particular order of execution. We ignore the results.
 */
@Category({ SlowTest.class })
public class TestTpchDistributedConcurrent extends BaseTestQuery {
    private static final Logger logger = LoggerFactory.getLogger(TestTpchDistributedConcurrent.class);

    @Rule
    public final TestRule TIMEOUT = TestTools.getTimeoutRule(360000);// Longer timeout than usual.


    /* Valid test names taken from TestTpchDistributed. Fuller path prefixes are
    used so that tests may also be taken from other locations -- more variety
    is better as far as this test goes.
     */
    private static final String[] queryFile = new String[]{ "queries/tpch/01.sql", "queries/tpch/03.sql", "queries/tpch/04.sql", "queries/tpch/05.sql", "queries/tpch/06.sql", "queries/tpch/07.sql", "queries/tpch/08.sql", "queries/tpch/09.sql", "queries/tpch/10.sql", "queries/tpch/11.sql", "queries/tpch/12.sql", "queries/tpch/13.sql", "queries/tpch/14.sql", // "queries/tpch/15.sql", this creates a view
    "queries/tpch/16.sql", "queries/tpch/18.sql", "queries/tpch/19_1.sql", "queries/tpch/20.sql" };

    private static final int TOTAL_QUERIES = 115;

    private static final int CONCURRENT_QUERIES = 15;

    private static final Random random = new Random(-559038737);

    private static final String alterSession = "alter session set `planner.slice_target` = 10";

    private int remainingQueries = (TestTpchDistributedConcurrent.TOTAL_QUERIES) - (TestTpchDistributedConcurrent.CONCURRENT_QUERIES);

    private final Semaphore completionSemaphore = new Semaphore(0);

    private final Semaphore submissionSemaphore = new Semaphore(0);

    private final Set<UserResultsListener> listeners = Sets.newIdentityHashSet();

    private Thread testThread = null;// used to interrupt semaphore wait in case of error


    private static class FailedQuery {
        final String queryFile;

        final UserException userEx;

        public FailedQuery(final String queryFile, final UserException userEx) {
            this.queryFile = queryFile;
            this.userEx = userEx;
        }
    }

    private final List<TestTpchDistributedConcurrent.FailedQuery> failedQueries = new LinkedList<>();

    private class ChainingSilentListener extends BaseTestQuery.SilentListener {
        private final String query;

        public ChainingSilentListener(final String query) {
            this.query = query;
        }

        @Override
        public void queryCompleted(QueryState state) {
            super.queryCompleted(state);
            completionSemaphore.release();
            synchronized(TestTpchDistributedConcurrent.this) {
                final Object object = listeners.remove(this);
                Assert.assertNotNull("listener not found", object);
                /* Only submit more queries if there hasn't been an error. */
                if (((failedQueries.size()) == 0) && ((remainingQueries) > 0)) {
                    /* We can't directly submit the query from here, because we're on the RPC
                    thread, and it throws an exception if we try to send from here. So we
                    allow the QuerySubmitter thread to advance.
                     */
                    submissionSemaphore.release();
                    --(remainingQueries);
                }
            }
        }

        @Override
        public void submissionFailed(UserException uex) {
            super.submissionFailed(uex);
            completionSemaphore.release();
            TestTpchDistributedConcurrent.logger.error("submissionFailed for {} \nwith:", query, uex);
            synchronized(TestTpchDistributedConcurrent.this) {
                final Object object = listeners.remove(this);
                Assert.assertNotNull("listener not found", object);
                failedQueries.add(new TestTpchDistributedConcurrent.FailedQuery(query, uex));
                testThread.interrupt();
            }
        }
    }

    private class QuerySubmitter extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    submissionSemaphore.acquire();
                } catch (InterruptedException e) {
                    TestTpchDistributedConcurrent.logger.error("QuerySubmitter quitting.");
                    return;
                }
                submitRandomQuery();
            } 
        }
    }

    @Test
    public void testConcurrentQueries() throws Exception {
        QueryTestUtil.testRunAndLog(BaseTestQuery.client, SQL, TestTpchDistributedConcurrent.alterSession);
        testThread = Thread.currentThread();
        final TestTpchDistributedConcurrent.QuerySubmitter querySubmitter = new TestTpchDistributedConcurrent.QuerySubmitter();
        querySubmitter.start();
        // Kick off the initial queries. As they complete, they will submit more.
        submissionSemaphore.release(TestTpchDistributedConcurrent.CONCURRENT_QUERIES);
        // Wait for all the queries to complete.
        InterruptedException interruptedException = null;
        try {
            completionSemaphore.acquire(TestTpchDistributedConcurrent.TOTAL_QUERIES);
        } catch (InterruptedException e) {
            interruptedException = e;
            // List the failed queries.
            for (final TestTpchDistributedConcurrent.FailedQuery fq : failedQueries) {
                TestTpchDistributedConcurrent.logger.error(String.format("%s failed with %s", fq.queryFile, fq.userEx));
            }
        }
        // Stop the querySubmitter thread.
        querySubmitter.interrupt();
        if (interruptedException != null) {
            final StackTraceElement[] ste = interruptedException.getStackTrace();
            final StringBuilder sb = new StringBuilder();
            for (StackTraceElement s : ste) {
                sb.append(s.toString());
                sb.append('\n');
            }
            TestTpchDistributedConcurrent.logger.error("Interruped Exception ", interruptedException);
        }
        Assert.assertNull("Query error caused interruption", interruptedException);
        final int nListeners = listeners.size();
        Assert.assertEquals((nListeners + " listeners still exist"), 0, nListeners);
        Assert.assertEquals("Didn't submit all queries", 0, remainingQueries);
        Assert.assertEquals("Queries failed", 0, failedQueries.size());
    }
}

