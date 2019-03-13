/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.svg;


import java.io.ByteArrayInputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the SvgSupport class
 */
public class SvgSupportTest {
    public static final String svgImage = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + ((("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.0\"\n" + "\twidth=\"38\" height=\"32\"  viewBox=\"0 0 39.875 33.6667\">\n") + "<path style=\"stroke: none; fill: #323296;\" d=\"M 10,0 L 30.5,0 39.875,17.5 30.5,33.6667 10,33.6667 L 0,17.5") + "L 10,0 z\"/>\n</svg>");

    @Test
    public void testIsSvgEnabled() throws Exception {
        Assert.assertTrue(SvgSupport.isSvgEnabled());
    }

    @Test
    public void testLoadSvgImage() throws Exception {
        SvgImage image = SvgSupport.loadSvgImage(new ByteArrayInputStream(SvgSupportTest.svgImage.getBytes()));
        Assert.assertNotNull(image);
    }

    @Test
    public void testToPngName() throws Exception {
        Assert.assertTrue(SvgSupport.isPngName("my_file.png"));
        Assert.assertTrue(SvgSupport.isPngName("my_file.PNG"));
        Assert.assertTrue(SvgSupport.isPngName(".png"));
        Assert.assertFalse(SvgSupport.isPngName("png"));
        Assert.assertFalse(SvgSupport.isPngName("myFile.svg"));
        Assert.assertEquals("myFile.png", SvgSupport.toPngName("myFile.svg"));
    }

    @Test
    public void testToSvgName() throws Exception {
        Assert.assertTrue(SvgSupport.isSvgName("my_file.svg"));
        Assert.assertTrue(SvgSupport.isSvgName("my_file.SVG"));
        Assert.assertTrue(SvgSupport.isSvgName(".svg"));
        Assert.assertFalse(SvgSupport.isSvgName("svg"));
        Assert.assertFalse(SvgSupport.isSvgName("myFile.png"));
        Assert.assertEquals("myFile.svg", SvgSupport.toSvgName("myFile.png"));
    }
}

