/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_3;


import java.util.HashMap;
import java.util.Map;
import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.geoserver.wms.WMSCascadeTestSupport;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


@RunWith(Parameterized.class)
public class WMSCascadeTest extends WMSCascadeTestSupport {
    private final boolean aphEnabled;

    public WMSCascadeTest(boolean aphEnabled) {
        this.aphEnabled = aphEnabled;
    }

    @Test
    public void testCascadeGetMapOnto13() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((("wms?bbox=-90,-180,90,180" + "&styles=&layers=") + (WMSCascadeTestSupport.WORLD4326_130)) + "&Format=image/png&request=GetMap&version=1.3.0&service=wms") + "&width=180&height=90&crs=EPSG:4326"));
        // we'll get a service exception if the requests are not the ones expected
        checkImage(response, "image/png", 180, 90);
    }

    @Test
    public void testCascadeGetMapOnto11() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((("wms?bbox=-90,-180,90,180" + "&styles=&layers=") + (WMSCascadeTestSupport.WORLD4326_110)) + "&Format=image/png&request=GetMap&version=1.3.0&service=wms") + "&width=180&height=90&crs=EPSG:4326"));
        // we'll get a service exception if the requests are not the ones expected
        checkImage(response, "image/png", 180, 90);
    }

    @Test
    public void testCascadeCapabilitiesClientNoGetFeatureInfo() throws Exception {
        Document dom = getAsDOM("wms?request=GetCapabilities&version=1.3.0&service=wms");
        // print(dom);
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("wms", "http://www.opengis.net/wms");
        namespaces.put("link", "http://www.w3.org/1999/xlink");
        namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        NamespaceContext newNsCtxt = new SimpleNamespaceContext(namespaces);
        xpath.setNamespaceContext(newNsCtxt);
        xpath.evaluate((("//wms:Layer[name='" + (WMSCascadeTestSupport.WORLD4326_110_NFI)) + "']"), dom);
    }

    @Test
    public void testGetFeatureInfoReprojection() throws Exception {
        // do the get feature request using EPSG:4326
        String url = ((((("wms?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetFeatureInfo&FORMAT=image/png&TRANSPARENT=true" + "&QUERY_LAYERS=") + (WMSCascadeTestSupport.WORLD4326_130)) + "&STYLES&LAYERS=") + (WMSCascadeTestSupport.WORLD4326_130)) + "&INFO_FORMAT=text/xml; subtype=gml/3.1.1") + "&FEATURE_COUNT=50&X=50&Y=50&SRS=EPSG:4326&WIDTH=101&HEIGHT=101&BBOX=-103.829117187,44.3898919295,-103.804563429,44.4069939679";
        Document result = getAsDOM(url);
        // setup XPATH engine namespaces
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("gml", "http://www.opengis.net/gml");
        namespaces.put("gs", "http://geoserver.org");
        namespaces.put("ogc", "http://www.opengis.net/ogc");
        namespaces.put("ows", "http://www.opengis.net/ows");
        namespaces.put("wfs", "http://www.opengis.net/wfs");
        namespaces.put("xlink", "http://www.w3.org/1999/xlink");
        namespaces.put("xs", "http://www.w3.org/2001/XMLSchema");
        namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xpath.setNamespaceContext(new SimpleNamespaceContext(namespaces));
        // check the response content, the features should have been reproject from EPSG:3857 to
        // EPSG:4326
        String srs = xpath.evaluate(("//wfs:FeatureCollection/gml:featureMembers/" + "gs:world4326_130[@gml:id='bugsites.55']/gs:the_geom/gml:Point/@srsName"), result);
        Assert.assertThat(srs, CoreMatchers.notNullValue());
        Assert.assertThat(srs.contains("4326"), CoreMatchers.is(true));
        String rawCoordinates = xpath.evaluate(("//wfs:FeatureCollection/gml:featureMembers/" + "gs:world4326_130[@gml:id='bugsites.55']/gs:the_geom/gml:Point/gml:pos/text()"), result);
        Assert.assertThat(rawCoordinates, CoreMatchers.notNullValue());
        String[] coordinates = rawCoordinates.split(" ");
        Assert.assertThat(coordinates.length, CoreMatchers.is(2));
        WMSTestSupport.checkNumberSimilar(coordinates[0], 44.39832008, 1.0E-4);
        WMSTestSupport.checkNumberSimilar(coordinates[1], (-103.81711048), 1.0E-4);
        // deactivate features reprojection
        WMSInfo wms = getGeoServer().getService(WMSInfo.class);
        wms.setFeaturesReprojectionDisabled(true);
        getGeoServer().save(wms);
        // execute the get feature info request
        result = getAsDOM(url);
        srs = xpath.evaluate(("//wfs:FeatureCollection/gml:featureMembers/" + "gs:world4326_130[@gml:id='bugsites.55']/gs:the_geom/gml:Point/@srsName"), result);
        Assert.assertThat(srs, CoreMatchers.notNullValue());
        Assert.assertThat(srs.contains("3857"), CoreMatchers.is(true));
        rawCoordinates = xpath.evaluate(("//wfs:FeatureCollection/gml:featureMembers/" + "gs:world4326_130[@gml:id='bugsites.55']/gs:the_geom/gml:Point/gml:pos/text()"), result);
        Assert.assertThat(rawCoordinates, CoreMatchers.notNullValue());
        coordinates = rawCoordinates.split(" ");
        Assert.assertThat(coordinates.length, CoreMatchers.is(2));
        WMSTestSupport.checkNumberSimilar(coordinates[0], (-1.1556867874E7), 1.0E-4);
        WMSTestSupport.checkNumberSimilar(coordinates[1], 5527291.47718493, 1.0E-4);
    }
}

