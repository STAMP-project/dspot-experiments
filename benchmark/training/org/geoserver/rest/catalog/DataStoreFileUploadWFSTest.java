/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.platform.resource.Files;
import org.geoserver.rest.RestBaseController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class DataStoreFileUploadWFSTest extends CatalogRESTTestSupport {
    @Test
    public void testPropertyFileUpload() throws Exception {
        /* Properties p = new Properties();
        p.put( "_", "name:String,pointProperty:Point");
        p.put( "pds.0", "'zero'|POINT(0 0)");
        p.put( "pds.1", "'one'|POINT(1 1)");
         */
        byte[] bytes = propertyFile();
        // p.store( output, null );
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/file.properties"), bytes, "text/plain");
        Document dom = getAsDOM("wfs?request=getfeature&typename=gs:pds");
        assertFeatures(dom);
    }

    @Test
    public void testPropertyFileUploadWithWorkspace() throws Exception {
        byte[] bytes = propertyFile();
        put(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/pds/file.properties"), bytes, "text/plain");
        Document dom = getAsDOM("wfs?request=getfeature&typename=sf:pds");
        assertFeatures(dom, "sf");
    }

    @Test
    public void testPropertyFileUploadZipped() throws Exception {
        byte[] bytes = propertyFile();
        // compress
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(out);
        zout.putNextEntry(new ZipEntry("pds.properties"));
        zout.write(bytes);
        zout.flush();
        zout.close();
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/file.properties"), out.toByteArray(), "application/zip");
        Document dom = getAsDOM("wfs?request=getfeature&typename=gs:pds");
        assertFeatures(dom);
    }

    @Test
    public void testShapeFileUpload() throws Exception {
        byte[] bytes = shpZipAsBytes();
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/file.shp"), bytes, "application/zip");
        Document dom = getAsDOM("wfs?request=getfeature&typename=gs:pds");
        assertFeatures(dom);
    }

    @Test
    public void testShapeFileUploadWithCharset() throws Exception {
        /* Requires that a zipped shapefile (chinese_poly.zip) be in test-data directory */
        byte[] bytes = shpChineseZipAsBytes();
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/chinese_poly/file.shp?charset=UTF-8"), bytes, "application/zip");
        Assert.assertEquals(201, response.getStatus());
        MockHttpServletResponse response2 = getAsServletResponse("wfs?request=getfeature&typename=gs:chinese_poly", "GB18030");
        Assert.assertTrue(response2.getContentAsString().contains("\u951f\u65a4\u62f7"));
    }

    @Test
    public void testShapeFileUploadExternal() throws Exception {
        Document dom = getAsDOM("wfs?request=getfeature&typename=gs:pds");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        File target = new File("target");
        File f = File.createTempFile("rest", "dir", target);
        try {
            f.delete();
            f.mkdir();
            File zip = new File(f, "pds.zip");
            IOUtils.copy(getClass().getResourceAsStream("test-data/pds.zip"), new FileOutputStream(zip));
            org.geoserver.rest.util.IOUtils.inflate(new ZipFile(zip), Files.asResource(f), null);
            MockHttpServletResponse resp = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/external.shp"), new File(f, "pds.shp").toURL().toString(), "text/plain");
            Assert.assertEquals(201, resp.getStatus());
            dom = getAsDOM("wfs?request=getfeature&typename=gs:pds");
            assertFeatures(dom);
            // try to download it again after a full reload from disk (GEOS-4616)
            getGeoServer().reload();
            resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/file.shp"));
            Assert.assertEquals(200, resp.getStatus());
            Assert.assertEquals("application/zip", resp.getContentType());
            Set<String> entryNames = new HashSet<>();
            try (ByteArrayInputStream bin = getBinaryInputStream(resp);ZipInputStream zin = new ZipInputStream(bin)) {
                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    entryNames.add(entry.getName());
                } 
            }
            Assert.assertTrue(entryNames.contains("pds.shp"));
            Assert.assertTrue(entryNames.contains("pds.shx"));
            Assert.assertTrue(entryNames.contains("pds.dbf"));
        } finally {
            FileUtils.deleteQuietly(f);
        }
    }

    @Test
    public void testShapeFileUploadIntoExisting() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getDataStoreByName("gs", "foo_h2"));
        String xml = ((((((("<dataStore>" + (((" <name>foo_h2</name>" + " <type>H2</type>") + " <connectionParameters>") + "<namespace>")) + (MockData.DEFAULT_URI)) + "</namespace>") + "<database>target/foo</database>") + "<dbtype>h2</dbtype>") + " </connectionParameters>") + "<workspace>gs</workspace>") + "</dataStore>";
        post(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores"), xml);
        DataStoreInfo ds = cat.getDataStoreByName("gs", "foo_h2");
        Assert.assertNotNull(ds);
        Assert.assertTrue(cat.getFeatureTypesByDataStore(ds).isEmpty());
        byte[] bytes = shpZipAsBytes();
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/foo_h2/file.shp"), bytes, "application/zip");
        Assert.assertFalse(cat.getFeatureTypesByDataStore(ds).isEmpty());
        Document dom = getAsDOM("wfs?request=getfeature&typename=gs:pds");
        assertFeatures(dom);
    }

    @Test
    public void testShapeFileUploadWithTarget() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getDataStoreByName("gs", "pds"));
        byte[] bytes = shpZipAsBytes();
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/file.shp?target=h2"), bytes, "application/zip");
        DataStoreInfo ds = cat.getDataStoreByName("gs", "pds");
        Assert.assertNotNull(ds);
        Assert.assertFalse(cat.getFeatureTypesByDataStore(ds).isEmpty());
        Document dom = getAsDOM("wfs?request=getfeature&typename=gs:pds");
        assertFeatures(dom);
    }
}

