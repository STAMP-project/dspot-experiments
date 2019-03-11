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
package org.teavm.metaprogramming.test;


import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;
import org.teavm.metaprogramming.CompileTime;
import org.teavm.metaprogramming.Metaprogramming;
import org.teavm.metaprogramming.Value;


@CompileTime
@RunWith(TeaVMTestRunner.class)
@SkipJVM
public class MetaprogrammingTest {
    @Test
    public void works() {
        Assert.assertEquals((("java.lang.Object".length()) + 2), MetaprogrammingTest.classNameLength(Object.class, 2));
        Assert.assertEquals((("java.lang.Integer".length()) + 3), MetaprogrammingTest.classNameLength(Integer.valueOf(5).getClass(), 3));
    }

    @Test
    public void getsField() {
        MetaprogrammingTest.Context ctx = new MetaprogrammingTest.Context();
        ctx.a = 2;
        ctx.b = 3;
        Assert.assertEquals(2, MetaprogrammingTest.getField(ctx.getClass(), ctx));
    }

    @Test
    public void setsField() {
        MetaprogrammingTest.Context ctx = new MetaprogrammingTest.Context();
        MetaprogrammingTest.setField(ctx.getClass(), ctx, 3);
        Assert.assertEquals(3, ctx.a);
    }

    @Test
    public void methodInvoked() {
        Assert.assertEquals("debug!", MetaprogrammingTest.callDebug(MetaprogrammingTest.A.class, new MetaprogrammingTest.A()));
        Assert.assertEquals("missing", MetaprogrammingTest.callDebug(MetaprogrammingTest.B.class, new MetaprogrammingTest.B()));
        Assert.assertEquals("missing", MetaprogrammingTest.callDebug(MetaprogrammingTest.A.class, new MetaprogrammingTest.A(), "foo", 23));
        Assert.assertEquals("debug!foo:23", MetaprogrammingTest.callDebug(MetaprogrammingTest.B.class, new MetaprogrammingTest.B(), "foo", 23));
    }

    @Test
    public void constructorInvoked() {
        Assert.assertEquals(MetaprogrammingTest.C.class.getName(), MetaprogrammingTest.callConstructor(MetaprogrammingTest.C.class).getClass().getName());
        Assert.assertNull(MetaprogrammingTest.callConstructor(MetaprogrammingTest.D.class));
        Assert.assertNull(MetaprogrammingTest.callConstructor(MetaprogrammingTest.C.class, "foo", 23));
        MetaprogrammingTest.D instance = ((MetaprogrammingTest.D) (MetaprogrammingTest.callConstructor(MetaprogrammingTest.D.class, "foo", 23)));
        Assert.assertEquals(MetaprogrammingTest.D.class.getName(), instance.getClass().getName());
        Assert.assertEquals("foo", instance.a);
        Assert.assertEquals(23, instance.b);
    }

    @Test
    public void capturesArray() {
        Assert.assertEquals("23:foo", MetaprogrammingTest.captureArray(23, "foo"));
    }

    @Test
    public void isInstanceWorks() {
        Assert.assertTrue(MetaprogrammingTest.isInstance(new MetaprogrammingTest.Context(), MetaprogrammingTest.Context.class));
        Assert.assertFalse(MetaprogrammingTest.isInstance(23, MetaprogrammingTest.Context.class));
    }

    @Test
    public void capturesNull() {
        Assert.assertEquals("foo:", MetaprogrammingTest.captureArgument("foo"));
    }

    @Test
    public void annotationsWork() {
        Assert.assertEquals(("" + (("foo:23:Object\n" + "foo=!:42:String:int\n") + "f=!:23\n")), MetaprogrammingTest.readAnnotations(MetaprogrammingTest.WithAnnotations.class, new MetaprogrammingTest.WithAnnotations()));
    }

    @Test
    public void compileTimeAnnotationRespectsPackage() {
        Assert.assertEquals("(foo)", MetaprogrammingTest.compileTimePackage(true));
    }

    @Test
    public void compileTimeAnnotationRespectsClass() {
        Assert.assertEquals("[foo]", MetaprogrammingTest.compileTimeClass(true));
    }

    @Test
    public void compileTimeAnnotationRespectsNestedClass() {
        Assert.assertEquals("{foo}", MetaprogrammingTest.compileTimeNestedClass(true));
    }

    @Test
    public void emitsClassLiteralFromReflectClass() {
        Assert.assertEquals(String[].class.getName(), MetaprogrammingTest.emitClassLiteral(String.class));
    }

    @Test
    public void createsArrayViaReflection() {
        Object array = MetaprogrammingTest.createArrayOfType(String.class, 10);
        Assert.assertEquals(String[].class, array.getClass());
        Assert.assertEquals(10, ((String[]) (array)).length);
    }

    @Test
    public void getsArrayElementViaReflection() {
        Assert.assertEquals("foo", MetaprogrammingTest.getArrayElement(String[].class, new String[]{ "foo" }, 0));
    }

    @Test
    public void lazyWorks() {
        MetaprogrammingTest.WithSideEffect a = new MetaprogrammingTest.WithSideEffect(10);
        MetaprogrammingTest.WithSideEffect b = new MetaprogrammingTest.WithSideEffect(20);
        Assert.assertEquals(1, MetaprogrammingTest.withLazy(a, b));
        Assert.assertEquals(1, a.reads);
        Assert.assertEquals(0, b.reads);
        a = new MetaprogrammingTest.WithSideEffect((-10));
        b = new MetaprogrammingTest.WithSideEffect(20);
        Assert.assertEquals(1, MetaprogrammingTest.withLazy(a, b));
        Assert.assertEquals(1, a.reads);
        Assert.assertEquals(1, b.reads);
        a = new MetaprogrammingTest.WithSideEffect((-10));
        b = new MetaprogrammingTest.WithSideEffect((-20));
        Assert.assertEquals(2, MetaprogrammingTest.withLazy(a, b));
        Assert.assertEquals(1, a.reads);
        Assert.assertEquals(1, b.reads);
    }

    @Test
    public void conditionalWorks() {
        Assert.assertEquals("int", MetaprogrammingTest.fieldType(MetaprogrammingTest.Context.class, "a"));
        Assert.assertEquals("int", MetaprogrammingTest.fieldType(MetaprogrammingTest.Context.class, "b"));
        Assert.assertNull(MetaprogrammingTest.fieldType(MetaprogrammingTest.Context.class, "c"));
    }

    @Test
    public void conditionalActionWorks() {
        class TypeConsumer implements Consumer<String> {
            String type;

            @Override
            public void accept(String t) {
                type = t;
            }
        }
        TypeConsumer consumer = new TypeConsumer();
        MetaprogrammingTest.fieldType(MetaprogrammingTest.Context.class, "a", consumer);
        Assert.assertEquals("int", consumer.type);
        MetaprogrammingTest.fieldType(MetaprogrammingTest.Context.class, "b", consumer);
        Assert.assertEquals("int", consumer.type);
        MetaprogrammingTest.fieldType(MetaprogrammingTest.Context.class, "c", consumer);
        Assert.assertNull(consumer.type);
    }

    @Test
    public void unassignedLazyEvaluated() {
        MetaprogrammingTest.withUnassignedLazy(MetaprogrammingTest.Context.class);
        Assert.assertEquals(23, MetaprogrammingTest.counter);
    }

    private static int counter;

    @Test
    public void arrayTypeSelected() {
        Assert.assertEquals(String[].class, MetaprogrammingTest.createInstance(String.class, 1).getClass());
        Assert.assertEquals(String[][].class, MetaprogrammingTest.createInstance(String[].class, 1).getClass());
    }

    @MetaprogrammingClass
    static class Context {
        public int a;

        public int b;
    }

    @MetaprogrammingClass
    class A {
        public String debug() {
            return "debug!";
        }
    }

    @MetaprogrammingClass
    class B {
        public String debug(String a, int b) {
            return (("debug!" + a) + ":") + b;
        }
    }

    @MetaprogrammingClass
    static class C {
        public C() {
        }
    }

    @MetaprogrammingClass
    static class D {
        String a;

        int b;

        public D(String a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    @TestAnnotation(a = "foo", c = Object.class)
    @MetaprogrammingClass
    static class WithAnnotations {
        @TestAnnotation(c = {  })
        int f;

        @TestAnnotation(b = 42, c = { String.class, int.class })
        int foo() {
            return 0;
        }
    }

    static class MetaprogrammingGenerator3 {
        public Value<String> addParentheses(String value) {
            return Metaprogramming.emit(() -> ("{" + value) + "}");
        }
    }

    static class WithSideEffect {
        private int value;

        public int reads;

        public WithSideEffect(int value) {
            this.value = value;
        }

        public int getValue() {
            ++(reads);
            return value;
        }
    }
}

