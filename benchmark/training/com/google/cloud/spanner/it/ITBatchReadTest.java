/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner.it;


import com.google.cloud.spanner.BatchClient;
import com.google.cloud.spanner.BatchReadOnlyTransaction;
import com.google.cloud.spanner.BatchTransactionId;
import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.IntegrationTest;
import com.google.cloud.spanner.IntegrationTestEnv;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Partition;
import com.google.cloud.spanner.PartitionOptions;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TimestampBound;
import com.google.common.hash.HashFunction;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration test reading large amounts of data using the Batch APIs. The size of data ensures
 * that multiple paritions are returned by the server.
 */
@Category(IntegrationTest.class)
@RunWith(JUnit4.class)
public class ITBatchReadTest {
    private static int numRows;

    private static final int WRITE_BATCH_SIZE = 1 << 20;

    private static final String TABLE_NAME = "BatchTestTable";

    private static final String INDEX_NAME = "TestIndexByValue";

    private static final long STALENESS_MILLISEC = 1 * 1000;

    @ClassRule
    public static IntegrationTestEnv env = new IntegrationTestEnv();

    private static Database db;

    private static HashFunction hasher;

    private static DatabaseClient dbClient;

    private static BatchClient client;

    private static final Random RANDOM = new Random();

    private BatchReadOnlyTransaction batchTxn;

    @Test
    public void read() {
        BitSet seenRows = new BitSet(ITBatchReadTest.numRows);
        TimestampBound bound = getRandomBound();
        PartitionOptions partitionParams = getRandomPartitionOptions();
        batchTxn = ITBatchReadTest.client.batchReadOnlyTransaction(bound);
        List<Partition> partitions = batchTxn.partitionRead(partitionParams, ITBatchReadTest.TABLE_NAME, KeySet.all(), Arrays.asList("Key", "Data", "Fingerprint", "Size"));
        BatchTransactionId txnID = batchTxn.getBatchTransactionId();
        fetchAndValidateRows(partitions, txnID, seenRows);
    }

    @Test
    public void readUsingIndex() {
        TimestampBound bound = getRandomBound();
        PartitionOptions partitionParams = getRandomPartitionOptions();
        batchTxn = ITBatchReadTest.client.batchReadOnlyTransaction(bound);
        List<Partition> partitions = batchTxn.partitionReadUsingIndex(partitionParams, ITBatchReadTest.TABLE_NAME, ITBatchReadTest.INDEX_NAME, KeySet.all(), Arrays.asList("Fingerprint"));
        BatchTransactionId txnID = batchTxn.getBatchTransactionId();
        int numRowsRead = 0;
        for (Partition p : partitions) {
            BatchReadOnlyTransaction batchTxnOnEachWorker = ITBatchReadTest.client.batchReadOnlyTransaction(txnID);
            try (ResultSet result = batchTxnOnEachWorker.execute(p)) {
                while (result.next()) {
                    numRowsRead++;
                } 
            }
        }
        assertThat(numRowsRead).isEqualTo(ITBatchReadTest.numRows);
    }

    @Test
    public void query() {
        BitSet seenRows = new BitSet(ITBatchReadTest.numRows);
        TimestampBound bound = getRandomBound();
        PartitionOptions partitionParams = getRandomPartitionOptions();
        batchTxn = ITBatchReadTest.client.batchReadOnlyTransaction(bound);
        List<Partition> partitions = batchTxn.partitionQuery(partitionParams, Statement.of(("SELECT Key, Data, Fingerprint, Size FROM " + (ITBatchReadTest.TABLE_NAME))));
        BatchTransactionId txnID = batchTxn.getBatchTransactionId();
        fetchAndValidateRows(partitions, txnID, seenRows);
    }
}

