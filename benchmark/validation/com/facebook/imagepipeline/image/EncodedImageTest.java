/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.image;


import DefaultImageFormats.JPEG;
import DefaultImageFormats.PNG;
import DefaultImageFormats.WEBP_ANIMATED;
import DefaultImageFormats.WEBP_EXTENDED;
import DefaultImageFormats.WEBP_EXTENDED_WITH_ALPHA;
import DefaultImageFormats.WEBP_LOSSLESS;
import DefaultImageFormats.WEBP_SIMPLE;
import com.facebook.common.internal.ByteStreams;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imagepipeline.testing.TrivialPooledByteBuffer;
import com.facebook.imageutils.JfifUtil;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link EncodedImage}
 */
@RunWith(RobolectricTestRunner.class)
public class EncodedImageTest {
    private static final int ENCODED_BYTES_LENGTH = 100;

    private CloseableReference<PooledByteBuffer> mByteBufferRef;

    private FileInputStream mInputStream;

    private Supplier<FileInputStream> mInputStreamSupplier;

    @Test
    public void testByteBufferRef() {
        EncodedImage encodedImage = new EncodedImage(mByteBufferRef);
        Assert.assertEquals(2, mByteBufferRef.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(encodedImage.getByteBufferRef().getUnderlyingReferenceTestOnly(), mByteBufferRef.getUnderlyingReferenceTestOnly());
    }

    @Test
    public void testInputStream() {
        EncodedImage encodedImage = new EncodedImage(mInputStreamSupplier);
        Assert.assertSame(encodedImage.getInputStream(), mInputStreamSupplier.get());
    }

    @Test
    public void testCloneOrNull() {
        EncodedImage encodedImage = new EncodedImage(mByteBufferRef);
        encodedImage.setImageFormat(JPEG);
        encodedImage.setRotationAngle(0);
        encodedImage.setExifOrientation(5);
        encodedImage.setWidth(1);
        encodedImage.setHeight(2);
        encodedImage.setSampleSize(4);
        encodedImage.setBytesRange(BytesRange.toMax(1000));
        EncodedImage encodedImage2 = EncodedImage.cloneOrNull(encodedImage);
        Assert.assertEquals(3, mByteBufferRef.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(encodedImage.getByteBufferRef().getUnderlyingReferenceTestOnly(), encodedImage2.getByteBufferRef().getUnderlyingReferenceTestOnly());
        Assert.assertEquals(encodedImage.getImageFormat(), encodedImage2.getImageFormat());
        Assert.assertEquals(encodedImage.getRotationAngle(), encodedImage2.getRotationAngle());
        Assert.assertEquals(encodedImage.getExifOrientation(), encodedImage2.getExifOrientation());
        Assert.assertEquals(encodedImage.getHeight(), encodedImage2.getHeight());
        Assert.assertEquals(encodedImage.getWidth(), encodedImage2.getWidth());
        Assert.assertEquals(encodedImage.getSampleSize(), encodedImage2.getSampleSize());
        Assert.assertEquals(encodedImage.getBytesRange(), encodedImage2.getBytesRange());
        encodedImage = new EncodedImage(mInputStreamSupplier, 100);
        encodedImage.setImageFormat(JPEG);
        encodedImage.setRotationAngle(0);
        encodedImage.setExifOrientation(5);
        encodedImage.setWidth(1);
        encodedImage.setHeight(2);
        encodedImage2 = EncodedImage.cloneOrNull(encodedImage);
        Assert.assertSame(encodedImage.getInputStream(), encodedImage2.getInputStream());
        Assert.assertEquals(encodedImage2.getSize(), encodedImage.getSize());
    }

    @Test
    public void testCloneOrNull_withInvalidOrNullReferences() {
        Assert.assertEquals(null, EncodedImage.cloneOrNull(null));
        EncodedImage encodedImage = new EncodedImage(mByteBufferRef);
        encodedImage.close();
        Assert.assertEquals(null, EncodedImage.cloneOrNull(encodedImage));
    }

    @Test
    public void testClose() {
        EncodedImage encodedImage = new EncodedImage(mByteBufferRef);
        encodedImage.close();
        Assert.assertEquals(1, mByteBufferRef.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
    }

    @Test
    public void testIsValid() {
        EncodedImage encodedImage = new EncodedImage(mByteBufferRef);
        Assert.assertTrue(encodedImage.isValid());
        encodedImage.close();
        Assert.assertFalse(encodedImage.isValid());
        encodedImage = new EncodedImage(mInputStreamSupplier);
        Assert.assertTrue(encodedImage.isValid());
        // Test the static method
        Assert.assertFalse(EncodedImage.isValid(null));
    }

    @Test
    public void testIsMetaDataAvailable() {
        EncodedImage encodedImage1 = new EncodedImage(mByteBufferRef);
        EncodedImage encodedImage2 = new EncodedImage(mByteBufferRef);
        encodedImage2.setRotationAngle(1);
        encodedImage2.setExifOrientation(1);
        encodedImage2.setWidth(1);
        encodedImage2.setHeight(1);
        Assert.assertFalse(EncodedImage.isMetaDataAvailable(encodedImage1));
        Assert.assertTrue(EncodedImage.isMetaDataAvailable(encodedImage2));
    }

    @Test
    public void testCloseSafely() {
        EncodedImage encodedImage = new EncodedImage(mByteBufferRef);
        EncodedImage.closeSafely(encodedImage);
        Assert.assertEquals(1, mByteBufferRef.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
    }

    @Test
    public void testGetInputStream() {
        EncodedImage encodedImage = new EncodedImage(mInputStreamSupplier);
        Assert.assertSame(mInputStream, encodedImage.getInputStream());
    }

    @Test
    public void testGetFirstBytesAsHexString() throws IOException {
        PooledByteBuffer buf = new TrivialPooledByteBuffer("12345abcd".getBytes());
        EncodedImage encodedImage = new EncodedImage(CloseableReference.of(buf));
        Assert.assertEquals("313233343561626364", encodedImage.getFirstBytesAsHexString(9));
        Assert.assertEquals("313233343561626364", encodedImage.getFirstBytesAsHexString(10));
        Assert.assertEquals("313233343561", encodedImage.getFirstBytesAsHexString(6));
    }

    @Test
    public void testParseMetaData_JPEG() throws IOException {
        PooledByteBuffer buf = new TrivialPooledByteBuffer(ByteStreams.toByteArray(EncodedImageTest.class.getResourceAsStream("images/image.jpg")));
        EncodedImage encodedImage = new EncodedImage(CloseableReference.of(buf));
        encodedImage.parseMetaData();
        Assert.assertSame(JPEG, encodedImage.getImageFormat());
        Assert.assertEquals(550, encodedImage.getWidth());
        Assert.assertEquals(468, encodedImage.getHeight());
        Assert.assertEquals(0, encodedImage.getRotationAngle());
        Assert.assertEquals(0, encodedImage.getExifOrientation());
    }

    @Test
    public void testParseMetaData_PNG() throws IOException {
        PooledByteBuffer buf = new TrivialPooledByteBuffer(ByteStreams.toByteArray(EncodedImageTest.class.getResourceAsStream("images/image.png")));
        EncodedImage encodedImage = new EncodedImage(CloseableReference.of(buf));
        encodedImage.parseMetaData();
        Assert.assertSame(PNG, encodedImage.getImageFormat());
        Assert.assertEquals(800, encodedImage.getWidth());
        Assert.assertEquals(600, encodedImage.getHeight());
        Assert.assertEquals(0, encodedImage.getRotationAngle());
        Assert.assertEquals(0, encodedImage.getExifOrientation());
    }

    @Test
    public void testParseMetaData_SimpleWEBP() throws IOException {
        checkWebpImage("images/1_webp_plain.webp", WEBP_SIMPLE, 320, 214);
    }

    @Test
    public void testParseMetaData_LosslessWEBP() throws IOException {
        checkWebpImage("images/1_webp_ll.webp", WEBP_LOSSLESS, 400, 301);
    }

    @Test
    public void testParseMetaData_ExtendedWithAlphaWEBP() throws IOException {
        checkWebpImage("images/1_webp_ea.webp", WEBP_EXTENDED_WITH_ALPHA, 400, 301);
    }

    @Test
    public void testParseMetaData_ExtendedWEBP() throws IOException {
        checkWebpImage("images/1_webp_e.webp", WEBP_EXTENDED, 480, 320);
    }

    @Test
    public void testParseMetaData_AnimatedWEBP() throws IOException {
        checkWebpImage("images/1_webp_anim.webp", WEBP_ANIMATED, 322, 477);
    }

    @Test
    public void testIsJpegCompleteAt_notComplete() {
        byte[] encodedBytes = new byte[EncodedImageTest.ENCODED_BYTES_LENGTH];
        encodedBytes[((EncodedImageTest.ENCODED_BYTES_LENGTH) - 2)] = 0;
        encodedBytes[((EncodedImageTest.ENCODED_BYTES_LENGTH) - 1)] = 0;
        PooledByteBuffer buf = new TrivialPooledByteBuffer(encodedBytes);
        EncodedImage encodedImage = new EncodedImage(CloseableReference.of(buf));
        encodedImage.setImageFormat(JPEG);
        Assert.assertFalse(encodedImage.isCompleteAt(EncodedImageTest.ENCODED_BYTES_LENGTH));
    }

    @Test
    public void testIsJpegCompleteAt_Complete() {
        byte[] encodedBytes = new byte[EncodedImageTest.ENCODED_BYTES_LENGTH];
        encodedBytes[((EncodedImageTest.ENCODED_BYTES_LENGTH) - 2)] = ((byte) (JfifUtil.MARKER_FIRST_BYTE));
        encodedBytes[((EncodedImageTest.ENCODED_BYTES_LENGTH) - 1)] = ((byte) (JfifUtil.MARKER_EOI));
        PooledByteBuffer buf = new TrivialPooledByteBuffer(encodedBytes);
        EncodedImage encodedImage = new EncodedImage(CloseableReference.of(buf));
        encodedImage.setImageFormat(JPEG);
        Assert.assertTrue(encodedImage.isCompleteAt(EncodedImageTest.ENCODED_BYTES_LENGTH));
    }

    @Test
    public void testCopyMetaData() {
        EncodedImage encodedImage = new EncodedImage(mByteBufferRef);
        encodedImage.setImageFormat(JPEG);
        encodedImage.setRotationAngle(0);
        encodedImage.setExifOrientation(1);
        encodedImage.setWidth(1);
        encodedImage.setHeight(2);
        encodedImage.setSampleSize(3);
        EncodedImage encodedImage2 = new EncodedImage(mByteBufferRef);
        encodedImage2.copyMetaDataFrom(encodedImage);
        Assert.assertEquals(encodedImage.getImageFormat(), encodedImage2.getImageFormat());
        Assert.assertEquals(encodedImage.getWidth(), encodedImage2.getWidth());
        Assert.assertEquals(encodedImage.getHeight(), encodedImage2.getHeight());
        Assert.assertEquals(encodedImage.getSampleSize(), encodedImage2.getSampleSize());
        Assert.assertEquals(encodedImage.getSize(), encodedImage2.getSize());
        Assert.assertEquals(encodedImage.getExifOrientation(), encodedImage2.getExifOrientation());
        EncodedImage encodedImage3 = new EncodedImage(mInputStreamSupplier);
        encodedImage3.setImageFormat(JPEG);
        encodedImage3.setRotationAngle(0);
        encodedImage3.setExifOrientation(1);
        encodedImage3.setWidth(1);
        encodedImage3.setHeight(2);
        encodedImage3.setSampleSize(3);
        encodedImage3.setStreamSize(4);
        EncodedImage encodedImage4 = new EncodedImage(mInputStreamSupplier);
        encodedImage4.copyMetaDataFrom(encodedImage3);
        Assert.assertEquals(encodedImage3.getImageFormat(), encodedImage4.getImageFormat());
        Assert.assertEquals(encodedImage3.getWidth(), encodedImage4.getWidth());
        Assert.assertEquals(encodedImage3.getHeight(), encodedImage4.getHeight());
        Assert.assertEquals(encodedImage3.getSampleSize(), encodedImage4.getSampleSize());
        Assert.assertEquals(encodedImage3.getSize(), encodedImage4.getSize());
        Assert.assertEquals(encodedImage3.getExifOrientation(), encodedImage4.getExifOrientation());
    }
}

