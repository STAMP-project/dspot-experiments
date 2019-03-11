/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql.txn.compactor;


import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hive.metastore.api.CompactionRequest;
import org.apache.hadoop.hive.metastore.api.CompactionType;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.txn.CompactionInfo;
import org.junit.Test;


public class TestCleanerWithReplication extends CompactorTest {
    private Path cmRootDirectory;

    private static FileSystem fs;

    private static MiniDFSCluster miniDFSCluster;

    private final String dbName = "TestCleanerWithReplication";

    @Test
    public void cleanupAfterMajorTableCompaction() throws Exception {
        Table t = newTable(dbName, "camtc", false);
        addBaseFile(t, null, 20L, 20);
        addDeltaFile(t, null, 21L, 22L, 2);
        addDeltaFile(t, null, 23L, 24L, 2);
        addBaseFile(t, null, 25L, 25);
        burnThroughTransactions(dbName, "camtc", 25);
        CompactionRequest rqst = new CompactionRequest(dbName, "camtc", CompactionType.MAJOR);
        txnHandler.compact(rqst);
        CompactionInfo ci = txnHandler.findNextToCompact("fred");
        ci.runAs = System.getProperty("user.name");
        txnHandler.updateCompactorState(ci, openTxn());
        txnHandler.markCompacted(ci);
        assertCleanerActions(6);
    }

    @Test
    public void cleanupAfterMajorPartitionCompaction() throws Exception {
        Table t = newTable(dbName, "campc", true);
        Partition p = newPartition(t, "today");
        addBaseFile(t, p, 20L, 20);
        addDeltaFile(t, p, 21L, 22L, 2);
        addDeltaFile(t, p, 23L, 24L, 2);
        addBaseFile(t, p, 25L, 25);
        burnThroughTransactions(dbName, "campc", 25);
        CompactionRequest rqst = new CompactionRequest(dbName, "campc", CompactionType.MAJOR);
        rqst.setPartitionname("ds=today");
        txnHandler.compact(rqst);
        CompactionInfo ci = txnHandler.findNextToCompact("fred");
        ci.runAs = System.getProperty("user.name");
        txnHandler.updateCompactorState(ci, openTxn());
        txnHandler.markCompacted(ci);
        assertCleanerActions(6);
    }

    @Test
    public void cleanupAfterMinorTableCompaction() throws Exception {
        Table t = newTable(dbName, "camitc", false);
        addBaseFile(t, null, 20L, 20);
        addDeltaFile(t, null, 21L, 22L, 2);
        addDeltaFile(t, null, 23L, 24L, 2);
        addDeltaFile(t, null, 21L, 24L, 4);
        burnThroughTransactions(dbName, "camitc", 25);
        CompactionRequest rqst = new CompactionRequest(dbName, "camitc", CompactionType.MINOR);
        txnHandler.compact(rqst);
        CompactionInfo ci = txnHandler.findNextToCompact("fred");
        ci.runAs = System.getProperty("user.name");
        txnHandler.updateCompactorState(ci, openTxn());
        txnHandler.markCompacted(ci);
        assertCleanerActions(4);
    }

    @Test
    public void cleanupAfterMinorPartitionCompaction() throws Exception {
        Table t = newTable(dbName, "camipc", true);
        Partition p = newPartition(t, "today");
        addBaseFile(t, p, 20L, 20);
        addDeltaFile(t, p, 21L, 22L, 2);
        addDeltaFile(t, p, 23L, 24L, 2);
        addDeltaFile(t, p, 21L, 24L, 4);
        burnThroughTransactions(dbName, "camipc", 25);
        CompactionRequest rqst = new CompactionRequest(dbName, "camipc", CompactionType.MINOR);
        rqst.setPartitionname("ds=today");
        txnHandler.compact(rqst);
        CompactionInfo ci = txnHandler.findNextToCompact("fred");
        ci.runAs = System.getProperty("user.name");
        txnHandler.updateCompactorState(ci, openTxn());
        txnHandler.markCompacted(ci);
        assertCleanerActions(4);
    }
}

