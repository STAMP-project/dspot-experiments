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
package org.apache.hadoop.hdfs.server.datanode;


import BlockMetadataHeader.VERSION;
import DataChecksum.Type.NULL;
import FsDatasetSpi.Factory;
import StorageType.DEFAULT;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsDatasetFactory;
import org.apache.hadoop.util.DataChecksum;
import org.junit.Assert;
import org.junit.Test;


/**
 * this class tests the methods of the  SimulatedFSDataset.
 */
public class TestSimulatedFSDataset {
    Configuration conf = null;

    static final String bpid = "BP-TEST";

    static final int NUMBLOCKS = 20;

    static final int BLOCK_LENGTH_MULTIPLIER = 79;

    static final long FIRST_BLK_ID = 1;

    private final int storageCount;

    public TestSimulatedFSDataset() {
        this(1);
    }

    protected TestSimulatedFSDataset(int storageCount) {
        this.storageCount = storageCount;
    }

    @Test
    public void testFSDatasetFactory() {
        final Configuration conf = new Configuration();
        FsDatasetSpi.Factory<?> f = Factory.getFactory(conf);
        Assert.assertEquals(FsDatasetFactory.class, f.getClass());
        Assert.assertFalse(f.isSimulated());
        SimulatedFSDataset.setFactory(conf);
        FsDatasetSpi.Factory<?> s = Factory.getFactory(conf);
        Assert.assertEquals(SimulatedFSDataset.Factory.class, s.getClass());
        Assert.assertTrue(s.isSimulated());
    }

    @Test
    public void testGetMetaData() throws IOException {
        final SimulatedFSDataset fsdataset = getSimulatedFSDataset();
        ExtendedBlock b = new ExtendedBlock(TestSimulatedFSDataset.bpid, TestSimulatedFSDataset.FIRST_BLK_ID, 5, 0);
        try {
            Assert.assertTrue(((fsdataset.getMetaDataInputStream(b)) == null));
            Assert.assertTrue("Expected an IO exception", false);
        } catch (IOException e) {
            // ok - as expected
        }
        TestSimulatedFSDataset.addSomeBlocks(fsdataset);// Only need to add one but ....

        b = new ExtendedBlock(TestSimulatedFSDataset.bpid, TestSimulatedFSDataset.FIRST_BLK_ID, 0, 0);
        InputStream metaInput = fsdataset.getMetaDataInputStream(b);
        DataInputStream metaDataInput = new DataInputStream(metaInput);
        short version = metaDataInput.readShort();
        Assert.assertEquals(VERSION, version);
        DataChecksum checksum = DataChecksum.newDataChecksum(metaDataInput);
        Assert.assertEquals(NULL, checksum.getChecksumType());
        Assert.assertEquals(0, checksum.getChecksumSize());
    }

    @Test
    public void testStorageUsage() throws IOException {
        final SimulatedFSDataset fsdataset = getSimulatedFSDataset();
        Assert.assertEquals(fsdataset.getDfsUsed(), 0);
        Assert.assertEquals(fsdataset.getRemaining(), fsdataset.getCapacity());
        int bytesAdded = TestSimulatedFSDataset.addSomeBlocks(fsdataset);
        Assert.assertEquals(bytesAdded, fsdataset.getDfsUsed());
        Assert.assertEquals(((fsdataset.getCapacity()) - bytesAdded), fsdataset.getRemaining());
    }

    @Test
    public void testWriteRead() throws IOException {
        testWriteRead(false);
        testWriteRead(true);
    }

    @Test
    public void testGetBlockReport() throws IOException {
        final SimulatedFSDataset fsdataset = getSimulatedFSDataset();
        assertBlockReportCountAndSize(fsdataset, 0);
        TestSimulatedFSDataset.addSomeBlocks(fsdataset);
        assertBlockReportCountAndSize(fsdataset, TestSimulatedFSDataset.NUMBLOCKS);
        assertBlockLengthInBlockReports(fsdataset);
    }

    @Test
    public void testInjectionEmpty() throws IOException {
        SimulatedFSDataset fsdataset = getSimulatedFSDataset();
        assertBlockReportCountAndSize(fsdataset, 0);
        int bytesAdded = TestSimulatedFSDataset.addSomeBlocks(fsdataset);
        assertBlockReportCountAndSize(fsdataset, TestSimulatedFSDataset.NUMBLOCKS);
        assertBlockLengthInBlockReports(fsdataset);
        // Inject blocks into an empty fsdataset
        // - injecting the blocks we got above.
        SimulatedFSDataset sfsdataset = getSimulatedFSDataset();
        injectBlocksFromBlockReport(fsdataset, sfsdataset);
        assertBlockReportCountAndSize(fsdataset, TestSimulatedFSDataset.NUMBLOCKS);
        assertBlockLengthInBlockReports(fsdataset, sfsdataset);
        Assert.assertEquals(bytesAdded, sfsdataset.getDfsUsed());
        Assert.assertEquals(((sfsdataset.getCapacity()) - bytesAdded), sfsdataset.getRemaining());
    }

    @Test
    public void testInjectionNonEmpty() throws IOException {
        SimulatedFSDataset fsdataset = getSimulatedFSDataset();
        assertBlockReportCountAndSize(fsdataset, 0);
        int bytesAdded = TestSimulatedFSDataset.addSomeBlocks(fsdataset);
        assertBlockReportCountAndSize(fsdataset, TestSimulatedFSDataset.NUMBLOCKS);
        assertBlockLengthInBlockReports(fsdataset);
        // Inject blocks into an non-empty fsdataset
        // - injecting the blocks we got above.
        SimulatedFSDataset sfsdataset = getSimulatedFSDataset();
        // Add come blocks whose block ids do not conflict with
        // the ones we are going to inject.
        bytesAdded += TestSimulatedFSDataset.addSomeBlocks(sfsdataset, ((TestSimulatedFSDataset.NUMBLOCKS) + 1), false);
        assertBlockReportCountAndSize(sfsdataset, TestSimulatedFSDataset.NUMBLOCKS);
        injectBlocksFromBlockReport(fsdataset, sfsdataset);
        assertBlockReportCountAndSize(sfsdataset, ((TestSimulatedFSDataset.NUMBLOCKS) * 2));
        assertBlockLengthInBlockReports(fsdataset, sfsdataset);
        Assert.assertEquals(bytesAdded, sfsdataset.getDfsUsed());
        Assert.assertEquals(((sfsdataset.getCapacity()) - bytesAdded), sfsdataset.getRemaining());
        // Now test that the dataset cannot be created if it does not have sufficient cap
        conf.setLong(SimulatedFSDataset.CONFIG_PROPERTY_CAPACITY, 10);
        try {
            sfsdataset = getSimulatedFSDataset();
            sfsdataset.addBlockPool(TestSimulatedFSDataset.bpid, conf);
            injectBlocksFromBlockReport(fsdataset, sfsdataset);
            Assert.assertTrue("Expected an IO exception", false);
        } catch (IOException e) {
            // ok - as expected
        }
    }

    @Test
    public void testInValidBlocks() throws IOException {
        final SimulatedFSDataset fsdataset = getSimulatedFSDataset();
        ExtendedBlock b = new ExtendedBlock(TestSimulatedFSDataset.bpid, TestSimulatedFSDataset.FIRST_BLK_ID, 5, 0);
        checkInvalidBlock(b);
        // Now check invlaid after adding some blocks
        TestSimulatedFSDataset.addSomeBlocks(fsdataset);
        b = new ExtendedBlock(TestSimulatedFSDataset.bpid, ((TestSimulatedFSDataset.NUMBLOCKS) + 99), 5, 0);
        checkInvalidBlock(b);
    }

    @Test
    public void testInvalidate() throws IOException {
        final SimulatedFSDataset fsdataset = getSimulatedFSDataset();
        int bytesAdded = TestSimulatedFSDataset.addSomeBlocks(fsdataset);
        Block[] deleteBlocks = new Block[2];
        deleteBlocks[0] = new Block(1, 0, 0);
        deleteBlocks[1] = new Block(2, 0, 0);
        fsdataset.invalidate(TestSimulatedFSDataset.bpid, deleteBlocks);
        checkInvalidBlock(new ExtendedBlock(TestSimulatedFSDataset.bpid, deleteBlocks[0]));
        checkInvalidBlock(new ExtendedBlock(TestSimulatedFSDataset.bpid, deleteBlocks[1]));
        long sizeDeleted = (TestSimulatedFSDataset.blockIdToLen(1)) + (TestSimulatedFSDataset.blockIdToLen(2));
        Assert.assertEquals((bytesAdded - sizeDeleted), fsdataset.getDfsUsed());
        Assert.assertEquals((((fsdataset.getCapacity()) - bytesAdded) + sizeDeleted), fsdataset.getRemaining());
        // Now make sure the rest of the blocks are valid
        for (int i = 3; i <= (TestSimulatedFSDataset.NUMBLOCKS); ++i) {
            Block b = new Block(i, 0, 0);
            Assert.assertTrue(fsdataset.isValidBlock(new ExtendedBlock(TestSimulatedFSDataset.bpid, b)));
        }
    }

    @Test
    public void testConcurrentAddBlockPool() throws IOException, InterruptedException {
        final String[] bpids = new String[]{ "BP-TEST1-", "BP-TEST2-" };
        final SimulatedFSDataset fsdataset = new SimulatedFSDataset(null, conf);
        class AddBlockPoolThread extends Thread {
            private int id;

            private IOException ioe;

            public AddBlockPoolThread(int id) {
                super();
                this.id = id;
            }

            public void test() throws IOException, InterruptedException {
                this.join();
                if ((ioe) != null) {
                    throw ioe;
                }
            }

            public void run() {
                for (int i = 0; i < 10000; i++) {
                    // add different block pools concurrently
                    String newbpid = (bpids[id]) + i;
                    fsdataset.addBlockPool(newbpid, conf);
                    // and then add a block into the pool
                    ExtendedBlock block = new ExtendedBlock(newbpid, 1);
                    try {
                        // it will throw an exception if the block pool is not found
                        fsdataset.createTemporary(DEFAULT, null, block, false);
                    } catch (IOException ioe) {
                        // JUnit does not capture exception in non-main thread,
                        // so cache it and then let main thread throw later.
                        this.ioe = ioe;
                    }
                    assert (fsdataset.getReplicaString(newbpid, 1)) != "null";
                }
            }
        }
        AddBlockPoolThread t1 = new AddBlockPoolThread(0);
        AddBlockPoolThread t2 = new AddBlockPoolThread(1);
        t1.start();
        t2.start();
        t1.test();
        t2.test();
    }
}

