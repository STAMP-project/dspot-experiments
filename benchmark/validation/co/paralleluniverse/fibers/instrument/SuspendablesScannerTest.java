/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.fibers.instrument;


import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;


public class SuspendablesScannerTest {
    private static SuspendablesScanner scanner;

    private static final Set<String> suspendables = new HashSet<>();

    private static final Set<String> suspendableSupers = new HashSet<>();

    @Test
    public void suspendableCallTest() {
        final String method = (SuspendablesScannerTest.B.class.getName()) + ".foo(I)V";
        Assert.assertTrue(SuspendablesScannerTest.suspendables.contains(method));
    }

    @Test
    public void superSuspendableCallTest() {
        final String method = ((SuspendablesScannerTest.A.class.getName()) + ".foo") + (Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(SuspendablesScannerTest.IA.class)));
        Assert.assertTrue(SuspendablesScannerTest.suspendables.contains(method));
    }

    @Test
    public void nonSuperSuspendableCallTest() {
        final String method = (SuspendablesScannerTest.A.class.getName()) + ".foo()";
        Assert.assertTrue((!(SuspendablesScannerTest.suspendables.contains(method))));
    }

    @Test
    public void superNonSuspendableCallTest() {
        final String method = ((SuspendablesScannerTest.A.class.getName()) + ".bar") + (Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(SuspendablesScannerTest.IA.class)));
        Assert.assertTrue((!(SuspendablesScannerTest.suspendables.contains(method))));
    }

    @Test
    public void inheritedSuspendableCallTest() {
        final String method = ((SuspendablesScannerTest.C.class.getName()) + ".bax") + (Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(SuspendablesScannerTest.IA.class)));
        Assert.assertTrue(SuspendablesScannerTest.suspendables.contains(method));
    }

    @Test
    public void inheritedNonSuspendableCallTest() {
        final String method = (SuspendablesScannerTest.C.class.getName()) + ".fon()";
        Assert.assertTrue((!(SuspendablesScannerTest.suspendables.contains(method))));
    }

    @Test
    public void superSuspendableTest() {
        final String method = (SuspendablesScannerTest.IA.class.getName()) + ".foo(I)V";
        Assert.assertTrue(SuspendablesScannerTest.suspendableSupers.contains(method));
    }

    @Test
    public void bridgeMethodTest() {
        Assert.assertTrue(SuspendablesScannerTest.suspendableSupers.contains(((SuspendablesScannerTest.I2.class.getName()) + ".foo(I)Ljava/lang/Number;")));
        Assert.assertTrue(SuspendablesScannerTest.suspendableSupers.contains(((SuspendablesScannerTest.A2.class.getName()) + ".bar(I)Ljava/lang/Object;")));
        Assert.assertTrue((!(SuspendablesScannerTest.suspendableSupers.contains(((SuspendablesScannerTest.A2.class.getName()) + ".baz(I)Ljava/lang/Object;")))));
    }

    static interface IA {
        // super suspendable
        void foo(int t);

        // doesn't have suspandable implementation
        void bar(int t);
    }

    static class A {
        // suspendable
        void foo(SuspendablesScannerTest.IA a) {
            a.foo(0);
        }

        // not suspendable
        void foo() {
            bar(null);// test that if foo->bar->foo->... doesn't cause infinite loop

        }

        // not suspendable
        void bar(SuspendablesScannerTest.IA a) {
            a.bar(0);
            foo();
        }

        // suspendable
        void baz(SuspendablesScannerTest.IA a) {
            a.foo(0);
        }
    }

    static class B implements SuspendablesScannerTest.IA {
        // suspendable
        @Override
        public void foo(int t) {
            try {
                Fiber.park();
            } catch (SuspendExecution ex) {
                throw new RuntimeException(ex);
            }
        }

        // not suspendable
        @Override
        public void bar(int t) {
        }
    }

    static class C extends SuspendablesScannerTest.A {
        // suspendable
        void bax(SuspendablesScannerTest.IA a) {
            baz(a);
        }

        // non suspendable
        void fon() {
            foo();
        }
    }

    static interface I2 {
        Number foo(int x);
    }

    static class A2 {
        protected Object bar(int x) {
            return null;
        }

        protected Object baz(int x) {
            return null;
        }
    }

    static class C2 extends SuspendablesScannerTest.A2 implements SuspendablesScannerTest.I2 {
        @Override
        @Suspendable
        protected String bar(int x) {
            return "3";
        }

        @Override
        @Suspendable
        public Integer foo(int x) {
            return 3;
        }

        public Integer baz(int x) {
            return 3;
        }
    }
}

