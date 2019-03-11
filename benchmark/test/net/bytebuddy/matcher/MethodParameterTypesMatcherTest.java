package net.bytebuddy.matcher;


import java.util.List;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodParameterTypesMatcherTest extends AbstractElementMatcherTest<MethodParameterTypesMatcher<?>> {
    @Mock
    private ElementMatcher<? super List<? extends TypeDescription.Generic>> parameterMatcher;

    @Mock
    private TypeList.Generic typeList;

    @Mock
    private ParameterList parameterList;

    @SuppressWarnings("unchecked")
    public MethodParameterTypesMatcherTest() {
        super(((Class<MethodParameterTypesMatcher<?>>) ((Object) (MethodParameterTypesMatcher.class))), "hasTypes");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(parameterMatcher.matches(typeList)).thenReturn(true);
        MatcherAssert.assertThat(new MethodParameterTypesMatcher<ParameterList<?>>(parameterMatcher).matches(parameterList), CoreMatchers.is(true));
        Mockito.verify(parameterMatcher).matches(typeList);
        Mockito.verifyNoMoreInteractions(parameterMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(parameterMatcher.matches(typeList)).thenReturn(false);
        MatcherAssert.assertThat(new MethodParameterTypesMatcher<ParameterList<?>>(parameterMatcher).matches(parameterList), CoreMatchers.is(false));
        Mockito.verify(parameterMatcher).matches(typeList);
        Mockito.verifyNoMoreInteractions(parameterMatcher);
    }
}

