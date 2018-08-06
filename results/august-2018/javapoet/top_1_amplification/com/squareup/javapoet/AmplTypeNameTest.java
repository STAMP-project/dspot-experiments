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


public class AmplTypeNameTest {
    protected <E extends Enum<E>> E generic(E[] values) {
        return values[0];
    }

    protected static class TestGeneric<T> {
        class Inner {}

        class InnerGeneric<T2> {}

        static class NestedNonGeneric {}
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
    public void genericType_rv93303() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93303__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv93303__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93303__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93303__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93303__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93303__4)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv93303__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93303__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93303__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93303__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93303__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93303__13)).isPrimitive());
        int o_genericType_rv93303__15 = __DSPOT_invoc_6.hashCode();
        Assert.assertEquals(69, ((int) (o_genericType_rv93303__15)));
        Assert.assertFalse(((ClassName) (o_genericType_rv93303__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93303__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93303__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93303__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93303__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93303__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93303__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93303__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93303__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93303__13)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv93313() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93313__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv93313__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93313__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93313__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93313__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93313__4)).isPrimitive());
        TypeName o_genericType_rv93313__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93313__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93313__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93313__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93313__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93313__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv93313__15 = __DSPOT_invoc_11.box();
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93313__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93313__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93313__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93313__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93313__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv93313__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93313__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93313__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93313__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93313__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93313__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93313__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93313__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93313__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93313__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv93320_failAssert31() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            __DSPOT_invoc_11.unbox();
            org.junit.Assert.fail("genericType_rv93320 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E[]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericType_rv93311() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_14379 = new AnnotationSpec[]{  };
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93311__5 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv93311__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93311__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93311__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93311__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93311__5)).isPrimitive());
        TypeName o_genericType_rv93311__7 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93311__7)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93311__7)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93311__7)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93311__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93311__7)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv93311__16 = __DSPOT_invoc_11.annotated(__DSPOT_annotations_14379);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93311__16)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93311__16)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93311__16)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93311__16)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93311__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv93311__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93311__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93311__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93311__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93311__5)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93311__7)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93311__7)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93311__7)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93311__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93311__7)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv93299() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_14376 = new AnnotationSpec[0];
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93299__5 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv93299__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93299__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93299__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93299__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93299__5)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv93299__14 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93299__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93299__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93299__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93299__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93299__14)).isPrimitive());
        TypeName o_genericType_rv93299__16 = __DSPOT_invoc_6.annotated(__DSPOT_annotations_14376);
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93299__16)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93299__16)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93299__16)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93299__16)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93299__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv93299__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93299__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93299__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93299__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93299__5)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93299__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93299__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93299__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93299__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93299__14)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv93321() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93321__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv93321__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93321__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93321__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93321__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93321__4)).isPrimitive());
        TypeName o_genericType_rv93321__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93321__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93321__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93321__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93321__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93321__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv93321__15 = __DSPOT_invoc_11.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93321__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93321__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93321__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93321__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93321__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv93321__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93321__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93321__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93321__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93321__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93321__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93321__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93321__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93321__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93321__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv93287() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_14373 = new AnnotationSpec[]{  };
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName __DSPOT_invoc_4 = TypeName.get(recursiveEnum.getReturnType());
        TypeName o_genericType_rv93287__9 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93287__9)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93287__9)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93287__9)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93287__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93287__9)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv93287__14 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93287__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93287__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93287__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93287__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93287__14)).isPrimitive());
        TypeName o_genericType_rv93287__16 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_14373);
        Assert.assertFalse(((ClassName) (o_genericType_rv93287__16)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93287__16)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93287__16)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93287__16)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93287__16)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93287__9)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93287__9)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93287__9)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93287__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93287__9)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93287__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93287__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93287__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93287__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93287__14)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericTypelitNum93251_failAssert20() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[Integer.MAX_VALUE]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            org.junit.Assert.fail("genericTypelitNum93251 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("2147483647", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericType_rv93309() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93309__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv93309__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93309__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93309__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93309__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93309__4)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv93309__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93309__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93309__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93309__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93309__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93309__13)).isPrimitive());
        TypeName o_genericType_rv93309__15 = __DSPOT_invoc_6.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93309__15)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93309__15)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93309__15)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93309__15)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv93309__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv93309__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv93309__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93309__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93309__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv93309__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93309__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93309__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93309__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93309__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv93309__13)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericTypelitNum93260_failAssert27_add97812() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum93260_failAssert27_add97812__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum93260_failAssert27_add97812__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum93260_failAssert27_add97812__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum93260_failAssert27_add97812__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum93260_failAssert27_add97812__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericTypelitNum93260_failAssert27_add97812__6)).isPrimitive());
            TypeName o_genericTypelitNum93260_failAssert27_add97812__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum93260_failAssert27_add97812__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum93260_failAssert27_add97812__8)).isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum93260_failAssert27_add97812__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum93260_failAssert27_add97812__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum93260_failAssert27_add97812__8)).isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
            Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
            TypeName.get(recursiveEnum.getGenericParameterTypes()[-248142447]);
            org.junit.Assert.fail("genericTypelitNum93260 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void genericTypelitNum93251_failAssert20_add97433() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum93251_failAssert20_add97433__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum93251_failAssert20_add97433__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum93251_failAssert20_add97433__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum93251_failAssert20_add97433__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum93251_failAssert20_add97433__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericTypelitNum93251_failAssert20_add97433__6)).isPrimitive());
            TypeName o_genericTypelitNum93251_failAssert20_add97433__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum93251_failAssert20_add97433__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum93251_failAssert20_add97433__8)).isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum93251_failAssert20_add97433__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum93251_failAssert20_add97433__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum93251_failAssert20_add97433__8)).isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[Integer.MAX_VALUE]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            org.junit.Assert.fail("genericTypelitNum93251 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__9)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__9)).isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__9)).isPrimitive());
        TypeName o_innerClassInGenericType__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__11)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__11)).isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__11)).isPrimitive());
        String String_0 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_0);
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__9)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__9)).isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__11)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__11)).isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType__11)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv116280() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv116280__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).isPrimitive());
        TypeName o_innerClassInGenericType_rv116280__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).isPrimitive());
        String String_47 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_47);
        TypeName o_innerClassInGenericType_rv116280__19 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv116280__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv116280__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv116280__19)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv116280__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv116280__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_47);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg116276() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg116276__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116276__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116276__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg116276__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg116276__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116276__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg116276__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).isPrimitive());
        TypeName o_innerClassInGenericType_mg116276__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).isPrimitive());
        String String_44 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_44);
        TypeName o_innerClassInGenericType_mg116276__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__17)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__17)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__17)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116276__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116276__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg116276__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg116276__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116276__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_44);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString116244_failAssert102() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGen}ricIntInner").getGenericReturnType());
            String String_12 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            org.junit.Assert.fail("innerClassInGenericTypelitString116244 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.testGen}ricIntInner()", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg116266() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_17658 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg116266__5 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116266__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116266__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg116266__5)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg116266__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116266__5)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg116266__10 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).isPrimitive());
        TypeName o_innerClassInGenericType_mg116266__12 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).isPrimitive());
        String String_34 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_34);
        TypeName o_innerClassInGenericType_mg116266__18 = genericTypeName.annotated(__DSPOT_annotations_17658);
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__18)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__18)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__18)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__18)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__18)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116266__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116266__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg116266__5)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg116266__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg116266__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_34);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv116288() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv116288__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).isPrimitive());
        TypeName o_innerClassInGenericType_rv116288__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).isPrimitive());
        String String_55 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
        TypeName o_innerClassInGenericType_rv116288__19 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv116288__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv116288__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv116288__19)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv116288__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv116288__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv116278() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_17661 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv116278__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).isPrimitive());
        TypeName o_innerClassInGenericType_rv116278__14 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).isPrimitive());
        String String_45 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_45);
        TypeName o_innerClassInGenericType_rv116278__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_17661);
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv116278__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv116278__20)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv116278__20)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv116278__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv116278__20)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_45);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv116287_failAssert110() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            String String_54 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerClassInGenericType_rv116287 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString116248_failAssert106litString116441() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString116248_failAssert106litString116441__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerClassInGenericTypelitString116248_failAssert106litString116441__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_16 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Strin!>.Inner";
            org.junit.Assert.fail("innerClassInGenericTypelitString116248 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString116248_failAssert106litString116433_mg128057() throws Exception {
        try {
            AnnotationSpec[] __DSPOT_annotations_19180 = new AnnotationSpec[]{  };
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString116248_failAssert106litString116433__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerClassInGenericTypelitString116248_failAssert106litString116433__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_16 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "testGenericStringInner";
            org.junit.Assert.fail("innerClassInGenericTypelitString116248 should have thrown NoSuchMethodException");
            o_innerClassInGenericTypelitString116248_failAssert106litString116433__11.annotated(__DSPOT_annotations_19180);
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv128954() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv128954__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv128954__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).isPrimitive());
        String String_106 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_106);
        String o_innerGenericInGenericType_rv128954__19 = __DSPOT_invoc_4.toString();
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", o_innerGenericInGenericType_rv128954__19);
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_106);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString128915_failAssert123() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("=n4GhZs-JXP%(V6:;;}").getGenericReturnType());
            String String_68 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            org.junit.Assert.fail("innerGenericInGenericTypelitString128915 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.=n4GhZs-JXP%(V6:;;}()", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv128948() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv128948__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv128948__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).isPrimitive());
        String String_100 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_100);
        TypeName o_innerGenericInGenericType_rv128948__19 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv128948__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv128948__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv128948__19)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv128948__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv128948__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_100);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv128946() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_19253 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv128946__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv128946__14 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).isPrimitive());
        String String_98 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_98);
        TypeName o_innerGenericInGenericType_rv128946__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_19253);
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv128946__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv128946__20)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv128946__20)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv128946__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv128946__20)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_98);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv128955_failAssert128() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            String String_107 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerGenericInGenericType_rv128955 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv128956() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv128956__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv128956__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).isPrimitive());
        String String_108 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_108);
        TypeName o_innerGenericInGenericType_rv128956__19 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv128956__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv128956__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv128956__19)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv128956__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv128956__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_108);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg128944() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg128944__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128944__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128944__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg128944__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg128944__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128944__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg128944__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg128944__11 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).isPrimitive());
        String String_97 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
        TypeName o_innerGenericInGenericType_mg128944__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__17)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__17)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__17)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128944__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128944__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg128944__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg128944__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128944__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg128934() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_19250 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg128934__5 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128934__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128934__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg128934__5)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg128934__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128934__5)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg128934__10 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg128934__12 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).isPrimitive());
        String String_87 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
        TypeName o_innerGenericInGenericType_mg128934__18 = genericTypeName.annotated(__DSPOT_annotations_19250);
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__18)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__18)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__18)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__18)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__18)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128934__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128934__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg128934__5)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg128934__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg128934__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString128916_failAssert124litString129144() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString128916_failAssert124litString129144__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__6)).toString());
            Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerGenericInGenericTypelitString128916_failAssert124litString129144__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__11)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_69 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "\n";
            org.junit.Assert.fail("innerGenericInGenericTypelitString128916 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString128918_failAssert126litString129122_mg136963() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_20175 = Collections.<AnnotationSpec>emptyList();
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString128918_failAssert126litString129122__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__6)).toString());
            Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod(":").getGenericReturnType());
            String String_71 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGenerWic<java.lang.Long>";
            org.junit.Assert.fail("innerGenericInGenericTypelitString128918 should have thrown NoSuchMethodException");
            o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11.annotated(__DSPOT_annotations_20175);
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
        String String_2 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
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
    public void innerStaticInGenericType_rv142075() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_20913 = new AnnotationSpec[]{  };
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_137 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_137);
        TypeName o_innerStaticInGenericType_rv142075__14 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_20913);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142075__14)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142075__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv142075__14)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_rv142075__14)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142075__14)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_137);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv142085() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_147 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_147);
        TypeName o_innerStaticInGenericType_rv142085__13 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142085__13)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142085__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv142085__13)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_rv142085__13)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142085__13)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_147);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv142077() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_139 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_139);
        TypeName o_innerStaticInGenericType_rv142077__13 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142077__13)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142077__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv142077__13)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_rv142077__13)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142077__13)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_139);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg142072_failAssert136() throws Exception {
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            String String_135 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
            typeName.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_mg142072 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg142069() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg142069__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142069__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142069__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142069__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142069__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142069__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_132 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_132);
        typeName.isBoxedPrimitive();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142069__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142069__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142069__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142069__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142069__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_132);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142056_mg144367() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142056__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add142056__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add142056_mg144367__17 = o_innerStaticInGenericType_add142056__4.box();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056_mg144367__17)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056_mg144367__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056_mg144367__17)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056_mg144367__17)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056_mg144367__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142056_mg144406() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_21162 = new AnnotationSpec[]{  };
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142056__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add142056__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add142056_mg144406__18 = typeName.annotated(__DSPOT_annotations_21162);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056_mg144406__18)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056_mg144406__18)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056_mg144406__18)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056_mg144406__18)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056_mg144406__18)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142056_mg144380() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142056__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add142056__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add142056_mg144380__17 = o_innerStaticInGenericType_add142056__4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056_mg144380__17)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056_mg144380__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056_mg144380__17)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056_mg144380__17)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056_mg144380__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142056_mg144401_failAssert139() throws Exception {
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            TypeName o_innerStaticInGenericType_add142056__4 = TypeName.get(staticInGeneric.getReturnType());
            TypeName o_innerStaticInGenericType_add142056__6 = TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
            o_innerStaticInGenericType_add142056__6.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_add142056_mg144401 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142056litString143805() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142056__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add142056__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NesteQdNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NesteQdNonGeneric", String_119);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142056__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
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
    public void equalsAndHashCodeArrayTypeName_add2_add28() throws Exception {
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
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg184() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_6 = new AnnotationSpec[0];
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
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg184__14 = o_equalsAndHashCodeArrayTypeName_add2__3.annotated(__DSPOT_annotations_6);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg184__14)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg184__14)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg184__14)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg184__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg184__14)).isPrimitive());
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
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add4_mg191() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_10 = Collections.<AnnotationSpec>emptyList();
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
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4_mg191__15 = o_equalsAndHashCodeArrayTypeName_add4__2.annotated(__DSPOT_annotations_10);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).isPrimitive());
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
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add3_mg211() throws Exception {
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
        TypeName o_equalsAndHashCodeArrayTypeName_add3_mg211__13 = o_equalsAndHashCodeArrayTypeName_add3__3.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg211__13)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg211__13)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg211__13)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg211__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg211__13)).isPrimitive());
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
    public void equalsAndHashCodeArrayTypeName_add4_mg191_mg5042() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_452 = new AnnotationSpec[]{  };
        List<AnnotationSpec> __DSPOT_annotations_10 = Collections.<AnnotationSpec>emptyList();
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
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4_mg191__15 = o_equalsAndHashCodeArrayTypeName_add4__2.annotated(__DSPOT_annotations_10);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add4_mg191_mg5042__19 = o_equalsAndHashCodeArrayTypeName_add4__3.annotated(__DSPOT_annotations_452);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191_mg5042__19)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191_mg5042__19)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191_mg5042__19)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191_mg5042__19)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191_mg5042__19)).isPrimitive());
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
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg181_mg5040() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_4 = Collections.<AnnotationSpec>emptyList();
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
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2_mg181__15 = o_equalsAndHashCodeArrayTypeName_add2__2.annotated(__DSPOT_annotations_4);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg181_mg5040__18 = o_equalsAndHashCodeArrayTypeName_add2_mg181__15.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg5040__18)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg5040__18)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg5040__18)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg5040__18)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg5040__18)).isPrimitive());
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
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassName() throws Exception {
        ClassName o_equalsAndHashCodeClassName__1 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__1)).isPrimitive());
        ClassName o_equalsAndHashCodeClassName__2 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__2)).isPrimitive());
        TypeName o_equalsAndHashCodeClassName__3 = TypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__3)).isPrimitive());
        ClassName o_equalsAndHashCodeClassName__4 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__4)).isPrimitive());
        ClassName o_equalsAndHashCodeClassName__5 = ClassName.bestGuess("java.lang.Object");
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__5)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__5)).isPrimitive());
        ClassName o_equalsAndHashCodeClassName__6 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__6)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__5)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassName__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12463_failAssert2() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("+1!kAF:15u&sdcOg");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12463 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for +1!kAF:15u&sdcOg", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12459_failAssert0() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("java.lang");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12459 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for java.lang", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12464_failAssert3() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12464 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12461() throws Exception {
        ClassName o_equalsAndHashCodeClassNamelitString12461__1 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12461__2 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).isPrimitive());
        TypeName o_equalsAndHashCodeClassNamelitString12461__3 = TypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12461__4 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12461__5 = ClassName.bestGuess("java.l%ang.Object");
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).isBoxedPrimitive());
        Assert.assertEquals("java.l%ang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).toString());
        Assert.assertEquals(-1662323656, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12461__6 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__6)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).isBoxedPrimitive());
        Assert.assertEquals("java.l%ang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).toString());
        Assert.assertEquals(-1662323656, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__3)).isPrimitive());
            ClassName __DSPOT_invoc_4 = ClassName.get(Object.class);
            TypeName o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__7 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__7)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__8 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__8)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__8)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__8)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__8)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__8)).isPrimitive());
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12464 should have thrown IllegalArgumentException");
            __DSPOT_invoc_4.enclosingClassName();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__4)).isPrimitive());
            TypeName __DSPOT_invoc_5 = TypeName.get(Object.class);
            ClassName o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__8 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__8)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__8)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__8)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__8)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__8)).isPrimitive());
            ClassName.bestGuess(":");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12466 should have thrown IllegalArgumentException");
            __DSPOT_invoc_5.isBoxedPrimitive();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12464_failAssert3_add12558_mg23572() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_3213 = Collections.<AnnotationSpec>emptyList();
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__4)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__5 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__7)).isPrimitive());
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12464 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6.annotated(__DSPOT_annotations_3213);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12463_failAssert2_add12572_mg19889() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_2485 = Collections.<AnnotationSpec>emptyList();
            ClassName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__4)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__5 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__7)).isPrimitive());
            ClassName.bestGuess("+1!kAF:15u&sdcOg");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12463 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6.annotated(__DSPOT_annotations_2485);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34677() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34677__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34677__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34677__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34677_mg35257() throws Exception {
        List<TypeName> __DSPOT_typeArguments_5441 = Collections.<TypeName>emptyList();
        String __DSPOT_name_5440 = "js6$O_RR*O>daa`JUe/:";
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34677__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34677__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34677__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677_mg35257__22 = o_equalsAndHashCodeParameterizedTypeName_add34677__6.nestedClass(__DSPOT_name_5440, __DSPOT_typeArguments_5441);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677_mg35257__22)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677_mg35257__22)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>.js6$O_RR*O>daa`JUe/:", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677_mg35257__22)).toString());
        Assert.assertEquals(729619466, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677_mg35257__22)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677_mg35257__22)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34673_mg35218() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_5423 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34673__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673_mg35218__21 = o_equalsAndHashCodeParameterizedTypeName_add34673__3.annotated(__DSPOT_annotations_5423);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35218__21)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35218__21)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35218__21)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35218__21)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35218__21)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34673_mg35199() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_5412 = new AnnotationSpec[0];
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34673__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673_mg35199__20 = o_equalsAndHashCodeParameterizedTypeName_add34673__2.annotated(__DSPOT_annotations_5412);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35199__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35199__20)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35199__20)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35199__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35199__20)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34673_add35052() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34673__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34672_add35020_add36236() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34672__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34672__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34672__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34672__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34672__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34672__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg56295() throws Exception {
        Type[] __DSPOT_bounds_9384 = new Type[0];
        TypeName o_equalsAndHashCodeTypeVariableName_mg56295__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56295__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56295__8 = typeVar2.withBounds(__DSPOT_bounds_9384);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56295__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56295__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56295__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56295__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56295__8)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableNamelitString56277() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableNamelitString56277__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableNamelitString56277__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).isPrimitive());
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
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56288() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9378 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56288__9 = typeVar1.annotated(__DSPOT_annotations_9378);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg56289() throws Exception {
        TypeName[] __DSPOT_bounds_9379 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56289__8 = typeVar1.withBounds(__DSPOT_bounds_9379);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg56292() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableName_mg56292__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56292__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).isPrimitive());
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
        TypeName o_equalsAndHashCodeTypeVariableName_mg56292__7 = typeVar1.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56292__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56292__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56292__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56292__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56292__7)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg56291_mg58161() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9662 = Collections.<AnnotationSpec>emptyList();
        List<? extends TypeName> __DSPOT_bounds_9381 = Collections.emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg56291__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56291__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56291__9 = typeVar1.withBounds(__DSPOT_bounds_9381);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56291_mg58161__18 = typeVar2.annotated(__DSPOT_annotations_9662);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291_mg58161__18)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291_mg58161__18)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291_mg58161__18)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291_mg58161__18)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291_mg58161__18)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56290_mg57780() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_9494 = new AnnotationSpec[0];
        Type[] __DSPOT_bounds_9380 = new Type[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56290__8 = typeVar1.withBounds(__DSPOT_bounds_9380);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290_mg57780__16 = o_equalsAndHashCodeTypeVariableName_mg56290__2.annotated(__DSPOT_annotations_9494);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg57780__16)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg57780__16)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg57780__16)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg57780__16)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg57780__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56290_mg58266() throws Exception {
        TypeName[] __DSPOT_bounds_9717 = new TypeName[]{  };
        Type[] __DSPOT_bounds_9380 = new Type[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56290__8 = typeVar1.withBounds(__DSPOT_bounds_9380);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56290_mg58266__16 = o_equalsAndHashCodeTypeVariableName_mg56290__8.withBounds(__DSPOT_bounds_9717);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg58266__16)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg58266__16)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg58266__16)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg58266__16)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg58266__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56288_mg57828() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9378 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56288__9 = typeVar1.annotated(__DSPOT_annotations_9378);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288_mg57828__16 = o_equalsAndHashCodeTypeVariableName_mg56288__4.box();
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288_mg57828__16)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288_mg57828__16)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288_mg57828__16)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288_mg57828__16)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288_mg57828__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56288litString56509() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9378 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56288__9 = typeVar1.annotated(__DSPOT_annotations_9378);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56289_mg58291() throws Exception {
        TypeName[] __DSPOT_bounds_9379 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56289__8 = typeVar1.withBounds(__DSPOT_bounds_9379);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289_mg58291__15 = o_equalsAndHashCodeTypeVariableName_mg56289__8.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289_mg58291__15)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289_mg58291__15)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289_mg58291__15)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289_mg58291__15)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289_mg58291__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56296_mg57673_failAssert9() throws Exception {
        try {
            List<? extends TypeName> __DSPOT_bounds_9385 = Collections.emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__3 = TypeVariableName.get(Object.class);
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__4 = TypeVariableName.get(Object.class);
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56296__9 = typeVar2.withBounds(__DSPOT_bounds_9385);
            o_equalsAndHashCodeTypeVariableName_mg56296__3.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg56296_mg57673 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox java.lang.Object", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56288_mg57916_failAssert7_add75680() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_9378 = Collections.<AnnotationSpec>emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg56288__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg56288__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).isPrimitive());
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
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56288__9 = typeVar1.annotated(__DSPOT_annotations_9378);
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg56288__4.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg56288_mg57916 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56296_mg57986_failAssert8_mg75604() throws Exception {
        try {
            Type[] __DSPOT_bounds_12660 = new Type[]{  };
            List<? extends TypeName> __DSPOT_bounds_9385 = Collections.emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).isPrimitive());
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
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56296__9 = typeVar2.withBounds(__DSPOT_bounds_9385);
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg56296__4.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg56296_mg57986 should have thrown UnsupportedOperationException");
            o_equalsAndHashCodeTypeVariableName_mg56296__9.withBounds(__DSPOT_bounds_12660);
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56296_mg57673_failAssert9litString75617() throws Exception {
        try {
            List<? extends TypeName> __DSPOT_bounds_9385 = Collections.emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).isPrimitive());
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
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56296__9 = typeVar2.withBounds(__DSPOT_bounds_9385);
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).isBoxedPrimitive());
            Assert.assertEquals("\n", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).toString());
            Assert.assertEquals(10, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg56296__3.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg56296_mg57673 should have thrown UnsupportedOperationException");
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
    public void equalsAndHashCodeWildcardTypeName_add78647() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add78644_mg79061() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_13278 = Collections.<AnnotationSpec>emptyList();
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644_mg79061__21 = o_equalsAndHashCodeWildcardTypeName_add78644__3.annotated(__DSPOT_annotations_13278);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644_mg79061__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644_mg79061__21)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644_mg79061__21)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644_mg79061__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644_mg79061__21)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add78644_add78958() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_13293 = Collections.<AnnotationSpec>emptyList();
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21 = o_equalsAndHashCodeWildcardTypeName_add78645__6.annotated(__DSPOT_annotations_13293);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).isPrimitive());
        TypeName o_equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706__24 = o_equalsAndHashCodeWildcardTypeName_add78645__6.withoutAnnotations();
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706__24)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706__24)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706__24)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706__24)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706__24)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).isPrimitive());
    }

    private void assertEqualsHashCodeAndToString(TypeName a, TypeName b) {
        Assert.assertEquals(a.toString(), b.toString());
        Truth.assertThat(a.equals(b)).isTrue();
        Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        Assert.assertFalse(a.equals(null));
    }
}

