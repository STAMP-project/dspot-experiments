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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RegexPathSpecTest {
    @Test
    public void testExactSpec() {
        RegexPathSpec spec = new RegexPathSpec("^/a$");
        Assertions.assertEquals("^/a$", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/a$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(1, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(EXACT, spec.group, "Spec.group");
        RegexPathSpecTest.assertMatches(spec, "/a");
        RegexPathSpecTest.assertNotMatches(spec, "/aa");
        RegexPathSpecTest.assertNotMatches(spec, "/a/");
    }

    @Test
    public void testMiddleSpec() {
        RegexPathSpec spec = new RegexPathSpec("^/rest/([^/]*)/list$");
        Assertions.assertEquals("^/rest/([^/]*)/list$", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/rest/([^/]*)/list$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(3, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(MIDDLE_GLOB, spec.group, "Spec.group");
        RegexPathSpecTest.assertMatches(spec, "/rest/api/list");
        RegexPathSpecTest.assertMatches(spec, "/rest/1.0/list");
        RegexPathSpecTest.assertMatches(spec, "/rest/2.0/list");
        RegexPathSpecTest.assertMatches(spec, "/rest/accounts/list");
        RegexPathSpecTest.assertNotMatches(spec, "/a");
        RegexPathSpecTest.assertNotMatches(spec, "/aa");
        RegexPathSpecTest.assertNotMatches(spec, "/aa/bb");
        RegexPathSpecTest.assertNotMatches(spec, "/rest/admin/delete");
        RegexPathSpecTest.assertNotMatches(spec, "/rest/list");
    }

    @Test
    public void testMiddleSpecNoGrouping() {
        RegexPathSpec spec = new RegexPathSpec("^/rest/[^/]+/list$");
        Assertions.assertEquals("^/rest/[^/]+/list$", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/rest/[^/]+/list$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(3, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(MIDDLE_GLOB, spec.group, "Spec.group");
        RegexPathSpecTest.assertMatches(spec, "/rest/api/list");
        RegexPathSpecTest.assertMatches(spec, "/rest/1.0/list");
        RegexPathSpecTest.assertMatches(spec, "/rest/2.0/list");
        RegexPathSpecTest.assertMatches(spec, "/rest/accounts/list");
        RegexPathSpecTest.assertNotMatches(spec, "/a");
        RegexPathSpecTest.assertNotMatches(spec, "/aa");
        RegexPathSpecTest.assertNotMatches(spec, "/aa/bb");
        RegexPathSpecTest.assertNotMatches(spec, "/rest/admin/delete");
        RegexPathSpecTest.assertNotMatches(spec, "/rest/list");
    }

    @Test
    public void testPrefixSpec() {
        RegexPathSpec spec = new RegexPathSpec("^/a/(.*)$");
        Assertions.assertEquals("^/a/(.*)$", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^/a/(.*)$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(2, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(PREFIX_GLOB, spec.group, "Spec.group");
        RegexPathSpecTest.assertMatches(spec, "/a/");
        RegexPathSpecTest.assertMatches(spec, "/a/b");
        RegexPathSpecTest.assertMatches(spec, "/a/b/c/d/e");
        RegexPathSpecTest.assertNotMatches(spec, "/a");
        RegexPathSpecTest.assertNotMatches(spec, "/aa");
        RegexPathSpecTest.assertNotMatches(spec, "/aa/bb");
    }

    @Test
    public void testSuffixSpec() {
        RegexPathSpec spec = new RegexPathSpec("^(.*).do$");
        Assertions.assertEquals("^(.*).do$", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals("^(.*).do$", spec.getPattern().pattern(), "Spec.pattern");
        Assertions.assertEquals(0, spec.getPathDepth(), "Spec.pathDepth");
        Assertions.assertEquals(SUFFIX_GLOB, spec.group, "Spec.group");
        RegexPathSpecTest.assertMatches(spec, "/a.do");
        RegexPathSpecTest.assertMatches(spec, "/a/b/c.do");
        RegexPathSpecTest.assertMatches(spec, "/abcde.do");
        RegexPathSpecTest.assertMatches(spec, "/abc/efg.do");
        RegexPathSpecTest.assertNotMatches(spec, "/a");
        RegexPathSpecTest.assertNotMatches(spec, "/aa");
        RegexPathSpecTest.assertNotMatches(spec, "/aa/bb");
        RegexPathSpecTest.assertNotMatches(spec, "/aa/bb.do/more");
    }
}

