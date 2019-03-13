/**
 * Copyright (C) 2018 Ryszard Wi?niewski <brut.alll@gmail.com>
 *  Copyright (C) 2018 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.androlib.decode;


import brut.androlib.BaseTest;
import brut.androlib.res.decoder.Res9patchStreamDecoder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;


public class MissingDiv9PatchTest extends BaseTest {
    @Test
    public void assertMissingDivAdded() throws Exception {
        InputStream inputStream = getFileInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Res9patchStreamDecoder decoder = new Res9patchStreamDecoder();
        decoder.decode(inputStream, outputStream);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
        int height = (image.getHeight()) - 1;
        // First and last pixel will be invisible, so lets check the first column and ensure its all black
        for (int y = 1; y < height; y++) {
            Assert.assertEquals(("y coordinate failed at: " + y), MissingDiv9PatchTest.NP_COLOR, image.getRGB(0, y));
        }
    }

    private static final int NP_COLOR = -16777216;
}

