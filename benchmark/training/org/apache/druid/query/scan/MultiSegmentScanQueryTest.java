/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.scan;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.java.util.common.guava.Sequences;
import org.apache.druid.query.DefaultGenericQueryMetricsFactory;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerFactory;
import org.apache.druid.segment.Segment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class MultiSegmentScanQueryTest {
    private static final ScanQueryQueryToolChest toolChest = new ScanQueryQueryToolChest(new ScanQueryConfig(), DefaultGenericQueryMetricsFactory.instance());

    private static final QueryRunnerFactory<ScanResultValue, ScanQuery> factory = new ScanQueryRunnerFactory(MultiSegmentScanQueryTest.toolChest, new ScanQueryEngine());

    // time modified version of druid.sample.numeric.tsv
    public static final String[] V_0112 = new String[]{ "2011-01-12T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t10000.0\t100000\tpreferred\ta\u0001preferred\t100.000000", "2011-01-12T01:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t11000.0\t110000\tpreferred\tb\u0001preferred\t100.000000", "2011-01-12T02:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t12000.0\t120000\tpreferred\te\u0001preferred\t100.000000", "2011-01-12T03:00:00.000Z\tspot\thealth\t1300\t13000.0\t13000.0\t130000\tpreferred\th\u0001preferred\t100.000000", "2011-01-12T04:00:00.000Z\tspot\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t100.000000", "2011-01-12T05:00:00.000Z\tspot\tnews\t1500\t15000.0\t15000.0\t150000\tpreferred\tn\u0001preferred\t100.000000", "2011-01-12T06:00:00.000Z\tspot\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t100.000000", "2011-01-12T07:00:00.000Z\tspot\ttechnology\t1700\t17000.0\t17000.0\t170000\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T08:00:00.000Z\tspot\ttravel\t1800\t18000.0\t18000.0\t180000\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T09:00:00.000Z\ttotal_market\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t1000.000000", "2011-01-12T10:00:00.000Z\ttotal_market\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t1000.000000", "2011-01-12T11:00:00.000Z\tupfront\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t800.000000\tvalue", "2011-01-12T12:00:00.000Z\tupfront\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t800.000000\tvalue" };

    public static final String[] V_0113 = new String[]{ "2011-01-13T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t10000.0\t100000\tpreferred\ta\u0001preferred\t94.874713", "2011-01-13T01:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t11000.0\t110000\tpreferred\tb\u0001preferred\t103.629399", "2011-01-13T02:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t12000.0\t120000\tpreferred\te\u0001preferred\t110.087299", "2011-01-13T03:00:00.000Z\tspot\thealth\t1300\t13000.0\t13000.0\t130000\tpreferred\th\u0001preferred\t114.947403", "2011-01-13T04:00:00.000Z\tspot\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t104.465767", "2011-01-13T05:00:00.000Z\tspot\tnews\t1500\t15000.0\t15000.0\t150000\tpreferred\tn\u0001preferred\t102.851683", "2011-01-13T06:00:00.000Z\tspot\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t108.863011", "2011-01-13T07:00:00.000Z\tspot\ttechnology\t1700\t17000.0\t17000.0\t170000\tpreferred\tt\u0001preferred\t111.356672", "2011-01-13T08:00:00.000Z\tspot\ttravel\t1800\t18000.0\t18000.0\t180000\tpreferred\tt\u0001preferred\t106.236928", "2011-01-13T09:00:00.000Z\ttotal_market\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t1040.945505", "2011-01-13T10:00:00.000Z\ttotal_market\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t1689.012875", "2011-01-13T11:00:00.000Z\tupfront\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t826.060182\tvalue", "2011-01-13T12:00:00.000Z\tupfront\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t1564.617729\tvalue" };

    private static Segment segment0;

    private static Segment segment1;

    private final int limit;

    private final int batchSize;

    public MultiSegmentScanQueryTest(int limit, int batchSize) {
        this.limit = limit;
        this.batchSize = batchSize;
    }

    @Test
    public void testMergeRunnersWithLimit() {
        ScanQuery query = newBuilder().build();
        List<ScanResultValue> results = MultiSegmentScanQueryTest.factory.mergeRunners(Execs.directExecutor(), ImmutableList.of(MultiSegmentScanQueryTest.factory.createRunner(MultiSegmentScanQueryTest.segment0), MultiSegmentScanQueryTest.factory.createRunner(MultiSegmentScanQueryTest.segment1))).run(QueryPlus.wrap(query), new HashMap()).toList();
        int totalCount = 0;
        for (ScanResultValue result : results) {
            System.out.println(((List) (result.getEvents())).size());
            totalCount += ((List) (result.getEvents())).size();
        }
        Assert.assertEquals(totalCount, ((limit) != 0 ? Math.min(limit, ((MultiSegmentScanQueryTest.V_0112.length) + (MultiSegmentScanQueryTest.V_0113.length))) : (MultiSegmentScanQueryTest.V_0112.length) + (MultiSegmentScanQueryTest.V_0113.length)));
    }

    @Test
    public void testMergeResultsWithLimit() {
        QueryRunner<ScanResultValue> runner = MultiSegmentScanQueryTest.toolChest.mergeResults(new QueryRunner<ScanResultValue>() {
            @Override
            public Sequence<ScanResultValue> run(QueryPlus<ScanResultValue> queryPlus, Map<String, Object> responseContext) {
                // simulate results back from 2 historicals
                List<Sequence<ScanResultValue>> sequences = Lists.newArrayListWithExpectedSize(2);
                sequences.add(MultiSegmentScanQueryTest.factory.createRunner(MultiSegmentScanQueryTest.segment0).run(queryPlus, new HashMap()));
                sequences.add(MultiSegmentScanQueryTest.factory.createRunner(MultiSegmentScanQueryTest.segment1).run(queryPlus, new HashMap()));
                return new org.apache.druid.java.util.common.guava.MergeSequence(queryPlus.getQuery().getResultOrdering(), Sequences.simple(sequences));
            }
        });
        ScanQuery query = newBuilder().build();
        List<ScanResultValue> results = runner.run(QueryPlus.wrap(query), new HashMap()).toList();
        int totalCount = 0;
        for (ScanResultValue result : results) {
            totalCount += ((List) (result.getEvents())).size();
        }
        Assert.assertEquals(totalCount, ((limit) != 0 ? Math.min(limit, ((MultiSegmentScanQueryTest.V_0112.length) + (MultiSegmentScanQueryTest.V_0113.length))) : (MultiSegmentScanQueryTest.V_0112.length) + (MultiSegmentScanQueryTest.V_0113.length)));
    }
}

