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
package org.apache.hadoop.hdfs;


import DFSClientFaultInjector.exceptionNum;
import DataTransferProtocol.LOG;
import HdfsClientConfigKeys.HedgedRead.THREADPOOL_SIZE_KEY;
import HdfsClientConfigKeys.HedgedRead.THRESHOLD_MILLIS_KEY;
import HdfsClientConfigKeys.Read.PREFETCH_SIZE_KEY;
import HdfsClientConfigKeys.Retry.WINDOW_BASE_KEY;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ChecksumException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests the DFS positional read functionality in a single node
 * mini-cluster.
 */
public class TestPread {
    static final long seed = 3735928559L;

    static final int blockSize = 4096;

    static final int numBlocksPerFile = 12;

    static final int fileSize = (TestPread.numBlocksPerFile) * (TestPread.blockSize);

    boolean simulatedStorage;

    boolean isHedgedRead;

    private static final Logger LOG = LoggerFactory.getLogger(TestPread.class.getName());

    /**
     * Tests positional read in DFS.
     */
    @Test
    public void testPreadDFS() throws IOException {
        Configuration conf = new Configuration();
        dfsPreadTest(conf, false, true);// normal pread

        dfsPreadTest(conf, true, true);// trigger read code path without

        // transferTo.
    }

    @Test
    public void testPreadDFSNoChecksum() throws IOException {
        Configuration conf = new Configuration();
        GenericTestUtils.setLogLevel(DataTransferProtocol.LOG, Level.ALL);
        dfsPreadTest(conf, false, false);
        dfsPreadTest(conf, true, false);
    }

    /**
     * Tests positional read in DFS, with hedged reads enabled.
     */
    @Test
    public void testHedgedPreadDFSBasic() throws IOException {
        isHedgedRead = true;
        Configuration conf = new Configuration();
        conf.setInt(THREADPOOL_SIZE_KEY, 5);
        conf.setLong(THRESHOLD_MILLIS_KEY, 1);
        dfsPreadTest(conf, false, true);// normal pread

        dfsPreadTest(conf, true, true);// trigger read code path without

        // transferTo.
    }

    @Test
    public void testHedgedReadLoopTooManyTimes() throws IOException {
        Configuration conf = new Configuration();
        int numHedgedReadPoolThreads = 5;
        final int hedgedReadTimeoutMillis = 50;
        conf.setInt(THREADPOOL_SIZE_KEY, numHedgedReadPoolThreads);
        conf.setLong(THRESHOLD_MILLIS_KEY, hedgedReadTimeoutMillis);
        conf.setInt(WINDOW_BASE_KEY, 0);
        // Set up the InjectionHandler
        DFSClientFaultInjector.set(Mockito.mock(DFSClientFaultInjector.class));
        DFSClientFaultInjector injector = DFSClientFaultInjector.get();
        final int sleepMs = 100;
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                if (true) {
                    Thread.sleep((hedgedReadTimeoutMillis + sleepMs));
                    if (exceptionNum.compareAndSet(0, 1)) {
                        System.out.println("-------------- throw Checksum Exception");
                        throw new ChecksumException("ChecksumException test", 100);
                    }
                }
                return null;
            }
        }).when(injector).fetchFromDatanodeException();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                if (true) {
                    Thread.sleep((sleepMs * 2));
                }
                return null;
            }
        }).when(injector).readFromDatanodeDelay();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).format(true).build();
        DistributedFileSystem fileSys = cluster.getFileSystem();
        DFSClient dfsClient = fileSys.getClient();
        FSDataOutputStream output = null;
        DFSInputStream input = null;
        String filename = "/hedgedReadMaxOut.dat";
        try {
            Path file = new Path(filename);
            output = fileSys.create(file, ((short) (2)));
            byte[] data = new byte[64 * 1024];
            output.write(data);
            output.flush();
            output.write(data);
            output.flush();
            output.write(data);
            output.flush();
            output.close();
            byte[] buffer = new byte[64 * 1024];
            input = dfsClient.open(filename);
            input.read(0, buffer, 0, 1024);
            input.close();
            Assert.assertEquals(3, input.getHedgedReadOpsLoopNumForTesting());
        } catch (BlockMissingException e) {
            Assert.assertTrue(false);
        } finally {
            Mockito.reset(injector);
            IOUtils.cleanup(null, input);
            IOUtils.cleanup(null, output);
            fileSys.close();
            cluster.shutdown();
        }
    }

    @Test
    public void testMaxOutHedgedReadPool() throws IOException, InterruptedException, ExecutionException {
        isHedgedRead = true;
        Configuration conf = new Configuration();
        int numHedgedReadPoolThreads = 5;
        final int initialHedgedReadTimeoutMillis = 50000;
        final int fixedSleepIntervalMillis = 50;
        conf.setInt(THREADPOOL_SIZE_KEY, numHedgedReadPoolThreads);
        conf.setLong(THRESHOLD_MILLIS_KEY, initialHedgedReadTimeoutMillis);
        // Set up the InjectionHandler
        DFSClientFaultInjector.set(Mockito.mock(DFSClientFaultInjector.class));
        DFSClientFaultInjector injector = DFSClientFaultInjector.get();
        // make preads sleep for 50ms
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(fixedSleepIntervalMillis);
                return null;
            }
        }).when(injector).startFetchFromDatanode();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).format(true).build();
        DistributedFileSystem fileSys = cluster.getFileSystem();
        DFSClient dfsClient = fileSys.getClient();
        DFSHedgedReadMetrics metrics = dfsClient.getHedgedReadMetrics();
        // Metrics instance is static, so we need to reset counts from prior tests.
        metrics.hedgedReadOps.set(0);
        metrics.hedgedReadOpsWin.set(0);
        metrics.hedgedReadOpsInCurThread.set(0);
        try {
            Path file1 = new Path("hedgedReadMaxOut.dat");
            writeFile(fileSys, file1);
            // Basic test. Reads complete within timeout. Assert that there were no
            // hedged reads.
            pReadFile(fileSys, file1);
            // assert that there were no hedged reads. 50ms + delta < 500ms
            Assert.assertTrue(((metrics.getHedgedReadOps()) == 0));
            Assert.assertTrue(((metrics.getHedgedReadOpsInCurThread()) == 0));
            /* Reads take longer than timeout. But, only one thread reading. Assert
            that there were hedged reads. But, none of the reads had to run in the
            current thread.
             */
            {
                Configuration conf2 = new Configuration(cluster.getConfiguration(0));
                conf2.setBoolean("fs.hdfs.impl.disable.cache", true);
                conf2.setLong(THRESHOLD_MILLIS_KEY, 50);
                fileSys.close();
                fileSys = ((DistributedFileSystem) (FileSystem.get(cluster.getURI(0), conf2)));
                metrics = fileSys.getClient().getHedgedReadMetrics();
            }
            pReadFile(fileSys, file1);
            // assert that there were hedged reads
            Assert.assertTrue(((metrics.getHedgedReadOps()) > 0));
            Assert.assertTrue(((metrics.getHedgedReadOpsInCurThread()) == 0));
            /* Multiple threads reading. Reads take longer than timeout. Assert that
            there were hedged reads. And that reads had to run in the current
            thread.
             */
            int factor = 10;
            int numHedgedReads = numHedgedReadPoolThreads * factor;
            long initialReadOpsValue = metrics.getHedgedReadOps();
            ExecutorService executor = Executors.newFixedThreadPool(numHedgedReads);
            ArrayList<Future<Void>> futures = new ArrayList<Future<Void>>();
            for (int i = 0; i < numHedgedReads; i++) {
                futures.add(executor.submit(getPReadFileCallable(fileSys, file1)));
            }
            for (int i = 0; i < numHedgedReads; i++) {
                futures.get(i).get();
            }
            Assert.assertTrue(((metrics.getHedgedReadOps()) > initialReadOpsValue));
            Assert.assertTrue(((metrics.getHedgedReadOpsInCurThread()) > 0));
            cleanupFile(fileSys, file1);
            executor.shutdown();
        } finally {
            fileSys.close();
            cluster.shutdown();
            Mockito.reset(injector);
        }
    }

    @Test
    public void testPreadDFSSimulated() throws IOException {
        simulatedStorage = true;
        testPreadDFS();
    }

    /**
     * Tests positional read in LocalFS.
     */
    @Test
    public void testPreadLocalFS() throws IOException {
        Configuration conf = new HdfsConfiguration();
        FileSystem fileSys = FileSystem.getLocal(conf);
        try {
            Path file1 = new Path(GenericTestUtils.getTempPath("preadtest.dat"));
            writeFile(fileSys, file1);
            pReadFile(fileSys, file1);
            cleanupFile(fileSys, file1);
        } finally {
            fileSys.close();
        }
    }

    @Test
    public void testTruncateWhileReading() throws Exception {
        Path path = new Path("/testfile");
        final int blockSize = 512;
        // prevent initial pre-fetch of multiple block locations
        Configuration conf = new Configuration();
        conf.setLong(PREFETCH_SIZE_KEY, blockSize);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        try {
            DistributedFileSystem fs = cluster.getFileSystem();
            // create multi-block file
            FSDataOutputStream dos = fs.create(path, true, blockSize, ((short) (1)), blockSize);
            dos.write(new byte[blockSize * 3]);
            dos.close();
            // truncate a file while it's open
            final FSDataInputStream dis = fs.open(path);
            while (!(fs.truncate(path, 10))) {
                Thread.sleep(10);
            } 
            // verify that reading bytes outside the initial pre-fetch do
            // not send the client into an infinite loop querying locations.
            ExecutorService executor = Executors.newFixedThreadPool(1);
            Future<?> future = executor.submit(new Callable<Void>() {
                @Override
                public Void call() throws IOException {
                    // read from 2nd block.
                    dis.readFully(blockSize, new byte[4]);
                    return null;
                }
            });
            try {
                future.get(4, TimeUnit.SECONDS);
                Assert.fail();
            } catch (ExecutionException ee) {
                Assert.assertTrue(ee.toString(), ((ee.getCause()) instanceof EOFException));
            } finally {
                future.cancel(true);
                executor.shutdown();
            }
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 30000)
    public void testHedgedReadFromAllDNFailed() throws IOException {
        Configuration conf = new Configuration();
        int numHedgedReadPoolThreads = 5;
        final int hedgedReadTimeoutMillis = 50;
        conf.setInt(THREADPOOL_SIZE_KEY, numHedgedReadPoolThreads);
        conf.setLong(THRESHOLD_MILLIS_KEY, hedgedReadTimeoutMillis);
        conf.setInt(WINDOW_BASE_KEY, 0);
        // Set up the InjectionHandler
        DFSClientFaultInjector.set(Mockito.mock(DFSClientFaultInjector.class));
        DFSClientFaultInjector injector = DFSClientFaultInjector.get();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                if (true) {
                    TestPread.LOG.info("-------------- throw Checksum Exception");
                    throw new ChecksumException("ChecksumException test", 100);
                }
                return null;
            }
        }).when(injector).fetchFromDatanodeException();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).format(true).build();
        DistributedFileSystem fileSys = cluster.getFileSystem();
        DFSClient dfsClient = fileSys.getClient();
        FSDataOutputStream output = null;
        DFSInputStream input = null;
        String filename = "/hedgedReadMaxOut.dat";
        DFSHedgedReadMetrics metrics = dfsClient.getHedgedReadMetrics();
        // Metrics instance is static, so we need to reset counts from prior tests.
        metrics.hedgedReadOps.set(0);
        try {
            Path file = new Path(filename);
            output = fileSys.create(file, ((short) (2)));
            byte[] data = new byte[64 * 1024];
            output.write(data);
            output.flush();
            output.close();
            byte[] buffer = new byte[64 * 1024];
            input = dfsClient.open(filename);
            input.read(0, buffer, 0, 1024);
            Assert.fail("Reading the block should have thrown BlockMissingException");
        } catch (BlockMissingException e) {
            Assert.assertEquals(3, input.getHedgedReadOpsLoopNumForTesting());
            Assert.assertTrue(((metrics.getHedgedReadOps()) == 0));
        } finally {
            Mockito.reset(injector);
            IOUtils.cleanupWithLogger(TestPread.LOG, input);
            IOUtils.cleanupWithLogger(TestPread.LOG, output);
            fileSys.close();
            cluster.shutdown();
        }
    }

    /**
     * Scenario: 1. Write a file with RF=2, DN1 and DN2<br>
     * 2. Open the stream, Consider Locations are [DN1, DN2] in LocatedBlock.<br>
     * 3. Move block from DN2 to DN3.<br>
     * 4. Let block gets replicated to another DN3<br>
     * 5. Stop DN1 also.<br>
     * 6. Current valid Block locations in NameNode [DN1, DN3]<br>
     * 7. Consider next calls to getBlockLocations() always returns DN3 as last
     * location.<br>
     */
    @Test
    public void testPreadFailureWithChangedBlockLocations() throws Exception {
        doPreadTestWithChangedLocations(1);
    }

    /**
     * Scenario: 1. Write a file with RF=2, DN1 and DN2<br>
     * 2. Open the stream, Consider Locations are [DN1, DN2] in LocatedBlock.<br>
     * 3. Move block from DN2 to DN3.<br>
     * 4. Let block gets replicated to another DN3<br>
     * 5. Stop DN1 also.<br>
     * 6. Current valid Block locations in NameNode [DN1, DN3]<br>
     * 7. Consider next calls to getBlockLocations() always returns DN3 as last
     * location.<br>
     */
    @Test(timeout = 60000)
    public void testPreadHedgedFailureWithChangedBlockLocations() throws Exception {
        isHedgedRead = true;
        DFSClientFaultInjector old = DFSClientFaultInjector.get();
        try {
            DFSClientFaultInjector.set(new DFSClientFaultInjector() {
                public void sleepBeforeHedgedGet() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            });
            doPreadTestWithChangedLocations(2);
        } finally {
            DFSClientFaultInjector.set(old);
        }
    }
}

