/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.util;


import com.facebook.common.internal.Closeables;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link StreamUtil}
 */
@RunWith(RobolectricTestRunner.class)
public class StreamUtilTest {
    /**
     * Verify that using a ByteArrayInputStream does not allocate a new byte array.
     */
    @Test
    public void testByteArrayInputStream() throws Exception {
        byte[] bytes = new byte[8];
        InputStream input = new ByteArrayInputStream(bytes);
        try {
            byte[] bytesRead = StreamUtil.getBytesFromStream(input);
            Assert.assertTrue(Arrays.equals(bytes, bytesRead));
        } finally {
            Closeables.close(input, true);
        }
    }

    /**
     * Verify that using an offset with ByteArrayInputStream still produces correct output.
     */
    @Test
    public void testByteArrayInputStreamWithOffset() throws Exception {
        byte[] bytes = new byte[]{ 0, 1, 2, 3, 4 };
        InputStream input = new ByteArrayInputStream(bytes, 1, 4);
        try {
            byte[] bytesRead = StreamUtil.getBytesFromStream(input);
            byte[] expectedBytes = new byte[]{ 1, 2, 3, 4 };
            Assert.assertTrue(Arrays.equals(expectedBytes, bytesRead));
        } finally {
            Closeables.close(input, true);
        }
    }

    /**
     * Verify getting a byte array from a FileInputStream.
     */
    @Test
    public void testFileInputStream() throws Exception {
        checkFileInputStream(4);
        checkFileInputStream(((64 * 1024) + 5));// Don't end on an even byte boundary

    }

    @Test
    public void testSuccessfulSkip() throws Exception {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.skip(ArgumentMatchers.anyLong())).thenReturn(2L);
        Assert.assertEquals(10, StreamUtil.skip(inputStream, 10));
        InOrder order = Mockito.inOrder(inputStream);
        order.verify(inputStream).skip(10);
        order.verify(inputStream).skip(8);
        order.verify(inputStream).skip(6);
        order.verify(inputStream).skip(4);
        order.verify(inputStream).skip(2);
        Mockito.verifyNoMoreInteractions(inputStream);
    }

    @Test
    public void testUnsuccessfulSkip() throws Exception {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.skip(ArgumentMatchers.anyLong())).thenReturn(3L, 5L, 0L, 6L, 0L);
        Mockito.when(inputStream.read()).thenReturn(3, (-1));
        Assert.assertEquals(15, StreamUtil.skip(inputStream, 20));
        InOrder order = Mockito.inOrder(inputStream);
        order.verify(inputStream).skip(20);
        order.verify(inputStream).skip(17);
        order.verify(inputStream).skip(12);
        order.verify(inputStream).read();
        order.verify(inputStream).skip(11);
        order.verify(inputStream).skip(5);
        order.verify(inputStream).read();
        Mockito.verifyNoMoreInteractions(inputStream);
    }
}

