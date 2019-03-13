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
package org.apache.tomcat.jdbc.test;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.apache.tomcat.jdbc.test.driver.Driver;
import org.junit.Test;


public class ConnectCountTest extends DefaultTestCase {
    protected boolean run = true;

    protected long sleep = Long.getLong("sleep", 10).longValue();

    protected long complete = Long.getLong("complete", 20000).longValue();

    protected boolean printthread = Boolean.getBoolean("printthread");

    CountDownLatch latch = null;

    @Test
    public void testDBCPThreads20Connections10() throws Exception {
        System.out.println("[testDBCPThreads20Connections10] Starting fairness - DBCP");
        this.threadcount = 20;
        this.transferProperties();
        this.tDatasource.getConnection().close();
        latch = new CountDownLatch(threadcount);
        long start = System.currentTimeMillis();
        ConnectCountTest.TestThread[] threads = new ConnectCountTest.TestThread[threadcount];
        for (int i = 0; i < (threadcount); i++) {
            threads[i] = new ConnectCountTest.TestThread();
            threads[i].setName(("tomcat-dbcp-" + i));
            threads[i].d = this.tDatasource;
        }
        for (int i = 0; i < (threadcount); i++) {
            threads[i].start();
        }
        if (!(latch.await(((complete) + 1000), TimeUnit.MILLISECONDS))) {
            System.out.println("Latch timed out.");
        }
        this.run = false;
        long delta = (System.currentTimeMillis()) - start;
        printThreadResults(threads, "testDBCPThreads20Connections10", Driver.connectCount.get(), 10);
        System.out.println((("Test completed in: " + delta) + "ms."));
    }

    @Test
    public void testPoolThreads20Connections10() throws Exception {
        System.out.println("[testPoolThreads20Connections10] Starting fairness - Tomcat JDBC - Non Fair");
        this.threadcount = 20;
        this.transferProperties();
        this.datasource.getConnection().close();
        latch = new CountDownLatch(threadcount);
        long start = System.currentTimeMillis();
        ConnectCountTest.TestThread[] threads = new ConnectCountTest.TestThread[threadcount];
        for (int i = 0; i < (threadcount); i++) {
            threads[i] = new ConnectCountTest.TestThread();
            threads[i].setName(("tomcat-pool-" + i));
            threads[i].d = this.datasource;
        }
        for (int i = 0; i < (threadcount); i++) {
            threads[i].start();
        }
        if (!(latch.await(((complete) + 1000), TimeUnit.MILLISECONDS))) {
            System.out.println("Latch timed out.");
        }
        this.run = false;
        long delta = (System.currentTimeMillis()) - start;
        printThreadResults(threads, "testPoolThreads20Connections10", Driver.connectCount.get(), 10);
        System.out.println((("Test completed in: " + delta) + "ms."));
    }

    @Test
    public void testPoolThreads20Connections10Fair() throws Exception {
        System.out.println("[testPoolThreads20Connections10Fair] Starting fairness - Tomcat JDBC - Fair");
        this.threadcount = 20;
        this.datasource.getPoolProperties().setFairQueue(true);
        this.transferProperties();
        this.datasource.getConnection().close();
        latch = new CountDownLatch(threadcount);
        long start = System.currentTimeMillis();
        ConnectCountTest.TestThread[] threads = new ConnectCountTest.TestThread[threadcount];
        for (int i = 0; i < (threadcount); i++) {
            threads[i] = new ConnectCountTest.TestThread();
            threads[i].setName(("tomcat-pool-" + i));
            threads[i].d = this.datasource;
        }
        for (int i = 0; i < (threadcount); i++) {
            threads[i].start();
        }
        if (!(latch.await(((complete) + 1000), TimeUnit.MILLISECONDS))) {
            System.out.println("Latch timed out.");
        }
        this.run = false;
        long delta = (System.currentTimeMillis()) - start;
        printThreadResults(threads, "testPoolThreads20Connections10Fair", Driver.connectCount.get(), 10);
        System.out.println((("Test completed in: " + delta) + "ms."));
    }

    @Test
    public void testPoolThreads20Connections10FairAsync() throws Exception {
        System.out.println("[testPoolThreads20Connections10FairAsync] Starting fairness - Tomcat JDBC - Fair - Async");
        this.threadcount = 20;
        this.datasource.getPoolProperties().setFairQueue(true);
        this.datasource.getPoolProperties().setInitialSize(this.datasource.getPoolProperties().getMaxActive());
        this.transferProperties();
        this.datasource.getConnection().close();
        latch = new CountDownLatch(threadcount);
        long start = System.currentTimeMillis();
        ConnectCountTest.TestThread[] threads = new ConnectCountTest.TestThread[threadcount];
        for (int i = 0; i < (threadcount); i++) {
            threads[i] = new ConnectCountTest.TestThread();
            threads[i].setName(("tomcat-pool-" + i));
            threads[i].async = true;
            threads[i].d = this.datasource;
        }
        for (int i = 0; i < (threadcount); i++) {
            threads[i].start();
        }
        if (!(latch.await(((complete) + 1000), TimeUnit.MILLISECONDS))) {
            System.out.println("Latch timed out.");
        }
        this.run = false;
        long delta = (System.currentTimeMillis()) - start;
        printThreadResults(threads, "testPoolThreads20Connections10FairAsync", Driver.connectCount.get(), 10);
        System.out.println((("Test completed in: " + delta) + "ms."));
    }

    // @Test
    // public void testC3P0Threads20Connections10() throws Exception {
    // System.out.println("[testC3P0Threads20Connections10] Starting fairness - C3P0");
    // this.threadcount = 20;
    // this.transferPropertiesToC3P0();
    // this.datasource.getConnection().close();
    // latch = new CountDownLatch(threadcount);
    // long start = System.currentTimeMillis();
    // TestThread[] threads = new TestThread[threadcount];
    // for (int i=0; i<threadcount; i++) {
    // threads[i] = new TestThread();
    // threads[i].setName("tomcat-pool-"+i);
    // threads[i].d = this.c3p0Datasource;
    // 
    // }
    // for (int i=0; i<threadcount; i++) {
    // threads[i].start();
    // }
    // if (!latch.await(complete+1000,TimeUnit.MILLISECONDS)) {
    // System.out.println("Latch timed out.");
    // }
    // this.run = false;
    // long delta = System.currentTimeMillis() - start;
    // printThreadResults(threads,"testC3P0Threads20Connections10",Driver.connectCount.get(),10);
    // }
    public class TestThread extends Thread {
        protected DataSource d;

        protected long sleep = 10;

        protected boolean async = false;

        long minwait = Long.MAX_VALUE;

        long maxwait = -1;

        long totalwait = 0;

        long totalcmax = 0;

        long cmax = -1;

        long nroffetch = 0;

        long totalruntime = 0;

        @Override
        public void run() {
            try {
                long now = System.currentTimeMillis();
                while (ConnectCountTest.this.run) {
                    if (((System.currentTimeMillis()) - now) >= (ConnectCountTest.this.complete))
                        break;

                    long start = System.nanoTime();
                    Connection con = null;
                    try {
                        if (async) {
                            Future<Connection> cf = getConnectionAsync();
                            con = cf.get();
                        } else {
                            con = d.getConnection();
                        }
                        long delta = (System.nanoTime()) - start;
                        totalwait += delta;
                        maxwait = Math.max(delta, maxwait);
                        minwait = Math.min(delta, minwait);
                        (nroffetch)++;
                        try {
                            if ((ConnectCountTest.this.sleep) > 0)
                                Thread.sleep(ConnectCountTest.this.sleep);

                        } catch (InterruptedException x) {
                            Thread.interrupted();
                        }
                    } finally {
                        long cstart = System.nanoTime();
                        if (con != null)
                            try {
                                con.close();
                            } catch (Exception x) {
                                x.printStackTrace();
                            }

                        long cdelta = (System.nanoTime()) - cstart;
                        totalcmax += cdelta;
                        cmax = Math.max(cdelta, cmax);
                    }
                    totalruntime += (System.nanoTime()) - start;
                } 
            } catch (RuntimeException | SQLException | ExecutionException | InterruptedException x) {
                x.printStackTrace();
            } finally {
                ConnectCountTest.this.latch.countDown();
            }
            if ((System.getProperty("print-thread-stats")) != null) {
                System.out.println(((((((((((((((((((((((((("[" + (getName())) + "] ") + "\n\tMax time to retrieve connection:") + ((maxwait) / 1000000.0F)) + " ms.") + "\n\tTotal time to retrieve connection:") + ((totalwait) / 1000000.0F)) + " ms.") + "\n\tAverage time to retrieve connection:") + (((totalwait) / 1000000.0F) / (nroffetch))) + " ms.") + "\n\tMax time to close connection:") + ((cmax) / 1000000.0F)) + " ms.") + "\n\tTotal time to close connection:") + ((totalcmax) / 1000000.0F)) + " ms.") + "\n\tAverage time to close connection:") + (((totalcmax) / 1000000.0F) / (nroffetch))) + " ms.") + "\n\tRun time:") + ((totalruntime) / 1000000.0F)) + " ms.") + "\n\tNr of fetch:") + (nroffetch)));
            }
        }
    }
}

