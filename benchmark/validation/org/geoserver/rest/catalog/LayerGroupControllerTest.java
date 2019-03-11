/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import LayerGroupInfo.Mode.CONTAINER;
import LayerGroupInfo.Mode.EO;
import StyleInfo.DEFAULT_POINT;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.rest.RestBaseController;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class LayerGroupControllerTest extends CatalogRESTTestSupport {
    @Test
    public void testGetAllAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups.xml"));
        Assert.assertEquals(CatalogRESTTestSupport.catalog.getLayerGroups().size(), dom.getElementsByTagName("layerGroup").getLength());
    }

    @Test
    public void testGetAllAsHTML() throws Exception {
        getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups.html"));
    }

    @Test
    public void testGetAllFromWorkspace() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups.xml"));
        Assert.assertEquals("layerGroups", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("0", "count(//layerGroup)", dom);
        addLayerGroupToWorkspace();
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups.xml"));
        Assert.assertEquals("layerGroups", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("1", "count(//layerGroup)", dom);
        assertXpathExists("//layerGroup/name[text() = 'workspaceLayerGroup']", dom);
    }

    @Test
    public void testGetAsXML() throws Exception {
        print(get(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup.xml")));
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup.xml"));
        print(dom);
        Assert.assertEquals("layerGroup", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("sfLayerGroup", "/layerGroup/name", dom);
        assertXpathEvaluatesTo("2", "count(//published)", dom);
        assertXpathEvaluatesTo("2", "count(//style)", dom);
        // check layer link
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//published[name='sf:PrimitiveGeoFeature']/atom:link/@href", dom), Matchers.endsWith(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layers/PrimitiveGeoFeature.xml")));
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//published[name='sf:PrimitiveGeoFeature']/atom:link/@type", dom), Matchers.equalTo("application/xml"));
        // check style link
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//style[1]/atom:link/@href", dom), Matchers.endsWith(((RestBaseController.ROOT_PATH) + "/styles/point.xml")));
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//style[1]/atom:link/@type", dom), Matchers.equalTo("application/xml"));
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups/citeLayerGroup.xml"));
        Assert.assertEquals("layerGroup", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("citeLayerGroup", "/layerGroup/name", dom);
        assertXpathEvaluatesTo("6", "count(//published)", dom);
        assertXpathEvaluatesTo("6", "count(//style)", dom);
        assertXpathEvaluatesTo("2", "count(//layerGroup/keywords/string)", dom);
        assertXpathEvaluatesTo("1", "count(//layerGroup/keywords[string=\'keyword1\\@language=en\\;\\@vocabulary=vocabulary1\\;\'])", dom);
        assertXpathEvaluatesTo("1", "count(//layerGroup/keywords[string=\'keyword2\\@language=pt\\;\\@vocabulary=vocabulary2\\;\'])", dom);
        // check keywords were encoded
    }

    @Test
    public void testGetAsXMLNestedLinks() throws Exception {
        LayerGroupInfo cite = CatalogRESTTestSupport.catalog.getLayerGroupByName("citeLayerGroup");
        cite.getLayers().add(CatalogRESTTestSupport.catalog.getLayerGroupByName("sfLayerGroup"));
        cite.getStyles().add(null);
        CatalogRESTTestSupport.catalog.save(cite);
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups/citeLayerGroup.xml"));
        Assert.assertEquals("layerGroup", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("citeLayerGroup", "/layerGroup/name", dom);
        assertXpathEvaluatesTo("7", "count(//published)", dom);
        assertXpathEvaluatesTo("7", "count(//style)", dom);
        assertXpathEvaluatesTo("7", "count(//published/atom:link)", dom);
    }

    @Test
    public void testGetAsJSON() throws Exception {
        print(get(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup.json")));
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup.json"));
        JSONArray arr = getJSONObject("publishables").getJSONArray("published");
        Assert.assertEquals(2, arr.size());
        arr = getJSONObject("styles").getJSONArray("style");
        Assert.assertEquals(2, arr.size());
        print(get(((RestBaseController.ROOT_PATH) + "/layergroups/citeLayerGroup.json")));
        json = getAsJSON(((RestBaseController.ROOT_PATH) + "/layergroups/citeLayerGroup.json"));
        arr = getJSONObject("publishables").getJSONArray("published");
        Assert.assertEquals(6, arr.size());
        arr = getJSONObject("styles").getJSONArray("style");
        Assert.assertEquals(6, arr.size());
        // GEOS-7873
        LayerGroupInfo lg2 = CatalogRESTTestSupport.catalog.getLayerGroupByName("citeLayerGroup");
        List<StyleInfo> styles = lg2.getStyles();
        styles.set(1, CatalogRESTTestSupport.catalog.getStyleByName(DEFAULT_POINT));
        styles.set(3, CatalogRESTTestSupport.catalog.getStyleByName(DEFAULT_POINT));
        CatalogRESTTestSupport.catalog.save(lg2);
        print(get(((RestBaseController.ROOT_PATH) + "/layergroups/citeLayerGroup.json")));
        json = getAsJSON(((RestBaseController.ROOT_PATH) + "/layergroups/citeLayerGroup.json"));
        JSONObject layerGroup = ((JSONObject) (json)).getJSONObject("layerGroup");
        arr = layerGroup.getJSONObject("publishables").getJSONArray("published");
        Assert.assertEquals(6, arr.size());
        arr = layerGroup.getJSONObject("styles").getJSONArray("style");
        Assert.assertEquals(6, arr.size());
        // check keywords were correctly encoded
        Assert.assertThat(layerGroup.containsKey("keywords"), Matchers.is(true));
        JSONObject keywordsObject = layerGroup.getJSONObject("keywords");
        Assert.assertThat(keywordsObject.containsKey("string"), Matchers.is(true));
        JSONArray keywords = keywordsObject.getJSONArray("string");
        Assert.assertThat(keywords.size(), Matchers.is(2));
        // created a list of keywords so we can check is content with hamcrest
        List<Object> keywordsList = new ArrayList<>();
        keywordsList.addAll(keywords);
        Assert.assertThat(keywordsList, Matchers.containsInAnyOrder("keyword1\\@language=en\\;\\@vocabulary=vocabulary1\\;", "keyword2\\@language=pt\\;\\@vocabulary=vocabulary2\\;"));
    }

    @Test
    public void testGetAsHTML() throws Exception {
        getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup.html"));
    }

    @Test
    public void testRoundTripXML() throws Exception {
        LayerGroupInfo before = getCatalog().getLayerGroupByName("sfLayerGroup");
        // get and re-write, does not go boom
        String xml = getAsString(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup.xml"));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        // check nothing actually changed
        LayerGroupInfo after = getCatalog().getLayerGroupByName("sfLayerGroup");
        Assert.assertEquals(before, after);
    }

    @Test
    public void testRoundTripJSON() throws Exception {
        LayerGroupInfo before = getCatalog().getLayerGroupByName("sfLayerGroup");
        // get and re-write, does not go boom
        String json = getAsString(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup.json"));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup"), json, "application/json");
        Assert.assertEquals(200, response.getStatus());
        // check nothing actually changed
        LayerGroupInfo after = getCatalog().getLayerGroupByName("sfLayerGroup");
        Assert.assertEquals(before, after);
    }

    @Test
    public void testWorkspaceRoundTripXML() throws Exception {
        addLayerGroupToWorkspace();
        LayerGroupInfo before = getCatalog().getLayerGroupByName("workspaceLayerGroup");
        // get and re-write, does not go boom
        String xml = getAsString(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup.xml"));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        // check nothing actually changed
        LayerGroupInfo after = getCatalog().getLayerGroupByName("workspaceLayerGroup");
        Assert.assertEquals(before, after);
    }

    @Test
    public void testWorkspaceRoundTripJSON() throws Exception {
        addLayerGroupToWorkspace();
        LayerGroupInfo before = getCatalog().getLayerGroupByName("workspaceLayerGroup");
        // get and re-write, does not go boom
        String json = getAsString(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup.json"));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup"), json, "application/json");
        Assert.assertEquals(200, response.getStatus());
        // check nothing actually changed
        LayerGroupInfo after = getCatalog().getLayerGroupByName("workspaceLayerGroup");
        Assert.assertEquals(before, after);
    }

    @Test
    public void testGetWrongLayerGroup() throws Exception {
        // Parameters for the request
        String ws = "sf";
        String lg = "foooooo";
        // Request path
        String requestPath = (((RestBaseController.ROOT_PATH) + "/layergroups/") + lg) + ".html";
        String requestPath2 = (((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/layergroups/") + lg) + ".html";
        // Exception path
        String exception = "No such layer group " + lg;
        String exception2 = (("No such layer group " + lg) + " in workspace ") + ws;
        // CASE 1: No workspace set
        // First request should thrown an exception
        MockHttpServletResponse response = getAsServletResponse(requestPath);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertTrue(response.getContentAsString().contains(exception));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertFalse(response.getContentAsString().contains(exception));
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
        // CASE 2: workspace set
        // First request should thrown an exception
        response = getAsServletResponse(requestPath2);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertTrue(response.getContentAsString().contains(exception2));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath2 + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertFalse(response.getContentAsString().contains(exception2));
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testGetFromWorkspace() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup.xml"));
        Assert.assertEquals(404, resp.getStatus());
        addLayerGroupToWorkspace();
        resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup.xml"));
        Assert.assertEquals(200, resp.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup.xml"));
        assertXpathEvaluatesTo("workspaceLayerGroup", "/layerGroup/name", dom);
        assertXpathEvaluatesTo("sf", "/layerGroup/workspace/name", dom);
    }

    @Test
    public void testPost() throws Exception {
        String xml = "<layerGroup>" + ((((((((((((("    <name>newLayerGroup</name>" + "    <layers>") + "        <layer>Ponds</layer>") + "        <layer>Forests</layer>") + "    </layers>") + "    <styles>") + "        <style>polygon</style>") + "        <style>point</style>") + "    </styles>") + "    <keywords>") + "        <string>keyword1\\@language=en\\;\\@vocabulary=vocabulary1\\;</string>") + "        <string>keyword2\\@language=pt\\;\\@vocabulary=vocabulary2\\;</string>") + "    </keywords>") + "</layerGroup>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/layergroups/newLayerGroup"));
        LayerGroupInfo lg = CatalogRESTTestSupport.catalog.getLayerGroupByName("newLayerGroup");
        Assert.assertNotNull(lg);
        Assert.assertEquals(2, lg.getLayers().size());
        Assert.assertEquals("Ponds", lg.getLayers().get(0).getName());
        Assert.assertEquals("Forests", lg.getLayers().get(1).getName());
        Assert.assertEquals(2, lg.getStyles().size());
        Assert.assertEquals("polygon", lg.getStyles().get(0).getName());
        Assert.assertEquals("point", lg.getStyles().get(1).getName());
        Assert.assertNotNull(lg.getBounds());
        // expected keywords
        Keyword keyword1 = new Keyword("keyword1");
        keyword1.setLanguage("en");
        keyword1.setVocabulary("vocabulary1");
        Keyword keyword2 = new Keyword("keyword2");
        keyword2.setLanguage("pt");
        keyword2.setVocabulary("vocabulary2");
        // check that the keywords were correctly added
        Assert.assertThat(lg.getKeywords().size(), Matchers.is(2));
        Assert.assertThat(lg.getKeywords(), Matchers.containsInAnyOrder(keyword1, keyword2));
    }

    @Test
    public void testPostWithStyleGroups() throws Exception {
        // right now styleGroups need declared bounds to work
        String xml = "<layerGroup>" + ((((((((((((("    <name>newLayerGroupWithStyleGroup</name>" + "    <layers>") + "        <layer>Ponds</layer>") + "        <layer></layer>") + "    </layers>") + "    <styles>") + "        <style>polygon</style>") + "        <style>singleStyleGroup</style>") + "    </styles>") + "    <keywords>") + "        <string>keyword1\\@language=en\\;\\@vocabulary=vocabulary1\\;</string>") + "        <string>keyword2\\@language=pt\\;\\@vocabulary=vocabulary2\\;</string>") + "    </keywords>") + "</layerGroup>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/layergroups/newLayerGroupWithStyleGroup"));
        LayerGroupInfo lg = CatalogRESTTestSupport.catalog.getLayerGroupByName("newLayerGroupWithStyleGroup");
        Assert.assertNotNull(lg);
        Assert.assertEquals(2, lg.getLayers().size());
        Assert.assertEquals("Ponds", lg.getLayers().get(0).getName());
        Assert.assertNull(lg.getLayers().get(1));
        Assert.assertEquals(2, lg.getStyles().size());
        Assert.assertEquals("polygon", lg.getStyles().get(0).getName());
        Assert.assertEquals("singleStyleGroup", lg.getStyles().get(1).getName());
        Assert.assertNotNull(lg.getBounds());
        // expected keywords
        Keyword keyword1 = new Keyword("keyword1");
        keyword1.setLanguage("en");
        keyword1.setVocabulary("vocabulary1");
        Keyword keyword2 = new Keyword("keyword2");
        keyword2.setLanguage("pt");
        keyword2.setVocabulary("vocabulary2");
        // check that the keywords were correctly added
        Assert.assertThat(lg.getKeywords().size(), Matchers.is(2));
        Assert.assertThat(lg.getKeywords(), Matchers.containsInAnyOrder(keyword1, keyword2));
    }

    @Test
    public void testPostWithNestedGroups() throws Exception {
        String xml = "<layerGroup>" + ((((((((((("<name>nestedLayerGroupTest</name>" + "<publishables>") + "<published type=\"layer\">Ponds</published>") + "<published type=\"layer\">Forests</published>") + "<published type=\"layerGroup\">sfLayerGroup</published>") + "</publishables>") + "<styles>") + "<style>polygon</style>") + "<style>point</style>") + "<style></style>") + "</styles>") + "</layerGroup>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/layergroups/nestedLayerGroupTest"));
        LayerGroupInfo lg = CatalogRESTTestSupport.catalog.getLayerGroupByName("nestedLayerGroupTest");
        Assert.assertNotNull(lg);
        Assert.assertEquals(3, lg.getLayers().size());
        Assert.assertEquals("Ponds", lg.getLayers().get(0).getName());
        Assert.assertEquals("Forests", lg.getLayers().get(1).getName());
        Assert.assertEquals("sfLayerGroup", lg.getLayers().get(2).getName());
        Assert.assertEquals(3, lg.getStyles().size());
        Assert.assertEquals("polygon", lg.getStyles().get(0).getName());
        Assert.assertEquals("point", lg.getStyles().get(1).getName());
        Assert.assertNull(lg.getStyles().get(2));
        Assert.assertNotNull(lg.getBounds());
    }

    @Test
    public void testPostWithTypeContainer() throws Exception {
        String xml = "<layerGroup>" + (((((((((("<name>newLayerGroupWithTypeCONTAINER</name>" + "<mode>CONTAINER</mode>") + "<layers>") + "<layer>Ponds</layer>") + "<layer>Forests</layer>") + "</layers>") + "<styles>") + "<style>polygon</style>") + "<style>point</style>") + "</styles>") + "</layerGroup>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups"), xml);
        Assert.assertEquals(201, response.getStatus());
        LayerGroupInfo lg = CatalogRESTTestSupport.catalog.getLayerGroupByName("newLayerGroupWithTypeCONTAINER");
        Assert.assertNotNull(lg);
        Assert.assertEquals(CONTAINER, lg.getMode());
    }

    @Test
    public void testPostWithTypeEO() throws Exception {
        String xml = "<layerGroup>" + (((((((((("<name>newLayerGroupWithTypeEO</name>" + "<mode>EO</mode>") + "<rootLayer>Ponds</rootLayer>") + "<rootLayerStyle>polygon</rootLayerStyle>") + "<layers>") + "<layer>Forests</layer>") + "</layers>") + "<styles>") + "<style>point</style>") + "</styles>") + "</layerGroup>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups"), xml);
        Assert.assertEquals(201, response.getStatus());
        LayerGroupInfo lg = CatalogRESTTestSupport.catalog.getLayerGroupByName("newLayerGroupWithTypeEO");
        Assert.assertNotNull(lg);
        Assert.assertEquals(EO, lg.getMode());
        Assert.assertEquals("Ponds", lg.getRootLayer().getName());
        Assert.assertEquals("polygon", lg.getRootLayerStyle().getName());
    }

    @Test
    public void testPostNoStyles() throws Exception {
        String xml = "<layerGroup>" + ((((("<name>newLayerGroup</name>" + "<layers>") + "<layer>Ponds</layer>") + "<layer>Forests</layer>") + "</layers>") + "</layerGroup>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups"), xml);
        Assert.assertEquals(201, response.getStatus());
        LayerGroupInfo lg = CatalogRESTTestSupport.catalog.getLayerGroupByName("newLayerGroup");
        Assert.assertNotNull(lg);
        Assert.assertEquals(2, lg.getLayers().size());
        Assert.assertEquals("Ponds", lg.getLayers().get(0).getName());
        Assert.assertEquals("Forests", lg.getLayers().get(1).getName());
        Assert.assertEquals(2, lg.getStyles().size());
        Assert.assertNull(lg.getStyles().get(0));
        Assert.assertNull(lg.getStyles().get(1));
    }

    @Test
    public void testPostToWorkspace() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getWorkspaceByName("sf"));
        Assert.assertNull(cat.getLayerGroupByName("sf", "workspaceLayerGroup"));
        String xml = "<layerGroup>" + (((("<name>workspaceLayerGroup</name>" + "<layers>") + "<layer>PrimitiveGeoFeature</layer>") + "</layers>") + "</layerGroup>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getLayerGroupByName("sf", "workspaceLayerGroup"));
    }

    @Test
    public void testPut() throws Exception {
        String xml = "<layerGroup>" + ((((((((((((((((((((((("<name>sfLayerGroup</name>" + "<styles>") + "<style>polygon</style>") + "<style>line</style>") + "</styles>") + "<attribution>") + "  <logoWidth>101</logoWidth>") + "  <logoHeight>102</logoHeight>") + "</attribution>") + "<metadataLinks>   ") + "<metadataLink>") + "  <id>1</id>") + "  <type>text/html</type>") + "  <metadataType>FGDC</metadataType>") + "  <content>http://my/metadata/link/1</content>") + "</metadataLink>    ") + "<metadataLink>") + "  <id>2</id>") + "  <type>text/html</type>") + "  <metadataType>FGDC</metadataType>") + "  <content>http://my/metadata/link/2</content>") + "</metadataLink>    ") + "</metadataLinks>") + "</layerGroup>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        LayerGroupInfo lg = CatalogRESTTestSupport.catalog.getLayerGroupByName("sfLayerGroup");
        Assert.assertEquals(2, lg.getLayers().size());
        Assert.assertEquals(2, lg.getStyles().size());
        Assert.assertEquals("polygon", lg.getStyles().get(0).getName());
        Assert.assertEquals("line", lg.getStyles().get(1).getName());
        Assert.assertEquals(101, lg.getAttribution().getLogoWidth());
        Assert.assertEquals(102, lg.getAttribution().getLogoHeight());
        Assert.assertEquals(2, lg.getMetadataLinks().size());
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        LayerGroupInfo lg = CatalogRESTTestSupport.catalog.getLayerGroupByName("sfLayerGroup");
        boolean isQueryDisabled = lg.isQueryDisabled();
        lg.setQueryDisabled(true);
        CatalogRESTTestSupport.catalog.save(lg);
        String xml = "<layerGroup>" + ("<name>sfLayerGroup</name>" + "</layerGroup>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        lg = CatalogRESTTestSupport.catalog.getLayerGroupByName("sfLayerGroup");
        Assert.assertTrue(lg.isQueryDisabled());
        lg.setQueryDisabled(isQueryDisabled);
        CatalogRESTTestSupport.catalog.save(lg);
    }

    @Test
    public void testPutToWorkspace() throws Exception {
        testPostToWorkspace();
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getLayerGroupByName("sf", "workspaceLayerGroup").getStyles().get(0));
        String xml = "<layerGroup>" + ((("<styles>" + "<style>line</style>") + "</styles>") + "</layerGroup>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup"), xml, "application/xml");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("line", cat.getLayerGroupByName("sf", "workspaceLayerGroup").getStyles().get(0).getName());
    }

    @Test
    public void testPutToWorkspaceChangeWorkspace() throws Exception {
        testPostToWorkspace();
        String xml = "<layerGroup>" + ("<workspace>cite</workspace>" + "</layerGroup>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup"), xml, "application/xml");
        Assert.assertEquals(403, response.getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups/sfLayerGroup"));
        Assert.assertEquals(200, response.getStatus());
        response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups/citeLayerGroup"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(0, CatalogRESTTestSupport.catalog.getLayerGroups().size());
    }

    @Test
    public void testDeleteFromWorkspace() throws Exception {
        testPostToWorkspace();
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerGroupByName("sf", "workspaceLayerGroup"));
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNull(cat.getLayerGroupByName("sf", "workspaceLayerGroup"));
    }

    @Test
    public void testLayerGroupDuplicateLayerNames() throws Exception {
        // Create a Lakes layer in the sf workspace
        Catalog catalog = getCatalog();
        FeatureTypeInfo lakesFt = catalog.getFactory().createFeatureType();
        lakesFt.setName("Lakes");
        lakesFt.setNamespace(catalog.getNamespaceByPrefix("sf"));
        lakesFt.setStore(catalog.getDefaultDataStore(catalog.getWorkspaceByName("sf")));
        lakesFt.setNativeBoundingBox(new org.geotools.geometry.jts.ReferencedEnvelope((-10), 10, (-10), 10, DefaultGeographicCRS.WGS84));
        catalog.add(lakesFt);
        lakesFt = catalog.getFeatureTypeByName("sf", "Lakes");
        LayerInfo lakes = catalog.getFactory().createLayer();
        lakes.setResource(lakesFt);
        catalog.add(lakes);
        Assert.assertNotNull(catalog.getLayerByName("sf:Lakes"));
        Assert.assertNotNull(catalog.getLayerByName("cite:Lakes"));
        // POST a new layer group consisting of sf:Lakes and cite:Lakes
        String xml = "<layerGroup>" + ((((("<name>doubleLayerGroup</name>" + "<layers>") + "<layer>sf:Lakes</layer>") + "<layer>cite:Lakes</layer>") + "</layers>") + "</layerGroup>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups"), xml);
        Assert.assertEquals(201, response.getStatus());
        // Verify the new layer group in the catalog
        LayerGroupInfo lg = catalog.getLayerGroupByName("doubleLayerGroup");
        Assert.assertNotNull(lg);
        Assert.assertEquals(2, lg.getLayers().size());
        Assert.assertEquals("Lakes", lg.getLayers().get(0).getName());
        Assert.assertEquals("sf:Lakes", lg.getLayers().get(0).prefixedName());
        Assert.assertEquals("Lakes", lg.getLayers().get(1).getName());
        Assert.assertEquals("cite:Lakes", lg.getLayers().get(1).prefixedName());
        // GET layer group and verify layer names
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups/doubleLayerGroup.xml"));
        print(dom);
        Assert.assertEquals("layerGroup", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("doubleLayerGroup", "/layerGroup/name", dom);
        assertXpathEvaluatesTo("2", "count(//published)", dom);
        assertXpathEvaluatesTo("2", "count(//style)", dom);
        // verify layer order
        assertXpathEvaluatesTo("sf:Lakes", "//publishables/published[1]/name", dom);
        assertXpathEvaluatesTo("cite:Lakes", "//publishables/published[2]/name", dom);
        // verify layer links
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//published[name='sf:Lakes']/atom:link/@href", dom), Matchers.endsWith(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layers/Lakes.xml")));
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//published[name='sf:Lakes']/atom:link/@type", dom), Matchers.equalTo("application/xml"));
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//published[name='cite:Lakes']/atom:link/@href", dom), Matchers.endsWith(((RestBaseController.ROOT_PATH) + "/workspaces/cite/layers/Lakes.xml")));
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//published[name='cite:Lakes']/atom:link/@type", dom), Matchers.equalTo("application/xml"));
    }

    @Test
    public void testLayersStylesInWorkspace() throws Exception {
        testPostToWorkspace();
        Catalog cat = getCatalog();
        StyleInfo s = cat.getFactory().createStyle();
        s.setName("s1");
        s.setWorkspace(cat.getWorkspaceByName("sf"));
        s.setFilename("s1.sld");
        cat.add(s);
        s = cat.getFactory().createStyle();
        s.setName("s2");
        s.setWorkspace(cat.getWorkspaceByName("sf"));
        s.setFilename("s2.sld");
        cat.add(s);
        String xml = "<layerGroup>" + (((((((((((((("<layers>" + "<layer>PrimitiveGeoFeature</layer>") + "<layer>AggregateGeoFeature</layer>") + "</layers>") + "<styles>") + "<style>") + "<name>s1</name>") + "<workspace>sf</workspace>") + "</style>") + "<style>") + "<name>s2</name>") + "<workspace>sf</workspace>") + "</style>") + "</styles>") + "</layerGroup>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        LayerGroupInfo lg = cat.getLayerGroupByName("sf", "workspaceLayerGroup");
        Assert.assertEquals(2, lg.getLayers().size());
        Assert.assertEquals(2, lg.getStyles().size());
        Assert.assertEquals("PrimitiveGeoFeature", lg.getLayers().get(0).getName());
        Assert.assertEquals("AggregateGeoFeature", lg.getLayers().get(1).getName());
        Assert.assertEquals("s1", lg.getStyles().get(0).getName());
        Assert.assertNotNull(lg.getStyles().get(0).getWorkspace());
        Assert.assertEquals("sf", lg.getStyles().get(0).getWorkspace().getName());
        Assert.assertEquals("s2", lg.getStyles().get(1).getName());
        Assert.assertNotNull(lg.getStyles().get(1).getWorkspace());
        Assert.assertEquals("sf", lg.getStyles().get(1).getWorkspace().getName());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups/workspaceLayerGroup.xml"));
        assertXpathEvaluatesTo("http://localhost:8080/geoserver/rest/workspaces/sf/styles/s1.xml", "//style[name = 'sf:s1']/atom:link/@href", dom);
        assertXpathEvaluatesTo("http://localhost:8080/geoserver/rest/workspaces/sf/styles/s2.xml", "//style[name = 'sf:s2']/atom:link/@href", dom);
    }
}

