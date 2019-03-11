package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ElementMatcherJunctionDisjunctionTest extends AbstractElementMatcherTest<ElementMatcher.Junction.Disjunction<?>> {
    @Mock
    private ElementMatcher<? super Object> first;

    @Mock
    private ElementMatcher<? super Object> second;

    @SuppressWarnings("unchecked")
    public ElementMatcherJunctionDisjunctionTest() {
        super(((Class<? extends ElementMatcher.Junction.Disjunction<?>>) ((Object) (ElementMatcher.Junction.Disjunction.class))), "");
    }

    @Test
    public void testApplicationBoth() throws Exception {
        Object target = new Object();
        Mockito.when(first.matches(target)).thenReturn(false);
        Mockito.when(second.matches(target)).thenReturn(false);
        MatcherAssert.assertThat(new ElementMatcher.Junction.Disjunction<Object>(first, second).matches(target), CoreMatchers.is(false));
        Mockito.verify(first).matches(target);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).matches(target);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testApplicationFirstOnly() throws Exception {
        Object target = new Object();
        Mockito.when(first.matches(target)).thenReturn(true);
        MatcherAssert.assertThat(new ElementMatcher.Junction.Disjunction<Object>(first, second).matches(target), CoreMatchers.is(true));
        Mockito.verify(first).matches(target);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyZeroInteractions(second);
    }

    @Test
    public void testApplicationBothPositive() throws Exception {
        Object target = new Object();
        Mockito.when(first.matches(target)).thenReturn(false);
        Mockito.when(second.matches(target)).thenReturn(true);
        MatcherAssert.assertThat(new ElementMatcher.Junction.Disjunction<Object>(first, second).matches(target), CoreMatchers.is(true));
        Mockito.verify(first).matches(target);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).matches(target);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testToString() {
        MatcherAssert.assertThat(new ElementMatcher.Junction.Disjunction<Object>(first, second).toString(), CoreMatchers.containsString(" or "));
    }
}

