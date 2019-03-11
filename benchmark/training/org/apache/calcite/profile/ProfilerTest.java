/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.profile;


import Profiler.Column;
import Profiler.Distribution;
import Profiler.Profile;
import Profiler.Statistic;
import ProfilerImpl.SurpriseQueue;
import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.metadata.NullSentinel;
import org.apache.calcite.test.CalciteAssert;
import org.apache.calcite.test.CalciteConnection;
import org.apache.calcite.test.Matchers;
import org.apache.calcite.test.SlowTests;
import org.apache.calcite.util.ImmutableBitSet;
import org.apache.calcite.util.JsonBuilder;
import org.apache.calcite.util.TestUtil;
import org.apache.calcite.util.Util;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for {@link Profiler}.
 */
@Category(SlowTests.class)
public class ProfilerTest {
    @Test
    public void testProfileZeroRows() throws Exception {
        final String sql = "select * from \"scott\".dept where false";
        ProfilerTest.sql(sql).unordered("{type:distribution,columns:[DEPTNO,DNAME,LOC],cardinality:0}", "{type:distribution,columns:[DEPTNO,DNAME],cardinality:0}", "{type:distribution,columns:[DEPTNO,LOC],cardinality:0}", "{type:distribution,columns:[DEPTNO],values:[],cardinality:0}", "{type:distribution,columns:[DNAME,LOC],cardinality:0}", "{type:distribution,columns:[DNAME],values:[],cardinality:0}", "{type:distribution,columns:[LOC],values:[],cardinality:0}", "{type:distribution,columns:[],cardinality:0}", "{type:rowCount,rowCount:0}", "{type:unique,columns:[]}");
    }

    @Test
    public void testProfileOneRow() throws Exception {
        final String sql = "select * from \"scott\".dept where deptno = 10";
        ProfilerTest.sql(sql).unordered("{type:distribution,columns:[DEPTNO,DNAME,LOC],cardinality:1}", "{type:distribution,columns:[DEPTNO,DNAME],cardinality:1}", "{type:distribution,columns:[DEPTNO,LOC],cardinality:1}", "{type:distribution,columns:[DEPTNO],values:[10],cardinality:1}", "{type:distribution,columns:[DNAME,LOC],cardinality:1}", "{type:distribution,columns:[DNAME],values:[ACCOUNTING],cardinality:1}", "{type:distribution,columns:[LOC],values:[NEWYORK],cardinality:1}", "{type:distribution,columns:[],cardinality:1}", "{type:rowCount,rowCount:1}", "{type:unique,columns:[]}");
    }

    @Test
    public void testProfileTwoRows() throws Exception {
        final String sql = "select * from \"scott\".dept where deptno in (10, 20)";
        ProfilerTest.sql(sql).unordered("{type:distribution,columns:[DEPTNO,DNAME,LOC],cardinality:2}", "{type:distribution,columns:[DEPTNO,DNAME],cardinality:2}", "{type:distribution,columns:[DEPTNO,LOC],cardinality:2}", "{type:distribution,columns:[DEPTNO],values:[10,20],cardinality:2}", "{type:distribution,columns:[DNAME,LOC],cardinality:2}", "{type:distribution,columns:[DNAME],values:[ACCOUNTING,RESEARCH],cardinality:2}", "{type:distribution,columns:[LOC],values:[DALLAS,NEWYORK],cardinality:2}", "{type:distribution,columns:[],cardinality:1}", "{type:rowCount,rowCount:2}", "{type:unique,columns:[DEPTNO]}", "{type:unique,columns:[DNAME]}", "{type:unique,columns:[LOC]}");
    }

    @Test
    public void testProfileScott() throws Exception {
        final String sql = "select * from \"scott\".emp\n" + "join \"scott\".dept on emp.deptno = dept.deptno";
        ProfilerTest.sql(sql).where(( statistic) -> (!(statistic instanceof Profiler.Distribution)) || (((((Profiler.Distribution) (statistic)).cardinality) < 14) && (((Profiler.Distribution) (statistic)).minimal))).unordered("{type:distribution,columns:[COMM,DEPTNO0],cardinality:5}", "{type:distribution,columns:[COMM,DEPTNO],cardinality:5}", "{type:distribution,columns:[COMM,DNAME],cardinality:5}", "{type:distribution,columns:[COMM,LOC],cardinality:5}", "{type:distribution,columns:[COMM],values:[0.00,300.00,500.00,1400.00],cardinality:5,nullCount:10}", "{type:distribution,columns:[DEPTNO,DEPTNO0],cardinality:3}", "{type:distribution,columns:[DEPTNO,DNAME],cardinality:3}", "{type:distribution,columns:[DEPTNO,LOC],cardinality:3}", "{type:distribution,columns:[DEPTNO0,DNAME],cardinality:3}", "{type:distribution,columns:[DEPTNO0,LOC],cardinality:3}", "{type:distribution,columns:[DEPTNO0],values:[10,20,30],cardinality:3}", "{type:distribution,columns:[DEPTNO],values:[10,20,30],cardinality:3}", "{type:distribution,columns:[DNAME,LOC],cardinality:3}", "{type:distribution,columns:[DNAME],values:[ACCOUNTING,RESEARCH,SALES],cardinality:3}", "{type:distribution,columns:[HIREDATE,COMM],cardinality:5}", "{type:distribution,columns:[HIREDATE],values:[1980-12-17,1981-01-05,1981-02-04,1981-02-20,1981-02-22,1981-06-09,1981-09-08,1981-09-28,1981-11-17,1981-12-03,1982-01-23,1987-04-19,1987-05-23],cardinality:13}", "{type:distribution,columns:[JOB,COMM],cardinality:5}", "{type:distribution,columns:[JOB,DEPTNO0],cardinality:9}", "{type:distribution,columns:[JOB,DEPTNO],cardinality:9}", "{type:distribution,columns:[JOB,DNAME],cardinality:9}", "{type:distribution,columns:[JOB,LOC],cardinality:9}", "{type:distribution,columns:[JOB,MGR,DEPTNO0],cardinality:10}", "{type:distribution,columns:[JOB,MGR,DEPTNO],cardinality:10}", "{type:distribution,columns:[JOB,MGR,DNAME],cardinality:10}", "{type:distribution,columns:[JOB,MGR,LOC],cardinality:10}", "{type:distribution,columns:[JOB,MGR],cardinality:8}", "{type:distribution,columns:[JOB,SAL],cardinality:12}", "{type:distribution,columns:[JOB],values:[ANALYST,CLERK,MANAGER,PRESIDENT,SALESMAN],cardinality:5}", "{type:distribution,columns:[LOC],values:[CHICAGO,DALLAS,NEWYORK],cardinality:3}", "{type:distribution,columns:[MGR,COMM],cardinality:5}", "{type:distribution,columns:[MGR,DEPTNO0],cardinality:9}", "{type:distribution,columns:[MGR,DEPTNO],cardinality:9}", "{type:distribution,columns:[MGR,DNAME],cardinality:9}", "{type:distribution,columns:[MGR,LOC],cardinality:9}", "{type:distribution,columns:[MGR,SAL],cardinality:12}", "{type:distribution,columns:[MGR],values:[7566,7698,7782,7788,7839,7902],cardinality:7,nullCount:1}", "{type:distribution,columns:[SAL,COMM],cardinality:5}", "{type:distribution,columns:[SAL,DEPTNO0],cardinality:12}", "{type:distribution,columns:[SAL,DEPTNO],cardinality:12}", "{type:distribution,columns:[SAL,DNAME],cardinality:12}", "{type:distribution,columns:[SAL,LOC],cardinality:12}", "{type:distribution,columns:[SAL],values:[800.00,950.00,1100.00,1250.00,1300.00,1500.00,1600.00,2450.00,2850.00,2975.00,3000.00,5000.00],cardinality:12}", "{type:distribution,columns:[],cardinality:1}", "{type:fd,columns:[DEPTNO0],dependentColumn:DEPTNO}", "{type:fd,columns:[DEPTNO0],dependentColumn:DNAME}", "{type:fd,columns:[DEPTNO0],dependentColumn:LOC}", "{type:fd,columns:[DEPTNO],dependentColumn:DEPTNO0}", "{type:fd,columns:[DEPTNO],dependentColumn:DNAME}", "{type:fd,columns:[DEPTNO],dependentColumn:LOC}", "{type:fd,columns:[DNAME],dependentColumn:DEPTNO0}", "{type:fd,columns:[DNAME],dependentColumn:DEPTNO}", "{type:fd,columns:[DNAME],dependentColumn:LOC}", "{type:fd,columns:[JOB],dependentColumn:COMM}", "{type:fd,columns:[LOC],dependentColumn:DEPTNO0}", "{type:fd,columns:[LOC],dependentColumn:DEPTNO}", "{type:fd,columns:[LOC],dependentColumn:DNAME}", "{type:fd,columns:[SAL],dependentColumn:DEPTNO0}", "{type:fd,columns:[SAL],dependentColumn:DEPTNO}", "{type:fd,columns:[SAL],dependentColumn:DNAME}", "{type:fd,columns:[SAL],dependentColumn:JOB}", "{type:fd,columns:[SAL],dependentColumn:LOC}", "{type:fd,columns:[SAL],dependentColumn:MGR}", "{type:rowCount,rowCount:14}", "{type:unique,columns:[EMPNO]}", "{type:unique,columns:[ENAME]}", "{type:unique,columns:[HIREDATE,DEPTNO0]}", "{type:unique,columns:[HIREDATE,DEPTNO]}", "{type:unique,columns:[HIREDATE,DNAME]}", "{type:unique,columns:[HIREDATE,LOC]}", "{type:unique,columns:[HIREDATE,SAL]}", "{type:unique,columns:[JOB,HIREDATE]}");
    }

    /**
     * As {@link #testProfileScott()}, but prints only the most surprising
     * distributions.
     */
    @Test
    public void testProfileScott2() throws Exception {
        scott().factory(ProfilerTest.Fluid.SIMPLE_FACTORY).unordered("{type:distribution,columns:[COMM],values:[0.00,300.00,500.00,1400.00],cardinality:5,nullCount:10,expectedCardinality:14,surprise:0.474}", "{type:distribution,columns:[DEPTNO,DEPTNO0],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DEPTNO,DNAME],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DEPTNO,LOC],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DEPTNO0,DNAME],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DEPTNO0,LOC],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DEPTNO0],values:[10,20,30],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[DEPTNO],values:[10,20,30],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[DNAME,LOC],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DNAME],values:[ACCOUNTING,RESEARCH,SALES],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[HIREDATE,COMM],cardinality:5,expectedCardinality:12.683,surprise:0.434}", "{type:distribution,columns:[HIREDATE],values:[1980-12-17,1981-01-05,1981-02-04,1981-02-20,1981-02-22,1981-06-09,1981-09-08,1981-09-28,1981-11-17,1981-12-03,1982-01-23,1987-04-19,1987-05-23],cardinality:13,expectedCardinality:14,surprise:0.0370}", "{type:distribution,columns:[JOB],values:[ANALYST,CLERK,MANAGER,PRESIDENT,SALESMAN],cardinality:5,expectedCardinality:14,surprise:0.474}", "{type:distribution,columns:[LOC],values:[CHICAGO,DALLAS,NEWYORK],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[MGR,COMM],cardinality:5,expectedCardinality:11.675,surprise:0.400}", "{type:distribution,columns:[MGR],values:[7566,7698,7782,7788,7839,7902],cardinality:7,nullCount:1,expectedCardinality:14,surprise:0.333}", "{type:distribution,columns:[SAL,COMM],cardinality:5,expectedCardinality:12.580,surprise:0.431}", "{type:distribution,columns:[SAL],values:[800.00,950.00,1100.00,1250.00,1300.00,1500.00,1600.00,2450.00,2850.00,2975.00,3000.00,5000.00],cardinality:12,expectedCardinality:14,surprise:0.0769}", "{type:distribution,columns:[],cardinality:1,expectedCardinality:1,surprise:0}");
    }

    /**
     * As {@link #testProfileScott2()}, but uses the breadth-first profiler.
     * Results should be the same, but are slightly different (extra EMPNO
     * and ENAME distributions).
     */
    @Test
    public void testProfileScott3() throws Exception {
        scott().factory(ProfilerTest.Fluid.BETTER_FACTORY).unordered("{type:distribution,columns:[COMM],values:[0.00,300.00,500.00,1400.00],cardinality:5,nullCount:10,expectedCardinality:14,surprise:0.474}", "{type:distribution,columns:[DEPTNO,DEPTNO0,DNAME,LOC],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DEPTNO,DEPTNO0],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DEPTNO,DNAME],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DEPTNO,LOC],cardinality:3,expectedCardinality:7.2698,surprise:0.416}", "{type:distribution,columns:[DEPTNO0,DNAME,LOC],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[DEPTNO0],values:[10,20,30],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[DEPTNO],values:[10,20,30],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[DNAME],values:[ACCOUNTING,RESEARCH,SALES],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[EMPNO],values:[7369,7499,7521,7566,7654,7698,7782,7788,7839,7844,7876,7900,7902,7934],cardinality:14,expectedCardinality:14,surprise:0}", "{type:distribution,columns:[ENAME],values:[ADAMS,ALLEN,BLAKE,CLARK,FORD,JAMES,JONES,KING,MARTIN,MILLER,SCOTT,SMITH,TURNER,WARD],cardinality:14,expectedCardinality:14,surprise:0}", "{type:distribution,columns:[HIREDATE],values:[1980-12-17,1981-01-05,1981-02-04,1981-02-20,1981-02-22,1981-06-09,1981-09-08,1981-09-28,1981-11-17,1981-12-03,1982-01-23,1987-04-19,1987-05-23],cardinality:13,expectedCardinality:14,surprise:0.0370}", "{type:distribution,columns:[JOB],values:[ANALYST,CLERK,MANAGER,PRESIDENT,SALESMAN],cardinality:5,expectedCardinality:14,surprise:0.474}", "{type:distribution,columns:[LOC],values:[CHICAGO,DALLAS,NEWYORK],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[MGR],values:[7566,7698,7782,7788,7839,7902],cardinality:7,nullCount:1,expectedCardinality:14,surprise:0.333}", "{type:distribution,columns:[SAL],values:[800.00,950.00,1100.00,1250.00,1300.00,1500.00,1600.00,2450.00,2850.00,2975.00,3000.00,5000.00],cardinality:12,expectedCardinality:14,surprise:0.0769}", "{type:distribution,columns:[],cardinality:1,expectedCardinality:1,surprise:0}");
    }

    /**
     * As {@link #testProfileScott3()}, but uses the breadth-first profiler
     * and deems everything uninteresting. Only first-level combinations (those
     * consisting of a single column) are computed.
     */
    @Test
    public void testProfileScott4() throws Exception {
        scott().factory(ProfilerTest.Fluid.INCURIOUS_PROFILER_FACTORY).unordered("{type:distribution,columns:[COMM],values:[0.00,300.00,500.00,1400.00],cardinality:5,nullCount:10,expectedCardinality:14,surprise:0.474}", "{type:distribution,columns:[DEPTNO0,DNAME,LOC],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[DEPTNO0],values:[10,20,30],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[DEPTNO],values:[10,20,30],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[DNAME],values:[ACCOUNTING,RESEARCH,SALES],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[EMPNO],values:[7369,7499,7521,7566,7654,7698,7782,7788,7839,7844,7876,7900,7902,7934],cardinality:14,expectedCardinality:14,surprise:0}", "{type:distribution,columns:[ENAME],values:[ADAMS,ALLEN,BLAKE,CLARK,FORD,JAMES,JONES,KING,MARTIN,MILLER,SCOTT,SMITH,TURNER,WARD],cardinality:14,expectedCardinality:14,surprise:0}", "{type:distribution,columns:[HIREDATE],values:[1980-12-17,1981-01-05,1981-02-04,1981-02-20,1981-02-22,1981-06-09,1981-09-08,1981-09-28,1981-11-17,1981-12-03,1982-01-23,1987-04-19,1987-05-23],cardinality:13,expectedCardinality:14,surprise:0.0370}", "{type:distribution,columns:[JOB],values:[ANALYST,CLERK,MANAGER,PRESIDENT,SALESMAN],cardinality:5,expectedCardinality:14,surprise:0.474}", "{type:distribution,columns:[LOC],values:[CHICAGO,DALLAS,NEWYORK],cardinality:3,expectedCardinality:14,surprise:0.647}", "{type:distribution,columns:[MGR],values:[7566,7698,7782,7788,7839,7902],cardinality:7,nullCount:1,expectedCardinality:14,surprise:0.333}", "{type:distribution,columns:[SAL],values:[800.00,950.00,1100.00,1250.00,1300.00,1500.00,1600.00,2450.00,2850.00,2975.00,3000.00,5000.00],cardinality:12,expectedCardinality:14,surprise:0.0769}", "{type:distribution,columns:[],cardinality:1,expectedCardinality:1,surprise:0}");
    }

    /**
     * Tests
     * {@link org.apache.calcite.profile.ProfilerImpl.SurpriseQueue}.
     */
    @Test
    public void testSurpriseQueue() {
        ProfilerImpl.SurpriseQueue q = new ProfilerImpl.SurpriseQueue(4, 3);
        Assert.assertThat(q.offer(2), Is.is(true));
        Assert.assertThat(q.toString(), Is.is("min: 2.0, contents: [2.0]"));
        Assert.assertThat(q.isValid(), Is.is(true));
        Assert.assertThat(q.offer(4), Is.is(true));
        Assert.assertThat(q.toString(), Is.is("min: 2.0, contents: [2.0, 4.0]"));
        Assert.assertThat(q.isValid(), Is.is(true));
        // Since we're in the warm-up period, a value lower than the minimum is
        // accepted.
        Assert.assertThat(q.offer(1), Is.is(true));
        Assert.assertThat(q.toString(), Is.is("min: 1.0, contents: [2.0, 4.0, 1.0]"));
        Assert.assertThat(q.isValid(), Is.is(true));
        Assert.assertThat(q.offer(5), Is.is(true));
        Assert.assertThat(q.toString(), Is.is("min: 1.0, contents: [4.0, 1.0, 5.0]"));
        Assert.assertThat(q.isValid(), Is.is(true));
        Assert.assertThat(q.offer(3), Is.is(true));
        Assert.assertThat(q.toString(), Is.is("min: 1.0, contents: [1.0, 5.0, 3.0]"));
        Assert.assertThat(q.isValid(), Is.is(true));
        // Duplicate entry
        Assert.assertThat(q.offer(5), Is.is(true));
        Assert.assertThat(q.toString(), Is.is("min: 3.0, contents: [5.0, 3.0, 5.0]"));
        Assert.assertThat(q.isValid(), Is.is(true));
        // Now that the list is full, a value below the minimum is refused.
        // "offer" returns false, and the value is not added to the queue.
        // Thus the median never decreases.
        Assert.assertThat(q.offer(2), Is.is(false));
        Assert.assertThat(q.toString(), Is.is("min: 3.0, contents: [5.0, 3.0, 5.0]"));
        Assert.assertThat(q.isValid(), Is.is(true));
        // Same applies for a value equal to the minimum.
        Assert.assertThat(q.offer(3), Is.is(false));
        Assert.assertThat(q.toString(), Is.is("min: 3.0, contents: [5.0, 3.0, 5.0]"));
        Assert.assertThat(q.isValid(), Is.is(true));
        // Add a value that is above the minimum.
        Assert.assertThat(q.offer(4.5), Is.is(true));
        Assert.assertThat(q.toString(), Is.is("min: 3.0, contents: [3.0, 5.0, 4.5]"));
        Assert.assertThat(q.isValid(), Is.is(true));
    }

    /**
     * Fluid interface for writing profiler test cases.
     */
    private static class Fluid {
        static final Supplier<Profiler> SIMPLE_FACTORY = SimpleProfiler::new;

        static final Supplier<Profiler> BETTER_FACTORY = () -> new ProfilerImpl(600, 200, ( p) -> true);

        static final Ordering<Profiler.Statistic> ORDERING = new Ordering<Profiler.Statistic>() {
            public int compare(Profiler.Statistic left, Profiler.Statistic right) {
                int c = left.getClass().getSimpleName().compareTo(right.getClass().getSimpleName());
                if (((c == 0) && (left instanceof Profiler.Distribution)) && (right instanceof Profiler.Distribution)) {
                    final Profiler.Distribution d0 = ((Profiler.Distribution) (left));
                    final Profiler.Distribution d1 = ((Profiler.Distribution) (right));
                    c = Double.compare(d0.surprise(), d1.surprise());
                    if (c == 0) {
                        c = d0.columns.toString().compareTo(d1.columns.toString());
                    }
                }
                return c;
            }
        };

        static final Predicate<Profiler.Statistic> STATISTIC_PREDICATE = ( statistic) -> ((statistic instanceof Profiler.Distribution) && (((((Profiler.Distribution) (statistic)).columns.size()) < 2) || ((surprise()) > 0.4))) && (((Profiler.Distribution) (statistic)).minimal);

        static final List<String> DEFAULT_COLUMNS = ImmutableList.of("type", "distribution", "columns", "cardinality", "values", "nullCount", "dependentColumn", "rowCount");

        static final List<String> EXTENDED_COLUMNS = ImmutableList.<String>builder().addAll(ProfilerTest.Fluid.DEFAULT_COLUMNS).add("expectedCardinality", "surprise").build();

        private static final Supplier<Profiler> PROFILER_FACTORY = () -> new ProfilerImpl(7500, 100, ( p) -> {
            final Profiler.Distribution distribution = p.left.distribution();
            if (distribution == null) {
                // We don't have a distribution yet, because this space
                // has not yet been evaluated. Let's do it anyway.
                return true;
            }
            return (distribution.surprise()) >= 0.3;
        });

        private static final Supplier<Profiler> INCURIOUS_PROFILER_FACTORY = () -> new ProfilerImpl(10, 200, ( p) -> false);

        private final String sql;

        private final List<String> columns;

        private final Comparator<Profiler.Statistic> comparator;

        private final int limit;

        private final Predicate<Profiler.Statistic> predicate;

        private final Supplier<Profiler> factory;

        private final CalciteAssert.Config config;

        Fluid(CalciteAssert.Config config, String sql, Supplier<Profiler> factory, Predicate<Profiler.Statistic> predicate, Comparator<Profiler.Statistic> comparator, int limit, List<String> columns) {
            this.sql = Objects.requireNonNull(sql);
            this.factory = Objects.requireNonNull(factory);
            this.columns = ImmutableList.copyOf(columns);
            this.predicate = Objects.requireNonNull(predicate);
            this.comparator = comparator;// null means sort on JSON representation

            this.limit = limit;
            this.config = config;
        }

        ProfilerTest.Fluid config(CalciteAssert.Config config) {
            return new ProfilerTest.Fluid(config, sql, factory, predicate, comparator, limit, columns);
        }

        ProfilerTest.Fluid factory(Supplier<Profiler> factory) {
            return new ProfilerTest.Fluid(config, sql, factory, predicate, comparator, limit, columns);
        }

        ProfilerTest.Fluid project(List<String> columns) {
            return new ProfilerTest.Fluid(config, sql, factory, predicate, comparator, limit, columns);
        }

        ProfilerTest.Fluid sort(Ordering<Profiler.Statistic> comparator) {
            return new ProfilerTest.Fluid(config, sql, factory, predicate, comparator, limit, columns);
        }

        ProfilerTest.Fluid limit(int limit) {
            return new ProfilerTest.Fluid(config, sql, factory, predicate, comparator, limit, columns);
        }

        ProfilerTest.Fluid where(Predicate<Profiler.Statistic> predicate) {
            return new ProfilerTest.Fluid(config, sql, factory, predicate, comparator, limit, columns);
        }

        ProfilerTest.Fluid unordered(String... lines) throws Exception {
            return check(Matchers.equalsUnordered(lines));
        }

        public ProfilerTest.Fluid check(final Matcher<Iterable<String>> matcher) throws Exception {
            CalciteAssert.that(config).doWithConnection(( c) -> {
                try (PreparedStatement s = c.prepareStatement(sql)) {
                    final ResultSetMetaData m = s.getMetaData();
                    final List<Profiler.Column> columns = new ArrayList<>();
                    final int columnCount = m.getColumnCount();
                    for (int i = 0; i < columnCount; i++) {
                        columns.add(new Profiler.Column(i, m.getColumnLabel((i + 1))));
                    }
                    // Create an initial group for each table in the query.
                    // Columns in the same table will tend to have the same
                    // cardinality as the table, and as the table's primary key.
                    final Multimap<String, Integer> groups = HashMultimap.create();
                    for (int i = 0; i < (m.getColumnCount()); i++) {
                        groups.put(m.getTableName((i + 1)), i);
                    }
                    final SortedSet<ImmutableBitSet> initialGroups = new TreeSet<>();
                    for (Collection<Integer> integers : groups.asMap().values()) {
                        initialGroups.add(ImmutableBitSet.of(integers));
                    }
                    final Profiler p = factory.get();
                    final Enumerable<List<Comparable>> rows = getRows(s);
                    final Profiler.Profile profile = p.profile(rows, columns, initialGroups);
                    final List<Profiler.Statistic> statistics = profile.statistics().stream().filter(predicate).collect(Util.toImmutableList());
                    // If no comparator specified, use the function that converts to
                    // JSON strings
                    final ProfilerTest.Fluid.StatisticToJson toJson = new ProfilerTest.Fluid.StatisticToJson();
                    Ordering<Profiler.Statistic> comp = ((comparator) != null) ? Ordering.from(comparator) : Ordering.natural().onResultOf(toJson::apply);
                    ImmutableList<Profiler.Statistic> statistics2 = comp.immutableSortedCopy(statistics);
                    if (((limit) >= 0) && ((limit) < (statistics2.size()))) {
                        statistics2 = statistics2.subList(0, limit);
                    }
                    final List<String> strings = statistics2.stream().map(toJson::apply).collect(Collectors.toList());
                    Assert.assertThat(strings, matcher);
                } catch (SQLException e) {
                    throw TestUtil.rethrow(e);
                }
            });
            return this;
        }

        private Enumerable<List<Comparable>> getRows(final PreparedStatement s) {
            return new org.apache.calcite.linq4j.AbstractEnumerable<List<Comparable>>() {
                public Enumerator<List<Comparable>> enumerator() {
                    try {
                        final ResultSet r = s.executeQuery();
                        return getListEnumerator(r, r.getMetaData().getColumnCount());
                    } catch (SQLException e) {
                        throw TestUtil.rethrow(e);
                    }
                }
            };
        }

        private Enumerator<List<Comparable>> getListEnumerator(final ResultSet r, final int columnCount) {
            return new Enumerator<List<Comparable>>() {
                final Comparable[] values = new Comparable[columnCount];

                public List<Comparable> current() {
                    for (int i = 0; i < columnCount; i++) {
                        try {
                            final Comparable value = ((Comparable) (r.getObject((i + 1))));
                            values[i] = NullSentinel.mask(value);
                        } catch (SQLException e) {
                            throw TestUtil.rethrow(e);
                        }
                    }
                    return ImmutableList.copyOf(values);
                }

                public boolean moveNext() {
                    try {
                        return r.next();
                    } catch (SQLException e) {
                        throw TestUtil.rethrow(e);
                    }
                }

                public void reset() {
                }

                public void close() {
                    try {
                        r.close();
                    } catch (SQLException e) {
                        throw TestUtil.rethrow(e);
                    }
                }
            };
        }

        /**
         * Returns a function that converts a statistic to a JSON string.
         */
        private class StatisticToJson {
            final JsonBuilder jb = new JsonBuilder();

            public String apply(Profiler.Statistic statistic) {
                Object map = statistic.toMap(jb);
                if (map instanceof Map) {
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> map1 = ((Map) (map));
                    map1.keySet().retainAll(ProfilerTest.Fluid.this.columns);
                }
                final String json = jb.toJsonString(map);
                return json.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\"", "");
            }
        }
    }
}

/**
 * End ProfilerTest.java
 */
