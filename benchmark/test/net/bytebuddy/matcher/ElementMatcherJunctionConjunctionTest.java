package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ElementMatcherJunctionConjunctionTest extends AbstractElementMatcherTest<ElementMatcher.Junction.Conjunction<?>> {
    @Mock
    private ElementMatcher<? super Object> first;

    @Mock
    private ElementMatcher<? super Object> second;

    @SuppressWarnings("unchecked")
    public ElementMatcherJunctionConjunctionTest() {
        super(((Class<? extends ElementMatcher.Junction.Conjunction<?>>) ((Object) (ElementMatcher.Junction.Conjunction.class))), "");
    }

    @Test
    public void testApplicationBoth() throws Exception {
        Object target = new Object();
        Mockito.when(first.matches(target)).thenReturn(true);
        Mockito.when(second.matches(target)).thenReturn(true);
        MatcherAssert.assertThat(new ElementMatcher.Junction.Conjunction<Object>(first, second).matches(target), CoreMatchers.is(true));
        Mockito.verify(first).matches(target);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).matches(target);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testApplicationFirstOnly() throws Exception {
        Object target = new Object();
        Mockito.when(first.matches(target)).thenReturn(false);
        MatcherAssert.assertThat(new ElementMatcher.Junction.Conjunction<Object>(first, second).matches(target), CoreMatchers.is(false));
        Mockito.verify(first).matches(target);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyZeroInteractions(second);
    }

    @Test
    public void testApplicationBothNegative() throws Exception {
        Object target = new Object();
        Mockito.when(first.matches(target)).thenReturn(true);
        Mockito.when(second.matches(target)).thenReturn(false);
        MatcherAssert.assertThat(new ElementMatcher.Junction.Conjunction<Object>(first, second).matches(target), CoreMatchers.is(false));
        Mockito.verify(first).matches(target);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).matches(target);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testToString() {
        MatcherAssert.assertThat(new ElementMatcher.Junction.Conjunction<Object>(first, second).toString(), CoreMatchers.containsString(" and "));
    }
}

