/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs3;


import HttpHeaders.LOCATION;
import OpenAPIResponse.OPEN_API_MIME;
import SLDHandler.MIMETYPE_10;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import java.awt.Color;
import java.util.List;
import java.util.logging.Level;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.StyleInfo;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class StyleTest extends WFS3TestSupport {
    @Test
    public void testGetStyles() throws Exception {
        final DocumentContext doc = getAsJSONPath("wfs3/styles", 200);
        // only the dashed line, put as the only non linked style
        Assert.assertEquals(Integer.valueOf(1), doc.read("styles.length()", Integer.class));
        Assert.assertEquals("dashed", doc.read("styles..id", List.class).get(0));
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/styles/dashed?f=application%2Fvnd.ogc.sld%2Bxml", doc.read("styles..links[?(@.rel=='style' && @.type=='application/vnd.ogc.sld+xml')].href", List.class).get(0));
    }

    @Test
    public void testGetCollectionStyles() throws Exception {
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        final DocumentContext doc = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/styles"), 200);
        // two stiles, the native one, and the line associated one
        Assert.assertEquals(Integer.valueOf(2), doc.read("styles.length()", Integer.class));
        Assert.assertEquals("RoadSegments", doc.read("styles..id", List.class).get(0));
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/collections/cite__RoadSegments/styles/RoadSegments?f=application%2Fvnd.ogc.sld%2Bxml", doc.read("styles..links[?(@.rel=='style' && @.type=='application/vnd.ogc.sld+xml')].href", List.class).get(0));
        Assert.assertEquals("line", doc.read("styles..id", List.class).get(1));
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/collections/cite__RoadSegments/styles/line?f=application%2Fvnd.ogc.sld%2Bxml", doc.read("styles..links[?(@.rel=='style' && @.type=='application/vnd.ogc.sld+xml')].href", List.class).get(1));
    }

    @Test
    public void testGetStyle() throws Exception {
        final MockHttpServletResponse response = getAsServletResponse("wfs3/styles/dashed?f=sld");
        Assert.assertEquals(OK.value(), response.getStatus());
        Assert.assertEquals(MIMETYPE_10, response.getContentType());
        Assert.assertEquals("inline; filename=dashed.sld", response.getHeader("Content-Disposition"));
        final Document dom = dom(response, true);
        assertXpathEvaluatesTo("SLD Cook Book: Dashed line", "//sld:UserStyle/sld:Title", dom);
        assertXpathEvaluatesTo("1", "count(//sld:Rule)", dom);
        assertXpathEvaluatesTo("1", "count(//sld:LineSymbolizer)", dom);
        assertXpathEvaluatesTo("5 2", "//sld:LineSymbolizer/sld:Stroke/sld:CssParameter[@name='stroke-dasharray']", dom);
    }

    @Test
    public void testGetCollectionStyle() throws Exception {
        final MockHttpServletResponse response = getAsServletResponse("wfs3/collections/cite__RoadSegments/styles/line");
        Assert.assertEquals(OK.value(), response.getStatus());
        Assert.assertEquals(MIMETYPE_10, response.getContentType());
        final Document dom = dom(response, true);
        // print(dom);
        assertXpathEvaluatesTo("A boring default style", "//sld:UserStyle/sld:Title", dom);
        assertXpathEvaluatesTo("1", "count(//sld:Rule)", dom);
        assertXpathEvaluatesTo("1", "count(//sld:LineSymbolizer)", dom);
        assertXpathEvaluatesTo("#0000FF", "//sld:LineSymbolizer/sld:Stroke/sld:CssParameter[@name='stroke']", dom);
    }

    @Test
    public void testGetCollectionNonAssociatedStyle() throws Exception {
        final MockHttpServletResponse response = getAsServletResponse("wfs3/collections/cite__RoadSegments/styles/polygon");
        Assert.assertEquals(NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testPostSLDStyleGlobal() throws Exception {
        String styleBody = loadStyle("simplePoint.sld");
        final MockHttpServletResponse response = postAsServletResponse("wfs3/styles", styleBody, MIMETYPE_10);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/styles/simplePoint", response.getHeader(LOCATION));
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("simplePoint");
        checkSimplePoint(styleInfo, Color.RED);
    }

    @Test
    public void testPostSLDStyleCollection() throws Exception {
        String styleBody = loadStyle("simplePoint.sld");
        final MockHttpServletResponse response = postAsServletResponse("wfs3/collections/cite__RoadSegments/styles", styleBody, MIMETYPE_10);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/collections/cite__RoadSegments/styles/simplePoint", response.getHeader(LOCATION));
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("simplePoint");
        checkSimplePoint(styleInfo, Color.RED);
        // check layer association
        final LayerInfo layer = getCatalog().getLayerByName(getLayerId(ROAD_SEGMENTS));
        Assert.assertThat(layer.getStyles(), Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.equalTo("simplePoint"))));
    }

    @Test
    public void testPostSLDStyleInWorkspace() throws Exception {
        String styleBody = loadStyle("simplePoint.sld");
        final MockHttpServletResponse response = postAsServletResponse("cite/wfs3/styles", styleBody, MIMETYPE_10);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals("http://localhost:8080/geoserver/cite/wfs3/styles/simplePoint", response.getHeader(LOCATION));
        final StyleInfo styleInfo = getCatalog().getStyleByName("cite", "simplePoint");
        checkSimplePoint(styleInfo, Color.RED);
    }

    @Test
    public void testPutSLDStyleGlobal() throws Exception {
        String styleBody = loadStyle("simplePoint.sld");
        // use a name not found in the style body
        final MockHttpServletResponse response = putAsServletResponse("wfs3/styles/testPoint", styleBody, MIMETYPE_10);
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("testPoint");
        checkSimplePoint(styleInfo, Color.RED);
    }

    @Test
    public void testPutSLDStyleCollection() throws Exception {
        String styleBody = loadStyle("simplePoint.sld");
        // use a name not found in the style body
        final MockHttpServletResponse response = putAsServletResponse("wfs3/collections/cite__RoadSegments/styles/testPoint", styleBody, MIMETYPE_10);
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("testPoint");
        checkSimplePoint(styleInfo, Color.RED);
        // check layer association
        final LayerInfo layer = getCatalog().getLayerByName(getLayerId(ROAD_SEGMENTS));
        Assert.assertThat(layer.getStyles(), Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.equalTo("testPoint"))));
    }

    @Test
    public void testPutSLDStyleModify() throws Exception {
        testPutSLDStyleGlobal();
        // use a different style body
        String styleBody = loadStyle("simplePoint2.sld");
        final MockHttpServletResponse response = putAsServletResponse("wfs3/styles/testPoint", styleBody, MIMETYPE_10);
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("testPoint");
        checkSimplePoint(styleInfo, Color.BLACK);
    }

    @Test
    public void testDeleteNonExistingStyle() throws Exception {
        MockHttpServletResponse response = deleteAsServletResponse("wfs3/styles/notThere");
        Assert.assertEquals(NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDeleteGlobalStyle() throws Exception {
        // creates "testPoint"
        testPutSLDStyleGlobal();
        // remove it
        MockHttpServletResponse response = deleteAsServletResponse("wfs3/styles/testPoint");
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        Assert.assertNull(getCatalog().getStyleByName("simplePoint"));
    }

    @Test
    public void testDeleteNonAssociatedBuiltInStyle() throws Exception {
        // add testPoint, but not associated to the road segments layer
        testPostSLDStyleGlobal();
        MockHttpServletResponse response = deleteAsServletResponse("wfs3/collections/cite__RoadSegments/styles/polygon");
        Assert.assertEquals(NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDeleteNonAssociatedStyle() throws Exception {
        // add testPoint, but not associated to the road segments layer
        testPostSLDStyleGlobal();
        MockHttpServletResponse response = deleteAsServletResponse("wfs3/collections/cite__RoadSegments/styles/simplePoint");
        Assert.assertEquals(NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDeleteAssociatedStyle() throws Exception {
        // add testPoint, but not associated to the road segments layer
        testPostSLDStyleCollection();
        MockHttpServletResponse response = deleteAsServletResponse("wfs3/collections/cite__RoadSegments/styles/simplePoint");
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check the style is gone and the association too
        Assert.assertNull(getCatalog().getStyleByName("simplePoint"));
        final LayerInfo layer = getCatalog().getLayerByName(getLayerId(ROAD_SEGMENTS));
        System.out.println(layer.getStyles());
        Assert.assertEquals(1, layer.getStyles().size());
        Assert.assertThat(layer.getStyles(), Matchers.hasItems(Matchers.hasProperty("name", CoreMatchers.equalTo("line"))));
    }

    @Test
    public void testMBStyle() throws Exception {
        String styleBody = loadStyle("mbcircle.json");
        // use a name not found in the style body
        MockHttpServletResponse response = postAsServletResponse("wfs3/styles", styleBody, MBStyleHandler.MIME_TYPE);
        Assert.assertEquals(CREATED.value(), response.getStatus());
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/styles/circles", response.getHeader(LOCATION));
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("circles");
        Assert.assertNotNull(styleInfo);
        // verify links for it
        DocumentContext doc = getAsJSONPath("wfs3/styles", 200);
        Assert.assertEquals(Integer.valueOf(2), doc.read("styles.length()", Integer.class));
        Assert.assertEquals(1, doc.read("styles[?(@.id=='circles')]", List.class).size());
        Assert.assertEquals(2, doc.read("styles[?(@.id=='circles')].links..href", List.class).size());
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/styles/circles?f=application%2Fvnd.ogc.sld%2Bxml", doc.read("styles[?(@.id=='circles')].links[?(@.rel=='style' && @.type=='application/vnd.ogc.sld+xml')].href", List.class).get(0));
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/styles/circles?f=application%2Fvnd.geoserver.mbstyle%2Bjson", doc.read("styles[?(@.id=='circles')].links[?(@.rel=='style' && @.type=='application/vnd.geoserver.mbstyle+json')].href", List.class).get(0));
        // check we can get both styles, first SLD
        Document dom = getAsDOM("wfs3/styles/circles?f=application%2Fvnd.ogc.sld%2Bxml", 200);
        // print(dom);
        assertXpathEvaluatesTo("circles", "//sld:StyledLayerDescriptor/sld:Name", dom);
        assertXpathEvaluatesTo("1", "count(//sld:Rule)", dom);
        assertXpathEvaluatesTo("1", "count(//sld:PointSymbolizer)", dom);
        assertXpathEvaluatesTo("circle", "//sld:PointSymbolizer/sld:Graphic/sld:Mark/sld:WellKnownName", dom);
        // .. then MBStyle
        response = getAsMockHttpServletResponse("wfs3/styles/circles?f=application%2Fvnd.geoserver.mbstyle%2Bjson", 200);
        Assert.assertEquals("inline; filename=circles.mbstyle", response.getHeader("Content-Disposition"));
        DocumentContext mbstyle = getAsJSONPath(response);
        Assert.assertEquals("circles", mbstyle.read("$.name"));
    }

    @Test
    public void testCSS() throws Exception {
        String styleBody = loadStyle("line.css");
        // create style
        MockHttpServletResponse response = putAsServletResponse("wfs3/styles/cssline", styleBody, CssHandler.MIME_TYPE);
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("cssline");
        Assert.assertNotNull(styleInfo);
        // verify links for it
        DocumentContext doc = getAsJSONPath("wfs3/styles", 200);
        Assert.assertEquals(Integer.valueOf(2), doc.read("styles.length()", Integer.class));
        Assert.assertEquals(1, doc.read("styles[?(@.id=='cssline')]", List.class).size());
        Assert.assertEquals(2, doc.read("styles[?(@.id=='cssline')].links..href", List.class).size());
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/styles/cssline?f=application%2Fvnd.ogc.sld%2Bxml", doc.read("styles[?(@.id=='cssline')].links[?(@.rel=='style' && @.type=='application/vnd.ogc.sld+xml')].href", List.class).get(0));
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/styles/cssline?f=application%2Fvnd.geoserver.geocss%2Bcss", doc.read("styles[?(@.id=='cssline')].links[?(@.rel=='style' && @.type=='application/vnd.geoserver.geocss+css')].href", List.class).get(0));
        // check we can get both styles, first SLD
        Document dom = getAsDOM("wfs3/styles/cssline?f=application%2Fvnd.ogc.sld%2Bxml", 200);
        // print(dom);
        assertXpathEvaluatesTo("cssline", "//sld:StyledLayerDescriptor/sld:Name", dom);
        assertXpathEvaluatesTo("1", "count(//sld:Rule)", dom);
        assertXpathEvaluatesTo("1", "count(//sld:LineSymbolizer)", dom);
        assertXpathEvaluatesTo("3", "//sld:LineSymbolizer/sld:Stroke/sld:CssParameter[@name='stroke-width']", dom);
        // .. then CSS
        response = getAsServletResponse("wfs3/styles/cssline?f=application%2Fvnd.geoserver.geocss%2Bcss");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("inline; filename=cssline.css", response.getHeader("Content-Disposition"));
        assertEqualsIgnoreNewLineStyle(("* {\n" + (("   stroke: black;\n" + "   stroke-width: 3;\n") + "}")), response.getContentAsString());
    }

    @Test
    public void testApiExtensions() throws Exception {
        MockHttpServletResponse response = getAsMockHttpServletResponse("wfs3/api", 200);
        Assert.assertEquals(OPEN_API_MIME, response.getContentType());
        String json = response.getContentAsString();
        LOGGER.log(Level.INFO, json);
        ObjectMapper mapper = Json.mapper();
        OpenAPI api = mapper.readValue(json, OpenAPI.class);
        // check paths
        Paths paths = api.getPaths();
        // ... global styles
        PathItem globalStyles = paths.get("/styles");
        Assert.assertNotNull(globalStyles);
        Assert.assertThat(globalStyles.getGet().getOperationId(), CoreMatchers.equalTo("getStyles"));
        Assert.assertThat(globalStyles.getPost().getOperationId(), CoreMatchers.equalTo("addStyle"));
        assertBodyMediaTypes(globalStyles.getPost());
        // ... global style
        PathItem globalStyle = paths.get("/styles/{styleId}");
        Assert.assertNotNull(globalStyle);
        Assert.assertThat(globalStyle.getGet().getOperationId(), CoreMatchers.equalTo("getStyle"));
        Assert.assertThat(globalStyle.getPut().getOperationId(), CoreMatchers.equalTo("replaceStyle"));
        assertBodyMediaTypes(globalStyle.getPut());
        Assert.assertThat(globalStyle.getDelete().getOperationId(), CoreMatchers.equalTo("deleteStyle"));
        // ... collection styles
        PathItem collectionStyles = paths.get("/collections/{collectionId}/styles");
        Assert.assertNotNull(collectionStyles);
        Assert.assertThat(collectionStyles.getGet().getOperationId(), CoreMatchers.equalTo("getCollectionStyles"));
        Assert.assertThat(collectionStyles.getPost().getOperationId(), CoreMatchers.equalTo("addCollectionStyle"));
        assertBodyMediaTypes(collectionStyles.getPost());
        // ... collection style
        PathItem collectionStyle = paths.get("/collections/{collectionId}/styles/{styleId}");
        Assert.assertNotNull(collectionStyle);
        Assert.assertThat(collectionStyle.getGet().getOperationId(), CoreMatchers.equalTo("getCollectionStyle"));
        Assert.assertThat(collectionStyle.getPut().getOperationId(), CoreMatchers.equalTo("replaceCollectionStyle"));
        assertBodyMediaTypes(collectionStyle.getPut());
        Assert.assertThat(collectionStyle.getDelete().getOperationId(), CoreMatchers.equalTo("deleteCollectionStyle"));
    }
}

