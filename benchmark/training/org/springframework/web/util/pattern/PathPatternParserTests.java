/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.util.pattern;


import PathPattern.PathMatchInfo;
import PatternMessage.CAPTURE_ALL_IS_STANDALONE_CONSTRUCT;
import PatternMessage.ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR;
import PatternMessage.ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR;
import PatternMessage.ILLEGAL_DOUBLE_CAPTURE;
import PatternMessage.ILLEGAL_NESTED_CAPTURE;
import PatternMessage.MISSING_CLOSE_CAPTURE;
import PatternMessage.MISSING_OPEN_CAPTURE;
import PatternMessage.MISSING_REGEX_CONSTRAINT;
import PatternMessage.NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST;
import PatternMessage.REGEX_PATTERN_SYNTAX_EXCEPTION;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Exercise the {@link PathPatternParser}.
 *
 * @author Andy Clement
 */
public class PathPatternParserTests {
    private PathPattern pathPattern;

    @Test
    public void basicPatterns() {
        checkStructure("/");
        checkStructure("/foo");
        checkStructure("foo");
        checkStructure("foo/");
        checkStructure("/foo/");
        checkStructure("");
    }

    @Test
    public void singleCharWildcardPatterns() {
        pathPattern = checkStructure("?");
        assertPathElements(pathPattern, SingleCharWildcardedPathElement.class);
        checkStructure("/?/");
        checkStructure("/?abc?/");
    }

    @Test
    public void multiwildcardPattern() {
        pathPattern = checkStructure("/**");
        assertPathElements(pathPattern, WildcardTheRestPathElement.class);
        // this is not double wildcard, it's / then **acb (an odd, unnecessary use of double *)
        pathPattern = checkStructure("/**acb");
        assertPathElements(pathPattern, SeparatorPathElement.class, RegexPathElement.class);
    }

    @Test
    public void toStringTests() {
        Assert.assertEquals("CaptureTheRest(/{*foobar})", checkStructure("/{*foobar}").toChainString());
        Assert.assertEquals("CaptureVariable({foobar})", checkStructure("{foobar}").toChainString());
        Assert.assertEquals("Literal(abc)", checkStructure("abc").toChainString());
        Assert.assertEquals("Regex({a}_*_{b})", checkStructure("{a}_*_{b}").toChainString());
        Assert.assertEquals("Separator(/)", checkStructure("/").toChainString());
        Assert.assertEquals("SingleCharWildcarded(?a?b?c)", checkStructure("?a?b?c").toChainString());
        Assert.assertEquals("Wildcard(*)", checkStructure("*").toChainString());
        Assert.assertEquals("WildcardTheRest(/**)", checkStructure("/**").toChainString());
    }

    @Test
    public void captureTheRestPatterns() {
        pathPattern = parse("{*foobar}");
        Assert.assertEquals("/{*foobar}", pathPattern.computePatternString());
        assertPathElements(pathPattern, CaptureTheRestPathElement.class);
        pathPattern = checkStructure("/{*foobar}");
        assertPathElements(pathPattern, CaptureTheRestPathElement.class);
        checkError("/{*foobar}/", 10, NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST);
        checkError("/{*foobar}abc", 10, NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST);
        checkError("/{*f%obar}", 4, ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR);
        checkError("/{*foobar}abc", 10, NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST);
        checkError("/{f*oobar}", 3, ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR);
        checkError("/{*foobar}/abc", 10, NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST);
        checkError("/{*foobar:.*}/abc", 9, ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR);
        checkError("/{abc}{*foobar}", 1, CAPTURE_ALL_IS_STANDALONE_CONSTRUCT);
        checkError("/{abc}{*foobar}{foo}", 15, NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST);
    }

    @Test
    public void equalsAndHashcode() {
        PathPatternParser caseInsensitiveParser = new PathPatternParser();
        caseInsensitiveParser.setCaseSensitive(false);
        PathPatternParser caseSensitiveParser = new PathPatternParser();
        PathPattern pp1 = caseInsensitiveParser.parse("/abc");
        PathPattern pp2 = caseInsensitiveParser.parse("/abc");
        PathPattern pp3 = caseInsensitiveParser.parse("/def");
        Assert.assertEquals(pp1, pp2);
        Assert.assertEquals(pp1.hashCode(), pp2.hashCode());
        Assert.assertNotEquals(pp1, pp3);
        Assert.assertFalse(pp1.equals("abc"));
        pp1 = caseInsensitiveParser.parse("/abc");
        pp2 = caseSensitiveParser.parse("/abc");
        Assert.assertFalse(pp1.equals(pp2));
        Assert.assertNotEquals(pp1.hashCode(), pp2.hashCode());
    }

    @Test
    public void regexPathElementPatterns() {
        checkError("/{var:[^/]*}", 8, MISSING_CLOSE_CAPTURE);
        checkError("/{var:abc", 8, MISSING_CLOSE_CAPTURE);
        checkError("/{var:a{{1,2}}}", 6, REGEX_PATTERN_SYNTAX_EXCEPTION);
        pathPattern = checkStructure("/{var:\\\\}");
        PathElement next = pathPattern.getHeadSection().next;
        Assert.assertEquals(CaptureVariablePathElement.class.getName(), next.getClass().getName());
        assertMatches(pathPattern, "/\\");
        pathPattern = checkStructure("/{var:\\/}");
        next = pathPattern.getHeadSection().next;
        Assert.assertEquals(CaptureVariablePathElement.class.getName(), next.getClass().getName());
        assertNoMatch(pathPattern, "/aaa");
        pathPattern = checkStructure("/{var:a{1,2}}");
        next = pathPattern.getHeadSection().next;
        Assert.assertEquals(CaptureVariablePathElement.class.getName(), next.getClass().getName());
        pathPattern = checkStructure("/{var:[^\\/]*}");
        next = pathPattern.getHeadSection().next;
        Assert.assertEquals(CaptureVariablePathElement.class.getName(), next.getClass().getName());
        PathPattern.PathMatchInfo result = matchAndExtract(pathPattern, "/foo");
        Assert.assertEquals("foo", result.getUriVariables().get("var"));
        pathPattern = checkStructure("/{var:\\[*}");
        next = pathPattern.getHeadSection().next;
        Assert.assertEquals(CaptureVariablePathElement.class.getName(), next.getClass().getName());
        result = matchAndExtract(pathPattern, "/[[[");
        Assert.assertEquals("[[[", result.getUriVariables().get("var"));
        pathPattern = checkStructure("/{var:[\\{]*}");
        next = pathPattern.getHeadSection().next;
        Assert.assertEquals(CaptureVariablePathElement.class.getName(), next.getClass().getName());
        result = matchAndExtract(pathPattern, "/{{{");
        Assert.assertEquals("{{{", result.getUriVariables().get("var"));
        pathPattern = checkStructure("/{var:[\\}]*}");
        next = pathPattern.getHeadSection().next;
        Assert.assertEquals(CaptureVariablePathElement.class.getName(), next.getClass().getName());
        result = matchAndExtract(pathPattern, "/}}}");
        Assert.assertEquals("}}}", result.getUriVariables().get("var"));
        pathPattern = checkStructure("*");
        Assert.assertEquals(WildcardPathElement.class.getName(), pathPattern.getHeadSection().getClass().getName());
        checkStructure("/*");
        checkStructure("/*/");
        checkStructure("*/");
        checkStructure("/*/");
        pathPattern = checkStructure("/*a*/");
        next = pathPattern.getHeadSection().next;
        Assert.assertEquals(RegexPathElement.class.getName(), next.getClass().getName());
        pathPattern = checkStructure("*/");
        Assert.assertEquals(WildcardPathElement.class.getName(), pathPattern.getHeadSection().getClass().getName());
        checkError("{foo}_{foo}", 0, ILLEGAL_DOUBLE_CAPTURE, "foo");
        checkError("/{bar}/{bar}", 7, ILLEGAL_DOUBLE_CAPTURE, "bar");
        checkError("/{bar}/{bar}_{foo}", 7, ILLEGAL_DOUBLE_CAPTURE, "bar");
        pathPattern = checkStructure("{symbolicName:[\\p{L}\\.]+}-sources-{version:[\\p{N}\\.]+}.jar");
        Assert.assertEquals(RegexPathElement.class.getName(), pathPattern.getHeadSection().getClass().getName());
    }

    @Test
    public void completeCapturingPatterns() {
        pathPattern = checkStructure("{foo}");
        Assert.assertEquals(CaptureVariablePathElement.class.getName(), pathPattern.getHeadSection().getClass().getName());
        checkStructure("/{foo}");
        checkStructure("/{f}/");
        checkStructure("/{foo}/{bar}/{wibble}");
    }

    @Test
    public void noEncoding() {
        // Check no encoding of expressions or constraints
        PathPattern pp = parse("/{var:f o}");
        Assert.assertEquals("Separator(/) CaptureVariable({var:f o})", pp.toChainString());
        pp = parse("/{var:f o}_");
        Assert.assertEquals("Separator(/) Regex({var:f o}_)", pp.toChainString());
        pp = parse("{foo:f o}_ _{bar:b\\|o}");
        Assert.assertEquals("Regex({foo:f o}_ _{bar:b\\|o})", pp.toChainString());
    }

    @Test
    public void completeCaptureWithConstraints() {
        pathPattern = checkStructure("{foo:...}");
        assertPathElements(pathPattern, CaptureVariablePathElement.class);
        pathPattern = checkStructure("{foo:[0-9]*}");
        assertPathElements(pathPattern, CaptureVariablePathElement.class);
        checkError("{foo:}", 5, MISSING_REGEX_CONSTRAINT);
    }

    @Test
    public void partialCapturingPatterns() {
        pathPattern = checkStructure("{foo}abc");
        Assert.assertEquals(RegexPathElement.class.getName(), pathPattern.getHeadSection().getClass().getName());
        checkStructure("abc{foo}");
        checkStructure("/abc{foo}");
        checkStructure("{foo}def/");
        checkStructure("/abc{foo}def/");
        checkStructure("{foo}abc{bar}");
        checkStructure("{foo}abc{bar}/");
        checkStructure("/{foo}abc{bar}/");
    }

    @Test
    public void illegalCapturePatterns() {
        checkError("{abc/", 4, MISSING_CLOSE_CAPTURE);
        checkError("{abc:}/", 5, MISSING_REGEX_CONSTRAINT);
        checkError("{", 1, MISSING_CLOSE_CAPTURE);
        checkError("{abc", 4, MISSING_CLOSE_CAPTURE);
        checkError("{/}", 1, MISSING_CLOSE_CAPTURE);
        checkError("/{", 2, MISSING_CLOSE_CAPTURE);
        checkError("}", 0, MISSING_OPEN_CAPTURE);
        checkError("/}", 1, MISSING_OPEN_CAPTURE);
        checkError("def}", 3, MISSING_OPEN_CAPTURE);
        checkError("/{/}", 2, MISSING_CLOSE_CAPTURE);
        checkError("/{{/}", 2, ILLEGAL_NESTED_CAPTURE);
        checkError("/{abc{/}", 5, ILLEGAL_NESTED_CAPTURE);
        checkError("/{0abc}/abc", 2, ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR);
        checkError("/{a?bc}/abc", 3, ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR);
        checkError("/{abc}_{abc}", 1, ILLEGAL_DOUBLE_CAPTURE);
        checkError("/foobar/{abc}_{abc}", 8, ILLEGAL_DOUBLE_CAPTURE);
        checkError("/foobar/{abc:..}_{abc:..}", 8, ILLEGAL_DOUBLE_CAPTURE);
        PathPattern pp = parse("/{abc:foo(bar)}");
        try {
            pp.matchAndExtract(toPSC("/foo"));
            Assert.fail("Should have raised exception");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("No capture groups allowed in the constraint regex: foo(bar)", iae.getMessage());
        }
        try {
            pp.matchAndExtract(toPSC("/foobar"));
            Assert.fail("Should have raised exception");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("No capture groups allowed in the constraint regex: foo(bar)", iae.getMessage());
        }
    }

    @Test
    public void badPatterns() {
        // checkError("/{foo}{bar}/",6,PatternMessage.CANNOT_HAVE_ADJACENT_CAPTURES);
        checkError("/{?}/", 2, ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR, "?");
        checkError("/{a?b}/", 3, ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR, "?");
        checkError("/{%%$}", 2, ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR, "%");
        checkError("/{ }", 2, ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR, " ");
        checkError("/{%:[0-9]*}", 2, ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR, "%");
    }

    @Test
    public void patternPropertyGetCaptureCountTests() {
        // Test all basic section types
        Assert.assertEquals(1, parse("{foo}").getCapturedVariableCount());
        Assert.assertEquals(0, parse("foo").getCapturedVariableCount());
        Assert.assertEquals(1, parse("{*foobar}").getCapturedVariableCount());
        Assert.assertEquals(1, parse("/{*foobar}").getCapturedVariableCount());
        Assert.assertEquals(0, parse("/**").getCapturedVariableCount());
        Assert.assertEquals(1, parse("{abc}asdf").getCapturedVariableCount());
        Assert.assertEquals(1, parse("{abc}_*").getCapturedVariableCount());
        Assert.assertEquals(2, parse("{abc}_{def}").getCapturedVariableCount());
        Assert.assertEquals(0, parse("/").getCapturedVariableCount());
        Assert.assertEquals(0, parse("a?b").getCapturedVariableCount());
        Assert.assertEquals(0, parse("*").getCapturedVariableCount());
        // Test on full templates
        Assert.assertEquals(0, parse("/foo/bar").getCapturedVariableCount());
        Assert.assertEquals(1, parse("/{foo}").getCapturedVariableCount());
        Assert.assertEquals(2, parse("/{foo}/{bar}").getCapturedVariableCount());
        Assert.assertEquals(4, parse("/{foo}/{bar}_{goo}_{wibble}/abc/bar").getCapturedVariableCount());
    }

    @Test
    public void patternPropertyGetWildcardCountTests() {
        // Test all basic section types
        Assert.assertEquals(computeScore(1, 0), parse("{foo}").getScore());
        Assert.assertEquals(computeScore(0, 0), parse("foo").getScore());
        Assert.assertEquals(computeScore(0, 0), parse("{*foobar}").getScore());
        // assertEquals(1,parse("/**").getScore());
        Assert.assertEquals(computeScore(1, 0), parse("{abc}asdf").getScore());
        Assert.assertEquals(computeScore(1, 1), parse("{abc}_*").getScore());
        Assert.assertEquals(computeScore(2, 0), parse("{abc}_{def}").getScore());
        Assert.assertEquals(computeScore(0, 0), parse("/").getScore());
        Assert.assertEquals(computeScore(0, 0), parse("a?b").getScore());// currently deliberate

        Assert.assertEquals(computeScore(0, 1), parse("*").getScore());
        // Test on full templates
        Assert.assertEquals(computeScore(0, 0), parse("/foo/bar").getScore());
        Assert.assertEquals(computeScore(1, 0), parse("/{foo}").getScore());
        Assert.assertEquals(computeScore(2, 0), parse("/{foo}/{bar}").getScore());
        Assert.assertEquals(computeScore(4, 0), parse("/{foo}/{bar}_{goo}_{wibble}/abc/bar").getScore());
        Assert.assertEquals(computeScore(4, 3), parse("/{foo}/*/*_*/{bar}_{goo}_{wibble}/abc/bar").getScore());
    }

    @Test
    public void multipleSeparatorPatterns() {
        pathPattern = checkStructure("///aaa");
        Assert.assertEquals(6, pathPattern.getNormalizedLength());
        assertPathElements(pathPattern, SeparatorPathElement.class, SeparatorPathElement.class, SeparatorPathElement.class, LiteralPathElement.class);
        pathPattern = checkStructure("///aaa////aaa/b");
        Assert.assertEquals(15, pathPattern.getNormalizedLength());
        assertPathElements(pathPattern, SeparatorPathElement.class, SeparatorPathElement.class, SeparatorPathElement.class, LiteralPathElement.class, SeparatorPathElement.class, SeparatorPathElement.class, SeparatorPathElement.class, SeparatorPathElement.class, LiteralPathElement.class, SeparatorPathElement.class, LiteralPathElement.class);
        pathPattern = checkStructure("/////**");
        Assert.assertEquals(5, pathPattern.getNormalizedLength());
        assertPathElements(pathPattern, SeparatorPathElement.class, SeparatorPathElement.class, SeparatorPathElement.class, SeparatorPathElement.class, WildcardTheRestPathElement.class);
    }

    @Test
    public void patternPropertyGetLengthTests() {
        // Test all basic section types
        Assert.assertEquals(1, parse("{foo}").getNormalizedLength());
        Assert.assertEquals(3, parse("foo").getNormalizedLength());
        Assert.assertEquals(1, parse("{*foobar}").getNormalizedLength());
        Assert.assertEquals(1, parse("/{*foobar}").getNormalizedLength());
        Assert.assertEquals(1, parse("/**").getNormalizedLength());
        Assert.assertEquals(5, parse("{abc}asdf").getNormalizedLength());
        Assert.assertEquals(3, parse("{abc}_*").getNormalizedLength());
        Assert.assertEquals(3, parse("{abc}_{def}").getNormalizedLength());
        Assert.assertEquals(1, parse("/").getNormalizedLength());
        Assert.assertEquals(3, parse("a?b").getNormalizedLength());
        Assert.assertEquals(1, parse("*").getNormalizedLength());
        // Test on full templates
        Assert.assertEquals(8, parse("/foo/bar").getNormalizedLength());
        Assert.assertEquals(2, parse("/{foo}").getNormalizedLength());
        Assert.assertEquals(4, parse("/{foo}/{bar}").getNormalizedLength());
        Assert.assertEquals(16, parse("/{foo}/{bar}_{goo}_{wibble}/abc/bar").getNormalizedLength());
    }

    @Test
    public void compareTests() {
        PathPattern p1;
        PathPattern p2;
        PathPattern p3;
        // Based purely on number of captures
        p1 = parse("{a}");
        p2 = parse("{a}/{b}");
        p3 = parse("{a}/{b}/{c}");
        Assert.assertEquals((-1), p1.compareTo(p2));// Based on number of captures

        List<PathPattern> patterns = new ArrayList<>();
        patterns.add(p2);
        patterns.add(p3);
        patterns.add(p1);
        Collections.sort(patterns);
        Assert.assertEquals(p1, patterns.get(0));
        // Based purely on length
        p1 = parse("/a/b/c");
        p2 = parse("/a/boo/c/doo");
        p3 = parse("/asdjflaksjdfjasdf");
        Assert.assertEquals(1, p1.compareTo(p2));
        patterns = new ArrayList();
        patterns.add(p2);
        patterns.add(p3);
        patterns.add(p1);
        Collections.sort(patterns);
        Assert.assertEquals(p3, patterns.get(0));
        // Based purely on 'wildness'
        p1 = parse("/*");
        p2 = parse("/*/*");
        p3 = parse("/*/*/*_*");
        Assert.assertEquals((-1), p1.compareTo(p2));
        patterns = new ArrayList();
        patterns.add(p2);
        patterns.add(p3);
        patterns.add(p1);
        Collections.sort(patterns);
        Assert.assertEquals(p1, patterns.get(0));
        // Based purely on catchAll
        p1 = parse("{*foobar}");
        p2 = parse("{*goo}");
        Assert.assertTrue(((p1.compareTo(p2)) != 0));
        p1 = parse("/{*foobar}");
        p2 = parse("/abc/{*ww}");
        Assert.assertEquals((+1), p1.compareTo(p2));
        Assert.assertEquals((-1), p2.compareTo(p1));
        p3 = parse("/this/that/theother");
        Assert.assertTrue(p1.isCatchAll());
        Assert.assertTrue(p2.isCatchAll());
        Assert.assertFalse(p3.isCatchAll());
        patterns = new ArrayList();
        patterns.add(p2);
        patterns.add(p3);
        patterns.add(p1);
        Collections.sort(patterns);
        Assert.assertEquals(p3, patterns.get(0));
        Assert.assertEquals(p2, patterns.get(1));
    }
}

