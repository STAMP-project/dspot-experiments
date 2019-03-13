/**
 * Copyright (C) 2015-2016 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bytedeco.javacv;


import OpenCVFrameConverter.ToIplImage;
import OpenCVFrameConverter.ToMat;
import OpenCVFrameConverter.ToOrgOpenCvCoreMat;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.opencv.opencv_java;
import org.junit.Assert;
import org.junit.Test;

import static Frame.DEPTH_FLOAT;
import static Frame.DEPTH_SHORT;
import static Frame.DEPTH_UBYTE;


/**
 * Test cases for FrameConverter classes. Also uses other classes from JavaCV.
 *
 * @author Samuel Audet
 */
public class FrameConverterTest {
    @Test
    public void testAndroidFrameConverter() {
        System.out.println("AndroidFrameConverter");
        AndroidFrameConverter converter = new AndroidFrameConverter();
        int width = 512;
        int height = 1024;
        byte[] yuvData = new byte[((3 * width) * height) / 2];
        for (int i = 0; i < (yuvData.length); i++) {
            yuvData[i] = ((byte) (i));
        }
        Mat yuvImage = new Mat(((3 * height) / 2), width, CV_8UC1, new BytePointer(yuvData));
        Mat bgrImage = new Mat(height, width, CV_8UC3);
        cvtColor(yuvImage, bgrImage, CV_YUV2BGR_NV21);
        Frame bgrFrame = converter.convert(yuvData, width, height);
        UByteIndexer bgrImageIdx = bgrImage.createIndexer();
        UByteIndexer bgrFrameIdx = bgrFrame.createIndexer();
        Assert.assertEquals(bgrImageIdx.rows(), bgrFrameIdx.rows());
        Assert.assertEquals(bgrImageIdx.cols(), bgrFrameIdx.cols());
        Assert.assertEquals(bgrImageIdx.channels(), bgrFrameIdx.channels());
        for (int i = 0; i < (bgrImageIdx.rows()); i++) {
            for (int j = 0; j < (bgrImageIdx.cols()); j++) {
                for (int k = 0; k < (bgrImageIdx.channels()); k++) {
                    Assert.assertEquals(((float) (bgrImageIdx.get(i, j, k))), ((float) (bgrFrameIdx.get(i, j, k))), 1.0F);
                }
            }
        }
        bgrImageIdx.release();
        bgrFrameIdx.release();
        Frame grayFrame = new Frame((1024 + 1), 768, DEPTH_UBYTE, 1);
        Frame colorFrame = new Frame((640 + 1), 480, DEPTH_UBYTE, 3);
        UByteIndexer grayFrameIdx = grayFrame.createIndexer();
        for (int i = 0; i < (grayFrameIdx.rows()); i++) {
            for (int j = 0; j < (grayFrameIdx.cols()); j++) {
                grayFrameIdx.put(i, j, (i + j));
            }
        }
        UByteIndexer colorFrameIdx = colorFrame.createIndexer();
        for (int i = 0; i < (colorFrameIdx.rows()); i++) {
            for (int j = 0; j < (colorFrameIdx.cols()); j++) {
                for (int k = 0; k < (colorFrameIdx.channels()); k++) {
                    colorFrameIdx.put(i, j, k, ((i + j) + k));
                }
            }
        }
        width = grayFrame.imageWidth;
        height = grayFrame.imageHeight;
        int stride = grayFrame.imageStride;
        int rowBytes = width * 4;
        ByteBuffer in = ((ByteBuffer) (grayFrame.image[0]));
        ByteBuffer buffer = converter.gray2rgba(in, width, height, stride, rowBytes);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // GRAY -> RGBA
                byte B = in.get(((y * stride) + x));
                Assert.assertEquals(buffer.get(((y * rowBytes) + (4 * x))), B);
                Assert.assertEquals(buffer.get((((y * rowBytes) + (4 * x)) + 1)), B);
                Assert.assertEquals(buffer.get((((y * rowBytes) + (4 * x)) + 2)), B);
                Assert.assertEquals(buffer.get((((y * rowBytes) + (4 * x)) + 3)), ((byte) (255)));
            }
        }
        width = colorFrame.imageWidth;
        height = colorFrame.imageHeight;
        stride = colorFrame.imageStride;
        rowBytes = width * 4;
        in = ((ByteBuffer) (colorFrame.image[0]));
        buffer = converter.bgr2rgba(in, width, height, stride, rowBytes);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // BGR -> RGBA
                byte B = in.get(((y * stride) + (3 * x)));
                byte G = in.get((((y * stride) + (3 * x)) + 1));
                byte R = in.get((((y * stride) + (3 * x)) + 2));
                Assert.assertEquals(buffer.get(((y * rowBytes) + (4 * x))), R);
                Assert.assertEquals(buffer.get((((y * rowBytes) + (4 * x)) + 1)), G);
                Assert.assertEquals(buffer.get((((y * rowBytes) + (4 * x)) + 2)), B);
                Assert.assertEquals(buffer.get((((y * rowBytes) + (4 * x)) + 3)), ((byte) (255)));
            }
        }
        colorFrameIdx.release();
        grayFrameIdx.release();
    }

    @Test
    public void testJava2DFrameConverter() {
        System.out.println("Java2DFrameConverter");
        int[] depths = new int[]{ DEPTH_UBYTE, DEPTH_SHORT, DEPTH_FLOAT };
        int[] channels = new int[]{ 1, 3, 4 };
        for (int i = 0; i < (depths.length); i++) {
            for (int j = 0; j < (channels.length); j++) {
                Frame frame = new Frame((640 + 1), 480, depths[i], channels[j]);
                Java2DFrameConverter converter = new Java2DFrameConverter();
                Indexer frameIdx = frame.createIndexer();
                for (int y = 0; y < (frameIdx.rows()); y++) {
                    for (int x = 0; x < (frameIdx.cols()); x++) {
                        for (int z = 0; z < (frameIdx.channels()); z++) {
                            frameIdx.putDouble(new long[]{ y, x, z }, ((y + x) + z));
                        }
                    }
                }
                BufferedImage image = converter.convert(frame);
                converter.frame = null;
                Frame frame2 = converter.convert(image);
                Indexer frame2Idx = frame2.createIndexer();
                for (int y = 0; y < (frameIdx.rows()); y++) {
                    for (int x = 0; x < (frameIdx.cols()); x++) {
                        for (int z = 0; z < (frameIdx.channels()); z++) {
                            double value = frameIdx.getDouble(y, x, z);
                            Assert.assertEquals(value, frame2Idx.getDouble(y, x, z), 0);
                        }
                    }
                }
                try {
                    frame2Idx.getDouble(((frameIdx.rows()) + 1), ((frameIdx.cols()) + 1));
                    Assert.fail("IndexOutOfBoundsException should have been thrown.");
                } catch (IndexOutOfBoundsException e) {
                }
                frameIdx.release();
                frame2Idx.release();
            }
        }
        int[] types = new int[]{ BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_INT_ARGB, BufferedImage.TYPE_INT_ARGB_PRE, BufferedImage.TYPE_INT_BGR };
        for (int i = 0; i < (types.length); i++) {
            BufferedImage image = new BufferedImage((640 + 1), 480, types[i]);
            Java2DFrameConverter converter = new Java2DFrameConverter();
            WritableRaster raster = image.getRaster();
            int[] array = ((DataBufferInt) (raster.getDataBuffer())).getData();
            for (int j = 0; j < (array.length); j++) {
                array[j] = j;
            }
            Frame frame = converter.convert(image);
            converter.bufferedImage = null;
            BufferedImage image2 = converter.convert(frame);
            WritableRaster raster2 = image2.getRaster();
            byte[] array2 = ((DataBufferByte) (raster2.getDataBuffer())).getData();
            for (int j = 0; j < (array.length); j++) {
                int n = (((((array2[(4 * j)]) & 255) << 24) | (((array2[((4 * j) + 1)]) & 255) << 16)) | (((array2[((4 * j) + 2)]) & 255) << 8)) | ((array2[((4 * j) + 3)]) & 255);
                Assert.assertEquals(array[j], n);
            }
        }
    }

    @Test
    public void testOpenCVFrameConverter() {
        System.out.println("OpenCVFrameConverter");
        Loader.load(opencv_java.class);
        for (int depth = 8; depth <= 64; depth *= 2) {
            Assert.assertEquals(depth, OpenCVFrameConverter.getFrameDepth(OpenCVFrameConverter.getIplImageDepth(depth)));
            Assert.assertEquals(depth, OpenCVFrameConverter.getFrameDepth(OpenCVFrameConverter.getMatDepth(depth)));
            if (depth < 64) {
                Assert.assertEquals((-depth), OpenCVFrameConverter.getFrameDepth(OpenCVFrameConverter.getIplImageDepth((-depth))));
                Assert.assertEquals((-depth), OpenCVFrameConverter.getFrameDepth(OpenCVFrameConverter.getMatDepth((-depth))));
            }
        }
        Frame frame = new Frame((640 + 1), 480, DEPTH_UBYTE, 3);
        OpenCVFrameConverter.ToIplImage converter1 = new OpenCVFrameConverter.ToIplImage();
        OpenCVFrameConverter.ToMat converter2 = new OpenCVFrameConverter.ToMat();
        OpenCVFrameConverter.ToOrgOpenCvCoreMat converter3 = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();
        UByteIndexer frameIdx = frame.createIndexer();
        for (int i = 0; i < (frameIdx.rows()); i++) {
            for (int j = 0; j < (frameIdx.cols()); j++) {
                for (int k = 0; k < (frameIdx.channels()); k++) {
                    frameIdx.put(i, j, k, ((i + j) + k));
                }
            }
        }
        IplImage image = converter1.convert(frame);
        Mat mat = converter2.convert(frame);
        final org.opencv.core.Mat cvmat = converter3.convert(frame);
        converter1.frame = null;
        converter2.frame = null;
        converter3.frame = null;
        Frame frame1 = converter1.convert(image);
        Frame frame2 = converter2.convert(mat);
        Frame frame3 = converter3.convert(cvmat);
        Assert.assertEquals(frame2.opaque, mat);
        Assert.assertEquals(frame3.opaque, cvmat);
        Mat mat2 = new Mat(mat.rows(), mat.cols(), mat.type(), mat.data(), mat.step());
        org.opencv.core.Mat cvmat2 = new org.opencv.core.Mat(cvmat.rows(), cvmat.cols(), cvmat.type(), new BytePointer() {
            {
                address = cvmat.dataAddr();
            }
        }.capacity((((cvmat.rows()) * (cvmat.cols())) * (cvmat.elemSize()))).asByteBuffer());
        Assert.assertNotEquals(mat, mat2);
        Assert.assertNotEquals(cvmat, cvmat2);
        frame2 = converter2.convert(mat2);
        frame3 = converter3.convert(cvmat2);
        Assert.assertEquals(frame2.opaque, mat2);
        Assert.assertEquals(frame3.opaque, cvmat2);
        // official Java API does not support memory aligned strides...
        Assert.assertEquals(((cvmat2.cols()) * (cvmat2.channels())), frame3.imageStride);
        frame3.imageStride = frame2.imageStride;
        UByteIndexer frame1Idx = frame1.createIndexer();
        UByteIndexer frame2Idx = frame2.createIndexer();
        UByteIndexer frame3Idx = frame3.createIndexer();
        for (int i = 0; i < (frameIdx.rows()); i++) {
            for (int j = 0; j < (frameIdx.cols()); j++) {
                for (int k = 0; k < (frameIdx.channels()); k++) {
                    int b = frameIdx.get(i, j, k);
                    Assert.assertEquals(b, frame1Idx.get(i, j, k));
                    Assert.assertEquals(b, frame2Idx.get(i, j, k));
                    if (i < ((frameIdx.rows()) - 2)) {
                        // ... so also cannot access most of the 2 last rows
                        Assert.assertEquals(b, frame3Idx.get(i, j, k));
                    }
                }
            }
        }
        try {
            frame1Idx.get(((frameIdx.rows()) + 1), ((frameIdx.cols()) + 1));
            Assert.fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            frame2Idx.get(((frameIdx.rows()) + 1), ((frameIdx.cols()) + 1));
            Assert.fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            frame3Idx.get(((frameIdx.rows()) + 1), ((frameIdx.cols()) + 1));
            Assert.fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) {
        }
        frameIdx.release();
        frame1Idx.release();
        frame2Idx.release();
        frame3Idx.release();
    }

    @Test
    public void testLeptonicaFrameConverter() {
        System.out.println("LeptonicaFrameConverter");
        Frame frame = new Frame((640 + 1), 480, DEPTH_UBYTE, 3);
        LeptonicaFrameConverter converter = new LeptonicaFrameConverter();
        UByteIndexer frameIdx = frame.createIndexer();
        for (int i = 0; i < (frameIdx.rows()); i++) {
            for (int j = 0; j < (frameIdx.cols()); j++) {
                for (int k = 0; k < (frameIdx.channels()); k++) {
                    frameIdx.put(i, j, k, ((i + j) + k));
                }
            }
        }
        PIX pix = converter.convert(frame);
        converter.frame = null;
        Frame frame1 = converter.convert(pix);
        Assert.assertEquals(frame1.opaque, pix);
        PIX pix2 = PIX.createHeader(pix.w(), pix.h(), pix.d()).data(pix.data()).wpl(pix.wpl());
        Assert.assertNotEquals(pix, pix2);
        Frame frame2 = converter.convert(pix2);
        Assert.assertEquals(frame2.opaque, pix2);
        IntBuffer frameBuf = ((ByteBuffer) (frame.image[0].position(0))).asIntBuffer();
        IntBuffer frame1Buf = ((ByteBuffer) (frame1.image[0].position(0))).asIntBuffer();
        IntBuffer frame2Buf = ((ByteBuffer) (frame2.image[0].position(0))).asIntBuffer();
        IntBuffer pixBuf = pix.createBuffer().order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        IntBuffer pix2Buf = pix2.createBuffer().order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        for (int i = 0; i < (frameBuf.capacity()); i++) {
            int j = frameBuf.get(i);
            Assert.assertEquals(j, frame1Buf.get(i));
            Assert.assertEquals(j, frame2Buf.get(i));
            Assert.assertEquals(j, pixBuf.get(i));
            Assert.assertEquals(j, pix2Buf.get(i));
        }
        try {
            frame1Buf.get(((frameBuf.capacity()) + 1));
            Assert.fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            frame2Buf.get(((frameBuf.capacity()) + 1));
            Assert.fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            pixBuf.get(((frameBuf.capacity()) + 1));
            Assert.fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            pix2Buf.get(((frameBuf.capacity()) + 1));
            Assert.fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) {
        }
        pix2.deallocate();
        pix.deallocate();
    }
}

