package com.squareup.javapoet;


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
    public void genericType_rv90697() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv90697__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv90697__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90697__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90697__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90697__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90697__4)).isPrimitive());
        TypeName o_genericType_rv90697__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90697__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90697__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv90697__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv90697__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90697__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv90697__15 = __DSPOT_invoc_11.box();
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90697__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90697__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv90697__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv90697__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90697__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv90697__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90697__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90697__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90697__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90697__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90697__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90697__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv90697__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv90697__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90697__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv90692_failAssert31() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            __DSPOT_invoc_6.unbox();
            org.junit.Assert.fail("genericType_rv90692 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericType_rv90704_failAssert32() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            __DSPOT_invoc_11.unbox();
            org.junit.Assert.fail("genericType_rv90704 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E[]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericType_rv90693() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv90693__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv90693__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90693__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90693__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90693__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90693__4)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv90693__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90693__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90693__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv90693__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv90693__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90693__13)).isPrimitive());
        TypeName o_genericType_rv90693__15 = __DSPOT_invoc_6.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90693__15)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90693__15)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv90693__15)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv90693__15)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90693__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv90693__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90693__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90693__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90693__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90693__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90693__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90693__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv90693__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv90693__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90693__13)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv90695() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_14010 = new AnnotationSpec[]{  };
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv90695__5 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv90695__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90695__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90695__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90695__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90695__5)).isPrimitive());
        TypeName o_genericType_rv90695__7 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90695__7)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90695__7)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv90695__7)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv90695__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90695__7)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv90695__16 = __DSPOT_invoc_11.annotated(__DSPOT_annotations_14010);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90695__16)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90695__16)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv90695__16)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv90695__16)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90695__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv90695__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90695__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90695__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90695__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90695__5)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90695__7)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90695__7)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv90695__7)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv90695__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90695__7)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv90684() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_14008 = Collections.<AnnotationSpec>emptyList();
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv90684__6 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv90684__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90684__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90684__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90684__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90684__6)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv90684__15 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90684__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90684__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv90684__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv90684__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90684__15)).isPrimitive());
        TypeName o_genericType_rv90684__17 = __DSPOT_invoc_6.annotated(__DSPOT_annotations_14008);
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90684__17)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90684__17)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv90684__17)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv90684__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90684__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv90684__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90684__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90684__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90684__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90684__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90684__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90684__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv90684__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv90684__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90684__15)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv90705() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv90705__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv90705__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90705__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90705__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90705__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90705__4)).isPrimitive());
        TypeName o_genericType_rv90705__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90705__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90705__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv90705__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv90705__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90705__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv90705__15 = __DSPOT_invoc_11.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90705__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90705__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv90705__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv90705__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv90705__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv90705__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv90705__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv90705__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv90705__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv90705__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90705__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90705__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv90705__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv90705__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv90705__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericTypelitNum90641_failAssert26litNum91182() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum90641_failAssert26litNum91182__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum90641_failAssert26litNum91182__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum90641_failAssert26litNum91182__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum90641_failAssert26litNum91182__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum90641_failAssert26litNum91182__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericTypelitNum90641_failAssert26litNum91182__6)).isPrimitive());
            TypeName o_genericTypelitNum90641_failAssert26litNum91182__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum90641_failAssert26litNum91182__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum90641_failAssert26litNum91182__8)).isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum90641_failAssert26litNum91182__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum90641_failAssert26litNum91182__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum90641_failAssert26litNum91182__8)).isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[1]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[Integer.MAX_VALUE]);
            org.junit.Assert.fail("genericTypelitNum90641 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void genericTypelitNum90642_failAssert27_add93657() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum90642_failAssert27_add93657__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum90642_failAssert27_add93657__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum90642_failAssert27_add93657__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum90642_failAssert27_add93657__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum90642_failAssert27_add93657__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericTypelitNum90642_failAssert27_add93657__6)).isPrimitive());
            TypeName o_genericTypelitNum90642_failAssert27_add93657__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum90642_failAssert27_add93657__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum90642_failAssert27_add93657__8)).isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum90642_failAssert27_add93657__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum90642_failAssert27_add93657__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum90642_failAssert27_add93657__8)).isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
            Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
            TypeName.get(recursiveEnum.getGenericParameterTypes()[Integer.MIN_VALUE]);
            org.junit.Assert.fail("genericTypelitNum90642 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
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
    public void innerClassInGenericType_rv113413() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_17256 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113413__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113413__14 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).isPrimitive());
        String String_45 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_45);
        TypeName o_innerClassInGenericType_rv113413__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_17256);
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113413__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113413__20)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv113413__20)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv113413__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113413__20)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__12)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113413__14)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_45);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv113423() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113423__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113423__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).isPrimitive());
        String String_55 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
        TypeName o_innerClassInGenericType_rv113423__19 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113423__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113423__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv113423__19)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv113423__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113423__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113423__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg113402() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_17254 = Collections.<AnnotationSpec>emptyList();
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg113402__6 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113402__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113402__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113402__6)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113402__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113402__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113402__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113402__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).isPrimitive());
        String String_35 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_35);
        TypeName o_innerClassInGenericType_mg113402__19 = genericTypeName.annotated(__DSPOT_annotations_17254);
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__19)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__19)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__19)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__19)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113402__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113402__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113402__6)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113402__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113402__6)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113402__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_35);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv113422_failAssert111() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            String String_54 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerClassInGenericType_rv113422 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg113411() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg113411__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113411__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113411__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113411__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113411__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113411__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113411__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113411__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).isPrimitive());
        String String_44 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_44);
        TypeName o_innerClassInGenericType_mg113411__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__17)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__17)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__17)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113411__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113411__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113411__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113411__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113411__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113411__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_44);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg113406() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg113406__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113406__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113406__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113406__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113406__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113406__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113406__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113406__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).isPrimitive());
        String String_39 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_39);
        genericTypeName.isAnnotated();
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113406__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113406__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113406__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113406__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113406__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113406__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_39);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg113403() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg113403__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113403__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113403__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113403__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113403__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113403__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113403__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113403__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).isPrimitive());
        String String_36 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_36);
        TypeName o_innerClassInGenericType_mg113403__17 = genericTypeName.box();
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__17)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__17)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__17)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113403__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113403__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113403__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113403__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113403__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113403__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_36);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString113384_failAssert108_add116392() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString113384_failAssert108_add116392__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerClassInGenericTypelitString113384_failAssert108_add116392__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113384_failAssert108_add116392__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("\n").getGenericReturnType());
            String String_17 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            org.junit.Assert.fail("innerClassInGenericTypelitString113384 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString113382_failAssert106litString113623_rv121545() throws Exception {
        try {
            AnnotationSpec[] __DSPOT_annotations_18225 = new AnnotationSpec[]{  };
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString113382_failAssert106litString113623__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerClassInGenericTypelitString113382_failAssert106litString113623__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113382_failAssert106litString113623__11)).isPrimitive());
            TypeName __DSPOT_invoc_62 = TypeName.get(getClass().getDeclaredMethod("=9BlP]!HY*9%K}(Csru").getGenericReturnType());
            String String_15 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "\n";
            org.junit.Assert.fail("innerClassInGenericTypelitString113382 should have thrown NoSuchMethodException");
            __DSPOT_invoc_62.annotated(__DSPOT_annotations_18225);
        } catch (NoSuchMethodException expected) {
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
    public void innerGenericInGenericType_rv125536_failAssert129() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            String String_107 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerGenericInGenericType_rv125536 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv125534() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv125534__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv125534__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).isPrimitive());
        String String_105 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_105);
        __DSPOT_invoc_4.isPrimitive();
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125534__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_105);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg125515() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_18754 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg125515__5 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125515__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125515__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg125515__5)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg125515__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125515__5)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg125515__10 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg125515__12 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).isPrimitive());
        String String_87 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
        TypeName o_innerGenericInGenericType_mg125515__18 = genericTypeName.annotated(__DSPOT_annotations_18754);
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__18)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__18)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__18)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__18)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__18)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125515__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125515__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg125515__5)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg125515__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125515__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__10)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125515__12)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg125525() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg125525__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125525__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125525__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg125525__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg125525__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125525__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg125525__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg125525__11 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).isPrimitive());
        String String_97 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
        TypeName o_innerGenericInGenericType_mg125525__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__17)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__17)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__17)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125525__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125525__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg125525__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg125525__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg125525__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg125525__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv125527() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_18757 = new AnnotationSpec[0];
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv125527__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv125527__14 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).isPrimitive());
        String String_98 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_98);
        TypeName o_innerGenericInGenericType_rv125527__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_18757);
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv125527__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv125527__20)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv125527__20)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv125527__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv125527__20)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__12)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125527__14)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_98);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv125537() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv125537__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv125537__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).isPrimitive());
        String String_108 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_108);
        TypeName o_innerGenericInGenericType_rv125537__19 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv125537__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv125537__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv125537__19)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv125537__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv125537__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125537__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_108);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv125529() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv125529__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv125529__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).isPrimitive());
        String String_100 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_100);
        TypeName o_innerGenericInGenericType_rv125529__19 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv125529__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv125529__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv125529__19)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv125529__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv125529__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv125529__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_100);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString125497_failAssert125_add126349() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString125497_failAssert125_add126349__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__6)).toString());
            Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerGenericInGenericTypelitString125497_failAssert125_add126349__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__11)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125497_failAssert125_add126349__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_69 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            org.junit.Assert.fail("innerGenericInGenericTypelitString125497 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString125499_failAssert127litString125781_mg132433() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_19402 = Collections.<AnnotationSpec>emptyList();
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString125499_failAssert127litString125781__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__6)).toString());
            Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerGenericInGenericTypelitString125499_failAssert127litString125781__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__11)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString125499_failAssert127litString125781__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod(":").getGenericReturnType());
            String String_71 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + "s66o[}J@%kTBuR_i@rYa@s!&[|UkBBu9ZVydDAIj/,]p)g";
            org.junit.Assert.fail("innerGenericInGenericTypelitString125499 should have thrown NoSuchMethodException");
            o_innerGenericInGenericTypelitString125499_failAssert127litString125781__6.annotated(__DSPOT_annotations_19402);
        } catch (NoSuchMethodException expected) {
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
    public void innerStaticInGenericType_mg138332() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg138332__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138332__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138332__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg138332__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg138332__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138332__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_136 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_136);
        TypeName o_innerStaticInGenericType_mg138332__11 = typeName.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138332__11)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138332__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg138332__11)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg138332__11)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138332__11)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138332__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138332__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg138332__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg138332__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138332__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_136);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv138334() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_20383 = new AnnotationSpec[]{  };
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_137 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_137);
        TypeName o_innerStaticInGenericType_rv138334__14 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_20383);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv138334__14)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv138334__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv138334__14)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_rv138334__14)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv138334__14)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_137);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg138331_failAssert137() throws Exception {
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            String String_135 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
            typeName.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_mg138331 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv138336() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_139 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_139);
        TypeName o_innerStaticInGenericType_rv138336__13 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv138336__13)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv138336__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv138336__13)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_rv138336__13)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv138336__13)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_139);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg138326() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg138326__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138326__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138326__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg138326__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg138326__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138326__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_130 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_130);
        typeName.hashCode();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138326__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138326__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg138326__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg138326__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg138326__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_130);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add138315_mg140300_failAssert140() throws Exception {
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            TypeName o_innerStaticInGenericType_add138315__4 = TypeName.get(staticInGeneric.getReturnType());
            TypeName o_innerStaticInGenericType_add138315__6 = TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            String String_119 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
            o_innerStaticInGenericType_add138315__4.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_add138315_mg140300 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add138315_mg140535() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add138315__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add138315__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add138315_mg140535__17 = o_innerStaticInGenericType_add138315__6.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315_mg140535__17)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315_mg140535__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315_mg140535__17)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315_mg140535__17)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315_mg140535__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add138315litString138738() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add138315__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add138315__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".Nest9edNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Nest9edNonGeneric", String_119);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add138315_mg140542() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_20613 = new AnnotationSpec[]{  };
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add138315__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add138315__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add138315_mg140542__18 = typeName.annotated(__DSPOT_annotations_20613);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315_mg140542__18)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315_mg140542__18)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315_mg140542__18)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315_mg140542__18)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315_mg140542__18)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add138315_mg140350() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add138315__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add138315__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add138315_mg140350__17 = o_innerStaticInGenericType_add138315__6.box();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315_mg140350__17)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315_mg140350__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315_mg140350__17)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315_mg140350__17)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315_mg140350__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add138315__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add138315__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add138315__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
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
    public void equalsAndHashCodeArrayTypeName_add4() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add4__3 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add3_mg188() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_10 = Collections.<AnnotationSpec>emptyList();
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add3__3 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add3_mg188__15 = o_equalsAndHashCodeArrayTypeName_add3__3.annotated(__DSPOT_annotations_10);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg188__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg188__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg188__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg188__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg188__15)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add3_mg185() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_8 = new AnnotationSpec[]{  };
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add3__3 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add3_mg185__14 = o_equalsAndHashCodeArrayTypeName_add3__3.annotated(__DSPOT_annotations_8);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg185__14)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg185__14)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg185__14)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg185__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg185__14)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add3_add159() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add3__3 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add3_mg178() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add3__3 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add3_mg178__13 = o_equalsAndHashCodeArrayTypeName_add3__1.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg178__13)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg178__13)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg178__13)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg178__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg178__13)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg201_mg6862() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_18 = Collections.<AnnotationSpec>emptyList();
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2__3 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg201__15 = o_equalsAndHashCodeArrayTypeName_add2__3.annotated(__DSPOT_annotations_18);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6862__18 = o_equalsAndHashCodeArrayTypeName_add2__4.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6862__18)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6862__18)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6862__18)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6862__18)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6862__18)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg201_mg6920() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_658 = new AnnotationSpec[0];
        List<AnnotationSpec> __DSPOT_annotations_18 = Collections.<AnnotationSpec>emptyList();
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2__3 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg201__15 = o_equalsAndHashCodeArrayTypeName_add2__3.annotated(__DSPOT_annotations_18);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6920__19 = o_equalsAndHashCodeArrayTypeName_add2_mg201__15.annotated(__DSPOT_annotations_658);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6920__19)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6920__19)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6920__19)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6920__19)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6920__19)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg201_mg6231() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_595 = Collections.<AnnotationSpec>emptyList();
        List<AnnotationSpec> __DSPOT_annotations_18 = Collections.<AnnotationSpec>emptyList();
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2__3 = TypeName.get(Object[].class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg201__15 = o_equalsAndHashCodeArrayTypeName_add2__3.annotated(__DSPOT_annotations_18);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6231__20 = o_equalsAndHashCodeArrayTypeName_add2__2.annotated(__DSPOT_annotations_595);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6231__20)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6231__20)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6231__20)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6231__20)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201_mg6231__20)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg201__15)).isPrimitive());
    }

    @Test
    public void equalsAndHashCodeClassName() {
        assertEqualsHashCodeAndToString(ClassName.get(Object.class), ClassName.get(Object.class));
        assertEqualsHashCodeAndToString(TypeName.get(Object.class), ClassName.get(Object.class));
        assertEqualsHashCodeAndToString(ClassName.bestGuess("java.lang.Object"), ClassName.get(Object.class));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12429_failAssert5() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess(":");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12429 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassName_add12433() throws Exception {
        ClassName o_equalsAndHashCodeClassName_add12433__1 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).isPrimitive());
        ClassName o_equalsAndHashCodeClassName_add12433__2 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).isPrimitive());
        TypeName o_equalsAndHashCodeClassName_add12433__3 = TypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).isPrimitive());
        ClassName o_equalsAndHashCodeClassName_add12433__4 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).isPrimitive());
        ClassName o_equalsAndHashCodeClassName_add12433__5 = ClassName.bestGuess("java.lang.Object");
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).isPrimitive());
        ClassName o_equalsAndHashCodeClassName_add12433__6 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__6)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName_add12433__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12423_failAssert1() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("java.langkObject");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12423 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for java.langkObject", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12427_failAssert3() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12427 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__4)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__5 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__5)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__6 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_rv12772__6)).isPrimitive());
            ClassName.bestGuess("");
            ClassName __DSPOT_invoc_8 = ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12427 should have thrown IllegalArgumentException");
            __DSPOT_invoc_8.simpleNames();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__4)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__5 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__5)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__6 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12429_failAssert5_rv12848__6)).isPrimitive());
            ClassName.bestGuess(":");
            ClassName __DSPOT_invoc_8 = ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12429 should have thrown IllegalArgumentException");
            __DSPOT_invoc_8.isAnnotated();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12427_failAssert3_add12521_mg27082() throws Exception {
        try {
            AnnotationSpec[] __DSPOT_annotations_3917 = new AnnotationSpec[]{  };
            ClassName o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__4)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__5 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__7)).isPrimitive());
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12427 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12427_failAssert3_add12521__6.annotated(__DSPOT_annotations_3917);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12426_failAssert2_add12530_mg32262() throws Exception {
        try {
            AnnotationSpec[] __DSPOT_annotations_4956 = new AnnotationSpec[]{  };
            ClassName o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__4)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__5 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__7)).isPrimitive());
            ClassName.bestGuess("15u&sdcOgKS{qxxj");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12426 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12426_failAssert2_add12530__6.annotated(__DSPOT_annotations_4956);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void equalsAndHashCodeParameterizedTypeName() {
        assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Object.class), ParameterizedTypeName.get(Object.class));
        assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Set.class, UUID.class), ParameterizedTypeName.get(Set.class, UUID.class));
        Assert.assertNotEquals(ClassName.get(List.class), ParameterizedTypeName.get(List.class, String.class));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34637() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34637__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34637__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34637__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34637__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34637__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34637__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34637__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34637__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34632_add35101() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34632__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34632__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34632__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34632__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34632__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34632__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34632_mg35288() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_5481 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34632__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34632__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34632__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34632__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34632__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34632__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34632_mg35288__21 = o_equalsAndHashCodeParameterizedTypeName_add34632__6.annotated(__DSPOT_annotations_5481);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632_mg35288__21)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632_mg35288__21)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632_mg35288__21)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632_mg35288__21)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632_mg35288__21)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34632__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34632__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34633_mg35107() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_5394 = new AnnotationSpec[]{  };
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34633__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34633__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34633__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34633__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34633__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34633__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34633_mg35107__20 = o_equalsAndHashCodeParameterizedTypeName_add34633__2.annotated(__DSPOT_annotations_5394);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633_mg35107__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633_mg35107__20)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633_mg35107__20)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633_mg35107__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633_mg35107__20)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34633__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34633__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34634_mg35273_mg39388() throws Exception {
        String __DSPOT_name_6118 = "h}]eYD?%i[Nbcdfp$b/f";
        List<TypeName> __DSPOT_typeArguments_5475 = Collections.<TypeName>emptyList();
        String __DSPOT_name_5474 = "OU&@SrllYOdSdvnM([UK";
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34634__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34634__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34634__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34634__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34634__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34634__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22 = o_equalsAndHashCodeParameterizedTypeName_add34634__6.nestedClass(__DSPOT_name_5474, __DSPOT_typeArguments_5475);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>.OU&@SrllYOdSdvnM([UK", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).toString());
        Assert.assertEquals(46687153, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273_mg39388__26 = o_equalsAndHashCodeParameterizedTypeName_add34634__5.peerClass(__DSPOT_name_6118);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273_mg39388__26)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273_mg39388__26)).isBoxedPrimitive());
        Assert.assertEquals("java.util.h}]eYD?%i[Nbcdfp$b/f", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273_mg39388__26)).toString());
        Assert.assertEquals(-211178284, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273_mg39388__26)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273_mg39388__26)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34634__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634__6)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>.OU&@SrllYOdSdvnM([UK", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).toString());
        Assert.assertEquals(46687153, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34634_mg35273__22)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34636_mg35136_mg46556() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_7464 = Collections.<AnnotationSpec>emptyList();
        List<AnnotationSpec> __DSPOT_annotations_5408 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34636__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34636__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34636__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34636__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34636__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34636__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21 = o_equalsAndHashCodeParameterizedTypeName_add34636__4.annotated(__DSPOT_annotations_5408);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136_mg46556__26 = o_equalsAndHashCodeParameterizedTypeName_add34636__5.annotated(__DSPOT_annotations_7464);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136_mg46556__26)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136_mg46556__26)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136_mg46556__26)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136_mg46556__26)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136_mg46556__26)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34636__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636__6)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34636_mg35136__21)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg39631() throws Exception {
        String __DSPOT_name_6187 = "w<d?I?faF%WDz<pTLZx%";
        List<TypeName> __DSPOT_typeArguments_5370 = Collections.<TypeName>emptyList();
        String __DSPOT_name_5369 = "_#?5W!Yt.6Vm<&7-)?ke";
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34635__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34635__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34635__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34635__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34635__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34635__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22 = o_equalsAndHashCodeParameterizedTypeName_add34635__3.nestedClass(__DSPOT_name_5369, __DSPOT_typeArguments_5370);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>._#?5W!Yt.6Vm<&7-)?ke", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).toString());
        Assert.assertEquals(-1583861133, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg39631__26 = o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22.nestedClass(__DSPOT_name_6187);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg39631__26)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg39631__26)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>._#?5W!Yt.6Vm<&7-)?ke.w<d?I?faF%WDz<pTLZx%", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg39631__26)).toString());
        Assert.assertEquals(-1612421383, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg39631__26)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg39631__26)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>._#?5W!Yt.6Vm<&7-)?ke", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).toString());
        Assert.assertEquals(-1583861133, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg38243() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_5619 = new AnnotationSpec[]{  };
        List<TypeName> __DSPOT_typeArguments_5370 = Collections.<TypeName>emptyList();
        String __DSPOT_name_5369 = "_#?5W!Yt.6Vm<&7-)?ke";
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34635__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34635__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34635__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34635__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34635__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34635__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22 = o_equalsAndHashCodeParameterizedTypeName_add34635__3.nestedClass(__DSPOT_name_5369, __DSPOT_typeArguments_5370);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>._#?5W!Yt.6Vm<&7-)?ke", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).toString());
        Assert.assertEquals(-1583861133, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg38243__26 = o_equalsAndHashCodeParameterizedTypeName_add34635__1.annotated(__DSPOT_annotations_5619);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg38243__26)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg38243__26)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg38243__26)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg38243__26)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979_mg38243__26)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34635__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635__6)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>._#?5W!Yt.6Vm<&7-)?ke", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).toString());
        Assert.assertEquals(-1583861133, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34635_mg34979__22)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg54658() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableName_mg54658__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54658__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isPrimitive());
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
        TypeName o_equalsAndHashCodeTypeVariableName_mg54658__7 = typeVar2.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamelitString54643() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableNamelitString54643__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableNamelitString54643__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("\n", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString54643__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54651() throws Exception {
        Type[] __DSPOT_bounds_9106 = new Type[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg54651__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54651__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54651__8 = typeVar1.withBounds(__DSPOT_bounds_9106);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54650() throws Exception {
        TypeName[] __DSPOT_bounds_9105 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg54650__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54650__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54650__8 = typeVar1.withBounds(__DSPOT_bounds_9105);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54650__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54650__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54650__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54650__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54650__8)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54650__3)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54649() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9104 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg54649__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54649__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54649__9 = typeVar1.annotated(__DSPOT_annotations_9104);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54649litString54742() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9104 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg54649__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54649__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54649__9 = typeVar1.annotated(__DSPOT_annotations_9104);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54651_mg56601() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9437 = Collections.<AnnotationSpec>emptyList();
        Type[] __DSPOT_bounds_9106 = new Type[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg54651__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54651__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54651__8 = typeVar1.withBounds(__DSPOT_bounds_9106);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54651_mg56601__17 = o_equalsAndHashCodeTypeVariableName_mg54651__8.annotated(__DSPOT_annotations_9437);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56601__17)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56601__17)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56601__17)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56601__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56601__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54651_mg56334() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_9298 = new AnnotationSpec[]{  };
        Type[] __DSPOT_bounds_9106 = new Type[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg54651__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54651__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54651__8 = typeVar1.withBounds(__DSPOT_bounds_9106);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54651_mg56334__16 = o_equalsAndHashCodeTypeVariableName_mg54651__3.annotated(__DSPOT_annotations_9298);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56334__16)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56334__16)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56334__16)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56334__16)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56334__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54652_mg56436_failAssert10() throws Exception {
        try {
            List<? extends TypeName> __DSPOT_bounds_9107 = Collections.emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg54652__3 = TypeVariableName.get(Object.class);
            TypeName o_equalsAndHashCodeTypeVariableName_mg54652__4 = TypeVariableName.get(Object.class);
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54652__9 = typeVar1.withBounds(__DSPOT_bounds_9107);
            o_equalsAndHashCodeTypeVariableName_mg54652__4.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg54652_mg56436 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox java.lang.Object", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54649_mg56288() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9104 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg54649__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54649__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54649__9 = typeVar1.annotated(__DSPOT_annotations_9104);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54649_mg56288__16 = o_equalsAndHashCodeTypeVariableName_mg54649__4.box();
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649_mg56288__16)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649_mg56288__16)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649_mg56288__16)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649_mg56288__16)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649_mg56288__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54651_mg56593() throws Exception {
        List<? extends TypeName> __DSPOT_bounds_9433 = Collections.emptyList();
        Type[] __DSPOT_bounds_9106 = new Type[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg54651__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54651__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54651__8 = typeVar1.withBounds(__DSPOT_bounds_9106);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54651_mg56593__17 = typeVar2.withBounds(__DSPOT_bounds_9433);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56593__17)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56593__17)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56593__17)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56593__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651_mg56593__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54651__3)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54651__8)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54658_mg56501() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9378 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg54658__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54658__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isPrimitive());
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
        TypeName o_equalsAndHashCodeTypeVariableName_mg54658__7 = typeVar2.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54658_mg56501__16 = o_equalsAndHashCodeTypeVariableName_mg54658__7.annotated(__DSPOT_annotations_9378);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658_mg56501__16)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658_mg56501__16)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658_mg56501__16)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658_mg56501__16)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658_mg56501__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54658__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54658__7)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54655_mg56654() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9462 = Collections.<AnnotationSpec>emptyList();
        TypeName[] __DSPOT_bounds_9109 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg54655__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg54655__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54655__8 = typeVar2.withBounds(__DSPOT_bounds_9109);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54655_mg56654__17 = o_equalsAndHashCodeTypeVariableName_mg54655__8.annotated(__DSPOT_annotations_9462);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655_mg56654__17)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655_mg56654__17)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655_mg56654__17)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655_mg56654__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655_mg56654__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54655__3)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54655__8)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54657_mg56228_failAssert8litString60578() throws Exception {
        try {
            List<? extends TypeName> __DSPOT_bounds_9111 = Collections.emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg54657__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg54657__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
            TypeVariableName typeVar2 = TypeVariableName.get("\n", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
            Assert.assertEquals("\n", ((TypeVariableName) (typeVar2)).toString());
            Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54657__9 = typeVar2.withBounds(__DSPOT_bounds_9111);
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).isBoxedPrimitive());
            Assert.assertEquals("\n", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).toString());
            Assert.assertEquals(10, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg54657__3.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg54657_mg56228 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54657_mg56228_failAssert8_mg61333() throws Exception {
        try {
            List<? extends TypeName> __DSPOT_bounds_9111 = Collections.emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg54657__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg54657__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54657__4)).isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54657__9 = typeVar2.withBounds(__DSPOT_bounds_9111);
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54657__9)).isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg54657__3.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg54657_mg56228 should have thrown UnsupportedOperationException");
            o_equalsAndHashCodeTypeVariableName_mg54657__4.isPrimitive();
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg54649_mg56559_failAssert6_mg57521() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_9104 = Collections.<AnnotationSpec>emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg54649__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg54649__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg54649__4)).isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg54649__9 = typeVar1.annotated(__DSPOT_annotations_9104);
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg54649__9)).isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg54649__4.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg54649_mg56559 should have thrown UnsupportedOperationException");
            o_equalsAndHashCodeTypeVariableName_mg54649__4.box();
        } catch (UnsupportedOperationException expected) {
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
    public void equalsAndHashCodeWildcardTypeName_add76530() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add76531_mg76945() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_12941 = Collections.<AnnotationSpec>emptyList();
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76531__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76531__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76531__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76531__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76531__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76531__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76531_mg76945__21 = o_equalsAndHashCodeWildcardTypeName_add76531__4.annotated(__DSPOT_annotations_12941);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531_mg76945__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531_mg76945__21)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531_mg76945__21)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531_mg76945__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531_mg76945__21)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__5)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76531__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add76530_add76625() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76530__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76530__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add76528_mg76934_mg79864() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_12936 = Collections.<AnnotationSpec>emptyList();
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76528__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76528__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76528__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76528__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76528__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76528__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21 = o_equalsAndHashCodeWildcardTypeName_add76528__2.annotated(__DSPOT_annotations_12936);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).isPrimitive());
        TypeName o_equalsAndHashCodeWildcardTypeName_add76528_mg76934_mg79864__24 = o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21.withoutAnnotations();
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934_mg79864__24)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934_mg79864__24)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934_mg79864__24)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934_mg79864__24)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934_mg79864__24)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__5)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76528_mg76934__21)).isPrimitive());
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

