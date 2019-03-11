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
package org.apache.calcite.runtime;


import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.EnumerableDefaults;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.linq4j.function.Function2;
import org.apache.calcite.linq4j.function.Functions;
import org.apache.calcite.linq4j.function.Predicate2;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link org.apache.calcite.runtime.Enumerables}.
 */
public class EnumerablesTest {
    private static final Enumerable<EnumerablesTest.Emp> EMPS = Linq4j.asEnumerable(Arrays.asList(new EnumerablesTest.Emp(10, "Fred"), new EnumerablesTest.Emp(20, "Theodore"), new EnumerablesTest.Emp(20, "Sebastian"), new EnumerablesTest.Emp(30, "Joe")));

    private static final Enumerable<EnumerablesTest.Dept> DEPTS = Linq4j.asEnumerable(Arrays.asList(new EnumerablesTest.Dept(20, "Sales"), new EnumerablesTest.Dept(15, "Marketing")));

    private static final Function2<EnumerablesTest.Emp, EnumerablesTest.Dept, String> EMP_DEPT_TO_STRING = ( v0, v1) -> ((((((("{" + (v0 == null ? null : v0.name)) + ", ") + (v0 == null ? null : v0.deptno)) + ", ") + (v1 == null ? null : v1.deptno)) + ", ") + (v1 == null ? null : v1.name)) + "}";

    private static final Predicate2<EnumerablesTest.Emp, EnumerablesTest.Dept> EQUAL_DEPTNO = ( e, d) -> e.deptno == d.deptno;

    @Test
    public void testSemiJoin() {
        Assert.assertThat(EnumerableDefaults.semiJoin(EnumerablesTest.EMPS, EnumerablesTest.DEPTS, ( e) -> e.deptno, ( d) -> d.deptno, Functions.identityComparer()).toList().toString(), CoreMatchers.equalTo("[Emp(20, Theodore), Emp(20, Sebastian)]"));
    }

    @Test
    public void testMergeJoin() {
        Assert.assertThat(EnumerableDefaults.mergeJoin(Linq4j.asEnumerable(Arrays.asList(new EnumerablesTest.Emp(10, "Fred"), new EnumerablesTest.Emp(20, "Theodore"), new EnumerablesTest.Emp(20, "Sebastian"), new EnumerablesTest.Emp(30, "Joe"), new EnumerablesTest.Emp(30, "Greg"))), Linq4j.asEnumerable(Arrays.asList(new EnumerablesTest.Dept(15, "Marketing"), new EnumerablesTest.Dept(20, "Sales"), new EnumerablesTest.Dept(30, "Research"), new EnumerablesTest.Dept(30, "Development"))), ( e) -> e.deptno, ( d) -> d.deptno, ( v0, v1) -> (v0 + ", ") + v1, false, false).toList().toString(), CoreMatchers.equalTo(("[Emp(20, Theodore), Dept(20, Sales)," + ((((" Emp(20, Sebastian), Dept(20, Sales)," + " Emp(30, Joe), Dept(30, Research),") + " Emp(30, Joe), Dept(30, Development),") + " Emp(30, Greg), Dept(30, Research),") + " Emp(30, Greg), Dept(30, Development)]"))));
    }

    @Test
    public void testMergeJoin2() {
        // Matching keys at start
        Assert.assertThat(EnumerablesTest.intersect(Lists.newArrayList(1, 3, 4), Lists.newArrayList(1, 4)).toList().toString(), CoreMatchers.equalTo("[1, 4]"));
        // Matching key at start and end of right, not of left
        Assert.assertThat(EnumerablesTest.intersect(Lists.newArrayList(0, 1, 3, 4, 5), Lists.newArrayList(1, 4)).toList().toString(), CoreMatchers.equalTo("[1, 4]"));
        // Matching key at start and end of left, not right
        Assert.assertThat(EnumerablesTest.intersect(Lists.newArrayList(1, 3, 4), Lists.newArrayList(0, 1, 4, 5)).toList().toString(), CoreMatchers.equalTo("[1, 4]"));
        // Matching key not at start or end of left or right
        Assert.assertThat(EnumerablesTest.intersect(Lists.newArrayList(0, 2, 3, 4, 5), Lists.newArrayList(1, 3, 4, 6)).toList().toString(), CoreMatchers.equalTo("[3, 4]"));
    }

    @Test
    public void testMergeJoin3() {
        // No overlap
        Assert.assertThat(EnumerablesTest.intersect(Lists.newArrayList(0, 2, 4), Lists.newArrayList(1, 3, 5)).toList().toString(), CoreMatchers.equalTo("[]"));
        // Left empty
        Assert.assertThat(EnumerablesTest.intersect(new ArrayList<>(), Lists.newArrayList(1, 3, 4, 6)).toList().toString(), CoreMatchers.equalTo("[]"));
        // Right empty
        Assert.assertThat(EnumerablesTest.intersect(Lists.newArrayList(3, 7), new ArrayList<>()).toList().toString(), CoreMatchers.equalTo("[]"));
        // Both empty
        Assert.assertThat(EnumerablesTest.intersect(new ArrayList<Integer>(), new ArrayList<>()).toList().toString(), CoreMatchers.equalTo("[]"));
    }

    @Test
    public void testThetaJoin() {
        Assert.assertThat(EnumerableDefaults.thetaJoin(EnumerablesTest.EMPS, EnumerablesTest.DEPTS, EnumerablesTest.EQUAL_DEPTNO, EnumerablesTest.EMP_DEPT_TO_STRING, false, false).toList().toString(), CoreMatchers.equalTo("[{Theodore, 20, 20, Sales}, {Sebastian, 20, 20, Sales}]"));
    }

    @Test
    public void testThetaLeftJoin() {
        Assert.assertThat(EnumerableDefaults.thetaJoin(EnumerablesTest.EMPS, EnumerablesTest.DEPTS, EnumerablesTest.EQUAL_DEPTNO, EnumerablesTest.EMP_DEPT_TO_STRING, false, true).toList().toString(), CoreMatchers.equalTo(("[{Fred, 10, null, null}, {Theodore, 20, 20, Sales}, " + "{Sebastian, 20, 20, Sales}, {Joe, 30, null, null}]")));
    }

    @Test
    public void testThetaRightJoin() {
        Assert.assertThat(EnumerableDefaults.thetaJoin(EnumerablesTest.EMPS, EnumerablesTest.DEPTS, EnumerablesTest.EQUAL_DEPTNO, EnumerablesTest.EMP_DEPT_TO_STRING, true, false).toList().toString(), CoreMatchers.equalTo(("[{Theodore, 20, 20, Sales}, {Sebastian, 20, 20, Sales}, " + "{null, null, 15, Marketing}]")));
    }

    @Test
    public void testThetaFullJoin() {
        Assert.assertThat(EnumerableDefaults.thetaJoin(EnumerablesTest.EMPS, EnumerablesTest.DEPTS, EnumerablesTest.EQUAL_DEPTNO, EnumerablesTest.EMP_DEPT_TO_STRING, true, true).toList().toString(), CoreMatchers.equalTo(("[{Fred, 10, null, null}, {Theodore, 20, 20, Sales}, " + ("{Sebastian, 20, 20, Sales}, {Joe, 30, null, null}, " + "{null, null, 15, Marketing}]"))));
    }

    @Test
    public void testThetaFullJoinLeftEmpty() {
        Assert.assertThat(EnumerableDefaults.thetaJoin(EnumerablesTest.EMPS.take(0), EnumerablesTest.DEPTS, EnumerablesTest.EQUAL_DEPTNO, EnumerablesTest.EMP_DEPT_TO_STRING, true, true).orderBy(Functions.identitySelector()).toList().toString(), CoreMatchers.equalTo("[{null, null, 15, Marketing}, {null, null, 20, Sales}]"));
    }

    @Test
    public void testThetaFullJoinRightEmpty() {
        Assert.assertThat(EnumerableDefaults.thetaJoin(EnumerablesTest.EMPS, EnumerablesTest.DEPTS.take(0), EnumerablesTest.EQUAL_DEPTNO, EnumerablesTest.EMP_DEPT_TO_STRING, true, true).toList().toString(), CoreMatchers.equalTo(("[{Fred, 10, null, null}, {Theodore, 20, null, null}, " + "{Sebastian, 20, null, null}, {Joe, 30, null, null}]")));
    }

    @Test
    public void testThetaFullJoinBothEmpty() {
        Assert.assertThat(EnumerableDefaults.thetaJoin(EnumerablesTest.EMPS.take(0), EnumerablesTest.DEPTS.take(0), EnumerablesTest.EQUAL_DEPTNO, EnumerablesTest.EMP_DEPT_TO_STRING, true, true).toList().toString(), CoreMatchers.equalTo("[]"));
    }

    /**
     * Employee record.
     */
    private static class Emp {
        final int deptno;

        final String name;

        Emp(int deptno, String name) {
            this.deptno = deptno;
            this.name = name;
        }

        @Override
        public String toString() {
            return ((("Emp(" + (deptno)) + ", ") + (name)) + ")";
        }
    }

    /**
     * Department record.
     */
    private static class Dept {
        final int deptno;

        final String name;

        Dept(int deptno, String name) {
            this.deptno = deptno;
            this.name = name;
        }

        @Override
        public String toString() {
            return ((("Dept(" + (deptno)) + ", ") + (name)) + ")";
        }
    }
}

/**
 * End EnumerablesTest.java
 */
