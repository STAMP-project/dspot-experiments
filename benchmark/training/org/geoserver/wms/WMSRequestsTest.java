/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;


import MockData.FORESTS;
import MockData.LAKES;
import MockData.TASMANIA_DEM;
import ScaleComputationMethod.Accurate;
import java.net.URL;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class WMSRequestsTest extends WMSTestSupport {
    @Test
    public void testGetGetMapUrlAllWithDimensions() {
        GetMapRequest request = initGetMapRequest(TASMANIA_DEM);
        request.getRawKvp().put("time", "2017-04-07T19:56:00.000Z");
        request.getRawKvp().put("elevation", "1013.2");
        request.getRawKvp().put("dim_my_dimension", "010");
        String url = WMSRequestsTest.getGetMapUrl(request);
        Assert.assertThat(url, CoreMatchers.containsString("&time=2017-04-07T19:56:00.000Z&"));
        Assert.assertThat(url, CoreMatchers.containsString("&elevation=1013.2&"));
        Assert.assertThat(url, CoreMatchers.containsString("&dim_my_dimension=010&"));
    }

    @Test
    public void testGetGetMapUrlWithDimensions() {
        GetMapRequest request = initGetMapRequest(TASMANIA_DEM);
        request.getRawKvp().put("time", "2017-04-07T19:56:00.000Z");
        request.getRawKvp().put("elevation", "1013.2");
        request.getRawKvp().put("dim_my_dimension", "010");
        List<String> urls = WMSRequestsTest.getGetMapUrls(request);
        Assert.assertEquals(1, urls.size());
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&time=2017-04-07T19:56:00.000Z&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&elevation=1013.2&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&dim_my_dimension=010&"));
    }

    @Test
    public void testGetGetMapUrlWithSingleLayer() throws Exception {
        GetMapRequest request = initGetMapRequest(LAKES);
        request.getRawKvp().put("cql_filter", "fid='123'");
        request.setExceptions("INIMAGE");
        request.setRemoteOwsType("WFS");
        request.setRemoteOwsURL(new URL("https://foo.com/geoserver/wfs"));
        request.setScaleMethod(Accurate);
        List<String> urls = WMSRequestsTest.getGetMapUrls(request);
        Assert.assertEquals(1, urls.size());
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&cql_filter=fid='123'&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&remote_ows_type=WFS&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&remote_ows_url=https://foo.com/geoserver/wfs&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&scalemethod=Accurate"));
    }

    @Test
    public void testGetGetMapUrlWithMultipleLayers() {
        GetMapRequest request = initGetMapRequest(LAKES, TASMANIA_DEM);
        request.getRawKvp().put("cql_filter", "fid='123';INCLUDE");
        request.getRawKvp().put("bgcolor", "0x808080");
        request.setStyleFormat("ysld");
        request.setSldBody("foo");
        List<String> urls = WMSRequestsTest.getGetMapUrls(request);
        Assert.assertEquals(2, urls.size());
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&cql_filter=fid='123'&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&bgcolor=0x808080&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&style_format=ysld&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&sld_body=foo"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&cql_filter=INCLUDE&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&bgcolor=0x808080&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&style_format=ysld&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&sld_body=foo"));
    }

    @Test
    public void testGetGetMapUrlWithSingleLayerGroup() throws Exception {
        GetMapRequest request = initGetMapRequest(LAKES, FORESTS);
        request.getRawKvp().put("layers", WMSTestSupport.NATURE_GROUP);
        request.getRawKvp().put("cql_filter", "name LIKE 'BLUE%'");
        request.getRawKvp().put("sortby", "name A");
        request.setStartIndex(25);
        request.setMaxFeatures(50);
        request.setStyleVersion("1.1.0");
        request.setSld(new URL("http://localhost/test.sld"));
        request.setValidateSchema(true);
        List<String> urls = WMSRequestsTest.getGetMapUrls(request);
        Assert.assertEquals(2, urls.size());
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&cql_filter=name LIKE 'BLUE%'&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&sortby=name A&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&startindex=25&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&maxfeatures=50&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&style_version=1.1.0&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&validateschema=true&"));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&sld=http://localhost/test.sld"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&cql_filter=name LIKE 'BLUE%'&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&sortby=name A&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&startindex=25&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&maxfeatures=50&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&style_version=1.1.0&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&validateschema=true&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&sld=http://localhost/test.sld"));
    }

    @Test
    public void testGetGetMapUrlWithLayerGroupAndLayers() {
        GetMapRequest request = initGetMapRequest(LAKES, LAKES, FORESTS, TASMANIA_DEM);
        request.getRawKvp().put("layers", (((((request.getLayers().get(0).getName()) + ',') + (WMSTestSupport.NATURE_GROUP)) + ',') + (request.getLayers().get(3).getName())));
        request.getRawKvp().put("cql_filter", "fid='123';name LIKE 'BLUE%';INCLUDE");
        request.getRawKvp().put("interpolations", ",,nearest neighbor");
        request.getRawKvp().put("sortby", "(fid)(name D)()");
        List<String> urls = WMSRequestsTest.getGetMapUrls(request);
        Assert.assertEquals(4, urls.size());
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&cql_filter=fid='123'&"));
        Assert.assertThat(urls.get(0), CoreMatchers.not(CoreMatchers.containsString("&interpolations=")));
        Assert.assertThat(urls.get(0), CoreMatchers.containsString("&sortby=fid&"));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&cql_filter=name LIKE 'BLUE%'&"));
        Assert.assertThat(urls.get(1), CoreMatchers.not(CoreMatchers.containsString("&interpolations=")));
        Assert.assertThat(urls.get(1), CoreMatchers.containsString("&sortby=name D&"));
        Assert.assertThat(urls.get(2), CoreMatchers.containsString("&cql_filter=name LIKE 'BLUE%'&"));
        Assert.assertThat(urls.get(2), CoreMatchers.not(CoreMatchers.containsString("&interpolations=")));
        Assert.assertThat(urls.get(2), CoreMatchers.containsString("&sortby=name D&"));
        Assert.assertThat(urls.get(3), CoreMatchers.containsString("&cql_filter=INCLUDE&"));
        Assert.assertThat(urls.get(3), CoreMatchers.containsString("&interpolations=nearest neighbor&"));
        Assert.assertThat(urls.get(3), CoreMatchers.not(CoreMatchers.containsString("&sortby=")));
    }
}

