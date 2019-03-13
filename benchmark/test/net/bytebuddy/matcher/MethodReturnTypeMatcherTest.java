package net.bytebuddy.matcher;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodReturnTypeMatcherTest extends AbstractElementMatcherTest<MethodReturnTypeMatcher<?>> {
    @Mock
    private ElementMatcher<? super TypeDescription.Generic> typeMatcher;

    @Mock
    private TypeDescription.Generic returnType;

    @Mock
    private MethodDescription methodDescription;

    @SuppressWarnings("unchecked")
    public MethodReturnTypeMatcherTest() {
        super(((Class<? extends MethodReturnTypeMatcher<?>>) ((Object) (MethodReturnTypeMatcher.class))), "returns");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(typeMatcher.matches(returnType)).thenReturn(true);
        MatcherAssert.assertThat(new MethodReturnTypeMatcher<MethodDescription>(typeMatcher).matches(methodDescription), CoreMatchers.is(true));
        Mockito.verify(typeMatcher).matches(returnType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(typeMatcher.matches(returnType)).thenReturn(false);
        MatcherAssert.assertThat(new MethodReturnTypeMatcher<MethodDescription>(typeMatcher).matches(methodDescription), CoreMatchers.is(false));
        Mockito.verify(typeMatcher).matches(returnType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }
}

