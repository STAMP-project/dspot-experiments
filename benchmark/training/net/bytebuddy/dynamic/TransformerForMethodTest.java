package net.bytebuddy.dynamic;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;
import static net.bytebuddy.dynamic.Transformer.ForMethod.withModifiers;


public class TransformerForMethodTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final int MODIFIERS = 42;

    private static final int RANGE = 3;

    private static final int MASK = 1;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private TypeDescription rawDeclaringType;

    @Mock
    private TypeDescription rawReturnType;

    @Mock
    private TypeDescription rawParameterType;

    @Mock
    private Transformer<MethodDescription.Token> tokenTransformer;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private MethodDescription.InDefinedShape definedMethod;

    @Mock
    private MethodDescription.Token methodToken;

    @Mock
    private ParameterDescription.Token parameterToken;

    @Mock
    private ParameterDescription.InDefinedShape definedParameter;

    @Mock
    private TypeDescription.Generic returnType;

    @Mock
    private TypeDescription.Generic typeVariableBound;

    @Mock
    private TypeDescription.Generic parameterType;

    @Mock
    private TypeDescription.Generic exceptionType;

    @Mock
    private TypeDescription.Generic declaringType;

    @Mock
    private AnnotationDescription methodAnnotation;

    @Mock
    private AnnotationDescription parameterAnnotation;

    @Mock
    private ModifierContributor.ForMethod modifierContributor;

    @Test
    public void testSimpleTransformation() throws Exception {
        Mockito.when(tokenTransformer.transform(instrumentedType, methodToken)).thenReturn(methodToken);
        MethodDescription transformed = new Transformer.ForMethod(tokenTransformer).transform(instrumentedType, methodDescription);
        MatcherAssert.assertThat(transformed.getDeclaringType(), CoreMatchers.is(((TypeDefinition) (declaringType))));
        MatcherAssert.assertThat(transformed.getInternalName(), CoreMatchers.is(TransformerForMethodTest.FOO));
        MatcherAssert.assertThat(transformed.getModifiers(), CoreMatchers.is(TransformerForMethodTest.MODIFIERS));
        MatcherAssert.assertThat(transformed.getReturnType(), CoreMatchers.is(returnType));
        MatcherAssert.assertThat(transformed.getTypeVariables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(transformed.getTypeVariables().getOnly().getSymbol(), CoreMatchers.is(TransformerForMethodTest.QUX));
        MatcherAssert.assertThat(transformed.getExceptionTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(transformed.getExceptionTypes().getOnly(), CoreMatchers.is(exceptionType));
        MatcherAssert.assertThat(transformed.getDeclaredAnnotations(), CoreMatchers.is(Collections.singletonList(methodAnnotation)));
        MatcherAssert.assertThat(transformed.getParameters().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(transformed.getParameters().getOnly().getType(), CoreMatchers.is(parameterType));
        MatcherAssert.assertThat(transformed.getParameters().getOnly().getName(), CoreMatchers.is(TransformerForMethodTest.BAR));
        MatcherAssert.assertThat(transformed.getParameters().getOnly().getModifiers(), CoreMatchers.is(((TransformerForMethodTest.MODIFIERS) * 2)));
        MatcherAssert.assertThat(transformed.getParameters().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(transformed.getParameters().getOnly().getDeclaredAnnotations().getOnly(), CoreMatchers.is(parameterAnnotation));
        MatcherAssert.assertThat(transformed.getParameters().getOnly().asDefined(), CoreMatchers.is(definedParameter));
        MatcherAssert.assertThat(transformed.getParameters().getOnly().getDeclaredAnnotations(), CoreMatchers.is(Collections.singletonList(parameterAnnotation)));
        MatcherAssert.assertThat(transformed.getParameters().getOnly().getDeclaringMethod(), CoreMatchers.is(transformed));
        MatcherAssert.assertThat(transformed.asDefined(), CoreMatchers.is(definedMethod));
    }

    @Test
    public void testModifierTransformation() throws Exception {
        MethodDescription.Token transformed = new Transformer.ForMethod.MethodModifierTransformer(ModifierContributor.Resolver.of(modifierContributor)).transform(instrumentedType, methodToken);
        MatcherAssert.assertThat(transformed.getName(), CoreMatchers.is(TransformerForMethodTest.FOO));
        MatcherAssert.assertThat(transformed.getModifiers(), CoreMatchers.is((((TransformerForMethodTest.MODIFIERS) & (~(TransformerForMethodTest.RANGE))) | (TransformerForMethodTest.MASK))));
        MatcherAssert.assertThat(transformed.getReturnType(), CoreMatchers.is(returnType));
        MatcherAssert.assertThat(transformed.getTypeVariableTokens().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(transformed.getTypeVariableTokens().get(0), CoreMatchers.is(new TypeVariableToken(TransformerForMethodTest.QUX, Collections.singletonList(typeVariableBound))));
        MatcherAssert.assertThat(transformed.getExceptionTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(transformed.getExceptionTypes().getOnly(), CoreMatchers.is(exceptionType));
        MatcherAssert.assertThat(transformed.getParameterTokens().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(transformed.getParameterTokens().getOnly(), CoreMatchers.is(parameterToken));
    }

    @Test
    public void testNoChangesUnlessSpecified() throws Exception {
        TypeDescription typeDescription = of(TransformerForMethodTest.Bar.class);
        MethodDescription methodDescription = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TransformerForMethodTest.FOO)).getOnly();
        MethodDescription transformed = withModifiers().transform(typeDescription, methodDescription);
        MatcherAssert.assertThat(transformed, CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(transformed.getModifiers(), CoreMatchers.is(methodDescription.getModifiers()));
    }

    @Test
    public void testRetainsInstrumentedType() throws Exception {
        TypeDescription typeDescription = of(TransformerForMethodTest.Bar.class);
        MethodDescription methodDescription = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TransformerForMethodTest.BAR)).getOnly();
        MethodDescription transformed = withModifiers().transform(typeDescription, methodDescription);
        MatcherAssert.assertThat(transformed, CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(transformed.getModifiers(), CoreMatchers.is(methodDescription.getModifiers()));
        MatcherAssert.assertThat(transformed.getReturnType().asErasure(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(transformed.getReturnType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(transformed.getReturnType().getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(transformed.getReturnType().getTypeArguments().getOnly(), CoreMatchers.is(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.named(TransformerForMethodTest.FOO)).getOnly().getReturnType()));
    }

    private static class Foo<T> {
        T foo() {
            return null;
        }

        TransformerForMethodTest.Bar<T> bar() {
            return null;
        }
    }

    /* empty */
    private static class Bar<S> extends TransformerForMethodTest.Foo<S> {}
}

