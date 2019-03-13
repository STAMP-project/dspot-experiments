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
package org.apache.calcite.test;


import CalciteSystemProperty.METADATA_HANDLER_CACHE_MAXIMUM_SIZE;
import CalciteSystemProperty.TEST_SLOW;
import JaninoRelMetadataProvider.NoHandler;
import JoinRelType.INNER;
import RelDistributions.BROADCAST_DISTRIBUTED;
import RelMetadataQuery.THREAD_PROVIDERS;
import SqlKind.PLUS;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptPredicateList;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelDistribution;
import org.apache.calcite.rel.RelDistributions;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.SingleRel;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.Minus;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.Sort;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Union;
import org.apache.calcite.rel.core.Values;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.logical.LogicalExchange;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.metadata.ChainedRelMetadataProvider;
import org.apache.calcite.rel.metadata.JaninoRelMetadataProvider;
import org.apache.calcite.rel.metadata.Metadata;
import org.apache.calcite.rel.metadata.MetadataDef;
import org.apache.calcite.rel.metadata.MetadataHandler;
import org.apache.calcite.rel.metadata.ReflectiveRelMetadataProvider;
import org.apache.calcite.rel.metadata.RelMdColumnUniqueness;
import org.apache.calcite.rel.metadata.RelMdUtil;
import org.apache.calcite.rel.metadata.RelMetadataProvider;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexTableInputRef;
import org.apache.calcite.rex.RexTableInputRef.RelTableRef;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.ImmutableBitSet;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static org.apache.calcite.test.JdbcAdapterTest.LockWrapper.lock;


/**
 * Unit test for {@link DefaultRelMetadataProvider}. See
 * {@link SqlToRelTestBase} class comments for details on the schema used. Note
 * that no optimizer rules are fired on the translation of the SQL into
 * relational algebra (e.g. join conditions in the WHERE clause will look like
 * filters), so it's necessary to phrase the SQL carefully.
 */
public class RelMetadataTest extends SqlToRelTestBase {
    // ~ Static fields/initializers ---------------------------------------------
    private static final double EPSILON = 1.0E-5;

    private static final double DEFAULT_EQUAL_SELECTIVITY = 0.15;

    private static final double DEFAULT_EQUAL_SELECTIVITY_SQUARED = (RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY) * (RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY);

    private static final double DEFAULT_COMP_SELECTIVITY = 0.5;

    private static final double DEFAULT_NOTNULL_SELECTIVITY = 0.9;

    private static final double DEFAULT_SELECTIVITY = 0.25;

    private static final double EMP_SIZE = 14.0;

    private static final double DEPT_SIZE = 4.0;

    private static final List<String> EMP_QNAME = ImmutableList.of("CATALOG", "SALES", "EMP");

    /**
     * Ensures that tests that use a lot of memory do not run at the same
     * time.
     */
    private static final ReentrantLock LOCK = new ReentrantLock();

    @Test
    public void testPercentageOriginalRowsTableOnly() {
        checkPercentageOriginalRows("select * from dept", 1.0);
    }

    @Test
    public void testPercentageOriginalRowsAgg() {
        checkPercentageOriginalRows("select deptno from dept group by deptno", 1.0);
    }

    @Test
    public void testPercentageOriginalRowsJoin() {
        checkPercentageOriginalRows("select * from emp inner join dept on emp.deptno=dept.deptno", 1.0);
    }

    @Test
    public void testPercentageOriginalRowsUnionNoFilter() {
        checkPercentageOriginalRows("select name from dept union all select ename from emp", 1.0);
    }

    @Test
    public void testColumnOriginsTableOnly() {
        checkSingleColumnOrigin("select name as dname from dept", "DEPT", "NAME", false);
    }

    @Test
    public void testColumnOriginsExpression() {
        checkSingleColumnOrigin("select upper(name) as dname from dept", "DEPT", "NAME", true);
    }

    @Test
    public void testColumnOriginsDyadicExpression() {
        checkTwoColumnOrigin("select name||ename from dept,emp", "DEPT", "NAME", "EMP", "ENAME", true);
    }

    @Test
    public void testColumnOriginsConstant() {
        checkNoColumnOrigin("select 'Minstrelsy' as dname from dept");
    }

    @Test
    public void testColumnOriginsFilter() {
        checkSingleColumnOrigin("select name as dname from dept where deptno=10", "DEPT", "NAME", false);
    }

    @Test
    public void testColumnOriginsJoinLeft() {
        checkSingleColumnOrigin("select ename from emp,dept", "EMP", "ENAME", false);
    }

    @Test
    public void testColumnOriginsJoinRight() {
        checkSingleColumnOrigin("select name as dname from emp,dept", "DEPT", "NAME", false);
    }

    @Test
    public void testColumnOriginsJoinOuter() {
        checkSingleColumnOrigin(("select name as dname from emp left outer join dept" + " on emp.deptno = dept.deptno"), "DEPT", "NAME", true);
    }

    @Test
    public void testColumnOriginsJoinFullOuter() {
        checkSingleColumnOrigin(("select name as dname from emp full outer join dept" + " on emp.deptno = dept.deptno"), "DEPT", "NAME", true);
    }

    @Test
    public void testColumnOriginsAggKey() {
        checkSingleColumnOrigin("select name,count(deptno) from dept group by name", "DEPT", "NAME", false);
    }

    @Test
    public void testColumnOriginsAggReduced() {
        checkNoColumnOrigin("select count(deptno),name from dept group by name");
    }

    @Test
    public void testColumnOriginsAggCountNullable() {
        checkSingleColumnOrigin("select count(mgr),ename from emp group by ename", "EMP", "MGR", true);
    }

    @Test
    public void testColumnOriginsAggCountStar() {
        checkNoColumnOrigin("select count(*),name from dept group by name");
    }

    @Test
    public void testColumnOriginsValues() {
        checkNoColumnOrigin("values(1,2,3)");
    }

    @Test
    public void testColumnOriginsUnion() {
        checkTwoColumnOrigin("select name from dept union all select ename from emp", "DEPT", "NAME", "EMP", "ENAME", false);
    }

    @Test
    public void testColumnOriginsSelfUnion() {
        checkSingleColumnOrigin("select ename from emp union all select ename from emp", "EMP", "ENAME", false);
    }

    @Test
    public void testRowCountEmp() {
        final String sql = "select * from emp";
        checkRowCount(sql, RelMetadataTest.EMP_SIZE, 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountDept() {
        final String sql = "select * from dept";
        checkRowCount(sql, RelMetadataTest.DEPT_SIZE, 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountValues() {
        final String sql = "select * from (values (1), (2)) as t(c)";
        checkRowCount(sql, 2, 2, 2);
    }

    @Test
    public void testRowCountCartesian() {
        final String sql = "select * from emp,dept";
        checkRowCount(sql, ((RelMetadataTest.EMP_SIZE) * (RelMetadataTest.DEPT_SIZE)), 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountJoin() {
        final String sql = "select * from emp\n" + "inner join dept on emp.deptno = dept.deptno";
        checkRowCount(sql, (((RelMetadataTest.EMP_SIZE) * (RelMetadataTest.DEPT_SIZE)) * (RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY)), 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountJoinFinite() {
        final String sql = "select * from (select * from emp limit 14) as emp\n" + ("inner join (select * from dept limit 4) as dept\n" + "on emp.deptno = dept.deptno");
        checkRowCount(sql, (((RelMetadataTest.EMP_SIZE) * (RelMetadataTest.DEPT_SIZE)) * (RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY)), 0.0, 56.0);// 4 * 14

    }

    @Test
    public void testRowCountJoinEmptyFinite() {
        final String sql = "select * from (select * from emp limit 0) as emp\n" + ("inner join (select * from dept limit 4) as dept\n" + "on emp.deptno = dept.deptno");
        // 0, rounded up to row count's minimum 1
        checkRowCount(sql, 1.0, 0.0, 0.0);// 0 * 4

    }

    @Test
    public void testRowCountLeftJoinEmptyFinite() {
        final String sql = "select * from (select * from emp limit 0) as emp\n" + ("left join (select * from dept limit 4) as dept\n" + "on emp.deptno = dept.deptno");
        // 0, rounded up to row count's minimum 1
        checkRowCount(sql, 1.0, 0.0, 0.0);// 0 * 4

    }

    @Test
    public void testRowCountRightJoinEmptyFinite() {
        final String sql = "select * from (select * from emp limit 0) as emp\n" + ("right join (select * from dept limit 4) as dept\n" + "on emp.deptno = dept.deptno");
        // 0, rounded up to row count's minimum 1
        checkRowCount(sql, 1.0, 0.0, 4.0);// 1 * 4

    }

    @Test
    public void testRowCountJoinFiniteEmpty() {
        final String sql = "select * from (select * from emp limit 7) as emp\n" + ("inner join (select * from dept limit 0) as dept\n" + "on emp.deptno = dept.deptno");
        // 0, rounded up to row count's minimum 1
        checkRowCount(sql, 1.0, 0.0, 0.0);// 7 * 0

    }

    @Test
    public void testRowCountJoinEmptyEmpty() {
        final String sql = "select * from (select * from emp limit 0) as emp\n" + ("inner join (select * from dept limit 0) as dept\n" + "on emp.deptno = dept.deptno");
        // 0, rounded up to row count's minimum 1
        checkRowCount(sql, 1.0, 0.0, 0.0);// 0 * 0

    }

    @Test
    public void testRowCountUnion() {
        final String sql = "select ename from emp\n" + ("union all\n" + "select name from dept");
        checkRowCount(sql, ((RelMetadataTest.EMP_SIZE) + (RelMetadataTest.DEPT_SIZE)), 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountUnionOnFinite() {
        final String sql = "select ename from (select * from emp limit 100)\n" + ("union all\n" + "select name from (select * from dept limit 40)");
        checkRowCount(sql, ((RelMetadataTest.EMP_SIZE) + (RelMetadataTest.DEPT_SIZE)), 0.0, 140.0);
    }

    @Test
    public void testRowCountIntersectOnFinite() {
        final String sql = "select ename from (select * from emp limit 100)\n" + ("intersect\n" + "select name from (select * from dept limit 40)");
        checkRowCount(sql, Math.min(RelMetadataTest.EMP_SIZE, RelMetadataTest.DEPT_SIZE), 0.0, 40.0);
    }

    @Test
    public void testRowCountMinusOnFinite() {
        final String sql = "select ename from (select * from emp limit 100)\n" + ("except\n" + "select name from (select * from dept limit 40)");
        checkRowCount(sql, 4.0, 0.0, 100.0);
    }

    @Test
    public void testRowCountFilter() {
        final String sql = "select * from emp where ename='Mathilda'";
        checkRowCount(sql, ((RelMetadataTest.EMP_SIZE) * (RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY)), 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountFilterOnFinite() {
        final String sql = "select * from (select * from emp limit 10)\n" + "where ename='Mathilda'";
        checkRowCount(sql, (10.0 * (RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY)), 0.0, 10.0);
    }

    @Test
    public void testRowCountFilterFalse() {
        final String sql = "select * from (values 'a', 'b') as t(x) where false";
        checkRowCount(sql, 1.0, 0.0, 0.0);
    }

    @Test
    public void testRowCountSort() {
        final String sql = "select * from emp order by ename";
        checkRowCount(sql, RelMetadataTest.EMP_SIZE, 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountSortHighLimit() {
        final String sql = "select * from emp order by ename limit 123456";
        checkRowCount(sql, RelMetadataTest.EMP_SIZE, 0.0, 123456.0);
    }

    @Test
    public void testRowCountSortHighOffset() {
        final String sql = "select * from emp order by ename offset 123456";
        checkRowCount(sql, 1.0, 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountSortHighOffsetLimit() {
        final String sql = "select * from emp order by ename limit 5 offset 123456";
        checkRowCount(sql, 1.0, 0.0, 5.0);
    }

    @Test
    public void testRowCountSortLimit() {
        final String sql = "select * from emp order by ename limit 10";
        checkRowCount(sql, 10.0, 0.0, 10.0);
    }

    @Test
    public void testRowCountSortLimit0() {
        final String sql = "select * from emp order by ename limit 10";
        checkRowCount(sql, 10.0, 0.0, 10.0);
    }

    @Test
    public void testRowCountSortLimitOffset() {
        final String sql = "select * from emp order by ename limit 10 offset 5";
        /* 14 - 5 */
        checkRowCount(sql, 9.0, 0.0, 10.0);
    }

    @Test
    public void testRowCountSortLimitOffsetOnFinite() {
        final String sql = "select * from (select * from emp limit 12)\n" + "order by ename limit 20 offset 5";
        checkRowCount(sql, 7.0, 0.0, 7.0);
    }

    @Test
    public void testRowCountAggregate() {
        final String sql = "select deptno from emp group by deptno";
        checkRowCount(sql, 1.4, 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountAggregateGroupingSets() {
        final String sql = "select deptno from emp\n" + "group by grouping sets ((deptno), (ename, deptno))";
        // EMP_SIZE / 10 * 2
        checkRowCount(sql, 2.8, 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountAggregateGroupingSetsOneEmpty() {
        final String sql = "select deptno from emp\n" + "group by grouping sets ((deptno), ())";
        checkRowCount(sql, 2.8, 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testRowCountAggregateEmptyKey() {
        final String sql = "select count(*) from emp";
        checkRowCount(sql, 1.0, 1.0, 1.0);
    }

    @Test
    public void testRowCountFilterAggregateEmptyKey() {
        final String sql = "select count(*) from emp where 1 = 0";
        checkRowCount(sql, 1.0, 1.0, 1.0);
    }

    @Test
    public void testRowCountAggregateEmptyKeyOnEmptyTable() {
        final String sql = "select count(*) from (select * from emp limit 0)";
        checkRowCount(sql, 1.0, 1.0, 1.0);
    }

    @Test
    public void testSelectivityIsNotNullFilter() {
        checkFilterSelectivity("select * from emp where mgr is not null", RelMetadataTest.DEFAULT_NOTNULL_SELECTIVITY);
    }

    @Test
    public void testSelectivityIsNotNullFilterOnNotNullColumn() {
        checkFilterSelectivity("select * from emp where deptno is not null", 1.0);
    }

    @Test
    public void testSelectivityComparisonFilter() {
        checkFilterSelectivity("select * from emp where deptno > 10", RelMetadataTest.DEFAULT_COMP_SELECTIVITY);
    }

    @Test
    public void testSelectivityAndFilter() {
        checkFilterSelectivity("select * from emp where ename = 'foo' and deptno = 10", RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY_SQUARED);
    }

    @Test
    public void testSelectivityOrFilter() {
        checkFilterSelectivity("select * from emp where ename = 'foo' or deptno = 10", RelMetadataTest.DEFAULT_SELECTIVITY);
    }

    @Test
    public void testSelectivityJoin() {
        checkFilterSelectivity("select * from emp join dept using (deptno) where ename = 'foo'", RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY);
    }

    @Test
    public void testSelectivityRedundantFilter() {
        RelNode rel = convertSql("select * from emp where deptno = 10");
        checkRelSelectivity(rel, RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY);
    }

    @Test
    public void testSelectivitySort() {
        RelNode rel = convertSql(("select * from emp where deptno = 10" + "order by ename"));
        checkRelSelectivity(rel, RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY);
    }

    @Test
    public void testSelectivityUnion() {
        RelNode rel = convertSql(("select * from (\n" + ("  select * from emp union all select * from emp) " + "where deptno = 10")));
        checkRelSelectivity(rel, RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY);
    }

    @Test
    public void testSelectivityAgg() {
        RelNode rel = convertSql(("select deptno, count(*) from emp where deptno > 10 " + "group by deptno having count(*) = 0"));
        checkRelSelectivity(rel, ((RelMetadataTest.DEFAULT_COMP_SELECTIVITY) * (RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY)));
    }

    /**
     * Checks that we can cache a metadata request that includes a null
     * argument.
     */
    @Test
    public void testSelectivityAggCached() {
        RelNode rel = convertSql(("select deptno, count(*) from emp where deptno > 10 " + "group by deptno having count(*) = 0"));
        rel.getCluster().setMetadataProvider(new org.apache.calcite.rel.metadata.CachingRelMetadataProvider(rel.getCluster().getMetadataProvider(), rel.getCluster().getPlanner()));
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        Double result = mq.getSelectivity(rel, null);
        Assert.assertThat(result, Matchers.within(((RelMetadataTest.DEFAULT_COMP_SELECTIVITY) * (RelMetadataTest.DEFAULT_EQUAL_SELECTIVITY)), RelMetadataTest.EPSILON));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1808">[CALCITE-1808]
     * JaninoRelMetadataProvider loading cache might cause
     * OutOfMemoryError</a>.
     */
    @Test
    public void testMetadataHandlerCacheLimit() {
        Assume.assumeTrue("too slow to run every day, and it does not reproduce the issue", TEST_SLOW.value());
        Assume.assumeTrue(("If cache size is too large, this test may fail and the " + "test won't be to blame"), ((METADATA_HANDLER_CACHE_MAXIMUM_SIZE.value()) < 10000));
        final int iterationCount = 2000;
        final RelNode rel = convertSql("select * from emp");
        final RelMetadataProvider metadataProvider = rel.getCluster().getMetadataProvider();
        final RelOptPlanner planner = rel.getCluster().getPlanner();
        for (int i = 0; i < iterationCount; i++) {
            THREAD_PROVIDERS.set(JaninoRelMetadataProvider.of(new org.apache.calcite.rel.metadata.CachingRelMetadataProvider(metadataProvider, planner)));
            final RelMetadataQuery mq = RelMetadataQuery.instance();
            final Double result = mq.getRowCount(rel);
            Assert.assertThat(result, Matchers.within(14.0, 0.1));
        }
    }

    @Test
    public void testDistinctRowCountTable() {
        // no unique key information is available so return null
        RelNode rel = convertSql("select * from emp where deptno = 10");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        ImmutableBitSet groupKey = ImmutableBitSet.of(rel.getRowType().getFieldNames().indexOf("DEPTNO"));
        Double result = mq.getDistinctRowCount(rel, groupKey, null);
        Assert.assertThat(result, CoreMatchers.nullValue());
    }

    @Test
    public void testDistinctRowCountTableEmptyKey() {
        RelNode rel = convertSql("select * from emp where deptno = 10");
        ImmutableBitSet groupKey = ImmutableBitSet.of();// empty key

        final RelMetadataQuery mq = RelMetadataQuery.instance();
        Double result = mq.getDistinctRowCount(rel, groupKey, null);
        Assert.assertThat(result, CoreMatchers.is(1.0));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-509">[CALCITE-509]
     * "RelMdColumnUniqueness uses ImmutableBitSet.Builder twice, gets
     * NullPointerException"</a>.
     */
    @Test
    public void testJoinUniqueKeys() {
        RelNode rel = convertSql("select * from emp join bonus using (ename)");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        Set<ImmutableBitSet> result = mq.getUniqueKeys(rel);
        Assert.assertThat(result.isEmpty(), CoreMatchers.is(true));
        assertUniqueConsistent(rel);
    }

    @Test
    public void testCorrelateUniqueKeys() {
        final String sql = "select *\n" + (("from (select distinct deptno from emp) as e,\n" + "  lateral (\n") + "    select * from dept where dept.deptno = e.deptno)");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        Assert.assertThat(rel, CoreMatchers.isA(((Class) (Project.class))));
        final Project project = ((Project) (rel));
        final Set<ImmutableBitSet> result = mq.getUniqueKeys(project);
        Assert.assertThat(result, RelMetadataTest.sortsAs("[{0}]"));
        if (false) {
            assertUniqueConsistent(project);
        }
        Assert.assertThat(project.getInput(), CoreMatchers.isA(((Class) (Correlate.class))));
        final Correlate correlate = ((Correlate) (project.getInput()));
        final Set<ImmutableBitSet> result2 = mq.getUniqueKeys(correlate);
        Assert.assertThat(result2, RelMetadataTest.sortsAs("[{0}]"));
        if (false) {
            assertUniqueConsistent(correlate);
        }
    }

    @Test
    public void testGroupByEmptyUniqueKeys() {
        RelNode rel = convertSql("select count(*) from emp");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        Set<ImmutableBitSet> result = mq.getUniqueKeys(rel);
        Assert.assertThat(result, CoreMatchers.equalTo(ImmutableSet.of(ImmutableBitSet.of())));
        assertUniqueConsistent(rel);
    }

    @Test
    public void testGroupByEmptyHavingUniqueKeys() {
        RelNode rel = convertSql("select count(*) from emp where 1 = 1");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final Set<ImmutableBitSet> result = mq.getUniqueKeys(rel);
        Assert.assertThat(result, CoreMatchers.equalTo(ImmutableSet.of(ImmutableBitSet.of())));
        assertUniqueConsistent(rel);
    }

    @Test
    public void testGroupBy() {
        RelNode rel = convertSql(("select deptno, count(*), sum(sal) from emp\n" + "group by deptno"));
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final Set<ImmutableBitSet> result = mq.getUniqueKeys(rel);
        Assert.assertThat(result, CoreMatchers.equalTo(ImmutableSet.of(ImmutableBitSet.of(0))));
        assertUniqueConsistent(rel);
    }

    @Test
    public void testUnion() {
        RelNode rel = convertSql(("select deptno from emp\n" + ("union\n" + "select deptno from dept")));
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final Set<ImmutableBitSet> result = mq.getUniqueKeys(rel);
        Assert.assertThat(result, CoreMatchers.equalTo(ImmutableSet.of(ImmutableBitSet.of(0))));
        assertUniqueConsistent(rel);
    }

    @Test
    public void testBrokenCustomProvider() {
        final List<String> buf = new ArrayList<>();
        RelMetadataTest.ColTypeImpl.THREAD_LIST.set(buf);
        final String sql = "select deptno, count(*) from emp where deptno > 10 " + "group by deptno having count(*) = 0";
        final RelRoot root = tester.withClusterFactory(( cluster) -> {
            cluster.setMetadataProvider(ChainedRelMetadataProvider.of(ImmutableList.of(RelMetadataTest.BrokenColTypeImpl.SOURCE, cluster.getMetadataProvider())));
            return cluster;
        }).convertSqlToRel(sql);
        final RelNode rel = root.rel;
        Assert.assertThat(rel, CoreMatchers.instanceOf(LogicalFilter.class));
        final RelMetadataTest.MyRelMetadataQuery mq = new RelMetadataTest.MyRelMetadataQuery();
        try {
            Assert.assertThat(colType(mq, rel, 0), CoreMatchers.equalTo("DEPTNO-rel"));
            Assert.fail("expected error");
        } catch (IllegalArgumentException e) {
            final String value = "No handler for method [public abstract java.lang.String " + (("org.apache.calcite.test.RelMetadataTest$ColType.getColType(int)] " + "applied to argument of type [interface org.apache.calcite.rel.RelNode]; ") + "we recommend you create a catch-all (RelNode) handler");
            Assert.assertThat(e.getMessage(), CoreMatchers.is(value));
        }
    }

    @Test
    public void testCustomProvider() {
        final List<String> buf = new ArrayList<>();
        RelMetadataTest.ColTypeImpl.THREAD_LIST.set(buf);
        final String sql = "select deptno, count(*) from emp where deptno > 10 " + "group by deptno having count(*) = 0";
        final RelRoot root = tester.withClusterFactory(( cluster) -> {
            // Create a custom provider that includes ColType.
            // Include the same provider twice just to be devious.
            final ImmutableList<RelMetadataProvider> list = ImmutableList.of(RelMetadataTest.ColTypeImpl.SOURCE, RelMetadataTest.ColTypeImpl.SOURCE, cluster.getMetadataProvider());
            cluster.setMetadataProvider(ChainedRelMetadataProvider.of(list));
            return cluster;
        }).convertSqlToRel(sql);
        final RelNode rel = root.rel;
        // Top node is a filter. Its metadata uses getColType(RelNode, int).
        Assert.assertThat(rel, CoreMatchers.instanceOf(LogicalFilter.class));
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        Assert.assertThat(colType(mq, rel, 0), CoreMatchers.equalTo("DEPTNO-rel"));
        Assert.assertThat(colType(mq, rel, 1), CoreMatchers.equalTo("EXPR$1-rel"));
        // Next node is an aggregate. Its metadata uses
        // getColType(LogicalAggregate, int).
        final RelNode input = rel.getInput(0);
        Assert.assertThat(input, CoreMatchers.instanceOf(LogicalAggregate.class));
        Assert.assertThat(colType(mq, input, 0), CoreMatchers.equalTo("DEPTNO-agg"));
        // There is no caching. Another request causes another call to the provider.
        Assert.assertThat(buf.toString(), CoreMatchers.equalTo("[DEPTNO-rel, EXPR$1-rel, DEPTNO-agg]"));
        Assert.assertThat(buf.size(), CoreMatchers.equalTo(3));
        Assert.assertThat(colType(mq, input, 0), CoreMatchers.equalTo("DEPTNO-agg"));
        Assert.assertThat(buf.size(), CoreMatchers.equalTo(4));
        // Now add a cache. Only the first request for each piece of metadata
        // generates a new call to the provider.
        final RelOptPlanner planner = rel.getCluster().getPlanner();
        rel.getCluster().setMetadataProvider(new org.apache.calcite.rel.metadata.CachingRelMetadataProvider(rel.getCluster().getMetadataProvider(), planner));
        Assert.assertThat(colType(mq, input, 0), CoreMatchers.equalTo("DEPTNO-agg"));
        Assert.assertThat(buf.size(), CoreMatchers.equalTo(5));
        Assert.assertThat(colType(mq, input, 0), CoreMatchers.equalTo("DEPTNO-agg"));
        Assert.assertThat(buf.size(), CoreMatchers.equalTo(5));
        Assert.assertThat(colType(mq, input, 1), CoreMatchers.equalTo("EXPR$1-agg"));
        Assert.assertThat(buf.size(), CoreMatchers.equalTo(6));
        Assert.assertThat(colType(mq, input, 1), CoreMatchers.equalTo("EXPR$1-agg"));
        Assert.assertThat(buf.size(), CoreMatchers.equalTo(6));
        Assert.assertThat(colType(mq, input, 0), CoreMatchers.equalTo("DEPTNO-agg"));
        Assert.assertThat(buf.size(), CoreMatchers.equalTo(6));
        // With a different timestamp, a metadata item is re-computed on first call.
        long timestamp = planner.getRelMetadataTimestamp(rel);
        Assert.assertThat(timestamp, CoreMatchers.equalTo(0L));
        ((MockRelOptPlanner) (planner)).setRelMetadataTimestamp((timestamp + 1));
        Assert.assertThat(colType(mq, input, 0), CoreMatchers.equalTo("DEPTNO-agg"));
        Assert.assertThat(buf.size(), CoreMatchers.equalTo(7));
        Assert.assertThat(colType(mq, input, 0), CoreMatchers.equalTo("DEPTNO-agg"));
        Assert.assertThat(buf.size(), CoreMatchers.equalTo(7));
    }

    /**
     * Unit test for
     * {@link org.apache.calcite.rel.metadata.RelMdCollation#project}
     * and other helper functions for deducing collations.
     */
    @Test
    public void testCollation() {
        final Project rel = ((Project) (convertSql("select * from emp, dept")));
        final Join join = ((Join) (rel.getInput()));
        final RelOptTable empTable = join.getInput(0).getTable();
        final RelOptTable deptTable = join.getInput(1).getTable();
        Frameworks.withPlanner(( cluster, relOptSchema, rootSchema) -> {
            checkCollation(cluster, empTable, deptTable);
            return null;
        });
    }

    /**
     * Unit test for
     * {@link org.apache.calcite.rel.metadata.RelMdColumnUniqueness#areColumnsUnique}
     * applied to {@link Values}.
     */
    @Test
    public void testColumnUniquenessForValues() {
        Frameworks.withPlanner(( cluster, relOptSchema, rootSchema) -> {
            final RexBuilder rexBuilder = cluster.getRexBuilder();
            final RelMetadataQuery mq = RelMetadataQuery.instance();
            final RelDataType rowType = cluster.getTypeFactory().builder().add("a", SqlTypeName.INTEGER).add("b", SqlTypeName.VARCHAR).build();
            final ImmutableList.Builder<ImmutableList<RexLiteral>> tuples = ImmutableList.builder();
            addRow(tuples, rexBuilder, 1, "X");
            addRow(tuples, rexBuilder, 2, "Y");
            addRow(tuples, rexBuilder, 3, "X");
            addRow(tuples, rexBuilder, 4, "X");
            final LogicalValues values = LogicalValues.create(cluster, rowType, tuples.build());
            final ImmutableBitSet colNone = ImmutableBitSet.of();
            final ImmutableBitSet col0 = ImmutableBitSet.of(0);
            final ImmutableBitSet col1 = ImmutableBitSet.of(1);
            final ImmutableBitSet colAll = ImmutableBitSet.of(0, 1);
            assertThat(mq.areColumnsUnique(values, col0), is(true));
            assertThat(mq.areColumnsUnique(values, col1), is(false));
            assertThat(mq.areColumnsUnique(values, colAll), is(true));
            assertThat(mq.areColumnsUnique(values, colNone), is(false));
            // Repeat the above tests directly against the handler.
            final RelMdColumnUniqueness handler = ((RelMdColumnUniqueness) (RelMdColumnUniqueness.SOURCE.handlers(BuiltInMetadata.ColumnUniqueness.DEF).get(BuiltInMethod.COLUMN_UNIQUENESS.method).iterator().next()));
            assertThat(handler.areColumnsUnique(values, mq, col0, false), is(true));
            assertThat(handler.areColumnsUnique(values, mq, col1, false), is(false));
            assertThat(handler.areColumnsUnique(values, mq, colAll, false), is(true));
            assertThat(handler.areColumnsUnique(values, mq, colNone, false), is(false));
            return null;
        });
    }

    /**
     * Unit test for
     * {@link org.apache.calcite.rel.metadata.RelMetadataQuery#getAverageColumnSizes(org.apache.calcite.rel.RelNode)},
     * {@link org.apache.calcite.rel.metadata.RelMetadataQuery#getAverageRowSize(org.apache.calcite.rel.RelNode)}.
     */
    @Test
    public void testAverageRowSize() {
        final Project rel = ((Project) (convertSql("select * from emp, dept")));
        final Join join = ((Join) (rel.getInput()));
        final RelOptTable empTable = join.getInput(0).getTable();
        final RelOptTable deptTable = join.getInput(1).getTable();
        Frameworks.withPlanner(( cluster, relOptSchema, rootSchema) -> {
            checkAverageRowSize(cluster, empTable, deptTable);
            return null;
        });
    }

    /**
     * Unit test for
     * {@link org.apache.calcite.rel.metadata.RelMdPredicates#getPredicates(Join, RelMetadataQuery)}.
     */
    @Test
    public void testPredicates() {
        final Project rel = ((Project) (convertSql("select * from emp, dept")));
        final Join join = ((Join) (rel.getInput()));
        final RelOptTable empTable = join.getInput(0).getTable();
        final RelOptTable deptTable = join.getInput(1).getTable();
        Frameworks.withPlanner(( cluster, relOptSchema, rootSchema) -> {
            checkPredicates(cluster, empTable, deptTable);
            return null;
        });
    }

    /**
     * Unit test for
     * {@link org.apache.calcite.rel.metadata.RelMdPredicates#getPredicates(Aggregate, RelMetadataQuery)}.
     */
    @Test
    public void testPullUpPredicatesFromAggregation() {
        final String sql = "select a, max(b) from (\n" + ("  select 1 as a, 2 as b from emp)subq\n" + "group by a");
        final Aggregate rel = ((Aggregate) (convertSql(sql)));
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelOptPredicateList inputSet = mq.getPulledUpPredicates(rel);
        ImmutableList<RexNode> pulledUpPredicates = inputSet.pulledUpPredicates;
        Assert.assertThat(pulledUpPredicates, RelMetadataTest.sortsAs("[=($0, 1)]"));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1960">[CALCITE-1960]
     * RelMdPredicates.getPredicates is slow if there are many equivalent
     * columns</a>. There are much less duplicates after
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2205">[CALCITE-2205]</a>.
     * Since this is a performance problem, the test result does not
     * change, but takes over 15 minutes before the fix and 6 seconds after.
     */
    @Test(timeout = 20000)
    public void testPullUpPredicatesForExprsItr() {
        // If we're running Windows, we are probably in a VM and the test may
        // exceed timeout by a small margin.
        Assume.assumeThat("Too slow to run on Windows", File.separatorChar, Is.is('/'));
        final String sql = "select a.EMPNO, a.ENAME\n" + ((((((((((((("from (select * from sales.emp ) a\n" + "join (select * from sales.emp  ) b\n") + "on a.empno = b.deptno\n") + "  and a.comm = b.comm\n") + "  and a.mgr=b.mgr\n") + "  and (a.empno < 10 or a.comm < 3 or a.deptno < 10\n") + "    or a.job =\'abc\' or a.ename=\'abc\' or a.sal=\'30\' or a.mgr >3\n") + "    or a.slacker is not null  or a.HIREDATE is not null\n") + "    or b.empno < 9 or b.comm < 3 or b.deptno < 10 or b.job =\'abc\'\n") + "    or b.ename=\'abc\' or b.sal=\'30\' or b.mgr >3 or b.slacker )\n") + "join emp c\n") + "on b.mgr =a.mgr and a.empno =b.deptno and a.comm=b.comm\n") + "  and a.deptno=b.deptno and a.job=b.job and a.ename=b.ename\n") + "  and a.mgr=b.deptno and a.slacker=b.slacker");
        // Lock to ensure that only one test is using this method at a time.
        try (JdbcAdapterTest.LockWrapper ignore = lock(RelMetadataTest.LOCK)) {
            final RelNode rel = convertSql(sql);
            final RelMetadataQuery mq = RelMetadataQuery.instance();
            RelOptPredicateList inputSet = mq.getPulledUpPredicates(rel.getInput(0));
            Assert.assertThat(inputSet.pulledUpPredicates.size(), CoreMatchers.is(18));
        }
    }

    @Test
    public void testPullUpPredicatesOnConstant() {
        final String sql = "select deptno, mgr, x, \'y\' as y, z from (\n" + (("  select deptno, mgr, cast(null as integer) as x, cast(\'1\' as int) as z\n" + "  from emp\n") + "  where mgr is null and deptno < 10)");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelOptPredicateList list = mq.getPulledUpPredicates(rel);
        Assert.assertThat(list.pulledUpPredicates, RelMetadataTest.sortsAs("[<($0, 10), =($3, 'y'), =($4, 1), IS NULL($1), IS NULL($2)]"));
    }

    @Test
    public void testPullUpPredicatesOnNullableConstant() {
        final String sql = "select nullif(1, 1) as c\n" + ("  from emp\n" + "  where mgr is null and deptno < 10");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelOptPredicateList list = mq.getPulledUpPredicates(rel);
        // Uses "IS NOT DISTINCT FROM" rather than "=" because cannot guarantee not null.
        Assert.assertThat(list.pulledUpPredicates, RelMetadataTest.sortsAs("[IS NULL($0)]"));
    }

    @Test
    public void testDistributionSimple() {
        RelNode rel = convertSql("select * from emp where deptno = 10");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelDistribution d = mq.getDistribution(rel);
        Assert.assertThat(d, CoreMatchers.is(BROADCAST_DISTRIBUTED));
    }

    @Test
    public void testDistributionHash() {
        final RelNode rel = convertSql("select * from emp");
        final RelDistribution dist = RelDistributions.hash(ImmutableList.of(1));
        final LogicalExchange exchange = LogicalExchange.create(rel, dist);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelDistribution d = mq.getDistribution(exchange);
        Assert.assertThat(d, CoreMatchers.is(dist));
    }

    @Test
    public void testDistributionHashEmpty() {
        final RelNode rel = convertSql("select * from emp");
        final RelDistribution dist = RelDistributions.hash(ImmutableList.<Integer>of());
        final LogicalExchange exchange = LogicalExchange.create(rel, dist);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelDistribution d = mq.getDistribution(exchange);
        Assert.assertThat(d, CoreMatchers.is(dist));
    }

    @Test
    public void testDistributionSingleton() {
        final RelNode rel = convertSql("select * from emp");
        final RelDistribution dist = RelDistributions.SINGLETON;
        final LogicalExchange exchange = LogicalExchange.create(rel, dist);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelDistribution d = mq.getDistribution(exchange);
        Assert.assertThat(d, CoreMatchers.is(dist));
    }

    /**
     * Unit test for {@link RelMdUtil#linear(int, int, int, double, double)}.
     */
    @Test
    public void testLinear() {
        Assert.assertThat(RelMdUtil.linear(0, 0, 10, 100, 200), CoreMatchers.is(100.0));
        Assert.assertThat(RelMdUtil.linear(5, 0, 10, 100, 200), CoreMatchers.is(150.0));
        Assert.assertThat(RelMdUtil.linear(6, 0, 10, 100, 200), CoreMatchers.is(160.0));
        Assert.assertThat(RelMdUtil.linear(10, 0, 10, 100, 200), CoreMatchers.is(200.0));
        Assert.assertThat(RelMdUtil.linear((-2), 0, 10, 100, 200), CoreMatchers.is(100.0));
        Assert.assertThat(RelMdUtil.linear(12, 0, 10, 100, 200), CoreMatchers.is(200.0));
    }

    @Test
    public void testExpressionLineageStar() {
        // All columns in output
        final RelNode tableRel = convertSql("select * from emp");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(4, tableRel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(tableRel, ref);
        final String inputRef = RexInputRef.of(4, tableRel.getRowType().getFieldList()).toString();
        Assert.assertThat(r.size(), CoreMatchers.is(1));
        final String resultString = r.iterator().next().toString();
        Assert.assertThat(resultString, CoreMatchers.startsWith(RelMetadataTest.EMP_QNAME.toString()));
        Assert.assertThat(resultString, CoreMatchers.endsWith(inputRef));
    }

    @Test
    public void testExpressionLineageTwoColumns() {
        // mgr is column 3 in catalog.sales.emp
        // deptno is column 7 in catalog.sales.emp
        final RelNode rel = convertSql("select mgr, deptno from emp");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref1 = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r1 = mq.getExpressionLineage(rel, ref1);
        Assert.assertThat(r1.size(), CoreMatchers.is(1));
        final RexTableInputRef result1 = ((RexTableInputRef) (r1.iterator().next()));
        Assert.assertEquals(result1.getQualifiedName(), RelMetadataTest.EMP_QNAME);
        Assert.assertThat(result1.getIndex(), CoreMatchers.is(3));
        final RexNode ref2 = RexInputRef.of(1, rel.getRowType().getFieldList());
        final Set<RexNode> r2 = mq.getExpressionLineage(rel, ref2);
        Assert.assertThat(r2.size(), CoreMatchers.is(1));
        final RexTableInputRef result2 = ((RexTableInputRef) (r2.iterator().next()));
        Assert.assertEquals(result2.getQualifiedName(), RelMetadataTest.EMP_QNAME);
        Assert.assertThat(result2.getIndex(), CoreMatchers.is(7));
        Assert.assertThat(result1.getIdentifier(), CoreMatchers.is(result2.getIdentifier()));
    }

    @Test
    public void testExpressionLineageTwoColumnsSwapped() {
        // deptno is column 7 in catalog.sales.emp
        // mgr is column 3 in catalog.sales.emp
        final RelNode rel = convertSql("select deptno, mgr from emp");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref1 = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r1 = mq.getExpressionLineage(rel, ref1);
        Assert.assertThat(r1.size(), CoreMatchers.is(1));
        final RexTableInputRef result1 = ((RexTableInputRef) (r1.iterator().next()));
        Assert.assertEquals(result1.getQualifiedName(), RelMetadataTest.EMP_QNAME);
        Assert.assertThat(result1.getIndex(), CoreMatchers.is(7));
        final RexNode ref2 = RexInputRef.of(1, rel.getRowType().getFieldList());
        final Set<RexNode> r2 = mq.getExpressionLineage(rel, ref2);
        Assert.assertThat(r2.size(), CoreMatchers.is(1));
        final RexTableInputRef result2 = ((RexTableInputRef) (r2.iterator().next()));
        Assert.assertEquals(result2.getQualifiedName(), RelMetadataTest.EMP_QNAME);
        Assert.assertThat(result2.getIndex(), CoreMatchers.is(3));
        Assert.assertThat(result1.getIdentifier(), CoreMatchers.is(result2.getIdentifier()));
    }

    @Test
    public void testExpressionLineageCombineTwoColumns() {
        // empno is column 0 in catalog.sales.emp
        // deptno is column 7 in catalog.sales.emp
        final RelNode rel = convertSql("select empno + deptno from emp");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        Assert.assertThat(r.size(), CoreMatchers.is(1));
        final RexNode result = r.iterator().next();
        Assert.assertThat(result.getKind(), CoreMatchers.is(PLUS));
        final RexCall call = ((RexCall) (result));
        Assert.assertThat(call.getOperands().size(), CoreMatchers.is(2));
        final RexTableInputRef inputRef1 = ((RexTableInputRef) (call.getOperands().get(0)));
        Assert.assertEquals(inputRef1.getQualifiedName(), RelMetadataTest.EMP_QNAME);
        Assert.assertThat(inputRef1.getIndex(), CoreMatchers.is(0));
        final RexTableInputRef inputRef2 = ((RexTableInputRef) (call.getOperands().get(1)));
        Assert.assertEquals(inputRef2.getQualifiedName(), RelMetadataTest.EMP_QNAME);
        Assert.assertThat(inputRef2.getIndex(), CoreMatchers.is(7));
        Assert.assertThat(inputRef1.getIdentifier(), CoreMatchers.is(inputRef2.getIdentifier()));
    }

    @Test
    public void testExpressionLineageInnerJoinLeft() {
        // ename is column 1 in catalog.sales.emp
        final RelNode rel = convertSql("select ename from emp,dept");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        Assert.assertThat(r.size(), CoreMatchers.is(1));
        final RexTableInputRef result = ((RexTableInputRef) (r.iterator().next()));
        Assert.assertEquals(result.getQualifiedName(), RelMetadataTest.EMP_QNAME);
        Assert.assertThat(result.getIndex(), CoreMatchers.is(1));
    }

    @Test
    public void testExpressionLineageInnerJoinRight() {
        // ename is column 0 in catalog.sales.bonus
        final RelNode rel = convertSql("select bonus.ename from emp join bonus using (ename)");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        Assert.assertThat(r.size(), CoreMatchers.is(1));
        final RexTableInputRef result = ((RexTableInputRef) (r.iterator().next()));
        Assert.assertEquals(result.getQualifiedName(), ImmutableList.of("CATALOG", "SALES", "BONUS"));
        Assert.assertThat(result.getIndex(), CoreMatchers.is(0));
    }

    @Test
    public void testExpressionLineageLeftJoinLeft() {
        // ename is column 1 in catalog.sales.emp
        final RelNode rel = convertSql("select ename from emp left join dept using (deptno)");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        Assert.assertThat(r.size(), CoreMatchers.is(1));
        final RexTableInputRef result = ((RexTableInputRef) (r.iterator().next()));
        Assert.assertEquals(result.getQualifiedName(), RelMetadataTest.EMP_QNAME);
        Assert.assertThat(result.getIndex(), CoreMatchers.is(1));
    }

    @Test
    public void testExpressionLineageRightJoinRight() {
        // ename is column 0 in catalog.sales.bonus
        final RelNode rel = convertSql("select bonus.ename from emp right join bonus using (ename)");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        Assert.assertThat(r.size(), CoreMatchers.is(1));
        final RexTableInputRef result = ((RexTableInputRef) (r.iterator().next()));
        Assert.assertEquals(result.getQualifiedName(), ImmutableList.of("CATALOG", "SALES", "BONUS"));
        Assert.assertThat(result.getIndex(), CoreMatchers.is(0));
    }

    @Test
    public void testExpressionLineageSelfJoin() {
        // deptno is column 7 in catalog.sales.emp
        // sal is column 5 in catalog.sales.emp
        final RelNode rel = convertSql(("select a.deptno, b.sal from (select * from emp limit 7) as a\n" + ("inner join (select * from emp limit 2) as b\n" + "on a.deptno = b.deptno")));
        final RelNode tableRel = convertSql("select * from emp");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref1 = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r1 = mq.getExpressionLineage(rel, ref1);
        final String inputRef1 = RexInputRef.of(7, tableRel.getRowType().getFieldList()).toString();
        Assert.assertThat(r1.size(), CoreMatchers.is(1));
        final String resultString1 = r1.iterator().next().toString();
        Assert.assertThat(resultString1, CoreMatchers.startsWith(RelMetadataTest.EMP_QNAME.toString()));
        Assert.assertThat(resultString1, CoreMatchers.endsWith(inputRef1));
        final RexNode ref2 = RexInputRef.of(1, rel.getRowType().getFieldList());
        final Set<RexNode> r2 = mq.getExpressionLineage(rel, ref2);
        final String inputRef2 = RexInputRef.of(5, tableRel.getRowType().getFieldList()).toString();
        Assert.assertThat(r2.size(), CoreMatchers.is(1));
        final String resultString2 = r2.iterator().next().toString();
        Assert.assertThat(resultString2, CoreMatchers.startsWith(RelMetadataTest.EMP_QNAME.toString()));
        Assert.assertThat(resultString2, CoreMatchers.endsWith(inputRef2));
        Assert.assertThat(getIdentifier(), CoreMatchers.not(getIdentifier()));
    }

    @Test
    public void testExpressionLineageOuterJoin() {
        // lineage cannot be determined
        final RelNode rel = convertSql(("select name as dname from emp left outer join dept" + " on emp.deptno = dept.deptno"));
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        Assert.assertNull(r);
    }

    @Test
    public void testExpressionLineageFilter() {
        // ename is column 1 in catalog.sales.emp
        final RelNode rel = convertSql("select ename from emp where deptno = 10");
        final RelNode tableRel = convertSql("select * from emp");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        final String inputRef = RexInputRef.of(1, tableRel.getRowType().getFieldList()).toString();
        Assert.assertThat(r.size(), CoreMatchers.is(1));
        final String resultString = r.iterator().next().toString();
        Assert.assertThat(resultString, CoreMatchers.startsWith(RelMetadataTest.EMP_QNAME.toString()));
        Assert.assertThat(resultString, CoreMatchers.endsWith(inputRef));
    }

    @Test
    public void testExpressionLineageAggregateGroupColumn() {
        // deptno is column 7 in catalog.sales.emp
        final RelNode rel = convertSql(("select deptno, count(*) from emp where deptno > 10 " + "group by deptno having count(*) = 0"));
        final RelNode tableRel = convertSql("select * from emp");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        final String inputRef = RexInputRef.of(7, tableRel.getRowType().getFieldList()).toString();
        Assert.assertThat(r.size(), CoreMatchers.is(1));
        final String resultString = r.iterator().next().toString();
        Assert.assertThat(resultString, CoreMatchers.startsWith(RelMetadataTest.EMP_QNAME.toString()));
        Assert.assertThat(resultString, CoreMatchers.endsWith(inputRef));
    }

    @Test
    public void testExpressionLineageAggregateAggColumn() {
        // lineage cannot be determined
        final RelNode rel = convertSql(("select deptno, count(*) from emp where deptno > 10 " + "group by deptno having count(*) = 0"));
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(1, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        Assert.assertNull(r);
    }

    @Test
    public void testExpressionLineageUnion() {
        // sal is column 5 in catalog.sales.emp
        final RelNode rel = convertSql(("select sal from (\n" + ("  select * from emp union all select * from emp) " + "where deptno = 10")));
        final RelNode tableRel = convertSql("select * from emp");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        final String inputRef = RexInputRef.of(5, tableRel.getRowType().getFieldList()).toString();
        Assert.assertThat(r.size(), CoreMatchers.is(2));
        for (RexNode result : r) {
            final String resultString = result.toString();
            Assert.assertThat(resultString, CoreMatchers.startsWith(RelMetadataTest.EMP_QNAME.toString()));
            Assert.assertThat(resultString, CoreMatchers.endsWith(inputRef));
        }
        Iterator<RexNode> it = r.iterator();
        Assert.assertThat(getIdentifier(), CoreMatchers.not(getIdentifier()));
    }

    @Test
    public void testExpressionLineageMultiUnion() {
        // empno is column 0 in catalog.sales.emp
        // sal is column 5 in catalog.sales.emp
        final RelNode rel = convertSql(("select a.empno + b.sal from \n" + (((" (select empno, ename from emp,dept) a join " + " (select * from emp union all select * from emp) b \n") + " on a.empno = b.empno \n") + " where b.deptno = 10")));
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        // With the union, we should get two origins
        // The first one should be the same one: join
        // The second should come from each union input
        final Set<List<String>> set = new HashSet<>();
        Assert.assertThat(r.size(), CoreMatchers.is(2));
        for (RexNode result : r) {
            Assert.assertThat(result.getKind(), CoreMatchers.is(PLUS));
            final RexCall call = ((RexCall) (result));
            Assert.assertThat(call.getOperands().size(), CoreMatchers.is(2));
            final RexTableInputRef inputRef1 = ((RexTableInputRef) (call.getOperands().get(0)));
            Assert.assertEquals(inputRef1.getQualifiedName(), RelMetadataTest.EMP_QNAME);
            // Add join alpha to set
            set.add(inputRef1.getQualifiedName());
            Assert.assertThat(inputRef1.getIndex(), CoreMatchers.is(0));
            final RexTableInputRef inputRef2 = ((RexTableInputRef) (call.getOperands().get(1)));
            Assert.assertEquals(inputRef2.getQualifiedName(), RelMetadataTest.EMP_QNAME);
            Assert.assertThat(inputRef2.getIndex(), CoreMatchers.is(5));
            Assert.assertThat(inputRef1.getIdentifier(), CoreMatchers.not(inputRef2.getIdentifier()));
        }
        Assert.assertThat(set.size(), CoreMatchers.is(1));
    }

    @Test
    public void testExpressionLineageValues() {
        // lineage cannot be determined
        final RelNode rel = convertSql("select * from (values (1), (2)) as t(c)");
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RexNode ref = RexInputRef.of(0, rel.getRowType().getFieldList());
        final Set<RexNode> r = mq.getExpressionLineage(rel, ref);
        Assert.assertNull(r);
    }

    @Test
    public void testAllPredicates() {
        final Project rel = ((Project) (convertSql("select * from emp, dept")));
        final Join join = ((Join) (rel.getInput()));
        final RelOptTable empTable = join.getInput(0).getTable();
        final RelOptTable deptTable = join.getInput(1).getTable();
        Frameworks.withPlanner(( cluster, relOptSchema, rootSchema) -> {
            checkAllPredicates(cluster, empTable, deptTable);
            return null;
        });
    }

    @Test
    public void testAllPredicatesAggregate1() {
        final String sql = "select a, max(b) from (\n" + ("  select empno as a, sal as b from emp where empno = 5)subq\n" + "group by a");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelOptPredicateList inputSet = mq.getAllPredicates(rel);
        ImmutableList<RexNode> pulledUpPredicates = inputSet.pulledUpPredicates;
        Assert.assertThat(pulledUpPredicates.size(), CoreMatchers.is(1));
        RexCall call = ((RexCall) (pulledUpPredicates.get(0)));
        Assert.assertThat(call.getOperands().size(), CoreMatchers.is(2));
        final RexTableInputRef inputRef1 = ((RexTableInputRef) (call.getOperands().get(0)));
        Assert.assertTrue(inputRef1.getQualifiedName().equals(RelMetadataTest.EMP_QNAME));
        Assert.assertThat(inputRef1.getIndex(), CoreMatchers.is(0));
        final RexLiteral constant = ((RexLiteral) (call.getOperands().get(1)));
        Assert.assertThat(constant.toString(), CoreMatchers.is("5"));
    }

    @Test
    public void testAllPredicatesAggregate2() {
        final String sql = "select * from (select a, max(b) from (\n" + (("  select empno as a, sal as b from emp)subq\n" + "group by a) \n") + "where a = 5");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelOptPredicateList inputSet = mq.getAllPredicates(rel);
        ImmutableList<RexNode> pulledUpPredicates = inputSet.pulledUpPredicates;
        Assert.assertThat(pulledUpPredicates.size(), CoreMatchers.is(1));
        RexCall call = ((RexCall) (pulledUpPredicates.get(0)));
        Assert.assertThat(call.getOperands().size(), CoreMatchers.is(2));
        final RexTableInputRef inputRef1 = ((RexTableInputRef) (call.getOperands().get(0)));
        Assert.assertTrue(inputRef1.getQualifiedName().equals(RelMetadataTest.EMP_QNAME));
        Assert.assertThat(inputRef1.getIndex(), CoreMatchers.is(0));
        final RexLiteral constant = ((RexLiteral) (call.getOperands().get(1)));
        Assert.assertThat(constant.toString(), CoreMatchers.is("5"));
    }

    @Test
    public void testAllPredicatesAggregate3() {
        final String sql = "select * from (select a, max(b) as b from (\n" + (("  select empno as a, sal as b from emp)subq\n" + "group by a) \n") + "where b = 5");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        RelOptPredicateList inputSet = mq.getAllPredicates(rel);
        // Filter on aggregate, we cannot infer lineage
        Assert.assertNull(inputSet);
    }

    @Test
    public void testAllPredicatesAndTablesJoin() {
        final String sql = "select x.sal, y.deptno from\n" + ((((((((("(select a.deptno, c.sal from (select * from emp limit 7) as a\n" + "cross join (select * from dept limit 1) as b\n") + "inner join (select * from emp limit 2) as c\n") + "on a.deptno = c.deptno) as x\n") + "inner join\n") + "(select a.deptno, c.sal from (select * from emp limit 7) as a\n") + "cross join (select * from dept limit 1) as b\n") + "inner join (select * from emp limit 2) as c\n") + "on a.deptno = c.deptno) as y\n") + "on x.deptno = y.deptno");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RelOptPredicateList inputSet = mq.getAllPredicates(rel);
        Assert.assertThat(inputSet.pulledUpPredicates.toString(), CoreMatchers.equalTo(("[true, " + ((("=([CATALOG, SALES, EMP].#0.$7, [CATALOG, SALES, EMP].#1.$7), " + "true, ") + "=([CATALOG, SALES, EMP].#2.$7, [CATALOG, SALES, EMP].#3.$7), ") + "=([CATALOG, SALES, EMP].#0.$7, [CATALOG, SALES, EMP].#2.$7)]"))));
        final Set<RelTableRef> tableReferences = Sets.newTreeSet(mq.getTableReferences(rel));
        Assert.assertThat(tableReferences.toString(), CoreMatchers.equalTo(("[[CATALOG, SALES, DEPT].#0, [CATALOG, SALES, DEPT].#1, " + ("[CATALOG, SALES, EMP].#0, [CATALOG, SALES, EMP].#1, " + "[CATALOG, SALES, EMP].#2, [CATALOG, SALES, EMP].#3]"))));
    }

    @Test
    public void testAllPredicatesAndTableUnion() {
        final String sql = "select a.deptno, c.sal from (select * from emp limit 7) as a\n" + ((((((("cross join (select * from dept limit 1) as b\n" + "inner join (select * from emp limit 2) as c\n") + "on a.deptno = c.deptno\n") + "union all\n") + "select a.deptno, c.sal from (select * from emp limit 7) as a\n") + "cross join (select * from dept limit 1) as b\n") + "inner join (select * from emp limit 2) as c\n") + "on a.deptno = c.deptno");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final RelOptPredicateList inputSet = mq.getAllPredicates(rel);
        Assert.assertThat(inputSet.pulledUpPredicates.toString(), CoreMatchers.equalTo(("[true, " + (("=([CATALOG, SALES, EMP].#0.$7, [CATALOG, SALES, EMP].#1.$7), " + "true, ") + "=([CATALOG, SALES, EMP].#2.$7, [CATALOG, SALES, EMP].#3.$7)]"))));
        final Set<RelTableRef> tableReferences = Sets.newTreeSet(mq.getTableReferences(rel));
        Assert.assertThat(tableReferences.toString(), CoreMatchers.equalTo(("[[CATALOG, SALES, DEPT].#0, [CATALOG, SALES, DEPT].#1, " + ("[CATALOG, SALES, EMP].#0, [CATALOG, SALES, EMP].#1, " + "[CATALOG, SALES, EMP].#2, [CATALOG, SALES, EMP].#3]"))));
    }

    @Test
    public void testAllPredicatesCrossJoinMultiTable() {
        final String sql = "select x.sal from\n" + (("(select a.deptno, c.sal from (select * from emp limit 7) as a\n" + "cross join (select * from dept limit 1) as b\n") + "cross join (select * from emp where empno = 5 limit 2) as c) as x");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final Set<RelTableRef> tableReferences = Sets.newTreeSet(mq.getTableReferences(rel));
        Assert.assertThat(tableReferences.toString(), CoreMatchers.equalTo(("[[CATALOG, SALES, DEPT].#0, " + ("[CATALOG, SALES, EMP].#0, " + "[CATALOG, SALES, EMP].#1]"))));
        final RelOptPredicateList inputSet = mq.getAllPredicates(rel);
        // Note that we reference [CATALOG, SALES, EMP].#1 rather than [CATALOG, SALES, EMP].#0
        Assert.assertThat(inputSet.pulledUpPredicates.toString(), CoreMatchers.equalTo("[true, =([CATALOG, SALES, EMP].#1.$0, 5), true]"));
    }

    @Test
    public void testTableReferencesJoinUnknownNode() {
        final String sql = "select * from emp limit 10";
        final RelNode node = convertSql(sql);
        final RelNode nodeWithUnknown = new RelMetadataTest.DummyRelNode(node.getCluster(), node.getTraitSet(), node);
        final RexBuilder rexBuilder = node.getCluster().getRexBuilder();
        // Join
        final LogicalJoin join = LogicalJoin.create(nodeWithUnknown, node, rexBuilder.makeLiteral(true), ImmutableSet.of(), INNER);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final Set<RelTableRef> tableReferences = mq.getTableReferences(join);
        Assert.assertNull(tableReferences);
    }

    @Test
    public void testAllPredicatesUnionMultiTable() {
        final String sql = "select x.sal from\n" + (("(select a.deptno, a.sal from (select * from emp) as a\n" + "union all select emp.deptno, emp.sal from emp\n") + "union all select emp.deptno, emp.sal from emp where empno = 5) as x");
        final RelNode rel = convertSql(sql);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final Set<RelTableRef> tableReferences = Sets.newTreeSet(mq.getTableReferences(rel));
        Assert.assertThat(tableReferences.toString(), CoreMatchers.equalTo(("[[CATALOG, SALES, EMP].#0, " + ("[CATALOG, SALES, EMP].#1, " + "[CATALOG, SALES, EMP].#2]"))));
        // Note that we reference [CATALOG, SALES, EMP].#2 rather than
        // [CATALOG, SALES, EMP].#0 or [CATALOG, SALES, EMP].#1
        final RelOptPredicateList inputSet = mq.getAllPredicates(rel);
        Assert.assertThat(inputSet.pulledUpPredicates.toString(), CoreMatchers.equalTo("[=([CATALOG, SALES, EMP].#2.$0, 5)]"));
    }

    @Test
    public void testTableReferencesUnionUnknownNode() {
        final String sql = "select * from emp limit 10";
        final RelNode node = convertSql(sql);
        final RelNode nodeWithUnknown = new RelMetadataTest.DummyRelNode(node.getCluster(), node.getTraitSet(), node);
        // Union
        final LogicalUnion union = LogicalUnion.create(ImmutableList.of(nodeWithUnknown, node), true);
        final RelMetadataQuery mq = RelMetadataQuery.instance();
        final Set<RelTableRef> tableReferences = mq.getTableReferences(union);
        Assert.assertNull(tableReferences);
    }

    @Test
    public void testNodeTypeCountEmp() {
        final String sql = "select * from emp";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountDept() {
        final String sql = "select * from dept";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountValues() {
        final String sql = "select * from (values (1), (2)) as t(c)";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(Values.class, 1);
        expected.put(Project.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountCartesian() {
        final String sql = "select * from emp,dept";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Join.class, 1);
        expected.put(Project.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountJoin() {
        final String sql = "select * from emp\n" + "inner join dept on emp.deptno = dept.deptno";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Join.class, 1);
        expected.put(Project.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountJoinFinite() {
        final String sql = "select * from (select * from emp limit 14) as emp\n" + ("inner join (select * from dept limit 4) as dept\n" + "on emp.deptno = dept.deptno");
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Join.class, 1);
        expected.put(Project.class, 3);
        expected.put(Sort.class, 2);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountJoinEmptyFinite() {
        final String sql = "select * from (select * from emp limit 0) as emp\n" + ("inner join (select * from dept limit 4) as dept\n" + "on emp.deptno = dept.deptno");
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Join.class, 1);
        expected.put(Project.class, 3);
        expected.put(Sort.class, 2);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountLeftJoinEmptyFinite() {
        final String sql = "select * from (select * from emp limit 0) as emp\n" + ("left join (select * from dept limit 4) as dept\n" + "on emp.deptno = dept.deptno");
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Join.class, 1);
        expected.put(Project.class, 3);
        expected.put(Sort.class, 2);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountRightJoinEmptyFinite() {
        final String sql = "select * from (select * from emp limit 0) as emp\n" + ("right join (select * from dept limit 4) as dept\n" + "on emp.deptno = dept.deptno");
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Join.class, 1);
        expected.put(Project.class, 3);
        expected.put(Sort.class, 2);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountJoinFiniteEmpty() {
        final String sql = "select * from (select * from emp limit 7) as emp\n" + ("inner join (select * from dept limit 0) as dept\n" + "on emp.deptno = dept.deptno");
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Join.class, 1);
        expected.put(Project.class, 3);
        expected.put(Sort.class, 2);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountJoinEmptyEmpty() {
        final String sql = "select * from (select * from emp limit 0) as emp\n" + ("inner join (select * from dept limit 0) as dept\n" + "on emp.deptno = dept.deptno");
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Join.class, 1);
        expected.put(Project.class, 3);
        expected.put(Sort.class, 2);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountUnion() {
        final String sql = "select ename from emp\n" + ("union all\n" + "select name from dept");
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Project.class, 2);
        expected.put(Union.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountUnionOnFinite() {
        final String sql = "select ename from (select * from emp limit 100)\n" + ("union all\n" + "select name from (select * from dept limit 40)");
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Union.class, 1);
        expected.put(Project.class, 4);
        expected.put(Sort.class, 2);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountMinusOnFinite() {
        final String sql = "select ename from (select * from emp limit 100)\n" + ("except\n" + "select name from (select * from dept limit 40)");
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 2);
        expected.put(Minus.class, 1);
        expected.put(Project.class, 4);
        expected.put(Sort.class, 2);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountFilter() {
        final String sql = "select * from emp where ename='Mathilda'";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 1);
        expected.put(Filter.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountSort() {
        final String sql = "select * from emp order by ename";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 1);
        expected.put(Sort.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountSortLimit() {
        final String sql = "select * from emp order by ename limit 10";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 1);
        expected.put(Sort.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountSortLimitOffset() {
        final String sql = "select * from emp order by ename limit 10 offset 5";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 1);
        expected.put(Sort.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountSortLimitOffsetOnFinite() {
        final String sql = "select * from (select * from emp limit 12)\n" + "order by ename limit 20 offset 5";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 2);
        expected.put(Sort.class, 2);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountAggregate() {
        final String sql = "select deptno from emp group by deptno";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 1);
        expected.put(Aggregate.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountAggregateGroupingSets() {
        final String sql = "select deptno from emp\n" + "group by grouping sets ((deptno), (ename, deptno))";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 2);
        expected.put(Aggregate.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountAggregateEmptyKeyOnEmptyTable() {
        final String sql = "select count(*) from (select * from emp limit 0)";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 2);
        expected.put(Aggregate.class, 1);
        expected.put(Sort.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    @Test
    public void testNodeTypeCountFilterAggregateEmptyKey() {
        final String sql = "select count(*) from emp where 1 = 0";
        final Map<Class<? extends RelNode>, Integer> expected = new HashMap<>();
        expected.put(TableScan.class, 1);
        expected.put(Project.class, 1);
        expected.put(Filter.class, 1);
        expected.put(Aggregate.class, 1);
        checkNodeTypeCount(sql, expected);
    }

    private static final SqlOperator NONDETERMINISTIC_OP = new SqlSpecialOperator("NDC", SqlKind.OTHER_FUNCTION, 0, false, ReturnTypes.BOOLEAN, null, null) {
        @Override
        public boolean isDeterministic() {
            return false;
        }
    };

    /**
     * Tests calling {@link RelMetadataQuery#getTableOrigin} for
     * an aggregate with no columns. Previously threw.
     */
    @Test
    public void testEmptyAggregateTableOrigin() {
        final FrameworkConfig config = RelBuilderTest.config().build();
        final RelBuilder builder = RelBuilder.create(config);
        RelMetadataQuery mq = RelMetadataQuery.instance();
        RelNode agg = builder.scan("EMP").aggregate(builder.groupKey()).build();
        final RelOptTable tableOrigin = mq.getTableOrigin(agg);
        Assert.assertThat(tableOrigin, CoreMatchers.nullValue());
    }

    @Test
    public void testGetPredicatesForJoin() throws Exception {
        final FrameworkConfig config = RelBuilderTest.config().build();
        final RelBuilder builder = RelBuilder.create(config);
        RelNode join = builder.scan("EMP").scan("DEPT").join(INNER, builder.call(RelMetadataTest.NONDETERMINISTIC_OP)).build();
        RelMetadataQuery mq = RelMetadataQuery.instance();
        Assert.assertTrue(mq.getPulledUpPredicates(join).pulledUpPredicates.isEmpty());
        RelNode join1 = builder.scan("EMP").scan("DEPT").join(INNER, builder.call(SqlStdOperatorTable.EQUALS, builder.field(2, 0, 0), builder.field(2, 1, 0))).build();
        Assert.assertEquals("=($0, $8)", mq.getPulledUpPredicates(join1).pulledUpPredicates.get(0).toString());
    }

    @Test
    public void testGetPredicatesForFilter() throws Exception {
        final FrameworkConfig config = RelBuilderTest.config().build();
        final RelBuilder builder = RelBuilder.create(config);
        RelNode filter = builder.scan("EMP").filter(builder.call(RelMetadataTest.NONDETERMINISTIC_OP)).build();
        RelMetadataQuery mq = RelMetadataQuery.instance();
        Assert.assertTrue(mq.getPulledUpPredicates(filter).pulledUpPredicates.isEmpty());
        RelNode filter1 = builder.scan("EMP").filter(builder.call(SqlStdOperatorTable.EQUALS, builder.field(1, 0, 0), builder.field(1, 0, 1))).build();
        Assert.assertEquals("=($0, $1)", mq.getPulledUpPredicates(filter1).pulledUpPredicates.get(0).toString());
    }

    /**
     * Custom metadata interface.
     */
    public interface ColType extends Metadata {
        Method METHOD = Types.lookupMethod(RelMetadataTest.ColType.class, "getColType", int.class);

        MetadataDef<RelMetadataTest.ColType> DEF = MetadataDef.of(RelMetadataTest.ColType.class, RelMetadataTest.ColType.Handler.class, RelMetadataTest.ColType.METHOD);

        String getColType(int column);

        /**
         * Handler API.
         */
        interface Handler extends MetadataHandler<RelMetadataTest.ColType> {
            String getColType(RelNode r, RelMetadataQuery mq, int column);
        }
    }

    /**
     * A provider for {@link org.apache.calcite.test.RelMetadataTest.ColType} via
     * reflection.
     */
    public abstract static class PartialColTypeImpl implements MetadataHandler<RelMetadataTest.ColType> {
        static final ThreadLocal<List<String>> THREAD_LIST = new ThreadLocal<>();

        public MetadataDef<RelMetadataTest.ColType> getDef() {
            return RelMetadataTest.ColType.DEF;
        }

        /**
         * Implementation of {@link ColType#getColType(int)} for
         * {@link org.apache.calcite.rel.logical.LogicalAggregate}, called via
         * reflection.
         */
        @SuppressWarnings("UnusedDeclaration")
        public String getColType(Aggregate rel, RelMetadataQuery mq, int column) {
            final String name = (rel.getRowType().getFieldList().get(column).getName()) + "-agg";
            RelMetadataTest.PartialColTypeImpl.THREAD_LIST.get().add(name);
            return name;
        }
    }

    /**
     * A provider for {@link org.apache.calcite.test.RelMetadataTest.ColType} via
     * reflection.
     */
    public static class ColTypeImpl extends RelMetadataTest.PartialColTypeImpl {
        public static final RelMetadataProvider SOURCE = ReflectiveRelMetadataProvider.reflectiveSource(RelMetadataTest.ColType.METHOD, new RelMetadataTest.ColTypeImpl());

        /**
         * Implementation of {@link ColType#getColType(int)} for
         * {@link RelNode}, called via reflection.
         */
        @SuppressWarnings("UnusedDeclaration")
        public String getColType(RelNode rel, RelMetadataQuery mq, int column) {
            final String name = (rel.getRowType().getFieldList().get(column).getName()) + "-rel";
            RelMetadataTest.PartialColTypeImpl.THREAD_LIST.get().add(name);
            return name;
        }
    }

    /**
     * Implementation of {@link ColType} that has no fall-back for {@link RelNode}.
     */
    public static class BrokenColTypeImpl extends RelMetadataTest.PartialColTypeImpl {
        public static final RelMetadataProvider SOURCE = ReflectiveRelMetadataProvider.reflectiveSource(RelMetadataTest.ColType.METHOD, new RelMetadataTest.BrokenColTypeImpl());
    }

    /**
     * Extension to {@link RelMetadataQuery} to support {@link ColType}.
     *
     * <p>Illustrates how you would package up a user-defined metadata type.
     */
    private static class MyRelMetadataQuery extends RelMetadataQuery {
        private RelMetadataTest.ColType.Handler colTypeHandler;

        MyRelMetadataQuery() {
            super(THREAD_PROVIDERS.get(), EMPTY);
            colTypeHandler = initialHandler(RelMetadataTest.ColType.Handler.class);
        }

        public String colType(RelNode rel, int column) {
            for (; ;) {
                try {
                    return colTypeHandler.getColType(rel, this, column);
                } catch (JaninoRelMetadataProvider e) {
                    colTypeHandler = revise(e.relClass, RelMetadataTest.ColType.DEF);
                }
            }
        }
    }

    /**
     * Dummy rel node used for testing.
     */
    private class DummyRelNode extends SingleRel {
        /**
         * Creates a <code>DummyRelNode</code>.
         */
        DummyRelNode(RelOptCluster cluster, RelTraitSet traits, RelNode input) {
            super(cluster, traits, input);
        }
    }
}

/**
 * End RelMetadataTest.java
 */
