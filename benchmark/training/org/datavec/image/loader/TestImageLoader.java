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


import java.awt.image.BufferedImage;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;


public class TestImageLoader {
    private static long seed = 10;

    private static Random rng = new Random(TestImageLoader.seed);

    @Test
    public void testToIntArrayArray() throws Exception {
        BufferedImage img = makeRandomBufferedImage(true);
        int w = img.getWidth();
        int h = img.getHeight();
        int ch = 4;
        ImageLoader loader = new ImageLoader(0, 0, ch);
        int[][] arr = loader.toIntArrayArray(img);
        Assert.assertEquals(h, arr.length);
        Assert.assertEquals(w, arr[0].length);
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                Assert.assertEquals(img.getRGB(j, i), arr[i][j]);
            }
        }
    }

    @Test
    public void testToINDArrayBGR() throws Exception {
        BufferedImage img = makeRandomBufferedImage(false);
        int w = img.getWidth();
        int h = img.getHeight();
        int ch = 3;
        ImageLoader loader = new ImageLoader(0, 0, ch);
        INDArray arr = loader.toINDArrayBGR(img);
        long[] shape = arr.shape();
        Assert.assertEquals(3, shape.length);
        Assert.assertEquals(ch, shape[0]);
        Assert.assertEquals(h, shape[1]);
        Assert.assertEquals(w, shape[2]);
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                int srcColor = img.getRGB(j, i);
                int a = 255 << 24;
                int r = (arr.getInt(2, i, j)) << 16;
                int g = (arr.getInt(1, i, j)) << 8;
                int b = (arr.getInt(0, i, j)) & 255;
                int dstColor = ((a | r) | g) | b;
                Assert.assertEquals(srcColor, dstColor);
            }
        }
    }

    @Test
    public void testScalingIfNeed() throws Exception {
        BufferedImage img1 = makeRandomBufferedImage(true);
        BufferedImage img2 = makeRandomBufferedImage(false);
        int w1 = 60;
        int h1 = 110;
        int ch1 = 6;
        ImageLoader loader1 = new ImageLoader(h1, w1, ch1);
        BufferedImage scaled1 = loader1.scalingIfNeed(img1, true);
        Assert.assertEquals(w1, scaled1.getWidth());
        Assert.assertEquals(h1, scaled1.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_4BYTE_ABGR, scaled1.getType());
        Assert.assertEquals(4, scaled1.getSampleModel().getNumBands());
        BufferedImage scaled2 = loader1.scalingIfNeed(img1, false);
        Assert.assertEquals(w1, scaled2.getWidth());
        Assert.assertEquals(h1, scaled2.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_3BYTE_BGR, scaled2.getType());
        Assert.assertEquals(3, scaled2.getSampleModel().getNumBands());
        BufferedImage scaled3 = loader1.scalingIfNeed(img2, true);
        Assert.assertEquals(w1, scaled3.getWidth());
        Assert.assertEquals(h1, scaled3.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_3BYTE_BGR, scaled3.getType());
        Assert.assertEquals(3, scaled3.getSampleModel().getNumBands());
        BufferedImage scaled4 = loader1.scalingIfNeed(img2, false);
        Assert.assertEquals(w1, scaled4.getWidth());
        Assert.assertEquals(h1, scaled4.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_3BYTE_BGR, scaled4.getType());
        Assert.assertEquals(3, scaled4.getSampleModel().getNumBands());
        int w2 = 70;
        int h2 = 120;
        int ch2 = 6;
        ImageLoader loader2 = new ImageLoader(h2, w2, ch2);
        BufferedImage scaled5 = loader2.scalingIfNeed(img1, true);
        Assert.assertEquals(w2, scaled5.getWidth());
        Assert.assertEquals(h2, scaled5.getHeight(), h2);
        Assert.assertEquals(BufferedImage.TYPE_4BYTE_ABGR, scaled5.getType());
        Assert.assertEquals(4, scaled5.getSampleModel().getNumBands());
        BufferedImage scaled6 = loader2.scalingIfNeed(img1, false);
        Assert.assertEquals(w2, scaled6.getWidth());
        Assert.assertEquals(h2, scaled6.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_3BYTE_BGR, scaled6.getType());
        Assert.assertEquals(3, scaled6.getSampleModel().getNumBands());
    }

    @Test
    public void testScalingIfNeedWhenSuitableSizeButDiffChannel() {
        int width1 = 60;
        int height1 = 110;
        int channel1 = BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage img1 = makeRandomBufferedImage(true, width1, height1);
        ImageLoader loader1 = new ImageLoader(height1, width1, channel1);
        BufferedImage scaled1 = loader1.scalingIfNeed(img1, false);
        Assert.assertEquals(width1, scaled1.getWidth());
        Assert.assertEquals(height1, scaled1.getHeight());
        Assert.assertEquals(channel1, scaled1.getType());
        Assert.assertEquals(1, scaled1.getSampleModel().getNumBands());
        int width2 = 70;
        int height2 = 120;
        int channel2 = BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage img2 = makeRandomBufferedImage(false, width2, height2);
        ImageLoader loader2 = new ImageLoader(height2, width2, channel2);
        BufferedImage scaled2 = loader2.scalingIfNeed(img2, false);
        Assert.assertEquals(width2, scaled2.getWidth());
        Assert.assertEquals(height2, scaled2.getHeight());
        Assert.assertEquals(channel2, scaled2.getType());
        Assert.assertEquals(1, scaled2.getSampleModel().getNumBands());
    }

    @Test
    public void testToBufferedImageRGB() {
        BufferedImage img = makeRandomBufferedImage(false);
        int w = img.getWidth();
        int h = img.getHeight();
        int ch = 3;
        ImageLoader loader = new ImageLoader(0, 0, ch);
        INDArray arr = loader.toINDArrayBGR(img);
        BufferedImage img2 = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        loader.toBufferedImageRGB(arr, img2);
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                int srcColor = img.getRGB(j, i);
                int restoredColor = img2.getRGB(j, i);
                Assert.assertEquals(srcColor, restoredColor);
            }
        }
    }
}

