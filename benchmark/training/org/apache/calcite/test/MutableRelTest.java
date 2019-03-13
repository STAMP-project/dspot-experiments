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


import FilterJoinRule.FILTER_ON_JOIN;
import FilterToCalcRule.INSTANCE;
import ProjectToWindowRule.PROJECT;
import com.google.common.collect.ImmutableList;
import org.junit.Test;


/**
 * Tests for {@link MutableRel} sub-classes.
 */
public class MutableRelTest {
    @Test
    public void testConvertAggregate() {
        MutableRelTest.checkConvertMutableRel("Aggregate", "select empno, sum(sal) from emp group by empno");
    }

    @Test
    public void testConvertFilter() {
        MutableRelTest.checkConvertMutableRel("Filter", "select * from emp where ename = 'DUMMY'");
    }

    @Test
    public void testConvertProject() {
        MutableRelTest.checkConvertMutableRel("Project", "select ename from emp");
    }

    @Test
    public void testConvertSort() {
        MutableRelTest.checkConvertMutableRel("Sort", "select * from emp order by ename");
    }

    @Test
    public void testConvertCalc() {
        MutableRelTest.checkConvertMutableRel("Calc", "select * from emp where ename = 'DUMMY'", false, ImmutableList.of(INSTANCE));
    }

    @Test
    public void testConvertWindow() {
        MutableRelTest.checkConvertMutableRel("Window", "select sal, avg(sal) over (partition by deptno) from emp", false, ImmutableList.of(PROJECT));
    }

    @Test
    public void testConvertCollect() {
        MutableRelTest.checkConvertMutableRel("Collect", "select multiset(select deptno from dept) from (values(true))");
    }

    @Test
    public void testConvertUncollect() {
        MutableRelTest.checkConvertMutableRel("Uncollect", "select * from unnest(multiset[1,2])");
    }

    @Test
    public void testConvertTableModify() {
        MutableRelTest.checkConvertMutableRel("TableModify", "insert into dept select empno, ename from emp");
    }

    @Test
    public void testConvertSample() {
        MutableRelTest.checkConvertMutableRel("Sample", "select * from emp tablesample system(50) where empno > 5");
    }

    @Test
    public void testConvertTableFunctionScan() {
        MutableRelTest.checkConvertMutableRel("TableFunctionScan", "select * from table(ramp(3))");
    }

    @Test
    public void testConvertValues() {
        MutableRelTest.checkConvertMutableRel("Values", "select * from (values (1, 2))");
    }

    @Test
    public void testConvertJoin() {
        MutableRelTest.checkConvertMutableRel("Join", "select * from emp join dept using (deptno)");
    }

    @Test
    public void testConvertSemiJoin() {
        final String sql = "select * from dept where exists (\n" + (("  select * from emp\n" + "  where emp.deptno = dept.deptno\n") + "  and emp.sal > 100)");
        MutableRelTest.checkConvertMutableRel("SemiJoin", sql, true, ImmutableList.of(FilterProjectTransposeRule.INSTANCE, FILTER_ON_JOIN, ProjectMergeRule.INSTANCE, SemiJoinRule.PROJECT));
    }

    @Test
    public void testConvertCorrelate() {
        final String sql = "select * from dept where exists (\n" + (("  select * from emp\n" + "  where emp.deptno = dept.deptno\n") + "  and emp.sal > 100)");
        MutableRelTest.checkConvertMutableRel("Correlate", sql);
    }

    @Test
    public void testConvertUnion() {
        MutableRelTest.checkConvertMutableRel("Union", ("select * from emp where deptno = 10" + "union select * from emp where ename like 'John%'"));
    }

    @Test
    public void testConvertMinus() {
        MutableRelTest.checkConvertMutableRel("Minus", ("select * from emp where deptno = 10" + "except select * from emp where ename like 'John%'"));
    }

    @Test
    public void testConvertIntersect() {
        MutableRelTest.checkConvertMutableRel("Intersect", ("select * from emp where deptno = 10" + "intersect select * from emp where ename like 'John%'"));
    }
}

/**
 * End MutableRelTest.java
 */
