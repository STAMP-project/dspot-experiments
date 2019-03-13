/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import SystemTestData.TASMANIA_BM;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.platform.resource.Files;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geoserver.rest.RestBaseController;
import org.geoserver.rest.util.IOUtils;
import org.geoserver.rest.util.RESTUtils;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.StructuredGridCoverage2DReader;
import org.geotools.data.DataUtilities;
import org.geotools.gce.imagemosaic.ImageMosaicFormat;
import org.geotools.util.URLs;
import org.geotools.util.factory.GeoTools;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverageReader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class CoverageStoreFileUploadTest extends CatalogRESTTestSupport {
    @Test
    public void testWorldImageUploadZipped() throws Exception {
        URL zip = getClass().getResource("test-data/usa.zip");
        byte[] bytes = FileUtils.readFileToByteArray(URLs.urlToFile(zip));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/coveragestores/usa/file.worldimage"), bytes, "application/zip");
        Assert.assertEquals(201, response.getStatus());
        String content = response.getContentAsString();
        Document d = dom(new ByteArrayInputStream(content.getBytes()));
        Assert.assertEquals("coverageStore", d.getDocumentElement().getNodeName());
        CoverageStoreInfo cs = getCatalog().getCoverageStoreByName("sf", "usa");
        Assert.assertNotNull(cs);
        CoverageInfo ci = getCatalog().getCoverageByName("sf", "usa");
        Assert.assertNotNull(ci);
    }

    @Test
    public void testUploadImageMosaic() throws Exception {
        URL zip = MockData.class.getResource("watertemp.zip");
        InputStream is = null;
        byte[] bytes;
        try {
            is = zip.openStream();
            bytes = IOUtils.toByteArray(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/watertemp/file.imagemosaic"), bytes, "application/zip");
        Assert.assertEquals(201, response.getStatus());
        // check the response contents
        String content = response.getContentAsString();
        Document d = dom(new ByteArrayInputStream(content.getBytes()));
        XMLAssert.assertXpathEvaluatesTo("watertemp", "//coverageStore/name", d);
        XMLAssert.assertXpathEvaluatesTo("ImageMosaic", "//coverageStore/type", d);
        // check the coverage is actually there
        CoverageStoreInfo storeInfo = getCatalog().getCoverageStoreByName("watertemp");
        Assert.assertNotNull(storeInfo);
        CoverageInfo ci = getCatalog().getCoverageByName("watertemp");
        Assert.assertNotNull(ci);
        Assert.assertEquals(storeInfo, ci.getStore());
    }

    @Test
    public void testHarvestImageMosaic() throws Exception {
        // Upload of the Mosaic via REST
        URL zip = MockData.class.getResource("watertemp.zip");
        InputStream is = null;
        byte[] bytes;
        try {
            is = zip.openStream();
            bytes = IOUtils.toByteArray(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/watertemp2/file.imagemosaic"), bytes, "application/zip");
        Assert.assertEquals(201, response.getStatus());
        // check the response contents
        String content = response.getContentAsString();
        Document d = dom(new ByteArrayInputStream(content.getBytes()));
        XMLAssert.assertXpathEvaluatesTo("watertemp2", "//coverageStore/name", d);
        XMLAssert.assertXpathEvaluatesTo("ImageMosaic", "//coverageStore/type", d);
        // check the coverage is actually there
        CoverageStoreInfo storeInfo = getCatalog().getCoverageStoreByName("watertemp2");
        Assert.assertNotNull(storeInfo);
        CoverageInfo ci = getCatalog().getCoverageByName("watertemp2");
        Assert.assertNotNull(ci);
        Assert.assertEquals(storeInfo, ci.getStore());
        // Harvesting of the Mosaic
        URL zipHarvest = MockData.class.getResource("harvesting.zip");
        // Extract a Byte array from the zip file
        is = null;
        try {
            is = zipHarvest.openStream();
            bytes = IOUtils.toByteArray(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        // Create the POST request
        MockHttpServletRequest request = createRequest(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/watertemp2/file.imagemosaic"));
        request.setMethod("POST");
        request.setContentType("application/zip");
        request.setContent(bytes);
        request.addHeader("Content-type", "application/zip");
        // Get The response
        dispatch(request);
        // Get the Mosaic Reader
        GridCoverageReader reader = storeInfo.getGridCoverageReader(null, GeoTools.getDefaultHints());
        // Test if all the TIME DOMAINS are present
        String[] metadataNames = reader.getMetadataNames();
        Assert.assertNotNull(metadataNames);
        Assert.assertEquals("true", reader.getMetadataValue("HAS_TIME_DOMAIN"));
        Assert.assertEquals("2008-10-31T00:00:00.000Z,2008-11-01T00:00:00.000Z,2008-11-02T00:00:00.000Z", reader.getMetadataValue(metadataNames[0]));
    }

    @Test
    public void testHarvestNotAllowedOnSimpleCoverageStore() throws Exception {
        // add bluemarble
        getTestData().addDefaultRasterLayer(TASMANIA_BM, getCatalog());
        // Harvesting of the Mosaic
        URL zipHarvest = MockData.class.getResource("harvesting.zip");
        // Extract a Byte array from the zip file
        InputStream is = null;
        byte[] bytes;
        try {
            is = zipHarvest.openStream();
            bytes = IOUtils.toByteArray(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        // Create the POST request
        MockHttpServletRequest request = createRequest(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble"));
        request.setMethod("POST");
        request.setContentType("application/zip");
        request.setContent(bytes);
        request.addHeader("Content-type", "application/zip");
        // Get The response
        MockHttpServletResponse response = dispatch(request);
        // not allowed
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testHarvestImageMosaicWithDirectory() throws Exception {
        // Upload of the Mosaic via REST
        URL zip = MockData.class.getResource("watertemp.zip");
        InputStream is = null;
        byte[] bytes;
        try {
            is = zip.openStream();
            bytes = IOUtils.toByteArray(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/watertemp3/file.imagemosaic"), bytes, "application/zip");
        Assert.assertEquals(201, response.getStatus());
        // check the response contents
        String content = response.getContentAsString();
        Document d = dom(new ByteArrayInputStream(content.getBytes()));
        XMLAssert.assertXpathEvaluatesTo("watertemp3", "//coverageStore/name", d);
        XMLAssert.assertXpathEvaluatesTo("ImageMosaic", "//coverageStore/type", d);
        // check the coverage is actually there
        CoverageStoreInfo storeInfo = getCatalog().getCoverageStoreByName("watertemp3");
        Assert.assertNotNull(storeInfo);
        CoverageInfo ci = getCatalog().getCoverageByName("watertemp3");
        Assert.assertNotNull(ci);
        Assert.assertEquals(storeInfo, ci.getStore());
        // Harvesting of the Mosaic
        URL zipHarvest = MockData.class.getResource("harvesting.zip");
        Resource newZip = Files.asResource(new File("./target/harvesting2.zip"));
        // Copy the content of the first zip to the second
        IOUtils.copyStream(zipHarvest.openStream(), newZip.out(), true, true);
        Resource outputDirectory = Files.asResource(new File("./target/harvesting"));
        RESTUtils.unzipFile(newZip, outputDirectory);
        // Create the POST request
        MockHttpServletRequest request = createRequest(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/watertemp3/external.imagemosaic"));
        request.setMethod("POST");
        request.setContentType("text/plain");
        request.setContent(("file:///" + (outputDirectory.dir().getAbsolutePath())).getBytes("UTF-8"));
        request.addHeader("Content-type", "text/plain");
        // Get The response
        dispatch(request);
        // Get the Mosaic Reader
        GridCoverageReader reader = storeInfo.getGridCoverageReader(null, GeoTools.getDefaultHints());
        // Test if all the TIME DOMAINS are present
        String[] metadataNames = reader.getMetadataNames();
        Assert.assertNotNull(metadataNames);
        Assert.assertEquals("true", reader.getMetadataValue("HAS_TIME_DOMAIN"));
        Assert.assertEquals("2008-10-31T00:00:00.000Z,2008-11-01T00:00:00.000Z,2008-11-02T00:00:00.000Z", reader.getMetadataValue(metadataNames[0]));
        // Removal of the temporary directory
        outputDirectory.delete();
    }

    @Test
    public void testHarvestExternalImageMosaic() throws Exception {
        // Check if an already existing directory called "mosaic" is present
        URL resource = getClass().getResource("test-data/mosaic");
        if (resource != null) {
            File oldDir = URLs.urlToFile(resource);
            if (oldDir.exists()) {
                FileUtils.deleteDirectory(oldDir);
            }
        }
        // reading of the mosaic directory
        Resource mosaic = readMosaic();
        // Creation of the builder for building a new CoverageStore
        CatalogBuilder builder = new CatalogBuilder(getCatalog());
        // Definition of the workspace associated to the coverage
        WorkspaceInfo ws = getCatalog().getWorkspaceByName("gs");
        // Creation of a CoverageStore
        CoverageStoreInfo store = builder.buildCoverageStore("watertemp4");
        store.setURL(URLs.fileToUrl(Resources.find(mosaic)).toExternalForm());
        store.setWorkspace(ws);
        ImageMosaicFormat imageMosaicFormat = new ImageMosaicFormat();
        store.setType(imageMosaicFormat.getName());
        // Addition to the catalog
        getCatalog().add(store);
        builder.setStore(store);
        // Input reader used for reading the mosaic folder
        GridCoverage2DReader reader = null;
        // Reader used for checking if the mosaic has been configured correctly
        StructuredGridCoverage2DReader reader2 = null;
        try {
            // Selection of the reader to use for the mosaic
            reader = imageMosaicFormat.getReader(DataUtilities.fileToURL(Resources.find(mosaic)));
            // configure the coverage
            configureCoverageInfo(builder, store, reader);
            // check the coverage is actually there
            CoverageStoreInfo storeInfo = getCatalog().getCoverageStoreByName("watertemp4");
            Assert.assertNotNull(storeInfo);
            CoverageInfo ci = getCatalog().getCoverageByName("mosaic");
            Assert.assertNotNull(ci);
            Assert.assertEquals(storeInfo, ci.getStore());
            // Harvesting of the Mosaic
            URL zipHarvest = MockData.class.getResource("harvesting.zip");
            // Extract a Byte array from the zip file
            InputStream is = null;
            byte[] bytes;
            try {
                is = zipHarvest.openStream();
                bytes = IOUtils.toByteArray(is);
            } finally {
                IOUtils.closeQuietly(is);
            }
            // Create the POST request
            MockHttpServletRequest request = createRequest(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/watertemp4/file.imagemosaic"));
            request.setMethod("POST");
            request.setContentType("application/zip");
            request.setContent(bytes);
            request.addHeader("Content-type", "application/zip");
            // Get The response
            MockHttpServletResponse response = dispatch(request);
            // Get the Mosaic Reader
            reader2 = ((StructuredGridCoverage2DReader) (storeInfo.getGridCoverageReader(null, GeoTools.getDefaultHints())));
            // Test if all the TIME DOMAINS are present
            String[] metadataNames = reader2.getMetadataNames();
            Assert.assertNotNull(metadataNames);
            Assert.assertEquals("true", reader2.getMetadataValue("HAS_TIME_DOMAIN"));
            Assert.assertEquals("2008-10-31T00:00:00.000Z,2008-11-01T00:00:00.000Z,2008-11-02T00:00:00.000Z", reader2.getMetadataValue(metadataNames[0]));
            // Removal of all the data associated to the mosaic
            reader2.delete(true);
        } finally {
            // Reader disposal
            if (reader != null) {
                try {
                    reader.dispose();
                } catch (Throwable t) {
                    // Does nothing
                }
            }
            if (reader2 != null) {
                try {
                    reader2.dispose();
                } catch (Throwable t) {
                    // Does nothing
                }
            }
        }
    }

    @Test
    public void testReHarvestSingleTiff() throws Exception {
        // Check if an already existing directory called "mosaic" is present
        URL resource = getClass().getResource("test-data/mosaic");
        if (resource != null) {
            File oldDir = URLs.urlToFile(resource);
            if (oldDir.exists()) {
                FileUtils.deleteDirectory(oldDir);
            }
        }
        // reading of the mosaic directory
        Resource mosaic = readMosaic();
        // Creation of the builder for building a new CoverageStore
        CatalogBuilder builder = new CatalogBuilder(getCatalog());
        // Definition of the workspace associated to the coverage
        WorkspaceInfo ws = getCatalog().getWorkspaceByName("gs");
        // Creation of a CoverageStore
        CoverageStoreInfo store = builder.buildCoverageStore("watertemp5");
        store.setURL(URLs.fileToUrl(Resources.find(mosaic)).toExternalForm());
        store.setWorkspace(ws);
        ImageMosaicFormat imageMosaicFormat = new ImageMosaicFormat();
        store.setType(imageMosaicFormat.getName());
        // Addition to the catalog
        getCatalog().add(store);
        builder.setStore(store);
        // Input reader used for reading the mosaic folder
        GridCoverage2DReader reader = null;
        // Reader used for checking if the mosaic has been configured correctly
        StructuredGridCoverage2DReader reader2 = null;
        try {
            // Selection of the reader to use for the mosaic
            reader = imageMosaicFormat.getReader(DataUtilities.fileToURL(Resources.find(mosaic)));
            // configure the coverage
            configureCoverageInfo(builder, store, reader);
            // check the coverage is actually there
            CoverageStoreInfo storeInfo = getCatalog().getCoverageStoreByName("watertemp5");
            Assert.assertNotNull(storeInfo);
            CoverageInfo ci = getCatalog().getCoverageByName("mosaic");
            Assert.assertNotNull(ci);
            Assert.assertEquals(storeInfo, ci.getStore());
            // Harvesting of the Mosaic
            URL zipHarvest = MockData.class.getResource("harvesting.zip");
            // Extract the first file as payload (the tiff)
            byte[] bytes = null;
            try (ZipInputStream zis = new ZipInputStream(zipHarvest.openStream())) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if ("NCOM_wattemp_000_20081102T0000000_12.tiff".equals(entry.getName())) {
                        bytes = IOUtils.toByteArray(zis);
                    }
                } 
                if (bytes == null) {
                    Assert.fail("Could not find the expected zip entry NCOM_wattemp_000_20081102T0000000_12.tiff");
                }
            }
            reader2 = uploadGeotiffAndCheck(storeInfo, bytes, "NCOM_wattemp_000_20081102T0000000_12.tiff");
            // now re-upload, used to blow up
            reader2 = uploadGeotiffAndCheck(storeInfo, bytes, "NCOM_wattemp_000_20081102T0000000_12.tiff");
            // Removal of all the data associated to the mosaic
            reader2.delete(true);
        } finally {
            // Reader disposal
            if (reader != null) {
                try {
                    reader.dispose();
                } catch (Throwable t) {
                    // Does nothing
                }
            }
            if (reader2 != null) {
                try {
                    reader2.dispose();
                } catch (Throwable t) {
                    // Does nothing
                }
            }
        }
    }
}

