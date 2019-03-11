package net.bytebuddy.implementation;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
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

import static net.bytebuddy.implementation.Implementation.Target.AbstractBase.DefaultMethodInvocation.DISABLED;


public abstract class AbstractImplementationTargetTest {
    protected static final String FOO = "foo";

    protected static final String QUX = "qux";

    protected static final String BAZ = "baz";

    protected static final String QUXBAZ = "quxbaz";

    protected static final String FOOBAZ = "foobaz";

    protected static final String BAZBAR = "bazbar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    protected MethodGraph.Linked methodGraph;

    @Mock
    protected MethodGraph.Linked superGraph;

    @Mock
    protected MethodGraph.Linked defaultGraph;

    @Mock
    protected TypeDescription instrumentedType;

    @Mock
    protected TypeDescription methodDeclaringType;

    @Mock
    protected TypeDescription returnType;

    @Mock
    protected TypeDescription defaultMethodDeclaringType;

    @Mock
    protected TypeDescription.Generic genericInstrumentedType;

    @Mock
    protected TypeDescription.Generic genericReturnType;

    @Mock
    protected MethodDescription.InDefinedShape invokableMethod;

    @Mock
    protected MethodDescription.InDefinedShape defaultMethod;

    @Mock
    protected MethodDescription.SignatureToken invokableToken;

    @Mock
    protected MethodDescription.SignatureToken defaultToken;

    protected Implementation.Target.AbstractBase.DefaultMethodInvocation defaultMethodInvocation;

    @Test
    public void testDefaultMethodInvocation() throws Exception {
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeDefault(defaultToken, defaultMethodDeclaringType);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(specialMethodInvocation.getMethodDescription(), CoreMatchers.is(((MethodDescription) (defaultMethod))));
        MatcherAssert.assertThat(specialMethodInvocation.getTypeDescription(), CoreMatchers.is(defaultMethodDeclaringType));
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        StackManipulation.Size size = specialMethodInvocation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, AbstractImplementationTargetTest.BAZBAR, AbstractImplementationTargetTest.QUXBAZ, AbstractImplementationTargetTest.FOOBAZ, true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test
    public void testDefaultMethodInvocationNotSupported() throws Exception {
        defaultMethodInvocation = DISABLED;
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeDefault(defaultToken, defaultMethodDeclaringType);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalDefaultMethod() throws Exception {
        MatcherAssert.assertThat(makeImplementationTarget().invokeDefault(Mockito.mock(MethodDescription.SignatureToken.class), defaultMethodDeclaringType).isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalSuperMethod() throws Exception {
        MethodDescription.SignatureToken token = Mockito.mock(MethodDescription.SignatureToken.class);
        Mockito.when(token.getName()).thenReturn(AbstractImplementationTargetTest.FOO);
        MatcherAssert.assertThat(makeImplementationTarget().invokeSuper(token).isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalSuperConstructor() throws Exception {
        MethodDescription.SignatureToken token = Mockito.mock(MethodDescription.SignatureToken.class);
        Mockito.when(token.getName()).thenReturn(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
        MatcherAssert.assertThat(makeImplementationTarget().invokeSuper(token).isValid(), CoreMatchers.is(false));
    }
}

