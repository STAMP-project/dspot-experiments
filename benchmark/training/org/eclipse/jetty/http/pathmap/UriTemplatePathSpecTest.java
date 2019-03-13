/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.http.pathmap;


import PathSpecGroup.EXACT;
import PathSpecGroup.MIDDLE_GLOB;
import PathSpecGroup.PREFIX_GLOB;
import PathSpecGroup.SUFFIX_GLOB;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for URI Template Path Specs
 */
public class UriTemplatePathSpecTest {
    @Test
    public void testDefaultPathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/");
        Assertions.assertEquals("/", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(1, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(EXACT, spec.getGroup(), "Spec.group");
        Assertions.assertEquals(0, spec.getVariableCount(), "Spec.variableCount");
        Assertions.assertEquals(0, spec.getVariables().length, "Spec.variable.length");
    }

    @Test
    public void testExactOnePathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/a");
        Assertions.assertEquals("/a", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/a$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(1, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(EXACT, spec.getGroup(), "Spec.group");
        assertMatches(spec, "/a");
        assertMatches(spec, "/a?type=other");
        assertNotMatches(spec, "/a/b");
        assertNotMatches(spec, "/a/");
        Assertions.assertEquals(0, spec.getVariableCount(), "Spec.variableCount");
        Assertions.assertEquals(0, spec.getVariables().length, "Spec.variable.length");
    }

    @Test
    public void testExactPathSpec_TestWebapp() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/deep.thought/");
        Assertions.assertEquals("/deep.thought/", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/deep\\.thought/$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(1, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(EXACT, spec.getGroup(), "Spec.group");
        assertMatches(spec, "/deep.thought/");
        assertNotMatches(spec, "/deep.thought");
        Assertions.assertEquals(0, spec.getVariableCount(), "Spec.variableCount");
        Assertions.assertEquals(0, spec.getVariables().length, "Spec.variable.length");
    }

    @Test
    public void testExactTwoPathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/a/b");
        Assertions.assertEquals("/a/b", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/a/b$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(2, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(EXACT, spec.getGroup(), "Spec.group");
        Assertions.assertEquals(0, spec.getVariableCount(), "Spec.variableCount");
        Assertions.assertEquals(0, spec.getVariables().length, "Spec.variable.length");
        assertMatches(spec, "/a/b");
        assertNotMatches(spec, "/a/b/");
        assertNotMatches(spec, "/a/");
        assertNotMatches(spec, "/a/bb");
    }

    @Test
    public void testMiddleVarPathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/a/{var}/c");
        Assertions.assertEquals("/a/{var}/c", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/a/([^/]+)/c$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(3, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(MIDDLE_GLOB, spec.getGroup(), "Spec.group");
        assertDetectedVars(spec, "var");
        assertMatches(spec, "/a/b/c");
        assertMatches(spec, "/a/zz/c");
        assertMatches(spec, "/a/hello+world/c");
        assertNotMatches(spec, "/a/bc");
        assertNotMatches(spec, "/a/b/");
        assertNotMatches(spec, "/a/b");
        Map<String, String> mapped = spec.getPathParams("/a/b/c");
        MatcherAssert.assertThat("Spec.pathParams", mapped, Matchers.notNullValue());
        MatcherAssert.assertThat("Spec.pathParams.size", mapped.size(), Matchers.is(1));
        Assertions.assertEquals("b", mapped.get("var"), "Spec.pathParams[var]");
    }

    @Test
    public void testOneVarPathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/a/{foo}");
        Assertions.assertEquals("/a/{foo}", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/a/([^/]+)$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(2, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(PREFIX_GLOB, spec.getGroup(), "Spec.group");
        assertDetectedVars(spec, "foo");
        assertMatches(spec, "/a/b");
        assertNotMatches(spec, "/a/");
        assertNotMatches(spec, "/a");
        Map<String, String> mapped = spec.getPathParams("/a/b");
        MatcherAssert.assertThat("Spec.pathParams", mapped, Matchers.notNullValue());
        MatcherAssert.assertThat("Spec.pathParams.size", mapped.size(), Matchers.is(1));
        Assertions.assertEquals("b", mapped.get("foo"), "Spec.pathParams[foo]");
    }

    @Test
    public void testOneVarSuffixPathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/{var}/b/c");
        Assertions.assertEquals("/{var}/b/c", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/([^/]+)/b/c$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(3, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(SUFFIX_GLOB, spec.getGroup(), "Spec.group");
        assertDetectedVars(spec, "var");
        assertMatches(spec, "/a/b/c");
        assertMatches(spec, "/az/b/c");
        assertMatches(spec, "/hello+world/b/c");
        assertNotMatches(spec, "/a/bc");
        assertNotMatches(spec, "/a/b/");
        assertNotMatches(spec, "/a/b");
        Map<String, String> mapped = spec.getPathParams("/a/b/c");
        MatcherAssert.assertThat("Spec.pathParams", mapped, Matchers.notNullValue());
        MatcherAssert.assertThat("Spec.pathParams.size", mapped.size(), Matchers.is(1));
        Assertions.assertEquals("a", mapped.get("var"), "Spec.pathParams[var]");
    }

    @Test
    public void testTwoVarComplexInnerPathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/a/{var1}/c/{var2}/e");
        Assertions.assertEquals("/a/{var1}/c/{var2}/e", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/a/([^/]+)/c/([^/]+)/e$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(5, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(MIDDLE_GLOB, spec.getGroup(), "Spec.group");
        assertDetectedVars(spec, "var1", "var2");
        assertMatches(spec, "/a/b/c/d/e");
        assertNotMatches(spec, "/a/bc/d/e");
        assertNotMatches(spec, "/a/b/d/e");
        assertNotMatches(spec, "/a/b//d/e");
        Map<String, String> mapped = spec.getPathParams("/a/b/c/d/e");
        MatcherAssert.assertThat("Spec.pathParams", mapped, Matchers.notNullValue());
        MatcherAssert.assertThat("Spec.pathParams.size", mapped.size(), Matchers.is(2));
        Assertions.assertEquals("b", mapped.get("var1"), "Spec.pathParams[var1]");
        Assertions.assertEquals("d", mapped.get("var2"), "Spec.pathParams[var2]");
    }

    @Test
    public void testTwoVarComplexOuterPathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/{var1}/b/{var2}/{var3}");
        Assertions.assertEquals("/{var1}/b/{var2}/{var3}", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/([^/]+)/b/([^/]+)/([^/]+)$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(4, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(MIDDLE_GLOB, spec.getGroup(), "Spec.group");
        assertDetectedVars(spec, "var1", "var2", "var3");
        assertMatches(spec, "/a/b/c/d");
        assertNotMatches(spec, "/a/bc/d/e");
        assertNotMatches(spec, "/a/c/d/e");
        assertNotMatches(spec, "/a//d/e");
        Map<String, String> mapped = spec.getPathParams("/a/b/c/d");
        MatcherAssert.assertThat("Spec.pathParams", mapped, Matchers.notNullValue());
        MatcherAssert.assertThat("Spec.pathParams.size", mapped.size(), Matchers.is(3));
        Assertions.assertEquals("a", mapped.get("var1"), "Spec.pathParams[var1]");
        Assertions.assertEquals("c", mapped.get("var2"), "Spec.pathParams[var2]");
        Assertions.assertEquals("d", mapped.get("var3"), "Spec.pathParams[var3]");
    }

    @Test
    public void testTwoVarPrefixPathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/a/{var1}/{var2}");
        Assertions.assertEquals("/a/{var1}/{var2}", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/a/([^/]+)/([^/]+)$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(3, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(PREFIX_GLOB, spec.getGroup(), "Spec.group");
        assertDetectedVars(spec, "var1", "var2");
        assertMatches(spec, "/a/b/c");
        assertNotMatches(spec, "/a/bc");
        assertNotMatches(spec, "/a/b/");
        assertNotMatches(spec, "/a/b");
        Map<String, String> mapped = spec.getPathParams("/a/b/c");
        MatcherAssert.assertThat("Spec.pathParams", mapped, Matchers.notNullValue());
        MatcherAssert.assertThat("Spec.pathParams.size", mapped.size(), Matchers.is(2));
        Assertions.assertEquals("b", mapped.get("var1"), "Spec.pathParams[var1]");
        Assertions.assertEquals("c", mapped.get("var2"), "Spec.pathParams[var2]");
    }

    @Test
    public void testVarOnlyPathSpec() {
        UriTemplatePathSpec spec = new UriTemplatePathSpec("/{var1}");
        Assertions.assertEquals("/{var1}", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/([^/]+)$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(1, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(PREFIX_GLOB, spec.getGroup(), "Spec.group");
        assertDetectedVars(spec, "var1");
        assertMatches(spec, "/a");
        assertNotMatches(spec, "/");
        assertNotMatches(spec, "/a/b");
        assertNotMatches(spec, "/a/b/c");
        Map<String, String> mapped = spec.getPathParams("/a");
        MatcherAssert.assertThat("Spec.pathParams", mapped, Matchers.notNullValue());
        MatcherAssert.assertThat("Spec.pathParams.size", mapped.size(), Matchers.is(1));
        Assertions.assertEquals("a", mapped.get("var1"), "Spec.pathParams[var1]");
    }
}

