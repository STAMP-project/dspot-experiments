package net.bytebuddy.dynamic.scaffold.inline;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.scaffold.inline.MethodRebaseResolver.Disabled.INSTANCE;


public class MethodRebaseResolverDisabledTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Test
    public void testResolutionPreservesMethod() throws Exception {
        MethodRebaseResolver.Resolution resolution = INSTANCE.resolve(methodDescription);
        MatcherAssert.assertThat(resolution.isRebased(), CoreMatchers.is(false));
        MatcherAssert.assertThat(resolution.getResolvedMethod(), CoreMatchers.is(methodDescription));
    }

    @Test
    public void testNoAuxiliaryTypes() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getAuxiliaryTypes().size(), CoreMatchers.is(0));
    }

    @Test
    public void testNoRebaseableMethods() throws Exception {
        MatcherAssert.assertThat(INSTANCE.asTokenMap().size(), CoreMatchers.is(0));
    }
}

