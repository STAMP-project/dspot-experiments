package net.bytebuddy.implementation.bind;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Directional.LEFT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Directional.RIGHT;


public class MethodDelegationBinderAmbiguityResolverDirectionalTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription source;

    @Mock
    private MethodDelegationBinder.MethodBinding left;

    @Mock
    private MethodDelegationBinder.MethodBinding right;

    @Test
    public void testLeft() throws Exception {
        MatcherAssert.assertThat(LEFT.resolve(source, left, right), CoreMatchers.is(MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT));
    }

    @Test
    public void testRight() throws Exception {
        MatcherAssert.assertThat(RIGHT.resolve(source, left, right), CoreMatchers.is(MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT));
    }
}

