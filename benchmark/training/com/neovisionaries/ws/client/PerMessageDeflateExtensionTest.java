/**
 * Copyright (C) 2015 Neo Visionaries Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.neovisionaries.ws.client;


import WebSocketError.PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS;
import WebSocketError.PERMESSAGE_DEFLATE_UNSUPPORTED_PARAMETER;
import org.junit.Assert;
import org.junit.Test;


public class PerMessageDeflateExtensionTest {
    private static final int DEFAULT_WINDOW_SIZE = 32768;

    @Test
    public void test001() {
        PerMessageDeflateExtension extension = PerMessageDeflateExtensionTest.parseValid("permessage-deflate");
        Assert.assertNotNull(extension);
        Assert.assertFalse(extension.isServerNoContextTakeover());
        Assert.assertFalse(extension.isClientNoContextTakeover());
        Assert.assertEquals(PerMessageDeflateExtensionTest.DEFAULT_WINDOW_SIZE, extension.getServerWindowSize());
        Assert.assertEquals(PerMessageDeflateExtensionTest.DEFAULT_WINDOW_SIZE, extension.getClientWindowSize());
    }

    @Test
    public void test002() {
        PerMessageDeflateExtension extension = PerMessageDeflateExtensionTest.parseValid("permessage-deflate; server_no_context_takeover; client_no_context_takeover");
        Assert.assertNotNull(extension);
        Assert.assertTrue(extension.isServerNoContextTakeover());
        Assert.assertTrue(extension.isClientNoContextTakeover());
    }

    @Test
    public void test003() {
        PerMessageDeflateExtension extension = PerMessageDeflateExtensionTest.parseValid("permessage-deflate; server_max_window_bits=8; client_max_window_bits=8");
        Assert.assertNotNull(extension);
        Assert.assertEquals(256, extension.getServerWindowSize());
        Assert.assertEquals(256, extension.getClientWindowSize());
    }

    @Test
    public void test004() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; unknown_parameter");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_UNSUPPORTED_PARAMETER, exception.getError());
    }

    @Test
    public void test005() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; server_max_window_bits");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }

    @Test
    public void test006() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; server_max_window_bits=abc");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }

    @Test
    public void test007() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; server_max_window_bits=0");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }

    @Test
    public void test008() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; server_max_window_bits=7");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }

    @Test
    public void test009() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; server_max_window_bits=16");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }

    @Test
    public void test010() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; client_max_window_bits");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }

    @Test
    public void test011() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; client_max_window_bits=abc");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }

    @Test
    public void test012() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; client_max_window_bits=0");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }

    @Test
    public void test013() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; client_max_window_bits=7");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }

    @Test
    public void test014() {
        WebSocketException exception = PerMessageDeflateExtensionTest.parseInvalid("permessage-deflate; client_max_window_bits=16");
        Assert.assertNotNull(exception);
        Assert.assertSame(PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, exception.getError());
    }
}

