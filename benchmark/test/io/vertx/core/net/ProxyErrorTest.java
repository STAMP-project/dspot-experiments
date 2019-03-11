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


import io.vertx.test.core.VertxTestBase;
import io.vertx.test.fakedns.FakeDNSServer;
import io.vertx.test.proxy.HttpProxy;
import java.net.InetSocketAddress;
import org.junit.Test;


/**
 * Test all kinds of errors raised by the proxy
 *
 * @author <a href="http://oss.lehmann.cx/">Alexander Lehmann</a>
 */
public class ProxyErrorTest extends VertxTestBase {
    private HttpProxy proxy = null;

    private FakeDNSServer dnsServer;

    private InetSocketAddress dnsServerAddress;

    @Test
    public void testProxyHttpsError() throws Exception {
        expectProxyException(403, null, "https://localhost/");
    }

    @Test
    public void testProxyHttpsAuthFail() throws Exception {
        expectProxyException(0, "user", "https://localhost/");
    }

    @Test
    public void testProxyHttpsHostUnknown() throws Exception {
        expectProxyException(0, null, "https://unknown.hostname/");
    }

    @Test
    public void testProxyError() throws Exception {
        expectStatusError(403, 403, null, "http://localhost/");
    }

    @Test
    public void testProxyAuthFail() throws Exception {
        expectStatusError(0, 407, "user", "http://localhost/");
    }

    @Test
    public void testProxyHostUnknown() throws Exception {
        expectStatusError(0, 504, null, "http://unknown.hostname/");
    }
}

