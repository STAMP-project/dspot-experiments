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
package org.eclipse.jetty.security.authentication;


import Authentication.SEND_CONTINUE;
import HttpHeader.AUTHORIZATION;
import HttpHeader.NEGOTIATE;
import HttpHeader.WWW_AUTHENTICATE;
import HttpServletResponse.SC_UNAUTHORIZED;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test class for {@link SpnegoAuthenticator}.
 */
public class SpnegoAuthenticatorTest {
    private SpnegoAuthenticator _authenticator;

    @Test
    public void testChallengeSentWithNoAuthorization() throws Exception {
        HttpChannel channel = new HttpChannel(null, new HttpConfiguration(), null, null) {
            @Override
            public Server getServer() {
                return null;
            }
        };
        Request req = new Request(channel, null);
        HttpOutput out = new HttpOutput(channel) {
            @Override
            public void close() {
                return;
            }
        };
        Response res = new Response(channel, out);
        MetaData.Request metadata = new MetaData.Request(new HttpFields());
        metadata.setURI(new HttpURI("http://localhost"));
        req.setMetaData(metadata);
        Assertions.assertEquals(SEND_CONTINUE, _authenticator.validateRequest(req, res, true));
        Assertions.assertEquals(NEGOTIATE.asString(), res.getHeader(WWW_AUTHENTICATE.asString()));
        Assertions.assertEquals(SC_UNAUTHORIZED, res.getStatus());
    }

    @Test
    public void testChallengeSentWithUnhandledAuthorization() throws Exception {
        HttpChannel channel = new HttpChannel(null, new HttpConfiguration(), null, null) {
            @Override
            public Server getServer() {
                return null;
            }
        };
        Request req = new Request(channel, null);
        HttpOutput out = new HttpOutput(channel) {
            @Override
            public void close() {
                return;
            }
        };
        Response res = new Response(channel, out);
        HttpFields http_fields = new HttpFields();
        // Create a bogus Authorization header. We don't care about the actual credentials.
        http_fields.add(AUTHORIZATION, "Basic asdf");
        MetaData.Request metadata = new MetaData.Request(http_fields);
        metadata.setURI(new HttpURI("http://localhost"));
        req.setMetaData(metadata);
        Assertions.assertEquals(SEND_CONTINUE, _authenticator.validateRequest(req, res, true));
        Assertions.assertEquals(NEGOTIATE.asString(), res.getHeader(WWW_AUTHENTICATE.asString()));
        Assertions.assertEquals(SC_UNAUTHORIZED, res.getStatus());
    }

    @Test
    public void testCaseInsensitiveHeaderParsing() {
        Assertions.assertFalse(_authenticator.isAuthSchemeNegotiate(null));
        Assertions.assertFalse(_authenticator.isAuthSchemeNegotiate(""));
        Assertions.assertFalse(_authenticator.isAuthSchemeNegotiate("Basic"));
        Assertions.assertFalse(_authenticator.isAuthSchemeNegotiate("basic"));
        Assertions.assertFalse(_authenticator.isAuthSchemeNegotiate("Digest"));
        Assertions.assertFalse(_authenticator.isAuthSchemeNegotiate("LotsandLotsandLots of nonsense"));
        Assertions.assertFalse(_authenticator.isAuthSchemeNegotiate("Negotiat asdfasdf"));
        Assertions.assertFalse(_authenticator.isAuthSchemeNegotiate("Negotiated"));
        Assertions.assertFalse(_authenticator.isAuthSchemeNegotiate("Negotiate-and-more"));
        Assertions.assertTrue(_authenticator.isAuthSchemeNegotiate("Negotiate"));
        Assertions.assertTrue(_authenticator.isAuthSchemeNegotiate("negotiate"));
        Assertions.assertTrue(_authenticator.isAuthSchemeNegotiate("negOtiAte"));
    }

    @Test
    public void testExtractAuthScheme() {
        Assertions.assertEquals("", _authenticator.getAuthSchemeFromHeader(null));
        Assertions.assertEquals("", _authenticator.getAuthSchemeFromHeader(""));
        Assertions.assertEquals("", _authenticator.getAuthSchemeFromHeader("   "));
        Assertions.assertEquals("Basic", _authenticator.getAuthSchemeFromHeader(" Basic asdfasdf"));
        Assertions.assertEquals("Basicasdf", _authenticator.getAuthSchemeFromHeader("Basicasdf asdfasdf"));
        Assertions.assertEquals("basic", _authenticator.getAuthSchemeFromHeader(" basic asdfasdf "));
        Assertions.assertEquals("Negotiate", _authenticator.getAuthSchemeFromHeader("Negotiate asdfasdf"));
        Assertions.assertEquals("negotiate", _authenticator.getAuthSchemeFromHeader("negotiate asdfasdf"));
        Assertions.assertEquals("negotiate", _authenticator.getAuthSchemeFromHeader(" negotiate  asdfasdf"));
        Assertions.assertEquals("negotiated", _authenticator.getAuthSchemeFromHeader(" negotiated  asdfasdf"));
    }
}

