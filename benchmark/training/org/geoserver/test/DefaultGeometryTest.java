package org.geoserver.test;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.custommonkey.xmlunit.XpathEngine;
import org.junit.Test;


public class DefaultGeometryTest extends AbstractAppSchemaTestSupport {
    private static final String GML31_PREFIX = "gml31";

    private static final String GML32_PREFIX = "gml32";

    private static final String XPATH_BASE_GML31 = "/wfs:FeatureCollection/gml:featureMember";

    private static final String XPATH_BASE_GML32 = "/wfs:FeatureCollection/wfs:member";

    private static final String STATION_FEATURE = "st_${GML_PREFIX}:Station_${GML_PREFIX}";

    private static final String STATION_WITH_MEASUREMENTS_FEATURE = "st_${GML_PREFIX}:StationWithMeasurements_${GML_PREFIX}";

    private static final String XPATH_STATION = "/st_${GML_PREFIX}:Station_${GML_PREFIX}";

    private static final String XPATH_STATION_FAKE_GEOM = ((DefaultGeometryTest.XPATH_STATION) + "/st_${GML_PREFIX}:") + (DEFAULT_GEOMETRY_LOCAL_NAME);

    private static final String XPATH_STATION_GEOM = (DefaultGeometryTest.XPATH_STATION) + "/st_${GML_PREFIX}:location/st_${GML_PREFIX}:position/gml:Point";

    private static final String XPATH_STATION_WITH_MEASUREMENTS = "/st_${GML_PREFIX}:StationWithMeasurements_${GML_PREFIX}";

    private static final String XPATH_STATION_WITH_MEASUREMENTS_FAKE_GEOM = ((DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS) + "/st_${GML_PREFIX}:") + (DEFAULT_GEOMETRY_LOCAL_NAME);

    private static final String XPATH_STATION_WITH_MEASUREMENTS_GEOM = (DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS) + "/st_${GML_PREFIX}:measurements/ms_${GML_PREFIX}:Measurement_${GML_PREFIX}/ms_${GML_PREFIX}:sampledArea/ms_${GML_PREFIX}:SampledArea/ms_${GML_PREFIX}:geometry";

    // xpath engines used to check WFS responses
    private XpathEngine WFS11_XPATH_ENGINE;

    private XpathEngine WFS20_XPATH_ENGINE;

    private static final class MockData extends StationsMockData {
        @Override
        public void addContent() {
            // add GML 3.1 namespaces
            putNamespace(StationsMockData.STATIONS_PREFIX_GML31, StationsMockData.STATIONS_URI_GML31);
            putNamespace(StationsMockData.MEASUREMENTS_PREFIX_GML31, StationsMockData.MEASUREMENTS_URI_GML31);
            // add GML 3.2 namespaces
            putNamespace(StationsMockData.STATIONS_PREFIX_GML32, StationsMockData.STATIONS_URI_GML32);
            putNamespace(StationsMockData.MEASUREMENTS_PREFIX_GML32, StationsMockData.MEASUREMENTS_URI_GML32);
            // add GML 3.1 feature types
            Map<String, String> gml31Parameters = new HashMap<>();
            gml31Parameters.put("GML_PREFIX", "gml31");
            gml31Parameters.put("GML_NAMESPACE", "http://www.opengis.net/gml");
            gml31Parameters.put("GML_LOCATION", "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");
            addStationFeatureType(StationsMockData.STATIONS_PREFIX_GML31, "gml31", "Station", "stations", "defaultGeometry/stations1.xml", gml31Parameters);
            addStationFeatureType(StationsMockData.STATIONS_PREFIX_GML31, "gml31", "StationWithMeasurements", "stations", "defaultGeometry/stations2.xml", "measurements", "defaultGeometry/measurements.xml", gml31Parameters);
            addMeasurementFeatureType(StationsMockData.MEASUREMENTS_PREFIX_GML31, "gml31", "measurements", "defaultGeometry/measurements.xml", gml31Parameters);
            // add GML 3.2 feature types
            Map<String, String> gml32Parameters = new HashMap<>();
            gml32Parameters.put("GML_PREFIX", "gml32");
            gml32Parameters.put("GML_NAMESPACE", "http://www.opengis.net/gml/3.2");
            gml32Parameters.put("GML_LOCATION", "http://schemas.opengis.net/gml/3.2.1/gml.xsd");
            addStationFeatureType(StationsMockData.STATIONS_PREFIX_GML32, "gml32", "Station", "stations", "defaultGeometry/stations1.xml", gml32Parameters);
            addStationFeatureType(StationsMockData.STATIONS_PREFIX_GML32, "gml32", "StationWithMeasurements", "stations", "defaultGeometry/stations2.xml", "measurements", "defaultGeometry/measurements.xml", gml32Parameters);
            addMeasurementFeatureType(StationsMockData.MEASUREMENTS_PREFIX_GML32, "gml32", "measurements", "defaultGeometry/measurements.xml", gml32Parameters);
        }

        protected void addStationFeatureType(String namespacePrefix, String gmlPrefix, String featureType, String mappingsName, String mappingsPath, Map<String, String> parameters) {
            // create root directory
            File gmlDirectory = getDirectoryForGmlPrefix(gmlPrefix);
            gmlDirectory.mkdirs();
            // add the necessary files
            File stationsMappings = new File(gmlDirectory, String.format("%s_%s.xml", mappingsName, gmlPrefix));
            File stationsProperties = new File(gmlDirectory, String.format("stations_%s.properties", gmlPrefix));
            File stationsSchema = new File(gmlDirectory, String.format("stations_%s.xsd", gmlPrefix));
            // perform the parameterization
            StationsMockData.substituteParameters(("/test-data/stations/" + mappingsPath), parameters, stationsMappings);
            StationsMockData.substituteParameters("/test-data/stations/defaultGeometry/stations.properties", parameters, stationsProperties);
            StationsMockData.substituteParameters("/test-data/stations/defaultGeometry/stations.xsd", parameters, stationsSchema);
            // create station feature type
            addFeatureType(namespacePrefix, String.format("%s_%s", featureType, gmlPrefix), stationsMappings.getAbsolutePath(), stationsProperties.getAbsolutePath(), stationsSchema.getAbsolutePath());
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
    public void testWfs11GetFeatureDefaultGeometry() throws Exception {
        testWfsGetFeature("1.1.0", DefaultGeometryTest.STATION_FEATURE, DefaultGeometryTest.XPATH_STATION, DefaultGeometryTest.XPATH_STATION_FAKE_GEOM, DefaultGeometryTest.XPATH_STATION_GEOM);
        testWfsGetFeature("1.1.0", DefaultGeometryTest.STATION_WITH_MEASUREMENTS_FEATURE, DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS, DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS_FAKE_GEOM, DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS_GEOM);
    }

    @Test
    public void testWfs20GetFeatureDefaultGeometry() throws Exception {
        testWfsGetFeature("2.0", DefaultGeometryTest.STATION_FEATURE, DefaultGeometryTest.XPATH_STATION, DefaultGeometryTest.XPATH_STATION_FAKE_GEOM, DefaultGeometryTest.XPATH_STATION_GEOM);
        testWfsGetFeature("2.0", DefaultGeometryTest.STATION_WITH_MEASUREMENTS_FEATURE, DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS, DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS_FAKE_GEOM, DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS_GEOM);
    }

    @Test
    public void testWmsGetMapDefaultGeometry() throws IOException {
        String layer = DefaultGeometryTest.STATION_FEATURE.replace("${GML_PREFIX}", DefaultGeometryTest.GML31_PREFIX);
        try (InputStream is = getBinary(buildGetMapUrl(layer, "Default_Point"))) {
            BufferedImage imageBuffer = ImageIO.read(is);
            assertNotBlank("app-schema test getmap nested default geom", imageBuffer, Color.WHITE);
        }
        layer = DefaultGeometryTest.STATION_FEATURE.replace("${GML_PREFIX}", DefaultGeometryTest.GML32_PREFIX);
        try (InputStream is = getBinary(buildGetMapUrl("st_gml32:Station_gml32", "Default_Point"))) {
            BufferedImage imageBuffer = ImageIO.read(is);
            assertNotBlank("app-schema test getmap nested default geom", imageBuffer, Color.WHITE);
        }
    }

    @Test
    public void testWmsGetMapDefaultGeometryInChainedFeature() throws IOException {
        String layer = DefaultGeometryTest.STATION_WITH_MEASUREMENTS_FEATURE.replace("${GML_PREFIX}", DefaultGeometryTest.GML31_PREFIX);
        try (InputStream is = getBinary(buildGetMapUrl(layer, "Default_Polygon"))) {
            BufferedImage imageBuffer = ImageIO.read(is);
            assertNotBlank("app-schema test getmap nested default geom feature chaining", imageBuffer, Color.WHITE);
        }
        layer = DefaultGeometryTest.STATION_WITH_MEASUREMENTS_FEATURE.replace("${GML_PREFIX}", DefaultGeometryTest.GML32_PREFIX);
        try (InputStream is = getBinary(buildGetMapUrl(layer, "Default_Polygon"))) {
            BufferedImage imageBuffer = ImageIO.read(is);
            assertNotBlank("app-schema test getmap nested default geom feature chaining", imageBuffer, Color.WHITE);
        }
    }

    @Test
    public void testWmsGetFeatureInfoDefaultGeometry() throws Exception {
        testWmsGetFeatureInfo(DefaultGeometryTest.STATION_FEATURE, "Default_Point", 89, 114, DefaultGeometryTest.XPATH_STATION, DefaultGeometryTest.XPATH_STATION_FAKE_GEOM, DefaultGeometryTest.XPATH_STATION_GEOM);
    }

    @Test
    public void testWmsGetFeatureInfoDefaultGeometryInChainedFeature() throws Exception {
        testWmsGetFeatureInfo(DefaultGeometryTest.STATION_WITH_MEASUREMENTS_FEATURE, "Default_Polygon", 125, 150, DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS, DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS_FAKE_GEOM, DefaultGeometryTest.XPATH_STATION_WITH_MEASUREMENTS_GEOM);
    }
}

