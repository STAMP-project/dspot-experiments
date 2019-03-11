package org.geoserver.test;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.geotools.data.complex.AppSchemaDataAccess;
import org.geotools.data.complex.FeatureTypeMapping;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.feature.NameImpl;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class NestedGeometryFilterEncodingTest extends AbstractAppSchemaTestSupport {
    private static final String GML32_PREFIX = "gml32";

    private static final String STATION_FEATURE = "st_gml32:StationWithMeasurements_gml32";

    private static final Name STATION_FEATURE_NAME = new NameImpl(NestedGeometryFilterEncodingTest.MockData.STATIONS_URI_GML32, "StationWithMeasurements_gml32");

    private static final String STATION_NESTED_GEOM = "st_gml32:measurements/ms_gml32:Measurement_gml32/" + "ms_gml32:sampledArea/ms_gml32:SampledArea/ms_gml32:geometry";

    private static final String STATION_NONEXISTENT_NESTED_GEOM = "st_gml32:measurements/ms_gml32:Measurement_gml32/" + "ms_gml32:sampledArea/ms_gml32:SampledArea/ms_gml32:not_there_geometry";

    private static final String STATION_WRONG_NESTED_GEOM = "st_gml32:measurements/ms_gml32:Measurement_gml32/" + "ms_gml32:sampledArea/ms_gml32:SampledArea/ms_gml32:code";

    AppSchemaDataAccess dataAccess;

    private FeatureTypeMapping rootMapping;

    private FilterFactory2 ff;

    private WKTReader wktReader = new WKTReader();

    private static final class MockData extends StationsMockData {
        @Override
        public void addContent() {
            // add GML 3.2 namespaces
            putNamespace(StationsMockData.STATIONS_PREFIX_GML32, StationsMockData.STATIONS_URI_GML32);
            putNamespace(StationsMockData.MEASUREMENTS_PREFIX_GML32, StationsMockData.MEASUREMENTS_URI_GML32);
            // add GML 3.2 feature types
            Map<String, String> gml32Parameters = new HashMap<>();
            gml32Parameters.put("GML_PREFIX", NestedGeometryFilterEncodingTest.GML32_PREFIX);
            gml32Parameters.put("GML_NAMESPACE", "http://www.opengis.net/gml/3.2");
            gml32Parameters.put("GML_LOCATION", "http://schemas.opengis.net/gml/3.2.1/gml.xsd");
            addStationFeatureType(StationsMockData.STATIONS_PREFIX_GML32, NestedGeometryFilterEncodingTest.GML32_PREFIX, "StationWithMeasurements", "stations", "defaultGeometry/stations2.xml", "measurements", "defaultGeometry/measurements.xml", gml32Parameters);
            addMeasurementFeatureType(StationsMockData.MEASUREMENTS_PREFIX_GML32, NestedGeometryFilterEncodingTest.GML32_PREFIX, "measurements", "defaultGeometry/measurements.xml", gml32Parameters);
        }

        /**
         * Helper method that will add the station feature type customizing it for the desired GML
         * version.
         */
        protected void addStationFeatureType(String namespacePrefix, String gmlPrefix, String stationsFeatureType, String stationsMappingsName, String stationsMappingsPath, String measurementsMappingsName, String measurementsMappingsPath, Map<String, String> parameters) {
            // create root directory
            File gmlDirectory = getDirectoryForGmlPrefix(gmlPrefix);
            gmlDirectory.mkdirs();
            // add the necessary files
            File stationsMappings = new File(gmlDirectory, String.format("%s_%s.xml", stationsMappingsName, gmlPrefix));
            File stationsProperties = new File(gmlDirectory, String.format("stations_%s.properties", gmlPrefix));
            File stationsSchema = new File(gmlDirectory, String.format("stations_%s.xsd", gmlPrefix));
            File measurementsSchema = new File(gmlDirectory, String.format("measurements_%s.xsd", gmlPrefix));
            // perform the parameterization
            StationsMockData.substituteParameters(("/test-data/stations/" + stationsMappingsPath), parameters, stationsMappings);
            StationsMockData.substituteParameters("/test-data/stations/defaultGeometry/stations.properties", parameters, stationsProperties);
            StationsMockData.substituteParameters("/test-data/stations/defaultGeometry/stations.xsd", parameters, stationsSchema);
            StationsMockData.substituteParameters("/test-data/stations/defaultGeometry/measurements.xsd", parameters, measurementsSchema);
            // create station feature type
            addFeatureType(namespacePrefix, String.format("%s_%s", stationsFeatureType, gmlPrefix), stationsMappings.getAbsolutePath(), stationsProperties.getAbsolutePath(), stationsSchema.getAbsolutePath(), measurementsSchema.getAbsolutePath());
        }

        @Override
        protected void addMeasurementFeatureType(String namespacePrefix, String gmlPrefix, String mappingsName, String mappingsPath, Map<String, String> parameters) {
            // create root directory
            File gmlDirectory = getDirectoryForGmlPrefix(gmlPrefix);
            gmlDirectory.mkdirs();
            // add the necessary files
            File measurementsMappings = new File(gmlDirectory, String.format("%s_%s.xml", mappingsName, gmlPrefix));
            File measurementsProperties = new File(gmlDirectory, String.format("measurements_%s.properties", gmlPrefix));
            File measurementsSchema = new File(gmlDirectory, String.format("measurements_%s.xsd", gmlPrefix));
            // perform the parameterization
            StationsMockData.substituteParameters(("/test-data/stations/" + mappingsPath), parameters, measurementsMappings);
            StationsMockData.substituteParameters("/test-data/stations/defaultGeometry/measurements.properties", parameters, measurementsProperties);
            StationsMockData.substituteParameters("/test-data/stations/defaultGeometry/measurements.xsd", parameters, measurementsSchema);
            // create measurements feature type
            addFeatureType(namespacePrefix, String.format("Measurement_%s", gmlPrefix), measurementsMappings.getAbsolutePath(), measurementsProperties.getAbsolutePath(), measurementsSchema.getAbsolutePath());
        }
    }

    @Test
    public void testNestedBBOXFilterEncoding() throws IOException, FilterToSQLException {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        BBOX bbox = ff.bbox(nestedGeom, (-4), 2.5, 0, 4, "EPSG:4326");
        checkPostPreFilterSplitting(bbox);
        checkFilterEncoding(bbox);
        checkFeatures(bbox, "2");
    }

    @Test
    public void testNestedContainsFilterEncoding() throws IOException, FilterToSQLException, ParseException {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        Polygon contained = ((Polygon) (wktReader.read("POLYGON((-1.5 -1.5, -1.5 1.5, 0 1.5, 0 -1.5, -1.5 -1.5))")));
        Contains contains = ff.contains(nestedGeom, ff.literal(contained));
        checkPostPreFilterSplitting(contains);
        checkFilterEncoding(contains);
        checkFeatures(contains, "1");
    }

    @Test
    public void testNestedTouchesFilterEncoding() throws IOException, FilterToSQLException, ParseException {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        Polygon touching = ((Polygon) (wktReader.read("POLYGON((-5 -2, -5 0, -4 0, -4 -2, -5 -2))")));
        Touches touches = ff.touches(nestedGeom, ff.literal(touching));
        checkPostPreFilterSplitting(touches);
        checkFilterEncoding(touches);
        checkFeatures(touches, "3");
    }

    @Test
    public void testNestedIntersectsFilterEncoding() throws IOException, FilterToSQLException, ParseException {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        Polygon intersecting = ((Polygon) (wktReader.read("POLYGON((0 0, -4 4, 0 4, 0 0))")));
        Intersects intersects = ff.intersects(nestedGeom, ff.literal(intersecting));
        checkPostPreFilterSplitting(intersects);
        checkFilterEncoding(intersects);
        checkFeatures(intersects, "1", "2");
    }

    @Test
    public void testNestedOverlapsFilterEncoding() throws IOException, FilterToSQLException, ParseException {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        Polygon intersecting = ((Polygon) (wktReader.read("POLYGON((-4 3, -2 4.5, -2.5 2, -3.5 2, -4 3))")));
        Overlaps overlaps = ff.overlaps(nestedGeom, ff.literal(intersecting));
        checkPostPreFilterSplitting(overlaps);
        checkFilterEncoding(overlaps);
        checkFeatures(overlaps, "2");
    }

    @Test
    public void testNestedWithinFilterEncoding() throws Exception {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        Polygon container = ((Polygon) (wktReader.read("POLYGON((-4 -3, -4 3, -1 3, -1 -3, -4 -3))")));
        Within within = ff.within(nestedGeom, ff.literal(container));
        checkPostPreFilterSplitting(within);
        checkFilterEncoding(within);
        checkFeatures(within, "2", "3");
    }

    @Test
    public void testNestedCrossesFilterEncoding() throws Exception {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        LineString container = ((LineString) (wktReader.read("LINESTRING(-5.5 -5, -5 -3, -3 -2)")));
        Crosses crosses = ff.crosses(ff.literal(container), nestedGeom);
        checkPostPreFilterSplitting(crosses);
        checkFilterEncoding(crosses);
        checkFeatures(crosses, "3");
    }

    @Test
    public void testNestedDisjointFilterEncoding() throws Exception {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        Polygon poly = ((Polygon) (wktReader.read("POLYGON((-5 -4, -5 -2, -3 -2, -3 -4, -5 -4))")));
        Disjoint disjoint = ff.disjoint(ff.literal(poly), nestedGeom);
        checkPostPreFilterSplitting(disjoint);
        checkFilterEncoding(disjoint);
        checkFeatures(disjoint, "1", "2");
    }

    @Test
    public void testNestedEqualsFilterEncoding() throws Exception {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        Polygon st1 = ((Polygon) (wktReader.read("POLYGON((-2 2, 0 2, 0 -2, -2 -2, -2 2))")));
        Equals equal = ff.equal(ff.literal(st1), nestedGeom);
        checkPostPreFilterSplitting(equal);
        checkFilterEncoding(equal);
        checkFeatures(equal, "1");
    }

    @Test
    public void testNestedBeyondFilterEncoding() throws Exception {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        String units = crs.getCoordinateSystem().getAxis(0).getUnit().toString();
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        Polygon beyondSt2 = ((Polygon) (wktReader.read("POLYGON((-5 -4, -5 -2.5, -2 -2.5, -2 -4, -5 -4))")));
        Beyond beyond = ff.beyond(nestedGeom, ff.literal(beyondSt2), 1.0, units);
        checkPostPreFilterSplitting(beyond);
        checkFilterEncoding(beyond);
        checkFeatures(beyond, "1", "2");
    }

    @Test
    public void testNestedDWithinFilterEncoding() throws Exception {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        String units = crs.getCoordinateSystem().getAxis(0).getUnit().toString();
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NESTED_GEOM);
        Polygon dwithinSt2 = ((Polygon) (wktReader.read("POLYGON((-5 -4, -5 -2.5, -2 -2.5, -2 -4, -5 -4))")));
        DWithin dwithin = ff.dwithin(nestedGeom, ff.literal(dwithinSt2), 1.0, units);
        checkPostPreFilterSplitting(dwithin);
        checkFilterEncoding(dwithin);
        checkFeatures(dwithin, "3");
    }

    @Test
    public void testNesteGeometryFilterOnNonExistentProperty() throws Exception {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_NONEXISTENT_NESTED_GEOM);
        Polygon intersecting = ((Polygon) (wktReader.read("POLYGON((0 0, -4 4, 0 4, 0 0))")));
        Intersects intersects = ff.intersects(nestedGeom, ff.literal(intersecting));
        try {
            checkPostPreFilterSplitting(intersects);
            Assert.fail("Expected IllegalArgumentException to be thrown, but none was thrown instead");
        } catch (IllegalArgumentException iae) {
            String errorMessage = iae.getMessage();
            Assert.assertTrue(errorMessage.contains("not_there_geometry"));
            Assert.assertTrue(errorMessage.contains("not found in type"));
        } catch (Exception other) {
            Assert.fail((("Expected IllegalArgumentException to be thrown, but " + (other.getClass().getName())) + " was thrown instead"));
        }
    }

    @Test
    public void testNesteGeometryFilterOnNonGeometryProperty() throws Exception {
        // make sure nested filters encoding is enabled, otherwise skip test
        Assume.assumeTrue(shouldTestNestedFiltersEncoding(rootMapping));
        PropertyName nestedGeom = ff.property(NestedGeometryFilterEncodingTest.STATION_WRONG_NESTED_GEOM);
        BBOX bbox = ff.bbox(nestedGeom, (-4), 2.5, 0, 4, "EPSG:4326");
        try {
            checkPostPreFilterSplitting(bbox);
            Assert.fail("Expected IllegalArgumentException to be thrown, but none was thrown instead");
        } catch (IllegalArgumentException iae) {
            String errorMessage = iae.getMessage();
            Assert.assertTrue(errorMessage.contains("code"));
            Assert.assertTrue(errorMessage.contains("AttributeDescriptor"));
            Assert.assertTrue(errorMessage.contains("should be of type"));
            Assert.assertTrue(errorMessage.contains("GeometryDescriptor"));
        } catch (Exception other) {
            Assert.fail((("Expected IllegalArgumentException to be thrown, but " + (other.getClass().getName())) + " was thrown instead"));
        }
    }
}

