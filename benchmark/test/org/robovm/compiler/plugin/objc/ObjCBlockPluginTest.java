/**
 * Copyright (C) 2014 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.compiler.plugin.objc;


import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.compiler.CompilerException;
import org.robovm.compiler.util.generic.SootMethodType;
import org.robovm.compiler.util.generic.Type;
import soot.SootMethod;


/**
 * Tests {@link ObjCBlockPlugin}.
 */
public class ObjCBlockPluginTest {
    private Type BOOLEAN;

    private Type VOID;

    public static interface F<R, A extends Number, B extends Number> {
        R run(A a, B b, boolean c);
    }

    public static interface G extends ObjCBlockPluginTest.F<BigDecimal, Double, Double> {}

    public static interface H extends ObjCBlockPluginTest.F<String, Integer, Integer> {
        void foo();
    }

    public static interface I {}

    public static interface J<T> extends ObjCBlockPluginTest.F<Comparable<T>, Integer, Integer> {}

    public static class Runners<T> {
        public native void runner1(ObjCBlockPluginTest.F<String, Integer, Integer> f);

        public native void runner2(ObjCBlockPluginTest.G g);

        public native void runner3(ObjCBlockPluginTest.F<?, ?, ? extends Double> f);

        public native void runner4(ObjCBlockPluginTest.F<Comparable<String>, ?, ?> f);

        public native void runner5(ObjCBlockPluginTest.J<String> f);

        public native void runner6(ObjCBlockPluginTest.F<Comparable<String>[][], ?, ?> f);

        public native void runner7(ObjCBlockPluginTest.F<T, Integer, Integer> f);

        public native <U extends Number> void runner8(ObjCBlockPluginTest.F<Object, Integer, U> f);

        public native void runner9(Runnable r);

        public native void runner10(@SuppressWarnings("rawtypes")
        ObjCBlockPluginTest.F f);

        public native void runner11(ObjCBlockPluginTest.H h);

        public native void runner12(ObjCBlockPluginTest.I i);
    }

    @Test
    public void testGetTargetBlockMethodDirect1() throws Exception {
        Assert.assertEquals(toSootClass(ObjCBlockPluginTest.F.class).getMethodByName("run"), ObjCBlockPlugin.ObjCBlockPlugin.getBlockTargetMethod(toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner1"), 0));
    }

    @Test
    public void testGetTargetBlockMethodDirect2() throws Exception {
        Assert.assertEquals(toSootClass(Runnable.class).getMethodByName("run"), ObjCBlockPlugin.ObjCBlockPlugin.getBlockTargetMethod(toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner9"), 0));
    }

    @Test
    public void testGetTargetBlockMethodInSuperInterface() throws Exception {
        Assert.assertEquals(toSootClass(ObjCBlockPluginTest.F.class).getMethodByName("run"), ObjCBlockPlugin.ObjCBlockPlugin.getBlockTargetMethod(toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner2"), 0));
    }

    @Test(expected = CompilerException.class)
    public void testGetTargetBlockMethodTooManyMethods() throws Exception {
        ObjCBlockPlugin.ObjCBlockPlugin.getBlockTargetMethod(toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner11"), 0);
    }

    @Test(expected = CompilerException.class)
    public void testGetTargetBlockMethodNoMethods() throws Exception {
        ObjCBlockPlugin.ObjCBlockPlugin.getBlockTargetMethod(toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner12"), 0);
    }

    @Test
    public void testResolveTargetMethodSignatureDirectGeneric() throws Exception {
        testResolveTargetMethodSignature("runner1", classNameToType("java.lang.String"), classNameToType("java.lang.Integer"), classNameToType("java.lang.Integer"), BOOLEAN);
    }

    @Test
    public void testResolveTargetMethodSignatureIndirectGeneric() throws Exception {
        testResolveTargetMethodSignature("runner2", classNameToType("java.math.BigDecimal"), classNameToType("java.lang.Double"), classNameToType("java.lang.Double"), BOOLEAN);
    }

    @Test
    public void testResolveTargetMethodSignatureGenericWithWildcards() throws Exception {
        testResolveTargetMethodSignature("runner3", classNameToType("java.lang.Object"), classNameToType("java.lang.Number"), classNameToType("java.lang.Double"), BOOLEAN);
    }

    @Test
    public void testResolveTargetMethodSignatureGenericWithDirectParameterizedType() throws Exception {
        testResolveTargetMethodSignature("runner4", signatureToType("Ljava/lang/Comparable<Ljava/lang/String;>;"), classNameToType("java.lang.Number"), classNameToType("java.lang.Number"), BOOLEAN);
    }

    @Test
    public void testResolveTargetMethodSignatureGenericWithIndirectParameterizedType() throws Exception {
        testResolveTargetMethodSignature("runner5", signatureToType("Ljava/lang/Comparable<Ljava/lang/String;>;"), classNameToType("java.lang.Integer"), classNameToType("java.lang.Integer"), BOOLEAN);
    }

    @Test
    public void testResolveTargetMethodSignatureGenericWithGenericArrayType() throws Exception {
        testResolveTargetMethodSignature("runner6", signatureToType("[[Ljava/lang/Comparable<Ljava/lang/String;>;"), classNameToType("java.lang.Number"), classNameToType("java.lang.Number"), BOOLEAN);
    }

    @Test(expected = CompilerException.class)
    public void testResolveTargetMethodSignatureGenericWithUnresolvedIndirectTypeVariable() throws Exception {
        SootMethod target = toSootClass(ObjCBlockPluginTest.F.class).getMethodByName("run");
        SootMethod m = toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner7");
        SootMethodType mType = new SootMethodType(m);
        ObjCBlockPlugin.ObjCBlockPlugin.resolveTargetMethodSignature(m, target, mType.getGenericParameterTypes()[0]);
    }

    @Test(expected = CompilerException.class)
    public void testResolveTargetMethodSignatureGenericWithUnresolvedDirectTypeVariable() throws Exception {
        SootMethod target = toSootClass(ObjCBlockPluginTest.F.class).getMethodByName("run");
        SootMethod m = toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner8");
        SootMethodType mType = new SootMethodType(m);
        ObjCBlockPlugin.ObjCBlockPlugin.resolveTargetMethodSignature(m, target, mType.getGenericParameterTypes()[0]);
    }

    @Test
    public void testResolveTargetMethodSignatureNonGeneric() throws Exception {
        testResolveTargetMethodSignature("runner9", VOID);
    }

    @Test
    public void testResolveTargetMethodSignatureRawType() throws Exception {
        testResolveTargetMethodSignature("runner10", classNameToType("java.lang.Object"), classNameToType("java.lang.Number"), classNameToType("java.lang.Number"), BOOLEAN);
    }

    @Test
    public void testParseTargetMethodAnnotations() throws Exception {
        SootMethod m = toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner1");
        Assert.assertArrayEquals(new String[][]{ new String[]{  } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 0, ""));
        Assert.assertArrayEquals(new String[][]{ new String[]{  } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 0, "  "));
        Assert.assertArrayEquals(new String[][]{ new String[]{  } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 0, "()"));
        Assert.assertArrayEquals(new String[][]{ new String[]{ BY_VAL, POINTER } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 0, "@Pointer@ByVal"));
        Assert.assertArrayEquals(new String[][]{ new String[]{ BY_VAL, POINTER } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 0, "@Pointer  @ByVal ()"));
        Assert.assertArrayEquals(new String[][]{ new String[]{ BY_VAL, POINTER }, new String[]{ BY_REF } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 1, "@Pointer  @ByVal (@ByRef)"));
        Assert.assertArrayEquals(new String[][]{ new String[]{ BY_VAL, POINTER }, new String[]{ BY_REF, POINTER } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 1, "@Pointer  @ByVal (  @ByRef  @Pointer  )"));
        Assert.assertArrayEquals(new String[][]{ new String[]{ BY_VAL, POINTER }, new String[]{ BY_REF, POINTER }, new String[]{ MACHINE_SIZED_S_INT }, new String[]{  } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 3, "@Pointer  @ByVal (  @ByRef  @Pointer , @MachineSizedSInt , )"));
        Assert.assertArrayEquals(new String[][]{ new String[]{ BY_VAL, POINTER }, new String[]{  }, new String[]{ BY_REF, POINTER } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 2, "@Pointer @ByVal(,@ByRef @Pointer)"));
        Assert.assertArrayEquals(new String[][]{ new String[]{  }, new String[]{ BY_REF, POINTER } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 1, "(@ByRef @Pointer)  "));
        Assert.assertArrayEquals(new String[][]{ new String[]{  }, new String[]{ BLOCK, BY_REF, BY_VAL, MACHINE_SIZED_FLOAT, MACHINE_SIZED_S_INT, MACHINE_SIZED_U_INT, POINTER } }, ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 1, ("(@ByRef @ByVal @Pointer @MachineSizedFloat @MachineSizedSInt " + "@MachineSizedUInt @Block)  ")));
    }

    @Test(expected = CompilerException.class)
    public void testParseTargetMethodAnnotationsInvalid1() throws Exception {
        SootMethod m = toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner1");
        ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 0, "(");
    }

    @Test(expected = CompilerException.class)
    public void testParseTargetMethodAnnotationsInvalid2() throws Exception {
        SootMethod m = toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner1");
        ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 0, "@Yada");
    }

    @Test(expected = CompilerException.class)
    public void testParseTargetMethodAnnotationsInvalid3() throws Exception {
        SootMethod m = toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner1");
        ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 0, "garbage");
    }

    @Test(expected = CompilerException.class)
    public void testParseTargetMethodAnnotationsInvalid4() throws Exception {
        SootMethod m = toSootClass(ObjCBlockPluginTest.Runners.class).getMethodByName("runner1");
        ObjCBlockPlugin.ObjCBlockPlugin.parseTargetMethodAnnotations(m, 0, "@ByVal(");
    }
}

