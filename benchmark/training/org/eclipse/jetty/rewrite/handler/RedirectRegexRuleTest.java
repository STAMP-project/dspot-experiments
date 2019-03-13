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
package org.eclipse.jetty.rewrite.handler;


import HttpStatus.FOUND_302;
import HttpStatus.MOVED_PERMANENTLY_301;
import java.io.IOException;
import org.junit.jupiter.api.Test;


public class RedirectRegexRuleTest extends AbstractRuleTestCase {
    @Test
    public void testLocationWithReplacementGroupEmpty() throws IOException {
        RedirectRegexRule rule = new RedirectRegexRule();
        rule.setRegex("/my/dir/file/(.*)$");
        rule.setReplacement("http://www.mortbay.org/$1");
        // Resource is dir
        rule.matchAndApply("/my/dir/file/", _request, _response);
        assertRedirectResponse(FOUND_302, "http://www.mortbay.org/");
    }

    @Test
    public void testLocationWithPathReplacement() throws IOException {
        RedirectRegexRule rule = new RedirectRegexRule();
        rule.setRegex("/documentation/(.*)$");
        rule.setReplacement("/docs/$1");
        // Resource is dir
        rule.matchAndApply("/documentation/top.html", _request, _response);
        assertRedirectResponse(FOUND_302, "http://0.0.0.0/docs/top.html");
    }

    @Test
    public void testLocationWithReplacmentGroupSimple() throws IOException {
        RedirectRegexRule rule = new RedirectRegexRule();
        rule.setRegex("/my/dir/file/(.*)$");
        rule.setReplacement("http://www.mortbay.org/$1");
        // Resource is an image
        rule.matchAndApply("/my/dir/file/image.png", _request, _response);
        assertRedirectResponse(FOUND_302, "http://www.mortbay.org/image.png");
    }

    @Test
    public void testLocationWithReplacementGroupDeepWithParams() throws IOException {
        RedirectRegexRule rule = new RedirectRegexRule();
        rule.setRegex("/my/dir/file/(.*)$");
        rule.setReplacement("http://www.mortbay.org/$1");
        // Resource is api with parameters
        rule.matchAndApply("/my/dir/file/api/rest/foo?id=100&sort=date", _request, _response);
        assertRedirectResponse(FOUND_302, "http://www.mortbay.org/api/rest/foo?id=100&sort=date");
    }

    @Test
    public void testMovedPermanently() throws IOException {
        RedirectRegexRule rule = new RedirectRegexRule();
        rule.setRegex("/api/(.*)$");
        rule.setReplacement("http://api.company.com/$1");
        rule.setStatusCode(MOVED_PERMANENTLY_301);
        // Resource is api with parameters
        rule.matchAndApply("/api/rest/foo?id=100&sort=date", _request, _response);
        assertRedirectResponse(MOVED_PERMANENTLY_301, "http://api.company.com/rest/foo?id=100&sort=date");
    }
}

