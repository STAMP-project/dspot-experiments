/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ppio;


import CoveragePPIO.QUALITY_KEY;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.geoserver.wps.ppio.CoveragePPIO.JPEGPPIO;
import org.geoserver.wps.ppio.CoveragePPIO.PNGPPIO;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing CoveragePPIOs is able to encode PNG and JPEG formats.
 */
public class CoveragePPIOTest {
    File geotiff = new File("./target/testInput.tiff");

    File targetPng = new File("./target/output.png");

    File targetJpeg = new File("./target/output.jpeg");

    GeoTiffReader reader;

    GridCoverage2D coverage;

    @Test
    public void testPNGEncode() throws Exception {
        GridCoverage2D coverage = getCoverage();
        PNGPPIO ppio = new PNGPPIO();
        testIsFormat(coverage, ppio, targetPng, "PNG");
    }

    @Test
    public void testJPEGEncode() throws Exception {
        GridCoverage2D coverage = getCoverage();
        JPEGPPIO ppio = new JPEGPPIO();
        testIsFormat(coverage, ppio, targetJpeg, "JPEG");
    }

    @Test
    public void testEncodeQuality() throws Exception {
        GridCoverage2D coverage = getCoverage();
        JPEGPPIO ppio = new JPEGPPIO();
        Map<String, Object> encodingParams = new HashMap<String, Object>();
        File highQualityFile = new File("./target/outputHiQ.jpg");
        encodingParams.put(QUALITY_KEY, "0.99");
        try (FileOutputStream fos = new FileOutputStream(highQualityFile)) {
            ppio.encode(coverage, encodingParams, fos);
        }
        final long highQualityFileSize = highQualityFile.length();
        File lowQualityFile = new File("./target/outputLoQ.jpg");
        encodingParams.put(QUALITY_KEY, "0.01");
        try (FileOutputStream fos = new FileOutputStream(lowQualityFile)) {
            ppio.encode(coverage, encodingParams, fos);
        }
        final long lowQualityFileSize = lowQualityFile.length();
        Assert.assertTrue((highQualityFileSize > lowQualityFileSize));
    }
}

