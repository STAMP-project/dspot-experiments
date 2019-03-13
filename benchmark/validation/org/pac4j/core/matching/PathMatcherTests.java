package org.pac4j.core.matching;


import java.util.HashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsHelper;


/**
 * Tests {@link PathMatcher}.
 *
 * @author Rob Ward
 * @since 2.0.0
 */
public class PathMatcherTests {
    @Test
    public void testBlankPath() {
        final PathMatcher pathMatcher = new PathMatcher();
        Assert.assertTrue(pathMatcher.matches(MockWebContext.create().setPath("/page.html")));
        Assert.assertTrue(pathMatcher.matches(MockWebContext.create()));
    }

    @Test
    public void testFixedPath() {
        final PathMatcher pathMatcher = new PathMatcher().excludePath("/foo");
        Assert.assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/foo")));
        Assert.assertTrue(pathMatcher.matches(MockWebContext.create().setPath("/foo/bar")));
    }

    @Test
    public void testBranch() {
        final PathMatcher pathMatcher = new PathMatcher().excludeBranch("/foo");
        Assert.assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/foo")));
        Assert.assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/foo/")));
        Assert.assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/foo/bar")));
    }

    @Test
    public void testMissingStartCharacterInRegexp() {
        TestsHelper.expectException(() -> new PathMatcher().excludeRegex("/img/.*$"), TechnicalException.class, "Your regular expression: '/img/.*$' must start with a ^ and end with a $ to define a full path matching");
    }

    @Test
    public void testMissingEndCharacterInRegexp() {
        TestsHelper.expectException(() -> new PathMatcher().excludeRegex("^/img/.*"), TechnicalException.class, "Your regular expression: '^/img/.*' must start with a ^ and end with a $ to define a full path matching");
    }

    @Test(expected = PatternSyntaxException.class)
    public void testBadRegexp() {
        new PathMatcher().excludeRegex("^/img/**$");
    }

    @Test
    public void testNoPath() {
        final PathMatcher pathMatcher = new PathMatcher().excludeRegex("^/$");
        Assert.assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/")));
    }

    @Test
    public void testMatch() {
        final PathMatcher matcher = new PathMatcher().excludeRegex("^/(img/.*|css/.*|page\\.html)$");
        Assert.assertTrue(matcher.matches(MockWebContext.create().setPath("/js/app.js")));
        Assert.assertTrue(matcher.matches(MockWebContext.create().setPath("/")));
        Assert.assertTrue(matcher.matches(MockWebContext.create().setPath("/page.htm")));
    }

    @Test
    public void testDontMatch() {
        final PathMatcher matcher = new PathMatcher().excludeRegex("^/(img/.*|css/.*|page\\.html)$");
        Assert.assertFalse(matcher.matches(MockWebContext.create().setPath("/css/app.css")));
        Assert.assertFalse(matcher.matches(MockWebContext.create().setPath("/img/")));
        Assert.assertFalse(matcher.matches(MockWebContext.create().setPath("/page.html")));
    }

    @Test
    public void testSetters() {
        final Set<String> excludedPaths = new HashSet<>();
        excludedPaths.add("/foo");
        final Set<String> excludedRegexs = new HashSet<>();
        excludedRegexs.add("^/(img/.*|css/.*|page\\.html)$");
        final PathMatcher matcher = new PathMatcher();
        matcher.setExcludedPaths(excludedPaths);
        matcher.setExcludedPatterns(excludedRegexs);
        Assert.assertFalse(matcher.matches(MockWebContext.create().setPath("/foo")));
        Assert.assertTrue(matcher.matches(MockWebContext.create().setPath("/foo/")));// because its a fixed path, not a regex

        Assert.assertTrue(matcher.matches(MockWebContext.create().setPath("/error/500.html")));
        Assert.assertFalse(matcher.matches(MockWebContext.create().setPath("/img/")));
    }
}

