/**
 * Copyright (C) 2015 Square, Inc.
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
package com.squareup.javapoet;


import com.google.common.truth.Truth;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;


public class AmplTypeNameTest {
    protected static class TestGeneric<T> {
        class Inner {}

        class InnerGeneric<T2> {}

        static class NestedNonGeneric {}
    }

    protected <E extends Enum<E>> E generic(E[] values) {
        return values[0];
    }

    protected static AmplTypeNameTest.TestGeneric<String>.Inner testGenericStringInner() {
        return null;
    }

    protected static AmplTypeNameTest.TestGeneric<Integer>.Inner testGenericIntInner() {
        return null;
    }

    protected static AmplTypeNameTest.TestGeneric<Short>.InnerGeneric<Long> testGenericInnerLong() {
        return null;
    }

    protected static AmplTypeNameTest.TestGeneric<Short>.InnerGeneric<Integer> testGenericInnerInt() {
        return null;
    }

    protected static AmplTypeNameTest.TestGeneric.NestedNonGeneric testNestedNonGeneric() {
        return null;
    }

    private void assertEqualsHashCodeAndToString(TypeName a, TypeName b) {
        Assert.assertEquals(a.toString(), b.toString());
        Truth.assertThat(a.equals(b)).isTrue();
        Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeTypeVariableName */
    /* amplification of equalsAndHashCodeTypeVariableName_sd7 */
    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_sd7_sd119_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            TypeVariableName.get(Object.class);
            TypeVariableName.get(Object.class);
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            // StatementAdd: generate variable from return value
            TypeName __DSPOT_invoc_9 = // StatementAdd: add invocation of a method
            typeVar1.withoutAnnotations();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_9.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_sd7_sd119 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#genericType */
    @Test(timeout = 10000)
    public void genericType_sd810_failAssert1() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            // StatementAdd: generate variable from return value
            TypeName __DSPOT_invoc_4 = TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("genericType_sd810 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#genericType */
    @Test(timeout = 10000)
    public void genericType_sd822_failAssert2() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            // StatementAdd: generate variable from return value
            TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_6.unbox();
            org.junit.Assert.fail("genericType_sd822 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#genericType */
    @Test(timeout = 10000)
    public void genericType_sd798_failAssert0() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            // StatementAdd: add invocation of a method
            genericTypeName.unbox();
            org.junit.Assert.fail("genericType_sd798 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#genericType */
    @Test(timeout = 10000)
    public void genericType_sd834_failAssert3() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            // StatementAdd: generate variable from return value
            TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_11.unbox();
            org.junit.Assert.fail("genericType_sd834 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @Test(timeout = 10000)
    public void innerClassInGenericType_sd11099_failAssert0() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            // StatementAdd: add invocation of a method
            genericTypeName.unbox();
            org.junit.Assert.fail("innerClassInGenericType_sd11099 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @Test(timeout = 10000)
    public void innerClassInGenericType_sd11111_failAssert1() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            // StatementAdd: generate variable from return value
            TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerClassInGenericType_sd11111 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @Test(timeout = 10000)
    public void innerGenericInGenericType_sd19249_failAssert1() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            // StatementAdd: generate variable from return value
            TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerGenericInGenericType_sd19249 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @Test(timeout = 10000)
    public void innerGenericInGenericType_sd19237_failAssert0() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            // StatementAdd: add invocation of a method
            genericTypeName.unbox();
            org.junit.Assert.fail("innerGenericInGenericType_sd19237 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerStaticInGenericType */
    @Test(timeout = 10000)
    public void innerStaticInGenericType_sd27375_failAssert1() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            // StatementAdd: generate variable from return value
            TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_sd27375 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }
}

