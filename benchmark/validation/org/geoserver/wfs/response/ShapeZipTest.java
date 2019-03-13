/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;


import Filter.EXCLUDE;
import Filter.INCLUDE;
import SystemTestData.BASIC_POLYGONS;
import WfsFactory.eINSTANCE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.namespace.QName;
import net.opengis.wfs.GetFeatureType;
import org.apache.commons.io.IOUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.Operation;
import org.geoserver.wfs.WFSTestSupport;
import org.geoserver.wfs.request.FeatureCollectionResponse;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.CoordinateXYZM;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.springframework.mock.web.MockHttpServletResponse;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class ShapeZipTest extends WFSTestSupport {
    private static final QName ALL_TYPES = new QName(SystemTestData.CITE_URI, "AllTypes", SystemTestData.CITE_PREFIX);

    private static final QName ALL_DOTS = new QName(SystemTestData.CITE_URI, "All.Types.Dots", SystemTestData.CITE_PREFIX);

    private static final QName GEOMMID = new QName(SystemTestData.CITE_URI, "geommid", SystemTestData.CITE_PREFIX);

    private static final QName LONGNAMES = new QName(SystemTestData.CITE_URI, "longnames", SystemTestData.CITE_PREFIX);

    private static final QName NULLGEOM = new QName(SystemTestData.CITE_URI, "nullgeom", SystemTestData.CITE_PREFIX);

    private static final QName DOTS = new QName(SystemTestData.CITE_URI, "dots.in.name", SystemTestData.CITE_PREFIX);

    private Operation op;

    private GetFeatureType gft;

    @Test
    public void testNoNativeProjection() throws Exception {
        byte[] zip = writeOut(getFeatureSource(BASIC_POLYGONS).getFeatures());
        checkShapefileIntegrity(new String[]{ "BasicPolygons" }, new ByteArrayInputStream(zip));
    }

    @Test
    public void testCharset() throws Exception {
        FeatureSource<? extends FeatureType, ? extends Feature> fs;
        fs = getFeatureSource(BASIC_POLYGONS);
        ShapeZipOutputFormat zip = new ShapeZipOutputFormat();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fct.getFeature().add(fs.getFeatures());
        // add the charset
        Map options = new HashMap();
        options.put("CHARSET", Charset.forName("ISO-8859-15"));
        gft.setFormatOptions(options);
        zip.write(fct, bos, op);
        checkShapefileIntegrity(new String[]{ "BasicPolygons" }, new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals("ISO-8859-15", getCharset(new ByteArrayInputStream(bos.toByteArray())));
    }

    @Test
    public void testRequestUrlNoProxy() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((("wfs?service=WFS&version=1.0.0" + "&request=GetFeature&typeName=") + (getLayerId(BASIC_POLYGONS))) + "&outputFormat=SHAPE-ZIP"));
        Assert.assertEquals("application/zip", response.getContentType());
        checkShapefileIntegrity(new String[]{ "BasicPolygons" }, getBinaryInputStream(response));
        Assert.assertEquals("http://localhost:8080/geoserver/wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=cite:BasicPolygons&outputFormat=SHAPE-ZIP", getRequest(getBinaryInputStream(response)));
    }

    @Test
    public void testRequestUrlWithProxyBase() throws Exception {
        // setup a proxy base url
        GeoServerInfo gs = getGeoServer().getGlobal();
        gs.getSettings().setProxyBaseUrl("https://www.geoserver.org/geoserver");
        getGeoServer().save(gs);
        // check it has been honored
        MockHttpServletResponse response = getAsServletResponse(((("wfs?service=WFS&version=1.0.0" + "&request=GetFeature&typeName=") + (getLayerId(BASIC_POLYGONS))) + "&outputFormat=SHAPE-ZIP"));
        Assert.assertEquals("application/zip", response.getContentType());
        checkShapefileIntegrity(new String[]{ "BasicPolygons" }, getBinaryInputStream(response));
        Assert.assertEquals("https://www.geoserver.org/geoserver/wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=cite:BasicPolygons&outputFormat=SHAPE-ZIP", getRequest(getBinaryInputStream(response)));
    }

    @Test
    public void testMultiType() throws Exception {
        byte[] zip = writeOut(getFeatureSource(ShapeZipTest.ALL_TYPES).getFeatures());
        final String[] expectedTypes = new String[]{ "AllTypesPoint", "AllTypesMPoint", "AllTypesPolygon", "AllTypesLine" };
        checkShapefileIntegrity(expectedTypes, new ByteArrayInputStream(zip));
        checkFieldsAreNotEmpty(new ByteArrayInputStream(zip));
    }

    @Test
    public void testSplitSize() throws Exception {
        ShapeZipOutputFormat of = ((ShapeZipOutputFormat) (applicationContext.getBean("shapezipOutputFormat")));
        byte[] zip = writeOut(getFeatureSource(BASIC_POLYGONS).getFeatures(), 500, 500);
        String shapefileName = BASIC_POLYGONS.getLocalPart();
        final String[] expectedTypes = new String[]{ shapefileName, shapefileName + "1", shapefileName + "2" };
        checkShapefileIntegrity(expectedTypes, new ByteArrayInputStream(zip));
    }

    @Test
    public void testMultiTypeDots() throws Exception {
        byte[] zip = writeOut(getFeatureSource(ShapeZipTest.ALL_DOTS).getFeatures());
        final String[] expectedTypes = new String[]{ "All_Types_DotsPoint", "All_Types_DotsMPoint", "All_Types_DotsPolygon", "All_Types_DotsLine" };
        checkShapefileIntegrity(expectedTypes, new ByteArrayInputStream(zip));
        checkFieldsAreNotEmpty(new ByteArrayInputStream(zip));
    }

    @Test
    public void testGeometryInTheMiddle() throws Exception {
        byte[] zip = writeOut(getFeatureSource(ShapeZipTest.GEOMMID).getFeatures());
        checkFieldsAreNotEmpty(new ByteArrayInputStream(zip));
    }

    @Test
    public void testNullGeometries() throws Exception {
        byte[] zip = writeOut(getFeatureSource(ShapeZipTest.NULLGEOM).getFeatures());
        final String[] expectedTypes = new String[]{ "nullgeom" };
        checkShapefileIntegrity(expectedTypes, new ByteArrayInputStream(zip));
    }

    @Test
    public void testLongNames() throws Exception {
        byte[] zip = writeOut(getFeatureSource(ShapeZipTest.LONGNAMES).getFeatures());
        // check the result is not empty
        SimpleFeatureType schema = checkFieldsAreNotEmpty(new ByteArrayInputStream(zip));
        // check the schema is the expected one
        checkLongNamesSchema(schema);
        // run it again, we had a bug in which the remapped names changed at each run
        zip = writeOut(getFeatureSource(ShapeZipTest.LONGNAMES).getFeatures());
        schema = checkFieldsAreNotEmpty(new ByteArrayInputStream(zip));
        checkLongNamesSchema(schema);
    }

    @Test
    public void testDots() throws Exception {
        byte[] zip = writeOut(getFeatureSource(ShapeZipTest.DOTS).getFeatures());
        final String[] expectedTypes = new String[]{ "dots_in_name" };
        checkShapefileIntegrity(expectedTypes, new ByteArrayInputStream(zip));
        checkFieldsAreNotEmpty(new ByteArrayInputStream(zip));
    }

    @Test
    public void testEmptyResult() throws Exception {
        byte[] zip = writeOut(getFeatureSource(BASIC_POLYGONS).getFeatures(EXCLUDE));
        checkShapefileIntegrity(new String[]{ "BasicPolygons" }, new ByteArrayInputStream(zip));
    }

    @Test
    public void testEmptyResultMultiGeom() throws Exception {
        byte[] zip = writeOut(getFeatureSource(ShapeZipTest.ALL_DOTS).getFeatures(EXCLUDE));
        boolean foundReadme = false;
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            foundReadme |= entry.getName().equals("README.TXT");
        } 
        Assert.assertTrue("Did not find readme file", foundReadme);
    }

    @Test
    public void testTemplateSingleType() throws Exception {
        // copy the new template to the data dir
        WorkspaceInfo ws = getCatalog().getWorkspaceByName(BASIC_POLYGONS.getPrefix());
        getDataDirectory().copyToWorkspaceDir(ws, getClass().getResourceAsStream("shapeziptest.ftl"), "shapezip.ftl");
        // setup the request params
        SimpleFeatureCollection fc = getFeatureSource(BASIC_POLYGONS).getFeatures(INCLUDE);
        ShapeZipOutputFormat zip = new ShapeZipOutputFormat();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fct.getFeature().add(fc);
        // get the file name
        Assert.assertEquals("shapezip_BasicPolygons.zip", zip.getAttachmentFileName(fct, op));
        // check the contents
        zip.write(fct, bos, op);
        byte[] zipBytes = bos.toByteArray();
        checkShapefileIntegrity(new String[]{ "theshape_BasicPolygons" }, new ByteArrayInputStream(zipBytes));
    }

    @Test
    public void testTemplateMultiType() throws Exception {
        // copy the new template to the data dir
        WorkspaceInfo ws = getCatalog().getWorkspaceByName(BASIC_POLYGONS.getPrefix());
        getDataDirectory().copyToWorkspaceDir(ws, getClass().getResourceAsStream("shapeziptest.ftl"), "shapezip.ftl");
        // setup the request params
        ShapeZipOutputFormat zip = new ShapeZipOutputFormat();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fct.getFeature().add(getFeatureSource(BASIC_POLYGONS).getFeatures(INCLUDE));
        fct.getFeature().add(getFeatureSource(SystemTestData.BRIDGES).getFeatures(INCLUDE));
        // get the file name
        Assert.assertEquals("shapezip_BasicPolygons.zip", zip.getAttachmentFileName(fct, op));
        // check the contents
        zip.write(fct, bos, op);
        byte[] zipBytes = bos.toByteArray();
        checkShapefileIntegrity(new String[]{ "theshape_BasicPolygons", "theshape_Bridges" }, new ByteArrayInputStream(zipBytes));
    }

    @Test
    public void testTemplateMultiGeomType() throws Exception {
        // copy the new template to the data dir
        WorkspaceInfo ws = getCatalog().getWorkspaceByName(ShapeZipTest.ALL_DOTS.getPrefix());
        getDataDirectory().copyToWorkspaceDir(ws, getClass().getResourceAsStream("shapeziptest.ftl"), "shapezip.ftl");
        // setup the request params
        ShapeZipOutputFormat zip = new ShapeZipOutputFormat();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fct.getFeature().add(getFeatureSource(ShapeZipTest.ALL_DOTS).getFeatures(INCLUDE));
        // get the file name
        Assert.assertEquals("shapezip_All_Types_Dots.zip", zip.getAttachmentFileName(fct, op));
        // check the contents
        zip.write(fct, bos, op);
        byte[] zipBytes = bos.toByteArray();
        checkShapefileIntegrity(new String[]{ "theshape_All_Types_DotsPoint", "theshape_All_Types_DotsMPoint", "theshape_All_Types_DotsPolygon", "theshape_All_Types_DotsLine" }, new ByteArrayInputStream(zipBytes));
    }

    @Test
    public void testTemplatePOSTRequest10() throws Exception {
        String xml = "<wfs:GetFeature " + ((((((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "outputFormat=\"shape-zip\" ") + "> ") + "<wfs:Query typeName=\"cdf:Other\"> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        MockHttpServletResponse response = postAsServletResponse("wfs", xml);
        Assert.assertEquals("application/zip", response.getContentType());
    }

    @Test
    public void testOutputZipFileNameSpecifiedInFormatOptions() throws Exception {
        ShapeZipOutputFormat zip = new ShapeZipOutputFormat(getGeoServer(), getCatalog(), getResourceLoader());
        FeatureCollectionResponse mockResult = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        mockResult.getFeature().add(getFeatureSource(ShapeZipTest.ALL_DOTS).getFeatures(INCLUDE));
        GetFeatureType mockRequest = eINSTANCE.createGetFeatureType();
        Operation mockOperation = new Operation("GetFeature", getServiceDescriptor10(), null, new Object[]{ mockRequest });
        Assert.assertEquals("All_Types_Dots.zip", zip.getAttachmentFileName(mockResult, mockOperation));
        mockRequest.getFormatOptions().put("FILENAME", "REQUEST_SUPPLIED_FILENAME.zip");
        Assert.assertEquals("REQUEST_SUPPLIED_FILENAME.zip", zip.getAttachmentFileName(mockResult, mockOperation));
    }

    @Test
    public void testTemplatePOSTRequest11() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + ((((((("<GetFeature xmlns=\"http://www.opengis.net/wfs\" xmlns:DigitalGlobe=\"http://www.digitalglobe.com\"\n" + "    xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "    xmlns:gml=\"http://www.opengis.net/gml\" service=\"WFS\" version=\"1.1.0\"\n") + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "    outputFormat=\"shape-zip\" maxFeatures=\"100\" handle=\"\">\n") + "    <Query typeName=\"cdf:Other\" srsName=\"urn:ogc:def:crs:EPSG::4326\">") + "</Query> ") + "</GetFeature>");
        MockHttpServletResponse response = postAsServletResponse("wfs", xml);
        Assert.assertEquals("application/zip", response.getContentType());
    }

    @Test
    public void testESRIFormat() throws Exception {
        setupESRIPropertyFile();
        FeatureSource<? extends FeatureType, ? extends Feature> fs;
        fs = getFeatureSource(BASIC_POLYGONS);
        ShapeZipOutputFormat zip = new ShapeZipOutputFormat(getGeoServer(), getCatalog(), getResourceLoader());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fct.getFeature().add(fs.getFeatures());
        // add the charset
        Map options = new HashMap();
        options.put("PRJFILEFORMAT", "ESRI");
        gft.setFormatOptions(options);
        zip.write(fct, bos, op);
        byte[] byteArrayZip = bos.toByteArray();
        checkShapefileIntegrity(new String[]{ "BasicPolygons" }, new ByteArrayInputStream(byteArrayZip));
        checkFileContent("BasicPolygons.prj", new ByteArrayInputStream(byteArrayZip), get4326_ESRI_WKTContent());
    }

    @Test
    public void testESRIFormatMultiType() throws Exception {
        setupESRIPropertyFile();
        ShapeZipOutputFormat zip = new ShapeZipOutputFormat(getGeoServer(), getCatalog(), getResourceLoader());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fct.getFeature().add(getFeatureSource(ShapeZipTest.ALL_TYPES).getFeatures());
        Map options = new HashMap();
        options.put("PRJFILEFORMAT", "ESRI");
        gft.setFormatOptions(options);
        zip.write(fct, bos, op);
        byte[] byteArrayZip = bos.toByteArray();
        final String[] expectedTypes = new String[]{ "AllTypesPoint", "AllTypesMPoint", "AllTypesPolygon", "AllTypesLine" };
        checkShapefileIntegrity(expectedTypes, new ByteArrayInputStream(byteArrayZip));
        for (String fileName : expectedTypes) {
            checkFileContent((fileName + ".prj"), new ByteArrayInputStream(byteArrayZip), get4326_ESRI_WKTContent());
        }
    }

    @Test
    public void testESRIFormatFromDefaultValue() throws Exception {
        setupESRIPropertyFile();
        final GeoServer geoServer = getGeoServer();
        setupESRIFormatByDefault(geoServer, true);
        final FeatureSource fs = getFeatureSource(BASIC_POLYGONS);
        final Catalog catalog = getCatalog();
        final GeoServerResourceLoader resourceLoader = getResourceLoader();
        ShapeZipOutputFormat zip = new ShapeZipOutputFormat(geoServer, catalog, resourceLoader);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FeatureCollectionResponse fct = FeatureCollectionResponse.adapt(eINSTANCE.createFeatureCollectionType());
        fct.getFeature().add(fs.getFeatures());
        // add the charset
        Map options = new HashMap();
        gft.setFormatOptions(options);
        zip.write(fct, bos, op);
        byte[] byteArrayZip = bos.toByteArray();
        checkShapefileIntegrity(new String[]{ "BasicPolygons" }, new ByteArrayInputStream(byteArrayZip));
        checkFileContent("BasicPolygons.prj", new ByteArrayInputStream(byteArrayZip), get4326_ESRI_WKTContent());
    }

    /**
     * Test for Point ZM support on GetFeature shapefile output
     */
    @Test
    public void testPointZMShp() throws Exception {
        // create a feature collection of POINT ZM (4D)
        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureType featureType = DataUtilities.createType("pointmz", "name:String,geom:Point:4326");
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(featureType);
        fb.add("point1");
        fb.add(gf.createPoint(new CoordinateXYZM(1, 2, 3, 4)));
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        features.add(fb.buildFeature("1"));
        SimpleFeatureCollection featureCollection = DataUtilities.collection(features);
        // write the zip shapefile bytes
        byte[] zipBytes = writeOut(featureCollection);
        // get shp file bytes only
        byte[] resultBytes = getShpOnlyBytes(zipBytes);
        // get expected byte array
        InputStream resource = getClass().getClassLoader().getResourceAsStream("org/geoserver/wfs/response/pointZm.shp");
        byte[] expectedBytes = IOUtils.toByteArray(resource);
        resource.close();
        // compare generated bytes
        Assert.assertTrue(Arrays.equals(resultBytes, expectedBytes));
    }

    /**
     * Test for MultiPoint ZM support on GetFeature shapefile output
     */
    @Test
    public void testMultiPointZMShp() throws Exception {
        // create a feature collection of POINT ZM (4D)
        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureType featureType = DataUtilities.createType("multipointmz", "name:String,geom:MultiPoint:4326");
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(featureType);
        fb.add("points1");
        fb.add(gf.createMultiPoint(new Point[]{ gf.createPoint(new CoordinateXYZM(1, 2, 3, 4)), gf.createPoint(new CoordinateXYZM(5, 6, 7, 8)) }));
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        features.add(fb.buildFeature("1"));
        SimpleFeatureCollection featureCollection = DataUtilities.collection(features);
        // write the zip shapefile bytes
        byte[] zipBytes = writeOut(featureCollection);
        // get shp file bytes only
        byte[] resultBytes = getShpOnlyBytes(zipBytes);
        // get expected byte array
        InputStream in = getClass().getClassLoader().getResourceAsStream("org/geoserver/wfs/response/multiPointZm.shp");
        byte[] expectedBytes = IOUtils.toByteArray(in);
        in.close();
        // compare generated bytes
        Assert.assertTrue(Arrays.equals(resultBytes, expectedBytes));
    }

    /**
     * Test for MultiLineString ZM support on GetFeature shapefile output
     */
    @Test
    public void testMultiLineStringZMShp() throws Exception {
        // create a feature collection of MULTILINESTRING ZM (4D)
        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureType featureType = DataUtilities.createType("linestringmz", "name:String,geom:MultiLineString:4326");
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(featureType);
        fb.add("line1");
        fb.add(gf.createMultiLineString(new LineString[]{ gf.createLineString(new CoordinateXYZM[]{ new CoordinateXYZM(1, 2, 3, 4), new CoordinateXYZM(5, 6, 7, 8) }) }));
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        features.add(fb.buildFeature("1"));
        SimpleFeatureCollection featureCollection = DataUtilities.collection(features);
        // write the zip shapefile bytes
        byte[] zipBytes = writeOut(featureCollection);
        // get shp file bytes only
        byte[] resultBytes = getShpOnlyBytes(zipBytes);
        // get expected byte array
        InputStream is = getClass().getClassLoader().getResourceAsStream("org/geoserver/wfs/response/lineStringZm.shp");
        byte[] expectedBytes = IOUtils.toByteArray(is);
        is.close();
        // compare generated bytes
        Assert.assertTrue(Arrays.equals(resultBytes, expectedBytes));
    }

    /**
     * Test for MultiPolygon ZM support on GetFeature shapefile output
     */
    @Test
    public void testMultiPolygonZMShp() throws Exception {
        // create a feature collection of MULTIPOLYGON ZM (4D)
        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureType featureType = DataUtilities.createType("polygonmz", "name:String,geom:MultiPolygon:4326");
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(featureType);
        fb.add("polygon1");
        fb.add(gf.createMultiPolygon(new Polygon[]{ gf.createPolygon(new CoordinateXYZM[]{ new CoordinateXYZM(0, 0, 3, 1), new CoordinateXYZM(1, 1, 7, 2), new CoordinateXYZM(1, 0, 7, 3), new CoordinateXYZM(0, 0, 3, 1) }) }));
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        features.add(fb.buildFeature("1"));
        SimpleFeatureCollection featureCollection = DataUtilities.collection(features);
        // write the zip shapefile bytes
        byte[] zipBytes = writeOut(featureCollection);
        // get shp file bytes only
        byte[] resultBytes = getShpOnlyBytes(zipBytes);
        // get expected byte array
        InputStream in = getClass().getClassLoader().getResourceAsStream("org/geoserver/wfs/response/polygonZm.shp");
        byte[] expectedBytes = IOUtils.toByteArray(in);
        // compare generated bytes
        Assert.assertTrue(Arrays.equals(resultBytes, expectedBytes));
    }
}

