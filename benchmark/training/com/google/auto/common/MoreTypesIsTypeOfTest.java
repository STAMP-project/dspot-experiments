/**
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.auto.common;


import com.google.common.collect.Iterables;
import com.google.testing.compile.CompilationRule;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link MoreTypes#isTypeOf}.
 */
@RunWith(JUnit4.class)
public class MoreTypesIsTypeOfTest {
    @Rule
    public CompilationRule compilationRule = new CompilationRule();

    private Elements elements;

    private interface TestType {}

    @Test
    public void isTypeOf_DeclaredType() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.TestType.class).asType()));
        assertThat(MoreTypes.isTypeOf(MoreTypesIsTypeOfTest.TestType.class, typeElementFor(MoreTypesIsTypeOfTest.TestType.class).asType())).named("mirror represents the TestType").isTrue();
        assertThat(MoreTypes.isTypeOf(String.class, typeElementFor(MoreTypesIsTypeOfTest.TestType.class).asType())).named("mirror does not represent a String").isFalse();
    }

    private interface ArrayType {
        String[] array();
    }

    @Test
    public void isTypeOf_ArrayType() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.ArrayType.class).asType()));
        TypeMirror type = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.ArrayType.class));
        assertThat(MoreTypes.isTypeOf(new String[]{  }.getClass(), type)).named("array mirror represents an array Class object").isTrue();
    }

    private interface PrimitiveBoolean {
        boolean method();
    }

    @Test
    public void isTypeOf_PrimitiveBoolean() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveBoolean.class).asType()));
        TypeMirror type = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveBoolean.class));
        assertThat(MoreTypes.isTypeOf(Boolean.TYPE, type)).named("mirror of a boolean").isTrue();
    }

    private interface PrimitiveByte {
        byte method();
    }

    @Test
    public void isTypeOf_PrimitiveByte() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveByte.class).asType()));
        TypeMirror type = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveByte.class));
        assertThat(MoreTypes.isTypeOf(Byte.TYPE, type)).named("mirror of a byte").isTrue();
    }

    private interface PrimitiveChar {
        char method();
    }

    @Test
    public void isTypeOf_PrimitiveChar() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveChar.class).asType()));
        TypeMirror type = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveChar.class));
        assertThat(MoreTypes.isTypeOf(Character.TYPE, type)).named("mirror of a char").isTrue();
    }

    private interface PrimitiveDouble {
        double method();
    }

    @Test
    public void isTypeOf_PrimitiveDouble() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveDouble.class).asType()));
        TypeMirror type = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveDouble.class));
        assertThat(MoreTypes.isTypeOf(Double.TYPE, type)).named("mirror of a double").isTrue();
    }

    private interface PrimitiveFloat {
        float method();
    }

    @Test
    public void isTypeOf_PrimitiveFloat() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveFloat.class).asType()));
        TypeMirror type = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveFloat.class));
        assertThat(MoreTypes.isTypeOf(Float.TYPE, type)).named("mirror of a float").isTrue();
    }

    private interface PrimitiveInt {
        int method();
    }

    @Test
    public void isTypeOf_PrimitiveInt() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveInt.class).asType()));
        TypeMirror type = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveInt.class));
        assertThat(MoreTypes.isTypeOf(Integer.TYPE, type)).named("mirror of a int").isTrue();
    }

    private interface PrimitiveLong {
        long method();
    }

    @Test
    public void isTypeOf_PrimitiveLong() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveLong.class).asType()));
        TypeMirror type = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveLong.class));
        assertThat(MoreTypes.isTypeOf(Long.TYPE, type)).named("mirror of a long").isTrue();
    }

    private interface PrimitiveShort {
        short method();
    }

    @Test
    public void isTypeOf_PrimitiveShort() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveShort.class).asType()));
        TypeMirror type = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveShort.class));
        assertThat(MoreTypes.isTypeOf(Short.TYPE, type)).named("mirror of a short").isTrue();
    }

    private interface PrimitiveVoid {
        void method();
    }

    @Test
    public void isTypeOf_void() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveVoid.class).asType()));
        TypeMirror primitive = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.PrimitiveVoid.class));
        assertThat(MoreTypes.isTypeOf(Void.TYPE, primitive)).named("mirror of a void").isTrue();
    }

    private interface DeclaredVoid {
        Void method();
    }

    @Test
    public void isTypeOf_Void() {
        Assert.assertTrue(MoreTypes.isType(typeElementFor(MoreTypesIsTypeOfTest.DeclaredVoid.class).asType()));
        TypeMirror declared = extractReturnTypeFromHolder(typeElementFor(MoreTypesIsTypeOfTest.DeclaredVoid.class));
        assertThat(MoreTypes.isTypeOf(Void.class, declared)).named("mirror of a void").isTrue();
    }

    @Test
    public void isTypeOf_fail() {
        Assert.assertFalse(MoreTypes.isType(Iterables.getOnlyElement(typeElementFor(MoreTypesIsTypeOfTest.DeclaredVoid.class).getEnclosedElements()).asType()));
        TypeMirror method = Iterables.getOnlyElement(typeElementFor(MoreTypesIsTypeOfTest.DeclaredVoid.class).getEnclosedElements()).asType();
        try {
            MoreTypes.isTypeOf(String.class, method);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }
}

