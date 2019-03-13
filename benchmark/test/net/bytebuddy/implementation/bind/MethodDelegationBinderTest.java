package net.bytebuddy.implementation.bind;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.MethodBinding.Illegal.INSTANCE;


public class MethodDelegationBinderTest {
    @Test
    public void testIllegalBindingInInvalid() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isValid(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalBindingParameterIndexThrowsException() throws Exception {
        INSTANCE.getTargetParameterIndex(Mockito.mock(Object.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalBindingApplicationThrowsException() throws Exception {
        INSTANCE.apply(Mockito.mock(MethodVisitor.class), Mockito.mock(Implementation.Context.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalBindingTargetThrowsException() throws Exception {
        INSTANCE.getTarget();
    }

    @Test
    public void testIgnored() throws Exception {
        MatcherAssert.assertThat(MethodDelegationBinder.Record.Illegal.INSTANCE.bind(Mockito.mock(Implementation.Target.class), Mockito.mock(MethodDescription.class), Mockito.mock(MethodDelegationBinder.TerminationHandler.class), Mockito.mock(MethodDelegationBinder.MethodInvoker.class), Mockito.mock(Assigner.class)).isValid(), CoreMatchers.is(false));
    }
}

