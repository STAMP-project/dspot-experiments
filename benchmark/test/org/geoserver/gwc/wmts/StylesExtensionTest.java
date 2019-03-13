/**
 * (c) 2019 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.wmts;


import SLDHandler.MIMETYPE_10;
import com.jayway.jsonpath.DocumentContext;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;


public class StylesExtensionTest extends GeoServerSystemTestSupport {
    // xpath engine that will be used to check XML content
    protected static XpathEngine xpath;

    {
        // registering namespaces for the xpath engine
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("xlink", "http://www.w3.org/1999/xlink");
        namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaces.put("ows", "http://www.opengis.net/ows/1.1");
        namespaces.put("wmts", "http://www.opengis.net/wmts/1.0");
        namespaces.put("gml", "http://www.opengis.net/gml");
        namespaces.put("sld", "http://www.opengis.net/sld");
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(namespaces));
        StylesExtensionTest.xpath = XMLUnit.newXpathEngine();
    }

    @Test
    public void testCapabilitiesLayerLinks() throws Exception {
        final Document dom = getAsDOM("gwc/service/wmts?request=GetCapabilities", 200);
        // print(dom);
        String template = "//wmts:Layer[ows:Identifier='cite:RoadSegments']/wmts:ResourceURL[@resourceType = '%s']";
        // layer styles resource
        final NamedNodeMap stylesAtts = getNodeAttributes(dom, String.format(template, "layerStyles"));
        Assert.assertEquals("text/json", attributeValue(stylesAtts, "format"));
        Assert.assertEquals("http://localhost:8080/geoserver/gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/?f=text%2Fjson", attributeValue(stylesAtts, "template"));
        // default style
        final NamedNodeMap defaultStylesAtts = getNodeAttributes(dom, String.format(template, "defaultStyle"));
        Assert.assertEquals(MIMETYPE_10, attributeValue(defaultStylesAtts, "format"));
        Assert.assertEquals("http://localhost:8080/geoserver/gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/RoadSegments?f=application%2Fvnd.ogc.sld%2Bxml", attributeValue(defaultStylesAtts, "template"));
        // alternate style
        final NamedNodeMap alternateStylesAtts = getNodeAttributes(dom, String.format(template, "style"));
        Assert.assertEquals(MIMETYPE_10, attributeValue(alternateStylesAtts, "format"));
        Assert.assertEquals("http://localhost:8080/geoserver/gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/line?f=application%2Fvnd.ogc.sld%2Bxml", attributeValue(alternateStylesAtts, "template"));
    }

    @Test
    public void testCapabilitiesLinksVirtualWorkspace() throws Exception {
        final Document dom = getAsDOM("cite/gwc/service/wmts?request=GetCapabilities", 200);
        print(dom);
        String template = "//wmts:Layer[ows:Identifier='RoadSegments']/wmts:ResourceURL[@resourceType = '%s']";
        // layer styles resource
        final NamedNodeMap stylesAtts = getNodeAttributes(dom, String.format(template, "layerStyles"));
        Assert.assertEquals("text/json", attributeValue(stylesAtts, "format"));
        Assert.assertEquals("http://localhost:8080/geoserver/cite/gwc/service/wmts/reststyles/layers/RoadSegments/styles/?f=text%2Fjson", attributeValue(stylesAtts, "template"));
        // default style
        final NamedNodeMap defaultStylesAtts = getNodeAttributes(dom, String.format(template, "defaultStyle"));
        Assert.assertEquals(MIMETYPE_10, attributeValue(defaultStylesAtts, "format"));
        Assert.assertEquals("http://localhost:8080/geoserver/cite/gwc/service/wmts/reststyles/layers/RoadSegments/styles/RoadSegments?f=application%2Fvnd.ogc.sld%2Bxml", attributeValue(defaultStylesAtts, "template"));
        // alternate style
        final NamedNodeMap alternateStylesAtts = getNodeAttributes(dom, String.format(template, "style"));
        Assert.assertEquals(MIMETYPE_10, attributeValue(alternateStylesAtts, "format"));
        Assert.assertEquals("http://localhost:8080/geoserver/cite/gwc/service/wmts/reststyles/layers/RoadSegments/styles/line?f=application%2Fvnd.ogc.sld%2Bxml", attributeValue(alternateStylesAtts, "template"));
    }

    @Test
    public void testLayerStyles() throws Exception {
        final MockHttpServletResponse response = getAsServletResponse(("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/RoadSegments?f" + "=application%2Fvnd.ogc.sld%2Bxml"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(MIMETYPE_10, response.getContentType());
        final Document dom = dom(response, true);
        Assert.assertEquals("dirt_road", StylesExtensionTest.xpath.evaluate("//sld:Rule/sld:Name", dom));
    }

    @Test
    public void testDefaultFormat() throws Exception {
        final MockHttpServletResponse response = getAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/RoadSegments");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(MIMETYPE_10, response.getContentType());
        final Document dom = dom(response, true);
        Assert.assertEquals("dirt_road", StylesExtensionTest.xpath.evaluate("//sld:Rule/sld:Name", dom));
    }

    @Test
    public void testLayerNonAssociatedStyle() throws Exception {
        final MockHttpServletResponse response = getAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/polygon");
        Assert.assertEquals(NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testPutStyle() throws Exception {
        String styleBody = loadStyle("simplePoint.sld");
        // use a name not found in the style body
        final MockHttpServletResponse response = putAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/testPoint", styleBody, MIMETYPE_10);
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
        testPutStyle();
        // use a different style body
        String styleBody = loadStyle("simplePoint2.sld");
        final MockHttpServletResponse response = putAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/testPoint", styleBody, MIMETYPE_10);
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("testPoint");
        checkSimplePoint(styleInfo, Color.BLACK);
    }

    @Test
    public void testDeleteNonExistingStyle() throws Exception {
        MockHttpServletResponse response = deleteAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/testPoint");
        Assert.assertEquals(NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDeleteNonAssociatedBuiltInStyle() throws Exception {
        MockHttpServletResponse response = deleteAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/polygon");
        Assert.assertEquals(NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDeleteNonAssociatedStyle() throws Exception {
        // add testPoint associated to cite:RoadSegments, try to remove it from Streams
        testPutStyle();
        MockHttpServletResponse response = deleteAsServletResponse("gwc/service/wmts/reststyles/layers/cite:Streams/styles/testPoint");
        Assert.assertEquals(NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDeleteAssociatedStyle() throws Exception {
        // add testPoint, but not associated to the road segments layer
        testPutStyle();
        MockHttpServletResponse response = deleteAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/testPoint");
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check the style is gone and the association too
        Assert.assertNull(getCatalog().getStyleByName("simplePoint"));
        final LayerInfo layer = getCatalog().getLayerByName(getLayerId(ROAD_SEGMENTS));
        Assert.assertEquals(1, layer.getStyles().size());
        Assert.assertThat(layer.getStyles(), Matchers.hasItems(Matchers.hasProperty("name", CoreMatchers.equalTo("line"))));
    }

    @Test
    public void testMBStyle() throws Exception {
        String styleBody = loadStyle("mbcircle.json");
        // use a name not found in the style body
        MockHttpServletResponse response = putAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/mbcircle", styleBody, MBStyleHandler.MIME_TYPE);
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("mbcircle");
        Assert.assertNotNull(styleInfo);
        // verify links for it
        Document dom = getAsDOM("gwc/service/wmts?request=GetCapabilities", 200);
        String template = "//wmts:Layer[ows:Identifier='cite:RoadSegments']/wmts:ResourceURL[contains(@template, 'mbcircle') and @format='%s']";
        // native format link
        final NamedNodeMap mbstyleAtts = getNodeAttributes(dom, String.format(template, "application/vnd.geoserver.mbstyle+json"));
        Assert.assertEquals("style", attributeValue(mbstyleAtts, "resourceType"));
        Assert.assertEquals("http://localhost:8080/geoserver/gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/mbcircle?f=application%2Fvnd.geoserver.mbstyle%2Bjson", attributeValue(mbstyleAtts, "template"));
        // converted format link
        final NamedNodeMap sldAtts = getNodeAttributes(dom, String.format(template, "application/vnd.ogc.sld+xml"));
        Assert.assertEquals("style", attributeValue(sldAtts, "resourceType"));
        Assert.assertEquals("http://localhost:8080/geoserver/gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/mbcircle?f=application%2Fvnd.ogc.sld%2Bxml", attributeValue(sldAtts, "template"));
        // check we can get both styles, first SLD
        response = getAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/mbcircle?f=application%2Fvnd.ogc.sld%2Bxml");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("inline; filename=mbcircle.sld", response.getHeader("Content-Disposition"));
        dom = dom(response, true);
        // print(dom);
        assertXpathEvaluatesTo("circles", "//sld:StyledLayerDescriptor/sld:Name", dom);
        assertXpathEvaluatesTo("1", "count(//sld:Rule)", dom);
        assertXpathEvaluatesTo("1", "count(//sld:PointSymbolizer)", dom);
        assertXpathEvaluatesTo("circle", "//sld:PointSymbolizer/sld:Graphic/sld:Mark/sld:WellKnownName", dom);
        // .. then MBStyle
        response = getAsServletResponse("/gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/mbcircle?f=application%2Fvnd.geoserver.mbstyle%2Bjson");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("inline; filename=mbcircle.mbstyle", response.getHeader("Content-Disposition"));
        DocumentContext mbstyle = getAsJSONPath(response);
        Assert.assertEquals("circles", mbstyle.read("$.name"));
    }

    @Test
    public void testCSS() throws Exception {
        String styleBody = loadStyle("line.css");
        // create style
        MockHttpServletResponse response = putAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/cssline", styleBody, CssHandler.MIME_TYPE);
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("cssline");
        Assert.assertNotNull(styleInfo);
        // verify links for it
        Document dom = getAsDOM("gwc/service/wmts?request=GetCapabilities", 200);
        String template = "//wmts:Layer[ows:Identifier='cite:RoadSegments']/wmts:ResourceURL[contains(@template, 'cssline') and @format='%s']";
        // native format link
        final NamedNodeMap mbstyleAtts = getNodeAttributes(dom, String.format(template, CssHandler.MIME_TYPE));
        Assert.assertEquals("style", attributeValue(mbstyleAtts, "resourceType"));
        Assert.assertEquals("http://localhost:8080/geoserver/gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/cssline?f=application%2Fvnd.geoserver.geocss%2Bcss", attributeValue(mbstyleAtts, "template"));
        // converted format link
        final NamedNodeMap sldAtts = getNodeAttributes(dom, String.format(template, "application/vnd.ogc.sld+xml"));
        Assert.assertEquals("style", attributeValue(sldAtts, "resourceType"));
        Assert.assertEquals("http://localhost:8080/geoserver/gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/cssline?f=application%2Fvnd.ogc.sld%2Bxml", attributeValue(sldAtts, "template"));
        // check we can get both styles, first SLD
        response = getAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/cssline?f=application%2Fvnd.ogc.sld%2Bxml");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("inline; filename=cssline.sld", response.getHeader("Content-Disposition"));
        dom = dom(response, true);
        // print(dom);
        assertXpathEvaluatesTo("cssline", "//sld:StyledLayerDescriptor/sld:Name", dom);
        assertXpathEvaluatesTo("1", "count(//sld:Rule)", dom);
        assertXpathEvaluatesTo("1", "count(//sld:LineSymbolizer)", dom);
        assertXpathEvaluatesTo("3", "//sld:LineSymbolizer/sld:Stroke/sld:CssParameter[@name='stroke-width']", dom);
        // .. then CSS
        response = getAsServletResponse("gwc/service/wmts/reststyles/layers/cite:RoadSegments/styles/cssline?f=application%2Fvnd.geoserver.geocss%2Bcss");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("inline; filename=cssline.css", response.getHeader("Content-Disposition"));
        assertEqualsIgnoreNewLineStyle(("* {\n" + (("   stroke: black;\n" + "   stroke-width: 3;\n") + "}")), response.getContentAsString());
    }

    @Test
    public void testWorkspaceSpecific() throws Exception {
        String styleBody = loadStyle("dashedline.sld");
        // use a name not found in the style body
        MockHttpServletResponse response = putAsServletResponse("cite/gwc/service/wmts/reststyles/layers/DividedRoutes/styles/dashed", styleBody, MIMETYPE_10);
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check style creation
        final StyleInfo styleInfo = getCatalog().getStyleByName("dashed");
        Assert.assertNotNull(styleInfo);
        Assert.assertThat(styleInfo.getWorkspace(), CoreMatchers.equalTo(getCatalog().getWorkspaceByName("cite")));
        // check layer association
        LayerInfo layer = getCatalog().getLayerByName(getLayerId(DIVIDED_ROUTES));
        Assert.assertThat(layer.getStyles(), Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.equalTo("dashed"))));
        // check capabilities with a layer specific service
        Document dom = getAsDOM("cite/DividedRoutes/gwc/service/wmts?request=GetCapabilities", 200);
        // print(dom);
        String template = "//wmts:Layer[ows:Identifier='DividedRoutes']/wmts:ResourceURL[contains(@template, 'dashed') and @format='%s']";
        final NamedNodeMap styleAtts = getNodeAttributes(dom, String.format(template, MIMETYPE_10));
        Assert.assertEquals("style", attributeValue(styleAtts, "resourceType"));
        // remove the style using a workspace specific delete
        response = deleteAsServletResponse("cite/gwc/service/wmts/reststyles/layers/DividedRoutes/styles/dashed");
        Assert.assertEquals(NO_CONTENT.value(), response.getStatus());
        // check the style is no more and it's no longer associated
        Assert.assertNull(getCatalog().getStyleByName("dashed"));
        layer = getCatalog().getLayerByName(getLayerId(DIVIDED_ROUTES));
        Assert.assertThat(layer.getStyles(), CoreMatchers.not(Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.equalTo("dashed")))));
    }

    @Test
    public void testStyleGroup() throws Exception {
        // check capabilities
        final Document dom = getAsDOM("gwc/service/wmts?request=GetCapabilities", 200);
        final NamedNodeMap attributes = getNodeAttributes(dom, ("//wmts:Layer[ows:Identifier='wmts-group']/wmts:ResourceURL[@resourceType = " + "'defaultStyle']"));
        Assert.assertEquals(MIMETYPE_10, attributeValue(attributes, "format"));
        Assert.assertEquals("http://localhost:8080/geoserver/gwc/service/wmts/reststyles/layers/wmts-group/styles/stylegroup?f=application%2Fvnd.ogc.sld%2Bxml", attributeValue(attributes, "template"));
        // make sure the style can be retrieved
        final MockHttpServletResponse response = getAsServletResponse(("gwc/service/wmts/reststyles/layers/wmts-group/styles/stylegroup?f=application" + "%2Fvnd.ogc.sld%2Bxml"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(MIMETYPE_10, response.getContentType());
        final Document styleDom = dom(response, true);
        assertXpathEvaluatesTo("DividedRoutes", "//sld:NamedLayer[1]/sld:Name", styleDom);
        assertXpathEvaluatesTo("Lakes", "//sld:NamedLayer[2]/sld:Name", styleDom);
        // check it can be modified
        final String style = loadStyle("stylegroup2.sld");
        final MockHttpServletResponse putResponse = putAsServletResponse("gwc/service/wmts/reststyles/layers/wmts-group/styles/stylegroup", style, MIMETYPE_10);
        Assert.assertEquals(204, putResponse.getStatus());
        // get it again and check
        final MockHttpServletResponse styleResponse2 = getAsServletResponse(("gwc/service/wmts/reststyles/layers/wmts-group/styles/stylegroup?f=application" + "%2Fvnd.ogc.sld%2Bxml"));
        Assert.assertEquals(200, styleResponse2.getStatus());
        Assert.assertEquals(MIMETYPE_10, styleResponse2.getContentType());
        final Document styleDom2 = dom(styleResponse2, true);
        assertXpathEvaluatesTo("#FF00FF", "//sld:NamedLayer[1]//sld:CssParameter", styleDom2);
        assertXpathEvaluatesTo("#000000", "//sld:NamedLayer[2]//sld:CssParameter", styleDom2);
    }
}

