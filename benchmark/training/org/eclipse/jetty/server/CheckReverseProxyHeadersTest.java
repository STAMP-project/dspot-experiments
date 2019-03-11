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
package org.eclipse.jetty.server;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class CheckReverseProxyHeadersTest {
    @Test
    public void testCheckReverseProxyHeaders() throws Exception {
        // Classic ProxyPass from example.com:80 to localhost:8080
        testRequest(("Host: localhost:8080\n" + ("X-Forwarded-For: 10.20.30.40\n" + "X-Forwarded-Host: example.com")), new CheckReverseProxyHeadersTest.RequestValidator() {
            @Override
            public void validate(HttpServletRequest request) {
                Assertions.assertEquals("example.com", request.getServerName());
                Assertions.assertEquals(80, request.getServerPort());
                Assertions.assertEquals("10.20.30.40", request.getRemoteAddr());
                Assertions.assertEquals("10.20.30.40", request.getRemoteHost());
                Assertions.assertEquals("example.com", request.getHeader("Host"));
                Assertions.assertEquals("http", request.getScheme());
                Assertions.assertFalse(request.isSecure());
            }
        });
        // IPv6 ProxyPass from example.com:80 to localhost:8080
        testRequest(("Host: localhost:8080\n" + ("X-Forwarded-For: 10.20.30.40\n" + "X-Forwarded-Host: [::1]")), new CheckReverseProxyHeadersTest.RequestValidator() {
            @Override
            public void validate(HttpServletRequest request) {
                Assertions.assertEquals("[::1]", request.getServerName());
                Assertions.assertEquals(80, request.getServerPort());
                Assertions.assertEquals("10.20.30.40", request.getRemoteAddr());
                Assertions.assertEquals("10.20.30.40", request.getRemoteHost());
                Assertions.assertEquals("[::1]", request.getHeader("Host"));
                Assertions.assertEquals("http", request.getScheme());
                Assertions.assertFalse(request.isSecure());
            }
        });
        // IPv6 ProxyPass from example.com:80 to localhost:8080
        testRequest(("Host: localhost:8080\n" + ("X-Forwarded-For: 10.20.30.40\n" + "X-Forwarded-Host: [::1]:8888")), new CheckReverseProxyHeadersTest.RequestValidator() {
            @Override
            public void validate(HttpServletRequest request) {
                Assertions.assertEquals("[::1]", request.getServerName());
                Assertions.assertEquals(8888, request.getServerPort());
                Assertions.assertEquals("10.20.30.40", request.getRemoteAddr());
                Assertions.assertEquals("10.20.30.40", request.getRemoteHost());
                Assertions.assertEquals("[::1]:8888", request.getHeader("Host"));
                Assertions.assertEquals("http", request.getScheme());
                Assertions.assertFalse(request.isSecure());
            }
        });
        // ProxyPass from example.com:81 to localhost:8080
        testRequest(("Host: localhost:8080\n" + ((("X-Forwarded-For: 10.20.30.40\n" + "X-Forwarded-Host: example.com:81\n") + "X-Forwarded-Server: example.com\n") + "X-Forwarded-Proto: https")), new CheckReverseProxyHeadersTest.RequestValidator() {
            @Override
            public void validate(HttpServletRequest request) {
                Assertions.assertEquals("example.com", request.getServerName());
                Assertions.assertEquals(81, request.getServerPort());
                Assertions.assertEquals("10.20.30.40", request.getRemoteAddr());
                Assertions.assertEquals("10.20.30.40", request.getRemoteHost());
                Assertions.assertEquals("example.com:81", request.getHeader("Host"));
                Assertions.assertEquals("https", request.getScheme());
                Assertions.assertTrue(request.isSecure());
            }
        });
        // Multiple ProxyPass from example.com:80 to rp.example.com:82 to localhost:8080
        testRequest(("Host: localhost:8080\n" + ((("X-Forwarded-For: 10.20.30.40, 10.0.0.1\n" + "X-Forwarded-Host: example.com, rp.example.com:82\n") + "X-Forwarded-Server: example.com, rp.example.com\n") + "X-Forwarded-Proto: https, http")), new CheckReverseProxyHeadersTest.RequestValidator() {
            @Override
            public void validate(HttpServletRequest request) {
                Assertions.assertEquals("example.com", request.getServerName());
                Assertions.assertEquals(443, request.getServerPort());
                Assertions.assertEquals("10.20.30.40", request.getRemoteAddr());
                Assertions.assertEquals("10.20.30.40", request.getRemoteHost());
                Assertions.assertEquals("example.com", request.getHeader("Host"));
                Assertions.assertEquals("https", request.getScheme());
                Assertions.assertTrue(request.isSecure());
            }
        });
    }

    /**
     * Interface for validate a wrapped request.
     */
    private static interface RequestValidator {
        /**
         * Validate the current request.
         *
         * @param request
         * 		the request.
         */
        void validate(HttpServletRequest request);
    }

    /**
     * Handler for validation.
     */
    private static class ValidationHandler extends AbstractHandler {
        private final CheckReverseProxyHeadersTest.RequestValidator _requestValidator;

        private Error _error;

        private ValidationHandler(CheckReverseProxyHeadersTest.RequestValidator requestValidator) {
            _requestValidator = requestValidator;
        }

        /**
         * Retrieve the validation error.
         *
         * @return the validation error or <code>null</code> if there was no error.
         */
        public Error getError() {
            return _error;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            try {
                _requestValidator.validate(request);
            } catch (Error e) {
                _error = e;
            } catch (Throwable e) {
                _error = new Error(e);
            }
        }
    }
}

