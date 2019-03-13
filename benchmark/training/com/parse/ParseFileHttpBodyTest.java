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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ParseFileHttpBodyTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testInitializeWithFileAndContentType() throws IOException {
        String contentType = "text/plain";
        File file = ParseFileHttpBodyTest.makeTestFile(temporaryFolder.getRoot());
        ParseFileHttpBody body = new ParseFileHttpBody(file, contentType);
        Assert.assertEquals(file.length(), body.getContentLength());
        Assert.assertEquals(contentType, body.getContentType());
        // Verify file content
        InputStream content = body.getContent();
        byte[] contentBytes = ParseIOUtils.toByteArray(content);
        ParseIOUtils.closeQuietly(content);
        ParseFileHttpBodyTest.verifyTestFileContent(contentBytes);
    }

    @Test
    public void testInitializeWithFile() throws IOException {
        File file = ParseFileHttpBodyTest.makeTestFile(temporaryFolder.getRoot());
        ParseFileHttpBody body = new ParseFileHttpBody(file);
        Assert.assertEquals(file.length(), body.getContentLength());
        Assert.assertNull(body.getContentType());
        // Verify file content
        InputStream content = body.getContent();
        byte[] contentBytes = ParseIOUtils.toByteArray(content);
        ParseIOUtils.closeQuietly(content);
        ParseFileHttpBodyTest.verifyTestFileContent(contentBytes);
    }

    @Test
    public void testWriteTo() throws IOException {
        File file = ParseFileHttpBodyTest.makeTestFile(temporaryFolder.getRoot());
        ParseFileHttpBody body = new ParseFileHttpBody(file);
        // Check content
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        body.writeTo(output);
        ParseFileHttpBodyTest.verifyTestFileContent(output.toByteArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteToWithNullOutput() throws Exception {
        ParseFileHttpBody body = new ParseFileHttpBody(ParseFileHttpBodyTest.makeTestFile(temporaryFolder.getRoot()));
        body.writeTo(null);
    }
}

