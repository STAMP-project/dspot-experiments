package net.bytebuddy.dynamic.scaffold.inline;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.dynamic.scaffold.inline.MethodRebaseResolver.Resolution.ForRebasedMethod.of;


@RunWith(Parameterized.class)
public class MethodRebaseResolverResolutionForRebasedMethodTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private final boolean interfaceType;

    private final int rebasedMethodModifiers;

    public MethodRebaseResolverResolutionForRebasedMethodTest(boolean interfaceType, int rebasedMethodModifiers) {
        this.interfaceType = interfaceType;
        this.rebasedMethodModifiers = rebasedMethodModifiers;
    }

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Mock
    private MethodNameTransformer methodNameTransformer;

    @Mock
    private MethodNameTransformer otherMethodNameTransformer;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription returnType;

    @Mock
    private TypeDescription parameterType;

    @Mock
    private TypeDescription.Generic genericReturnType;

    @Mock
    private TypeDescription.Generic genericParameterType;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testPreservation() throws Exception {
        MethodRebaseResolver.Resolution resolution = of(instrumentedType, methodDescription, methodNameTransformer);
        MatcherAssert.assertThat(resolution.isRebased(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getDeclaringType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getInternalName(), CoreMatchers.is(MethodRebaseResolverResolutionForRebasedMethodTest.QUX));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getModifiers(), CoreMatchers.is(rebasedMethodModifiers));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getReturnType(), CoreMatchers.is(genericReturnType));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getParameters(), CoreMatchers.is(((ParameterList<ParameterDescription.InDefinedShape>) (new ParameterList.Explicit.ForTypes(resolution.getResolvedMethod(), parameterType)))));
        StackManipulation.Size size = resolution.getAdditionalArguments().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

