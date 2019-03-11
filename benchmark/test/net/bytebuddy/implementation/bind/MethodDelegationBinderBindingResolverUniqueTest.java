package net.bytebuddy.implementation.bind;


import java.util.Arrays;
import java.util.Collections;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.BindingResolver.Unique.INSTANCE;


public class MethodDelegationBinderBindingResolverUniqueTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription source;

    @Mock
    private MethodDescription target;

    @Mock
    private MethodDelegationBinder.MethodBinding methodBinding;

    @Mock
    private MethodDelegationBinder.AmbiguityResolver ambiguityResolver;

    @Test
    public void testUnique() throws Exception {
        Assert.assertThat(INSTANCE.resolve(ambiguityResolver, source, Collections.singletonList(methodBinding)), CoreMatchers.is(methodBinding));
    }

    @Test(expected = IllegalStateException.class)
    public void testNonUnique() throws Exception {
        INSTANCE.resolve(ambiguityResolver, source, Arrays.asList(Mockito.mock(MethodDelegationBinder.MethodBinding.class), methodBinding));
    }
}

