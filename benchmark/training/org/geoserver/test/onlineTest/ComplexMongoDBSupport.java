/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test.onlineTest;


import com.mongodb.MongoClient;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.image.test.ImageAssert;
import org.geotools.util.URLs;
import org.geotools.util.logging.Logging;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * Support for integration tests between MongoDB and App-schema. This test are integration tests
 * hence they require a MongoDB instance. If no fixture file for MongoDB exists these tests will be
 * skipped.
 */
public abstract class ComplexMongoDBSupport extends GeoServerSystemTestSupport {
    private static final Logger LOGGER = Logging.getLogger(ComplexMongoDBSupport.class);

    private static final Path ROOT_DIRECTORY = ComplexMongoDBSupport.createTempDir();

    private static File APP_SCHEMA_MAPPINGS;

    private static final String STATIONS_DATA_BASE_NAME = UUID.randomUUID().toString();

    private static final String STATIONS_COLLECTION_NAME = "stations";

    private static MongoClient MONGO_CLIENT;

    // xpath engines used to check WFS responses
    private XpathEngine WFS11_XPATH_ENGINE;

    private XpathEngine WFS20_XPATH_ENGINE;

    @Test
    public void testGetStationFeatures() throws Exception {
        Document document = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=st:StationFeature");
        // assert that the response contains station 1 measurements
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 1", "station1@mail.com", "wind", "km/h", "1482146833", "155.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 1", "station1@mail.com", "temp", "c", "1482146800", "20.0");
        // assert that the response contains station 2 measurements
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "pression", "pa", "1482147051", "1015.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "pression", "pa", "1482147026", "1019.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "wind", "km/h", "1482146964", "80.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "temp", "c", "1482146911", "35.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "temp", "c", "1482146935", "25.0");
    }

    @Test
    public void testGetStationFeaturesWithFilter() throws Exception {
        String postContent = ComplexMongoDBSupport.readResourceContent("/querys/postQuery1.xml");
        Document document = postAsDOM("wfs?request=GetFeature&version=1.1.0&typename=st:StationFeature", postContent);
        // assert that the response contains station 2 measurements
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "pression", "pa", "1482147051", "1015.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "pression", "pa", "1482147026", "1019.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "wind", "km/h", "1482146964", "80.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "temp", "c", "1482146911", "35.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "temp", "c", "1482146935", "25.0");
    }

    @Test
    public void testStationsWmsGetMap() throws Exception {
        // execute the WMS GetMap request
        MockHttpServletResponse result = getAsServletResponse(("wms?SERVICE=WMS&VERSION=1.1.1" + (("&REQUEST=GetMap&FORMAT=image/png&TRANSPARENT=true&STYLES&LAYERS=st:StationFeature" + "&SRS=EPSG:4326&WIDTH=349&HEIGHT=768") + "&BBOX=96.251220703125,-57.81005859375,103.919677734375,-40.93505859375")));
        Assert.assertThat(result.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(result.getContentType(), CoreMatchers.is("image/png"));
        // check that we got the expected image back
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(getBinary(result)));
        ImageAssert.assertEquals(URLs.urlToFile(getClass().getResource("/results/result1.png")), image, 10);
    }

    @Test
    public void testStationsWmsGetFeatureInfo() throws Exception {
        // execute the WMS GetFeatureInfo request
        Document document = getAsDOM(("wms?SERVICE=WMS&VERSION=1.1.1" + ((("&REQUEST=GetFeatureInfo&FORMAT=image/png&TRANSPARENT=true&QUERY_LAYERS=st:StationFeature" + "&STYLES&LAYERS=st:StationFeature&INFO_FORMAT=text/xml; subtype=gml/3.1.1") + "&FEATURE_COUNT=50&X=50&Y=50&SRS=EPSG:4326&WIDTH=101&HEIGHT=101") + "&BBOX=91.23046875,-58.623046874999986,108.984375,-40.869140624999986")));
        // assert that the response contains station 2 measurements
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "pression", "pa", "1482147051", "1015.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "pression", "pa", "1482147026", "1019.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "wind", "km/h", "1482146964", "80.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "temp", "c", "1482146911", "35.0");
        checkMeasurementExists(WFS11_XPATH_ENGINE, document, "station 2", "station2@mail.com", "temp", "c", "1482146935", "25.0");
    }
}

