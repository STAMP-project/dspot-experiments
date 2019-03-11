package net.bytebuddy.description.type;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;
import static net.bytebuddy.description.type.TypeDefinition.Sort.VARIABLE;


public class TypeDescriptionGenericOfParameterizedTypeForGenerifiedErasureTest {
    @Test
    public void testNonGenerifiedType() throws Exception {
        TypeDescription.Generic typeDescription = TypeDescription.Generic.OfParameterizedType.ForGenerifiedErasure.of(TypeDescription.OBJECT);
        Assert.assertThat(typeDescription.getSort(), CoreMatchers.is(NON_GENERIC));
    }

    @Test
    public void testGenerifiedType() throws Exception {
        TypeDescription.Generic typeDescription = TypeDescription.Generic.OfParameterizedType.ForGenerifiedErasure.of(of(TypeDescriptionGenericOfParameterizedTypeForGenerifiedErasureTest.Foo.class));
        Assert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        Assert.assertThat(typeDescription.getTypeArguments().getOnly().getSymbol(), CoreMatchers.is("T"));
    }

    /* empty */
    public static class Foo<T> {}
}

