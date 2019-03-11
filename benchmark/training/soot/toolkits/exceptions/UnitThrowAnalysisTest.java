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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import soot.AnySubType;
import soot.ArrayType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.baf.AddInst;
import soot.baf.AndInst;
import soot.baf.ArrayLengthInst;
import soot.grimp.Grimp;
import soot.jimple.ArrayRef;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.RemExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.VirtualInvokeExpr;


public class UnitThrowAnalysisTest {
    static {
        Scene.v().loadBasicClasses();
    }

    class ImmaculateInvokeUnitThrowAnalysis extends UnitThrowAnalysis {
        // A variant of UnitThrowAnalysis which assumes that invoked
        // methods will never throw any exceptions, rather than that
        // they might throw anything Throwable. This allows us to
        // test that individual arguments to invocations are being
        // examined.
        protected ThrowableSet mightThrow(SootMethod m) {
            return Manager.v().EMPTY;
        }
    }

    UnitThrowAnalysis unitAnalysis;

    UnitThrowAnalysis immaculateAnalysis;

    // A collection of Grimp values and expressions used in various tests:
    protected StaticFieldRef floatStaticFieldRef;

    protected Local floatLocal;

    protected FloatConstant floatConstant;

    protected Local floatConstantLocal;

    protected InstanceFieldRef floatInstanceFieldRef;

    protected ArrayRef floatArrayRef;

    protected VirtualInvokeExpr floatVirtualInvoke;

    protected StaticInvokeExpr floatStaticInvoke;

    private ExceptionTestUtility utility;

    @Test
    public void testJBreakpointStmt() {
        Stmt s = Grimp.v().newBreakpointStmt();
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGBreakpointStmt() {
        Stmt s = Grimp.v().newBreakpointStmt();
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJAssignStmt() {
        // local0 = 0
        Stmt s = Jimple.v().newAssignStmt(Jimple.v().newLocal("local0", IntType.v()), IntConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        ArrayRef arrayRef = Jimple.v().newArrayRef(Jimple.v().newLocal("local1", ArrayType.v(RefType.v("java.lang.Object"), 1)), IntConstant.v(0));
        Local scalarRef = Jimple.v().newLocal("local2", RefType.v("java.lang.Object"));
        // local2 = local1[0]
        s = Jimple.v().newAssignStmt(scalarRef, arrayRef);
        Set<RefLikeType> expectedRep = new ExceptionTestUtility.ExceptionHashSet<RefLikeType>(utility.VM_ERRORS);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        expectedRep.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.<AnySubType>emptySet(), unitAnalysis.mightThrow(s)));
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedCatch.add(utility.INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        // local1[0] = local2
        s = Jimple.v().newAssignStmt(arrayRef, scalarRef);
        expectedRep.add(utility.ARRAY_STORE_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        expectedCatch.add(utility.ARRAY_STORE_EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGAssignStmt() {
        // local0 = 0
        Stmt s = Grimp.v().newAssignStmt(Grimp.v().newLocal("local0", IntType.v()), IntConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        ArrayRef arrayRef = Grimp.v().newArrayRef(Grimp.v().newLocal("local1", ArrayType.v(RefType.v("java.lang.Object"), 1)), IntConstant.v(0));
        Local scalarRef = Grimp.v().newLocal("local2", RefType.v("java.lang.Object"));
        // local2 = local1[0]
        s = Grimp.v().newAssignStmt(scalarRef, arrayRef);
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        expectedRep.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedCatch.add(utility.INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        // local1[0] = local2
        s = Grimp.v().newAssignStmt(arrayRef, scalarRef);
        expectedRep.add(utility.ARRAY_STORE_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        expectedCatch.add(utility.ARRAY_STORE_EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJIdentityStmt() {
        Stmt s = Jimple.v().newIdentityStmt(Grimp.v().newLocal("local0", IntType.v()), Jimple.v().newCaughtExceptionRef());
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        s = Jimple.v().newIdentityStmt(Grimp.v().newLocal("local0", RefType.v("java.lang.NullPointerException")), Jimple.v().newThisRef(RefType.v("java.lang.NullPointerException")));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        s = Jimple.v().newIdentityStmt(Grimp.v().newLocal("local0", RefType.v("java.lang.NullPointerException")), Jimple.v().newParameterRef(RefType.v("java.lang.NullPointerException"), 0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGIdentityStmt() {
        Stmt s = Grimp.v().newIdentityStmt(Grimp.v().newLocal("local0", IntType.v()), Grimp.v().newCaughtExceptionRef());
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        s = Grimp.v().newIdentityStmt(Grimp.v().newLocal("local0", RefType.v("java.lang.NullPointerException")), Grimp.v().newThisRef(RefType.v("java.lang.NullPointerException")));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        s = Grimp.v().newIdentityStmt(Grimp.v().newLocal("local0", RefType.v("java.lang.NullPointerException")), Grimp.v().newParameterRef(RefType.v("java.lang.NullPointerException"), 0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJEnterMonitorStmt() {
        Stmt s = Jimple.v().newEnterMonitorStmt(StringConstant.v("test"));
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGEnterMonitorStmt() {
        Stmt s = Grimp.v().newEnterMonitorStmt(StringConstant.v("test"));
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJExitMonitorStmt() {
        Stmt s = Jimple.v().newExitMonitorStmt(StringConstant.v("test"));
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        expectedRep.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        expectedCatch.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGExitMonitorStmt() {
        Stmt s = Grimp.v().newExitMonitorStmt(StringConstant.v("test"));
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        expectedRep.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        expectedCatch.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJGotoStmt() {
        Stmt nop = Jimple.v().newNopStmt();
        Stmt s = Jimple.v().newGotoStmt(nop);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGGotoStmt() {
        Stmt nop = Grimp.v().newNopStmt();
        Stmt s = Grimp.v().newGotoStmt(nop);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJIfStmt() {
        IfStmt s = Jimple.v().newIfStmt(Jimple.v().newEqExpr(IntConstant.v(1), IntConstant.v(1)), ((Unit) (null)));
        s.setTarget(s);// A very tight infinite loop.

        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGIfStmt() {
        IfStmt s = Grimp.v().newIfStmt(Grimp.v().newEqExpr(IntConstant.v(1), IntConstant.v(1)), ((Unit) (null)));
        s.setTarget(s);// A very tight infinite loop.

        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJLookupSwitchStmt() {
        Stmt target = Jimple.v().newAssignStmt(Jimple.v().newLocal("local0", IntType.v()), IntConstant.v(0));
        Stmt s = Jimple.v().newLookupSwitchStmt(IntConstant.v(1), Collections.singletonList(IntConstant.v(1)), Collections.singletonList(target), target);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGLookupSwitchStmt() {
        Stmt target = Grimp.v().newAssignStmt(Grimp.v().newLocal("local0", IntType.v()), IntConstant.v(0));
        Stmt s = Grimp.v().newLookupSwitchStmt(IntConstant.v(1), Arrays.asList(new Value[]{ IntConstant.v(1) }), Arrays.asList(new Unit[]{ target }), target);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJNopStmt() {
        Stmt s = Jimple.v().newNopStmt();
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGNopStmt() {
        Stmt s = Grimp.v().newNopStmt();
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJTableSwitchStmt() {
        Stmt target = Jimple.v().newAssignStmt(Jimple.v().newLocal("local0", IntType.v()), IntConstant.v(0));
        Stmt s = Jimple.v().newTableSwitchStmt(IntConstant.v(1), 0, 1, Arrays.asList(new Unit[]{ target }), target);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGTableSwitchStmt() {
        Stmt target = Grimp.v().newAssignStmt(Grimp.v().newLocal("local0", IntType.v()), IntConstant.v(0));
        Stmt s = Grimp.v().newTableSwitchStmt(IntConstant.v(1), 0, 1, Arrays.asList(new Unit[]{ target }), target);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJThrowStmt() {
        // First test with an argument that is included in
        // PERENNIAL_THROW_EXCEPTIONS.
        ThrowStmt s = Jimple.v().newThrowStmt(Jimple.v().newLocal("local0", RefType.v("java.lang.NullPointerException")));
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.PERENNIAL_THROW_EXCEPTIONS);
        expectedRep.remove(utility.NULL_POINTER_EXCEPTION);
        expectedRep.add(AnySubType.v(utility.NULL_POINTER_EXCEPTION));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.PERENNIAL_THROW_EXCEPTIONS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        // Throw a local of type IncompatibleClassChangeError.
        Local local = Jimple.v().newLocal("local1", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        s.setOp(local);
        expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE);
        expectedRep.remove(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        expectedRep.add(AnySubType.v(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUBTYPES_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        // Throw a local of unknown type.
        local = Jimple.v().newLocal("local1", soot.UnknownType.v());
        s.setOp(local);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testGThrowStmt() {
        ThrowStmt s = Grimp.v().newThrowStmt(Grimp.v().newLocal("local0", RefType.v("java.util.zip.ZipException")));
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.PERENNIAL_THROW_EXCEPTIONS);
        expectedRep.add(AnySubType.v(Scene.v().getRefType("java.util.zip.ZipException")));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.PERENNIAL_THROW_EXCEPTIONS_PLUS_SUPERTYPES);
        // We don't need to add java.util.zip.ZipException, since it is not
        // in the universe of test Throwables.
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        // Now throw a new IncompatibleClassChangeError.
        s = Grimp.v().newThrowStmt(Grimp.v().newNewInvokeExpr(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR, Scene.v().makeMethodRef(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR.getSootClass(), "void <init>", Collections.EMPTY_LIST, VoidType.v(), false), new ArrayList()));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        // Throw a local of type IncompatibleClassChangeError.
        Local local = Grimp.v().newLocal("local1", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        s.setOp(local);
        expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.PERENNIAL_THROW_EXCEPTIONS);
        expectedRep.remove(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        expectedRep.add(AnySubType.v(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUBTYPES_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
        // Throw a local of unknown type.
        local = Jimple.v().newLocal("local1", soot.UnknownType.v());
        s.setOp(local);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
        Assert.assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
    }

    @Test
    public void testJArrayRef() {
        ArrayRef arrayRef = Jimple.v().newArrayRef(Jimple.v().newLocal("local1", ArrayType.v(RefType.v("java.lang.Object"), 1)), IntConstant.v(0));
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        expectedRep.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(arrayRef)));
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedCatch.add(utility.INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(arrayRef)));
    }

    @Test
    public void testGArrayRef() {
        ArrayRef arrayRef = Grimp.v().newArrayRef(Grimp.v().newLocal("local1", ArrayType.v(RefType.v("java.lang.Object"), 1)), IntConstant.v(0));
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        expectedRep.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(arrayRef)));
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedCatch.add(utility.INDEX_OUT_OF_BOUNDS_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(arrayRef)));
    }

    @Test
    public void testJDivExpr() {
        Set vmAndArithmetic = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        vmAndArithmetic.add(utility.ARITHMETIC_EXCEPTION);
        Set vmAndArithmeticAndSupertypes = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        vmAndArithmeticAndSupertypes.add(utility.ARITHMETIC_EXCEPTION);
        vmAndArithmeticAndSupertypes.add(utility.RUNTIME_EXCEPTION);
        vmAndArithmeticAndSupertypes.add(utility.EXCEPTION);
        Local intLocal = Jimple.v().newLocal("intLocal", IntType.v());
        Local longLocal = Jimple.v().newLocal("longLocal", LongType.v());
        Local floatLocal = Jimple.v().newLocal("floatLocal", FloatType.v());
        Local doubleLocal = Jimple.v().newLocal("doubleLocal", DoubleType.v());
        DivExpr v = Jimple.v().newDivExpr(intLocal, IntConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(intLocal, IntConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(IntConstant.v(0), IntConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(intLocal, intLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(longLocal, LongConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(longLocal, LongConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(LongConstant.v(0), LongConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(longLocal, longLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(floatLocal, FloatConstant.v(0.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(floatLocal, FloatConstant.v(2.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(FloatConstant.v(0), FloatConstant.v(2.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(floatLocal, floatLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(doubleLocal, DoubleConstant.v(0.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(doubleLocal, DoubleConstant.v(2.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(DoubleConstant.v(0), DoubleConstant.v(2.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newDivExpr(doubleLocal, doubleLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
    }

    @Test
    public void testGDivExpr() {
        Set vmAndArithmetic = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        vmAndArithmetic.add(utility.ARITHMETIC_EXCEPTION);
        Set vmAndArithmeticAndSupertypes = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        vmAndArithmeticAndSupertypes.add(utility.ARITHMETIC_EXCEPTION);
        vmAndArithmeticAndSupertypes.add(utility.RUNTIME_EXCEPTION);
        vmAndArithmeticAndSupertypes.add(utility.EXCEPTION);
        Local intLocal = Grimp.v().newLocal("intLocal", IntType.v());
        Local longLocal = Grimp.v().newLocal("longLocal", LongType.v());
        Local floatLocal = Grimp.v().newLocal("floatLocal", FloatType.v());
        Local doubleLocal = Grimp.v().newLocal("doubleLocal", DoubleType.v());
        DivExpr v = Grimp.v().newDivExpr(intLocal, IntConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(intLocal, IntConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(IntConstant.v(0), IntConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(intLocal, intLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(Grimp.v().newAddExpr(intLocal, intLocal), Grimp.v().newMulExpr(intLocal, intLocal));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(longLocal, LongConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(longLocal, LongConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(LongConstant.v(0), LongConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(longLocal, longLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(Grimp.v().newAddExpr(longLocal, longLocal), Grimp.v().newMulExpr(longLocal, longLocal));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(floatLocal, FloatConstant.v(0.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(floatLocal, FloatConstant.v(2.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(FloatConstant.v(0), FloatConstant.v(2.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(floatLocal, floatLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(doubleLocal, DoubleConstant.v(0.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(doubleLocal, DoubleConstant.v(2.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(DoubleConstant.v(0), DoubleConstant.v(2.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newDivExpr(doubleLocal, doubleLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
    }

    @Test
    public void testJRemExpr() {
        Set vmAndArithmetic = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        vmAndArithmetic.add(utility.ARITHMETIC_EXCEPTION);
        Set vmAndArithmeticAndSupertypes = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        vmAndArithmeticAndSupertypes.add(utility.ARITHMETIC_EXCEPTION);
        vmAndArithmeticAndSupertypes.add(utility.RUNTIME_EXCEPTION);
        vmAndArithmeticAndSupertypes.add(utility.EXCEPTION);
        Local intLocal = Jimple.v().newLocal("intLocal", IntType.v());
        Local longLocal = Jimple.v().newLocal("longLocal", LongType.v());
        Local floatLocal = Jimple.v().newLocal("floatLocal", FloatType.v());
        Local doubleLocal = Jimple.v().newLocal("doubleLocal", DoubleType.v());
        RemExpr v = Jimple.v().newRemExpr(intLocal, IntConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(intLocal, IntConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(IntConstant.v(0), IntConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(intLocal, intLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(longLocal, LongConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(longLocal, LongConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(LongConstant.v(0), LongConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(longLocal, longLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(floatLocal, FloatConstant.v(0.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(floatLocal, FloatConstant.v(2.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(FloatConstant.v(0), FloatConstant.v(2.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(floatLocal, floatLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(doubleLocal, DoubleConstant.v(0.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(doubleLocal, DoubleConstant.v(2.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(DoubleConstant.v(0), DoubleConstant.v(2.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newRemExpr(doubleLocal, doubleLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
    }

    @Test
    public void testGRemExpr() {
        Set vmAndArithmetic = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        vmAndArithmetic.add(utility.ARITHMETIC_EXCEPTION);
        Set vmAndArithmeticAndSupertypes = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        vmAndArithmeticAndSupertypes.add(utility.ARITHMETIC_EXCEPTION);
        vmAndArithmeticAndSupertypes.add(utility.RUNTIME_EXCEPTION);
        vmAndArithmeticAndSupertypes.add(utility.EXCEPTION);
        Local intLocal = Grimp.v().newLocal("intLocal", IntType.v());
        Local longLocal = Grimp.v().newLocal("longLocal", LongType.v());
        Local floatLocal = Grimp.v().newLocal("floatLocal", FloatType.v());
        Local doubleLocal = Grimp.v().newLocal("doubleLocal", DoubleType.v());
        RemExpr v = Grimp.v().newRemExpr(intLocal, IntConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(intLocal, IntConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(IntConstant.v(0), IntConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(intLocal, intLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(Grimp.v().newAddExpr(intLocal, intLocal), Grimp.v().newMulExpr(intLocal, intLocal));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(longLocal, LongConstant.v(0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(longLocal, LongConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(LongConstant.v(0), LongConstant.v(2));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(longLocal, longLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(Grimp.v().newAddExpr(longLocal, longLocal), Grimp.v().newMulExpr(longLocal, longLocal));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(floatLocal, FloatConstant.v(0.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(floatLocal, FloatConstant.v(2.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(FloatConstant.v(0), FloatConstant.v(2.0F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(floatLocal, floatLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(doubleLocal, DoubleConstant.v(0.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(doubleLocal, DoubleConstant.v(2.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(DoubleConstant.v(0), DoubleConstant.v(2.0));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Grimp.v().newRemExpr(doubleLocal, doubleLocal);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
    }

    @Test
    public void testJBinOpExp() {
        Value v = Jimple.v().newAddExpr(IntConstant.v(456), Jimple.v().newLocal("local", IntType.v()));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newOrExpr(Jimple.v().newLocal("local", LongType.v()), LongConstant.v(33));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newLeExpr(Jimple.v().newLocal("local", FloatType.v()), FloatConstant.v(33.42F));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        v = Jimple.v().newEqExpr(DoubleConstant.v((-0.03345)), Jimple.v().newLocal("local", DoubleType.v()));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
    }

    @Test
    public void testJCastExpr() {
        // First an upcast that can be statically proved safe.
        Value v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR), utility.LINKAGE_ERROR);
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_AND_RESOLVE_CLASS_ERRORS_REP);
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_AND_RESOLVE_CLASS_ERRORS_PLUS_SUPERTYPES);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        // Then a vacuous cast which can be statically proved safe.
        v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.LINKAGE_ERROR), utility.LINKAGE_ERROR);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        // Finally a downcast which is not necessarily safe:
        v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.LINKAGE_ERROR), utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        expectedRep.add(utility.CLASS_CAST_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        expectedCatch.add(utility.CLASS_CAST_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
    }

    @Test
    public void testGCastExpr() {
        // First an upcast that can be statically proved safe.
        Value v = Grimp.v().newCastExpr(Jimple.v().newLocal("local", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR), utility.LINKAGE_ERROR);
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_AND_RESOLVE_CLASS_ERRORS_REP);
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_AND_RESOLVE_CLASS_ERRORS_PLUS_SUPERTYPES);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        // Then a vacuous cast which can be statically proved safe.
        v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.LINKAGE_ERROR), utility.LINKAGE_ERROR);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
        // Finally a downcast which is not necessarily safe:
        v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.LINKAGE_ERROR), utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        expectedRep.add(utility.CLASS_CAST_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        expectedCatch.add(utility.CLASS_CAST_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
    }

    @Test
    public void testGInstanceFieldRef() {
        Local local = Grimp.v().newLocal("local", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_AND_RESOLVE_FIELD_ERRORS_REP);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_AND_RESOLVE_FIELD_ERRORS_PLUS_SUPERTYPES);
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Value v = Grimp.v().newInstanceFieldRef(local, Scene.v().makeFieldRef(utility.THROWABLE.getSootClass(), "detailMessage", RefType.v("java.lang.String"), false));
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
    }

    @Test
    public void testStringConstant() {
        Value v = StringConstant.v("test");
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
    }

    @Test
    public void testJLocal() {
        Local local = Jimple.v().newLocal("local1", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(local)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(local)));
    }

    @Test
    public void testGLocal() {
        Local local = Grimp.v().newLocal("local1", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(local)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(local)));
    }

    @Test
    public void testBAddInst() {
        soot.baf.AddInst i = soot.baf.Baf.v().newAddInst(IntType.v());
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(i)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(i)));
    }

    @Test
    public void testBAndInst() {
        soot.baf.AndInst i = soot.baf.Baf.v().newAndInst(IntType.v());
        Assert.assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(i)));
        Assert.assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(i)));
    }

    @Test
    public void testBArrayLengthInst() {
        soot.baf.ArrayLengthInst i = soot.baf.Baf.v().newArrayLengthInst();
        Set expectedRep = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS);
        expectedRep.add(utility.NULL_POINTER_EXCEPTION);
        Assert.assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(i)));
        Set expectedCatch = new ExceptionTestUtility.ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
        expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
        expectedCatch.add(utility.RUNTIME_EXCEPTION);
        expectedCatch.add(utility.EXCEPTION);
        Assert.assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(i)));
    }
}

