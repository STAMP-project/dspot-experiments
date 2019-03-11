/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs3;


import MockData.ROAD_SEGMENTS;
import com.jayway.jsonpath.DocumentContext;
import java.util.List;
import java.util.Map;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.wfs.request.FeatureCollectionResponse;
import org.geoserver.wfs3.response.GML32WFS3OutputFormat;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class CollectionTest extends WFS3TestSupport {
    @Test
    public void testCollectionJson() throws Exception {
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        DocumentContext json = getAsJSONPath(("wfs3/collections/" + roadSegments), 200);
        Assert.assertEquals("cite__RoadSegments", json.read("$.name", String.class));
        Assert.assertEquals("RoadSegments", json.read("$.title", String.class));
        Assert.assertEquals((-180), json.read("$.extent.spatial[0]", Double.class), 0.0);
        Assert.assertEquals((-90), json.read("$.extent.spatial[1]", Double.class), 0.0);
        Assert.assertEquals(180, json.read("$.extent.spatial[2]", Double.class), 0.0);
        Assert.assertEquals(90, json.read("$.extent.spatial[3]", Double.class), 0.0);
        // check we have the expected number of links and they all use the right "rel" relation
        List<String> formats = DefaultWebFeatureService30.getAvailableFormats(FeatureCollectionResponse.class);
        Assert.assertThat(((int) (json.read("$.links.length()", Integer.class))), Matchers.greaterThanOrEqualTo(formats.size()));
        for (String format : formats) {
            // check title and rel.
            List items = json.read((("$.links[?(@.type=='" + format) + "')]"), List.class);
            Map item = ((Map) (items.get(0)));
            Assert.assertEquals(("cite__RoadSegments items as " + format), item.get("title"));
            Assert.assertEquals("item", item.get("rel"));
        }
        // the WFS3 specific GML3.2 output format is available
        Assert.assertNotNull(json.read((("$.links[?(@.type=='" + (GML32WFS3OutputFormat.FORMAT)) + "')]")));
        // tiling scheme extension
        Map tilingScheme = ((Map) (json.read("links[?(@.rel=='tilingScheme')]", List.class).get(0)));
        Assert.assertEquals((("http://localhost:8080/geoserver/wfs3/collections/" + roadSegments) + "/tiles/{tilingSchemeId}"), tilingScheme.get("href"));
        Map tiles = ((Map) (json.read("links[?(@.rel=='tiles')]", List.class).get(0)));
        Assert.assertEquals((("http://localhost:8080/geoserver/wfs3/collections/" + roadSegments) + "/tiles/{tilingSchemeId}/{level}/{row}/{col}"), tiles.get("href"));
    }

    @Test
    public void testCollectionVirtualWorkspace() throws Exception {
        String roadSegments = ROAD_SEGMENTS.getLocalPart();
        DocumentContext json = getAsJSONPath(("cite/wfs3/collections/" + roadSegments), 200);
        Assert.assertEquals("RoadSegments", json.read("$.name", String.class));
        Assert.assertEquals("RoadSegments", json.read("$.title", String.class));
        // check we have the expected number of links and they all use the right "rel" relation
        List<String> formats = DefaultWebFeatureService30.getAvailableFormats(FeatureCollectionResponse.class);
        Assert.assertThat(((int) (json.read("$.links.length()", Integer.class))), Matchers.greaterThanOrEqualTo(formats.size()));
        for (String format : formats) {
            // check title and rel.
            List items = json.read((("$.links[?(@.type=='" + format) + "')]"), List.class);
            Map item = ((Map) (items.get(0)));
            Assert.assertEquals(("RoadSegments items as " + format), item.get("title"));
            Assert.assertEquals("item", item.get("rel"));
        }
        // the WFS3 specific GML3.2 output format is available
        Assert.assertNotNull(json.read((("$.links[?(@.type=='" + (GML32WFS3OutputFormat.FORMAT)) + "')]")));
        // tiling scheme extension
        Map tilingScheme = ((Map) (json.read("links[?(@.rel=='tilingScheme')]", List.class).get(0)));
        Assert.assertEquals((("http://localhost:8080/geoserver/cite/wfs3/collections/" + roadSegments) + "/tiles/{tilingSchemeId}"), tilingScheme.get("href"));
        Map tiles = ((Map) (json.read("links[?(@.rel=='tiles')]", List.class).get(0)));
        Assert.assertEquals((("http://localhost:8080/geoserver/cite/wfs3/collections/" + roadSegments) + "/tiles/{tilingSchemeId}/{level}/{row}/{col}"), tiles.get("href"));
    }

    @Test
    public void testCollectionsXML() throws Exception {
        Document dom = getAsDOM((("wfs3/collections/" + (getEncodedName(ROAD_SEGMENTS))) + "?f=application/xml"));
        print(dom);
        String expected = "http://localhost:8080/geoserver/wfs3/collections/cite__RoadSegments/items?f=application%2Fjson";
        XMLAssert.assertXpathEvaluatesTo(expected, "//wfs:Collection[wfs:Name='cite__RoadSegments']/atom:link[@atom:type='application/json']/@atom:href", dom);
    }

    @Test
    public void testCollectionYaml() throws Exception {
        String yaml = getAsString((("wfs3/collections/" + (getEncodedName(ROAD_SEGMENTS))) + "?f=application/x-yaml"));
        // System.out.println(yaml);
    }
}

