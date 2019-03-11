/**
 * Copyright 2013 MovingBlocks
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


import Color.RED;
import TerasologyConstants.ENGINE_MODULE;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.assets.ResourceUrn;
import org.terasology.naming.Name;
import org.terasology.rendering.nui.Color;


/**
 *
 */
public class TextureUtilTest {
    @Test
    public void testColorTransformedToTextureUri() throws Exception {
        ResourceUrn assetUri = TextureUtil.getTextureUriForColor(RED);
        Assert.assertEquals(ENGINE_MODULE, assetUri.getModuleName());
        Assert.assertEquals(new Name("color"), assetUri.getResourceName());
        Assert.assertEquals(new Name("ff0000ff"), assetUri.getFragmentName());
        int red = 18;
        int green = 3;
        int blue = 196;
        int alpha = 14;
        assetUri = TextureUtil.getTextureUriForColor(new Color(red, green, blue, alpha));
        Assert.assertEquals(ENGINE_MODULE, assetUri.getModuleName());
        Assert.assertEquals(new Name("color"), assetUri.getResourceName());
        Assert.assertEquals(new Name("1203c40e"), assetUri.getFragmentName());
    }

    @Test
    public void testColorNameTransformedToColor() throws Exception {
        Color actualColor = TextureUtil.getColorForColorName("ff0000ff");
        Color expectedColor = Color.RED;
        Assert.assertEquals(expectedColor, actualColor);
        actualColor = TextureUtil.getColorForColorName("1203c40e");
        int red = 18;
        int green = 3;
        int blue = 196;
        int alpha = 14;
        expectedColor = new Color(red, green, blue, alpha);
        Assert.assertEquals(expectedColor, actualColor);
    }

    @Test
    public void testColorTransformedToAssetUriTransformedToColor() throws Exception {
        Color expectedColor = Color.RED;
        ResourceUrn assetUri = TextureUtil.getTextureUriForColor(expectedColor);
        Color actualColor = TextureUtil.getColorForColorName(assetUri.getFragmentName().toLowerCase());
        Assert.assertEquals(expectedColor, actualColor);
        int red = 18;
        int green = 3;
        int blue = 196;
        int alpha = 14;
        expectedColor = new Color(red, green, blue, alpha);
        assetUri = TextureUtil.getTextureUriForColor(expectedColor);
        actualColor = TextureUtil.getColorForColorName(assetUri.getFragmentName().toLowerCase());
        Assert.assertEquals(expectedColor, actualColor);
    }
}

