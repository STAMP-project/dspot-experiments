/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;


import Filter.EXCLUDE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.GetFeatureType;
import org.geoserver.platform.Operation;
import org.geotools.data.DataStore;
import org.geotools.feature.FeatureCollection;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class Ogr2OgrFormatTest {
    DataStore dataStore;

    Ogr2OgrOutputFormat ogr;

    Operation op;

    FeatureCollectionType fct;

    GetFeatureType gft;

    @Test
    public void testCanHandle() {
        gft.setOutputFormat("OGR-KML");
        Assert.assertTrue(ogr.canHandle(op));
        gft.setOutputFormat("OGR-CSV");
        Assert.assertTrue(ogr.canHandle(op));
        gft.setOutputFormat("RANDOM_FORMAT");
        Assert.assertTrue(ogr.canHandle(op));
    }

    @Test
    public void testContentTypeZip() {
        gft.setOutputFormat("OGR-SHP");
        Assert.assertEquals("application/zip", ogr.getMimeType(null, op));
    }

    @Test
    public void testContentTypeKml() {
        gft.setOutputFormat("OGR-KML");
        Assert.assertEquals("application/vnd.google-earth.kml", ogr.getMimeType(null, op));
    }

    @Test
    public void testSimpleKML() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("Buildings").getFeatures();
        fct.getFeature().add(fc);
        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-KML");
        ogr.write(fct, bos, op);
        // parse the kml to check it's really xml...
        Document dom = dom(new ByteArrayInputStream(bos.toByteArray()));
        // print(dom);
        // some very light assumptions on the contents, since we
        // cannot control how ogr encodes the kml... let's just assess
        // it's kml with the proper number of features
        Assert.assertEquals("kml", dom.getDocumentElement().getTagName());
        Assert.assertEquals(2, dom.getElementsByTagName("Placemark").getLength());
    }

    @Test
    public void testZippedKML() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("Buildings").getFeatures();
        fct.getFeature().add(fc);
        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-KML-ZIP");
        ogr.write(fct, bos, op);
        // unzip the result
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Document dom = null;
        ZipEntry entry = zis.getNextEntry();
        Assert.assertEquals("Buildings.kml", entry.getName());
        dom = dom(zis);
        // some very light assumptions on the contents, since we
        // cannot control how ogr encodes the kml... let's just assess
        // it's kml with the proper number of features
        Assert.assertEquals("kml", dom.getDocumentElement().getTagName());
        Assert.assertEquals(2, dom.getElementsByTagName("Placemark").getLength());
    }

    @Test
    public void testEmptyKML() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("Buildings").getFeatures(EXCLUDE);
        fct.getFeature().add(fc);
        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-KML");
        ogr.write(fct, bos, op);
        // parse the kml to check it's really xml...
        Document dom = dom(new ByteArrayInputStream(bos.toByteArray()));
        // print(dom);
        // some very light assumptions on the contents, since we
        // cannot control how ogr encodes the kml... let's just assess
        // it's kml with the proper number of features
        Assert.assertEquals("kml", dom.getDocumentElement().getTagName());
        Assert.assertEquals(0, dom.getElementsByTagName("Placemark").getLength());
    }

    @Test
    public void testSimpleCSV() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("Buildings").getFeatures();
        fct.getFeature().add(fc);
        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-CSV");
        ogr.write(fct, bos, op);
        // read back
        String csv = read(new ByteArrayInputStream(bos.toByteArray()));
        // couple simple checks
        String[] lines = csv.split("\n");
        // headers and the two lines
        Assert.assertEquals(3, lines.length);
        Assert.assertTrue(csv.contains("123 Main Street"));
    }

    @Test
    public void testSimpleMIF() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("Buildings").getFeatures();
        fct.getFeature().add(fc);
        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-MIF");
        ogr.write(fct, bos, op);
        // read back
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bos.toByteArray()));
        // we should get two files at least, a .mif and a .mid
        Set<String> fileNames = new HashSet<String>();
        ZipEntry entry = null;
        while ((entry = zis.getNextEntry()) != null) {
            fileNames.add(entry.getName());
        } 
        Assert.assertTrue(fileNames.contains("Buildings.mif"));
        Assert.assertTrue(fileNames.contains("Buildings.mid"));
    }

    @Test
    public void testGeometrylessCSV() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("Geometryless").getFeatures();
        fct.getFeature().add(fc);
        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-CSV");
        ogr.write(fct, bos, op);
        // read back
        String csv = read(new ByteArrayInputStream(bos.toByteArray()));
        // couple simple checks
        String[] lines = csv.split("\n");
        // headers and the feature lines
        Assert.assertEquals(4, lines.length);
        // let's see if one of the expected lines is there
        Assert.assertTrue(csv.contains("Alessia"));
    }

    @Test
    public void testAllTypesKML() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("AllTypes").getFeatures();
        fct.getFeature().add(fc);
        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-KML");
        ogr.write(fct, bos, op);
        // read back
        Document dom = dom(new ByteArrayInputStream(bos.toByteArray()));
        // print(dom);
        // some very light assumptions on the contents, since we
        // cannot control how ogr encodes the kml... let's just assess
        // it's kml with the proper number of features
        Assert.assertEquals("kml", dom.getDocumentElement().getTagName());
        Assert.assertEquals(6, dom.getElementsByTagName("Placemark").getLength());
    }
}

