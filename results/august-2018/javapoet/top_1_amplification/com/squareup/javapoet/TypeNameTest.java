package com.squareup.javapoet;


import com.google.common.truth.Truth;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class TypeNameTest {
    protected <E extends Enum<E>> E generic(E[] values) {
        return values[0];
    }

    protected static class TestGeneric<T> {
        class Inner {}

        class InnerGeneric<T2> {}

        static class NestedNonGeneric {}
    }

    protected static TypeNameTest.TestGeneric<String>.Inner testGenericStringInner() {
        return null;
    }

    protected static TypeNameTest.TestGeneric<Integer>.Inner testGenericIntInner() {
        return null;
    }

    protected static TypeNameTest.TestGeneric<Short>.InnerGeneric<Long> testGenericInnerLong() {
        return null;
    }

    protected static TypeNameTest.TestGeneric<Short>.InnerGeneric<Integer> testGenericInnerInt() {
        return null;
    }

    protected static TypeNameTest.TestGeneric.NestedNonGeneric testNestedNonGeneric() {
        return null;
    }

    @Test(timeout = 10000)
    public void genericType() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType__4)).isPrimitive());
        TypeName o_genericType__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType__6)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType__6)).isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType__11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType__11)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType__11)).isAnnotated());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType__11)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType__11)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType__11)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType__6)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType__6)).isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericTypenull175765() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericTypenull175765__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericTypenull175765__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericTypenull175765__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypenull175765__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypenull175765__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericTypenull175765__4)).isPrimitive());
        TypeName o_genericTypenull175765__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericTypenull175765__6)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericTypenull175765__6)).isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericTypenull175765__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypenull175765__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericTypenull175765__6)).isPrimitive());
        TypeName genericTypeName = null;
        TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
    }

    @Test(timeout = 10000)
    public void genericTypenull175772() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericTypenull175772__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericTypenull175772__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericTypenull175772__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypenull175772__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypenull175772__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericTypenull175772__4)).isPrimitive());
        TypeName o_genericTypenull175772__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericTypenull175772__6)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericTypenull175772__6)).isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericTypenull175772__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypenull175772__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericTypenull175772__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericTypenull175772__11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericTypenull175772__11)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericTypenull175772__11)).isAnnotated());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericTypenull175772__11)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericTypenull175772__11)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericTypenull175772__11)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericTypenull175772__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericTypenull175772__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypenull175772__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypenull175772__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericTypenull175772__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericTypenull175772__6)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericTypenull175772__6)).isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericTypenull175772__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypenull175772__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericTypenull175772__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_add175740_add179958() throws Exception {
        Method o_genericType_add175740__1 = getClass().getDeclaredMethod("generic", Enum[].class);
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_add175740__6 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_add175740__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_add175740__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isPrimitive());
        TypeName o_genericType_add175740__8 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_add175740__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_add175740__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_add175740__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_add175740__13)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_add175740__13)).isAnnotated());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_add175740__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_add175740__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_add175740__13)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_add175740__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_add175740__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_add175740__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_add175740__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_add175740litNum176373_failAssert61null189918() throws Exception {
        try {
            Method o_genericType_add175740__1 = getClass().getDeclaredMethod("generic", Enum[].class);
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericType_add175740__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_add175740__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_add175740__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isPrimitive());
            TypeName o_genericType_add175740__8 = null;
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[-1]);
            TypeName o_genericType_add175740__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            org.junit.Assert.fail("genericType_add175740litNum176373 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void genericType_add175740litNum176373_failAssert61null189948() throws Exception {
        try {
            Method o_genericType_add175740__1 = getClass().getDeclaredMethod("generic", Enum[].class);
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericType_add175740__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_add175740__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_add175740__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isPrimitive());
            TypeName o_genericType_add175740__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isBoxedPrimitive());
            Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isAnnotated());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericType_add175740__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_add175740__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[-1]);
            TypeName o_genericType_add175740__13 = null;
            org.junit.Assert.fail("genericType_add175740litNum176373 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void genericType_add175740_add178501_add184706() throws Exception {
        Method o_genericType_add175740__1 = getClass().getDeclaredMethod("generic", Enum[].class);
        Method o_genericType_add175740_add178501__5 = getClass().getDeclaredMethod("generic", Enum[].class);
        ((Method) (o_genericType_add175740_add178501__5)).toString();
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_add175740__6 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_add175740__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_add175740__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isPrimitive());
        TypeName o_genericType_add175740__8 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_add175740__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_add175740__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_add175740__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_add175740__13)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_add175740__13)).isAnnotated());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_add175740__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_add175740__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_add175740__13)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_add175740__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_add175740__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_add175740__6)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_add175740__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_add175740__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_add175740__8)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test
    public void innerClassInGenericType() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        Truth.assertThat(genericTypeName.toString()).isEqualTo(((TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypenull218176() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericTypenull218176__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericTypenull218176__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericTypenull218176__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypenull218176__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypenull218176__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericTypenull218176__4)).isPrimitive());
        TypeName genericTypeName = null;
        TypeName.get(genericStringInner.getGenericReturnType());
        TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        String String_41 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypenull218191_failAssert102null219388() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypenull218191_failAssert102null219388__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypenull218191_failAssert102null219388__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypenull218191_failAssert102null219388__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypenull218191_failAssert102null219388__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypenull218191_failAssert102null219388__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypenull218191_failAssert102null219388__6)).isPrimitive());
            TypeName genericTypeName = null;
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod(null).getGenericReturnType());
            String String_56 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            org.junit.Assert.fail("innerClassInGenericTypenull218191 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void innerGenericInGenericType() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        Truth.assertThat(genericTypeName.toString()).isEqualTo(((TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypenull228270() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericTypenull228270__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypenull228270__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypenull228270__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypenull228270__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypenull228270__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypenull228270__4)).isPrimitive());
        TypeName genericTypeName = null;
        TypeName.get(genericStringInner.getGenericReturnType());
        TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        String String_100 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypenull228285_failAssert129null229381() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypenull228285_failAssert129null229381__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypenull228285_failAssert129null229381__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypenull228285_failAssert129null229381__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypenull228285_failAssert129null229381__6)).toString());
            Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypenull228285_failAssert129null229381__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypenull228285_failAssert129null229381__6)).isPrimitive());
            TypeName genericTypeName = null;
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod(null).getGenericReturnType());
            String String_115 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            org.junit.Assert.fail("innerGenericInGenericTypenull228285 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_2 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_2);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericTypenull238619() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericTypenull238619__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericTypenull238619__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericTypenull238619__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericTypenull238619__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericTypenull238619__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericTypenull238619__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericTypenull238619__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericTypenull238619__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericTypenull238619__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericTypenull238619__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericTypenull238619__4)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add238595null240817() throws Exception {
        Method o_innerStaticInGenericType_add238595__1 = getClass().getDeclaredMethod("testNestedNonGeneric");
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add238595__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add238595__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add238595__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_129 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_129);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add238595__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add238595__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add238595_add240175null259077() throws Exception {
        Method o_innerStaticInGenericType_add238595__1 = getClass().getDeclaredMethod("testNestedNonGeneric");
        Method o_innerStaticInGenericType_add238595_add240175__5 = getClass().getDeclaredMethod("testNestedNonGeneric");
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add238595__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add238595__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add238595__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_129 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_129);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add238595__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add238595__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add238595__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
    }

    @Test
    public void equalsAndHashCodePrimitive() {
        assertEqualsHashCodeAndToString(TypeName.BOOLEAN, TypeName.BOOLEAN);
        assertEqualsHashCodeAndToString(TypeName.BYTE, TypeName.BYTE);
        assertEqualsHashCodeAndToString(TypeName.CHAR, TypeName.CHAR);
        assertEqualsHashCodeAndToString(TypeName.DOUBLE, TypeName.DOUBLE);
        assertEqualsHashCodeAndToString(TypeName.FLOAT, TypeName.FLOAT);
        assertEqualsHashCodeAndToString(TypeName.INT, TypeName.INT);
        assertEqualsHashCodeAndToString(TypeName.LONG, TypeName.LONG);
        assertEqualsHashCodeAndToString(TypeName.SHORT, TypeName.SHORT);
        assertEqualsHashCodeAndToString(TypeName.VOID, TypeName.VOID);
    }

    @Test
    public void equalsAndHashCodeArrayTypeName() {
        assertEqualsHashCodeAndToString(ArrayTypeName.of(Object.class), ArrayTypeName.of(Object.class));
        assertEqualsHashCodeAndToString(TypeName.get(Object[].class), ArrayTypeName.of(Object.class));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeNamenull13() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeNamenull13__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeNamenull13__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeNamenull13__3 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__3)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull13__2)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeNamenull6null355() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeNamenull6__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeNamenull6__2 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeNamenull6__3 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__3)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull6__2)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeNamenull9null367null10118() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeNamenull9__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__1)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__1)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeNamenull9__2 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__2)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeNamenull9__2)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeNamenull9__3 = null;
    }

    @Test
    public void equalsAndHashCodeClassName() {
        assertEqualsHashCodeAndToString(ClassName.get(Object.class), ClassName.get(Object.class));
        assertEqualsHashCodeAndToString(TypeName.get(Object.class), ClassName.get(Object.class));
        assertEqualsHashCodeAndToString(ClassName.bestGuess("java.lang.Object"), ClassName.get(Object.class));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamenull26662() throws Exception {
        ClassName o_equalsAndHashCodeClassNamenull26662__1 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamenull26662__2 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamenull26662__3 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamenull26662__4 = ClassName.bestGuess("java.lang.Object");
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamenull26662__5 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26662__5)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26662__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__5)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26662__4)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString26645_failAssert2() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString26645 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString26640_failAssert0() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("java.lang");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString26640 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for java.lang", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString26646_failAssert3() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("\n");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString26646 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for \n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString26646_failAssert3_add26862() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__4)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__5 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26646_failAssert3_add26862__7)).isPrimitive());
            ClassName.bestGuess("\n");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString26646 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString26640_failAssert0_add26864() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__4)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__5 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26640_failAssert0_add26864__7)).isPrimitive());
            ClassName.bestGuess("java.lang");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString26640 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString26645_failAssert2_add26914() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__4)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__5 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__5)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__6 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString26645_failAssert2_add26914__6)).isPrimitive());
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString26645 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamenull26671_failAssert5null26931null52366() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__4)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__5 = null;
            ClassName o_equalsAndHashCodeClassNamenull26671_failAssert5null26931__6 = ClassName.get(Object.class);
            ClassName.bestGuess(null);
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamenull26671 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void equalsAndHashCodeParameterizedTypeName() {
        assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Object.class), ParameterizedTypeName.get(Object.class));
        assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Set.class, UUID.class), ParameterizedTypeName.get(Set.class, UUID.class));
        Assert.assertNotEquals(ClassName.get(List.class), ParameterizedTypeName.get(List.class, String.class));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7() throws Exception {
        try {
            ParameterizedTypeName.get(Object.class);
            ParameterizedTypeName.get(Object.class);
            ParameterizedTypeName.get(Set.class, UUID.class);
            ParameterizedTypeName.get(null, UUID.class);
            ClassName.get(List.class);
            ParameterizedTypeName.get(List.class, String.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeNamenull65125 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("clazz == null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeNamenull65119() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeNamenull65119__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeNamenull65119__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeNamenull65119__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeNamenull65119__4 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeNamenull65119__5 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__5)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__5)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__5)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__5)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__5)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65119__4)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263() throws Exception {
        try {
            TypeName o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__3 = ParameterizedTypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__3)).isPrimitive());
            TypeName o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__4 = ParameterizedTypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65121_failAssert6null65263__4)).isPrimitive());
            ParameterizedTypeName.get(null, UUID.class);
            ParameterizedTypeName.get(Set.class, UUID.class);
            ClassName.get(List.class);
            ParameterizedTypeName.get(List.class, String.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeNamenull65121 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254null77314() throws Exception {
        try {
            TypeName o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__3 = ParameterizedTypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__3)).isPrimitive());
            TypeName o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__4 = ParameterizedTypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeNamenull65125_failAssert7null65254__4)).isPrimitive());
            ParameterizedTypeName.get(null, UUID.class);
            ParameterizedTypeName.get(null, UUID.class);
            ClassName.get(List.class);
            ParameterizedTypeName.get(List.class, String.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeNamenull65125 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableName__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113811_failAssert10() throws Exception {
        try {
            TypeVariableName.get(Object.class);
            TypeVariableName.get(Object.class);
            TypeVariableName typeVar1 = TypeVariableName.get("T", null, Serializable.class);
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113811 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("unexpected type: null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamelitString113790() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableNamelitString113790__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableNamelitString113790__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("\n", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113790__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamelitString113793() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableNamelitString113793__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableNamelitString113793__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("D", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("D", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(68, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString113793__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113810_failAssert9() throws Exception {
        try {
            TypeVariableName.get(Object.class);
            TypeVariableName.get(Object.class);
            TypeVariableName typeVar1 = TypeVariableName.get(null, Comparator.class, Serializable.class);
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113810 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("name == null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203() throws Exception {
        try {
            TypeName o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113812_failAssert11null114203__4)).isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, null);
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113812 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21() throws Exception {
        try {
            try {
                TypeVariableName.get(Object.class);
                TypeVariableName.get(Object.class);
                TypeVariableName typeVar1 = TypeVariableName.get(null, Comparator.class, Serializable.class);
                TypeVariableName typeVar2 = TypeVariableName.get("T", null, Serializable.class);
                org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113816 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("name == null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006() throws Exception {
        try {
            TypeName o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113817_failAssert14litString114006__4)).isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("\n", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
            Assert.assertEquals("\n", ((TypeVariableName) (typeVar1)).toString());
            Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, null);
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113817 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114154_failAssert17() throws Exception {
        try {
            try {
                TypeVariableName.get(Object.class);
                TypeVariableName.get(Object.class);
                TypeVariableName typeVar1 = TypeVariableName.get("T", null, Serializable.class);
                TypeVariableName typeVar2 = TypeVariableName.get(null, Comparator.class, Serializable.class);
                org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113815 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114154 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("unexpected type: null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120() throws Exception {
        try {
            TypeName o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__4)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__5 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12_add114120__5)).isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
            TypeVariableName typeVar2 = TypeVariableName.get(null, Comparator.class, Serializable.class);
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113815 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088() throws Exception {
        try {
            try {
                TypeName o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__5 = TypeVariableName.get(Object.class);
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__5)).isAnnotated());
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__5)).isBoxedPrimitive());
                Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__5)).toString());
                Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__5)).hashCode())));
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__5)).isPrimitive());
                TypeName o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__6 = TypeVariableName.get(Object.class);
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__6)).isAnnotated());
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__6)).isBoxedPrimitive());
                Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__6)).toString());
                Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__6)).hashCode())));
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146_failAssert16litString123088__6)).isPrimitive());
                TypeVariableName typeVar1 = TypeVariableName.get(null, Comparator.class, null);
                TypeVariableName typeVar2 = TypeVariableName.get("", Comparator.class, Serializable.class);
                org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113810 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113810_failAssert9null114146 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468() throws Exception {
        try {
            try {
                TypeName o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__5 = TypeVariableName.get(Object.class);
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__5)).isAnnotated());
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__5)).isBoxedPrimitive());
                Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__5)).toString());
                Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__5)).hashCode())));
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__5)).isPrimitive());
                TypeName o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__6 = TypeVariableName.get(Object.class);
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__6)).isAnnotated());
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__6)).isBoxedPrimitive());
                Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__6)).toString());
                Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__6)).hashCode())));
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104_failAssert21_add123468__6)).isPrimitive());
                TypeVariableName.get(null, Comparator.class, Serializable.class);
                TypeVariableName typeVar1 = TypeVariableName.get(null, Comparator.class, Serializable.class);
                TypeVariableName typeVar2 = TypeVariableName.get("T", null, Serializable.class);
                org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113816 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113816_failAssert13null114104 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268() throws Exception {
        try {
            try {
                TypeName o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__5 = TypeVariableName.get(Object.class);
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__5)).isAnnotated());
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__5)).isBoxedPrimitive());
                Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__5)).toString());
                Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__5)).hashCode())));
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__5)).isPrimitive());
                TypeName o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__6 = TypeVariableName.get(Object.class);
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__6)).isAnnotated());
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__6)).isBoxedPrimitive());
                Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__6)).toString());
                Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__6)).hashCode())));
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__6)).isPrimitive());
                TypeName o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__7 = TypeVariableName.get(Object.class);
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__7)).isAnnotated());
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__7)).isBoxedPrimitive());
                Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__7)).toString());
                Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__7)).hashCode())));
                Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166_failAssert18_add122268__7)).isPrimitive());
                TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
                Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
                Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
                Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
                Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
                Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
                TypeVariableName typeVar2 = TypeVariableName.get(null, null, Serializable.class);
                org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113815 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableNamenull113815_failAssert12null114166 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeNamenull127587() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127587__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127587__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127587__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127587__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127587__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127587__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127587__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeNamenull127591null129420() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127591__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127591__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127591__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127591__4 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127591__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__5)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127591__4)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeNamenull127598null129173_add168542() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127598__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127598__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127598__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127598__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeNamenull127598__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).isPrimitive());
        ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).isBoxedPrimitive();
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeNamenull127598__5)).isPrimitive());
    }

    @Test
    public void isPrimitive() throws Exception {
        Truth.assertThat(TypeName.INT.isPrimitive()).isTrue();
        Truth.assertThat(ClassName.get("java.lang", "Integer").isPrimitive()).isFalse();
        Truth.assertThat(ClassName.get("java.lang", "String").isPrimitive()).isFalse();
        Truth.assertThat(TypeName.VOID.isPrimitive()).isFalse();
        Truth.assertThat(ClassName.get("java.lang", "Void").isPrimitive()).isFalse();
    }

    @Test
    public void isBoxedPrimitive() throws Exception {
        Truth.assertThat(TypeName.INT.isBoxedPrimitive()).isFalse();
        Truth.assertThat(ClassName.get("java.lang", "Integer").isBoxedPrimitive()).isTrue();
        Truth.assertThat(ClassName.get("java.lang", "String").isBoxedPrimitive()).isFalse();
        Truth.assertThat(TypeName.VOID.isBoxedPrimitive()).isFalse();
        Truth.assertThat(ClassName.get("java.lang", "Void").isBoxedPrimitive()).isFalse();
    }

    private void assertEqualsHashCodeAndToString(TypeName a, TypeName b) {
        Assert.assertEquals(a.toString(), b.toString());
        Truth.assertThat(a.equals(b)).isTrue();
        Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        Assert.assertFalse(a.equals(null));
    }
}

