package net.bytebuddy.matcher;


import java.util.concurrent.ConcurrentMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class CachingMatcherTest extends AbstractElementMatcherTest<CachingMatcher<?>> {
    @Mock
    private Object target;

    @Mock
    private ElementMatcher<? super Object> matcher;

    private ConcurrentMap<Object, Boolean> map;

    @SuppressWarnings("unchecked")
    public CachingMatcherTest() {
        super(((Class<CachingMatcher<?>>) ((Object) (CachingMatcher.class))), "cached");
    }

    @Test
    public void testMatchCachesNoEviction() throws Exception {
        ElementMatcher<Object> matcher = new CachingMatcher<Object>(this.matcher, map);
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        Mockito.verify(this.matcher).matches(target);
        Mockito.verifyNoMoreInteractions(this.matcher);
        Mockito.verifyZeroInteractions(target);
    }

    @Test
    public void testMatchCachesEviction() throws Exception {
        ElementMatcher<Object> matcher = new CachingMatcher.WithInlineEviction<Object>(this.matcher, map, 1);
        Object other = Mockito.mock(Object.class);
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        MatcherAssert.assertThat(matcher.matches(other), CoreMatchers.is(false));
        MatcherAssert.assertThat(matcher.matches(other), CoreMatchers.is(false));
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        Mockito.verify(this.matcher, Mockito.times(2)).matches(target);
        Mockito.verify(this.matcher).matches(other);
        Mockito.verifyNoMoreInteractions(this.matcher);
        Mockito.verifyZeroInteractions(target);
    }

    @Test
    public void testMatchNoCachesEviction() throws Exception {
        ElementMatcher<Object> matcher = new CachingMatcher.WithInlineEviction<Object>(this.matcher, map, 2);
        Object other = Mockito.mock(Object.class);
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        MatcherAssert.assertThat(matcher.matches(other), CoreMatchers.is(false));
        MatcherAssert.assertThat(matcher.matches(other), CoreMatchers.is(false));
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        Mockito.verify(this.matcher).matches(target);
        Mockito.verify(this.matcher).matches(other);
        Mockito.verifyNoMoreInteractions(this.matcher);
        Mockito.verifyZeroInteractions(target);
    }
}

