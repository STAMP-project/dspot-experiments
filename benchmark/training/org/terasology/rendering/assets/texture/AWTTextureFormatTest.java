/**
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.assets.texture;


import Texture.FilterMode.LINEAR;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class AWTTextureFormatTest {
    @Test
    public void invalidImageTypeTest() {
        BufferedImage image = createBufferedImage(1, 2, BufferedImage.TYPE_BYTE_GRAY);
        try {
            AWTTextureFormat.convertToTextureData(image, LINEAR);
            Assert.fail("IOException should be thrown");
        } catch (IOException ex) {
            Assert.assertEquals(("Unsupported AWT format: " + (image.getType())), ex.getMessage());
        }
    }

    @Test
    public void successTest() throws IOException {
        BufferedImage image = createBufferedImage(2, 3, BufferedImage.TYPE_3BYTE_BGR);
        TextureData textureData = AWTTextureFormat.convertToTextureData(image, LINEAR);
        Assert.assertNotNull(textureData);
        Assert.assertEquals(2, textureData.getWidth());
        Assert.assertEquals(3, textureData.getHeight());
        Assert.assertEquals(LINEAR, textureData.getFilterMode());
    }
}

