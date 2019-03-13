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
package org.eclipse.jetty.websocket.common;


import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.util.TypeUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class AcceptHashTest {
    @Test
    public void testHash() {
        byte[] key = TypeUtil.fromHexString("00112233445566778899AABBCCDDEEFF");
        MatcherAssert.assertThat("Key size", key.length, Matchers.is(16));
        // what the client sends
        String clientKey = String.valueOf(B64Code.encode(key));
        // what the server responds with
        String serverHash = AcceptHash.hashKey(clientKey);
        // how the client validates
        MatcherAssert.assertThat(serverHash, Matchers.is("mVL6JKtNRC4tluIaFAW2hhMffgE="));
    }

    /**
     * Test of values present in RFC-6455.
     * <p>
     * Note: client key bytes are "7468652073616d706c65206e6f6e6365"
     */
    @Test
    public void testRfcHashExample() {
        // What the client sends in the RFC
        String clientKey = "dGhlIHNhbXBsZSBub25jZQ==";
        // What the server responds with
        String serverAccept = AcceptHash.hashKey(clientKey);
        String expectedHash = "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=";
        MatcherAssert.assertThat(serverAccept, Matchers.is(expectedHash));
    }
}

