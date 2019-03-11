package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class NullMatcherTest extends AbstractElementMatcherTest<NullMatcher<?>> {
    @SuppressWarnings("unchecked")
    public NullMatcherTest() {
        super(((Class<NullMatcher<?>>) ((Object) (NullMatcher.class))), "isNull");
    }

    @Test
    public void testMatch() throws Exception {
        MatcherAssert.assertThat(new NullMatcher<Object>().matches(null), CoreMatchers.is(true));
    }

    @Test
    public void testPositiveToNegative() throws Exception {
        MatcherAssert.assertThat(new NullMatcher<Object>().matches(new Object()), CoreMatchers.is(false));
    }
}

