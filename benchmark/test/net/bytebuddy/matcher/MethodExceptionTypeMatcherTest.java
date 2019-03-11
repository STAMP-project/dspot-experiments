package net.bytebuddy.matcher;


import java.util.List;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodExceptionTypeMatcherTest extends AbstractElementMatcherTest<MethodExceptionTypeMatcher<?>> {
    @Mock
    private ElementMatcher<? super List<? extends TypeDescription.Generic>> exceptionMatcher;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private TypeList.Generic typeList;

    @SuppressWarnings("unchecked")
    public MethodExceptionTypeMatcherTest() {
        super(((Class<MethodExceptionTypeMatcher<?>>) ((Object) (MethodExceptionTypeMatcher.class))), "exceptions");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(exceptionMatcher.matches(typeList)).thenReturn(true);
        MatcherAssert.assertThat(new MethodExceptionTypeMatcher<MethodDescription>(exceptionMatcher).matches(methodDescription), CoreMatchers.is(true));
        Mockito.verify(exceptionMatcher).matches(typeList);
        Mockito.verifyNoMoreInteractions(exceptionMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(exceptionMatcher.matches(typeList)).thenReturn(false);
        MatcherAssert.assertThat(new MethodExceptionTypeMatcher<MethodDescription>(exceptionMatcher).matches(methodDescription), CoreMatchers.is(false));
        Mockito.verify(exceptionMatcher).matches(typeList);
        Mockito.verifyNoMoreInteractions(exceptionMatcher);
    }
}

