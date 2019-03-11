package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FailSafeMatcherTest extends AbstractElementMatcherTest<FailSafeMatcher<?>> {
    @SuppressWarnings("unchecked")
    public FailSafeMatcherTest() {
        super(((Class<FailSafeMatcher<?>>) ((Object) (FailSafeMatcher.class))), "failSafe");
    }

    @Mock
    private ElementMatcher<Object> elementMatcher;

    @Mock
    private Object target;

    @Test
    public void testMatch() throws Exception {
        Mockito.when(elementMatcher.matches(target)).thenReturn(true);
        MatcherAssert.assertThat(new FailSafeMatcher<Object>(elementMatcher, false).matches(target), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(target);
        Mockito.verify(elementMatcher).matches(target);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(elementMatcher.matches(target)).thenReturn(false);
        MatcherAssert.assertThat(new FailSafeMatcher<Object>(elementMatcher, false).matches(target), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(target);
        Mockito.verify(elementMatcher).matches(target);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatchOnFailure() throws Exception {
        Mockito.when(elementMatcher.matches(target)).thenThrow(RuntimeException.class);
        MatcherAssert.assertThat(new FailSafeMatcher<Object>(elementMatcher, true).matches(target), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(target);
        Mockito.verify(elementMatcher).matches(target);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoMatchOnFailure() throws Exception {
        Mockito.when(elementMatcher.matches(target)).thenThrow(RuntimeException.class);
        MatcherAssert.assertThat(new FailSafeMatcher<Object>(elementMatcher, false).matches(target), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(target);
        Mockito.verify(elementMatcher).matches(target);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }
}

