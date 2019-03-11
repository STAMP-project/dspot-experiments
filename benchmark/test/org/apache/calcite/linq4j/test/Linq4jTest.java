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


import com.example.Linq4jExample;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.EnumerableDefaults;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.Grouping;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.linq4j.Lookup;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.linq4j.QueryableDefaults;
import org.apache.calcite.linq4j.function.Function0;
import org.apache.calcite.linq4j.function.Function1;
import org.apache.calcite.linq4j.function.Function2;
import org.apache.calcite.linq4j.function.Functions;
import org.apache.calcite.linq4j.function.IntegerFunction1;
import org.apache.calcite.linq4j.function.Predicate1;
import org.apache.calcite.linq4j.function.Predicate2;
import org.apache.calcite.linq4j.tree.ConstantExpression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for LINQ4J.
 */
public class Linq4jTest {
    public static final Function1<Linq4jTest.Employee, String> EMP_NAME_SELECTOR = ( employee) -> employee.name;

    public static final Function1<Linq4jTest.Employee, Integer> EMP_DEPTNO_SELECTOR = ( employee) -> employee.deptno;

    public static final Function1<Linq4jTest.Employee, Integer> EMP_EMPNO_SELECTOR = ( employee) -> employee.empno;

    public static final Function1<Linq4jTest.Department, Enumerable<Linq4jTest.Employee>> DEPT_EMPLOYEES_SELECTOR = ( a0) -> Linq4j.asEnumerable(a0.employees);

    public static final Function1<Linq4jTest.Department, String> DEPT_NAME_SELECTOR = ( department) -> department.name;

    public static final Function1<Linq4jTest.Department, Integer> DEPT_DEPTNO_SELECTOR = ( department) -> department.deptno;

    public static final IntegerFunction1<Linq4jTest.Department> DEPT_DEPTNO_SELECTOR2 = ( department) -> department.deptno;

    public static final Function1<Object, Integer> ONE_SELECTOR = ( employee) -> 1;

    private static final Function2<Object, Object, Integer> PAIR_SELECTOR = ( employee, v2) -> 1;

    @Test
    public void testSelect() {
        List<String> names = Linq4j.asEnumerable(Linq4jTest.emps).select(Linq4jTest.EMP_NAME_SELECTOR).toList();
        Assert.assertEquals("[Fred, Bill, Eric, Janet]", names.toString());
    }

    @Test
    public void testWhere() {
        List<String> names = Linq4j.asEnumerable(Linq4jTest.emps).where(( employee) -> employee.deptno < 15).select(Linq4jTest.EMP_NAME_SELECTOR).toList();
        Assert.assertEquals("[Fred, Eric, Janet]", names.toString());
    }

    @Test
    public void testWhereIndexed() {
        // Returns every other employee.
        List<String> names = Linq4j.asEnumerable(Linq4jTest.emps).where(( employee, n) -> (n % 2) == 0).select(Linq4jTest.EMP_NAME_SELECTOR).toList();
        Assert.assertEquals("[Fred, Eric]", names.toString());
    }

    @Test
    public void testSelectMany() {
        final List<String> nameSeqs = Linq4j.asEnumerable(Linq4jTest.depts).selectMany(Linq4jTest.DEPT_EMPLOYEES_SELECTOR).select(( v1, v2) -> (("#" + v2) + ": ") + v1.name).toList();
        Assert.assertEquals("[#0: Fred, #1: Eric, #2: Janet, #3: Bill]", nameSeqs.toString());
    }

    @Test
    public void testCount() {
        final int count = Linq4j.asEnumerable(Linq4jTest.depts).count();
        Assert.assertEquals(3, count);
    }

    @Test
    public void testCountPredicate() {
        final int count = Linq4j.asEnumerable(Linq4jTest.depts).count(( v1) -> (v1.employees.size()) > 0);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testLongCount() {
        final long count = Linq4j.asEnumerable(Linq4jTest.depts).longCount();
        Assert.assertEquals(3, count);
    }

    @Test
    public void testLongCountPredicate() {
        final long count = Linq4j.asEnumerable(Linq4jTest.depts).longCount(( v1) -> (v1.employees.size()) > 0);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testAllPredicate() {
        Predicate1<Linq4jTest.Employee> allEmpnoGE100 = ( emp) -> emp.empno >= 100;
        Predicate1<Linq4jTest.Employee> allEmpnoGT100 = ( emp) -> emp.empno > 100;
        Assert.assertTrue(Linq4j.asEnumerable(Linq4jTest.emps).all(allEmpnoGE100));
        Assert.assertFalse(Linq4j.asEnumerable(Linq4jTest.emps).all(allEmpnoGT100));
    }

    @Test
    public void testAny() {
        List<Linq4jTest.Employee> emptyList = Collections.emptyList();
        Assert.assertFalse(Linq4j.asEnumerable(emptyList).any());
        Assert.assertTrue(Linq4j.asEnumerable(Linq4jTest.emps).any());
    }

    @Test
    public void testAnyPredicate() {
        Predicate1<Linq4jTest.Department> deptoNameIT = ( v1) -> (v1.name != null) && (v1.name.equals("IT"));
        Predicate1<Linq4jTest.Department> deptoNameSales = ( v1) -> (v1.name != null) && (v1.name.equals("Sales"));
        Assert.assertFalse(Linq4j.asEnumerable(Linq4jTest.depts).any(deptoNameIT));
        Assert.assertTrue(Linq4j.asEnumerable(Linq4jTest.depts).any(deptoNameSales));
    }

    @Test
    public void testAverageSelector() {
        Assert.assertEquals(20, Linq4j.asEnumerable(Linq4jTest.depts).average(Linq4jTest.DEPT_DEPTNO_SELECTOR2));
    }

    @Test
    public void testMin() {
        Assert.assertEquals(10, ((int) (Linq4j.asEnumerable(Linq4jTest.depts).select(Linq4jTest.DEPT_DEPTNO_SELECTOR).min())));
    }

    @Test
    public void testMinSelector() {
        Assert.assertEquals(10, ((int) (Linq4j.asEnumerable(Linq4jTest.depts).min(Linq4jTest.DEPT_DEPTNO_SELECTOR))));
    }

    @Test
    public void testMinSelector2() {
        Assert.assertEquals(10, Linq4j.asEnumerable(Linq4jTest.depts).min(Linq4jTest.DEPT_DEPTNO_SELECTOR2));
    }

    @Test
    public void testMax() {
        Assert.assertEquals(30, ((int) (Linq4j.asEnumerable(Linq4jTest.depts).select(Linq4jTest.DEPT_DEPTNO_SELECTOR).max())));
    }

    @Test
    public void testMaxSelector() {
        Assert.assertEquals(30, ((int) (Linq4j.asEnumerable(Linq4jTest.depts).max(Linq4jTest.DEPT_DEPTNO_SELECTOR))));
    }

    @Test
    public void testMaxSelector2() {
        Assert.assertEquals(30, Linq4j.asEnumerable(Linq4jTest.depts).max(Linq4jTest.DEPT_DEPTNO_SELECTOR2));
    }

    @Test
    public void testAggregate() {
        Assert.assertEquals("Sales,HR,Marketing", Linq4j.asEnumerable(Linq4jTest.depts).select(Linq4jTest.DEPT_NAME_SELECTOR).aggregate(null, ((Function2<String, String, String>) (( v1, v2) -> v1 == null ? v2 : (v1 + ",") + v2))));
    }

    @Test
    public void testToMap() {
        final Map<Integer, Linq4jTest.Employee> map = Linq4j.asEnumerable(Linq4jTest.emps).toMap(Linq4jTest.EMP_EMPNO_SELECTOR);
        Assert.assertEquals(4, map.size());
        Assert.assertTrue(map.get(110).name.equals("Bill"));
    }

    @Test
    public void testToMapWithComparer() {
        final Map<String, String> map = Linq4j.asEnumerable(Arrays.asList("foo", "bar", "far")).toMap(Functions.identitySelector(), new org.apache.calcite.linq4j.function.EqualityComparer<String>() {
            public boolean equal(String v1, String v2) {
                return (String.CASE_INSENSITIVE_ORDER.compare(v1, v2)) == 0;
            }

            public int hashCode(String s) {
                return s == null ? Objects.hashCode(null) : s.toLowerCase(Locale.ROOT).hashCode();
            }
        });
        Assert.assertEquals(3, map.size());
        Assert.assertTrue(map.get("foo").equals("foo"));
        Assert.assertTrue(map.get("Foo").equals("foo"));
        Assert.assertTrue(map.get("FOO").equals("foo"));
    }

    @Test
    public void testToMap2() {
        final Map<Integer, Integer> map = Linq4j.asEnumerable(Linq4jTest.emps).toMap(Linq4jTest.EMP_EMPNO_SELECTOR, Linq4jTest.EMP_DEPTNO_SELECTOR);
        Assert.assertEquals(4, map.size());
        Assert.assertTrue(((map.get(110)) == 30));
    }

    @Test
    public void testToMap2WithComparer() {
        final Map<String, String> map = Linq4j.asEnumerable(Arrays.asList("foo", "bar", "far")).toMap(Functions.identitySelector(), ( x) -> x == null ? null : x.toUpperCase(Locale.ROOT), new org.apache.calcite.linq4j.function.EqualityComparer<String>() {
            public boolean equal(String v1, String v2) {
                return (String.CASE_INSENSITIVE_ORDER.compare(v1, v2)) == 0;
            }

            public int hashCode(String s) {
                return s == null ? Objects.hashCode(null) : s.toLowerCase(Locale.ROOT).hashCode();
            }
        });
        Assert.assertEquals(3, map.size());
        Assert.assertTrue(map.get("foo").equals("FOO"));
        Assert.assertTrue(map.get("Foo").equals("FOO"));
        Assert.assertTrue(map.get("FOO").equals("FOO"));
    }

    @Test
    public void testToLookup() {
        final Lookup<Integer, Linq4jTest.Employee> lookup = Linq4j.asEnumerable(Linq4jTest.emps).toLookup(Linq4jTest.EMP_DEPTNO_SELECTOR);
        int n = 0;
        for (Grouping<Integer, Linq4jTest.Employee> grouping : lookup) {
            ++n;
            switch (grouping.getKey()) {
                case 10 :
                    Assert.assertEquals(3, grouping.count());
                    break;
                case 30 :
                    Assert.assertEquals(1, grouping.count());
                    break;
                default :
                    Assert.fail(("unknown department number " + grouping));
            }
        }
        Assert.assertEquals(n, 2);
    }

    @Test
    public void testToLookupSelector() {
        final Lookup<Integer, String> lookup = Linq4j.asEnumerable(Linq4jTest.emps).toLookup(Linq4jTest.EMP_DEPTNO_SELECTOR, Linq4jTest.EMP_NAME_SELECTOR);
        int n = 0;
        for (Grouping<Integer, String> grouping : lookup) {
            ++n;
            switch (grouping.getKey()) {
                case 10 :
                    Assert.assertEquals(3, grouping.count());
                    Assert.assertTrue(grouping.contains("Fred"));
                    Assert.assertTrue(grouping.contains("Eric"));
                    Assert.assertTrue(grouping.contains("Janet"));
                    Assert.assertFalse(grouping.contains("Bill"));
                    break;
                case 30 :
                    Assert.assertEquals(1, grouping.count());
                    Assert.assertTrue(grouping.contains("Bill"));
                    Assert.assertFalse(grouping.contains("Fred"));
                    break;
                default :
                    Assert.fail(("unknown department number " + grouping));
            }
        }
        Assert.assertEquals(n, 2);
        Assert.assertEquals("[10:3, 30:1]", lookup.applyResultSelector(( v1, v2) -> (v1 + ":") + (v2.count())).orderBy(Functions.identitySelector()).toList().toString());
    }

    @Test
    public void testContains() {
        Linq4jTest.Employee e = Linq4jTest.emps[1];
        Linq4jTest.Employee employeeClone = new Linq4jTest.Employee(e.empno, e.name, e.deptno);
        Linq4jTest.Employee employeeOther = Linq4jTest.badEmps[0];
        Assert.assertEquals(e, employeeClone);
        Assert.assertTrue(Linq4j.asEnumerable(Linq4jTest.emps).contains(e));
        Assert.assertTrue(Linq4j.asEnumerable(Linq4jTest.emps).contains(employeeClone));
        Assert.assertFalse(Linq4j.asEnumerable(Linq4jTest.emps).contains(employeeOther));
    }

    @Test
    public void testContainsWithEqualityComparer() {
        org.apache.calcite.linq4j.function.EqualityComparer<Linq4jTest.Employee> compareByEmpno = new org.apache.calcite.linq4j.function.EqualityComparer<Linq4jTest.Employee>() {
            public boolean equal(Linq4jTest.Employee e1, Linq4jTest.Employee e2) {
                return ((e1 != null) && (e2 != null)) && ((e1.empno) == (e2.empno));
            }

            public int hashCode(Linq4jTest.Employee t) {
                return t == null ? 30877 : t.hashCode();
            }
        };
        Linq4jTest.Employee e = Linq4jTest.emps[1];
        Linq4jTest.Employee employeeClone = new Linq4jTest.Employee(e.empno, e.name, e.deptno);
        Linq4jTest.Employee employeeOther = Linq4jTest.badEmps[0];
        Assert.assertEquals(e, employeeClone);
        Assert.assertTrue(Linq4j.asEnumerable(Linq4jTest.emps).contains(e, compareByEmpno));
        Assert.assertTrue(Linq4j.asEnumerable(Linq4jTest.emps).contains(employeeClone, compareByEmpno));
        Assert.assertFalse(Linq4j.asEnumerable(Linq4jTest.emps).contains(employeeOther, compareByEmpno));
    }

    @Test
    public void testFirst() {
        Linq4jTest.Employee e = Linq4jTest.emps[0];
        Assert.assertEquals(e, Linq4jTest.emps[0]);
        Assert.assertEquals(e, Linq4j.asEnumerable(Linq4jTest.emps).first());
        Linq4jTest.Department d = Linq4jTest.depts[0];
        Assert.assertEquals(d, Linq4jTest.depts[0]);
        Assert.assertEquals(d, Linq4j.asEnumerable(Linq4jTest.depts).first());
        try {
            String s = Linq4j.<String>emptyEnumerable().first();
            Assert.fail(("expected exception, got " + s));
        } catch (NoSuchElementException ex) {
            // ok
        }
        // close occurs if first throws
        final int[] closeCount = new int[]{ 0 };
        try {
            String s = myEnumerable(closeCount, 0).first();
            Assert.fail(("expected exception, got " + s));
        } catch (NoSuchElementException ex) {
            // ok
        }
        Assert.assertThat(closeCount[0], CoreMatchers.equalTo(1));
        // close occurs if first does not throw
        closeCount[0] = 0;
        final String s = myEnumerable(closeCount, 1).first();
        Assert.assertThat(s, CoreMatchers.equalTo("x"));
        Assert.assertThat(closeCount[0], CoreMatchers.equalTo(1));
    }

    @Test
    public void testFirstPredicate1() {
        Predicate1<String> startWithS = ( s) -> (s != null) && (Character.toString(s.charAt(0)).equals("S"));
        Predicate1<Integer> numberGT15 = ( i) -> i > 15;
        String[] people = new String[]{ "Brill", "Smith", "Simpsom" };
        String[] peopleWithoutCharS = new String[]{ "Brill", "Andrew", "Alice" };
        Integer[] numbers = new Integer[]{ 5, 10, 15, 20, 25 };
        Assert.assertEquals(people[1], Linq4j.asEnumerable(people).first(startWithS));
        Assert.assertEquals(numbers[3], Linq4j.asEnumerable(numbers).first(numberGT15));
        try {
            String s = Linq4j.asEnumerable(peopleWithoutCharS).first(startWithS);
            Assert.fail(("expected exception, but got" + s));
        } catch (NoSuchElementException e) {
            // ok
        }
    }

    @Test
    public void testFirstOrDefault() {
        String[] people = new String[]{ "Brill", "Smith", "Simpsom" };
        String[] empty = new String[]{  };
        Integer[] numbers = new Integer[]{ 5, 10, 15, 20, 25 };
        Assert.assertEquals(people[0], Linq4j.asEnumerable(people).firstOrDefault());
        Assert.assertEquals(numbers[0], Linq4j.asEnumerable(numbers).firstOrDefault());
        Assert.assertNull(Linq4j.asEnumerable(empty).firstOrDefault());
    }

    @Test
    public void testFirstOrDefaultPredicate1() {
        Predicate1<String> startWithS = ( s) -> (s != null) && (Character.toString(s.charAt(0)).equals("S"));
        Predicate1<Integer> numberGT15 = ( i) -> i > 15;
        String[] people = new String[]{ "Brill", "Smith", "Simpsom" };
        String[] peopleWithoutCharS = new String[]{ "Brill", "Andrew", "Alice" };
        Integer[] numbers = new Integer[]{ 5, 10, 15, 20, 25 };
        Assert.assertEquals(people[1], Linq4j.asEnumerable(people).firstOrDefault(startWithS));
        Assert.assertEquals(numbers[3], Linq4j.asEnumerable(numbers).firstOrDefault(numberGT15));
        Assert.assertNull(Linq4j.asEnumerable(peopleWithoutCharS).firstOrDefault(startWithS));
    }

    @Test
    public void testSingle() {
        String[] person = new String[]{ "Smith" };
        String[] people = new String[]{ "Brill", "Smith", "Simpson" };
        Integer[] number = new Integer[]{ 20 };
        Integer[] numbers = new Integer[]{ 5, 10, 15, 20 };
        Assert.assertEquals(person[0], Linq4j.asEnumerable(person).single());
        Assert.assertEquals(number[0], Linq4j.asEnumerable(number).single());
        try {
            String s = Linq4j.asEnumerable(people).single();
            Assert.fail(("expected exception, but got" + s));
        } catch (IllegalStateException e) {
            // ok
        }
        try {
            int i = Linq4j.asEnumerable(numbers).single();
            Assert.fail(("expected exception, but got" + i));
        } catch (IllegalStateException e) {
            // ok
        }
    }

    @Test
    public void testSingleOrDefault() {
        String[] person = new String[]{ "Smith" };
        String[] people = new String[]{ "Brill", "Smith", "Simpson" };
        Integer[] number = new Integer[]{ 20 };
        Integer[] numbers = new Integer[]{ 5, 10, 15, 20 };
        Assert.assertEquals(person[0], Linq4j.asEnumerable(person).singleOrDefault());
        Assert.assertEquals(number[0], Linq4j.asEnumerable(number).singleOrDefault());
        Assert.assertNull(Linq4j.asEnumerable(people).singleOrDefault());
        Assert.assertNull(Linq4j.asEnumerable(numbers).singleOrDefault());
    }

    @Test
    public void testSinglePredicate1() {
        Predicate1<String> startWithS = ( s) -> (s != null) && (Character.toString(s.charAt(0)).equals("S"));
        Predicate1<Integer> numberGT15 = ( i) -> i > 15;
        String[] people = new String[]{ "Brill", "Smith" };
        String[] twoPeopleWithCharS = new String[]{ "Brill", "Smith", "Simpson" };
        String[] peopleWithoutCharS = new String[]{ "Brill", "Andrew", "Alice" };
        Integer[] numbers = new Integer[]{ 5, 10, 15, 20 };
        Integer[] numbersWithoutGT15 = new Integer[]{ 5, 10, 15 };
        Integer[] numbersWithTwoGT15 = new Integer[]{ 5, 10, 15, 20, 25 };
        Assert.assertEquals(people[1], Linq4j.asEnumerable(people).single(startWithS));
        Assert.assertEquals(numbers[3], Linq4j.asEnumerable(numbers).single(numberGT15));
        try {
            String s = Linq4j.asEnumerable(twoPeopleWithCharS).single(startWithS);
            Assert.fail(("expected exception, but got" + s));
        } catch (IllegalStateException e) {
            // ok
        }
        try {
            int i = Linq4j.asEnumerable(numbersWithTwoGT15).single(numberGT15);
            Assert.fail(("expected exception, but got" + i));
        } catch (IllegalStateException e) {
            // ok
        }
        try {
            String s = Linq4j.asEnumerable(peopleWithoutCharS).single(startWithS);
            Assert.fail(("expected exception, but got" + s));
        } catch (IllegalStateException e) {
            // ok
        }
        try {
            int i = Linq4j.asEnumerable(numbersWithoutGT15).single(numberGT15);
            Assert.fail(("expected exception, but got" + i));
        } catch (IllegalStateException e) {
            // ok
        }
    }

    @Test
    public void testSingleOrDefaultPredicate1() {
        Predicate1<String> startWithS = ( s) -> (s != null) && (Character.toString(s.charAt(0)).equals("S"));
        Predicate1<Integer> numberGT15 = ( i) -> i > 15;
        String[] people = new String[]{ "Brill", "Smith" };
        String[] twoPeopleWithCharS = new String[]{ "Brill", "Smith", "Simpson" };
        String[] peopleWithoutCharS = new String[]{ "Brill", "Andrew", "Alice" };
        Integer[] numbers = new Integer[]{ 5, 10, 15, 20 };
        Integer[] numbersWithTwoGT15 = new Integer[]{ 5, 10, 15, 20, 25 };
        Integer[] numbersWithoutGT15 = new Integer[]{ 5, 10, 15 };
        Assert.assertEquals(people[1], Linq4j.asEnumerable(people).singleOrDefault(startWithS));
        Assert.assertEquals(numbers[3], Linq4j.asEnumerable(numbers).singleOrDefault(numberGT15));
        Assert.assertNull(Linq4j.asEnumerable(twoPeopleWithCharS).singleOrDefault(startWithS));
        Assert.assertNull(Linq4j.asEnumerable(numbersWithTwoGT15).singleOrDefault(numberGT15));
        Assert.assertNull(Linq4j.asEnumerable(peopleWithoutCharS).singleOrDefault(startWithS));
        Assert.assertNull(Linq4j.asEnumerable(numbersWithoutGT15).singleOrDefault(numberGT15));
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    public void testIdentityEqualityComparer() {
        final Integer one = 1000;
        final Integer one2 = Integer.valueOf(one.toString());
        Assert.assertThat(one, IsNot.not(CoreMatchers.sameInstance(one2)));
        final Integer two = 2;
        final org.apache.calcite.linq4j.function.EqualityComparer<Integer> idComparer = Functions.identityComparer();
        Assert.assertTrue(idComparer.equal(one, one));
        Assert.assertTrue(idComparer.equal(one, one2));
        Assert.assertFalse(idComparer.equal(one, two));
    }

    @Test
    public void testSelectorEqualityComparer() {
        final org.apache.calcite.linq4j.function.EqualityComparer<Linq4jTest.Employee> comparer = Functions.selectorComparer(((Function1<Linq4jTest.Employee, Object>) (( a0) -> a0.deptno)));
        Assert.assertTrue(comparer.equal(Linq4jTest.emps[0], Linq4jTest.emps[0]));
        Assert.assertEquals(comparer.hashCode(Linq4jTest.emps[0]), comparer.hashCode(Linq4jTest.emps[0]));
        Assert.assertTrue(comparer.equal(Linq4jTest.emps[0], Linq4jTest.emps[2]));
        Assert.assertEquals(comparer.hashCode(Linq4jTest.emps[0]), comparer.hashCode(Linq4jTest.emps[2]));
        Assert.assertFalse(comparer.equal(Linq4jTest.emps[0], Linq4jTest.emps[1]));
        // not 100% guaranteed, but works for this data
        Assert.assertNotEquals(comparer.hashCode(Linq4jTest.emps[0]), comparer.hashCode(Linq4jTest.emps[1]));
        Assert.assertFalse(comparer.equal(Linq4jTest.emps[0], null));
        Assert.assertNotEquals(comparer.hashCode(Linq4jTest.emps[0]), comparer.hashCode(null));
        Assert.assertFalse(comparer.equal(null, Linq4jTest.emps[1]));
        Assert.assertTrue(comparer.equal(null, null));
        Assert.assertEquals(comparer.hashCode(null), comparer.hashCode(null));
    }

    @Test
    public void testToLookupSelectorComparer() {
        final Lookup<String, Linq4jTest.Employee> lookup = Linq4j.asEnumerable(Linq4jTest.emps).toLookup(Linq4jTest.EMP_NAME_SELECTOR, new org.apache.calcite.linq4j.function.EqualityComparer<String>() {
            public boolean equal(String v1, String v2) {
                return (v1.length()) == (v2.length());
            }

            public int hashCode(String s) {
                return s.length();
            }
        });
        Assert.assertEquals(2, lookup.size());
        Assert.assertEquals("[Fred, Janet]", new java.util.TreeSet(lookup.keySet()).toString());
        StringBuilder buf = new StringBuilder();
        for (Grouping<String, Linq4jTest.Employee> grouping : lookup.orderBy(Linq4jTest.groupingKeyExtractor())) {
            buf.append(grouping).append("\n");
        }
        Assert.assertEquals(("Fred: [Employee(name: Fred, deptno:10), Employee(name: Bill, deptno:30), Employee(name: Eric, deptno:10)]\n" + "Janet: [Employee(name: Janet, deptno:10)]\n"), buf.toString());
    }

    /**
     * Tests the version of {@link ExtendedEnumerable#groupBy}
     * that uses an accumulator; does not build intermediate lists.
     */
    @Test
    public void testGroupBy() {
        String s = Linq4j.asEnumerable(Linq4jTest.emps).groupBy(Linq4jTest.EMP_DEPTNO_SELECTOR, ((Function0<String>) (() -> null)), ( v1, e0) -> v1 == null ? e0.name : (v1 + "+") + e0.name, ( v1, v2) -> (v1 + ": ") + v2).orderBy(Functions.identitySelector()).toList().toString();
        Assert.assertEquals("[10: Fred+Eric+Janet, 30: Bill]", s);
    }

    /**
     * Tests the version of
     * {@link ExtendedEnumerable#aggregate}
     * that has a result selector. Note how similar it is to
     * {@link #testGroupBy()}.
     */
    @Test
    public void testAggregate2() {
        String s = // CHECKSTYLE: IGNORE 0
        Linq4j.asEnumerable(Linq4jTest.emps).aggregate(((Function0<String>) (() -> null)).apply(), ( v1, e0) -> v1 == null ? e0.name : (v1 + "+") + e0.name, ( v2) -> "<no key>: " + v2);
        Assert.assertEquals("<no key>: Fred+Bill+Eric+Janet", s);
    }

    @Test
    public void testEmptyEnumerable() {
        final Enumerable<Object> enumerable = Linq4j.emptyEnumerable();
        Assert.assertThat(enumerable.any(), CoreMatchers.is(false));
        Assert.assertThat(enumerable.longCount(), CoreMatchers.equalTo(0L));
        final Enumerator<Object> enumerator = enumerable.enumerator();
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(false));
    }

    @Test
    public void testSingletonEnumerable() {
        final Enumerable<String> enumerable = Linq4j.singletonEnumerable("foo");
        Assert.assertThat(enumerable.any(), CoreMatchers.is(true));
        Assert.assertThat(enumerable.longCount(), CoreMatchers.equalTo(1L));
        final Enumerator<String> enumerator = enumerable.enumerator();
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(true));
        Assert.assertThat(enumerator.current(), CoreMatchers.equalTo("foo"));
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(false));
    }

    @Test
    public void testSingletonEnumerator() {
        final Enumerator<String> enumerator = Linq4j.singletonEnumerator("foo");
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(true));
        Assert.assertThat(enumerator.current(), CoreMatchers.equalTo("foo"));
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(false));
    }

    @Test
    public void testSingletonNullEnumerator() {
        final Enumerator<String> enumerator = Linq4j.singletonNullEnumerator();
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(true));
        Assert.assertThat(enumerator.current(), CoreMatchers.nullValue());
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(false));
    }

    @Test
    public void testTransformEnumerator() {
        final List<String> strings = Arrays.asList("one", "two", "three");
        final Function1<String, Integer> func = String::length;
        final Enumerator<Integer> enumerator = Linq4j.transform(Linq4j.enumerator(strings), func);
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(true));
        Assert.assertThat(enumerator.current(), CoreMatchers.is(3));
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(true));
        Assert.assertThat(enumerator.current(), CoreMatchers.is(3));
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(true));
        Assert.assertThat(enumerator.current(), CoreMatchers.is(5));
        Assert.assertThat(enumerator.moveNext(), CoreMatchers.is(false));
        final Enumerator<Integer> enumerator2 = Linq4j.transform(Linq4j.emptyEnumerator(), func);
        Assert.assertThat(enumerator2.moveNext(), CoreMatchers.is(false));
    }

    @Test
    public void testCast() {
        final List<Number> numbers = Arrays.asList(((Number) (2)), null, 3.14, 5);
        final Enumerator<Integer> enumerator = Linq4j.asEnumerable(numbers).cast(Integer.class).enumerator();
        checkCast(enumerator);
    }

    @Test
    public void testIterableCast() {
        final List<Number> numbers = Arrays.asList(((Number) (2)), null, 3.14, 5);
        final Enumerator<Integer> enumerator = Linq4j.cast(numbers, Integer.class).enumerator();
        checkCast(enumerator);
    }

    @Test
    public void testOfType() {
        final List<Number> numbers = Arrays.asList(((Number) (2)), null, 3.14, 5);
        final Enumerator<Integer> enumerator = Linq4j.asEnumerable(numbers).ofType(Integer.class).enumerator();
        checkIterable(enumerator);
    }

    @Test
    public void testIterableOfType() {
        final List<Number> numbers = Arrays.asList(((Number) (2)), null, 3.14, 5);
        final Enumerator<Integer> enumerator = Linq4j.ofType(numbers, Integer.class).enumerator();
        checkIterable(enumerator);
    }

    @Test
    public void testConcat() {
        Assert.assertEquals(5, Linq4j.asEnumerable(Linq4jTest.emps).concat(Linq4j.asEnumerable(Linq4jTest.badEmps)).count());
    }

    @Test
    public void testUnion() {
        Assert.assertEquals(5, Linq4j.asEnumerable(Linq4jTest.emps).union(Linq4j.asEnumerable(Linq4jTest.badEmps)).union(Linq4j.asEnumerable(Linq4jTest.emps)).count());
    }

    @Test
    public void testIntersect() {
        final Linq4jTest.Employee[] emps2 = new Linq4jTest.Employee[]{ new Linq4jTest.Employee(150, "Theodore", 10), Linq4jTest.emps[3] };
        Assert.assertEquals(1, Linq4j.asEnumerable(Linq4jTest.emps).intersect(Linq4j.asEnumerable(emps2)).count());
    }

    @Test
    public void testExcept() {
        final Linq4jTest.Employee[] emps2 = new Linq4jTest.Employee[]{ new Linq4jTest.Employee(150, "Theodore", 10), Linq4jTest.emps[3] };
        Assert.assertEquals(3, Linq4j.asEnumerable(Linq4jTest.emps).except(Linq4j.asEnumerable(emps2)).count());
    }

    @Test
    public void testDistinct() {
        final Linq4jTest.Employee[] emps2 = new Linq4jTest.Employee[]{ new Linq4jTest.Employee(150, "Theodore", 10), Linq4jTest.emps[3], Linq4jTest.emps[0], Linq4jTest.emps[3] };
        Assert.assertEquals(3, Linq4j.asEnumerable(emps2).distinct().count());
    }

    @Test
    public void testDistinctWithEqualityComparer() {
        final Linq4jTest.Employee[] emps2 = new Linq4jTest.Employee[]{ new Linq4jTest.Employee(150, "Theodore", 10), Linq4jTest.emps[3], Linq4jTest.emps[1], Linq4jTest.emps[3] };
        Assert.assertEquals(2, Linq4j.asEnumerable(emps2).distinct(new org.apache.calcite.linq4j.function.EqualityComparer<Linq4jTest.Employee>() {
            public boolean equal(Linq4jTest.Employee v1, Linq4jTest.Employee v2) {
                return (v1.deptno) == (v2.deptno);
            }

            public int hashCode(Linq4jTest.Employee employee) {
                return employee.deptno;
            }
        }).count());
    }

    @Test
    public void testGroupJoin() {
        // Note #1: Group join is a "left join": "bad employees" are filtered
        // out, but empty departments are not.
        // Note #2: Order of departments is preserved.
        String s = Linq4j.asEnumerable(Linq4jTest.depts).groupJoin(Linq4j.asEnumerable(Linq4jTest.emps).concat(Linq4j.asEnumerable(Linq4jTest.badEmps)), Linq4jTest.DEPT_DEPTNO_SELECTOR, Linq4jTest.EMP_DEPTNO_SELECTOR, ( v1, v2) -> {
            final StringBuilder buf = new StringBuilder("[");
            int n = 0;
            for (org.apache.calcite.linq4j.test.Employee employee : v2) {
                if ((n++) > 0) {
                    buf.append(", ");
                }
                buf.append(employee.name);
            }
            return buf.append("] work(s) in ").append(v1.name).toString();
        }).toList().toString();
        Assert.assertEquals(("[[Fred, Eric, Janet] work(s) in Sales, " + ("[] work(s) in HR, " + "[Bill] work(s) in Marketing]")), s);
    }

    @Test
    public void testGroupJoinWithComparer() {
        // Note #1: Group join is a "left join": "bad employees" are filtered
        // out, but empty departments are not.
        // Note #2: Order of departments is preserved.
        String s = Linq4j.asEnumerable(Linq4jTest.depts).groupJoin(Linq4j.asEnumerable(Linq4jTest.emps).concat(Linq4j.asEnumerable(Linq4jTest.badEmps)), Linq4jTest.DEPT_DEPTNO_SELECTOR, Linq4jTest.EMP_DEPTNO_SELECTOR, ( v1, v2) -> {
            final StringBuilder buf = new StringBuilder("[");
            int n = 0;
            for (org.apache.calcite.linq4j.test.Employee employee : v2) {
                if ((n++) > 0) {
                    buf.append(", ");
                }
                buf.append(employee.name);
            }
            return buf.append("] work(s) in ").append(v1.name).toString();
        }, new org.apache.calcite.linq4j.function.EqualityComparer<Integer>() {
            public boolean equal(Integer v1, Integer v2) {
                return true;
            }

            public int hashCode(Integer integer) {
                return 0;
            }
        }).toList().toString();
        Assert.assertEquals("[[Fred, Bill, Eric, Janet, Cedric] work(s) in Marketing]", s);
    }

    @Test
    public void testJoin() {
        // Note #1: Inner on both sides. Employees with bad departments,
        // and departments with no employees are eliminated.
        // Note #2: Order of employees is preserved.
        String s = Linq4j.asEnumerable(Linq4jTest.emps).concat(Linq4j.asEnumerable(Linq4jTest.badEmps)).join(Linq4j.asEnumerable(Linq4jTest.depts), Linq4jTest.EMP_DEPTNO_SELECTOR, Linq4jTest.DEPT_DEPTNO_SELECTOR, ( v1, v2) -> (v1.name + " works in ") + v2.name).orderBy(Functions.identitySelector()).toList().toString();
        Assert.assertEquals(("[Bill works in Marketing, " + (("Eric works in Sales, " + "Fred works in Sales, ") + "Janet works in Sales]")), s);
    }

    @Test
    public void testLeftJoin() {
        // Note #1: Left join means emit nulls on RHS but not LHS.
        // Employees with bad departments are not eliminated;
        // departments with no employees are eliminated.
        // Note #2: Order of employees is preserved.
        String s = Linq4j.asEnumerable(Linq4jTest.emps).concat(Linq4j.asEnumerable(Linq4jTest.badEmps)).join(Linq4j.asEnumerable(Linq4jTest.depts), Linq4jTest.EMP_DEPTNO_SELECTOR, Linq4jTest.DEPT_DEPTNO_SELECTOR, ( v1, v2) -> (v1.name + " works in ") + (v2 == null ? null : v2.name), null, false, true).orderBy(Functions.identitySelector()).toList().toString();
        Assert.assertEquals(("[Bill works in Marketing, " + ((("Cedric works in null, " + "Eric works in Sales, ") + "Fred works in Sales, ") + "Janet works in Sales]")), s);
    }

    @Test
    public void testRightJoin() {
        // Note #1: Left join means emit nulls on LHS but not RHS.
        // Employees with bad departments are eliminated;
        // departments with no employees are not eliminated.
        // Note #2: Order of employees is preserved.
        String s = Linq4j.asEnumerable(Linq4jTest.emps).concat(Linq4j.asEnumerable(Linq4jTest.badEmps)).join(Linq4j.asEnumerable(Linq4jTest.depts), Linq4jTest.EMP_DEPTNO_SELECTOR, Linq4jTest.DEPT_DEPTNO_SELECTOR, ( v1, v2) -> ((v1 == null ? null : v1.name) + " works in ") + (v2 == null ? null : v2.name), null, true, false).orderBy(Functions.identitySelector()).toList().toString();
        Assert.assertEquals(("[Bill works in Marketing, " + ((("Eric works in Sales, " + "Fred works in Sales, ") + "Janet works in Sales, ") + "null works in HR]")), s);
    }

    @Test
    public void testFullJoin() {
        // Note #1: Full join means emit nulls both LHS and RHS.
        // Employees with bad departments are not eliminated;
        // departments with no employees are not eliminated.
        // Note #2: Order of employees is preserved.
        String s = Linq4j.asEnumerable(Linq4jTest.emps).concat(Linq4j.asEnumerable(Linq4jTest.badEmps)).join(Linq4j.asEnumerable(Linq4jTest.depts), Linq4jTest.EMP_DEPTNO_SELECTOR, Linq4jTest.DEPT_DEPTNO_SELECTOR, ( v1, v2) -> ((v1 == null ? null : v1.name) + " works in ") + (v2 == null ? null : v2.name), null, true, true).orderBy(Functions.identitySelector()).toList().toString();
        Assert.assertEquals(("[Bill works in Marketing, " + (((("Cedric works in null, " + "Eric works in Sales, ") + "Fred works in Sales, ") + "Janet works in Sales, ") + "null works in HR]")), s);
    }

    @Test
    public void testJoinCartesianProduct() {
        int n = Linq4j.asEnumerable(Linq4jTest.emps).<Linq4jTest.Department, Integer, Integer>join(Linq4j.asEnumerable(Linq4jTest.depts), ((Function1) (Linq4jTest.ONE_SELECTOR)), ((Function1) (Linq4jTest.ONE_SELECTOR)), ((Function2) (Linq4jTest.PAIR_SELECTOR))).count();
        Assert.assertEquals(12, n);// 4 employees times 3 departments

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCartesianProductEnumerator() {
        final Enumerable<String> abc = Linq4j.asEnumerable(Arrays.asList("a", "b", "c"));
        final Enumerable<String> xy = Linq4j.asEnumerable(Arrays.asList("x", "y"));
        final Enumerator<List<String>> productEmpty = Linq4j.product(Arrays.<Enumerator<String>>asList());
        Assert.assertTrue(productEmpty.moveNext());
        Assert.assertEquals(Arrays.<String>asList(), productEmpty.current());
        Assert.assertFalse(productEmpty.moveNext());
        final Enumerator<List<String>> product0 = Linq4j.product(Arrays.asList(Linq4j.emptyEnumerator()));
        Assert.assertFalse(product0.moveNext());
        final Enumerator<List<String>> productFullEmpty = Linq4j.product(Arrays.asList(abc.enumerator(), Linq4j.emptyEnumerator()));
        Assert.assertFalse(productFullEmpty.moveNext());
        final Enumerator<List<String>> productEmptyFull = Linq4j.product(Arrays.asList(abc.enumerator(), Linq4j.emptyEnumerator()));
        Assert.assertFalse(productEmptyFull.moveNext());
        final Enumerator<List<String>> productAbcXy = Linq4j.product(Arrays.asList(abc.enumerator(), xy.enumerator()));
        Assert.assertTrue(productAbcXy.moveNext());
        Assert.assertEquals(Arrays.asList("a", "x"), productAbcXy.current());
        Assert.assertTrue(productAbcXy.moveNext());
        Assert.assertEquals(Arrays.asList("a", "y"), productAbcXy.current());
        Assert.assertTrue(productAbcXy.moveNext());
        Assert.assertEquals(Arrays.asList("b", "x"), productAbcXy.current());
        Assert.assertTrue(productAbcXy.moveNext());
        Assert.assertTrue(productAbcXy.moveNext());
        Assert.assertTrue(productAbcXy.moveNext());
        Assert.assertFalse(productAbcXy.moveNext());
    }

    @Test
    public void testAsQueryable() {
        // "count" is an Enumerable method.
        final int n = Linq4j.asEnumerable(Linq4jTest.emps).asQueryable().count();
        Assert.assertEquals(4, n);
        // "where" is a Queryable method
        // first, use a lambda
        ParameterExpression parameter = Expressions.parameter(Linq4jTest.Employee.class);
        final Queryable<Linq4jTest.Employee> nh = Linq4j.asEnumerable(Linq4jTest.emps).asQueryable().where(Expressions.lambda(Predicate1.class, Expressions.equal(Expressions.field(parameter, Linq4jTest.Employee.class, "deptno"), Expressions.constant(10)), parameter));
        Assert.assertEquals(3, nh.count());
        // second, use an expression
        final Queryable<Linq4jTest.Employee> nh2 = Linq4j.asEnumerable(Linq4jTest.emps).asQueryable().where(Expressions.lambda(( v1) -> v1.deptno == 10));
        Assert.assertEquals(3, nh2.count());
        // use lambda, this time call whereN
        ParameterExpression parameterE = Expressions.parameter(Linq4jTest.Employee.class);
        ParameterExpression parameterN = Expressions.parameter(Integer.TYPE);
        final Queryable<Linq4jTest.Employee> nh3 = Linq4j.asEnumerable(Linq4jTest.emps).asQueryable().whereN(Expressions.lambda(((Class<Predicate2<Linq4jTest.Employee, Integer>>) ((Class) (Predicate2.class))), Expressions.andAlso(Expressions.equal(Expressions.field(parameterE, Linq4jTest.Employee.class, "deptno"), Expressions.constant(10)), Expressions.lessThan(parameterN, Expressions.constant(3))), parameterE, parameterN));
        Assert.assertEquals(2, nh3.count());
    }

    @Test
    public void testTake() {
        final Enumerable<Linq4jTest.Department> enumerableDepts = Linq4j.asEnumerable(Linq4jTest.depts);
        final List<Linq4jTest.Department> enumerableDeptsResult = enumerableDepts.take(2).toList();
        Assert.assertEquals(2, enumerableDeptsResult.size());
        Assert.assertEquals(Linq4jTest.depts[0], enumerableDeptsResult.get(0));
        Assert.assertEquals(Linq4jTest.depts[1], enumerableDeptsResult.get(1));
        final List<Linq4jTest.Department> enumerableDeptsResult5 = enumerableDepts.take(5).toList();
        Assert.assertEquals(3, enumerableDeptsResult5.size());
    }

    @Test
    public void testTakeEnumerable() {
        final Enumerable<Linq4jTest.Department> enumerableDepts = Linq4j.asEnumerable(Linq4jTest.depts);
        final List<Linq4jTest.Department> enumerableDeptsResult = EnumerableDefaults.take(enumerableDepts, 2).toList();
        Assert.assertEquals(2, enumerableDeptsResult.size());
        Assert.assertEquals(Linq4jTest.depts[0], enumerableDeptsResult.get(0));
        Assert.assertEquals(Linq4jTest.depts[1], enumerableDeptsResult.get(1));
        final List<Linq4jTest.Department> enumerableDeptsResult5 = EnumerableDefaults.take(enumerableDepts, 5).toList();
        Assert.assertEquals(3, enumerableDeptsResult5.size());
    }

    @Test
    public void testTakeQueryable() {
        final Queryable<Linq4jTest.Department> querableDepts = Linq4j.asEnumerable(Linq4jTest.depts).asQueryable();
        final List<Linq4jTest.Department> queryableResult = QueryableDefaults.take(querableDepts, 2).toList();
        Assert.assertEquals(2, queryableResult.size());
        Assert.assertEquals(Linq4jTest.depts[0], queryableResult.get(0));
        Assert.assertEquals(Linq4jTest.depts[1], queryableResult.get(1));
    }

    @Test
    public void testTakeEnumerableZeroOrNegativeSize() {
        Assert.assertEquals(0, EnumerableDefaults.take(Linq4j.asEnumerable(Linq4jTest.depts), 0).toList().size());
        Assert.assertEquals(0, EnumerableDefaults.take(Linq4j.asEnumerable(Linq4jTest.depts), (-2)).toList().size());
    }

    @Test
    public void testTakeQueryableZeroOrNegativeSize() {
        Assert.assertEquals(0, QueryableDefaults.take(Linq4j.asEnumerable(Linq4jTest.depts).asQueryable(), 0).toList().size());
        Assert.assertEquals(0, QueryableDefaults.take(Linq4j.asEnumerable(Linq4jTest.depts).asQueryable(), (-2)).toList().size());
    }

    @Test
    public void testTakeEnumerableGreaterThanLength() {
        final Enumerable<Linq4jTest.Department> enumerableDepts = Linq4j.asEnumerable(Linq4jTest.depts);
        final List<Linq4jTest.Department> depList = EnumerableDefaults.take(enumerableDepts, 5).toList();
        Assert.assertEquals(3, depList.size());
        Assert.assertEquals(Linq4jTest.depts[0], depList.get(0));
        Assert.assertEquals(Linq4jTest.depts[1], depList.get(1));
        Assert.assertEquals(Linq4jTest.depts[2], depList.get(2));
    }

    @Test
    public void testTakeQueryableGreaterThanLength() {
        final Enumerable<Linq4jTest.Department> enumerableDepts = Linq4j.asEnumerable(Linq4jTest.depts);
        final List<Linq4jTest.Department> depList = EnumerableDefaults.take(enumerableDepts, 5).toList();
        Assert.assertEquals(3, depList.size());
        Assert.assertEquals(Linq4jTest.depts[0], depList.get(0));
        Assert.assertEquals(Linq4jTest.depts[1], depList.get(1));
        Assert.assertEquals(Linq4jTest.depts[2], depList.get(2));
    }

    @Test
    public void testTakeWhileEnumerablePredicate() {
        final Enumerable<Linq4jTest.Department> enumerableDepts = Linq4j.asEnumerable(Linq4jTest.depts);
        final List<Linq4jTest.Department> deptList = EnumerableDefaults.takeWhile(enumerableDepts, ( v1) -> v1.name.contains("e")).toList();
        // Only one department:
        // 0: Sales --> true
        // 1: HR --> false
        // 2: Marketing --> never get to it (we stop after false)
        Assert.assertEquals(1, deptList.size());
        Assert.assertEquals(Linq4jTest.depts[0], deptList.get(0));
    }

    @Test
    public void testTakeWhileEnumerableFunction() {
        final Enumerable<Linq4jTest.Department> enumerableDepts = Linq4j.asEnumerable(Linq4jTest.depts);
        final List<Linq4jTest.Department> deptList = EnumerableDefaults.takeWhile(enumerableDepts, new Predicate2<Linq4jTest.Department, Integer>() {
            int index = 0;

            public boolean apply(Linq4jTest.Department v1, Integer v2) {
                // Make sure we're passed the correct indices
                Assert.assertEquals("Invalid index passed to function", ((index)++), ((int) (v2)));
                return 20 != (v1.deptno);
            }
        }).toList();
        Assert.assertEquals(1, deptList.size());
        Assert.assertEquals(Linq4jTest.depts[0], deptList.get(0));
    }

    @Test
    public void testTakeWhileQueryableFunctionExpressionPredicate() {
        final Queryable<Linq4jTest.Department> queryableDepts = Linq4j.asEnumerable(Linq4jTest.depts).asQueryable();
        Predicate1<Linq4jTest.Department> predicate = ( v1) -> "HR".equals(v1.name);
        List<Linq4jTest.Department> deptList = QueryableDefaults.takeWhile(queryableDepts, Expressions.lambda(predicate)).toList();
        Assert.assertEquals(0, deptList.size());
        predicate = ( v1) -> "Sales".equals(v1.name);
        deptList = QueryableDefaults.takeWhile(queryableDepts, Expressions.lambda(predicate)).toList();
        Assert.assertEquals(1, deptList.size());
        Assert.assertEquals(Linq4jTest.depts[0], deptList.get(0));
    }

    @Test
    public void testTakeWhileN() {
        final Queryable<Linq4jTest.Department> queryableDepts = Linq4j.asEnumerable(Linq4jTest.depts).asQueryable();
        Predicate2<Linq4jTest.Department, Integer> function2 = new Predicate2<Linq4jTest.Department, Integer>() {
            int index = 0;

            public boolean apply(Linq4jTest.Department v1, Integer v2) {
                // Make sure we're passed the correct indices
                Assert.assertEquals("Invalid index passed to function", ((index)++), ((int) (v2)));
                return v2 < 2;
            }
        };
        final List<Linq4jTest.Department> deptList = QueryableDefaults.takeWhileN(queryableDepts, Expressions.lambda(function2)).toList();
        Assert.assertEquals(2, deptList.size());
        Assert.assertEquals(Linq4jTest.depts[0], deptList.get(0));
        Assert.assertEquals(Linq4jTest.depts[1], deptList.get(1));
    }

    @Test
    public void testTakeWhileNNoMatch() {
        final Queryable<Linq4jTest.Department> queryableDepts = Linq4j.asEnumerable(Linq4jTest.depts).asQueryable();
        Predicate2<Linq4jTest.Department, Integer> function2 = Functions.falsePredicate2();
        final List<Linq4jTest.Department> deptList = QueryableDefaults.takeWhileN(queryableDepts, Expressions.lambda(function2)).toList();
        Assert.assertEquals(0, deptList.size());
    }

    @Test
    public void testSkip() {
        Assert.assertEquals(2, Linq4j.asEnumerable(Linq4jTest.depts).skip(1).count());
        Assert.assertEquals(2, Linq4j.asEnumerable(Linq4jTest.depts).skipWhile(( v1) -> v1.name.equals("Sales")).count());
        Assert.assertEquals(3, Linq4j.asEnumerable(Linq4jTest.depts).skipWhile(( v1) -> !(v1.name.equals("Sales"))).count());
        Assert.assertEquals(1, Linq4j.asEnumerable(Linq4jTest.depts).skipWhile(( v1, v2) -> (v1.name.equals("Sales")) || (v2 == 1)).count());
        Assert.assertEquals(2, Linq4j.asEnumerable(Linq4jTest.depts).skip(1).count());
        Assert.assertEquals(0, Linq4j.asEnumerable(Linq4jTest.depts).skip(5).count());
        Assert.assertEquals(1, Linq4j.asEnumerable(Linq4jTest.depts).skipWhile(( v1, v2) -> (v1.name.equals("Sales")) || (v2 == 1)).count());
        Assert.assertEquals(2, Linq4j.asEnumerable(Linq4jTest.depts).asQueryable().skip(1).count());
        Assert.assertEquals(0, Linq4j.asEnumerable(Linq4jTest.depts).asQueryable().skip(5).count());
        Assert.assertEquals(1, Linq4j.asEnumerable(Linq4jTest.depts).asQueryable().skipWhileN(Expressions.lambda(( v1, v2) -> (v1.name.equals("Sales")) || (v2 == 1))).count());
    }

    @Test
    public void testOrderBy() {
        // Note: sort is stable. Records occur Fred, Eric, Janet in input.
        Assert.assertEquals(("[Employee(name: Fred, deptno:10)," + ((" Employee(name: Eric, deptno:10)," + " Employee(name: Janet, deptno:10),") + " Employee(name: Bill, deptno:30)]")), Linq4j.asEnumerable(Linq4jTest.emps).orderBy(Linq4jTest.EMP_DEPTNO_SELECTOR).toList().toString());
    }

    @Test
    public void testOrderByComparator() {
        Assert.assertEquals(("[Employee(name: Bill, deptno:30)," + ((" Employee(name: Eric, deptno:10)," + " Employee(name: Fred, deptno:10),") + " Employee(name: Janet, deptno:10)]")), Linq4j.asEnumerable(Linq4jTest.emps).orderBy(Linq4jTest.EMP_NAME_SELECTOR).orderBy(Linq4jTest.EMP_DEPTNO_SELECTOR, Collections.reverseOrder()).toList().toString());
    }

    @Test
    public void testOrderByInSeries() {
        // OrderBy in series works because sort is stable.
        Assert.assertEquals(("[Employee(name: Eric, deptno:10)," + ((" Employee(name: Fred, deptno:10)," + " Employee(name: Janet, deptno:10),") + " Employee(name: Bill, deptno:30)]")), Linq4j.asEnumerable(Linq4jTest.emps).orderBy(Linq4jTest.EMP_NAME_SELECTOR).orderBy(Linq4jTest.EMP_DEPTNO_SELECTOR).toList().toString());
    }

    @Test
    public void testOrderByDescending() {
        Assert.assertEquals(("[Employee(name: Janet, deptno:10)," + ((" Employee(name: Fred, deptno:10)," + " Employee(name: Eric, deptno:10),") + " Employee(name: Bill, deptno:30)]")), Linq4j.asEnumerable(Linq4jTest.emps).orderByDescending(Linq4jTest.EMP_NAME_SELECTOR).toList().toString());
    }

    @Test
    public void testReverse() {
        Assert.assertEquals(("[Employee(name: Janet, deptno:10)," + ((" Employee(name: Eric, deptno:10)," + " Employee(name: Bill, deptno:30),") + " Employee(name: Fred, deptno:10)]")), Linq4j.asEnumerable(Linq4jTest.emps).reverse().toList().toString());
    }

    @Test
    public void testList0() {
        final List<Linq4jTest.Employee> employees = Arrays.asList(new Linq4jTest.Employee(100, "Fred", 10), new Linq4jTest.Employee(110, "Bill", 30), new Linq4jTest.Employee(120, "Eric", 10), new Linq4jTest.Employee(130, "Janet", 10));
        final List<Linq4jTest.Employee> result = new ArrayList<>();
        Linq4j.asEnumerable(employees).where(( e) -> e.name.contains("e")).into(result);
        Assert.assertEquals("[Employee(name: Fred, deptno:10), Employee(name: Janet, deptno:10)]", result.toString());
    }

    @Test
    public void testList() {
        final List<Linq4jTest.Employee> employees = Arrays.asList(new Linq4jTest.Employee(100, "Fred", 10), new Linq4jTest.Employee(110, "Bill", 30), new Linq4jTest.Employee(120, "Eric", 10), new Linq4jTest.Employee(130, "Janet", 10));
        final Map<Linq4jTest.Employee, Linq4jTest.Department> empDepts = new HashMap<>();
        for (Linq4jTest.Employee employee : employees) {
            empDepts.put(employee, Linq4jTest.depts[(((employee.deptno) - 10) / 10)]);
        }
        final List<Grouping<Object, Map.Entry<Linq4jTest.Employee, Linq4jTest.Department>>> result = new ArrayList<>();
        Linq4j.asEnumerable(empDepts.entrySet()).groupBy(((Function1<Map.Entry<Linq4jTest.Employee, Linq4jTest.Department>, Object>) (Map.Entry::getValue))).into(result);
        Assert.assertNotNull(result.toString());
    }

    @Test
    public void testList2() {
        final List<String> experience = Arrays.asList("jimi", "mitch", "noel");
        final Enumerator<String> enumerator = Linq4j.enumerator(experience);
        Assert.assertThat(enumerator.getClass().getName(), CoreMatchers.endsWith("ListEnumerator"));
        Assert.assertThat(Linq4jTest.count(enumerator), CoreMatchers.equalTo(3));
        final Enumerable<String> listEnumerable = Linq4j.asEnumerable(experience);
        final Enumerator<String> listEnumerator = listEnumerable.enumerator();
        Assert.assertThat(listEnumerator.getClass().getName(), CoreMatchers.endsWith("ListEnumerator"));
        Assert.assertThat(Linq4jTest.count(listEnumerator), CoreMatchers.equalTo(3));
        final Enumerable<String> linkedListEnumerable = Linq4j.asEnumerable(Lists.newLinkedList(experience));
        final Enumerator<String> iterableEnumerator = linkedListEnumerable.enumerator();
        Assert.assertThat(iterableEnumerator.getClass().getName(), CoreMatchers.endsWith("IterableEnumerator"));
        Assert.assertThat(Linq4jTest.count(iterableEnumerator), CoreMatchers.equalTo(3));
    }

    @Test
    public void testDefaultIfEmpty() {
        final List<String> experience = Arrays.asList("jimi", "mitch", "noel");
        final Enumerable<String> notEmptyEnumerable = Linq4j.asEnumerable(experience).defaultIfEmpty();
        final Enumerator<String> notEmptyEnumerator = notEmptyEnumerable.enumerator();
        notEmptyEnumerator.moveNext();
        Assert.assertEquals("jimi", notEmptyEnumerator.current());
        notEmptyEnumerator.moveNext();
        Assert.assertEquals("mitch", notEmptyEnumerator.current());
        notEmptyEnumerator.moveNext();
        Assert.assertEquals("noel", notEmptyEnumerator.current());
        final Enumerable<String> emptyEnumerable = Linq4j.asEnumerable(Linq4j.<String>emptyEnumerable()).defaultIfEmpty();
        final Enumerator<String> emptyEnumerator = emptyEnumerable.enumerator();
        Assert.assertTrue(emptyEnumerator.moveNext());
        Assert.assertNull(emptyEnumerator.current());
        Assert.assertFalse(emptyEnumerator.moveNext());
    }

    @Test
    public void testDefaultIfEmpty2() {
        final List<String> experience = Arrays.asList("jimi", "mitch", "noel");
        final Enumerable<String> notEmptyEnumerable = Linq4j.asEnumerable(experience).defaultIfEmpty("dummy");
        final Enumerator<String> notEmptyEnumerator = notEmptyEnumerable.enumerator();
        notEmptyEnumerator.moveNext();
        Assert.assertEquals("jimi", notEmptyEnumerator.current());
        notEmptyEnumerator.moveNext();
        Assert.assertEquals("mitch", notEmptyEnumerator.current());
        notEmptyEnumerator.moveNext();
        Assert.assertEquals("noel", notEmptyEnumerator.current());
        final Enumerable<String> emptyEnumerable = Linq4j.asEnumerable(Linq4j.<String>emptyEnumerable()).defaultIfEmpty("N/A");
        final Enumerator<String> emptyEnumerator = emptyEnumerable.enumerator();
        Assert.assertTrue(emptyEnumerator.moveNext());
        Assert.assertEquals("N/A", emptyEnumerator.current());
        Assert.assertFalse(emptyEnumerator.moveNext());
    }

    @Test
    public void testElementAt() {
        final Enumerable<String> enumerable = Linq4j.asEnumerable(Arrays.asList("jimi", "mitch"));
        Assert.assertEquals("jimi", enumerable.elementAt(0));
        try {
            enumerable.elementAt(2);
            Assert.fail();
        } catch (Exception ignored) {
            // ok
        }
        try {
            enumerable.elementAt((-1));
            Assert.fail();
        } catch (Exception ignored) {
            // ok
        }
    }

    @Test
    public void testElementAtWithoutList() {
        final Enumerable<String> enumerable = Linq4j.asEnumerable(Collections.unmodifiableCollection(Arrays.asList("jimi", "mitch")));
        Assert.assertEquals("jimi", enumerable.elementAt(0));
        try {
            enumerable.elementAt(2);
            Assert.fail();
        } catch (Exception ignored) {
            // ok
        }
        try {
            enumerable.elementAt((-1));
            Assert.fail();
        } catch (Exception ignored) {
            // ok
        }
    }

    @Test
    public void testElementAtOrDefault() {
        final Enumerable<String> enumerable = Linq4j.asEnumerable(Arrays.asList("jimi", "mitch"));
        Assert.assertEquals("jimi", enumerable.elementAtOrDefault(0));
        Assert.assertNull(enumerable.elementAtOrDefault(2));
        Assert.assertNull(enumerable.elementAtOrDefault((-1)));
    }

    @Test
    public void testElementAtOrDefaultWithoutList() {
        final Enumerable<String> enumerable = Linq4j.asEnumerable(Collections.unmodifiableCollection(Arrays.asList("jimi", "mitch")));
        Assert.assertEquals("jimi", enumerable.elementAt(0));
        try {
            enumerable.elementAt(2);
            Assert.fail();
        } catch (Exception ignored) {
            // ok
        }
        try {
            enumerable.elementAt((-1));
            Assert.fail();
        } catch (Exception ignored) {
            // ok
        }
    }

    @Test
    public void testLast() {
        final Enumerable<String> enumerable = Linq4j.asEnumerable(Arrays.asList("jimi", "mitch"));
        Assert.assertEquals("mitch", enumerable.last());
        final Enumerable<?> emptyEnumerable = Linq4j.asEnumerable(Collections.EMPTY_LIST);
        try {
            emptyEnumerable.last();
            Assert.fail();
        } catch (Exception ignored) {
            // ok
        }
    }

    @Test
    public void testLastWithoutList() {
        final Enumerable<String> enumerable = Linq4j.asEnumerable(Collections.unmodifiableCollection(Arrays.asList("jimi", "noel", "mitch")));
        Assert.assertEquals("mitch", enumerable.last());
    }

    @Test
    public void testLastOrDefault() {
        final Enumerable<String> enumerable = Linq4j.asEnumerable(Arrays.asList("jimi", "mitch"));
        Assert.assertEquals("mitch", enumerable.lastOrDefault());
        final Enumerable<?> emptyEnumerable = Linq4j.asEnumerable(Collections.EMPTY_LIST);
        Assert.assertNull(emptyEnumerable.lastOrDefault());
    }

    @Test
    public void testLastWithPredicate() {
        final Enumerable<String> enumerable = Linq4j.asEnumerable(Arrays.asList("jimi", "mitch", "ming"));
        Assert.assertEquals("mitch", enumerable.last(( x) -> x.startsWith("mit")));
        try {
            enumerable.last(( x) -> false);
            Assert.fail();
        } catch (Exception ignored) {
            // ok
        }
        @SuppressWarnings("unchecked")
        final Enumerable<String> emptyEnumerable = Linq4j.asEnumerable(Collections.EMPTY_LIST);
        try {
            emptyEnumerable.last(( x) -> {
                fail();
                return false;
            });
            Assert.fail();
        } catch (Exception ignored) {
            // ok
        }
    }

    @Test
    public void testLastOrDefaultWithPredicate() {
        final Enumerable<String> enumerable = Linq4j.asEnumerable(Arrays.asList("jimi", "mitch", "ming"));
        Assert.assertEquals("mitch", enumerable.lastOrDefault(( x) -> x.startsWith("mit")));
        Assert.assertNull(enumerable.lastOrDefault(( x) -> false));
        @SuppressWarnings("unchecked")
        final Enumerable<String> emptyEnumerable = Linq4j.asEnumerable(Collections.EMPTY_LIST);
        Assert.assertNull(emptyEnumerable.lastOrDefault(( x) -> {
            fail();
            return false;
        }));
    }

    @Test
    public void testSelectManyWithIndexableSelector() {
        final int[] indexRef = new int[]{ 0 };
        final List<String> nameSeqs = Linq4j.asEnumerable(Linq4jTest.depts).selectMany(( element, index) -> {
            assertEquals(indexRef[0], index.longValue());
            indexRef[0] = index + 1;
            return Linq4j.asEnumerable(element.employees);
        }).select(( v1, v2) -> (("#" + v2) + ": ") + v1.name).toList();
        Assert.assertEquals("[#0: Fred, #1: Eric, #2: Janet, #3: Bill]", nameSeqs.toString());
    }

    @Test
    public void testSelectManyWithResultSelector() {
        final List<String> nameSeqs = Linq4j.asEnumerable(Linq4jTest.depts).selectMany(Linq4jTest.DEPT_EMPLOYEES_SELECTOR, ( element, subElement) -> (subElement.name + "@") + element.name).select(( v0, v1) -> (("#" + v1) + ": ") + v0).toList();
        Assert.assertEquals("[#0: Fred@Sales, #1: Eric@Sales, #2: Janet@Sales, #3: Bill@Marketing]", nameSeqs.toString());
    }

    @Test
    public void testSelectManyWithIndexableSelectorAndResultSelector() {
        final int[] indexRef = new int[]{ 0 };
        final List<String> nameSeqs = Linq4j.asEnumerable(Linq4jTest.depts).selectMany(( element, index) -> {
            assertEquals(indexRef[0], index.longValue());
            indexRef[0] = index + 1;
            return Linq4j.asEnumerable(element.employees);
        }, ( element, subElement) -> (subElement.name + "@") + element.name).select(( v0, v1) -> (("#" + v1) + ": ") + v0).toList();
        Assert.assertEquals("[#0: Fred@Sales, #1: Eric@Sales, #2: Janet@Sales, #3: Bill@Marketing]", nameSeqs.toString());
    }

    @Test
    public void testSequenceEqual() {
        final Enumerable<String> enumerable1 = Linq4j.asEnumerable(Collections.unmodifiableCollection(Arrays.asList("ming", "foo", "bar")));
        final Enumerable<String> enumerable2 = Linq4j.asEnumerable(Collections.unmodifiableCollection(Arrays.asList("ming", "foo", "bar")));
        Assert.assertTrue(enumerable1.sequenceEqual(enumerable2));
        Assert.assertFalse(enumerable1.sequenceEqual(Linq4j.asEnumerable(new String[]{ "ming", "foo", "far" })));
        try {
            EnumerableDefaults.sequenceEqual(null, enumerable2);
            Assert.fail();
        } catch (NullPointerException ignored) {
            // ok
        }
        try {
            EnumerableDefaults.sequenceEqual(enumerable1, null);
            Assert.fail();
        } catch (NullPointerException ignored) {
            // ok
        }
        Assert.assertFalse(// Keep as collection
        Linq4j.asEnumerable(enumerable1.skip(1).toList()).sequenceEqual(enumerable2));
        Assert.assertFalse(enumerable1.sequenceEqual(Linq4j.asEnumerable(enumerable2.skip(1).toList())));// Keep as collection

    }

    @Test
    public void testSequenceEqualWithoutCollection() {
        final Enumerable<String> enumerable1 = Linq4j.asEnumerable(() -> Arrays.asList("ming", "foo", "bar").iterator());
        final Enumerable<String> enumerable2 = Linq4j.asEnumerable(() -> Arrays.asList("ming", "foo", "bar").iterator());
        Assert.assertTrue(enumerable1.sequenceEqual(enumerable2));
        Assert.assertFalse(enumerable1.sequenceEqual(Linq4j.asEnumerable(() -> Arrays.asList("ming", "foo", "far").iterator())));
        try {
            EnumerableDefaults.sequenceEqual(null, enumerable2);
            Assert.fail();
        } catch (NullPointerException ignored) {
            // ok
        }
        try {
            EnumerableDefaults.sequenceEqual(enumerable1, null);
            Assert.fail();
        } catch (NullPointerException ignored) {
            // ok
        }
        Assert.assertFalse(enumerable1.skip(1).sequenceEqual(enumerable2));
        Assert.assertFalse(enumerable1.sequenceEqual(enumerable2.skip(1)));
    }

    @Test
    public void testSequenceEqualWithComparer() {
        final Enumerable<String> enumerable1 = Linq4j.asEnumerable(Collections.unmodifiableCollection(Arrays.asList("ming", "foo", "bar")));
        final Enumerable<String> enumerable2 = Linq4j.asEnumerable(Collections.unmodifiableCollection(Arrays.asList("ming", "foo", "bar")));
        final org.apache.calcite.linq4j.function.EqualityComparer<String> equalityComparer = new org.apache.calcite.linq4j.function.EqualityComparer<String>() {
            public boolean equal(String v1, String v2) {
                return !(Objects.equals(v1, v2));// reverse the equality.

            }

            public int hashCode(String s) {
                return Objects.hashCode(s);
            }
        };
        Assert.assertFalse(enumerable1.sequenceEqual(enumerable2, equalityComparer));
        Assert.assertTrue(enumerable1.sequenceEqual(Linq4j.asEnumerable(Arrays.asList("fun", "lol", "far")), equalityComparer));
        try {
            EnumerableDefaults.sequenceEqual(null, enumerable2);
            Assert.fail();
        } catch (NullPointerException ignored) {
            // ok
        }
        try {
            EnumerableDefaults.sequenceEqual(enumerable1, null);
            Assert.fail();
        } catch (NullPointerException ignored) {
            // ok
        }
        Assert.assertFalse(// Keep as collection
        Linq4j.asEnumerable(enumerable1.skip(1).toList()).sequenceEqual(enumerable2));
        Assert.assertFalse(enumerable1.sequenceEqual(Linq4j.asEnumerable(enumerable2.skip(1).toList())));// Keep as collection

    }

    @Test
    public void testSequenceEqualWithComparerWithoutCollection() {
        final Enumerable<String> enumerable1 = Linq4j.asEnumerable(() -> Arrays.asList("ming", "foo", "bar").iterator());
        final Enumerable<String> enumerable2 = Linq4j.asEnumerable(() -> Arrays.asList("ming", "foo", "bar").iterator());
        final org.apache.calcite.linq4j.function.EqualityComparer<String> equalityComparer = new org.apache.calcite.linq4j.function.EqualityComparer<String>() {
            public boolean equal(String v1, String v2) {
                return !(Objects.equals(v1, v2));// reverse the equality.

            }

            public int hashCode(String s) {
                return Objects.hashCode(s);
            }
        };
        Assert.assertFalse(enumerable1.sequenceEqual(enumerable2, equalityComparer));
        final Enumerable<String> enumerable3 = Linq4j.asEnumerable(() -> Arrays.asList("fun", "lol", "far").iterator());
        Assert.assertTrue(enumerable1.sequenceEqual(enumerable3, equalityComparer));
        try {
            EnumerableDefaults.sequenceEqual(null, enumerable2);
            Assert.fail();
        } catch (NullPointerException ignored) {
            // ok
        }
        try {
            EnumerableDefaults.sequenceEqual(enumerable1, null);
            Assert.fail();
        } catch (NullPointerException ignored) {
            // ok
        }
        Assert.assertFalse(enumerable1.skip(1).sequenceEqual(enumerable2));
        Assert.assertFalse(enumerable1.sequenceEqual(enumerable2.skip(1)));
    }

    @Test
    public void testGroupByWithKeySelector() {
        String s = Linq4j.asEnumerable(Linq4jTest.emps).groupBy(Linq4jTest.EMP_DEPTNO_SELECTOR).select(( group) -> String.format(Locale.ROOT, "%s: %s", group.getKey(), stringJoin("+", group.select(( element) -> element.name)))).toList().toString();
        Assert.assertThat(s, CoreMatchers.is("[10: Fred+Eric+Janet, 30: Bill]"));
    }

    @Test
    public void testGroupByWithKeySelectorAndComparer() {
        String s = Linq4j.asEnumerable(Linq4jTest.emps).groupBy(Linq4jTest.EMP_DEPTNO_SELECTOR, new org.apache.calcite.linq4j.function.EqualityComparer<Integer>() {
            public boolean equal(Integer v1, Integer v2) {
                return true;
            }

            public int hashCode(Integer integer) {
                return 0;
            }
        }).select(( group) -> String.format(Locale.ROOT, "%s: %s", group.getKey(), stringJoin("+", group.select(( element) -> element.name)))).toList().toString();
        Assert.assertThat(s, CoreMatchers.is("[10: Fred+Bill+Eric+Janet]"));
    }

    @Test
    public void testGroupByWithKeySelectorAndElementSelector() {
        String s = Linq4j.asEnumerable(Linq4jTest.emps).groupBy(Linq4jTest.EMP_DEPTNO_SELECTOR, Linq4jTest.EMP_NAME_SELECTOR).select(( group) -> String.format(Locale.ROOT, "%s: %s", group.getKey(), stringJoin("+", group))).toList().toString();
        Assert.assertThat(s, CoreMatchers.is("[10: Fred+Eric+Janet, 30: Bill]"));
    }

    @Test
    public void testGroupByWithKeySelectorAndElementSelectorAndComparer() {
        String s = Linq4j.asEnumerable(Linq4jTest.emps).groupBy(Linq4jTest.EMP_DEPTNO_SELECTOR, Linq4jTest.EMP_NAME_SELECTOR, new org.apache.calcite.linq4j.function.EqualityComparer<Integer>() {
            public boolean equal(Integer v1, Integer v2) {
                return true;
            }

            public int hashCode(Integer integer) {
                return 0;
            }
        }).select(( group) -> String.format(Locale.ROOT, "%s: %s", group.getKey(), stringJoin("+", group))).toList().toString();
        Assert.assertEquals("[10: Fred+Bill+Eric+Janet]", s);
    }

    @Test
    public void testGroupByWithKeySelectorAndResultSelector() {
        String s = Linq4j.asEnumerable(Linq4jTest.emps).groupBy(Linq4jTest.EMP_DEPTNO_SELECTOR, ( key, group) -> String.format(Locale.ROOT, "%s: %s", key, stringJoin("+", group.select(( element) -> element.name)))).toList().toString();
        Assert.assertEquals("[10: Fred+Eric+Janet, 30: Bill]", s);
    }

    @Test
    public void testGroupByWithKeySelectorAndResultSelectorAndComparer() {
        String s = Linq4j.asEnumerable(Linq4jTest.emps).groupBy(Linq4jTest.EMP_DEPTNO_SELECTOR, ( key, group) -> String.format(Locale.ROOT, "%s: %s", key, stringJoin("+", group.select(( element) -> element.name))), new org.apache.calcite.linq4j.function.EqualityComparer<Integer>() {
            public boolean equal(Integer v1, Integer v2) {
                return true;
            }

            public int hashCode(Integer integer) {
                return 0;
            }
        }).toList().toString();
        Assert.assertEquals("[10: Fred+Bill+Eric+Janet]", s);
    }

    @Test
    public void testGroupByWithKeySelectorAndElementSelectorAndResultSelector() {
        String s = Linq4j.asEnumerable(Linq4jTest.emps).groupBy(Linq4jTest.EMP_DEPTNO_SELECTOR, Linq4jTest.EMP_NAME_SELECTOR, ( key, group) -> String.format(Locale.ROOT, "%s: %s", key, stringJoin("+", group))).toList().toString();
        Assert.assertEquals("[10: Fred+Eric+Janet, 30: Bill]", s);
    }

    @Test
    public void testGroupByWithKeySelectorAndElementSelectorAndResultSelectorAndComparer() {
        String s = Linq4j.asEnumerable(Linq4jTest.emps).groupBy(Linq4jTest.EMP_DEPTNO_SELECTOR, Linq4jTest.EMP_NAME_SELECTOR, ( key, group) -> String.format(Locale.ROOT, "%s: %s", key, stringJoin("+", group)), new org.apache.calcite.linq4j.function.EqualityComparer<Integer>() {
            public boolean equal(Integer v1, Integer v2) {
                return true;
            }

            public int hashCode(Integer integer) {
                return 0;
            }
        }).toList().toString();
        Assert.assertEquals("[10: Fred+Bill+Eric+Janet]", s);
    }

    @Test
    public void testZip() {
        final Enumerable<String> e1 = Linq4j.asEnumerable(Arrays.asList("a", "b", "c"));
        final Enumerable<String> e2 = Linq4j.asEnumerable(Arrays.asList("1", "2", "3"));
        final Enumerable<String> zipped = e1.zip(e2, ( v0, v1) -> v0 + v1);
        Assert.assertEquals(3, zipped.count());
        zipped.enumerator().reset();
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals((("" + ((char) ('a' + i))) + ((char) ('1' + i))), zipped.elementAt(i));
        }
    }

    @Test
    public void testZipLengthNotMatch() {
        final Enumerable<String> e1 = Linq4j.asEnumerable(Arrays.asList("a", "b"));
        final Enumerable<String> e2 = Linq4j.asEnumerable(Arrays.asList("1", "2", "3"));
        final Function2<String, String, String> resultSelector = ( v0, v1) -> v0 + v1;
        final Enumerable<String> zipped1 = e1.zip(e2, resultSelector);
        Assert.assertEquals(2, zipped1.count());
        Assert.assertEquals(2, Linq4jTest.count(zipped1.enumerator()));
        zipped1.enumerator().reset();
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals((("" + ((char) ('a' + i))) + ((char) ('1' + i))), zipped1.elementAt(i));
        }
        final Enumerable<String> zipped2 = e2.zip(e1, resultSelector);
        Assert.assertEquals(2, zipped2.count());
        Assert.assertEquals(2, Linq4jTest.count(zipped2.enumerator()));
        zipped2.enumerator().reset();
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals((("" + ((char) ('1' + i))) + ((char) ('a' + i))), zipped2.elementAt(i));
        }
    }

    @Test
    public void testExample() {
        Linq4jExample.main(new String[0]);
    }

    /**
     * We use BigDecimal to represent literals of float and double using
     * BigDecimal, because we want an exact representation.
     */
    @Test
    public void testApproxConstant() {
        ConstantExpression c;
        c = Expressions.constant(new BigDecimal("3.1"), float.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("3.1F"));
        c = Expressions.constant(new BigDecimal("-5.156"), float.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("-5.156F"));
        c = Expressions.constant(new BigDecimal("-51.6"), Float.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("Float.valueOf(-51.6F)"));
        c = Expressions.constant(new BigDecimal(Float.MAX_VALUE), Float.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("Float.valueOf(Float.intBitsToFloat(2139095039))"));
        c = Expressions.constant(new BigDecimal(Float.MIN_VALUE), Float.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("Float.valueOf(Float.intBitsToFloat(1))"));
        c = Expressions.constant(new BigDecimal("3.1"), double.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("3.1D"));
        c = Expressions.constant(new BigDecimal("-5.156"), double.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("-5.156D"));
        c = Expressions.constant(new BigDecimal("-51.6"), Double.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("Double.valueOf(-51.6D)"));
        c = Expressions.constant(new BigDecimal(Double.MAX_VALUE), Double.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("Double.valueOf(Double.longBitsToDouble(9218868437227405311L))"));
        c = Expressions.constant(new BigDecimal(Double.MIN_VALUE), Double.class);
        Assert.assertThat(Expressions.toString(c), CoreMatchers.equalTo("Double.valueOf(Double.longBitsToDouble(1L))"));
    }

    /**
     * Employee.
     */
    public static class Employee {
        public final int empno;

        public final String name;

        public final int deptno;

        public Employee(int empno, String name, int deptno) {
            this.empno = empno;
            this.name = name;
            this.deptno = deptno;
        }

        public String toString() {
            return ((("Employee(name: " + (name)) + ", deptno:") + (deptno)) + ")";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (deptno);
            result = (prime * result) + (empno);
            result = (prime * result) + ((name) == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            Linq4jTest.Employee other = ((Linq4jTest.Employee) (obj));
            if ((deptno) != (other.deptno)) {
                return false;
            }
            if ((empno) != (other.empno)) {
                return false;
            }
            if ((name) == null) {
                if ((other.name) != null) {
                    return false;
                }
            } else
                if (!(name.equals(other.name))) {
                    return false;
                }

            return true;
        }
    }

    /**
     * Department.
     */
    public static class Department {
        public final String name;

        public final int deptno;

        public final List<Linq4jTest.Employee> employees;

        public Department(String name, int deptno, List<Linq4jTest.Employee> employees) {
            this.name = name;
            this.deptno = deptno;
            this.employees = employees;
        }

        public String toString() {
            return ((((("Department(name: " + (name)) + ", deptno:") + (deptno)) + ", employees: ") + (employees)) + ")";
        }
    }

    // Cedric works in a non-existent department.
    // CHECKSTYLE: IGNORE 1
    public static final Linq4jTest.Employee[] badEmps = new Linq4jTest.Employee[]{ new Linq4jTest.Employee(140, "Cedric", 40) };

    // CHECKSTYLE: IGNORE 1
    public static final Linq4jTest.Employee[] emps = new Linq4jTest.Employee[]{ new Linq4jTest.Employee(100, "Fred", 10), new Linq4jTest.Employee(110, "Bill", 30), new Linq4jTest.Employee(120, "Eric", 10), new Linq4jTest.Employee(130, "Janet", 10) };

    // CHECKSTYLE: IGNORE 1
    public static final Linq4jTest.Department[] depts = new Linq4jTest.Department[]{ new Linq4jTest.Department("Sales", 10, Arrays.asList(Linq4jTest.emps[0], Linq4jTest.emps[2], Linq4jTest.emps[3])), new Linq4jTest.Department("HR", 20, ImmutableList.of()), new Linq4jTest.Department("Marketing", 30, ImmutableList.of(Linq4jTest.emps[1])) };
}

/**
 * End Linq4jTest.java
 */
