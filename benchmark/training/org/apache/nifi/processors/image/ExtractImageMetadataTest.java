/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.image;


import ExtractImageMetadata.FAILURE;
import ExtractImageMetadata.SUCCESS;
import java.io.IOException;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class ExtractImageMetadataTest {
    private static final String BMP_HEADER = "BMP Header.";

    private static final String JPEG_HEADER = "JPEG.";

    private static final String GIF_HEADER = "GIF Header.";

    private static final String GIF_CONTROL = "GIF Control.";

    private static final String PNG_HEADER = "PNG-";

    private TestRunner testRunner;

    @Test
    public void testFailedExtraction() throws IOException {
        MockFlowFile flowFile = verifyTestRunnerFlow("src/test/resources/notImage.txt", FAILURE, null);
    }

    @Test
    public void testExtractJPG() throws IOException {
        MockFlowFile flowFile = verifyTestRunnerFlow("src/test/resources/simple.jpg", SUCCESS, null);
        Map<String, String> attributes = flowFile.getAttributes();
        Assert.assertEquals("800 pixels", attributes.get(((ExtractImageMetadataTest.JPEG_HEADER) + "Image Width")));
        Assert.assertEquals("600 pixels", attributes.get(((ExtractImageMetadataTest.JPEG_HEADER) + "Image Height")));
        Assert.assertEquals("8 bits", attributes.get(((ExtractImageMetadataTest.JPEG_HEADER) + "Data Precision")));
        Assert.assertEquals("Baseline", attributes.get(((ExtractImageMetadataTest.JPEG_HEADER) + "Compression Type")));
        Assert.assertEquals("3", attributes.get(((ExtractImageMetadataTest.JPEG_HEADER) + "Number of Components")));
        Assert.assertEquals("Y component: Quantization table 0, Sampling factors 2 horiz/2 vert", attributes.get(((ExtractImageMetadataTest.JPEG_HEADER) + "Component 1")));
        Assert.assertEquals("Cb component: Quantization table 1, Sampling factors 1 horiz/1 vert", attributes.get(((ExtractImageMetadataTest.JPEG_HEADER) + "Component 2")));
        Assert.assertEquals("Cr component: Quantization table 1, Sampling factors 1 horiz/1 vert", attributes.get(((ExtractImageMetadataTest.JPEG_HEADER) + "Component 3")));
    }

    @Test
    public void testExtractGIF() throws IOException {
        MockFlowFile flowFile = verifyTestRunnerFlow("src/test/resources/photoshop-8x12-32colors-alpha.gif", SUCCESS, null);
        Map<String, String> attributes = flowFile.getAttributes();
        Assert.assertEquals("8", attributes.get(((ExtractImageMetadataTest.GIF_HEADER) + "Image Width")));
        Assert.assertEquals("12", attributes.get(((ExtractImageMetadataTest.GIF_HEADER) + "Image Height")));
        Assert.assertEquals("true", attributes.get(((ExtractImageMetadataTest.GIF_HEADER) + "Has Global Color Table")));
        Assert.assertEquals("32", attributes.get(((ExtractImageMetadataTest.GIF_HEADER) + "Color Table Size")));
        Assert.assertEquals("8", attributes.get(((ExtractImageMetadataTest.GIF_CONTROL) + "Transparent Color Index")));
        Assert.assertEquals("89a", attributes.get(((ExtractImageMetadataTest.GIF_HEADER) + "GIF Format Version")));
        Assert.assertEquals("5", attributes.get(((ExtractImageMetadataTest.GIF_HEADER) + "Bits per Pixel")));
        Assert.assertEquals("false", attributes.get(((ExtractImageMetadataTest.GIF_HEADER) + "Is Color Table Sorted")));
    }

    @Test
    public void testExtractPNG() throws IOException {
        MockFlowFile flowFile = verifyTestRunnerFlow("src/test/resources/mspaint-8x10.png", SUCCESS, null);
        Map<String, String> attributes = flowFile.getAttributes();
        Assert.assertEquals("8", attributes.get(((ExtractImageMetadataTest.PNG_HEADER) + "IHDR.Image Width")));
        Assert.assertEquals("12", attributes.get(((ExtractImageMetadataTest.PNG_HEADER) + "IHDR.Image Height")));
        Assert.assertEquals("0.455", attributes.get(((ExtractImageMetadataTest.PNG_HEADER) + "gAMA.Image Gamma")));
        Assert.assertEquals("Deflate", attributes.get(((ExtractImageMetadataTest.PNG_HEADER) + "IHDR.Compression Type")));
        Assert.assertEquals("No Interlace", attributes.get(((ExtractImageMetadataTest.PNG_HEADER) + "IHDR.Interlace Method")));
        Assert.assertEquals("Perceptual", attributes.get(((ExtractImageMetadataTest.PNG_HEADER) + "sRGB.sRGB Rendering Intent")));
        Assert.assertEquals("Adaptive", attributes.get(((ExtractImageMetadataTest.PNG_HEADER) + "IHDR.Filter Method")));
        Assert.assertEquals("8", attributes.get(((ExtractImageMetadataTest.PNG_HEADER) + "IHDR.Bits Per Sample")));
        Assert.assertEquals("True Color", attributes.get(((ExtractImageMetadataTest.PNG_HEADER) + "IHDR.Color Type")));
    }

    @Test
    public void testExtractBMP() throws IOException {
        MockFlowFile flowFile = verifyTestRunnerFlow("src/test/resources/16color-10x10.bmp", SUCCESS, null);
        Map<String, String> attributes = flowFile.getAttributes();
        Assert.assertEquals("10", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Image Width")));
        Assert.assertEquals("10", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Image Height")));
        Assert.assertEquals("4", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Bits Per Pixel")));
        Assert.assertEquals("None", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Compression")));
        Assert.assertEquals("0", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "X Pixels per Meter")));
        Assert.assertEquals("0", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Y Pixels per Meter")));
        Assert.assertEquals("0", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Palette Colour Count")));
        Assert.assertEquals("0", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Important Colour Count")));
        Assert.assertEquals("1", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Planes")));
        Assert.assertEquals("40", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Header Size")));
    }

    @Test
    public void testExtractLimitedAttributesBMP() throws IOException {
        MockFlowFile flowFile = verifyTestRunnerFlow("src/test/resources/16color-10x10.bmp", SUCCESS, "5");
        Map<String, String> attributes = flowFile.getAttributes();
        Assert.assertEquals("10", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Image Width")));
        Assert.assertEquals("10", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Image Height")));
        Assert.assertEquals("1", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Planes")));
        Assert.assertEquals("40", attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Header Size")));
        Assert.assertNull(attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Compression")));
        Assert.assertNull(attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "X Pixels per Meter")));
        Assert.assertNull(attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Y Pixels per Meter")));
        Assert.assertNull(attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Palette Colour Count")));
        Assert.assertNull(attributes.get(((ExtractImageMetadataTest.BMP_HEADER) + "Important Colour Count")));
    }
}

