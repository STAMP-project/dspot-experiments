/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import Filter.INCLUDE;
import java.io.IOException;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geotools.appschema.filter.FilterFactoryImplNamespaceAware;
import org.geotools.appschema.jdbc.NestedFilterToSQL;
import org.geotools.data.FeatureSource;
import org.geotools.data.complex.AppSchemaDataAccess;
import org.geotools.data.complex.FeatureTypeMapping;
import org.geotools.data.complex.filter.ComplexFilterSplitter;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.data.util.NullProgressListener;
import org.geotools.geometry.jts.JTS;
import org.geotools.gml.producer.CoordinateFormatter;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.filter.Filter;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.w3c.dom.Document;


/**
 * This is to test encoding of SRS information and reprojection values in app-schema features. This
 * is separated from SRSWfsTest as both test uses the same top level mapping and this test doesn't
 * make changes to the Axis Order.
 *
 * @author Victor Tey, CSIRO Exploration and Mining
 */
public class SRSReprojectionTest extends AbstractAppSchemaTestSupport {
    final String EPSG_4326 = "urn:x-ogc:def:crs:EPSG:4326";

    final String EPSG_4283 = "EPSG:4283";

    /**
     * Tests re-projection of NonFeatureTypeProxy.
     */
    @Test
    public void testNonFeatureTypeProxy() {
        Document doc = null;
        doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=gsml:MappedFeature&srsName=EPSG:4326");
        LOGGER.info(("WFS GetFeature&typename=gsml:MappedFeature response:\n" + (prettyString(doc))));
        assertXpathEvaluatesTo("value01", "//gsml:MappedFeature[@gml:id='gsml.mappedfeature.mf1']/gsml:observationMethod[1]/gsml:CGI_TermValue/gsml:value[@codeSpace='codespace01']", doc);
        assertXpathEvaluatesTo("value02", "//gsml:MappedFeature[@gml:id='gsml.mappedfeature.mf1']/gsml:observationMethod[2]/gsml:CGI_TermValue/gsml:value[@codeSpace='codespace02']", doc);
        assertXpathEvaluatesTo("value03", "//gsml:MappedFeature[@gml:id='gsml.mappedfeature.mf2']/gsml:observationMethod[1]/gsml:CGI_TermValue/gsml:value[@codeSpace='codespace03']", doc);
        assertXpathCount(0, "//gsml:MappedFeature[@gml:id='gsml.mappedfeature.mf3']/gsml:observationMethod[1]/gsml:CGI_TermValue/gsml:value", doc);
        assertXpathCount(0, "//gsml:MappedFeature[@gml:id='gsml.mappedfeature.mf4']/gsml:observationMethod[2]/gsml:CGI_TermValue/gsml:value", doc);
        assertXpathEvaluatesTo("http://www.opengis.net/gml/srs/epsg.xml#4326", "//gsml:MappedFeature[@gml:id='gsml.mappedfeature.mf1']/gsml:shape/gml:Point/@srsName", doc);
        assertXpathEvaluatesTo("2", "//gsml:MappedFeature[@gml:id='gsml.mappedfeature.mf1']/gsml:shape/gml:Point/@srsDimension", doc);
        // test that result returns in long,lat order.
        assertXpathEvaluatesTo("133.8855 -23.6701", "//gsml:MappedFeature[@gml:id='gsml.mappedfeature.mf1']/gsml:shape/gml:Point/gml:pos", doc);
        doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=gsml:MappedFeature&srsName=urn:x-ogc:def:crs:EPSG:4326");
        LOGGER.info(("WFS GetFeature&typename=gsml:MappedFeature response:\n" + (prettyString(doc))));
        // test result returns in lat,long order
        assertXpathEvaluatesTo("-23.6701 133.8855", "//gsml:MappedFeature[@gml:id='gsml.mappedfeature.mf1']/gsml:shape/gml:Point/gml:pos", doc);
    }

    /**
     * Tests re-projection in a normal feature chaining mapping where there is no geometry on the
     * parent feature and only on the nested feature level
     */
    @Test
    public void testChainingReprojection() throws MismatchedDimensionException, FactoryException, NoSuchAuthorityCodeException, TransformException {
        Document doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=ex:geomContainer");
        LOGGER.info(("WFS GetFeature&typename=ex:geomContainer response:\n" + (prettyString(doc))));
        // Generate test geometries and its results after re-projection.
        CoordinateReferenceSystem sourceCRS = ((CoordinateReferenceSystem) (CRS.decode(EPSG_4283)));
        CoordinateReferenceSystem targetCRS = ((CoordinateReferenceSystem) (CRS.decode(EPSG_4326)));
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        GeometryFactory factory = new GeometryFactory();
        Polygon srcPolygon = factory.createPolygon(factory.createLinearRing(factory.getCoordinateSequenceFactory().create(new Coordinate[]{ new Coordinate((-1.2), 52.5), new Coordinate((-1.2), 52.6), new Coordinate((-1.1), 52.6), new Coordinate((-1.1), 52.5), new Coordinate((-1.2), 52.5) })), null);
        Polygon targetPolygon = ((Polygon) (JTS.transform(srcPolygon, transform)));
        StringBuffer polygonBuffer = new StringBuffer();
        CoordinateFormatter formatter = new CoordinateFormatter(8);
        for (Coordinate coord : targetPolygon.getCoordinates()) {
            formatter.format(coord.x, polygonBuffer).append(" ");
            formatter.format(coord.y, polygonBuffer).append(" ");
        }
        String targetPolygonCoords = polygonBuffer.toString().trim();
        Point targetPoint = ((Point) (JTS.transform(factory.createPoint(new Coordinate(42.58, 31.29)), transform)));
        String targetPointCoord = ((formatter.format(targetPoint.getCoordinate().x)) + " ") + (formatter.format(targetPoint.getCoordinate().y));
        assertXpathEvaluatesTo("52.5 -1.2 52.6 -1.2 52.6 -1.1 52.5 -1.1 52.5 -1.2", "//ex:geomContainer[@gml:id='1']/ex:nestedFeature[2]/ex:nestedGeom[@gml:id='secondNested.2']/ex:nestedFeature/ex:nestedGeom[@gml:id='thirdNested.1']/ex:geom/gml:Polygon/gml:exterior/gml:LinearRing/gml:posList", doc);
        assertXpathEvaluatesTo("urn:x-ogc:def:crs:EPSG:4283", "//ex:geomContainer[@gml:id='2']/ex:nestedFeature[2]/ex:nestedGeom[@gml:id='secondNested.1']/ex:nestedFeature/ex:nestedGeom[@gml:id='thirdNested.2']/ex:geom/gml:Point/@srsName", doc);
        assertXpathEvaluatesTo("31.29 42.58", "//ex:geomContainer[@gml:id='2']/ex:nestedFeature[2]/ex:nestedGeom[@gml:id='secondNested.1']/ex:nestedFeature/ex:nestedGeom[@gml:id='thirdNested.2']/ex:geom/gml:Point/gml:pos", doc);
        assertXpathEvaluatesTo("NAME", "//ex:geomContainer[@gml:id='2']/ex:nestedFeature[2]/ex:nestedGeom[@gml:id='secondNested.1']/ex:name", doc);
        // test that having empty geometry in a re-projection doesn't throw an exception.
        assertXpathCount(0, "//ex:geomContainer[@gml:id='2']/ex:nestedFeature[2]/ex:nestedGeom[@gml:id='secondNested.1']/ex:geom", doc);
        doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=ex:geomContainer&srsName=urn:x-ogc:def:crs:EPSG:4326");
        LOGGER.info(("WFS GetFeature&typename=ex:geomContainer response:\n" + (prettyString(doc))));
        // test that the polygon is correctly re-projected.
        assertXpathEvaluatesTo(targetPolygonCoords, "//ex:geomContainer[@gml:id='1']/ex:nestedFeature[2]/ex:nestedGeom[@gml:id='secondNested.2']/ex:nestedFeature/ex:nestedGeom[@gml:id='thirdNested.1']/ex:geom/gml:Polygon/gml:exterior/gml:LinearRing/gml:posList", doc);
        assertXpathEvaluatesTo("urn:x-ogc:def:crs:EPSG:4326", "//ex:geomContainer[@gml:id='2']/ex:nestedFeature[2]/ex:nestedGeom[@gml:id='secondNested.1']/ex:nestedFeature/ex:nestedGeom[@gml:id='thirdNested.2']/ex:geom/gml:Point/@srsName", doc);
        // Test that the point coordinate are correctly projected.
        assertXpathEvaluatesTo(targetPointCoord, "//ex:geomContainer[@gml:id='2']/ex:nestedFeature[2]/ex:nestedGeom[@gml:id='secondNested.1']/ex:nestedFeature/ex:nestedGeom[@gml:id='thirdNested.2']/ex:geom/gml:Point/gml:pos", doc);
    }

    /**
     * Tests that Xlink href works fine in nested feature chaining with features that contains
     * geometry
     */
    @Test
    public void testChainingXlink() {
        Document doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=ex:geomContainer");
        LOGGER.info(("WFS GetFeature&typename=ex:geomContainer response:\n" + (prettyString(doc))));
        assertXpathEvaluatesTo("http://example.com/UrnResolver/?uri=1", "//ex:geomContainer[@gml:id='1']/ex:nestedFeature[3]/@xlink:href", doc);
        assertXpathEvaluatesTo("http://example.com/UrnResolver/?uri=2", "//ex:geomContainer[@gml:id='2']/ex:nestedFeature[3]/@xlink:href", doc);
    }

    @Test
    public void testNestedSpatialFilterEncoding() throws IOException, FilterToSQLException {
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName("ex", "geomContainer");
        FeatureSource fs = ftInfo.getFeatureSource(new NullProgressListener(), null);
        AppSchemaDataAccess da = ((AppSchemaDataAccess) (fs.getDataStore()));
        FeatureTypeMapping rootMapping = da.getMappingByNameOrElement(ftInfo.getQualifiedName());
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        JDBCDataStore store = ((JDBCDataStore) (rootMapping.getSource().getDataStore()));
        FilterFactoryImplNamespaceAware ff = new FilterFactoryImplNamespaceAware();
        ff.setNamepaceContext(rootMapping.getNamespaces());
        /* test spatial filter on nested geometry attribute */
        GeometryFactory factory = new GeometryFactory();
        Polygon srcPolygon = factory.createPolygon(factory.createLinearRing(factory.getCoordinateSequenceFactory().create(new Coordinate[]{ new Coordinate((-1.2), 52.5), new Coordinate((-1.2), 52.6), new Coordinate((-1.1), 52.6), new Coordinate((-1.1), 52.5), new Coordinate((-1.2), 52.5) })), null);
        Envelope bounds = srcPolygon.getEnvelopeInternal();
        BBOX intersects = ff.bbox("ex:nestedFeature[2]/ex:nestedGeom/ex:nestedFeature/ex:nestedGeom/ex:geom", bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), "EPSG:4283");
        // Filter involving nested geometry attribute --> CAN be encoded
        ComplexFilterSplitter splitter = new ComplexFilterSplitter(store.getFilterCapabilities(), rootMapping);
        splitter.visit(intersects, null);
        Filter preFilter = splitter.getFilterPre();
        Filter postFilter = splitter.getFilterPost();
        Assert.assertEquals(intersects, preFilter);
        Assert.assertEquals(INCLUDE, postFilter);
        // filter must be "unrolled" (i.e. reverse mapped) first
        Filter unrolled = AppSchemaDataAccess.unrollFilter(intersects, rootMapping);
        // Filter is nested
        Assert.assertTrue(NestedFilterToSQL.isNestedFilter(unrolled));
        // Encode nested filter
        NestedFilterToSQL nestedFilterToSQL = createNestedFilterEncoder(rootMapping);
        String encodedFilter = nestedFilterToSQL.encodeToString(unrolled);
        Assert.assertTrue(encodedFilter.contains("EXISTS"));
    }
}

