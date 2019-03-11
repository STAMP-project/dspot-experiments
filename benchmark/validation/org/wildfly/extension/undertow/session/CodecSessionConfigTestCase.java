/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.extension.undertow.session;


import SessionConfig.SessionCookieSource;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionConfig;
import org.jboss.as.web.session.SessionIdentifierCodec;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link CodecSessionConfig}
 *
 * @author Paul Ferraro
 */
public class CodecSessionConfigTestCase {
    private final SessionConfig config = Mockito.mock(SessionConfig.class);

    private final SessionIdentifierCodec codec = Mockito.mock(SessionIdentifierCodec.class);

    private final SessionConfig subject = new CodecSessionConfig(this.config, this.codec);

    @Test
    public void findSessionId() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        Mockito.when(this.config.findSessionId(exchange)).thenReturn(null);
        String result = this.subject.findSessionId(exchange);
        Assert.assertNull(result);
        String encodedSessionId = "session.route1";
        String sessionId = "session";
        Mockito.when(this.config.findSessionId(exchange)).thenReturn(encodedSessionId);
        Mockito.when(this.codec.decode(encodedSessionId)).thenReturn(sessionId);
        Mockito.when(this.codec.encode(sessionId)).thenReturn(encodedSessionId);
        result = this.subject.findSessionId(exchange);
        Assert.assertSame(sessionId, result);
        Mockito.verify(this.config, Mockito.never()).setSessionId(ArgumentMatchers.same(exchange), ArgumentMatchers.anyString());
        String reencodedSessionId = "session.route2";
        Mockito.when(this.codec.encode(sessionId)).thenReturn(reencodedSessionId);
        result = this.subject.findSessionId(exchange);
        Assert.assertSame(sessionId, result);
        Mockito.verify(this.config).setSessionId(exchange, reencodedSessionId);
    }

    @Test
    public void setSessionId() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        String encodedSessionId = "session.route";
        String sessionId = "session";
        Mockito.when(this.codec.encode(sessionId)).thenReturn(encodedSessionId);
        this.subject.setSessionId(exchange, sessionId);
        Mockito.verify(this.config).setSessionId(exchange, encodedSessionId);
    }

    @Test
    public void clearSession() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        String encodedSessionId = "session.route";
        String sessionId = "session";
        Mockito.when(this.codec.encode(sessionId)).thenReturn(encodedSessionId);
        this.subject.clearSession(exchange, sessionId);
        Mockito.verify(this.config).clearSession(exchange, encodedSessionId);
    }

    @Test
    public void rewriteUrl() {
        String url = "http://test";
        String encodedUrl = "http://test/session";
        String encodedSessionId = "session.route";
        String sessionId = "session";
        Mockito.when(this.codec.encode(sessionId)).thenReturn(encodedSessionId);
        Mockito.when(this.config.rewriteUrl(url, encodedSessionId)).thenReturn(encodedUrl);
        String result = this.subject.rewriteUrl(url, sessionId);
        Assert.assertSame(encodedUrl, result);
    }

    @Test
    public void sessionCookieSource() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        SessionConfig.SessionCookieSource expected = SessionCookieSource.OTHER;
        Mockito.when(this.config.sessionCookieSource(exchange)).thenReturn(expected);
        SessionConfig.SessionCookieSource result = this.subject.sessionCookieSource(exchange);
        Assert.assertSame(expected, result);
    }
}

