package net.bytebuddy.description.type;


import java.util.List;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;


public class TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Test
    public void testNonGenerifiedType() throws Exception {
        TypeDescription.Generic typeDescription = TypeDescription.Generic.OfNonGenericType.ForReifiedErasure.of(TypeDescription.OBJECT);
        Assert.assertThat(typeDescription.getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSort(), CoreMatchers.not(CoreMatchers.instanceOf(TypeDescription.Generic.OfNonGenericType.ForReifiedErasure.class)));
    }

    @Test
    public void testGenerifiedType() throws Exception {
        TypeDescription.Generic typeDescription = TypeDescription.Generic.OfNonGenericType.ForReifiedErasure.of(of(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Qux.class));
        Assert.assertThat(typeDescription.getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Qux.class)))));
        Assert.assertThat(typeDescription.getDeclaredFields().getOnly().getType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getDeclaredFields().getOnly().getType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.QUX)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.QUX)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(List.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getSuperClass().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Foo.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredFields().getOnly().getType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredFields().getOnly().getType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.FOO)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.FOO)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(List.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Bar.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.FOO)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.FOO)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(List.class)))));
    }

    @Test
    public void testNonGenericIntermediateType() throws Exception {
        TypeDescription.Generic typeDescription = TypeDescription.Generic.OfNonGenericType.ForReifiedErasure.of(of(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.GenericIntermediate.class)).getSuperClass();
        Assert.assertThat(typeDescription.getSuperClass().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getSuperClass().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Foo.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredFields().getOnly().getType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredFields().getOnly().getType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.FOO)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.FOO)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(List.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Bar.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.FOO)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.FOO)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.BAR)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(List.class)))));
    }

    private static class Foo<T> {
        T foo;

        public T foo() {
            return null;
        }

        public List<?> bar() {
            return null;
        }
    }

    private interface Bar<T> {
        T foo();

        List<?> bar();
    }

    private static class Qux<T extends Number> extends TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Foo<T> implements TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Bar<T> {
        T foo;

        public T qux() {
            return null;
        }

        public List<?> bar() {
            return null;
        }
    }

    /* empty */
    private static class NonGenericIntermediate extends TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Foo<Number> implements TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.Bar<Number> {}

    /* empty */
    private static class GenericIntermediate<T> extends TypeDescriptionGenericOfNonGenericTypeForReifiedErasureTest.NonGenericIntermediate {}
}

