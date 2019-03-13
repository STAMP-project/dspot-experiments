/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm.block;


import DeletedBlocksTransaction.Builder;
import Table.KeyValue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.DeletedBlocksTransaction;
import org.apache.hadoop.hdds.scm.ScmConfigKeys;
import org.apache.hadoop.hdds.scm.container.ContainerManager;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.utils.MetadataKeyFilters;
import org.apache.hadoop.utils.db.TableIterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for DeletedBlockLog.
 */
public class TestDeletedBlockLog {
    private static DeletedBlockLogImpl deletedBlockLog;

    private OzoneConfiguration conf;

    private File testDir;

    private ContainerManager containerManager;

    private StorageContainerManager scm;

    private List<DatanodeDetails> dnList;

    @Test
    public void testIncrementCount() throws Exception {
        int maxRetry = conf.getInt(ScmConfigKeys.OZONE_SCM_BLOCK_DELETION_MAX_RETRY, 20);
        // Create 30 TXs in the log.
        for (Map.Entry<Long, List<Long>> entry : generateData(30).entrySet()) {
            TestDeletedBlockLog.deletedBlockLog.addTransaction(entry.getKey(), entry.getValue());
        }
        // This will return all TXs, total num 30.
        List<DeletedBlocksTransaction> blocks = getTransactions(40);
        List<Long> txIDs = blocks.stream().map(DeletedBlocksTransaction::getTxID).collect(Collectors.toList());
        for (int i = 0; i < maxRetry; i++) {
            TestDeletedBlockLog.deletedBlockLog.incrementCount(txIDs);
        }
        // Increment another time so it exceed the maxRetry.
        // On this call, count will be set to -1 which means TX eventually fails.
        TestDeletedBlockLog.deletedBlockLog.incrementCount(txIDs);
        blocks = getTransactions(40);
        for (DeletedBlocksTransaction block : blocks) {
            Assert.assertEquals((-1), block.getCount());
        }
        // If all TXs are failed, getTransactions call will always return nothing.
        blocks = getTransactions(40);
        Assert.assertEquals(blocks.size(), 0);
    }

    @Test
    public void testCommitTransactions() throws Exception {
        for (Map.Entry<Long, List<Long>> entry : generateData(50).entrySet()) {
            TestDeletedBlockLog.deletedBlockLog.addTransaction(entry.getKey(), entry.getValue());
        }
        List<DeletedBlocksTransaction> blocks = getTransactions(20);
        // Add an invalid txn.
        blocks.add(DeletedBlocksTransaction.newBuilder().setContainerID(1).setTxID(70).setCount(0).addLocalID(0).build());
        commitTransactions(blocks);
        blocks.remove(((blocks.size()) - 1));
        blocks = getTransactions(50);
        Assert.assertEquals(30, blocks.size());
        commitTransactions(blocks, dnList.get(1), dnList.get(2), DatanodeDetails.newBuilder().setUuid(UUID.randomUUID().toString()).build());
        blocks = getTransactions(50);
        Assert.assertEquals(30, blocks.size());
        commitTransactions(blocks, dnList.get(0));
        blocks = getTransactions(50);
        Assert.assertEquals(0, blocks.size());
    }

    @Test
    public void testRandomOperateTransactions() throws Exception {
        Random random = new Random();
        int added = 0;
        int committed = 0;
        List<DeletedBlocksTransaction> blocks = new ArrayList<>();
        List<Long> txIDs = new ArrayList<>();
        byte[] latestTxid = DFSUtil.string2Bytes("#LATEST_TXID#");
        MetadataKeyFilters.MetadataKeyFilter avoidLatestTxid = ( preKey, currentKey, nextKey) -> !(Arrays.equals(latestTxid, currentKey));
        // Randomly add/get/commit/increase transactions.
        for (int i = 0; i < 100; i++) {
            int state = random.nextInt(4);
            if (state == 0) {
                for (Map.Entry<Long, List<Long>> entry : generateData(10).entrySet()) {
                    TestDeletedBlockLog.deletedBlockLog.addTransaction(entry.getKey(), entry.getValue());
                }
                added += 10;
            } else
                if (state == 1) {
                    blocks = getTransactions(20);
                    txIDs = new ArrayList<>();
                    for (DeletedBlocksTransaction block : blocks) {
                        txIDs.add(block.getTxID());
                    }
                    TestDeletedBlockLog.deletedBlockLog.incrementCount(txIDs);
                } else
                    if (state == 2) {
                        commitTransactions(blocks);
                        committed += blocks.size();
                        blocks = new ArrayList();
                    } else {
                        // verify the number of added and committed.
                        try (TableIterator<Long, ? extends KeyValue<Long, DeletedBlocksTransaction>> iter = scm.getScmMetadataStore().getDeletedBlocksTXTable().iterator()) {
                            AtomicInteger count = new AtomicInteger();
                            iter.forEachRemaining(( keyValue) -> count.incrementAndGet());
                            Assert.assertEquals(added, ((count.get()) + committed));
                        }
                    }


        }
        blocks = getTransactions(1000);
        commitTransactions(blocks);
    }

    @Test
    public void testPersistence() throws Exception {
        for (Map.Entry<Long, List<Long>> entry : generateData(50).entrySet()) {
            TestDeletedBlockLog.deletedBlockLog.addTransaction(entry.getKey(), entry.getValue());
        }
        // close db and reopen it again to make sure
        // transactions are stored persistently.
        TestDeletedBlockLog.deletedBlockLog.close();
        TestDeletedBlockLog.deletedBlockLog = new DeletedBlockLogImpl(conf, containerManager, scm.getScmMetadataStore());
        List<DeletedBlocksTransaction> blocks = getTransactions(10);
        commitTransactions(blocks);
        blocks = getTransactions(100);
        Assert.assertEquals(40, blocks.size());
        commitTransactions(blocks);
    }

    @Test
    public void testDeletedBlockTransactions() throws IOException {
        int txNum = 10;
        int maximumAllowedTXNum = 5;
        List<DeletedBlocksTransaction> blocks = null;
        List<Long> containerIDs = new LinkedList<>();
        DatanodeDetails dnId1 = dnList.get(0);
        DatanodeDetails dnId2 = dnList.get(1);
        int count = 0;
        long containerID = 0L;
        // Creates {TXNum} TX in the log.
        for (Map.Entry<Long, List<Long>> entry : generateData(txNum).entrySet()) {
            count++;
            containerID = entry.getKey();
            containerIDs.add(containerID);
            TestDeletedBlockLog.deletedBlockLog.addTransaction(containerID, entry.getValue());
            // make TX[1-6] for datanode1; TX[7-10] for datanode2
            if (count <= (maximumAllowedTXNum + 1)) {
                mockContainerInfo(containerID, dnId1);
            } else {
                mockContainerInfo(containerID, dnId2);
            }
        }
        DatanodeDeletedBlockTransactions transactions = new DatanodeDeletedBlockTransactions(containerManager, maximumAllowedTXNum, 2);
        TestDeletedBlockLog.deletedBlockLog.getTransactions(transactions);
        for (UUID id : transactions.getDatanodeIDs()) {
            List<DeletedBlocksTransaction> txs = transactions.getDatanodeTransactions(id);
            // delete TX ID
            commitTransactions(txs);
        }
        blocks = getTransactions(txNum);
        // There should be one block remained since dnID1 reaches
        // the maximum value (5).
        Assert.assertEquals(1, blocks.size());
        Assert.assertFalse(transactions.isFull());
        // The number of TX in dnID1 won't more than maximum value.
        Assert.assertEquals(maximumAllowedTXNum, transactions.getDatanodeTransactions(dnId1.getUuid()).size());
        int size = transactions.getDatanodeTransactions(dnId2.getUuid()).size();
        // add duplicated container in dnID2, this should be failed.
        DeletedBlocksTransaction.Builder builder = DeletedBlocksTransaction.newBuilder();
        builder.setTxID(11);
        builder.setContainerID(containerID);
        builder.setCount(0);
        transactions.addTransaction(builder.build(), null);
        // The number of TX in dnID2 should not be changed.
        Assert.assertEquals(size, transactions.getDatanodeTransactions(dnId2.getUuid()).size());
        // Add new TX in dnID2, then dnID2 will reach maximum value.
        containerID = RandomUtils.nextLong();
        builder = DeletedBlocksTransaction.newBuilder();
        builder.setTxID(12);
        builder.setContainerID(containerID);
        builder.setCount(0);
        mockContainerInfo(containerID, dnId2);
        transactions.addTransaction(builder.build(), null);
        // Since all node are full, then transactions is full.
        Assert.assertTrue(transactions.isFull());
    }
}

