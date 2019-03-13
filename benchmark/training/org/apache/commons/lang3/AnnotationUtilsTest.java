/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 */
public class AnnotationUtilsTest {
    @AnnotationUtilsTest.TestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, nest = @AnnotationUtilsTest.NestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.CURLY, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.SHEMP }, string = "", strings = { "" }, type = Object.class, types = { Object.class }), nests = { @AnnotationUtilsTest.NestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.CURLY, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.SHEMP }, string = "", strings = { "" }, type = Object[].class, types = { Object[].class }) }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.SHEMP, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.CURLY }, string = "", strings = { "" }, type = Object.class, types = { Object.class })
    public Object dummy1;

    @AnnotationUtilsTest.TestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, nest = @AnnotationUtilsTest.NestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.CURLY, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.SHEMP }, string = "", strings = { "" }, type = Object.class, types = { Object.class }), nests = { @AnnotationUtilsTest.NestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.CURLY, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.SHEMP }, string = "", strings = { "" }, type = Object[].class, types = { Object[].class }) }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.SHEMP, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.CURLY }, string = "", strings = { "" }, type = Object.class, types = { Object.class })
    public Object dummy2;

    @AnnotationUtilsTest.TestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, nest = @AnnotationUtilsTest.NestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.CURLY, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.SHEMP }, string = "", strings = { "" }, type = Object.class, types = { Object.class }), nests = { @AnnotationUtilsTest.NestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.CURLY, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.SHEMP }, string = "", strings = { "" }, type = Object[].class, types = { Object[].class }), @AnnotationUtilsTest.NestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.CURLY, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.SHEMP }, string = "", strings = { "" }, type = Object[].class, types = { Object[].class }) }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.SHEMP, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.CURLY }, string = "", strings = { "" }, type = Object.class, types = { Object.class })
    public Object dummy3;

    @AnnotationUtilsTest.NestAnnotation(booleanValue = false, booleanValues = { false }, byteValue = 0, byteValues = { 0 }, charValue = 0, charValues = { 0 }, doubleValue = 0, doubleValues = { 0 }, floatValue = 0, floatValues = { 0 }, intValue = 0, intValues = { 0 }, longValue = 0, longValues = { 0 }, shortValue = 0, shortValues = { 0 }, stooge = AnnotationUtilsTest.Stooge.CURLY, stooges = { AnnotationUtilsTest.Stooge.MOE, AnnotationUtilsTest.Stooge.LARRY, AnnotationUtilsTest.Stooge.SHEMP }, string = "", strings = { "" }, type = Object[].class, types = { Object[].class })
    public Object dummy4;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
        String string();

        String[] strings();

        Class<?> type();

        Class<?>[] types();

        byte byteValue();

        byte[] byteValues();

        short shortValue();

        short[] shortValues();

        int intValue();

        int[] intValues();

        char charValue();

        char[] charValues();

        long longValue();

        long[] longValues();

        float floatValue();

        float[] floatValues();

        double doubleValue();

        double[] doubleValues();

        boolean booleanValue();

        boolean[] booleanValues();

        AnnotationUtilsTest.Stooge stooge();

        AnnotationUtilsTest.Stooge[] stooges();

        AnnotationUtilsTest.NestAnnotation nest();

        AnnotationUtilsTest.NestAnnotation[] nests();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface NestAnnotation {
        String string();

        String[] strings();

        Class<?> type();

        Class<?>[] types();

        byte byteValue();

        byte[] byteValues();

        short shortValue();

        short[] shortValues();

        int intValue();

        int[] intValues();

        char charValue();

        char[] charValues();

        long longValue();

        long[] longValues();

        float floatValue();

        float[] floatValues();

        double doubleValue();

        double[] doubleValues();

        boolean booleanValue();

        boolean[] booleanValues();

        AnnotationUtilsTest.Stooge stooge();

        AnnotationUtilsTest.Stooge[] stooges();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface TestMethodAnnotation {
        Class<? extends Throwable> expected() default AnnotationUtilsTest.TestMethodAnnotation.None.class;

        long timeout() default 0L;

        class None extends Throwable {}
    }

    public enum Stooge {

        MOE,
        LARRY,
        CURLY,
        JOE,
        SHEMP;}

    private Field field1;

    private Field field2;

    private Field field3;

    private Field field4;

    @Test
    public void testEquivalence() {
        Assertions.assertTrue(AnnotationUtils.equals(field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class), field2.getAnnotation(AnnotationUtilsTest.TestAnnotation.class)));
        Assertions.assertTrue(AnnotationUtils.equals(field2.getAnnotation(AnnotationUtilsTest.TestAnnotation.class), field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class)));
    }

    @Test
    public void testSameInstance() {
        Assertions.assertTrue(AnnotationUtils.equals(field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class), field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class)));
    }

    @Test
    public void testNonEquivalentAnnotationsOfSameType() {
        Assertions.assertFalse(AnnotationUtils.equals(field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class), field3.getAnnotation(AnnotationUtilsTest.TestAnnotation.class)));
        Assertions.assertFalse(AnnotationUtils.equals(field3.getAnnotation(AnnotationUtilsTest.TestAnnotation.class), field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class)));
    }

    @Test
    public void testAnnotationsOfDifferingTypes() {
        Assertions.assertFalse(AnnotationUtils.equals(field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class), field4.getAnnotation(AnnotationUtilsTest.NestAnnotation.class)));
        Assertions.assertFalse(AnnotationUtils.equals(field4.getAnnotation(AnnotationUtilsTest.NestAnnotation.class), field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class)));
    }

    @Test
    public void testOneArgNull() {
        Assertions.assertFalse(AnnotationUtils.equals(field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class), null));
        Assertions.assertFalse(AnnotationUtils.equals(null, field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class)));
    }

    @Test
    public void testBothArgsNull() {
        Assertions.assertTrue(AnnotationUtils.equals(null, null));
    }

    @Test
    public void testIsValidAnnotationMemberType() {
        for (final Class<?> type : new Class[]{ byte.class, short.class, int.class, char.class, long.class, float.class, double.class, boolean.class, String.class, Class.class, AnnotationUtilsTest.NestAnnotation.class, AnnotationUtilsTest.TestAnnotation.class, AnnotationUtilsTest.Stooge.class, ElementType.class }) {
            Assertions.assertTrue(AnnotationUtils.isValidAnnotationMemberType(type));
            Assertions.assertTrue(AnnotationUtils.isValidAnnotationMemberType(Array.newInstance(type, 0).getClass()));
        }
        for (final Class<?> type : new Class[]{ Object.class, Map.class, Collection.class }) {
            Assertions.assertFalse(AnnotationUtils.isValidAnnotationMemberType(type));
            Assertions.assertFalse(AnnotationUtils.isValidAnnotationMemberType(Array.newInstance(type, 0).getClass()));
        }
    }

    @Test
    public void testGeneratedAnnotationEquivalentToRealAnnotation() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(666L), () -> {
            final Test real = getClass().getDeclaredMethod("testGeneratedAnnotationEquivalentToRealAnnotation").getAnnotation(Test.class);
            final InvocationHandler generatedTestInvocationHandler = new InvocationHandler() {
                @Override
                public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                    if (("equals".equals(method.getName())) && ((method.getParameterTypes().length) == 1)) {
                        return Boolean.valueOf((proxy == (args[0])));
                    }
                    if (("hashCode".equals(method.getName())) && ((method.getParameterTypes().length) == 0)) {
                        return Integer.valueOf(System.identityHashCode(proxy));
                    }
                    if (("toString".equals(method.getName())) && ((method.getParameterTypes().length) == 0)) {
                        return "Test proxy";
                    }
                    return method.invoke(real, args);
                }
            };
            final Test generated = ((Test) (Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ Test.class }, generatedTestInvocationHandler)));
            Assertions.assertEquals(real, generated);
            Assertions.assertNotEquals(generated, real);
            Assertions.assertTrue(AnnotationUtils.equals(generated, real));
            Assertions.assertTrue(AnnotationUtils.equals(real, generated));
            final Test generated2 = ((Test) (Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ Test.class }, generatedTestInvocationHandler)));
            Assertions.assertNotEquals(generated, generated2);
            Assertions.assertNotEquals(generated2, generated);
            Assertions.assertTrue(AnnotationUtils.equals(generated, generated2));
            Assertions.assertTrue(AnnotationUtils.equals(generated2, generated));
        });
    }

    @Test
    public void testHashCode() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(666L), () -> {
            final Test test = getClass().getDeclaredMethod("testHashCode").getAnnotation(Test.class);
            Assertions.assertEquals(test.hashCode(), AnnotationUtils.hashCode(test));
            final AnnotationUtilsTest.TestAnnotation testAnnotation1 = field1.getAnnotation(AnnotationUtilsTest.TestAnnotation.class);
            Assertions.assertEquals(testAnnotation1.hashCode(), AnnotationUtils.hashCode(testAnnotation1));
            final AnnotationUtilsTest.TestAnnotation testAnnotation3 = field3.getAnnotation(AnnotationUtilsTest.TestAnnotation.class);
            Assertions.assertEquals(testAnnotation3.hashCode(), AnnotationUtils.hashCode(testAnnotation3));
        });
    }

    @Test
    @AnnotationUtilsTest.TestMethodAnnotation(timeout = 666000)
    public void testToString() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(666L), () -> {
            final AnnotationUtilsTest.TestMethodAnnotation testAnnotation = getClass().getDeclaredMethod("testToString").getAnnotation(AnnotationUtilsTest.TestMethodAnnotation.class);
            final String annotationString = AnnotationUtils.toString(testAnnotation);
            Assertions.assertTrue(annotationString.startsWith("@org.apache.commons.lang3.AnnotationUtilsTest$TestMethodAnnotation("));
            Assertions.assertTrue(annotationString.endsWith(")"));
            Assertions.assertTrue(annotationString.contains("expected=class org.apache.commons.lang3.AnnotationUtilsTest$TestMethodAnnotation$None"));
            Assertions.assertTrue(annotationString.contains("timeout=666000"));
            Assertions.assertTrue(annotationString.contains(", "));
        });
    }
}

