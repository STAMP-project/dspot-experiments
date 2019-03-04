package com.squareup.javapoet;


import TypeName.BOOLEAN;
import TypeName.BYTE;
import TypeName.CHAR;
import TypeName.DOUBLE;
import TypeName.FLOAT;
import TypeName.INT;
import TypeName.LONG;
import TypeName.SHORT;
import TypeName.VOID;
import com.google.common.truth.Truth;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
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
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]).unbox();
            Assert.fail("genericType_rv92834 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E[]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSimpleMethod() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv92835__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92835__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92835__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92835__4)).isPrimitive());
        TypeName o_genericType_rv92835__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92835__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92835__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92835__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv92835__15 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]).withoutAnnotations();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92835__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92835__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92835__15)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void getParameterType() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum92778__4 = TypeName.get(recursiveEnum.getReturnType());
            TypeName o_genericTypelitNum92778__6 = TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName o_genericTypelitNum92778__11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]).unbox();
            Assert.fail("genericTypelitNum92778_mg97317 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E[]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEnum() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericTypelitNum92778__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum92778__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum92778__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericTypelitNum92778__4)).isPrimitive());
        TypeName o_genericTypelitNum92778__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum92778__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum92778__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum92778__6)).isPrimitive());
        TypeName o_genericTypelitNum92778_add95100__12 = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (o_genericTypelitNum92778_add95100__12)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (o_genericTypelitNum92778_add95100__12)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericTypelitNum92778_add95100__12)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericTypelitNum92778__11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericTypelitNum92778__11)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericTypelitNum92778__11)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericTypelitNum92778__11)).isPrimitive());
        TypeName o_genericTypelitNum92778_add95100_mg108446__24 = o_genericTypelitNum92778_add95100__12.annotated(new AnnotationSpec[]{  });
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (o_genericTypelitNum92778_add95100_mg108446__24)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (o_genericTypelitNum92778_add95100_mg108446__24)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericTypelitNum92778_add95100_mg108446__24)).isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (o_genericTypelitNum92778_add95100__12)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (o_genericTypelitNum92778_add95100__12)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericTypelitNum92778_add95100__12)).isPrimitive());
    }

    @Test
    public void testProvider() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName.get(genericStringInner.getReturnType());
        Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        Truth.assertThat(TypeName.get(genericStringInner.getGenericReturnType()).toString()).isEqualTo(((TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    @Test(timeout = 10000)
    public void testFalse() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv131073__13 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv131073__13)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv131073__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv131073__13)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv131073__15 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv131073__15)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv131073__15)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv131073__15)).isPrimitive());
        String String_90 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_90);
        Assert.assertFalse(TypeName.get(genericStringInner.getReturnType()).equals(new Object()));
    }

    @Test(timeout = 10000)
    public void testBasic() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName o_innerGenericInGenericType_remove131062__7 = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName o_innerGenericInGenericType_remove131062__9 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            String String_53 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            TypeName.get(genericStringInner.getGenericReturnType()).unbox();
            Assert.fail("innerGenericInGenericType_remove131062_mg134129 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void suite() throws Exception {
        try {
            try {
                Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
                TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
                TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
                Assert.assertFalse(isBoxedPrimitive());
                Assert.assertFalse(isAnnotated());
                Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
                Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
                Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
                TypeName o_innerGenericInGenericType_rv131079_failAssert477litString132173_failAssert513litString138561__15 = TypeName.get(genericStringInner.getGenericReturnType());
                Assert.assertFalse(isBoxedPrimitive());
                Assert.assertFalse(isAnnotated());
                Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv131079_failAssert477litString132173_failAssert513litString138561__15)).toString());
                Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv131079_failAssert477litString132173_failAssert513litString138561__15)).hashCode())));
                Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv131079_failAssert477litString132173_failAssert513litString138561__15)).isPrimitive());
                TypeName.get(getClass().getDeclaredMethod("testGenVericInnerInt").getGenericReturnType());
                String String_88 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "tN+ZB50q]YIJ>/6LKEfhHK _P5NuXuZ:*`m+Z;,P;Oi@V,";
                TypeName.get(genericStringInner.getReturnType()).unbox();
                Assert.fail("innerGenericInGenericType_rv131079 should have thrown UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            Assert.fail("innerGenericInGenericType_rv131079_failAssert477litString132173 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void type() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_21117 = Collections.<AnnotationSpec>emptyList();
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName o_innerGenericInGenericType_rv131071__13 = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName o_innerGenericInGenericType_rv131071__15 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            String String_92 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            TypeName o_innerGenericInGenericType_rv131071__21 = TypeName.get(genericStringInner.getReturnType()).annotated(Collections.<AnnotationSpec>emptyList());
            TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()).isPrimitive();
            TypeName.get(genericStringInner.getReturnType()).annotated(Collections.<AnnotationSpec>emptyList()).unbox();
            Assert.fail("innerGenericInGenericType_rv131071_add133406_mg145077 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void returnType() throws Exception {
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            String String_131 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
            TypeName.get(staticInGeneric.getReturnType()).unbox();
            Assert.fail("innerStaticInGenericType_rv148380 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void run() throws Exception {
        TypeName __DSPOT_o_23725 = null;
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg148367__5 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg148367__5)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_mg148367__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg148367__5)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_132 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_132);
        boolean o_innerStaticInGenericType_mg148367_add149427__14 = typeName.equals(__DSPOT_o_23725);
        boolean o_innerStaticInGenericType_mg148367__12 = typeName.equals(__DSPOT_o_23725);
        TypeName o_innerStaticInGenericType_mg148367_add149427_mg155937__21 = o_innerStaticInGenericType_mg148367__5.annotated(new AnnotationSpec[]{  });
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg148367_add149427_mg155937__21)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_mg148367_add149427_mg155937__21)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg148367_add149427_mg155937__21)).isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg148367__5)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_mg148367__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg148367__5)).isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
    }

    @Test
    public void testConstructor() {
        assertEqualsHashCodeAndToString(BOOLEAN, BOOLEAN);
        assertEqualsHashCodeAndToString(BYTE, BYTE);
        assertEqualsHashCodeAndToString(CHAR, CHAR);
        assertEqualsHashCodeAndToString(DOUBLE, DOUBLE);
        assertEqualsHashCodeAndToString(FLOAT, FLOAT);
        assertEqualsHashCodeAndToString(INT, INT);
        assertEqualsHashCodeAndToString(LONG, LONG);
        assertEqualsHashCodeAndToString(SHORT, SHORT);
        assertEqualsHashCodeAndToString(VOID, VOID);
    }

    @Test(timeout = 10000)
    public void arrayType() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4_mg253_add6020__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg253_add6020__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg253_add6020__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg253_add6020__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__3 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add4__4 = TypeName.get(Object[].class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__5 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__5)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__5)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__5)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add4_mg253__16 = o_equalsAndHashCodeArrayTypeName_add4__4.box();
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg253__16)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg253__16)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg253__16)).isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void testBuilder() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("\n");
            ClassName.get(Object.class);
            Assert.fail("equalsAndHashCodeClassNamelitString13163 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for \n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unbox() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassName_add13166__1 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13166__2 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13166__3 = ClassName.get(Object.class);
            TypeName o_equalsAndHashCodeClassName_add13166__4 = TypeName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13166__5 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13166__6 = ClassName.bestGuess("java.lang.Object");
            ClassName o_equalsAndHashCodeClassName_add13166__7 = ClassName.get(Object.class);
            TypeName.get(Object.class).unbox();
            Assert.fail("equalsAndHashCodeClassName_add13166_mg14167 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox java.lang.Object", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void readObject() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassName_add13168__1 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13168__2 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13168__3 = ClassName.get(Object.class);
            TypeName o_equalsAndHashCodeClassName_add13168__4 = TypeName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13168__5 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13168__6 = ClassName.bestGuess("java.lang.bject");
            ClassName o_equalsAndHashCodeClassName_add13168__7 = ClassName.get(Object.class);
            Assert.fail("equalsAndHashCodeClassName_add13168litString13229 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for java.lang.bject", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getMetadataNames() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassName_add13165__1 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13165__2 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13165__3 = ClassName.get(Object.class);
            TypeName o_equalsAndHashCodeClassName_add13165__4 = TypeName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13165__5 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13165__6 = ClassName.bestGuess("");
            ClassName o_equalsAndHashCodeClassName_add13165__7 = ClassName.get(Object.class);
            String o_equalsAndHashCodeClassName_add13165_mg13921__22 = ClassName.bestGuess("").simpleName();
            Assert.fail("equalsAndHashCodeClassName_add13165_mg13921litString15748 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void onlyOnce() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassName_add13168__1 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13168__2 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13168__3 = ClassName.get(Object.class);
            TypeName o_equalsAndHashCodeClassName_add13168__4 = TypeName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13168__5 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13168__6 = ClassName.bestGuess("java.lang.Object");
            ClassName o_equalsAndHashCodeClassName_add13168__7 = ClassName.get(Object.class);
            ClassName.get(Object.class).enclosingClassName();
            TypeName.get(Object.class).unbox();
            Assert.fail("equalsAndHashCodeClassName_add13168_mg14038_mg30426 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox java.lang.Object", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void compute() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassName_add13167__1 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13167__2 = ClassName.get(Object.class);
            TypeName o_equalsAndHashCodeClassName_add13167__3 = TypeName.get(Object.class);
            TypeName o_equalsAndHashCodeClassName_add13167__4 = TypeName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13167__5 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13167__6 = ClassName.bestGuess(":");
            ClassName o_equalsAndHashCodeClassName_add13167__7 = ClassName.get(Object.class);
            ClassName o_equalsAndHashCodeClassName_add13167_mg14259__22 = ClassName.get(Object.class).topLevelClassName();
            Assert.fail("equalsAndHashCodeClassName_add13167_mg14259litString15494 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for :", expected.getMessage());
        }
    }

    @Test
    public void testEquals() {
        assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Object.class), ParameterizedTypeName.get(Object.class));
        assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Set.class, UUID.class), ParameterizedTypeName.get(Set.class, UUID.class));
        Assert.assertNotEquals(ClassName.get(List.class), ParameterizedTypeName.get(List.class, String.class));
    }

    @Test(timeout = 10000)
    public void getNumberValue() throws Exception {
        try {
            TypeName o_equalsAndHashCodeParameterizedTypeName_add37425__1 = ParameterizedTypeName.get(Object.class);
            TypeName o_equalsAndHashCodeParameterizedTypeName_add37425__2 = ParameterizedTypeName.get(Object.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37425__3 = ParameterizedTypeName.get(Set.class, UUID.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37425__4 = ParameterizedTypeName.get(Set.class, UUID.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37425__5 = ParameterizedTypeName.get(Set.class, UUID.class);
            ClassName o_equalsAndHashCodeParameterizedTypeName_add37425__6 = ClassName.get(List.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37425__7 = ParameterizedTypeName.get(List.class, String.class);
            ParameterizedTypeName.get(Object.class).unbox();
            Assert.fail("equalsAndHashCodeParameterizedTypeName_add37425_mg38088 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox java.lang.Object", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserialize() throws Exception {
        try {
            String __DSPOT_name_5696 = null;
            TypeName o_equalsAndHashCodeParameterizedTypeName_add37424__1 = ParameterizedTypeName.get(Object.class);
            TypeName o_equalsAndHashCodeParameterizedTypeName_add37424__2 = ParameterizedTypeName.get(Object.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37424__3 = ParameterizedTypeName.get(Set.class, UUID.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37424__4 = ParameterizedTypeName.get(Set.class, UUID.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37424__5 = ParameterizedTypeName.get(Set.class, UUID.class);
            ClassName o_equalsAndHashCodeParameterizedTypeName_add37424__6 = ClassName.get(List.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37424__7 = ParameterizedTypeName.get(List.class, String.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37424_mg38061__23 = ParameterizedTypeName.get(Set.class, UUID.class).nestedClass(null);
            Assert.fail("equalsAndHashCodeParameterizedTypeName_add37424_mg38061null59301 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("name == null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testForIssue() throws Exception {
        try {
            List<TypeName> __DSPOT_typeArguments_5601 = Collections.<TypeName>emptyList();
            String __DSPOT_name_5600 = "TQIf^egSY;@d6-]]nXQ>";
            TypeName o_equalsAndHashCodeParameterizedTypeName_add37427__1 = ParameterizedTypeName.get(Object.class);
            TypeName o_equalsAndHashCodeParameterizedTypeName_add37427__2 = ParameterizedTypeName.get(Object.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37427__3 = ParameterizedTypeName.get(Set.class, UUID.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37427__4 = ParameterizedTypeName.get(Set.class, UUID.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37427__5 = ParameterizedTypeName.get(List.class, String.class);
            ClassName o_equalsAndHashCodeParameterizedTypeName_add37427__6 = ClassName.get(List.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37427__7 = ParameterizedTypeName.get(List.class, String.class);
            ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add37427_mg37900__25 = ParameterizedTypeName.get(List.class, String.class).nestedClass(null, Collections.<TypeName>emptyList());
            Assert.fail("equalsAndHashCodeParameterizedTypeName_add37427_mg37900null59267 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("name == null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void typeVariable() throws Exception {
        try {
            TypeName o_equalsAndHashCodeTypeVariableNamenull59883_failAssert123_add61186__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull59883_failAssert123_add61186__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull59883_failAssert123_add61186__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull59883_failAssert123_add61186__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableNamenull59883_failAssert123_add61186__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamenull59883_failAssert123_add61186__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamenull59883_failAssert123_add61186__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamenull59883_failAssert123_add61186__4)).isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get(null, Comparator.class, Serializable.class);
            TypeVariableName.get("T", Comparator.class, Serializable.class);
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.fail("equalsAndHashCodeTypeVariableNamenull59883 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNode() throws Exception {
        try {
            TypeName o_equalsAndHashCodeTypeVariableName_add59871__1 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add59871__1)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add59871__1)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add59871__1)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_add59871__2 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add59871__2)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add59871__2)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add59871__2)).isPrimitive());
            TypeVariableName o_equalsAndHashCodeTypeVariableName_add59871__3 = TypeVariableName.get(null, Comparator.class, Serializable.class);
            TypeVariableName typeVar1 = TypeVariableName.get(null, Comparator.class, Serializable.class);
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.fail("equalsAndHashCodeTypeVariableName_add59871null61834 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void isPrimitive() throws Exception {
        Truth.assertThat(INT.isPrimitive()).isTrue();
        Truth.assertThat(ClassName.get("java.lang", "Integer").isPrimitive()).isFalse();
        Truth.assertThat(ClassName.get("java.lang", "String").isPrimitive()).isFalse();
        Truth.assertThat(VOID.isPrimitive()).isFalse();
        Truth.assertThat(ClassName.get("java.lang", "Void").isPrimitive()).isFalse();
    }

    @Test(timeout = 10000)
    public void testExplicit() throws Exception {
        ClassName o_isPrimitive_add163662__1 = ClassName.get("java.lang", "Void");
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Void", ((ClassName) (o_isPrimitive_add163662__1)).toString());
        Assert.assertEquals(399092968, ((int) (((ClassName) (o_isPrimitive_add163662__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isPrimitive_add163662__1)).isPrimitive());
        ClassName o_isPrimitive_add163662_mg163945__5 = o_isPrimitive_add163662__1.peerClass(">Q:q*g_A/zD0Ax0MMGco");
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.>Q:q*g_A/zD0Ax0MMGco", ((ClassName) (o_isPrimitive_add163662_mg163945__5)).toString());
        Assert.assertEquals((-323810226), ((int) (((ClassName) (o_isPrimitive_add163662_mg163945__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isPrimitive_add163662_mg163945__5)).isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Void", ((ClassName) (o_isPrimitive_add163662__1)).toString());
        Assert.assertEquals(399092968, ((int) (((ClassName) (o_isPrimitive_add163662__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isPrimitive_add163662__1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void loadClass() throws Exception {
        ClassName o_isPrimitive_add163662__1 = ClassName.get("java.lang", "Void");
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Void", ((ClassName) (o_isPrimitive_add163662__1)).toString());
        Assert.assertEquals(399092968, ((int) (((ClassName) (o_isPrimitive_add163662__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isPrimitive_add163662__1)).isPrimitive());
        Assert.assertEquals("java.lang.Void", o_isPrimitive_add163662__1.reflectionName());
    }

    @Test(timeout = 10000)
    public void testMisc() throws Exception {
        ClassName o_isPrimitive_add163659__1 = ClassName.get("", "String");
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("String", ((ClassName) (o_isPrimitive_add163659__1)).toString());
        Assert.assertEquals((-1808118735), ((int) (((ClassName) (o_isPrimitive_add163659__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isPrimitive_add163659__1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void testAnnotated() throws Exception {
        ClassName o_isPrimitive_add163662__1 = ClassName.get("java.lang", "\n");
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.\n", ((ClassName) (o_isPrimitive_add163662__1)).toString());
        Assert.assertEquals(697795926, ((int) (((ClassName) (o_isPrimitive_add163662__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isPrimitive_add163662__1)).isPrimitive());
        Assert.assertEquals("\n", o_isPrimitive_add163662__1.simpleName());
    }

    @Test(timeout = 10000)
    public void toString() throws Exception {
        String __DSPOT_name_25648 = ">Q:q*g_A/zD0Ax0MMGco";
        ClassName o_isPrimitive_add163662__1 = ClassName.get("java.lang", "Void");
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Void", ((ClassName) (o_isPrimitive_add163662__1)).toString());
        Assert.assertEquals(399092968, ((int) (((ClassName) (o_isPrimitive_add163662__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isPrimitive_add163662__1)).isPrimitive());
        ClassName o_isPrimitive_add163662_mg163945__5 = o_isPrimitive_add163662__1.peerClass(null);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.null", ((ClassName) (o_isPrimitive_add163662_mg163945__5)).toString());
        Assert.assertEquals(399813819, ((int) (((ClassName) (o_isPrimitive_add163662_mg163945__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isPrimitive_add163662_mg163945__5)).isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Void", ((ClassName) (o_isPrimitive_add163662__1)).toString());
        Assert.assertEquals(399092968, ((int) (((ClassName) (o_isPrimitive_add163662__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isPrimitive_add163662__1)).isPrimitive());
    }

    @Test
    public void testValueOfLjavaLangString() throws Exception {
        Truth.assertThat(INT.isBoxedPrimitive()).isFalse();
        Truth.assertThat(isBoxedPrimitive()).isTrue();
        Truth.assertThat(isBoxedPrimitive()).isFalse();
        Truth.assertThat(VOID.isBoxedPrimitive()).isFalse();
        Truth.assertThat(isBoxedPrimitive()).isFalse();
    }

    @Test(timeout = 10000)
    public void testIt() throws Exception {
        ClassName o_isBoxedPrimitive_add159291__1 = ClassName.get("", "String");
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("String", ((ClassName) (o_isBoxedPrimitive_add159291__1)).toString());
        Assert.assertEquals((-1808118735), ((int) (((ClassName) (o_isBoxedPrimitive_add159291__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_isBoxedPrimitive_add159291__1)).isPrimitive());
        o_isBoxedPrimitive_add159291__1.enclosingClassName();
    }

    private void assertEqualsHashCodeAndToString(TypeName a, TypeName b) {
        Assert.assertEquals(a.toString(), b.toString());
        Truth.assertThat(a.equals(b)).isTrue();
        Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        Assert.assertFalse(a.equals(null));
    }
}

