package net.bytebuddy.matcher;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodParameterMatcherTest extends AbstractElementMatcherTest<MethodParametersMatcher<?>> {
    @Mock
    private ElementMatcher<? super ParameterList<?>> parameterListMatcher;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private ParameterList<?> parameterList;

    @SuppressWarnings("unchecked")
    public MethodParameterMatcherTest() {
        super(((Class<MethodParametersMatcher<?>>) ((Object) (MethodParametersMatcher.class))), "hasParameter");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(parameterListMatcher.matches(parameterList)).thenReturn(true);
        MatcherAssert.assertThat(new MethodParametersMatcher<MethodDescription>(parameterListMatcher).matches(methodDescription), CoreMatchers.is(true));
        Mockito.verify(parameterListMatcher).matches(parameterList);
        Mockito.verifyNoMoreInteractions(parameterListMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(parameterListMatcher.matches(parameterList)).thenReturn(false);
        MatcherAssert.assertThat(new MethodParametersMatcher<MethodDescription>(parameterListMatcher).matches(methodDescription), CoreMatchers.is(false));
        Mockito.verify(parameterListMatcher).matches(parameterList);
        Mockito.verifyNoMoreInteractions(parameterListMatcher);
    }
}

