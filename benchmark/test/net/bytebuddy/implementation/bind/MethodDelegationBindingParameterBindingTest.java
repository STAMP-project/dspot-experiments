package net.bytebuddy.implementation.bind;


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

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.MethodBinding.Illegal.INSTANCE;


public class MethodDelegationBindingParameterBindingTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private StackManipulation stackManipulation;

    @Mock
    private StackManipulation.Size size;

    @Test
    public void testIllegalBindingIsInvalid() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isValid(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalBindingCannotExtractToken() throws Exception {
        MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE.getIdentificationToken();
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalBindingIsNotApplicable() throws Exception {
        MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE.apply(methodVisitor, implementationContext);
    }

    @Test
    public void testAnonymousToken() throws Exception {
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = new MethodDelegationBinder.ParameterBinding.Anonymous(stackManipulation);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterBinding.getIdentificationToken(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(parameterBinding.apply(methodVisitor, implementationContext), CoreMatchers.is(size));
        Mockito.verify(stackManipulation).isValid();
        Mockito.verify(stackManipulation).apply(methodVisitor, implementationContext);
        Mockito.verifyNoMoreInteractions(stackManipulation);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test
    public void testUniqueToken() throws Exception {
        Object token = new Object();
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = new MethodDelegationBinder.ParameterBinding.Unique<Object>(stackManipulation, token);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterBinding.getIdentificationToken(), CoreMatchers.is(token));
        MatcherAssert.assertThat(parameterBinding.apply(methodVisitor, implementationContext), CoreMatchers.is(size));
        Mockito.verify(stackManipulation).isValid();
        Mockito.verify(stackManipulation).apply(methodVisitor, implementationContext);
        Mockito.verifyNoMoreInteractions(stackManipulation);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

