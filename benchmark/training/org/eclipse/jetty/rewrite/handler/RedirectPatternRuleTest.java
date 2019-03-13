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


public class RedirectPatternRuleTest extends AbstractRuleTestCase {
    @Test
    public void testGlobPattern() throws IOException {
        String location = "http://eclipse.com";
        RedirectPatternRule rule = new RedirectPatternRule();
        rule.setPattern("*");
        rule.setLocation(location);
        rule.apply("/", _request, _response);
        assertRedirectResponse(FOUND_302, location);
    }

    @Test
    public void testPrefixPattern() throws IOException {
        String location = "http://api.company.com/";
        RedirectPatternRule rule = new RedirectPatternRule();
        rule.setPattern("/api/*");
        rule.setLocation(location);
        rule.setStatusCode(MOVED_PERMANENTLY_301);
        rule.apply("/api/rest?foo=1", _request, _response);
        assertRedirectResponse(MOVED_PERMANENTLY_301, location);
    }
}

