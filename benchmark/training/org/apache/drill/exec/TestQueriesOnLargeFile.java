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
package org.apache.drill.exec;


import Charsets.UTF_8;
import java.io.File;
import java.util.List;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.vector.BigIntVector;
import org.apache.drill.shaded.guava.com.google.common.io.Files;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SlowTest.class })
public class TestQueriesOnLargeFile extends BaseTestQuery {
    private static File dataFile = null;

    private static int NUM_RECORDS = 15000;

    @Test
    public void testRead() throws Exception {
        List<QueryDataBatch> results = BaseTestQuery.testSqlWithResults(String.format("SELECT count(*) FROM dfs.`default`.`%s`", TestQueriesOnLargeFile.dataFile.getName()));
        RecordBatchLoader batchLoader = new RecordBatchLoader(BaseTestQuery.getAllocator());
        for (QueryDataBatch batch : results) {
            batchLoader.load(batch.getHeader().getDef(), batch.getData());
            if ((batchLoader.getRecordCount()) <= 0) {
                continue;
            }
            BigIntVector countV = ((BigIntVector) (batchLoader.getValueAccessorById(BigIntVector.class, 0).getValueVector()));
            Assert.assertEquals((("Total of " + (TestQueriesOnLargeFile.NUM_RECORDS)) + " records expected in count"), countV.getAccessor().get(0), TestQueriesOnLargeFile.NUM_RECORDS);
            batchLoader.clear();
            batch.release();
        }
    }

    @Test
    public void testMergingReceiver() throws Exception {
        String plan = Files.asCharSource(DrillFileUtils.getResourceAsFile("/largefiles/merging_receiver_large_data.json"), UTF_8).read().replace("#{TEST_FILE}", escapeJsonString(TestQueriesOnLargeFile.dataFile.getPath()));
        List<QueryDataBatch> results = BaseTestQuery.testPhysicalWithResults(plan);
        int recordsInOutput = 0;
        for (QueryDataBatch batch : results) {
            recordsInOutput += batch.getHeader().getDef().getRecordCount();
            batch.release();
        }
        Assert.assertEquals(String.format("Number of records in output is wrong: expected=%d, actual=%s", TestQueriesOnLargeFile.NUM_RECORDS, recordsInOutput), TestQueriesOnLargeFile.NUM_RECORDS, recordsInOutput);
    }
}

