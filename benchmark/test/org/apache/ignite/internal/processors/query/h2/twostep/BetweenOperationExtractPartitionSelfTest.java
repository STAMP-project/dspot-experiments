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
package org.apache.ignite.internal.processors.query.h2.twostep;


import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.processors.query.h2.twostep.msg.GridH2QueryRequest;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.plugin.extensions.communication.Message;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test checks partition extraction for between (where x between 10 and 17) and simple range (where x > 10 and x < 17)
 * expressions.
 */
public class BetweenOperationExtractPartitionSelfTest extends GridCommonAbstractTest {
    /**
     * Nodes count.
     */
    private static final int NODES_COUNT = 8;

    /**
     * Organizations count.
     */
    private static final int ORG_COUNT = 10000;

    /**
     * Organizations cache name.
     */
    private static final String ORG_CACHE_NAME = "orgBetweenTest";

    /**
     * Empty partitions array.
     */
    private static final int[] EMPTY_PARTITIONS_ARRAY = new int[]{  };

    /**
     * Between query.
     */
    private static final String BETWEEN_QRY = "select * from Organization org where org._KEY between %d and %d";

    /**
     * Range query.
     */
    private static final String RANGE_QRY = "select * from Organization org where org._KEY %s %d and org._KEY %s %d";

    /**
     * And + between query.
     */
    private static final String AND_BETWEEN_QRY = "select * from Organization org where org._KEY > 10 and org._KEY between %d and %d";

    /**
     * And + range query.
     */
    private static final String AND_RANGE_QRY = "select * from Organization org where org._KEY > 10 and org._KEY %s %d and org._KEY %s %d";

    /**
     * Between + and query.
     */
    private static final String BETWEEN_AND_QRY = "select * from Organization org where org._KEY between %d and %d and org._KEY > 10";

    /**
     * Range + and query.
     */
    private static final String RANGE_AND_QRY = "select * from Organization org where org._KEY %s %d and org._KEY %s %d and org._KEY > 10";

    /**
     * Between + between query.
     */
    private static final String BETWEEN_AND_BETWEEN_QRY = "select * from Organization org where org._KEY between %d and %d and org._KEY between 10 and 20";

    /**
     * Range + Range query.
     */
    private static final String RANGE_AND_RANGE_QRY = "select * from Organization org where org._KEY %s %d and org._KEY %s %d and org._KEY >= 10 and org._KEY <= 20";

    /**
     * Between + and + between query.
     */
    private static final String BETWEEN_AND_AND_AND_BETWEEN_QRY = "select * from Organization org where org._KEY between %d and %d and org._KEY < 30 and org._KEY" + " between 10 and 20";

    /**
     * Range + and + Range query.
     */
    private static final String RANGE_AND_AND_AND_RANGE_QRY = "select * from Organization org where org._KEY %s %d and org._KEY %s %d and org._KEY < 30 and" + " org._KEY >= 10 and org._KEY <= 20";

    /**
     * Between + or query.
     */
    private static final String BETWEEN_OR_QRY = "select * from Organization org where org._KEY between %d and %d or org._KEY < 5";

    /**
     * Range + or query.
     */
    private static final String RANGE_OR_QRY = "select * from Organization org where org._KEY %s %d and org._KEY %s %d or org._KEY < 5";

    /**
     * Between + or query.
     */
    private static final String OR_BETWEEN_QRY = "select * from Organization org where org._KEY < 5 or org._KEY between %d and %d";

    /**
     * Range + or query.
     */
    private static final String OR_RANGE_QRY = "select * from Organization org where org._KEY < 5 or org._KEY %s %d and org._KEY %s %d";

    /**
     * Between or between query.
     */
    private static final String BETWEEN_OR_BETWEEN_QRY = "select * from Organization org where org._KEY between %d and %d or org._KEY between 20 and 25";

    /**
     * Range or range query.
     */
    private static final String RANGE_OR_RANGE_QRY = "select * from Organization org where org._KEY %s %d and org._KEY %s %d or org._KEY >= 20 and org._KEY <= 25";

    /**
     * Range or range query.
     */
    private static final String RANGE_OR_BETWEEN_QRY = "select * from Organization org where org._KEY %s %d and org._KEY %s %d or org._KEY between 20 and 25";

    /**
     * Empty Range.
     */
    private static final String EMPTY_RANGE_QRY = "select * from Organization org where org._KEY %s %d and org._KEY %s %d";

    /**
     * Organizations cache.
     */
    private static IgniteCache<Integer, JoinSqlTestHelper.Organization> orgCache;

    /**
     * Client mode.
     */
    private boolean clientMode;

    /**
     * Check between expression with constant values.
     */
    @Test
    public void testBetweenConst() {
        // select * from Organization org where org._KEY between %d and %d.
        // 
        // between
        // 
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_QRY, 1, 3, 3);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_QRY, 5, 5, 1);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_QRY, 7, 8, 2);
        // select * from Organization org where org._KEY > 10 and org._KEY between %d and %d
        // 
        // and
        // /   \
        // /     \
        // >    between
        // 
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.AND_BETWEEN_QRY, 11, 13, 3);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.AND_BETWEEN_QRY, 15, 15, 1);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.AND_BETWEEN_QRY, 17, 18, 2);
        // select * from Organization org where org._KEY between %d and %d and org._KEY > 10
        // 
        // and
        // /   \
        // /     \
        // between    >
        // 
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_AND_QRY, 11, 13, 3);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_AND_QRY, 15, 15, 1);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_AND_QRY, 17, 18, 2);
        // select * from Organization org where org._KEY between %d and %d and org._KEY between 10 and 20
        // 
        // and
        // /   \
        // /     \
        // between between
        // 
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_AND_BETWEEN_QRY, 11, 13, 3);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_AND_BETWEEN_QRY, 15, 15, 1);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_AND_BETWEEN_QRY, 17, 18, 2);
        // select * from Organization org where org._KEY between %d and %d and org._KEY < 30
        // and org._KEY between 10 and 20
        // 
        // and
        // /   \
        // /     \
        // between   and
        // /   \
        // /     \
        // <     between
        // 
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_AND_AND_AND_BETWEEN_QRY, 11, 13, 3);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_AND_AND_AND_BETWEEN_QRY, 15, 15, 1);
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_AND_AND_AND_BETWEEN_QRY, 17, 18, 2);
        // select * from Organization org where org._KEY between %d and %d or org._KEY < 5
        // 
        // or
        // /   \
        // /     \
        // between    <
        // 
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_OR_QRY, 11, 13, 8, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        // select * from Organization org where org._KEY < 5 or org._KEY between %d and %d
        // 
        // or
        // /   \
        // /     \
        // <     between
        // 
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.OR_BETWEEN_QRY, 11, 13, 8, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        // select * from Organization org where org._KEY between %d and %d or between 20 and 25
        // 
        // or
        // /   \
        // /     \
        // between   between
        // 
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_OR_BETWEEN_QRY, 11, 13, 9, 11, 12, 13, 20, 21, 22, 23, 24, 25);
    }

    /**
     * Check range expression with constant values.
     */
    @Test
    public void testRangeConst() {
        // select * from Organization org where org._KEY %s %d and org._KEY %s %d
        // 
        // >(=) <(=)
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_QRY, 1, 3, 3, false);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_QRY, 5, 5, 1, false);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_QRY, 7, 8, 2, false);
        // At the moment between-based-partition-pruning doesn't support range expression
        // with any extra expressions because of optimisations that change expressions order:
        // org where org._KEY > 10 and org._KEY > 11 and org._KEY < 13 converts to
        // ((ORG__Z0._KEY < 13) AND ((ORG__Z0._KEY > 10) AND (ORG__Z0._KEY > 11)))
        // So bellow we only check expected result rows count and not expected partitions matching.
        // select * from Organization org where org._KEY > 10 and org._KEY %s %d and org._KEY %s %d
        // 
        // and
        // /   \
        // /     \
        // >      and
        // /   \
        // /     \
        // >(=)  <(=)
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.AND_RANGE_QRY, 11, 13, 3, true);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.AND_RANGE_QRY, 15, 15, 1, true);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.AND_RANGE_QRY, 17, 18, 2, true);
        // select * from Organization org where org._KEY %s %d and org._KEY %s %d and org._KEY > 10
        // 
        // and
        // /   \
        // /     \
        // and      >
        // /  \
        // /    \
        // >(=)   <(=)
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_AND_QRY, 11, 13, 3, true);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_AND_QRY, 15, 15, 1, true);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_AND_QRY, 17, 18, 2, true);
        // select * from Organization org where org._KEY %s %d and org._KEY %s %d and org._KEY >= 10 and org._KEY <= 20
        // 
        // and
        // /   \
        // /     \
        // /       \
        // and       and
        // /   \      /  \
        // /     \    /    \
        // >(=)  <(=) >(=)  <(=)
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_AND_RANGE_QRY, 11, 13, 3, true);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_AND_RANGE_QRY, 15, 15, 1, true);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_AND_RANGE_QRY, 17, 18, 2, true);
        // select * from Organization org where org._KEY %s %d and org._KEY %s %d and org._KEY < 30 and
        // org._KEY >= 10 and org._KEY <= 20
        // 
        // and
        // /   \
        // /     \
        // /       \
        // and       and
        // /   \      /  \
        // /     \    /    \
        // >(=)  <(=) >(=)  and
        // /   \
        // /     \
        // >(=)   <(=)
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_AND_AND_AND_RANGE_QRY, 11, 13, 3, true);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_AND_AND_AND_RANGE_QRY, 15, 15, 1, true);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_AND_AND_AND_RANGE_QRY, 17, 18, 2, true);
        // select * from Organization org where org._KEY %s %d and org._KEY %s %d or org._KEY < 5
        // 
        // or
        // /   \
        // /     \
        // and      <
        // /  \
        // /    \
        // >(=)   <(=)
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_QRY, 11, 13, ">", "<", 6, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_QRY, 11, 13, ">=", "<", 7, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_QRY, 11, 13, ">", "<=", 7, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_QRY, 11, 13, ">=", "<=", 8, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        // select * from Organization org where org._KEY < 5 or org._KEY %s %d and org._KEY %s %d
        // 
        // and
        // /   \
        // /     \
        // <      and
        // /   \
        // /     \
        // >(=)  <(=)
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.OR_RANGE_QRY, 11, 13, ">", "<", 6, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.OR_RANGE_QRY, 11, 13, ">=", "<", 7, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.OR_RANGE_QRY, 11, 13, ">", "<=", 7, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.OR_RANGE_QRY, 11, 13, ">=", "<=", 8, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
        // select * from Organization org where org._KEY %s %d and org._KEY %s %d or org._KEY >= 20 and org._KEY <= 25
        // 
        // or
        // /   \
        // /     \
        // /       \
        // and       and
        // /   \      /  \
        // /     \    /    \
        // >(=)  <(=) >(=)  <(=)
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_RANGE_QRY, 11, 13, ">", "<", 7, 12, 20, 21, 22, 23, 24, 25);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_RANGE_QRY, 11, 13, ">=", "<", 8, 11, 12, 20, 21, 22, 23, 24, 25);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_RANGE_QRY, 11, 13, ">", "<=", 8, 12, 13, 20, 21, 22, 23, 24, 25);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_RANGE_QRY, 11, 13, ">=", "<=", 9, 11, 12, 13, 20, 21, 22, 23, 24, 25);
        // select * from Organization org where org._KEY %s %d and org._KEY %s %d or org._KEY between 20 and 25
        // 
        // or
        // /   \
        // /     \
        // /       \
        // and       and
        // /   \      /  \
        // /     \    /    \
        // >(=)  <(=) >(=)  <(=)
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_BETWEEN_QRY, 11, 13, ">", "<", 7, 12, 20, 21, 22, 23, 24, 25);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_BETWEEN_QRY, 11, 13, ">=", "<", 8, 11, 12, 20, 21, 22, 23, 24, 25);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_BETWEEN_QRY, 11, 13, ">", "<=", 8, 12, 13, 20, 21, 22, 23, 24, 25);
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.RANGE_OR_BETWEEN_QRY, 11, 13, ">=", "<=", 9, 11, 12, 13, 20, 21, 22, 23, 24, 25);
        // select * from Organization org where org._KEY < %d and org._KEY > %d
        // 
        // Empty range < >
        // 
        testRangeConstOperator(BetweenOperationExtractPartitionSelfTest.EMPTY_RANGE_QRY, 11, 13, "<", ">", 0, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
    }

    /**
     * Check between expression against non-affinity column.
     */
    @Test
    public void testBetweenConstAgainstNonAffinityColumn() {
        testBetweenConstOperator("select * from Organization org where org.debtCapital between %d and %d", 1, 3, 3, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
    }

    /**
     * Check range expression against different columns.
     */
    @Test
    public void testBetweenConstAgainstDifferentColumns() {
        testRangeConstOperator("select * from Organization org where org._key %s %d and org.debtCapital %s %d", 1, 3, ">=", "<=", 3, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
    }

    /**
     * Check default partitions limit exceeding.
     */
    @Test
    public void testBetweenPartitionsDefaultLimitExceeding() {
        // Default limit (16) not exceeded.
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_QRY, 1, 16, 16);
        // Default limit (16) exceeded.
        testBetweenConstOperator(BetweenOperationExtractPartitionSelfTest.BETWEEN_QRY, 1, 17, 17, BetweenOperationExtractPartitionSelfTest.EMPTY_PARTITIONS_ARRAY);
    }

    /**
     * Check range expression with constant values.
     */
    @Test
    public void testRevertedRangeConst() {
        // select * from Organization org where org._KEY %s %d and org._KEY %s %d
        // 
        // <(=) >(=)
        // 
        testRevertedRangeConstOperator(3, 1, 3);
        testRevertedRangeConstOperator(5, 5, 1);
        testRevertedRangeConstOperator(8, 7, 2);
    }

    /**
     * Test communication SPI.
     */
    private static class TestCommunicationSpi extends TcpCommunicationSpi {
        /**
         * Used partitions.
         */
        Set<Integer> partitions = ConcurrentHashMap.newKeySet();

        /**
         * {@inheritDoc }
         */
        @Override
        public void sendMessage(ClusterNode node, Message msg, IgniteInClosure<IgniteException> ackC) throws IgniteSpiException {
            if ((message()) instanceof GridH2QueryRequest) {
                GridH2QueryRequest gridH2QryReq = ((GridH2QueryRequest) (message()));
                if ((gridH2QryReq.queryPartitions()) != null) {
                    for (int partition : gridH2QryReq.queryPartitions())
                        partitions.add(partition);

                }
            }
            super.sendMessage(node, msg, ackC);
        }

        /**
         *
         *
         * @return Used partitons set.
         */
        Set<Integer> partitionsSet() {
            return partitions;
        }

        /**
         * Clear partitions set.
         */
        void resetPartitions() {
            partitions.clear();
        }
    }
}

