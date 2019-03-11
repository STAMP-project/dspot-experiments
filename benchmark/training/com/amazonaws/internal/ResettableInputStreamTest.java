/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.internal;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ClosedChannelException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class ResettableInputStreamTest {
    private static File file;

    @Test
    public void testFileInputStream() throws IOException {
        InputStream is = new FileInputStream(ResettableInputStreamTest.file);
        Assert.assertFalse(is.markSupported());
        final String content = IOUtils.toString(is);
        final String content2 = IOUtils.toString(is);
        Assert.assertTrue(((content.length()) == 100));
        Assert.assertEquals(content2, "");
        is.close();
    }

    @Test
    public void testResetInputStreamWithFile() throws IOException {
        ResettableInputStream is = new ResettableInputStream(ResettableInputStreamTest.file);
        Assert.assertTrue(is.markSupported());
        final String content = IOUtils.toString(is);
        is.reset();
        final String content2 = IOUtils.toString(is);
        Assert.assertTrue(((content.length()) == 100));
        Assert.assertEquals(content, content2);
        is.close();
        Assert.assertEquals(ResettableInputStreamTest.file, is.getFile());
    }

    @Test
    public void testResetFileInputStream() throws IOException {
        ResettableInputStream is = new ResettableInputStream(new FileInputStream(ResettableInputStreamTest.file));
        Assert.assertTrue(is.markSupported());
        final String content = IOUtils.toString(is);
        is.reset();
        final String content2 = IOUtils.toString(is);
        Assert.assertTrue(((content.length()) == 100));
        Assert.assertEquals(content, content2);
        is.close();
        Assert.assertNull(is.getFile());
    }

    @Test
    public void testMarkAndResetWithFile() throws IOException {
        ResettableInputStream is = new ResettableInputStream(ResettableInputStreamTest.file);
        is.read(new byte[10]);
        is.mark((-1));
        final String content = IOUtils.toString(is);
        is.reset();
        final String content2 = IOUtils.toString(is);
        Assert.assertTrue(((content.length()) == 90));
        Assert.assertEquals(content, content2);
        is.close();
    }

    @Test
    public void testMarkAndResetFileInputStream() throws IOException {
        ResettableInputStream is = new ResettableInputStream(new FileInputStream(ResettableInputStreamTest.file));
        is.read(new byte[10]);
        is.mark((-1));
        final String content = IOUtils.toString(is);
        is.reset();
        final String content2 = IOUtils.toString(is);
        Assert.assertTrue(((content.length()) == 90));
        Assert.assertEquals(content, content2);
        is.close();
    }

    @Test
    public void testResetWithClosedFile() throws IOException {
        ResettableInputStream is = new ResettableInputStream(ResettableInputStreamTest.file).disableClose();
        final String content = IOUtils.toString(is);
        is.close();
        is.reset();// survive a close operation!

        final String content2 = IOUtils.toString(is);
        Assert.assertTrue(((content.length()) == 100));
        Assert.assertEquals(content, content2);
        is.release();
    }

    @Test(expected = ClosedChannelException.class)
    public void negativeTestResetWithClosedFile() throws IOException {
        ResettableInputStream is = new ResettableInputStream(ResettableInputStreamTest.file);
        is.close();
        is.reset();
    }

    @Test
    public void testMarkAndResetWithClosedFile() throws IOException {
        ResettableInputStream is = new ResettableInputStream(ResettableInputStreamTest.file).disableClose();
        is.read(new byte[10]);
        is.mark((-1));
        final String content = IOUtils.toString(is);
        is.close();
        is.reset();// survive a close operation!

        final String content2 = IOUtils.toString(is);
        Assert.assertTrue(((content.length()) == 90));
        Assert.assertEquals(content, content2);
        is.release();
    }

    @Test(expected = ClosedChannelException.class)
    public void testMarkAndResetClosedFileInputStream() throws IOException {
        ResettableInputStream is = new ResettableInputStream(new FileInputStream(ResettableInputStreamTest.file));
        is.close();
        is.reset();// cannot survive a close if not disabled

    }
}

