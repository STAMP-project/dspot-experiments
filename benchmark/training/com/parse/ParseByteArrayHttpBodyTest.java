/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ParseByteArrayHttpBodyTest {
    @Test
    public void testInitializeWithString() throws IOException {
        String content = "content";
        String contentType = "application/json";
        ParseByteArrayHttpBody body = new ParseByteArrayHttpBody(content, contentType);
        Assert.assertArrayEquals(content.getBytes(), ParseIOUtils.toByteArray(body.getContent()));
        Assert.assertEquals(contentType, body.getContentType());
        Assert.assertEquals(7, body.getContentLength());
    }

    @Test
    public void testInitializeWithByteArray() throws IOException {
        byte[] content = new byte[]{ 1, 1, 1, 1, 1 };
        String contentType = "application/json";
        ParseByteArrayHttpBody body = new ParseByteArrayHttpBody(content, contentType);
        Assert.assertArrayEquals(content, ParseIOUtils.toByteArray(body.getContent()));
        Assert.assertEquals(contentType, body.getContentType());
        Assert.assertEquals(5, body.getContentLength());
    }

    @Test
    public void testWriteTo() throws IOException {
        String content = "content";
        String contentType = "application/json";
        ParseByteArrayHttpBody body = new ParseByteArrayHttpBody(content, contentType);
        // Check content
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        body.writeTo(output);
        String contentAgain = output.toString();
        Assert.assertEquals(content, contentAgain);
        // No need to check whether content input stream is closed since it is a ByteArrayInputStream
    }
}

