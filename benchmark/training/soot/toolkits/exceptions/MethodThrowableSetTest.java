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


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import soot.AnySubType;
import soot.RefLikeType;
import soot.Scene;
import soot.SootMethod;


public class MethodThrowableSetTest {
    private static String TARGET_CLASS = "soot.toolkits.exceptions.targets.MethodThrowableSetClass";

    private static String EXCEPTION_CLASS = "soot.toolkits.exceptions.targets.MyException";

    private static ExceptionTestUtility testUtility;

    /**
     * Derived class to allow access to some protected members
     */
    @Ignore
    private static class ThrowAnalysisForTest extends UnitThrowAnalysis {
        public ThrowAnalysisForTest() {
            super(true);
        }

        @Override
        public ThrowableSet mightThrow(SootMethod sm) {
            return super.mightThrow(sm);
        }
    }

    @Test
    public void simpleExceptionTest1() {
        ThrowableSet ts = getExceptionsForMethod("<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void foo()>");
        Set<RefLikeType> expected = new HashSet<RefLikeType>();
        expected.addAll(MethodThrowableSetTest.testUtility.VM_ERRORS);
        expected.add(MethodThrowableSetTest.testUtility.ARITHMETIC_EXCEPTION);
        expected.add(MethodThrowableSetTest.testUtility.NULL_POINTER_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
    }

    @Test
    public void simpleExceptionTest2() {
        ThrowableSet ts = getExceptionsForMethod("<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void bar()>");
        Set<RefLikeType> expected = new HashSet<RefLikeType>();
        expected.addAll(MethodThrowableSetTest.testUtility.VM_ERRORS);
        expected.add(MethodThrowableSetTest.testUtility.ARITHMETIC_EXCEPTION);
        expected.add(MethodThrowableSetTest.testUtility.NULL_POINTER_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
    }

    @Test
    public void simpleExceptionTest3() {
        ThrowableSet ts = getExceptionsForMethod("<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void tool()>");
        Set<RefLikeType> expected = new HashSet<RefLikeType>();
        expected.addAll(MethodThrowableSetTest.testUtility.VM_ERRORS);
        expected.add(MethodThrowableSetTest.testUtility.ARITHMETIC_EXCEPTION);
        expected.add(MethodThrowableSetTest.testUtility.NULL_POINTER_EXCEPTION);
        expected.add(MethodThrowableSetTest.testUtility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
    }

    @Test
    public void getAllExceptionTest1() {
        ThrowableSet ts = getExceptionsForMethod("<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void getAllException()>");
        Set<RefLikeType> expected = new HashSet<RefLikeType>();
        expected.addAll(MethodThrowableSetTest.testUtility.VM_ERRORS);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
    }

    @Test
    public void getMyExceptionTest1() {
        ThrowableSet ts = getExceptionsForMethod("<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void getMyException()>");
        Set<RefLikeType> expected = new HashSet<RefLikeType>();
        expected.add(AnySubType.v(MethodThrowableSetTest.testUtility.ERROR));// for NewExpr

        expected.add(MethodThrowableSetTest.testUtility.ILLEGAL_MONITOR_STATE_EXCEPTION);
        expected.add(MethodThrowableSetTest.testUtility.NULL_POINTER_EXCEPTION);
        expected.add(AnySubType.v(Scene.v().getSootClass(MethodThrowableSetTest.EXCEPTION_CLASS).getType()));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
    }

    @Test
    public void nestedTryCatchTest1() {
        ThrowableSet ts = getExceptionsForMethod("<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void nestedTry()>");
        Set<RefLikeType> expected = new HashSet<RefLikeType>();
        expected.addAll(MethodThrowableSetTest.testUtility.VM_ERRORS);
        expected.add(MethodThrowableSetTest.testUtility.ARITHMETIC_EXCEPTION);
        expected.add(MethodThrowableSetTest.testUtility.NULL_POINTER_EXCEPTION);
        expected.add(MethodThrowableSetTest.testUtility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
    }

    @Test
    public void recursionTest1() {
        ThrowableSet ts = getExceptionsForMethod("<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void recursion()>");
        Set<RefLikeType> expected = new HashSet<RefLikeType>();
        expected.addAll(MethodThrowableSetTest.testUtility.VM_ERRORS);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
    }

    @Test
    public void unitInCatchBlockTest1() {
        ThrowableSet ts = getExceptionsForMethod("<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void unitInCatchBlock()>");
        Set<RefLikeType> expected = new HashSet<RefLikeType>();
        expected.addAll(MethodThrowableSetTest.testUtility.VM_ERRORS);
        expected.add(MethodThrowableSetTest.testUtility.ARITHMETIC_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
    }
}

