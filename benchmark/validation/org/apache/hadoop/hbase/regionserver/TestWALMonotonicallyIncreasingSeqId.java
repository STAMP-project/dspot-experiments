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
package org.apache.hadoop.hbase.regionserver;


import HConstants.HBASE_DIR;
import WAL.Entry;
import WAL.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.regionserver.wal.AbstractFSWAL;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for HBASE-17471.
 * <p>
 * MVCCPreAssign is added by HBASE-16698, but pre-assign mvcc is only used in put/delete path. Other
 * write paths like increment/append still assign mvcc in ringbuffer's consumer thread. If put and
 * increment are used parallel. Then seqid in WAL may not increase monotonically Disorder in wals
 * will lead to data loss.
 * <p>
 * This case use two thread to put and increment at the same time in a single region. Then check the
 * seqid in WAL. If seqid is wal is not monotonically increasing, this case will fail
 */
@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, SmallTests.class })
public class TestWALMonotonicallyIncreasingSeqId {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALMonotonicallyIncreasingSeqId.class);

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Path testDir = getDataTestDir("TestWALMonotonicallyIncreasingSeqId");

    private WALFactory wals;

    private FileSystem fileSystem;

    private Configuration walConf;

    private HRegion region;

    @Parameterized.Parameter
    public String walProvider;

    @Rule
    public TestName name = new TestName();

    CountDownLatch latch = new CountDownLatch(1);

    public class PutThread extends Thread {
        HRegion region;

        public PutThread(HRegion region) {
            this.region = region;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 100; i++) {
                    byte[] row = Bytes.toBytes(("putRow" + i));
                    Put put = new Put(row);
                    put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes(0), new byte[0]);
                    latch.await();
                    region.batchMutate(new Mutation[]{ put });
                    Thread.sleep(10);
                }
            } catch (Throwable t) {
                LOG.warn("Error happend when Increment: ", t);
            }
        }
    }

    public class IncThread extends Thread {
        HRegion region;

        public IncThread(HRegion region) {
            this.region = region;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 100; i++) {
                    byte[] row = Bytes.toBytes(("incrementRow" + i));
                    Increment inc = new Increment(row);
                    inc.addColumn(Bytes.toBytes("cf"), Bytes.toBytes(0), 1);
                    // inc.setDurability(Durability.ASYNC_WAL);
                    region.increment(inc);
                    latch.countDown();
                    Thread.sleep(10);
                }
            } catch (Throwable t) {
                LOG.warn("Error happend when Put: ", t);
            }
        }
    }

    @Test
    public void testWALMonotonicallyIncreasingSeqId() throws Exception {
        List<Thread> putThreads = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            putThreads.add(new TestWALMonotonicallyIncreasingSeqId.PutThread(region));
        }
        TestWALMonotonicallyIncreasingSeqId.IncThread incThread = new TestWALMonotonicallyIncreasingSeqId.IncThread(region);
        for (int i = 0; i < 1; i++) {
            putThreads.get(i).start();
        }
        incThread.start();
        incThread.join();
        Path logPath = ((AbstractFSWAL<?>) (region.getWAL())).getCurrentFileName();
        region.getWAL().rollWriter();
        Thread.sleep(10);
        Path hbaseDir = new Path(walConf.get(HBASE_DIR));
        Path oldWalsDir = new Path(hbaseDir, HConstants.HREGION_OLDLOGDIR_NAME);
        try (WAL.Reader reader = createReader(logPath, oldWalsDir)) {
            long currentMaxSeqid = 0;
            for (WAL.Entry e; (e = reader.next()) != null;) {
                if (!(WALEdit.isMetaEditFamily(e.getEdit().getCells().get(0)))) {
                    long currentSeqid = e.getKey().getSequenceId();
                    if (currentSeqid > currentMaxSeqid) {
                        currentMaxSeqid = currentSeqid;
                    } else {
                        Assert.fail(((("Current max Seqid is " + currentMaxSeqid) + ", but the next seqid in wal is smaller:") + currentSeqid));
                    }
                }
            }
        }
    }
}

