/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.json;


import CRS.AxisOrder.EAST_NORTH;
import CRS.AxisOrder.NORTH_EAST;
import JSONType.jsonp;
import java.util.Collection;
import javax.xml.namespace.QName;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.wfs.WFSInfo;
import org.geoserver.wfs.WFSTestSupport;
import org.geotools.referencing.CRS;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.mock.web.MockHttpServletResponse;

import static JSONType.CALLBACK_FUNCTION_KEY;
import static JSONType.ID_POLICY;
import static JSONType.json;
import static JSONType.jsonp;
import static JSONType.simple_json;


/**
 *
 *
 * @author carlo cancellieri - GeoSolutions
 */
public class GeoJSONTest extends WFSTestSupport {
    public static QName LINE3D = new QName(SystemTestData.CITE_URI, "Line3D", SystemTestData.CITE_PREFIX);

    public static QName POINT_LATLON = new QName(SystemTestData.CITE_URI, "PointLatLon", SystemTestData.CITE_PREFIX);

    public static QName POINT_LONLAT = new QName(SystemTestData.CITE_URI, "PointLonLat", SystemTestData.CITE_PREFIX);

    public static QName MULTI_GEOMETRIES_WITH_NULL = new QName(SystemTestData.CITE_URI, "MultiGeometriesWithNull", SystemTestData.CITE_PREFIX);

    public static QName POINT_REDUCED = new QName(SystemTestData.CITE_URI, "PointReduced", SystemTestData.CITE_PREFIX);

    @Test
    public void testFeatureBoundingDisabledCollection() throws Exception {
        /* In GML we have the option not to compute the bounds in the response,
        and by default we don't, but GeoServer can be configured to return
        the bounds, in that case it will issue a bounds query against the store,
        which might take a long time (that depends a lot on what the store can do,
        some can compute it quickly, no idea what SDE).
        For GeoJSON it seems that the "feature bounding" flag is respected
        for the single feature bounds, but not for the collection.
        Looking at the spec ( http://geojson.org/geojson-spec.html ) it seems to
        me the collection bbox is not required:
        "To include information on the coordinate range for geometries, features,
        or feature collections, a GeoJSON object may have a member named "bbox""
        disable Feature bounding
         */
        GeoServer gs = getGeoServer();
        WFSInfo wfs = getWFS();
        boolean before = wfs.isFeatureBounding();
        wfs.setFeatureBounding(false);
        try {
            gs.save(wfs);
            String out = getAsString(("wfs?request=GetFeature&version=1.0.0&typename=sf:AggregateGeoFeature&maxfeatures=3&outputformat=" + (json)));
            JSONObject rootObject = JSONObject.fromObject(out);
            JSONObject bbox = rootObject.getJSONObject("bbox");
            Assert.assertEquals(JSONNull.getInstance(), bbox);
        } finally {
            wfs.setFeatureBounding(before);
            gs.save(wfs);
        }
    }

    @Test
    public void testGet() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature&maxfeatures=1&outputformat=" + (json)));
        Assert.assertEquals("application/json", response.getContentType());
        String out = response.getContentAsString();
        JSONObject rootObject = JSONObject.fromObject(out);
        // print(rootObject);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertEquals(aFeature.getString("geometry_name"), "surfaceProperty");
        // check a timestamp exists and matches the structure of a ISO timestamp
        String timeStamp = rootObject.getString("timeStamp");
        Assert.assertNotNull(timeStamp);
        Assert.assertTrue(timeStamp.matches(("(\\d{4})-(\\d{2})-(\\d{2})" + "T(\\d{2})\\:(\\d{2})\\:(\\d{2})\\.(\\d{3})Z")));
    }

    @Test
    public void testGetSkipCounting() throws Exception {
        Catalog catalog = getCatalog();
        try {
            // skip the feature count
            FeatureTypeInfo primitive = catalog.getFeatureTypeByName(getLayerId(MockData.PRIMITIVEGEOFEATURE));
            primitive.setSkipNumberMatched(true);
            catalog.save(primitive);
            MockHttpServletResponse response = getAsServletResponse(("wfs?request=GetFeature&version=2.0.0&typename=sf:PrimitiveGeoFeature&outputformat=" + (json)));
            Assert.assertEquals("application/json", response.getContentType());
            String out = response.getContentAsString();
            JSONObject rootObject = JSONObject.fromObject(out);
            Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
            JSONArray featureCol = rootObject.getJSONArray("features");
            JSONObject aFeature = featureCol.getJSONObject(0);
            Assert.assertEquals(aFeature.getString("geometry_name"), "surfaceProperty");
        } finally {
            FeatureTypeInfo primitive = catalog.getFeatureTypeByName(getLayerId(MockData.PRIMITIVEGEOFEATURE));
            primitive.setSkipNumberMatched(false);
            catalog.save(primitive);
        }
    }

    @Test
    public void testGetSimpleJson() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature&maxfeatures=1&outputformat=" + (simple_json)), "");
        Assert.assertEquals("application/json", response.getContentType());
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        String out = response.getContentAsString();
        JSONObject rootObject = JSONObject.fromObject(out);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertEquals(aFeature.getString("geometry_name"), "surfaceProperty");
    }

    @Test
    public void testGetJsonIdPolicyTrue() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature&maxfeatures=1&outputformat=" + (simple_json)) + "&format_options=") + (ID_POLICY)) + ":true"));
        Assert.assertEquals("application/json", response.getContentType());
        String out = response.getContentAsString();
        JSONObject rootObject = JSONObject.fromObject(out);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertTrue("id", aFeature.containsKey("id"));
        Object id = aFeature.get("id");
        Assert.assertNotNull("id", id);
        Assert.assertEquals("PrimitiveGeoFeature.f001", id);
    }

    @Test
    public void testGetJsonIdPolicyFalse() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature&maxfeatures=1&outputformat=" + (simple_json)) + "&format_options=") + (ID_POLICY)) + ":false"));
        Assert.assertEquals("application/json", response.getContentType());
        String out = response.getContentAsString();
        JSONObject rootObject = JSONObject.fromObject(out);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertFalse("supress id", aFeature.containsKey("id"));
    }

    @Test
    public void testGetJsonIdPolicyAttribute() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature&maxfeatures=1&outputformat=" + (simple_json)) + "&format_options=") + (ID_POLICY)) + ":name"));
        Assert.assertEquals("application/json", response.getContentType());
        String out = response.getContentAsString();
        JSONObject rootObject = JSONObject.fromObject(out);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertTrue("id", aFeature.containsKey("id"));
        Object id = aFeature.get("id");
        Assert.assertNotNull("id", id);
        Assert.assertEquals("name-f001", id);
        JSONObject properties = aFeature.getJSONObject("properties");
        Assert.assertFalse(properties.containsKey("name"));
    }

    @Test
    public void testPost() throws Exception {
        String xml = (((((((((("<wfs:GetFeature " + ("service=\"WFS\" " + "outputFormat=\"")) + (json)) + "\" ") + "version=\"1.0.0\" ") + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"sf:PrimitiveGeoFeature\"> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
        String out = postAsServletResponse("wfs", xml).getContentAsString();
        JSONObject rootObject = JSONObject.fromObject(out);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertEquals(aFeature.getString("geometry_name"), "surfaceProperty");
    }

    @Test
    public void testGeometryCollection() throws Exception {
        String out = getAsString(("wfs?request=GetFeature&version=1.0.0&typename=sf:AggregateGeoFeature&maxfeatures=3&outputformat=" + (json)));
        JSONObject rootObject = JSONObject.fromObject(out);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(1);
        JSONObject aPropeties = aFeature.getJSONObject("properties");
        JSONObject aGeometry = aPropeties.getJSONObject("multiCurveProperty");
        Assert.assertEquals(aGeometry.getString("type"), "MultiLineString");
        JSONArray geomArray = aGeometry.getJSONArray("coordinates");
        geomArray = geomArray.getJSONArray(0);
        geomArray = geomArray.getJSONArray(0);
        Assert.assertEquals(geomArray.getString(0), "55.174");
        CoordinateReferenceSystem expectedCrs = getCatalog().getLayerByName(getLayerId(SystemTestData.AGGREGATEGEOFEATURE)).getResource().getCRS();
        JSONObject aCRS = rootObject.getJSONObject("crs");
        Assert.assertThat(aCRS.getString("type"), Matchers.equalTo("name"));
        Assert.assertThat(aCRS, encodesCRS(expectedCrs));
    }

    @Test
    public void testMixedCollection() throws Exception {
        String xml = (((((((((("<wfs:GetFeature " + ("service=\"WFS\" " + "outputFormat=\"")) + (json)) + "\" ") + "version=\"1.0.0\" ") + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"sf:PrimitiveGeoFeature\" /> ") + "<wfs:Query typeName=\"sf:AggregateGeoFeature\" /> ") + "</wfs:GetFeature>";
        // System.out.println("\n" + xml + "\n");
        String out = postAsServletResponse("wfs", xml).getContentAsString();
        JSONObject rootObject = JSONObject.fromObject(out);
        // System.out.println(rootObject.get("type"));
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        // Check that there are at least two different types of features in here
        JSONObject aFeature = featureCol.getJSONObject(1);
        // System.out.println(aFeature.getString("id").substring(0,19));
        Assert.assertTrue(aFeature.getString("id").substring(0, 19).equalsIgnoreCase("PrimitiveGeoFeature"));
        aFeature = featureCol.getJSONObject(6);
        // System.out.println(aFeature.getString("id").substring(0,19));
        Assert.assertTrue(aFeature.getString("id").substring(0, 19).equalsIgnoreCase("AggregateGeoFeature"));
        // Check that a feature has the expected attributes
        JSONObject aProperties = aFeature.getJSONObject("properties");
        JSONObject aGeometry = aProperties.getJSONObject("multiCurveProperty");
        // System.out.println(aGeometry.getString("type"));
        Assert.assertEquals(aGeometry.getString("type"), "MultiLineString");
    }

    @Test
    public void testCallbackFunction() throws Exception {
        JSONType.setJsonpEnabled(true);
        MockHttpServletResponse resp = getAsServletResponse((((("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature&maxfeatures=1&outputformat=" + (jsonp)) + "&format_options=") + (CALLBACK_FUNCTION_KEY)) + ":myFunc"));
        JSONType.setJsonpEnabled(false);
        String out = resp.getContentAsString();
        Assert.assertEquals(jsonp, resp.getContentType());
        Assert.assertTrue(out.startsWith("myFunc("));
        Assert.assertTrue(out.endsWith(")"));
        // extract the json and check it
        out = out.substring(7, ((out.length()) - 1));
        JSONObject rootObject = JSONObject.fromObject(out);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertEquals(aFeature.getString("geometry_name"), "surfaceProperty");
    }

    @Test
    public void testGetFeatureCountNoFilter() throws Exception {
        // request without filter
        String out = getAsString(("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature&maxfeatures=10&outputformat=" + (json)));
        JSONObject rootObject = JSONObject.fromObject(out);
        Assert.assertEquals(rootObject.get("totalFeatures"), 5);
    }

    @Test
    public void testGetFeatureCountFilter() throws Exception {
        // request with filter (featureid=PrimitiveGeoFeature.f001)
        String out2 = getAsString((("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature&maxfeatures=10&outputformat=" + (json)) + "&featureid=PrimitiveGeoFeature.f001"));
        JSONObject rootObject2 = JSONObject.fromObject(out2);
        Assert.assertEquals(rootObject2.get("totalFeatures"), 1);
    }

    @Test
    public void testGetFeatureCountMaxFeatures() throws Exception {
        // check if maxFeatures doesn't affect totalFeatureCount; set Filter and maxFeatures
        String out3 = getAsString((("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature&maxfeatures=1&outputformat=" + (json)) + "&featureid=PrimitiveGeoFeature.f001,PrimitiveGeoFeature.f002"));
        JSONObject rootObject3 = JSONObject.fromObject(out3);
        Assert.assertEquals(rootObject3.get("totalFeatures"), 2);
    }

    @Test
    public void testGetFeatureCountMultipleFeatureTypes() throws Exception {
        // request with multiple featureTypes and Filter
        String out4 = getAsString((("wfs?request=GetFeature&version=1.0.0&typename=sf:PrimitiveGeoFeature,sf:AggregateGeoFeature&outputformat=" + (json)) + "&featureid=PrimitiveGeoFeature.f001,PrimitiveGeoFeature.f002,AggregateGeoFeature.f009"));
        JSONObject rootObject4 = JSONObject.fromObject(out4);
        Assert.assertEquals(rootObject4.get("totalFeatures"), 3);
    }

    @Test
    public void testGetFeatureCountSpatialFilter() throws Exception {
        // post with spatial-filter in another projection than layer-projection
        String xml = (((((((((((((((((((((("<wfs:GetFeature " + ("service=\"WFS\" " + "outputFormat=\"")) + (json)) + "\" ") + "version=\"1.1.0\" ") + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"sf:AggregateGeoFeature\" srsName=\"EPSG:900913\"> ") + "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"> ") + "<ogc:Intersects> ") + "<ogc:PropertyName></ogc:PropertyName> ") + "<gml:Polygon xmlns:gml=\"http://www.opengis.net/gml\" srsName=\"EPSG:900913\"> ") + "<gml:exterior> ") + "<gml:LinearRing> ") + "<gml:posList>7666573.330932751 3485566.812628661 8010550.557483965 3485566.812628661 8010550.557483965 3788277.001334882 7666573.330932751 3788277.001334882 7666573.330932751 3485566.812628661</gml:posList> ") + "</gml:LinearRing> ") + "</gml:exterior> ") + "</gml:Polygon> ") + "</ogc:Intersects> ") + "</ogc:Filter> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
        String out5 = postAsServletResponse("wfs", xml).getContentAsString();
        JSONObject rootObject5 = JSONObject.fromObject(out5);
        Assert.assertEquals(rootObject5.get("totalFeatures"), 1);
    }

    @Test
    public void testGetFeatureCountWfs20() throws Exception {
        // request without filter
        String out = getAsString(("wfs?request=GetFeature&version=2.0.0&typename=sf:PrimitiveGeoFeature&count=10&outputformat=" + (json)));
        JSONObject rootObject = JSONObject.fromObject(out);
        // print(rootObject);
        Assert.assertEquals(rootObject.get("totalFeatures"), 5);
        Assert.assertEquals(rootObject.get("numberMatched"), 5);
        Assert.assertNull(rootObject.get("links"));
        // request with filter (featureid=PrimitiveGeoFeature.f001)
        String out2 = getAsString((("wfs?request=GetFeature&version=2.0.0&typename=sf:PrimitiveGeoFeature&count=10&outputformat=" + (json)) + "&featureid=PrimitiveGeoFeature.f001"));
        JSONObject rootObject2 = JSONObject.fromObject(out2);
        Assert.assertEquals(rootObject2.get("totalFeatures"), 1);
        Assert.assertEquals(rootObject2.get("numberMatched"), 1);
        Assert.assertNull(rootObject2.get("links"));
        // check if maxFeatures doesn't affect totalFeatureCount; set Filter and maxFeatures
        String out3 = getAsString((("wfs?request=GetFeature&version=2.0.0&typename=sf:PrimitiveGeoFeature&count=1&outputformat=" + (json)) + "&featureid=PrimitiveGeoFeature.f001,PrimitiveGeoFeature.f002"));
        JSONObject rootObject3 = JSONObject.fromObject(out3);
        Assert.assertEquals(rootObject3.get("totalFeatures"), 2);
        Assert.assertEquals(rootObject3.get("numberMatched"), 2);
        Assert.assertNull(rootObject3.get("links"));
        // request with multiple featureTypes and Filter
        String out4 = getAsString((("wfs?request=GetFeature&version=2.0.0&typename=sf:PrimitiveGeoFeature,sf:AggregateGeoFeature&outputformat=" + (json)) + "&featureid=PrimitiveGeoFeature.f001,PrimitiveGeoFeature.f002,AggregateGeoFeature.f009"));
        JSONObject rootObject4 = JSONObject.fromObject(out4);
        Assert.assertEquals(rootObject4.get("totalFeatures"), 3);
        Assert.assertEquals(rootObject4.get("numberMatched"), 3);
        Assert.assertNull(rootObject4.get("links"));
    }

    @Test
    public void getGetFeatureWithPagingFirstPage() throws Exception {
        // request with paging
        String out = getAsString((("wfs?request=GetFeature&version=2.0.0&typename=sf:PrimitiveGeoFeature" + "&startIndex=0&&count=2&outputformat=") + (json)));
        JSONObject rootObject = JSONObject.fromObject(out);
        // print(rootObject);
        Assert.assertEquals(rootObject.get("totalFeatures"), 5);
        Assert.assertEquals(rootObject.get("numberMatched"), 5);
        Assert.assertEquals(rootObject.get("numberReturned"), 2);
        JSONArray links = rootObject.getJSONArray("links");
        Assert.assertNotNull(links);
        Assert.assertEquals(1, links.size());
        JSONObject link = links.getJSONObject(0);
        assertLink(link, "next page", "application/json", "next", ("http://localhost:8080/geoserver" + ("/wfs?TYPENAME=sf%3APrimitiveGeoFeature&REQUEST=GetFeature" + "&OUTPUTFORMAT=application%2Fjson&VERSION=2.0.0&COUNT=2&STARTINDEX=2")));
    }

    @Test
    public void getGetFeatureWithPagingMidPage() throws Exception {
        // request with paging
        String out = getAsString((("wfs?request=GetFeature&version=2.0.0&typename=sf:PrimitiveGeoFeature" + "&startIndex=2&&count=2&outputformat=") + (json)));
        JSONObject rootObject = JSONObject.fromObject(out);
        print(rootObject);
        Assert.assertEquals(rootObject.get("totalFeatures"), 5);
        Assert.assertEquals(rootObject.get("numberMatched"), 5);
        Assert.assertEquals(rootObject.get("numberReturned"), 2);
        JSONArray links = rootObject.getJSONArray("links");
        Assert.assertNotNull(links);
        Assert.assertEquals(2, links.size());
        JSONObject prev = links.getJSONObject(0);
        assertLink(prev, "previous page", "application/json", "previous", ("http://localhost:8080/geoserver" + ("/wfs?TYPENAME=sf%3APrimitiveGeoFeature&REQUEST=GetFeature" + "&OUTPUTFORMAT=application%2Fjson&VERSION=2.0.0&COUNT=2&STARTINDEX=0")));
        JSONObject next = links.getJSONObject(1);
        assertLink(next, "next page", "application/json", "next", ("http://localhost:8080/geoserver" + ("/wfs?TYPENAME=sf%3APrimitiveGeoFeature&REQUEST=GetFeature" + "&OUTPUTFORMAT=application%2Fjson&VERSION=2.0.0&COUNT=2&STARTINDEX=4")));
    }

    @Test
    public void getGetFeatureWithPagingLastPage() throws Exception {
        // request with paging
        String out = getAsString((("wfs?request=GetFeature&version=2.0.0&typename=sf:PrimitiveGeoFeature" + "&startIndex=4&&count=2&outputformat=") + (json)));
        JSONObject rootObject = JSONObject.fromObject(out);
        print(rootObject);
        Assert.assertEquals(rootObject.get("totalFeatures"), 5);
        Assert.assertEquals(rootObject.get("numberMatched"), 5);
        Assert.assertEquals(rootObject.get("numberReturned"), 1);
        JSONArray links = rootObject.getJSONArray("links");
        Assert.assertNotNull(links);
        Assert.assertEquals(1, links.size());
        JSONObject prev = links.getJSONObject(0);
        assertLink(prev, "previous page", "application/json", "previous", ("http://localhost:8080/geoserver" + ("/wfs?TYPENAME=sf%3APrimitiveGeoFeature&REQUEST=GetFeature" + "&OUTPUTFORMAT=application%2Fjson&VERSION=2.0.0&COUNT=2&STARTINDEX=2")));
    }

    @Test
    public void testGetFeatureLine3D() throws Exception {
        JSONObject collection = ((JSONObject) (getAsJSON(((("wfs?request=GetFeature&version=1.0.0&typename=" + (getLayerId(GeoJSONTest.LINE3D))) + "&outputformat=") + (json)))));
        // print(collection);
        Assert.assertEquals(1, collection.getInt("totalFeatures"));
        // assertEquals("4327",
        // collection.getJSONObject("crs").getJSONObject("properties").getString("code"));
        JSONArray features = collection.getJSONArray("features");
        Assert.assertEquals(1, features.size());
        JSONObject feature = features.getJSONObject(0);
        JSONObject geometry = feature.getJSONObject("geometry");
        Assert.assertEquals("LineString", geometry.getString("type"));
        JSONArray coords = geometry.getJSONArray("coordinates");
        JSONArray c1 = coords.getJSONArray(0);
        Assert.assertEquals(0, c1.getInt(0));
        Assert.assertEquals(0, c1.getInt(1));
        Assert.assertEquals(50, c1.getInt(2));
        JSONArray c2 = coords.getJSONArray(1);
        Assert.assertEquals(120, c2.getInt(0));
        Assert.assertEquals(0, c2.getInt(1));
        Assert.assertEquals(100, c2.getInt(2));
        CoordinateReferenceSystem expectedCrs = CRS.decode("EPSG:4327");
        JSONObject aCRS = collection.getJSONObject("crs");
        Assert.assertThat(aCRS, encodesCRS(expectedCrs));
    }

    @Test
    public void testGetFeatureWhereLayerHasDecimalPointsSet() throws Exception {
        JSONObject collection = ((JSONObject) (getAsJSON(((("wfs?request=GetFeature&version=1.0.0&typename=" + (getLayerId(GeoJSONTest.POINT_REDUCED))) + "&outputformat=") + (json)))));
        Assert.assertThat(collection.getInt("totalFeatures"), Matchers.is(3));
        JSONArray features = collection.getJSONArray("features");
        Assert.assertThat(((Collection<?>) (features)), Matchers.hasSize(3));
        JSONObject feature = features.getJSONObject(0);
        JSONObject geometry = feature.getJSONObject("geometry");
        Assert.assertThat(geometry.getString("type"), Matchers.is("Point"));
        JSONArray coords = geometry.getJSONArray("coordinates");
        Assert.assertThat(((Iterable<?>) (coords)), Matchers.contains(((Object) (120.12)), 0.56));
        JSONArray bbox = collection.getJSONArray("bbox");
        Assert.assertThat(((Iterable<?>) (bbox)), Matchers.contains(((Object) (-170.19)), (-30.13), 120.12, 45.23));
        CoordinateReferenceSystem expectedCrs = CRS.decode("EPSG:4326");
        JSONObject aCRS = collection.getJSONObject("crs");
        Assert.assertThat(aCRS, encodesCRS(expectedCrs));
    }

    @Test
    public void testGetFeatureAxisSwap() throws Exception {
        // Check that a NORTH_EAST source is swapped
        doAxisSwapTest(GeoJSONTest.POINT_LATLON, NORTH_EAST);
    }

    @Test
    public void testGetFeatureNoAxisSwap() throws Exception {
        // Check that an EAST_NORTH source is not swapped
        doAxisSwapTest(GeoJSONTest.POINT_LONLAT, EAST_NORTH);
    }

    @Test
    public void testGetFeatureCRS() throws Exception {
        QName layer = SystemTestData.LINES;
        JSONObject collection = ((JSONObject) (getAsJSON(((("wfs?request=GetFeature&version=1.0.0&typename=" + (getLayerId(layer))) + "&outputformat=") + (json)))));
        CoordinateReferenceSystem expectedCrs = getCatalog().getLayerByName(getLayerId(layer)).getResource().getCRS();
        JSONObject aCRS = collection.getJSONObject("crs");
        Assert.assertThat(aCRS, encodesCRS(expectedCrs));
    }

    /**
     * Tests if:
     *
     * <ul>
     *   <li>"geometry_name" is always the attribute name of the default geometry
     *   <li>"geometry" is always the attribute value of the default geometry
     *   <li>the default geometry is never contained within the properties, as duplicate
     * </ul>
     */
    @Test
    public void testGeometryAndGeometryNameConsistency() throws Exception {
        JSONObject collection = ((JSONObject) (getAsJSON(((("wfs?request=GetFeature&version=1.0.0&typename=" + (getLayerId(GeoJSONTest.MULTI_GEOMETRIES_WITH_NULL))) + "&outputformat=") + (json)))));
        // print(collection);
        Assert.assertEquals(3, collection.getInt("totalFeatures"));
        JSONArray features = collection.getJSONArray("features");
        Assert.assertEquals(3, features.size());
        // see MultiGeometriesWithNull.properties
        // -- MultiGeometriesWithNull.0 -- all geoms present
        JSONObject feature = features.getJSONObject(0);
        Assert.assertEquals("MultiGeometriesWithNull.0", feature.getString("id"));
        Assert.assertEquals("All geometries present, first geometry must be default", "geom_a", feature.getString("geometry_name"));
        JSONObject geometry = feature.getJSONObject("geometry");
        JSONArray coords = geometry.getJSONArray("coordinates");
        Assert.assertEquals("geom_a has coodinate 1", 1, coords.getInt(0));
        JSONObject properties = feature.getJSONObject("properties");
        Assert.assertFalse("geom_a must not be present, its the default geom", properties.containsKey("geom_a"));
        JSONObject propertyGeomB = properties.getJSONObject("geom_b");
        coords = propertyGeomB.getJSONArray("coordinates");
        Assert.assertEquals("geom_b has coodinate 2", 2, coords.getInt(0));
        JSONObject propertyGeomC = properties.getJSONObject("geom_c");
        coords = propertyGeomC.getJSONArray("coordinates");
        Assert.assertEquals("geom_c has coodinate 3", 3, coords.getInt(0));
        // -- MultiGeometriesWithNull.1 --, geom_b and geom_c present
        feature = features.getJSONObject(1);
        Assert.assertEquals("MultiGeometriesWithNull.1", feature.getString("id"));
        Assert.assertEquals("1st geometry null, still default", "geom_a", feature.getString("geometry_name"));
        geometry = feature.getJSONObject("geometry");
        Assert.assertTrue(geometry.isNullObject());
        properties = feature.getJSONObject("properties");
        Assert.assertFalse("geom_a must not be present, its the default geom", properties.containsKey("geom_a"));
        propertyGeomB = properties.getJSONObject("geom_b");
        coords = propertyGeomB.getJSONArray("coordinates");
        Assert.assertEquals("geom_b has coodinate 2", 2, coords.getInt(0));
        propertyGeomC = properties.getJSONObject("geom_c");
        coords = propertyGeomC.getJSONArray("coordinates");
        Assert.assertEquals("geom_c has coodinate 3", 3, coords.getInt(0));
        // -- MultiGeometriesWithNull.2 --, all geoms null
        feature = features.getJSONObject(2);
        Assert.assertEquals("MultiGeometriesWithNull.2", feature.getString("id"));
        Assert.assertEquals("no geometries present, 1st still default", "geom_a", feature.getString("geometry_name"));
        geometry = feature.getJSONObject("geometry");
        Assert.assertTrue(geometry.isNullObject());
        properties = feature.getJSONObject("properties");
        Assert.assertFalse("geom_a must not be present, its the default geom", properties.containsKey("geom_a"));
        propertyGeomB = properties.getJSONObject("geom_b");
        Assert.assertTrue(propertyGeomB.isNullObject());
        propertyGeomC = properties.getJSONObject("geom_c");
        Assert.assertTrue(propertyGeomC.isNullObject());
    }
}

