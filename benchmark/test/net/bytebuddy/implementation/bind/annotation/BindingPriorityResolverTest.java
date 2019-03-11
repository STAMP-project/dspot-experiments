package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.annotation.AnnotationDescription.ForLoadedAnnotation.of;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;
import static net.bytebuddy.implementation.bind.annotation.BindingPriority.Resolver.INSTANCE;


public class BindingPriorityResolverTest extends AbstractAnnotationTest<BindingPriority> {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription source;

    @Mock
    private MethodDescription leftMethod;

    @Mock
    private MethodDescription rightMethod;

    @Mock
    private MethodDelegationBinder.MethodBinding left;

    @Mock
    private MethodDelegationBinder.MethodBinding right;

    @Mock
    private AnnotationList leftAnnotations;

    @Mock
    private AnnotationList rightAnnotations;

    @Mock
    private BindingPriority highPriority;

    @Mock
    private BindingPriority lowPriority;

    public BindingPriorityResolverTest() {
        super(BindingPriority.class);
    }

    @Test
    public void testNoPriorities() throws Exception {
        MatcherAssert.assertThat(INSTANCE.resolve(source, left, right), CoreMatchers.is(AMBIGUOUS));
    }

    @Test
    public void testLeftPrioritized() throws Exception {
        AnnotationDescription.Loadable<BindingPriority> highPriority = of(this.highPriority);
        Mockito.when(leftAnnotations.ofType(BindingPriority.class)).thenReturn(highPriority);
        AnnotationDescription.Loadable<BindingPriority> lowPriority = of(this.lowPriority);
        Mockito.when(rightAnnotations.ofType(BindingPriority.class)).thenReturn(lowPriority);
        MatcherAssert.assertThat(INSTANCE.resolve(source, left, right), CoreMatchers.is(LEFT));
    }

    @Test
    public void testRightPrioritized() throws Exception {
        AnnotationDescription.Loadable<BindingPriority> lowPriority = of(this.lowPriority);
        Mockito.when(leftAnnotations.ofType(BindingPriority.class)).thenReturn(lowPriority);
        AnnotationDescription.Loadable<BindingPriority> highPriority = of(this.highPriority);
        Mockito.when(rightAnnotations.ofType(BindingPriority.class)).thenReturn(highPriority);
        MatcherAssert.assertThat(INSTANCE.resolve(source, left, right), CoreMatchers.is(RIGHT));
    }
}

