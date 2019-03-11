/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.net;


import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.impl.SSLHelper;
import io.vertx.test.core.VertxTestBase;
import io.vertx.test.tls.Cert;
import io.vertx.test.tls.Trust;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class SSLHelperTest extends VertxTestBase {
    @Test
    public void testUseJdkCiphersWhenNotSpecified() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        SSLEngine engine = context.createSSLEngine();
        String[] expected = engine.getEnabledCipherSuites();
        SSLHelper helper = new SSLHelper(new HttpClientOptions(), Cert.CLIENT_JKS.get(), Trust.SERVER_JKS.get());
        SslContext ctx = helper.getContext(((VertxInternal) (vertx)));
        assertEquals(new HashSet(Arrays.asList(expected)), new HashSet(ctx.cipherSuites()));
    }

    @Test
    public void testUseOpenSSLCiphersWhenNotSpecified() throws Exception {
        Set<String> expected = OpenSsl.availableOpenSslCipherSuites();
        SSLHelper helper = new SSLHelper(new HttpClientOptions().setOpenSslEngineOptions(new OpenSSLEngineOptions()), Cert.CLIENT_PEM.get(), Trust.SERVER_PEM.get());
        SslContext ctx = helper.getContext(((VertxInternal) (vertx)));
        assertEquals(expected, new HashSet(ctx.cipherSuites()));
    }

    @Test
    public void testDefaultOpenSslServerSessionContext() throws Exception {
        testOpenSslServerSessionContext(true);
    }

    @Test
    public void testUserSetOpenSslServerSessionContext() throws Exception {
        testOpenSslServerSessionContext(false);
    }

    @Test
    public void testPreserveEnabledCipherSuitesOrder() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        SSLEngine engine = context.createSSLEngine();
        HttpServerOptions options = new HttpServerOptions();
        for (String suite : engine.getEnabledCipherSuites()) {
            options.addEnabledCipherSuite(suite);
        }
        assertEquals(new java.util.ArrayList(options.getEnabledCipherSuites()), Arrays.asList(engine.getEnabledCipherSuites()));
        assertEquals(new java.util.ArrayList(getEnabledCipherSuites()), Arrays.asList(engine.getEnabledCipherSuites()));
        JsonObject json = options.toJson();
        assertEquals(new java.util.ArrayList(getEnabledCipherSuites()), Arrays.asList(engine.getEnabledCipherSuites()));
        SSLHelper helper = new SSLHelper(options, Cert.SERVER_JKS.get(), null);
        assertEquals(Arrays.asList(getEnabledCipherSuites()), Arrays.asList(engine.getEnabledCipherSuites()));
    }

    @Test
    public void testPreserveEnabledSecureTransportProtocolOrder() throws Exception {
        HttpServerOptions options = new HttpServerOptions();
        List<String> expectedProtocols = new java.util.ArrayList(options.getEnabledSecureTransportProtocols());
        options.removeEnabledSecureTransportProtocol("TLSv1");
        options.addEnabledSecureTransportProtocol("SSLv3");
        expectedProtocols.remove("TLSv1");
        expectedProtocols.add("SSLv3");
        assertEquals(new java.util.ArrayList(options.getEnabledSecureTransportProtocols()), expectedProtocols);
        assertEquals(new java.util.ArrayList(getEnabledSecureTransportProtocols()), expectedProtocols);
        JsonObject json = options.toJson();
        assertEquals(new java.util.ArrayList(getEnabledSecureTransportProtocols()), expectedProtocols);
    }
}

