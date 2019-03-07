package com.squareup.javapoet;


import com.google.common.truth.Truth;
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

    @Test
    public void arrayType() {
        assertEqualsHashCodeAndToString(ArrayTypeName.of(Object.class), ArrayTypeName.of(Object.class));
    }

    @Test(timeout = 10000)
    public void arrayType1() throws Exception {
        ArrayTypeName o_equalsAndHashCodeArrayTypeName_add2__1 = ArrayTypeName.of(Object.class);
        Assert.assertFalse(isBoxedPrimitive());
        Assert.assertEquals("java.lang.Object[]", ((ArrayTypeName) (o_equalsAndHashCodeArrayTypeName_add2__1)).toString());
        Assert.assertFalse(isPrimitive());
    }

    private void assertEqualsHashCodeAndToString(TypeName a, TypeName b) {
        Assert.assertEquals(a.toString(), b.toString());
        Truth.assertThat(a.equals(b)).isTrue();
        Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        Assert.assertFalse(a.equals(null));
    }
}

