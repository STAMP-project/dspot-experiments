package net.bytebuddy.implementation.bind;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;


public class MethodDelegationBinderAmbiguityResolverChainTest extends AbstractAmbiguityResolverTest {
    @Mock
    private MethodDelegationBinder.AmbiguityResolver first;

    @Mock
    private MethodDelegationBinder.AmbiguityResolver second;

    @Mock
    private MethodDelegationBinder.AmbiguityResolver third;

    private MethodDelegationBinder.AmbiguityResolver chain;

    @Test
    public void testFirstResolves() throws Exception {
        Mockito.when(first.resolve(source, left, right)).thenReturn(LEFT);
        MatcherAssert.assertThat(chain.resolve(source, left, right), CoreMatchers.is(LEFT));
        Mockito.verify(first).resolve(source, left, right);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyZeroInteractions(second);
    }

    @Test
    public void testSecondResolves() throws Exception {
        Mockito.when(first.resolve(source, left, right)).thenReturn(AMBIGUOUS);
        Mockito.when(second.resolve(source, left, right)).thenReturn(RIGHT);
        MatcherAssert.assertThat(chain.resolve(source, left, right), CoreMatchers.is(RIGHT));
        Mockito.verify(first).resolve(source, left, right);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).resolve(source, left, right);
        Mockito.verifyNoMoreInteractions(second);
    }
}

