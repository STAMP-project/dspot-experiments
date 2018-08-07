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
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType__11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType__11)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv92839() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName __DSPOT_invoc_4 = TypeName.get(recursiveEnum.getReturnType());
        TypeName o_genericType_rv92839__8 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92839__8)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92839__8)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92839__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92839__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92839__8)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv92839__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92839__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92839__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92839__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92839__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92839__13)).isPrimitive());
        TypeName o_genericType_rv92839__15 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_genericType_rv92839__15)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92839__15)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92839__15)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92839__15)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92839__15)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92839__8)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92839__8)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92839__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92839__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92839__8)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92839__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92839__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92839__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92839__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92839__13)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv92841() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_14224 = new AnnotationSpec[0];
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv92841__5 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv92841__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92841__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92841__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92841__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92841__5)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv92841__14 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92841__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92841__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92841__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92841__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92841__14)).isPrimitive());
        TypeName o_genericType_rv92841__16 = __DSPOT_invoc_6.annotated(__DSPOT_annotations_14224);
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92841__16)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92841__16)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92841__16)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92841__16)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92841__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv92841__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92841__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92841__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92841__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92841__5)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92841__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92841__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92841__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92841__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92841__14)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv92842() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_14225 = Collections.<AnnotationSpec>emptyList();
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv92842__6 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv92842__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92842__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92842__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92842__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92842__6)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv92842__15 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92842__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92842__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92842__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92842__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92842__15)).isPrimitive());
        TypeName o_genericType_rv92842__17 = __DSPOT_invoc_6.annotated(__DSPOT_annotations_14225);
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92842__17)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92842__17)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92842__17)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92842__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92842__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv92842__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92842__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92842__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92842__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92842__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92842__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92842__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92842__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92842__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92842__15)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv92850_failAssert34() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            __DSPOT_invoc_6.unbox();
            org.junit.Assert.fail("genericType_rv92850 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericType_rv92851() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv92851__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv92851__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92851__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92851__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92851__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92851__4)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv92851__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92851__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92851__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92851__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92851__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92851__13)).isPrimitive());
        TypeName o_genericType_rv92851__15 = __DSPOT_invoc_6.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92851__15)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92851__15)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92851__15)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92851__15)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92851__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv92851__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92851__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92851__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92851__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92851__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92851__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92851__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92851__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92851__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92851__13)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv92854() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_14228 = Collections.<AnnotationSpec>emptyList();
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv92854__6 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv92854__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92854__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92854__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92854__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92854__6)).isPrimitive());
        TypeName o_genericType_rv92854__8 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92854__8)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92854__8)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92854__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92854__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92854__8)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv92854__17 = __DSPOT_invoc_11.annotated(__DSPOT_annotations_14228);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92854__17)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92854__17)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92854__17)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92854__17)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92854__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv92854__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92854__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92854__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92854__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92854__6)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92854__8)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92854__8)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92854__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92854__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92854__8)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv92855() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv92855__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv92855__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92855__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92855__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92855__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92855__4)).isPrimitive());
        TypeName o_genericType_rv92855__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92855__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92855__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92855__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92855__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92855__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv92855__15 = __DSPOT_invoc_11.box();
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92855__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92855__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92855__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92855__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92855__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv92855__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92855__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92855__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92855__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92855__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92855__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92855__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92855__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92855__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92855__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv92862_failAssert35() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            __DSPOT_invoc_11.unbox();
            org.junit.Assert.fail("genericType_rv92862 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E[]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericType_rv92863() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv92863__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv92863__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92863__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92863__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92863__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92863__4)).isPrimitive());
        TypeName o_genericType_rv92863__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92863__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92863__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92863__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92863__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92863__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv92863__15 = __DSPOT_invoc_11.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92863__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92863__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv92863__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv92863__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv92863__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv92863__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv92863__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv92863__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv92863__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv92863__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92863__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92863__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv92863__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv92863__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv92863__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericTypelitNum92792_failAssert23() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[-1]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            org.junit.Assert.fail("genericTypelitNum92792 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericTypelitNum92793_failAssert24_add93623() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum92793_failAssert24_add93623__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum92793_failAssert24_add93623__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum92793_failAssert24_add93623__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum92793_failAssert24_add93623__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum92793_failAssert24_add93623__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericTypelitNum92793_failAssert24_add93623__6)).isPrimitive());
            TypeName o_genericTypelitNum92793_failAssert24_add93623__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum92793_failAssert24_add93623__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum92793_failAssert24_add93623__8)).isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum92793_failAssert24_add93623__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum92793_failAssert24_add93623__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum92793_failAssert24_add93623__8)).isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[Integer.MAX_VALUE]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            org.junit.Assert.fail("genericTypelitNum92793 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void genericTypelitNum92796_failAssert26() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[-340171450]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            org.junit.Assert.fail("genericTypelitNum92796 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-340171450", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericTypelitNum92802_failAssert31_add93681() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum92802_failAssert31_add93681__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum92802_failAssert31_add93681__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum92802_failAssert31_add93681__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum92802_failAssert31_add93681__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum92802_failAssert31_add93681__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericTypelitNum92802_failAssert31_add93681__6)).isPrimitive());
            TypeName o_genericTypelitNum92802_failAssert31_add93681__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum92802_failAssert31_add93681__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum92802_failAssert31_add93681__8)).isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum92802_failAssert31_add93681__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum92802_failAssert31_add93681__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum92802_failAssert31_add93681__8)).isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
            Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
            TypeName.get(recursiveEnum.getGenericParameterTypes()[-1276148211]);
            org.junit.Assert.fail("genericTypelitNum92802 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg115850() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_17504 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg115850__5 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115850__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115850__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg115850__5)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg115850__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115850__5)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg115850__10 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).isPrimitive());
        TypeName o_innerClassInGenericType_mg115850__12 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).isPrimitive());
        String String_34 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_34);
        TypeName o_innerClassInGenericType_mg115850__18 = genericTypeName.annotated(__DSPOT_annotations_17504);
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__18)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__18)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__18)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__18)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__18)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115850__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115850__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg115850__5)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg115850__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115850__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__10)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115850__12)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_34);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg115860() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg115860__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115860__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115860__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg115860__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg115860__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115860__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg115860__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).isPrimitive());
        TypeName o_innerClassInGenericType_mg115860__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).isPrimitive());
        String String_44 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_44);
        TypeName o_innerClassInGenericType_mg115860__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__17)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__17)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__17)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115860__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115860__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg115860__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg115860__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg115860__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg115860__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_44);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv115862() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_17507 = new AnnotationSpec[0];
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv115862__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).isPrimitive());
        TypeName o_innerClassInGenericType_rv115862__14 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).isPrimitive());
        String String_45 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_45);
        TypeName o_innerClassInGenericType_rv115862__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_17507);
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv115862__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv115862__20)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv115862__20)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv115862__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv115862__20)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__12)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115862__14)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_45);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv115864() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv115864__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).isPrimitive());
        TypeName o_innerClassInGenericType_rv115864__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).isPrimitive());
        String String_47 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_47);
        TypeName o_innerClassInGenericType_rv115864__19 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv115864__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv115864__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv115864__19)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv115864__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv115864__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115864__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_47);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv115866() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv115866__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).isPrimitive());
        TypeName o_innerClassInGenericType_rv115866__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).isPrimitive());
        String String_49 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_49);
        __DSPOT_invoc_4.hashCode();
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115866__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_49);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv115871_failAssert114() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            String String_54 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerClassInGenericType_rv115871 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv115872() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv115872__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).isPrimitive());
        TypeName o_innerClassInGenericType_rv115872__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).isPrimitive());
        String String_55 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
        TypeName o_innerClassInGenericType_rv115872__19 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv115872__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv115872__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv115872__19)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv115872__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv115872__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv115872__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString115832_failAssert110_add116666() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString115832_failAssert110_add116666__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerClassInGenericTypelitString115832_failAssert110_add116666__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110_add116666__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_16 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            org.junit.Assert.fail("innerClassInGenericTypelitString115832 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString115832_failAssert110litString116020_mg122729() throws Exception {
        try {
            AnnotationSpec[] __DSPOT_annotations_18143 = new AnnotationSpec[]{  };
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString115832_failAssert110litString116020__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerClassInGenericTypelitString115832_failAssert110litString116020__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString115832_failAssert110litString116020__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_16 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<Tava.lang.String>.Inner";
            org.junit.Assert.fail("innerClassInGenericTypelitString115832 should have thrown NoSuchMethodException");
            o_innerClassInGenericTypelitString115832_failAssert110litString116020__6.annotated(__DSPOT_annotations_18143);
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString115833_failAssert111() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("\n").getGenericReturnType());
            String String_17 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            org.junit.Assert.fail("innerClassInGenericTypelitString115833 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.\n()", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_add129522() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_add129522__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_add129522__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_add129522__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_add129522__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_add129522__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_add129522__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_add129522__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).isPrimitive());
        TypeName o_innerGenericInGenericType_add129522__11 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).isPrimitive());
        String String_81 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_81);
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_add129522__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_add129522__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_add129522__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_add129522__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_add129522__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_add129522__11)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg129528() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_19253 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg129528__5 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129528__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129528__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg129528__5)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg129528__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129528__5)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg129528__10 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg129528__12 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).isPrimitive());
        String String_87 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
        TypeName o_innerGenericInGenericType_mg129528__18 = genericTypeName.annotated(__DSPOT_annotations_19253);
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__18)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__18)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__18)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__18)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__18)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129528__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129528__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg129528__5)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg129528__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129528__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__10)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129528__12)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg129537_failAssert131() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            String String_96 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            genericTypeName.unbox();
            org.junit.Assert.fail("innerGenericInGenericType_mg129537 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg129538() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg129538__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129538__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129538__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg129538__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg129538__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129538__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg129538__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg129538__11 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).isPrimitive());
        String String_97 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
        TypeName o_innerGenericInGenericType_mg129538__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__17)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__17)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__17)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129538__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129538__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg129538__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg129538__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg129538__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg129538__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv129540() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_19256 = new AnnotationSpec[0];
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv129540__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv129540__14 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).isPrimitive());
        String String_98 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_98);
        TypeName o_innerGenericInGenericType_rv129540__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_19256);
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv129540__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv129540__20)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv129540__20)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv129540__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv129540__20)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__12)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129540__14)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_98);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv129542() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv129542__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv129542__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).isPrimitive());
        String String_100 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_100);
        TypeName o_innerGenericInGenericType_rv129542__19 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv129542__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv129542__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv129542__19)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv129542__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv129542__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv129542__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_100);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString129505_failAssert123() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("String").getGenericReturnType());
            String String_64 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            org.junit.Assert.fail("innerGenericInGenericTypelitString129505 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.String()", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString129510_failAssert128litString129695_mg139718() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_20504 = Collections.<AnnotationSpec>emptyList();
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString129510_failAssert128litString129695__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__6)).toString());
            Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerGenericInGenericTypelitString129510_failAssert128litString129695__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__11)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129510_failAssert128litString129695__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_69 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang[Short>.InnerGeneric<java.lang.Long>";
            org.junit.Assert.fail("innerGenericInGenericTypelitString129510 should have thrown NoSuchMethodException");
            genericTypeName.annotated(__DSPOT_annotations_20504);
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString129511_failAssert129_add131701() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString129511_failAssert129_add131701__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__6)).toString());
            Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerGenericInGenericTypelitString129511_failAssert129_add131701__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__11)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString129511_failAssert129_add131701__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("\n").getGenericReturnType());
            String String_70 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            org.junit.Assert.fail("innerGenericInGenericTypelitString129511 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142602_mg144801() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142602__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add142602__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add142602_mg144801__17 = o_innerStaticInGenericType_add142602__6.box();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602_mg144801__17)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602_mg144801__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602_mg144801__17)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602_mg144801__17)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602_mg144801__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142602_mg144824_failAssert144() throws Exception {
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            TypeName o_innerStaticInGenericType_add142602__4 = TypeName.get(staticInGeneric.getReturnType());
            TypeName o_innerStaticInGenericType_add142602__6 = TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
            o_innerStaticInGenericType_add142602__6.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_add142602_mg144824 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142602_mg144828() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142602__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add142602__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add142602_mg144828__17 = o_innerStaticInGenericType_add142602__6.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602_mg144828__17)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602_mg144828__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602_mg144828__17)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602_mg144828__17)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602_mg144828__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142602__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142602litString142832() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testGenericIntInner");
        TypeName o_innerStaticInGenericType_add142602__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerStaticInGenericType_add142602__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add142602__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerStaticInGenericType_add142602__6)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (typeName)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (typeName)).isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (typeName)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (typeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (typeName)).isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerStaticInGenericType_add142602__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerStaticInGenericType_add142602__6)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerStaticInGenericType_add142602__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add142602__6)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (typeName)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (typeName)).isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (typeName)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (typeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (typeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg142609_mg144371() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_21069 = Collections.<AnnotationSpec>emptyList();
        AnnotationSpec[] __DSPOT_annotations_20913 = new AnnotationSpec[]{  };
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg142609__5 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142609__5)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142609__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__5)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_126 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_126);
        TypeName o_innerStaticInGenericType_mg142609__12 = typeName.annotated(__DSPOT_annotations_20913);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__12)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142609__12)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142609__12)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__12)).isPrimitive());
        TypeName o_innerStaticInGenericType_mg142609_mg144371__19 = o_innerStaticInGenericType_mg142609__5.annotated(__DSPOT_annotations_21069);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609_mg144371__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609_mg144371__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142609_mg144371__19)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142609_mg144371__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609_mg144371__19)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142609__5)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142609__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__5)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_126);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__12)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142609__12)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142609__12)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142609__12)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg142611() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg142611__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142611__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142611__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142611__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142611__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142611__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_128 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_128);
        TypeName o_innerStaticInGenericType_mg142611__11 = typeName.box();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142611__11)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142611__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142611__11)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142611__11)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142611__11)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142611__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142611__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142611__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142611__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142611__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_128);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg142615() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg142615__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142615__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142615__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142615__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142615__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142615__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_132 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_132);
        typeName.isBoxedPrimitive();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142615__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142615__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142615__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142615__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142615__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_132);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg142619() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg142619__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142619__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142619__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142619__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142619__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142619__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_136 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_136);
        TypeName o_innerStaticInGenericType_mg142619__11 = typeName.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142619__11)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142619__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142619__11)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142619__11)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142619__11)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142619__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142619__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142619__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg142619__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg142619__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_136);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv142621() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_20916 = new AnnotationSpec[0];
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
        TypeName o_innerStaticInGenericType_rv142621__14 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_20916);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142621__14)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142621__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv142621__14)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_rv142621__14)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv142621__14)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_137);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv142630_failAssert142() throws Exception {
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            String String_146 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_rv142630 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2() throws Exception {
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
    public void equalsAndHashCodeArrayTypeName_add2_mg181_mg10681() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_1064 = Collections.<AnnotationSpec>emptyList();
        AnnotationSpec[] __DSPOT_annotations_7 = new AnnotationSpec[]{  };
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
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg181__14 = o_equalsAndHashCodeArrayTypeName_add2__3.annotated(__DSPOT_annotations_7);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2_mg181_mg10681__19 = o_equalsAndHashCodeArrayTypeName_add2__4.annotated(__DSPOT_annotations_1064);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg10681__19)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg10681__19)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg10681__19)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg10681__19)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg10681__19)).isPrimitive());
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
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__14)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg185_mg11631() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9 = Collections.<AnnotationSpec>emptyList();
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
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg185__15 = o_equalsAndHashCodeArrayTypeName_add2__3.annotated(__DSPOT_annotations_9);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg185_mg11631__18 = o_equalsAndHashCodeArrayTypeName_add2__3.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185_mg11631__18)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185_mg11631__18)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185_mg11631__18)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185_mg11631__18)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185_mg11631__18)).isPrimitive());
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
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg185__15)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add3_mg186() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_10 = new AnnotationSpec[]{  };
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
        TypeName o_equalsAndHashCodeArrayTypeName_add3_mg186__14 = o_equalsAndHashCodeArrayTypeName_add3__3.annotated(__DSPOT_annotations_10);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg186__14)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg186__14)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg186__14)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg186__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg186__14)).isPrimitive());
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
    public void equalsAndHashCodeArrayTypeName_add3_mg188() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_12 = Collections.<AnnotationSpec>emptyList();
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
        TypeName o_equalsAndHashCodeArrayTypeName_add3_mg188__15 = o_equalsAndHashCodeArrayTypeName_add3__3.annotated(__DSPOT_annotations_12);
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
    public void equalsAndHashCodeArrayTypeName_add4_mg198() throws Exception {
        Object __DSPOT_o_18 = new Object();
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
        boolean o_equalsAndHashCodeArrayTypeName_add4_mg198__15 = o_equalsAndHashCodeArrayTypeName_add4__3.equals(__DSPOT_o_18);
        Assert.assertFalse(o_equalsAndHashCodeArrayTypeName_add4_mg198__15);
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
    public void equalsAndHashCodeArrayTypeName_add4_mg205() throws Exception {
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
        TypeName o_equalsAndHashCodeArrayTypeName_add4_mg205__13 = o_equalsAndHashCodeArrayTypeName_add4__3.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg205__13)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg205__13)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg205__13)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg205__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg205__13)).isPrimitive());
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
    public void genericType_rv93303() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93303__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93303__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93303__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93303__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93303__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93303__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        int o_genericType_rv93303__15 = __DSPOT_invoc_6.hashCode();
        Assert.assertEquals(69, ((int) (o_genericType_rv93303__15)));
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93303__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93303__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93303__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93303__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12356() throws Exception {
        ClassName o_equalsAndHashCodeClassNamelitString12356__1 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12356__2 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).isPrimitive());
        TypeName o_equalsAndHashCodeClassNamelitString12356__3 = TypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12356__4 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12356__5 = ClassName.bestGuess("java.lang+.Object");
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang+.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).toString());
        Assert.assertEquals(-2087056708, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12356__6 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__6)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang+.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).toString());
        Assert.assertEquals(-2087056708, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12356__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12358_failAssert2_add12456_mg33078() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_5109 = Collections.<AnnotationSpec>emptyList();
            ClassName o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__4)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__5 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__7)).isPrimitive());
            ClassName.bestGuess("!kAF:15u&sdcOgKS");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12358 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12358_failAssert2_add12456__6.annotated(__DSPOT_annotations_5109);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12359_failAssert3() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12359 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12359_failAssert3_add12466_mg32754() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_5027 = Collections.<AnnotationSpec>emptyList();
            ClassName o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__4)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__5 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__7)).isPrimitive());
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12359 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12359_failAssert3_add12466__5.annotated(__DSPOT_annotations_5027);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__3)).isPrimitive());
            ClassName __DSPOT_invoc_4 = ClassName.get(Object.class);
            TypeName o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__7 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__7)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__8 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__8)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__8)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__8)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__8)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12359_failAssert3_rv12650__8)).isPrimitive());
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12359 should have thrown IllegalArgumentException");
            __DSPOT_invoc_4.reflectionName();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__4)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__5 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__5)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__6 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12360_failAssert4_rv12711__6)).isPrimitive());
            ClassName.bestGuess("\n");
            ClassName __DSPOT_invoc_8 = ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12360 should have thrown IllegalArgumentException");
            __DSPOT_invoc_8.isAnnotated();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12361_failAssert5() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess(":");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12361 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34515_mg35157() throws Exception {
        List<TypeName> __DSPOT_typeArguments_5449 = Collections.<TypeName>emptyList();
        String __DSPOT_name_5448 = "riveeMGa{.5?)!Mmvs}c";
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34515__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34515__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34515__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34515__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34515__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34515__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34515_mg35157__22 = o_equalsAndHashCodeParameterizedTypeName_add34515__6.nestedClass(__DSPOT_name_5448, __DSPOT_typeArguments_5449);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515_mg35157__22)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515_mg35157__22)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>.riveeMGa{.5?)!Mmvs}c", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515_mg35157__22)).toString());
        Assert.assertEquals(2109748979, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515_mg35157__22)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515_mg35157__22)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34515__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34515__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34517() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34517__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34517__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34517__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34517__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34517__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34517__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34517__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34517__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34520_add34543() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34520__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34520__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34520__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34520_add34730_add50426() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34520__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34520__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34520__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34520_mg34979() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_5359 = new AnnotationSpec[0];
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34520__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34520__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34520__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34520_mg34979__20 = o_equalsAndHashCodeParameterizedTypeName_add34520__2.annotated(__DSPOT_annotations_5359);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg34979__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg34979__20)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg34979__20)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg34979__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg34979__20)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34520_mg35050() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_5389 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34520__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34520__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34520__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34520_mg35050__21 = o_equalsAndHashCodeParameterizedTypeName_add34520__3.annotated(__DSPOT_annotations_5389);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg35050__21)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg35050__21)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg35050__21)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg35050__21)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520_mg35050__21)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34520__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34520__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55912() throws Exception {
        List<? extends TypeName> __DSPOT_bounds_9304 = Collections.emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg55912__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55912__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55912__9 = typeVar1.withBounds(__DSPOT_bounds_9304);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg55912_mg57435() throws Exception {
        List<? extends TypeName> __DSPOT_bounds_9304 = Collections.emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg55912__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55912__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55912__9 = typeVar1.withBounds(__DSPOT_bounds_9304);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55912_mg57435__16 = o_equalsAndHashCodeTypeVariableName_mg55912__3.box();
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57435__16)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57435__16)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57435__16)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57435__16)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57435__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55912_mg57736() throws Exception {
        TypeName[] __DSPOT_bounds_9596 = new TypeName[0];
        List<? extends TypeName> __DSPOT_bounds_9304 = Collections.emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg55912__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55912__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55912__9 = typeVar1.withBounds(__DSPOT_bounds_9304);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55912_mg57736__17 = typeVar1.withBounds(__DSPOT_bounds_9596);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57736__17)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57736__17)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57736__17)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57736__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57736__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55912_mg57739() throws Exception {
        Type[] __DSPOT_bounds_9599 = new Type[]{  };
        List<? extends TypeName> __DSPOT_bounds_9304 = Collections.emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg55912__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55912__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55912__9 = typeVar1.withBounds(__DSPOT_bounds_9304);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55912_mg57739__17 = typeVar1.withBounds(__DSPOT_bounds_9599);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57739__17)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57739__17)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57739__17)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57739__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912_mg57739__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55912__4)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55912__9)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55913() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableName_mg55913__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55913__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isPrimitive());
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
        TypeName o_equalsAndHashCodeTypeVariableName_mg55913__7 = typeVar1.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg55913_mg57807() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_9641 = new AnnotationSpec[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg55913__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55913__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isPrimitive());
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
        TypeName o_equalsAndHashCodeTypeVariableName_mg55913__7 = typeVar1.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55913_mg57807__15 = o_equalsAndHashCodeTypeVariableName_mg55913__7.annotated(__DSPOT_annotations_9641);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913_mg57807__15)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913_mg57807__15)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913_mg57807__15)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913_mg57807__15)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913_mg57807__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55913__2)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55913__7)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55915() throws Exception {
        TypeName[] __DSPOT_bounds_9306 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg55915__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55915__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55915__8 = typeVar2.withBounds(__DSPOT_bounds_9306);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg55915_mg57708_failAssert10_mg60861() throws Exception {
        try {
            TypeName[] __DSPOT_bounds_9306 = new TypeName[]{  };
            TypeName o_equalsAndHashCodeTypeVariableName_mg55915__2 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg55915__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isPrimitive());
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
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55915__8 = typeVar2.withBounds(__DSPOT_bounds_9306);
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg55915__3.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg55915_mg57708 should have thrown UnsupportedOperationException");
            o_equalsAndHashCodeTypeVariableName_mg55915__3.isAnnotated();
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55915_mg57708_failAssert10litString59459() throws Exception {
        try {
            TypeName[] __DSPOT_bounds_9306 = new TypeName[]{  };
            TypeName o_equalsAndHashCodeTypeVariableName_mg55915__2 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg55915__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("\n", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
            Assert.assertEquals("\n", ((TypeVariableName) (typeVar1)).toString());
            Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55915__8 = typeVar2.withBounds(__DSPOT_bounds_9306);
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg55915__3.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg55915_mg57708 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55915_mg57766() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9612 = Collections.<AnnotationSpec>emptyList();
        TypeName[] __DSPOT_bounds_9306 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg55915__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55915__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55915__8 = typeVar2.withBounds(__DSPOT_bounds_9306);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55915_mg57766__17 = o_equalsAndHashCodeTypeVariableName_mg55915__8.annotated(__DSPOT_annotations_9612);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915_mg57766__17)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915_mg57766__17)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915_mg57766__17)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915_mg57766__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915_mg57766__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55915__3)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55915__8)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55916() throws Exception {
        Type[] __DSPOT_bounds_9307 = new Type[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg55916__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55916__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55916__8 = typeVar2.withBounds(__DSPOT_bounds_9307);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55916__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55916__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55916__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55916__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55916__8)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55916__3)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg55917_mg57173_failAssert7() throws Exception {
        try {
            List<? extends TypeName> __DSPOT_bounds_9308 = Collections.emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg55917__3 = TypeVariableName.get(Object.class);
            TypeName o_equalsAndHashCodeTypeVariableName_mg55917__4 = TypeVariableName.get(Object.class);
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55917__9 = typeVar2.withBounds(__DSPOT_bounds_9308);
            o_equalsAndHashCodeTypeVariableName_mg55917__3.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_mg55917_mg57173 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox java.lang.Object", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55918_mg57780() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9620 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg55918__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55918__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).isPrimitive());
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
        TypeName o_equalsAndHashCodeTypeVariableName_mg55918__7 = typeVar2.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55918_mg57780__16 = typeVar1.annotated(__DSPOT_annotations_9620);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918_mg57780__16)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918_mg57780__16)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918_mg57780__16)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918_mg57780__16)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918_mg57780__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55918__2)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55918__7)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamelitString55895() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableNamelitString55895__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableNamelitString55895__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("String", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("String", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(-1808118735, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55895__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("String", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(-1808118735, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamelitString55903() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableNamelitString55903__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableNamelitString55903__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).isPrimitive());
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
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55903__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add77553_add77631_add90260() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77553__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77553__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77553__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77553__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77553__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77553__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77553__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add77555_add77612() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add77555_mg77950() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_13075 = Collections.<AnnotationSpec>emptyList();
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77555_mg77950__21 = o_equalsAndHashCodeWildcardTypeName_add77555__2.annotated(__DSPOT_annotations_13075);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555_mg77950__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555_mg77950__21)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555_mg77950__21)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555_mg77950__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555_mg77950__21)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__5)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77555__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add77558() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77558__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77558__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77558__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77558__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77558__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add77558__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add77558__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv93313() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93313__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93313__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93313__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93313__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93313__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93313__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv93313__15 = __DSPOT_invoc_11.box();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93313__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93313__15)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93313__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93313__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93313__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93313__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
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
            Assert.fail("genericType_rv93320 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E[]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericType_rv93311() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_14379 = new AnnotationSpec[]{  };
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93311__5 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93311__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93311__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93311__7 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93311__7)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93311__7)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv93311__16 = __DSPOT_invoc_11.annotated(__DSPOT_annotations_14379);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93311__16)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93311__16)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93311__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93311__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93311__7)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93311__7)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv93299() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_14376 = new AnnotationSpec[0];
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93299__5 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93299__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93299__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93299__14 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93299__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93299__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93299__16 = __DSPOT_invoc_6.annotated(__DSPOT_annotations_14376);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93299__16)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93299__16)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93299__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93299__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93299__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93299__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv93321() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93321__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93321__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93321__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93321__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93321__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93321__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv93321__15 = __DSPOT_invoc_11.withoutAnnotations();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93321__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93321__15)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93321__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93321__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93321__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93321__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv93287() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_14373 = new AnnotationSpec[]{  };
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName __DSPOT_invoc_4 = TypeName.get(recursiveEnum.getReturnType());
        TypeName o_genericType_rv93287__9 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93287__9)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93287__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93287__14 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93287__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93287__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93287__16 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_14373);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93287__16)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93287__16)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93287__9)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93287__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93287__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93287__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericTypelitNum93251_failAssert20() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[Integer.MAX_VALUE]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            Assert.fail("genericTypelitNum93251 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("2147483647", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericType_rv93309() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv93309__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93309__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93309__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93309__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93309__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93309__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_genericType_rv93309__15 = __DSPOT_invoc_6.withoutAnnotations();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv93309__15)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv93309__15)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv93309__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv93309__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv93309__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv93309__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericTypelitNum93260_failAssert27_add97812() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum93260_failAssert27_add97812__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum93260_failAssert27_add97812__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum93260_failAssert27_add97812__6)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_genericTypelitNum93260_failAssert27_add97812__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum93260_failAssert27_add97812__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum93260_failAssert27_add97812__8)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
            Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName.get(recursiveEnum.getGenericParameterTypes()[(-248142447)]);
            Assert.fail("genericTypelitNum93260 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void genericTypelitNum93251_failAssert20_add97433() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum93251_failAssert20_add97433__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum93251_failAssert20_add97433__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum93251_failAssert20_add97433__6)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_genericTypelitNum93251_failAssert20_add97433__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum93251_failAssert20_add97433__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum93251_failAssert20_add97433__8)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[Integer.MAX_VALUE]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            Assert.fail("genericTypelitNum93251 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType__11)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_0 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_0);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType__11)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv116280() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_rv116280__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_rv116280__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_47 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_47);
        TypeName o_innerClassInGenericType_rv116280__19 = __DSPOT_invoc_4.box();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv116280__19)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv116280__19)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116280__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_47);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg116276() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg116276__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg116276__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg116276__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_mg116276__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_mg116276__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_44 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_44);
        TypeName o_innerClassInGenericType_mg116276__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__17)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__17)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg116276__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg116276__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116276__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
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
            Assert.fail("innerClassInGenericTypelitString116244 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.testGen}ricIntInner()", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg116266() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_17658 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg116266__5 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg116266__5)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg116266__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_mg116266__10 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_mg116266__12 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_34 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_34);
        TypeName o_innerClassInGenericType_mg116266__18 = genericTypeName.annotated(__DSPOT_annotations_17658);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__18)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__18)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg116266__5)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg116266__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__10)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg116266__12)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_34);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv116288() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_rv116288__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_rv116288__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_55 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
        TypeName o_innerClassInGenericType_rv116288__19 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv116288__19)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv116288__19)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116288__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv116278() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_17661 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_rv116278__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerClassInGenericType_rv116278__14 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_45 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", String_45);
        TypeName o_innerClassInGenericType_rv116278__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_17661);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv116278__20)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv116278__20)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__12)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).toString());
        Assert.assertEquals((-1471120843), ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv116278__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
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
            Assert.fail("innerClassInGenericType_rv116287 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString116248_failAssert106litString116441() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString116248_failAssert106litString116441__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__6)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertFalse(isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_innerClassInGenericTypelitString116248_failAssert106litString116441__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertFalse(isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116441__11)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_16 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Strin!>.Inner";
            Assert.fail("innerClassInGenericTypelitString116248 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString116248_failAssert106litString116433_mg128057() throws Exception {
        try {
            AnnotationSpec[] __DSPOT_annotations_19180 = new AnnotationSpec[]{  };
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString116248_failAssert106litString116433__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__6)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertFalse(isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_innerClassInGenericTypelitString116248_failAssert106litString116433__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertFalse(isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString116248_failAssert106litString116433__11)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_16 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "testGenericStringInner";
            Assert.fail("innerClassInGenericTypelitString116248 should have thrown NoSuchMethodException");
            o_innerClassInGenericTypelitString116248_failAssert106litString116433__11.annotated(__DSPOT_annotations_19180);
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv128954() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_rv128954__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_rv128954__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_106 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_106);
        String o_innerGenericInGenericType_rv128954__19 = __DSPOT_invoc_4.toString();
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", o_innerGenericInGenericType_rv128954__19);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128954__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
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
            Assert.fail("innerGenericInGenericTypelitString128915 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.=n4GhZs-JXP%(V6:;;}()", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv128948() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_rv128948__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_rv128948__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_100 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_100);
        TypeName o_innerGenericInGenericType_rv128948__19 = __DSPOT_invoc_4.box();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv128948__19)).toString());
        Assert.assertEquals((-1262750836), ((int) (((ClassName) (o_innerGenericInGenericType_rv128948__19)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128948__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_100);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv128946() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_19253 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_rv128946__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_rv128946__14 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_98 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_98);
        TypeName o_innerGenericInGenericType_rv128946__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_19253);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv128946__20)).toString());
        Assert.assertEquals((-1262750836), ((int) (((ClassName) (o_innerGenericInGenericType_rv128946__20)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__12)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128946__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
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
            Assert.fail("innerGenericInGenericType_rv128955 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv128956() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_rv128956__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_rv128956__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_108 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_108);
        TypeName o_innerGenericInGenericType_rv128956__19 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv128956__19)).toString());
        Assert.assertEquals((-1262750836), ((int) (((ClassName) (o_innerGenericInGenericType_rv128956__19)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv128956__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_108);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg128944() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg128944__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg128944__4)).toString());
        Assert.assertEquals((-1262750836), ((int) (((ClassName) (o_innerGenericInGenericType_mg128944__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_mg128944__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_mg128944__11 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_97 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
        TypeName o_innerGenericInGenericType_mg128944__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__17)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__17)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg128944__4)).toString());
        Assert.assertEquals((-1262750836), ((int) (((ClassName) (o_innerGenericInGenericType_mg128944__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128944__11)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg128934() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_19250 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg128934__5 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg128934__5)).toString());
        Assert.assertEquals((-1262750836), ((int) (((ClassName) (o_innerGenericInGenericType_mg128934__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_mg128934__10 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerGenericInGenericType_mg128934__12 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_87 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
        TypeName o_innerGenericInGenericType_mg128934__18 = genericTypeName.annotated(__DSPOT_annotations_19250);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__18)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__18)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg128934__5)).toString());
        Assert.assertEquals((-1262750836), ((int) (((ClassName) (o_innerGenericInGenericType_mg128934__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).toString());
        Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__10)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg128934__12)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString128916_failAssert124litString129144() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString128916_failAssert124litString129144__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__6)).toString());
            Assert.assertEquals((-1262750836), ((int) (((ClassName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__6)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertFalse(isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_innerGenericInGenericTypelitString128916_failAssert124litString129144__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertFalse(isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__11)).toString());
            Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128916_failAssert124litString129144__11)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("").getGenericReturnType());
            String String_69 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "\n";
            Assert.fail("innerGenericInGenericTypelitString128916 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString128918_failAssert126litString129122_mg136963() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_20175 = Collections.<AnnotationSpec>emptyList();
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString128918_failAssert126litString129122__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__6)).toString());
            Assert.assertEquals((-1262750836), ((int) (((ClassName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__6)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertFalse(isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertFalse(isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11)).toString());
            Assert.assertEquals((-302376430), ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName.get(getClass().getDeclaredMethod(":").getGenericReturnType());
            String String_71 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGenerWic<java.lang.Long>";
            Assert.fail("innerGenericInGenericTypelitString128918 should have thrown NoSuchMethodException");
            o_innerGenericInGenericTypelitString128918_failAssert126litString129122__11.annotated(__DSPOT_annotations_20175);
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_2 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_2);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv142075() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_20913 = new AnnotationSpec[]{  };
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_137 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_137);
        TypeName o_innerStaticInGenericType_rv142075__14 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_20913);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv142075__14)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_rv142075__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_137);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv142085() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_147 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_147);
        TypeName o_innerStaticInGenericType_rv142085__13 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv142085__13)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_rv142085__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_147);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv142077() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_139 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_139);
        TypeName o_innerStaticInGenericType_rv142077__13 = __DSPOT_invoc_4.box();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv142077__13)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_rv142077__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
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
            Assert.fail("innerStaticInGenericType_mg142072 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg142069() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg142069__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142069__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_mg142069__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_132 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_132);
        typeName.isBoxedPrimitive();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg142069__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_mg142069__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_132);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142056_mg144367() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142056__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerStaticInGenericType_add142056__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add142056_mg144367__17 = o_innerStaticInGenericType_add142056__4.box();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056_mg144367__17)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056_mg144367__17)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142056_mg144406() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_21162 = new AnnotationSpec[]{  };
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142056__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerStaticInGenericType_add142056__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add142056_mg144406__18 = typeName.annotated(__DSPOT_annotations_21162);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056_mg144406__18)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056_mg144406__18)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142056_mg144380() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142056__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerStaticInGenericType_add142056__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", String_119);
        TypeName o_innerStaticInGenericType_add142056_mg144380__17 = o_innerStaticInGenericType_add142056__4.withoutAnnotations();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056_mg144380__17)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056_mg144380__17)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
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
            Assert.fail("innerStaticInGenericType_add142056_mg144401 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add142056litString143805() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add142056__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_innerStaticInGenericType_add142056__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
        String String_119 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NesteQdNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NesteQdNonGeneric", String_119);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__4)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add142056__6)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (o_innerStaticInGenericType_add142056__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals((-1020773876), ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add4() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add4__3 = TypeName.get(Object[].class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_add28() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2__3 = TypeName.get(Object[].class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg184() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_6 = new AnnotationSpec[0];
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2__3 = TypeName.get(Object[].class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg184__14 = o_equalsAndHashCodeArrayTypeName_add2__3.annotated(__DSPOT_annotations_6);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg184__14)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg184__14)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add4_mg191() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_10 = Collections.<AnnotationSpec>emptyList();
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add4__3 = TypeName.get(Object[].class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4_mg191__15 = o_equalsAndHashCodeArrayTypeName_add4__2.annotated(__DSPOT_annotations_10);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add3_mg211() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add3__3 = TypeName.get(Object[].class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add3_mg211__13 = o_equalsAndHashCodeArrayTypeName_add3__3.withoutAnnotations();
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg211__13)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3_mg211__13)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add3__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add4_mg191_mg5042() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_452 = new AnnotationSpec[]{  };
        List<AnnotationSpec> __DSPOT_annotations_10 = Collections.<AnnotationSpec>emptyList();
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add4__3 = TypeName.get(Object[].class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add4_mg191__15 = o_equalsAndHashCodeArrayTypeName_add4__2.annotated(__DSPOT_annotations_10);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add4_mg191_mg5042__19 = o_equalsAndHashCodeArrayTypeName_add4__3.annotated(__DSPOT_annotations_452);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191_mg5042__19)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191_mg5042__19)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg191__15)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg181_mg5040() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_4 = Collections.<AnnotationSpec>emptyList();
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__2 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2__3 = TypeName.get(Object[].class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__4 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2_mg181__15 = o_equalsAndHashCodeArrayTypeName_add2__2.annotated(__DSPOT_annotations_4);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg181_mg5040__18 = o_equalsAndHashCodeArrayTypeName_add2_mg181__15.withoutAnnotations();
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg5040__18)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181_mg5040__18)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg181__15)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassName() throws Exception {
        ClassName o_equalsAndHashCodeClassName__1 = ClassName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeClassName__2 = ClassName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeClassName__3 = TypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeClassName__4 = ClassName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeClassName__5 = ClassName.bestGuess("java.lang.Object");
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__5)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeClassName__6 = ClassName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__6)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassName__5)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassName__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
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
            Assert.fail("equalsAndHashCodeClassNamelitString12463 should have thrown IllegalArgumentException");
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
            Assert.fail("equalsAndHashCodeClassNamelitString12459 should have thrown IllegalArgumentException");
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
            Assert.fail("equalsAndHashCodeClassNamelitString12464 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12461() throws Exception {
        ClassName o_equalsAndHashCodeClassNamelitString12461__1 = ClassName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12461__2 = ClassName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeClassNamelitString12461__3 = TypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12461__4 = ClassName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12461__5 = ClassName.bestGuess("java.l%ang.Object");
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.l%ang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).toString());
        Assert.assertEquals((-1662323656), ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12461__6 = ClassName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__6)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.l%ang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).toString());
        Assert.assertEquals((-1662323656), ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12461__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__3 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__3)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName __DSPOT_invoc_4 = ClassName.get(Object.class);
            TypeName o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__7 = TypeName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__7)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__8 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__8)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_rv12691__8)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            Assert.fail("equalsAndHashCodeClassNamelitString12464 should have thrown IllegalArgumentException");
            __DSPOT_invoc_4.enclosingClassName();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__3 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__3)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__4 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__4)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName __DSPOT_invoc_5 = TypeName.get(Object.class);
            ClassName o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__8 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__8)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12466_failAssert5_rv12819__8)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName.bestGuess(":");
            ClassName.get(Object.class);
            Assert.fail("equalsAndHashCodeClassNamelitString12466 should have thrown IllegalArgumentException");
            __DSPOT_invoc_5.isBoxedPrimitive();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12464_failAssert3_add12558_mg23572() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_3213 = Collections.<AnnotationSpec>emptyList();
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__3 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__3)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__4 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__4)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__5 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__5)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6 = TypeName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__7 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__7)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            Assert.fail("equalsAndHashCodeClassNamelitString12464 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12464_failAssert3_add12558__6.annotated(__DSPOT_annotations_3213);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12463_failAssert2_add12572_mg19889() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_2485 = Collections.<AnnotationSpec>emptyList();
            ClassName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__3 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__3)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__4 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__4)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__5 = TypeName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__5)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6 = TypeName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__7 = ClassName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__7)).hashCode())));
            Assert.assertFalse(isPrimitive());
            ClassName.bestGuess("+1!kAF:15u&sdcOg");
            ClassName.get(Object.class);
            Assert.fail("equalsAndHashCodeClassNamelitString12463 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12463_failAssert2_add12572__6.annotated(__DSPOT_annotations_2485);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName__5 = ClassName.get(List.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34677() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34677__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34677__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34677__5 = ClassName.get(List.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34677_mg35257() throws Exception {
        List<TypeName> __DSPOT_typeArguments_5441 = Collections.<TypeName>emptyList();
        String __DSPOT_name_5440 = "js6$O_RR*O>daa`JUe/:";
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34677__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34677__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34677__5 = ClassName.get(List.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34677_mg35257__22 = o_equalsAndHashCodeParameterizedTypeName_add34677__6.nestedClass(__DSPOT_name_5440, __DSPOT_typeArguments_5441);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>.js6$O_RR*O>daa`JUe/:", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677_mg35257__22)).toString());
        Assert.assertEquals(729619466, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677_mg35257__22)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34677__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34677__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34673_mg35218() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_5423 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34673__5 = ClassName.get(List.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673_mg35218__21 = o_equalsAndHashCodeParameterizedTypeName_add34673__3.annotated(__DSPOT_annotations_5423);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35218__21)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35218__21)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34673_mg35199() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_5412 = new AnnotationSpec[0];
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34673__5 = ClassName.get(List.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673_mg35199__20 = o_equalsAndHashCodeParameterizedTypeName_add34673__2.annotated(__DSPOT_annotations_5412);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35199__20)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673_mg35199__20)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34673_add35052() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34673__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34673__5 = ClassName.get(List.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34673__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34673__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34673__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34672_add35020_add36236() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34672__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34672__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34672__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34672__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34672__5 = ClassName.get(List.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34672__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34672__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34672__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableName__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56295() throws Exception {
        Type[] __DSPOT_bounds_9384 = new Type[0];
        TypeName o_equalsAndHashCodeTypeVariableName_mg56295__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56295__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56295__8 = typeVar2.withBounds(__DSPOT_bounds_9384);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56295__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56295__8)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56295__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableNamelitString56277() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableNamelitString56277__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableNamelitString56277__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("\n", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString56277__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56288() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9378 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56288__9 = typeVar1.annotated(__DSPOT_annotations_9378);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56289() throws Exception {
        TypeName[] __DSPOT_bounds_9379 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56289__8 = typeVar1.withBounds(__DSPOT_bounds_9379);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56292() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableName_mg56292__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56292__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56292__7 = typeVar1.withoutAnnotations();
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56292__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56292__7)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56292__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56291_mg58161() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9662 = Collections.<AnnotationSpec>emptyList();
        List<? extends TypeName> __DSPOT_bounds_9381 = Collections.emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg56291__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56291__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56291__9 = typeVar1.withBounds(__DSPOT_bounds_9381);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56291_mg58161__18 = typeVar2.annotated(__DSPOT_annotations_9662);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291_mg58161__18)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291_mg58161__18)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56291__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56291__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56290_mg57780() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_9494 = new AnnotationSpec[0];
        Type[] __DSPOT_bounds_9380 = new Type[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56290__8 = typeVar1.withBounds(__DSPOT_bounds_9380);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290_mg57780__16 = o_equalsAndHashCodeTypeVariableName_mg56290__2.annotated(__DSPOT_annotations_9494);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg57780__16)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg57780__16)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56290_mg58266() throws Exception {
        TypeName[] __DSPOT_bounds_9717 = new TypeName[]{  };
        Type[] __DSPOT_bounds_9380 = new Type[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56290__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56290__8 = typeVar1.withBounds(__DSPOT_bounds_9380);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56290_mg58266__16 = o_equalsAndHashCodeTypeVariableName_mg56290__8.withBounds(__DSPOT_bounds_9717);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg58266__16)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290_mg58266__16)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56290__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56290__8)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56288_mg57828() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9378 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56288__9 = typeVar1.annotated(__DSPOT_annotations_9378);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288_mg57828__16 = o_equalsAndHashCodeTypeVariableName_mg56288__4.box();
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288_mg57828__16)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288_mg57828__16)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56288litString56509() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9378 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56288__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("\n", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56288__9 = typeVar1.annotated(__DSPOT_annotations_9378);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("\n", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56289_mg58291() throws Exception {
        TypeName[] __DSPOT_bounds_9379 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56289__8 = typeVar1.withBounds(__DSPOT_bounds_9379);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg56289_mg58291__15 = o_equalsAndHashCodeTypeVariableName_mg56289__8.withoutAnnotations();
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289_mg58291__15)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289_mg58291__15)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56289__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56289__8)).hashCode())));
        Assert.assertFalse(isPrimitive());
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
            Assert.fail("equalsAndHashCodeTypeVariableName_mg56296_mg57673 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox java.lang.Object", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56288_mg57916_failAssert7_add75680() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_9378 = Collections.<AnnotationSpec>emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg56288__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__3)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg56288__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56288__4)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56288__9 = typeVar1.annotated(__DSPOT_annotations_9378);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56288__9)).hashCode())));
            Assert.assertFalse(isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg56288__4.unbox();
            Assert.fail("equalsAndHashCodeTypeVariableName_mg56288_mg57916 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56296_mg57986_failAssert8_mg75604() throws Exception {
        try {
            Type[] __DSPOT_bounds_12660 = new Type[]{  };
            List<? extends TypeName> __DSPOT_bounds_9385 = Collections.emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeVariableName typeVar2 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar2)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56296__9 = typeVar2.withBounds(__DSPOT_bounds_9385);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).hashCode())));
            Assert.assertFalse(isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg56296__4.unbox();
            Assert.fail("equalsAndHashCodeTypeVariableName_mg56296_mg57986 should have thrown UnsupportedOperationException");
            o_equalsAndHashCodeTypeVariableName_mg56296__9.withBounds(__DSPOT_bounds_12660);
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg56296_mg57673_failAssert9litString75617() throws Exception {
        try {
            List<? extends TypeName> __DSPOT_bounds_9385 = Collections.emptyList();
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__3 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__3)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeName o_equalsAndHashCodeTypeVariableName_mg56296__4 = TypeVariableName.get(Object.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg56296__4)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeVariableName typeVar1 = TypeVariableName.get("T", Comparator.class, Serializable.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
            Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeVariableName typeVar2 = TypeVariableName.get("\n", Comparator.class, Serializable.class);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("\n", ((TypeVariableName) (typeVar2)).toString());
            Assert.assertEquals(10, ((int) (((TypeVariableName) (typeVar2)).hashCode())));
            Assert.assertFalse(isPrimitive());
            TypeVariableName o_equalsAndHashCodeTypeVariableName_mg56296__9 = typeVar2.withBounds(__DSPOT_bounds_9385);
            Assert.assertFalse(isAnnotated());
            Assert.assertFalse(isBoxedPrimitive());
            Assert.assertEquals("\n", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).toString());
            Assert.assertEquals(10, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg56296__9)).hashCode())));
            Assert.assertFalse(isPrimitive());
            o_equalsAndHashCodeTypeVariableName_mg56296__3.unbox();
            Assert.fail("equalsAndHashCodeTypeVariableName_mg56296_mg57673 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__6)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add78647() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78647__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__6)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78647__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add78644_mg79061() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_13278 = Collections.<AnnotationSpec>emptyList();
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644_mg79061__21 = o_equalsAndHashCodeWildcardTypeName_add78644__3.annotated(__DSPOT_annotations_13278);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644_mg79061__21)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644_mg79061__21)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add78644_add78958() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78644__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78644__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_13293 = Collections.<AnnotationSpec>emptyList();
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21 = o_equalsAndHashCodeWildcardTypeName_add78645__6.annotated(__DSPOT_annotations_13293);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).hashCode())));
        Assert.assertFalse(isPrimitive());
        TypeName o_equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706__24 = o_equalsAndHashCodeWildcardTypeName_add78645__6.withoutAnnotations();
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706__24)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094_mg87706__24)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__1)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__2)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__3)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).toString());
        Assert.assertEquals((-454279549), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__4)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__5)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645__6)).hashCode())));
        Assert.assertFalse(isPrimitive());
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertFalse(isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).toString());
        Assert.assertEquals((-620274325), ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add78645_mg79094__21)).hashCode())));
        Assert.assertFalse(isPrimitive());
    }

    private void assertEqualsHashCodeAndToString(TypeName a, TypeName b) {
        Assert.assertEquals(a.toString(), b.toString());
        Truth.assertThat(a.equals(b)).isTrue();
        Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        Assert.assertFalse(a.equals(null));
    }
}

