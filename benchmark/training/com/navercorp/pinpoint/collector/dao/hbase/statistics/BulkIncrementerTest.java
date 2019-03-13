/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.collector.dao.hbase.statistics;


import com.google.common.collect.Lists;
import com.navercorp.pinpoint.common.util.BytesUtils;
import com.sematext.hbase.wd.RowKeyDistributorByHashPrefix;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;


/**
 *
 *
 * @author HyunGil Jeong
 */
@RunWith(MockitoJUnitRunner.class)
public class BulkIncrementerTest {
    private static final byte[] CF = Bytes.toBytes("CF");

    private final BulkIncrementer bulkIncrementer = new BulkIncrementer(new RowKeyMerge(BulkIncrementerTest.CF));

    @Mock
    private RowKeyDistributorByHashPrefix rowKeyDistributor;

    @Test
    public void singleTable() {
        // Given
        TableName tableA = TableName.valueOf("A");
        BulkIncrementerTest.TestDataSet testDataSetA_0_0 = new BulkIncrementerTest.TestDataSet(tableA, 0, 0, 100);
        BulkIncrementerTest.TestDataSet testDataSetA_0_1 = new BulkIncrementerTest.TestDataSet(tableA, 0, 1, 200);
        List<BulkIncrementerTest.TestData> testDatas = new ArrayList<>();
        testDatas.addAll(testDataSetA_0_0.getTestDatas());
        testDatas.addAll(testDataSetA_0_1.getTestDatas());
        Collections.shuffle(testDatas);
        // When
        for (BulkIncrementerTest.TestData testData : testDatas) {
            bulkIncrementer.increment(testData.getTableName(), testData.getRowKey(), testData.getColumnName());
        }
        // Then
        Map<TableName, List<Increment>> incrementMap = bulkIncrementer.getIncrements(rowKeyDistributor);
        BulkIncrementerTest.TestVerifier verifier = new BulkIncrementerTest.TestVerifier(incrementMap);
        verifier.verify(testDataSetA_0_0);
        verifier.verify(testDataSetA_0_1);
    }

    @Test
    public void multipleTables() {
        // Given
        TableName tableA = TableName.valueOf("a", "A");
        TableName tableB = TableName.valueOf("b", "A");
        BulkIncrementerTest.TestDataSet testDataSetA_0_0 = new BulkIncrementerTest.TestDataSet(tableA, 0, 0, 100);
        BulkIncrementerTest.TestDataSet testDataSetA_0_1 = new BulkIncrementerTest.TestDataSet(tableA, 0, 1, 200);
        BulkIncrementerTest.TestDataSet testDataSetA_1_0 = new BulkIncrementerTest.TestDataSet(tableA, 1, 0, 300);
        BulkIncrementerTest.TestDataSet testDataSetA_1_1 = new BulkIncrementerTest.TestDataSet(tableA, 1, 1, 400);
        BulkIncrementerTest.TestDataSet testDataSetB_0_0 = new BulkIncrementerTest.TestDataSet(tableB, 0, 0, 500);
        BulkIncrementerTest.TestDataSet testDataSetB_0_1 = new BulkIncrementerTest.TestDataSet(tableB, 0, 1, 600);
        BulkIncrementerTest.TestDataSet testDataSetB_1_0 = new BulkIncrementerTest.TestDataSet(tableB, 1, 0, 700);
        BulkIncrementerTest.TestDataSet testDataSetB_1_1 = new BulkIncrementerTest.TestDataSet(tableB, 1, 1, 800);
        List<BulkIncrementerTest.TestData> testDatas = new ArrayList<>();
        testDatas.addAll(testDataSetA_0_0.getTestDatas());
        testDatas.addAll(testDataSetA_0_1.getTestDatas());
        testDatas.addAll(testDataSetA_1_0.getTestDatas());
        testDatas.addAll(testDataSetA_1_1.getTestDatas());
        testDatas.addAll(testDataSetB_0_0.getTestDatas());
        testDatas.addAll(testDataSetB_0_1.getTestDatas());
        testDatas.addAll(testDataSetB_1_0.getTestDatas());
        testDatas.addAll(testDataSetB_1_1.getTestDatas());
        Collections.shuffle(testDatas);
        // When
        for (BulkIncrementerTest.TestData testData : testDatas) {
            bulkIncrementer.increment(testData.getTableName(), testData.getRowKey(), testData.getColumnName());
        }
        // Then
        Map<TableName, List<Increment>> incrementMap = bulkIncrementer.getIncrements(rowKeyDistributor);
        BulkIncrementerTest.TestVerifier verifier = new BulkIncrementerTest.TestVerifier(incrementMap);
        verifier.verify(testDataSetA_0_0);
        verifier.verify(testDataSetA_0_1);
        verifier.verify(testDataSetA_1_0);
        verifier.verify(testDataSetA_1_1);
        verifier.verify(testDataSetB_0_0);
        verifier.verify(testDataSetB_0_1);
        verifier.verify(testDataSetB_1_0);
        verifier.verify(testDataSetB_1_1);
    }

    @Test
    public void singleTableConcurrent() throws Exception {
        // Given
        TableName tableA = TableName.valueOf("A");
        BulkIncrementerTest.TestDataSet testDataSetA_0_0 = new BulkIncrementerTest.TestDataSet(tableA, 0, 0, 1000000);
        BulkIncrementerTest.TestDataSet testDataSetA_0_1 = new BulkIncrementerTest.TestDataSet(tableA, 0, 1, 1000001);
        List<BulkIncrementerTest.TestData> testDatas = new ArrayList<>();
        testDatas.addAll(testDataSetA_0_0.getTestDatas());
        testDatas.addAll(testDataSetA_0_1.getTestDatas());
        Collections.shuffle(testDatas);
        // When
        final int numIncrementers = 16;
        List<List<BulkIncrementerTest.TestData>> testDataPartitions = Lists.partition(testDatas, ((testDatas.size()) / (numIncrementers - 1)));
        final CountDownLatch completeLatch = new CountDownLatch(testDataPartitions.size());
        final CountDownLatch flusherLatch = new CountDownLatch(1);
        FutureTask<Map<TableName, List<Increment>>> flushTask = new FutureTask(new BulkIncrementerTest.Flusher(completeLatch, flusherLatch));
        new Thread(flushTask, "Flusher").start();
        int counter = 0;
        for (List<BulkIncrementerTest.TestData> testDataPartition : testDataPartitions) {
            BulkIncrementerTest.Incrementer incrementer = new BulkIncrementerTest.Incrementer(completeLatch, testDataPartition);
            new Thread(incrementer, ("Incrementer-" + (counter++))).start();
        }
        flusherLatch.await(30L, TimeUnit.SECONDS);
        // Then
        Map<TableName, List<Increment>> incrementMap = flushTask.get(5L, TimeUnit.SECONDS);
        BulkIncrementerTest.TestVerifier verifier = new BulkIncrementerTest.TestVerifier(incrementMap);
        verifier.verify(testDataSetA_0_0);
        verifier.verify(testDataSetA_0_1);
    }

    @Test
    public void multipleTablesConcurrent() throws Exception {
        // Given
        final int numTables = 50;
        List<TableName> tableNames = new ArrayList<>(numTables);
        for (int i = 0; i < numTables; i++) {
            tableNames.add(TableName.valueOf((i + "")));
        }
        final int numRowIds = 100;
        final int numColumnIds = 20;
        final int numTestDataSets = (numTables * numRowIds) * numColumnIds;
        final int maxCallCount = 200;
        final Random random = new Random();
        List<BulkIncrementerTest.TestDataSet> testDataSets = new ArrayList<>(numTestDataSets);
        for (TableName tableName : tableNames) {
            for (int i = 0; i < numRowIds; i++) {
                for (int j = 0; j < numColumnIds; j++) {
                    int callCount = (random.nextInt((maxCallCount - 100))) + 100;
                    BulkIncrementerTest.TestDataSet testDataSet = new BulkIncrementerTest.TestDataSet(tableName, i, j, callCount);
                    testDataSets.add(testDataSet);
                }
            }
        }
        final int maxNumTestDatas = numTestDataSets * maxCallCount;
        List<BulkIncrementerTest.TestData> testDatas = new ArrayList<>(maxNumTestDatas);
        for (BulkIncrementerTest.TestDataSet testDataSet : testDataSets) {
            testDatas.addAll(testDataSet.getTestDatas());
        }
        Collections.shuffle(testDatas);
        // When
        final int numIncrementers = 16;
        List<List<BulkIncrementerTest.TestData>> testDataPartitions = Lists.partition(testDatas, ((testDatas.size()) / (numIncrementers - 1)));
        final CountDownLatch incrementorLatch = new CountDownLatch(testDataPartitions.size());
        final CountDownLatch flusherLatch = new CountDownLatch(1);
        FutureTask<Map<TableName, List<Increment>>> flushTask = new FutureTask(new BulkIncrementerTest.Flusher(incrementorLatch, flusherLatch));
        new Thread(flushTask, "Flusher").start();
        int counter = 0;
        for (List<BulkIncrementerTest.TestData> testDataPartition : testDataPartitions) {
            BulkIncrementerTest.Incrementer incrementer = new BulkIncrementerTest.Incrementer(incrementorLatch, testDataPartition);
            new Thread(incrementer, ("Incrementer-" + (counter++))).start();
        }
        flusherLatch.await(30L, TimeUnit.SECONDS);
        // Then
        Map<TableName, List<Increment>> incrementMap = flushTask.get(5L, TimeUnit.SECONDS);
        BulkIncrementerTest.TestVerifier verifier = new BulkIncrementerTest.TestVerifier(incrementMap);
        for (BulkIncrementerTest.TestDataSet testDataSet : testDataSets) {
            verifier.verify(testDataSet);
        }
    }

    private class Incrementer implements Runnable {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final CountDownLatch completeLatch;

        private final List<BulkIncrementerTest.TestData> testDatas;

        private Incrementer(CountDownLatch completeLatch, List<BulkIncrementerTest.TestData> testDatas) {
            this.completeLatch = completeLatch;
            this.testDatas = testDatas;
        }

        @Override
        public void run() {
            for (BulkIncrementerTest.TestData testData : testDatas) {
                bulkIncrementer.increment(testData.getTableName(), testData.getRowKey(), testData.getColumnName());
            }
            completeLatch.countDown();
        }
    }

    private class Flusher implements Callable<Map<TableName, List<Increment>>> {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final CountDownLatch awaitLatch;

        private final CountDownLatch completeLatch;

        private Flusher(CountDownLatch awaitLatch, CountDownLatch completeLatch) {
            this.awaitLatch = awaitLatch;
            this.completeLatch = completeLatch;
        }

        private void flushToMap(Map<TableName, List<Increment>> resultMap) {
            Map<TableName, List<Increment>> incrementMap = bulkIncrementer.getIncrements(rowKeyDistributor);
            for (Map.Entry<TableName, List<Increment>> incrementMapEntry : incrementMap.entrySet()) {
                TableName tableName = incrementMapEntry.getKey();
                List<Increment> increments = resultMap.computeIfAbsent(tableName, ( k) -> new ArrayList<>());
                increments.addAll(incrementMapEntry.getValue());
            }
        }

        @Override
        public Map<TableName, List<Increment>> call() {
            Map<TableName, List<Increment>> resultMap = new HashMap<>();
            try {
                do {
                    flushToMap(resultMap);
                } while (!(awaitLatch.await(10L, TimeUnit.MILLISECONDS)) );
                flushToMap(resultMap);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Collections.emptyMap();
            } finally {
                completeLatch.countDown();
            }
            return resultMap;
        }
    }

    private static class TestVerifier {
        // Map<table, Map<row, Map<column, count>>>
        private final Map<TableName, Map<ByteBuffer, Map<ByteBuffer, Long>>> resultMap;

        public TestVerifier(Map<TableName, List<Increment>> incrementMap) {
            this.resultMap = new HashMap();
            for (Map.Entry<TableName, List<Increment>> incrementMapEntry : incrementMap.entrySet()) {
                TableName tableName = incrementMapEntry.getKey();
                List<Increment> increments = incrementMapEntry.getValue();
                resultMap.put(tableName, convertIncrements(increments));
            }
        }

        private Map<ByteBuffer, Map<ByteBuffer, Long>> convertIncrements(List<Increment> increments) {
            if (CollectionUtils.isEmpty(increments)) {
                return Collections.emptyMap();
            }
            Map<ByteBuffer, Map<ByteBuffer, Long>> convertedMap = new HashMap<>();
            for (Increment increment : increments) {
                ByteBuffer rowKey = ByteBuffer.wrap(increment.getRow());
                Map<ByteBuffer, Long> convertedKeyValueMap = convertedMap.computeIfAbsent(rowKey, ( key) -> new HashMap<>());
                NavigableMap<byte[], Long> keyValues = increment.getFamilyMapOfLongs().get(BulkIncrementerTest.CF);
                for (Map.Entry<byte[], Long> keyValue : keyValues.entrySet()) {
                    ByteBuffer key = ByteBuffer.wrap(keyValue.getKey());
                    Long value = keyValue.getValue();
                    if (value != null) {
                        convertedKeyValueMap.merge(key, value, ( val, prev) -> val + prev);
                    }
                }
            }
            return convertedMap;
        }

        public void verify(BulkIncrementerTest.TestDataSet testDataSet) {
            TableName expectedTableName = testDataSet.getTableName();
            RowKey expectedRowKey = testDataSet.getRowKey();
            ColumnName expectedColumnName = testDataSet.getColumnName();
            long expectedCount = testDataSet.getCount();
            Map<ByteBuffer, Map<ByteBuffer, Long>> rows = resultMap.get(expectedTableName);
            if (rows == null) {
                Assert.fail(("Expected rows not found for " + testDataSet));
            }
            Map<ByteBuffer, Long> keyValues = rows.get(ByteBuffer.wrap(expectedRowKey.getRowKey()));
            if (keyValues == null) {
                Assert.fail(("Expected row not found for " + testDataSet));
            }
            Long actualCount = keyValues.get(ByteBuffer.wrap(expectedColumnName.getColumnName()));
            if (actualCount == null) {
                Assert.fail(("Expected column not found for " + testDataSet));
            }
            Assert.assertEquals(("Expected counts do not match for " + testDataSet), expectedCount, ((long) (actualCount)));
        }
    }

    private static class TestDataSet {
        private final TableName tableName;

        private final BulkIncrementerTest.TestRowKey rowKey;

        private final BulkIncrementerTest.TestColumnName columnName;

        private final int count;

        private List<BulkIncrementerTest.TestData> testDatas;

        private TestDataSet(TableName tableName, int rowId, int columnId, int count) {
            this.tableName = tableName;
            this.rowKey = new BulkIncrementerTest.TestRowKey(rowId);
            this.columnName = new BulkIncrementerTest.TestColumnName(columnId);
            this.count = count;
        }

        public TableName getTableName() {
            return tableName;
        }

        public RowKey getRowKey() {
            return rowKey;
        }

        public ColumnName getColumnName() {
            return columnName;
        }

        public int getCount() {
            return count;
        }

        public List<BulkIncrementerTest.TestData> getTestDatas() {
            if ((testDatas) == null) {
                if ((count) < 1) {
                    testDatas = Collections.emptyList();
                } else {
                    testDatas = new ArrayList<>(count);
                    for (int i = 0; i < (count); i++) {
                        BulkIncrementerTest.TestData testData = new BulkIncrementerTest.TestData(this.tableName, this.rowKey, this.columnName);
                        testDatas.add(testData);
                    }
                }
            }
            return testDatas;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestDataSet{");
            sb.append("tableName=").append(tableName);
            sb.append(", row=").append(rowKey.getId());
            sb.append(", column=").append(columnName.getId());
            sb.append(", count=").append(count);
            sb.append('}');
            return sb.toString();
        }
    }

    private static class TestData {
        private final TableName tableName;

        private final RowKey rowKey;

        private final ColumnName columnName;

        private TestData(TableName tableName, RowKey rowKey, ColumnName columnName) {
            this.tableName = tableName;
            this.rowKey = rowKey;
            this.columnName = columnName;
        }

        public TableName getTableName() {
            return tableName;
        }

        public RowKey getRowKey() {
            return rowKey;
        }

        public ColumnName getColumnName() {
            return columnName;
        }
    }

    private static class TestRowKey implements RowKey {
        private final int id;

        private TestRowKey(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public byte[] getRowKey() {
            return BytesUtils.intToVar32(id);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            BulkIncrementerTest.TestRowKey that = ((BulkIncrementerTest.TestRowKey) (o));
            return (id) == (that.id);
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    private static class TestColumnName implements ColumnName {
        private final int id;

        private long count;

        private TestColumnName(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public byte[] getColumnName() {
            return BytesUtils.intToVar32(id);
        }

        @Override
        public long getCallCount() {
            return count;
        }

        @Override
        public void setCallCount(long callCount) {
            this.count = callCount;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            BulkIncrementerTest.TestColumnName that = ((BulkIncrementerTest.TestColumnName) (o));
            return (id) == (that.id);
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestColumnName{");
            sb.append("id=").append(id);
            sb.append(", count=").append(count);
            sb.append('}');
            return sb.toString();
        }
    }
}

