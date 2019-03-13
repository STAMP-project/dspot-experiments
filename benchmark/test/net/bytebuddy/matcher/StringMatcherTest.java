package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class StringMatcherTest extends AbstractElementMatcherTest<StringMatcher> {
    private static final String FOO = "foo";

    private final StringMatcher.Mode mode;

    private final String matching;

    private final String nonMatching;

    public StringMatcherTest(StringMatcher.Mode mode, String matching, String nonMatching) {
        super(StringMatcher.class, mode.getDescription());
        this.mode = mode;
        this.matching = matching;
        this.nonMatching = nonMatching;
    }

    @Test
    public void testMatch() throws Exception {
        MatcherAssert.assertThat(new StringMatcher(matching, mode).matches(StringMatcherTest.FOO), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatch() throws Exception {
        MatcherAssert.assertThat(new StringMatcher(nonMatching, mode).matches(StringMatcherTest.FOO), CoreMatchers.is(false));
    }

    @Test
    public void testStringRepresentation() throws Exception {
        MatcherAssert.assertThat(new StringMatcher(StringMatcherTest.FOO, mode).toString(), CoreMatchers.startsWith(mode.getDescription()));
    }
}

