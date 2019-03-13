/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.protocols.http;


import HttpHeaderNames.AUTHORIZATION;
import HttpResponseStatus.UNAUTHORIZED;
import Settings.EMPTY;
import Unpooled.EMPTY_BUFFER;
import User.Role.SUPERUSER;
import io.crate.auth.AlwaysOKAuthentication;
import io.crate.auth.AlwaysOKNullAuthentication;
import io.crate.auth.Authentication;
import io.crate.auth.user.User;
import io.crate.test.integration.CrateUnitTest;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.EnumSet;
import javax.net.ssl.SSLSession;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public class HttpAuthUpstreamHandlerTest extends CrateUnitTest {
    private final Settings hbaEnabled = Settings.builder().put("auth.host_based.enabled", true).put("auth.host_based.config.0.user", "crate").build();

    // UserLookup always returns null, so there are no users (even no default crate superuser)
    private final Authentication authService = new io.crate.auth.HostBasedAuthentication(hbaEnabled, ( userName) -> null);

    @Test
    public void testChannelClosedWhenUnauthorized() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel();
        HttpAuthUpstreamHandler.sendUnauthorized(ch, null);
        ch.releaseInbound();
        HttpResponse resp = ch.readOutbound();
        assertThat(resp.status(), Is.is(UNAUTHORIZED));
        assertThat(ch.isOpen(), Is.is(false));
    }

    @Test
    public void testSendUnauthorizedWithoutBody() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel();
        HttpAuthUpstreamHandler.sendUnauthorized(ch, null);
        ch.releaseInbound();
        DefaultFullHttpResponse resp = ch.readOutbound();
        assertThat(resp.content(), Is.is(EMPTY_BUFFER));
    }

    @Test
    public void testSendUnauthorizedWithBody() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel();
        HttpAuthUpstreamHandler.sendUnauthorized(ch, "not allowed\n");
        ch.releaseInbound();
        DefaultFullHttpResponse resp = ch.readOutbound();
        assertThat(resp.content().toString(StandardCharsets.UTF_8), Is.is("not allowed\n"));
    }

    @Test
    public void testSendUnauthorizedWithBodyNoNewline() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel();
        HttpAuthUpstreamHandler.sendUnauthorized(ch, "not allowed");
        ch.releaseInbound();
        DefaultFullHttpResponse resp = ch.readOutbound();
        assertThat(resp.content().toString(StandardCharsets.UTF_8), Is.is("not allowed\n"));
    }

    @Test
    public void testAuthorized() throws Exception {
        HttpAuthUpstreamHandler handler = new HttpAuthUpstreamHandler(Settings.EMPTY, new AlwaysOKNullAuthentication());
        EmbeddedChannel ch = new EmbeddedChannel(handler);
        DefaultHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/_sql");
        ch.writeInbound(request);
        ch.releaseInbound();
        assertThat(handler.authorized(), Is.is(true));
    }

    @Test
    public void testNotNoHbaConfig() throws Exception {
        HttpAuthUpstreamHandler handler = new HttpAuthUpstreamHandler(Settings.EMPTY, authService);
        EmbeddedChannel ch = new EmbeddedChannel(handler);
        DefaultHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/_sql");
        request.headers().add(AUTHORIZATION.toString(), "Basic QWxhZGRpbjpPcGVuU2VzYW1l");
        request.headers().add("X-Real-Ip", "10.1.0.100");
        ch.writeInbound(request);
        ch.releaseInbound();
        assertFalse(handler.authorized());
        HttpAuthUpstreamHandlerTest.assertUnauthorized(ch.readOutbound(), "No valid auth.host_based.config entry found for host \"10.1.0.100\", user \"Aladdin\", protocol \"http\"\n");
    }

    @Test
    public void testUnauthorizedUser() throws Exception {
        HttpAuthUpstreamHandler handler = new HttpAuthUpstreamHandler(Settings.EMPTY, authService);
        EmbeddedChannel ch = new EmbeddedChannel(handler);
        HttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/_sql");
        ch.writeInbound(request);
        ch.releaseInbound();
        assertFalse(handler.authorized());
        HttpAuthUpstreamHandlerTest.assertUnauthorized(ch.readOutbound(), "trust authentication failed for user \"crate\"\n");
    }

    @Test
    public void testClientCertUserHasPreferenceOverTrustAuthDefault() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SSLSession session = Mockito.mock(SSLSession.class);
        Mockito.when(session.getPeerCertificates()).thenReturn(new Certificate[]{ ssc.cert() });
        HttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/_sql");
        String userName = HttpAuthUpstreamHandler.credentialsFromRequest(request, session, EMPTY).v1();
        assertThat(userName, Is.is("example.com"));
    }

    @Test
    public void testUserAuthenticationWithDisabledHBA() throws Exception {
        User crateUser = User.of("crate", EnumSet.of(SUPERUSER));
        Authentication authServiceNoHBA = new AlwaysOKAuthentication(( userName) -> crateUser);
        HttpAuthUpstreamHandler handler = new HttpAuthUpstreamHandler(Settings.EMPTY, authServiceNoHBA);
        EmbeddedChannel ch = new EmbeddedChannel(handler);
        HttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/_sql");
        request.headers().add(AUTHORIZATION.toString(), "Basic Y3JhdGU6");
        ch.writeInbound(request);
        ch.releaseInbound();
        assertTrue(handler.authorized());
    }

    @Test
    public void testUnauthorizedUserWithDisabledHBA() throws Exception {
        Authentication authServiceNoHBA = new AlwaysOKAuthentication(( userName) -> null);
        HttpAuthUpstreamHandler handler = new HttpAuthUpstreamHandler(Settings.EMPTY, authServiceNoHBA);
        EmbeddedChannel ch = new EmbeddedChannel(handler);
        HttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/_sql");
        request.headers().add(AUTHORIZATION.toString(), "Basic QWxhZGRpbjpPcGVuU2VzYW1l");
        ch.writeInbound(request);
        ch.releaseInbound();
        assertFalse(handler.authorized());
        HttpAuthUpstreamHandlerTest.assertUnauthorized(ch.readOutbound(), "trust authentication failed for user \"Aladdin\"\n");
    }
}

