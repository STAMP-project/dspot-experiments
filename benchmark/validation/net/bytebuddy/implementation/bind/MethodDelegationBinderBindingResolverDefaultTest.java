package net.bytebuddy.implementation.bind;


import java.util.Arrays;
import java.util.Collections;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.BindingResolver.Default.INSTANCE;


public class MethodDelegationBinderBindingResolverDefaultTest {
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

    @Mock
    private MethodDelegationBinder.MethodBinding boundDelegation;

    @Mock
    private MethodDelegationBinder.MethodBinding dominantBoundDelegation;

    @Test
    public void testOneBindableTarget() throws Exception {
        MethodDelegationBinder.MethodBinding result = INSTANCE.resolve(ambiguityResolver, source, Collections.singletonList(boundDelegation));
        MatcherAssert.assertThat(result, CoreMatchers.is(boundDelegation));
        Mockito.verifyZeroInteractions(ambiguityResolver);
    }

    @Test
    public void testTwoBindableTargetsWithDominant() throws Exception {
        MethodDelegationBinder.MethodBinding result = INSTANCE.resolve(ambiguityResolver, source, Arrays.asList(boundDelegation, dominantBoundDelegation));
        MatcherAssert.assertThat(result, CoreMatchers.is(dominantBoundDelegation));
        Mockito.verify(ambiguityResolver).resolve(source, boundDelegation, dominantBoundDelegation);
        Mockito.verifyNoMoreInteractions(ambiguityResolver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTwoBindableTargetsWithoutDominant() throws Exception {
        INSTANCE.resolve(ambiguityResolver, source, Arrays.asList(boundDelegation, boundDelegation));
    }

    @Test
    public void testThreeBindableTargetsDominantBindableFirst() throws Exception {
        INSTANCE.resolve(ambiguityResolver, source, Arrays.asList(dominantBoundDelegation, boundDelegation, boundDelegation));
        Mockito.verify(ambiguityResolver, Mockito.times(2)).resolve(source, dominantBoundDelegation, boundDelegation);
        Mockito.verifyNoMoreInteractions(ambiguityResolver);
    }

    @Test
    public void testThreeBindableTargetsDominantBindableMid() throws Exception {
        INSTANCE.resolve(ambiguityResolver, source, Arrays.asList(boundDelegation, dominantBoundDelegation, boundDelegation));
        Mockito.verify(ambiguityResolver).resolve(source, dominantBoundDelegation, boundDelegation);
        Mockito.verify(ambiguityResolver).resolve(source, boundDelegation, dominantBoundDelegation);
        Mockito.verifyNoMoreInteractions(ambiguityResolver);
    }

    @Test
    public void testThreeBindableTargetsDominantBindableLast() throws Exception {
        INSTANCE.resolve(ambiguityResolver, source, Arrays.asList(boundDelegation, boundDelegation, dominantBoundDelegation));
        Mockito.verify(ambiguityResolver).resolve(source, boundDelegation, boundDelegation);
        Mockito.verify(ambiguityResolver, Mockito.times(2)).resolve(source, boundDelegation, dominantBoundDelegation);
        Mockito.verifyNoMoreInteractions(ambiguityResolver);
    }
}

