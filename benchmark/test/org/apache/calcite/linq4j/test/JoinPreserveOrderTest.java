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
package org.apache.calcite.linq4j.test;


import CorrelateJoinType.ANTI;
import CorrelateJoinType.INNER;
import CorrelateJoinType.LEFT;
import CorrelateJoinType.SEMI;
import java.util.Arrays;
import java.util.List;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.function.Function1;
import org.apache.calcite.linq4j.function.Function2;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test validating the order preserving properties of join algorithms in
 * {@link org.apache.calcite.linq4j.ExtendedEnumerable}. The correctness of the
 * join algorithm is not examined by this set of tests.
 *
 * <p>To verify that the order of left/right/both input(s) is preserved they
 * must be all ordered by at least one column. The inputs are either sorted on
 * the join or some other column. For the tests to be meaningful the result of
 * the join must not be empty.
 *
 * <p>Interesting variants that may affect the join output and thus destroy the
 * order of one or both inputs is when the join column or the sorted column
 * (when join column != sort column) contain nulls or duplicate values.
 *
 * <p>In addition, the way that nulls are sorted before the join can also play
 * an important role regarding the order preserving semantics of the join.
 *
 * <p>Last but not least, the type of the join (left/right/full/inner/semi/anti)
 * has a major impact on the preservation of order for the various joins.
 */
@RunWith(Parameterized.class)
public final class JoinPreserveOrderTest {
    /**
     * A description holding which column must be sorted and how.
     *
     * @param <T>
     * 		the type of the input relation
     */
    private static class FieldCollationDescription<T> {
        private final String colName;

        private final Function1<T, Comparable> colSelector;

        private final boolean isAscending;

        private final boolean isNullsFirst;

        FieldCollationDescription(final String colName, final Function1<T, Comparable> colSelector, final boolean isAscending, final boolean isNullsFirst) {
            this.colName = colName;
            this.colSelector = colSelector;
            this.isAscending = isAscending;
            this.isNullsFirst = isNullsFirst;
        }

        @Override
        public String toString() {
            return ((((("on='" + (colName)) + "', asc=") + (isAscending)) + ", nullsFirst=") + (isNullsFirst)) + '}';
        }
    }

    /**
     * An abstraction for a join algorithm which performs an operation on two inputs and produces a
     * result.
     *
     * @param <L>
     * 		the type of the left input
     * @param <R>
     * 		the type of the right input
     * @param <Result>
     * 		the type of the result
     */
    private interface JoinAlgorithm<L, R, Result> {
        Enumerable<Result> join(Enumerable<L> left, Enumerable<R> right);
    }

    private final JoinPreserveOrderTest.FieldCollationDescription<JoinPreserveOrderTest.Employee> leftColumn;

    private final JoinPreserveOrderTest.FieldCollationDescription<JoinPreserveOrderTest.Department> rightColumn;

    private static final Function2<JoinPreserveOrderTest.Employee, JoinPreserveOrderTest.Department, List<Integer>> RESULT_SELECTOR = ( emp, dept) -> Arrays.asList((emp != null ? emp.eid : null), (dept != null ? dept.did : null));

    public JoinPreserveOrderTest(final JoinPreserveOrderTest.FieldCollationDescription<JoinPreserveOrderTest.Employee> leftColumn, final JoinPreserveOrderTest.FieldCollationDescription<JoinPreserveOrderTest.Department> rightColumn) {
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    @Test
    public void testLeftJoinPreservesOrderOfLeftInput() {
        testJoin(hashJoin(false, true), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testRightJoinPreservesOrderOfLeftInput() {
        Assume.assumeFalse(leftColumn.isNullsFirst);
        testJoin(hashJoin(true, false), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testFullJoinPreservesOrderOfLeftInput() {
        Assume.assumeFalse(leftColumn.isNullsFirst);
        testJoin(hashJoin(true, true), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testInnerJoinPreservesOrderOfLeftInput() {
        testJoin(hashJoin(false, false), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testLeftThetaJoinPreservesOrderOfLeftInput() {
        testJoin(thetaJoin(false, true), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testRightThetaJoinPreservesOrderOfLeftInput() {
        Assume.assumeFalse(leftColumn.isNullsFirst);
        testJoin(thetaJoin(true, false), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testFullThetaJoinPreservesOrderOfLeftInput() {
        Assume.assumeFalse(leftColumn.isNullsFirst);
        testJoin(thetaJoin(true, true), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testInnerThetaJoinPreservesOrderOfLeftInput() {
        testJoin(thetaJoin(false, false), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testLeftCorrelateJoinPreservesOrderOfLeftInput() {
        testJoin(correlateJoin(LEFT), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testInnerCorrelateJoinPreservesOrderOfLeftInput() {
        testJoin(correlateJoin(INNER), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testAntiCorrelateJoinPreservesOrderOfLeftInput() {
        testJoin(correlateJoin(ANTI), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testSemiCorrelateJoinPreservesOrderOfLeftInput() {
        testJoin(correlateJoin(SEMI), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    @Test
    public void testSemiDefaultJoinPreservesOrderOfLeftInput() {
        testJoin(semiJoin(), JoinPreserveOrderTest.AssertOrder.PRESERVED, JoinPreserveOrderTest.AssertOrder.IGNORED);
    }

    /**
     * Different assertions for the result of the join.
     */
    private enum AssertOrder {

        PRESERVED() {
            @Override
            <E> void check(final List<E> expected, final List<E> actual, final boolean nullsFirst) {
                Assert.assertTrue((((("Order is not preserved. Expected:<" + expected) + "> but was:<") + actual) + ">"), isOrderPreserved(expected, actual, nullsFirst));
            }
        },
        DESTROYED() {
            @Override
            <E> void check(final List<E> expected, final List<E> actual, final boolean nullsFirst) {
                Assert.assertFalse((((("Order is not destroyed. Expected:<" + expected) + "> but was:<") + actual) + ">"), isOrderPreserved(expected, actual, nullsFirst));
            }
        },
        IGNORED() {
            @Override
            <E> void check(final List<E> expected, final List<E> actual, final boolean nullsFirst) {
                // Do nothing
            }
        };
        abstract <E> void check(List<E> expected, List<E> actual, boolean nullsFirst);

        /**
         * Checks that the elements in the list are in the expected order.
         */
        <E> boolean isOrderPreserved(List<E> expected, List<E> actual, boolean nullsFirst) {
            boolean isPreserved = true;
            for (int i = 1; i < (actual.size()); i++) {
                E prev = actual.get((i - 1));
                E next = actual.get(i);
                int posPrev = (prev == null) ? nullsFirst ? -1 : actual.size() : expected.indexOf(prev);
                int posNext = (next == null) ? nullsFirst ? -1 : actual.size() : expected.indexOf(next);
                isPreserved &= posPrev <= posNext;
            }
            return isPreserved;
        }
    }

    /**
     * Department
     */
    private static class Department {
        private final int did;

        private final Integer deptno;

        private final String name;

        Department(final int did, final Integer deptno, final String name) {
            this.did = did;
            this.deptno = deptno;
            this.name = name;
        }

        int getDid() {
            return did;
        }

        Integer getDeptno() {
            return deptno;
        }

        String getName() {
            return name;
        }
    }

    /**
     * Employee
     */
    private static class Employee {
        private final int eid;

        private final String name;

        private final Integer deptno;

        Employee(final int eid, final String name, final Integer deptno) {
            this.eid = eid;
            this.name = name;
            this.deptno = deptno;
        }

        int getEid() {
            return eid;
        }

        String getName() {
            return name;
        }

        Integer getDeptno() {
            return deptno;
        }

        @Override
        public String toString() {
            return (((((("Employee{eid=" + (eid)) + ", name='") + (name)) + '\'') + ", deptno=") + (deptno)) + '}';
        }
    }

    private static final JoinPreserveOrderTest.Employee[] EMPS = new JoinPreserveOrderTest.Employee[]{ new JoinPreserveOrderTest.Employee(100, "Stam", 10), new JoinPreserveOrderTest.Employee(110, "Greg", 20), new JoinPreserveOrderTest.Employee(120, "Ilias", 30), new JoinPreserveOrderTest.Employee(130, "Ruben", 40), new JoinPreserveOrderTest.Employee(140, "Tanguy", 50), new JoinPreserveOrderTest.Employee(150, "Andrew", (-10)), // Nulls on name
    new JoinPreserveOrderTest.Employee(160, null, 60), new JoinPreserveOrderTest.Employee(170, null, (-60)), // Nulls on deptno
    new JoinPreserveOrderTest.Employee(180, "Achille", null), // Duplicate values on name
    new JoinPreserveOrderTest.Employee(190, "Greg", 70), new JoinPreserveOrderTest.Employee(200, "Ilias", (-70)), // Duplicates values on deptno
    new JoinPreserveOrderTest.Employee(210, "Sophia", 40), new JoinPreserveOrderTest.Employee(220, "Alexia", (-40)), new JoinPreserveOrderTest.Employee(230, "Loukia", (-40)) };

    private static final JoinPreserveOrderTest.Department[] DEPTS = new JoinPreserveOrderTest.Department[]{ new JoinPreserveOrderTest.Department(1, 10, "Sales"), new JoinPreserveOrderTest.Department(2, 20, "Pre-sales"), new JoinPreserveOrderTest.Department(4, 40, "Support"), new JoinPreserveOrderTest.Department(5, 50, "Marketing"), new JoinPreserveOrderTest.Department(6, 60, "Engineering"), new JoinPreserveOrderTest.Department(7, 70, "Management"), new JoinPreserveOrderTest.Department(8, 80, "HR"), new JoinPreserveOrderTest.Department(9, 90, "Product design"), // Nulls on name
    new JoinPreserveOrderTest.Department(3, 30, null), new JoinPreserveOrderTest.Department(10, 100, null), // Nulls on deptno
    new JoinPreserveOrderTest.Department(11, null, "Post-sales"), // Duplicate values on name
    new JoinPreserveOrderTest.Department(12, 50, "Support"), new JoinPreserveOrderTest.Department(13, 140, "Support"), // Duplicate values on deptno
    new JoinPreserveOrderTest.Department(14, 20, "Board"), new JoinPreserveOrderTest.Department(15, 40, "Promotions") };
}

/**
 * End JoinPreserveOrderTest.java
 */
