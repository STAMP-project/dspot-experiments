package net.bytebuddy.implementation.bind;


import net.bytebuddy.description.method.ParameterList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;


public class ParameterLengthResolverTest extends AbstractAmbiguityResolverTest {
    @Mock
    private ParameterList<?> leftList;

    @Mock
    private ParameterList<?> rightList;

    @Test
    public void testAmbiguous() throws Exception {
        MatcherAssert.assertThat(ParameterLengthResolver.INSTANCE.resolve(source, left, right), CoreMatchers.is(AMBIGUOUS));
    }

    @Test
    public void testLeft() throws Exception {
        Mockito.when(leftList.size()).thenReturn(1);
        MatcherAssert.assertThat(ParameterLengthResolver.INSTANCE.resolve(source, left, right), CoreMatchers.is(LEFT));
    }

    @Test
    public void testRight() throws Exception {
        Mockito.when(rightList.size()).thenReturn(1);
        MatcherAssert.assertThat(ParameterLengthResolver.INSTANCE.resolve(source, left, right), CoreMatchers.is(RIGHT));
    }
}

