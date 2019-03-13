package net.bytebuddy.matcher;


import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodParameterTypeMatcherTest extends AbstractElementMatcherTest<MethodParameterTypeMatcher<?>> {
    @Mock
    private ElementMatcher<? super TypeDescription.Generic> parameterMatcher;

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private ParameterDescription parameterDescription;

    @SuppressWarnings("unchecked")
    public MethodParameterTypeMatcherTest() {
        super(((Class<MethodParameterTypeMatcher<?>>) ((Object) (MethodParameterTypeMatcher.class))), "hasType");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(parameterMatcher.matches(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new MethodParameterTypeMatcher<ParameterDescription>(parameterMatcher).matches(parameterDescription), CoreMatchers.is(true));
        Mockito.verify(parameterMatcher).matches(typeDescription);
        Mockito.verifyNoMoreInteractions(parameterMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(parameterMatcher.matches(typeDescription)).thenReturn(false);
        MatcherAssert.assertThat(new MethodParameterTypeMatcher<ParameterDescription>(parameterMatcher).matches(parameterDescription), CoreMatchers.is(false));
        Mockito.verify(parameterMatcher).matches(typeDescription);
        Mockito.verifyNoMoreInteractions(parameterMatcher);
    }
}

