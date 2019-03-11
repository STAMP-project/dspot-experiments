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
package org.datavec.image.transform;


import OpenCVFrameConverter.ToMat;
import Scalar.GRAY;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.datavec.image.data.ImageWritable;
import org.datavec.image.loader.NativeImageLoader;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.primitives.Pair;


/**
 *
 *
 * @author saudet
 */
public class TestImageTransform {
    static final long seed = 10;

    static final Random rng = new Random(TestImageTransform.seed);

    static final ToMat converter = new OpenCVFrameConverter.ToMat();

    @Test
    public void testBoxImageTransform() throws Exception {
        ImageTransform transform = new BoxImageTransform(TestImageTransform.rng, 237, 242).borderValue(GRAY);
        for (int i = 0; i < 100; i++) {
            ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, ((i % 4) + 1));
            Frame frame = writable.getFrame();
            ImageWritable w = transform.transform(writable);
            Frame f = w.getFrame();
            Assert.assertEquals(237, f.imageWidth);
            Assert.assertEquals(242, f.imageHeight);
            Assert.assertEquals(frame.imageChannels, f.imageChannels);
            float[] coordinates = new float[]{ 1, 2, 3, 4, 0, 0 };
            float[] transformed = transform.query(coordinates);
            int x = ((frame.imageWidth) - (f.imageWidth)) / 2;
            int y = ((frame.imageHeight) - (f.imageHeight)) / 2;
            Assert.assertEquals((1 - x), transformed[0], 0);
            Assert.assertEquals((2 - y), transformed[1], 0);
            Assert.assertEquals((3 - x), transformed[2], 0);
            Assert.assertEquals((4 - y), transformed[3], 0);
            Assert.assertEquals((-x), transformed[4], 0);
            Assert.assertEquals((-y), transformed[5], 0);
        }
        Assert.assertEquals(null, transform.transform(null));
    }

    @Test
    public void testCropImageTransform() throws Exception {
        ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, 1);
        Frame frame = writable.getFrame();
        ImageTransform transform = new CropImageTransform(TestImageTransform.rng, ((frame.imageHeight) / 2), ((frame.imageWidth) / 2), ((frame.imageHeight) / 2), ((frame.imageWidth) / 2));
        for (int i = 0; i < 100; i++) {
            ImageWritable w = transform.transform(writable);
            Frame f = w.getFrame();
            Assert.assertTrue(((f.imageHeight) <= (frame.imageHeight)));
            Assert.assertTrue(((f.imageWidth) <= (frame.imageWidth)));
            Assert.assertEquals(f.imageChannels, frame.imageChannels);
        }
        Assert.assertEquals(null, transform.transform(null));
        transform = new CropImageTransform(1, 2, 3, 4);
        writable = transform.transform(writable);
        float[] coordinates = new float[]{ 1, 2, 3, 4 };
        float[] transformed = transform.query(coordinates);
        Assert.assertEquals((1 - 2), transformed[0], 0);
        Assert.assertEquals((2 - 1), transformed[1], 0);
        Assert.assertEquals((3 - 2), transformed[2], 0);
        Assert.assertEquals((4 - 1), transformed[3], 0);
    }

    @Test
    public void testFlipImageTransform() throws Exception {
        ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, 3);
        Frame frame = writable.getFrame();
        ImageTransform transform = new FlipImageTransform(TestImageTransform.rng);
        for (int i = 0; i < 100; i++) {
            ImageWritable w = transform.transform(writable);
            Frame f = w.getFrame();
            Assert.assertEquals(f.imageHeight, frame.imageHeight);
            Assert.assertEquals(f.imageWidth, frame.imageWidth);
            Assert.assertEquals(f.imageChannels, frame.imageChannels);
        }
        Assert.assertEquals(null, transform.transform(null));
        transform = new FlipImageTransform((-2));
        writable = transform.transform(writable);
        float[] transformed = transform.query(new float[]{ 10, 20 });
        Assert.assertEquals(10, transformed[0], 0);
        Assert.assertEquals(20, transformed[1], 0);
        transform = new FlipImageTransform(0);
        writable = transform.transform(writable);
        transformed = transform.query(new float[]{ 30, 40 });
        Assert.assertEquals(30, transformed[0], 0);
        Assert.assertEquals((((frame.imageHeight) - 40) - 1), transformed[1], 0);
        transform = new FlipImageTransform(1);
        writable = transform.transform(writable);
        transformed = transform.query(new float[]{ 50, 60 });
        Assert.assertEquals((((frame.imageWidth) - 50) - 1), transformed[0], 0);
        Assert.assertEquals(60, transformed[1], 0);
        transform = new FlipImageTransform((-1));
        writable = transform.transform(writable);
        transformed = transform.query(new float[]{ 70, 80 });
        Assert.assertEquals((((frame.imageWidth) - 70) - 1), transformed[0], 0);
        Assert.assertEquals((((frame.imageHeight) - 80) - 1), transformed[1], 0);
    }

    @Test
    public void testScaleImageTransform() throws Exception {
        ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, 4);
        Frame frame = writable.getFrame();
        ImageTransform transform = new ScaleImageTransform(TestImageTransform.rng, ((frame.imageWidth) / 2), ((frame.imageHeight) / 2));
        for (int i = 0; i < 100; i++) {
            ImageWritable w = transform.transform(writable);
            Frame f = w.getFrame();
            Assert.assertTrue(((f.imageHeight) >= ((frame.imageHeight) / 2)));
            Assert.assertTrue(((f.imageHeight) <= ((3 * (frame.imageHeight)) / 2)));
            Assert.assertTrue(((f.imageWidth) >= ((frame.imageWidth) / 2)));
            Assert.assertTrue(((f.imageWidth) <= ((3 * (frame.imageWidth)) / 2)));
            Assert.assertEquals(f.imageChannels, frame.imageChannels);
        }
        Assert.assertEquals(null, transform.transform(null));
        transform = new ScaleImageTransform(frame.imageWidth, (2 * (frame.imageHeight)));
        writable = transform.transform(writable);
        float[] coordinates = new float[]{ 5, 7, 11, 13 };
        float[] transformed = transform.query(coordinates);
        Assert.assertEquals((5 * 2), transformed[0], 0);
        Assert.assertEquals((7 * 3), transformed[1], 0);
        Assert.assertEquals((11 * 2), transformed[2], 0);
        Assert.assertEquals((13 * 3), transformed[3], 0);
    }

    @Test
    public void testRotateImageTransform() throws Exception {
        ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, 1);
        Frame frame = writable.getFrame();
        ImageTransform transform = new RotateImageTransform(TestImageTransform.rng, 180).interMode(INTER_NEAREST).borderMode(BORDER_REFLECT);
        for (int i = 0; i < 100; i++) {
            ImageWritable w = transform.transform(writable);
            Frame f = w.getFrame();
            Assert.assertEquals(f.imageHeight, frame.imageHeight);
            Assert.assertEquals(f.imageWidth, frame.imageWidth);
            Assert.assertEquals(f.imageChannels, frame.imageChannels);
        }
        Assert.assertEquals(null, transform.transform(null));
        transform = new RotateImageTransform(0, 0, (-90), 0);
        writable = transform.transform(writable);
        float[] coordinates = new float[]{ (frame.imageWidth) / 2, (frame.imageHeight) / 2, 0, 0 };
        float[] transformed = transform.query(coordinates);
        Assert.assertEquals(((frame.imageWidth) / 2), transformed[0], 0);
        Assert.assertEquals(((frame.imageHeight) / 2), transformed[1], 0);
        Assert.assertEquals((((frame.imageHeight) + (frame.imageWidth)) / 2), transformed[2], 1);
        Assert.assertEquals((((frame.imageHeight) - (frame.imageWidth)) / 2), transformed[3], 1);
    }

    @Test
    public void testWarpImageTransform() throws Exception {
        ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, 1);
        Frame frame = writable.getFrame();
        ImageTransform transform = new WarpImageTransform(TestImageTransform.rng, ((frame.imageWidth) / 10)).interMode(INTER_CUBIC).borderMode(BORDER_REPLICATE);
        for (int i = 0; i < 100; i++) {
            ImageWritable w = transform.transform(writable);
            Frame f = w.getFrame();
            Assert.assertEquals(f.imageHeight, frame.imageHeight);
            Assert.assertEquals(f.imageWidth, frame.imageWidth);
            Assert.assertEquals(f.imageChannels, frame.imageChannels);
        }
        Assert.assertEquals(null, transform.transform(null));
        transform = new WarpImageTransform(1, 2, 3, 4, 5, 6, 7, 8);
        writable = transform.transform(writable);
        float[] coordinates = new float[]{ 0, 0, frame.imageWidth, 0, frame.imageWidth, frame.imageHeight, 0, frame.imageHeight };
        float[] transformed = transform.query(coordinates);
        Assert.assertEquals(1, transformed[0], 0);
        Assert.assertEquals(2, transformed[1], 0);
        Assert.assertEquals((3 + (frame.imageWidth)), transformed[2], 0);
        Assert.assertEquals(4, transformed[3], 0);
        Assert.assertEquals((5 + (frame.imageWidth)), transformed[4], 0);
        Assert.assertEquals((6 + (frame.imageHeight)), transformed[5], 0);
        Assert.assertEquals(7, transformed[6], 0);
        Assert.assertEquals((8 + (frame.imageHeight)), transformed[7], 0);
    }

    @Test
    public void testMultiImageTransform() throws Exception {
        ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, 3);
        Frame frame = writable.getFrame();
        ImageTransform transform = new MultiImageTransform(TestImageTransform.rng, new CropImageTransform(10), new FlipImageTransform(), new ScaleImageTransform(10), new WarpImageTransform(10));
        for (int i = 0; i < 100; i++) {
            ImageWritable w = transform.transform(writable);
            Frame f = w.getFrame();
            Assert.assertTrue(((f.imageHeight) >= ((frame.imageHeight) - 30)));
            Assert.assertTrue(((f.imageHeight) <= ((frame.imageHeight) + 20)));
            Assert.assertTrue(((f.imageWidth) >= ((frame.imageWidth) - 30)));
            Assert.assertTrue(((f.imageWidth) <= ((frame.imageWidth) + 20)));
            Assert.assertEquals(f.imageChannels, frame.imageChannels);
        }
        Assert.assertEquals(null, transform.transform(null));
        transform = new MultiImageTransform(new ColorConversionTransform(COLOR_BGR2RGB));
        writable = transform.transform(writable);
        float[] transformed = transform.query(new float[]{ 11, 22 });
        Assert.assertEquals(11, transformed[0], 0);
        Assert.assertEquals(22, transformed[1], 0);
    }

    @Test
    public void testShowImageTransform() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, 3);
        ImageTransform transform = new ShowImageTransform("testShowImageTransform", 100);
        for (int i = 0; i < 10; i++) {
            ImageWritable w = transform.transform(writable);
            Assert.assertEquals(w, writable);
        }
        Assert.assertEquals(null, transform.transform(null));
        float[] transformed = transform.query(new float[]{ 33, 44 });
        Assert.assertEquals(33, transformed[0], 0);
        Assert.assertEquals(44, transformed[1], 0);
    }

    @Test
    public void testConvertColorTransform() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        // Mat origImage = new Mat();
        // Mat transImage = new Mat();
        // OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        ImageWritable writable = TestImageTransform.makeRandomImage(32, 32, 3);
        Frame frame = writable.getFrame();
        ImageTransform showOrig = new ShowImageTransform("Original Image", 50);
        showOrig.transform(writable);
        // origImage = converter.convert(writable.getFrame());
        ImageTransform transform = new ColorConversionTransform(new Random(42), COLOR_BGR2YCrCb);
        ImageWritable w = transform.transform(writable);
        ImageTransform showTrans = new ShowImageTransform("LUV Image", 50);
        showTrans.transform(writable);
        // transImage = converter.convert(writable.getFrame());
        Frame newframe = w.getFrame();
        Assert.assertNotEquals(frame, newframe);
        Assert.assertEquals(null, transform.transform(null));
        float[] transformed = transform.query(new float[]{ 55, 66 });
        Assert.assertEquals(55, transformed[0], 0);
        Assert.assertEquals(66, transformed[1], 0);
    }

    @Test
    public void testHistEqualization() throws CanvasFrame.Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        // TODO pull out historgram to confirm equalization...
        ImageWritable writable = TestImageTransform.makeRandomImage(32, 32, 3);
        Frame frame = writable.getFrame();
        ImageTransform showOrig = new ShowImageTransform("Original Image", 50);
        showOrig.transform(writable);
        ImageTransform transform = new EqualizeHistTransform(new Random(42), COLOR_BGR2YCrCb);
        ImageWritable w = transform.transform(writable);
        ImageTransform showTrans = new ShowImageTransform("LUV Image", 50);
        showTrans.transform(writable);
        Frame newframe = w.getFrame();
        Assert.assertNotEquals(frame, newframe);
        Assert.assertEquals(null, transform.transform(null));
        float[] transformed = transform.query(new float[]{ 66, 77 });
        Assert.assertEquals(66, transformed[0], 0);
        Assert.assertEquals(77, transformed[1], 0);
    }

    @Test
    public void testRandomCropTransform() throws Exception {
        ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, 1);
        Frame frame = writable.getFrame();
        ImageTransform transform = new RandomCropTransform(((frame.imageHeight) / 2), ((frame.imageWidth) / 2));
        for (int i = 0; i < 100; i++) {
            ImageWritable w = transform.transform(writable);
            Frame f = w.getFrame();
            Assert.assertTrue(((f.imageHeight) == ((frame.imageHeight) / 2)));
            Assert.assertTrue(((f.imageWidth) == ((frame.imageWidth) / 2)));
        }
        Assert.assertEquals(null, transform.transform(null));
        transform = new RandomCropTransform(frame.imageHeight, frame.imageWidth);
        writable = transform.transform(writable);
        float[] coordinates = new float[]{ 2, 4, 6, 8 };
        float[] transformed = transform.query(coordinates);
        Assert.assertEquals(2, transformed[0], 0);
        Assert.assertEquals(4, transformed[1], 0);
        Assert.assertEquals(6, transformed[2], 0);
        Assert.assertEquals(8, transformed[3], 0);
    }

    @Test
    public void testProbabilisticPipelineTransform() throws Exception {
        ImageWritable writable = TestImageTransform.makeRandomImage(0, 0, 3);
        Frame frame = writable.getFrame();
        ImageTransform randCrop = new RandomCropTransform(((frame.imageHeight) / 2), ((frame.imageWidth) / 2));
        ImageTransform flip = new FlipImageTransform();
        List<Pair<ImageTransform, Double>> pipeline = new LinkedList<>();
        pipeline.add(new Pair(randCrop, 1.0));
        pipeline.add(new Pair(flip, 0.5));
        ImageTransform transform = new PipelineImageTransform(pipeline, true);
        for (int i = 0; i < 100; i++) {
            ImageWritable w = transform.transform(writable);
            Frame f = w.getFrame();
            Assert.assertTrue(((f.imageHeight) == ((frame.imageHeight) / 2)));
            Assert.assertTrue(((f.imageWidth) == ((frame.imageWidth) / 2)));
            Assert.assertEquals(f.imageChannels, frame.imageChannels);
        }
        Assert.assertEquals(null, transform.transform(null));
        transform = new PipelineImageTransform(new EqualizeHistTransform());
        writable = transform.transform(writable);
        float[] transformed = transform.query(new float[]{ 88, 99 });
        Assert.assertEquals(88, transformed[0], 0);
        Assert.assertEquals(99, transformed[1], 0);
    }

    /**
     * This test code is kind of a manual test using specific image(largestblobtest.jpg)
     * with particular thresholds(blur size, thresholds for edge detector)
     * The cropped largest blob size should be 74x61
     * because we use a specific image and thresholds
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testLargestBlobCropTransform() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        File f1 = new ClassPathResource("datavec-data-image/testimages2/largestblobtest.jpg").getFile();
        NativeImageLoader loader = new NativeImageLoader();
        ImageWritable writable = loader.asWritable(f1);
        ImageTransform showOrig = new ShowImageTransform("Original Image", 50);
        showOrig.transform(writable);
        ImageTransform transform = new LargestBlobCropTransform(null, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, 3, 3, 100, 300, true);
        ImageWritable w = transform.transform(writable);
        ImageTransform showTrans = new ShowImageTransform("Largest Blob", 50);
        showTrans.transform(w);
        Frame newFrame = w.getFrame();
        Assert.assertEquals(newFrame.imageHeight, 74);
        Assert.assertEquals(newFrame.imageWidth, 61);
        float[] transformed = transform.query(new float[]{ 88, 32 });
        Assert.assertEquals(0, transformed[0], 0);
        Assert.assertEquals(0, transformed[1], 0);
    }
}

