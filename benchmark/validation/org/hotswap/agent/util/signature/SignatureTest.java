/**
 * Copyright 2013-2019 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package org.hotswap.agent.util.signature;


import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hotswap.agent.javassist.ClassPool;
import org.hotswap.agent.javassist.CtClass;
import org.hotswap.agent.javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;

import static ClassSignatureElement.CLASS_ANNOTATION;
import static ClassSignatureElement.CONSTRUCTOR;
import static ClassSignatureElement.CONSTRUCTOR_PRIVATE;
import static ClassSignatureElement.FIELD;
import static ClassSignatureElement.FIELD_ANNOTATION;
import static ClassSignatureElement.FIELD_STATIC;
import static ClassSignatureElement.INTERFACES;
import static ClassSignatureElement.METHOD;
import static ClassSignatureElement.METHOD_ANNOTATION;
import static ClassSignatureElement.METHOD_EXCEPTION;
import static ClassSignatureElement.METHOD_PARAM_ANNOTATION;
import static ClassSignatureElement.METHOD_PRIVATE;
import static ClassSignatureElement.METHOD_STATIC;
import static ClassSignatureElement.SUPER_CLASS;


public class SignatureTest {
    private static ClassSignatureElement[] SIGNATURE_ELEMENTS = new ClassSignatureElement[]{ SUPER_CLASS, INTERFACES, CLASS_ANNOTATION, CONSTRUCTOR, CONSTRUCTOR_PRIVATE, METHOD, METHOD_PRIVATE, METHOD_STATIC, METHOD_ANNOTATION, METHOD_PARAM_ANNOTATION, METHOD_EXCEPTION, FIELD, FIELD_STATIC, FIELD_ANNOTATION };

    public enum SigTestEnum {

        NEW,
        FINISHED,
        WAITING;}

    @Target({ ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Asd {
        SignatureTest.SigTestEnum value() default SignatureTest.SigTestEnum.FINISHED;

        SignatureTest.SigTestEnum value2() default SignatureTest.SigTestEnum.FINISHED;

        String[] array() default { "string" };
    }

    @Target({ ElementType.PARAMETER, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Bcd {}

    @SignatureTest.Asd(value2 = SignatureTest.SigTestEnum.FINISHED)
    @SignatureTest.Bcd
    public static class A {
        static {
        }

        {
        }
    }

    @SignatureTest.Asd(SignatureTest.SigTestEnum.WAITING)
    public static class OneMethod {
        @SignatureTest.Asd(SignatureTest.SigTestEnum.WAITING)
        int aField;

        @SignatureTest.Asd(SignatureTest.SigTestEnum.WAITING)
        public int get9(@SignatureTest.Asd(SignatureTest.SigTestEnum.NEW)
        @SignatureTest.Bcd
        Object o, @SignatureTest.Asd(SignatureTest.SigTestEnum.NEW)
        @SignatureTest.Bcd
        Object o2) throws IOException {
            return 0;
        }
    }

    public abstract static class B implements SignatureTest.TestSignatures {
        public abstract int get8();

        public int get9() {
            return 0;
        }
    }

    public static class C extends SignatureTest.B {
        @Override
        public int get9() {
            return 0;
        }

        @Override
        public int get3() {
            return 0;
        }

        @Override
        public int get2() {
            return 0;
        }

        @Override
        public int get1() {
            return 0;
        }

        @Override
        public int getA() {
            return 0;
        }

        @Override
        public int getB() {
            return 0;
        }

        @Override
        public int[] getBArray() {
            return null;
        }

        @Override
        public Object[] getBObjectArray() {
            return null;
        }

        @Override
        public Object getBObject() {
            return null;
        }

        @Override
        public int get3(int[] a) {
            return 0;
        }

        @Override
        public int get2(int[] a, int[] b) {
            return 0;
        }

        @Override
        public int get1(int[]... b) {
            return 0;
        }

        @Override
        public int getA(Object o) {
            return 0;
        }

        @Override
        public int getB(Object... o) {
            return 0;
        }

        @Override
        public int get3(Object[] a) {
            return 0;
        }

        @Override
        public int get2(Object[] a, Object[] b) {
            return 0;
        }

        @Override
        public int getA(Object[] a, Object[]... b) {
            return 0;
        }

        @Override
        public int getB(Object[]... o) {
            return 0;
        }

        @Override
        public int get8() {
            return 0;
        }

        public int get123() throws IOException, NotFoundException {
            return 0;
        }
    }

    public interface TestSignatures {
        // test ordering
        public int get3();

        public int get2();

        public int get1();

        public int getA();

        public int getB();

        // test return types
        public int[] getBArray();

        public Object[] getBObjectArray();

        public Object getBObject();

        // test parameters
        public int get3(int[] a);

        public int get2(int[] a, int[] b);

        public int get1(int[]... b);

        public int getA(Object o);

        public int getB(Object... o);

        public int get3(Object[] a);

        public int get2(@SignatureTest.Asd
        Object[] a, @SignatureTest.Asd
        Object[] b);

        public int getA(Object[] a, Object[]... b);

        public int getB(Object[]... o);
    }

    @Test
    public void testInterfaceSignature() throws Exception {
        CtClass makeClass = ClassPool.getDefault().get(SignatureTest.TestSignatures.class.getName());
        String expected = ClassSignatureComparerHelper.getJavaClassSignature(SignatureTest.TestSignatures.class, SignatureTest.SIGNATURE_ELEMENTS);
        String actual = ClassSignatureComparerHelper.getCtClassSignature(makeClass, SignatureTest.SIGNATURE_ELEMENTS);
        Assert.assertEquals("Signatures not equal", expected, actual);
    }

    @Test
    public void testClassSignature() throws Exception {
        {
            CtClass makeClass = ClassPool.getDefault().get(SignatureTest.A.class.getName());
            String expected = ClassSignatureComparerHelper.getJavaClassSignature(SignatureTest.A.class, SignatureTest.SIGNATURE_ELEMENTS);
            String actual = ClassSignatureComparerHelper.getCtClassSignature(makeClass, SignatureTest.SIGNATURE_ELEMENTS);
            Assert.assertEquals("Signatures not equal", expected, actual);
        }
        {
            CtClass makeClass = ClassPool.getDefault().get(SignatureTest.C.class.getName());
            String expected = ClassSignatureComparerHelper.getJavaClassSignature(SignatureTest.C.class, SignatureTest.SIGNATURE_ELEMENTS);
            String actual = ClassSignatureComparerHelper.getCtClassSignature(makeClass, SignatureTest.SIGNATURE_ELEMENTS);
            Assert.assertEquals("Signatures not equal", expected, actual);
        }
    }

    @Test
    public void testAbstractClassSignature() throws Exception {
        CtClass makeClass = ClassPool.getDefault().get(SignatureTest.B.class.getName());
        String expected = ClassSignatureComparerHelper.getJavaClassSignature(SignatureTest.B.class, SignatureTest.SIGNATURE_ELEMENTS);
        String actual = ClassSignatureComparerHelper.getCtClassSignature(makeClass, SignatureTest.SIGNATURE_ELEMENTS);
        Assert.assertEquals("Signatures not equal", expected, actual);
    }

    @Test
    public void testOne() throws Exception {
        CtClass makeClass = ClassPool.getDefault().get(SignatureTest.OneMethod.class.getName());
        String expected = ClassSignatureComparerHelper.getJavaClassSignature(SignatureTest.OneMethod.class, SignatureTest.SIGNATURE_ELEMENTS);
        String actual = ClassSignatureComparerHelper.getCtClassSignature(makeClass, SignatureTest.SIGNATURE_ELEMENTS);
        Assert.assertEquals("Signatures not equal", expected, actual);
    }

    @Test
    public void switchSignatureTest() throws Exception {
        CtClass makeClass = ClassPool.getDefault().get(SwitchTestClass.class.getName());
        String expected = ClassSignatureComparerHelper.getJavaClassSignature(SwitchTestClass.class, SignatureTest.SIGNATURE_ELEMENTS);
        String actual = ClassSignatureComparerHelper.getCtClassSignature(makeClass, SignatureTest.SIGNATURE_ELEMENTS);
        Assert.assertEquals("Signatures not equal", expected, actual);
    }
}

