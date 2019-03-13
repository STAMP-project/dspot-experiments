package net.bytebuddy.description.type;


import java.lang.reflect.Type;
import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.parameterizedType;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.rawType;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.UNDEFINED;


public class TypeDescriptionGenericBuilderTest extends AbstractTypeDescriptionGenericTest {
    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test(expected = IllegalArgumentException.class)
    public void testNoOwnerTypeWhenRequired() throws Exception {
        parameterizedType(TypeDescriptionGenericBuilderTest.Foo.Inner.class, Object.class);
    }

    @Test
    public void testImplicitOwnerTypeWhenRequired() throws Exception {
        MatcherAssert.assertThat(parameterizedType(TypeDescriptionGenericBuilderTest.Foo.class, Object.class).build().getOwnerType(), CoreMatchers.is(describe(getClass())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOwnerTypeWhenNotRequired() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(TypeDescriptionGenericBuilderTest.Foo.class, Object.class, Collections.<Type>singletonList(Object.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalOwnerType() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(TypeDescriptionGenericBuilderTest.Foo.Inner.class, Object.class, Collections.<Type>singletonList(TypeDescriptionGenericBuilderTest.Foo.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonGenericOwnerType() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(TypeDescriptionGenericBuilderTest.Foo.Inner.class, TypeDescriptionGenericBuilderTest.Foo.class, Collections.<Type>singletonList(TypeDescriptionGenericBuilderTest.Foo.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenericOwnerType() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(of(TypeDescriptionGenericBuilderTest.Foo.Nested.class), parameterizedType(TypeDescriptionGenericBuilderTest.Foo.class, Object.class).build(), Collections.<TypeDefinition>singletonList(TypeDescription.OBJECT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleParameterTypeNumber() throws Exception {
        parameterizedType(TypeDescriptionGenericBuilderTest.Foo.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForbiddenZeroArity() throws Exception {
        TypeDescription.Generic.Builder.rawType(TypeDescriptionGenericBuilderTest.Foo.class).asArray(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForbiddenNegativeType() throws Exception {
        TypeDescription.Generic.Builder.rawType(TypeDescriptionGenericBuilderTest.Foo.class).asArray((-1));
    }

    @Test
    public void testMultipleArityArray() throws Exception {
        MatcherAssert.assertThat(TypeDescription.Generic.Builder.rawType(TypeDescriptionGenericBuilderTest.Foo.class).asArray(2).build().getComponentType().getComponentType().represents(TypeDescriptionGenericBuilderTest.Foo.class), CoreMatchers.is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotAnnotateVoid() throws Exception {
        TypeDescription.Generic.Builder.rawType(void.class).annotate(Mockito.mock(AnnotationDescription.class)).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonGenericTypeAsParameterizedType() throws Exception {
        parameterizedType(Object.class).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingOwnerType() throws Exception {
        rawType(TypeDescriptionGenericBuilderTest.Bar.Inner.class, UNDEFINED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleType() throws Exception {
        rawType(TypeDescriptionGenericBuilderTest.Bar.Inner.class, OBJECT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleOwnerTypeWhenNonRequired() throws Exception {
        rawType(Object.class, OBJECT);
    }

    @Test
    public void testExplicitOwnerTypeOfNonGenericType() throws Exception {
        TypeDescription.Generic ownerType = TypeDescription.Generic.Builder.rawType(TypeDescriptionGenericBuilderTest.Bar.class).build();
        TypeDescription.Generic typeDescription = rawType(TypeDescriptionGenericBuilderTest.Bar.Inner.class, ownerType).build();
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.represents(TypeDescriptionGenericBuilderTest.Bar.Inner.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getOwnerType(), CoreMatchers.sameInstance(ownerType));
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testTypeAnnotationOwnerType() throws Exception {
        super.testTypeAnnotationOwnerType();
    }

    @SuppressWarnings("unused")
    private static class Foo<T> {
        /* empty */
        private class Inner<S> {}

        /* empty */
        private static class Nested<S> {}
    }

    @SuppressWarnings("unused")
    private class Bar {
        /* empty */
        private class Inner {}
    }
}

