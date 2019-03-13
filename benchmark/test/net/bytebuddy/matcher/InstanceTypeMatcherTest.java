package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class InstanceTypeMatcherTest extends AbstractElementMatcherTest<InstanceTypeMatcher<?>> {
    @Mock
    private Object object;

    @Mock
    private ElementMatcher<? super TypeDescription> matcher;

    @SuppressWarnings("unchecked")
    public InstanceTypeMatcherTest() {
        super(((Class<InstanceTypeMatcher<?>>) ((Object) (InstanceTypeMatcher.class))), "ofType");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(matcher.matches(of(object.getClass()))).thenReturn(true);
        MatcherAssert.assertThat(new InstanceTypeMatcher<Object>(matcher).matches(object), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(matcher.matches(of(object.getClass()))).thenReturn(false);
        MatcherAssert.assertThat(new InstanceTypeMatcher<Object>(matcher).matches(object), CoreMatchers.is(false));
    }

    @Test
    public void testNoMatchNull() throws Exception {
        MatcherAssert.assertThat(new InstanceTypeMatcher<Object>(matcher).matches(null), CoreMatchers.is(false));
    }
}

