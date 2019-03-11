/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.decoder;


import com.facebook.common.references.ResourceReleaser;
import com.facebook.imagepipeline.testing.TrivialPooledByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;


@RunWith(RobolectricTestRunner.class)
public class ProgressiveJpegParserTest {
    @Mock
    public ResourceReleaser mResourceReleaser;

    private ProgressiveJpegParser mProgressiveJpegParser;

    private byte[] mWebpBytes;

    private byte[] mPartialWebpBytes;

    private byte[] mJpegBytes;

    @Test
    public void testOnPartialWebp() {
        final TrivialPooledByteBuffer byteBuffer = new TrivialPooledByteBuffer(mPartialWebpBytes);
        mProgressiveJpegParser.parseMoreData(buildEncodedImage(byteBuffer));
        Assert.assertFalse(mProgressiveJpegParser.isJpeg());
    }

    @Test
    public void testOnWebp() {
        final TrivialPooledByteBuffer byteBuffer = new TrivialPooledByteBuffer(mWebpBytes);
        mProgressiveJpegParser.parseMoreData(buildEncodedImage(byteBuffer));
        Assert.assertFalse(mProgressiveJpegParser.isJpeg());
    }

    @Test
    public void testOnTooShortImage() {
        final TrivialPooledByteBuffer shortByteBuffer = new TrivialPooledByteBuffer(new byte[]{ ((byte) (255)) });
        Assert.assertFalse(mProgressiveJpegParser.isJpeg());
        Assert.assertFalse(mProgressiveJpegParser.parseMoreData(buildEncodedImage(shortByteBuffer)));
        Assert.assertFalse(mProgressiveJpegParser.isJpeg());
        Assert.assertEquals(0, mProgressiveJpegParser.getBestScanEndOffset());
        Assert.assertEquals(0, mProgressiveJpegParser.getBestScanNumber());
    }

    @Test
    public void testOnShortestJpeg() {
        final TrivialPooledByteBuffer shortByteBuffer = new TrivialPooledByteBuffer(new byte[]{ ((byte) (255)), ((byte) (216)) });
        Assert.assertFalse(mProgressiveJpegParser.parseMoreData(buildEncodedImage(shortByteBuffer)));
        Assert.assertTrue(mProgressiveJpegParser.isJpeg());
        Assert.assertEquals(0, mProgressiveJpegParser.getBestScanEndOffset());
        Assert.assertEquals(0, mProgressiveJpegParser.getBestScanNumber());
    }

    @Test
    public void testBasic() {
        byte[] veryFakeJpeg = new byte[]{ ((byte) (255)), ((byte) (216)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (218)), ((byte) (0)), ((byte) (3)), ((byte) (0)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (218)), ((byte) (0)), ((byte) (3)), ((byte) (0)), ((byte) (255)), ((byte) (218)), ((byte) (0)), ((byte) (3)), ((byte) (0)), ((byte) (255)), ((byte) (218)) };
        testFirstNBytes(veryFakeJpeg, 3, false, 0, 0);
        testFirstNBytes(veryFakeJpeg, 6, false, 0, 0);
        testFirstNBytes(veryFakeJpeg, 8, false, 0, 0);
        testFirstNBytes(veryFakeJpeg, 13, true, 1, 11);
        testFirstNBytes(veryFakeJpeg, 13, false, 1, 11);
        testFirstNBytes(veryFakeJpeg, 17, false, 1, 11);
        testFirstNBytes(veryFakeJpeg, 18, true, 2, 16);
        testFirstNBytes(veryFakeJpeg, 20, false, 2, 16);
        testFirstNBytes(veryFakeJpeg, veryFakeJpeg.length, true, 3, 21);
    }

    @Test
    public void testOnRealJpeg() {
        testFirstNBytes(mJpegBytes, 7000, true, 1, 4332);
        testFirstNBytes(mJpegBytes, mJpegBytes.length, true, 10, 32844);
    }
}

