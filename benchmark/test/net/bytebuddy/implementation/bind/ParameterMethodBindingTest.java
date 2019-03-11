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

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.ParameterBinding.Unique.of;


public class ParameterMethodBindingTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private StackManipulation stackManipulation;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testIllegal() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testAnonymous() throws Exception {
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = new MethodDelegationBinder.ParameterBinding.Anonymous(stackManipulation);
        MatcherAssert.assertThat(parameterBinding.getIdentificationToken(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        parameterBinding.apply(methodVisitor, implementationContext);
        Mockito.verify(stackManipulation).apply(methodVisitor, implementationContext);
        Mockito.verify(stackManipulation).isValid();
    }

    @Test
    public void testIdentified() throws Exception {
        Object identificationToken = new Object();
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = of(stackManipulation, identificationToken);
        MatcherAssert.assertThat(parameterBinding.getIdentificationToken(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        parameterBinding.apply(methodVisitor, implementationContext);
        Mockito.verify(stackManipulation).apply(methodVisitor, implementationContext);
        Mockito.verify(stackManipulation).isValid();
    }
}

