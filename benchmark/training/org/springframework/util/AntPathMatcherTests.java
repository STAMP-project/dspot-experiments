/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link AntPathMatcher}.
 *
 * @author Alef Arendsen
 * @author Seth Ladd
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 */
public class AntPathMatcherTests {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void match() {
        // test exact matching
        Assert.assertTrue(pathMatcher.match("test", "test"));
        Assert.assertTrue(pathMatcher.match("/test", "/test"));
        Assert.assertTrue(pathMatcher.match("http://example.org", "http://example.org"));// SPR-14141

        Assert.assertFalse(pathMatcher.match("/test.jpg", "test.jpg"));
        Assert.assertFalse(pathMatcher.match("test", "/test"));
        Assert.assertFalse(pathMatcher.match("/test", "test"));
        // test matching with ?'s
        Assert.assertTrue(pathMatcher.match("t?st", "test"));
        Assert.assertTrue(pathMatcher.match("??st", "test"));
        Assert.assertTrue(pathMatcher.match("tes?", "test"));
        Assert.assertTrue(pathMatcher.match("te??", "test"));
        Assert.assertTrue(pathMatcher.match("?es?", "test"));
        Assert.assertFalse(pathMatcher.match("tes?", "tes"));
        Assert.assertFalse(pathMatcher.match("tes?", "testt"));
        Assert.assertFalse(pathMatcher.match("tes?", "tsst"));
        // test matching with *'s
        Assert.assertTrue(pathMatcher.match("*", "test"));
        Assert.assertTrue(pathMatcher.match("test*", "test"));
        Assert.assertTrue(pathMatcher.match("test*", "testTest"));
        Assert.assertTrue(pathMatcher.match("test/*", "test/Test"));
        Assert.assertTrue(pathMatcher.match("test/*", "test/t"));
        Assert.assertTrue(pathMatcher.match("test/*", "test/"));
        Assert.assertTrue(pathMatcher.match("*test*", "AnothertestTest"));
        Assert.assertTrue(pathMatcher.match("*test", "Anothertest"));
        Assert.assertTrue(pathMatcher.match("*.*", "test."));
        Assert.assertTrue(pathMatcher.match("*.*", "test.test"));
        Assert.assertTrue(pathMatcher.match("*.*", "test.test.test"));
        Assert.assertTrue(pathMatcher.match("test*aaa", "testblaaaa"));
        Assert.assertFalse(pathMatcher.match("test*", "tst"));
        Assert.assertFalse(pathMatcher.match("test*", "tsttest"));
        Assert.assertFalse(pathMatcher.match("test*", "test/"));
        Assert.assertFalse(pathMatcher.match("test*", "test/t"));
        Assert.assertFalse(pathMatcher.match("test/*", "test"));
        Assert.assertFalse(pathMatcher.match("*test*", "tsttst"));
        Assert.assertFalse(pathMatcher.match("*test", "tsttst"));
        Assert.assertFalse(pathMatcher.match("*.*", "tsttst"));
        Assert.assertFalse(pathMatcher.match("test*aaa", "test"));
        Assert.assertFalse(pathMatcher.match("test*aaa", "testblaaab"));
        // test matching with ?'s and /'s
        Assert.assertTrue(pathMatcher.match("/?", "/a"));
        Assert.assertTrue(pathMatcher.match("/?/a", "/a/a"));
        Assert.assertTrue(pathMatcher.match("/a/?", "/a/b"));
        Assert.assertTrue(pathMatcher.match("/??/a", "/aa/a"));
        Assert.assertTrue(pathMatcher.match("/a/??", "/a/bb"));
        Assert.assertTrue(pathMatcher.match("/?", "/a"));
        // test matching with **'s
        Assert.assertTrue(pathMatcher.match("/**", "/testing/testing"));
        Assert.assertTrue(pathMatcher.match("/*/**", "/testing/testing"));
        Assert.assertTrue(pathMatcher.match("/**/*", "/testing/testing"));
        Assert.assertTrue(pathMatcher.match("/bla/**/bla", "/bla/testing/testing/bla"));
        Assert.assertTrue(pathMatcher.match("/bla/**/bla", "/bla/testing/testing/bla/bla"));
        Assert.assertTrue(pathMatcher.match("/**/test", "/bla/bla/test"));
        Assert.assertTrue(pathMatcher.match("/bla/**/**/bla", "/bla/bla/bla/bla/bla/bla"));
        Assert.assertTrue(pathMatcher.match("/bla*bla/test", "/blaXXXbla/test"));
        Assert.assertTrue(pathMatcher.match("/*bla/test", "/XXXbla/test"));
        Assert.assertFalse(pathMatcher.match("/bla*bla/test", "/blaXXXbl/test"));
        Assert.assertFalse(pathMatcher.match("/*bla/test", "XXXblab/test"));
        Assert.assertFalse(pathMatcher.match("/*bla/test", "XXXbl/test"));
        Assert.assertFalse(pathMatcher.match("/????", "/bala/bla"));
        Assert.assertFalse(pathMatcher.match("/**/*bla", "/bla/bla/bla/bbb"));
        Assert.assertTrue(pathMatcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing/"));
        Assert.assertTrue(pathMatcher.match("/*bla*/**/bla/*", "/XXXblaXXXX/testing/testing/bla/testing"));
        Assert.assertTrue(pathMatcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing"));
        Assert.assertTrue(pathMatcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing.jpg"));
        Assert.assertTrue(pathMatcher.match("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing/"));
        Assert.assertTrue(pathMatcher.match("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing"));
        Assert.assertTrue(pathMatcher.match("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing"));
        Assert.assertFalse(pathMatcher.match("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing/testing"));
        Assert.assertFalse(pathMatcher.match("/x/x/**/bla", "/x/x/x/"));
        Assert.assertTrue(pathMatcher.match("/foo/bar/**", "/foo/bar"));
        Assert.assertTrue(pathMatcher.match("", ""));
        Assert.assertTrue(pathMatcher.match("/{bla}.*", "/testing.html"));
    }

    // SPR-14247
    @Test
    public void matchWithTrimTokensEnabled() throws Exception {
        pathMatcher.setTrimTokens(true);
        Assert.assertTrue(pathMatcher.match("/foo/bar", "/foo /bar"));
    }

    @Test
    public void withMatchStart() {
        // test exact matching
        Assert.assertTrue(pathMatcher.matchStart("test", "test"));
        Assert.assertTrue(pathMatcher.matchStart("/test", "/test"));
        Assert.assertFalse(pathMatcher.matchStart("/test.jpg", "test.jpg"));
        Assert.assertFalse(pathMatcher.matchStart("test", "/test"));
        Assert.assertFalse(pathMatcher.matchStart("/test", "test"));
        // test matching with ?'s
        Assert.assertTrue(pathMatcher.matchStart("t?st", "test"));
        Assert.assertTrue(pathMatcher.matchStart("??st", "test"));
        Assert.assertTrue(pathMatcher.matchStart("tes?", "test"));
        Assert.assertTrue(pathMatcher.matchStart("te??", "test"));
        Assert.assertTrue(pathMatcher.matchStart("?es?", "test"));
        Assert.assertFalse(pathMatcher.matchStart("tes?", "tes"));
        Assert.assertFalse(pathMatcher.matchStart("tes?", "testt"));
        Assert.assertFalse(pathMatcher.matchStart("tes?", "tsst"));
        // test matching with *'s
        Assert.assertTrue(pathMatcher.matchStart("*", "test"));
        Assert.assertTrue(pathMatcher.matchStart("test*", "test"));
        Assert.assertTrue(pathMatcher.matchStart("test*", "testTest"));
        Assert.assertTrue(pathMatcher.matchStart("test/*", "test/Test"));
        Assert.assertTrue(pathMatcher.matchStart("test/*", "test/t"));
        Assert.assertTrue(pathMatcher.matchStart("test/*", "test/"));
        Assert.assertTrue(pathMatcher.matchStart("*test*", "AnothertestTest"));
        Assert.assertTrue(pathMatcher.matchStart("*test", "Anothertest"));
        Assert.assertTrue(pathMatcher.matchStart("*.*", "test."));
        Assert.assertTrue(pathMatcher.matchStart("*.*", "test.test"));
        Assert.assertTrue(pathMatcher.matchStart("*.*", "test.test.test"));
        Assert.assertTrue(pathMatcher.matchStart("test*aaa", "testblaaaa"));
        Assert.assertFalse(pathMatcher.matchStart("test*", "tst"));
        Assert.assertFalse(pathMatcher.matchStart("test*", "test/"));
        Assert.assertFalse(pathMatcher.matchStart("test*", "tsttest"));
        Assert.assertFalse(pathMatcher.matchStart("test*", "test/"));
        Assert.assertFalse(pathMatcher.matchStart("test*", "test/t"));
        Assert.assertTrue(pathMatcher.matchStart("test/*", "test"));
        Assert.assertTrue(pathMatcher.matchStart("test/t*.txt", "test"));
        Assert.assertFalse(pathMatcher.matchStart("*test*", "tsttst"));
        Assert.assertFalse(pathMatcher.matchStart("*test", "tsttst"));
        Assert.assertFalse(pathMatcher.matchStart("*.*", "tsttst"));
        Assert.assertFalse(pathMatcher.matchStart("test*aaa", "test"));
        Assert.assertFalse(pathMatcher.matchStart("test*aaa", "testblaaab"));
        // test matching with ?'s and /'s
        Assert.assertTrue(pathMatcher.matchStart("/?", "/a"));
        Assert.assertTrue(pathMatcher.matchStart("/?/a", "/a/a"));
        Assert.assertTrue(pathMatcher.matchStart("/a/?", "/a/b"));
        Assert.assertTrue(pathMatcher.matchStart("/??/a", "/aa/a"));
        Assert.assertTrue(pathMatcher.matchStart("/a/??", "/a/bb"));
        Assert.assertTrue(pathMatcher.matchStart("/?", "/a"));
        // test matching with **'s
        Assert.assertTrue(pathMatcher.matchStart("/**", "/testing/testing"));
        Assert.assertTrue(pathMatcher.matchStart("/*/**", "/testing/testing"));
        Assert.assertTrue(pathMatcher.matchStart("/**/*", "/testing/testing"));
        Assert.assertTrue(pathMatcher.matchStart("test*/**", "test/"));
        Assert.assertTrue(pathMatcher.matchStart("test*/**", "test/t"));
        Assert.assertTrue(pathMatcher.matchStart("/bla/**/bla", "/bla/testing/testing/bla"));
        Assert.assertTrue(pathMatcher.matchStart("/bla/**/bla", "/bla/testing/testing/bla/bla"));
        Assert.assertTrue(pathMatcher.matchStart("/**/test", "/bla/bla/test"));
        Assert.assertTrue(pathMatcher.matchStart("/bla/**/**/bla", "/bla/bla/bla/bla/bla/bla"));
        Assert.assertTrue(pathMatcher.matchStart("/bla*bla/test", "/blaXXXbla/test"));
        Assert.assertTrue(pathMatcher.matchStart("/*bla/test", "/XXXbla/test"));
        Assert.assertFalse(pathMatcher.matchStart("/bla*bla/test", "/blaXXXbl/test"));
        Assert.assertFalse(pathMatcher.matchStart("/*bla/test", "XXXblab/test"));
        Assert.assertFalse(pathMatcher.matchStart("/*bla/test", "XXXbl/test"));
        Assert.assertFalse(pathMatcher.matchStart("/????", "/bala/bla"));
        Assert.assertTrue(pathMatcher.matchStart("/**/*bla", "/bla/bla/bla/bbb"));
        Assert.assertTrue(pathMatcher.matchStart("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing/"));
        Assert.assertTrue(pathMatcher.matchStart("/*bla*/**/bla/*", "/XXXblaXXXX/testing/testing/bla/testing"));
        Assert.assertTrue(pathMatcher.matchStart("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing"));
        Assert.assertTrue(pathMatcher.matchStart("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing.jpg"));
        Assert.assertTrue(pathMatcher.matchStart("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing/"));
        Assert.assertTrue(pathMatcher.matchStart("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing"));
        Assert.assertTrue(pathMatcher.matchStart("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing"));
        Assert.assertTrue(pathMatcher.matchStart("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing/testing"));
        Assert.assertTrue(pathMatcher.matchStart("/x/x/**/bla", "/x/x/x/"));
        Assert.assertTrue(pathMatcher.matchStart("", ""));
    }

    @Test
    public void uniqueDeliminator() {
        pathMatcher.setPathSeparator(".");
        // test exact matching
        Assert.assertTrue(pathMatcher.match("test", "test"));
        Assert.assertTrue(pathMatcher.match(".test", ".test"));
        Assert.assertFalse(pathMatcher.match(".test/jpg", "test/jpg"));
        Assert.assertFalse(pathMatcher.match("test", ".test"));
        Assert.assertFalse(pathMatcher.match(".test", "test"));
        // test matching with ?'s
        Assert.assertTrue(pathMatcher.match("t?st", "test"));
        Assert.assertTrue(pathMatcher.match("??st", "test"));
        Assert.assertTrue(pathMatcher.match("tes?", "test"));
        Assert.assertTrue(pathMatcher.match("te??", "test"));
        Assert.assertTrue(pathMatcher.match("?es?", "test"));
        Assert.assertFalse(pathMatcher.match("tes?", "tes"));
        Assert.assertFalse(pathMatcher.match("tes?", "testt"));
        Assert.assertFalse(pathMatcher.match("tes?", "tsst"));
        // test matching with *'s
        Assert.assertTrue(pathMatcher.match("*", "test"));
        Assert.assertTrue(pathMatcher.match("test*", "test"));
        Assert.assertTrue(pathMatcher.match("test*", "testTest"));
        Assert.assertTrue(pathMatcher.match("*test*", "AnothertestTest"));
        Assert.assertTrue(pathMatcher.match("*test", "Anothertest"));
        Assert.assertTrue(pathMatcher.match("*/*", "test/"));
        Assert.assertTrue(pathMatcher.match("*/*", "test/test"));
        Assert.assertTrue(pathMatcher.match("*/*", "test/test/test"));
        Assert.assertTrue(pathMatcher.match("test*aaa", "testblaaaa"));
        Assert.assertFalse(pathMatcher.match("test*", "tst"));
        Assert.assertFalse(pathMatcher.match("test*", "tsttest"));
        Assert.assertFalse(pathMatcher.match("*test*", "tsttst"));
        Assert.assertFalse(pathMatcher.match("*test", "tsttst"));
        Assert.assertFalse(pathMatcher.match("*/*", "tsttst"));
        Assert.assertFalse(pathMatcher.match("test*aaa", "test"));
        Assert.assertFalse(pathMatcher.match("test*aaa", "testblaaab"));
        // test matching with ?'s and .'s
        Assert.assertTrue(pathMatcher.match(".?", ".a"));
        Assert.assertTrue(pathMatcher.match(".?.a", ".a.a"));
        Assert.assertTrue(pathMatcher.match(".a.?", ".a.b"));
        Assert.assertTrue(pathMatcher.match(".??.a", ".aa.a"));
        Assert.assertTrue(pathMatcher.match(".a.??", ".a.bb"));
        Assert.assertTrue(pathMatcher.match(".?", ".a"));
        // test matching with **'s
        Assert.assertTrue(pathMatcher.match(".**", ".testing.testing"));
        Assert.assertTrue(pathMatcher.match(".*.**", ".testing.testing"));
        Assert.assertTrue(pathMatcher.match(".**.*", ".testing.testing"));
        Assert.assertTrue(pathMatcher.match(".bla.**.bla", ".bla.testing.testing.bla"));
        Assert.assertTrue(pathMatcher.match(".bla.**.bla", ".bla.testing.testing.bla.bla"));
        Assert.assertTrue(pathMatcher.match(".**.test", ".bla.bla.test"));
        Assert.assertTrue(pathMatcher.match(".bla.**.**.bla", ".bla.bla.bla.bla.bla.bla"));
        Assert.assertTrue(pathMatcher.match(".bla*bla.test", ".blaXXXbla.test"));
        Assert.assertTrue(pathMatcher.match(".*bla.test", ".XXXbla.test"));
        Assert.assertFalse(pathMatcher.match(".bla*bla.test", ".blaXXXbl.test"));
        Assert.assertFalse(pathMatcher.match(".*bla.test", "XXXblab.test"));
        Assert.assertFalse(pathMatcher.match(".*bla.test", "XXXbl.test"));
    }

    @Test
    public void extractPathWithinPattern() throws Exception {
        Assert.assertEquals("", pathMatcher.extractPathWithinPattern("/docs/commit.html", "/docs/commit.html"));
        Assert.assertEquals("cvs/commit", pathMatcher.extractPathWithinPattern("/docs/*", "/docs/cvs/commit"));
        Assert.assertEquals("commit.html", pathMatcher.extractPathWithinPattern("/docs/cvs/*.html", "/docs/cvs/commit.html"));
        Assert.assertEquals("cvs/commit", pathMatcher.extractPathWithinPattern("/docs/**", "/docs/cvs/commit"));
        Assert.assertEquals("cvs/commit.html", pathMatcher.extractPathWithinPattern("/docs/**/*.html", "/docs/cvs/commit.html"));
        Assert.assertEquals("commit.html", pathMatcher.extractPathWithinPattern("/docs/**/*.html", "/docs/commit.html"));
        Assert.assertEquals("commit.html", pathMatcher.extractPathWithinPattern("/*.html", "/commit.html"));
        Assert.assertEquals("docs/commit.html", pathMatcher.extractPathWithinPattern("/*.html", "/docs/commit.html"));
        Assert.assertEquals("/commit.html", pathMatcher.extractPathWithinPattern("*.html", "/commit.html"));
        Assert.assertEquals("/docs/commit.html", pathMatcher.extractPathWithinPattern("*.html", "/docs/commit.html"));
        Assert.assertEquals("/docs/commit.html", pathMatcher.extractPathWithinPattern("**/*.*", "/docs/commit.html"));
        Assert.assertEquals("/docs/commit.html", pathMatcher.extractPathWithinPattern("*", "/docs/commit.html"));
        // SPR-10515
        Assert.assertEquals("/docs/cvs/other/commit.html", pathMatcher.extractPathWithinPattern("**/commit.html", "/docs/cvs/other/commit.html"));
        Assert.assertEquals("cvs/other/commit.html", pathMatcher.extractPathWithinPattern("/docs/**/commit.html", "/docs/cvs/other/commit.html"));
        Assert.assertEquals("cvs/other/commit.html", pathMatcher.extractPathWithinPattern("/docs/**/**/**/**", "/docs/cvs/other/commit.html"));
        Assert.assertEquals("docs/cvs/commit", pathMatcher.extractPathWithinPattern("/d?cs/*", "/docs/cvs/commit"));
        Assert.assertEquals("cvs/commit.html", pathMatcher.extractPathWithinPattern("/docs/c?s/*.html", "/docs/cvs/commit.html"));
        Assert.assertEquals("docs/cvs/commit", pathMatcher.extractPathWithinPattern("/d?cs/**", "/docs/cvs/commit"));
        Assert.assertEquals("docs/cvs/commit.html", pathMatcher.extractPathWithinPattern("/d?cs/**/*.html", "/docs/cvs/commit.html"));
    }

    @Test
    public void extractUriTemplateVariables() throws Exception {
        Map<String, String> result = pathMatcher.extractUriTemplateVariables("/hotels/{hotel}", "/hotels/1");
        Assert.assertEquals(Collections.singletonMap("hotel", "1"), result);
        result = pathMatcher.extractUriTemplateVariables("/h?tels/{hotel}", "/hotels/1");
        Assert.assertEquals(Collections.singletonMap("hotel", "1"), result);
        result = pathMatcher.extractUriTemplateVariables("/hotels/{hotel}/bookings/{booking}", "/hotels/1/bookings/2");
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("hotel", "1");
        expected.put("booking", "2");
        Assert.assertEquals(expected, result);
        result = pathMatcher.extractUriTemplateVariables("/**/hotels/**/{hotel}", "/foo/hotels/bar/1");
        Assert.assertEquals(Collections.singletonMap("hotel", "1"), result);
        result = pathMatcher.extractUriTemplateVariables("/{page}.html", "/42.html");
        Assert.assertEquals(Collections.singletonMap("page", "42"), result);
        result = pathMatcher.extractUriTemplateVariables("/{page}.*", "/42.html");
        Assert.assertEquals(Collections.singletonMap("page", "42"), result);
        result = pathMatcher.extractUriTemplateVariables("/A-{B}-C", "/A-b-C");
        Assert.assertEquals(Collections.singletonMap("B", "b"), result);
        result = pathMatcher.extractUriTemplateVariables("/{name}.{extension}", "/test.html");
        expected = new LinkedHashMap<>();
        expected.put("name", "test");
        expected.put("extension", "html");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void extractUriTemplateVariablesRegex() {
        Map<String, String> result = pathMatcher.extractUriTemplateVariables("{symbolicName:[\\w\\.]+}-{version:[\\w\\.]+}.jar", "com.example-1.0.0.jar");
        Assert.assertEquals("com.example", result.get("symbolicName"));
        Assert.assertEquals("1.0.0", result.get("version"));
        result = pathMatcher.extractUriTemplateVariables("{symbolicName:[\\w\\.]+}-sources-{version:[\\w\\.]+}.jar", "com.example-sources-1.0.0.jar");
        Assert.assertEquals("com.example", result.get("symbolicName"));
        Assert.assertEquals("1.0.0", result.get("version"));
    }

    /**
     * SPR-7787
     */
    @Test
    public void extractUriTemplateVarsRegexQualifiers() {
        Map<String, String> result = pathMatcher.extractUriTemplateVariables("{symbolicName:[\\p{L}\\.]+}-sources-{version:[\\p{N}\\.]+}.jar", "com.example-sources-1.0.0.jar");
        Assert.assertEquals("com.example", result.get("symbolicName"));
        Assert.assertEquals("1.0.0", result.get("version"));
        result = pathMatcher.extractUriTemplateVariables("{symbolicName:[\\w\\.]+}-sources-{version:[\\d\\.]+}-{year:\\d{4}}{month:\\d{2}}{day:\\d{2}}.jar", "com.example-sources-1.0.0-20100220.jar");
        Assert.assertEquals("com.example", result.get("symbolicName"));
        Assert.assertEquals("1.0.0", result.get("version"));
        Assert.assertEquals("2010", result.get("year"));
        Assert.assertEquals("02", result.get("month"));
        Assert.assertEquals("20", result.get("day"));
        result = pathMatcher.extractUriTemplateVariables("{symbolicName:[\\p{L}\\.]+}-sources-{version:[\\p{N}\\.\\{\\}]+}.jar", "com.example-sources-1.0.0.{12}.jar");
        Assert.assertEquals("com.example", result.get("symbolicName"));
        Assert.assertEquals("1.0.0.{12}", result.get("version"));
    }

    /**
     * SPR-8455
     */
    @Test
    public void extractUriTemplateVarsRegexCapturingGroups() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.containsString("The number of capturing groups in the pattern"));
        pathMatcher.extractUriTemplateVariables("/web/{id:foo(bar)?}", "/web/foobar");
    }

    @Test
    public void combine() {
        Assert.assertEquals("", pathMatcher.combine(null, null));
        Assert.assertEquals("/hotels", pathMatcher.combine("/hotels", null));
        Assert.assertEquals("/hotels", pathMatcher.combine(null, "/hotels"));
        Assert.assertEquals("/hotels/booking", pathMatcher.combine("/hotels/*", "booking"));
        Assert.assertEquals("/hotels/booking", pathMatcher.combine("/hotels/*", "/booking"));
        Assert.assertEquals("/hotels/**/booking", pathMatcher.combine("/hotels/**", "booking"));
        Assert.assertEquals("/hotels/**/booking", pathMatcher.combine("/hotels/**", "/booking"));
        Assert.assertEquals("/hotels/booking", pathMatcher.combine("/hotels", "/booking"));
        Assert.assertEquals("/hotels/booking", pathMatcher.combine("/hotels", "booking"));
        Assert.assertEquals("/hotels/booking", pathMatcher.combine("/hotels/", "booking"));
        Assert.assertEquals("/hotels/{hotel}", pathMatcher.combine("/hotels/*", "{hotel}"));
        Assert.assertEquals("/hotels/**/{hotel}", pathMatcher.combine("/hotels/**", "{hotel}"));
        Assert.assertEquals("/hotels/{hotel}", pathMatcher.combine("/hotels", "{hotel}"));
        Assert.assertEquals("/hotels/{hotel}.*", pathMatcher.combine("/hotels", "{hotel}.*"));
        Assert.assertEquals("/hotels/*/booking/{booking}", pathMatcher.combine("/hotels/*/booking", "{booking}"));
        Assert.assertEquals("/hotel.html", pathMatcher.combine("/*.html", "/hotel.html"));
        Assert.assertEquals("/hotel.html", pathMatcher.combine("/*.html", "/hotel"));
        Assert.assertEquals("/hotel.html", pathMatcher.combine("/*.html", "/hotel.*"));
        Assert.assertEquals("/*.html", pathMatcher.combine("/**", "/*.html"));
        Assert.assertEquals("/*.html", pathMatcher.combine("/*", "/*.html"));
        Assert.assertEquals("/*.html", pathMatcher.combine("/*.*", "/*.html"));
        Assert.assertEquals("/{foo}/bar", pathMatcher.combine("/{foo}", "/bar"));
        // SPR-8858
        Assert.assertEquals("/user/user", pathMatcher.combine("/user", "/user"));
        // SPR-7970
        Assert.assertEquals("/{foo:.*[^0-9].*}/edit/", pathMatcher.combine("/{foo:.*[^0-9].*}", "/edit/"));// SPR-10062

        Assert.assertEquals("/1.0/foo/test", pathMatcher.combine("/1.0", "/foo/test"));// SPR-10554

        Assert.assertEquals("/hotel", pathMatcher.combine("/", "/hotel"));// SPR-12975

        Assert.assertEquals("/hotel/booking", pathMatcher.combine("/hotel/", "/booking"));// SPR-12975

    }

    @Test
    public void combineWithTwoFileExtensionPatterns() {
        exception.expect(IllegalArgumentException.class);
        pathMatcher.combine("/*.html", "/*.txt");
    }

    @Test
    public void patternComparator() {
        Comparator<String> comparator = pathMatcher.getPatternComparator("/hotels/new");
        Assert.assertEquals(0, comparator.compare(null, null));
        Assert.assertEquals(1, comparator.compare(null, "/hotels/new"));
        Assert.assertEquals((-1), comparator.compare("/hotels/new", null));
        Assert.assertEquals(0, comparator.compare("/hotels/new", "/hotels/new"));
        Assert.assertEquals((-1), comparator.compare("/hotels/new", "/hotels/*"));
        Assert.assertEquals(1, comparator.compare("/hotels/*", "/hotels/new"));
        Assert.assertEquals(0, comparator.compare("/hotels/*", "/hotels/*"));
        Assert.assertEquals((-1), comparator.compare("/hotels/new", "/hotels/{hotel}"));
        Assert.assertEquals(1, comparator.compare("/hotels/{hotel}", "/hotels/new"));
        Assert.assertEquals(0, comparator.compare("/hotels/{hotel}", "/hotels/{hotel}"));
        Assert.assertEquals((-1), comparator.compare("/hotels/{hotel}/booking", "/hotels/{hotel}/bookings/{booking}"));
        Assert.assertEquals(1, comparator.compare("/hotels/{hotel}/bookings/{booking}", "/hotels/{hotel}/booking"));
        // SPR-10550
        Assert.assertEquals((-1), comparator.compare("/hotels/{hotel}/bookings/{booking}/cutomers/{customer}", "/**"));
        Assert.assertEquals(1, comparator.compare("/**", "/hotels/{hotel}/bookings/{booking}/cutomers/{customer}"));
        Assert.assertEquals(0, comparator.compare("/**", "/**"));
        Assert.assertEquals((-1), comparator.compare("/hotels/{hotel}", "/hotels/*"));
        Assert.assertEquals(1, comparator.compare("/hotels/*", "/hotels/{hotel}"));
        Assert.assertEquals((-1), comparator.compare("/hotels/*", "/hotels/*/**"));
        Assert.assertEquals(1, comparator.compare("/hotels/*/**", "/hotels/*"));
        Assert.assertEquals((-1), comparator.compare("/hotels/new", "/hotels/new.*"));
        Assert.assertEquals(2, comparator.compare("/hotels/{hotel}", "/hotels/{hotel}.*"));
        // SPR-6741
        Assert.assertEquals((-1), comparator.compare("/hotels/{hotel}/bookings/{booking}/cutomers/{customer}", "/hotels/**"));
        Assert.assertEquals(1, comparator.compare("/hotels/**", "/hotels/{hotel}/bookings/{booking}/cutomers/{customer}"));
        Assert.assertEquals(1, comparator.compare("/hotels/foo/bar/**", "/hotels/{hotel}"));
        Assert.assertEquals((-1), comparator.compare("/hotels/{hotel}", "/hotels/foo/bar/**"));
        Assert.assertEquals(2, comparator.compare("/hotels/**/bookings/**", "/hotels/**"));
        Assert.assertEquals((-2), comparator.compare("/hotels/**", "/hotels/**/bookings/**"));
        // SPR-8683
        Assert.assertEquals(1, comparator.compare("/**", "/hotels/{hotel}"));
        // longer is better
        Assert.assertEquals(1, comparator.compare("/hotels", "/hotels2"));
        // SPR-13139
        Assert.assertEquals((-1), comparator.compare("*", "*/**"));
        Assert.assertEquals(1, comparator.compare("*/**", "*"));
    }

    @Test
    public void patternComparatorSort() {
        Comparator<String> comparator = pathMatcher.getPatternComparator("/hotels/new");
        List<String> paths = new ArrayList<>(3);
        paths.add(null);
        paths.add("/hotels/new");
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/new", paths.get(0));
        Assert.assertNull(paths.get(1));
        paths.clear();
        paths.add("/hotels/new");
        paths.add(null);
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/new", paths.get(0));
        Assert.assertNull(paths.get(1));
        paths.clear();
        paths.add("/hotels/*");
        paths.add("/hotels/new");
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/new", paths.get(0));
        Assert.assertEquals("/hotels/*", paths.get(1));
        paths.clear();
        paths.add("/hotels/new");
        paths.add("/hotels/*");
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/new", paths.get(0));
        Assert.assertEquals("/hotels/*", paths.get(1));
        paths.clear();
        paths.add("/hotels/**");
        paths.add("/hotels/*");
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/*", paths.get(0));
        Assert.assertEquals("/hotels/**", paths.get(1));
        paths.clear();
        paths.add("/hotels/*");
        paths.add("/hotels/**");
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/*", paths.get(0));
        Assert.assertEquals("/hotels/**", paths.get(1));
        paths.clear();
        paths.add("/hotels/{hotel}");
        paths.add("/hotels/new");
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/new", paths.get(0));
        Assert.assertEquals("/hotels/{hotel}", paths.get(1));
        paths.clear();
        paths.add("/hotels/new");
        paths.add("/hotels/{hotel}");
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/new", paths.get(0));
        Assert.assertEquals("/hotels/{hotel}", paths.get(1));
        paths.clear();
        paths.add("/hotels/*");
        paths.add("/hotels/{hotel}");
        paths.add("/hotels/new");
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/new", paths.get(0));
        Assert.assertEquals("/hotels/{hotel}", paths.get(1));
        Assert.assertEquals("/hotels/*", paths.get(2));
        paths.clear();
        paths.add("/hotels/ne*");
        paths.add("/hotels/n*");
        Collections.shuffle(paths);
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/ne*", paths.get(0));
        Assert.assertEquals("/hotels/n*", paths.get(1));
        paths.clear();
        comparator = pathMatcher.getPatternComparator("/hotels/new.html");
        paths.add("/hotels/new.*");
        paths.add("/hotels/{hotel}");
        Collections.shuffle(paths);
        Collections.sort(paths, comparator);
        Assert.assertEquals("/hotels/new.*", paths.get(0));
        Assert.assertEquals("/hotels/{hotel}", paths.get(1));
        paths.clear();
        comparator = pathMatcher.getPatternComparator("/web/endUser/action/login.html");
        paths.add("/**/login.*");
        paths.add("/**/endUser/action/login.*");
        Collections.sort(paths, comparator);
        Assert.assertEquals("/**/endUser/action/login.*", paths.get(0));
        Assert.assertEquals("/**/login.*", paths.get(1));
        paths.clear();
    }

    // SPR-8687
    @Test
    public void trimTokensOff() {
        pathMatcher.setTrimTokens(false);
        Assert.assertTrue(pathMatcher.match("/group/{groupName}/members", "/group/sales/members"));
        Assert.assertTrue(pathMatcher.match("/group/{groupName}/members", "/group/  sales/members"));
        Assert.assertFalse(pathMatcher.match("/group/{groupName}/members", "/Group/  Sales/Members"));
    }

    // SPR-13286
    @Test
    public void caseInsensitive() {
        pathMatcher.setCaseSensitive(false);
        Assert.assertTrue(pathMatcher.match("/group/{groupName}/members", "/group/sales/members"));
        Assert.assertTrue(pathMatcher.match("/group/{groupName}/members", "/Group/Sales/Members"));
        Assert.assertTrue(pathMatcher.match("/Group/{groupName}/Members", "/group/Sales/members"));
    }

    @Test
    public void defaultCacheSetting() {
        match();
        Assert.assertTrue(((pathMatcher.stringMatcherCache.size()) > 20));
        for (int i = 0; i < 65536; i++) {
            pathMatcher.match(("test" + i), "test");
        }
        // Cache turned off because it went beyond the threshold
        Assert.assertTrue(pathMatcher.stringMatcherCache.isEmpty());
    }

    @Test
    public void cachePatternsSetToTrue() {
        pathMatcher.setCachePatterns(true);
        match();
        Assert.assertTrue(((pathMatcher.stringMatcherCache.size()) > 20));
        for (int i = 0; i < 65536; i++) {
            pathMatcher.match(("test" + i), ("test" + i));
        }
        // Cache keeps being alive due to the explicit cache setting
        Assert.assertTrue(((pathMatcher.stringMatcherCache.size()) > 65536));
    }

    @Test
    public void preventCreatingStringMatchersIfPathDoesNotStartsWithPatternPrefix() {
        pathMatcher.setCachePatterns(true);
        Assert.assertEquals(0, pathMatcher.stringMatcherCache.size());
        pathMatcher.match("test?", "test");
        Assert.assertEquals(1, pathMatcher.stringMatcherCache.size());
        pathMatcher.match("test?", "best");
        pathMatcher.match("test/*", "view/test.jpg");
        pathMatcher.match("test/**/test.jpg", "view/test.jpg");
        pathMatcher.match("test/{name}.jpg", "view/test.jpg");
        Assert.assertEquals(1, pathMatcher.stringMatcherCache.size());
    }

    @Test
    public void creatingStringMatchersIfPatternPrefixCannotDetermineIfPathMatch() {
        pathMatcher.setCachePatterns(true);
        Assert.assertEquals(0, pathMatcher.stringMatcherCache.size());
        pathMatcher.match("test", "testian");
        pathMatcher.match("test?", "testFf");
        pathMatcher.match("test/*", "test/dir/name.jpg");
        pathMatcher.match("test/{name}.jpg", "test/lorem.jpg");
        pathMatcher.match("bla/**/test.jpg", "bla/test.jpg");
        pathMatcher.match("**/{name}.jpg", "test/lorem.jpg");
        pathMatcher.match("/**/{name}.jpg", "/test/lorem.jpg");
        pathMatcher.match("/*/dir/{name}.jpg", "/*/dir/lorem.jpg");
        Assert.assertEquals(7, pathMatcher.stringMatcherCache.size());
    }

    @Test
    public void cachePatternsSetToFalse() {
        pathMatcher.setCachePatterns(false);
        match();
        Assert.assertTrue(pathMatcher.stringMatcherCache.isEmpty());
    }

    @Test
    public void extensionMappingWithDotPathSeparator() {
        pathMatcher.setPathSeparator(".");
        Assert.assertEquals("Extension mapping should be disabled with \".\" as path separator", "/*.html.hotel.*", pathMatcher.combine("/*.html", "hotel.*"));
    }
}

