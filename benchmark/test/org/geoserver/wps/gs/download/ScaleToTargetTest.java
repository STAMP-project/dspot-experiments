/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs.download;


import Interpolation.INTERP_BILINEAR;
import java.io.File;
import javax.media.jai.Interpolation;
import org.geoserver.wcs.CoverageCleanerCallback;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.Envelope;


public class ScaleToTargetTest {
    private static final String TEST_COVERAGE = "/org/geoserver/data/test/tazbm.tiff";

    private static final double DELTA = 1.0E-6;

    private static final double[] EXP_NATIVE_RES = new double[]{ 0.0041666667, 0.0041666667 };

    private static final ReferencedEnvelope ROI = new ReferencedEnvelope(146.5, 148, (-43.5), (-43), DefaultGeographicCRS.WGS84);

    private File inputTempFile = null;

    @Test
    public void testAdjustSize() throws Exception {
        GeoTiffReader reader = null;
        try {
            reader = new GeoTiffReader(inputTempFile);
            Assert.assertNotNull(reader);
            Envelope fullSizeEnvelope = reader.getOriginalEnvelope();
            ScaleToTarget scalingFullSize = new ScaleToTarget(reader, fullSizeEnvelope);
            scalingFullSize.setTargetSize(160, null);
            Integer[] targetSize = scalingFullSize.getTargetSize();
            Assert.assertEquals(160, targetSize[0].intValue());
            Assert.assertEquals(160, targetSize[1].intValue());
            scalingFullSize.setTargetSize(null, 200);
            targetSize = scalingFullSize.getTargetSize();
            Assert.assertEquals(200, targetSize[0].intValue());
            Assert.assertEquals(200, targetSize[1].intValue());
            // test with ROI with a different aspect ratio
            ScaleToTarget scalingRoi = new ScaleToTarget(reader, ScaleToTargetTest.ROI);
            scalingRoi.setTargetSize(150, null);
            targetSize = scalingRoi.getTargetSize();
            Assert.assertEquals(150, targetSize[0].intValue());
            Assert.assertEquals(50, targetSize[1].intValue());
            scalingRoi.setTargetSize(null, 100);
            targetSize = scalingRoi.getTargetSize();
            Assert.assertEquals(300, targetSize[0].intValue());
            Assert.assertEquals(100, targetSize[1].intValue());
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
    }

    @Test
    public void testNoScalingJustInterpolation() throws Exception {
        GeoTiffReader reader = null;
        GridCoverage2D gc = null;
        try {
            reader = new GeoTiffReader(inputTempFile);
            Assert.assertNotNull(reader);
            Envelope fullSizeEnvelope = reader.getOriginalEnvelope();
            ScaleToTarget noScaling = new ScaleToTarget(reader, fullSizeEnvelope);
            // I deliberately omit setting the target size: only interpolation will be performed
            // set interpolation method to something other than NEAREST
            noScaling.setInterpolation(Interpolation.getInstance(INTERP_BILINEAR));
            gc = noScaling.scale(reader.read(null));
            Assert.assertNotNull(gc);
            // TODO: this only proves the code ran without throwing exceptions: how do I actually
            // test that the interpolation was done?
        } finally {
            if (reader != null) {
                reader.dispose();
            }
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
        }
    }

    @Test
    public void testFullSizeScale2X() throws Exception {
        final int targetSizeX = 720;
        final int targetSizeY = 720;
        final double[] expectedRequestedResolution = new double[]{ (ScaleToTargetTest.EXP_NATIVE_RES[0]) / 2, (ScaleToTargetTest.EXP_NATIVE_RES[1]) / 2 };
        final double[] expectedReadResolution = ScaleToTargetTest.EXP_NATIVE_RES;
        final int[] expectedGridSize = new int[]{ 360, 360 };// full size image

        testFullSize(targetSizeX, targetSizeY, expectedRequestedResolution, expectedReadResolution, expectedGridSize);
    }

    @Test
    public void testFullSizeTargetSizeMatchesOverview() throws Exception {
        final int targetSizeX = 90;
        final int targetSizeY = 90;
        final double[] expectedRequestedResolution = new double[]{ 0.0166666667, 0.0166666667 };
        final double[] expectedReadResolution = expectedRequestedResolution;
        final int[] expectedGridSize = new int[]{ targetSizeX, targetSizeY };// matches 90x90 overview

        testFullSize(targetSizeX, targetSizeY, expectedRequestedResolution, expectedReadResolution, expectedGridSize);
    }

    @Test
    public void testFullSizeTargetSizeDoesNotMatchOverview() throws Exception {
        final int targetSizeX = 110;
        final int targetSizeY = 110;
        final double[] expectedRequestedResolution = new double[]{ 0.0136363636, 0.0136363636 };
        final double[] expectedReadResolution = new double[]{ 0.0166666667, 0.0166666667 };
        final int[] expectedGridSize = new int[]{ 90, 90 };// closest overview: 90x90

        testFullSize(targetSizeX, targetSizeY, expectedRequestedResolution, expectedReadResolution, expectedGridSize);
    }

    @Test
    public void testROITargetSizeMatchesOverview() throws Exception {
        final int targetSizeX = 180;// matches 180x180 overview

        final int targetSizeY = 60;
        final double[] expectedRequestedResolution = new double[]{ 0.0083333333, 0.0083333333 };
        final double[] expectedReadResolution = expectedRequestedResolution;
        final int[] expectedGridSize = new int[]{ 180, 60 };// matches 180x180 overview

        testROI(targetSizeX, targetSizeY, expectedRequestedResolution, expectedReadResolution, expectedGridSize);
    }

    @Test
    public void testROITargetSizeDoesNotMatchOverview() throws Exception {
        final int targetSizeX = 150;// closest overview is 180x180

        final int targetSizeY = 50;
        final double[] expectedRequestedResolution = new double[]{ 0.01, 0.01 };
        final double[] expectedReadResolution = new double[]{ 0.0083333333, 0.0083333333 };
        final int[] expectedGridSize = new int[]{ 180, 60 };// targetSize * requestedRes / readRes

        testROI(targetSizeX, targetSizeY, expectedRequestedResolution, expectedReadResolution, expectedGridSize);
    }
}

