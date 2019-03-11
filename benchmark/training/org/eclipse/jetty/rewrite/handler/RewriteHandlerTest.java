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


public class RewriteHandlerTest extends AbstractRuleTestCase {
    private RewriteHandler _handler;

    private RewritePatternRule _rule1;

    private RewritePatternRule _rule2;

    private RewritePatternRule _rule3;

    private RewriteRegexRule _rule4;

    @Test
    public void test() throws Exception {
        _response.setStatus(200);
        _request.setHandled(false);
        _handler.setOriginalPathAttribute("/before");
        _handler.setRewriteRequestURI(true);
        _handler.setRewritePathInfo(true);
        _request.setURIPathQuery("/xxx/bar");
        _request.setPathInfo("/xxx/bar");
        _handler.handle("/xxx/bar", _request, _request, _response);
        Assertions.assertEquals(201, _response.getStatus());
        Assertions.assertEquals("/bar/zzz", _request.getAttribute("target"));
        Assertions.assertEquals("/bar/zzz", _request.getAttribute("URI"));
        Assertions.assertEquals("/bar/zzz", _request.getAttribute("info"));
        Assertions.assertEquals(null, _request.getAttribute("before"));
        _response.setStatus(200);
        _request.setHandled(false);
        _handler.setOriginalPathAttribute("/before");
        _handler.setRewriteRequestURI(false);
        _handler.setRewritePathInfo(false);
        _request.setURIPathQuery("/foo/bar");
        _request.setPathInfo("/foo/bar");
        _handler.handle("/foo/bar", _request, _request, _response);
        Assertions.assertEquals(201, _response.getStatus());
        Assertions.assertEquals("/foo/bar", _request.getAttribute("target"));
        Assertions.assertEquals("/foo/bar", _request.getAttribute("URI"));
        Assertions.assertEquals("/foo/bar", _request.getAttribute("info"));
        Assertions.assertEquals(null, _request.getAttribute("before"));
        _response.setStatus(200);
        _request.setHandled(false);
        _handler.setOriginalPathAttribute(null);
        _request.setURIPathQuery("/aaa/bar");
        _request.setPathInfo("/aaa/bar");
        _handler.handle("/aaa/bar", _request, _request, _response);
        Assertions.assertEquals(201, _response.getStatus());
        Assertions.assertEquals("/ddd/bar", _request.getAttribute("target"));
        Assertions.assertEquals("/aaa/bar", _request.getAttribute("URI"));
        Assertions.assertEquals("/aaa/bar", _request.getAttribute("info"));
        Assertions.assertEquals(null, _request.getAttribute("before"));
        _response.setStatus(200);
        _request.setHandled(false);
        _handler.setOriginalPathAttribute("before");
        _handler.setRewriteRequestURI(true);
        _handler.setRewritePathInfo(true);
        _request.setURIPathQuery("/aaa/bar");
        _request.setPathInfo("/aaa/bar");
        _handler.handle("/aaa/bar", _request, _request, _response);
        Assertions.assertEquals(201, _response.getStatus());
        Assertions.assertEquals("/ddd/bar", _request.getAttribute("target"));
        Assertions.assertEquals("/ddd/bar", _request.getAttribute("URI"));
        Assertions.assertEquals("/ddd/bar", _request.getAttribute("info"));
        Assertions.assertEquals("/aaa/bar", _request.getAttribute("before"));
        _response.setStatus(200);
        _request.setHandled(false);
        _rule2.setTerminating(true);
        _request.setURIPathQuery("/aaa/bar");
        _request.setPathInfo("/aaa/bar");
        _handler.handle("/aaa/bar", _request, _request, _response);
        Assertions.assertEquals(201, _response.getStatus());
        Assertions.assertEquals("/ccc/bar", _request.getAttribute("target"));
        Assertions.assertEquals("/ccc/bar", _request.getAttribute("URI"));
        Assertions.assertEquals("/ccc/bar", _request.getAttribute("info"));
        Assertions.assertEquals("/aaa/bar", _request.getAttribute("before"));
        _response.setStatus(200);
        _request.setHandled(false);
        _rule2.setHandling(true);
        _request.setAttribute("before", null);
        _request.setAttribute("target", null);
        _request.setAttribute("URI", null);
        _request.setAttribute("info", null);
        _request.setURIPathQuery("/aaa/bar");
        _request.setPathInfo("/aaa/bar");
        _handler.handle("/aaa/bar", _request, _request, _response);
        Assertions.assertEquals(200, _response.getStatus());
        Assertions.assertEquals(null, _request.getAttribute("target"));
        Assertions.assertEquals(null, _request.getAttribute("URI"));
        Assertions.assertEquals(null, _request.getAttribute("info"));
        Assertions.assertEquals("/aaa/bar", _request.getAttribute("before"));
        Assertions.assertTrue(_request.isHandled());
    }

    @Test
    public void testEncodedPattern() throws Exception {
        _response.setStatus(200);
        _request.setHandled(false);
        _handler.setOriginalPathAttribute("/before");
        _handler.setRewriteRequestURI(true);
        _handler.setRewritePathInfo(false);
        _request.setURIPathQuery("/ccc/x%20y");
        _request.setPathInfo("/ccc/x y");
        _handler.handle("/ccc/x y", _request, _request, _response);
        Assertions.assertEquals(201, _response.getStatus());
        Assertions.assertEquals("/ddd/x y", _request.getAttribute("target"));
        Assertions.assertEquals("/ddd/x%20y", _request.getAttribute("URI"));
        Assertions.assertEquals("/ccc/x y", _request.getAttribute("info"));
    }

    @Test
    public void testEncodedRegex() throws Exception {
        _response.setStatus(200);
        _request.setHandled(false);
        _handler.setOriginalPathAttribute("/before");
        _handler.setRewriteRequestURI(true);
        _handler.setRewritePathInfo(false);
        _request.setURIPathQuery("/xxx/x%20y");
        _request.setPathInfo("/xxx/x y");
        _handler.handle("/xxx/x y", _request, _request, _response);
        Assertions.assertEquals(201, _response.getStatus());
        Assertions.assertEquals("/x y/zzz", _request.getAttribute("target"));
        Assertions.assertEquals("/x%20y/zzz", _request.getAttribute("URI"));
        Assertions.assertEquals("/xxx/x y", _request.getAttribute("info"));
    }
}

