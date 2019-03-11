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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;
import static net.bytebuddy.dynamic.scaffold.inline.MethodRebaseResolver.Resolution.ForRebasedConstructor.of;


public class MethodRebaseResolverResolutionForRebasedConstructorTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Mock
    private StackManipulation stackManipulation;

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private TypeDescription.Generic parameterType;

    @Mock
    private TypeDescription.Generic placeholderType;

    @Mock
    private TypeDescription.Generic returnType;

    @Mock
    private TypeDescription rawTypeDescription;

    @Mock
    private TypeDescription rawParameterType;

    @Mock
    private TypeDescription rawReturnType;

    @Mock
    private TypeDescription otherPlaceHolderType;

    @Mock
    private TypeDescription rawPlaceholderType;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testPreservation() throws Exception {
        MethodRebaseResolver.Resolution resolution = of(methodDescription, rawPlaceholderType);
        MatcherAssert.assertThat(resolution.isRebased(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getDeclaringType(), CoreMatchers.is(rawTypeDescription));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getInternalName(), CoreMatchers.is(MethodDescription.CONSTRUCTOR_INTERNAL_NAME));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getModifiers(), CoreMatchers.is(((Opcodes.ACC_SYNTHETIC) | (Opcodes.ACC_PRIVATE))));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getReturnType(), CoreMatchers.is(VOID));
        MatcherAssert.assertThat(resolution.getResolvedMethod().getParameters(), CoreMatchers.is(((ParameterList<ParameterDescription.InDefinedShape>) (new ParameterList.Explicit.ForTypes(resolution.getResolvedMethod(), parameterType, placeholderType)))));
        StackManipulation.Size size = resolution.getAdditionalArguments().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitInsn(Opcodes.ACONST_NULL);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

