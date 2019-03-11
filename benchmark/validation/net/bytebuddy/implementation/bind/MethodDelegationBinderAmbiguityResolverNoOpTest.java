package net.bytebuddy.implementation.bind;


import net.bytebuddy.description.method.MethodDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.NoOp.INSTANCE;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.UNKNOWN;


public class MethodDelegationBinderAmbiguityResolverNoOpTest {
    @Test
    public void testResolution() throws Exception {
        MatcherAssert.assertThat(INSTANCE.resolve(Mockito.mock(MethodDescription.class), Mockito.mock(MethodDelegationBinder.MethodBinding.class), Mockito.mock(MethodDelegationBinder.MethodBinding.class)), CoreMatchers.is(UNKNOWN));
    }
}

