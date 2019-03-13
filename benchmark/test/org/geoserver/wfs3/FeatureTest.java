/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs3;


import MockData.GENERICENTITY;
import MockData.PRIMITIVEGEOFEATURE;
import MockData.ROAD_SEGMENTS;
import com.jayway.jsonpath.DocumentContext;
import java.net.URLEncoder;
import java.util.List;
import net.minidev.json.JSONArray;
import org.geoserver.catalog.FeatureTypeInfo;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class FeatureTest extends WFS3TestSupport {
    @Test
    public void testGetLayerAsGeoJson() throws Exception {
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        DocumentContext json = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/items"), 200);
        Assert.assertEquals("FeatureCollection", json.read("type", String.class));
        Assert.assertEquals(5, ((int) (json.read("features.length()", Integer.class))));
        // check self link
        List selfRels = json.read("links[?(@.type == 'application/geo+json')].rel");
        Assert.assertEquals(1, selfRels.size());
        Assert.assertEquals("self", selfRels.get(0));
        // check alternate link
        List alternatefRels = json.read("links[?(@.type == 'application/json')].rel");
        Assert.assertEquals(2, alternatefRels.size());
        Assert.assertEquals("alternate", alternatefRels.get(0));
        Assert.assertEquals("collection", alternatefRels.get(1));
        // check collection link
        List selfLink = json.read("links[?(@.rel == 'collection')].href");
        Assert.assertThat(selfLink.size(), Matchers.greaterThan(0));
        Assert.assertThat(((String) (selfLink.get(0))), Matchers.startsWith((("http://localhost:8080/geoserver/wfs3/collections/" + roadSegments) + "?")));
    }

    @Test
    public void testWorkspaceQualified() throws Exception {
        String roadSegments = ROAD_SEGMENTS.getLocalPart();
        DocumentContext json = getAsJSONPath(((((ROAD_SEGMENTS.getPrefix()) + "/wfs3/collections/") + roadSegments) + "/items"), 200);
        Assert.assertEquals("FeatureCollection", json.read("type", String.class));
        Assert.assertEquals(5, ((int) (json.read("features.length()", Integer.class))));
        // check self link
        List selfRels = json.read("links[?(@.type == 'application/geo+json')].rel");
        Assert.assertEquals(1, selfRels.size());
        Assert.assertEquals("self", selfRels.get(0));
        // check json links
        List alternatefRels = json.read("links[?(@.type == 'application/json')].rel");
        Assert.assertEquals(2, alternatefRels.size());
        Assert.assertEquals("alternate", alternatefRels.get(0));
        Assert.assertEquals("collection", alternatefRels.get(1));
        // check collection link
        List selfLink = json.read("links[?(@.rel == 'collection')].href");
        Assert.assertThat(selfLink.size(), Matchers.greaterThan(0));
        Assert.assertThat(((String) (selfLink.get(0))), Matchers.startsWith((("http://localhost:8080/geoserver/cite/wfs3/collections/" + roadSegments) + "?")));
    }

    @Test
    public void testBBoxFilter() throws Exception {
        String roadSegments = getEncodedName(PRIMITIVEGEOFEATURE);
        DocumentContext json = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/items?bbox=35,0,60,3"), 200);
        Assert.assertEquals("FeatureCollection", json.read("type", String.class));
        // should return only f002 and f003
        Assert.assertEquals(2, ((int) (json.read("features.length()", Integer.class))));
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f001')]", List.class).size());
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f002')]", List.class).size());
    }

    @Test
    public void testBBoxDatelineCrossingFilter() throws Exception {
        String roadSegments = getEncodedName(PRIMITIVEGEOFEATURE);
        DocumentContext json = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/items?bbox=170,0,60,3"), 200);
        Assert.assertEquals("FeatureCollection", json.read("type", String.class));
        // should return only f002 and f003
        Assert.assertEquals(2, ((int) (json.read("features.length()", Integer.class))));
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f001')]", List.class).size());
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f002')]", List.class).size());
    }

    @Test
    public void testTimeFilter() throws Exception {
        String roadSegments = getEncodedName(PRIMITIVEGEOFEATURE);
        DocumentContext json = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/items?time=2006-10-25"), 200);
        Assert.assertEquals("FeatureCollection", json.read("type", String.class));
        // should return only f001
        Assert.assertEquals(1, ((int) (json.read("features.length()", Integer.class))));
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f001')]", List.class).size());
    }

    @Test
    public void testTimeRangeFilter() throws Exception {
        String roadSegments = getEncodedName(PRIMITIVEGEOFEATURE);
        DocumentContext json = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/items?time=2006-09-01/2006-10-23"), 200);
        Assert.assertEquals("FeatureCollection", json.read("type", String.class));
        Assert.assertEquals(2, ((int) (json.read("features.length()", Integer.class))));
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f002')]", List.class).size());
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f003')]", List.class).size());
    }

    @Test
    public void testTimeDurationFilter() throws Exception {
        String roadSegments = getEncodedName(PRIMITIVEGEOFEATURE);
        DocumentContext json = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/items?time=2006-09-01/P1M23DT12H31M12S"), 200);
        Assert.assertEquals("FeatureCollection", json.read("type", String.class));
        Assert.assertEquals(2, ((int) (json.read("features.length()", Integer.class))));
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f002')]", List.class).size());
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f003')]", List.class).size());
    }

    @Test
    public void testCombinedSpaceTimeFilter() throws Exception {
        String roadSegments = getEncodedName(PRIMITIVEGEOFEATURE);
        DocumentContext json = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/items?time=2006-09-01/2006-10-23&bbox=35,0,60,3"), 200);
        Assert.assertEquals("FeatureCollection", json.read("type", String.class));
        Assert.assertEquals(1, ((int) (json.read("features.length()", Integer.class))));
        Assert.assertEquals(1, json.read("features[?(@.id == 'PrimitiveGeoFeature.f002')]", List.class).size());
    }

    @Test
    public void testSingleFeatureAsGeoJson() throws Exception {
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        DocumentContext json = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/items/RoadSegments.1107532045088"), 200);
        Assert.assertEquals("Feature", json.read("type", String.class));
        // check self link
        String geoJsonLinkPath = "links[?(@.type == 'application/geo+json')]";
        List selfRels = json.read((geoJsonLinkPath + ".rel"));
        Assert.assertEquals(1, selfRels.size());
        Assert.assertEquals("self", selfRels.get(0));
        String href = ((String) (((List) (json.read((geoJsonLinkPath + "href")))).get(0)));
        String expected = "http://localhost:8080/geoserver/wfs3/collections/cite__RoadSegments" + "/items/RoadSegments.1107532045088?f=application%2Fgeo%2Bjson";
        Assert.assertEquals(expected, href);
        // check alternate link
        List alternatefRels = json.read("links[?(@.type == 'application/json')].rel");
        Assert.assertEquals(2, alternatefRels.size());
        Assert.assertEquals("alternate", alternatefRels.get(0));
        Assert.assertEquals("collection", alternatefRels.get(1));
    }

    @Test
    public void testFirstPage() throws Exception {
        String expectedNextURL = "http://localhost:8080/geoserver/wfs3/collections/cite__RoadSegments/items?startIndex=3&limit=3";
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        MockHttpServletResponse response = getAsMockHttpServletResponse((("wfs3/collections/" + roadSegments) + "/items?limit=3"), 200);
        List<String> links = response.getHeaders("Link");
        Assert.assertThat(links, Matchers.hasSize(1));
        Assert.assertEquals(links.get(0), (("<" + expectedNextURL) + ">; rel=\"next\"; type=\"application/geo+json\""));
        DocumentContext json = getAsJSONPath(response);
        Assert.assertEquals(3, ((int) (json.read("features.length()", Integer.class))));
        // check the paging link is there
        Assert.assertThat(json.read("$.links[?(@.rel=='prev')].href"), Matchers.empty());
        Assert.assertThat(json.read("$.links[?(@.rel=='next')].href", JSONArray.class).get(0), CoreMatchers.equalTo(expectedNextURL));
    }

    @Test
    public void testMiddlePage() throws Exception {
        String expectedPrevURL = "http://localhost:8080/geoserver/wfs3/collections/cite__RoadSegments/items?startIndex=2&limit=1";
        String expectedNextURL = "http://localhost:8080/geoserver/wfs3/collections/cite__RoadSegments/items?startIndex=4&limit=1";
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        MockHttpServletResponse response = getAsMockHttpServletResponse((("wfs3/collections/" + roadSegments) + "/items?startIndex=3&limit=1"), 200);
        List<String> links = response.getHeaders("Link");
        Assert.assertThat(links, Matchers.hasSize(2));
        Assert.assertEquals(links.get(0), (("<" + expectedPrevURL) + ">; rel=\"prev\"; type=\"application/geo+json\""));
        Assert.assertEquals(links.get(1), (("<" + expectedNextURL) + ">; rel=\"next\"; type=\"application/geo+json\""));
        DocumentContext json = getAsJSONPath(response);
        Assert.assertEquals(1, ((int) (json.read("features.length()", Integer.class))));
        // check the paging link is there
        Assert.assertThat(json.read("$.links[?(@.rel=='prev')].href", JSONArray.class).get(0), CoreMatchers.equalTo(expectedPrevURL));
        Assert.assertThat(json.read("$.links[?(@.rel=='next')].href", JSONArray.class).get(0), CoreMatchers.equalTo(expectedNextURL));
    }

    @Test
    public void testLastPage() throws Exception {
        String expectedPrevLink = "http://localhost:8080/geoserver/wfs3/collections/cite__RoadSegments/items?startIndex=0&limit=3";
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        MockHttpServletResponse response = getAsMockHttpServletResponse((("wfs3/collections/" + roadSegments) + "/items?startIndex=3&limit=3"), 200);
        List<String> links = response.getHeaders("Link");
        Assert.assertThat(links, Matchers.hasSize(1));
        Assert.assertEquals(links.get(0), (("<" + expectedPrevLink) + ">; rel=\"prev\"; type=\"application/geo+json\""));
        DocumentContext json = getAsJSONPath(response);
        Assert.assertEquals(2, ((int) (json.read("features.length()", Integer.class))));
        // check the paging link is there
        Assert.assertThat(json.read("$.links[?(@.rel=='prev')].href", JSONArray.class).get(0), CoreMatchers.equalTo(expectedPrevLink));
        Assert.assertThat(json.read("$.links[?(@.rel=='next')].href"), Matchers.empty());
    }

    @Test
    public void testErrorHandling() throws Exception {
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        DocumentContext json = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/items?limit=abc"), 400);
        Assert.assertEquals("InvalidParameterValue", json.read("code"));
        Assert.assertThat(json.read("description"), CoreMatchers.both(CoreMatchers.containsString("COUNT")).and(CoreMatchers.containsString("abc")));
    }

    @Test
    public void testGetLayerAsHTML() throws Exception {
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        String url = ("wfs3/collections/" + roadSegments) + "/items?f=html";
        Document document = getAsJSoup(url);
        Assert.assertEquals(5, document.select("td:matches(RoadSegments\\..*)").size());
        // all elements expected are there
        // check the id of a known tag
        Assert.assertEquals("106", document.select("td:matches(RoadSegments\\.1107532045091) + td").text());
    }

    @Test
    public void testGetLayerAsHTMLPagingLinks() throws Exception {
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        String urlBase = ("wfs3/collections/" + roadSegments) + "/items?f=html";
        String expectedBase = ("http://localhost:8080/geoserver/wfs3/collections/" + roadSegments) + "/items";
        // first page, should only have next URL
        String firstPageURL = urlBase + "&limit=2";
        Document document = getAsJSoup(firstPageURL);
        Assert.assertNull(document.getElementById("prevPage"));
        Assert.assertNotNull(document.getElementById("nextPage"));
        String expectedSecondPageURL = expectedBase + "?f=html&limit=2&startIndex=2";
        assertURL(expectedSecondPageURL, document.getElementById("nextPage").attr("href"));
        // second page, should have both prev and next
        document = getAsJSoup((urlBase + "&limit=2&startIndex=2"));
        Assert.assertNotNull(document.getElementById("prevPage"));
        Assert.assertNotNull(document.getElementById("nextPage"));
        String expectedThirdPageURL = expectedBase + "?f=html&limit=2&startIndex=4";
        assertURL(expectedThirdPageURL, document.getElementById("nextPage").attr("href"));
        // last page, only prev
        document = getAsJSoup((urlBase + "&limit=2&startIndex=4"));
        Assert.assertNotNull(document.getElementById("prevPage"));
        Assert.assertNull(document.getElementById("nextPage"));
        assertURL(expectedSecondPageURL, document.getElementById("prevPage").attr("href"));
    }

    @Test
    public void testSpecialCharsInTypeName() throws Exception {
        FeatureTypeInfo genericEntity = getCatalog().getFeatureTypeByName(getLayerId(GENERICENTITY));
        genericEntity.setName("Entit?G?n?rique");
        getCatalog().save(genericEntity);
        try {
            String encodedLocalName = URLEncoder.encode(genericEntity.getName(), "UTF-8");
            String typeName = ((GENERICENTITY.getPrefix()) + "__") + encodedLocalName;
            String encodedFeatureId = encodedLocalName + ".f004";
            DocumentContext json = getAsJSONPath(((("wfs3/collections/" + typeName) + "/items/") + encodedFeatureId), 200);
            Assert.assertEquals("Feature", json.read("type", String.class));
            // check self link
            String geoJsonLinkPath = "links[?(@.type == 'application/geo+json')]";
            List selfRels = json.read((geoJsonLinkPath + ".rel"));
            Assert.assertEquals(1, selfRels.size());
            Assert.assertEquals("self", selfRels.get(0));
            String href = ((String) (((List) (json.read((geoJsonLinkPath + "href")))).get(0)));
            String expected = ((("http://localhost:8080/geoserver/wfs3/collections/" + typeName) + "/items/") + encodedFeatureId) + "?f=application%2Fgeo%2Bjson";
            Assert.assertEquals(expected, href);
            // check alternate link
            List alternatefRels = json.read("links[?(@.type == 'application/json')].rel");
            Assert.assertEquals(2, alternatefRels.size());
            Assert.assertEquals("alternate", alternatefRels.get(0));
            Assert.assertEquals("collection", alternatefRels.get(1));
        } finally {
            genericEntity.setName(GENERICENTITY.getLocalPart());
            getCatalog().save(genericEntity);
        }
    }
}

