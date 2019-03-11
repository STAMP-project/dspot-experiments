package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class NegatingMatcherTest extends AbstractElementMatcherTest<NegatingMatcher<?>> {
    @Mock
    private ElementMatcher<? super Object> elementMatcher;

    @SuppressWarnings("unchecked")
    public NegatingMatcherTest() {
        super(((Class<NegatingMatcher<?>>) ((Object) (NegatingMatcher.class))), "not");
    }

    @Test
    public void testNegateToPositive() throws Exception {
        Object target = new Object();
        Mockito.when(elementMatcher.matches(target)).thenReturn(true);
        MatcherAssert.assertThat(new NegatingMatcher<Object>(elementMatcher).matches(target), CoreMatchers.is(false));
        Mockito.verify(elementMatcher).matches(target);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }

    @Test
    public void testPositiveToNegative() throws Exception {
        Object target = new Object();
        Mockito.when(elementMatcher.matches(target)).thenReturn(false);
        MatcherAssert.assertThat(new NegatingMatcher<Object>(elementMatcher).matches(target), CoreMatchers.is(true));
        Mockito.verify(elementMatcher).matches(target);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }
}

