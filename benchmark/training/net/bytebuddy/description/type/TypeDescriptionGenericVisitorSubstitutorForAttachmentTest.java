package net.bytebuddy.description.type;


import java.util.List;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;
import static net.bytebuddy.description.type.TypeDefinition.Sort.VARIABLE;
import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;


public class TypeDescriptionGenericVisitorSubstitutorForAttachmentTest {
    private static final String FOO = "foo";

    @Test
    public void testAttachment() throws Exception {
        TypeDescription.Generic original = describe(TypeDescriptionGenericVisitorSubstitutorForAttachmentTest.Foo.Inner.class.getDeclaredField(TypeDescriptionGenericVisitorSubstitutorForAttachmentTest.FOO).getGenericType());
        TypeDescription.Generic detached = original.accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(ElementMatchers.is(TypeDescriptionGenericVisitorSubstitutorForAttachmentTest.Foo.Inner.class)));
        TypeDescription target = of(TypeDescriptionGenericVisitorSubstitutorForAttachmentTest.Bar.class);
        TypeDescription.Generic attached = detached.accept(new TypeDescription.Generic.Visitor.Substitutor.ForAttachment(target.asGenericType(), target));
        MatcherAssert.assertThat(attached.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(attached.asErasure(), CoreMatchers.is(target));
        MatcherAssert.assertThat(attached.getTypeArguments().size(), CoreMatchers.is(4));
        MatcherAssert.assertThat(attached.getTypeArguments().get(0).getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(attached.getTypeArguments().get(0).getSymbol(), CoreMatchers.is("T"));
        MatcherAssert.assertThat(attached.getTypeArguments().get(0), CoreMatchers.is(target.getTypeVariables().filter(ElementMatchers.named("T")).getOnly()));
        MatcherAssert.assertThat(attached.getTypeArguments().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(attached.getTypeArguments().get(1).asErasure().represents(String.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(attached.getTypeArguments().get(2).getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(attached.getTypeArguments().get(2).getSymbol(), CoreMatchers.is("U"));
        MatcherAssert.assertThat(attached.getTypeArguments().get(2), CoreMatchers.is(target.getTypeVariables().filter(ElementMatchers.named("U")).getOnly()));
        MatcherAssert.assertThat(attached.getTypeArguments().get(3).getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(attached.getTypeArguments().get(3).asErasure().represents(List.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(attached.getTypeArguments().get(3).getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(attached.getTypeArguments().get(3).getTypeArguments().getOnly(), CoreMatchers.is(target.getTypeVariables().filter(ElementMatchers.named("S")).getOnly()));
        MatcherAssert.assertThat(attached.getOwnerType(), CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(attached.getOwnerType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(attached.getOwnerType().getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(attached.getOwnerType().getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(attached.getOwnerType().getTypeArguments().getOnly().getSymbol(), CoreMatchers.is("T"));
        MatcherAssert.assertThat(attached.getOwnerType().getTypeArguments().getOnly(), CoreMatchers.is(target.getTypeVariables().filter(ElementMatchers.named("T")).getOnly()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalAttachment() throws Exception {
        TypeDescription.Generic original = describe(TypeDescriptionGenericVisitorSubstitutorForAttachmentTest.Foo.Inner.class.getDeclaredField(TypeDescriptionGenericVisitorSubstitutorForAttachmentTest.FOO).getGenericType());
        TypeDescription.Generic detached = original.accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(ElementMatchers.is(TypeDescriptionGenericVisitorSubstitutorForAttachmentTest.Foo.Inner.class)));
        detached.accept(new TypeDescription.Generic.Visitor.Substitutor.ForAttachment(OBJECT, TypeDescription.OBJECT));
    }

    @SuppressWarnings("unused")
    public static class Foo<O> {
        public abstract class Inner<T, S extends CharSequence, U extends T, V> {
            TypeDescriptionGenericVisitorSubstitutorForAttachmentTest.Foo<T>.Inner<T, String, U, List<S>> foo;
        }
    }

    /* empty */
    @SuppressWarnings("unused")
    public abstract static class Bar<U, T, S, V extends Number> {}
}

