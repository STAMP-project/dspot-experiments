/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vall?e-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot.toolkits.exceptions;


import ThrowableSet.Manager;
import ThrowableSet.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import soot.AnySubType;
import soot.G;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;


/* // Suite that uses a prescribed order, rather than whatever
// order reflection produces.
public static Test cannedSuite() {
TestSuite suite = new TestSuite();
suite.addTest(new ThrowableSetTest("testInitialState"));
suite.addTest(new ThrowableSetTest("testSingleInstance0"));
suite.addTest(new ThrowableSetTest("testSingleInstance1"));
suite.addTest(new ThrowableSetTest("testAddingSubclasses"));
suite.addTest(new ThrowableSetTest("testAddingSets0"));
suite.addTest(new ThrowableSetTest("testAddingSets1"));
TestSetup setup = new ThrowableSetTestSetup(suite);
return setup;
}


public static Test reflectionSuite() {
TestSuite suite = new TestSuite(ThrowableSetTest.class);
TestSetup setup = new ThrowableSetTestSetup(suite);
return setup;
}

public static Test suite() {
Scene.v().loadBasicClasses();
return reflectionSuite();
}

public static void main(String arg[]) {
if (arg.length > 0) {
jdkLocation = arg[0];
}
Scene.v().loadBasicClasses();
junit.textui.TestRunner.run(reflectionSuite());
System.out.println(ThrowableSet.Manager.v().reportInstrumentation());
}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ThrowableSetTest {
    static {
        G.reset();
        Scene.v().loadBasicClasses();
    }

    static final boolean DUMP_INTERNALS = false;

    final Manager mgr = Manager.v();

    // A class for verifying that the sizeToSetsMap
    // follows our expectations.
    static class ExpectedSizeToSets {
        private Map<Integer, Set<ThrowableSetTest.ExpectedSizeToSets.SetPair>> expectedMap = new HashMap<Integer, Set<ThrowableSetTest.ExpectedSizeToSets.SetPair>>();// from Integer to Set.


        private static class SetPair {
            Set<RefLikeType> included;

            Set<AnySubType> excluded;

            SetPair(Set<RefLikeType> included, Set<AnySubType> excluded) {
                this.included = included;
                this.excluded = excluded;
            }

            @Override
            public boolean equals(Object o) {
                if (o == (this)) {
                    return true;
                }
                if (!(o instanceof ThrowableSetTest.ExpectedSizeToSets.SetPair)) {
                    return false;
                }
                ThrowableSetTest.ExpectedSizeToSets.SetPair sp = ((ThrowableSetTest.ExpectedSizeToSets.SetPair) (o));
                return (this.included.equals(sp.included)) && (this.excluded.equals(sp.excluded));
            }

            @Override
            public int hashCode() {
                int result = 31;
                result = (37 * result) + (included.hashCode());
                result = (37 * result) + (excluded.hashCode());
                return result;
            }

            @Override
            public String toString() {
                return (((((((super.toString()) + (System.getProperty("line.separator"))) + "+[") + (included.toString())) + ']') + "-[") + (excluded.toString())) + ']';
            }
        }

        ExpectedSizeToSets() {
            // The empty set.
            this.add(Collections.<RefLikeType>emptySet(), Collections.<AnySubType>emptySet());
            // All Throwables set.
            Set<RefLikeType> temp = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>();
            temp.add(AnySubType.v(Scene.v().getRefType("java.lang.Throwable")));
            this.add(temp, Collections.<AnySubType>emptySet());
            // VM errors set.
            temp = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>();
            temp.add(Scene.v().getRefType("java.lang.InternalError"));
            temp.add(Scene.v().getRefType("java.lang.OutOfMemoryError"));
            temp.add(Scene.v().getRefType("java.lang.StackOverflowError"));
            temp.add(Scene.v().getRefType("java.lang.UnknownError"));
            temp.add(Scene.v().getRefType("java.lang.ThreadDeath"));
            this.add(temp, Collections.<AnySubType>emptySet());
            // Resolve Class errors set.
            Set<RefLikeType> classErrors = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>();
            classErrors.add(Scene.v().getRefType("java.lang.ClassCircularityError"));
            classErrors.add(AnySubType.v(Scene.v().getRefType("java.lang.ClassFormatError")));
            classErrors.add(Scene.v().getRefType("java.lang.IllegalAccessError"));
            classErrors.add(Scene.v().getRefType("java.lang.IncompatibleClassChangeError"));
            classErrors.add(Scene.v().getRefType("java.lang.LinkageError"));
            classErrors.add(Scene.v().getRefType("java.lang.NoClassDefFoundError"));
            classErrors.add(Scene.v().getRefType("java.lang.VerifyError"));
            this.add(classErrors, Collections.<AnySubType>emptySet());
            // Resolve Field errors set.
            temp = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(classErrors);
            temp.add(Scene.v().getRefType("java.lang.NoSuchFieldError"));
            this.add(temp, Collections.<AnySubType>emptySet());
            // Resolve method errors set.
            temp = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(classErrors);
            temp.add(Scene.v().getRefType("java.lang.AbstractMethodError"));
            temp.add(Scene.v().getRefType("java.lang.NoSuchMethodError"));
            temp.add(Scene.v().getRefType("java.lang.UnsatisfiedLinkError"));
            this.add(temp, Collections.<AnySubType>emptySet());
            // Initialization errors set.
            temp = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>();
            temp.add(AnySubType.v(Scene.v().getRefType("java.lang.Error")));
            this.add(temp, Collections.<AnySubType>emptySet());
        }

        void add(Set<RefLikeType> inclusions, Set<AnySubType> exclusions) {
            int key = (inclusions.size()) + (exclusions.size());
            Set<ThrowableSetTest.ExpectedSizeToSets.SetPair> values = expectedMap.get(key);
            if (values == null) {
                values = new HashSet<ThrowableSetTest.ExpectedSizeToSets.SetPair>();
                expectedMap.put(key, values);
            }
            // Make sure we have our own copies of the sets.
            values.add(newSetPair(inclusions, exclusions));
        }

        void addAndCheck(Set<RefLikeType> inclusions, Set<AnySubType> exclusions) {
            add(inclusions, exclusions);
            Assert.assertTrue(match());
        }

        ThrowableSetTest.ExpectedSizeToSets.SetPair newSetPair(Collection<RefLikeType> inclusions, Collection<AnySubType> exclusions) {
            return new ThrowableSetTest.ExpectedSizeToSets.SetPair(new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(inclusions), new ExceptionTestUtility.ExceptionHashSet<AnySubType>(exclusions));
        }

        boolean match() {
            final Collection<ThrowableSet> toCompare = Manager.v().getThrowableSets();
            int sum = 0;
            for (Collection<ThrowableSetTest.ExpectedSizeToSets.SetPair> expectedValues : expectedMap.values()) {
                sum += expectedValues.size();
            }
            Assert.assertEquals(sum, toCompare.size());
            for (ThrowableSet actual : toCompare) {
                Collection<RefLikeType> included = actual.typesIncluded();
                Collection<AnySubType> excluded = actual.typesExcluded();
                ThrowableSetTest.ExpectedSizeToSets.SetPair actualPair = newSetPair(included, excluded);
                int key = (included.size()) + (excluded.size());
                Assert.assertTrue("Undefined SetPair found", expectedMap.get(key).contains(actualPair));
            }
            boolean result = true;
            if (ThrowableSetTest.DUMP_INTERNALS) {
                if (!result)
                    System.err.println("!!!ExpectedSizeToSets.match() FAILED!!!");

                System.err.println("expectedMap:");
                System.err.println(expectedMap);
                System.err.println("actualMap:");
                System.err.println(toCompare);
                System.err.flush();
            }
            return result;
        }
    }

    private static ThrowableSetTest.ExpectedSizeToSets expectedSizeToSets;

    // A class to check that memoized results match what we expect.
    // Admittedly, this amounts to a reimplementation of the memoized
    // structures within ThrowableSet -- I'm hoping that the two
    // implementations will have different bugs!
    static class ExpectedMemoizations {
        Map<ThrowableSet, Map<Object, ThrowableSet>> throwableSetToMemoized = new HashMap<ThrowableSet, Map<Object, ThrowableSet>>();

        void checkAdd(ThrowableSet lhs, Object rhs, ThrowableSet result) {
            // rhs should be either a ThrowableSet or a RefType.
            Map<Object, ThrowableSet> actualMemoized = lhs.getMemoizedAdds();
            Assert.assertTrue(((actualMemoized.get(rhs)) == result));
            Map<Object, ThrowableSet> expectedMemoized = throwableSetToMemoized.get(lhs);
            if (expectedMemoized == null) {
                expectedMemoized = new HashMap<Object, ThrowableSet>();
                throwableSetToMemoized.put(lhs, expectedMemoized);
            }
            expectedMemoized.put(rhs, result);
            Assert.assertEquals(expectedMemoized, actualMemoized);
        }
    }

    private static ThrowableSetTest.ExpectedMemoizations expectedMemoizations;

    private static ExceptionTestUtility util;

    @Test
    public void test_01_InitialState() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestInitialState()");
        }
        Assert.assertTrue(ThrowableSetTest.expectedSizeToSets.match());
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_02_SingleInstance0() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestSingleInstance0()");
        }
        Set<RefLikeType> expected = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION }));
        ThrowableSet set0 = add(mgr.EMPTY, ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, expected);
        ThrowableSet set1 = add(mgr.EMPTY, ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, expected);
        Assert.assertTrue("The same ThrowableSet object should represent two sets containing the same single class.", (set0 == set1));
        Set<RefType> catchable = new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, ThrowableSetTest.util.RUNTIME_EXCEPTION, ThrowableSetTest.util.EXCEPTION, ThrowableSetTest.util.THROWABLE }));
        Assert.assertEquals("Should be catchable only as UndeclaredThrowableException and its superclasses", catchable, ThrowableSetTest.util.catchableSubset(set0));
        ThrowableSet.Pair catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.LINKAGE_ERROR);
        Assert.assertEquals(mgr.EMPTY, catchableAs.getCaught());
        Assert.assertEquals(set0, catchableAs.getUncaught());
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION);
        Assert.assertEquals(catchableAs.getCaught(), set0);
        Assert.assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.RUNTIME_EXCEPTION);
        Assert.assertEquals(catchableAs.getCaught(), set0);
        Assert.assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_03_SingleInstance1() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestSingleInstance1()");
        }
        Set<RefLikeType> expected0 = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION }));
        Set<RefLikeType> expected1 = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION }));
        Set<RefLikeType> expectedResult = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION }));
        ThrowableSet set0 = add(mgr.EMPTY, ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, expected0);
        ThrowableSet set0a = add(set0, ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION, expectedResult);
        ThrowableSet set1 = add(mgr.EMPTY, ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION, expected1);
        ThrowableSet set1a = add(set1, ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, expectedResult);
        Assert.assertTrue("The same ThrowableSet object should represent two sets containing the same two exceptions, even if added in different orders.", (set0a == set1a));
        Set<RefLikeType> catchable = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(expectedResult);
        catchable.add(ThrowableSetTest.util.RUNTIME_EXCEPTION);
        catchable.add(ThrowableSetTest.util.EXCEPTION);
        catchable.add(ThrowableSetTest.util.THROWABLE);
        Assert.assertEquals(("Should be catchable only as UndeclaredThrowableException " + "UnsupportedLookAndFeelException and superclasses"), catchable, ThrowableSetTest.util.catchableSubset(set0a));
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_04_AddingSubclasses() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestAddingSubclasses()");
        }
        Set<RefLikeType> expected = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>();
        expected.add(ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION);
        ThrowableSet set0 = add(mgr.EMPTY, ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION, expected);
        expected.clear();
        expected.add(AnySubType.v(ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION));
        ThrowableSet set1 = add(mgr.EMPTY, AnySubType.v(ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION), expected);
        Assert.assertTrue("ThrowableSet should distinguish the case where a single exception includes subclasses from that where it does not.", (set0 != set1));
        Set<RefLikeType> catchable = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION, ThrowableSetTest.util.RUNTIME_EXCEPTION, ThrowableSetTest.util.EXCEPTION, ThrowableSetTest.util.THROWABLE }));
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(set0));
        catchable.add(ThrowableSetTest.util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        catchable.add(ThrowableSetTest.util.STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(set1));
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_05_AddingSets0() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestAddingSets0()");
        }
        Set<RefLikeType> expected = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION }));
        ThrowableSet set0 = add(mgr.EMPTY, ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION, expected);
        expected.clear();
        expected.add(AnySubType.v(ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION));
        ThrowableSet set1 = add(mgr.EMPTY, AnySubType.v(ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION), expected);
        ThrowableSet result = add(set1, set0, expected);
        Assert.assertTrue("{AnySubType(E)} union {E} should equal {AnySubType(E)}", (result == set1));
        result = add(set1, set0, expected);
        Assert.assertTrue("{E} union {AnySubType(E)} should equal {AnySubType(E)}", (result == set1));
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("testAddingSets0()");
            printAllSets();
        }
    }

    @Test
    public void test_06_AddingSets1() {
        Set<RefLikeType> expected = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(ThrowableSetTest.util.VM_ERRORS);
        expected.add(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION);
        ThrowableSet set0 = add(mgr.VM_ERRORS, ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, expected);
        expected.add(ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
        set0 = add(set0, ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION, expected);
        ThrowableSet set1 = mgr.INITIALIZATION_ERRORS;
        expected = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>();
        expected.add(AnySubType.v(ThrowableSetTest.util.ERROR));
        ThrowableSetTest.assertSameMembers(set1, expected, Collections.<AnySubType>emptySet());
        expected.add(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION);
        expected.add(ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
        ThrowableSet result0 = add(set0, set1, expected);
        ThrowableSet result1 = add(set1, set0, expected);
        Assert.assertTrue("Adding sets should be commutative.", (result0 == result1));
        Set<RefLikeType> catchable = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(ThrowableSetTest.util.ALL_TEST_ERRORS_PLUS_SUPERTYPES);
        catchable.add(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION);
        catchable.add(ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
        catchable.add(ThrowableSetTest.util.RUNTIME_EXCEPTION);// Superclasses of

        catchable.add(ThrowableSetTest.util.EXCEPTION);
        // others.
        catchable.add(ThrowableSetTest.util.ERROR);
        catchable.add(ThrowableSetTest.util.THROWABLE);
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(result0));
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_07_AddingSets2() {
        Set<RefLikeType> expected = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(ThrowableSetTest.util.VM_ERRORS);
        expected.add(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION);
        ThrowableSet set0 = add(mgr.VM_ERRORS, ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, expected);
        expected.add(ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
        set0 = add(set0, ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION, expected);
        ThrowableSet set1 = mgr.INITIALIZATION_ERRORS;
        expected = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>();
        expected.add(AnySubType.v(ThrowableSetTest.util.ERROR));
        ThrowableSetTest.assertSameMembers(set1, expected, Collections.<AnySubType>emptySet());
        expected.add(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION);
        expected.add(ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
        ThrowableSet result0 = add(set0, set1, expected);
        ThrowableSet result1 = add(set1, set0, expected);
        Assert.assertTrue("Adding sets should be commutative.", (result0 == result1));
        Set<RefLikeType> catchable = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(ThrowableSetTest.util.ALL_TEST_ERRORS_PLUS_SUPERTYPES);
        catchable.add(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION);
        catchable.add(ThrowableSetTest.util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
        catchable.add(ThrowableSetTest.util.RUNTIME_EXCEPTION);// Superclasses of

        catchable.add(ThrowableSetTest.util.EXCEPTION);
        // others.
        catchable.add(ThrowableSetTest.util.ERROR);
        catchable.add(ThrowableSetTest.util.THROWABLE);
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(result0));
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_08_WhichCatchable0() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestWhichCatchable0()");
        }
        Set<RefLikeType> expected = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION }));
        ThrowableSet set0 = add(mgr.EMPTY, ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, expected);
        Set<RefLikeType> catchable = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION, ThrowableSetTest.util.RUNTIME_EXCEPTION, ThrowableSetTest.util.EXCEPTION, ThrowableSetTest.util.THROWABLE }));
        ThrowableSet.Pair catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.LINKAGE_ERROR);
        Assert.assertEquals(mgr.EMPTY, catchableAs.getCaught());
        Assert.assertEquals(set0, catchableAs.getUncaught());
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION);
        Assert.assertEquals(catchableAs.getCaught(), set0);
        Assert.assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.RUNTIME_EXCEPTION));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.RUNTIME_EXCEPTION);
        Assert.assertEquals(catchableAs.getCaught(), set0);
        Assert.assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.EXCEPTION));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.EXCEPTION);
        Assert.assertEquals(catchableAs.getCaught(), set0);
        Assert.assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.THROWABLE));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.THROWABLE);
        Assert.assertEquals(catchableAs.getCaught(), set0);
        Assert.assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue((!(set0.catchableAs(ThrowableSetTest.util.ERROR))));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.ERROR);
        Assert.assertEquals(catchableAs.getCaught(), mgr.EMPTY);
        Assert.assertEquals(catchableAs.getUncaught(), set0);
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(catchable, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_09_WhichCatchable1() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestWhichCatchable1()");
        }
        ThrowableSet set0 = mgr.EMPTY.add(ThrowableSetTest.util.LINKAGE_ERROR);
        Set<RefType> catcherTypes = new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.THROWABLE }));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.ERROR));
        ThrowableSet.Pair catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.ERROR);
        Assert.assertEquals(set0, catchableAs.getCaught());
        Assert.assertEquals(mgr.EMPTY, catchableAs.getUncaught());
        Assert.assertEquals(catcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.LINKAGE_ERROR));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.LINKAGE_ERROR);
        Assert.assertEquals(set0, catchableAs.getCaught());
        Assert.assertEquals(mgr.EMPTY, catchableAs.getUncaught());
        Assert.assertEquals(catcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue((!(set0.catchableAs(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR))));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        Assert.assertEquals(mgr.EMPTY, catchableAs.getCaught());
        Assert.assertEquals(set0, catchableAs.getUncaught());
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(catcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue((!(set0.catchableAs(ThrowableSetTest.util.INSTANTIATION_ERROR))));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.INSTANTIATION_ERROR);
        Assert.assertEquals(mgr.EMPTY, catchableAs.getCaught());
        Assert.assertEquals(set0, catchableAs.getUncaught());
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(catcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue((!(set0.catchableAs(ThrowableSetTest.util.INTERNAL_ERROR))));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.INTERNAL_ERROR);
        Assert.assertEquals(mgr.EMPTY, catchableAs.getCaught());
        Assert.assertEquals(set0, catchableAs.getUncaught());
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(catcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_10_WhichCatchable2() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestWhichCatchable2()");
        }
        ThrowableSet set0 = mgr.EMPTY.add(AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR));
        Set<RefType> catcherTypes = new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR, ThrowableSetTest.util.ABSTRACT_METHOD_ERROR, ThrowableSetTest.util.ILLEGAL_ACCESS_ERROR, ThrowableSetTest.util.INSTANTIATION_ERROR, ThrowableSetTest.util.NO_SUCH_FIELD_ERROR, ThrowableSetTest.util.NO_SUCH_METHOD_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.THROWABLE }));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.ERROR));
        ThrowableSet.Pair catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.ERROR);
        Assert.assertEquals(set0, catchableAs.getCaught());
        Assert.assertEquals(mgr.EMPTY, catchableAs.getUncaught());
        Assert.assertEquals(catcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.LINKAGE_ERROR));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.LINKAGE_ERROR);
        Assert.assertEquals(set0, catchableAs.getCaught());
        Assert.assertEquals(mgr.EMPTY, catchableAs.getUncaught());
        Assert.assertEquals(catcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        Set<AnySubType> expectedCaughtIncluded = new ExceptionTestUtility.ExceptionHashSet<AnySubType>(Arrays.asList(new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR) }));
        Set<AnySubType> expectedCaughtExcluded = Collections.emptySet();
        Set<AnySubType> expectedUncaughtIncluded = new ExceptionTestUtility.ExceptionHashSet<AnySubType>(Arrays.asList(new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) }));
        Set<AnySubType> expectedUncaughtExcluded = expectedCaughtIncluded;
        ThrowableSetTest.assertSameMembers(catchableAs, expectedCaughtIncluded, expectedCaughtExcluded, expectedUncaughtIncluded, expectedUncaughtExcluded);
        catcherTypes = new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR, ThrowableSetTest.util.ABSTRACT_METHOD_ERROR, ThrowableSetTest.util.ILLEGAL_ACCESS_ERROR, ThrowableSetTest.util.INSTANTIATION_ERROR, ThrowableSetTest.util.NO_SUCH_FIELD_ERROR, ThrowableSetTest.util.NO_SUCH_METHOD_ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.THROWABLE }));
        Set<RefType> noncatcherTypes = new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.THROWABLE }));
        Assert.assertEquals(catcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(noncatcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.INSTANTIATION_ERROR));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.INSTANTIATION_ERROR);
        expectedCaughtIncluded = new ExceptionTestUtility.ExceptionHashSet<AnySubType>(Arrays.asList(new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.INSTANTIATION_ERROR) }));
        expectedCaughtExcluded = Collections.emptySet();
        expectedUncaughtExcluded = expectedCaughtIncluded;
        ThrowableSetTest.assertSameMembers(catchableAs, expectedCaughtIncluded, expectedCaughtExcluded, expectedUncaughtIncluded, expectedUncaughtExcluded);
        catcherTypes = new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.INSTANTIATION_ERROR, ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.THROWABLE }));
        noncatcherTypes = new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.ABSTRACT_METHOD_ERROR, ThrowableSetTest.util.ILLEGAL_ACCESS_ERROR, ThrowableSetTest.util.NO_SUCH_FIELD_ERROR, ThrowableSetTest.util.NO_SUCH_METHOD_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR, ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.THROWABLE }));
        Assert.assertEquals(catcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(noncatcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue((!(set0.catchableAs(ThrowableSetTest.util.INTERNAL_ERROR))));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.INTERNAL_ERROR);
        Assert.assertEquals(mgr.EMPTY, catchableAs.getCaught());
        Assert.assertEquals(set0, catchableAs.getUncaught());
        noncatcherTypes = new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.ABSTRACT_METHOD_ERROR, ThrowableSetTest.util.ILLEGAL_ACCESS_ERROR, ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR, ThrowableSetTest.util.NO_SUCH_FIELD_ERROR, ThrowableSetTest.util.NO_SUCH_METHOD_ERROR, ThrowableSetTest.util.INSTANTIATION_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR, ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.THROWABLE }));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(noncatcherTypes, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_11_WhichCatchable3() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestWhichCatchable3()");
        }
        ThrowableSet set0 = mgr.EMPTY;
        set0 = set0.add(AnySubType.v(ThrowableSetTest.util.ERROR));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR));
        ThrowableSet.Pair catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        Set<AnySubType> expectedCaughtIncluded = new ExceptionTestUtility.ExceptionHashSet<AnySubType>(Arrays.asList(new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR) }));
        Set<AnySubType> expectedCaughtExcluded = Collections.emptySet();
        Set<AnySubType> expectedUncaughtIncluded = new ExceptionTestUtility.ExceptionHashSet<AnySubType>(Arrays.asList(new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.ERROR) }));
        Set<AnySubType> expectedUncaughtExcluded = expectedCaughtIncluded;
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedCaughtIncluded, expectedCaughtExcluded, catchableAs.getCaught()));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedUncaughtIncluded, expectedUncaughtExcluded, catchableAs.getUncaught()));
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR, ThrowableSetTest.util.ABSTRACT_METHOD_ERROR, ThrowableSetTest.util.INSTANTIATION_ERROR, ThrowableSetTest.util.ILLEGAL_ACCESS_ERROR, ThrowableSetTest.util.NO_SUCH_FIELD_ERROR, ThrowableSetTest.util.NO_SUCH_METHOD_ERROR })), ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.AWT_ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR, ThrowableSetTest.util.THREAD_DEATH, ThrowableSetTest.util.VIRTUAL_MACHINE_ERROR, ThrowableSetTest.util.INTERNAL_ERROR, ThrowableSetTest.util.OUT_OF_MEMORY_ERROR, ThrowableSetTest.util.STACK_OVERFLOW_ERROR, ThrowableSetTest.util.UNKNOWN_ERROR })), ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        set0 = catchableAs.getUncaught();
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.THROWABLE));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.THROWABLE);
        Assert.assertEquals(set0, catchableAs.getCaught());
        Assert.assertEquals(mgr.EMPTY, catchableAs.getUncaught());
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.AWT_ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR, ThrowableSetTest.util.THREAD_DEATH, ThrowableSetTest.util.VIRTUAL_MACHINE_ERROR, ThrowableSetTest.util.INTERNAL_ERROR, ThrowableSetTest.util.OUT_OF_MEMORY_ERROR, ThrowableSetTest.util.STACK_OVERFLOW_ERROR, ThrowableSetTest.util.UNKNOWN_ERROR })), ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.ERROR));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.ERROR);
        Assert.assertEquals(set0, catchableAs.getCaught());
        Assert.assertEquals(mgr.EMPTY, catchableAs.getUncaught());
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.AWT_ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR, ThrowableSetTest.util.THREAD_DEATH, ThrowableSetTest.util.VIRTUAL_MACHINE_ERROR, ThrowableSetTest.util.INTERNAL_ERROR, ThrowableSetTest.util.OUT_OF_MEMORY_ERROR, ThrowableSetTest.util.STACK_OVERFLOW_ERROR, ThrowableSetTest.util.UNKNOWN_ERROR })), ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.LINKAGE_ERROR));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.LINKAGE_ERROR);
        expectedCaughtIncluded = new ExceptionTestUtility.ExceptionHashSet<AnySubType>(Arrays.asList(new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) }));
        expectedCaughtExcluded = new ExceptionTestUtility.ExceptionHashSet<AnySubType>(Arrays.asList(new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR) }));
        expectedUncaughtIncluded = new ExceptionTestUtility.ExceptionHashSet<AnySubType>(Arrays.asList(new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.ERROR) }));
        expectedUncaughtExcluded = expectedCaughtIncluded;
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedCaughtIncluded, expectedCaughtExcluded, catchableAs.getCaught()));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedUncaughtIncluded, expectedUncaughtExcluded, catchableAs.getUncaught()));
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR })), ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.AWT_ERROR, ThrowableSetTest.util.THREAD_DEATH, ThrowableSetTest.util.VIRTUAL_MACHINE_ERROR, ThrowableSetTest.util.INTERNAL_ERROR, ThrowableSetTest.util.OUT_OF_MEMORY_ERROR, ThrowableSetTest.util.STACK_OVERFLOW_ERROR, ThrowableSetTest.util.UNKNOWN_ERROR })), ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        Assert.assertTrue((!(set0.catchableAs(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR))));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        Assert.assertEquals(mgr.EMPTY, catchableAs.getCaught());
        Assert.assertEquals(set0, catchableAs.getUncaught());
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.AWT_ERROR, ThrowableSetTest.util.THREAD_DEATH, ThrowableSetTest.util.VIRTUAL_MACHINE_ERROR, ThrowableSetTest.util.INTERNAL_ERROR, ThrowableSetTest.util.OUT_OF_MEMORY_ERROR, ThrowableSetTest.util.STACK_OVERFLOW_ERROR, ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR, ThrowableSetTest.util.UNKNOWN_ERROR })), ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.ILLEGAL_ACCESS_ERROR);
        Assert.assertEquals(mgr.EMPTY, catchableAs.getCaught());
        Assert.assertEquals(set0, catchableAs.getUncaught());
        Assert.assertEquals(Collections.EMPTY_SET, ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefType>(Arrays.asList(new RefType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.AWT_ERROR, ThrowableSetTest.util.THREAD_DEATH, ThrowableSetTest.util.VIRTUAL_MACHINE_ERROR, ThrowableSetTest.util.INTERNAL_ERROR, ThrowableSetTest.util.OUT_OF_MEMORY_ERROR, ThrowableSetTest.util.STACK_OVERFLOW_ERROR, ThrowableSetTest.util.CLASS_CIRCULARITY_ERROR, ThrowableSetTest.util.CLASS_FORMAT_ERROR, ThrowableSetTest.util.UNSUPPORTED_CLASS_VERSION_ERROR, ThrowableSetTest.util.EXCEPTION_IN_INITIALIZER_ERROR, ThrowableSetTest.util.NO_CLASS_DEF_FOUND_ERROR, ThrowableSetTest.util.UNSATISFIED_LINK_ERROR, ThrowableSetTest.util.VERIFY_ERROR, ThrowableSetTest.util.UNKNOWN_ERROR })), ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        if (ThrowableSetTest.DUMP_INTERNALS) {
            printAllSets();
        }
    }

    @Test
    public void test_12_WhichCatchable10() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestWhichCatchable3()");
        }
        ThrowableSet set0 = mgr.EMPTY;
        set0 = set0.add(AnySubType.v(ThrowableSetTest.util.THROWABLE));
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.ARITHMETIC_EXCEPTION));
        ThrowableSet.Pair catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.ARITHMETIC_EXCEPTION);
        ThrowableSetTest.assertSameMembers(catchableAs, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ARITHMETIC_EXCEPTION) }, new AnySubType[]{  }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.THROWABLE) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.ARITHMETIC_EXCEPTION) });
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.EXCEPTION, ThrowableSetTest.util.RUNTIME_EXCEPTION, ThrowableSetTest.util.ARITHMETIC_EXCEPTION })), ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        HashSet<RefLikeType> expectedUncaught = new HashSet<RefLikeType>(ThrowableSetTest.util.ALL_TEST_THROWABLES);
        expectedUncaught.remove(ThrowableSetTest.util.ARITHMETIC_EXCEPTION);
        Assert.assertEquals(expectedUncaught, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        set0 = catchableAs.getUncaught();
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.ABSTRACT_METHOD_ERROR));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.ABSTRACT_METHOD_ERROR);
        ThrowableSetTest.assertSameMembers(catchableAs, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ABSTRACT_METHOD_ERROR) }, new AnySubType[]{  }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.THROWABLE) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.ARITHMETIC_EXCEPTION), AnySubType.v(ThrowableSetTest.util.ABSTRACT_METHOD_ERROR) });
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.ERROR, ThrowableSetTest.util.LINKAGE_ERROR, ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR, ThrowableSetTest.util.ABSTRACT_METHOD_ERROR })), ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        expectedUncaught.remove(ThrowableSetTest.util.ABSTRACT_METHOD_ERROR);
        Assert.assertEquals(expectedUncaught, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
        set0 = catchableAs.getUncaught();
        Assert.assertTrue(set0.catchableAs(ThrowableSetTest.util.RUNTIME_EXCEPTION));
        catchableAs = set0.whichCatchableAs(ThrowableSetTest.util.RUNTIME_EXCEPTION);
        ThrowableSetTest.assertSameMembers(catchableAs, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.RUNTIME_EXCEPTION) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.ARITHMETIC_EXCEPTION) }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.THROWABLE) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.RUNTIME_EXCEPTION), AnySubType.v(ThrowableSetTest.util.ABSTRACT_METHOD_ERROR) });
        Assert.assertEquals(new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[]{ ThrowableSetTest.util.THROWABLE, ThrowableSetTest.util.EXCEPTION, ThrowableSetTest.util.RUNTIME_EXCEPTION, ThrowableSetTest.util.ARRAY_STORE_EXCEPTION, ThrowableSetTest.util.CLASS_CAST_EXCEPTION, ThrowableSetTest.util.ILLEGAL_MONITOR_STATE_EXCEPTION, ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION, ThrowableSetTest.util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION, ThrowableSetTest.util.STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION, ThrowableSetTest.util.NEGATIVE_ARRAY_SIZE_EXCEPTION, ThrowableSetTest.util.NULL_POINTER_EXCEPTION, ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION })), ThrowableSetTest.util.catchableSubset(catchableAs.getCaught()));
        expectedUncaught.remove(ThrowableSetTest.util.RUNTIME_EXCEPTION);
        expectedUncaught.remove(ThrowableSetTest.util.ARRAY_STORE_EXCEPTION);
        expectedUncaught.remove(ThrowableSetTest.util.CLASS_CAST_EXCEPTION);
        expectedUncaught.remove(ThrowableSetTest.util.ILLEGAL_MONITOR_STATE_EXCEPTION);
        expectedUncaught.remove(ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedUncaught.remove(ThrowableSetTest.util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedUncaught.remove(ThrowableSetTest.util.STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedUncaught.remove(ThrowableSetTest.util.NEGATIVE_ARRAY_SIZE_EXCEPTION);
        expectedUncaught.remove(ThrowableSetTest.util.NULL_POINTER_EXCEPTION);
        expectedUncaught.remove(ThrowableSetTest.util.UNDECLARED_THROWABLE_EXCEPTION);
        Assert.assertEquals(expectedUncaught, ThrowableSetTest.util.catchableSubset(catchableAs.getUncaught()));
    }

    @Test
    public void test_13_AddAfterWhichCatchableAs0() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestAddAfterWhichCatchable0()");
        }
        ThrowableSet anyError = mgr.EMPTY.add(AnySubType.v(ThrowableSetTest.util.ERROR));
        Assert.assertTrue(anyError.catchableAs(ThrowableSetTest.util.LINKAGE_ERROR));
        ThrowableSet.Pair catchableAs = anyError.whichCatchableAs(ThrowableSetTest.util.LINKAGE_ERROR);
        ThrowableSetTest.assertSameMembers(catchableAs, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) }, new AnySubType[]{  }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) });
        ThrowableSet anyErrorMinusLinkage = catchableAs.getUncaught();
        try {
            ThrowableSet anyErrorMinusLinkagePlusIncompatibleClassChange = anyErrorMinusLinkage.add(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
            Assert.fail("add(IncompatiableClassChangeError) after removing LinkageError should currently generate an exception");
            // Following documents what we would like to be able to implement:
            ThrowableSetTest.assertSameMembers(anyErrorMinusLinkagePlusIncompatibleClassChange, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR), ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) });
        } catch (ThrowableSet e) {
            // this is what should happen.
        }
        try {
            ThrowableSet anyErrorMinusLinkagePlusAnyIncompatibleClassChange = anyErrorMinusLinkage.add(AnySubType.v(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR));
            Assert.fail("add(AnySubType.v(IncompatiableClassChangeError)) after removing LinkageError should currently generate an exception");
            // Following documents what we would like to be able to implement:
            ThrowableSetTest.assertSameMembers(anyErrorMinusLinkagePlusAnyIncompatibleClassChange, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR), AnySubType.v(ThrowableSetTest.util.INCOMPATIBLE_CLASS_CHANGE_ERROR) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) });
        } catch (ThrowableSet e) {
            // this is what should happen.
        }
        // Add types that should not change the set.
        ThrowableSet sameSet = anyErrorMinusLinkage.add(ThrowableSetTest.util.VIRTUAL_MACHINE_ERROR);
        Assert.assertTrue((sameSet == anyErrorMinusLinkage));
        ThrowableSetTest.assertSameMembers(sameSet, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) });
        sameSet = anyErrorMinusLinkage.add(AnySubType.v(ThrowableSetTest.util.VIRTUAL_MACHINE_ERROR));
        Assert.assertTrue((sameSet == anyErrorMinusLinkage));
        ThrowableSetTest.assertSameMembers(sameSet, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) });
        ThrowableSet anyErrorMinusLinkagePlusArrayIndex = anyErrorMinusLinkage.add(ThrowableSetTest.util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        ThrowableSetTest.assertSameMembers(anyErrorMinusLinkagePlusArrayIndex, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR), ThrowableSetTest.util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) });
        ThrowableSet anyErrorMinusLinkagePlusAnyIndex = anyErrorMinusLinkagePlusArrayIndex.add(AnySubType.v(ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION));
        ThrowableSetTest.assertSameMembers(anyErrorMinusLinkagePlusAnyIndex, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR), AnySubType.v(ThrowableSetTest.util.INDEX_OUT_OF_BOUNDS_EXCEPTION) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) });
        ThrowableSet anyErrorMinusLinkagePlusAnyRuntime = anyErrorMinusLinkagePlusAnyIndex.add(AnySubType.v(ThrowableSetTest.util.RUNTIME_EXCEPTION));
        ThrowableSetTest.assertSameMembers(anyErrorMinusLinkagePlusAnyRuntime, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR), AnySubType.v(ThrowableSetTest.util.RUNTIME_EXCEPTION) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) });
        try {
            ThrowableSet anyErrorMinusLinkagePlusAnyRuntimePlusError = anyErrorMinusLinkagePlusAnyRuntime.add(AnySubType.v(ThrowableSetTest.util.ERROR));
            Assert.fail("add(AnySubType(Error)) after removing LinkageError should currently generate an exception.");
            // This documents what we would like to implement:
            ThrowableSetTest.assertSameMembers(anyErrorMinusLinkagePlusAnyRuntimePlusError, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR), AnySubType.v(ThrowableSetTest.util.RUNTIME_EXCEPTION) }, new AnySubType[]{  });
        } catch (ThrowableSet e) {
            // This is what should happen.
        }
        try {
            ThrowableSet anyErrorMinusLinkagePlusAnyRuntimePlusLinkageError = anyErrorMinusLinkagePlusAnyRuntime.add(AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR));
            Assert.fail("add(AnySubType(LinkageError)) after removing LinkageError should currently generate an exception.");
            // This documents what we would like to implement:
            ThrowableSetTest.assertSameMembers(anyErrorMinusLinkagePlusAnyRuntimePlusLinkageError, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR), AnySubType.v(ThrowableSetTest.util.RUNTIME_EXCEPTION) }, new AnySubType[]{  });
        } catch (ThrowableSet e) {
            // This is what should happen.
        }
    }

    @Test
    public void test_14_WhichCatchablePhantom0() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestWhichCatchablePhantom0()");
        }
        ThrowableSet anyError = mgr.EMPTY.add(AnySubType.v(ThrowableSetTest.util.ERROR));
        ThrowableSetTest.assertSameMembers(anyError.whichCatchableAs(ThrowableSetTest.util.LINKAGE_ERROR), new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) }, new AnySubType[]{  }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR) }, new AnySubType[]{ AnySubType.v(ThrowableSetTest.util.LINKAGE_ERROR) });
        ThrowableSetTest.assertSameMembers(anyError.whichCatchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1), new RefLikeType[]{  }, new AnySubType[]{  }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.ERROR) }, new AnySubType[]{  });
        Assert.assertTrue(anyError.catchableAs(ThrowableSetTest.util.LINKAGE_ERROR));
        Assert.assertFalse(anyError.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1));
        ThrowableSet phantomOnly = mgr.EMPTY.add(ThrowableSetTest.util.PHANTOM_EXCEPTION1);
        ThrowableSetTest.assertSameMembers(phantomOnly.whichCatchableAs(ThrowableSetTest.util.LINKAGE_ERROR), new RefLikeType[]{  }, new AnySubType[]{  }, new RefLikeType[]{ ThrowableSetTest.util.PHANTOM_EXCEPTION1 }, new AnySubType[]{  });
        ThrowableSetTest.assertSameMembers(phantomOnly.whichCatchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION2), new RefLikeType[]{  }, new AnySubType[]{  }, new RefLikeType[]{ ThrowableSetTest.util.PHANTOM_EXCEPTION1 }, new AnySubType[]{  });
        ThrowableSetTest.assertSameMembers(phantomOnly.whichCatchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1), new RefLikeType[]{ ThrowableSetTest.util.PHANTOM_EXCEPTION1 }, new AnySubType[]{  }, new RefLikeType[]{  }, new AnySubType[]{  });
        Assert.assertFalse(phantomOnly.catchableAs(ThrowableSetTest.util.LINKAGE_ERROR));
        Assert.assertTrue(phantomOnly.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1));
        Assert.assertFalse(phantomOnly.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION2));
        ThrowableSet bothPhantoms = phantomOnly.add(ThrowableSetTest.util.PHANTOM_EXCEPTION2);
        ThrowableSetTest.assertSameMembers(bothPhantoms.whichCatchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1), new RefLikeType[]{ ThrowableSetTest.util.PHANTOM_EXCEPTION1 }, new AnySubType[]{  }, new RefLikeType[]{ ThrowableSetTest.util.PHANTOM_EXCEPTION2 }, new AnySubType[]{  });
        ThrowableSetTest.assertSameMembers(bothPhantoms.whichCatchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION2), new RefLikeType[]{ ThrowableSetTest.util.PHANTOM_EXCEPTION2 }, new AnySubType[]{  }, new RefLikeType[]{ ThrowableSetTest.util.PHANTOM_EXCEPTION1 }, new AnySubType[]{  });
        Assert.assertFalse(bothPhantoms.catchableAs(ThrowableSetTest.util.LINKAGE_ERROR));
        Assert.assertTrue(bothPhantoms.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1));
        Assert.assertTrue(bothPhantoms.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION2));
        ThrowableSet bothPhantoms2 = phantomOnly.add(ThrowableSetTest.util.PHANTOM_EXCEPTION2);
        Assert.assertTrue((bothPhantoms == bothPhantoms2));
        ThrowableSet throwableOnly = mgr.EMPTY.add(ThrowableSetTest.util.THROWABLE);
        Assert.assertFalse(throwableOnly.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1));
        ThrowableSet allThrowables = mgr.EMPTY.add(AnySubType.v(ThrowableSetTest.util.THROWABLE));
        Assert.assertTrue(allThrowables.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1));
    }

    @Test
    public void test_14_WhichCatchablePhantom1() {
        if (ThrowableSetTest.DUMP_INTERNALS) {
            System.err.println("\n\ntestWhichCatchablePhantom1()");
        }
        ThrowableSet phantomOnly = mgr.EMPTY.add(AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION1));
        ThrowableSetTest.assertSameMembers(phantomOnly.whichCatchableAs(ThrowableSetTest.util.LINKAGE_ERROR), new RefLikeType[]{  }, new AnySubType[]{  }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION1) }, new AnySubType[]{  });
        ThrowableSetTest.assertSameMembers(phantomOnly.whichCatchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION2), new RefLikeType[]{  }, new AnySubType[]{  }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION1) }, new AnySubType[]{  });
        ThrowableSetTest.assertSameMembers(phantomOnly.whichCatchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1), new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION1) }, new AnySubType[]{  }, new RefLikeType[]{  }, new AnySubType[]{  });
        Assert.assertFalse(phantomOnly.catchableAs(ThrowableSetTest.util.LINKAGE_ERROR));
        Assert.assertTrue(phantomOnly.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1));
        Assert.assertFalse(phantomOnly.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION2));
        ThrowableSet bothPhantoms = phantomOnly.add(AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION2));
        ThrowableSetTest.assertSameMembers(bothPhantoms.whichCatchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1), new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION1) }, new AnySubType[]{  }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION2) }, new AnySubType[]{  });
        ThrowableSetTest.assertSameMembers(bothPhantoms.whichCatchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION2), new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION2) }, new AnySubType[]{  }, new RefLikeType[]{ AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION1) }, new AnySubType[]{  });
        Assert.assertFalse(bothPhantoms.catchableAs(ThrowableSetTest.util.LINKAGE_ERROR));
        Assert.assertTrue(bothPhantoms.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION1));
        Assert.assertTrue(bothPhantoms.catchableAs(ThrowableSetTest.util.PHANTOM_EXCEPTION2));
        ThrowableSet bothPhantoms2 = phantomOnly.add(AnySubType.v(ThrowableSetTest.util.PHANTOM_EXCEPTION2));
        Assert.assertTrue((bothPhantoms == bothPhantoms2));
    }
}

