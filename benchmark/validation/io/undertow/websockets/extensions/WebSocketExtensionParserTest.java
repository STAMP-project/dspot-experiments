/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.websockets.extensions;


import ExtensionFunction.RSV1;
import ExtensionFunction.RSV2;
import ExtensionFunction.RSV3;
import io.undertow.testutils.category.UnitTest;
import io.undertow.websockets.WebSocketExtension;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ExtensionFunction.RSV1;
import static ExtensionFunction.RSV2;
import static ExtensionFunction.RSV3;


/**
 * A test class for WebSocket Extensions parsing operations.
 *
 * @author Lucas Ponce
 */
@Category(UnitTest.class)
public class WebSocketExtensionParserTest {
    @Test
    public void testParseExtension() {
        /* Original header:
        Sec-WebSocket-Extensions: x-webkit-deflate-message, x-custom-extension
         */
        final String EXTENSION_HEADER1 = " x-webkit-deflate-message , x-custom-extension ";
        final List<WebSocketExtension> extensions1 = WebSocketExtension.parse(EXTENSION_HEADER1);
        Assert.assertEquals(2, extensions1.size());
        Assert.assertEquals("x-webkit-deflate-message", extensions1.get(0).getName());
        Assert.assertEquals("x-custom-extension", extensions1.get(1).getName());
        /* Original header:
        Sec-WebSocket-Extensions: foo, bar; baz=2
         */
        final String EXTENSION_HEADER2 = " foo, bar; baz=2";
        final List<WebSocketExtension> extensions2 = WebSocketExtension.parse(EXTENSION_HEADER2);
        Assert.assertEquals(2, extensions2.size());
        Assert.assertEquals("foo", extensions2.get(0).getName());
        Assert.assertEquals(0, extensions2.get(0).getParameters().size());
        Assert.assertEquals("bar", extensions2.get(1).getName());
        Assert.assertEquals(1, extensions2.get(1).getParameters().size());
        Assert.assertEquals("baz", extensions2.get(1).getParameters().get(0).getName());
        Assert.assertEquals("2", extensions2.get(1).getParameters().get(0).getValue());
    }

    @Test
    public void testToExtensionHeader() {
        /* Original header:
        Sec-WebSocket-Extensions: x-webkit-deflate-message, x-custom-extension
         */
        final String EXTENSION_HEADER1 = " x-webkit-deflate-message , x-custom-extension ";
        final List<WebSocketExtension> extensions1 = WebSocketExtension.parse(EXTENSION_HEADER1);
        final String extensionHeader1 = WebSocketExtension.toExtensionHeader(extensions1);
        Assert.assertEquals("x-webkit-deflate-message, x-custom-extension", extensionHeader1);
        /* Original header:
        Sec-WebSocket-Extensions: foo, bar; baz=2
         */
        final String EXTENSION_HEADER2 = " foo, bar; baz=2";
        final List<WebSocketExtension> extensions2 = WebSocketExtension.parse(EXTENSION_HEADER2);
        final String extensionHeader2 = WebSocketExtension.toExtensionHeader(extensions2);
        Assert.assertEquals("foo, bar; baz=2", extensionHeader2);
    }

    @Test
    public void testWriteRsvBits() {
        int rsv = 4;
        Assert.assertEquals(RSV1, (rsv & (RSV1)));
        Assert.assertNotEquals(RSV2, (rsv & (RSV2)));
        Assert.assertNotEquals(RSV3, (rsv & (RSV3)));
        rsv = 2;
        Assert.assertEquals(RSV2, (rsv & (RSV2)));
        Assert.assertNotEquals(RSV1, (rsv & (RSV1)));
        Assert.assertNotEquals(RSV3, (rsv & (RSV3)));
        rsv = 1;
        Assert.assertEquals(RSV3, (rsv & (RSV3)));
        Assert.assertNotEquals(RSV1, (rsv & (RSV1)));
        Assert.assertNotEquals(RSV2, (rsv & (RSV2)));
        rsv = 6;
        Assert.assertEquals(RSV1, (rsv & (RSV1)));
        Assert.assertEquals(RSV2, (rsv & (RSV2)));
        Assert.assertNotEquals(RSV3, (rsv & (RSV3)));
        rsv = 3;
        Assert.assertNotEquals(RSV1, (rsv & (RSV1)));
        Assert.assertEquals(RSV2, (rsv & (RSV2)));
        Assert.assertEquals(RSV3, (rsv & (RSV3)));
        rsv = 5;
        Assert.assertEquals(RSV1, (rsv & (RSV1)));
        Assert.assertNotEquals(RSV2, (rsv & (RSV2)));
        Assert.assertEquals(RSV3, (rsv & (RSV3)));
        rsv = 7;
        Assert.assertEquals(RSV1, (rsv & (RSV1)));
        Assert.assertEquals(RSV2, (rsv & (RSV2)));
        Assert.assertEquals(RSV3, (rsv & (RSV3)));
        rsv = 8;
        Assert.assertNotEquals(RSV1, (rsv & (RSV1)));
        Assert.assertNotEquals(RSV2, (rsv & (RSV2)));
        Assert.assertNotEquals(RSV3, (rsv & (RSV3)));
        rsv = 0 | (RSV1);
        Assert.assertEquals(RSV1, (rsv & (RSV1)));
        Assert.assertNotEquals(RSV2, (rsv & (RSV2)));
        Assert.assertNotEquals(RSV3, (rsv & (RSV3)));
        rsv = 0 | (RSV2);
        Assert.assertNotEquals(RSV1, (rsv & (RSV1)));
        Assert.assertEquals(RSV2, (rsv & (RSV2)));
        Assert.assertNotEquals(RSV3, (rsv & (RSV3)));
        rsv = 0 | (RSV3);
        Assert.assertNotEquals(RSV1, (rsv & (RSV1)));
        Assert.assertNotEquals(RSV2, (rsv & (RSV2)));
        Assert.assertEquals(RSV3, (rsv & (RSV3)));
    }
}

