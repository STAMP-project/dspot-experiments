/**
 * Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.vm;


import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class VMTest {
    @Test
    public void multiArrayCreated() {
        int[][] array = new int[2][3];
        Assert.assertEquals(2, array.length);
        Assert.assertEquals(3, array[0].length);
        Assert.assertEquals(int[][].class, array.getClass());
        Assert.assertEquals(int[].class, array[0].getClass());
    }

    @Test
    public void catchExceptionFromLambda() {
        try {
            Runnable r = () -> throwException();
            r.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void emptyMultiArrayCreated() {
        int[][] array = new int[0][0];
        Assert.assertEquals(0, array.length);
        Assert.assertEquals(int[][].class, array.getClass());
    }

    @Test
    public void emptyMultiArrayCreated2() {
        int[][][] array = new int[1][0][1];
        Assert.assertEquals(1, array.length);
        Assert.assertEquals(0, array[0].length);
        Assert.assertEquals(int[][][].class, array.getClass());
    }

    @Test
    public void emptyMultiArrayCreated3() {
        int[][][] array = new int[1][1][0];
        Assert.assertEquals(1, array.length);
        Assert.assertEquals(1, array[0].length);
        Assert.assertEquals(0, array[0][0].length);
        Assert.assertEquals(int[][][].class, array.getClass());
    }

    @Test
    public void catchesException() {
        try {
            throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    @Test
    public void setsVariableBeforeTryCatch() {
        int a = 23;
        try {
            a = Integer.parseInt("not a number");
        } catch (NumberFormatException e) {
            // do nothing
        }
        Assert.assertEquals(23, a);
    }

    @Test
    public void emptyTryCatchInsideFinally() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("before;");
            try {
                sb.append("inside;");
                Integer.parseInt("not a number");
                sb.append("ignore;");
            } catch (NumberFormatException e) {
                // do nothing
            }
            sb.append("after;");
        } finally {
            sb.append("finally;");
        }
        Assert.assertEquals("before;inside;after;finally;", sb.toString());
    }

    @Test
    public void catchFinally() {
        StringBuilder sb = new StringBuilder();
        try {
            if ((Integer.parseInt("invalid")) > 0) {
                sb.append("err1;");
            } else {
                sb.append("err2;");
            }
            sb.append("err3");
        } catch (NumberFormatException e) {
            sb.append("catch;");
        } finally {
            sb.append("finally;");
        }
        Assert.assertEquals("catch;finally;", sb.toString());
    }

    @Test
    public void surrogateInStringLiteralsWork() {
        Assert.assertEquals(56770, "a\uddc2b".charAt(1));
    }

    @Test
    public void subtractingNegativeWorks() {
        int a = 23;
        int b = a - -1;
        Assert.assertEquals(24, b);
    }

    @Test
    public void separatesExceptionAndVariable() {
        int n = foo();
        try {
            bar();
        } catch (RuntimeException e) {
            Assert.assertEquals(RuntimeException.class, e.getClass());
            Assert.assertEquals(2, n);
        }
    }

    // See https://github.com/konsoletyper/teavm/issues/167
    @Test
    public void passesStaticFieldToSuperClassConstructor() {
        VMTest.SubClass obj = new VMTest.SubClass();
        Assert.assertNotNull(obj.getValue());
    }

    // See https://github.com/konsoletyper/teavm/issues/196
    @Test
    public void stringConstantsInitializedProperly() {
        Assert.assertEquals("FIRST ", VMTest.ClassWithStaticField.foo(true));
        Assert.assertEquals("SECOND ", VMTest.ClassWithStaticField.foo(false));
    }

    @Test
    public void stringConcat() {
        Assert.assertEquals("(23)", surroundWithParentheses(23));
        Assert.assertEquals("(42)", surroundWithParentheses(42));
    }

    @Test
    public void variableReadInCatchBlock() {
        int n = foo();
        try {
            for (int i = 0; i < 10; ++i) {
                n += foo();
            }
            bar();
            n += (foo()) * 5;
        } catch (RuntimeException e) {
            Assert.assertEquals(RuntimeException.class, e.getClass());
            Assert.assertEquals(n, 22);
        }
    }

    @Test
    public void inlineThrow() {
        int x = id(23);
        if (x == 42) {
            x++;
            throwException();
            x++;
        }
        Assert.assertEquals(x, id(23));
    }

    @Test
    @SkipJVM
    public void asyncClinit() {
        Assert.assertEquals(0, VMTest.initCount);
        Assert.assertEquals("foo", VMTest.AsyncClinitClass.foo());
        Assert.assertEquals(1, VMTest.initCount);
        Assert.assertEquals("ok", VMTest.AsyncClinitClass.state);
        Assert.assertEquals("bar", VMTest.AsyncClinitClass.bar());
        Assert.assertEquals(1, VMTest.initCount);
        Assert.assertEquals("ok", VMTest.AsyncClinitClass.state);
    }

    @Test
    public void asyncClinitField() {
        Assert.assertEquals("ok", VMTest.AsyncClinitClass.state);
    }

    @Test
    public void asyncClinitInstance() {
        VMTest.AsyncClinitClass acl = new VMTest.AsyncClinitClass();
        Assert.assertEquals("ok", VMTest.AsyncClinitClass.state);
        Assert.assertEquals("ok", acl.instanceState);
    }

    @Test
    public void asyncWait() {
        VMTest.AsyncClinitClass acl = new VMTest.AsyncClinitClass();
        acl.doWait();
        Assert.assertEquals("ok", acl.instanceState);
    }

    @Test
    @SkipJVM
    public void loopAndExceptionPhi() {
        int[] a = VMTest.createArray();
        int s = 0;
        for (int i = 0; i < 10; ++i) {
            int x = 0;
            try {
                x += 2;
                x += 3;
            } catch (RuntimeException e) {
                Assert.fail(("Unexpected exception caught: " + x));
            }
            s += (a[0]) + (a[1]);
        }
        Assert.assertEquals(30, s);
    }

    @Test
    @SkipJVM
    public void asyncTryCatch() {
        try {
            VMTest.throwExceptionAsync();
            Assert.fail("Exception should have been thrown");
        } catch (RuntimeException e) {
            Assert.assertEquals("OK", e.getMessage());
        }
    }

    @Test
    @SkipJVM
    public void asyncExceptionHandler() {
        try {
            throw new RuntimeException("OK");
        } catch (RuntimeException e) {
            Assert.assertEquals("OK", VMTest.suspendAndReturn(e).getMessage());
        }
    }

    @Test
    public void defaultMethodsSupported() {
        VMTest.WithDefaultMethod[] instances = new VMTest.WithDefaultMethod[]{ new VMTest.WithDefaultMethodDerivedA(), new VMTest.WithDefaultMethodDerivedB(), new VMTest.WithDefaultMethodDerivedC() };
        StringBuilder sb = new StringBuilder();
        for (VMTest.WithDefaultMethod instance : instances) {
            sb.append(((((instance.foo()) + ",") + (instance.bar())) + ";"));
        }
        Assert.assertEquals("default,A;default,B;overridden,C;", sb.toString());
    }

    interface WithDefaultMethod {
        default String foo() {
            return "default";
        }

        String bar();
    }

    class WithDefaultMethodDerivedA implements VMTest.WithDefaultMethod {
        @Override
        public String bar() {
            return "A";
        }
    }

    class WithDefaultMethodDerivedB implements VMTest.WithDefaultMethod {
        @Override
        public String bar() {
            return "B";
        }
    }

    class WithDefaultMethodDerivedC implements VMTest.WithDefaultMethod {
        @Override
        public String foo() {
            return "overridden";
        }

        @Override
        public String bar() {
            return "C";
        }
    }

    static int initCount;

    private static class AsyncClinitClass {
        static String state = "";

        String instanceState = "";

        static {
            (VMTest.initCount)++;
            try {
                Thread.sleep(1);
                VMTest.AsyncClinitClass.state += "ok";
            } catch (InterruptedException e) {
                VMTest.AsyncClinitClass.state += "error";
            }
        }

        public static String foo() {
            return "foo";
        }

        public static String bar() {
            return "bar";
        }

        public AsyncClinitClass() {
            instanceState += "ok";
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
        }

        public synchronized void doWait() {
            new Thread(() -> {
                synchronized(this) {
                    notify();
                }
            }).start();
            try {
                Thread.sleep(1);
                synchronized(this) {
                    wait();
                }
            } catch (InterruptedException ie) {
                instanceState = "error";
                throw new RuntimeException(ie);
            }
        }
    }

    private static class ClassWithStaticField {
        public static final String CONST1 = "FIRST";

        public static final String CONST2 = "SECOND";

        public static String foo(boolean value) {
            StringBuilder sb = new StringBuilder();
            sb.append((value ? VMTest.ClassWithStaticField.CONST1 : VMTest.ClassWithStaticField.CONST2)).append(" ");
            return sb.toString();
        }
    }

    static class SuperClass {
        static final Integer ONE = new Integer(1);

        private Integer value;

        public SuperClass(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    static class SubClass extends VMTest.SuperClass {
        SubClass() {
            super(VMTest.SuperClass.ONE);
        }
    }

    @Test
    public void indirectDefaultMethod() {
        VMTest.PathJoint o = new VMTest.PathJoint();
        Assert.assertEquals("SecondPath.foo", o.foo());
    }

    interface FirstPath {
        default String foo() {
            return "FirstPath.foo";
        }
    }

    interface SecondPath extends VMTest.FirstPath {
        @Override
        default String foo() {
            return "SecondPath.foo";
        }
    }

    class PathJoint implements VMTest.FirstPath , VMTest.SecondPath {}

    @Test
    public void cloneArray() {
        String[] a = new String[]{ "foo" };
        String[] b = a.clone();
        Assert.assertNotSame(a, b);
        a[0] = "bar";
        Assert.assertEquals("foo", b[0]);
    }

    @Test
    public void stringConstantsInBaseClass() {
        new VMTest.DerivedClassWithConstantFields();
    }

    static class BaseClassWithConstantFields {
        public final String foo = "bar";
    }

    static class DerivedClassWithConstantFields extends VMTest.BaseClassWithConstantFields {}

    interface ScriptExecutionWrapper {
        Object wrap(Supplier<Object> execution);
    }
}

