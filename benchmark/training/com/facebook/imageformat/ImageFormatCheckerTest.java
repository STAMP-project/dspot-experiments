/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imageformat;


import DefaultImageFormats.BMP;
import DefaultImageFormats.GIF;
import DefaultImageFormats.HEIF;
import DefaultImageFormats.JPEG;
import DefaultImageFormats.PNG;
import DefaultImageFormats.WEBP_ANIMATED;
import DefaultImageFormats.WEBP_EXTENDED;
import DefaultImageFormats.WEBP_EXTENDED_WITH_ALPHA;
import DefaultImageFormats.WEBP_LOSSLESS;
import DefaultImageFormats.WEBP_SIMPLE;
import com.facebook.soloader.SoLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link ImageFormatChecker}
 */
@RunWith(RobolectricTestRunner.class)
public class ImageFormatCheckerTest {
    static {
        SoLoader.setInTestMode();
    }

    @Test
    public void testSimpleWebps() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getNames(2, "webps/%d_webp_plain.webp"), WEBP_SIMPLE);
    }

    @Test
    public void testLosslessWebps() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getNames(5, "webps/%d_webp_ll.webp"), WEBP_LOSSLESS);
    }

    @Test
    public void testExtendedWebpsWithAlpha() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getNames(5, "webps/%d_webp_ea.webp"), WEBP_EXTENDED_WITH_ALPHA);
    }

    @Test
    public void testExtendedWebpsWithoutAlpha() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getName("webps/1_webp_e.webp"), WEBP_EXTENDED);
    }

    @Test
    public void testAnimatedWebps() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getName("webps/1_webp_anim.webp"), WEBP_ANIMATED);
    }

    @Test
    public void testJpegs() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getNames(5, "jpegs/%d.jpeg"), JPEG);
    }

    @Test
    public void testPngs() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getNames(5, "pngs/%d.png"), PNG);
    }

    @Test
    public void testGifs() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getNames(5, "gifs/%d.gif"), GIF);
    }

    @Test
    public void testBmps() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getNames(5, "bmps/%d.bmp"), BMP);
    }

    @Test
    public void testHeifs() throws Exception {
        singleImageTypeTest(ImageFormatCheckerTest.getName("heifs/1.heif"), HEIF);
    }
}

