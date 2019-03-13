/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs.response;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.Assert;
import org.junit.Test;


public class GdalFormatTest {
    GdalCoverageResponseDelegate gdalCovRespDelegate;

    @Test
    public void testCanProduce() {
        Assert.assertTrue(gdalCovRespDelegate.canProduce("GDAL-JPEG2000"));
        Assert.assertTrue(gdalCovRespDelegate.canProduce("GDAL-XYZ"));
        // not among default formats
        Assert.assertFalse(gdalCovRespDelegate.canProduce("GDAL-MrSID"));
    }

    @Test
    public void testContentTypeZip() {
        Assert.assertEquals("application/zip", gdalCovRespDelegate.getMimeType("GDAL-ArcInfoGrid"));
        Assert.assertEquals("zip", gdalCovRespDelegate.getFileExtension("GDAL-ArcInfoGrid"));
    }

    @Test
    public void testContentTypeJP2K() {
        Assert.assertEquals("image/jp2", gdalCovRespDelegate.getMimeType("GDAL-JPEG2000"));
        Assert.assertEquals("jp2", gdalCovRespDelegate.getFileExtension("GDAL-JPEG2000"));
    }

    @Test
    public void testContentTypePDF() {
        Assert.assertEquals("application/pdf", gdalCovRespDelegate.getMimeType("GDAL-PDF"));
        Assert.assertEquals("pdf", gdalCovRespDelegate.getFileExtension("GDAL-PDF"));
    }

    @Test
    public void testContentTypeText() {
        Assert.assertEquals("text/plain", gdalCovRespDelegate.getMimeType("GDAL-XYZ"));
        Assert.assertEquals("txt", gdalCovRespDelegate.getFileExtension("GDAL-XYZ"));
    }

    @Test
    public void testXYZ() throws Exception {
        // prepare input
        File tempFile = prepareInput();
        try {
            GridCoverage2DReader covReader = new GeoTiffReader(tempFile);
            GridCoverage2D cov = covReader.read(null);
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                // write out
                gdalCovRespDelegate.encode(cov, "GDAL-XYZ", null, bos);
                // parse the text output to check it's really XYZ data
                try (InputStream is = new ByteArrayInputStream(bos.toByteArray())) {
                    GdalTestUtil.checkXyzData(is);
                }
            }
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    @Test
    public void testZippedGrid() throws Exception {
        // prepare input
        File tempFile = prepareInput();
        try {
            GridCoverage2DReader covReader = new GeoTiffReader(tempFile);
            GridCoverage2D cov = covReader.read(null);
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                // write out
                gdalCovRespDelegate.encode(cov, "GDAL-ArcInfoGrid", null, bos);
                GdalTestUtil.checkZippedGridData(new ByteArrayInputStream(bos.toByteArray()));
            }
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}

