/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.image.loader;


import Frame.DEPTH_UBYTE;
import NativeImageLoader.MultiPageMode;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Random;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacv.Frame;
import org.datavec.image.data.ImageWritable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.io.ClassPathResource;


/**
 *
 *
 * @author saudet
 */
public class TestNativeImageLoader {
    static final long seed = 10;

    static final Random rng = new Random(TestNativeImageLoader.seed);

    @Test
    public void testConvertPix() throws Exception {
        PIX pix;
        Mat mat;
        pix = pixCreate(11, 22, 1);
        mat = NativeImageLoader.convert(pix);
        Assert.assertEquals(11, mat.cols());
        Assert.assertEquals(22, mat.rows());
        Assert.assertEquals(CV_8UC1, mat.type());
        pix = pixCreate(33, 44, 2);
        mat = NativeImageLoader.convert(pix);
        Assert.assertEquals(33, mat.cols());
        Assert.assertEquals(44, mat.rows());
        Assert.assertEquals(CV_8UC1, mat.type());
        pix = pixCreate(55, 66, 4);
        mat = NativeImageLoader.convert(pix);
        Assert.assertEquals(55, mat.cols());
        Assert.assertEquals(66, mat.rows());
        Assert.assertEquals(CV_8UC1, mat.type());
        pix = pixCreate(77, 88, 8);
        mat = NativeImageLoader.convert(pix);
        Assert.assertEquals(77, mat.cols());
        Assert.assertEquals(88, mat.rows());
        Assert.assertEquals(CV_8UC1, mat.type());
        pix = pixCreate(99, 111, 16);
        mat = NativeImageLoader.convert(pix);
        Assert.assertEquals(99, mat.cols());
        Assert.assertEquals(111, mat.rows());
        Assert.assertEquals(CV_8UC2, mat.type());
        pix = pixCreate(222, 333, 24);
        mat = NativeImageLoader.convert(pix);
        Assert.assertEquals(222, mat.cols());
        Assert.assertEquals(333, mat.rows());
        Assert.assertEquals(CV_8UC3, mat.type());
        pix = pixCreate(444, 555, 32);
        mat = NativeImageLoader.convert(pix);
        Assert.assertEquals(444, mat.cols());
        Assert.assertEquals(555, mat.rows());
        Assert.assertEquals(CV_8UC4, mat.type());
        // a GIF file, for example
        pix = pixCreate(32, 32, 8);
        PIXCMAP cmap = pixcmapCreateLinear(8, 256);
        pixSetColormap(pix, cmap);
        mat = NativeImageLoader.convert(pix);
        Assert.assertEquals(32, mat.cols());
        Assert.assertEquals(32, mat.rows());
        Assert.assertEquals(CV_8UC4, mat.type());
        int w4 = 100;
        int h4 = 238;
        int ch4 = 1;
        int pages = 1;
        String path2MitosisFile = "datavec-data-image/testimages2/mitosis.tif";
        NativeImageLoader loader5 = new NativeImageLoader(h4, w4, ch4, MultiPageMode.FIRST);
        INDArray array6 = null;
        try {
            array6 = loader5.asMatrix(new ClassPathResource(path2MitosisFile).getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(4, array6.rank());
        Assert.assertEquals(pages, array6.size(0));
        Assert.assertEquals(ch4, array6.size(1));
        Assert.assertEquals(h4, array6.size(2));
        Assert.assertEquals(w4, array6.size(3));
        int ch5 = 4;
        int pages1 = 1;
        NativeImageLoader loader6 = new NativeImageLoader(h4, w4, 1, MultiPageMode.CHANNELS);
        loader6.direct = false;// simulate conditions under Android

        INDArray array7 = null;
        try {
            array7 = loader6.asMatrix(new ClassPathResource(path2MitosisFile).getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(4, array7.rank());
        Assert.assertEquals(pages1, array7.size(0));
        Assert.assertEquals(ch5, array7.size(1));
        Assert.assertEquals(h4, array7.size(2));
        Assert.assertEquals(w4, array7.size(3));
        int ch6 = 1;
        int pages2 = 4;
        NativeImageLoader loader7 = new NativeImageLoader(h4, w4, ch6, MultiPageMode.MINIBATCH);
        INDArray array8 = null;
        try {
            array8 = loader7.asMatrix(new ClassPathResource(path2MitosisFile).getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(4, array8.rank());
        Assert.assertEquals(pages2, array8.size(0));
        Assert.assertEquals(ch6, array8.size(1));
        Assert.assertEquals(h4, array8.size(2));
        Assert.assertEquals(w4, array8.size(3));
    }

    @Test
    public void testAsRowVector() throws Exception {
        org.opencv.core.Mat img1 = makeRandomOrgOpenCvCoreMatImage(0, 0, 1);
        Mat img2 = makeRandomImage(0, 0, 3);
        int w1 = 35;
        int h1 = 79;
        int ch1 = 3;
        NativeImageLoader loader1 = new NativeImageLoader(h1, w1, ch1);
        INDArray array1 = loader1.asRowVector(img1);
        Assert.assertEquals(2, array1.rank());
        Assert.assertEquals(1, array1.rows());
        Assert.assertEquals(((h1 * w1) * ch1), array1.columns());
        Assert.assertNotEquals(0.0, array1.sum().getDouble(0), 0.0);
        INDArray array2 = loader1.asRowVector(img2);
        Assert.assertEquals(2, array2.rank());
        Assert.assertEquals(1, array2.rows());
        Assert.assertEquals(((h1 * w1) * ch1), array2.columns());
        Assert.assertNotEquals(0.0, array2.sum().getDouble(0), 0.0);
        int w2 = 103;
        int h2 = 68;
        int ch2 = 4;
        NativeImageLoader loader2 = new NativeImageLoader(h2, w2, ch2);
        loader2.direct = false;// simulate conditions under Android

        INDArray array3 = loader2.asRowVector(img1);
        Assert.assertEquals(2, array3.rank());
        Assert.assertEquals(1, array3.rows());
        Assert.assertEquals(((h2 * w2) * ch2), array3.columns());
        Assert.assertNotEquals(0.0, array3.sum().getDouble(0), 0.0);
        INDArray array4 = loader2.asRowVector(img2);
        Assert.assertEquals(2, array4.rank());
        Assert.assertEquals(1, array4.rows());
        Assert.assertEquals(((h2 * w2) * ch2), array4.columns());
        Assert.assertNotEquals(0.0, array4.sum().getDouble(0), 0.0);
    }

    @Test
    public void testAsMatrix() throws Exception {
        BufferedImage img1 = makeRandomBufferedImage(0, 0, 3);
        Mat img2 = makeRandomImage(0, 0, 4);
        int w1 = 33;
        int h1 = 77;
        int ch1 = 1;
        NativeImageLoader loader1 = new NativeImageLoader(h1, w1, ch1);
        INDArray array1 = loader1.asMatrix(img1);
        Assert.assertEquals(4, array1.rank());
        Assert.assertEquals(1, array1.size(0));
        Assert.assertEquals(1, array1.size(1));
        Assert.assertEquals(h1, array1.size(2));
        Assert.assertEquals(w1, array1.size(3));
        Assert.assertNotEquals(0.0, array1.sum().getDouble(0), 0.0);
        INDArray array2 = loader1.asMatrix(img2);
        Assert.assertEquals(4, array2.rank());
        Assert.assertEquals(1, array2.size(0));
        Assert.assertEquals(1, array2.size(1));
        Assert.assertEquals(h1, array2.size(2));
        Assert.assertEquals(w1, array2.size(3));
        Assert.assertNotEquals(0.0, array2.sum().getDouble(0), 0.0);
        int w2 = 111;
        int h2 = 66;
        int ch2 = 3;
        NativeImageLoader loader2 = new NativeImageLoader(h2, w2, ch2);
        loader2.direct = false;// simulate conditions under Android

        INDArray array3 = loader2.asMatrix(img1);
        Assert.assertEquals(4, array3.rank());
        Assert.assertEquals(1, array3.size(0));
        Assert.assertEquals(3, array3.size(1));
        Assert.assertEquals(h2, array3.size(2));
        Assert.assertEquals(w2, array3.size(3));
        Assert.assertNotEquals(0.0, array3.sum().getDouble(0), 0.0);
        INDArray array4 = loader2.asMatrix(img2);
        Assert.assertEquals(4, array4.rank());
        Assert.assertEquals(1, array4.size(0));
        Assert.assertEquals(3, array4.size(1));
        Assert.assertEquals(h2, array4.size(2));
        Assert.assertEquals(w2, array4.size(3));
        Assert.assertNotEquals(0.0, array4.sum().getDouble(0), 0.0);
        int w3 = 123;
        int h3 = 77;
        int ch3 = 3;
        NativeImageLoader loader3 = new NativeImageLoader(h3, w3, ch3);
        File f3 = new ClassPathResource("datavec-data-image/testimages/class0/2.jpg").getFile();
        ImageWritable iw3 = loader3.asWritable(f3);
        INDArray array5 = loader3.asMatrix(iw3);
        Assert.assertEquals(4, array5.rank());
        Assert.assertEquals(1, array5.size(0));
        Assert.assertEquals(3, array5.size(1));
        Assert.assertEquals(h3, array5.size(2));
        Assert.assertEquals(w3, array5.size(3));
        Assert.assertNotEquals(0.0, array5.sum().getDouble(0), 0.0);
        Mat mat = loader3.asMat(array5);
        Assert.assertEquals(w3, mat.cols());
        Assert.assertEquals(h3, mat.rows());
        Assert.assertEquals(ch3, mat.channels());
        Assert.assertTrue((((mat.type()) == (CV_32FC(ch3))) || ((mat.type()) == (CV_64FC(ch3)))));
        Assert.assertNotEquals(0.0, sumElems(mat).get(), 0.0);
        Frame frame = loader3.asFrame(array5, DEPTH_UBYTE);
        Assert.assertEquals(w3, frame.imageWidth);
        Assert.assertEquals(h3, frame.imageHeight);
        Assert.assertEquals(ch3, frame.imageChannels);
        Assert.assertEquals(DEPTH_UBYTE, frame.imageDepth);
        Java2DNativeImageLoader loader4 = new Java2DNativeImageLoader();
        BufferedImage img12 = loader4.asBufferedImage(array1);
        Assert.assertEquals(array1, loader4.asMatrix(img12));
        NativeImageLoader loader5 = new NativeImageLoader(0, 0, 0);
        loader5.direct = false;// simulate conditions under Android

        INDArray array7 = loader5.asMatrix(f3);
        Assert.assertEquals(4, array7.rank());
        Assert.assertEquals(1, array7.size(0));
        Assert.assertEquals(3, array7.size(1));
        Assert.assertEquals(32, array7.size(2));
        Assert.assertEquals(32, array7.size(3));
        Assert.assertNotEquals(0.0, array7.sum().getDouble(0), 0.0);
    }

    @Test
    public void testScalingIfNeed() throws Exception {
        Mat img1 = makeRandomImage(0, 0, 1);
        Mat img2 = makeRandomImage(0, 0, 3);
        int w1 = 60;
        int h1 = 110;
        int ch1 = 1;
        NativeImageLoader loader1 = new NativeImageLoader(h1, w1, ch1);
        Mat scaled1 = loader1.scalingIfNeed(img1);
        Assert.assertEquals(h1, scaled1.rows());
        Assert.assertEquals(w1, scaled1.cols());
        Assert.assertEquals(img1.channels(), scaled1.channels());
        Assert.assertNotEquals(0.0, sumElems(scaled1).get(), 0.0);
        Mat scaled2 = loader1.scalingIfNeed(img2);
        Assert.assertEquals(h1, scaled2.rows());
        Assert.assertEquals(w1, scaled2.cols());
        Assert.assertEquals(img2.channels(), scaled2.channels());
        Assert.assertNotEquals(0.0, sumElems(scaled2).get(), 0.0);
        int w2 = 70;
        int h2 = 120;
        int ch2 = 3;
        NativeImageLoader loader2 = new NativeImageLoader(h2, w2, ch2);
        loader2.direct = false;// simulate conditions under Android

        Mat scaled3 = loader2.scalingIfNeed(img1);
        Assert.assertEquals(h2, scaled3.rows());
        Assert.assertEquals(w2, scaled3.cols());
        Assert.assertEquals(img1.channels(), scaled3.channels());
        Assert.assertNotEquals(0.0, sumElems(scaled3).get(), 0.0);
        Mat scaled4 = loader2.scalingIfNeed(img2);
        Assert.assertEquals(h2, scaled4.rows());
        Assert.assertEquals(w2, scaled4.cols());
        Assert.assertEquals(img2.channels(), scaled4.channels());
        Assert.assertNotEquals(0.0, sumElems(scaled4).get(), 0.0);
    }

    @Test
    public void testCenterCropIfNeeded() throws Exception {
        int w1 = 60;
        int h1 = 110;
        int ch1 = 1;
        int w2 = 120;
        int h2 = 70;
        int ch2 = 3;
        Mat img1 = makeRandomImage(h1, w1, ch1);
        Mat img2 = makeRandomImage(h2, w2, ch2);
        NativeImageLoader loader = new NativeImageLoader(h1, w1, ch1, true);
        Mat cropped1 = loader.centerCropIfNeeded(img1);
        Assert.assertEquals(85, cropped1.rows());
        Assert.assertEquals(60, cropped1.cols());
        Assert.assertEquals(img1.channels(), cropped1.channels());
        Assert.assertNotEquals(0.0, sumElems(cropped1).get(), 0.0);
        Mat cropped2 = loader.centerCropIfNeeded(img2);
        Assert.assertEquals(70, cropped2.rows());
        Assert.assertEquals(95, cropped2.cols());
        Assert.assertEquals(img2.channels(), cropped2.channels());
        Assert.assertNotEquals(0.0, sumElems(cropped2).get(), 0.0);
    }

    @Test
    public void testAsWritable() throws Exception {
        String f0 = new ClassPathResource("datavec-data-image/testimages/class0/0.jpg").getFile().getAbsolutePath();
        NativeImageLoader imageLoader = new NativeImageLoader();
        ImageWritable img = imageLoader.asWritable(f0);
        Assert.assertEquals(32, img.getFrame().imageHeight);
        Assert.assertEquals(32, img.getFrame().imageWidth);
        Assert.assertEquals(3, img.getFrame().imageChannels);
        BufferedImage img1 = makeRandomBufferedImage(0, 0, 3);
        Mat img2 = makeRandomImage(0, 0, 4);
        int w1 = 33;
        int h1 = 77;
        int ch1 = 1;
        NativeImageLoader loader1 = new NativeImageLoader(h1, w1, ch1);
        INDArray array1 = loader1.asMatrix(f0);
        Assert.assertEquals(4, array1.rank());
        Assert.assertEquals(1, array1.size(0));
        Assert.assertEquals(1, array1.size(1));
        Assert.assertEquals(h1, array1.size(2));
        Assert.assertEquals(w1, array1.size(3));
        Assert.assertNotEquals(0.0, array1.sum().getDouble(0), 0.0);
    }

    @Test
    public void testBufferRealloc() throws Exception {
        Field f = NativeImageLoader.class.getDeclaredField("buffer");
        Field m = NativeImageLoader.class.getDeclaredField("bufferMat");
        f.setAccessible(true);
        m.setAccessible(true);
        File f1 = new ClassPathResource("datavec-data-image/voc/2007/JPEGImages/000005.jpg").getFile();
        String f2 = new ClassPathResource("datavec-data-image/voc/2007/JPEGImages/000007.jpg").getFile().getAbsolutePath();
        // Start with a large buffer
        byte[] buffer = new byte[(20 * 1024) * 1024];
        Mat bufferMat = new Mat(buffer);
        NativeImageLoader loader = new NativeImageLoader(28, 28, 1);
        f.set(loader, buffer);
        m.set(loader, bufferMat);
        INDArray img1LargeBuffer = loader.asMatrix(f1);
        INDArray img2LargeBuffer = loader.asMatrix(f2);
        // Check multiple reads:
        INDArray img1LargeBuffer2 = loader.asMatrix(f1);
        INDArray img1LargeBuffer3 = loader.asMatrix(f1);
        Assert.assertEquals(img1LargeBuffer2, img1LargeBuffer3);
        INDArray img2LargeBuffer2 = loader.asMatrix(f1);
        INDArray img2LargeBuffer3 = loader.asMatrix(f1);
        Assert.assertEquals(img2LargeBuffer2, img2LargeBuffer3);
        // Clear the buffer and re-read:
        f.set(loader, null);
        INDArray img1NoBuffer1 = loader.asMatrix(f1);
        INDArray img1NoBuffer2 = loader.asMatrix(f1);
        Assert.assertEquals(img1LargeBuffer, img1NoBuffer1);
        Assert.assertEquals(img1LargeBuffer, img1NoBuffer2);
        f.set(loader, null);
        INDArray img2NoBuffer1 = loader.asMatrix(f2);
        INDArray img2NoBuffer2 = loader.asMatrix(f2);
        Assert.assertEquals(img2LargeBuffer, img2NoBuffer1);
        Assert.assertEquals(img2LargeBuffer, img2NoBuffer2);
        // Assign much too small buffer:
        buffer = new byte[10];
        bufferMat = new Mat(buffer);
        f.set(loader, buffer);
        m.set(loader, bufferMat);
        INDArray img1SmallBuffer1 = loader.asMatrix(f1);
        INDArray img1SmallBuffer2 = loader.asMatrix(f1);
        Assert.assertEquals(img1LargeBuffer, img1SmallBuffer1);
        Assert.assertEquals(img1LargeBuffer, img1SmallBuffer2);
        f.set(loader, buffer);
        m.set(loader, bufferMat);
        INDArray img2SmallBuffer1 = loader.asMatrix(f2);
        INDArray img2SmallBuffer2 = loader.asMatrix(f2);
        Assert.assertEquals(img2LargeBuffer, img2SmallBuffer1);
        Assert.assertEquals(img2LargeBuffer, img2SmallBuffer2);
        // Assign an exact buffer:
        try (InputStream is = new FileInputStream(f1)) {
            byte[] temp = IOUtils.toByteArray(is);
            buffer = new byte[temp.length];
            bufferMat = new Mat(buffer);
        }
        f.set(loader, buffer);
        m.set(loader, bufferMat);
        INDArray img1ExactBuffer = loader.asMatrix(f1);
        Assert.assertEquals(img1LargeBuffer, img1ExactBuffer);
    }
}

