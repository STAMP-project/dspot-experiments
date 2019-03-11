/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.transform;


import ImportTask.State.COMPLETE;
import ImportTask.State.READY;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.io.File;
import javax.media.jai.ImageLayout;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.importer.ImportContext;
import org.geoserver.importer.ImportTask;
import org.geoserver.importer.ImporterTestSupport;
import org.geoserver.importer.SpatialFile;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class GdalTransformTest extends ImporterTestSupport {
    @Test
    public void testGdalTranslateTrasform() throws Exception {
        Assume.assumeTrue(GdalTranslateTransform.isAvailable());
        File dir = unpack("geotiff/EmissiveCampania.tif.bz2");
        File tif = new File(dir, "EmissiveCampania.tif");
        ImportContext context = importer.createContext(new SpatialFile(tif));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        // setup the transformation
        GdalTranslateTransform gtx = buildGdalTranslate();
        task.getTransform().add(gtx);
        Assert.assertEquals("EmissiveCampania", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("EmissiveCampania"));
        Assert.assertEquals(COMPLETE, task.getState());
        runChecks("EmissiveCampania");
        // check we did the gdal_transform on the file
        GeoTiffReader reader = null;
        try {
            reader = new GeoTiffReader(tif);
            ImageLayout layout = reader.getImageLayout();
            ColorModel cm = layout.getColorModel(null);
            Assert.assertEquals(3, cm.getNumComponents());
            SampleModel sm = layout.getSampleModel(null);
            Assert.assertEquals(3, sm.getNumBands());
            Assert.assertEquals(DataBuffer.TYPE_BYTE, sm.getDataType());
            Assert.assertEquals(0, reader.getNumOverviews());
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
    }

    @Test
    public void testGdalAddo() throws Exception {
        Assume.assumeTrue(GdalAddoTransform.isAvailable());
        File dir = unpack("geotiff/EmissiveCampania.tif.bz2");
        File tif = new File(dir, "EmissiveCampania.tif");
        ImportContext context = importer.createContext(new SpatialFile(tif));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        // setup the transformation
        GdalAddoTransform gad = buildGdalAddo();
        task.getTransform().add(gad);
        Assert.assertEquals("EmissiveCampania", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("EmissiveCampania"));
        Assert.assertEquals(COMPLETE, task.getState());
        runChecks("EmissiveCampania");
        // check we did the gdaladdo on the file
        GeoTiffReader reader = null;
        try {
            reader = new GeoTiffReader(tif);
            ImageLayout layout = reader.getImageLayout();
            ColorModel cm = layout.getColorModel(null);
            Assert.assertEquals(16, cm.getNumComponents());
            SampleModel sm = layout.getSampleModel(null);
            Assert.assertEquals(16, sm.getNumBands());
            Assert.assertEquals(DataBuffer.TYPE_USHORT, sm.getDataType());
            Assert.assertEquals(3, reader.getNumOverviews());
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
    }

    @Test
    public void testTranslateAddo() throws Exception {
        Assume.assumeTrue(GdalTranslateTransform.isAvailable());
        Assume.assumeTrue(GdalAddoTransform.isAvailable());
        File dir = unpack("geotiff/EmissiveCampania.tif.bz2");
        File tif = new File(dir, "EmissiveCampania.tif");
        ImportContext context = importer.createContext(new SpatialFile(tif));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        GdalTranslateTransform gtx = buildGdalTranslate();
        task.getTransform().add(gtx);
        GdalAddoTransform gad = buildGdalAddo();
        task.getTransform().add(gad);
        Assert.assertEquals("EmissiveCampania", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("EmissiveCampania"));
        Assert.assertEquals(COMPLETE, task.getState());
        runChecks("EmissiveCampania");
        // check we did the gdal_transform and gdaladdo on the file
        GeoTiffReader reader = null;
        try {
            reader = new GeoTiffReader(tif);
            ImageLayout layout = reader.getImageLayout();
            ColorModel cm = layout.getColorModel(null);
            Assert.assertEquals(3, cm.getNumComponents());
            SampleModel sm = layout.getSampleModel(null);
            Assert.assertEquals(3, sm.getNumBands());
            Assert.assertEquals(DataBuffer.TYPE_BYTE, sm.getDataType());
            Assert.assertEquals(3, reader.getNumOverviews());
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
    }

    @Test
    public void testWarpFromGroundControlPoint() throws Exception {
        Assume.assumeTrue(GdalWarpTransform.isAvailable());
        File dir = unpack("geotiff/box_gcp_fixed.tif.bz2");
        File tif = new File(dir, "box_gcp_fixed.tif");
        ImportContext context = importer.createContext(new SpatialFile(tif));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        // setup the transformation
        GdalWarpTransform warp = buildGdalWarp();
        task.getTransform().add(warp);
        Assert.assertEquals("box_gcp_fixed", task.getLayer().getResource().getName());
        CoverageStoreInfo store = ((CoverageStoreInfo) (task.getStore()));
        Assert.assertEquals("GeoTIFF", store.getFormat().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("box_gcp_fixed"));
        Assert.assertEquals(COMPLETE, task.getState());
        runChecks("box_gcp_fixed");
        // check we did the gdaladdo on the file
        GeoTiffReader reader = null;
        try {
            reader = new GeoTiffReader(tif);
            ImageLayout layout = reader.getImageLayout();
            ColorModel cm = layout.getColorModel(null);
            Assert.assertEquals(3, cm.getNumComponents());
            SampleModel sm = layout.getSampleModel(null);
            Assert.assertEquals(1, sm.getNumBands());
            Assert.assertEquals(DataBuffer.TYPE_BYTE, sm.getDataType());
            Assert.assertEquals(0, reader.getNumOverviews());
            Assert.assertEquals(Integer.valueOf(4326), CRS.lookupEpsgCode(reader.getCoordinateReferenceSystem(), false));
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
    }
}

