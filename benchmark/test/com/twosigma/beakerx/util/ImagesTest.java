/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.util;


import java.awt.image.BufferedImage;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Images}.
 */
public class ImagesTest {
    @Test
    public void testRoundTrip() throws IOException {
        byte[] happyFace = new byte[]{ -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 7, 0, 0, 0, 7, 8, 2, 0, 0, 0, 75, 48, -64, -124, 0, 0, 0, 61, 73, 68, 65, 84, 120, -38, 99, 96, 96, 96, 80, 81, 81, -15, -12, -12, -52, -51, -51, -99, 52, 105, -46, -74, 109, -37, 110, -33, -66, -51, 0, 17, 101, -104, -80, 19, 40, 10, 36, -119, 16, -59, 98, 66, 44, 51, 3, 92, 20, -56, -58, 43, 10, 52, 1, -56, -127, 32, -120, 9, 0, 70, 42, 56, 112, -125, 100, 72, 122, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };
        BufferedImage bi = Images.decode(happyFace);
        Assert.assertEquals(7, bi.getWidth());
        Assert.assertEquals(7, bi.getHeight());
        Assert.assertEquals(-16740167, bi.getRGB(2, 1));
        Assert.assertEquals(-10681600, bi.getRGB(5, 5));
        byte[] reencoded = Images.encode(bi);
        Assert.assertArrayEquals(happyFace, reencoded);
    }
}

