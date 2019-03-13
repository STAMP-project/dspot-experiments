package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class EqualityMatcherTest extends AbstractElementMatcherTest<EqualityMatcher<?>> {
    @SuppressWarnings("unchecked")
    public EqualityMatcherTest() {
        super(((Class<EqualityMatcher<?>>) ((Object) (EqualityMatcher.class))), "is");
    }

    @Test
    public void testMatch() throws Exception {
        Object target = new Object();
        MatcherAssert.assertThat(new EqualityMatcher<Object>(target).matches(target), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatch() throws Exception {
        MatcherAssert.assertThat(new EqualityMatcher<Object>(new Object()).matches(new Object()), CoreMatchers.is(false));
    }
}

