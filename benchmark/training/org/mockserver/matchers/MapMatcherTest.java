package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.Headers;


/**
 *
 *
 * @author jamesdbloom
 */
public class MapMatcherTest {
    private Headers matcher;

    private Headers matched;

    @Test
    public void matchesMatchingValues() {
        // given
        matcher.withEntry("foo", "bar");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo", "bar");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void doesNotMatchEmptyValueInExpectation() {
        // given
        matcher.withEntry("foo", "");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo", "bar", "bob");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesMatchingRegexValue() {
        // given
        matcher.withEntry("foo", "b.*");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo", "bar", "bob");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesMatchingRegexKey() {
        // given
        matcher.withEntry("f.*", "bar");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo", "bar");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesMatchingRegexValueAndKey() {
        // given
        matcher.withEntry("f.*", "b.*");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo", "bar");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesMatchingValuesWithExtraValues() {
        // given
        matcher.withEntry("foo1", "bar1");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo0", "bar0");
        matched.withEntry("foo1", "bar1");
        matched.withEntry("foo2", "bar2");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesMatchingValuesIgnoringCase() {
        // given
        matcher.withEntry("foo1", "bar1");
        matcher.withEntry("FOO2", "bar2");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo0", "bar0");
        matched.withEntry("FOO1", "bar1");
        matched.withEntry("foo2", "bar2");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesMatchingRegexValuesWithExtraValues() {
        // given
        matcher.withEntry("foo1", ".*1");
        matcher.withEntry("foo2", ".*2");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo0", "bar0");
        matched.withEntry("foo1", "bar1");
        matched.withEntry("foo2", "bar2");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesMatchingRegexKeysWithExtraValues() {
        // given
        matcher.withEntry("f.*1", "bar1");
        matcher.withEntry("f.*2", "bar2");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo0", "bar0");
        matched.withEntry("foo1", "bar1");
        matched.withEntry("foo2", "bar2");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesMatchingRegexKeysAndValuesWithExtraValues() {
        // given
        matcher.withEntry("f.*1", ".*1");
        matcher.withEntry("f.*2", ".*2");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo0", "bar0");
        matched.withEntry("foo1", "bar1");
        matched.withEntry("foo2", "bar2");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesMatchingRegexValuesIgnoringCase() {
        // given
        matcher.withEntry("FOO1", ".*1");
        matcher.withEntry("foo2", ".*2");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo1", "bar1");
        matched.withEntry("FOO2", "bar2");
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void matchesEmptyExpectation() {
        // given
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // then
        Assert.assertTrue(mapMatcher.matches(null, matched));
    }

    @Test
    public void doesNotMatchDifferentKeys() {
        // given
        matcher.withEntry("foo", "bar");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo2", "bar");
        // then
        Assert.assertFalse(mapMatcher.matches(null, matched));
    }

    @Test
    public void doesNotMatchDifferentValues() {
        // given
        matcher.withEntry("foo", "bar");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo", "bar2");
        // then
        Assert.assertFalse(mapMatcher.matches(null, matched));
    }

    @Test
    public void doesNotMatchDifferentEmptyValue() {
        // given
        matcher.withEntry("foo", "bar");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo", "");
        // then
        Assert.assertFalse(mapMatcher.matches(null, matched));
    }

    @Test
    public void doesNotMatchIncorrectRegexValue() {
        // given
        matcher.withEntry("foo1", "a.*1");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo0", "bar0");
        matched.withEntry("foo1", "bar1");
        matched.withEntry("foo2", "bar2");
        // then
        Assert.assertFalse(mapMatcher.matches(null, matched));
    }

    @Test
    public void doesNotMatchIncorrectRegexKey() {
        // given
        matcher.withEntry("g.*1", "bar1");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo0", "bar0");
        matched.withEntry("foo1", "bar1");
        matched.withEntry("foo2", "bar2");
        // then
        Assert.assertFalse(mapMatcher.matches(null, matched));
    }

    @Test
    public void doesNotMatchIncorrectRegexKeyAndValue() {
        // given
        matcher.withEntry("g.*1", "a.*1");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo0", "bar0");
        matched.withEntry("foo1", "bar1");
        matched.withEntry("foo2", "bar2");
        // then
        Assert.assertFalse(mapMatcher.matches(null, matched));
    }

    @Test
    public void shouldHandleIllegalRegexValuePattern() {
        // given
        matcher.withEntry("foo", "/{}");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo", "/{}/");
        // then
        Assert.assertFalse(mapMatcher.matches(null, matched));
    }

    @Test
    public void shouldHandleIllegalRegexKeyPattern() {
        // given
        matcher.withEntry("/{}", "bar");
        MultiValueMapMatcher mapMatcher = new MultiValueMapMatcher(new MockServerLogger(), matcher);
        // when
        matched.withEntry("foo", "/{}/");
        // then
        Assert.assertFalse(mapMatcher.matches(null, matched));
    }
}

