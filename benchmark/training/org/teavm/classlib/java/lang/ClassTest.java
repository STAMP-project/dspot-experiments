/**
 * Copyright 2013 Alexey Andreev.
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
package org.teavm.classlib.java.lang;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ClassTest {
    @Test
    public void classNameEvaluated() {
        Assert.assertEquals("java.lang.Object", Object.class.getName());
        Assert.assertEquals("[Ljava.lang.Object;", Object[].class.getName());
        Assert.assertEquals("int", int.class.getName());
        Assert.assertEquals("[I", int[].class.getName());
    }

    @Test
    public void classSimpleNameEvaluated() {
        Assert.assertEquals("Object", Object.class.getSimpleName());
        Assert.assertEquals("Object[]", Object[].class.getSimpleName());
        Assert.assertEquals("int", int.class.getSimpleName());
        Assert.assertEquals("int[]", int[].class.getSimpleName());
        Assert.assertEquals("InnerClass", ClassTest.InnerClass.class.getSimpleName());
        Assert.assertEquals("", new Object() {}.getClass().getSimpleName());
    }

    @Test
    public void objectClassNameEvaluated() {
        Assert.assertEquals("java.lang.Object", new Object().getClass().getName());
    }

    @Test
    public void superClassFound() {
        Assert.assertEquals(Number.class, Integer.class.getSuperclass());
    }

    @Test
    public void superClassOfObjectIsNull() {
        Assert.assertNull(Object.class.getSuperclass());
    }

    @Test
    public void superClassOfArrayIsObject() {
        Assert.assertEquals(Object.class, Runnable[].class.getSuperclass());
    }

    @Test
    public void superClassOfPrimitiveIsNull() {
        Assert.assertNull(int.class.getSuperclass());
    }

    @Test
    public void objectClassConsideredNotArray() {
        Assert.assertFalse(Object.class.isArray());
    }

    @Test
    public void arrayClassConsideredArray() {
        Assert.assertTrue(Object[].class.isArray());
    }

    @Test
    public void arrayComponentTypeDetected() {
        Assert.assertEquals(Object.class, Object[].class.getComponentType());
    }

    @Test
    public void arrayOfArraysComponentTypeDetected() {
        Assert.assertEquals(Object[].class, Object[][].class.getComponentType());
    }

    @Test
    public void nonArrayComponentTypeIsNull() {
        Assert.assertNull(Object.class.getComponentType());
    }

    @Test
    public void castingAppropriateObject() {
        Object obj = 23;
        Assert.assertEquals(Integer.valueOf(23), Integer.class.cast(obj));
    }

    @Test(expected = ClassCastException.class)
    public void inappropriateObjectCastingFails() {
        Object obj = 23;
        Float.class.cast(obj);
    }

    @Test
    public void instanceCreatedThroughReflection() throws Exception {
        Runnable instance = ((Runnable) (Class.forName(TestObject.class.getName()).newInstance()));
        instance.run();
        Assert.assertEquals(TestObject.class, instance.getClass());
        Assert.assertEquals(1, ((TestObject) (instance)).getCounter());
    }

    @Test
    public void instanceCreatedThroughReflectionAsync() throws Exception {
        Runnable instance = TestObjectAsync.class.newInstance();
        instance.run();
        Assert.assertEquals(TestObjectAsync.class, instance.getClass());
        Assert.assertEquals(2, ((TestObjectAsync) (instance)).getCounter());
    }

    @Test
    public void declaringClassFound() {
        Assert.assertEquals(ClassTest.class, new ClassTest.A().getClass().getDeclaringClass());
    }

    @Test
    public void annotationsExposed() {
        Annotation[] annotations = ClassTest.A.class.getAnnotations();
        Assert.assertEquals(1, annotations.length);
        Assert.assertTrue(ClassTest.TestAnnot.class.isAssignableFrom(annotations[0].getClass()));
    }

    @Test
    public void annotationFieldsExposed() {
        ClassTest.AnnotWithDefaultField annot = ClassTest.B.class.getAnnotation(ClassTest.AnnotWithDefaultField.class);
        Assert.assertEquals(2, annot.x());
        annot = ClassTest.C.class.getAnnotation(ClassTest.AnnotWithDefaultField.class);
        Assert.assertEquals(3, annot.x());
    }

    @Test
    public void annotationFieldTypesSupported() {
        ClassTest.AnnotWithVariousFields annot = ClassTest.D.class.getAnnotation(ClassTest.AnnotWithVariousFields.class);
        Assert.assertEquals(true, annot.a());
        Assert.assertEquals(((byte) (2)), annot.b());
        Assert.assertEquals(((short) (3)), annot.c());
        Assert.assertEquals(4, annot.d());
        Assert.assertEquals(5L, annot.e());
        Assert.assertEquals(6.5, annot.f(), 0.01);
        Assert.assertEquals(7.2, annot.g(), 0.01);
        Assert.assertArrayEquals(new int[]{ 2, 3 }, annot.h());
        Assert.assertEquals(RetentionPolicy.CLASS, annot.i());
        Assert.assertEquals(Retention.class, annot.j().annotationType());
        Assert.assertEquals(1, annot.k().length);
        Assert.assertEquals(RetentionPolicy.RUNTIME, annot.k()[0].value());
        Assert.assertEquals("foo", annot.l());
        Assert.assertArrayEquals(new String[]{ "bar" }, annot.m());
        Assert.assertEquals(Integer.class, annot.n());
    }

    @ClassTest.TestAnnot
    private static class A {}

    @ClassTest.AnnotWithDefaultField
    private static class B {}

    @ClassTest.AnnotWithDefaultField(x = 3)
    private static class C {}

    @ClassTest.AnnotWithVariousFields(a = true, b = 2, c = 3, d = 4, e = 5, f = 6.5F, g = 7.2, h = { 2, 3 }, i = RetentionPolicy.CLASS, j = @Retention(RetentionPolicy.SOURCE), k = { @Retention(RetentionPolicy.RUNTIME) }, l = "foo", m = "bar", n = Integer.class)
    private static class D {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnot {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AnnotWithDefaultField {
        int x() default 2;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface AnnotWithVariousFields {
        boolean a();

        byte b();

        short c();

        int d();

        long e();

        float f();

        double g();

        int[] h();

        RetentionPolicy i();

        Retention j();

        Retention[] k();

        String l();

        String[] m();

        Class<?> n();
    }

    static class InnerClass {}
}

