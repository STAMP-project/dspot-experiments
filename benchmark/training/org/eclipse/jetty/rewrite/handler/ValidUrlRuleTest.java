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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@SuppressWarnings("unused")
public class ValidUrlRuleTest extends AbstractRuleTestCase {
    private ValidUrlRule _rule;

    @Test
    public void testValidUrl() throws Exception {
        _rule.setCode("404");
        _request.setURIPathQuery("/valid/uri.html");
        _rule.matchAndApply(_request.getRequestURI(), _request, _response);
        Assertions.assertEquals(200, _response.getStatus());
    }

    @Test
    public void testInvalidUrl() throws Exception {
        _rule.setCode("404");
        _request.setURIPathQuery("/invalid%0c/uri.html");
        String result = _rule.matchAndApply(_request.getRequestURI(), _request, _response);
        Assertions.assertEquals(404, _response.getStatus());
    }

    @Test
    public void testInvalidUrlWithReason() throws Exception {
        _rule.setCode("405");
        _rule.setReason("foo");
        _request.setURIPathQuery("/%00/");
        String result = _rule.matchAndApply(_request.getRequestURI(), _request, _response);
        Assertions.assertEquals(405, _response.getStatus());
        Assertions.assertEquals("foo", _response.getReason());
    }

    @Test
    public void testInvalidJsp() throws Exception {
        _rule.setCode("405");
        _rule.setReason("foo");
        _request.setURIPathQuery("/jsp/bean1.jsp%00");
        String result = _rule.matchAndApply(_request.getRequestURI(), _request, _response);
        Assertions.assertEquals(405, _response.getStatus());
        Assertions.assertEquals("foo", _response.getReason());
    }

    @Test
    public void testCharacters() throws Exception {
        // space
        Assertions.assertTrue(_rule.isValidChar(" ".charAt(0)));
        // form feed
        Assertions.assertFalse(_rule.isValidChar("\f".charAt(0)));
    }
}

