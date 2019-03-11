package net.bytebuddy.description.type;


import java.util.List;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;
import static net.bytebuddy.description.type.TypeDefinition.Sort.VARIABLE_SYMBOLIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;


public class TypeDescriptionGenericVisitorSubstitutorForDetachmentTest {
    private static final String FOO = "foo";

    @Test
    public void testDetachment() throws Exception {
        TypeDescription.Generic original = describe(TypeDescriptionGenericVisitorSubstitutorForDetachmentTest.Foo.Inner.class.getDeclaredField(TypeDescriptionGenericVisitorSubstitutorForDetachmentTest.FOO).getGenericType());
        TypeDescription.Generic detached = original.accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(ElementMatchers.is(TypeDescriptionGenericVisitorSubstitutorForDetachmentTest.Foo.Inner.class)));
        MatcherAssert.assertThat(detached, CoreMatchers.not(CoreMatchers.sameInstance(original)));
        MatcherAssert.assertThat(detached.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(detached.asErasure(), CoreMatchers.is(TargetType.DESCRIPTION));
        MatcherAssert.assertThat(detached.getTypeArguments().size(), CoreMatchers.is(4));
        MatcherAssert.assertThat(detached.getTypeArguments().get(0).getSort(), CoreMatchers.is(VARIABLE_SYMBOLIC));
        MatcherAssert.assertThat(detached.getTypeArguments().get(0).getSymbol(), CoreMatchers.is("T"));
        MatcherAssert.assertThat(detached.getTypeArguments().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(detached.getTypeArguments().get(1).asErasure().represents(String.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(detached.getTypeArguments().get(2).getSort(), CoreMatchers.is(VARIABLE_SYMBOLIC));
        MatcherAssert.assertThat(detached.getTypeArguments().get(2).getSymbol(), CoreMatchers.is("U"));
        MatcherAssert.assertThat(detached.getTypeArguments().get(3).getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(detached.getTypeArguments().get(3).getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(detached.getTypeArguments().get(3).getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE_SYMBOLIC));
        MatcherAssert.assertThat(detached.getTypeArguments().get(3).getTypeArguments().getOnly().getSymbol(), CoreMatchers.is("S"));
        MatcherAssert.assertThat(detached.getOwnerType(), CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(detached.getOwnerType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(detached.getOwnerType().getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(detached.getOwnerType().getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE_SYMBOLIC));
        MatcherAssert.assertThat(detached.getOwnerType().getTypeArguments().getOnly().getSymbol(), CoreMatchers.is("T"));
    }

    @Test(expected = IllegalStateException.class)
    public void testDetachedNoSource() throws Exception {
        TypeDescription.Generic original = describe(TypeDescriptionGenericVisitorSubstitutorForDetachmentTest.Foo.Inner.class.getDeclaredField(TypeDescriptionGenericVisitorSubstitutorForDetachmentTest.FOO).getGenericType());
        TypeDescription.Generic detached = original.accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(ElementMatchers.is(TypeDescriptionGenericVisitorSubstitutorForDetachmentTest.Foo.Inner.class)));
        detached.getTypeArguments().get(0).getTypeVariableSource();
    }

    @SuppressWarnings("unused")
    public static class Foo<O> {
        public abstract class Inner<T, S extends CharSequence, U extends T, V> {
            TypeDescriptionGenericVisitorSubstitutorForDetachmentTest.Foo<T>.Inner<T, String, U, List<S>> foo;
        }
    }

    /* empty */
    @SuppressWarnings("unused")
    public abstract static class Bar<A, T, S, V extends Number> {}
}

