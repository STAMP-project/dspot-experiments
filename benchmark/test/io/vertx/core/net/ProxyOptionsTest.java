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


import ProxyOptions.DEFAULT_HOST;
import ProxyOptions.DEFAULT_PORT;
import ProxyOptions.DEFAULT_TYPE;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ProxyOptionsTest extends VertxTestBase {
    ProxyType randType;

    String randHost;

    int randPort;

    String randUsername;

    String randPassword;

    @Test
    public void testProxyOptions() {
        ProxyOptions options = new ProxyOptions();
        assertEquals(DEFAULT_TYPE, options.getType());
        assertEquals(options, options.setType(randType));
        assertEquals(randType, options.getType());
        TestUtils.assertNullPointerException(() -> options.setType(null));
        assertEquals(DEFAULT_HOST, options.getHost());
        assertEquals(options, options.setHost(randHost));
        assertEquals(randHost, options.getHost());
        TestUtils.assertNullPointerException(() -> options.setHost(null));
        assertEquals(DEFAULT_PORT, options.getPort());
        assertEquals(options, options.setPort(randPort));
        assertEquals(randPort, options.getPort());
        TestUtils.assertIllegalArgumentException(() -> options.setPort((-1)));
        TestUtils.assertIllegalArgumentException(() -> options.setPort(65536));
        assertEquals(null, options.getUsername());
        assertEquals(options, options.setUsername(randUsername));
        assertEquals(randUsername, options.getUsername());
        assertEquals(null, options.getPassword());
        assertEquals(options, options.setPassword(randPassword));
        assertEquals(randPassword, options.getPassword());
    }

    @Test
    public void testCopyProxyOptions() {
        ProxyOptions options = new ProxyOptions();
        options.setType(randType);
        options.setHost(randHost);
        options.setPort(randPort);
        options.setUsername(randUsername);
        options.setPassword(randPassword);
        ProxyOptions copy = new ProxyOptions(options);
        assertEquals(randType, copy.getType());
        assertEquals(randPort, copy.getPort());
        assertEquals(randHost, copy.getHost());
        assertEquals(randUsername, copy.getUsername());
        assertEquals(randPassword, copy.getPassword());
    }

    @Test
    public void testDefaultOptionsJson() {
        ProxyOptions def = new ProxyOptions();
        ProxyOptions options = new ProxyOptions(new JsonObject());
        assertEquals(def.getType(), options.getType());
        assertEquals(def.getPort(), options.getPort());
        assertEquals(def.getHost(), options.getHost());
        assertEquals(def.getUsername(), options.getUsername());
        assertEquals(def.getPassword(), options.getPassword());
    }

    @Test
    public void testOptionsJson() {
        JsonObject json = new JsonObject();
        json.put("type", randType.toString()).put("host", randHost).put("port", randPort).put("username", randUsername).put("password", randPassword);
        ProxyOptions options = new ProxyOptions(json);
        assertEquals(randType, options.getType());
        assertEquals(randPort, options.getPort());
        assertEquals(randHost, options.getHost());
        assertEquals(randUsername, options.getUsername());
        assertEquals(randPassword, options.getPassword());
    }
}

