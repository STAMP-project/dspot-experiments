/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.rest.RestBaseController;
import org.geotools.util.URLs;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class DataStoreFileUploadTest extends CatalogRESTTestSupport {
    @Test
    public void testShapeFileUploadNotExisting() throws Exception {
        File file = new File("./target/notThere.tiff");
        if (file.exists()) {
            Assert.assertTrue(file.delete());
        }
        URL url = URLs.fileToUrl(file.getCanonicalFile());
        String body = url.toExternalForm();
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/external.shp"), body, "text/plain");
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testShapefileUploadMultiple() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getDataStoreByName("gs", "pdst"));
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pdst/file.shp?configure=all"), shpMultiZipAsBytes(), "application/zip");
        DataStoreInfo ds = cat.getDataStoreByName("gs", "pdst");
        Assert.assertNotNull(ds);
        Assert.assertEquals(2, cat.getFeatureTypesByDataStore(ds).size());
    }

    @Test
    public void testShapefileUploadZip() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getDataStoreByName("gs", "san_andres_y_providencia"));
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/san_andres_y_providencia/file.shp"), shpSanAndresShapefilesZipAsBytes(), "application/zip");
        DataStoreInfo ds = cat.getDataStoreByName("gs", "san_andres_y_providencia");
        Assert.assertNotNull(ds);
        Assert.assertEquals(1, cat.getFeatureTypesByDataStore(ds).size());
    }

    @Test
    public void testGetProperties() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/file.properties"));
        Assert.assertEquals(404, resp.getStatus());
        byte[] bytes = propertyFile();
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/file.properties"), bytes, "text/plain");
        resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/datastores/pds/file.properties"));
        Assert.assertEquals(200, resp.getStatus());
        Assert.assertEquals("application/zip", resp.getContentType());
        ByteArrayInputStream bin = getBinaryInputStream(resp);
        ZipInputStream zin = new ZipInputStream(bin);
        ZipEntry entry = zin.getNextEntry();
        Assert.assertNotNull(entry);
        Assert.assertEquals("pds.properties", entry.getName());
    }

    @Test
    public void testAppSchemaMappingFileUpload() throws Exception {
        byte[] bytes = appSchemaMappingAsBytes();
        if (bytes == null) {
            // skip test
            LOGGER.warning("app-schema test data not available: skipping test");
            return;
        }
        // copy necessary .properties files from classpath
        loadAppSchemaTestData();
        // upload mapping file (datastore is created implicitly)
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gsml/datastores/mappedPolygons/file.appschema"), bytes, "text/xml");
        Document dom = getAsDOM("wfs?request=getfeature&typename=gsml:MappedFeature");
        // print(dom);
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        NodeList mappedFeatureNodes = dom.getDocumentElement().getElementsByTagNameNS("http://www.cgi-iugs.org/xml/GeoSciML/2", "MappedFeature");
        Assert.assertNotNull(mappedFeatureNodes);
        Assert.assertEquals(2, mappedFeatureNodes.getLength());
        int namesCount = countNameAttributes(mappedFeatureNodes.item(0));
        Assert.assertEquals(2, namesCount);
        // upload alternative mapping file
        bytes = appSchemaAlternativeMappingAsBytes();
        put(((RestBaseController.ROOT_PATH) + "/workspaces/gsml/datastores/mappedPolygons/file.appschema?configure=none"), bytes, "text/xml");
        dom = getAsDOM("wfs?request=getfeature&typename=gsml:MappedFeature");
        // print(dom);
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        mappedFeatureNodes = dom.getDocumentElement().getElementsByTagNameNS("http://www.cgi-iugs.org/xml/GeoSciML/2", "MappedFeature");
        Assert.assertNotNull(mappedFeatureNodes);
        Assert.assertEquals(2, mappedFeatureNodes.getLength());
        namesCount = countNameAttributes(mappedFeatureNodes.item(0));
        // just one name should be found
        Assert.assertEquals(1, namesCount);
    }
}

