/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ppio;


import java.io.File;
import java.io.FileOutputStream;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.raster.CropCoverage;
import org.junit.Assert;
import org.junit.Test;


public class GeotiffPPIOTest {
    File geotiff = new File("./target/test.tiff");

    File target = new File("./target/target.tiff");

    GeoTiffReader reader;

    GridCoverage2D coverage;

    @Test
    public void testRawCopy() throws Exception {
        GridCoverage2D coverage = getCoverage();
        GeoTiffPPIO ppio = new GeoTiffPPIO();
        try (FileOutputStream fos = new FileOutputStream(target)) {
            ppio.encode(coverage, fos);
        }
        // was a straight copy (a re-encoding would change the size as the input
        // is compressed, the output is not)
        Assert.assertEquals(geotiff.length(), target.length());
    }

    @Test
    public void testCropped() throws Exception {
        GridCoverage2D cov = getCoverage();
        ReferencedEnvelope re = ReferencedEnvelope.reference(coverage.getEnvelope2D());
        re.expandBy((-0.1));
        this.coverage = new CropCoverage().execute(coverage, JTS.toGeometry(re), null);
        GeoTiffPPIO ppio = new GeoTiffPPIO();
        try (FileOutputStream fos = new FileOutputStream(target)) {
            ppio.encode(coverage, fos);
        }
        // not a straight copy, size is different
        Assert.assertNotEquals(geotiff.length(), target.length());
    }
}

