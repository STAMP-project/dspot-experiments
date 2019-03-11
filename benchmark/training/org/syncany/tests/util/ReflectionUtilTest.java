/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.tests.util;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.util.ReflectionUtil;


public class ReflectionUtilTest {
    @Test
    public void testMatchingConstructor() throws Exception {
        final Constructor<?> reference = ReflectionUtilTest.X.class.getConstructor(ReflectionUtilTest.A.class, ReflectionUtilTest.A.class);
        Assert.assertNotNull(reference);
        for (Constructor<?> c : ReflectionUtilTest.X.class.getConstructors()) {
            System.out.println(c.toString());
        }
        Assert.assertNotNull(ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class, ReflectionUtilTest.A.class, ReflectionUtilTest.A.class));
        Assert.assertEquals(reference, ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class, ReflectionUtilTest.A.class, ReflectionUtilTest.A.class));
        Assert.assertNotNull(ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class, ReflectionUtilTest.A.class, ReflectionUtilTest.C.class));
        Assert.assertNotNull(ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class, ReflectionUtilTest.A.class));
        Assert.assertNotNull(ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class, ReflectionUtilTest.B.class));
        Assert.assertNull(ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class, ReflectionUtilTest.A.class, ReflectionUtilTest.B.class));// type erasure :(

        Assert.assertNull(ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class, ReflectionUtilTest.B.class, ReflectionUtilTest.B.class));
        Assert.assertNull(ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class, ReflectionUtilTest.B.class, ReflectionUtilTest.A.class));
        Assert.assertNull(ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class, ReflectionUtilTest.A.class, ReflectionUtilTest.B.class, ReflectionUtilTest.B.class));
        Assert.assertNull(ReflectionUtil.getMatchingConstructorForClass(ReflectionUtilTest.X.class));
    }

    @Test
    public void testIsEnum() throws Exception {
        Assert.assertTrue(ReflectionUtil.isValidEnum("Value1", ReflectionUtilTest.SomeEnum.class));
        Assert.assertTrue(ReflectionUtil.isValidEnum("Value2", ReflectionUtilTest.SomeEnum.class));
        Assert.assertFalse(ReflectionUtil.isValidEnum("VALUE1", ReflectionUtilTest.SomeEnum.class));
        Assert.assertFalse(ReflectionUtil.isValidEnum("VALUE2", ReflectionUtilTest.SomeEnum.class));
        Assert.assertFalse(ReflectionUtil.isValidEnum("InvalidValue", ReflectionUtilTest.SomeEnum.class));
    }

    @Test
    public void testGetMethodsWithAnnotation() throws Exception {
        Assert.assertEquals(2, ReflectionUtil.getAllMethodsWithAnnotation(ReflectionUtilTest.ClassWithAnnotations.class, ReflectionUtilTest.SomeAnnotation.class).length);
        Assert.assertEquals(1, ReflectionUtil.getAllMethodsWithAnnotation(ReflectionUtilTest.ClassWithAnnotations.class, ReflectionUtilTest.AnotherAnnotation.class).length);
    }

    @Test
    public void testGetFieldsWithAnnotation() throws Exception {
        Assert.assertEquals(2, ReflectionUtil.getAllFieldsWithAnnotation(ReflectionUtilTest.ClassWithAnnotations.class, ReflectionUtilTest.SomeAnnotation.class).length);
        Assert.assertEquals(1, ReflectionUtil.getAllFieldsWithAnnotation(ReflectionUtilTest.ClassWithAnnotations.class, ReflectionUtilTest.AnotherAnnotation.class).length);
    }

    public static class X {
        public <T extends ReflectionUtilTest.A> X(ReflectionUtilTest.A a, T b) {
        }

        public X(ReflectionUtilTest.B b) {
        }

        public X(ReflectionUtilTest.A a, ReflectionUtilTest.C c) {
        }
    }

    public abstract static class A {}

    public static class B extends ReflectionUtilTest.A {}

    public static class C extends ReflectionUtilTest.A {}

    public enum SomeEnum {

        Value1,
        Value2;}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SomeAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnotherAnnotation {}

    @ReflectionUtilTest.SomeAnnotation
    @SuppressWarnings("unused")
    public static class ClassWithAnnotations {
        @ReflectionUtilTest.SomeAnnotation
        private int field1;

        @ReflectionUtilTest.SomeAnnotation
        @ReflectionUtilTest.AnotherAnnotation
        private String field2;

        private boolean field3;

        @ReflectionUtilTest.SomeAnnotation
        public void method1() {
        }

        @ReflectionUtilTest.SomeAnnotation
        @ReflectionUtilTest.AnotherAnnotation
        public void method2() {
        }

        public void method3() {
        }
    }
}

