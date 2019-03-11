/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.jdbc.test;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReport.QueryStats;
import org.junit.Assert;
import org.junit.Test;


public class TestSlowQueryComparator {
    @Test
    public void testBug58489() throws Exception {
        long[] testData = new long[]{ 0, 0, 0, 1444225382010L, 0, 1444225382011L, 0, 1444225382012L, 0, 1444225382056L, 0, 1444225382014L, 0, 1444225382015L, 0, 1444225382016L, 0, 0, 1444225382017L, 0, 1444225678350L, 0, 1444225680397L, 0, 1444225382018L, 1444225382019L, 1444225382020L, 0, 1444225382021L, 0, 1444225382022L, 1444225382023L };
        List<QueryStats> stats = new ArrayList<>();
        for (int i = 0; i < (testData.length); i++) {
            QueryStats qs = new QueryStats(String.valueOf(i));
            qs.add(0, testData[i]);
            stats.add(qs);
        }
        try {
            Collections.sort(stats, createComparator());
        } catch (IllegalArgumentException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testEqualQueryStatsWithNoLastInvocation() throws Exception {
        Comparator<QueryStats> queryStatsComparator = createComparator();
        QueryStats q1 = new QueryStats("abc");
        Assert.assertEquals(0, queryStatsComparator.compare(q1, q1));
    }

    @Test
    public void testEqualQueryStatsWithLastInvocation() throws Exception {
        Comparator<QueryStats> queryStatsComparator = createComparator();
        QueryStats q1 = new QueryStats("abc");
        q1.add(0, 100);
        Assert.assertEquals(0, queryStatsComparator.compare(q1, q1));
    }

    @Test
    public void testQueryStatsOneWithLastInvocation() throws Exception {
        Comparator<QueryStats> queryStatsComparator = createComparator();
        QueryStats q1 = new QueryStats("abc");
        QueryStats q2 = new QueryStats("def");
        q2.add(0, 100);
        Assert.assertEquals(1, queryStatsComparator.compare(q1, q2));
        Assert.assertEquals((-1), queryStatsComparator.compare(q2, q1));
    }

    @Test
    public void testQueryStatsBothWithSameLastInvocation() throws Exception {
        Comparator<QueryStats> queryStatsComparator = createComparator();
        QueryStats q1 = new QueryStats("abc");
        QueryStats q2 = new QueryStats("def");
        q1.add(0, 100);
        q2.add(0, 100);
        Assert.assertEquals(0, queryStatsComparator.compare(q1, q2));
        Assert.assertEquals(0, queryStatsComparator.compare(q2, q1));
    }

    @Test
    public void testQueryStatsBothWithSomeLastInvocation() throws Exception {
        Comparator<QueryStats> queryStatsComparator = createComparator();
        QueryStats q1 = new QueryStats("abc");
        QueryStats q2 = new QueryStats("abc");
        q1.add(0, 100);
        q2.add(0, 150);
        Assert.assertEquals((-1), queryStatsComparator.compare(q1, q2));
        Assert.assertEquals(1, queryStatsComparator.compare(q2, q1));
    }
}

