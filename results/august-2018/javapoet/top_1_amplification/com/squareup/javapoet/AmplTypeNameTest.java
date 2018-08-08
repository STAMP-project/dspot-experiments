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
    public void genericType_rv91226() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv91226__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv91226__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91226__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91226__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91226__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91226__4)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv91226__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91226__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91226__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91226__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91226__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91226__13)).isPrimitive());
        TypeName o_genericType_rv91226__15 = __DSPOT_invoc_6.box();
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91226__15)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91226__15)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv91226__15)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv91226__15)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91226__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv91226__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91226__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91226__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91226__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91226__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91226__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91226__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91226__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91226__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91226__13)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv91237() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_13854 = Collections.<AnnotationSpec>emptyList();
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv91237__6 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv91237__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91237__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91237__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91237__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91237__6)).isPrimitive());
        TypeName o_genericType_rv91237__8 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91237__8)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91237__8)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv91237__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv91237__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91237__8)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv91237__17 = __DSPOT_invoc_11.annotated(__DSPOT_annotations_13854);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91237__17)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91237__17)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91237__17)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91237__17)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91237__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv91237__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91237__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91237__6)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91237__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91237__6)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91237__8)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91237__8)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv91237__8)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv91237__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91237__8)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv91212() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_13847 = new AnnotationSpec[]{  };
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName __DSPOT_invoc_4 = TypeName.get(recursiveEnum.getReturnType());
        TypeName o_genericType_rv91212__9 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91212__9)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91212__9)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv91212__9)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv91212__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91212__9)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv91212__14 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91212__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91212__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91212__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91212__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91212__14)).isPrimitive());
        TypeName o_genericType_rv91212__16 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_13847);
        Assert.assertFalse(((ClassName) (o_genericType_rv91212__16)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91212__16)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91212__16)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91212__16)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91212__16)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91212__9)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91212__9)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv91212__9)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv91212__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91212__9)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91212__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91212__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91212__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91212__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91212__14)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv91234() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv91234__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv91234__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91234__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91234__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91234__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91234__4)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv91234__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91234__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91234__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91234__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91234__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91234__13)).isPrimitive());
        TypeName o_genericType_rv91234__15 = __DSPOT_invoc_6.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91234__15)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91234__15)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv91234__15)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv91234__15)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91234__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv91234__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91234__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91234__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91234__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91234__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91234__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91234__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91234__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91234__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91234__13)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv91233_failAssert25() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            __DSPOT_invoc_6.unbox();
            org.junit.Assert.fail("genericType_rv91233 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericType_rv91224() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_13850 = new AnnotationSpec[]{  };
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv91224__5 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv91224__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91224__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91224__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91224__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91224__5)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv91224__14 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91224__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91224__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91224__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91224__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91224__14)).isPrimitive());
        TypeName o_genericType_rv91224__16 = __DSPOT_invoc_6.annotated(__DSPOT_annotations_13850);
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91224__16)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91224__16)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv91224__16)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv91224__16)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91224__16)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv91224__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91224__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91224__5)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91224__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91224__5)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91224__14)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91224__14)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91224__14)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91224__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91224__14)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv91246() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv91246__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv91246__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91246__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91246__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91246__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91246__4)).isPrimitive());
        TypeName o_genericType_rv91246__6 = TypeName.get(recursiveEnum.getGenericReturnType());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91246__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91246__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv91246__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv91246__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91246__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        TypeName o_genericType_rv91246__15 = __DSPOT_invoc_11.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91246__15)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91246__15)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91246__15)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91246__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91246__15)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_genericType_rv91246__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91246__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91246__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91246__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91246__4)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91246__6)).isAnnotated());
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91246__6)).isBoxedPrimitive());
        Assert.assertEquals("E", ((TypeVariableName) (o_genericType_rv91246__6)).toString());
        Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericType_rv91246__6)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_genericType_rv91246__6)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv91232() throws Exception {
        Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName o_genericType_rv91232__4 = TypeName.get(recursiveEnum.getReturnType());
        Assert.assertFalse(((ClassName) (o_genericType_rv91232__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91232__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91232__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91232__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91232__4)).isPrimitive());
        TypeName __DSPOT_invoc_6 = TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        TypeName o_genericType_rv91232__13 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91232__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91232__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91232__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91232__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91232__13)).isPrimitive());
        __DSPOT_invoc_6.toString();
        Assert.assertFalse(((ClassName) (o_genericType_rv91232__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_genericType_rv91232__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericType_rv91232__4)).toString());
        Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericType_rv91232__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_genericType_rv91232__4)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
        Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91232__13)).isAnnotated());
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91232__13)).isBoxedPrimitive());
        Assert.assertEquals("E[]", ((ArrayTypeName) (o_genericType_rv91232__13)).toString());
        Assert.assertEquals(69223, ((int) (((ArrayTypeName) (o_genericType_rv91232__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_genericType_rv91232__13)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void genericType_rv91245_failAssert26() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName.get(recursiveEnum.getReturnType());
            TypeName.get(recursiveEnum.getGenericReturnType());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            TypeName __DSPOT_invoc_11 = TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            __DSPOT_invoc_11.unbox();
            org.junit.Assert.fail("genericType_rv91245 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox E[]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void genericTypelitNum91182_failAssert20litNum91477() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum91182_failAssert20litNum91477__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum91182_failAssert20litNum91477__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum91182_failAssert20litNum91477__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum91182_failAssert20litNum91477__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum91182_failAssert20litNum91477__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericTypelitNum91182_failAssert20litNum91477__6)).isPrimitive());
            TypeName o_genericTypelitNum91182_failAssert20litNum91477__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum91182_failAssert20litNum91477__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum91182_failAssert20litNum91477__8)).isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum91182_failAssert20litNum91477__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum91182_failAssert20litNum91477__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum91182_failAssert20litNum91477__8)).isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[1]);
            TypeName.get(recursiveEnum.getGenericParameterTypes()[Integer.MAX_VALUE]);
            org.junit.Assert.fail("genericTypelitNum91182 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void genericTypelitNum91182_failAssert20_add92070() throws Exception {
        try {
            Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
            TypeName o_genericTypelitNum91182_failAssert20_add92070__6 = TypeName.get(recursiveEnum.getReturnType());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum91182_failAssert20_add92070__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_genericTypelitNum91182_failAssert20_add92070__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum", ((ClassName) (o_genericTypelitNum91182_failAssert20_add92070__6)).toString());
            Assert.assertEquals(398585941, ((int) (((ClassName) (o_genericTypelitNum91182_failAssert20_add92070__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_genericTypelitNum91182_failAssert20_add92070__6)).isPrimitive());
            TypeName o_genericTypelitNum91182_failAssert20_add92070__8 = TypeName.get(recursiveEnum.getGenericReturnType());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum91182_failAssert20_add92070__8)).isAnnotated());
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum91182_failAssert20_add92070__8)).isBoxedPrimitive());
            Assert.assertEquals("E", ((TypeVariableName) (o_genericTypelitNum91182_failAssert20_add92070__8)).toString());
            Assert.assertEquals(69, ((int) (((TypeVariableName) (o_genericTypelitNum91182_failAssert20_add92070__8)).hashCode())));
            Assert.assertFalse(((TypeVariableName) (o_genericTypelitNum91182_failAssert20_add92070__8)).isPrimitive());
            TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isAnnotated());
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Enum[]", ((ArrayTypeName) (genericTypeName)).toString());
            Assert.assertEquals(789002871, ((int) (((ArrayTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ArrayTypeName) (genericTypeName)).isPrimitive());
            TypeName.get(recursiveEnum.getGenericParameterTypes()[Integer.MAX_VALUE]);
            org.junit.Assert.fail("genericTypelitNum91182 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv113874() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_17087 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113874__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113874__14 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).isPrimitive());
        String String_45 = (AmplAmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_45);
        TypeName o_innerClassInGenericType_rv113874__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_17087);
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113874__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113874__20)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv113874__20)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv113874__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113874__20)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__12)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113874__14)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_45);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv113876() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113876__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113876__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).isPrimitive());
        String String_47 = (AmplAmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_47);
        TypeName o_innerClassInGenericType_rv113876__19 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113876__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113876__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv113876__19)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv113876__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113876__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113876__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_47);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv113884() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113884__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).isPrimitive());
        TypeName o_innerClassInGenericType_rv113884__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).isPrimitive());
        String String_55 = (AmplAmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
        TypeName o_innerClassInGenericType_rv113884__19 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113884__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113884__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_rv113884__19)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_rv113884__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_rv113884__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_rv113884__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_55);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg113863() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_17085 = Collections.<AnnotationSpec>emptyList();
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg113863__6 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113863__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113863__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113863__6)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113863__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113863__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113863__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113863__13 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).isPrimitive());
        String String_35 = (AmplAmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_35);
        TypeName o_innerClassInGenericType_mg113863__19 = genericTypeName.annotated(__DSPOT_annotations_17085);
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__19)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__19)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__19)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__19)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113863__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113863__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113863__6)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113863__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113863__6)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113863__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_35);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_rv113883_failAssert102() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            String String_54 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerClassInGenericType_rv113883 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg113872() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg113872__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113872__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113872__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113872__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113872__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113872__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113872__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113872__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).isPrimitive());
        String String_44 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_44);
        TypeName o_innerClassInGenericType_mg113872__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__17)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__17)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__17)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113872__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113872__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113872__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113872__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113872__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113872__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_44);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericType_mg113866() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName o_innerClassInGenericType_mg113866__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113866__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113866__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113866__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113866__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113866__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113866__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).isPrimitive());
        TypeName o_innerClassInGenericType_mg113866__11 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).isPrimitive());
        String String_38 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_38);
        genericTypeName.hashCode();
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113866__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113866__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericType_mg113866__4)).toString());
        Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericType_mg113866__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerClassInGenericType_mg113866__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).toString());
        Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).toString());
        Assert.assertEquals(-1471120843, ((int) (((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericType_mg113866__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", String_38);
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString113845_failAssert99_add114590() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString113845_failAssert99_add114590__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerClassInGenericTypelitString113845_failAssert99_add114590__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__11)).isAnnotated());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__11)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99_add114590__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("\n").getGenericReturnType());
            String String_17 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner";
            org.junit.Assert.fail("innerClassInGenericTypelitString113845 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerClassInGenericTypelitString113845_failAssert99litString114121_rv122130() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_18046 = Collections.<AnnotationSpec>emptyList();
            Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName o_innerClassInGenericTypelitString113845_failAssert99litString114121__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.Inner", ((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__6)).toString());
            Assert.assertEquals(1615782859, ((int) (((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerClassInGenericTypelitString113845_failAssert99litString114121__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner", ((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__11)).toString());
            Assert.assertEquals(409558280, ((int) (((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerClassInGenericTypelitString113845_failAssert99litString114121__11)).isPrimitive());
            TypeName __DSPOT_invoc_62 = TypeName.get(getClass().getDeclaredMethod("\n").getGenericReturnType());
            String String_17 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ":";
            org.junit.Assert.fail("innerClassInGenericTypelitString113845 should have thrown NoSuchMethodException");
            __DSPOT_invoc_62.annotated(__DSPOT_annotations_18046);
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg127249() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg127249__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127249__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127249__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg127249__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg127249__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127249__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg127249__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg127249__11 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).isPrimitive());
        String String_92 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_92);
        genericTypeName.isBoxedPrimitive();
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127249__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127249__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg127249__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg127249__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127249__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127249__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_92);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg127252_failAssert116() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(genericStringInner.getGenericReturnType());
            TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            String String_95 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            genericTypeName.unbox();
            org.junit.Assert.fail("innerGenericInGenericType_mg127252 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg127244() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_18762 = Collections.<AnnotationSpec>emptyList();
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg127244__6 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127244__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127244__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg127244__6)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg127244__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127244__6)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg127244__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg127244__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).isPrimitive());
        String String_87 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
        TypeName o_innerGenericInGenericType_mg127244__19 = genericTypeName.annotated(__DSPOT_annotations_18762);
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__19)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__19)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__19)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__19)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127244__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127244__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg127244__6)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg127244__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127244__6)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127244__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_87);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv127257() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv127257__11 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv127257__13 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).isPrimitive());
        String String_99 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_99);
        TypeName o_innerGenericInGenericType_rv127257__19 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv127257__19)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv127257__19)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv127257__19)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv127257__19)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv127257__19)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__11)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127257__13)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_99);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_mg127253() throws Exception {
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName o_innerGenericInGenericType_mg127253__4 = TypeName.get(genericStringInner.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127253__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127253__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg127253__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg127253__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127253__4)).isPrimitive());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg127253__9 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).isPrimitive());
        TypeName o_innerGenericInGenericType_mg127253__11 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).isPrimitive());
        String String_96 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_96);
        TypeName o_innerGenericInGenericType_mg127253__17 = genericTypeName.withoutAnnotations();
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__17)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__17)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__17)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127253__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127253__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_mg127253__4)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_mg127253__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_mg127253__4)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__9)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_mg127253__11)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_96);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericType_rv127255() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_18764 = new AnnotationSpec[]{  };
        Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName __DSPOT_invoc_4 = TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv127255__12 = TypeName.get(genericStringInner.getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).isPrimitive());
        TypeName o_innerGenericInGenericType_rv127255__14 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).isPrimitive());
        String String_97 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
        TypeName o_innerGenericInGenericType_rv127255__20 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_18764);
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv127255__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv127255__20)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericType_rv127255__20)).toString());
        Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericType_rv127255__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerGenericInGenericType_rv127255__20)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).toString());
        Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__12)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).isAnnotated());
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer>", ((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).toString());
        Assert.assertEquals(1095443440, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericType_rv127255__14)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", String_97);
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString127226_failAssert114_add128029() throws Exception {
        try {
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString127226_failAssert114_add128029__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__6)).toString());
            Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerGenericInGenericTypelitString127226_failAssert114_add128029__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__11)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114_add128029__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("\n").getGenericReturnType());
            String String_69 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>";
            org.junit.Assert.fail("innerGenericInGenericTypelitString127226 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerGenericInGenericTypelitString127226_failAssert114litString127453_mg138677() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_20201 = Collections.<AnnotationSpec>emptyList();
            Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName o_innerGenericInGenericTypelitString127226_failAssert114litString127453__6 = TypeName.get(genericStringInner.getReturnType());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__6)).isBoxedPrimitive());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.InnerGeneric", ((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__6)).toString());
            Assert.assertEquals(-1262750836, ((int) (((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__6)).isPrimitive());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (genericTypeName)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (genericTypeName)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (genericTypeName)).isPrimitive());
            TypeName o_innerGenericInGenericTypelitString127226_failAssert114litString127453__11 = TypeName.get(genericStringInner.getGenericReturnType());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__11)).isBoxedPrimitive());
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__11)).isAnnotated());
            Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long>", ((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__11)).toString());
            Assert.assertEquals(-302376430, ((int) (((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__11)).hashCode())));
            Assert.assertFalse(((ParameterizedTypeName) (o_innerGenericInGenericTypelitString127226_failAssert114litString127453__11)).isPrimitive());
            TypeName.get(getClass().getDeclaredMethod("\n").getGenericReturnType());
            String String_69 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "6#$g3v7QsDI?t(vu:YwR5md[8gL{.]t.2ZV;UI_bG?[LN+";
            org.junit.Assert.fail("innerGenericInGenericTypelitString127226 should have thrown NoSuchMethodException");
            genericTypeName.annotated(__DSPOT_annotations_20201);
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_2 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_2);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg139549() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg139549__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139549__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139549__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg139549__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg139549__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139549__4)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_130 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_130);
        typeName.isAnnotated();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139549__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139549__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg139549__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg139549__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139549__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_130);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv139566() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_146 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_146);
        TypeName o_innerStaticInGenericType_rv139566__13 = __DSPOT_invoc_4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv139566__13)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv139566__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv139566__13)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_rv139566__13)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv139566__13)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_146);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv139556() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_20281 = new AnnotationSpec[]{  };
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_136 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_136);
        TypeName o_innerStaticInGenericType_rv139556__14 = __DSPOT_invoc_4.annotated(__DSPOT_annotations_20281);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv139556__14)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv139556__14)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv139556__14)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_rv139556__14)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv139556__14)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_136);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv139565_failAssert127() throws Exception {
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            String String_145 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_rv139565 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_rv139558() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName __DSPOT_invoc_4 = TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_138 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_138);
        TypeName o_innerStaticInGenericType_rv139558__13 = __DSPOT_invoc_4.box();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv139558__13)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv139558__13)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_rv139558__13)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_rv139558__13)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_rv139558__13)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_138);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add139537_mg141718() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add139537__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add139537__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_118 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_118);
        TypeName o_innerStaticInGenericType_add139537_mg141718__17 = o_innerStaticInGenericType_add139537__4.withoutAnnotations();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537_mg141718__17)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537_mg141718__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537_mg141718__17)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537_mg141718__17)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537_mg141718__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_118);
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add139537_mg141714_failAssert128() throws Exception {
        try {
            Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            TypeName o_innerStaticInGenericType_add139537__4 = TypeName.get(staticInGeneric.getReturnType());
            TypeName o_innerStaticInGenericType_add139537__6 = TypeName.get(staticInGeneric.getReturnType());
            TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
            String String_118 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
            o_innerStaticInGenericType_add139537__4.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_add139537_mg141714 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_mg139544_mg141334() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_20445 = new AnnotationSpec[]{  };
        AnnotationSpec[] __DSPOT_annotations_20278 = new AnnotationSpec[]{  };
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_mg139544__5 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg139544__5)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg139544__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__5)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_125 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_125);
        TypeName o_innerStaticInGenericType_mg139544__12 = typeName.annotated(__DSPOT_annotations_20278);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__12)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg139544__12)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg139544__12)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__12)).isPrimitive());
        TypeName o_innerStaticInGenericType_mg139544_mg141334__18 = typeName.annotated(__DSPOT_annotations_20445);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544_mg141334__18)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544_mg141334__18)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg139544_mg141334__18)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg139544_mg141334__18)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544_mg141334__18)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__5)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg139544__5)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg139544__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__5)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_125);
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__12)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__12)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_mg139544__12)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_mg139544__12)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_mg139544__12)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void innerStaticInGenericType_add139537_mg141730() throws Exception {
        Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName o_innerStaticInGenericType_add139537__4 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isPrimitive());
        TypeName o_innerStaticInGenericType_add139537__6 = TypeName.get(staticInGeneric.getReturnType());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isPrimitive());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        String String_118 = (AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric";
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_118);
        TypeName o_innerStaticInGenericType_add139537_mg141730__17 = o_innerStaticInGenericType_add139537__6.box();
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537_mg141730__17)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537_mg141730__17)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537_mg141730__17)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537_mg141730__17)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537_mg141730__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537__4)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (o_innerStaticInGenericType_add139537__6)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (o_innerStaticInGenericType_add139537__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_innerStaticInGenericType_add139537__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (typeName)).isAnnotated());
        Assert.assertFalse(((ClassName) (typeName)).isBoxedPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", ((ClassName) (typeName)).toString());
        Assert.assertEquals(-1020773876, ((int) (((ClassName) (typeName)).hashCode())));
        Assert.assertFalse(((ClassName) (typeName)).isPrimitive());
        Assert.assertEquals("com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric", String_118);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add3() throws Exception {
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
    public void equalsAndHashCodeArrayTypeName_add3_add92() throws Exception {
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
    public void equalsAndHashCodeArrayTypeName_add4_mg168() throws Exception {
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
        TypeName o_equalsAndHashCodeArrayTypeName_add4_mg168__13 = o_equalsAndHashCodeArrayTypeName_add4__1.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg168__13)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg168__13)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg168__13)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg168__13)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg168__13)).isPrimitive());
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
    public void equalsAndHashCodeArrayTypeName_add4_mg180() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_5 = new AnnotationSpec[]{  };
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
        TypeName o_equalsAndHashCodeArrayTypeName_add4_mg180__14 = o_equalsAndHashCodeArrayTypeName_add4__3.annotated(__DSPOT_annotations_5);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg180__14)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg180__14)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg180__14)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg180__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add4_mg180__14)).isPrimitive());
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
    public void equalsAndHashCodeArrayTypeName_add3_mg199_mg10956_failAssert0() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_16 = Collections.<AnnotationSpec>emptyList();
            ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__1 = ArrayTypeName.of(Object.class);
            ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__2 = ArrayTypeName.of(Object.class);
            TypeName o_equalsAndHashCodeArrayTypeName_add3__3 = TypeName.get(Object[].class);
            ArrayTypeName o_equalsAndHashCodeArrayTypeName_add3__4 = ArrayTypeName.of(Object.class);
            TypeName o_equalsAndHashCodeArrayTypeName_add3_mg199__15 = o_equalsAndHashCodeArrayTypeName_add3__3.annotated(__DSPOT_annotations_16);
            o_equalsAndHashCodeArrayTypeName_add3_mg199__15.unbox();
            org.junit.Assert.fail("equalsAndHashCodeArrayTypeName_add3_mg199_mg10956 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals("cannot unbox java.lang.Object[]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg195_mg10047() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_13 = Collections.<AnnotationSpec>emptyList();
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
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg195__15 = o_equalsAndHashCodeArrayTypeName_add2__3.annotated(__DSPOT_annotations_13);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg195_mg10047__18 = o_equalsAndHashCodeArrayTypeName_add2__3.withoutAnnotations();
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195_mg10047__18)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195_mg10047__18)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195_mg10047__18)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195_mg10047__18)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195_mg10047__18)).isPrimitive());
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
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg195__15)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add2_mg192_mg5765() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_544 = new AnnotationSpec[0];
        AnnotationSpec[] __DSPOT_annotations_11 = new AnnotationSpec[]{  };
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
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg192__14 = o_equalsAndHashCodeArrayTypeName_add2__3.annotated(__DSPOT_annotations_11);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).isPrimitive());
        TypeName o_equalsAndHashCodeArrayTypeName_add2_mg192_mg5765__18 = o_equalsAndHashCodeArrayTypeName_add2_mg192__14.annotated(__DSPOT_annotations_544);
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192_mg5765__18)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192_mg5765__18)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192_mg5765__18)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192_mg5765__18)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192_mg5765__18)).isPrimitive());
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
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).isBoxedPrimitive());
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).isAnnotated());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).toString());
        Assert.assertEquals(183594037, ((int) (((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).hashCode())));
        Assert.assertFalse(((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2_mg192__14)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12346() throws Exception {
        ClassName o_equalsAndHashCodeClassNamelitString12346__1 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12346__2 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).isPrimitive());
        TypeName o_equalsAndHashCodeClassNamelitString12346__3 = TypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12346__4 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12346__5 = ClassName.bestGuess("U-1%h+1!kAF:15u&");
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).isBoxedPrimitive());
        Assert.assertEquals("U-1%h+1!kAF:15u&", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).toString());
        Assert.assertEquals(-1154973058, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12346__6 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__6)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).isBoxedPrimitive());
        Assert.assertEquals("U-1%h+1!kAF:15u&", ((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).toString());
        Assert.assertEquals(-1154973058, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12346__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12347_failAssert2() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12347 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12345_failAssert1() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess("java.lang.bject");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12345 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for java.lang.bject", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12349_failAssert4() throws Exception {
        try {
            ClassName.get(Object.class);
            ClassName.get(Object.class);
            TypeName.get(Object.class);
            ClassName.get(Object.class);
            ClassName.bestGuess(":");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12349 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("couldn\'t make a guess for :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12344() throws Exception {
        ClassName o_equalsAndHashCodeClassNamelitString12344__1 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12344__2 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).isPrimitive());
        TypeName o_equalsAndHashCodeClassNamelitString12344__3 = TypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12344__4 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12344__5 = ClassName.bestGuess("java.lang.Obje?ct");
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Obje?ct", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).toString());
        Assert.assertEquals(-1379586034, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).isPrimitive());
        ClassName o_equalsAndHashCodeClassNamelitString12344__6 = ClassName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__6)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__6)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__6)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__6)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Obje?ct", ((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).toString());
        Assert.assertEquals(-1379586034, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12344__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__4)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__5 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_rv12661__5)).isPrimitive());
            ClassName __DSPOT_invoc_6 = ClassName.get(Object.class);
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12347 should have thrown IllegalArgumentException");
            __DSPOT_invoc_6.enclosingClassName();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527() throws Exception {
        try {
            ClassName o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__3)).isPrimitive());
            ClassName __DSPOT_invoc_4 = ClassName.get(Object.class);
            TypeName o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__7 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__7)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__8 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__8)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__8)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__8)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__8)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12349_failAssert4_rv12527__8)).isPrimitive());
            ClassName.bestGuess(":");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12349 should have thrown IllegalArgumentException");
            __DSPOT_invoc_4.simpleName();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12348_failAssert3_add12449_mg30776() throws Exception {
        try {
            String __DSPOT_name_4616 = "<_>QRgPuT[%@S3cRZ)W5";
            ClassName o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__4)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__5 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__7)).isPrimitive());
            ClassName.bestGuess("\n");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12348 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12348_failAssert3_add12449__3.nestedClass(__DSPOT_name_4616);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12347_failAssert2_add12457_mg27880() throws Exception {
        try {
            List<AnnotationSpec> __DSPOT_annotations_4051 = Collections.<AnnotationSpec>emptyList();
            ClassName o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__4)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__5 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__5)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__6 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__7)).isPrimitive());
            ClassName.bestGuess("");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12347 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12347_failAssert2_add12457__4.annotated(__DSPOT_annotations_4051);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeClassNamelitString12345_failAssert1_add12445_mg19929() throws Exception {
        try {
            String __DSPOT_name_2488 = "m4r:Pnc]J7w5ba<t-!6!";
            ClassName o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__3 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__3)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__3)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__3)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__3)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__3)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__4 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__4)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__4)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__4)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__4)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__4)).isPrimitive());
            TypeName o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__5 = TypeName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__5)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__5)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__5)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__5)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__5)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__6 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__6)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__6)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__6)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__6)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__6)).isPrimitive());
            ClassName o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__7 = ClassName.get(Object.class);
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__7)).isAnnotated());
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__7)).isBoxedPrimitive());
            Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__7)).toString());
            Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__7)).hashCode())));
            Assert.assertFalse(((ClassName) (o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__7)).isPrimitive());
            ClassName.bestGuess("java.lang.bject");
            ClassName.get(Object.class);
            org.junit.Assert.fail("equalsAndHashCodeClassNamelitString12345 should have thrown IllegalArgumentException");
            o_equalsAndHashCodeClassNamelitString12345_failAssert1_add12445__4.peerClass(__DSPOT_name_2488);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34479() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34479__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34479__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34479__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34479__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34479__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34479__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34479__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34479__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34475_mg35109() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_5423 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34475__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34475__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34475__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34475__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34475__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34475__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34475_mg35109__21 = o_equalsAndHashCodeParameterizedTypeName_add34475__5.annotated(__DSPOT_annotations_5423);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35109__21)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35109__21)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35109__21)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35109__21)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35109__21)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34480_mg34855() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_5325 = new AnnotationSpec[]{  };
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34480__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34480__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34480__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34480__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34480__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34480__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34480_mg34855__20 = o_equalsAndHashCodeParameterizedTypeName_add34480__2.annotated(__DSPOT_annotations_5325);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480_mg34855__20)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480_mg34855__20)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480_mg34855__20)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480_mg34855__20)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480_mg34855__20)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34475_mg35122() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_5429 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34475__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34475__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34475__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34475__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34475__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34475__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34475_mg35122__21 = o_equalsAndHashCodeParameterizedTypeName_add34475__6.annotated(__DSPOT_annotations_5429);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35122__21)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35122__21)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35122__21)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35122__21)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475_mg35122__21)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34475__5)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34475__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add34480_add34750_add53210() throws Exception {
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34480__1 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isPrimitive());
        TypeName o_equalsAndHashCodeParameterizedTypeName_add34480__2 = ParameterizedTypeName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34480__3 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34480__4 = ParameterizedTypeName.get(Set.class, UUID.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isPrimitive());
        ClassName o_equalsAndHashCodeParameterizedTypeName_add34480__5 = ClassName.get(List.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isPrimitive());
        ParameterizedTypeName o_equalsAndHashCodeParameterizedTypeName_add34480__6 = ParameterizedTypeName.get(List.class, String.class);
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).isAnnotated());
        Assert.assertEquals("java.util.List<java.lang.String>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).toString());
        Assert.assertEquals(2123584667, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__6)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__2)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__3)).isPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isBoxedPrimitive());
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isAnnotated());
        Assert.assertEquals("java.util.Set<java.util.UUID>", ((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).toString());
        Assert.assertEquals(1113219369, ((int) (((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).hashCode())));
        Assert.assertFalse(((ParameterizedTypeName) (o_equalsAndHashCodeParameterizedTypeName_add34480__4)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isBoxedPrimitive());
        Assert.assertEquals("java.util.List", ((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).toString());
        Assert.assertEquals(65821278, ((int) (((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeParameterizedTypeName_add34480__5)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg55705() throws Exception {
        TypeName[] __DSPOT_bounds_9250 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg55705__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55705__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55705__8 = typeVar1.withBounds(__DSPOT_bounds_9250);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg55706() throws Exception {
        Type[] __DSPOT_bounds_9251 = new Type[0];
        TypeName o_equalsAndHashCodeTypeVariableName_mg55706__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55706__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55706__8 = typeVar1.withBounds(__DSPOT_bounds_9251);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55706__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55706__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55706__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55706__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55706__8)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55706__3)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg55709() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9253 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg55709__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55709__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55709__9 = typeVar2.annotated(__DSPOT_annotations_9253);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg55712() throws Exception {
        List<? extends TypeName> __DSPOT_bounds_9256 = Collections.emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg55712__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55712__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55712__9 = typeVar2.withBounds(__DSPOT_bounds_9256);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55712__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55712__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55712__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55712__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55712__9)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55712__4)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableName_mg55713() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableName_mg55713__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55713__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).isPrimitive());
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
        TypeName o_equalsAndHashCodeTypeVariableName_mg55713__7 = typeVar2.withoutAnnotations();
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55713__7)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55713__7)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55713__7)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55713__7)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55713__7)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55713__2)).isPrimitive());
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
    public void equalsAndHashCodeTypeVariableNamelitString55698() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableNamelitString55698__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableNamelitString55698__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).isPrimitive());
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
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableNamelitString55698__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55704_mg57769() throws Exception {
        Type[] __DSPOT_bounds_9587 = new Type[0];
        List<AnnotationSpec> __DSPOT_annotations_9249 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg55704__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55704__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55704__9 = typeVar1.annotated(__DSPOT_annotations_9249);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55704_mg57769__17 = typeVar2.withBounds(__DSPOT_bounds_9587);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704_mg57769__17)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704_mg57769__17)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704_mg57769__17)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704_mg57769__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704_mg57769__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55704__4)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55704__9)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_add55701_add56401() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableName_add55701__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_add55701__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).isPrimitive());
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
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55701__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55705_mg57533() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_9455 = Collections.<AnnotationSpec>emptyList();
        TypeName[] __DSPOT_bounds_9250 = new TypeName[]{  };
        TypeName o_equalsAndHashCodeTypeVariableName_mg55705__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55705__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55705__8 = typeVar1.withBounds(__DSPOT_bounds_9250);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).isPrimitive());
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55705_mg57533__17 = o_equalsAndHashCodeTypeVariableName_mg55705__8.annotated(__DSPOT_annotations_9455);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705_mg57533__17)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705_mg57533__17)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705_mg57533__17)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705_mg57533__17)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705_mg57533__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__2)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55705__3)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55705__8)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_mg55709_mg57204() throws Exception {
        AnnotationSpec[] __DSPOT_annotations_9346 = new AnnotationSpec[]{  };
        List<AnnotationSpec> __DSPOT_annotations_9253 = Collections.<AnnotationSpec>emptyList();
        TypeName o_equalsAndHashCodeTypeVariableName_mg55709__3 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55709__4 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isPrimitive());
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
        TypeVariableName o_equalsAndHashCodeTypeVariableName_mg55709__9 = typeVar2.annotated(__DSPOT_annotations_9253);
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_mg55709_mg57204__17 = o_equalsAndHashCodeTypeVariableName_mg55709__3.annotated(__DSPOT_annotations_9346);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709_mg57204__17)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709_mg57204__17)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709_mg57204__17)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709_mg57204__17)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709_mg57204__17)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__3)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_mg55709__4)).isPrimitive());
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
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (o_equalsAndHashCodeTypeVariableName_mg55709__9)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_add55700_add56419_add62202() throws Exception {
        TypeName o_equalsAndHashCodeTypeVariableName_add55700__1 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).isPrimitive());
        TypeName o_equalsAndHashCodeTypeVariableName_add55700__2 = TypeVariableName.get(Object.class);
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).isPrimitive());
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
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__1)).isPrimitive());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).isAnnotated());
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object", ((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).toString());
        Assert.assertEquals(1063877011, ((int) (((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).hashCode())));
        Assert.assertFalse(((ClassName) (o_equalsAndHashCodeTypeVariableName_add55700__2)).isPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isBoxedPrimitive());
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isAnnotated());
        Assert.assertEquals("T", ((TypeVariableName) (typeVar1)).toString());
        Assert.assertEquals(84, ((int) (((TypeVariableName) (typeVar1)).hashCode())));
        Assert.assertFalse(((TypeVariableName) (typeVar1)).isPrimitive());
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
    public void equalsAndHashCodeWildcardTypeName_add76397() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add76397_mg76814() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_12736 = Collections.<AnnotationSpec>emptyList();
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76397_mg76814__21 = o_equalsAndHashCodeWildcardTypeName_add76397__2.annotated(__DSPOT_annotations_12736);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397_mg76814__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397_mg76814__21)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397_mg76814__21)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397_mg76814__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397_mg76814__21)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__5)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76397__6)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add76395_add76550() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76395__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76395__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76395__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76395__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76395__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76395__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76395__5)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add76394_mg76834_mg82304() throws Exception {
        List<AnnotationSpec> __DSPOT_annotations_13138 = Collections.<AnnotationSpec>emptyList();
        List<AnnotationSpec> __DSPOT_annotations_12746 = Collections.<AnnotationSpec>emptyList();
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76394__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76394__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76394__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76394__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76394__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76394__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21 = o_equalsAndHashCodeWildcardTypeName_add76394__5.annotated(__DSPOT_annotations_12746);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76394_mg76834_mg82304__26 = o_equalsAndHashCodeWildcardTypeName_add76394__5.annotated(__DSPOT_annotations_13138);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834_mg82304__26)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834_mg82304__26)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834_mg82304__26)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834_mg82304__26)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834_mg82304__26)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__5)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76394_mg76834__21)).isPrimitive());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add76393_add76542_add80214() throws Exception {
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76393__1 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76393__2 = WildcardTypeName.subtypeOf(Object.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76393__3 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76393__4 = WildcardTypeName.subtypeOf(Serializable.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76393__5 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).isPrimitive());
        WildcardTypeName o_equalsAndHashCodeWildcardTypeName_add76393__6 = WildcardTypeName.supertypeOf(String.class);
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__6)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__6)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__6)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__6)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__6)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__1)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).isAnnotated());
        Assert.assertEquals("?", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).toString());
        Assert.assertEquals(63, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__2)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__3)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).isAnnotated());
        Assert.assertEquals("? extends java.io.Serializable", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).toString());
        Assert.assertEquals(-454279549, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__4)).isPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).isBoxedPrimitive());
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).isAnnotated());
        Assert.assertEquals("? super java.lang.String", ((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).toString());
        Assert.assertEquals(-620274325, ((int) (((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).hashCode())));
        Assert.assertFalse(((WildcardTypeName) (o_equalsAndHashCodeWildcardTypeName_add76393__5)).isPrimitive());
    }

    private void assertEqualsHashCodeAndToString(TypeName a, TypeName b) {
        Assert.assertEquals(a.toString(), b.toString());
        Truth.assertThat(a.equals(b)).isTrue();
        Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        Assert.assertFalse(a.equals(null));
    }
}

