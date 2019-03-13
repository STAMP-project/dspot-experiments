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


import HttpStatus.MOVED_TEMPORARILY_302;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.jupiter.api.Test;


public class TerminatingRegexRuleTest extends AbstractRuleTestCase {
    private RewriteHandler rewriteHandler;

    @Test
    public void testTerminatingEarly() throws IOException, ServletException {
        rewriteHandler.handle("/login.jsp", _request, _request, _response);
        assertIsRequest("/login.jsp");
    }

    @Test
    public void testNoTerminationDo() throws IOException, ServletException {
        rewriteHandler.handle("/login.do", _request, _request, _response);
        assertIsRedirect(MOVED_TEMPORARILY_302, "http://login.company.com/");
    }

    @Test
    public void testNoTerminationDir() throws IOException, ServletException {
        rewriteHandler.handle("/login/", _request, _request, _response);
        assertIsRedirect(MOVED_TEMPORARILY_302, "http://login.company.com/");
    }
}

