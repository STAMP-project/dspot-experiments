package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public class BooleanMatcherTest extends AbstractElementMatcherTest<BooleanMatcher<?>> {
    @SuppressWarnings("unchecked")
    public BooleanMatcherTest() {
        super(((Class<BooleanMatcher<?>>) ((Object) (BooleanMatcher.class))), "");
    }

    @Test
    public void testMatch() throws Exception {
        Object target = Mockito.mock(Object.class);
        MatcherAssert.assertThat(new BooleanMatcher<Object>(true).matches(target), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(target);
    }

    @Test
    public void testNoMatch() throws Exception {
        Object target = Mockito.mock(Object.class);
        MatcherAssert.assertThat(new BooleanMatcher<Object>(false).matches(target), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(target);
    }

    @Test
    public void testToString() throws Exception {
        MatcherAssert.assertThat(new BooleanMatcher<Object>(true).toString(), CoreMatchers.is("true"));
        MatcherAssert.assertThat(new BooleanMatcher<Object>(false).toString(), CoreMatchers.is("false"));
    }
}

