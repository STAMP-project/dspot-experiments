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
package org.apache.drill.exec.physical.impl.join;


import Charsets.UTF_8;
import ValueVector.Accessor;
import java.util.Iterator;
import java.util.List;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.client.DrillClient;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.apache.drill.exec.proto.UserBitShared.QueryType.PHYSICAL;
import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.record.VectorWrapper;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.server.Drillbit;
import org.apache.drill.exec.server.RemoteServiceSet;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.shaded.guava.com.google.common.io.Files;
import org.apache.drill.test.TestTools;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;


@Category({ SlowTest.class, OperatorTest.class })
public class TestHashJoin extends PopUnitTestBase {
    @Rule
    public final TestRule TIMEOUT = TestTools.getTimeoutRule(100000);

    private final DrillConfig c = DrillConfig.create();

    @Test
    public void multiBatchEqualityJoin() throws Throwable {
        testHJMockScanCommon("/join/hash_join_multi_batch.json", 200000);
    }

    @Test
    public void multiBatchRightOuterJoin() throws Throwable {
        testHJMockScanCommon("/join/hj_right_outer_multi_batch.json", 100000);
    }

    @Test
    public void multiBatchLeftOuterJoin() throws Throwable {
        testHJMockScanCommon("/join/hj_left_outer_multi_batch.json", 100000);
    }

    @Test
    public void simpleEqualityJoin() throws Throwable {
        // Function checks hash join with single equality condition
        try (RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();Drillbit bit = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            // run query.
            bit.run();
            client.connect();
            List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/hash_join.json"), UTF_8).read().replace("#{TEST_FILE_1}", DrillFileUtils.getResourceAsFile("/build_side_input.json").toURI().toString()).replace("#{TEST_FILE_2}", DrillFileUtils.getResourceAsFile("/probe_side_input.json").toURI().toString()));
            RecordBatchLoader batchLoader = new RecordBatchLoader(bit.getContext().getAllocator());
            QueryDataBatch batch = results.get(1);
            Assert.assertTrue(batchLoader.load(batch.getHeader().getDef(), batch.getData()));
            Iterator<VectorWrapper<?>> itr = batchLoader.iterator();
            // Just test the join key
            long[] colA = new long[]{ 1, 1, 2, 2, 1, 1 };
            // Check the output of decimal9
            ValueVector.Accessor intAccessor1 = itr.next().getValueVector().getAccessor();
            for (int i = 0; i < (intAccessor1.getValueCount()); i++) {
                Assert.assertEquals(intAccessor1.getObject(i), colA[i]);
            }
            Assert.assertEquals(6, intAccessor1.getValueCount());
            batchLoader.clear();
            for (QueryDataBatch result : results) {
                result.release();
            }
        }
    }

    @Test
    public void hjWithExchange() throws Throwable {
        // Function tests with hash join with exchanges
        try (final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();final Drillbit bit = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);final DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            // run query.
            bit.run();
            client.connect();
            final List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/hj_exchanges.json"), UTF_8).read());
            int count = 0;
            for (final QueryDataBatch b : results) {
                if ((b.getHeader().getRowCount()) != 0) {
                    count += b.getHeader().getRowCount();
                }
                b.release();
            }
            Assert.assertEquals(25, count);
        }
    }

    @Test
    public void multipleConditionJoin() throws Throwable {
        // Function tests hash join with multiple join conditions
        try (final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();final Drillbit bit = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);final DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            // run query.
            bit.run();
            client.connect();
            final List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/hj_multi_condition_join.json"), UTF_8).read().replace("#{TEST_FILE_1}", DrillFileUtils.getResourceAsFile("/build_side_input.json").toURI().toString()).replace("#{TEST_FILE_2}", DrillFileUtils.getResourceAsFile("/probe_side_input.json").toURI().toString()));
            final RecordBatchLoader batchLoader = new RecordBatchLoader(bit.getContext().getAllocator());
            final QueryDataBatch batch = results.get(1);
            Assert.assertTrue(batchLoader.load(batch.getHeader().getDef(), batch.getData()));
            final Iterator<VectorWrapper<?>> itr = batchLoader.iterator();
            // Just test the join key
            final long[] colA = new long[]{ 1, 2, 1 };
            final long[] colC = new long[]{ 100, 200, 500 };
            // Check the output of decimal9
            final ValueVector.Accessor intAccessor1 = itr.next().getValueVector().getAccessor();
            final ValueVector.Accessor intAccessor2 = itr.next().getValueVector().getAccessor();
            for (int i = 0; i < (intAccessor1.getValueCount()); i++) {
                Assert.assertEquals(intAccessor1.getObject(i), colA[i]);
                Assert.assertEquals(intAccessor2.getObject(i), colC[i]);
            }
            Assert.assertEquals(3, intAccessor1.getValueCount());
            batchLoader.clear();
            for (final QueryDataBatch result : results) {
                result.release();
            }
        }
    }

    @Test
    public void hjWithExchange1() throws Throwable {
        // Another test for hash join with exchanges
        try (final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();final Drillbit bit = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);final DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            // run query.
            bit.run();
            client.connect();
            final List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/hj_exchanges1.json"), UTF_8).read());
            int count = 0;
            for (final QueryDataBatch b : results) {
                if ((b.getHeader().getRowCount()) != 0) {
                    count += b.getHeader().getRowCount();
                }
                b.release();
            }
            Assert.assertEquals(272, count);
        }
    }

    @Test
    public void testHashJoinExprInCondition() throws Exception {
        final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();
        try (final Drillbit bit1 = new Drillbit(PopUnitTestBase.CONFIG, serviceSet);final DrillClient client = new DrillClient(PopUnitTestBase.CONFIG, serviceSet.getCoordinator())) {
            bit1.run();
            client.connect();
            final List<QueryDataBatch> results = client.runQuery(PHYSICAL, Files.asCharSource(DrillFileUtils.getResourceAsFile("/join/hashJoinExpr.json"), UTF_8).read());
            int count = 0;
            for (final QueryDataBatch b : results) {
                if ((b.getHeader().getRowCount()) != 0) {
                    count += b.getHeader().getRowCount();
                }
                b.release();
            }
            Assert.assertEquals(10, count);
        }
    }
}

