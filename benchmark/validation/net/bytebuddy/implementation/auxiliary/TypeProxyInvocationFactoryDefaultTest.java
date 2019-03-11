package net.bytebuddy.implementation.auxiliary;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.auxiliary.TypeProxy.InvocationFactory.Default.DEFAULT_METHOD;
import static net.bytebuddy.implementation.auxiliary.TypeProxy.InvocationFactory.Default.SUPER_METHOD;


public class TypeProxyInvocationFactoryDefaultTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Implementation.Target implementationTarget;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private MethodDescription.SignatureToken token;

    @Mock
    private Implementation.SpecialMethodInvocation specialMethodInvocation;

    @Test
    public void testSuperMethod() throws Exception {
        Mockito.when(implementationTarget.invokeDominant(token)).thenReturn(specialMethodInvocation);
        MatcherAssert.assertThat(SUPER_METHOD.invoke(implementationTarget, typeDescription, methodDescription), CoreMatchers.is(specialMethodInvocation));
        Mockito.verify(implementationTarget).invokeDominant(token);
        Mockito.verifyNoMoreInteractions(implementationTarget);
    }

    @Test
    public void testDefaultMethod() throws Exception {
        Mockito.when(implementationTarget.invokeDefault(token, typeDescription)).thenReturn(specialMethodInvocation);
        MatcherAssert.assertThat(DEFAULT_METHOD.invoke(implementationTarget, typeDescription, methodDescription), CoreMatchers.is(specialMethodInvocation));
        Mockito.verify(implementationTarget).invokeDefault(token, typeDescription);
        Mockito.verifyNoMoreInteractions(implementationTarget);
    }
}

