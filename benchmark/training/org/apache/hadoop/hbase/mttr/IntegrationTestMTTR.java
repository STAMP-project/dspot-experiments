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
package org.apache.hadoop.hbase.mttr;


import AlwaysSampler.INSTANCE;
import MoreObjects.ToStringHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.IntegrationTestingUtility;
import org.apache.hadoop.hbase.InvalidFamilyOperationException;
import org.apache.hadoop.hbase.NamespaceExistException;
import org.apache.hadoop.hbase.NamespaceNotFoundException;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.chaos.actions.Action;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.CoprocessorException;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.ipc.FatalConnectionException;
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.security.AccessDeniedException;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.trace.TraceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.LoadTestTool;
import org.apache.hbase.thirdparty.com.google.common.base.MoreObjects;
import org.apache.htrace.core.Span;
import org.apache.htrace.core.TraceScope;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration test that should benchmark how fast HBase can recover from failures. This test starts
 * different threads:
 * <ol>
 * <li>
 * Load Test Tool.<br/>
 * This runs so that all RegionServers will have some load and WALs will be full.
 * </li>
 * <li>
 * Scan thread.<br/>
 * This thread runs a very short scan over and over again recording how log it takes to respond.
 * The longest response is assumed to be the time it took to recover.
 * </li>
 * <li>
 * Put thread.<br/>
 * This thread just like the scan thread except it does a very small put.
 * </li>
 * <li>
 * Admin thread. <br/>
 * This thread will continually go to the master to try and get the cluster status.  Just like the
 * put and scan threads, the time to respond is recorded.
 * </li>
 * <li>
 * Chaos Monkey thread.<br/>
 * This thread runs a ChaosMonkey.Action.
 * </li>
 * </ol>
 * <p/>
 * The ChaosMonkey actions currently run are:
 * <ul>
 * <li>Restart the RegionServer holding meta.</li>
 * <li>Move the Regions of meta.</li>
 * <li>Restart the RegionServer holding the table the scan and put threads are targeting.</li>
 * <li>Move the Regions of the table used by the scan and put threads.</li>
 * <li>Restart the master.</li>
 * </ul>
 * <p/>
 * At the end of the test a log line is output on the INFO level containing the timing data that was
 * collected.
 */
@Category(IntegrationTests.class)
public class IntegrationTestMTTR {
    /**
     * Constants.
     */
    private static final byte[] FAMILY = Bytes.toBytes("d");

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestMTTR.class);

    private static long sleepTime;

    private static final String SLEEP_TIME_KEY = "hbase.IntegrationTestMTTR.sleeptime";

    private static final long SLEEP_TIME_DEFAULT = 60 * 1000L;

    /**
     * Configurable table names.
     */
    private static TableName tableName;

    private static TableName loadTableName;

    /**
     * Util to get at the cluster.
     */
    private static IntegrationTestingUtility util;

    /**
     * Executor for test threads.
     */
    private static ExecutorService executorService;

    /**
     * All of the chaos monkey actions used.
     */
    private static Action restartRSAction;

    private static Action restartMetaAction;

    private static Action moveMetaRegionsAction;

    private static Action moveRegionAction;

    private static Action restartMasterAction;

    /**
     * The load test tool used to create load and make sure that WALs aren't empty.
     */
    private static LoadTestTool loadTool;

    @Test
    public void testRestartRsHoldingTable() throws Exception {
        run(new IntegrationTestMTTR.ActionCallable(IntegrationTestMTTR.restartRSAction), "RestartRsHoldingTableAction");
    }

    @Test
    public void testKillRsHoldingMeta() throws Exception {
        Assume.assumeFalse(IntegrationTestMTTR.tablesOnMaster());
        run(new IntegrationTestMTTR.ActionCallable(IntegrationTestMTTR.restartMetaAction), "KillRsHoldingMeta");
    }

    @Test
    public void testMoveMeta() throws Exception {
        run(new IntegrationTestMTTR.ActionCallable(IntegrationTestMTTR.moveMetaRegionsAction), "MoveMeta");
    }

    @Test
    public void testMoveRegion() throws Exception {
        run(new IntegrationTestMTTR.ActionCallable(IntegrationTestMTTR.moveRegionAction), "MoveRegion");
    }

    @Test
    public void testRestartMaster() throws Exception {
        run(new IntegrationTestMTTR.ActionCallable(IntegrationTestMTTR.restartMasterAction), "RestartMaster");
    }

    /**
     * Class to store results of TimingCallable.
     *
     * Stores times and trace id.
     */
    private static class TimingResult {
        DescriptiveStatistics stats = new DescriptiveStatistics();

        ArrayList<String> traces = new ArrayList<>(10);

        /**
         * Add a result to this aggregate result.
         *
         * @param time
         * 		Time in nanoseconds
         * @param span
         * 		Span.  To be kept if the time taken was over 1 second
         */
        public void addResult(long time, Span span) {
            if (span == null) {
                return;
            }
            stats.addValue(TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS));
            if ((TimeUnit.SECONDS.convert(time, TimeUnit.NANOSECONDS)) >= 1) {
                traces.add(span.getTracerId());
            }
        }

        @Override
        public String toString() {
            MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this).add("numResults", stats.getN()).add("minTime", stats.getMin()).add("meanTime", stats.getMean()).add("maxTime", stats.getMax()).add("25th", stats.getPercentile(25)).add("50th", stats.getPercentile(50)).add("75th", stats.getPercentile(75)).add("90th", stats.getPercentile(90)).add("95th", stats.getPercentile(95)).add("99th", stats.getPercentile(99)).add("99.9th", stats.getPercentile(99.9)).add("99.99th", stats.getPercentile(99.99)).add("traces", traces);
            return helper.toString();
        }
    }

    /**
     * Base class for actions that need to record the time needed to recover from a failure.
     */
    abstract static class TimingCallable implements Callable<IntegrationTestMTTR.TimingResult> {
        protected final Future<?> future;

        public TimingCallable(Future<?> f) {
            future = f;
        }

        @Override
        public IntegrationTestMTTR.TimingResult call() throws Exception {
            IntegrationTestMTTR.TimingResult result = new IntegrationTestMTTR.TimingResult();
            final int maxIterations = 10;
            int numAfterDone = 0;
            int resetCount = 0;
            TraceUtil.addSampler(INSTANCE);
            // Keep trying until the rs is back up and we've gotten a put through
            while (numAfterDone < maxIterations) {
                long start = System.nanoTime();
                Span span = null;
                try (TraceScope scope = TraceUtil.createTrace(getSpanName())) {
                    if (scope != null) {
                        span = scope.getSpan();
                    }
                    boolean actionResult = doAction();
                    if (actionResult && (future.isDone())) {
                        numAfterDone++;
                    }
                    // the following Exceptions derive from DoNotRetryIOException. They are considered
                    // fatal for the purpose of this test. If we see one of these, it means something is
                    // broken and needs investigation. This is not the case for all children of DNRIOE.
                    // Unfortunately, this is an explicit enumeration and will need periodically refreshed.
                    // See HBASE-9655 for further discussion.
                } catch (AccessDeniedException e) {
                    throw e;
                } catch (CoprocessorException e) {
                    throw e;
                } catch (FatalConnectionException e) {
                    throw e;
                } catch (InvalidFamilyOperationException e) {
                    throw e;
                } catch (NamespaceExistException e) {
                    throw e;
                } catch (NamespaceNotFoundException e) {
                    throw e;
                } catch (NoSuchColumnFamilyException e) {
                    throw e;
                } catch (TableExistsException e) {
                    throw e;
                } catch (TableNotFoundException e) {
                    throw e;
                } catch (RetriesExhaustedException e) {
                    throw e;
                    // Everything else is potentially recoverable on the application side. For instance, a CM
                    // action kills the RS that hosted a scanner the client was using. Continued use of that
                    // scanner should be terminated, but a new scanner can be created and the read attempted
                    // again.
                } catch (Exception e) {
                    resetCount++;
                    if (resetCount < maxIterations) {
                        IntegrationTestMTTR.LOG.info((("Non-fatal exception while running " + (this.toString())) + ". Resetting loop counter"), e);
                        numAfterDone = 0;
                    } else {
                        IntegrationTestMTTR.LOG.info("Too many unexpected Exceptions. Aborting.", e);
                        throw e;
                    }
                }
                result.addResult(((System.nanoTime()) - start), span);
            } 
            return result;
        }

        protected abstract boolean doAction() throws Exception;

        protected String getSpanName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String toString() {
            return this.getSpanName();
        }
    }

    /**
     * Callable that will keep putting small amounts of data into a table
     * until  the future supplied returns.  It keeps track of the max time.
     */
    static class PutCallable extends IntegrationTestMTTR.TimingCallable {
        private final Table table;

        public PutCallable(Future<?> f) throws IOException {
            super(f);
            this.table = getConnection().getTable(IntegrationTestMTTR.tableName);
        }

        @Override
        protected boolean doAction() throws Exception {
            Put p = new Put(Bytes.toBytes(RandomStringUtils.randomAlphanumeric(5)));
            p.addColumn(IntegrationTestMTTR.FAMILY, Bytes.toBytes("\u0000"), Bytes.toBytes(RandomStringUtils.randomAscii(5)));
            table.put(p);
            return true;
        }

        @Override
        protected String getSpanName() {
            return "MTTR Put Test";
        }
    }

    /**
     * Callable that will keep scanning for small amounts of data until the
     * supplied future returns.  Returns the max time taken to scan.
     */
    static class ScanCallable extends IntegrationTestMTTR.TimingCallable {
        private final Table table;

        public ScanCallable(Future<?> f) throws IOException {
            super(f);
            this.table = getConnection().getTable(IntegrationTestMTTR.tableName);
        }

        @Override
        protected boolean doAction() throws Exception {
            ResultScanner rs = null;
            try {
                Scan s = new Scan();
                s.setBatch(2);
                s.addFamily(IntegrationTestMTTR.FAMILY);
                s.setFilter(new KeyOnlyFilter());
                s.setMaxVersions(1);
                rs = table.getScanner(s);
                Result result = rs.next();
                return (result != null) && ((result.size()) > 0);
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }

        @Override
        protected String getSpanName() {
            return "MTTR Scan Test";
        }
    }

    /**
     * Callable that will keep going to the master for cluster status.  Returns the max time taken.
     */
    static class AdminCallable extends IntegrationTestMTTR.TimingCallable {
        public AdminCallable(Future<?> f) throws IOException {
            super(f);
        }

        @Override
        protected boolean doAction() throws Exception {
            Admin admin = null;
            try {
                admin = getAdmin();
                ClusterStatus status = admin.getClusterStatus();
                return status != null;
            } finally {
                if (admin != null) {
                    admin.close();
                }
            }
        }

        @Override
        protected String getSpanName() {
            return "MTTR Admin Test";
        }
    }

    static class ActionCallable implements Callable<Boolean> {
        private final Action action;

        public ActionCallable(Action action) {
            this.action = action;
        }

        @Override
        public Boolean call() throws Exception {
            this.action.perform();
            return true;
        }
    }

    /**
     * Callable used to make sure the cluster has some load on it.
     * This callable uses LoadTest tool to
     */
    public static class LoadCallable implements Callable<Boolean> {
        private final Future<?> future;

        public LoadCallable(Future<?> f) {
            future = f;
        }

        @Override
        public Boolean call() throws Exception {
            int colsPerKey = 10;
            int numServers = getHBaseClusterInterface().getInitialClusterMetrics().getLiveServerMetrics().size();
            int numKeys = numServers * 5000;
            int writeThreads = 10;
            // Loop until the chaos monkey future is done.
            // But always go in just in case some action completes quickly
            do {
                int ret = IntegrationTestMTTR.loadTool.run(new String[]{ "-tn", IntegrationTestMTTR.loadTableName.getNameAsString(), "-write", String.format("%d:%d:%d", colsPerKey, 500, writeThreads), "-num_keys", String.valueOf(numKeys), "-skip_init" });
                Assert.assertEquals("Load failed", 0, ret);
            } while (!(future.isDone()) );
            return true;
        }
    }
}

