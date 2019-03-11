/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test.onlineTest;


import SchemaCache.PROVIDED_CACHE_LOCATION_KEY;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import javax.imageio.ImageIO;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.data.solr.TestsSolrUtils;
import org.geotools.image.test.ImageAssert;
import org.geotools.util.URLs;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * This class contains the integration tests (online tests) for the integration between App-Schema
 * and Apache Solr. To activate this tests a fixture file needs to be created in the user home, this
 * follows the usual GeoServer conventions for fixture files. Read the README.rst file for more
 * instructions.
 */
public final class ComplexSolrTest extends GeoServerSystemTestSupport {
    // xpath engines used to check WFS responses
    private XpathEngine WFS11_XPATH_ENGINE;

    private XpathEngine WFS20_XPATH_ENGINE;

    // test root directory
    private static final File TESTS_ROOT_DIR = TestsSolrUtils.createTempDirectory("complex-solr");

    static {
        // create and set App-Schema cache directory
        System.setProperty(PROVIDED_CACHE_LOCATION_KEY, new File(ComplexSolrTest.TESTS_ROOT_DIR, "app-schema-cache").getAbsolutePath());
    }

    private static String solrUrl;

    // HTTP Apache Solr client
    private static HttpSolrClient solrClient;

    @Test
    public void testGetStationFeatures() throws Exception {
        // perform a WFS GetFeature request returning all the available complex features
        Document document = getAsDOM("wfs?request=GetFeature&version=1.1.0&srsName=EPSG:4326&typename=st:Station");
        // check that we got the expected stations
        checkStationsNumber(2, WFS11_XPATH_ENGINE, document);
        checkStationData(7, "Bologna", "POINT (11.34 44.5)", WFS11_XPATH_ENGINE, document);
        checkStationData(13, "Alessandria", "POINT (8.63 44.92)", WFS11_XPATH_ENGINE, document);
    }

    @Test
    public void testFilterStationFeatures() throws Exception {
        // perform a WFS GetFeature POST request matching only a single station
        String postContent = ComplexSolrTest.readResourceContent("/querys/postQuery1.xml");
        Document document = postAsDOM("wfs?request=GetFeature&version=1.1.0&srsName=EPSG:4326&typename=st:Station", postContent);
        // check that we got the expected stations
        checkStationsNumber(1, WFS11_XPATH_ENGINE, document);
        checkStationData(7, "Bologna", "POINT (11.34 44.5)", WFS11_XPATH_ENGINE, document);
        checkNoStationId(13, WFS11_XPATH_ENGINE, document);
    }

    @Test
    public void testStationsWmsGetMap() throws Exception {
        // execute the WMS GetMap request that should render all stations
        MockHttpServletResponse result = getAsServletResponse(("wms?SERVICE=WMS&VERSION=1.1.1" + (("&REQUEST=GetMap&FORMAT=image/png&TRANSPARENT=true&STYLES&LAYERS=st:Station" + "&SRS=EPSG:4326&WIDTH=768&HEIGHT=768") + "&BBOX=5,40,15,50")));
        Assert.assertThat(result.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(result.getContentType(), CoreMatchers.is("image/png"));
        // check that we got the expected image back
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(getBinary(result)));
        ImageAssert.assertEquals(URLs.urlToFile(getClass().getResource("/results/wms_result.png")), image, 10);
    }

    @Test
    public void testStationsWmsGetFeatureInfo() throws Exception {
        // execute a WMS GetFeatureInfo request that should hit the Alessandria station
        Document document = getAsDOM(("wms?SERVICE=WMS&VERSION=1.1.1" + ((("&REQUEST=GetFeatureInfo&FORMAT=image/png&TRANSPARENT=true&QUERY_LAYERS=st:Station" + "&STYLES&LAYERS=st:Station&INFO_FORMAT=text/xml; subtype=gml/3.1.1") + "&FEATURE_COUNT=50&X=278&Y=390&SRS=EPSG:4326&WIDTH=768&HEIGHT=768") + "&BBOX=5,40,15,50")));
        checkStationData(13, "Alessandria", "POINT (8.63 44.92)", WFS11_XPATH_ENGINE, document);
        checkNoStationId(7, WFS11_XPATH_ENGINE, document);
        // execute a WMS GetFeatureInfo request that should hit the Bologna station
        document = getAsDOM(("wms?SERVICE=WMS&VERSION=1.1.1" + ((("&REQUEST=GetFeatureInfo&FORMAT=image/png&TRANSPARENT=true&QUERY_LAYERS=st:Station" + "&STYLES&LAYERS=st:Station&INFO_FORMAT=text/xml; subtype=gml/3.1.1") + "&FEATURE_COUNT=50&X=486&Y=422&SRS=EPSG:4326&WIDTH=768&HEIGHT=768") + "&BBOX=5,40,15,50")));
        checkStationData(7, "Bologna", "POINT (11.34 44.5)", WFS11_XPATH_ENGINE, document);
        checkNoStationId(13, WFS11_XPATH_ENGINE, document);
    }
}

