/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.mosaic;


import Filter.INCLUDE;
import ImportContext.State.COMPLETE;
import ImportTask.State.READY;
import Query.ALL;
import TimeMode.AUTO;
import TimeMode.FILENAME;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import javax.xml.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DimensionInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.TestData;
import org.geoserver.data.util.IOUtils;
import org.geoserver.importer.FileData;
import org.geoserver.importer.ImportContext;
import org.geoserver.importer.ImportTask;
import org.geoserver.importer.ImporterTest;
import org.geoserver.importer.ImporterTestSupport;
import org.geoserver.importer.SpatialFile;
import org.geotools.coverage.grid.io.GranuleSource;
import org.geotools.coverage.grid.io.StructuredGridCoverage2DReader;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.URLs;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class ImporterMosaicTest extends ImporterTestSupport {
    protected static QName WATTEMP = new QName(MockData.SF_URI, "watertemp", MockData.SF_PREFIX);

    protected static QName POLYPHEMUS = new QName(MockData.SF_URI, "polyphemus", MockData.SF_PREFIX);

    @Test
    public void testSimpleMosaic() throws Exception {
        File dir = unpack("mosaic/bm.zip");
        ImportContext context = importer.createContext(new Mosaic(dir));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertTrue(((task.getData()) instanceof Mosaic));
        Assert.assertTrue(((task.getData().getFormat()) instanceof MosaicFormat));
        importer.run(context);
        runChecks(dir.getName());
    }

    @Test
    public void testFilenameTimeHandler() throws Exception {
        Mosaic m = new Mosaic(unpack("mosaic/bm_time.zip"));
        m.setTimeMode(FILENAME);
        FilenameTimeHandler th = ((FilenameTimeHandler) (m.getTimeHandler()));
        th.setFilenameRegex("(\\d){6}");
        th.setTimeFormat("yyyyMM");
        m.prepare();
        List<FileData> files = m.getFiles();
        Assert.assertEquals(4, files.size());
        for (int i = 0; i < (files.size()); i++) {
            FileData fd = files.get(i);
            Assert.assertTrue((fd instanceof Granule));
            Granule g = ((Granule) (fd));
            // TODO: comparison fails on build server
            Assert.assertNotNull(g.getTimestamp());
            // assertEquals(date(2004, i), g.getTimestamp());
        }
    }

    @Test
    public void testTimeMosaic() throws Exception {
        Mosaic m = new Mosaic(unpack("mosaic/bm_time.zip"));
        m.setTimeMode(FILENAME);
        FilenameTimeHandler th = ((FilenameTimeHandler) (m.getTimeHandler()));
        th.setFilenameRegex("(\\d){6}");
        th.setTimeFormat("yyyyMM");
        ImportContext context = importer.createContext(m);
        Assert.assertEquals(1, context.getTasks().size());
        importer.run(context);
        LayerInfo l = context.getTasks().get(0).getLayer();
        ResourceInfo r = l.getResource();
        Assert.assertTrue(r.getMetadata().containsKey("time"));
        DimensionInfo d = ((DimensionInfo) (r.getMetadata().get("time")));
        Assert.assertNotNull(d);
        runChecks(l.getName());
        Document dom = getAsDOM(String.format("/%s/%s/wms?request=getcapabilities", r.getStore().getWorkspace().getName(), l.getName()));
        XMLAssert.assertXpathExists((("//wms:Layer[wms:Name = '" + (m.getName())) + "']/wms:Dimension[@name = 'time']"), dom);
    }

    @Test
    public void testTimeMosaicAuto() throws Exception {
        Mosaic m = new Mosaic(unpack("mosaic/bm_time.zip"));
        m.setTimeMode(AUTO);
        ImportContext context = importer.createContext(m);
        Assert.assertEquals(1, context.getTasks().size());
        importer.run(context);
        LayerInfo l = context.getTasks().get(0).getLayer();
        ResourceInfo r = l.getResource();
        Assert.assertTrue(r.getMetadata().containsKey("time"));
        DimensionInfo d = ((DimensionInfo) (r.getMetadata().get("time")));
        Assert.assertNotNull(d);
        runChecks(l.getName());
        Document dom = getAsDOM(String.format("/%s/%s/wms?request=getcapabilities", r.getStore().getWorkspace().getName(), l.getName()));
        XMLAssert.assertXpathExists((("//wms:Layer[wms:Name = '" + (m.getName())) + "']/wms:Dimension[@name = 'time']"), dom);
    }

    @Test
    public void testHarvest() throws Exception {
        Catalog catalog = getCatalog();
        getTestData().addRasterLayer(ImporterMosaicTest.WATTEMP, "watertemp.zip", null, null, TestData.class, catalog);
        // check how many layers we have
        int initialLayerCount = catalog.count(LayerInfo.class, INCLUDE);
        // grab the original count
        CoverageStoreInfo store = catalog.getCoverageStoreByName(ImporterMosaicTest.WATTEMP.getLocalPart());
        StructuredGridCoverage2DReader reader = ((StructuredGridCoverage2DReader) (store.getGridCoverageReader(null, null)));
        GranuleSource gs = reader.getGranules(reader.getGridCoverageNames()[0], true);
        int originalCount = gs.getCount(ALL);
        String mosaicLocation = store.getURL();
        File mosaicFolder = URLs.urlToFile(new URL(mosaicLocation));
        try (InputStream is = MockData.class.getResourceAsStream("harvesting.zip")) {
            IOUtils.decompress(is, mosaicFolder);
        }
        String fileName1 = "NCOM_wattemp_000_20081102T0000000_12.tiff";
        File file1 = new File(mosaicFolder, fileName1);
        Assert.assertTrue(file1.exists());
        ImportContext context = importer.createContext(new SpatialFile(file1), store);
        String fileName2 = "NCOM_wattemp_100_20081102T0000000_12.tiff";
        File file2 = new File(mosaicFolder, fileName2);
        importer.update(context, new SpatialFile(file2));
        Assert.assertEquals(2, context.getTasks().size());
        importer.run(context);
        Assert.assertEquals((originalCount + 2), gs.getCount(ALL));
        Assert.assertEquals(1, gs.getCount(new org.geotools.data.Query(null, ECQL.toFilter((("location = '" + fileName1) + "'")))));
        Assert.assertEquals(1, gs.getCount(new org.geotools.data.Query(null, ECQL.toFilter((("location = '" + fileName2) + "'")))));
        // make sure we did not create a new layer
        int layerCount = catalog.count(LayerInfo.class, INCLUDE);
        Assert.assertEquals(initialLayerCount, layerCount);
    }

    @Test
    public void testPopulateEmptyMosaic() throws Exception {
        Catalog catalog = getCatalog();
        // prepare an empty mosaic
        File root = getTestData().getDataDirectoryRoot();
        String mosaicName = "emptyMosaic";
        File mosaicRoot = new File(root, mosaicName);
        if (mosaicRoot.exists()) {
            FileUtils.deleteDirectory(mosaicRoot);
        }
        mosaicRoot.mkdirs();
        Properties props = new Properties();
        props.put("SPI", "org.geotools.data.h2.H2DataStoreFactory");
        props.put("database", "empty");
        try (FileOutputStream fos = new FileOutputStream(new File(mosaicRoot, "datastore.properties"))) {
            props.store(fos, null);
        }
        CatalogBuilder cb = new CatalogBuilder(catalog);
        cb.setWorkspace(catalog.getDefaultWorkspace());
        CoverageStoreInfo store = cb.buildCoverageStore(mosaicName);
        store.setURL(("./" + mosaicName));
        store.setType("ImageMosaic");
        catalog.save(store);
        // put a granule in the mosaic
        unpack("geotiff/EmissiveCampania.tif.bz2", mosaicRoot);
        File granule = new File(mosaicRoot, "EmissiveCampania.tif");
        store = catalog.getCoverageStoreByName(mosaicName);
        ImportContext context = importer.createContext(new SpatialFile(granule), store);
        Assert.assertEquals(1, context.getTasks().size());
        importer.run(context);
        // check the import produced a granule
        StructuredGridCoverage2DReader reader = ((StructuredGridCoverage2DReader) (store.getGridCoverageReader(null, null)));
        GranuleSource granules = reader.getGranules(mosaicName, true);
        Assert.assertEquals(1, granules.getCount(ALL));
        // check we now also have a layer
        LayerInfo layer = catalog.getLayerByName(mosaicName);
        Assert.assertNotNull(layer);
    }

    @Test
    public void testHarvestNetCDF() throws Exception {
        Catalog catalog = getCatalog();
        getTestData().addRasterLayer(ImporterMosaicTest.POLYPHEMUS, "test-data/mosaic/polyphemus.zip", null, null, ImporterTest.class, catalog);
        // check how many layers we have
        int initialLayerCount = catalog.count(LayerInfo.class, INCLUDE);
        // grab the original count
        CoverageStoreInfo store = catalog.getCoverageStoreByName(ImporterMosaicTest.POLYPHEMUS.getLocalPart());
        StructuredGridCoverage2DReader reader = ((StructuredGridCoverage2DReader) (store.getGridCoverageReader(null, null)));
        GranuleSource gs = reader.getGranules(reader.getGridCoverageNames()[0], true);
        int originalCount = gs.getCount(ALL);
        String mosaicLocation = store.getURL();
        File mosaicFolder = URLs.urlToFile(new URL(mosaicLocation));
        File fileToHarvest = new File(mosaicFolder, "polyphemus_20130302_test.nc");
        try (InputStream is = ImporterTest.class.getResourceAsStream("test-data/mosaic/polyphemus_20130302_test.nc")) {
            FileUtils.copyInputStreamToFile(is, fileToHarvest);
        }
        Assert.assertTrue(fileToHarvest.exists());
        ImportContext context = importer.createContext(new SpatialFile(fileToHarvest), store);
        Assert.assertEquals(1, context.getTasks().size());
        Assert.assertEquals(READY, context.getTasks().get(0).getState());
        importer.run(context);
        // check it succeeded
        Assert.assertEquals(COMPLETE, context.getState());
        // check the import added slices (2 times per file)
        Assert.assertEquals((originalCount + 2), gs.getCount(ALL));
        Assert.assertEquals(2, gs.getCount(new org.geotools.data.Query(null, ECQL.toFilter("location = 'polyphemus_20130301_test.nc'"))));
        Assert.assertEquals(2, gs.getCount(new org.geotools.data.Query(null, ECQL.toFilter("location = 'polyphemus_20130302_test.nc'"))));
        // make sure we did not create a new layer
        int layerCount = catalog.count(LayerInfo.class, INCLUDE);
        Assert.assertEquals(initialLayerCount, layerCount);
    }

    @Test
    public void testHarvestNetCDFWithAuxiliaryNetCDFStore() throws Exception {
        Catalog catalog = getCatalog();
        // same as test above, but using a auxiliary datastore for netcdf internal indexes
        getTestData().addRasterLayer(ImporterMosaicTest.POLYPHEMUS, "test-data/mosaic/polyphemus_aux.zip", null, null, ImporterTest.class, catalog);
        // check how many layers we have
        int initialLayerCount = catalog.count(LayerInfo.class, INCLUDE);
        // grab the original count
        CoverageStoreInfo store = catalog.getCoverageStoreByName(ImporterMosaicTest.POLYPHEMUS.getLocalPart());
        StructuredGridCoverage2DReader reader = ((StructuredGridCoverage2DReader) (store.getGridCoverageReader(null, null)));
        GranuleSource gs = reader.getGranules(reader.getGridCoverageNames()[0], true);
        int originalCount = gs.getCount(ALL);
        String mosaicLocation = store.getURL();
        File mosaicFolder = URLs.urlToFile(new URL(mosaicLocation));
        File fileToHarvest = new File(mosaicFolder, "polyphemus_20130302_test.nc");
        try (InputStream is = ImporterTest.class.getResourceAsStream("test-data/mosaic/polyphemus_20130302_test.nc")) {
            FileUtils.copyInputStreamToFile(is, fileToHarvest);
        }
        Assert.assertTrue(fileToHarvest.exists());
        ImportContext context = importer.createContext(new SpatialFile(fileToHarvest), store);
        Assert.assertEquals(1, context.getTasks().size());
        Assert.assertEquals(READY, context.getTasks().get(0).getState());
        importer.run(context);
        // check it succeeded
        Assert.assertEquals(COMPLETE, context.getState());
        // check the import added slices (2 times per file)
        Assert.assertEquals((originalCount + 2), gs.getCount(ALL));
        Assert.assertEquals(2, gs.getCount(new org.geotools.data.Query(null, ECQL.toFilter("location = 'polyphemus_20130301_test.nc'"))));
        Assert.assertEquals(2, gs.getCount(new org.geotools.data.Query(null, ECQL.toFilter("location = 'polyphemus_20130302_test.nc'"))));
        // make sure we did not create a new layer
        int layerCount = catalog.count(LayerInfo.class, INCLUDE);
        Assert.assertEquals(initialLayerCount, layerCount);
    }
}

