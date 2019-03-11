package net.bytebuddy.description.method;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Reducing.<init>;


public class MethodDescriptionTokenTest {
    private static final String FOO = "foo";

    private static final int MODIFIERS = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic returnType;

    @Mock
    private TypeDescription.Generic visitedReturnType;

    @Mock
    private TypeDescription.Generic exceptionType;

    @Mock
    private TypeDescription.Generic visitedExceptionType;

    @Mock
    private TypeDescription.Generic parameterType;

    @Mock
    private TypeDescription.Generic receiverType;

    @Mock
    private TypeDescription.Generic visitedReceiverType;

    @Mock
    private ParameterDescription.Token parameterToken;

    @Mock
    private ParameterDescription.Token visitedParameterToken;

    @Mock
    private TypeVariableToken typeVariableToken;

    @Mock
    private TypeVariableToken visitedTypeVariableToken;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription rawReturnType;

    @Mock
    private TypeDescription rawParameterType;

    @Mock
    private AnnotationDescription annotation;

    @Mock
    private AnnotationValue<?, ?> defaultValue;

    @Mock
    private TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

    @Test
    @SuppressWarnings("unchecked")
    public void testProperties() throws Exception {
        MethodDescription.Token token = new MethodDescription.Token(MethodDescriptionTokenTest.FOO, MethodDescriptionTokenTest.MODIFIERS, Collections.singletonList(typeVariableToken), returnType, Collections.singletonList(parameterToken), Collections.singletonList(exceptionType), Collections.singletonList(annotation), defaultValue, receiverType);
        MatcherAssert.assertThat(token.getName(), CoreMatchers.is(MethodDescriptionTokenTest.FOO));
        MatcherAssert.assertThat(token.getModifiers(), CoreMatchers.is(MethodDescriptionTokenTest.MODIFIERS));
        MatcherAssert.assertThat(token.getTypeVariableTokens(), CoreMatchers.is(Collections.singletonList(typeVariableToken)));
        MatcherAssert.assertThat(token.getReturnType(), CoreMatchers.is(returnType));
        MatcherAssert.assertThat(token.getParameterTokens(), CoreMatchers.is(Collections.singletonList(parameterToken)));
        MatcherAssert.assertThat(token.getExceptionTypes(), CoreMatchers.is(Collections.singletonList(exceptionType)));
        MatcherAssert.assertThat(token.getAnnotations(), CoreMatchers.is(Collections.singletonList(annotation)));
        MatcherAssert.assertThat(token.getDefaultValue(), CoreMatchers.is(((AnnotationValue) (defaultValue))));
        MatcherAssert.assertThat(token.getReceiverType(), CoreMatchers.is(receiverType));
    }

    @Test
    public void testVisitor() throws Exception {
        MatcherAssert.assertThat(new MethodDescription.Token(MethodDescriptionTokenTest.FOO, MethodDescriptionTokenTest.MODIFIERS, Collections.singletonList(typeVariableToken), returnType, Collections.singletonList(parameterToken), Collections.singletonList(exceptionType), Collections.singletonList(annotation), defaultValue, receiverType).accept(visitor), CoreMatchers.is(new MethodDescription.Token(MethodDescriptionTokenTest.FOO, MethodDescriptionTokenTest.MODIFIERS, Collections.singletonList(visitedTypeVariableToken), visitedReturnType, Collections.singletonList(visitedParameterToken), Collections.singletonList(visitedExceptionType), Collections.singletonList(annotation), defaultValue, visitedReceiverType)));
    }

    @Test
    public void testSignatureTokenTransformation() throws Exception {
        Mockito.when(returnType.accept(FieldByFieldComparison.matchesPrototype(new TypeDescription.Generic.Visitor.Reducing(typeDescription, typeVariableToken)))).thenReturn(rawReturnType);
        Mockito.when(parameterToken.getType()).thenReturn(parameterType);
        Mockito.when(parameterType.accept(FieldByFieldComparison.matchesPrototype(new TypeDescription.Generic.Visitor.Reducing(typeDescription, typeVariableToken)))).thenReturn(rawParameterType);
        MatcherAssert.assertThat(new MethodDescription.Token(MethodDescriptionTokenTest.FOO, MethodDescriptionTokenTest.MODIFIERS, Collections.singletonList(typeVariableToken), returnType, Collections.singletonList(parameterToken), Collections.singletonList(exceptionType), Collections.singletonList(annotation), defaultValue, receiverType).asSignatureToken(typeDescription), CoreMatchers.is(new MethodDescription.SignatureToken(MethodDescriptionTokenTest.FOO, rawReturnType, Collections.singletonList(rawParameterType))));
    }
}

