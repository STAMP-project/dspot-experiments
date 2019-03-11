package net.bytebuddy.description.type;


import java.util.List;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;


public class TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testSuperType() throws Exception {
        TypeDescription.Generic typeDescription = new TypeDescription.Generic.OfParameterizedType.ForReifiedType(of(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Sample.class).getSuperClass());
        Assert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Bar.class)))));
        Assert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getSuperClass().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Foo.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredFields().getOnly().getType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredFields().getOnly().getType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.FOO)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.FOO)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.BAR)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.BAR)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(List.class)))));
    }

    @Test
    public void testInterfaceType() throws Exception {
        TypeDescription.Generic typeDescription = new TypeDescription.Generic.OfParameterizedType.ForReifiedType(of(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Sample.class).getInterfaces().getOnly());
        Assert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Baz.class)))));
        Assert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Qux.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.FOO)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.FOO)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.BAR)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.BAR)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(List.class)))));
    }

    @Test
    public void testNonGenericIntermediateType() throws Exception {
        TypeDescription.Generic typeDescription = TypeDescription.Generic.OfNonGenericType.ForReifiedErasure.of(of(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.NonGenericSample.class)).getSuperClass().getSuperClass();
        Assert.assertThat(typeDescription.getSuperClass().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getSuperClass().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Foo.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredFields().getOnly().getType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredFields().getOnly().getType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.FOO)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.FOO)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.BAR)).getOnly().getReturnType().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.BAR)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(List.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Qux.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().size(), CoreMatchers.is(1));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getTypeArguments().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.FOO)).getOnly().getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.FOO)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Number.class)))));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.BAR)).getOnly().getReturnType().getSort(), CoreMatchers.is(PARAMETERIZED));
        Assert.assertThat(typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.BAR)).getOnly().getReturnType().asErasure(), CoreMatchers.is(((TypeDescription) (of(List.class)))));
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

    /* empty */
    private static class Bar<T extends Number> extends TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Foo<T> {}

    private interface Qux<T> {
        T foo();

        List<?> bar();
    }

    /* empty */
    private interface Baz<T extends Number> extends TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Qux<T> {}

    /* empty */
    private abstract static class Sample extends TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Bar<Number> implements TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Baz<Number> {}

    /* empty */
    private class NonGenericIntermediate extends TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Foo<Number> implements TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.Qux<Number> {}

    /* empty */
    private class RawTypeIntermediate<T> extends TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.NonGenericIntermediate {}

    /* empty */
    private class NonGenericSample extends TypeDescriptionGenericOfParameterizedTypeForReifiedTypeTest.RawTypeIntermediate<Number> {}
}

